package com.basic;

//Import required java libraries
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


//Extend HttpServlet class
public class URLshortener extends HttpServlet {

	public URLshortener() {
		super();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
   
		Connection connection=null;
	
		try {
			Class.forName("com.mysql.jdbc.Driver"); 
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/urls","root","1234");

			String url = request.getParameter("url");
			String short_url;
			PrintWriter out = response.getWriter();
			
			if(url != null) {
				String check = "SELECT count(*) from map WHERE url = ?";
				final PreparedStatement s = connection.prepareStatement(check);
				s.setString(1, url);
				ResultSet resultset = s.executeQuery();
				resultset.next();
				int res = resultset.getInt("count(*)");
				//out.println(res);
			
				//if already in DB
				if(res==1)
				{
					String query = "SELECT url, short_url from map WHERE url = ? OR short_url = ?";
					final PreparedStatement s1 = connection.prepareStatement(query);
					s1.setString(1, url);
					s1.setString(2, url);
					final ResultSet resultset1 = s1.executeQuery();
					resultset1.next();
					url = resultset1.getString("url");
					short_url = resultset1.getString("short_url");
					out.println(url + ": " + short_url);
				}
			
				//if not already in DB
				else
				{
					String keys = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
					String keyword = "";
					String q1 = "SELECT count(*) from map";
					final PreparedStatement s2 = connection.prepareStatement(q1);
					ResultSet result = s2.executeQuery();
					result.next();
					int index = result.getInt("count(*)") + 1;
					
					while(index != 0) {
						int temp = index % keys.length();
						keyword += keys.charAt(temp);
						index /= keys.length();
					}
					
					short_url = "https://sho.rt/" + keyword;
					
					String query = "INSERT INTO map(url,short_url) values(?,?)";
					PreparedStatement statement = connection.prepareStatement(query);
					statement.setString(1,url);
					statement.setString(2,short_url);
					int no_of_rows = statement.executeUpdate();
					System.out.println("No of rows affected : "+ no_of_rows);
		
					//PrintWriter out = response.getWriter();
					out.println("{\n\turl: " + url + "\n\tshort_url: " + short_url + "\n}");
				}
			}
		}
		
		catch(Exception e){
		}
	}
}
