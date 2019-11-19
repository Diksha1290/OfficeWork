package com.officework.models;

import com.google.gson.annotations.SerializedName;

public class TokenResponse {
    @SerializedName("access_token")
    String accessToken="";

    @SerializedName("token_type")
    String token_type="";


    @SerializedName("trackingid")
    String trackingid="";


    @SerializedName("expires_in")
    String expires_in="";

    @SerializedName("Subscriber")
    String Subscriber="";

    @SerializedName("SubscriberCode")
    String SubscriberCode="";

    public String getAccessToken() {
        return accessToken;
    }


    public String getTrackingid() {
        return trackingid;
    }

    public void setTrackingid(String trackingid) {
        this.trackingid = trackingid;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public String getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(String expires_in) {
        this.expires_in = expires_in;
    }

    public String getSubscriber() {
        return Subscriber;
    }

    public void setSubscriber(String subscriber) {
        Subscriber = subscriber;
    }

    public String getSubscriberCode() {
        return SubscriberCode;
    }

    public void setSubscriberCode(String subscriberCode) {
        SubscriberCode = subscriberCode;
    }

    public String getSubscriberProductCode() {
        return SubscriberProductCode;
    }

    public void setSubscriberProductCode(String subscriberProductCode) {
        SubscriberProductCode = subscriberProductCode;
    }

    public String getSubscriberProductID() {
        return SubscriberProductID;
    }

    public void setSubscriberProductID(String subscriberProductID) {
        SubscriberProductID = subscriberProductID;
    }

    @SerializedName("SubscriberProductCode")
    String SubscriberProductCode="";

    @SerializedName("SubscriberProductID")
    String SubscriberProductID="";


}
