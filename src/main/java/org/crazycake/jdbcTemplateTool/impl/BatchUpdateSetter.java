package org.crazycake.jdbcTemplateTool.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;

public class BatchUpdateSetter implements BatchPreparedStatementSetter {

	private List<Object[]> paramsList = new ArrayList<Object[]>();
	
	public BatchUpdateSetter(List<Object[]> paramsList){
		this.paramsList = paramsList;
	}
	
	@Override
	public void setValues(PreparedStatement ps, int i) throws SQLException {
		Object[] params = paramsList.get(i);
		for(int j=0;j<params.length;j++){
			ps.setObject(j+1, params[j]);
		}
	}

	@Override
	public int getBatchSize() {
		return paramsList.size();
	}

}
