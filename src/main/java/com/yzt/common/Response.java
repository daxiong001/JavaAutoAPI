package com.yzt.common;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.beust.jcommander.internal.Maps;

public class Response {
	private String code = "";
	private String jsonString = "";
	private Map<String, Object> paramtersMap = Maps.newHashMap();
	
	public void setCode(String statusLine) {
		String [] tokens = StringUtils.split(statusLine.trim(), " ");
		this.code = tokens[tokens.length - 1];
	}
	
	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}
	
	public String getCode() {
		return this.code;
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
}
