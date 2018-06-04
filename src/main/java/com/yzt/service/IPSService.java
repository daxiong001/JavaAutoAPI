package com.yzt.service;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.test.framework.CaseMeta;
import com.yzt.common.HttpMethod;
import com.yzt.common.Response;
import com.yzt.entity.CauseMeta;
import com.yzt.entity.SignImage;
import com.yzt.entity.Worker;

import contants.Contants;
import exception.customsException;
import utils.AssertUtil;
import utils.CommonUtils;
import utils.ContextUtil;
import utils.HttpHelper;
import utils.ParamtersHelper;
import utils.UrlUtil;

/**
 * 系统具体业务
 *
 * @author vivi.zhang
 */
public class IPSService {

    private String url;
    private final static String URLKEY = "ips.url";
    private final static String LOGINURL = "/login";
    private final static String UPLOADURL = "/upload";
    private final static String APIURL = "/api.do";

    private static Logger logger = Logger.getLogger(IPSService.class);

    public IPSService() {
        this.url = UrlUtil.getUrl(URLKEY);
    }

    @CaseMeta("登录")
    public Boolean login(String jsonParam) {
        String loginUrl = url + LOGINURL;
        String jwt = "Bearer ";
        Map<String, Object> resultMap = Maps.newHashMap();
        Response response = HttpHelper.create().addUrl(loginUrl).addJsonParam(jsonParam).request(HttpMethod.POST);
        if (response.getHttpCode().equals("200")) {
            resultMap.putAll(response.getParamterMap());
            if (resultMap.containsKey(Contants.JWT_KEY)) {
                jwt += (String) resultMap.get(Contants.JWT_KEY);
                ParamtersHelper.getInstance().saveParam(Contants.JWT_KEY, jwt);
                return true;
            }
        } else {
            throw new customsException("登录失败，请确认登录账户有效性！");
        }
        return false;
    }

    @CaseMeta("调度任务统计")
    public Response taskCount(String jsonParam) {
        String commonUrl = url + APIURL;
        Response response = HttpHelper.create().addUrl(commonUrl).addJsonParam(jsonParam).request(HttpMethod.POST);
        AssertUtil.AssertResponeCode(response);
        return response;
    }

    @CaseMeta("查询调度任务记录")
    public Response findTaskInstall(String jsonParam) {
        String commonUrl = url + APIURL;
        // 存储waybillId至上下文
        ParamtersHelper.getInstance().saveParams(jsonParam, Contants.WAYBILL_ID);
        Response response = HttpHelper.create().addUrl(commonUrl).addJsonParam(jsonParam).request(HttpMethod.POST);
        AssertUtil.AssertResponeCode(response);
        // 返回json 用id存储taskId值，转换下context map存储key 为taskId

        ParamtersHelper.getInstance().saveParams(response.getJsonString(), Contants.ID, Contants.TASK_ID);
        return response;
    }

    @CaseMeta("查找增加跟踪信息")
    public Response findMeta(String jsonParam) {
        String commonUrl = url + APIURL;
        Response response = HttpHelper.create().addUrl(commonUrl).addJsonParam(jsonParam).request(HttpMethod.POST);
        AssertUtil.AssertResponeCode(response);

        ParamtersHelper.getInstance().saveParams(response.getJsonString(), CauseMeta.class);
        return response;
    }

    @CaseMeta("添加跟踪信息")
    public void saveTaskTrace(String jsonParam) {
        String commonUrl = url + APIURL;
        Response response = HttpHelper.create().addUrl(commonUrl).addJsonParam(jsonParam).request(HttpMethod.POST);
        AssertUtil.AssertResponeCode(response);
    }

    @CaseMeta("干线结束")
    public void trunkEnd(String jsonParam) {
        String commonUrl = url + APIURL;
        Response response = HttpHelper.create().addUrl(commonUrl).addJsonParam(jsonParam).request(HttpMethod.POST);
        AssertUtil.AssertResponeCode(response);
    }

    @CaseMeta("查询调度任务")
    public Response findTaskFee(String jsonParam) {
        String commonUrl = url + APIURL;
        Response response = HttpHelper.create().addUrl(commonUrl).addJsonParam(jsonParam).request(HttpMethod.POST);
        AssertUtil.AssertResponeCode(response);
        // 存储调度任务的branchFee， installFee， mediateFee
        List<String> lists = Lists.newArrayList();
        lists.add(Contants.BRANCH_FEE);
        lists.add(Contants.MEDIATE_FEE);
        lists.add(Contants.INSTALL_FEE);

        ParamtersHelper.getInstance().saveParams(response.getJsonString(), lists);
        return response;
    }

    @CaseMeta("查询待分配师傅")
    public Response queryUserJzt(String jsonParam) {
        String commonUrl = url + APIURL;
        List<String> list = Lists.newArrayList();
        Response response = HttpHelper.create().addUrl(commonUrl).addJsonParam(jsonParam).request(HttpMethod.POST);
        AssertUtil.AssertResponeCode(response);

        List<String> clist = Lists.newArrayList();
        clist.add(Contants.REALNAME);
        clist.add(Contants.ID);
        // 查询待分配师傅相关信息，获取workerId,存在多个workerId
        ParamtersHelper.getInstance().saveParams(response.getJsonString(), Worker.class);
        return response;
    }

    @CaseMeta("分配师傅")
    public void batDisWorker(String jsonParam) {
        String commonUrl = url + APIURL;
        Response response = HttpHelper.create().addUrl(commonUrl).addJsonParam(jsonParam).request(HttpMethod.POST);
        AssertUtil.AssertResponeCode(response);
    }

    @CaseMeta("预约")
    public void appointment(String jsonParam) {
        String commonUrl = url + APIURL;
        Response response = HttpHelper.create().addUrl(commonUrl).addJsonParam(jsonParam).request(HttpMethod.POST);
        AssertUtil.AssertResponeCode(response);
    }

    @CaseMeta("提货")
    public void pickUp(String jsonParam) {
        String commonUrl = url + APIURL;
        Response response = HttpHelper.create().addUrl(commonUrl).addJsonParam(jsonParam).request(HttpMethod.POST);
        AssertUtil.AssertResponeCode(response);
    }

    /**
     * 上传签收图片
     *
     * @param filename，放在uploads文件夹下待上传的文件名，exp：test.txt
     * @return
     */
    @CaseMeta("上传一张签收图片")
    public Response uploadToSign(String filename) {
        String commonUrl = url + UPLOADURL;
        Response response = HttpHelper.create().addUrl(commonUrl).addUploads(filename).request(HttpMethod.POST);
        AssertUtil.AssertResponeCode(response);
        // 存储图片

        ParamtersHelper.getInstance().saveParams(response.getJsonString(), SignImage.class);

        return response;
    }

    @CaseMeta("签收内容提交")
    public Response sign(String jsonParam) {
        String commonUrl = url + APIURL;
        Response response = HttpHelper.create().addUrl(commonUrl).addJsonParam(jsonParam).request(HttpMethod.POST);
        AssertUtil.AssertResponeCode(response);
        return response;
    }

}
