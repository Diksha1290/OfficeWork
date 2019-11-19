package com.officework.activities;


import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.officework.ApiController.APIClient;
import com.officework.ApiController.APIInterface;
import com.officework.ApiController.WebService;
import com.officework.ApiController.WebServiceInterface;
import com.officework.R;
import com.officework.Services.OnClearFromRecentService;
import com.officework.adapters.PermissionsScreenAdapter;
import com.officework.base.BaseCompatActivity;
import com.officework.constants.AppConstantsTheme;
import com.officework.constants.Constants;
import com.officework.constants.FragmentTag;
import com.officework.constants.JsonTags;
import com.officework.constants.PreferenceConstants;
import com.officework.fragments.GetQRCodeFragment;
import com.officework.fragments.HardwareInfoFragment;
import com.officework.fragments.TitleBarFragment;
import com.officework.fragments.VerifyTradableManualFragment;
import com.officework.models.GetTokenModel;
import com.officework.models.RequestPermissionModel;
import com.officework.models.SocketDataSync;
import com.officework.models.TokenResponse;
import com.officework.testing_profiles.realmDatabase.operationManager.RealmOperations;
import com.officework.testing_profiles.ui.fragment.RecieptInformationScreenFragment;
import com.officework.utils.Utilities;
import com.officework.utils.WebserviceUrls;
import com.officework.utils.socket.SocketConstants;
import com.officework.utils.socket.SocketHelper;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import io.socket.client.IO;
import retrofit2.Response;

