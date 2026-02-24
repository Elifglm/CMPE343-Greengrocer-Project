package dao;

import util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SystemMessageDAO {

    public static class SystemMessage {
        private int id;
        private String messageType;
        private String title;
        private String message;
        private Integer relatedProductId;
        private Timestamp createdAt;
        private boolean isRead;

        public SystemMessage(int id, String messageType, String title, String message,
                Integer relatedProductId, Timestamp createdAt, boolean isRead) {
            this.id = id;
            this.messageType = messageType;
            this.title = title;
            this.message = message;
            this.relatedProductId = relatedProductId;
            this.createdAt = createdAt;
            this.isRead = isRead;
        }

        public int getId() {
            return id;
        }

        public String getMessageType() {
            return messageType;
        }

        public String getTitle() {
            return title;
        }

        public String getMessage() {
            return message;
        }

        public Integer getRelatedProductId() {
            return relatedProductId;
        }

        public Timestamp getCreatedAt() {
            return createdAt;
        }

        public boolean isRead() {
            return isRead;
        }
    }

    public static List<SystemMessage> getAllMessages() {
        List<SystemMessage> messages = new ArrayList<>();
        String sql = "SELECT * FROM SystemMessage ORDER BY created_at DESC";

        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                messages.add(new SystemMessage(
                        rs.getInt("id"),
                        rs.getString("message_type"),
                        rs.getString("title"),
                        rs.getString("message"),
                        rs.getObject("related_product_id") != null ? rs.getInt("related_product_id") : null,
                        rs.getTimestamp("created_at"),
                        rs.getBoolean("is_read")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return messages;
    }

    public static int getUnreadCount() {
        String sql = "SELECT COUNT(*) FROM SystemMessage WHERE is_read = FALSE";
        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next())
                return rs.getInt(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void markAsRead(int id) {
        String sql = "UPDATE SystemMessage SET is_read = TRUE WHERE id = ?";
        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createLowStockAlert(int productId, String productName, double currentStock, int threshold) {
        if (alertExistsForProduct(productId))
            return;

        String sql = "INSERT INTO SystemMessage (message_type, title, message, related_product_id) VALUES (?, ?, ?, ?)";
        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, "LOW_STOCK");
            ps.setString(2, "Low Stock Alert!");
            ps.setString(3, String.format("%s stock has fallen to %.1f kg (Threshold: %d kg)",
                    productName, currentStock, threshold));
            ps.setInt(4, productId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean alertExistsForProduct(int productId) {
        String sql = "SELECT COUNT(*) FROM SystemMessage WHERE related_product_id = ? AND is_read = FALSE AND message_type = 'LOW_STOCK'";
        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void deleteMessage(int id) {
        String sql = "DELETE FROM SystemMessage WHERE id = ?";
        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void checkAndCreateLowStockAlerts() {
        List<model.Product> lowStockProducts = ProductDAO.getLowStockProducts();
        for (model.Product p : lowStockProducts) {
            createLowStockAlert(p.getProductId(), p.getName(), p.getStock(), p.getThreshold());
        }
    }
}
