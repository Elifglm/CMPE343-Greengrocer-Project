package dao;

import model.CarrierRating;
import util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Carrier Rating operations.
 * 
 * INHERITANCE: Extends AbstractDAO<CarrierRating>
 */
public class CarrierRatingDAO extends AbstractDAO<CarrierRating> {

    private static final CarrierRatingDAO INSTANCE = new CarrierRatingDAO();

    public static CarrierRatingDAO getInstance() {
        return INSTANCE;
    }

    @Override
    protected String getTableName() {
        return "CarrierRating";
    }

    @Override
    protected String getIdColumnName() {
        return "rating_id";
    }

    @Override
    protected CarrierRating mapResultSetToEntity(ResultSet rs) throws Exception {
        return new CarrierRating(
                rs.getInt("rating_id"),
                rs.getInt("order_id"),
                rs.getString("carrier_username"),
                rs.getString("customer_username"),
                rs.getInt("rating"),
                rs.getString("comment"),
                rs.getTimestamp("rated_at"));
    }

    /**
     * Add rating for a carrier.
     */
    public static boolean addRating(int orderId, String carrierUsername,
            String customerUsername, int rating, String comment) {
        String sql = """
                INSERT INTO CarrierRating(order_id, carrier_username, customer_username, rating, comment)
                VALUES(?, ?, ?, ?, ?)
                """;

        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ps.setString(2, carrierUsername);
            ps.setString(3, customerUsername);
            ps.setInt(4, rating);
            ps.setString(5, comment);

            return ps.executeUpdate() == 1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Check if order has been rated.
     */
    public static boolean isOrderRated(int orderId) {
        String sql = "SELECT 1 FROM CarrierRating WHERE order_id = ?";

        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, orderId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get average rating for a carrier.
     */
    public static double getCarrierAverageRating(String carrierUsername) {
        String sql = "SELECT AVG(rating) as avg_rating FROM CarrierRating WHERE carrier_username = ?";

        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, carrierUsername);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("avg_rating");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    /**
     * Get all ratings for a carrier.
     */
    public static List<CarrierRating> getRatingsByCarrier(String carrierUsername) {
        List<CarrierRating> list = new ArrayList<>();

        String sql = """
                SELECT * FROM CarrierRating
                WHERE carrier_username = ?
                ORDER BY rated_at DESC
                """;

        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, carrierUsername);

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
     * Get rating count for a carrier.
     */
    public static int getCarrierRatingCount(String carrierUsername) {
        String sql = "SELECT COUNT(*) as cnt FROM CarrierRating WHERE carrier_username = ?";

        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, carrierUsername);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cnt");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get all carrier ratings summary (for owner view).
     */
    public static List<CarrierRatingSummary> getAllCarrierRatings() {
        List<CarrierRatingSummary> list = new ArrayList<>();

        String sql = """
                SELECT carrier_username,
                       AVG(rating) as avg_rating,
                       COUNT(*) as rating_count
                FROM CarrierRating
                GROUP BY carrier_username
                ORDER BY avg_rating DESC
                """;

        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new CarrierRatingSummary(
                        rs.getString("carrier_username"),
                        rs.getDouble("avg_rating"),
                        rs.getInt("rating_count")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Summary class for carrier ratings.
     */
    public static class CarrierRatingSummary {
        private final String carrierUsername;
        private final double averageRating;
        private final int ratingCount;

        public CarrierRatingSummary(String carrierUsername, double averageRating, int ratingCount) {
            this.carrierUsername = carrierUsername;
            this.averageRating = averageRating;
            this.ratingCount = ratingCount;
        }

        public String getCarrierUsername() {
            return carrierUsername;
        }

        public double getAverageRating() {
            return averageRating;
        }

        public int getRatingCount() {
            return ratingCount;
        }
    }
}
