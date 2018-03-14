package com.yzt.service;

import exception.customsException;

public class ServiceFactory {

	public static Object create(Class<?> clazz) {
		if (clazz.getName() == IPSService.class.getName()) {
			return createIPSService();
		} else if (clazz.getName().toLowerCase().contains("ips")) {
			return createIPSService();
		} else if (clazz.getName().toLowerCase().contains("context")) {
			return createContext();
		} else {
			throw new customsException("没有这个类，请确认！");
		}
	}

	private static IPSService createIPSService() {
		return new IPSService();
	}

	private static Context createContext() {
		return new Context();
	}
}
