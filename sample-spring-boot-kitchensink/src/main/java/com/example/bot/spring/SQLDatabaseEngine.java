package com.example.bot.spring;

import lombok.extern.slf4j.Slf4j;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.*;
import java.net.URISyntaxException;
import java.net.URI;

@Slf4j
public class SQLDatabaseEngine extends DatabaseEngine {
	
	public String searchForCount() throws Exception {
		int count = 0;
		try {
			Connection connection = getConnection();
			PreparedStatement stmt = connection.prepareStatement(
			"SELECT keyword, response, count FROM bot");
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				count = count + rs.getInt(3);

			}
			rs.close();
			stmt.close();
			connection.close();
			
		} catch (Exception e) {
			log.info("IOException while reading file: {}", e.toString());
			
		}
		return "You got the keyword " +count + " times!";
	}
	
	@Override

	String search(String text) throws Exception {
		//Write your code here
		String result = null;
		int count = 0;
		try {
			Connection connection = getConnection();
			PreparedStatement stmt = connection.prepareStatement(
			"SELECT keyword, response, count FROM bot");
//			 where keyword like concat('%', ?, '%')
//			stmt.setString(0,text);
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				count = count + rs.getInt(3);
				if (text.toLowerCase().contains(rs.getString(1).toLowerCase())) {
					result = rs.getString(2);
					int newCount = rs.getInt(3) + 1; 
					PreparedStatement stmt2 = connection.prepareStatement(
					"UPDATE bot SET count = " + newCount + "WHERE keyword = " + rs.getString(1)
							);
					stmt2.executeUpdate();
				}

			}
			rs.close();
			stmt.close();
			connection.close();
			
		} catch (Exception e) {
			log.info("IOException while reading file: {}", e.toString());
			
		}
		if (result != null)
			return result;
		throw new Exception("NOT FOUND");
	}
	
	
	private Connection getConnection() throws URISyntaxException, SQLException {
		Connection connection;
		URI dbUri = new URI(System.getenv("DATABASE_URL"));

		String username = dbUri.getUserInfo().split(":")[0];
		String password = dbUri.getUserInfo().split(":")[1];
		String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() +  "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";

		log.info("Username: {} Password: {}", username, password);
		log.info ("dbUrl: {}", dbUrl);
		
		connection = DriverManager.getConnection(dbUrl, username, password);

		return connection;
	}

}
