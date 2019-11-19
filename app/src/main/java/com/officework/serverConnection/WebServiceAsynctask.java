package com.officework.serverConnection;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.officework.R;
import com.officework.constants.JsonTags;
import com.officework.interfaces.WebServiceCallback;
import com.officework.models.WebServiceRequestModel;
import com.officework.utils.ExceptionLogUtils;
import com.officework.utils.Utilities;
import com.officework.utils.WebserviceUrls;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

/**
 * Created by girish on 8/8/2016.
 */
public class WebServiceAsynctask extends AsyncTask<Void, Void, Void> {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final MediaType JSON2 = MediaType.parse("application/x-www-form-urlencoded; " +
            "charset=utf-8");
    Context ctx;
    WebServiceCallback listener;
    ProgressDialog bar;
    WebServiceRequestModel requestModel;
    OkHttpClient client;
    RequestBody body;
    Response response;
    Request request;
    String mstrResponse = "";
    int callbackID = 0;
    boolean status = false;
    boolean isTokenRequest = false;
    Utilities utils;
    boolean progressshow = false;

    public WebServiceAsynctask(Context ctx, WebServiceCallback listener,
                               WebServiceRequestModel requestModel, int callbackID,
                               boolean isToken_Request) {
        this.ctx = ctx;
        this.listener = listener;
        this.requestModel = requestModel;
        this.callbackID = callbackID;
        isTokenRequest = isToken_Request;
        utils = Utilities.getInstance(ctx);

    }

    /**
     * This method is called before final execution is started
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        try {
            bar = new ProgressDialog(ctx);
            bar.setTitle(requestModel.getDialogTitle());
            bar.setCancelable(false);
            bar.setMessage(TextUtils.isEmpty(requestModel.getDialogMessage()) ?
                    ctx.getResources().getString(R.string.loaderMsg) : requestModel.getDialogMessage());
            if (isTokenRequest) {
                if (requestModel.isDialogEnabled()) {
                    bar.show();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
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
                status = false;
                if (isTokenRequest) {
                    OkHttpClient client = new OkHttpClient();
                    client.setConnectTimeout(30000, TimeUnit.SECONDS); // connect timeout
                    client.setReadTimeout(30000, TimeUnit.SECONDS);
                    MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
                    RequestBody body = RequestBody.create(mediaType, requestModel.getJsonData());
                    Request request = new Request.Builder()
                            .url(WebserviceUrls.BaseUrl + WebserviceUrls.newToken)
                            .post(body)
                            .addHeader("content-type", "application/x-www-form-urlencoded")
                            .addHeader("cache-control", "no-cache")

                            .build();
                    Log.d("REQUEST : " + requestModel.getUrl() + "---", requestModel.getJsonData());
                    Response response = client.newCall(request).execute();
                    mstrResponse = response.body().string();

                    status = true;
                    progressshow = true;
                } else {
                    client = new OkHttpClient();
                    client.setConnectTimeout(30000, TimeUnit.SECONDS); // connect timeout
                    client.setReadTimeout(30000, TimeUnit.SECONDS);
                    String authDetails = getAuthDetails();
                    String tokenType = getTokenType();
                    if (requestModel.isPost()) {
                        body = RequestBody.create(JSON, requestModel.getJsonData());
                        request = new Request.Builder().url(requestModel.getUrl()).addHeader(
                                "SubscriberProductID", utils.getPreference(ctx,
                                        JsonTags.SubscriberProductID.name(), "")).addHeader(
                                "authorization",
                                tokenType + " " + authDetails.trim()).post(body).build();
                    } else
                        request =
                                new Request.Builder().url(requestModel.getUrl() + requestModel.getJsonData()).addHeader("SubscriberProductID", utils.getPreference(ctx, JsonTags.SubscriberProductID.name(), "")).addHeader("authorization", tokenType + " " + authDetails.trim()).build();

                    Log.d("REQUEST : " + requestModel.getUrl() + "---", requestModel.getJsonData());
                    response = client.newCall(request).execute();
                    mstrResponse = response.body().string();
                    Log.d("RESPONSE : ---", mstrResponse);
                    status = true;
                    JSONObject responseObj = new JSONObject(mstrResponse);
                    if (!responseObj.getBoolean("IsSuccess")) {

                        if (bar.isShowing()) {
                            bar.dismiss();
                        }
                    }
                }
            } catch (Exception e) {
                if (bar.isShowing()) {
                    bar.dismiss();
                }
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
        return utils.getPreference(ctx, JsonTags.access_token.name(), "");
    }

    private String getTokenType() {
        return utils.getPreference(ctx, JsonTags.token_type.name(), "");
    }

    /*private String getAuthDetails() {
        Cursor cr = ctx.getContentResolver().query(TblUserDetails.BASE_CONTENT_URI, null, null,
        null, null);
        if (cr != null) {
            if (cr.getCount() > 0) {
                String EncodedUsename = "";
                String EncodedPswd = "";
                cr.moveToFirst();
                String email = cr.getString(cr.getColumnIndex(TblUserDetails.EMAIL));
                EncodedPswd = cr.getString(cr.getColumnIndex(TblUserDetails.PASSWORD));
                String phNumber = cr.getString(cr.getColumnIndex(TblUserDetails.PH_NO));
                try {
                    if (!TextUtils.isEmpty(email)) {
                        EncodedUsename = new String( Base64.encode(email.getBytes("UTF-8"),
                        Base64.NO_WRAP));
                    } else {
                        EncodedUsename = new String( Base64.encode(phNumber.getBytes("UTF-8"),
                        Base64.NO_WRAP));
                    }
                   // byte[] data = pswd.getBytes("UTF-8");
                   // EncodedPswd = Base64.encodeToString(data, Base64.NO_WRAP);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return new String( EncodedUsename+":"+EncodedPswd).trim();
            } else
                return "";
        }
        return "";
    }*/

    /**
     * This method post response to callback method
     *
     * @param aVoid
     */
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        try {
            if (requestModel.getUrl().equals("https://stagingmcheck-im-api.cellde" +
                    ".com/api/get-diagnostic-tests")) {
                if (bar.isShowing())
                    bar.dismiss();
            }
            listener.onServiceResponse(status, mstrResponse, callbackID);
            status = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void logException(Exception e, String methodName) {
        try {

            ExceptionLogUtils exceptionLogUtils = new ExceptionLogUtils(utils, ctx, null);
            exceptionLogUtils.addExceptionLog(utils, ctx, null, null, null, e, methodName);
        } catch (Exception exp) {
            //  logException(exp, "WebServiceAsynctask_logException()");
        }

    }
}
