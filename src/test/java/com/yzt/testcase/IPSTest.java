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
import com.yzt.service.IPSService;
import com.yzt.service.ServiceFactory;

import contants.Contants;
import utils.CommonUtils;

public class IPSTest {

	private IPSService service = (IPSService) ServiceFactory.create(this.getClass());

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
		if (service.getContext().scHasKey(Contants.TASK_ID)) {
			String taskId = (String) service.getContext().getValue(Contants.TASK_ID);
			changedValue.put("id", taskId);
		}
		if (service.getContext().scHasKey(Contants.其他原因)) {
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
		if (service.getContext().scHasKey(Contants.TASK_ID)) {
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
		if (service.getContext().scHasKey(Contants.WAYBILL_ID)) {
			String waybillId = (String) service.getContext().getValue(Contants.WAYBILL_ID);
			changedValue.put("waybillId", waybillId);
		}
		inputJson = CommonUtils.analysisJsonAndUpdate(JSON.parse(inputJson), changedValue).toString();
		service.findTaskFee(inputJson);
	}

	
	@DataProvider
	public Object[][] testData(Method method) {
		return CommonUtils.getTestNGData("IPSTest", method);
	}

	@Test(dependsOnMethods = { "loginTest" })
	public void uploadTest() {
		String filename = "test.txt";
		service.uploadToIPS(filename);
	}
}
