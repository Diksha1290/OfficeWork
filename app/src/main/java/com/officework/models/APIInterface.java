package com.officework.models;


import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Created by Diksha on 10/5/2018.
 */

public interface APIInterface {

    @POST("/api/save-diagnosticResult")
    Call<SocketDataSync> createUser(@Body HashMap<String, Object> map);

    @POST("/api/offer-price")
    Call<SocketDataSync> getOfferPrice(@Body HashMap<String, Object> map);

//    @Multipart
//    @POST("/api/add-customer-info")
//    Call<SocketDataSync> submitInfo(@Part HashMap<String, Object> map, @Part MultipartBody.Part file, @Part("file") RequestBody name);




//    @POST("/api/add-customer-info")
//    Call<SocketDataSync> submitInfo(@Body HashMap<String, Object> map,@Body byte bytes);


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
                                    @Part("IdentityValue") String IdentityNumber, @Part("IdentityType") String IdentityType,
                                    @Part("McheckInformationID") String McheckInformationID,
                                    @Part("QuotedPrice") String QuotedPrice,
                                    @Part("SerialNumber") String SerialNumber,
                                    @Part("IMEI") String IMEI,
                                    @Part("FailTestId") String FailTestId,
                                    @Part("PassTestId") String PassTestId,
                                    @Part("UserID") String UserID,
                                    @Part("StoreID") String StoreID,
                                    @Part("EnterprisePartnerID") String EnterprisePartnerID,
                                    @Part("SKUID") String SKUID,
                                    @Part("CatalogPartnerPricingID") String CatalogPartnerPricingID,
                                    @Part("LocationID") String LocationID
            , @Part MultipartBody.Part file, @Part("image") RequestBody name, @Part("Make") String Make, @Part("Model") String Model, @Part("Capacity") String Capcity, @Part("ActualPrice") String ActualPrice, @Part("DelcarationTest") ArrayList<DeclarationTest> arrayList);










    @GET("/api/get-data-by-imei/{imei}")
    Call<SocketDataSync> getOrderdetail(@Path("imei") String value);

    @POST("/api/save-deviceInformation")
    Call<SocketDataSync> saveDaigonsticInfo(@Body HashMap<String, Object> map);
    
    @POST("/gateway/GetPriceForTradeInPro")
    Call<SocketDataSync> saveTestDaigonsticInfo(@Body HashMap<String, Object> map);

    @POST("/api/get-scanned-devices-data-by-udi/")
    Call<SocketDataSync> getOrderdetailBydeviceId(@Body ScannedDevicesRequestModel scannedDevicesRequestModel);

    @POST("/api/complete-order-mail")
    Call<SocketDataSync> completeOrder(@Body HashMap<String, Object> map);

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
}
