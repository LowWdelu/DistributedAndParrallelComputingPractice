package com.lowwdel.ParrallelComputing.SocketProdCons.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//利用MySQL数据库，将数据持久化存储
public class DatabaseUtil {
    private static final String URL = "jdbc:mysql://localhost:3306/message_queue?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "1234";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL,USER,PASSWORD);
    }
}