package com.yzt.service;

import exception.customsException;

public class ServiceFactory {
	private static IPSService ipsService;
	private static Context context;

	public static Object getInstance(Class<?> clazz) {
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
		if (ipsService == null) {
			ipsService = new IPSService();
		}
		return ipsService;
	}

	private static Context createContext() {
		if (context == null) {
			context = new Context();
		}
		return context;
	}
}
