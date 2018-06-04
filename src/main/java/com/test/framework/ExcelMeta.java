package com.test.framework;

import lombok.Getter;
import lombok.Setter;

/**
 * 测试数据表头信息
 *
 * @author vivi.zhang
 */
public enum ExcelMeta {

    TESTFUNCTIONNAME("testFunctionName"), INPUTJSON("inputJson"), EXPECTED("expected"), COMMON("备注");

    private String name;

    private ExcelMeta(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
