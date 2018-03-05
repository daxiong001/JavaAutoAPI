package com.yzt.service;

import java.util.Map;

import org.testng.Assert;

import com.beust.jcommander.internal.Maps;
import com.yzt.common.Response;

public class BasicService {
	
	public void AssertResponeVO(Response actual,Response expected) {
		Assert.assertEquals(actual, expected);
	}
	
	public void AssertResponeCode(Response actual) {
		Assert.assertEquals(actual.getCode(), "200");
	}
	
	public void AssertResponeJsonString(Response actual,String expectedJsonString) {
		String jsonString = actual.getJsonString();
		Assert.assertEquals(jsonString, expectedJsonString);
	}
	
/*	public void AssertResponeParamMap(Response actual,Map<String, Object> expectedMap) {
		Map<String, Object> actualMap = Maps.newHashMap();
		actualMap = actual.getParamterMap();
		Assert.assertEquals(actualMap, expectedJsonString);
	}*/
	
	public static void main(String[] args) {
		
	}

}
