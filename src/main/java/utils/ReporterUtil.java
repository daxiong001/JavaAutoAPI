package utils;

import java.text.DecimalFormat;

public class ReporterUtil {
    /*
     * 转换成功率格式
     */
    public static String rateConvertor(Double rate) {
        String result = "";
        DecimalFormat df = new DecimalFormat("0.00%");
        if (rate >= 1) {
            result = "100%";
        } else if (rate <= 0) {
            result = "0";
        } else {
            result = df.format(result);
        }
        return result;
    }

    /*
     * 转换测试用例状态格式
     */
    public static String statusConverter(Integer status) {
        String result = "";
        if (status == 0) {
            result = "passed";
        } else if (status == 1) {
            result = "failed";
        } else if (status == 2) {
            result = "skipped";
        } else {
            result = null;
        }
        return result;
    }
}
