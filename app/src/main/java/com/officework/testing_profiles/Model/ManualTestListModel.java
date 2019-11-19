package com.officework.testing_profiles.Model;

public class ManualTestListModel {
    String name;
    String description;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    int resource;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    int position;

    public int getIsTestSuccess() {
        return isTestSuccess;
    }

    public void setIsTestSuccess(int isTestSuccess) {
        this.isTestSuccess = isTestSuccess;
    }

    int isTestSuccess;

    public ManualTestListModel() {

    }

    public ManualTestListModel(String name, int resource, int isTestSuccess, int position, String description) {
        this.name = name;
        this.resource = resource;
        this.isTestSuccess = isTestSuccess;
        this.position = position;
        this.description = description;
    }
}
