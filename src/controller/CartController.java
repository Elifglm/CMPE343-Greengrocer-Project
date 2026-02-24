package controller;

import dao.OrderDAO;
import dao.CustomerLoyaltyDAO;
import service.InvoiceService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.CartItem;
import model.Product;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Controller for Shopping Cart screen.
 * 
 * INHERITANCE: Extends BaseController
 * IMPORTANT: All prices must use getEffectivePrice()
 */
public class CartController extends BaseController {

    private static final double VAT_RATE = 0.18;
    private static final double MIN_CART_TOTAL = 100.00;

    @FXML
    private Label titleLabel;

    @FXML
    private TableView<CartItem> cartTable;
    @FXML
    private TableColumn<CartItem, String> nameCol;
    @FXML
    private TableColumn<CartItem, Double> kgCol;
    @FXML
    private TableColumn<CartItem, Double> unitPriceCol;
    @FXML
    private TableColumn<CartItem, Double> lineTotalCol;

    @FXML
    private Label subtotalLabel;
    @FXML
    private Label vatLabel;
    @FXML
    private Label totalLabel;
    @FXML
    private Label infoLabel;

    @FXML
    private TextField couponField;
    @FXML
    private Label discountLabel;

    private final ObservableList<CartItem> cartItems = FXCollections.observableArrayList();
    private Map<Integer, CustomerController.LocalCartItem> cartMapRef;

    private double appliedDiscount = 0;

    @Override
    public void setUsername(String username) {
        this.currentUsername = username;
        titleLabel.setText("Shopping Cart - " + username);
    }

    @Override
    protected Label getUsernameLabel() {
        return titleLabel;
    }

    @Override
    protected String getScreenTitle() {
        return "Shopping Cart";
    }

