package com.officework.models;

import java.io.Serializable;

public class DiagnosticListPojo implements Serializable {
    public int TestID;
    public int DiagnosticTypeID;
    public String TestName;
    public String DiagnosticSequence;
    public int ParentTestID;
    public String DiagnosticProfile;
    public int getTestID() {
        return TestID;
    }

    public void setTestID(int testID) {
        TestID = testID;
    }

    public int getDiagnosticTypeID() {
        return DiagnosticTypeID;
    }

    public void setDiagnosticTypeID(int diagnosticTypeID) {
        DiagnosticTypeID = diagnosticTypeID;
    }

    public String getTestName() {
        return TestName;
    }

    public void setTestName(String testName) {
        TestName = testName;
    }

    public String getDiagnosticSequence() {
        return DiagnosticSequence;
    }

    public void setDiagnosticSequence(String diagnosticSequence) {
        DiagnosticSequence = diagnosticSequence;
    }

    public int getParentTestID() {
        return ParentTestID;
    }

    public void setParentTestID(int parentTestID) {
        ParentTestID = parentTestID;
    }

    public String getDiagnosticProfile() {
        return DiagnosticProfile;
    }

    public void setDiagnosticProfile(String diagnosticProfile) {
        DiagnosticProfile = diagnosticProfile;
    }





}
