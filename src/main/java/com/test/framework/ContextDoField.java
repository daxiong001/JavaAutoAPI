package com.test.framework;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.tools.ant.taskdefs.Execute;

public interface ContextDoField {
	public Object execute(Field fd,Method getMethod,Class clazz);
}
