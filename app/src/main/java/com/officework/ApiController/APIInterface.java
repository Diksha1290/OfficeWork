package com.officework.ApiController;


import com.officework.models.DeclarationTest;
import com.officework.models.DiagonsticSyncModel;
import com.officework.models.GetTokenModel;
import com.officework.models.ScannedDevicesRequestModel;
import com.officework.models.SocketDataSync;
import com.officework.models.StoreVerifyModel;
import com.officework.models.TokenResponse;

import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;

/**
 * Created by Diksha on 10/5/2018.
 */

public interface APIInterface {
    @POST("/api/save-diagnosticResult")
    Call<SocketDataSync> createUser(@Body HashMap<String, Object> map);

    @POST("/api/offer-price")
    Call<SocketDataSync> getOfferPrice(@Body HashMap<String, Object> map);

// @Multipart
// @POST("/api/add-customer-info")
// Call<SocketDataSync> submitInfo(@Part HashMap<String, Object> map, @Part MultipartBody.Part file, @Part("file") RequestBody name);




// @POST("/api/add-customer-info")
// Call<SocketDataSync> submitInfo(@Body HashMap<String, Object> map,@Body byte bytes);


    @Multipart
    @POST("/api/add-customer-info")
    Call<SocketDataSync> submitInfo(@Part("UniqueID") String uniqueId, @Part("QrCodeID") String QrCodeID, @Part("OrderID") String OrderID,
                                    @Part("UDIID") String UDIID,
                                    @Part("FirstName") String FirstName,
                                    @Part("MiddleName") String MiddleName,
                                    @Part("LastName") String LastName,
                                    @Part("CustomerID") int CustomerID,
                                    @Part("EmailAddress") String EmailAddress,
                                    @Part("ContactNumber") String ContactNumber,
                                    @Part("Address1") String Address1,
                                    @Part("PercentageDeduction") String PercentageDeduction,
                                    @Part("Address2") String Address2,
                                    @Part("Address3") String Address3,
                                    @Part("PostCode") String PostCode,
                                    @Part("City") String City,
// @Part("IdentityValue") String IdentityNumber,
                                    @Part("IdentityType") int IdentityType,
                                    @Part("McheckInformationID") String McheckInformationID,
                                    @Part("QuotedPrice") String QuotedPrice,
                                    @Part("SerialNumber") String SerialNumber,
                                    @Part("IMEI") String IMEI,
                                    @Part("FailTestId") String FailTestId,
                                    @Part("PassTestId") String PassTestId,
                                    @Part("UserID") String UserID,
                                    @Part("StoreID")String StoreID,
                                    @Part("EnterprisePartnerID") String EnterprisePartnerID,
                                    @Part("SKUID") String SKUID,
                                    @Part("CatalogPartnerPricingID") String CatalogPartnerPricingID,
                                    @Part("LocationID") String LocationID,
                                    @Part MultipartBody.Part[] file,
                                    @Part("Make")String Make,
                                    @Part("Model")String Model,
                                    @Part("Capacity")String Capcity,
                                    @Part("ActualPrice")String ActualPrice,
                                    @Part("TradeInValidityDays")String TradeInValidityDays,
                                    @Part("QuoteExpireDays")String QuoteExpireDays,
                                    @PartMap() HashMap<String,ArrayList<DeclarationTest>> map,
                                    @Part("StoreType")String StoreType,
                                    @Part("PaymentConfigrationId")int PaymentConfigrationId,
                                    @Part("ReferenceCode")String ReferenceCode,
                                    @Part("CustomNote")String CustomNote,
                                    @Part("CustomerAccountNumber")String CustomerAccountNumber,
                                    @Part("CustomerSortCode")String CustomerSortCode,
                                    @Part("CustomerBankName")String CustomerBankName,
                                    @Part("CurrencySymbol")String CurrencySymbol
                                    ,@Part("StateCode")String StateCode
                                    ,@Part("Pay_EmailAddress")String Pay_EmailAddress,
                                    @Part("Pay_Name")String Pay_Name,
                                    @Part("Pay_Address")String Pay_Address,
                                    @Part("Pay_MobileNumber")String Pay_MobileNumber





    );


// @Part("Declarations") ArrayList<DeclarationTest> arrayList


    @POST("/api/check-tradebility")
    Call<SocketDataSync> checkTradability(@Body HashMap<String, Object> map);




    @GET("/api/get-data-by-imei/{imei}")
    Call<SocketDataSync> getOrderdetail(@Path("imei") String value);

    @POST("/api/save-deviceInformation")
    Call<SocketDataSync> saveDaigonsticInfo(@Body HashMap<String, Object> map);

    @POST("/gateway/GetPriceForTradeInPro")
    Call<SocketDataSync> saveTestDaigonsticInfo(@Body HashMap<String, Object> map);





    @GET("api/generate-udid/{subscriber_id}")
    Call<SocketDataSync> getUDI(@Path(value = "subscriber_id", encoded = true) String subscriber_id);


    @POST("/api/log-exception")
    Call<SocketDataSync> logApiException(@Body HashMap<String, Object> map);

    @POST("/api/get-scanned-devices-data-by-udi/")
    Call<SocketDataSync> getOrderdetailBydeviceId(@Body ScannedDevicesRequestModel scannedDevicesRequestModel);

    @POST("/api/complete-order-mail")
    Call<SocketDataSync> completeOrder(@Body HashMap<String,Object> map);

    @POST("/api/verify-store")
    Call<SocketDataSync> verifyStore(@Body StoreVerifyModel storeVerifyModel);

    @POST("token")
    Call<TokenResponse> getToken(@Body GetTokenModel getTokenModel);


    @GET("api/get-diagnostic-tests/{SubscriberProductID}")
    Call<DiagonsticSyncModel> getDiagnosticTest(@Path("SubscriberProductID") String SubscriberProductID);

    @FormUrlEncoded
    @POST("api/check-email-registered")
    Call<SocketDataSync> checkEmail(@FieldMap HashMap<String, Object> map);

    @POST("api/client-login")
    Call<SocketDataSync> clientLogin(@Body HashMap<String, Object> map);

    @POST("api/get-secret-code")
    Call<SocketDataSync> getSecretCode(@Body HashMap<String, Object> map);

    @POST("api/check-secret-code")
    Call<SocketDataSync> checkSecretCode(@Body HashMap<String, Object> map);
//https://deviceapi.cellde.com/api/get-data-by-uniqueid/49251fb4-840d-46b9-8717-4e1bd6f7a2ed


    @POST("api/resend-password")
    Call<SocketDataSync> resendPassword(@Body HashMap<String, Object> map);

    @POST("/api/add-to-tradeable")
    Call<SocketDataSync> addToTradeble(@Body HashMap<String, String> map);

    @GET("api/get-documents-type/{EnterprisePartnerID}")
    Call<SocketDataSync> getDocumentsType(@Path("EnterprisePartnerID") String EnterprisePartnerID);

    @POST("api/get-payment-integration-type")
    Call<SocketDataSync> getPaymentModes(@Body HashMap<String, Object> map);

    @POST("api/validate-Address")
    Call<SocketDataSync> validateAddress(@Body HashMap<String, Object> map);
}