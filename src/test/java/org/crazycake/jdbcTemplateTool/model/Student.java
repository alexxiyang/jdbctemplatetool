package org.crazycake.jdbcTemplateTool.model;

import javax.persistence.Transient;

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
