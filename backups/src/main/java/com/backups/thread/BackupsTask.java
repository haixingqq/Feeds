package com.backups.thread;

import java.lang.reflect.Method;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.session.SqlSession;

import com.backups.annotation.BackupTable;
import com.backups.datasource.BackupsSessionFactory;

public class BackupsTask {

	
		public BackupsTask(final Invocation invocation) {
			new Thread(new Runnable() {
				public void run() {
					try {
						 Object parameter = null;
						 String tablename =null;
						 Boolean  isBackups=false;
						 String sql;
						   MappedStatement mappedStatement=(MappedStatement) invocation.getArgs()[0];
						   String clazzname=mappedStatement.getId().substring(0, mappedStatement.getId().lastIndexOf("."));
						   String methodname=mappedStatement.getId().substring(mappedStatement.getId().lastIndexOf(".")+1, mappedStatement.getId().length());
						   Class clazz=Class.forName(clazzname);
						   Method[] methods=clazz.getMethods();
						   for (Method method : methods) {
							   if(methodname.equals(method.getName())){
								   BackupTable backupTable=method.getAnnotation(BackupTable.class);
								    tablename=null!=backupTable ? backupTable.name() : null;
							   }
						   }
						   if (invocation.getArgs().length > 1) {
					            parameter = invocation.getArgs()[1];
					        }
					        BoundSql boundSql = mappedStatement.getBoundSql(parameter);
					        sql=boundSql.getSql().toLowerCase().replaceAll("delete", "select").replaceAll("update", "insert");
					        isBackups=boundSql.getSql().toLowerCase().contains("delete") ||boundSql.getSql().toLowerCase().contains("update") ;
					        if(isBackups&& null!=tablename){
					        	//TODO 做数据源 并且执行SQL
					        	SqlSession session=BackupsSessionFactory.getInstance().openSession();
					        	session.insert(sql);
					        	System.out.println(sql);
					        }
			
					} catch (Exception e) {
						e.printStackTrace();
					}
			
					
				}}).start();
		}
		
		public void teste(){
			new Runnable() {
				
				public void run() {
					// TODO Auto-generated method stub
					
				}
			};
		}
	

}
