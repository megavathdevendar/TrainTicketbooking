

package com.util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class EestablationConnection {
	
	private static String url= "jdbc:mysql://localhost:3306/demoDBdevya";
	private static String user ="root";
	private static String password = "Devendar@1122";
	private static Connection connection = null;

	public  Connection getConnection() {
		try {
			connection = DriverManager.getConnection(url, user, password);
			return connection;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return connection;
	}
}
