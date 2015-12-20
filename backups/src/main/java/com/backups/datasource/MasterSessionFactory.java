package com.backups.datasource;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class MasterSessionFactory {

    private static volatile  SqlSessionFactory sqlSessionFactory = null;  
	private MasterSessionFactory(){
	    	   new Thread(new Runnable() {
				@SuppressWarnings("resource")
				public void run() {
					 try {
				            String rs = "masterdb.xml";  
				            Reader reader = null;  
							 reader = Resources.getResourceAsReader(rs);
							 sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader); 
							 System.out.println(sqlSessionFactory);
							 new Scanner(System.in);
						} catch (IOException e) {
							e.printStackTrace();
						}
				}
			}).start();
			try {
				//该秒数为自己调整的秒数，暂时没有想到更好的方法
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	  	    
	}
	
	@SuppressWarnings("static-access")
	public static SqlSessionFactory getInstance(){
		if(null==sqlSessionFactory){
			synchronized (Thread.currentThread()) {
				if(null==sqlSessionFactory){
					return  new MasterSessionFactory().sqlSessionFactory;
				}
			}
		}
		return sqlSessionFactory;
	}
	
	public static void main(String[] args) throws SQLException {
		Connection connection= MasterSessionFactory.getInstance().openSession().getConnection();
		Statement stmt = connection.createStatement();
		String query = "SHOW CREATE TABLE user";
		ResultSet rs=stmt.executeQuery(query);
		while(rs.next()){
			System.out.println(rs.getString(2));
		}
	}


}
