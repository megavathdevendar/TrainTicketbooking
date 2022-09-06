package com.ticket;

import com.passenger.Passenger;
import com.ticket.Ticket;
import com.train.Train;
import com.util.EestablationConnection;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;



public class Ticket {
	
	private String pnr_no;
	private Date travel_date;
	private static HashMap<Passenger,Double> passengers = new HashMap<>();
	private Train train;
	
	
	public Ticket(Date travel_date,Train train) {
		this.travel_date=travel_date;
		this.train=train;

	}
	
	
	@SuppressWarnings("deprecation")
	public String generatePNR() {
		
		String source = train.getSource();
		String destination = train.getDestination();
		
		int year= travel_date.getYear()+1900;
		int month = travel_date.getMonth()+1;
		int date = travel_date.getDate();
		
		//System.out.println((char)source.charAt(0)+(char)destination.charAt(0));
		char src = source.charAt(0); 
		char dest = destination.charAt(0);
		StringBuilder sb = new StringBuilder();
		
		//get the last id
		final int id=getLastId();
			
		pnr_no = sb.append(src).append(dest).append("_").append(year).append(month).append(date).append("_").append(id).toString();
		
		return pnr_no;
	}
	
	public double calculatePassengerFair(Passenger p) {
		
		if(p.getGender()=='F')
			return train.getTicket_price()*0.25;
		else if(p.getAge()<=12) {
			return train.getTicket_price()*.50;
		}
		else if(p.getAge()>=60) {
			return train.getTicket_price()*.60;
		}
		
		return train.getTicket_price();
	}
	
	public void addPassenger(String name,int age,char gender){
		Passenger passenger = new Passenger(name,age,gender);
		double fair = calculatePassengerFair(passenger);
		passengers.put(passenger, fair);
	}
	
	public double calculateTotalTicketPrice() {
		double total =0;
		for(Map.Entry<Passenger,Double> entry : passengers.entrySet()) {
			total=total+entry.getValue();
		}
		return total;
	}
	
	@SuppressWarnings("deprecation")
	public StringBuilder generateTicket() {
		
		StringBuilder sb = new StringBuilder();
		
		String pnr = generatePNR();
		int trainNo = train.getTrain_no();
		String trainName=train.getTrain_name();
		String from = train.getSource();
		String to = train.getDestination();
		
		String date= String.valueOf(travel_date.getDate());
		String month = String.valueOf(travel_date.getMonth()+1);
		String year=String.valueOf(travel_date.getYear()+1990);
		
		//for printing the date in the given format
		String newDate = date+"/"+month+"/"+year;
		
		sb.append("PNR\t\t\t\t:\t"+pnr_no+"\nTrainNo\t\t\t:\t"+trainNo+"\nTrain Name\t\t:\t"+trainName+"\nFrom\t\t\t:\t"+from+"\nTo\t\t\t\t:\t"+to+"\nTravel Date\t\t:\t"+newDate);
		sb.append("\n\nPassengers:\n");
		sb.append("---------------------------------------------------------\n");
		sb.append("Name\t\tAge\t\tGender\t\tFair\n");
		sb.append("---------------------------------------------------------\n");
		for(Map.Entry<Passenger,Double> entry : passengers.entrySet()) {
			Passenger p = entry.getKey();
			sb.append(p.getName()+"\t\t"+p.getAge()+"\t\t"+p.getGender()+"\t\t"+entry.getValue()+"\n");
		}
		
		sb.append("\nTotal Price: "+calculateTotalTicketPrice());
		return sb;
		
	}
	
	public void writeTicket() {
		//create a file with PNR number 
		
		StringBuilder s = generateTicket();
		try {
			FileWriter writer = new FileWriter(generatePNR()+".txt");
			
			writer.write(s.toString());
			writer.close();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		 
		
	}
	
	public void insertIntoTicketTable() {
		
		EestablationConnection ec = new EestablationConnection();
		Connection connection = ec.getConnection();
		try {
			
			SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");  
		    String strDate = formatter.format(travel_date);  
			
			String query = "insert into Ticket(pnr_no,train_no,train_name,train_source,train_destination,travel_date,no_of_passengers,total_fair) values (?,?,?,?,?,?,?,?)";
			PreparedStatement pstm = connection.prepareStatement(query);
	
			pstm.setString(1, generatePNR());
			pstm.setInt(2, train.getTrain_no());
			pstm.setString(3, train.getTrain_name());
			pstm.setString(4, train.getSource());
			pstm.setString(5, train.getDestination());
			pstm.setString(6, strDate);
			pstm.setInt(7, passengers.size());
			pstm.setDouble(8, calculateTotalTicketPrice());
			
			pstm.executeUpdate();
			
			pstm.close();
			connection.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public int getLastId() {
		int id=0;
		
		try {
			EestablationConnection ec = new EestablationConnection();
			Connection connection = ec.getConnection();
			String query="SELECT * FROM TICKET WHERE id=(SELECT max(id) FROM TICKET)";
			PreparedStatement pstm = connection.prepareStatement(query);
			ResultSet rs = pstm.executeQuery();
			while(rs.next()) {
				id=rs.getInt(1);
			}
			return id;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return id;
	}
	
}