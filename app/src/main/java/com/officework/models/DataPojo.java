package com.officework.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Diksha on 11/21/2018.
 */

public class DataPojo implements Parcelable {


    private boolean IsOrderCompleted;
    private OrderDetailPojo OrderDetail;
    private String DevicePrice;
    private String StoreType;
    private String DeductionAmount;
    private String OfferPrice;
    private String DeductionPercentage;
    private String OrderID;
    private String OrderDetailID;
    private String DeductionGroup;
    private String CacheManagerID;
    private String LocationID;
    private String CurrencySymbol;
    private String TrackingID;
    private DevicePriceInfo DevicePriceInfo;
    private boolean Status;
    private String Message;
    private String Name;
    private String LastName;
    private String ContactNumber;
    private boolean IsValidUser;
    private int CustomerID;
    private String EmailSent;
    private CustomerDetailPojo CustomerDetail;
    private List<DocumentsTypePojo> DocumentsTypeList;
    private List<PaymentTypePojo> PaymentTypeList;


    public List<PaymentTypePojo> getPaymentTypeList() {
        return PaymentTypeList;
    }

    public void setPaymentTypeList(List<PaymentTypePojo> paymentTypeList) {
        PaymentTypeList = paymentTypeList;
    }

    public CustomerDetailPojo getCustomerDetail() {
        return CustomerDetail;
    }

    public void setCustomerDetail(CustomerDetailPojo customerDetail) {
        CustomerDetail = customerDetail;
    }

    public String getTradeInValidityDays() {
        return TradeInValidityDays;
    }

    public void setTradeInValidityDays(String tradeInValidityDays) {
        TradeInValidityDays = tradeInValidityDays;
    }

    public String getTrackingID() {
        return TrackingID;
    }

    public void setTrackingID(String trackingID) {
        TrackingID = trackingID;
    }

    public String getQuoteExpireDays() {
        return QuoteExpireDays;
    }

    public void setQuoteExpireDays(String quoteExpireDays) {
        QuoteExpireDays = quoteExpireDays;
    }

    String TradeInValidityDays;
    String QuoteExpireDays;
    boolean IsDeviceTradable;
    String UDI;
    private String CreatedDate;

    public String getUDI() {
        return UDI;
    }

    public void setUDI(String UDI) {
        this.UDI = UDI;
    }

    private String Address1;

    public boolean isDeviceTradable() {
        return IsDeviceTradable;
    }

    public void setDeviceTradable(boolean deviceTradable) {
        IsDeviceTradable = deviceTradable;
    }

    private String Address2;
    private String Address3;
    private boolean IsSuccess;
    protected DataPojo(Parcel in) {
        StoreType= in.readString();
        DevicePrice = in.readString();
        DeductionAmount = in.readString();
        OfferPrice = in.readString();
        DeductionPercentage = in.readString();
        TrackingID= in.readString();
        OrderID = in.readString();
        OrderDetailID = in.readString();
        DeductionGroup = in.readString();
        CacheManagerID = in.readString();
        LocationID = in.readString();
        TradeInValidityDays= in.readString();
        QuoteExpireDays = in.readString();
        UDI= in.readString();
        CurrencySymbol = in.readString();
        CreatedDate = in.readString();
        Address1 = in.readString();
        Address2 = in.readString();
        Address3 = in.readString();
        City = in.readString();
        PostCode = in.readString();
        UDIID = in.readString();
        QuotedPrice = in.readString();
        PercentageDeduction = in.readString();
        SKUID = in.readString();
        McheckInformationID = in.readString();
        IMEI = in.readString();
        SerialNumber = in.readString();
        FailTestId = in.readString();
        PassTestId = in.readString();
        UserID = in.readString();
        CatalogPartnerPricingID = in.readString();
        StoreID = in.readString();
        EnterprisePartnerID = in.readString();
        EmailSent=in.readString();
        Declarations = in.createTypedArrayList(DeclarationTest.CREATOR);
    }


    public String getStoreType() {
        return StoreType;
    }

    public void setStoreType(String storeType) {
        StoreType = storeType;
    }

    public boolean isSuccess() {
        return IsSuccess;
    }

    public void setSuccess(boolean success) {
        IsSuccess = success;
    }

