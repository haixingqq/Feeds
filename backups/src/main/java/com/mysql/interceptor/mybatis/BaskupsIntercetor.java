package com.mysql.interceptor.mybatis;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;

import com.backups.annotation.BackupTable;
import com.backups.datasource.BackupsSessionFactory;
import com.backups.datasource.MasterSessionFactory;

/**
 * 
 * @author robin
 *
 */
@Intercepts({@Signature(type = Executor.class, method = "query",   args = { MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class }),
	                     @Signature(type = Executor.class, method = "update",  args = { MappedStatement.class, Object.class })
})
public class BaskupsIntercetor implements Interceptor {

	public Object intercept(Invocation invocation) throws Throwable {
		   //分析是否存在自定义注解
	    	//new BackupsTask(invocation);
		
		 Object parameter = null;
		 String tablename =null;
		 Boolean  isBackups=false;
		 String sql;
		 String checktablesql=null;
		   MappedStatement mappedStatement=(MappedStatement) invocation.getArgs()[0];
		   String clazzname=mappedStatement.getId().substring(0, mappedStatement.getId().lastIndexOf("."));
		   String methodname=mappedStatement.getId().substring(mappedStatement.getId().lastIndexOf(".")+1, mappedStatement.getId().length());
		   Class clazz=Class.forName(clazzname);
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
	        sql=boundSql.getSql().toLowerCase().replaceAll("delete", "select").replaceAll("update", "insert");
	        isBackups=boundSql.getSql().toLowerCase().contains("delete") ||boundSql.getSql().toLowerCase().contains("update") ;
	        
	        if(isBackups&& null!=tablename){
	        	try {
	        		Map<String,String>createTable= MasterSessionFactory.getInstance().openSession().selectOne(checktablesql);
	        	   	System.out.println(sql);
		        	SqlSession session=BackupsSessionFactory.getInstance().openSession();
		        	session.insert(sql);
				} catch (Exception e) {
				}
	       
	        }
		    return invocation.proceed();  
	}

	public Object plugin(Object target) {
		return Plugin.wrap(target, this);  
	}

	public void setProperties(Properties properties) {
		
	}
	

}
