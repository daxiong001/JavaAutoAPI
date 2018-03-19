package com.test.framework;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class Suite {
	/*
	 * 套件名
	 */
	private String name;
	/*
	 * 套件启动时间
	 */
	private Date startTime;
	/*
	 * 套件结束时间
	 */
	private Date endTime;
	/*
	 * 用例总数
	 */
	private Integer sum;
	/*
	 * passed 用例总数
	 */
	private Integer passedNum;
	/*
	 * failed 用例总数
	 */
	private Integer failedNum;
	/*
	 * skipped 用例总数
	 */
	private Integer skippedNum;
	/*
	 * passed 用例集
	 */
	private List<TestCase> passed = Lists.newArrayList();
	/*
	 * failed 用例集
	 */
	private List<TestCase> failed = Lists.newArrayList();
	/*
	 * skipped 用例集
	 */
	private List<TestCase> skipped = Lists.newArrayList();
	/*
	 * 成功率
	 */
	private Double passedRate;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Integer getSum() {
		return sum;
	}

	public void setSum(Integer sum) {
		this.sum = sum;
	}

	public Integer getPassedNum() {
		return passedNum;
	}

	public void setPassedNum(Integer passedNum) {
		this.passedNum = passedNum;
	}

	public Integer getFailedNum() {
		return failedNum;
	}

	public void setFailedNum(Integer failedNum) {
		this.failedNum = failedNum;
	}

	public Integer getSkippedNum() {
		return skippedNum;
	}

	public void setSkippedNum(Integer skippedNum) {
		this.skippedNum = skippedNum;
	}

	public List<TestCase> getPassed() {
		return passed;
	}

	public void addPassed(TestCase testCase) {
		this.passed.add(testCase);
	}

	public List<TestCase> getFailed() {
		return failed;
	}

	public void addFailed(TestCase testCase) {
		this.failed.add(testCase);
	}

	public List<TestCase> getSkipped() {
		return skipped;
	}

	public void addSkipped(TestCase testCase) {
		this.skipped.add(testCase);
	}

	public Double getPassedRate() {
		return passedRate;
	}

	public void setPassedRate(Double passedRate) {
		this.passedRate = passedRate;
	}

	public String rateConvertor(Double rate) {
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

}
