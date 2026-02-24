package dao;

import model.Product;
import util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Product operations.
 * 
 * INHERITANCE: Extends AbstractDAO<Product>
 * POLYMORPHISM: Implements abstract methods from AbstractDAO
 * ENCAPSULATION: Private helper methods
 */
public class ProductDAO extends AbstractDAO<Product> {

    // Singleton Pattern
    private static final ProductDAO INSTANCE = new ProductDAO();

    public static ProductDAO getInstance() {
        return INSTANCE;
    }

    // POLYMORPHISM: Implement abstract methods from AbstractDAO
    @Override
    protected String getTableName() {
        return "Product";
    }

    @Override
    protected String getIdColumnName() {
        return "product_id";
    }

    @Override
    protected Product mapResultSetToEntity(ResultSet rs) throws Exception {
        Product p = new Product();
        p.setId(rs.getInt("product_id"));
        p.setName(rs.getString("name"));
        p.setPrice(rs.getDouble("price"));
        p.setStock(rs.getDouble("stock"));
        p.setType(rs.getString("type"));
        p.setThreshold(rs.getInt("threshold"));
        p.setImage(rs.getBytes("image"));
        try {
            double discount = rs.getDouble("discount_percent");
            p.setDiscountPercent(discount);
        } catch (Exception e) {
            System.err.println("Error reading discount_percent: " + e.getMessage());
            p.setDiscountPercent(0);
        }
        return p;
    }

    // ========== STATIC METHODS ==========

    /**
     * Check if product with same name and type already exists.
     */
    public static boolean productExists(String name, String type) {
        String sql = "SELECT COUNT(*) FROM Product WHERE LOWER(name) = LOWER(?) AND type = ?";

        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name.trim());
            ps.setString(2, type);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean addProduct(String name,
            double price,
            double stock,
            String type,
            int threshold) {
        return addProduct(name, price, stock, type, threshold, null);
    }

