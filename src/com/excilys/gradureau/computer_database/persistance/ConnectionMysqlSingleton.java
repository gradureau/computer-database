package com.excilys.gradureau.computer_database.persistance;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionMysqlSingleton {
	
	private static Connection connection;
	
	private ConnectionMysqlSingleton() {}
	
	public static Connection getInstance(final String DB_URL, final String DB_USER, final String DB_PASSWORD) {
		if(connection == null) {
			try {
				Class.forName("com.mysql.jdbc.Driver");
				connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}
		}
		return connection;
	}

}
