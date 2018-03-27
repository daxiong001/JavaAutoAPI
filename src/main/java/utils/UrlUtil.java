package utils;

import com.yzt.common.Env;

import exception.customsException;

public class UrlUtil {
	
	/**
	 * 获取参数配置文件中服务器地址
	 * 
	 * @return
	 */
	public static String getUrl(String key) {
		if (Env.getEnv().containsKey(key)) {
			return Env.getEnv().get(key);
		} else {
			throw new customsException("参数配置文件中未定义url参数值，请定义");
		}
	}
}
