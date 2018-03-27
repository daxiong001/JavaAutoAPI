package com.test.framework;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

import com.yzt.service.Context;
import com.yzt.service.ServiceFactory;

import utils.ContextUtil;

public class LogListener extends TestListenerAdapter {

	private static Logger logger = Logger.getLogger(LogListener.class);

	@Override
	public void onTestSuccess(ITestResult tr) {
		logger.info("====" + tr.getClass() + "." + tr.getName() + " 执行成功!====");
	}

	@Override
	public void onTestFailure(ITestResult tr) {
		logger.info("====" + tr.getClass() + "." + tr.getName() + "  执行失败!!====");
	}

	@Override
	public void onTestSkipped(ITestResult tr) {
		logger.info("====由于依赖的测试用例执行失败，导致 " + tr.getTestName() + "." + tr.getName() + " 未执行!!!====");
	}

	@Override
	public void onFinish(ITestContext testContext) {
		logger.info("**** context容器数据信息收集汇总    **** start ****");
		logContext();
		logger.info("**** context容器数据信息收集汇总    **** end ****");	
	}
	
	private void logContext() {

		ContextUtil.contextFieldTemplate(null, new ContextDoField() {
			Context context = (Context) ServiceFactory.getInstance(Context.class);

			@Override
			public Object execute(Field fd, Method getMethod, Class clazz) {
				try {
					if (fd.getType().isAssignableFrom(List.class)) {
						List list = (List) getMethod.invoke(context, null);
						if (list.size() > 0) {
							logger.info("*** 实体容器 List<Object> objects 具体参数如下所示  ***");
							for (int i = 0; i < list.size(); i++) {
								Object object = list.get(i);
								if (!(object instanceof Map<?, ?>)) {
									logger.info("** 第" + i + "个实体类型是  : " + object.getClass().getName() + " **");
									Field[] fields = object.getClass().getDeclaredFields();
									for (Field field : fields) {
										if (!field.isAccessible()) {
											field.setAccessible(true);
										}
										logger.info("* " + object.getClass().getName() + " 包含属性 :" + field.getName() + " = " + field.get(object) + " *");
									}
								}
							}
						}
					} else if (fd.getType().isAssignableFrom(Map.class)) {
						Map map = (Map) getMethod.invoke(context, null);
						if (map.size() > 0) {
							logger.info("***容器 Map<String, Object> sc 包含" + map.size() + "个键值对，具体参数如下所示 ***");
							Iterator iterator = map.keySet().iterator();
							while (iterator.hasNext()) {
								Object key = iterator.next();
								logger.info("** " + key.toString() + " : " + map.get(key) + " **");
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
