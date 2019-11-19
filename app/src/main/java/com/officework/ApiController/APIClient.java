package com.officework.ApiController;

/**
 * Created by Diksha on 10/5/2018.
 */



import android.content.Context;
import android.os.Build;

import com.officework.constants.Constants;
import com.officework.constants.JsonTags;
import com.officework.utils.Utilities;
import com.officework.utils.WebserviceUrls;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class APIClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient(final Context context) {


        OkHttpClient.Builder httpClient = new OkHttpClient.Builder().connectTimeout(1000, TimeUnit.MINUTES).readTimeout(1000,TimeUnit.MINUTES).writeTimeout(1000,TimeUnit.MINUTES);
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient.addInterceptor(interceptor);
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request original = chain.request();

                // Request customization: add request headers
                Request.Builder requestBuilder = original.newBuilder()
                        .header("Authorization",  Utilities.getInstance(context).getPreference(context,  JsonTags.token_type.name(), "")+Utilities.getInstance(context).getPreference(context,  JsonTags.access_token.name(), ""))
                        .header("DeviceType","android")
                        .header("DeviceName",   Utilities.getInstance(context).getSecurePreference(context, "MMR_5", Build.MODEL))
                        .header("TrackingID",Utilities.getInstance(context).getSecurePreference(context, Constants.TrackingID,""))
                        .header("SubscriberProductID", Utilities.getInstance(context).getPreference(context, JsonTags.SubscriberProductID.name(), "")); // <-- this is the important line

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        OkHttpClient client = httpClient.build();
//                        .baseUrl("http://10.129.10.191:3000/")
//// .baseUrl("https://deviceapi.cellde.com/")

//                .baseUrl("http://mmrapi.product.veridic.local/")
        //.baseUrl("https://tradeinpro-api.cellde.com/")
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(WebserviceUrls.BaseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();



        return retrofit;
    }

}