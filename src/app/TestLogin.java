package app;

import dao.UserDAO;
import util.DBUtil;
import java.sql.*;

public class TestLogin {
    public static void main(String[] args) {
        System.out.println("=== Testing DB Connection ===");
        try (Connection con = DBUtil.getConnection()) {
            System.out.println("DB CONNECTION: OK");

            // Check if cust user exists
            System.out.println("\n=== Checking 'cust' user ===");
            String sql = "SELECT * FROM UserInfo WHERE username = 'cust'";
            try (PreparedStatement ps = con.prepareStatement(sql);
                    ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("User found!");
                    System.out.println("  username: " + rs.getString("username"));
                    System.out.println("  password: " + rs.getString("password"));
                    System.out.println("  role: " + rs.getString("role"));
                } else {
                    System.out.println("User 'cust' NOT FOUND!");
                    System.out.println("Creating user...");

                    String insert = "INSERT INTO UserInfo (username, password, role) VALUES ('cust', 'cust', 'customer')";
                    try (PreparedStatement insertPs = con.prepareStatement(insert)) {
                        insertPs.executeUpdate();
                        System.out.println("User 'cust' created successfully!");
                    }
                }
            }

            // Test authenticate
            System.out.println("\n=== Testing UserDAO.authenticate ===");
            model.Person person = UserDAO.authenticateAndGetPerson("cust", "cust");
            System.out.println("authenticateAndGetPerson('cust', 'cust') returned: " + person);

            if (person == null) {
                System.out.println("AUTH FAILED - Check password in database!");
            } else {
                System.out.println("AUTH SUCCESS - Role: " + person.getRole());
            }

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
