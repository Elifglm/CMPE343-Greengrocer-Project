package dao;

import model.*;
import util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for User/Person operations.
 * 
 * INHERITANCE: Extends AbstractDAO<Person>
 * POLYMORPHISM: Factory Pattern for Person subtypes
 */
public class UserDAO extends AbstractDAO<Person> {

    private static final UserDAO INSTANCE = new UserDAO();

    public static UserDAO getInstance() {
        return INSTANCE;
    }

    @Override
    protected String getTableName() {
        return "UserInfo";
    }

    @Override
    protected String getIdColumnName() {
        return "username";
    }

    // POLYMORPHISM: Factory Pattern - creates correct Person subclass based on role
    @Override
    protected Person mapResultSetToEntity(ResultSet rs) throws Exception {
        String role = rs.getString("role");
        String username = rs.getString("username");
        String password = rs.getString("password");
        String address = rs.getString("address");
        String phone = rs.getString("phone");

        System.out.println("  [mapResultSetToEntity] role='" + role + "', username='" + username + "'");

        Person person = null;
        if ("CUSTOMER".equalsIgnoreCase(role)) {
            person = new Customer(username, password, address, phone);
            System.out.println("  [mapResultSetToEntity] Created Customer object");
        } else if ("CARRIER".equalsIgnoreCase(role)) {
            person = new Carrier(username, password, address, phone);
            System.out.println("  [mapResultSetToEntity] Created Carrier object, getRole()='" + person.getRole() + "'");
        } else if ("OWNER".equalsIgnoreCase(role)) {
            person = new Owner(username, password);
            System.out.println("  [mapResultSetToEntity] Created Owner object");
        } else {
            System.out.println("  [mapResultSetToEntity] Unknown role '" + role + "', returning null");
        }
        return person;
    }

    // ========== STATIC METHODS ==========

    public static Person findByUsername(String username) {
        String sql = "SELECT * FROM UserInfo WHERE username = ?";
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

    public static Person authenticateAndGetPerson(String username, String password) {
        String sql = "SELECT * FROM UserInfo WHERE username = ? AND password = ?";

        // DEBUG
        System.out.println("=== LOGIN DEBUG ===");
        System.out.println("Attempting login with:");
        System.out.println("  Username: '" + username + "' (length: " + username.length() + ")");
        System.out.println("  Password: '" + password + "' (length: " + password.length() + ")");

        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            System.out.println("  SQL: " + sql);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("  ✅ User found in database!");
                    System.out.println("  Role: " + rs.getString("role"));
                    return INSTANCE.mapResultSetToEntity(rs);
                } else {
                    System.out.println("  ❌ No user found with these credentials!");

                    // Check if username exists at all
                    String checkSql = "SELECT username, password, role FROM UserInfo WHERE username = ?";
                    try (PreparedStatement checkPs = con.prepareStatement(checkSql)) {
                        checkPs.setString(1, username);
                        try (ResultSet checkRs = checkPs.executeQuery()) {
                            if (checkRs.next()) {
                                System.out.println("  Username exists but password mismatch:");
                                System.out.println("    DB Password: '" + checkRs.getString("password") + "'");
                                System.out.println("    Input Password: '" + password + "'");
                            } else {
                                System.out.println("  Username '" + username + "' does not exist in database");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("  ❌ ERROR: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static boolean updateProfile(String username, String address, String phone) {
        String sql = "UPDATE UserInfo SET address=?, phone=? WHERE username=?";
        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            if (address == null || address.trim().isEmpty()) {
                ps.setNull(1, java.sql.Types.VARCHAR);
            } else {
                ps.setString(1, address.trim());
            }

            if (phone == null || phone.trim().isEmpty()) {
                ps.setNull(2, java.sql.Types.VARCHAR);
            } else {
                ps.setString(2, phone.trim());
            }

            ps.setString(3, username);
            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ========== LOGIN/REGISTER ==========

    public static boolean authenticate(String username, String password) {
        String sql = "SELECT COUNT(*) FROM UserInfo WHERE username=? AND password=?";
        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);
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

    public static boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM UserInfo WHERE username=?";
        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
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

    /**
     * Check if phone number already exists in database.
     */
    public static boolean phoneExists(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }

        String sql = "SELECT COUNT(*) FROM UserInfo WHERE phone=?";
        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, phone.trim());
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

    public static boolean registerCustomer(String username, String password, String address, String phone) {
        String sql = "INSERT INTO UserInfo(username, password, role, address, phone) VALUES(?,?,?,?,?)";
        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, "CUSTOMER");

            if (address == null) {
                ps.setNull(4, java.sql.Types.VARCHAR);
            } else {
                ps.setString(4, address);
            }

            if (phone == null) {
                ps.setNull(5, java.sql.Types.VARCHAR);
            } else {
                ps.setString(5, phone);
            }

            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Backward compatibility
    public static Person getUserInfo(String username) {
        return findByUsername(username);
    }

    public static boolean updateProfileByUsername(String username, String address, String phone) {
        return updateProfile(username, address, phone);
    }

    // ========== CARRIER OPERATIONS ==========

    // Get all carriers (for owner view).
    public static List<Carrier> getAllCarriers() {
        List<Carrier> list = new ArrayList<>();
        String sql = "SELECT * FROM UserInfo WHERE role='CARRIER'";

        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Carrier(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("address"),
                        rs.getString("phone")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Employ (add) a new carrier.
    public static boolean employCarrier(String username, String password, String address, String phone) {
        if (usernameExists(username)) {
            System.err.println("Cannot employ carrier: username already exists");
            return false;
        }

        String sql = "INSERT INTO UserInfo(username,password,role,address,phone) VALUES(?,?,?,?,?)";
        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, "CARRIER");

            if (address == null) {
                ps.setNull(4, java.sql.Types.VARCHAR);
            } else {
                ps.setString(4, address);
            }

            if (phone == null) {
                ps.setNull(5, java.sql.Types.VARCHAR);
            } else {
                ps.setString(5, phone);
            }

            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Fire (delete) a carrier.
    public static boolean fireCarrier(String username) {
        // Check if carrier has active orders
        String checkSql = "SELECT COUNT(*) FROM Orders WHERE carrier_username=? AND status IN ('NEW','IN_PROGRESS')";
        try (Connection con = DBUtil.getConnection();
                PreparedStatement checkPs = con.prepareStatement(checkSql)) {

            checkPs.setString(1, username);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    System.err.println("Cannot fire carrier with active orders");
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        // Delete carrier
        String sql = "DELETE FROM UserInfo WHERE username=? AND role='CARRIER'";
        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            return ps.executeUpdate() == 1;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get all customers (for owner view).
    public static List<Customer> getAllCustomers() {
        List<Customer> list = new ArrayList<>();
        String sql = "SELECT * FROM UserInfo WHERE role='CUSTOMER'";

        try (Connection con = DBUtil.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Customer(
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("address"),
                        rs.getString("phone")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
