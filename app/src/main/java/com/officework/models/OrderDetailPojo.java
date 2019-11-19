package com.officework.models;

public class OrderDetailPojo {

    private int RemainingDays;
    private String ErrorMessage;
    private String OrderDetailID;
    private String EmailAddress;
    private String CreatedDate;
    private String IMEI;
    private String QuotedPrice;
private  String CurrencySymbol;

    public String getCurrencySymbol() {
        return CurrencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        CurrencySymbol = currencySymbol;
    }

    public int getRemainingDays() {
        return RemainingDays;
    }

    public void setRemainingDays(int remainingDays) {
        RemainingDays = remainingDays;
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        ErrorMessage = errorMessage;
    }

    public String getOrderDetailID() {
        return OrderDetailID;
    }

    public void setOrderDetailID(String orderDetailID) {
        OrderDetailID = orderDetailID;
    }

    public String getEmailAddress() {
        return EmailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        EmailAddress = emailAddress;
    }

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }

    public String getQuotedPrice() {
        return QuotedPrice;
    }

    public void setQuotedPrice(String quotedPrice) {
        QuotedPrice = quotedPrice;
    }
}
