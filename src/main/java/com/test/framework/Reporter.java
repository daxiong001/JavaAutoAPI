package com.test.framework;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.xml.XmlSuite;
import com.google.common.collect.Lists;

import utils.DateUtil;
import utils.ReporterUtil;

/**
 * 输出测试报告
 *
 * @author vivi.zhang
 */
public class Reporter implements IReporter {

    private List<Suite> results = Lists.newArrayList();
    private Double passRate = 0.0;
    private Integer caseSum = 0;
    private Integer passedNum = 0;
    private Integer failedNum = 0;
    private Integer skippedNum = 0;

    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        for (ISuite iSuite : suites) {
            int suiteCaseSum = 0, suitePassedNum = 0, suiteFailedNum = 0, suiteSkippedNum = 0;
            Suite suite = new Suite();
            Map<String, ISuiteResult> suiteCaseResults = iSuite.getResults();
            for (ISuiteResult ist : suiteCaseResults.values()) {
                ITestContext cx = ist.getTestContext();
                suite.setName(cx.getName());
                suite.setStartTime(cx.getStartDate());
                suite.setEndTime(cx.getEndDate());
                suiteCaseSum = cx.getAllTestMethods().length;

                suitePassedNum = cx.getPassedTests().size();
                Set<ITestResult> result = cx.getPassedTests().getAllResults();
                for (ITestResult iTestResult : result) {
                    TestCase testCase = new TestCase();
                    testCase.setClassName(iTestResult.getTestClass().getName());
                    testCase.setMethodName(iTestResult.getName());
                    testCase.setStatus(0);
                    try {
                        Class<?> clazz = Class.forName(iTestResult.getTestClass().getName());
                        Method method = clazz.getMethod(iTestResult.getName(), new Class[]{String.class});
                        if (method.isAnnotationPresent(CaseMeta.class)) {
                            CaseMeta cMeta = method.getAnnotation(CaseMeta.class);
                            testCase.setName(cMeta.value());
                        } else {
                            testCase.setName("");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    suite.addPassed(testCase);
                }

                suiteFailedNum = cx.getFailedTests().size();
                result = cx.getFailedTests().getAllResults();
                for (ITestResult iTestResult : result) {
                    TestCase testCase = new TestCase();
                    testCase.setClassName(iTestResult.getTestClass().getName());
                    testCase.setMethodName(iTestResult.getName());
                    testCase.setStatus(1);
                    testCase.setThrowable(iTestResult.getThrowable());
                    try {
                        Class<?> clazz = Class.forName(iTestResult.getTestClass().getName());
                        Method method = clazz.getMethod(iTestResult.getName(), new Class[]{String.class});
                        if (method.isAnnotationPresent(CaseMeta.class)) {
                            CaseMeta cMeta = method.getAnnotation(CaseMeta.class);
                            testCase.setName(cMeta.value());
                        } else {
                            testCase.setName("");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    suite.addFailed(testCase);
                }

                suiteSkippedNum = cx.getSkippedTests().size();
                result = cx.getSkippedTests().getAllResults();
                for (ITestResult iTestResult : result) {
                    TestCase testCase = new TestCase();
                    testCase.setClassName(iTestResult.getTestClass().getName());
                    testCase.setMethodName(iTestResult.getName());
                    testCase.setStatus(2);
                    testCase.setThrowable(iTestResult.getThrowable());
                    try {
                        Class<?> clazz = Class.forName(iTestResult.getTestClass().getName());
                        Method method = clazz.getMethod(iTestResult.getName(), new Class[]{String.class});
                        if (method.isAnnotationPresent(CaseMeta.class)) {
                            CaseMeta cMeta = method.getAnnotation(CaseMeta.class);
                            testCase.setName(cMeta.value());
                        } else {
                            testCase.setName("");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    suite.addFailed(testCase);
                }
            }

            caseSum += suiteCaseSum;
            passedNum += suitePassedNum;
            failedNum += suiteFailedNum;
            skippedNum += suiteSkippedNum;

            suite.setPassedNum(suitePassedNum);
            suite.setFailedNum(suiteFailedNum);
            suite.setSkippedNum(suiteSkippedNum);
            suite.setSum(suiteCaseSum);
            if (suiteCaseSum != 0 || suitePassedNum != 0) {
                suite.setPassedRate((double) (suitePassedNum / suiteCaseSum));
            } else {
                suite.setPassedRate(0.0);
            }
            results.add(suite);
        }
        if (caseSum != 0 || passedNum != 0) {
            passRate = (double) (passedNum / caseSum);
        }

        write2file(generateHtml(), outputDirectory);
    }

    private String generateHtml() {

        String line = "";
        InputStream ips = Thread.currentThread().getContextClassLoader().getResourceAsStream("reporterTemplate.html");
        BufferedReader in = new BufferedReader(new InputStreamReader(ips));

        StringBuilder tableOfContents = new StringBuilder();

        try {
            while ((line = in.readLine()) != null) {
                tableOfContents.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        tableOfContents.append("\n<body>\n");
        if (results.size() > 1) {
            tableOfContents.append("<h2>").append("Suite Summary Reporter").append("</h2>\n")
                    .append("<table> class=\"table table-striped table-bordered\"\n").append("<thead>\n")
                    .append("<tr>\n").append("<th>").append("Suite Total").append("</th>\n").append("<th>")
                    .append("Total").append("</th>\n").append("<th>").append("Passed").append("</th>\n").append("<th>")
                    .append("Failed").append("</th>\n").append("<th>").append("Skipped").append("</th>\n")
                    .append("<th>").append("Passed Rate").append("</th>\n").append("</tr>\n").append("</thead>\n")
                    .append("<tbody>\n").append("<tr>\n").append("<td>").append(results.size()).append("</td>\n")
                    .append("<td>").append(caseSum).append("</td>\n").append("<td>").append(passedNum).append("</td>\n")
                    .append("<td>").append(failedNum).append("</td>\n").append("<td>").append(skippedNum)
                    .append("</td>\n").append("<td>").append(ReporterUtil.rateConvertor(passRate)).append("</td>\n")
                    .append("</tr>\n").append("</tbody>\n").append("</table>\n\n\n");
        }

        for (Suite suite : results) {
            List<TestCase> passedCases = suite.getPassed();
            List<TestCase> failedCases = suite.getFailed();
            List<TestCase> skippedCases = suite.getSkipped();

            tableOfContents.append("<h2>").append("Test Suite : " + suite.getName()).append("</h2>\n")
                    .append("<table class=\"table table-striped table-bordered\">\n").append("<h3>")
                    .append("Summary Reporter").append("</h3>\n").append("<thead>\n").append("<tr>\n").append("<th>")
                    .append("# Total").append("</th>\n").append("<th>").append("# Passed").append("</th>\n")
                    .append("<th>").append("# Failed").append("</th>\n").append("<th>").append("# Skipped")
                    .append("</th>\n").append("<th>").append("# Passed Rate").append("</th>\n").append("<th>")
                    .append("# Start Time").append("</th>\n").append("<th>").append("# End Time").append("</th>\n")
                    .append("</tr>\n").append("</thead>\n").append("<tbody>\n").append("<tr>\n").append("<td>")
                    .append(suite.getSum()).append("</td>\n").append("<td>").append(suite.getPassedNum())
                    .append("</td>\n").append("<td>").append(suite.getFailedNum()).append("</td>\n").append("<td>")
                    .append(suite.getSkippedNum()).append("</td>\n").append("<td>")
                    .append(ReporterUtil.rateConvertor(suite.getPassedRate())).append("</td>\n").append("<td>")
                    .append(DateUtil.parseDateToString(suite.getStartTime(), DateUtil.FORMAT7)).append("</td>\n")
                    .append("<td>").append(DateUtil.parseDateToString(suite.getEndTime(), DateUtil.FORMAT7))
                    .append("</td>\n").append("</tr>\n").append("</tbody>\n\n\n");

            tableOfContents.append("<table class=\"table table-striped table-bordered\">\n").append("<h3>")
                    .append("Detail Result").append("</h3>\n").append("<thead>\n").append("<tr>\n")
                    .append("<th>").append("# Name").append("</th>\n")
                    .append("<th>").append("# Class Name").append("</th>\n")
                    .append("<th>").append("# Method Name").append("</th>\n")
                    .append("<th>").append("# Status").append("</th>\n").append("<th>").append("# Error")
                    .append("</th>\n").append("</tr>\n").append("</thead>\n").append("<tbody>\n");

            if (passedCases != null || passedCases.size() > 0) {
                for (TestCase ts : passedCases) {
                    tableOfContents.append("<tr  class=\"success\">\n")
                            .append("<td>").append(ts.getName()).append("</td>\n")
                            .append("<td>").append(ts.getClassName())
                            .append("</td>\n").append("<td>").append(ts.getMethodName()).append("</td>\n")
                            .append("<td>").append(ReporterUtil.statusConverter(ts.getStatus())).append("</td>\n")
                            .append("<td>").append("").append("</td>\n").append("</tr>\n");
                }
            }

            if (failedCases != null || failedCases.size() > 0) {
                for (TestCase ts : failedCases) {
                    tableOfContents.append("<tr class=\"danger\">\n")
                            .append("<td>").append(ts.getName()).append("</td>\n")
                            .append("<td>").append(ts.getClassName())
                            .append("</td>\n").append("<td>").append(ts.getMethodName()).append("</td>\n")
                            .append("<td>").append(ReporterUtil.statusConverter(ts.getStatus())).append("</td>\n")
                            .append("<td>").append(ts.getThrowable()).append("</td>\n").append("</tr>\n");
                }
            }

            if (skippedCases != null || skippedCases.size() > 0) {
                for (TestCase ts : skippedCases) {
                    tableOfContents.append("<tr class=\"warning\">\n")
                            .append("<td>").append(ts.getName()).append("</td>\n")
                            .append("<td>").append(ts.getClassName())
                            .append("</td>\n").append("<td>").append(ts.getMethodName()).append("</td>\n")
                            .append("<td>").append(ReporterUtil.statusConverter(ts.getStatus())).append("</td>\n")
                            .append("<td>").append(ts.getThrowable()).append("</td>\n").append("</tr>\n");
                }
            }
            tableOfContents.append("</tbody>\n").append("</table>\n");
        }
        tableOfContents.append("</body>\n").append("</html>\n");

        return tableOfContents.toString();
    }

    private void write2file(String data, String path) {
        String fullPath = path + File.separator + "customReporter.html";
        File file = new File(fullPath);
        if (file.exists()) {
            file.delete();
        }
        PrintWriter pfp = null;
        try {
            file.createNewFile();
            pfp = new PrintWriter(file, "UTF-8");
            pfp.print(data + "\n");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            pfp.close();
        }
    }

}
