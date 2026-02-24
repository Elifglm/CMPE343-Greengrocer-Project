package model;

import java.sql.Timestamp;

/**
 * Coupon entity for discount coupons.
 * 
 * INHERITANCE: Extends Entity
 */
public class Coupon extends Entity {

    private String code;
    private double discountPercent;
    private double discountAmount;
    private double minOrderAmount;
    private Timestamp validFrom;
    private Timestamp validUntil;
    private int maxUses;
    private int usedCount;
    private boolean isActive;
    private Timestamp createdAt;

    public Coupon() {
        super();
    }

    public Coupon(int couponId, String code, double discountPercent, double discountAmount,
            double minOrderAmount, Timestamp validFrom, Timestamp validUntil,
            int maxUses, int usedCount, boolean isActive) {
        super(couponId);
        this.code = code;
        this.discountPercent = discountPercent;
        this.discountAmount = discountAmount;
        this.minOrderAmount = minOrderAmount;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
        this.maxUses = maxUses;
        this.usedCount = usedCount;
        this.isActive = isActive;
    }

    // Getters and Setters
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public double getMinOrderAmount() {
        return minOrderAmount;
    }

    public void setMinOrderAmount(double minOrderAmount) {
        this.minOrderAmount = minOrderAmount;
    }

    public Timestamp getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Timestamp validFrom) {
        this.validFrom = validFrom;
    }

    public Timestamp getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(Timestamp validUntil) {
        this.validUntil = validUntil;
    }

    public int getMaxUses() {
        return maxUses;
    }

    public void setMaxUses(int maxUses) {
        this.maxUses = maxUses;
    }

    public int getUsedCount() {
        return usedCount;
    }

    public void setUsedCount(int usedCount) {
        this.usedCount = usedCount;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Check if coupon is valid for use.
     */
    public boolean isValid() {
        if (!isActive)
            return false;
        if (usedCount >= maxUses)
            return false;

        Timestamp now = new Timestamp(System.currentTimeMillis());
        if (validFrom != null && now.before(validFrom))
            return false;
        if (validUntil != null && now.after(validUntil))
            return false;

        return true;
    }

    /**
     * Calculate discount for given order total.
     */
    public double calculateDiscount(double orderTotal) {
        if (!isValid())
            return 0;
        if (orderTotal < minOrderAmount)
            return 0;

        double discount = 0;
        if (discountPercent > 0) {
            discount = orderTotal * (discountPercent / 100.0);
        }
        if (discountAmount > 0) {
            discount = Math.max(discount, discountAmount);
        }

        return Math.min(discount, orderTotal); // Don't exceed order total
    }

    @Override
    public String getDisplayName() {
        if (discountPercent > 0) {
            return code + " - " + discountPercent + "% off";
        }
        return code + " - " + discountAmount + " TL off";
    }
}