    public String getEmailSent() {
        return EmailSent;
    }

    public void setEmailSent(String emailSent) {
        EmailSent = emailSent;
    }

    public static final Creator<DataPojo> CREATOR = new Creator<DataPojo>() {
        @Override
        public DataPojo createFromParcel(Parcel in) {
            return new DataPojo(in);
        }

        @Override
        public DataPojo[] newArray(int size) {
            return new DataPojo[size];
        }
    };

    public String getCreatedDate() {
        return CreatedDate;
    }

    public void setCreatedDate(String createdDate) {
        CreatedDate = createdDate;
    }

    public String getAddress1() {
        return Address1;
    }

    public void setAddress1(String address1) {
        Address1 = address1;
    }

    public String getAddress2() {
        return Address2;
    }

    public void setAddress2(String address2) {
        Address2 = address2;
    }

    public String getAddress3() {
        return Address3;
    }

    public void setAddress3(String address3) {
        Address3 = address3;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getPostCode() {
        return PostCode;
    }

    public void setPostCode(String postCode) {
        PostCode = postCode;
    }

    private String City;
    private String PostCode;


    public DevicePriceInfo getDevicePriceInfo() {
        return DevicePriceInfo;
    }

    public void setDevicePriceInfo(DevicePriceInfo devicePriceInfo) {
        this.DevicePriceInfo = devicePriceInfo;
    }



    public String getCurrencySymbol() {
        return CurrencySymbol;
    }

    public void setCurrencySymbol(String currencySymbol) {
        CurrencySymbol = currencySymbol;
    }

/*public static Creator<DataPojo> getCREATOR() {
return CREATOR;
}*/

    public String getOrderDetailID() {
        return OrderDetailID;
    }

    public void setOrderDetailID(String orderDetailID) {
        OrderDetailID = orderDetailID;
    }

    public String getDeductionGroup() {
        return DeductionGroup;
    }

    public void setDeductionGroup(String deductionGroup) {
        DeductionGroup = deductionGroup;
    }




    private String UDIID;
    private ArrayList<TestObject> TestResult;

    public ArrayList<TestObject> getTestResult() {
        return TestResult;
    }

    public void setTestResult(ArrayList<TestObject> testResult) {
        TestResult = testResult;
    }

    public String getOrderID() {
        return OrderID;
    }

    public void setOrderID(String orderID) {
        OrderID = orderID;
    }

    public String getDevicePrice() {
        return DevicePrice;
    }

    public void setDevicePrice(String devicePrice) {
        DevicePrice = devicePrice;
    }

    public String getDeductionAmount() {
        return DeductionAmount;
    }



    public void setDeductionAmount(String deductionAmount) {
        DeductionAmount = deductionAmount;
    }

    public String getOfferPrice() {
        return OfferPrice;
    }

    public void setOfferPrice(String offerPrice) {
        OfferPrice = offerPrice;
    }

    public String getDeductionPercentage() {
        return DeductionPercentage;
    }

    public void setDeductionPercentage(String deductionPercentage) {
        DeductionPercentage = deductionPercentage;
    }

    public String getUDIID() {
        return UDIID;
    }

    public void setUDIID(String UDIID) {
        this.UDIID = UDIID;
    }

    public String getCacheManagerID() {
        return CacheManagerID;
    }

    public void setCacheManagerID(String cacheManagerID) {
        CacheManagerID = cacheManagerID;
    }

    public List<DocumentsTypePojo> getDocumentsTypeList() {
        return DocumentsTypeList;
    }

    public void setDocumentsTypeList(List<DocumentsTypePojo> documentsTypeList) {
        DocumentsTypeList = documentsTypeList;
    }

//OFERR PRICE API RESPONSE

    private String QuotedPrice;
    private String PercentageDeduction;
    private String SKUID;
    private String McheckInformationID;
    private String IMEI;
    private String SerialNumber;
    private String FailTestId;
    private String PassTestId;
    private String UserID;
    private String CatalogPartnerPricingID;
    private String StoreID;
    private String EnterprisePartnerID;


    private ArrayList<DeclarationTest>
            Declarations;

    public String getQuotedPrice() {
        return QuotedPrice;
    }

    public void setQuotedPrice(String quotedPrice) {
        QuotedPrice = quotedPrice;
    }

    public String getPercentageDeduction() {
        return PercentageDeduction;
    }

    public void setPercentageDeduction(String percentageDeduction) {
        PercentageDeduction = percentageDeduction;
    }

    public String getSKUID() {
        return SKUID;
    }

    public void setSKUID(String SKUID) {
        this.SKUID = SKUID;
    }

    public String getLocationID() {
        return LocationID;
    }

    public void setLocationID(String locationID) {
        LocationID = locationID;
    }

    public String getMcheckInformationID() {
        return McheckInformationID;
    }

    public void setMcheckInformationID(String mcheckInformationID) {
        McheckInformationID = mcheckInformationID;
    }

    public String getIMEI() {
        return IMEI;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }

    public String getSerialNumber() {
        return SerialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        SerialNumber = serialNumber;
    }

    public String getFailTestId() {
        return FailTestId;
    }

    public void setFailTestId(String failTestId) {
        FailTestId = failTestId;
    }

    public String getPassTestId() {
        return PassTestId;
    }

    public void setPassTestId(String passTestId) {
        PassTestId = passTestId;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getCatalogPartnerPricingID() {
        return CatalogPartnerPricingID;
    }

    public void setCatalogPartnerPricingID(String catalogPartnerPricingID) {
        CatalogPartnerPricingID = catalogPartnerPricingID;
    }

    public String getStoreID() {
        return StoreID;
    }

    public void setStoreID(String storeID) {
        StoreID = storeID;
    }

    public String getEnterprisePartnerID() {
        return EnterprisePartnerID;
    }

    public void setEnterprisePartnerID(String enterprisePartnerID) {
        EnterprisePartnerID = enterprisePartnerID;
    }

    public boolean isStatus() {
        return Status;
    }

    public void setStatus(boolean status) {
        Status = status;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getContactNumber() {
        return ContactNumber;
    }

    public void setContactNumber(String contactNumber) {
        ContactNumber = contactNumber;
    }

    public boolean isValidUser() {
        return IsValidUser;
    }

    public int getCustomerID() {
        return CustomerID;
    }

    public void setCustomerID(int customerID) {
        CustomerID = customerID;
    }

    public void setValidUser(boolean validUser) {
        IsValidUser = validUser;
    }

    public ArrayList<DeclarationTest> getDeclarationTestArrayList() {
        return Declarations;
    }

    public void setDeclarationTestArrayList(ArrayList<DeclarationTest> declarationTestArrayList) {
        this.Declarations = declarationTestArrayList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(DevicePrice);
        dest.writeString(DeductionAmount);
        dest.writeString(OfferPrice);
        dest.writeString(DeductionPercentage);
        dest.writeString(OrderID);
        dest.writeString(OrderDetailID);
        dest.writeString(DeductionGroup);
        dest.writeString(CacheManagerID);
        dest.writeString(LocationID);
        dest.writeString(CurrencySymbol);
        dest.writeString(CreatedDate);
        dest.writeString(Address1);
        dest.writeString(Address2);
        dest.writeString(Address3);
        dest.writeString(City);
        dest.writeString(PostCode);
        dest.writeString(UDIID);
        dest.writeString(QuotedPrice);
        dest.writeString(PercentageDeduction);
        dest.writeString(SKUID);
        dest.writeString(McheckInformationID);
        dest.writeString(IMEI);
        dest.writeString(SerialNumber);
        dest.writeString(FailTestId);
        dest.writeString(PassTestId);
        dest.writeString(UserID);
        dest.writeString(CatalogPartnerPricingID);
        dest.writeString(StoreID);
        dest.writeString(EnterprisePartnerID);
        dest.writeTypedList(Declarations);
    }

    public boolean isOrderCompleted() {
        return IsOrderCompleted;
    }

    public void setOrderCompleted(boolean orderCompleted) {
        IsOrderCompleted = orderCompleted;
    }

    public OrderDetailPojo getOrderDetail() {
        return OrderDetail;
    }

    public void setOrderDetail(OrderDetailPojo orderDetail) {
        OrderDetail = orderDetail;
    }
}