package com.yzt.service;

import java.util.List;
import java.util.Map;

import com.beust.jcommander.internal.Maps;
import com.google.common.collect.Lists;
import com.yzt.common.HttpMethod;
import com.yzt.common.Response;
import com.yzt.entity.CauseMeta;

import contants.Contants;
import exception.customsException;
import utils.CommonUtils;
import utils.HttpHelper;

/**
 * 系统具体业务
 * 
 * @author vivi.zhang
 *
 */
public class IPSService extends BasicService {

	private String url;
	private final static String URLKEY = "ips.url";
	private final static String LOGINURL = "/login";
	private final static String UPLOADURL = "/upload";
	private final static String APIURL = "/api.do";

	private String JWT = "Bearer ";

	private Context context;

	public Context getContext() {
		return context;
	}

	public IPSService() {
		this.url = getUrl(URLKEY);
		this.context = (Context) ServiceFactory.create(Context.class);
	}

	/**
	 * 获得jwt并设置到请求头
	 * 
	 * @return
	 */
	protected Map<String, String> setAuthToHeader() {
		Map<String, String> header = Maps.newHashMap();
		if (context.scHasKey(Contants.JWT_KEY)) {
			header.put(Contants.AUTH, (String) context.getValue(Contants.JWT_KEY));
		}
		return header;
	}

	/**
	 * 登录操作步骤封装，获得JWT
	 * 
	 * @param inputJson
	 */
	public Boolean login(String jsonParam) {
		String loginUrl = url + LOGINURL;
		Map<String, Object> resultMap = Maps.newHashMap();
		Response response = HttpHelper.create().addUrl(loginUrl).addJsonParam(jsonParam).request(HttpMethod.POST);
		if (response.getCode().equals("200")) {
			resultMap.putAll(response.getParamterMap());
			if (resultMap.containsKey(Contants.JWT_KEY)) {
				JWT += (String) resultMap.get(Contants.JWT_KEY);
				context.addValue(Contants.JWT_KEY, JWT);
				return true;
			}
		} else {
			throw new customsException("登录失败，请确认登录账户有效性！");
		}
		return false;
	}

	/**
	 * 上传接口封装
	 * 
	 * @param filename，放在uploads文件夹下待上传的文件名，exp：test.txt
	 * @return
	 */
	public Response uploadToIPS(String filename) {
		String commonUrl = url + UPLOADURL;
		Response response = HttpHelper.create().addUrl(url).addHeaders(setAuthToHeader()).addUploads(filename)
				.request(HttpMethod.POST);
		AssertResponeCode(response);
		return response;
	}

	/**
	 * 调度任务统计操作步骤封装
	 * 
	 * @param jsonParam，请求参数
	 */
	public Response taskCount(String jsonParam) {
		String commonUrl = url + APIURL;
		Response response = HttpHelper.create().addUrl(commonUrl).addHeaders(setAuthToHeader()).addJsonParam(jsonParam)
				.request(HttpMethod.POST);
		AssertResponeCode(response);
		return response;
	}

	/**
	 * 根据运单号查找调度任务记录，获得taskID，存储waybillId至上下文
	 * 
	 * @param inputJson
	 */
	public Response findTaskInstall(String jsonParam) {
		String commonUrl = url + APIURL;
		// 存储waybillId至上下文

		CommonUtils.analysisJson(jsonParam, context, Contants.WAYBILL_ID);
		Response response = HttpHelper.create().addUrl(commonUrl).addHeaders(setAuthToHeader()).addJsonParam(jsonParam)
				.request(HttpMethod.POST);
		AssertResponeCode(response);
		// 获取返回值中的ID,默认返回一条数据
		CommonUtils.analysisJson(response.getJsonString(), context, Contants.TASK_ID);

		return response;
	}

	/**
	 * 查找增加跟踪信息的原因及其ID
	 * 
	 * @param inputJson
	 */
	public Response findMeta(String jsonParam) {
		String commonUrl = url + APIURL;
		Response response = HttpHelper.create().addUrl(commonUrl).addHeaders(setAuthToHeader()).addJsonParam(jsonParam)
				.request(HttpMethod.POST);
		AssertResponeCode(response);
		List<String> lists = Lists.newArrayList();
		lists.add(Contants.LABEL);
		lists.add(Contants.ID);
		// 获取返回值中的具体原因和对应的ID
		CommonUtils.analysisJson(response.getJsonString(), context, lists);
		for (int i = 0; i < context.getSCList().size(); i++) {
			if (context.getSCList().get(i).containsKey(Contants.ID)
					|| context.getSCList().get(i).containsKey(Contants.LABEL)) {
				CauseMeta causeMeta = new CauseMeta();
				causeMeta.setId((String) context.getSCList().get(i).get(Contants.ID));
				causeMeta.setLabel((String) context.getSCList().get(i).get(Contants.LABEL));
				context.addCauseMeta(causeMeta);
			}
		}
		for (int i = 0; i < context.getCauseLists().size(); i++) {
			System.out.println("Cause id " + i + " = " + context.getCauseLists().get(i).getId() + " , label = "
					+ context.getCauseLists().get(i).getLabel());
		}
		return response;
	}

	/**
	 * 添加跟踪信息
	 * 
	 * @param inputJson
	 */
	public void saveTaskTrace(String jsonParam) {
		String commonUrl = url + APIURL;
		Response response = HttpHelper.create().addUrl(commonUrl).addHeaders(setAuthToHeader()).addJsonParam(jsonParam)
				.request(HttpMethod.POST);
		AssertResponeCode(response);
	}

	/**
	 * 干线结束
	 */
	public void trunkEnd(String jsonParam) {
		String commonUrl = url + APIURL;
		Response response = HttpHelper.create().addUrl(commonUrl).addHeaders(setAuthToHeader()).addJsonParam(jsonParam)
				.request(HttpMethod.POST);
		AssertResponeCode(response);
	}

	/**
	 * 查询调度任务的branchFee， installFee， mediateFee
	 * 
	 * @param jsonParam
	 * @return
	 */
	public Response findTaskFee(String jsonParam) {
		String commonUrl = url + APIURL;
		Response response = HttpHelper.create().addUrl(commonUrl).addHeaders(setAuthToHeader()).addJsonParam(jsonParam)
				.request(HttpMethod.POST);
		AssertResponeCode(response);
		//存储调度任务的branchFee， installFee， mediateFee
		List<String> lists = Lists.newArrayList();
		lists.add(Contants.BRANCH_FEE);
		lists.add(Contants.MEDIATE_FEE);
		lists.add(Contants.INSTALL_FEE);

		CommonUtils.analysisJson(response.getJsonString(), context, lists);
		return response;
	}
	
}
