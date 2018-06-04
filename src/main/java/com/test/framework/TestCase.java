package com.test.framework;

public class TestCase {
    /*
     * 用例名
     */
    private String name;
    /*
     * 用例类名
     */
    private String className;
    /*
     * 测试方法名
     */
    private String methodName;
    /*
     * 异常
     */
    private Throwable throwable;
    /*
     * 用例执行状态，0：passed ， 1: failed ，2：sckipped
     */
    private Integer status;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
