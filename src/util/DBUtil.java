package util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBUtil {

    private static final String URL = "jdbc:mysql://localhost:3306/greengrocer?useSSL=false&allowPublicKeyRetrieval=true";

    private static final String USER = "root";
    private static final String PASS = "1234abcd";

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
