package com.test.framework;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.activation.FileDataSource;

import org.apache.log4j.Logger;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import com.yzt.service.Context;
import com.yzt.service.ServiceFactory;

public class LogListener extends TestListenerAdapter {

	private static Logger logger = Logger.getLogger(LogListener.class);
	private static Context context = (Context) ServiceFactory.getInstance(Context.class);

	@Override
	public void onTestSuccess(ITestResult tr) {
		logger.info("===========================" + tr.getClass() + "." + tr.getName() + " run successed!============================");
		logger.info("************ current context massage ************ start ************");
		logContext();
		logger.info("************ current context massage ************ end ************");
	}

	@Override
	public void onTestFailure(ITestResult tr) {
		logger.info("===========================" + tr.getClass() + "." + tr.getName() + " run failured!!============================");
		logger.info("************ current context massage ************ start ************");
		logContext();
		logger.info("************ current context massage ************ end ************");
	}

	@Override
	public void onTestSkipped(ITestResult tr) {
		logger.info("===========================" + tr.getTestName() + "." + tr.getName() + " run skiped!!!!!============================");
		logger.info("************ current context massage ************ start ************");
		logContext();
		logger.info("************ current context massage ************ end ************");
	}

	private void logContext() {		
		try {
			Field[] fds = context.getClass().getDeclaredFields();
			for (Field fd : fds) {
				if (!fd.isAccessible()) {
					fd.setAccessible(true);
				}
				Method method = context.getClass().getDeclaredMethod("get" + change(fd.getName()), null);
				if (fd.getType().isAssignableFrom(List.class)) {
					List list = (List) method.invoke(context, null);
					if (list.size() > 0) {
						for (int i = 0; i < list.size(); i++) {
							Object object = list.get(i);
							if (!(object instanceof Map<?, ?>)) {
								logger.info("***" + i + " : context have List type " + object.getClass().getName());
								Field[] fields = object.getClass().getDeclaredFields();
								for (Field field : fields) {
									if (!field.isAccessible()) {
										field.setAccessible(true);
									}
									logger.info("***" + field.getName() + " : " + field.get(object)+ "***");
								}
							}
						}
					}
				}else if(fd.getType().isAssignableFrom(Map.class)) {
					Map map = (Map) method.invoke(context, null);					
					if (map.size() > 0) {
						logger.info("***context have Map type,size : " + map.size() + "***");
						Iterator iterator = map.keySet().iterator();
						while (iterator.hasNext()) {
							Object key = iterator.next();
							logger.info("***" + key.toString() + " : " + map.get(key) + "***");							
						}
					}
				}
			}
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param 
	 *            
	 * @return 
	 */
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
