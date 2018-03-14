package com.yzt.service;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.testng.Assert;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.yzt.common.Env;
import com.yzt.common.HttpMethod;
import com.yzt.common.Response;

import contants.Contants;
import exception.customsException;
import utils.CommonUtils;
import utils.DataProviders;
import utils.HttpHelper;

/**
 * 基础服务
 * 
 * @author vivi.zhang
 *
 */
public class BasicService {

	/**
	 * 获取参数配置文件中服务器地址
	 * 
	 * @return
	 */
	public String getUrl(String key) {
		if (Env.getEnv().containsKey(key)) {
			return Env.getEnv().get(key);
		} else {
			throw new customsException("参数配置文件中未定义url参数值，请定义");
		}
	}

	protected void AssertResponeVO(Response actual, Response expected) {
		Assert.assertEquals(actual, expected);
	}

	protected void AssertResponeCode(Response actual) {
		Assert.assertEquals(actual.getCode(), "200");
	}

	protected void AssertResponeJsonString(Response actual, String expectedJsonString) {
		String jsonString = actual.getJsonString();
		Assert.assertEquals(jsonString, expectedJsonString);
	}

/*	*//**
	 * 获取返回值中content字段值
	 * 
	 * @return
	 *//*
	protected JSONArray getResponeContent(Response response) {
		JSONArray content = null;
		if (JSONObject.parseObject(response.getJsonString()).containsKey("result")) {
			JSONObject result = JSONObject.parseObject(response.getJsonString()).getJSONObject("result");
			if (result.containsKey("content")) {
				content = result.getJSONArray("content");
			}
		}
		return content;
	}*/
}