public class RequestAllPermissionsActivity extends BaseCompatActivity {
    final public int wifiSettingsRequestCode = 23;
    String[] permissionsArray;
    Button rqstPermissionbtn;
    String[] permissionnames;
    int[] permissionsimg;
    String[] description;
    Utilities utils;
    public BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            utils.addPreference(RequestAllPermissionsActivity.this, JsonTags.MMR_68.name(),
                    String.valueOf(level) + "%");
        }
    };
    RecyclerView recyclerView;
    PermissionsScreenAdapter adapter;
    ArrayList<RequestPermissionModel> list;
    AlertDialog.Builder alert;
    boolean settings = false;
    String SubscriberCode;
    String username;
    String tokenpassword;
    String grant_type;
    String SubscriberProductID;
    BluetoothAdapter bAdapter;
    ProgressDialog progressBar;
    FrameLayout frame;
    RelativeLayout reqst_desc;
    RealmOperations realmOperations;
    private int MULTIPLE_PERMISSION_CODE = 111;
    private IntentIntegrator qrScan;
    private String androidId, diagnosresult;

    @Override
    protected void initUI(Bundle savedInstanceState) {
        try {
            setContentView(R.layout.activity_request_all_permissions);
            utils = Utilities.getInstance(this);
            frame = findViewById(R.id.frame);
            reqst_desc = findViewById(R.id.reqst_desc);
            qrScan = new IntentIntegrator(this);
            bAdapter = BluetoothAdapter.getDefaultAdapter();
            int colorValue = Color.parseColor("#" + "2B2680");
            AppConstantsTheme.setColor(colorValue, colorValue, colorValue, colorValue, colorValue);
        } catch (Exception e) {
            //logException(e, "MainActivity_initUI()");
        }
    }

    @Override
    protected void initVariable(Bundle savedInstanceState) {
        TitleBarFragment titleBarFragment = new TitleBarFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("SHOULDHIDE", true);
        titleBarFragment.setArguments(bundle);
        addFragment(R.id.headerContainer, titleBarFragment, FragmentTag.HEADER_FRAGMENT.name(),
                false);
        permissionnames = new String[]{"Phone", "Camera", "Microphone", "Storage", "Location",
                "Bluetooth"};
        permissionsimg = new int[]{R.drawable.ic_call_permscrn, R.drawable.ic_camera_svg,
                R.drawable.ic_mic_blue_svg_128, R.drawable.ic_sd_perm, R.drawable.ic_gps_svg,
                R.drawable.ic_connectivity};
        description = new String[]{};
        list = new ArrayList<>();
        for (int i = 0; i < permissionsimg.length; i++) {
            RequestPermissionModel object = new RequestPermissionModel();
            object.setImg(permissionsimg[i]);
            object.setName(permissionnames[i]);
            object.setDescription(getResources().getStringArray(R.array.permissionDescription)[i]);
            list.add(object);
        }
        recyclerView = findViewById(R.id.permissionsrcv);
        try {

            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            adapter = new PermissionsScreenAdapter(list, RequestAllPermissionsActivity.this);

            recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        rqstPermissionbtn = findViewById(R.id.rqstBtn);
        permissionsArray = new String[]{Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_SMS};
        rqstPermissionbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settings = false;
                start();
            }
        });
    }

    public void requestAllPermission() {
        ActivityCompat.requestPermissions(RequestAllPermissionsActivity.this, permissionsArray,
                MULTIPLE_PERMISSION_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, final String[] permissions,
                                           int[] grantResults) {
        if (requestCode == MULTIPLE_PERMISSION_CODE) {

            for (int i = 0, len = permissions.length; i < len; i++) {
                String permission = permissions[i];
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {

                    boolean showRationale = shouldShowRequestPermissionRationale(permission);
                    if (!showRationale) {
                        if (!settings) {
                            settings = true;
                            alert = new AlertDialog.Builder(RequestAllPermissionsActivity.this);
                            alert.setTitle("Permission Needed");
                            alert.setMessage("Click ok to go to the app settings");
                            alert.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);
                                }
                            })
                                    .setNegativeButton("cancel",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog,
                                                                    int which) {
                                                    dialog.dismiss();
                                                }
                                            }).create().show();
                        }

                    } else if (Manifest.permission.READ_PHONE_STATE.equals(permission) && Manifest.permission.CAMERA.equals(permission) && Manifest.permission.ACCESS_FINE_LOCATION.equals(permission) && Manifest.permission.RECORD_AUDIO.equals(permission) && Manifest.permission.READ_SMS.equals(permission)) {
                        ActivityCompat.requestPermissions(RequestAllPermissionsActivity.this,
                                permissionsArray, MULTIPLE_PERMISSION_CODE);


                    }

                }
            }
            if (checkallPermissions()) {
                start();


            }

        }
    }

    @Override
    protected void onResume() {

        super.onResume();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case wifiSettingsRequestCode:
                WifiManager wifiManager = (WifiManager)
                        getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if (wifiManager.isWifiEnabled()) {
                    //startScanner();
                    start();
                }
                break;
            case 101:
                if (bAdapter.isEnabled()) {
                    staticData();
                }
                break;


            case GetQRCodeFragment.MESSAGE_CODE:

                if (resultCode == RESULT_OK) {
                    staticData();
                } else {
                    Utilities.showAlert(this, new Utilities.onAlertOkListener() {
                        @Override
                        public void onOkButtonClicked(String tag) {

                            staticData();
                        }
                    }, getResources().getString(R.string.message_permission),
                            getResources().getString(R.string.app_name), "Ok", "", "Sync");

                }
                break;

        }
    }

    public void staticData() {
        boolean isIMEiAvailable = GetQRCodeFragment.getIMEI(true, Utilities.getInstance(this),
                this, RequestAllPermissionsActivity.this);

        if (isIMEiAvailable) {
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
    }

    private void getToken() {
        try {
            showProgressDialog();
            WebService webService = new WebService();
            APIInterface apiInterface =
                    APIClient.getClient(RequestAllPermissionsActivity.this).create(APIInterface.class);
            webService.apiCall(apiInterface.getToken(new GetTokenModel(username, tokenpassword,
                    grant_type)), RequestAllPermissionsActivity.this,
                    new WebServiceInterface<Response<TokenResponse>>() {
                @Override
                public void apiResponse(Response<TokenResponse> response) {
                    if (response.code() == Constants.RESPONSE_SUCCESS) {

                        if (response.body().getTrackingid() != null) {
                            Utilities.getInstance(RequestAllPermissionsActivity.this).addSecurePreference(RequestAllPermissionsActivity.this, Constants.TrackingID, response.body().getTrackingid());
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
                        GetQRCodeFragment.unRegisterBatteryReceiver(RequestAllPermissionsActivity.this);
//                        landingActivity();
                        GetQRCodeFragment.getIMEI(true,
                                Utilities.getInstance(RequestAllPermissionsActivity.this),
                                getApplicationContext(), RequestAllPermissionsActivity.this);


                        if (utils.getPreference(RequestAllPermissionsActivity.this,
                                JsonTags.UDI.name(), "").isEmpty() || utils.getPreference(RequestAllPermissionsActivity.this, JsonTags.UDI.name(), "").equals("")) {
                            hitUdiApi();
                        } else {
                            if (!utils.getPreference(RequestAllPermissionsActivity.this,
                                    Constants.STOREID, "").equalsIgnoreCase("")) {
                                checkTradeAbility();
                            }else{
                                landingActivity();
                            }

                        }


                    }
                }

                @Override
                public void apiError(Response<TokenResponse> response) {
                    Toast.makeText(RequestAllPermissionsActivity.this,
                            getResources().getText(R.string.api_error_toast), Toast.LENGTH_LONG).show();
                    progressBar.dismiss();
                }

                @Override
                public void serverError(Throwable t) {
                    Toast.makeText(RequestAllPermissionsActivity.this,
                            getResources().getText(R.string.server_error_toast),
                            Toast.LENGTH_LONG).show();
                    progressBar.dismiss();
                    if (t instanceof UnknownHostException) {
                        Toast.makeText(RequestAllPermissionsActivity.this,
                                getResources().getText(R.string.internet_error_toast),
                                Toast.LENGTH_LONG).show();
                    }
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }


    }




    public HashMap<String, Object> saveTradabilityMap() {
        HashMap<String, Object> map = new HashMap<>();
        try {
            map.put("Make", utils.getSecurePreference(RequestAllPermissionsActivity.this, "MMR_4"
                    , Build.MANUFACTURER));
            map.put("Model", utils.getSecurePreference(RequestAllPermissionsActivity.this, "MMR_5"
                    , Build.MODEL));
            map.put("Capacity", utils.getSecurePreference(RequestAllPermissionsActivity.this,
                    "MMR_6", HardwareInfoFragment.getTotalInternalMemorySize()));
            map.put("IMEI", utils.getSecurePreference(RequestAllPermissionsActivity.this, "MMR_1"
                    , ""));
        } catch (Exception E) {
            E.printStackTrace();
        }
        return map;
    }

    private void landingActivity() {

        //  postDiagnosticsData();  Call verify Storeid Api Call

        Intent intent = new Intent(RequestAllPermissionsActivity.this,
                LandingActivity.class);
        startActivity(intent);
        RequestAllPermissionsActivity.this.finish();
    }


    private void hitUdiApi() {

        try {
            WebService webService = new WebService();

            APIInterface apiInterface =
                    APIClient.getClient(RequestAllPermissionsActivity.this).create(APIInterface.class);

            webService.apiCall(apiInterface.getUDI(Utilities.getInstance(RequestAllPermissionsActivity.this).getPreference(RequestAllPermissionsActivity.this, JsonTags.SubscriberProductID.name(), "")), RequestAllPermissionsActivity.this, new WebServiceInterface<Response<SocketDataSync>>() {
                @Override
                public void apiResponse(Response<SocketDataSync> response) {
                    Log.d("UDI", response.body().getData().getUDI());
                    if (response.body().isSuccess()) {
                        String UDI = response.body().getData().getUDI();
                        utils.addPreference(RequestAllPermissionsActivity.this,
                                JsonTags.UDI.name(), UDI);
                        Log.e("UDI", UDI);
                        PreferenceConstants.Udid = UDI;

                        if (!utils.getPreference(RequestAllPermissionsActivity.this,
                                Constants.STOREID, "").equalsIgnoreCase("")) {
                            checkTradeAbility();
                        }else{
                            landingActivity();
                        }

                      //  checkTradeAbility();

                    }
                }

                @Override
                public void apiError(Response<SocketDataSync> response) {
                    Toast.makeText(RequestAllPermissionsActivity.this,
                            getResources().getText(R.string.api_error_toast), Toast.LENGTH_LONG).show();
                    progressBar.dismiss();

                }

                @Override
                public void serverError(Throwable t) {
                    progressBar.dismiss();
                    Toast.makeText(RequestAllPermissionsActivity.this,
                            getResources().getText(R.string.server_error_toast),
                            Toast.LENGTH_LONG).show();
                    if (t instanceof UnknownHostException) {
                        Toast.makeText(RequestAllPermissionsActivity.this,
                                getResources().getText(R.string.internet_error_toast),
                                Toast.LENGTH_LONG).show();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void checkTradeAbility() {
//        showProgressDialog();
        WebService webService = new WebService();
        APIInterface apiInterface =
                APIClient.getClient(RequestAllPermissionsActivity.this).create(APIInterface.class);
        webService.apiCall(apiInterface.checkTradability(saveTradabilityMap()),
                RequestAllPermissionsActivity.this,
                new WebServiceInterface<Response<SocketDataSync>>() {
            @Override
            public void apiResponse(Response<SocketDataSync> response) {
                progressBar.dismiss();
                progressBar = null;

                if (response.isSuccessful()) {

                    if (response.body().isSuccess()) {
                        if (response.body().getData().isDeviceTradable()) {
                            if (response.body().getData() != null && response.body().getData().getDevicePriceInfo() != null) {
                                utils.addPreference(RequestAllPermissionsActivity.this,
                                        Constants.SKUID,
                                        response.body().getData().getDevicePriceInfo().getSKUID());
                                Log.w("SKUID",
                                        response.body().getData().getDevicePriceInfo().getSKUID());
                                utils.addPreference(RequestAllPermissionsActivity.this,
                                        Constants.DEVICEPRICE,
                                        response.body().getData().getDevicePriceInfo().getDeviceFullPrice());
//                                utils.addPreference(RequestAllPermissionsActivity.this,
//                                Constants.CURRENCYSYMBOL, response.body().getData()
//                                .getDevicePriceInfo().getDeviceFullPrice() );

                            }
                            if (!utils.getPreference(RequestAllPermissionsActivity.this,
                                    Constants.STOREID, "").equalsIgnoreCase("")) {
//                                String storeid=utils.getPreference2
//                                (RequestAllPermissionsActivity.this,Constants.STOREID,"");
//                                String enterprisePartnerID=utils.getPreference2
//                                (RequestAllPermissionsActivity.this,Constants
//                                .ENTERPRISEPATNERID,"");
//
////                                utils.clearPreferences(WelcomeActivity.this);
//
//                                Utilities.getInstance(RequestAllPermissionsActivity.this)
//                                .addPreference(RequestAllPermissionsActivity.this, Constants
//                                .QRCODEID, storeid);
//                                Utilities.getInstance(RequestAllPermissionsActivity.this)
//                                .addPreference(RequestAllPermissionsActivity.this, Constants
//                                .STOREID, storeid);
//                                Utilities.getInstance(RequestAllPermissionsActivity.this)
//                                .addPreference(RequestAllPermissionsActivity.this, Constants
//                                .ENTERPRISEPATNERID, enterprisePartnerID);

                                checkingDeviceActivity();
                            } else {
                                landingActivity();
                            }


                        } else if (response.body().getData().isOrderCompleted()) {
                            reqst_desc.setVisibility(View.GONE);
                            frame.setVisibility(View.VISIBLE);
                            Utilities.getInstance(RequestAllPermissionsActivity.this).addPreference(RequestAllPermissionsActivity.this, Constants.CURRENCYSYMBOL, response.body().getData().getOrderDetail().getCurrencySymbol());
                            RecieptInformationScreenFragment recieptInformationScreenFragment =
                                    new RecieptInformationScreenFragment();
                            Bundle bundle = new Bundle();

                            bundle.putString("OFFER_PRICE",
                                    utils.getPreference(RequestAllPermissionsActivity.this,
                                            Constants.CURRENCYSYMBOL, "") + " " + response.body().getData().getOrderDetail().getQuotedPrice());
                            bundle.putString("ORDER_ID",
                                    response.body().getData().getOrderDetail().getOrderDetailID());

                            bundle.putString("CUSTOMER_ADDRESS1", "");
                            bundle.putString("CUSTOMER_ADDRESS2", "");
                            bundle.putString("CUSTOMER_ADDRESS3", "");

// bundle.putString("POSTAL_CODE", txtPostCode.getText().toString());
                            bundle.putString("CUSTOMER_CITY", "");

                            bundle.putString("ADDRESS1", "");
                            bundle.putString("ADDRESS2", "");
                            bundle.putString("ADDRESS3", "");
                            bundle.putString("POSTAL_CODE", "");
                            bundle.putString("CITY", "");
                            bundle.putString("CREATED_DATE",
                                    response.body().getData().getOrderDetail().getCreatedDate());
                            bundle.putString("EMAIL",
                                    response.body().getData().getOrderDetail().getEmailAddress());
                            recieptInformationScreenFragment.setArguments(bundle);
                            replaceFragment(R.id.frame, recieptInformationScreenFragment,
                                    FragmentTag.MANUAL_TEST_FRAGMENT.name(), false);
                        } else {
                            reqst_desc.setVisibility(View.GONE);
                            frame.setVisibility(View.VISIBLE);
                            TitleBarFragment fragment1 =
                                    (TitleBarFragment) getFragment(R.id.headerContainer);
                            if (fragment1 != null) {
                                fragment1.setTitleBarVisibility(true);
                                fragment1.setHeaderTitleAndSideIcon(getResources().getString(R.string.app_name), true, false, 0);
                            }
                            Fragment fragment = new VerifyTradableManualFragment();
                            replaceFragment(R.id.frame, fragment,
                                    "", false);
                        }
                    }
                } else {
                    Toast.makeText(RequestAllPermissionsActivity.this,
                            getResources().getString(R.string.api_error_toast),
                            Toast.LENGTH_LONG).show();
                }
            }


            @Override
            public void apiError(Response<SocketDataSync> response) {
                progressBar.dismiss();
                progressBar = null;
                Toast.makeText(RequestAllPermissionsActivity.this,
                        getResources().getString(R.string.api_error_toast), Toast.LENGTH_LONG).show();
            }

            @Override
            public void serverError(Throwable t) {
                progressBar.dismiss();
                progressBar = null;
                Toast.makeText(RequestAllPermissionsActivity.this,
                        getResources().getText(R.string.server_error_toast), Toast.LENGTH_LONG).show();
                if (t instanceof UnknownHostException) {
                    Toast.makeText(RequestAllPermissionsActivity.this,
                            getResources().getText(R.string.internet_error_toast),
                            Toast.LENGTH_LONG).show();
                }
// fabricLog("save_deviceinfo_api_exception",t.getMessage());
                utils.addPreferenceBoolean(RequestAllPermissionsActivity.this,
                        Constants.IS_SAVE_API_CALL, false);
            }
        });
    }


    public void start() {
        if (checkallPermissions()) {
            //  if (bAdapter.isEnabled()) {
            if (Utilities.isInternetWorking(RequestAllPermissionsActivity.this)) {
                staticData();
                // startScanner();
            } else {


                AlertDialog.Builder alert =
                        new AlertDialog.Builder(RequestAllPermissionsActivity.this);
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
            requestAllPermission();
        }
    }

    public boolean checkallPermissions() {
        boolean isAllPermissionAccepted = false;
        if (ContextCompat.checkSelfPermission(RequestAllPermissionsActivity.this,
                Manifest.permission.READ_PHONE_STATE)
                + ContextCompat.checkSelfPermission(RequestAllPermissionsActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                + ContextCompat.checkSelfPermission(RequestAllPermissionsActivity.this,
                Manifest.permission.CAMERA)
                + ContextCompat.checkSelfPermission(RequestAllPermissionsActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                + ContextCompat.checkSelfPermission(RequestAllPermissionsActivity.this,
                Manifest.permission.RECORD_AUDIO)
                + ContextCompat.checkSelfPermission(RequestAllPermissionsActivity.this,
                Manifest.permission.READ_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            isAllPermissionAccepted = true;
        }
        return isAllPermissionAccepted;
    }

    public void showProgressDialog() {
        progressBar = new ProgressDialog(RequestAllPermissionsActivity.this);
        try {
            progressBar.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
//                progressBar.setMessage("");
        progressBar.getWindow()
                .setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressBar.setContentView(R.layout.progressdialog);

//                View view = LayoutInflater.from(getActivity()).inflate(R.layout.progressdialog,
//                null);
//                progressBar.addContentView(view,new RelativeLayout.LayoutParams(RelativeLayout
//                .LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        progressBar.setCancelable(false);
        progressBar.setIndeterminate(true);
        progressBar.show();
    }

    @Override
    public void onBackPressed() {
        Utilities.getInstance(this).showAlert(this, new Utilities.onAlertOkListener() {
            @Override
            public void onOkButtonClicked(String tag) {

                finishAffinity();
            }
        }, Html.fromHtml(getResources().getString(R.string.exit_msg)),
                getResources().getString(R.string.txtAlertTitleGreatAlert), "No", "Yes", "Sync");
    }

    public void checkingDeviceActivity() {
        if (Utilities.getInstance(this).isInternetWorking(this)) {
            startScan();
            utils.loadDatabase(this);
            startService(new Intent(this.getBaseContext(),
                    OnClearFromRecentService.class));
            realmOperations = new RealmOperations();
            realmOperations.resetTestStatus();
            utils.checkSensors(this);

            Intent intent = new Intent(this, CheckingDeviceActivity.class);
            intent.putExtra("jsonArray", diagnosresult);
            startActivity(intent);
            unregisterReceiver(this.mBatInfoReceiver);
        }
    }

    private void startScan() {
        try {
            if (utils.getPreference(this,
                    Constants.ANDROID_ID, "").isEmpty()) {
                androidId = UUID.randomUUID().toString();
                Utilities.getInstance(this).addPreference(this,
                        Constants.ANDROID_ID, androidId);

            } else {
                androidId = Utilities.getInstance(this).getPreference(this,
                        Constants.ANDROID_ID, "");

            }
            if (utils.isInternetWorking(this)) {
                try {
                    String androidId = Utilities.getInstance(this).getPreference(this,
                            Constants.ANDROID_ID, "");
                    String qrCodeiD = Utilities.getInstance(this).getPreference(this,
                            JsonTags.StoreID.name(), "");
                    IO.Options options = new IO.Options();
                    Log.d("scan android id", androidId);
                    SocketHelper socketHelper =
                            new SocketHelper.Builder(WebserviceUrls.BaseUrl + "?" + Constants.REQUEST_UNIQUE_ID + "=" + androidId, options)
                                    .addEvent(SocketConstants.EVENT_CONNECTED)
                                    .addListener(null)
                                    .build();

                    if (socketHelper != null && socketHelper.socket.connected()) {

                        socketHelper.emitScreenChangeEvent("scanner", androidId, qrCodeiD);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            fetchAndroidId();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void fetchAndroidId() {

//        Constants.isDoAllClicked = false;
        if (Utilities.getInstance(this).getPreference(this,
                Constants.ANDROID_ID, "").isEmpty()) {
            androidId = UUID.randomUUID().toString();
            Utilities.getInstance(this).addPreference(this,
                    Constants.ANDROID_ID, androidId);

        } else {
            registerReceiver(this.mBatInfoReceiver,
                    new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        }

    }

}
