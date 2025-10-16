package com.example.jdbc;

import java.sql.*;

public class main {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/jdbc_demo";
        String user = "root";
        String password = "Menaha~17";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Connected!");

            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM person");

            while (rs.next()) {
                System.out.println(rs.getInt("id") + " | " +
                        rs.getString("name") + " | " +
                        rs.getString("email") + " | " +
                        rs.getInt("age"));
            }

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
