package com.yzt.service;

import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.collect.Maps;
import com.yzt.common.HttpMethod;
import com.yzt.common.Response;

import contants.Contants;
import utils.HttpHelper;

public class OrderService extends BasicService {
	private String url;
	private final static String URLKEY = "scm.url";
	private final static String APIURL = "/api/order";

	private static Logger logger = Logger.getLogger(OrderService.class);

	public OrderService() {
		this.url = getUrl(URLKEY);
	}

	/**
	 * 运单列表-修改查询回显
	 * 
	 * @return
	 */
	public Response queryWaybillById(String jsonParam) {
		String commonUrl = url + APIURL + "/queryWaybillById";
		logger.info("----运单列表-修改查询回显----queryWaybillById----请求url = " + commonUrl + " ----");
		logger.info("----运单列表-修改查询回显----发送请求----开始----");
		Response response = HttpHelper.create().addUrl(commonUrl).addHeaders(setAuthToHeader()).addJsonParam(jsonParam)
				.request(HttpMethod.POST);
		logger.info("----运单列表-修改查询回显----发送请求----结束----");
		logger.info("----运单列表-修改查询回显----返回结果----response = " + response + " ----");
		AssertResponeCode(response);
		return response;
	}
}
