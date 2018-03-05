package utils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import com.google.common.collect.Maps;
import com.yzt.common.HttpMethod;
import com.yzt.common.Response;

public class HttpHelper {

	public static final String CHARTSET = "UTF-8";
	public static final String DEFULT_CONTENT_TYPE_NAME = "content-type";
	public static final String DEFULT_CONTENT_TYPE_VALUE = "application/json";
	public static final Integer REQUEST_TIMEOUT = 20000;
	public static final Integer CONNECT_TIMEOUT = 20000;
	public static final Integer SOCKET_TIMEOUT = 20000;

	public static class HttpRequest {

		private String url;

		private String jsonParam;

		private Map<String, String> urlParams = Maps.newHashMap();

		private Map<String, String> headers = Maps.newHashMap();

		public HttpRequest() {
			this.headers.put(DEFULT_CONTENT_TYPE_NAME, DEFULT_CONTENT_TYPE_VALUE);
		}

		public HttpRequest addHeaders(Map<String, String> header) {
			this.headers.putAll(header);
			return this;
		}

		public HttpRequest addUrl(String url) {
			this.url = url;
			return this;
		}

		public HttpRequest addUrlParam(Map<String, String> urlParams) {
			this.urlParams.putAll(urlParams);
			return this;
		}

		public HttpRequest addJsonParam(String jsonParam) {
			this.jsonParam = jsonParam;
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
			if (httpPost != null) {
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

		private void handlePost(HttpPost httpPost) {
			handleUrl(httpPost, null);
			handleConfig(httpPost, null);
			handleHeader(httpPost, null);
			if (this.jsonParam != null || !this.jsonParam.isEmpty()) {
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
		
		private Response handleRespone(HttpResponse httpResponse){
			String result = "";
			Response respone = new Response();
			if (httpResponse != null) {
				try {
					result = EntityUtils.toString(httpResponse.getEntity());
					respone.setCode(httpResponse.getStatusLine().toString());
					respone.setJsonString(result);
					respone.setParamterMap(CommonUtils.JsonStringToMap(result));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return respone;
		}

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

	public static HttpRequest create() {
		return new HttpRequest();
	}

	public static void main(String[] args) {
		String jsonData = "{\"source\": \"core\",\"mobile\": \"17576075478\",\"password\": \"525241\"}";
		Response response = HttpHelper.create().addUrl("http://120.76.247.73:11006/login").addJsonParam(jsonData).request(HttpMethod.POST);
		
		System.out.println(response.getCode() + "===" + response.getJsonString());

	}

}
