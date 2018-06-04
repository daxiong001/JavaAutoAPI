package com.yzt.testcase;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.test.framework.CaseMeta;

import com.yzt.entity.SignImage;
import com.yzt.entity.Task;
import com.yzt.service.Context;
import com.yzt.service.IPSService;
import com.yzt.service.ServiceFactory;

import contants.Contants;
import utils.CommonUtils;
import utils.ContextUtil;
import utils.DateUtil;
import utils.ParamtersHelper;

public class IPSTest {

    private IPSService service = (IPSService) ServiceFactory.getInstance(IPSService.class);
    private static Logger logger = Logger.getLogger(IPSTest.class);

    @Test(dataProvider = "testData")
    @CaseMeta("登录")
    public void loginTest(String inputJson) {
        Assert.assertTrue(service.login(inputJson));
    }

    @Test(dataProvider = "testData", dependsOnMethods = {"loginTest"})
    @CaseMeta("调度任务统计")
    public void taskCountTest(String inputJson) {
        service.taskCount(inputJson);
    }

    @Test(dataProvider = "testData", dependsOnMethods = {"loginTest"})
    @CaseMeta("查询调度任务记录")
    public void findTaskInstallTest(String inputJson) {
        Map<String, Object> changedValue = Maps.newHashMap();
        changedValue.put("waybillId", "1zt003000003");

        logger.info("参数化前入参：inputJson = " + inputJson);
        inputJson = ParamtersHelper.getInstance().updateInputParam(JSON.parse(inputJson), changedValue).toString();
        logger.info("参数化后入参变更：inputJson = " + inputJson);
        service.findTaskInstall(inputJson);
    }

    @CaseMeta("查找增加跟踪信息")
    @Test(dataProvider = "testData", dependsOnMethods = {"findTaskInstallTest"})
    public void findMetaTest(String inputJson) {
        service.findMeta(inputJson);
    }

    @CaseMeta("添加跟踪信息")
    @Test(dataProvider = "testData", dependsOnMethods = {"findMetaTest"})
    public void saveTaskTraceTest(String inputJson) {
        Map<String, Object> changedValue = Maps.newHashMap();
        Context context = (Context) ServiceFactory.getInstance(Context.class);
        if (context.hasKey(Contants.TASK_ID)) {
            String taskId = (String) context.getValue(Contants.TASK_ID);
            changedValue.put("id", taskId);
        }
        if (context.hasKey(Contants.其他原因)) {
            String abnormalCauseId = (String) context.getValue(Contants.其他原因);
            changedValue.put("abnormalCauseId", abnormalCauseId);
        }

        inputJson = ParamtersHelper.getInstance().updateInputParam(JSON.parse(inputJson), changedValue).toString();
        service.saveTaskTrace(inputJson);
    }

    @CaseMeta("干线结束")
    @Test(dataProvider = "testData", dependsOnMethods = {"findTaskInstallTest"})
    public void trunkEndTest(String inputJson) {
        Map<String, Object> changedValue = Maps.newHashMap();
        Context context = (Context) ServiceFactory.getInstance(Context.class);
        List<String> list = Lists.newArrayList();
        if (context.hasKey(Contants.TASK_ID)) {
            String taskId = (String) context.getValue(Contants.TASK_ID);
            list.add(taskId);
            changedValue.put("taskIds", list);
        }

        inputJson = ParamtersHelper.getInstance().updateInputParam(JSON.parse(inputJson), changedValue).toString();
        service.trunkEnd(inputJson);
    }

    @CaseMeta("查询调度任务")
    @Test(dataProvider = "testData", dependsOnMethods = {"findTaskInstallTest"})
    public void findTaskFeeTest(String inputJson) {
        Map<String, Object> changedValue = Maps.newHashMap();
        Context context = (Context) ServiceFactory.getInstance(Context.class);
        if (context.hasKey(Contants.WAYBILL_ID)) {
            String waybillId = (String) context.getValue(Contants.WAYBILL_ID);
            changedValue.put("waybillId", waybillId);
        }

        inputJson = ParamtersHelper.getInstance().updateInputParam(JSON.parse(inputJson), changedValue).toString();
        service.findTaskFee(inputJson);
    }

