package com.test.framework;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.apache.log4j.Logger;

import com.yzt.common.Response;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class LogProxyFactory implements MethodInterceptor {

	private Class<?> target;

	private Logger logger = Logger.getLogger(LogProxyFactory.class);

	public Object getProxyInstance(Class clazz) {
		this.target = clazz;
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(this.target);
		enhancer.setCallback(this);
		return enhancer.create();
	}

	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {

		if (method.isAnnotationPresent(CaseMeta.class)) {
			CaseMeta an = method.getAnnotation(CaseMeta.class);
			logger.info("---- 测试用例 : <" + an.value() + "> 开始执行 ----");
		}

		logger.info("---- 测试用例逻辑类 : <" + obj.getClass() + "> ----");
		logger.info("---- 测试用例逻辑方法 : <" + method.getName() + "> ----");
		for (Object object : args) {
			logger.info("---- 请求实际入参 : " + object.toString() + " ----");
		}
		Object result = proxy.invokeSuper(obj, args);
		if (result != null) {
			if (result.getClass().isAssignableFrom(Response.class)) {
				logger.info("---- 请求实际返回结果 : " + ((Response) result).getJsonString() + " ----");
			}else {
				logger.info("---- 请求实际返回结果 : " + result.toString() + " ----");
			}			
		}else {
			logger.info("---- 请求实际返回结果为空  : {} ----");
		}		
		return result;
	}

}
