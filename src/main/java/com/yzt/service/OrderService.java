package com.yzt.service;

import org.apache.log4j.Logger;

import com.test.framework.CaseMeta;
import com.yzt.common.HttpMethod;
import com.yzt.common.Response;
import utils.AssertUtil;
import utils.HttpHelper;
import utils.UrlUtil;

public class OrderService {
	private String url;
	private final static String URLKEY = "scm.url";
	private final static String APIURL = "/api/order";

	private static Logger logger = Logger.getLogger(OrderService.class);

	public OrderService() {
		this.url = UrlUtil.getUrl(URLKEY);
	}

	@CaseMeta("运单列表-修改查询回显")
	public Response queryWaybillById(String jsonParam) {
		String commonUrl = url + APIURL + "/queryWaybillById";

		Response response = HttpHelper.create().addUrl(commonUrl).addJsonParam(jsonParam).request(HttpMethod.POST);

		return response;
	}

	@CaseMeta("客户中心-订单详情")
	public Response getOrderDetails(String jsonParam) {

		String commonUrl = url + APIURL + "/getOrderDetails";

		Response response = HttpHelper.create().addUrl(commonUrl).addJsonParam(jsonParam).request(HttpMethod.POST);

		return response;
	}

	@CaseMeta("第三方订单-列表查询")
	public Response queryOrderListPage(String jsonParam) {

		String commonUrl = url + APIURL + "/queryOrderListPage";

		Response response = HttpHelper.create().addUrl(commonUrl).addJsonParam(jsonParam).request(HttpMethod.POST);

		return response;
	}

	@CaseMeta("第三方订单-拒绝接单")
	public Response orderRefuse(String jsonParam) {

		String commonUrl = url + APIURL + "/orderRefuse";

		Response response = HttpHelper.create().addUrl(commonUrl).addJsonParam(jsonParam).request(HttpMethod.POST);

		return response;
	}

	@CaseMeta("第三方订单-订单号查询订单")
	public Response queryOrderById(String jsonParam) {

		String commonUrl = url + APIURL + "/queryOrderById";

		Response response = HttpHelper.create().addUrl(commonUrl).addJsonParam(jsonParam).request(HttpMethod.POST);

		return response;
	}
}
