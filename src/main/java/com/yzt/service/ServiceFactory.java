package com.yzt.service;

public class ServiceFactory {

	private static Object object;

	public static Object getInstance(Class<?> clazz) {
		return create(clazz);
	}

	private static Object create(Class<?> clazz) {

		try {
			if (object == null || !object.getClass().isAssignableFrom(clazz)) {
				object = clazz.newInstance();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return object;
	}
}
