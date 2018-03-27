package com.yzt.service;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.beust.jcommander.internal.Maps;
import com.google.common.collect.Lists;
import com.yzt.common.HttpMethod;
import com.yzt.common.Response;

import contants.Contants;
import exception.customsException;
import utils.AssertUtil;
import utils.CommonUtils;
import utils.HttpHelper;
import utils.UrlUtil;

public class CMPService {
	private String url;
	private final static String URLKEY = "cmp.url";
	private final static String LOGINURL = "/login";
	private final static String UPLOADURL = "/upload";
	private final static String APIURL = "/api.do";

	public CMPService() {
		this.url = UrlUtil.getUrl(URLKEY);
	}

	/**
	 * 登录操作步骤封装
	 * 
	 * @param jsonParam，请求参数
	 * @return
	 */
	public Boolean login(String jsonParam) {
		String loginUrl = url + LOGINURL;
		String jwt = "Bearer ";
		Map<String, Object> resultMap = Maps.newHashMap();
		Response response = HttpHelper.create().addUrl(loginUrl).addJsonParam(jsonParam).request(HttpMethod.POST);

		if (response.getHttpCode().equals("200")) {
			resultMap.putAll(response.getParamterMap());
			if (resultMap.containsKey(Contants.JWT_KEY)) {
				jwt += (String) resultMap.get(Contants.JWT_KEY);
				CommonUtils.setKeyValueToContext(Contants.JWT_KEY, jwt);
				return true;
			}
		} else {
			throw new customsException("登录失败，请确认登录账户有效性！");
		}
		return false;
	}

	/**
	 * 待受理页面根据单号查询待受理任务单
	 * 
	 * @param jsonParam，请求参数
	 * @return
	 */
	public Response queryTeskByWaybillId(String jsonParam) {
		String loginUrl = url + APIURL;
		Map<String, Object> result = Maps.newHashMap();
		List<String> list = Lists.newArrayList();
		List<Map<String, Object>> maps = Lists.newArrayList();
		Response response = HttpHelper.create().addUrl(loginUrl).addJsonParam(jsonParam)
				.request(HttpMethod.POST);
		CommonUtils.analysisJson(response.getJsonString(), Contants.RELAY_ID);
		return response;	
		/*System.out.println("queryTeskByWaybillId respone : " + response.getJsonString());
		result.putAll(CommonUtils.JsonStringToMap(response.getJsonString()));
		if (result.containsKey("content")) {
			list.addAll(CommonUtils.jsonArrayToList((String) result.get("content")));
		}
		for (int i = 0; i < list.size(); i++) {
			maps.add(CommonUtils.JsonStringToMap(list.get(i)));
		}
		if (maps.size() == 1) {
			if (maps.get(0).containsKey("relayId")) {
				context.addValue(Contants.RELAY_ID, maps.get(0).get("relayId"));
			}
		} else if (maps.size() > 1) {
			throw new customsException("查询出同一单号存在多条记录！暂时不支持！");
		}
		System.out.println("this.relayId = " + context.getValue(Contants.RELAY_ID));*/		
	}

	/**
	 * 待受理页面根据单号更新数据
	 * 
	 * @param jsonParam，请求参数
	 * @return
	 */
	public Response updateTeskByWaybillId(String jsonParam) {
		String loginUrl = url + APIURL;
		Map<String, Object> result = Maps.newHashMap();
		Response response = HttpHelper.create().addUrl(loginUrl).addJsonParam(jsonParam)
				.request(HttpMethod.POST);
		return response;
//		AssertResponeCode(response);
/*		System.out.println("updateTeskByWaybillId respone : " + response.getJsonString());
		result.putAll(CommonUtils.JsonStringToMap(response.getJsonString()));
		for (Entry<String, Object> entry : result.entrySet()) {
			System.out.println(entry.getKey() + " = " + entry.getValue());
		}*/
	}
}
