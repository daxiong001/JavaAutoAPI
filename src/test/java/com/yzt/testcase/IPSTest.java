package com.yzt.testcase;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.yzt.service.IPSService;

public class IPSTest extends IPSService {

	@Test(dataProvider = "login")
	public void loginTest(String jsonParam) {
		Assert.assertTrue(login(jsonParam));
	}

	@Test(dataProvider = "taskCount", dependsOnMethods = { "loginTest" })
	public void taskCountTest(String jsonParam) {
		taskCount(jsonParam);
	}

	@DataProvider(name = "login")
	public Object[][] loginData() {
		return new Object[][] { { "{\"source\": \"core\",\"mobile\": \"17576075478\",\"password\": \"525241\"}" } };
	}

	@DataProvider(name = "taskCount")
	public Object[][] taskCountData() {
		return new Object[][] {
				{ "{\"name\": \"taskInstallController.getNodeTypeCount\",\"token\": null,\"args\": []}" } };
	}
}
