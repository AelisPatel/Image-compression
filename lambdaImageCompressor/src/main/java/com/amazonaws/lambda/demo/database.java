package com.amazonaws.lambda.demo;
import java.sql.Connection;
import java.sql.DriverManager;

public class database {

	public void insert(String bucketName, String s3Link1, String to_bucket, String s3Link) {
	
		System.out.println("before insert....");
		try{
			Class.forName("com.mysql.jdbc.Driver");
			Connection c= DriverManager.getConnection("jdbc:mysql://database-1.cbkfii1ye02m.us-east-1.rds.amazonaws.com/lambda","admin","12345678");
			java.sql.Statement s=c.createStatement();
			System.out.println("insert value");
			s.executeUpdate("INSERT INTO path(bucketName,s3Link1,to_bucket,s3Link)VALUES('"+bucketName+"','"+s3Link1+"','"+to_bucket+"','"+s3Link+"')");
			s.close();
			c.close();	
			System.out.println(" insert done....");
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	}
	/*static Connection cn=null;
	static final String HOSTNAME="jdbc:mysql://database-1.cbkfii1ye02m.us-east-1.rds.amazonaws.com/lambda";
	static final String USERNAME="admin";
	static final String PASSWORD="admin1234";
	
	public static Statement connection()
	{
		try {
			Class.forName("com.mysql.jdbc.Driver");
			cn = DriverManager.getConnection(HOSTNAME,USERNAME,PASSWORD);
			Statement s = cn.createStatement();
			return s;
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public void insert(String bucketName, String key, String to_bucket, String s3Link)
	{
		System.out.println("before insert....");
		try{
			
				Statement s = connection();
				
				s.executeUpdate("insert into path(bucketName,key,to_bucket,s3Link)  values('"+bucketName+"','"+key+"','"+to_bucket+"','"+s3Link+"')");
				s.close();
				cn.close();
			System.out.println("after insert....");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}*/
	
