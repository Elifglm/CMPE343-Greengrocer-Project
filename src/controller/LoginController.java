package controller;

import dao.UserDAO;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Controller for Login screen.
 * 
 * INHERITANCE: Extends BaseController
 * POLYMORPHISM: Overrides abstract methods from BaseController
 * ENCAPSULATION: Private fields
 */
public class LoginController extends BaseController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label messageLabel;

    // Animation elements
    @FXML
    private VBox loginCard;
    @FXML
    private ImageView logoImage;
    @FXML
    private Label titleLabel;
    @FXML
    private Label subtitleLabel;
    @FXML
    private VBox formSection;

    // ========== POLYMORPHISM: Override abstract methods from BaseController
    // ==========

    @Override
    public void setUsername(String username) {
        this.currentUsername = username;
    }

    @Override
    protected Label getUsernameLabel() {
        return messageLabel;
    }

    @Override
    protected String getScreenTitle() {
        return "GreenGrocer Login";
    }

    @FXML
    public void initialize() {
        messageLabel.setText("");

        // Enter ile login
        usernameField.setOnAction(e -> handleLogin());
        passwordField.setOnAction(e -> handleLogin());

        // Play entrance animation
        playEntranceAnimation();
    }

    /**
     * Premium entrance animation - fade in and slide up effect
     */
    private void playEntranceAnimation() {
        // Card fade in + slide up
        loginCard.setTranslateY(30);
        FadeTransition cardFade = new FadeTransition(Duration.millis(600), loginCard);
        cardFade.setFromValue(0);
        cardFade.setToValue(1);

        TranslateTransition cardSlide = new TranslateTransition(Duration.millis(600), loginCard);
        cardSlide.setFromY(30);
        cardSlide.setToY(0);
        cardSlide.setInterpolator(Interpolator.EASE_OUT);

        ParallelTransition cardAnim = new ParallelTransition(cardFade, cardSlide);

        // Logo scale + fade (starts a bit later)
        logoImage.setScaleX(0.5);
        logoImage.setScaleY(0.5);

        FadeTransition logoFade = new FadeTransition(Duration.millis(500), logoImage);
        logoFade.setFromValue(0);
        logoFade.setToValue(1);
        logoFade.setDelay(Duration.millis(200));

        ScaleTransition logoScale = new ScaleTransition(Duration.millis(500), logoImage);
        logoScale.setFromX(0.5);
        logoScale.setFromY(0.5);
        logoScale.setToX(1);
        logoScale.setToY(1);
        logoScale.setDelay(Duration.millis(200));
        logoScale.setInterpolator(Interpolator.EASE_OUT);

        ParallelTransition logoAnim = new ParallelTransition(logoFade, logoScale);

        // Title fade in
        FadeTransition titleFade = new FadeTransition(Duration.millis(400), titleLabel);
        titleFade.setFromValue(0);
        titleFade.setToValue(1);
        titleFade.setDelay(Duration.millis(350));

        // Subtitle fade in
        FadeTransition subtitleFade = new FadeTransition(Duration.millis(400), subtitleLabel);
        subtitleFade.setFromValue(0);
        subtitleFade.setToValue(1);
        subtitleFade.setDelay(Duration.millis(450));

        // Form section fade in
        formSection.setTranslateY(15);
        FadeTransition formFade = new FadeTransition(Duration.millis(500), formSection);
        formFade.setFromValue(0);
        formFade.setToValue(1);
        formFade.setDelay(Duration.millis(500));

        TranslateTransition formSlide = new TranslateTransition(Duration.millis(500), formSection);
        formSlide.setFromY(15);
        formSlide.setToY(0);
        formSlide.setDelay(Duration.millis(500));
        formSlide.setInterpolator(Interpolator.EASE_OUT);

        ParallelTransition formAnim = new ParallelTransition(formFade, formSlide);

        // Play all animations
        ParallelTransition allAnimations = new ParallelTransition(
                cardAnim, logoAnim, titleFade, subtitleFade, formAnim);
        allAnimations.play();
    }

    @FXML
    private void handleLogin() {
        messageLabel.setText("");

        String username = safeTrim(usernameField.getText());
        String password = safeTrim(passwordField.getText());

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter username and password");
            return;
        }

        // Use authenticateAndGetPerson to get the user object with role
        model.Person person = UserDAO.authenticateAndGetPerson(username, password);

        System.out.println("  [LoginController] person = " + person);
        if (person == null) {
            System.out.println("  [LoginController] person is NULL - showing error");
            messageLabel.setText("Wrong username or password!");
            return;
        }

        try {
            String role = person.getRole().toLowerCase();
            System.out.println(
                    "  [LoginController] person.getRole() = '" + person.getRole() + "', lowercase = '" + role + "'");
            switch (role) {
                case "customer":
                    openCustomer(username);
                    break;
                case "carrier":
                    openCarrier(username);
                    break;
                case "owner":
                    openOwner(username);
                    break;
                default:
                    messageLabel.setText("Unknown role: " + role);
            }
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error opening dashboard!");
        }
    }

    @FXML
    private void handleOpenRegister() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            boolean wasMaximized = stage.isMaximized();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/register.fxml"));
            Scene scene = new Scene(loader.load(), 960, 540);

            applyStylesheet(scene);

            stage.setTitle("GreenGrocer - Register");
            stage.setScene(scene);
            stage.setMaximized(wasMaximized);
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Cannot open register screen!");
        }
    }

    private void openCustomer(String username) throws Exception {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        boolean wasMaximized = stage.isMaximized();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/customer.fxml"));
        Scene scene = new Scene(loader.load(), 960, 540);

        applyStylesheet(scene);

        CustomerController controller = loader.getController();
        controller.setUsername(username);

        stage.setTitle("Group30 GreenGrocer");
        stage.setScene(scene);
        stage.setMaximized(wasMaximized);
    }

    private void openCarrier(String username) throws Exception {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        boolean wasMaximized = stage.isMaximized();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/carrier.fxml"));
        Scene scene = new Scene(loader.load(), 960, 540);

        applyStylesheet(scene);

        CarrierController controller = loader.getController();
        controller.setUsername(username);

        stage.setTitle("Group30 GreenGrocer");
        stage.setScene(scene);
        stage.setMaximized(wasMaximized);
    }

    private void openOwner(String username) throws Exception {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        boolean wasMaximized = stage.isMaximized();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/owner.fxml"));
        Scene scene = new Scene(loader.load(), 960, 540);

        applyStylesheet(scene);

        OwnerController controller = loader.getController();
        controller.setUsername(username);

        stage.setTitle("Group30 GreenGrocer");
        stage.setScene(scene);
        stage.setMaximized(wasMaximized);
    }
}
