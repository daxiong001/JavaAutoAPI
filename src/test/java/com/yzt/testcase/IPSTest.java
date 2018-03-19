package com.yzt.testcase;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yzt.entity.CauseMeta;
import com.yzt.entity.SignImage;
import com.yzt.service.Context;
import com.yzt.service.IPSService;
import com.yzt.service.ServiceFactory;

import contants.Contants;
import utils.CommonUtils;
import utils.ContextUtil;
import utils.DateUtil;

public class IPSTest {

	private IPSService service = (IPSService) ServiceFactory.getInstance(this.getClass());

	/**
	 * 登录，获得JWT
	 * 
	 * @param inputJson
	 */
	@Test(dataProvider = "testData")
	public void loginTest(String inputJson) {
		Assert.assertTrue(service.login(inputJson));
	}

	/**
	 * 调度任务计数
	 * 
	 * @param inputJson
	 */
	@Test(dataProvider = "testData", dependsOnMethods = { "loginTest" })
	public void taskCountTest(String inputJson) {
		service.taskCount(inputJson);
	}

	/**
	 * 根据运单号查找调度任务记录，获得taskID
	 * 
	 * @param inputJson
	 */
	@Test(dataProvider = "testData", dependsOnMethods = { "loginTest" })
	public void findTaskInstallTest(String inputJson) {
		Map<String, Object> changedValue = Maps.newHashMap();
		changedValue.put("waybillId", "1zt003000003");
		inputJson = CommonUtils.analysisJsonAndUpdate(JSON.parse(inputJson), changedValue).toString();
		service.findTaskInstall(inputJson);
	}

	/**
	 * 查找增加跟踪信息的原因及其ID
	 * 
	 * @param inputJson
	 */
	@Test(dataProvider = "testData", dependsOnMethods = { "findTaskInstallTest" })
	public void findMetaTest(String inputJson) {
		service.findMeta(inputJson);
	}

	/**
	 * 添加跟踪信息
	 * 
	 * @param inputJson
	 */
	@Test(dataProvider = "testData", dependsOnMethods = { "findMetaTest" })
	public void saveTaskTraceTest(String inputJson) {
		Map<String, Object> changedValue = Maps.newHashMap();
		if (service.getContext().hasKey(Contants.TASK_ID)) {
			String taskId = (String) service.getContext().getValue(Contants.TASK_ID);
			changedValue.put("id", taskId);
		}
		if (service.getContext().hasKey(Contants.其他原因)) {
			String abnormalCauseId = (String) service.getContext().getValue(Contants.其他原因);
			changedValue.put("abnormalCauseId", abnormalCauseId);
		}
		inputJson = CommonUtils.analysisJsonAndUpdate(JSON.parse(inputJson), changedValue).toString();
		service.saveTaskTrace(inputJson);
	}

	/**
	 * 干线结束
	 */
	@Test(dataProvider = "testData", dependsOnMethods = { "findTaskInstallTest" })
	public void trunkEndTest(String inputJson) {
		Map<String, Object> changedValue = Maps.newHashMap();
		List<String> list = Lists.newArrayList();
		if (service.getContext().hasKey(Contants.TASK_ID)) {
			String taskId = (String) service.getContext().getValue(Contants.TASK_ID);
			list.add(taskId);
			changedValue.put("taskIds", list);
		}
		inputJson = CommonUtils.analysisJsonAndUpdate(JSON.parse(inputJson), changedValue).toString();
		service.trunkEnd(inputJson);
	}

	/**
	 * 查询调度任务
	 * 
	 * @param inputJson
	 */
	@Test(dataProvider = "testData", dependsOnMethods = { "findTaskInstallTest" })
	public void findTaskFeeTest(String inputJson) {
		Map<String, Object> changedValue = Maps.newHashMap();
		if (service.getContext().hasKey(Contants.WAYBILL_ID)) {
			String waybillId = (String) service.getContext().getValue(Contants.WAYBILL_ID);
			changedValue.put("waybillId", waybillId);
		}
		inputJson = CommonUtils.analysisJsonAndUpdate(JSON.parse(inputJson), changedValue).toString();
		service.findTaskFee(inputJson);
	}

	@Test(dataProvider = "testData", dependsOnMethods = { "findTaskFeeTest" })
	public void queryUserJztTest(String inputJson) {
		Map<String, Object> changedValue = Maps.newHashMap();
		if (service.getContext().hasKey(Contants.WAYBILL_ID)) {
			String waybillId = (String) service.getContext().getValue(Contants.WAYBILL_ID);
			changedValue.put(Contants.WAYBILL_ID, waybillId);
		}
		inputJson = CommonUtils.analysisJsonAndUpdate(JSON.parse(inputJson), changedValue).toString();
		service.queryUserJzt(inputJson);
	}

