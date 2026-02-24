package model;

/**
 * Product entity representing items sold in the store.
 * 
 * INHERITANCE: Extends Entity
 * ENCAPSULATION: Private fields with getters/setters
 * POLYMORPHISM: Overrides getDisplayName()
 */
public class Product extends Entity {

    // ENCAPSULATION: Private fields
    private String name;
    private double price;
    private double stock;
    private String type;
    private int threshold;
    private byte[] image;
    private double discountPercent;

    // Constructors
    public Product() {
        super();
    }

    public Product(int productId,
            String name,
            double price,
            double stock,
            String type,
            int threshold,
            byte[] image) {
        super(productId);
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.type = type;
        this.threshold = threshold;
        this.image = image;
    }

    public Product(int productId,
            String name,
            double price,
            double stock,
            String type,
            int threshold) {
        this(productId, name, price, stock, type, threshold, null);
    }

    // ENCAPSULATION: Getters/Setters

    // Backward compatibility: productId maps to inherited id
    public int getProductId() {
        return getId();
    }

    public void setProductId(int productId) {
        setId(productId);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getStock() {
        return stock;
    }

    public void setStock(double stock) {
        this.stock = stock;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
    }

    public double getOriginalPrice() {
        return price;
    }

    public double getEffectivePrice() {
        double discountedPrice = price;
        if (discountPercent > 0) {
            discountedPrice = price * (1 - discountPercent / 100.0);
        }
        return (stock <= threshold) ? discountedPrice * 2.0 : discountedPrice;
    }

    // POLYMORPHISM: Override abstract method from Entity
    @Override
    public String getDisplayName() {
        return name + " - " + String.format("%.2f TL", getEffectivePrice());
    }
}
