package com.excilys.gradureau.computer_database.persistance;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionMysqlSingleton {
	
	private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/computer-database-db";
	
	private static final String DB_USER = "admincdb";
	
	private static final String DB_PASSWORD = "qwerty1234";
	
	private static Connection connection;
	
	private ConnectionMysqlSingleton() {}
	
	public static Connection getInstance() {
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
