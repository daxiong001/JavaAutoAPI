package utils;

import org.testng.Assert;

import com.yzt.common.Response;

public class AssertUtil {
	
	public static void AssertResponeVO(Response actual, Response expected) {
		Assert.assertEquals(actual, expected);
	}

	/*
	 * 检验http code
	 * 
	 */
	public static void AssertResponeCode(Response actual) {
		Assert.assertEquals(actual.getHttpCode(), "200");
	}

	/*
	 * 检验业务成功与否result code
	 * 
	 */
	public static void AssertResponeResultCode(Response actual) {
		Assert.assertEquals(actual.getResultCode(), "200");
	}

	public static void AssertResponeJsonString(Response actual, String expectedJsonString) {
		String jsonString = actual.getJsonString();
		Assert.assertEquals(jsonString, expectedJsonString);
	}
}
