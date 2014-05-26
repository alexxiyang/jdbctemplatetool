package org.crazycake.jdbcTemplateTool.exception;

public class NoIdAnnotationFoundException extends Exception {
	
	public NoIdAnnotationFoundException(Class clazz){
		super(clazz + " doesn't have an id field, please make sure " + clazz + " has a column with an @id annotation.");
	}
}
