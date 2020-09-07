> This repo is deprecated!

JdbcTemplateTool
============

[![Build Status](https://travis-ci.org/alexxiyang/jdbctemplatetool.svg?branch=master)](https://travis-ci.org/alexxiyang/jdbctemplatetool)

Spring JdbcTemplate did very convient and useful, but it also has some disadvantages or in another word "inconvenient". 
For example, you can't just pass an object and tell JdbcTemplate to turn it into a sql and save to database just like hibernate does. Alright, you may say "JdbcTemplate doesn't need you to write hbm.xml , so it's understandable". But is it true that this thing is impossible to achieve? 
And also you know JdbcTemplate can auto turn the result of a query to a list of persistent object a.k.a PO, but when you want to do this you will meet a problem: "How to ?".Because you can't find any function of JdbcTemplate to pass a sql and a PO class as we expected. After you google for it, you've been told you can create a BeanPropertyRowMapper to deal with this. But isn't it could be more easier?
Based on those questions I create JdbcTemplateTool which can provide these features:

-  Turn the result of query into a list of PO without knowing `BeanPropertyRowMapper`
- Pass a `select count(1) from table` sql to it, it will return the count result to you, and you don't have to consider the complex implement of this thing.
- Save an object into database
- Pass a PO class and the id , it will give you the PO with the id you assigned.
- Auto generate the update sql and execute it with the PO you pass.
- Batch update without care about implement BatchPreparedStatementSetter
- Delete an object from database 
- You can also use the original `JdbcTemplate`

> Only tested on mysql for now.

Maven dependency
-------------
```xml
<dependency>
  <groupId>org.crazycake</groupId>
  <artifactId>jdbctemplatetool</artifactId>
  <version>1.0.4-RELEASE</version>
</dependency>
```

#Quick start

 
####STEP 1. create a maven project
Create a maven project called `testjtt`. And add **jdbctemplatetool** dependency to pom.xml. Also add these dependencies to your pom.xml.

```xml
<dependency>
  <groupId>junit</groupId>
  <artifactId>junit</artifactId>
  <version>4.11</version>
  <scope>test</scope>
</dependency>
<dependency>
	<groupId>org.springframework</groupId>
	<artifactId>spring-context</artifactId>
	<version>3.2.2.RELEASE</version>
	<scope>test</scope>
</dependency>
<dependency>
	<groupId>com.mchange</groupId>
	<artifactId>c3p0</artifactId>
	<version>0.9.2.1</version>
	<scope>test</scope>
</dependency>
<dependency>
	<groupId>mysql</groupId>
	<artifactId>mysql-connector-java</artifactId>
	<version>5.1.19</version>
	<scope>test</scope>
</dependency>
<dependency>
	<groupId>org.springframework</groupId>
	<artifactId>spring-test</artifactId>
	<version>3.2.2.RELEASE</version>
	<scope>test</scope>
</dependency>
```

> You'd better use 1.6+ jdk. Cause I didn't test it on 1.5

####STEP 2. Create test database
Create a database named `jtt_test` and create an user named `travis` and don't assign password. Assign all privileges to `travis` .

```sql
CREATE USER 'travis'@'%' IDENTIFIED BY '';
GRANT ALL ON jtt_test.* TO 'travis'@'%';
flush privileges;
```

Create a table `employee` and fill this table with some test data.

```sql
DROP TABLE IF EXISTS `employee`;

CREATE TABLE `employee` (
  `id` int(11) NOT NULL,
  `name` varchar(300) NOT NULL,
  `join_date` datetime NOT NULL,
  `age` int(11) NOT NULL,
  PRIMARY KEY  (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `employee` */

insert  into `employee`(`id`,`name`,`join_date`,`age`) values (1,'jack','2014-09-22 00:00:00',23),(2,'ted','2014-08-30 00:00:00',25),(3,'jim','2014-06-22 00:00:00',33);
```

####STEP 3. Prepare for Spring
Create `resources` folder under test folder. Make `resources` as a source folder and change the Output folder into `target/test-classes`
Create `spring.xml` under test/resources 
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.springframework.org/schema/beans
    http://www.springframework.org/schema/beans/spring-beans-3.0.xsd">
	
	<bean name="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource" destroy-method="close">
		<property name="jdbcUrl"><value>jdbc:mysql://localhost:3306/jtt_test?characterEncoding=utf8</value></property>
		<property name="driverClass"><value>com.mysql.jdbc.Driver</value></property>
		<property name="user"><value>travis</value></property>
		<property name="password"><value></value></property>
	</bean>
	
	<bean id = "jdbcTemplate" class = "org.springframework.jdbc.core.JdbcTemplate">   
         <property name = "dataSource" ref="dataSource"/>   
    </bean>
    
    <bean id="jdbcTemplateTool" class="org.crazycake.jdbcTemplateTool.JdbcTemplateTool">
    	<property name = "jdbcTemplate" ref="jdbcTemplate" />
    </bean>
</beans>
```

####STEP 4. create PO
Create `Employee.java`
```java
import java.sql.Timestamp;
import javax.persistence.Id;

public class Employee {
	
	private Integer id;
	private String name;
	private Timestamp joinDate;
	private Integer age;
	
	@Id
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Timestamp getJoinDate() {
		return joinDate;
	}
	public void setJoinDate(Timestamp joinDate) {
		this.joinDate = joinDate;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
}
```


####STEP 5. create test case
Create `HelloJTTTest.java` 
```java
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.crazycake.jdbcTemplateTool.JdbcTemplateTool;
import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

@ContextConfiguration(locations={"classpath:spring.xml"})
public class HelloJTTTest extends AbstractJUnit4SpringContextTests{
	
	@Test
	public void testSave(){
		JdbcTemplateTool jtt = super.applicationContext.getBean("jdbcTemplateTool",JdbcTemplateTool.class);
		
		Employee e = new Employee();
		e.setId(4);
		e.setName("billy");
		Date now = new Date();
		e.setJoinDate(new Timestamp(now.getTime()));
		e.setAge(33);
		
		try {
			jtt.save(e);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}
```

####STEP 6. launch!
Run this test! After you see the green bar check the database. There is a new record : 

id	| name	| join_date	| age		
---	|----- 		| ----			|-----
|4	|billy	|2014-09-24 22:51:20	|33


#Further more
I will introduce more features I mentioned in introduction. 
> all test case are based on the test data we created in **Quick Start**

##list
It can turn the result of query into a list of PO without knowing `BeanPropertyRowMapper`. It will use the underscore style column name to guess the camel style field name and call the setter function.
```java
@Test
public void testList(){
	JdbcTemplateTool jtt = super.applicationContext.getBean("jdbcTemplateTool",JdbcTemplateTool.class);
	List<Employee> es = jtt.list("select * from employee where age < ? order by id desc", new Object[]{30}, Employee.class);
		
	assertThat(new Integer(es.size()),is(2));
	assertThat(es.get(1).getName(),is("jack"));
}
```

##count
Pass a `select count(1) from table` sql to it, it will return the count result to you, and you don't have to consider the complex implement of this thing.

```java
@Test
public void testCount() throws IOException, SQLException {
		
	JdbcTemplateTool jtt = super.applicationContext.getBean("jdbcTemplateTool",JdbcTemplateTool.class);
	int total = jtt.count("select count(1) from employee", null);
	assertThat(total,is(4));
	}
```

##save
Save an object into database.
If you have some fields you don't want to save to database. You can add `@Transient` to the getter. Like this
```java
public class Student {
	
	private Integer id;
	private String name;
	private String nothing;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Transient
	public String getNothing() {
		return nothing;
	}
	public void setNothing(String nothing) {
		this.nothing = nothing;
	}
}
```
Then **JdbcTemplateTool** will not turn this field into sql:
```java
@Test
public void testSave() throws Exception {

	JdbcTemplateTool jtt = super.applicationContext.getBean("jdbcTemplateTool",JdbcTemplateTool.class);
		
	Student s = new Student();
	s.setName("michael");
	s.setNothing("nothing");
	jtt.save(s);
}
```

##get
Pass a PO class and the id , it will give you the PO with the id you assigned.
First all, You need to add `@Id` to the getter of the field which match with the primary key of this table.So that **JdbcTemplateTool** can know what's the primary key. Like this:
```java
@Id
public Integer getId() {
	return id;
}
```
Example
```java
@Test
public void testGet() throws NoIdAnnotationFoundException, NoColumnAnnotationFoundException, IOException, SQLException {
	
	JdbcTemplateTool jtt = super.applicationContext.getBean("jdbcTemplateTool",JdbcTemplateTool.class);
	
	Employee e = jtt.get(Employee.class, 3);
	assertThat(e.getName(),is("jim"));
}
```
##update
Auto generate the update sql and execute it with the PO you pass. Also remember to add `@Id` to the primary key field.
```java
@Test
public void testUpdate() throws Exception {

	JdbcTemplateTool jtt = super.applicationContext.getBean("jdbcTemplateTool",JdbcTemplateTool.class);
	
	Employee e = jtt.get(Employee.class, 1);
	e.setAge(23);
	jtt.update(e);
}
```

##batchUpdate
Batch update without care about implement BatchPreparedStatementSetter
```java
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
	
}
```

##delete
Delete an object from database 
```java
@Test
public void testDelete() throws Exception {
	JdbcTemplateTool jtt = super.applicationContext.getBean("jdbcTemplateTool",JdbcTemplateTool.class);
	Employee e = new Employee();
	e.setId(1);
	jtt.delete(e);
}
```

##getJdbcTemplate
You can also use the original `JdbcTemplate`. And there are many situation which **JdbcTemplateTool** can't handle with. In those situations  just call `JdbcTemplateTool.getJdbcTemplate()` to get `JdbcTemplate` and use the original methods of it.

##If you found any bugs
Please send email to idante@qq.com
