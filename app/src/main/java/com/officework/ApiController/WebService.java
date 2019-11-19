package com.officework.ApiController;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.officework.constants.JsonTags;
import com.officework.models.CheckSuccess;
import com.officework.models.SocketDataSync;
import com.officework.utils.Utilities;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;

import okhttp3.RequestBody;
import okio.Buffer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WebService<T> {


    public void apiCall(Call<T> offerPrice, Context ctx,WebServiceInterface webServiceInterface){
        offerPrice.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                try {
                    Gson gson = new GsonBuilder().create();
                    String json = gson.toJson(response.body());
                    CheckSuccess checkSuccess = new CheckSuccess();

                    checkSuccess = gson.fromJson(json, CheckSuccess.class);
                    /**  to check if token api   **/
                    if (checkSuccess.getAccess_token().isEmpty()) {
                        if (checkSuccess.isSuccess()) {
                            webServiceInterface.apiResponse(response);

                        } else {
                            webServiceInterface.apiError(response);

                            logApiException(ctx, call.request().url().url(), bodyToString(call.request().body()).replaceAll("\"", ""), json);
                        }
                    } else {
                        webServiceInterface.apiResponse(response);

                    }
                }catch (Exception e){
                    e.printStackTrace();

                }


            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {


                try {
                    webServiceInterface.serverError(t);
                    if (t instanceof UnknownHostException) {
                    } else {

                        logApiException(ctx, call.request().url().url(), bodyToString(call.request().body()).replaceAll("\"", ""), "Server Error");

                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }




    public void logApiException(Context ctx, URL url, String request, String response) {
        try {
            APIInterface apiInterface = APIClient.getClient(ctx).create(APIInterface.class);
            Call<SocketDataSync> log = apiInterface.logApiException(saveLogExceptionMap(ctx, url, request, response));
            log.enqueue(new Callback<SocketDataSync>() {
                @Override
                public void onResponse(Call<SocketDataSync> call, Response<SocketDataSync> response) {
                    try {
                        if(response.body()!=null) {
                            if (response.body().isSuccess()) {
                                Log.i("exception logged", "true");
                            }
                        }

                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<SocketDataSync> call, Throwable t) {
                    Log.i("exception logged", "false");

                }
            });
        }catch (Exception e)
        {
            e.printStackTrace();
        }



    }




    private HashMap<String, Object> saveLogExceptionMap(Context context, URL url, String request, String response) {
        HashMap<String, Object> map = new HashMap<>();
        try {
            map.put("SubscriberProductID", Utilities.getInstance(context).getPreference(context, JsonTags.SubscriberProductID.name(), ""));
            map.put("UDI",Utilities.getInstance(context).getPreference(context, JsonTags.UDI.name(), ""));
            map.put("IMEI",Utilities.getInstance(context).getSecurePreference(context, JsonTags.MMR_1.name(), ""));
            map.put("ApplicationName","Office Work");
            map.put("DeviceMake","Android");
            map.put("DeviceModel",Utilities.getInstance(context).getSecurePreference(context, "MMR_5", Build.MODEL));
            map.put("DeviceCapacity",Utilities.getInstance(context).getSecurePreference(context, "MMR_6", ""));
            map.put("Request",request);
            map.put("Response",response);
            map.put("Url",url.toString());


        } catch (Exception E) {
            E.printStackTrace();
        }
        return map;
    }

    private String bodyToString(final RequestBody request) {
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if (copy != null)
                copy.writeTo(buffer);
            else
                return "";
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }

}
