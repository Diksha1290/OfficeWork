package com.officework.models;

/**
 * Created by Diksha on 1/24/2019.
 */

public class TestObject {

    private int TestID;
    private String TestName;
    private String TestStatus;
    private int TestResult ;

    public int getTestResult() {
        return TestResult;
    }

    public void setTestResult(int testResult) {
        TestResult = testResult;
    }

    public int getTestID() {
        return TestID;
    }

    public void setTestID(int testID) {
        TestID = testID;
    }

    public String getTestName() {
        return TestName;
    }

    public void setTestName(String testName) {
        TestName = testName;
    }

    public String getTestStatus() {
        return TestStatus;
    }

    public void setTestStatus(String testStatus) {
        TestStatus = testStatus;
    }
}
