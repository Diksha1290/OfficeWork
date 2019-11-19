package com.officework.models;

public class ScannedDevicesRequestModel {
    private String SubscriberProductCode;
    private String UDI;

    public ScannedDevicesRequestModel(String SubscriberProductCode,String UDI){
        this.SubscriberProductCode=SubscriberProductCode;
        this.UDI=UDI;
    }
}
