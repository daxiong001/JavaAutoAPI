package com.yzt.testcase.order;

import java.lang.reflect.Method;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.test.framework.CaseMeta;
import com.yzt.common.Response;
import com.yzt.service.OrderService;
import com.yzt.service.ServiceFactory;

import utils.AssertUtil;
import utils.CommonUtils;

public class OrderTest {

	private OrderService service = (OrderService) ServiceFactory.getInstance(OrderService.class);

	@Test(dataProvider = "testData",enabled = false)
	@CaseMeta("运单列表-修改查询回显")
	public void queryWaybillByIdTest(String inputJson) {
		Response response = service.queryWaybillById(inputJson);
		AssertUtil.AssertResponeResultCode(response);
	}

	@Test(dataProvider = "testData",enabled = false)
	@CaseMeta("客户中心-订单详情")
	public void getOrderDetailsTest(String inputJson) {
		Response response = service.getOrderDetails(inputJson);
		AssertUtil.AssertResponeResultCode(response);
	}

	@Test(dataProvider = "testData")
	@CaseMeta("第三方订单-列表查询")
	public void queryOrderListPageTest(String inputJson) {
		Response response = service.queryOrderListPage(inputJson);
		AssertUtil.AssertResponeResultCode(response);
	}

	@Test(dataProvider = "testData",enabled=false)
	@CaseMeta("第三方订单-拒绝接单")
	public void orderRefuseTest(String inputJson) {
		Response response = service.orderRefuse(inputJson);
		AssertUtil.AssertResponeResultCode(response);
	}

	@Test(dataProvider = "testData")
	@CaseMeta("第三方订单-绑定运单列表")
	public void orderBindWaybillListPageTest(String inputJson) {
		Response response = service.queryOrderListPage(inputJson);
		AssertUtil.AssertResponeResultCode(response);
	}

	@Test(dataProvider = "testData",enabled=false)
	@CaseMeta("第三方订单-订单号查询订单")
	public void queryOrderByIdTest(String inputJson) {
		Response response = service.queryOrderListPage(inputJson);
		AssertUtil.AssertResponeResultCode(response);
	}

	@DataProvider
	public Object[][] testData(Method method) {
		return CommonUtils.getTestNGData("OrderTest", method);
	}
}
