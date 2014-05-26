package org.crazycake.jdbcTemplateTool.exception;

import java.lang.reflect.Method;

public class NoColumnAnnotationFoundException extends Exception {
	
	public NoColumnAnnotationFoundException(Method getter){
		super(getter.getName() + " should have an @Column annotation.");
	}
}
