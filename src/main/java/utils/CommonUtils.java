package utils;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.codec.binary.Base64;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;

public class CommonUtils {
	
	public static String Base64Decode(String base64String) {
		return new String(Base64.decodeBase64(base64String));
	}
	
	public static String Base64Encode(final byte[] binaryData) {  
        return new String(Base64.encodeBase64(binaryData));  
    }  
	
	public static String MapToJsonString(Map<String, Object> paramsMap) {
		return JSONObject.toJSONString(paramsMap);
	}
	
	public static Map<String, Object> JsonStringToMap(String data) {
		Map<String, Object> paramsMap = Maps.newHashMap();
		JSONObject redult = JSONObject.parseObject(data);
		for (Entry<String, Object> entry : redult.entrySet()) {
			paramsMap.put(entry.getKey(), entry.getValue());
		}
		return paramsMap;
	}
	
	public static JSONObject MapToJsonObject(Map<String, Object> paramsMap) {
		JSONObject jsonObject = new JSONObject();
		for (Entry<String, Object> entry : paramsMap.entrySet()) {
			jsonObject.put(entry.getKey(), entry.getValue());
		}
		return jsonObject;
	}
	
	public static String markJWT(String jwt) {
		return "Bearer " + jwt;
	}

	public static void main(String[] args) {
		String header = "eyJraWQiOiJhY2E3Mjk2Ni00NTk3LTQxYWQtYWFiZC0xOWNlNWI4MGJmOTIiLCJhbGciOiJSUzI1NiJ9";
		String payload = "eyJpc3MiOiJpc3N1ZXIiLCJhdWQiOiJhdWRpZW5jZSIsImV4cCI6MTUyMTI1MjA1NywianRpIjoiczZ2OTZTeHRMa24xU0VTXzQ3UmVRQSIsImlhdCI6MTUyMDA0MjQ1NywibmJmIjoxNTIwMDQyMzM3LCJzdWIiOiIxNzU3NjA3NTQ3OCIsImF0dHJpYnV0ZXMiOiJ7XCJtb2JpbGVcIjpcIjE3NTc2MDc1NDc4XCIsXCJyZWFsTmFtZVwiOlwi5oi06ZuE5Z2kLea1i-ivlVwiLFwiZW1haWxWZXJpZnlcIjpmYWxzZSxcIiRleHBpcmF0aW9uLXRpbWVcIjoxNTIxMjUyMDU3MzQ1LFwic3RhdHVzXCI6XCJhY3RpdmF0ZWRcIn0ifQ";

		System.out.println("header = " + Base64Decode(header));
		System.out.println("payload = " + Base64Decode(payload));
		
		Map<String, Object> paramsMap = Maps.newHashMap();
		paramsMap.put("a", "aa");
		
//		System.out.println(MapToJson(paramsMap));
	}

}