    @FXML
    public void initialize() {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        kgCol.setCellValueFactory(new PropertyValueFactory<>("kg"));
        unitPriceCol.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        lineTotalCol.setCellValueFactory(new PropertyValueFactory<>("subTotal"));

        unitPriceCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null)
                    setText("");
                else
                    setText(String.format("%.2f", item));
            }
        });
        lineTotalCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null)
                    setText("");
                else
                    setText(String.format("%.2f", item));
            }
        });

        cartTable.setItems(cartItems);
        clearInfoLabel(infoLabel);

        if (discountLabel != null)
            discountLabel.setText("");

        updateTotals();
    }

    public void init(String username, Map<Integer, CustomerController.LocalCartItem> cartMap) {
        this.currentUsername = username;
        this.cartMapRef = cartMap;

        titleLabel.setText("Shopping Cart - " + username);
        rebuildUiListFromMap();
        updateTotals();
    }

    private void rebuildUiListFromMap() {
        cartItems.clear();
        if (cartMapRef == null)
            return;

        for (CustomerController.LocalCartItem local : cartMapRef.values()) {
            Product p = local.getProduct();

            // CRITICAL: Use getEffectivePrice() for unitPrice
            double effectivePrice = p.getEffectivePrice();

            cartItems.add(new CartItem(
                    p.getProductId(),
                    p.getName(),
                    effectivePrice, // MUST be getEffectivePrice()
                    local.getKg()));
        }
    }

    private double getSubtotal() {
        double subtotal = 0.0;
        for (CartItem i : cartItems)
            subtotal += i.getSubTotal();
        return subtotal;
    }

    private void updateTotals() {
        double subtotal = getSubtotal();
        double discount = appliedDiscount;
        double afterDiscount = subtotal - discount;
        if (afterDiscount < 0)
            afterDiscount = 0;

        double vat = afterDiscount * VAT_RATE;
        double total = afterDiscount + vat;

        subtotalLabel.setText(String.format("%.2f", subtotal));
        if (discountLabel != null && discount > 0) {
            discountLabel.setText(String.format("-%.2f", discount));
        }
        vatLabel.setText(String.format("%.2f", vat));
        totalLabel.setText(String.format("%.2f", total));
    }

    @FXML
    private Label couponInfoLabel;
    @FXML
    private Label loyaltyLabel;

    private model.Coupon appliedCoupon = null;

    @FXML
    private void handleApplyCoupon() {
        if (couponField == null)
            return;

        String code = couponField.getText();
        if (code == null || code.trim().isEmpty()) {
            showCouponInfo("Enter a coupon code!", true);
            return;
        }

        code = code.trim().toUpperCase();

        // Get subtotal for minimum order check
        double subtotal = getSubtotal();
        if (subtotal <= 0) {
            showCouponInfo("Add items to cart first!", true);
            return;
        }

        // Validate coupon using CouponDAO
        model.Coupon coupon = dao.CouponDAO.getCouponByCode(code);

        if (coupon == null) {
            showCouponInfo("Invalid coupon code!", true);
            appliedDiscount = 0;
            appliedCoupon = null;
            updateTotals();
            return;
        }

        // Check if coupon is active
        if (!coupon.isActive()) {
            showCouponInfo("This coupon is no longer active!", true);
            appliedDiscount = 0;
            appliedCoupon = null;
            updateTotals();
            return;
        }

        // Check validity date
        if (coupon.getValidUntil() != null &&
                coupon.getValidUntil().toLocalDateTime().isBefore(LocalDateTime.now())) {
            showCouponInfo("This coupon has expired!", true);
            appliedDiscount = 0;
            appliedCoupon = null;
            updateTotals();
            return;
        }

        // Check minimum order (compare with subtotal + VAT since that's what user sees)
        double totalWithVat = subtotal * (1 + VAT_RATE);
        if (totalWithVat < coupon.getMinOrderAmount()) {
            showCouponInfo(
                    String.format("Min order: %.2f TL (current: %.2f TL)", coupon.getMinOrderAmount(), totalWithVat),
                    true);
            appliedDiscount = 0;
            appliedCoupon = null;
            updateTotals();
            return;
        }

        // Check max uses
        if (coupon.getMaxUses() > 0 && coupon.getUsedCount() >= coupon.getMaxUses()) {
            showCouponInfo("Coupon usage limit reached!", true);
            appliedDiscount = 0;
            appliedCoupon = null;
            updateTotals();
            return;
        }

        // Apply discount
        appliedCoupon = coupon;
        appliedDiscount = subtotal * (coupon.getDiscountPercent() / 100.0);

        showCouponInfo(String.format("✅ %s applied! (%.0f%% off)", code, coupon.getDiscountPercent()), false);
        if (discountLabel != null) {
            discountLabel.setText(String.format("Coupon: -%.2f TL", appliedDiscount));
        }

        updateTotals();
    }

    private void showCouponInfo(String msg, boolean isError) {
        if (couponInfoLabel != null) {
            couponInfoLabel.setStyle(isError ? "-fx-text-fill: #F87171;" : "-fx-text-fill: #10B981;");
            couponInfoLabel.setText(msg);
        }
    }

    @FXML
    private void handleRemoveSelected() {
        CartItem sel = cartTable.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showInfoLabel(infoLabel, "Select an item!", true);
            return;
        }

        cartItems.remove(sel);
        if (cartMapRef != null)
            cartMapRef.remove(sel.getProductId());

        updateTotals();
        showInfoLabel(infoLabel, "Removed.", false);
    }

    @FXML
    private void handleClear() {
        cartItems.clear();
        if (cartMapRef != null)
            cartMapRef.clear();
        appliedDiscount = 0;
        updateTotals();
        showInfoLabel(infoLabel, "Cart cleared.", false);
    }

    @FXML
    private void handleCheckout() {
        clearInfoLabel(infoLabel);

        if (cartItems.isEmpty()) {
            showInfoLabel(infoLabel, "Cart is empty!", true);
            return;
        }

        double subtotal = getSubtotal();
        double afterDiscount = subtotal - appliedDiscount;
        if (afterDiscount < 0)
            afterDiscount = 0;
        double totalVatInc = afterDiscount + afterDiscount * VAT_RATE;

        if (totalVatInc < MIN_CART_TOTAL) {
            showInfoLabel(infoLabel,
                    "Minimum cart total is " + String.format("%.2f", MIN_CART_TOTAL) + " TL (VAT inc.)", true);
            return;
        }

        LocalDateTime requested = pickDeliveryDateTime();
        if (requested == null) {
            showInfoLabel(infoLabel, "Delivery time not selected / invalid.", true);
            return;
        }

        // Build confirmation summary
        StringBuilder sb = new StringBuilder();
        sb.append("Customer: ").append(currentUsername).append("\n");
        sb.append("Requested delivery: ").append(requested).append("\n\n");

        for (CartItem i : cartItems) {
            sb.append("• ").append(i.getName())
                    .append(" | ").append(String.format("%.2f", i.getKg())).append(" kg")
                    .append(" | ").append(String.format("%.2f TL/kg", i.getUnitPrice()))
                    .append(" | ").append(String.format("%.2f TL", i.getSubTotal()))
                    .append("\n");
        }

        sb.append("\nSubtotal: ").append(String.format("%.2f", subtotal)).append(" TL");
        if (appliedDiscount > 0) {
            sb.append("\nDiscount: -").append(String.format("%.2f", appliedDiscount)).append(" TL");
        }
        sb.append("\nVAT (18%): ").append(String.format("%.2f", afterDiscount * VAT_RATE)).append(" TL");
        sb.append("\nTOTAL: ").append(String.format("%.2f", totalVatInc)).append(" TL");
        sb.append("\n\nConfirm purchase?");

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Order Summary");
        confirm.setHeaderText("Review before purchase");
        confirm.setContentText(sb.toString());

        ButtonType ok = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirm.getButtonTypes().setAll(ok, cancel);

        var res = confirm.showAndWait();
        if (res.isEmpty() || res.get() != ok)
            return;

        // Create order - CartItem.unitPrice is already getEffectivePrice
        boolean saved = OrderDAO.createCartOrder(
                currentUsername,
                Timestamp.valueOf(requested),
                totalVatInc,
                cartItems);

        if (!saved) {
            showInfoLabel(infoLabel, "Checkout failed! (DB insert/stock issue)", true);
            return;
        }

        if (appliedCoupon != null) {
            dao.CouponDAO.useCoupon(appliedCoupon.getCode());
            appliedCoupon = null;
        }

        cartItems.clear();
        if (cartMapRef != null)
            cartMapRef.clear();
        appliedDiscount = 0;
        updateTotals();

        // Show success message
        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle("Order Confirmation");
        successAlert.setHeaderText("✅ Your Order Has Been Received!");
        successAlert.setContentText("Your order has been successfully placed.\nYou can track it in 'My Orders'.");
        successAlert.showAndWait();

        // Close the cart window
        Stage stage = (Stage) cartTable.getScene().getWindow();
        stage.close();
    }

    private LocalDateTime pickDeliveryDateTime() {
        Dialog<LocalDateTime> dialog = new Dialog<>();
        dialog.setTitle("Select Delivery Time");
        dialog.setHeaderText("Choose delivery within 48 hours.");

        Spinner<Integer> hoursFromNow = new Spinner<>(1, 48, 2);
        hoursFromNow.setEditable(true);
        hoursFromNow.setPrefWidth(80);

        Spinner<Integer> minutes = new Spinner<>(0, 59, 0);
        minutes.setEditable(true);
        minutes.setPrefWidth(80);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.addRow(0, new Label("Hours from now (1-48):"), hoursFromNow);
        grid.addRow(1, new Label("Extra minutes (0-59):"), minutes);

        dialog.getDialogPane().setContent(grid);

        ButtonType okBtn = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okBtn, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn != okBtn)
                return null;

            try {
                int h = Integer.parseInt(hoursFromNow.getEditor().getText().trim());
                int m = Integer.parseInt(minutes.getEditor().getText().trim());

                if (h < 1 || h > 48)
                    return null;
                if (m < 0 || m > 59)
                    return null;

                return LocalDateTime.now().plusHours(h).plusMinutes(m);
            } catch (Exception e) {
                return null;
            }
        });

        return dialog.showAndWait().orElse(null);
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) cartTable.getScene().getWindow();
        stage.close();
    }
}
