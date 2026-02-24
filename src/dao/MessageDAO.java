package dao;

import model.Message;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Message operations.
 * Handles customer-owner communication.
 * 
 * INHERITANCE: Extends AbstractDAO<Message>
 */
public class MessageDAO extends AbstractDAO<Message> {

    private static final MessageDAO INSTANCE = new MessageDAO();

    public static MessageDAO getInstance() {
        return INSTANCE;
    }

    @Override
    protected String getTableName() {
        return "Messages";
    }

    @Override
    protected String getIdColumnName() {
        return "message_id";
    }

    @Override
    protected Message mapResultSetToEntity(ResultSet rs) throws Exception {
        Integer parentId = rs.getInt("parent_message_id");
        if (rs.wasNull())
            parentId = null;

        return new Message(
                rs.getInt("message_id"),
                rs.getString("sender_username"),
                rs.getString("receiver_username"),
                rs.getString("subject"),
                rs.getString("content"),
                rs.getBoolean("is_read"),
                parentId,
                rs.getTimestamp("sent_at"));
    }

    /**
     * Send a message.
     */
    public static boolean sendMessage(String sender, String receiver,
            String subject, String content, Integer parentId) {
        String sql = """
                INSERT INTO Messages(sender_username, receiver_username, subject, content, parent_message_id)
                VALUES(?, ?, ?, ?, ?)
                """;

        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, sender);
            ps.setString(2, receiver);
            ps.setString(3, subject);
            ps.setString(4, content);

            if (parentId == null) {
                ps.setNull(5, Types.INTEGER);
            } else {
                ps.setInt(5, parentId);
            }

            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get messages received by a user.
     */
    public static List<Message> getReceivedMessages(String username) {
        List<Message> list = new ArrayList<>();

        String sql = """
                SELECT * FROM Messages
                WHERE receiver_username = ?
                ORDER BY sent_at DESC
                """;

        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(INSTANCE.mapResultSetToEntity(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Get messages sent by a user.
     */
    public static List<Message> getSentMessages(String username) {
        List<Message> list = new ArrayList<>();

        String sql = """
                SELECT * FROM Messages
                WHERE sender_username = ?
                ORDER BY sent_at DESC
                """;

        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(INSTANCE.mapResultSetToEntity(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Get unread message count.
     */
    public static int getUnreadCount(String username) {
        String sql = "SELECT COUNT(*) FROM Messages WHERE receiver_username = ? AND is_read = FALSE";

        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Mark message as read.
     */
    public static boolean markAsRead(int messageId) {
        String sql = "UPDATE Messages SET is_read = TRUE WHERE message_id = ?";

        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, messageId);
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get conversation thread.
     */
    public static List<Message> getConversation(int messageId) {
        List<Message> list = new ArrayList<>();

        // Get the message and all replies
        String sql = """
                SELECT * FROM Messages
                WHERE message_id = ? OR parent_message_id = ?
                ORDER BY sent_at ASC
                """;

        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, messageId);
            ps.setInt(2, messageId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(INSTANCE.mapResultSetToEntity(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Get first owner username for messaging.
     */
    public static String getOwnerUsername() {
        String sql = "SELECT username FROM UserInfo WHERE role = 'owner' LIMIT 1";

        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getString("username");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "owner"; // default
    }
}
