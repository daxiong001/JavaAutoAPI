package utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.test.framework.ExcelMeta;
import com.test.framework.LogProxyFactory;
import com.yzt.service.Context;
import com.yzt.service.ServiceFactory;

import contants.Contants;
import exception.customsException;

/**
 * 公共方法
 * 
 * @author vivi.zhang
 *
 */
public class CommonUtils {
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

	/**
	 * Excel中读出的数据转换成带key=inputJson的map格式，value=json
	 * string，exp：{inputJson:{xxx}}
	 * 
	 * @param data
	 * @return
	 */
	public static Map<String, Object> inputJsonToMap(String data) {
		Map<String, Object> paramsMap = Maps.newHashMap();
		String value = "";
		String input = "";
		StringBuffer sb = new StringBuffer();
		Pattern pattern = Pattern.compile("inputJson=([\\s\\S]*)[\\}\\]]+");
		Matcher matcher = pattern.matcher(data);
		if (matcher.find()) {
			value = matcher.group(1);
		}
		if (!StringUtils.isEmpty(value)) {
			if (value.startsWith("\\{")) {
				input = sb.append("{").append(value).append("}").toString();
			}else if (value.startsWith("\\[")) {
				input = sb.append("[").append(value).append("]").toString();
			}			
			paramsMap.put(Contants.INPUT, input);
		}
		return paramsMap;
	}

	public static List JsonStringToList(String data) {
		List list = Lists.newArrayList();
		JSONArray redult = JSON.parseArray(data);
		Iterator<Object> array = (Iterator<Object>) redult.iterator();
		while (((Iterator<Object>) array).hasNext()) {
			list.add(array.next());
		}
		return list;
	}

	public static JSONObject MapToJsonObject(Map<String, Object> paramsMap) {
		JSONObject jsonObject = new JSONObject();
		for (Entry<String, Object> entry : paramsMap.entrySet()) {
			jsonObject.put(entry.getKey(), entry.getValue());
		}
		return jsonObject;
	}

	public static Object[][] copyArrays(Object[][] objects) {
		int rowNum = 0;
		int columnNum = 0;
		if (objects != null) {
			for (int i = 0; i < objects.length; i++) {
				if (objects[i].length == 0 || objects[i][0] == null) {
					continue;
				}
				rowNum++;
			}
		}
		columnNum = objects[rowNum - 1].length;
		Object[][] copyed = new Object[rowNum][columnNum];
		for (int i = 0; i < rowNum; i++) {
			for (int j = 0; j < columnNum; j++) {
				copyed[i][j] = objects[i][j];
			}
		}
		return copyed;
	}

	/**
	 * 测试用例参数格式转换公共方法
	 * 
	 * @param inputJson
	 *            入参：{name=login, inputJson={ "mobile": "13000000008", "passwd":
	 *            "13000000008" }} 返回：{ "mobile": "13000000008", "passwd":
	 *            "13000000008" }
	 * @return
	 */
	public static String getInputJsonParam(String inputJson) {
		Map<String, Object> input = Maps.newHashMap();
		input.putAll(CommonUtils.inputJsonToMap(inputJson));
		if (input.containsKey(Contants.INPUT)) {
			return (String) input.get(Contants.INPUT);
		} else {
			throw new customsException("数据表中无inputJson数据头，请确认！");
		}
	}

	/**
	 * inputJson 格式:{name=login, inputJson={ "mobile": "13000000008", "passwd":
	 * "13000000008" }}
	 * 
	 * @param inputJson
	 * @return Map<String, String>
	 */
	public static Map<String, String> stringToMap(String inputJson) {
		Map<String, String> input = Maps.newHashMap();
		inputJson = inputJson.substring(1, inputJson.length() - 1);
		String[] tokens = StringUtils.split(inputJson, ",");
		for (String string : tokens) {
			if (string.contains("=")) {
				String[] values = StringUtils.split(string, "=");
				input.put(values[0], values[1]);
			}
		}
		return input;
	}

	/**
	 * jsonArray转成List
	 * 
	 * @param jsonArray
	 * @return
	 */
	public static List<String> jsonArrayToList(String jsonArray) {
		List<String> list = Lists.newArrayList();
		JSONArray array = JSON.parseArray(jsonArray);
		for (int i = 0; i < array.size(); i++) {
			list.add(array.getString(i));
		}
		return list;
	}

