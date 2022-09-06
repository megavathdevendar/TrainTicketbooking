package com.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.train.Train;

public class TrainDao {
	

	
	public static Train findTrain(int train) {
		Train t = null;
		EestablationConnection ec = new EestablationConnection();
		try {
			
	        Connection connection = ec.getConnection();
	        System.out.println("Database Connected successfully ! \n");
	        String query = "select * from Train where Train_no = ?";
	        PreparedStatement stm = connection.prepareStatement(query);
	        stm.setInt(1, train);
	        
	        ResultSet rs =stm.executeQuery();

	        while(rs.next()) {
	        	int trainNo = rs.getInt("Train_no");
	        	String trainName = rs.getString("Train_name");
	        	String source = rs.getString("source");
	        	String destination = rs.getString("destination");
	        	double price = rs.getDouble("Ticket_price");
	        	
	        	t = new Train(trainNo,trainName,source,destination,price);
	        	return t;
	        	
	        }
	       
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return t;
	}
	
	
}
