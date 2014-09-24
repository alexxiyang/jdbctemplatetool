package org.crazycake.jdbcTemplateTool.model;

import java.util.Arrays;

public class SqlParamsPairs {
	
	private String sql;
	
	private Object[] params;
	
	public SqlParamsPairs(){
		
	}
	
	public SqlParamsPairs(String sql,Object[] params){
		this.sql = sql;
		this.params = params;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public Object[] getParams() {
		return params;
	}

	public void setParams(Object[] params) {
		this.params = params;
	}

	@Override
	public String toString() {
		return "SqlParamsPairs [sql=" + sql + ", params="
				+ Arrays.toString(params) + "]";
	}
	
}
