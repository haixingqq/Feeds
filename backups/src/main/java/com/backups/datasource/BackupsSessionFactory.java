package com.backups.datasource;

import java.io.IOException;
import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class BackupsSessionFactory {
    private static volatile  SqlSessionFactory sqlSessionFactory = null;  
	private BackupsSessionFactory(){
	    	   new Thread(new Runnable() {
				public void run() {
					 try {
				            String rs = "slavedb.xml";  
				            Reader reader = null;  
							 reader = Resources.getResourceAsReader(rs);
							 sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader); 
							 System.in.read();
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
					return  new BackupsSessionFactory().sqlSessionFactory;
				}
			}
		}
		return sqlSessionFactory;
	}
	
	public static void main(String[] args) {
		System.out.println(BackupsSessionFactory.getInstance().openSession());
	}

}
