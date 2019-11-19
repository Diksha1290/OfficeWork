package com.officework.models;

/**
 * Created by Ashwani on 11/20/2017.
 */

public class ChannelLoginModel {
    public String getPartnerCode() {
        return partnerCode;
    }

    public void setPartnerCode(String partnerCode) {
        this.partnerCode = partnerCode;
    }

    public String getStoreCode() {
        return storeCode;
    }

    public void setStoreCode(String storeCode) {
        this.storeCode = storeCode;
    }

    String partnerCode;
    String storeCode;
}
