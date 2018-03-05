package com.yzt.service;

import java.util.Map;

import com.beust.jcommander.internal.Maps;
import com.yzt.common.Env;
import com.yzt.common.HttpMethod;
import com.yzt.common.Response;
import contants.Contants;
import exception.customsException;
import utils.HttpHelper;

public class IPSService extends BasicService {
	public static String JWT = "Bearer ";
	
	public static String url;
	
	static{
		url = getUrl();
	}

	private static String getUrl() {
		if (Env.getEnv().containsKey("url")) {
			return Env.getEnv().get("url");
		} else {
			throw new customsException("参数配置文件中未定义url参数值，请定义");
		}
	}

	public Boolean login(String jsonParam) {
		String loginUrl = url + Contants.LOGIN_URL;
		Map<String, Object> resultMap = Maps.newHashMap();
		Response response = HttpHelper.create().addUrl(loginUrl).addJsonParam(jsonParam).request(HttpMethod.POST);

		if (response.getCode().equals("200")) {
			resultMap.putAll(response.getParamterMap());
			if (resultMap.containsKey(Contants.JWT_KEY)) {
				JWT += (String) resultMap.get(Contants.JWT_KEY);
				return true;
			}
		} else {
			throw new customsException("登录失败，请确认登录账户有效性！");
		}
		return false;
	}

	private Response handleAuth(String jsonParam) {
		Map<String, String> header = Maps.newHashMap();
		header.put(Contants.AUTH, JWT);
		String commonUrl = url + Contants.API_URL;
		Response response = HttpHelper.create().addUrl(commonUrl).addHeaders(header).addJsonParam(jsonParam)
				.request(HttpMethod.POST);
		return response;
	}
	
	public void taskCount(String jsonParam) {
		Response response = handleAuth(jsonParam);
		AssertResponeCode(response);
	}
}
