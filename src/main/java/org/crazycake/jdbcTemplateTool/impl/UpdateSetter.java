package org.crazycake.jdbcTemplateTool.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.jdbc.core.PreparedStatementSetter;

public class UpdateSetter implements PreparedStatementSetter {
	
	private Object[] params;
	
	public UpdateSetter(Object[] params){
		this.params = params;
	}
	
	@Override
	public void setValues(PreparedStatement ps) throws SQLException {
		for(int i=0;i<params.length;i++){
			ps.setObject(i+1, params[i]);
		}
	}

}
