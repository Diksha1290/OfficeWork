package com.officework.testing_profiles.realmDatabase.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SubTestMapModel extends RealmObject {
    @PrimaryKey
    private int sub_test_id;
    private int test_id;

    public int getSub_test_id() {
        return sub_test_id;
    }

    public void setSub_test_id(int sub_test_id) {
        this.sub_test_id = sub_test_id;
    }

    public int getTest_id() {
        return test_id;
    }

    public void setTest_id(int test_id) {
        this.test_id = test_id;
    }
}