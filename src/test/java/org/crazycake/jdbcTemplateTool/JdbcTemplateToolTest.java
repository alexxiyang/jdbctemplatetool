package org.crazycake.jdbcTemplateTool;

import static org.crazycake.ScaffoldUnit.ScaffoldUnit.build;
import static org.crazycake.ScaffoldUnit.ScaffoldUnit.dbAssertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.crazycake.ScaffoldUnit.ScaffoldUnit;
import org.crazycake.jdbcTemplateTool.exception.NoColumnAnnotationFoundException;
import org.crazycake.jdbcTemplateTool.exception.NoIdAnnotationFoundException;
import org.crazycake.jdbcTemplateTool.model.Employee;
import org.crazycake.jdbcTemplateTool.model.Person;
import org.crazycake.jdbcTemplateTool.model.Student;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations={"classpath:spring.xml"})
public class JdbcTemplateToolTest extends AbstractJUnit4SpringContextTests{

	@Test
	public void testList() throws IOException, SQLException {
		build();
		JdbcTemplateTool jtt = super.applicationContext.getBean("jdbcTemplateTool",JdbcTemplateTool.class);
		List<Employee> es = jtt.list("select * from employee where age < ? order by id desc", new Object[]{30}, Employee.class);
		
		assertThat(new Integer(es.size()),is(2));
		
		assertThat(es.get(1).getName(),is("tim"));
	}

	@Test
	public void testCount() throws IOException, SQLException {
		build();
		
		JdbcTemplateTool jtt = super.applicationContext.getBean("jdbcTemplateTool",JdbcTemplateTool.class);
		
		int total = jtt.count("select count(1) from employee", null);
		assertThat(total,is(3));
	}

	@Test
	public void testGet() throws NoIdAnnotationFoundException, NoColumnAnnotationFoundException, IOException, SQLException {
		
		build();
		
		
		JdbcTemplateTool jtt = super.applicationContext.getBean("jdbcTemplateTool",JdbcTemplateTool.class);
		
		Employee e = jtt.get(Employee.class, 3);
		assertThat(e.getName(),is("jacob"));
	}
	
	@Test
    public void testGet2() throws NoIdAnnotationFoundException, NoColumnAnnotationFoundException, IOException, SQLException {
        
        build();
        
        JdbcTemplateTool jtt = super.applicationContext.getBean("jdbcTemplateTool",JdbcTemplateTool.class);
        
        Person e = jtt.get(Person.class, 1);
        assertThat(e.getName(),is("jackie"));
    }

	@Test
	public void testUpdate() throws Exception {
		build();
		
		JdbcTemplateTool jtt = super.applicationContext.getBean("jdbcTemplateTool",JdbcTemplateTool.class);
		
		Employee e = jtt.get(Employee.class, 1);
		e.setAge(23);
		jtt.update(e);
		ScaffoldUnit.dbAssertThat("select age from employee where name='jack'", is(23));
	}

	@Test
	public void testBatchUpdate() throws SQLException, IOException {
		build();
		
		JdbcTemplateTool jtt = super.applicationContext.getBean("jdbcTemplateTool",JdbcTemplateTool.class);
		
		List<Object[]> params = new ArrayList<Object[]>();
		Object[] p1 = new Object[]{23,"jack"};
		params.add(p1);
		Object[] p2 = new Object[]{29,"tim"};
		params.add(p2);
		
		jtt.batchUpdate("update employee set age = ? where name = ?", params);
		
		ScaffoldUnit.dbAssertThat("select age from employee where name='jack'", is(23));
		ScaffoldUnit.dbAssertThat("select age from employee where name='tim'", is(29));
	}

	@Test
	public void testSave() throws Exception {
		build();
		
		JdbcTemplateTool jtt = super.applicationContext.getBean("jdbcTemplateTool",JdbcTemplateTool.class);
		
		Employee e = new Employee();
		e.setId(4);
		e.setName("billy");
		Date now = new Date();
		e.setJoinDate(new Timestamp(now.getTime()));
		e.setAge(33);
		
		jtt.save(e);
		
		dbAssertThat("select name from employee where id=4", is("billy"));
	}
	
	@Test
	public void testSaveWithoutSomeField() throws Exception {
		build();
		
		JdbcTemplateTool jtt = super.applicationContext.getBean("jdbcTemplateTool",JdbcTemplateTool.class);
		
		Student s = new Student();
		s.setName("michael");
		s.setNothing("nothing");
		jtt.save(s);
		
		dbAssertThat("select name from student where name = 'michael'", is("michael"));
	}

	@Test
	public void testDelete() throws Exception {
		build();
		
		JdbcTemplateTool jtt = super.applicationContext.getBean("jdbcTemplateTool",JdbcTemplateTool.class);
		Employee e = new Employee();
		e.setId(1);
		jtt.delete(e);
		dbAssertThat("select name from employee where id=1", nullValue());
	}

}
