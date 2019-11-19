package com.officework.models;

public class DevicePriceInfo {

    private String SKUID;
    private String DeviceModelID;
    private String CatalogPartnerPricingID;
    private String DeviceModelFullName;
    private String DeviceFullPrice;
    private String StartDate;
    private String EndDate;

    public String getSKUID() {
        return SKUID;
    }

    public void setSKUID(String SKUID) {
        this.SKUID = SKUID;
    }

    public String getDeviceModelID() {
        return DeviceModelID;
    }

    public void setDeviceModelID(String deviceModelID) {
        DeviceModelID = deviceModelID;
    }

    public String getCatalogPartnerPricingID() {
        return CatalogPartnerPricingID;
    }

    public void setCatalogPartnerPricingID(String catalogPartnerPricingID) {
        CatalogPartnerPricingID = catalogPartnerPricingID;
    }

    public String getDeviceModelFullName() {
        return DeviceModelFullName;
    }

    public void setDeviceModelFullName(String deviceModelFullName) {
        DeviceModelFullName = deviceModelFullName;
    }

    public String getDeviceFullPrice() {
        return DeviceFullPrice;
    }

    public void setDeviceFullPrice(String deviceFullPrice) {
        DeviceFullPrice = deviceFullPrice;
    }

    public String getStartDate() {
        return StartDate;
    }

    public void setStartDate(String startDate) {
        StartDate = startDate;
    }

    public String getEndDate() {
        return EndDate;
    }

    public void setEndDate(String endDate) {
        EndDate = endDate;
    }
}