	@Test(dataProvider = "testData", dependsOnMethods = { "queryUserJztTest" })
	public void batDisWorkerTest(String inputJson) {
		// 参数化 "waybillId"，"branchFee"，"installFee"，"mediateFee"，"workerId";
		Map<String, Object> changedValues = Maps.newHashMap();

		if (service.getContext().hasKey(Contants.TASK_ID)) {
			changedValues.put(Contants.TASK_ID, service.getContext().getValue(Contants.TASK_ID));
		}
		if (service.getContext().hasKey(Contants.WORKER_ID)) {
			changedValues.put(Contants.WORKER_ID, service.getContext().getValue(Contants.WORKER_ID));
		}
		if (service.getContext().hasKey(Contants.BRANCH_FEE)) {
			changedValues.put(Contants.BRANCH_FEE, service.getContext().getValue(Contants.BRANCH_FEE));
		}
		if (service.getContext().hasKey(Contants.INSTALL_FEE)) {
			changedValues.put(Contants.INSTALL_FEE, service.getContext().getValue(Contants.INSTALL_FEE));
		}
		if (service.getContext().hasKey(Contants.MEDIATE_FEE)) {
			changedValues.put(Contants.MEDIATE_FEE, service.getContext().getValue(Contants.MEDIATE_FEE));
		}

		inputJson = CommonUtils.analysisJsonAndUpdate(JSON.parse(inputJson), changedValues).toString();
		service.batDisWorker(inputJson);
	}

	@Test(dataProvider = "testData", dependsOnMethods = { "batDisWorkerTest" })
	public void appointmentTest(String inputJson) {
		// 参数化 "taskId"
		Map<String, Object> changedValues = Maps.newHashMap();

		if (service.getContext().hasKey(Contants.TASK_ID)) {
			changedValues.put(Contants.TASK_ID, service.getContext().getValue(Contants.TASK_ID));
		}
		changedValues.put("appointmentTime", DateUtil.getCurrentDate(DateUtil.FORMAT16));

		inputJson = CommonUtils.analysisJsonAndUpdate(JSON.parse(inputJson), changedValues).toString();
		service.appointment(inputJson);
	}

	@Test(dataProvider = "testData", dependsOnMethods = { "appointmentTest" })
	public void pickUpTest(String inputJson) {
		// 参数化 "taskId"
		Map<String, Object> changedValues = Maps.newHashMap();
		
		Map<String, Object> result = ContextUtil.getContextMap();
		if (result != null && result.containsKey(Contants.TASK_ID)) {
			changedValues.put(Contants.TASK_ID, result.get(Contants.TASK_ID));
		}
		inputJson = CommonUtils.analysisJsonAndUpdate(JSON.parse(inputJson), changedValues).toString();
		service.pickUp(inputJson);
	}

	//签收时仅上传一张图片
	@Test(dependsOnMethods = { "pickUpTest" })
	public void uploadToSignTest() {
		String filename = "开门密码.jpg";
		service.uploadToSign(filename);
	}

	@Test(dataProvider = "testData", dependsOnMethods = { "uploadToSignTest" })
	public void signTest(String inputJson) {
		// 参数化图片id，签收时仅上传一张图片
		Map<String, Object> changedValues = Maps.newHashMap();
		List<Object> objectList = Lists.newArrayList();
		List<String> idList = Lists.newArrayList();

		objectList = ContextUtil.getContextObject(SignImage.class);
		for (Object li : objectList) {
			SignImage signImage = (SignImage) li;
			idList.add(signImage.getId());
			changedValues.put("files", idList);
		}

		Map<String, Object> result = ContextUtil.getContextMap();
		if (result != null && result.containsKey(Contants.TASK_ID)) {
			changedValues.put(Contants.TASK_ID, result.get(Contants.TASK_ID));
		}
		changedValues.put("signer", "signer");
		changedValues.put("describe", "describe");		
		inputJson = CommonUtils.analysisJsonAndUpdate(JSON.parse(inputJson), changedValues).toString();
		service.sign(inputJson);
	}

	@DataProvider
	public Object[][] testData(Method method) {
		return CommonUtils.getTestNGData("IPSTest", method);
	}
}
