package org.crazycake.jdbcTemplateTool.exception;

public class NoDefinedGetterException extends Exception {
	
	private String fieldName;
	
	public NoDefinedGetterException(String fieldName){
		super(fieldName + " should have an getter method.");
		this.fieldName = fieldName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
}
