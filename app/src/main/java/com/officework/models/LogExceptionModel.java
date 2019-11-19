package com.officework.models;

/**
 * Created by Ashwani on 6/26/2017.
 */

public class LogExceptionModel {
    String UDI;
    String ExceptionDateTime;
    String RequestType;
    String MethodName;
    String ExceptionDetail;
    String StackTrace;

    public String getUDI() {
        return UDI;
    }

    public void setUDI(String UDI) {
        this.UDI = UDI;
    }

    public String getExceptionDateTime() {
        return ExceptionDateTime;
    }

    public void setExceptionDateTime(String exceptionDateTime) {
        ExceptionDateTime = exceptionDateTime;
    }

    public String getRequestType() {
        return RequestType;
    }

    public void setRequestType(String requestType) {
        RequestType = requestType;
    }

    public String getMethodName() {
        return MethodName;
    }

    public void setMethodName(String methodName) {
        MethodName = methodName;
    }

    public String getExceptionDetail() {
        return ExceptionDetail;
    }

    public void setExceptionDetail(String exceptionDetail) {
        ExceptionDetail = exceptionDetail;
    }

    public String getStackTrace() {
        return StackTrace;
    }

    public void setStackTrace(String stackTrace) {
        StackTrace = stackTrace;
    }


}
