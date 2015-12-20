package com.mysql.interceptor.mybatis;

import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import com.backups.thread.BackupsTask;

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
	    	new BackupsTask(invocation);
		    return invocation.proceed();  
	}

	public Object plugin(Object target) {
		return Plugin.wrap(target, this);  
	}

	public void setProperties(Properties properties) {
		
	}
	

}
