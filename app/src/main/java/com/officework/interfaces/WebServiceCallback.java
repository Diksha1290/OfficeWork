package com.officework.interfaces;

/**
 * Created by prerna.kapoor on 1/19/2016.
 */
public interface WebServiceCallback {

    public void onServiceResponse(boolean serviceStatus, String response, int callbackID);
}
