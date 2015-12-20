package com.backups.dbinterface;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.backups.annotation.BackupTable;

public interface IMySql {
	
	@Select("select name from user where id=1")
	@BackupTable(name="user")
	@ResultType(String.class)
	public String select();
	@Select("select * from user")
	public List<Map<String,Object>>getUsers();

	@Update("update user set name='258' where id=1")
	@BackupTable(name="user")
	public void updateUser();
}
