package com.officework.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.officework.activities.MainActivity;
import com.officework.constants.Constants;
import com.officework.constants.JsonTags;
import com.officework.fragments.GetQRCodeFragment;
import com.officework.interfaces.InterfaceBatteryCallback;
import com.officework.interfaces.InterfaceTimerCallBack;
import com.officework.interfaces.WebServiceCallback;


import org.json.JSONObject;

/**
 * Created by Ashwani on 3/21/2017.
 */

public class TestResultUpdateToServer implements WebServiceCallback, InterfaceBatteryCallback, InterfaceTimerCallBack {
    WebServiceCallback webServiceCallback = null;
    int SERVICE_REQUEST_ID = -1;
    Activity activity;
    Context context;
    Utilities utilities;
    boolean isDialogEnabled = false;

    /**
     * Initialize variables constructor
     *
     * @param utils
     * @param context
     * @param activity
     */
    public TestResultUpdateToServer(Utilities utils, Context context, Activity activity) {
        this.activity = activity;
        this.utilities = utils;
        this.context = context;
    }


    public void updateTestResult(WebServiceCallback webServiceCallback, boolean isTokenRequest, int callBackId) {
        try {
            if (utilities.isInternetWorking(context)) {
                utilities.addPreferenceBoolean(context, JsonTags.isTestDataChanged.name(), false);
            } else {
            }
            this.webServiceCallback = webServiceCallback;

            if (isTokenRequest) {
                if (webServiceCallback == null) {
                  //  postDiagnosticsData(1);
                } else {
                   // postDiagnosticsData(1);
                }

            } else {
                if (webServiceCallback == null) {
                  //  postDiagnosticsData(1);
                } else {
                  //  postDiagnosticsData(callBackId);
                }

            }
        }catch (Exception e){
            logException(e, "TestResultUpdateToServer_updateTestResult()");
        }

    }


    private void initializeTimer() {
        try {
            Constants.interfaceTimerCallBack = (InterfaceTimerCallBack) this;
            MainActivity mainActivity = new MainActivity();
           // mainActivity.startTimerTask(Constants.interfaceTimerCallBack, utilities, context);
        } catch (Exception e) {
            logException(e, "DashBoardFragment_initializeTimer()");
        }

    }

    /**
     * Get token from server
     */

//    private void getToken(int CallBackId) {
//        try {
//            GetQRCodeFragment.getDeviceInfoData(utilities, activity, context, (InterfaceBatteryCallback) this);
//            GetQRCodeFragment.batteryInfoReceiver = null;
//            if (utilities.isInternetWorking(context)) {
//                SERVICE_REQUEST_ID = 0;
//                if (webServiceCallback == null) {
//                    utilities.serviceCalls(context, WebserviceUrls.DiagnosticDataTokenURL, true, GetQRCodeFragment.createJson(utilities, context).toString(), false, CallBackId,
//                            (WebServiceCallback) this, true);
//                } else {
//                    utilities.serviceCalls(context, WebserviceUrls.DiagnosticDataTokenURL, true, GetQRCodeFragment.createJson(utilities, context).toString(), false, CallBackId,
//                            webServiceCallback, true);
//                }
//
//            } else {
//                utilities.addPreferenceBoolean(context, JsonTags.isTestDataChanged.name(), true);
//            }
//
//        }catch (Exception e){
//            logException(e, "TestResultUpdateToServer_getToken()");
//        }
//
//    }

    /**
     * post result set API to server
     */

    public void postDiagnosticsData(int CallBackId,boolean ifAll,int testId) {
        try {
            if(ifAll) {

                if (utilities.isInternetWorking(context)) {
                    SERVICE_REQUEST_ID = 1;

                    if (webServiceCallback == null) {
//                        utilities.serviceCalls(activity, WebserviceUrls.SaveDiagnosticInfo, true, GetQRCodeFragment.createSaveDataJson(utilities, context).toString(), false, CallBackId,
//                                (WebServiceCallback) this, false);
//                        utilities.serviceCalls(activity, WebserviceUrls.SaveDiagnosticInfo, true, GetQRCodeFragment.createSaveDataJson(utilities, context).toString(), false, CallBackId,
//                                (WebServiceCallback) this, false);

//                    utilities.serviceCalls(activity, WebserviceUrls.DiagnosticDataURL, true, GetQRCodeFragment.createJson(utilities, context).toString(), false, CallBackId,
//                            (WebServiceCallback) this, false);
                    } else {
//                        utilities.serviceCalls(activity, WebserviceUrls.SaveDiagnosticInfo, true, GetQRCodeFragment.createSaveDataJson(utilities, context).toString(), false, CallBackId,
//                                webServiceCallback, false);
                    }
                } else {
                    utilities.addPreferenceBoolean(context, JsonTags.isTestDataChanged.name(), true);
                }

            }
            else{

//                utilities.serviceCalls(activity, WebserviceUrls.SaveDiagnosticData, true, GetQRCodeFragment.creatDiagnocticDataJson(utilities, context).toString(), false, CallBackId,
//                        webServiceCallback, false);

            }

        }catch (Exception e){
            logException(e, "TestResultUpdateToServer_postDiagnosticsData()");
        }


    }

