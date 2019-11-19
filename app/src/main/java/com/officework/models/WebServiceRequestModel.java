package com.officework.models;

/**
 * Created by prerna.kapoor on 1/19/2016.
 */
public class WebServiceRequestModel {

    String Url = "";
    String jsonData = "";

    public String getDialogMessage() {
        return dialogMessage;
    }

    public void setDialogMessage(String dialogMessage) {
        this.dialogMessage = dialogMessage;
    }

    public String getDialogTitle() {
        return dialogTitle;
    }

    public void setDialogTitle(String dialogTitle) {
        this.dialogTitle = dialogTitle;
    }

    String dialogMessage = "";
    String dialogTitle = "";
    boolean isPost = false, isDialogEnabled = false;

    public boolean isDialogEnabled() {
        return isDialogEnabled;
    }

    public void setIsDialogEnabled(boolean isDialogEnabled) {
        this.isDialogEnabled = isDialogEnabled;
    }

    public boolean isPost() {
        return isPost;
    }

    public void setIsPost(boolean isPost) {
        this.isPost = isPost;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

}
