package com.officework.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Girish on 8/9/2016.
 */
public class AutomatedTestListModel implements Parcelable {
    String name;
    String testDes;
    String test_type;
    int test_id;
    boolean requireRegistration;
    int resource;
    int position;
    int isTestSuccess;
    private boolean isDuel;
    private String sensorValue;
    private String mAzimuth;

    public AutomatedTestListModel() {

    }

    public AutomatedTestListModel(String name, int resource, int isTestSuccess, int position) {
        this.name = name;
        this.resource = resource;
        this.isTestSuccess = isTestSuccess;
        this.position = position;
    }

    protected AutomatedTestListModel(Parcel in) {
        name = in.readString();
        testDes = in.readString();
        test_type = in.readString();
        test_id = in.readInt();
        requireRegistration = in.readByte() != 0;
        resource = in.readInt();
        position = in.readInt();
        isTestSuccess = in.readInt();
        isDuel = in.readByte() != 0;
        sensorValue = in.readString();
        mAzimuth = in.readString();
    }

    public static final Creator<AutomatedTestListModel> CREATOR = new Creator<AutomatedTestListModel>() {
        @Override
        public AutomatedTestListModel createFromParcel(Parcel in) {
            return new AutomatedTestListModel(in);
        }

        @Override
        public AutomatedTestListModel[] newArray(int size) {
            return new AutomatedTestListModel[size];
        }
    };

    public String getSensorValue() {
        return sensorValue;
    }

    public void setSensorValue(String sensorValue) {
        this.sensorValue = sensorValue;
    }

    public String getmAzimuth() {
        return mAzimuth;
    }

    public void setmAzimuth(String mAzimuth) {
        this.mAzimuth = mAzimuth;
    }

    public boolean isRequireRegistration() {
        return requireRegistration;
    }

    public void setRequireRegistration(boolean requireRegistration) {
        this.requireRegistration = requireRegistration;
    }

    public String getTest_type() {
        return test_type;
    }

    public void setTest_type(String test_type) {
        this.test_type = test_type;
    }

    public int getTest_id() {
        return test_id;
    }

    public void setTest_id(int test_id) {
        this.test_id = test_id;
    }

    public String getTestDes() {
        return testDes;
    }

    public void setTestDes(String testDes) {
        this.testDes = testDes;
    }

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getIsTestSuccess() {
        return isTestSuccess;
    }

    public void setIsTestSuccess(int isTestSuccess) {
        this.isTestSuccess = isTestSuccess;
    }

    public boolean isDuel() {
        return isDuel;
    }

    public void setDuel(boolean duel) {
        isDuel = duel;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(testDes);
        parcel.writeString(test_type);
        parcel.writeInt(test_id);
        parcel.writeByte((byte) (requireRegistration ? 1 : 0));
        parcel.writeInt(resource);
        parcel.writeInt(position);
        parcel.writeInt(isTestSuccess);
        parcel.writeByte((byte) (isDuel ? 1 : 0));
        parcel.writeString(sensorValue);
        parcel.writeString(mAzimuth);
    }
}
