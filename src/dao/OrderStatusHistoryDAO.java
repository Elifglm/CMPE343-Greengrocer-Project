package dao;

import model.OrderStatusHistory;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Order Status History operations.
 * 
 * INHERITANCE: Extends AbstractDAO<OrderStatusHistory>
 */
public class OrderStatusHistoryDAO extends AbstractDAO<OrderStatusHistory> {

    private static final OrderStatusHistoryDAO INSTANCE = new OrderStatusHistoryDAO();

    public static OrderStatusHistoryDAO getInstance() {
        return INSTANCE;
    }

    @Override
    protected String getTableName() {
        return "OrderStatusHistory";
    }

    @Override
    protected String getIdColumnName() {
        return "history_id";
    }

    @Override
    protected OrderStatusHistory mapResultSetToEntity(ResultSet rs) throws Exception {
        return new OrderStatusHistory(
                rs.getInt("history_id"),
                rs.getInt("order_id"),
                rs.getString("status"),
                rs.getTimestamp("changed_at"),
                rs.getString("changed_by"),
                rs.getString("notes"));
    }

    /**
     * Add status history entry.
     */
    public static boolean addHistory(int orderId, String status, String changedBy, String notes) {
        String sql = """
                INSERT INTO OrderStatusHistory(order_id, status, changed_by, notes)
                VALUES(?, ?, ?, ?)
                """;

        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ps.setString(2, status);
            ps.setString(3, changedBy);
            ps.setString(4, notes);

            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get status history for an order (time-ordered).
     */
    public static List<OrderStatusHistory> getHistoryByOrder(int orderId) {
        List<OrderStatusHistory> list = new ArrayList<>();

        String sql = """
                SELECT * FROM OrderStatusHistory
                WHERE order_id = ?
                ORDER BY changed_at DESC
                """;

        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, orderId);

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
}
