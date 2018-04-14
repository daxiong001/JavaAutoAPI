package utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.test.framework.ContextDoField;
import com.yzt.entity.CauseMeta;
import com.yzt.service.Context;
import com.yzt.service.ServiceFactory;

import contants.Contants;

public class ContextUtil {

	private static Logger logger = Logger.getLogger(ContextUtil.class);
	
	/**
	 * 根据类对象，从json字符串中获取类对象属性值，并存储至 context List<Object> objects容器中
	 * 
	 * @param inputJson
	 *            json字符串
	 * @param clazz
	 *            实体类对象
	 */
	public static void insertParams2Context(String inputJson, Class clazz) {
		analysisJson(inputJson, clazz);
	}
	

	/**从json字符串中获取单个键值对，直接存储键值对至容器
	 * 
	 * @param inputJson json 字符串
	 * @param key 源待获取的json key
	 */
	public static void insertParams2Context(String inputJson, String key) {
		analysisJson(inputJson, key);
	}
	
	/**
	 * 根据类对象，从json字符串中获取类对象属性值，并存储至 context List<Object> objects容器中,
	 * list的每个值亦会做为键值对存储在context map sc容器中，key = list.get(i);
	 * 
	 * @param inputJson
	 *            json字符串
	 * @param clazz
	 *            实体类对象
	 * @param list
	 *            实体类对象对应属性值
	 */
	public static void insertParams2Context(String inputJson, Class clazz, List<String> list) {
		analysisJson(inputJson, list);
		setObjectToContext(list,clazz);
		// CommonUtils.analysisJson(inputJson, list);
//		ContextUtil.setObjectToContext(clazz);
	}
	
	/**
	 * 存储多个键值对至容器，list的每个值亦会做为键值对存储在context map sc容器中，key = list.get(i);
	 * 
	 * @param inputJson
	 *            json字符串
	 * @param list
	 *            待获取的多个jsonkey
	 */
	public static void insertParams2Context(String inputJson,List<String> list) {
		analysisJson(inputJson, list);
	}
	
	/**
	 * 直接存储键值对至context Map<String, Object> sc容器
	 */
	public static void insertParam2Context(String key, String value) {
		setKeyValueToContext(key, value);
	}
	
	/**从json字符串中获取单个键值对，替换sourceKey为targetKey，存储键值对至context Map<String, Object> sc容器
	 * 
	 * @param inputJson json 字符串
	 * @param sourceKey 源待获取的json key
	 * @param targetKey 存储到context map sc容器中的key 对应sourceKey的value值
	 */
	public static void insertParam2Context(String inputJson, String sourceKey, String targetKey) {
		analysisJson(inputJson, sourceKey);
		convertContextMapKey(targetKey, sourceKey);
	}
	
	/**
	 * 从容器中获取参数clazz对象列表
	 * @param clazz
	 * @return
	 */
	public static <T> List<T> getParams(Class<T> clazz) {
		return (List<T>) getContextObject(clazz);
	}
	
	/**
	 * 从容器中获取参数Map对象
	 */
	public static Map<String, Object> getParams() {
		return getContextMap();	
	}
	
	/**
	 * 从容器中获取参数key,value值
	 */
	public static Object getParams(String key) {
		return getContextValueByKey(key);
	}
	
