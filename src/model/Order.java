package model;

import java.sql.Timestamp;

/**
 * Order entity representing customer orders.
 * 
 * INHERITANCE: Extends Entity
 * ENCAPSULATION: Private fields with getters/setters
 * POLYMORPHISM: Overrides getDisplayName()
 */
public class Order extends Entity {

    // ENCAPSULATION: Private fields
    private String username;
    private int productId;
    private double quantity;
    private double total; // DB: Orders.total (VAT dahil)
    private String status;
    private Timestamp requestedDelivery;
    private Timestamp deliveredAt;
    private String carrierUsername;

    // Constructors
    public Order() {
        super();
    }

    public Order(int orderId, String username, int productId, int quantity, double total, String status) {
        super(orderId);
        this.username = username;
        this.productId = productId;
        this.quantity = quantity;
        this.total = total;
        this.status = status;
    }

    public Order(int orderId, String username, String status,
            Timestamp requestedDelivery, double total) {
        super(orderId);
        this.username = username;
        this.status = status;
        this.requestedDelivery = requestedDelivery;
        this.total = total;
    }

    public Order(int orderId, String username, String status,
            Timestamp requestedDelivery, Timestamp deliveredAt,
            String carrierUsername, double total) {
        super(orderId);
        this.username = username;
        this.status = status;
        this.requestedDelivery = requestedDelivery;
        this.deliveredAt = deliveredAt;
        this.carrierUsername = carrierUsername;
        this.total = total;
    }

    // ENCAPSULATION: Getters/Setters

    // Backward compatibility: orderId maps to inherited id
    public int getOrderId() {
        return getId();
    }

    public void setOrderId(int orderId) {
        setId(orderId);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getTotalCost() {
        return total;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
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

    // POLYMORPHISM: Override abstract method from Entity
    @Override
    public String getDisplayName() {
        return "Order #" + getId() + " - " + status;
    }
}
