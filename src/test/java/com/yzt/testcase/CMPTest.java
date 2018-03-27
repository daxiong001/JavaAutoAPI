package com.yzt.testcase;

import java.lang.reflect.Method;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.collect.Maps;
import com.yzt.common.Response;
import com.yzt.service.CMPService;
import com.yzt.service.ServiceFactory;

import utils.AssertUtil;
import utils.CommonUtils;

public class CMPTest {
	
	CMPService service = (CMPService) ServiceFactory.getInstance(CMPService.class);
	
	@Test(dataProvider = "testData")
	public void loginTest(String inputJson) {
		Assert.assertTrue(service.login(inputJson));
	}
	
	@Test(dataProvider = "testData", dependsOnMethods = { "loginTest" })
	public void queryTeskByWaybillIdTest(String inputJson) {
		Response response = service.queryTeskByWaybillId(inputJson);
		AssertUtil.AssertResponeCode(response);
	}
	
	@Test(dataProvider = "testData", dependsOnMethods = { "queryTeskByWaybillIdTest" })
	public void updateTeskByWaybillIdTest(String inputJson) {
	/*	Map<String, String> changedValues = Maps.newHashMap();
		changedValues.put("relayId", "999999999");
		changedValues.put("internalId", "88888888");
		System.out.println("changed === " + CommonUtils.updateInputAgrs(inputJson,changedValues));*/
		Response response = service.updateTeskByWaybillId(inputJson);
		AssertUtil.AssertResponeCode(response);
	}

	@DataProvider
	public Object[][] testData(Method method) {
		return CommonUtils.getTestNGData("CMPTest",method);
	}}
