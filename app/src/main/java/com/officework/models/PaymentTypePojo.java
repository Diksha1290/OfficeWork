package com.officework.models;

public class PaymentTypePojo {
    int PaymentTypeId;
    String PaymentType;
    int PaymentConfigrationId;

    public int getPaymentTypeId() {
        return PaymentTypeId;
    }

    public void setPaymentTypeId(int paymentTypeId) {
        PaymentTypeId = paymentTypeId;
    }

    public String getPaymentType() {
        return PaymentType;
    }

    public void setPaymentType(String paymentType) {
        PaymentType = paymentType;
    }

    public int getPaymentConfigrationId() {
        return PaymentConfigrationId;
    }

    public void setPaymentConfigrationId(int paymentConfigrationId) {
        PaymentConfigrationId = paymentConfigrationId;
    }
}
