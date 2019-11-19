package com.officework.models;

public class DiagonsticSyncModel {

    private boolean IsSuccess;
    private String ErrorMessage;
    private DiagnosticPojo Data;


    public DiagnosticPojo getData() {
        return Data;
    }

    public void setData(DiagnosticPojo data) {
        Data = data;
    }

    public boolean isSuccess() {
        return IsSuccess;
    }

    public void setSuccess(boolean success) {
        IsSuccess = success;
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        ErrorMessage = errorMessage;
    }
}
