package model;

import java.sql.Timestamp;

/**
 * Carrier Rating entity for customer ratings of carriers.
 * 
 * INHERITANCE: Extends Entity
 */
public class CarrierRating extends Entity {

    private int orderId;
    private String carrierUsername;
    private String customerUsername;
    private int rating; // 1-5
    private String comment;
    private Timestamp ratedAt;

    public CarrierRating() {
        super();
    }

    public CarrierRating(int ratingId, int orderId, String carrierUsername,
            String customerUsername, int rating, String comment, Timestamp ratedAt) {
        super(ratingId);
        this.orderId = orderId;
        this.carrierUsername = carrierUsername;
        this.customerUsername = customerUsername;
        this.rating = rating;
        this.comment = comment;
        this.ratedAt = ratedAt;
    }

    // Getters and Setters
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getCarrierUsername() {
        return carrierUsername;
    }

    public void setCarrierUsername(String carrierUsername) {
        this.carrierUsername = carrierUsername;
    }

    public String getCustomerUsername() {
        return customerUsername;
    }

    public void setCustomerUsername(String customerUsername) {
        this.customerUsername = customerUsername;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        if (rating < 1)
            rating = 1;
        if (rating > 5)
            rating = 5;
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Timestamp getRatedAt() {
        return ratedAt;
    }

    public void setRatedAt(Timestamp ratedAt) {
        this.ratedAt = ratedAt;
    }

    @Override
    public String getDisplayName() {
        return carrierUsername + " - " + rating + " stars";
    }
}
