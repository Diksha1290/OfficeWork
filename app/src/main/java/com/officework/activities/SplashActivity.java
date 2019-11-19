package com.officework.activities;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.officework.ApiController.APIClient;
import com.officework.ApiController.APIInterface;
import com.officework.ApiController.WebService;
import com.officework.ApiController.WebServiceInterface;
import com.officework.R;
import com.officework.Services.TaskRemovedService;
import com.officework.broadcastReceivers.BatteryReceiver;
import com.officework.constants.AppConstantsTheme;
import com.officework.constants.Constants;
import com.officework.constants.JsonTags;
import com.officework.constants.PreferenceConstants;
import com.officework.fragments.GetQRCodeFragment;
import com.officework.interfaces.InterfaceBatteryCallback;
import com.officework.models.GetTokenModel;
import com.officework.models.SocketDataSync;
import com.officework.models.TokenResponse;
import com.officework.testing_profiles.realmDatabase.operationManager.RealmOperations;
import com.officework.utils.ExceptionLogUtils;
import com.officework.utils.Utilities;

import org.json.JSONObject;

import java.net.UnknownHostException;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.fabric.sdk.android.Fabric;
import retrofit2.Response;


public class SplashActivity extends AppCompatActivity implements
        InterfaceBatteryCallback {
    public static final String PREFS_NAME = "AOP_PREFS";
    final public int wifiSettingsRequestCode = 22;
    public String SubscriberProductID;
    Utilities utils;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    Context context;
    BatteryReceiver batteryInfoReceiver = null;
    String SubscriberCode;
    String username;
    String tokenpassword;
    String grant_type;
    Context ctx;
    String code = null;
    String storeid;
    String enterprisePartnerID;
    String currencySymbol;
    String UDI;
    private RealmOperations realmOperations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            super.onCreate(savedInstanceState);

            if (!isTaskRoot()
                    && getIntent().hasCategory(Intent.CATEGORY_LAUNCHER)
                    && getIntent().getAction() != null
                    && getIntent().getAction().equals(Intent.ACTION_MAIN)) {
                finish();
                return;
            }


            ThemeManager themeManager = new ThemeManager(SplashActivity.this);

            int colorValue = getResources().getColor(R.color.colorPrimary);
            utils = Utilities.getInstance(this);

            prefrenceOperations();
            AppConstantsTheme.setColor(colorValue, colorValue, colorValue, colorValue, colorValue);
            utils = Utilities.getInstance(this);
            AppConstantsTheme.setColor(colorValue, colorValue, colorValue, colorValue, colorValue);
            ctx = getApplicationContext();
            realmOperations = new RealmOperations();
            realmOperations.resetTestStatus();
            utils.initializePreferences(SplashActivity.this);
            String android_Id =
                    Utilities.getInstance(SplashActivity.this).getPreference(SplashActivity.this,
                            Constants.ANDROID_ID, "");
            Boolean isSaveApiCall =
                    Utilities.getInstance(SplashActivity.this).getPreferenceBoolean(this,
                            Constants.IS_SAVE_API_CALL, false);
//              utils.clearPreferences(SplashActivity.this);
            Utilities.getInstance(SplashActivity.this).addPreference(SplashActivity.this,
                    Constants.ANDROID_ID, android_Id);
            Utilities.getInstance(SplashActivity.this).addPreferenceBoolean(SplashActivity.this,
                    Constants.IS_SAVE_API_CALL, isSaveApiCall);
            context = this;
            settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE); //1
            editor = settings.edit(); //2
            setContentView(R.layout.activity_splash);
            Fabric.with(this, new Crashlytics());
            if (!Constants.isTimerRunning) {
                utils.addPreference(context, JsonTags.refcode.name(), "");
            }
            GetQRCodeFragment.deviceMarketName(this);

            String UDI = utils.getPreference(getApplicationContext(), JsonTags.udi.name(), "");
            displaySplash();
        } catch (Exception e) {
            logException(e, "SplashActivity_onCreate()");
        }


    }

    public void Exp() throws Exception {
        throw new Exception("xyx");
    }


    public void prefrenceOperations() {

        storeid = Utilities.getInstance(SplashActivity.this).getPreference(SplashActivity.this,
                Constants.STOREID, "");
        enterprisePartnerID =
                Utilities.getInstance(SplashActivity.this).getPreference(SplashActivity.this,
                        Constants.ENTERPRISEPATNERID, "");
        currencySymbol =
                Utilities.getInstance(SplashActivity.this).getPreference(SplashActivity.this,
                        Constants.CURRENCYSYMBOL, "");
        UDI = Utilities.getInstance(SplashActivity.this).getPreference(SplashActivity.this,
                JsonTags.UDI.name(), "");
        utils.clearPreferences(SplashActivity.this);


        Utilities.getInstance(SplashActivity.this).addPreference(SplashActivity.this,
                Constants.QRCODEID, storeid);
        Utilities.getInstance(SplashActivity.this).addPreference(SplashActivity.this,
                Constants.STOREID, storeid);
        Utilities.getInstance(SplashActivity.this).addPreference(SplashActivity.this,
                Constants.ENTERPRISEPATNERID, enterprisePartnerID);
        Utilities.getInstance(SplashActivity.this).addPreference(SplashActivity.this,
                JsonTags.UDI.name(), UDI);
//       Utilities.getInstance(SplashActivity.this).addPreference(SplashActivity.this, Constants
//       .CURRENCYSYMBOL, currencySymbol);


    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(new Intent(context, TaskRemovedService.class));
        if (batteryInfoReceiver == null) {
            batteryInfoReceiver = new BatteryReceiver(this);
            registerReceiver(batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * This is used to show splash screen
     * It sleeps thread for one second
     */

    public void displaySplash() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                changeActivity();
            }
        }, 800);


    }

    private void changeActivity() {
        if (ContextCompat.checkSelfPermission(SplashActivity.this,
                Manifest.permission.READ_PHONE_STATE)
                + ContextCompat.checkSelfPermission(SplashActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                + ContextCompat.checkSelfPermission(SplashActivity.this,
                Manifest.permission.CAMERA)
                + ContextCompat.checkSelfPermission(SplashActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                + ContextCompat.checkSelfPermission(SplashActivity.this,
                Manifest.permission.RECORD_AUDIO)
                + ContextCompat.checkSelfPermission(SplashActivity.this,
                Manifest.permission.READ_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            if (Utilities.isInternetWorking(SplashActivity.this)) {
                staticData();
            } else {
                androidx.appcompat.app.AlertDialog.Builder alert =
                        new androidx.appcompat.app.AlertDialog.Builder(SplashActivity.this);
                alert.setTitle("No Internet");
                alert.setMessage("Click ok to go to Wifi settings");
                alert.setCancelable(false);
                alert.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_WIFI_SETTINGS);
                        startActivityForResult(intent, wifiSettingsRequestCode);
                    }
                }).create().show();
            }
        } else {
            Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
            startActivity(intent);
            SplashActivity.this.finish();

        }
    }

    /**
     * This receive call back from GETQRCODEFAGMENT where we register receiver
     * for find battery level
     * This is called while when we call getDeviceInfoData() method of GETQRCODEFAGMENT class
     * battery information callback
     *
     * @param intent
     */

    @Override
    public void onBatteryCallBack(Intent intent) {
        try {
            utils.compare_UpdatePreferenceString(getApplicationContext(), JsonTags.MMR_17.name(),
                    intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0) + "%");
        } catch (Exception e) {
            logException(e, "SplashActivity_onBatteryCallBack()");
        }

    }


    /**
     * onPause method
     */

    @Override
    protected void onPause() {
        try {
            if (batteryInfoReceiver != null) {
                unregisterReceiver(batteryInfoReceiver);
                batteryInfoReceiver = null;
                Log.d("Battery Receiver", " Unregistered");
            }
        } catch (Exception e) {
            logException(e, "SplashActivity_onPause()");
        }

        super.onPause();
    }

    public void logException(Exception e, String methodName) {
        try {
            Utilities utilities = Utilities.getInstance(this);
            Activity activity = this;
            Context context = getApplicationContext();
            ExceptionLogUtils exceptionLogUtils = new ExceptionLogUtils(utilities, context,
                    activity);
            exceptionLogUtils.addExceptionLog(utilities, context, activity, null, null, e,
                    methodName);
        } catch (Exception exp) {
            //     logException(exp, "SplashActivity_logException()");
        }
    }

    public void staticData() {
        SubscriberCode = "OFWK";
        //setting values to textviews
        username = "OFWK";
        tokenpassword = "OFCWRK";
        // SubscriberProductID = "1";
        grant_type = "password";


        utils.addPreference(getApplicationContext(), JsonTags.SubscriberProductID.name(),
                SubscriberProductID);
        getToken();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case wifiSettingsRequestCode:
                WifiManager wifiManager = (WifiManager)
                        getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if (wifiManager.isWifiEnabled()) {
                    changeActivity();
                } else {
                    changeActivity();
                }
                break;
        }
    }

    private void getToken() {
        try {
            WebService webService = new WebService();
            APIInterface apiInterface =
                    APIClient.getClient(SplashActivity.this).create(APIInterface.class);
            webService.apiCall(apiInterface.getToken(new GetTokenModel(username, tokenpassword,
                    grant_type)), SplashActivity.this,
                    new WebServiceInterface<Response<TokenResponse>>() {
                @Override
                public void apiResponse(Response<TokenResponse> response) {
                    if (response.code() == Constants.RESPONSE_SUCCESS) {


                        if (response.body().getTrackingid() != null) {
                            Utilities.getInstance(SplashActivity.this).addSecurePreference(SplashActivity.this, Constants.TrackingID, response.body().getTrackingid());
                            Log.d("Trackrecordsss", response.body().getTrackingid());
                        }

                        utils.addPreference(getApplicationContext(), JsonTags.access_token.name()
                                , response.body().getAccessToken());
                        utils.addPreference(getApplicationContext(), JsonTags.token_type.name(),
                                response.body().getToken_type());
                        utils.addPreference(getApplicationContext(),
                                JsonTags.SubscriberProductID.name(),
                                response.body().getSubscriberProductID());
                        PreferenceConstants.Token =
                                response.body().getAccessToken();
                        SubscriberProductID =
                                response.body().getSubscriberProductID();
                        PreferenceConstants.SubscriberProductID = SubscriberProductID;
                        Log.e("Token", response.body().getAccessToken());

                        Log.e("SubscriberProductID", response.body().getSubscriberProductID());

                        GetQRCodeFragment.unRegisterBatteryReceiver(SplashActivity.this);
                        if (utils.getPreference(SplashActivity.this, JsonTags.UDI.name(), "").isEmpty() || utils.getPreference(SplashActivity.this, JsonTags.UDI.name(), "").equals("")) {
                            hitUdiApi();
                        } else {
                            landingActivity();
                        }
                    }
                }

                @Override
                public void apiError(Response<TokenResponse> response) {
                    Toast.makeText(SplashActivity.this,
                            getResources().getText(R.string.api_error_toast), Toast.LENGTH_LONG).show();

                }

                @Override
                public void serverError(Throwable t) {
                    Toast.makeText(SplashActivity.this,
                            getResources().getText(R.string.server_error_toast),
                            Toast.LENGTH_LONG).show();
                    if (t instanceof UnknownHostException) {
                        Toast.makeText(SplashActivity.this,
                                getResources().getText(R.string.internet_error_toast),
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void hitUdiApi() {

        try {
            WebService webService = new WebService();

            APIInterface apiInterface =
                    APIClient.getClient(SplashActivity.this).create(APIInterface.class);

            webService.apiCall(apiInterface.getUDI(Utilities.getInstance(SplashActivity.this).getPreference(SplashActivity.this, JsonTags.SubscriberProductID.name(), "")), SplashActivity.this, new WebServiceInterface<Response<SocketDataSync>>() {
                @Override
                public void apiResponse(Response<SocketDataSync> response) {
                    Log.d("UDI", response.body().getData().getUDI());
                    if (response.body().isSuccess()) {
                        String UDI = response.body().getData().getUDI();
                        utils.addPreference(SplashActivity.this, JsonTags.UDI.name(), UDI);
                        Log.e("UDI", UDI);
                        PreferenceConstants.Udid = UDI;
                        landingActivity();

                    }
                }

                @Override
                public void apiError(Response<SocketDataSync> response) {
                    Toast.makeText(SplashActivity.this,
                            getResources().getText(R.string.api_error_toast), Toast.LENGTH_LONG).show();


                }

                @Override
                public void serverError(Throwable t) {
                    Toast.makeText(SplashActivity.this,
                            getResources().getText(R.string.server_error_toast),
                            Toast.LENGTH_LONG).show();
                    if (t instanceof UnknownHostException) {
                        Toast.makeText(SplashActivity.this,
                                getResources().getText(R.string.internet_error_toast),
                                Toast.LENGTH_LONG).show();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void landingActivity() {
        Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
        startActivity(intent);
        SplashActivity.this.finish();
    }


    @Override
    public void onStart() {
        super.onStart();
        final Branch branch = Branch.getAutoInstance(SplashActivity.this);
        // Branch init
        branch.initSession(new Branch.BranchReferralInitListener() {
            @Override
            public void onInitFinished(JSONObject referringParams, BranchError error) {
                if (error == null) {
                    try {
                        Log.d("branch data::", referringParams.toString());
                        if (referringParams.has("StoreId")) {

                            String code1 = referringParams.optString("StoreId");
                            String enterprise_code = referringParams.optString("EnterpriseID");

                            if (!code1.equalsIgnoreCase("")) {
                                Utilities.getInstance(SplashActivity.this).addPreference(SplashActivity.this, Constants.QRCODEID, code1);
                                Utilities.getInstance(SplashActivity.this).addPreference(SplashActivity.this, Constants.STOREID, code1);
                                Utilities.getInstance(SplashActivity.this).addPreference(SplashActivity.this, Constants.ENTERPRISEPATNERID, enterprise_code);
                            }
//                            utils.addPreference(getApplicationContext(), Constants.QRCODEID,
//                            code1);
//                            utils.addPreference(getApplicationContext(), Constants.STOREID,
//                            code1);
//                            utils.addPreference(getApplicationContext(), Constants
//                            .ENTERPRISEPATNERID, enterprise_code);
                            code = code1;
                            Log.w("barbardebugchutkara", code1);
                            Log.w("barbardebugchutkara2", enterprise_code);
                            Answers.getInstance().logCustom(new CustomEvent("session _start")
                                    .putCustomAttribute("response", code));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //Open Activity as per deep link parameters (if found), otherwise open
                    // default Activity
                } else {

                    Log.i("BRANCH SDK", error.getMessage());
                    Answers.getInstance().logCustom(new CustomEvent("Branch_error")
                            .putCustomAttribute("response", code));
                }
            }
        }, this.getIntent().getData(), this);
    }
}


