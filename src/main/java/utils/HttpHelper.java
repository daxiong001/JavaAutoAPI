package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ObjectUtils.Null;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yzt.common.HttpMethod;
import com.yzt.common.Response;
import com.yzt.service.Context;
import com.yzt.service.ServiceFactory;

import contants.Contants;

/**
 * HTTP工具
 *
 * @author vivi.zhang
 */
public class HttpHelper {

    public static final String CHARTSET = "UTF-8";
    public static final String CONTENT_TYPE_NAME = "Content-Type";
    public static final String DEFULT_CONTENT_TYPE_VALUE = "application/json";
    public static final Integer REQUEST_TIMEOUT = 20000;
    public static final Integer CONNECT_TIMEOUT = 20000;
    public static final Integer SOCKET_TIMEOUT = 20000;
    public static final String UPLOAD_FILE_PATH = System.getProperty("user.dir") + File.separator + "src"
            + File.separator + "test" + File.separator + "resource" + File.separator + "uploads" + File.separator;
    public static final String RESULT_CODE = "resultCode";

    static class HttpRequestUpload {
        String fileName;
        File file;
        String textName;
        List<String> text;
    }

    public static class HttpRequest {

        private String url;

        private String jsonParam;

        private Map<String, String> urlParams = Maps.newHashMap();

        private Map<String, String> headers = Maps.newHashMap();

        private List<HttpRequestUpload> uploads = Lists.newArrayList();

        /**
         * 添加请求头
         *
         * @param header
         * @return
         */
        public HttpRequest addHeaders(Map<String, String> header) {
            this.headers.putAll(header);
            return this;
        }

        /**
         * 添加请求url地址
         *
         * @param url
         * @return
         */
        public HttpRequest addUrl(String url) {
            this.url = url;
            return this;
        }

        /**
         * 添加get请求参数
         *
         * @param urlParams
         * @return
         */
        public HttpRequest addUrlParam(Map<String, String> urlParams) {
            this.urlParams.putAll(urlParams);
            return this;
        }

        /**
         * 添加post请求参数
         *
         * @param jsonParam
         * @return
         */
        public HttpRequest addJsonParam(String jsonParam) {
            this.jsonParam = jsonParam;
            return this;
        }

        /**
         * @param filename exp： test.txt,待上传的文件固定放在uploads目录下
         * @return
         */
        public HttpRequest addUploads(String filename) {
            HttpRequestUpload upload = new HttpRequestUpload();
            upload.fileName = filename;
            upload.file = new File(UPLOAD_FILE_PATH + filename);
            uploads.add(upload);
            return this;
        }

        private void handleConfig(HttpPost httpPost, HttpGet httpGet) {
            RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(REQUEST_TIMEOUT)
                    .setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();

            if (httpPost != null) {
                httpPost.setConfig(requestConfig);
            }
            if (httpGet != null) {
                httpGet.setConfig(requestConfig);
            }
        }

        private void handleHeader(HttpPost httpPost, HttpGet httpGet) {
            Map<String, String> header = Maps.newHashMap();
            Context context = (Context) ServiceFactory.getInstance(Context.class);
            if (context.hasKey(Contants.JWT_KEY)) {
                header.put(Contants.AUTH, (String) context.getValue(Contants.JWT_KEY));
                this.headers.putAll(header);
            }
            if (httpPost != null) {
                if (this.uploads.isEmpty()) {
                    this.headers.put(CONTENT_TYPE_NAME, DEFULT_CONTENT_TYPE_VALUE);
                }
                for (String key : this.headers.keySet()) {
                    httpPost.addHeader(key, this.headers.get(key));
                }
            }
            if (httpGet != null) {
                for (String key : this.headers.keySet()) {
                    httpGet.addHeader(key, this.headers.get(key));
                }
            }
        }

        private void handleUrl(HttpPost httpPost, HttpGet httpGet) {
            URI uri = null;
            try {
                uri = new URI(this.url);
            } catch (URISyntaxException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (httpPost != null) {
                httpPost.setURI(uri);
            }
            if (httpGet != null) {
                httpGet.setURI(uri);
            }
        }

        private void handleUploads(HttpPost httpPost) {
            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
            for (HttpRequestUpload httpRequestUpload : this.uploads) {
                multipartEntityBuilder.addBinaryBody("file", httpRequestUpload.file);
            }
            HttpEntity httpEntity = multipartEntityBuilder.build();
            httpPost.setEntity(httpEntity);
        }

        private void handlePost(HttpPost httpPost) {
            handleUrl(httpPost, null);
            handleConfig(httpPost, null);
            handleHeader(httpPost, null);
            if (!this.uploads.isEmpty()) {
                handleUploads(httpPost);
            } else if (!this.jsonParam.isEmpty()) {
                HttpEntity httpEntity = new StringEntity(this.jsonParam, CHARTSET);
                httpPost.setEntity(httpEntity);
            }
        }

        private void handleGet(HttpGet httpGet) {
            if (this.urlParams != null || this.urlParams.size() != 0) {
                if (this.url.indexOf("?") == -1 && this.url.indexOf("=") == -1) {
                    this.url += "?";
                    for (Entry<String, String> entry : this.urlParams.entrySet()) {
                        this.url += entry.getKey() + "=" + entry.getValue() + "&";
                    }
                    this.url = this.url.lastIndexOf("&") == -1 ? this.url
                            : this.url.substring(0, this.url.length() - 1);
                }
            }
            handleUrl(null, httpGet);
            handleConfig(null, httpGet);
            handleHeader(null, httpGet);
        }

        private Response handleRespone(HttpResponse httpResponse) {
            Response respone = new Response();
            HttpEntity responseEntity = null;
            String result = "";
            String httpCode = "", resultCode = "";
            if (httpResponse != null) {
                try {
                    httpCode = httpResponse.getStatusLine().toString();
                    responseEntity = httpResponse.getEntity();
                    result = EntityUtils.toString(responseEntity);
                    if (result.contains(RESULT_CODE)) {
//						resultCode = (String) CommonUtils.analysisJson(result, RESULT_CODE).getValue(RESULT_CODE);
                        resultCode = (String) ParamtersHelper.getInstance().saveParams(result, RESULT_CODE).readParams(RESULT_CODE);
                    }
                } catch (ParseException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                if (this.uploads.isEmpty()) {
                    try {
                        respone.setParamterMap(CommonUtils.JsonStringToMap(result));
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                respone.setHttpCode(httpCode);
                respone.setResultCode(resultCode);
                respone.setJsonString(result);
            }
            return respone;
        }

        /**
         * http请求执行入口
         *
         * @param method 使用HttpMethod枚举值，exp:HttpMethod.POST
         * @return
         */
        public Response request(HttpMethod method) {
            HttpResponse httpResponse = null;
            HttpClient httpClient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost();
            HttpGet httpGet = new HttpGet();

            method = method == null ? HttpMethod.GET : method;
            try {
                if (method == HttpMethod.POST) {
                    handlePost(httpPost);
                    httpResponse = httpClient.execute(httpPost);
                } else if (method == HttpMethod.GET) {
                    handleGet(httpGet);
                    httpResponse = httpClient.execute(httpGet);
                }
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return handleRespone(httpResponse);
        }

    }

    /**
     * http请求实例化入口，发送请求方式，exp：HttpHelper.create().addUrl("http://120.76.247.73:11006/login").addJsonParam(jsonData)
     * .request(HttpMethod.POST)
     *
     * @return
     */
    public static HttpRequest create() {
        return new HttpRequest();
    }
}