    public static boolean addProduct(String name,
            double price,
            double stock,
            String type,
            int threshold,
            byte[] image) {

        // Threshold validation: minimum 1
        if (threshold < 1)
            threshold = 1;

        String sql = """
                INSERT INTO Product(name, price, stock, type, threshold, image)
                VALUES(?,?,?,?,?,?)
                """;

        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.setDouble(2, price);
            ps.setDouble(3, stock);
            ps.setString(4, type);
            ps.setInt(5, threshold);

            if (image == null) {
                ps.setNull(6, java.sql.Types.BLOB);
            } else {
                ps.setBytes(6, image);
            }

            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get all products (for owner view).
     */
    public static List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();

        String sql = """
                SELECT product_id, name, price, stock, type, threshold, image
                FROM Product
                ORDER BY name ASC
                """;

        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Product(
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getDouble("stock"),
                        rs.getString("type"),
                        rs.getInt("threshold"),
                        rs.getBytes("image")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Get products by type with stock > 0 (for customer view).
     * Alphabetically sorted.
     */
    public static List<Product> getProductsByType(String type) {
        List<Product> list = new ArrayList<>();

        String sql = """
                SELECT product_id, name, price, stock, type, threshold, image, discount_percent
                FROM Product
                WHERE type = ? AND stock >= 0
                ORDER BY name ASC
                """;

        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, type);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product p = new Product(
                            rs.getInt("product_id"),
                            rs.getString("name"),
                            rs.getDouble("price"),
                            rs.getDouble("stock"),
                            rs.getString("type"),
                            rs.getInt("threshold"),
                            rs.getBytes("image"));
                    try {
                        p.setDiscountPercent(rs.getDouble("discount_percent"));
                    } catch (Exception e) {
                        p.setDiscountPercent(0);
                    }
                    list.add(p);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Get fruits with stock > 0 (for customer view).
     */
    public static List<Product> getFruits() {
        return getProductsByType("fruit");
    }

    /**
     * Get vegetables with stock > 0 (for customer view).
     */
    public static List<Product> getVegetables() {
        return getProductsByType("vegetable");
    }

    /**
     * Get all available products with stock > 0 (for customer view).
     */
    public static List<Product> getAvailableProducts() {
        List<Product> list = new ArrayList<>();

        String sql = """
                SELECT product_id, name, price, stock, type, threshold, image, discount_percent
                FROM Product
                WHERE stock >= 0
                ORDER BY name ASC
                """;

        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Product p = new Product(
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getDouble("stock"),
                        rs.getString("type"),
                        rs.getInt("threshold"),
                        rs.getBytes("image"));
                try {
                    p.setDiscountPercent(rs.getDouble("discount_percent"));
                } catch (Exception e) {
                    p.setDiscountPercent(0);
                }
                list.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static List<Product> getLowStockProducts() {
        List<Product> list = new ArrayList<>();

        String sql = """
                SELECT product_id, name, price, stock, type, threshold, image, discount_percent
                FROM Product
                WHERE stock <= threshold
                ORDER BY stock
                """;

        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Product p = new Product(
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getDouble("stock"),
                        rs.getString("type"),
                        rs.getInt("threshold"),
                        rs.getBytes("image"));
                try {
                    p.setDiscountPercent(rs.getDouble("discount_percent"));
                } catch (Exception e) {
                    p.setDiscountPercent(0);
                }
                list.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static boolean updatePrice(int productId, double newPrice) {
        String sql = "UPDATE Product SET price=? WHERE product_id=?";

        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, newPrice);
            ps.setInt(2, productId);

            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateThreshold(int productId, int newThreshold) {
        // Threshold validation: minimum 1
        if (newThreshold < 1) {
            return false; // Reject invalid threshold
        }

        String sql = "UPDATE Product SET threshold=? WHERE product_id=?";

        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, newThreshold);
            ps.setInt(2, productId);

            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean addStock(int productId, double addKg) {
        if (addKg <= 0)
            return false;

        String sql = "UPDATE Product SET stock = stock + ? WHERE product_id=?";

        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, addKg);
            ps.setInt(2, productId);

            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean decreaseStock(int productId, double decreaseKg) {
        if (decreaseKg <= 0)
            return false;

        String sql = "UPDATE Product SET stock = stock - ? WHERE product_id=? AND stock >= ?";

        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, decreaseKg);
            ps.setInt(2, productId);
            ps.setDouble(3, decreaseKg);

            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateDiscount(int productId, double discountPercent) {
        if (discountPercent < 0 || discountPercent > 100)
            return false;

        String sql = "UPDATE Product SET discount_percent=? WHERE product_id=?";

        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setDouble(1, discountPercent);
            ps.setInt(2, productId);

            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean decreaseStockKg(Connection con,
            int productId,
            double kg) throws Exception {

        if (kg <= 0)
            return false;

        String sql = """
                UPDATE Product
                SET stock = stock - ?
                WHERE product_id = ?
                  AND stock >= ?
                """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, kg);
            ps.setInt(2, productId);
            ps.setDouble(3, kg);
            return ps.executeUpdate() == 1;
        }
    }

    /**
     * Delete a product.
     * Also deletes related SystemMessage alerts, OrderItems, and Orders.
     */
    public static boolean deleteProduct(int productId) {
        try (Connection con = DBUtil.getConnection()) {
            // 1. Delete related system messages (alerts)
            String deleteAlertsSQL = "DELETE FROM SystemMessage WHERE related_product_id = ?";
            try (PreparedStatement ps = con.prepareStatement(deleteAlertsSQL)) {
                ps.setInt(1, productId);
                ps.executeUpdate();
            }

            // 2. Delete related order items
            String deleteOrderItemsSQL = "DELETE FROM OrderItems WHERE product_id = ?";
            try (PreparedStatement ps = con.prepareStatement(deleteOrderItemsSQL)) {
                ps.setInt(1, productId);
                ps.executeUpdate();
            }

            // 3. Delete related orders (handling legacy product_id column in Orders table)
            String deleteOrdersSQL = "DELETE FROM Orders WHERE product_id = ?";
            try (PreparedStatement ps = con.prepareStatement(deleteOrdersSQL)) {
                ps.setInt(1, productId);
                ps.executeUpdate();
            }

            // 4. Finally delete the product
            String sql = "DELETE FROM Product WHERE product_id = ?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, productId);
                return ps.executeUpdate() == 1;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update product name.
     */
    public static boolean updateName(int productId, String newName) {
        String sql = "UPDATE Product SET name=? WHERE product_id=?";

        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, newName);
            ps.setInt(2, productId);

            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get product by ID.
     */
    public static Product getProductById(int productId) {
        return INSTANCE.findById(productId);
    }
}
