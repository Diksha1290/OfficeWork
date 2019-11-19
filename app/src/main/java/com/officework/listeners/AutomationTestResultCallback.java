package com.officework.listeners;

public interface AutomationTestResultCallback {
     void onTestCompleted(boolean serviceStatus, String response, int callbackID);
}