	/*
	 * 获取context List<Object> objects 实体容器
	 */
	@SuppressWarnings("unchecked")
	private static List<Object> getContextObject(Class clazz) {
		return (List<Object>) contextFieldTemplate(clazz, new ContextDoField() {
			Context context = (Context) ServiceFactory.getInstance(Context.class);
			public Object execute(Field fd, Method getMethod, Class clazz) {
				List<Object> result = Lists.newArrayList();
				try {
					if (fd.getType().isAssignableFrom(List.class)) {
						List list = (List) getMethod.invoke(context, null);
						Object targetObject = clazz.newInstance();
						if (list.size() > 0) {
							for (int i = 0; i < list.size(); i++) {
								Object currentObject = list.get(i);
								if (currentObject.getClass().isAssignableFrom(clazz)) {
									if (targetObject instanceof Cloneable) {
										Method cloneMethod = clazz.getDeclaredMethod("clone", null);//解决并发赋值冲突的问题
										targetObject = cloneMethod.invoke(currentObject, null);
										result.add(targetObject);
									}else {
										result.add(currentObject);
									}									
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return result;
			}
		});
	}

	/*
	 * 获取context 中map sc值
	 */
	@SuppressWarnings("unchecked")
	private static Map<String, Object> getContextMap() {
		return (Map<String, Object>) contextFieldTemplate(null, new ContextDoField() {
			Map<String, Object> targetObject = Maps.newHashMap();
			Context context = (Context) ServiceFactory.getInstance(Context.class);
			public Object execute(Field fd, Method getMethod, Class clazz) {
				try {
					if (fd.getType().isAssignableFrom(Map.class)) {
						Map map = (Map) getMethod.invoke(context, null);
						if (map.size() > 0) {
							Iterator iterator = map.keySet().iterator();
							while (iterator.hasNext()) {
								Object key = iterator.next();
								targetObject.put(key.toString(), map.get(key));
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return targetObject;
			}
		});
	}	

	/**
	 * 获取 context Map<String, Object> sc容器中对应key的value值
	 */
	private static Object getContextValueByKey(String key) {
		Context context = (Context) ServiceFactory.getInstance(Context.class);
		if (context.getSc().containsKey(key)) {
			return context.getValue(key);
		}else {
			return null;
		}
	}

	/*
	 * 根据context map中已有的sourceKey值新增targetKey键值对
	 */
	@SuppressWarnings("unchecked")
	private static Map<String, Object> convertContextMapKey(final String targetKey, final String sourceKey) {
		

		return (Map<String, Object>) contextFieldTemplate(null, new ContextDoField() {
			Context context = (Context) ServiceFactory.getInstance(Context.class);
			public Object execute(Field fd, Method getMethod, Class clazz) {
				try {
					Type fdType = fd.getGenericType();
					if (fd.getType().isAssignableFrom(Map.class)) {
						Map map = (Map) getMethod.invoke(context, null);
						if (map.size() > 0) {
							Iterator iterator = map.keySet().iterator();
							while (iterator.hasNext()) {
								Object key = iterator.next();
								if (key.equals(sourceKey) && map.containsKey(sourceKey)
										&& !map.containsKey(targetKey)) {
									context.addValue(targetKey, context.getValue(sourceKey));
								}
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		});
	}

	/*
	 * 遍历context field
	 */
	public static <T> Object contextFieldTemplate(Class<T> clazz, ContextDoField contextDoField) {
		Object result = null;
		final Context<T> context = (Context<T>) ServiceFactory.getInstance(Context.class);
		try {
			Field[] fds = context.getClass().getDeclaredFields();
			for (Field fd : fds) {
				if (!fd.isAccessible()) {
					fd.setAccessible(true);
				}
				Method method = context.getClass().getDeclaredMethod("get" + change(fd.getName()), null);
				result = contextDoField.execute(fd, method, clazz);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 直接存储键值对至context Map<String, Object> sc容器
	 */
	private static void setKeyValueToContext(String key, String value) {
		Context context = (Context) ServiceFactory.getInstance(Context.class);
		context.addValue(key, value);
	}

	/**
	 * 存储至 context List<Object> objects容器中
	 * 
	 * @param list
	 *            entity实体类对应的所有属性名字
	 * @param clazz
	 *            entity实体类对象
	 */
	private static void setObjectToContext(List<String> list, Class<?> clazz) {
		Context context = (Context) ServiceFactory.getInstance(Context.class);
		List<Map<String, Object>> tempList = context.getScs();
		int size = tempList.size();
		try {
			Constructor<?> c = clazz.getDeclaredConstructor();
			if (size > 0) {
				for (Map<String, Object> map : tempList) {
					int count = 0;
					if (map.keySet().size() == list.size()) {
						for (String st : list) {
							if (map.containsKey(st)) {
								count++;
							}
						}
					}
					if (count == list.size()) {
						Object cz = c.newInstance();
						for (String key : map.keySet()) {
							Field field = clazz.getDeclaredField(key);
							field.setAccessible(true);
							field.set(cz, map.get(key));
						}
						context.addObject(cz);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 存储实体类对象至 context List<Object> objects容器中
	 * 
	 * @param clazz
	 *            entity实体类对象
	 */
	private static void setObjectToContext(Class<?> clazz) {
		Context context = (Context) ServiceFactory.getInstance(Context.class);
		List<Map<String, Object>> tempList = context.getScs();
		int size = tempList.size();
		try {
			Constructor<?> c = clazz.getDeclaredConstructor();
			Field[] fds = clazz.getDeclaredFields();
			if (size > 0) {
				for (Map<String, Object> map : tempList) {
					int count = 0;
					for (Field fd : fds) {
						if (map.containsKey(fd)) {
							count++;
						}
					}
					if (count == map.size()) {
						Object cz = c.newInstance();
						for (String key : map.keySet()) {
							Field field = clazz.getDeclaredField(key);
							field.setAccessible(true);
							field.set(cz, map.get(key));
						}
						context.addObject(cz);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String change(String src) {
		if (src != null) {
			StringBuffer sb = new StringBuffer(src);
			sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
			return sb.toString();
		} else {
			return null;
		}
	}
	
	/**
	 * 读取json string中指定的多个key值，并存入context List<Object>容器中
	 * 
	 * @param inputJson
	 *            json string
	 * @param context
	 *            获取的key：value
	 * @param Class
	 *            待获取json的对象
	 */
	private static void analysisJson(String inputJson, Class clazz) {
		Object object = JSON.parse(inputJson);
		Context context = (Context) ServiceFactory.getInstance(Context.class);
		Field[] fields = clazz.getDeclaredFields();
		List<String> fieldlist = Lists.newArrayList();
		for (Field field : fields) {
			fieldlist.add(field.getName());
		}
		try {
			if (object instanceof JSONArray) {
				JSONArray jsonArray = (JSONArray) object;
				for (int i = 0; i < jsonArray.size(); i++) {
					analysisJson(jsonArray.get(i).toString(), clazz);
				}
			} else if (object instanceof JSONObject) {
				JSONObject jsonObject = (JSONObject) object;
				int count = 0;
				for (String jsonKey : jsonObject.keySet()) {
					if (fieldlist.contains(jsonKey)) {
						count++;
					} else if (jsonObject.get(jsonKey) instanceof JSONObject
							|| jsonObject.get(jsonKey) instanceof JSONArray) {
						analysisJson(jsonObject.get(jsonKey).toString(), clazz);
					}
				}
				if (count == fieldlist.size()) {
					Constructor<?> c = clazz.getDeclaredConstructor();
					Object cz = c.newInstance();
					for (String key : fieldlist) {
						Field field = clazz.getDeclaredField(key);
						field.setAccessible(true);
						field.set(cz, jsonObject.get(key));
					}
					context.addObject(cz);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 读取json string中指定的多个key值，并存入context容器中
	 * 
	 * @param inputJson
	 *            json string
	 * @param context
	 *            获取的key：value
	 * @param list
	 *            待获取json的key
	 */
	private static Context analysisJson(String inputJson, List<String> list) {
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
			int count = 0;
			for (String jsonKey : jsonObject.keySet()) {			
				if (list.contains(jsonKey)) {
//					tempMap.put(jsonKey, jsonObject.get(jsonKey));
//					context.addValue(jsonKey, jsonObject.get(jsonKey));
					count++;
				} else if (jsonObject.get(jsonKey) instanceof JSONObject
						|| jsonObject.get(jsonKey) instanceof JSONArray) {
					analysisJson(jsonObject.get(jsonKey).toString(), list);
				}
			}
			if (count == list.size()) {
				for (String key : list) {
					tempMap.put(key, jsonObject.get(key));
					context.addValue(key, jsonObject.get(key));
				}
			}
			if (!tempMap.isEmpty()) {
				context.addSCToList(tempMap);
			}
		}
		return context;
	}

	/**
	 * 读取json string中指定的单个key值，并存入context容器中
	 * 
	 * @param inputJson json字符串
	 * @param context
	 * @param key
	 * @return
	 */
	private static Context analysisJson(String inputJson, String key) {
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
}