    @Test(dataProvider = "testData", dependsOnMethods = {"findTaskFeeTest"})
    @CaseMeta("查询待分配师傅")
    public void queryUserJztTest(String inputJson) {
        Map<String, Object> changedValue = Maps.newHashMap();
        Context context = (Context) ServiceFactory.getInstance(Context.class);
        if (context.hasKey(Contants.WAYBILL_ID)) {
            String waybillId = (String) context.getValue(Contants.WAYBILL_ID);
            changedValue.put(Contants.WAYBILL_ID, waybillId);
        }
        inputJson = ParamtersHelper.getInstance().updateInputParam(JSON.parse(inputJson), changedValue).toString();
        service.queryUserJzt(inputJson);
    }

    @Test(dataProvider = "testData", dependsOnMethods = {"queryUserJztTest"})
    @CaseMeta("分配师傅")
    public void batDisWorkerTest(String inputJson) {
        // 参数化 "waybillId"，"branchFee"，"installFee"，"mediateFee"，"workerId";
        Map<String, Object> changedValues = Maps.newHashMap();
        Context context = (Context) ServiceFactory.getInstance(Context.class);
        if (context.hasKey(Contants.TASK_ID)) {
            changedValues.put(Contants.TASK_ID, context.getValue(Contants.TASK_ID));
        }
        if (context.hasKey(Contants.WORKER_ID)) {
            changedValues.put(Contants.WORKER_ID, context.getValue(Contants.WORKER_ID));
        }
        if (context.hasKey(Contants.BRANCH_FEE)) {
            changedValues.put(Contants.BRANCH_FEE, context.getValue(Contants.BRANCH_FEE));
        }
        if (context.hasKey(Contants.INSTALL_FEE)) {
            changedValues.put(Contants.INSTALL_FEE, context.getValue(Contants.INSTALL_FEE));
        }
        if (context.hasKey(Contants.MEDIATE_FEE)) {
            changedValues.put(Contants.MEDIATE_FEE, context.getValue(Contants.MEDIATE_FEE));
        }

        inputJson = ParamtersHelper.getInstance().updateInputParam(JSON.parse(inputJson), changedValues).toString();
        service.batDisWorker(inputJson);
    }

    @Test(dataProvider = "testData", dependsOnMethods = {"batDisWorkerTest"})
    @CaseMeta("预约")
    public void appointmentTest(String inputJson) {
        // 参数化 "taskId"
        Map<String, Object> changedValues = Maps.newHashMap();
        Context context = (Context) ServiceFactory.getInstance(Context.class);
        if (context.hasKey(Contants.TASK_ID)) {
            changedValues.put(Contants.TASK_ID, context.getValue(Contants.TASK_ID));
        }
        changedValues.put("appointmentTime", DateUtil.getCurrentDate(DateUtil.FORMAT16));
        inputJson = ParamtersHelper.getInstance().updateInputParam(JSON.parse(inputJson), changedValues).toString();
        service.appointment(inputJson);
    }

    @Test(dataProvider = "testData", dependsOnMethods = {"appointmentTest"})
    @CaseMeta("提货")
    public void pickUpTest(String inputJson) {
        // 参数化 "taskId"
        Task task = new Task();
        task.setTaskId((String) ParamtersHelper.getInstance().readParams(Contants.TASK_ID));
        inputJson = ParamtersHelper.getInstance().updateInputParam(JSON.parse(inputJson), task).toString();
        service.pickUp(inputJson);
    }

    // 签收时仅上传一张图片
    @Test(dependsOnMethods = {"pickUpTest"})
    @CaseMeta("上传一张签收图片")
    public void uploadToSignTest() {
        String filename = "sign.jpg";
        service.uploadToSign(filename);
    }

    @Test(dataProvider = "testData", dependsOnMethods = {"uploadToSignTest"})
    @CaseMeta("签收内容提交")
    public void signTest(String inputJson) {
        // 参数化图片id，签收时仅上传一张图片
        Map<String, Object> changedValues = Maps.newHashMap();
        List<Object> objectList = Lists.newArrayList();
        List<String> idList = Lists.newArrayList();

        List<SignImage> signs = ParamtersHelper.getInstance().readParams(SignImage.class);
        for (SignImage signImage : signs) {
            idList.add(signImage.getId());
            changedValues.put("files", idList);
        }

        changedValues.put(Contants.TASK_ID, ParamtersHelper.getInstance().readParams(Contants.TASK_ID));
        changedValues.put("signer", "signer");
        changedValues.put("describe", "describe");
        inputJson = ParamtersHelper.getInstance().updateInputParam(JSON.parse(inputJson), changedValues).toString();
        service.sign(inputJson);
    }

    @DataProvider
    public Object[][] testData(Method method) {
        return CommonUtils.getTestNGData("IPSTest", method);
    }
}
