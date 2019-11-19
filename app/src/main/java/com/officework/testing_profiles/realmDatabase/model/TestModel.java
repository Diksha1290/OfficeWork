package com.officework.testing_profiles.realmDatabase.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class TestModel extends RealmObject {
    @PrimaryKey
    private int test_id;
    private String test_name;
    private String testType;
    private int testDrawable;
    private int testStstus;
    private boolean requireRegistration;
    private boolean isDuel;
    private String testDes;

    public String getTestDes() {
        return testDes;
    }

    public void setTestDes(String testDes) {
        this.testDes = testDes;
    }



    public int getTest_id() {
        return test_id;
    }

    public void setTest_id(int test_id) {
        this.test_id = test_id;
    }

    public String getTest_name() {
        return test_name;
    }

    public void setTest_name(String test_name) {
        this.test_name = test_name;
    }

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public int getTestDrawable() {
        return testDrawable;
    }

    public void setTestDrawable(int testDrawable) {
        this.testDrawable = testDrawable;
    }

    public int isTestStstus() {
        return testStstus;
    }

    public void setTestStstus(int testStstus) {
        this.testStstus = testStstus;
    }

    public boolean isRequireRegistration() {
        return requireRegistration;
    }

    public void setRequireRegistration(boolean requireRegistration) {
        this.requireRegistration = requireRegistration;
    }

    public boolean isDuel() {
        return isDuel;
    }

    public void setDuel(boolean duel) {
        isDuel = duel;
    }
}