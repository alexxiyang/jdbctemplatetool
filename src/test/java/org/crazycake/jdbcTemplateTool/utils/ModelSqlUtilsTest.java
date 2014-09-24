package org.crazycake.jdbcTemplateTool.utils;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.crazycake.jdbcTemplateTool.exception.NoColumnAnnotationFoundException;
import org.crazycake.jdbcTemplateTool.exception.NoIdAnnotationFoundException;
import org.crazycake.jdbcTemplateTool.model.Employee;
import org.crazycake.jdbcTemplateTool.model.SqlParamsPairs;
import org.junit.Test;

public class ModelSqlUtilsTest {

	@Test
	public void testGetGetFromObject() throws NoIdAnnotationFoundException, NoColumnAnnotationFoundException {
		
		SqlParamsPairs sqlAndParams = ModelSqlUtils.getGetFromObject(Employee.class, 3);
		
		assertThat(sqlAndParams.getSql(),is("select * from employee where id = ?"));
		
	}

}
