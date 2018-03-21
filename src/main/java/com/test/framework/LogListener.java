package com.test.framework;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import com.yzt.service.Context;
import com.yzt.service.ServiceFactory;

import utils.ContextUtil;

public class LogListener extends TestListenerAdapter {

	private static Logger logger = Logger.getLogger(LogListener.class);
	private static Context context = (Context) ServiceFactory.getInstance(Context.class);

	@Override
	public void onTestSuccess(ITestResult tr) {
		logger.info("===========================" + tr.getClass() + "." + tr.getName()
				+ " run successed!============================");
		logger.info("************ context容器数据信息收集 ************ start ************");
		logContext();
		logger.info("************ context容器数据信息收集 ************ end ************");
		logger.info("##############################################################");
		logger.info("####################       具体用例日志                         ################");
	}

	@Override
	public void onTestFailure(ITestResult tr) {
		logger.info("===========================" + tr.getClass() + "." + tr.getName()
				+ " run failured!!============================");
		logger.info("************ context容器数据信息收集 ************ start ************");
		logContext();
		logger.info("************ context容器数据信息收集 ************ end ************");
		logger.info("##############################################################");
		logger.info("####################       具体用例日志                         ################");
	}

	@Override
	public void onTestSkipped(ITestResult tr) {
		logger.info("===========================" + tr.getTestName() + "." + tr.getName()
				+ " run skiped!!!!!============================");
		logger.info("************ context容器数据信息收集 ************ start ************");
		logContext();
		logger.info("************ context容器数据信息收集 ************ end ************");
		logger.info("##############################################################");
		logger.info("####################       具体用例日志                         ################");
	}

	private void logContext() {
		
		ContextUtil.contextFieldTemplate(null,new ContextDoField() {

			@Override
			public Object execute(Field fd, Method getMethod,Class clazz) {
				try {
					if (fd.getType().isAssignableFrom(List.class)) {
						List list = (List) getMethod.invoke(context, null);
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
										logger.info("***" + field.getName() + " : " + field.get(object) + "***");
									}
								}
							}
						}
					} else if (fd.getType().isAssignableFrom(Map.class)) {
						Map map = (Map) getMethod.invoke(context, null);
						if (map.size() > 0) {
							logger.info("***context have Map type,size : " + map.size() + "***");
							Iterator iterator = map.keySet().iterator();
							while (iterator.hasNext()) {
								Object key = iterator.next();
								logger.info("***" + key.toString() + " : " + map.get(key) + "***");
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
}
