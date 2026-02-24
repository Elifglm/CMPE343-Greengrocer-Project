package app;

import util.DBUtil;

public class TestDB {
    public static void main(String[] args) {
        try {
            DBUtil.getConnection();
            System.out.println("DB CONNECTION OK");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
