package model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * OrderDetail entity for displaying order with product details.
 * Used for carrier's available deliveries view.
 * 
 * INHERITANCE: Extends Entity
 */
public class OrderDetail extends Entity {

    private int orderId;
    private String customerUsername;
    private String customerAddress;
    private String customerPhone;
    private String status;
    private Timestamp requestedDelivery;
    private Timestamp deliveredAt;
    private String carrierUsername;
    private double totalVatIncluded;
    private Timestamp createdAt;
    private Timestamp cancelledAt;
    private String cancelReason;

    // Product items in this order
    private List<OrderItem> items = new ArrayList<>();

    public OrderDetail() {
        super();
    }

    // Getters and Setters
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
        setId(orderId);
    }

    public String getCustomerUsername() {
        return customerUsername;
    }

    public void setCustomerUsername(String customerUsername) {
        this.customerUsername = customerUsername;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getRequestedDelivery() {
        return requestedDelivery;
    }

    public void setRequestedDelivery(Timestamp requestedDelivery) {
        this.requestedDelivery = requestedDelivery;
    }

    public Timestamp getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(Timestamp deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public String getCarrierUsername() {
        return carrierUsername;
    }

    public void setCarrierUsername(String carrierUsername) {
        this.carrierUsername = carrierUsername;
    }

    public double getTotalVatIncluded() {
        return totalVatIncluded;
    }

    public void setTotalVatIncluded(double totalVatIncluded) {
        this.totalVatIncluded = totalVatIncluded;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(Timestamp cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public void addItem(OrderItem item) {
        this.items.add(item);
    }

    /**
     * Get product list as string.
     */
    public String getProductListString() {
        if (items == null || items.isEmpty())
            return "No items";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            OrderItem item = items.get(i);
            if (i > 0)
                sb.append(", ");
            sb.append(item.getProductName())
                    .append(" (").append(item.getKg()).append(" kg)");
        }
        return sb.toString();
    }

    /**
     * Check if order can be cancelled (within 1 hour of creation).
     */
    public boolean canCancel() {
        if (!"NEW".equalsIgnoreCase(status))
            return false;
        if (createdAt == null)
            return false;

        long oneHourMs = 60 * 60 * 1000;
        long now = System.currentTimeMillis();
        return (now - createdAt.getTime()) <= oneHourMs;
    }

    @Override
    public String getDisplayName() {
        return "Order #" + orderId + " - " + customerUsername + " - " + status;
    }

    /**
     * Inner class for order items.
     */
    public static class OrderItem {
        private int productId;
        private String productName;
        private double kg;
        private double priceAtTime; // getEffectivePrice at order time

        public OrderItem() {
        }

        public OrderItem(int productId, String productName, double kg, double priceAtTime) {
            this.productId = productId;
            this.productName = productName;
            this.kg = kg;
            this.priceAtTime = priceAtTime;
        }

        public int getProductId() {
            return productId;
        }

        public void setProductId(int productId) {
            this.productId = productId;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public double getKg() {
            return kg;
        }

        public void setKg(double kg) {
            this.kg = kg;
        }

        public double getPriceAtTime() {
            return priceAtTime;
        }

        public void setPriceAtTime(double priceAtTime) {
            this.priceAtTime = priceAtTime;
        }

        public double getLineTotal() {
            return kg * priceAtTime;
        }
    }
}
