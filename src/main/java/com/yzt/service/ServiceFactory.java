package com.yzt.service;

import utils.CommonUtils;

public class ServiceFactory {

	private static Object object;

	private static Context context;

	public static Object getInstance(Class<?> clazz) {
		return create(clazz);
	}

	private static Object create(Class<?> clazz) {

		try {
			if (clazz.isAssignableFrom(Context.class)) {
				if (context == null) {
					context = (Context) clazz.newInstance();
				}
				return context;
			} else {
				if (object == null || !object.getClass().isAssignableFrom(clazz)) {
					object = CommonUtils.getServiceProxyInstance(clazz);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return object;
	}
}
