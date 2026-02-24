package model;

/**
 * CartItem entity representing items in customer's shopping cart.
 * 
 * INHERITANCE: Extends Entity
 * ENCAPSULATION: Private fields with getters/setters
 * POLYMORPHISM: Overrides getDisplayName()
 */
public class CartItem extends Entity {

    // ENCAPSULATION: Private fields
    private int productId;
    private String name;
    private double unitPrice;
    private double kg;

    // Constructor
    public CartItem(int productId, String name, double unitPrice, double kg) {
        super(productId);
        this.productId = productId;
        this.name = name;
        this.unitPrice = unitPrice;
        this.kg = kg;
    }

    // ENCAPSULATION: Getters/Setters
    public int getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public double getKg() {
        return kg;
    }

    public void setKg(double kg) {
        this.kg = kg;
    }

    // Business logic method
    public double getSubTotal() {
        return unitPrice * kg;
    }

    // POLYMORPHISM: Override abstract method from Entity
    @Override
    public String getDisplayName() {
        return name + " (" + kg + " kg)";
    }
}