	/**
	 * 获取Excel数据
	 * 
	 * @param classname
	 * @param methodname
	 * @return
	 */
	public static Object[][] getExcelData(String classname, String methodname) {
		Object[][] objects = new Object[1024][1];
		int i = 0;
		DataProviders data = new DataProviders(classname, methodname);
		while (data.hasNext()) {
			objects[i][0] = data.next().toString();
			i++;
		}
		return CommonUtils.copyArrays(objects);
	}

	/**
	 * 获取Excel数据
	 * 
	 * @param classname
	 * @param methodname
	 * @return
	 */
	public static List<Map<String, String>> getExcelData(String classname) {
		List<Map<String, String>> lists = Lists.newArrayList();
		DataProviders data = new DataProviders(classname);
		while (data.hasNext()) {
			lists.add(data.next());
		}
		return lists;
	}

	/**
	 * 把Excel获取的数据转成testng的数据
	 * 
	 * @param classname
	 * @param method
	 * @return
	 */
	public static Object[][] getTestNGData(String classname, Method method) {
		Object[][] result = new Object[1024][1];
		int i = 0;
		List<Map<String, String>> lists = Lists.newArrayList();
		lists = CommonUtils.getExcelData(classname);
		for (Map<String, String> map : lists) {
			for (String key : map.keySet()) {
				if (key.equals(ExcelMeta.TESTFUNCTIONNAME.getName()) && map.get(key).equals(method.getName())) {
					result[i][0] = map.get(ExcelMeta.INPUTJSON.getName());
					i++;
				}
			}
		}
		return CommonUtils.copyArrays(result);
	}

	/**
	 * 读取json string中指定的多个key值
	 * 
	 * @param inputJson
	 *            json string
	 * @param context
	 *            获取的key：value
	 * @param list
	 *            待获取json的key
	 */
	public static Context analysisJson(String inputJson, List<String> list) {
		Object object = JSON.parse(inputJson);	
		Context context = (Context) ServiceFactory.getInstance(Context.class);
		if (object instanceof JSONArray) {
			JSONArray jsonArray = (JSONArray) object;
			for (int i = 0; i < jsonArray.size(); i++) {
				analysisJson(jsonArray.get(i).toString(), list);
			}
		} else if (object instanceof JSONObject) {
			JSONObject jsonObject = (JSONObject) object;		
			Map<String, Object> tempMap = Maps.newHashMap();
			for (String jsonKey : jsonObject.keySet()) {				
				if (list.contains(jsonKey)) {
					tempMap.put(jsonKey, jsonObject.get(jsonKey));
					context.addValue(jsonKey, jsonObject.get(jsonKey));					
				}else if (jsonObject.get(jsonKey) instanceof JSONObject
						|| jsonObject.get(jsonKey) instanceof JSONArray) {
					analysisJson(jsonObject.get(jsonKey).toString(), list);
				}				
			}
			if (!tempMap.isEmpty()) {
				context.addSCToList(tempMap);
			}			
		}
		return context;
	}

	/**
	 * 读取json string中指定的单个key值
	 * 
	 * @param inputJson
	 * @param context
	 * @param key
	 * @return
	 */
	public static Context analysisJson(String inputJson, String key) {
		Object object = JSON.parse(inputJson);
		Map<String, Object> tempMap = Maps.newHashMap();
		Context context = (Context) ServiceFactory.getInstance(Context.class);
		if (object instanceof JSONArray) {
			JSONArray jsonArray = (JSONArray) object;
			for (int i = 0; i < jsonArray.size(); i++) {
				analysisJson(jsonArray.get(i).toString(), key);
			}
		} else if (object instanceof JSONObject) {
			JSONObject jsonObject = (JSONObject) object;
			for (String jsonKey : jsonObject.keySet()) {
				if (key.equals(jsonKey)) {
					tempMap.put(key, jsonObject.get(jsonKey));
					context.addValue(jsonKey, jsonObject.get(jsonKey));
				} else if (jsonObject.get(jsonKey) instanceof JSONObject
						|| jsonObject.get(jsonKey) instanceof JSONArray) {
					analysisJson(jsonObject.get(jsonKey).toString(), key);
				}
			}
			if (!tempMap.isEmpty()) {
				context.addSCToList(tempMap);
			}	
		}
		return context;
	}

