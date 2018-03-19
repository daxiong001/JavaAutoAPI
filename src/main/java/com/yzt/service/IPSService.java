package com.yzt.service;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yzt.common.HttpMethod;
import com.yzt.common.Response;
import com.yzt.entity.CauseMeta;
import com.yzt.entity.SignImage;
import com.yzt.entity.Worker;

import contants.Contants;
import exception.customsException;
import utils.CommonUtils;
import utils.ContextUtil;
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
	
	private static Logger logger = Logger.getLogger(IPSService.class);

	private String JWT = "Bearer ";

	private Context context;

	public Context getContext() {
		return context;
	}

	public IPSService() {
		this.url = getUrl(URLKEY);
		this.context = (Context) ServiceFactory.getInstance(Context.class);
	}

	/**
	 * 获得jwt并设置到请求头
	 * 
	 * @return
	 */
	protected Map<String, String> setAuthToHeader() {
		Map<String, String> header = Maps.newHashMap();
		if (context.hasKey(Contants.JWT_KEY)) {
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
		CommonUtils.analysisJson(response.getJsonString(), context, Contants.ID);

		// 返回json 用id存储taskId值，转换下context map存储key 为taskId
//		context.addValue(Contants.TASK_ID, context.getValue(Contants.ID));
		ContextUtil.convertContextMapKey(Contants.TASK_ID,Contants.ID);
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
		//存储至context List<Object> 中
		CommonUtils.setObjectToContext(lists, CauseMeta.class);
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
		// 存储调度任务的branchFee， installFee， mediateFee
		List<String> lists = Lists.newArrayList();
		lists.add(Contants.BRANCH_FEE);
		lists.add(Contants.MEDIATE_FEE);
		lists.add(Contants.INSTALL_FEE);

		CommonUtils.analysisJson(response.getJsonString(), context, lists);
		return response;
	}

	/**
	 * 查询待分配师傅相关信息，获取workerId 对存在多个workerId，使用的时候需要随机
	 * 
	 * @param jsonParam
	 * @return
	 */
	public Response queryUserJzt(String jsonParam) {
		String commonUrl = url + APIURL;
		List<String> list = Lists.newArrayList();
		Response response = HttpHelper.create().addUrl(commonUrl).addHeaders(setAuthToHeader()).addJsonParam(jsonParam)
				.request(HttpMethod.POST);
		AssertResponeCode(response);

		List<String> clist = Lists.newArrayList();
		clist.add(Contants.REALNAME);
		clist.add(Contants.ID);
		// 查询待分配师傅相关信息，获取workerId,存在多个workerId
		CommonUtils.analysisJson(response.getJsonString(), context, clist);

		CommonUtils.setObjectToContext(clist, Worker.class);
		return response;
	}

	/**
	 * 分配师傅
	 * 
	 * @param jsonParam
	 */
	public void batDisWorker(String jsonParam) {
		String commonUrl = url + APIURL;
		Response response = HttpHelper.create().addUrl(commonUrl).addHeaders(setAuthToHeader()).addJsonParam(jsonParam)
				.request(HttpMethod.POST);
		AssertResponeCode(response);
	}

	/**
	 * 预约
	 * 
	 * @param jsonParam
	 */
	public void appointment(String jsonParam) {
		String commonUrl = url + APIURL;
		Response response = HttpHelper.create().addUrl(commonUrl).addHeaders(setAuthToHeader()).addJsonParam(jsonParam)
				.request(HttpMethod.POST);
		AssertResponeCode(response);
	}

	/**
	 * 提货
	 * 
	 * @param jsonParam
	 */
	public void pickUp(String jsonParam) {
		String commonUrl = url + APIURL;
		Response response = HttpHelper.create().addUrl(commonUrl).addHeaders(setAuthToHeader()).addJsonParam(jsonParam)
				.request(HttpMethod.POST);
		AssertResponeCode(response);
	}

	/**
	 * 上传签收图片
	 * 
	 * @param filename，放在uploads文件夹下待上传的文件名，exp：test.txt
	 * @return
	 */
	public Response uploadToSign(String filename) {
		String commonUrl = url + UPLOADURL;
		Response response = HttpHelper.create().addUrl(commonUrl).addHeaders(setAuthToHeader()).addUploads(filename)
				.request(HttpMethod.POST);
		AssertResponeCode(response);
		// 存储图片
		List<String> clist = Lists.newArrayList();
		clist.add(Contants.ETAG);
		clist.add(Contants.ID);
		clist.add(Contants.PATH);
		clist.add(Contants.URL);
		clist.add(Contants.NAME);

		CommonUtils.analysisJson(response.getJsonString(), context, clist);

		CommonUtils.setObjectToContext(clist, SignImage.class);

		return response;
	}

	/**
	 * 签收人，签收内容提交
	 * 
	 * @param
	 * @return
	 */
	public Response sign(String jsonParam) {
		String commonUrl = url + APIURL;
		Response response = HttpHelper.create().addUrl(commonUrl).addHeaders(setAuthToHeader()).addJsonParam(jsonParam)
				.request(HttpMethod.POST);
		AssertResponeCode(response);
		return response;
	}

}
