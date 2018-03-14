package com.yzt.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import com.google.common.collect.Maps;

public class Env {

	public static Map<String, String> getEnv() {
		Map<String, String> env = Maps.newHashMap();
		Properties properties = new Properties();
		InputStream params = Thread.currentThread().getContextClassLoader().getResourceAsStream("params.properties");
		try {
			properties.load(params);
			for (Object key : properties.keySet()) {
				env.put((String) key, properties.getProperty((String) key));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return env;
	}

	public static void main(String[] args) {
		
		
		System.out.println(getEnv().values());

	}

}
