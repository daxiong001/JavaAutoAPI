package com.yzt.common;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;

/**
 * 请求响应实体
 * @author vivi.zhang
 *
 */
public class Response {
	private String httpCode = "";
	private String resultCode = "";
	private String jsonString = "";
	private Map<String, Object> paramtersMap = Maps.newHashMap();
	
	public void setHttpCode(String statusLine) {
		String [] tokens = StringUtils.split(statusLine.trim(), " ");
		this.httpCode = tokens[tokens.length - 1];
	}
	
	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}
	
	public String getHttpCode() {
		return this.httpCode;
	}
	
	public String getJsonString() {
		return this.jsonString;
	}
	
	public void setParamterMap(Map<String, Object> paramsMap) {
		this.paramtersMap = paramsMap;
	}
	
	public Map<String, Object> getParamterMap() {
		return this.paramtersMap;
	}

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}	
}
