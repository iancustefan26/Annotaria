package org.example;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        // test for database
        String sql = "select 1 from dual";
        try (Connection conn = DatabaseConnectionPool.getPool().getConnection();
        ){
            ResultSet rs = conn.createStatement().executeQuery(sql);
            if(rs.next()){
                System.out.println(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}