package org.crazycake.jdbcTemplateTool;

import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations={"classpath:spring.xml"})
public class JdbcTemplateProxyTest extends AbstractJUnit4SpringContextTests {

//	@Test
//	public void testQueryStringRowMapperOfT() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testQueryStringObjectArrayRowMapperOfT() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testQueryForMapString() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testQueryForMapStringObjectArray() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testUpdate() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testBatchUpdate() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testInsert() {
//		fail("Not yet implemented");
//	}

	@Test
	public void testSetJdbcTemplate() {
		JdbcTemplate jdbcTemplate = super.applicationContext.getBean("jdbcTemplate",JdbcTemplate.class);
		JdbcTemplateProxy proxy = new JdbcTemplateProxy();
		proxy.setJdbcTemplate(jdbcTemplate);
		
	}

}
