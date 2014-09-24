package org.crazycake.jdbcTemplateTool.exception;

import java.lang.reflect.Method;

public class NoColumnAnnotationFoundException extends Exception {
	
	public NoColumnAnnotationFoundException(String ClassName,Method getter){
		super(ClassName + "." + getter.getName() + "() should have an @Column annotation.");
	}
}
