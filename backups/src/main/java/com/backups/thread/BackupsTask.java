package com.backups.thread;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Invocation;

import com.backups.annotation.BackupTable;
import com.backups.datasource.BackupsSessionFactory;
import com.backups.datasource.MasterSessionFactory;
import com.backups.utils.db.DBResultsUtil;

public class BackupsTask {
		public BackupsTask(final Invocation invocation) {
			new Thread(new Runnable() {
				
				public void run() {
					   Object parameter = null;
					   String tablename =null;
					   Boolean  isBackups=false;
					   String sql=null;
					   String checktablesql=null;
					   String createtablesql=null;
					   MappedStatement mappedStatement=(MappedStatement) invocation.getArgs()[0];
					   String clazzname=mappedStatement.getId().substring(0, mappedStatement.getId().lastIndexOf("."));
					   String methodname=mappedStatement.getId().substring(mappedStatement.getId().lastIndexOf(".")+1, mappedStatement.getId().length());
					   try {
						@SuppressWarnings("rawtypes")
						Class  clazz = Class.forName(clazzname);
						   Method[] methods=clazz.getMethods();
						   for (Method method : methods) {
							   if(methodname.equals(method.getName())){
								   BackupTable backupTable=method.getAnnotation(BackupTable.class);
								    tablename=null!=backupTable ? backupTable.name() : null;
								    checktablesql="SHOW CREATE TABLE "+tablename;
							   }
						   }
						   if (invocation.getArgs().length > 1) {
					            parameter = invocation.getArgs()[1];
					        }
					        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
					        sql="SELECT * FROM " +tablename+" "+boundSql.getSql().toLowerCase().substring(boundSql.getSql().indexOf("where"));
					        isBackups=boundSql.getSql().toLowerCase().contains("delete") ||boundSql.getSql().toLowerCase().contains("update") ;
					        if(isBackups&& null!=tablename){
					        	Connection connection=null;
					        	Connection bconnection=null;
					        	try {
					        	    connection= MasterSessionFactory.getInstance().openSession().getConnection();
					        		Statement stmt = connection.createStatement();
					        		ResultSet rs=stmt.executeQuery(checktablesql);
					        		while(rs.next()){
					        			createtablesql=rs.getString(2).replaceAll("AUTO_INCREMENT,", ",`uuid` int(11) NOT NULL  AUTO_INCREMENT,").replaceAll("PRIMARY KEY \\([\\w\\W]*\\)", "PRIMARY KEY (`uuid`))");
					        			createtablesql=createtablesql.replaceAll("CREATE TABLE ", "CREATE TABLE IF NOT EXISTS");
					        		}
					        	   ResultSet srs=stmt.executeQuery(sql);
					        	   List<Map<String,Object>>list=DBResultsUtil.convertList(srs);
					        	  String colussql=null;
					        	  String valuesql=null;
					        	  for (Map<String, Object> map : list) {
					        		  for (Map.Entry<String, Object>  entry : map.entrySet()) {
					        			  colussql=colussql==null?entry.getKey() : colussql+","+entry.getKey() ;
					        			  valuesql=valuesql==null ? entry.getValue().toString() : valuesql +" ,"+entry.getValue();
									   }
								}
					        	  String insertsql="INSERT INTO "+tablename +" ("+colussql+") VALUES (" +valuesql+")";
					        	  bconnection= BackupsSessionFactory.getInstance().openSession().getConnection();
					        	  bconnection.createStatement().execute(createtablesql);
					        	  bconnection.createStatement().execute(insertsql);
					        	  bconnection.commit();
					        	  connection.commit();
								} catch (Exception e) {
									e.printStackTrace();
										try {
											connection.rollback();
											bconnection.rollback();
										} catch (SQLException e1) {
											e1.printStackTrace();
										}
										
								}finally{
									try {
										connection.close();
										bconnection.close();
									} catch (SQLException e) {
										e.printStackTrace();
									}
							
								}
					        }
					} catch (ClassNotFoundException e1) {
						e1.printStackTrace();
					}
				
					
					
					
					
					
					
					
					
					
					
				}
			}).start();
		}
		
		public void teste(){
			new Runnable() {
				
				public void run() {
					// TODO Auto-generated method stub
					
				}
			};
		}
	

}