	/**
	 * 更新json string中指定的多个key值
	 * 
	 * @param inputJson
	 * @param changedValue
	 *            待更新的key和value键值对
	 * @return
	 */
	public static Object analysisJsonAndUpdate(Object object, Map<String, Object> changedValue) {
		if (object instanceof JSONArray) {
			JSONArray jsonArray = (JSONArray) object;
			JSONArray jsonAr = new JSONArray();
			for (int i = 0; i < jsonArray.size(); i++) {
				jsonAr.add(analysisJsonAndUpdate(jsonArray.get(i), changedValue));
			}
			return jsonAr;
		} else if (object instanceof JSONObject) {
			JSONObject jsonObject = (JSONObject) object;
			JSONObject jsonOb = new JSONObject();
			for (String jsonKey : jsonObject.keySet()) {
				if (changedValue.containsKey(jsonKey)) {
					jsonOb.put(jsonKey, changedValue.get(jsonKey));
				} else if (jsonObject.get(jsonKey) instanceof JSONObject
						|| jsonObject.get(jsonKey) instanceof JSONArray) {
					jsonOb.put(jsonKey, analysisJsonAndUpdate(jsonObject.get(jsonKey), changedValue));
				} else {
					jsonOb.put(jsonKey, jsonObject.get(jsonKey));
				}
			}
			return jsonOb;
		} else {
			return object;
		}
	}

	/**
	 * 更新json string中指定的单个key和value值
	 * 
	 * @param inputJson
	 * @param changedValue
	 * @return
	 */
	public static Object analysisJsonAndUpdate(String inputJson, String key, Object value) {
		Object object = JSON.parse(inputJson);
		if (object instanceof JSONArray) {
			JSONArray jsonArray = (JSONArray) object;
			JSONArray jsonAr = new JSONArray();
			for (int i = 0; i < jsonArray.size(); i++) {
				jsonAr.add(analysisJsonAndUpdate(jsonArray.get(i).toString(), key, value));
			}
			return jsonAr;
		} else if (object instanceof JSONObject) {
			JSONObject jsonObject = (JSONObject) object;
			JSONObject jsonOb = new JSONObject();
			for (String jsonKey : jsonObject.keySet()) {
				if (key.equals(jsonKey)) {
					jsonOb.put(jsonKey, value);
				} else if (jsonObject.get(jsonKey) instanceof JSONObject
						|| jsonObject.get(jsonKey) instanceof JSONArray) {
					jsonOb.put(jsonKey, analysisJsonAndUpdate(jsonObject.get(jsonKey).toString(), key, value));
				} else {
					jsonOb.put(jsonKey, jsonObject.get(jsonKey));
				}
			}
			return jsonOb;
		} else {
			return object;
		}		
	}
	
	/**
	 * 直接存储键值对至context Map<String, Object> sc容器
	 */
	public static void setKeyValueToContext(String key,String value){
		Context context = (Context) ServiceFactory.getInstance(Context.class);
		context.addValue(key, value);
	}
	/**
	 * 存储至 context List<Object> objects容器中
	 * @param list entity实体类对应的所有属性名字
	 * @param clazz entity实体类对象
	 */
	public static void setObjectToContext(List<String> list,Class<?> clazz){
		Context context = (Context) ServiceFactory.getInstance(Context.class);
		List<Map<String, Object>> tempList = context.getScs();
		int size = tempList.size();
		boolean flag = false;
		if (size > 0) {
			for (Map<String, Object> map : tempList) {
				if (map.keySet().size() == list.size()) {
					for (String st : list) {
						if (map.containsKey(st)) {
							flag = true;
						}else {
							flag = false;
						}
					}
				}
			}
		}
		if (flag) {
			try {
				Constructor<?> c = clazz.getDeclaredConstructor();
				Object cz = c.newInstance();
				Field[] fds = clazz.getDeclaredFields();
				for (Field field : fds) {
					if (!field.isAccessible()) {
						field.setAccessible(true);
					}
					for (Map<String, Object> sg : tempList) {
						if (sg.containsKey(field.getName())) {
							field.set(cz, sg.get(field.getName()));
						}
					}
				}
				context.addObject(cz);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static Object getServiceProxyInstance(Class clazz) {
		LogProxyFactory logProxyFactory = new LogProxyFactory();
		return logProxyFactory.getProxyInstance(clazz);
	}

}
