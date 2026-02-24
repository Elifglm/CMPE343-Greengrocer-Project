package dao;

import model.Coupon;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Coupon operations.
 * 
 * INHERITANCE: Extends AbstractDAO<Coupon>
 */
public class CouponDAO extends AbstractDAO<Coupon> {

    private static final CouponDAO INSTANCE = new CouponDAO();

    public static CouponDAO getInstance() {
        return INSTANCE;
    }

    @Override
    protected String getTableName() {
        return "Coupon";
    }

    @Override
    protected String getIdColumnName() {
        return "coupon_id";
    }

    @Override
    protected Coupon mapResultSetToEntity(ResultSet rs) throws Exception {
        Coupon c = new Coupon(
                rs.getInt("coupon_id"),
                rs.getString("code"),
                rs.getDouble("discount_percent"),
                rs.getDouble("discount_amount"),
                rs.getDouble("min_order_amount"),
                rs.getTimestamp("valid_from"),
                rs.getTimestamp("valid_until"),
                rs.getInt("max_uses"),
                rs.getInt("used_count"),
                rs.getBoolean("is_active"));
        c.setCreatedAt(rs.getTimestamp("created_at"));
        return c;
    }

    /**
     * Create a new coupon.
     */
    public static boolean createCoupon(String code, double discountPercent, double discountAmount,
            double minOrderAmount, Timestamp validFrom, Timestamp validUntil,
            int maxUses) {
        String sql = """
                INSERT INTO Coupon(code, discount_percent, discount_amount, min_order_amount,
                                   valid_from, valid_until, max_uses)
                VALUES(?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, code.toUpperCase());
            ps.setDouble(2, discountPercent);
            ps.setDouble(3, discountAmount);
            ps.setDouble(4, minOrderAmount);
            ps.setTimestamp(5, validFrom);
            ps.setTimestamp(6, validUntil);
            ps.setInt(7, maxUses);

            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get coupon by code.
     */
    public static Coupon getCouponByCode(String code) {
        String sql = "SELECT * FROM Coupon WHERE code = ?";

        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, code.toUpperCase());

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
     * Use a coupon (increment used_count).
     */
    public static boolean useCoupon(String code) {
        String sql = """
                UPDATE Coupon
                SET used_count = used_count + 1
                WHERE code = ? AND is_active = TRUE AND used_count < max_uses
                """;

        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, code.toUpperCase());
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deactivate a coupon.
     */
    public static boolean deactivateCoupon(int couponId) {
        String sql = "UPDATE Coupon SET is_active = FALSE WHERE coupon_id = ?";

        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, couponId);
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get all coupons (for owner view).
     */
    public static List<Coupon> getAllCoupons() {
        List<Coupon> list = new ArrayList<>();

        String sql = "SELECT * FROM Coupon ORDER BY created_at DESC";

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
     * Get active coupons.
     */
    public static List<Coupon> getActiveCoupons() {
        List<Coupon> list = new ArrayList<>();

        String sql = """
                SELECT * FROM Coupon
                WHERE is_active = TRUE
                  AND used_count < max_uses
                  AND (valid_until IS NULL OR valid_until > CURRENT_TIMESTAMP)
                ORDER BY created_at DESC
                """;

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
     * Assign an existing coupon to a specific user (by user ID).
     * Only unassigned coupons can be assigned.
     *
     * @param code   coupon code
     * @param userId target user ID from userinfo table
     * @return true if assignment succeeded
     */
    public static boolean assignCouponToUser(String code, int userId) {
        String sql = """
                UPDATE Coupon
                SET assigned_user_id = ?
                WHERE code = ?
                  AND assigned_user_id IS NULL
                """;

        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, code.toUpperCase());
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get all coupons assigned to a specific user.
     *
     * @param userId user ID from userinfo table
     * @return list of coupons assigned to that user
     */
    public static List<Coupon> getCouponsByUser(int userId) {
        List<Coupon> list = new ArrayList<>();

        String sql = """
                SELECT * FROM Coupon
                WHERE assigned_user_id = ?
                ORDER BY created_at DESC
                """;

        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

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
     * Re-activate a previously deactivated coupon.
     *
     * @param couponId target coupon ID
     * @return true if activation succeeded
     */
    public static boolean activateCoupon(int couponId) {
        String sql = "UPDATE Coupon SET is_active = TRUE WHERE coupon_id = ?";

        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, couponId);
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Permanently delete a coupon.
     * Should only be used by the owner for cleanup of expired/unused coupons.
     *
     * @param couponId target coupon ID
     * @return true if deletion succeeded
     */
    public static boolean deleteCoupon(int couponId) {
        String sql = "DELETE FROM Coupon WHERE coupon_id = ?";

        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, couponId);
            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
