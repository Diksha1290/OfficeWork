package com.officework.serverConnection;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.officework.R;
import com.officework.constants.JsonTags;
import com.officework.interfaces.WebServiceCallback;
import com.officework.models.ChannelLoginModel;
import com.officework.models.WebServiceRequestModel;
import com.officework.utils.ExceptionLogUtils;
import com.officework.utils.Utilities;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.util.concurrent.TimeUnit;

/**
 * Created by Ashwani on 11/20/2017.
 */

public class WebServiceAsyncTaskAbacusAPI extends AsyncTask<Void, Void, Void> {

    Context ctx;
    WebServiceCallback listener;
    ProgressDialog bar;
    WebServiceRequestModel requestModel;
    OkHttpClient client;
    RequestBody body;
    Response response;
    Request request;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    String mstrResponse = "";
    int callbackID = 0;
    boolean status = false;
    boolean isTokenRequest = false;
    Utilities utils;
    ChannelLoginModel channelLoginModel;

    public WebServiceAsyncTaskAbacusAPI(Context ctx, WebServiceCallback listener, WebServiceRequestModel requestModel, int callbackID, boolean isToken_Request
            , ChannelLoginModel channelLoginModel) {
        this.ctx = ctx;
        this.listener = listener;
        this.requestModel = requestModel;
        this.callbackID = callbackID;
        this.channelLoginModel = channelLoginModel;
        isTokenRequest = isToken_Request;
        utils = Utilities.getInstance(ctx);
    }

    /**
     * This method is called before final execution is started
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        bar = new ProgressDialog(ctx);
        bar.setTitle(requestModel.getDialogTitle());
        bar.setCancelable(false);
        bar.setMessage(TextUtils.isEmpty(requestModel.getDialogMessage()) ? ctx.getResources().getString(R.string.loaderMsg) : requestModel.getDialogMessage());
        if (requestModel.isDialogEnabled())
            bar.show();
    }

    /**
     * This method perform actual execution
     *
     * @param params
     * @return
     */
    @Override
    protected Void doInBackground(Void... params) {
        if (Utilities.getInstance(ctx).isInternetWorking(ctx)) {
            try {
                client = new OkHttpClient();
                client.setConnectTimeout(30000, TimeUnit.SECONDS); // connect timeout
                client.setReadTimeout(30000, TimeUnit.SECONDS);
                String authDetails = getAuthDetails();
                String tokenType = getTokenType();
                if (isTokenRequest) {
                    if (requestModel.isPost()) {
                        body = RequestBody.create(JSON, "grant_type=password&username=BelmontApiUser&password=P@$$w0RT!2017");
                        request = new Request.Builder().url(requestModel.getUrl()).post(body).build();
                    }
                } else {
                    if (requestModel.isPost()) {
                        body = RequestBody.create(JSON, requestModel.getJsonData());
                        request = new Request.Builder().url(requestModel.getUrl()).addHeader("authorization", tokenType + " " + authDetails.trim()).post(body).build();
                    } else
                        request = new Request.Builder().url(requestModel.getUrl() + requestModel.getJsonData()).addHeader("authorization", tokenType + " " + authDetails.trim())
                                .addHeader(JsonTags.PartnerCode.name(), channelLoginModel.getPartnerCode()).addHeader(JsonTags.StoreID.name(),
                                        channelLoginModel.getStoreCode()).build();
                }
                Log.d("REQUEST : " + requestModel.getUrl() + "---", requestModel.getJsonData());
                response = client.newCall(request).execute();
                mstrResponse = response.body().string();
                Log.d("RESPONSE : ---", mstrResponse);
                status = true;
            } catch (Exception e) {
                status = false;
                mstrResponse = ctx.getResources().getString(R.string.ServerError);
                e.printStackTrace();
                logException(e, "WebServiceAsynctask_doInBackground()");
            }
        } else {
            status = false;
            mstrResponse = ctx.getResources().getString(R.string.InternetError);

        }
        return null;
    }

    private String getAuthDetails() {
        return utils.getPreference(ctx, JsonTags.access_token_abacus_api.name(), "");
    }

    private String getTokenType() {
        return utils.getPreference(ctx, JsonTags.token_type_abacus_api.name(), "");
    }

    /**
     * This method post response to callback method
     *
     * @param aVoid
     */
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (bar.isShowing())
            bar.dismiss();
        listener.onServiceResponse(status, mstrResponse, callbackID);
    }

    public void logException(Exception e, String methodName) {
        try {

            ExceptionLogUtils exceptionLogUtils = new ExceptionLogUtils(utils, ctx, null);
            exceptionLogUtils.addExceptionLog(utils, ctx, null, null, null, e, methodName);
        } catch (Exception exp) {
           // logException(exp, "WebServiceAsynctask_logException()");
        }

    }
}
