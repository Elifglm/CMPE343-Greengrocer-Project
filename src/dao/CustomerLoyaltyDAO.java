package dao;

import model.CustomerLoyalty;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Customer Loyalty operations.
 * 
 * INHERITANCE: Extends AbstractDAO<CustomerLoyalty>
 */
public class CustomerLoyaltyDAO extends AbstractDAO<CustomerLoyalty> {

    private static final CustomerLoyaltyDAO INSTANCE = new CustomerLoyaltyDAO();

    public static CustomerLoyaltyDAO getInstance() {
        return INSTANCE;
    }

    @Override
    protected String getTableName() {
        return "CustomerLoyalty";
    }

    @Override
    protected String getIdColumnName() {
        return "loyalty_id";
    }

    @Override
    protected CustomerLoyalty mapResultSetToEntity(ResultSet rs) throws Exception {
        CustomerLoyalty cl = new CustomerLoyalty(
                rs.getInt("loyalty_id"),
                rs.getString("username"),
                rs.getInt("points"),
                rs.getString("tier"),
                rs.getDouble("total_spent"));
        cl.setCreatedAt(rs.getTimestamp("created_at"));
        cl.setUpdatedAt(rs.getTimestamp("updated_at"));
        return cl;
    }

    /**
     * Get or create loyalty record for a customer.
     */
    public static CustomerLoyalty getOrCreate(String username) {
        CustomerLoyalty existing = getByUsername(username);
        if (existing != null)
            return existing;

        // Create new record
        String sql = "INSERT INTO CustomerLoyalty(username) VALUES(?)";

        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return getByUsername(username);
    }

    /**
     * Get loyalty by username.
     */
    public static CustomerLoyalty getByUsername(String username) {
        String sql = "SELECT * FROM CustomerLoyalty WHERE username = ?";

        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return INSTANCE.mapResultSetToEntity(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Add points from an order.
     */
    public static boolean addPointsFromOrder(String username, double orderTotal) {
        int points = CustomerLoyalty.calculatePointsFromOrder(orderTotal);

        // Get or create record
        CustomerLoyalty cl = getOrCreate(username);
        if (cl == null)
            return false;

        // Update points and total spent
        String sql = """
                UPDATE CustomerLoyalty
                SET points = points + ?,
                    total_spent = total_spent + ?,
                    tier = CASE
                        WHEN points + ? >= 2500 THEN 'PLATINUM'
                        WHEN points + ? >= 1000 THEN 'GOLD'
                        WHEN points + ? >= 500 THEN 'SILVER'
                        ELSE 'BRONZE'
                    END
                WHERE username = ?
                """;

        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, points);
            ps.setDouble(2, orderTotal);
            ps.setInt(3, points);
            ps.setInt(4, points);
            ps.setInt(5, points);
            ps.setString(6, username);

            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get all loyalty records (for owner view).
     */
    public static List<CustomerLoyalty> getAllLoyalty() {
        List<CustomerLoyalty> list = new ArrayList<>();

        String sql = "SELECT * FROM CustomerLoyalty ORDER BY points DESC";

        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(INSTANCE.mapResultSetToEntity(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Redeem (spend) loyalty points for a customer.
     * Fails if the customer does not have enough points.
     *
     * @param username    customer username
     * @param pointsToUse number of points to deduct
     * @return true if redemption succeeded, false if insufficient points
     */
    public static boolean redeemPoints(String username, int pointsToUse) {
        if (pointsToUse <= 0)
            return false;

        CustomerLoyalty cl = getByUsername(username);
        if (cl == null || cl.getPoints() < pointsToUse)
            return false;

        String sql = """
                UPDATE CustomerLoyalty
                SET points = points - ?,
                    tier = CASE
                        WHEN points - ? >= 2500 THEN 'PLATINUM'
                        WHEN points - ? >= 1000 THEN 'GOLD'
                        WHEN points - ? >= 500  THEN 'SILVER'
                        ELSE 'BRONZE'
                    END
                WHERE username = ? AND points >= ?
                """;

        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, pointsToUse);
            ps.setInt(2, pointsToUse);
            ps.setInt(3, pointsToUse);
            ps.setInt(4, pointsToUse);
            ps.setString(5, username);
            ps.setInt(6, pointsToUse);
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get top customers ranked by loyalty points (leaderboard).
     *
     * @param limit how many customers to return
     * @return list of CustomerLoyalty records sorted by points descending
     */
    public static List<CustomerLoyalty> getTopCustomers(int limit) {
        List<CustomerLoyalty> list = new ArrayList<>();

        String sql = """
                SELECT * FROM CustomerLoyalty
                ORDER BY points DESC
                LIMIT ?
                """;

        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, limit);

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
