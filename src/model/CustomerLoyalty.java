package model;

import java.sql.Timestamp;

/**
 * Customer Loyalty entity for loyalty program.
 * 
 * INHERITANCE: Extends Entity
 */
public class CustomerLoyalty extends Entity {

    public enum Tier {
        BRONZE(0, 0),
        SILVER(500, 5),
        GOLD(1000, 10),
        PLATINUM(2500, 15);

        private final int minPoints;
        private final int discountPercent;

        Tier(int minPoints, int discountPercent) {
            this.minPoints = minPoints;
            this.discountPercent = discountPercent;
        }

        public int getMinPoints() {
            return minPoints;
        }

        public int getDiscountPercent() {
            return discountPercent;
        }

        public static Tier fromPoints(int points) {
            if (points >= PLATINUM.minPoints)
                return PLATINUM;
            if (points >= GOLD.minPoints)
                return GOLD;
            if (points >= SILVER.minPoints)
                return SILVER;
            return BRONZE;
        }
    }

    private String username;
    private int points;
    private Tier tier;
    private double totalSpent;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public CustomerLoyalty() {
        super();
        this.tier = Tier.BRONZE;
    }

    public CustomerLoyalty(int loyaltyId, String username, int points,
            String tierStr, double totalSpent) {
        super(loyaltyId);
        this.username = username;
        this.points = points;
        try {
            this.tier = Tier.valueOf(tierStr.toUpperCase());
        } catch (Exception e) {
            this.tier = Tier.BRONZE;
        }
        this.totalSpent = totalSpent;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
        updateTier();
    }

    public Tier getTier() {
        return tier;
    }

    public String getTierName() {
        return tier.name();
    }

    public double getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(double totalSpent) {
        this.totalSpent = totalSpent;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Add points and update tier.
     */
    public void addPoints(int additionalPoints) {
        this.points += additionalPoints;
        updateTier();
    }

    /**
     * Update tier based on current points.
     */
    private void updateTier() {
        this.tier = Tier.fromPoints(this.points);
    }

    /**
     * Get discount percentage based on tier.
     */
    public int getDiscountPercent() {
        return tier.getDiscountPercent();
    }

    /**
     * Calculate points to earn from order total.
     * 1 point per 10 TL spent.
     */
    public static int calculatePointsFromOrder(double orderTotal) {
        return (int) (orderTotal / 10);
    }

    @Override
    public String getDisplayName() {
        return username + " - " + tier.name() + " (" + points + " points)";
    }
}
