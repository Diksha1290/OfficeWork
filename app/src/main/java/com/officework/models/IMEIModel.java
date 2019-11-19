package com.officework.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class IMEIModel extends RealmObject {
    @PrimaryKey
    private String id;
    private String Telephonic_value;

    public IMEIModel() {

    }

    public IMEIModel(String id, String Telephonic_value) {
        this.id = id;
        this.Telephonic_value = Telephonic_value;
    }

    public String getTelephonic_value() {
        return Telephonic_value;
    }

    public void setTelephonic_value(String telephonic_value) {
        Telephonic_value = telephonic_value;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}