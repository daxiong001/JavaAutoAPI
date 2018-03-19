package utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.test.framework.ContextDoField;
import com.yzt.service.Context;
import com.yzt.service.ServiceFactory;

import contants.Contants;

public class ContextUtil {

	private static Logger logger = Logger.getLogger(ContextUtil.class);

	/*
	 * 遍历context field
	 */
	public static Object contextFieldTemplate(Class clazz, ContextDoField contextDoField) {
		Object result = null;
		final Context context = (Context) ServiceFactory.getInstance(Context.class);
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

	/*
	 * 获取context List<Object> objects 实体容器
	 */
	@SuppressWarnings("unchecked")
	public static List<Object> getContextObject(Class clazz) {
		final Context context = (Context) ServiceFactory.getInstance(Context.class);

		return (List<Object>) contextFieldTemplate(clazz, new ContextDoField() {

			@Override
			public Object execute(Field fd, Method getMethod, Class clazz) {
				List<Object> result = Lists.newArrayList();
				try {
					if (fd.getType().isAssignableFrom(List.class)) {
						List list = (List) getMethod.invoke(context, null);
						Object targetObject = clazz.newInstance();
						if (list.size() > 0) {
							for (int i = 0; i < list.size(); i++) {
								Object currentObject = list.get(i);
								if (currentObject.getClass().isAssignableFrom(clazz)
										&& targetObject instanceof Cloneable) {
									Method cloneMethod = clazz.getDeclaredMethod("clone", null);
									targetObject = cloneMethod.invoke(currentObject, null);
									result.add(targetObject);
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
	public static Map<String, Object> getContextMap() {
		final Context context = (Context) ServiceFactory.getInstance(Context.class);

		return (Map<String, Object>) contextFieldTemplate(null, new ContextDoField() {
			Map<String, Object> targetObject = Maps.newHashMap();

			@Override
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

	/*
	 * 根据context map中已有的sourceKey值新增targetKey键值对
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> convertContextMapKey(String targetKey, String sourceKey) {
		final Context context = (Context) ServiceFactory.getInstance(Context.class);

		return (Map<String, Object>) contextFieldTemplate(null, new ContextDoField() {
			@Override
			public Object execute(Field fd, Method getMethod, Class clazz) {
				try {
					if (fd.getType().isAssignableFrom(Map.class)) {
						Map map = (Map) getMethod.invoke(context, null);
						if (map.size() > 0) {
							Iterator iterator = map.keySet().iterator();
							while (iterator.hasNext()) {
								Object key = iterator.next();
								if (key.equals(sourceKey) && map.containsKey(sourceKey) && !map.containsKey(targetKey)) {
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

	public static String change(String src) {
		if (src != null) {
			StringBuffer sb = new StringBuffer(src);
			sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
			return sb.toString();
		} else {
			return null;
		}
	}
}
