package com.excilys.gradureau.computer_database.persistance;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionMysqlSingleton {

    private static Connection connection;

    private ConnectionMysqlSingleton() {
    }

    public static Connection getInstance(final String dbUrl, final String dbUser, final String dbPassword) {
        if (connection == null) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }

}
