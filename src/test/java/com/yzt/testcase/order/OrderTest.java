package com.yzt.testcase.order;

import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.yzt.common.Response;
import com.yzt.service.OrderService;
import com.yzt.service.ServiceFactory;

import utils.CommonUtils;

public class OrderTest {
	
	private OrderService service = (OrderService) ServiceFactory.getInstance(OrderService.class);

	/**
	 * 运单列表-修改查询回显
	 * 
	 * @param inputJson
	 */
	@Test(dataProvider = "testData")
	public void queryWaybillByIdTest(String inputJson) {
		Response response = service.queryWaybillById(inputJson);
	}
	
	
	@DataProvider
	public Object[][] testData(Method method) {
		return CommonUtils.getTestNGData("OrderTest", method);
	}
}
