package utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.test.framework.ContextDoField;
import com.yzt.service.Context;
import com.yzt.service.ServiceFactory;

public class ContextUtil {
	
	public static void analysisContextField(ContextDoField contextDoField){		
		try {
			Context context = (Context) ServiceFactory.getInstance(Context.class);
			Field[] fds = context.getClass().getDeclaredFields();
			for (Field fd : fds) {
				if (!fd.isAccessible()) {
					fd.setAccessible(true);
				}
				Method method = context.getClass().getDeclaredMethod("get" + change(fd.getName()), null);
				contextDoField.execute(fd,method);
			}
		} catch (Exception e) {		
			e.printStackTrace();
		}
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
