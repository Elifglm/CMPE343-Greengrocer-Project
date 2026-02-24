package model;

import java.sql.Timestamp;

/**
 * Order Status History entity for tracking order status changes.
 * 
 * INHERITANCE: Extends Entity
 */
public class OrderStatusHistory extends Entity {

    private int orderId;
    private String status;
    private Timestamp changedAt;
    private String changedBy;
    private String notes;

    public OrderStatusHistory() {
        super();
    }

    public OrderStatusHistory(int historyId, int orderId, String status,
            Timestamp changedAt, String changedBy, String notes) {
        super(historyId);
        this.orderId = orderId;
        this.status = status;
        this.changedAt = changedAt;
        this.changedBy = changedBy;
        this.notes = notes;
    }

    // Getters and Setters
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(Timestamp changedAt) {
        this.changedAt = changedAt;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String getDisplayName() {
        return "Order #" + orderId + " -> " + status + " at " + changedAt;
    }
}
