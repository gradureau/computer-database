import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.excilys.gradureau.computer_database.persistance.ConnectionMysqlSingleton;

public class Main {

	public static void main(String[] args) {
		System.out.println("Hello World!");
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = ConnectionMysqlSingleton.getInstance();
			String query = "SELECT name FROM company;";
			Statement stmt = conn.createStatement();
			ResultSet results = stmt.executeQuery(query);
			
			while(results.next()) {
				String name = results.getString("name");
				System.out.println(name);
			}
			
			conn.close();
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