    public void updateTestResultToServer(boolean ifAll,int testId,int callBackId)
    {
        int CallBackId=1;
        try {
            if(ifAll) {

                    SERVICE_REQUEST_ID = 1;
//
//                        utilities.serviceCalls(activity, WebserviceUrls.SaveDiagnosticData, true, GetQRCodeFragment.createSaveDataJson(utilities, context).toString(), false, CallBackId,
//                                ((TestResultUpdateToServer.this)), false);

            }
            else{

//                utilities.serviceCalls(activity, WebserviceUrls.SaveDiagnosticData, true, GetQRCodeFragment.createSingleTestJson(utilities, context,testId).toString(), false, CallBackId,
//                        ((TestResultUpdateToServer.this)), false);

            }

        }catch (Exception e){
            logException(e, "TestResultUpdateToServer_postDiagnosticsData()");
        }
    }

    /**
     * service response callback
     *
     * @param serviceStatus
     * @param response
     * @param callbackID
     */

    @Override
    public void onServiceResponse(boolean serviceStatus, String response, int callbackID) {
        if (serviceStatus) {
            try {
                JSONObject responseObj = new JSONObject(response);
                switch (callbackID) {
                    case 0:
                        utilities.addPreference(context, JsonTags.access_token.name(), responseObj.getString(JsonTags.access_token.name()));
                        utilities.addPreferenceBoolean(context, JsonTags.isTestDataChanged.name(), false);
                        utilities.addPreference(context, JsonTags.token_type.name(), responseObj.getString(JsonTags.token_type.name()));
                        GetQRCodeFragment.unRegisterBatteryReceiver(activity);
                        //updateTestResult(null, false, 0);
                        break;
                    case 1:
                        if (responseObj.getBoolean("IsSuccess") ){
//                            utilities.addPreference(context, JsonTags.udi.name(), responseObj.getString(JsonTags.udi.name()));
                            utilities.addPreferenceBoolean(context, JsonTags.isTestDataChanged.name(), false);
//                            if (!(responseObj.getString(JsonTags.refcode.name()).equalsIgnoreCase(""))) {
//                                utilities.addPreferenceLong(context, JsonTags.ttl.name(), responseObj.getLong(JsonTags.ttl.name()));
//                                utilities.addPreference(context, JsonTags.refcode.name(), responseObj.getString(JsonTags.refcode.name()));
//                                if (!Constants.isTimerRunning) {
//                                    initializeTimer();
                           //    }
                         //   }
                       //     PreferenceConstants.udi = responseObj.getString(JsonTags.udi.name());
                        } else {
                            utilities.addPreferenceBoolean(context, JsonTags.isTestDataChanged.name(), true);
                      //      utilities.addPreference(context, JsonTags.udi.name(), "");
                        }
                        break;

                }
            } catch (Exception e) {
                utilities.addPreferenceBoolean(context, JsonTags.isTestDataChanged.name(), true);
                e.printStackTrace();
                logException(e, "TestResultUpdateToServer_onServiceResponse()");

            }
        } else {
            Answers.getInstance().logCustom(new CustomEvent("Save_diagonstic")
                    .putCustomAttribute("response", response));
            utilities.addPreferenceBoolean(context, JsonTags.isTestDataChanged.name(), true);
        }


    }

    /**
     * battery information callback
     *
     * @param intent
     */

    @Override
    public void onBatteryCallBack(Intent intent) {
        try {
            utilities.compare_UpdatePreferenceString(context, JsonTags.MMR_17.name(),
                    intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0) + "%");
            GetQRCodeFragment.unRegisterBatteryReceiver(activity);
        }catch (Exception e){
            logException(e, "TestResultUpdateToServer_onBatteryCallBack()");
        }

    }

    public void logException(Exception e, String methodName) {
        try {
            ExceptionLogUtils exceptionLogUtils = new ExceptionLogUtils(utilities, context, activity);
            exceptionLogUtils.addExceptionLog(utilities, context, activity, null, null, e, methodName);
        } catch (Exception exp) {
           // logException(exp, "TestResultUpdateToServer_logException()");
        }

    }

    @Override
    public void onTimerCallBack(boolean status, long time) {

    }

    @Override
    public void onTimerFinish(boolean status, long time, boolean isTimerFinished) {

    }
}
