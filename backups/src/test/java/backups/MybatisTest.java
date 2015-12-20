package backups;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.backups.dbinterface.IMySql;

/**
 * 
 * @author robin
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)		
@ContextConfiguration(locations = {"classpath:mybatis.xml"})
public class MybatisTest {
	@Autowired
	IMySql imysql;
	
	@Test
	public  void testdb(){
		imysql.updateUser();
	}
	
	

}
