package com.officework.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
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

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.officework.ApiController.APIClient;
import com.officework.ApiController.APIInterface;
import com.officework.ApiController.WebService;
import com.officework.ApiController.WebServiceInterface;
import com.officework.R;
import com.officework.Services.OnClearFromRecentService;
import com.officework.base.BaseCompatActivity;
import com.officework.constants.Constants;
import com.officework.constants.FragmentTag;
import com.officework.constants.JsonTags;
import com.officework.fragments.GetQRCodeFragment;
import com.officework.fragments.HardwareInfoFragment;
import com.officework.fragments.TitleBarFragment;
import com.officework.fragments.VerifyTradableManualFragment;
import com.officework.interfaces.InterfaceBatteryCallback;
import com.officework.models.SocketDataSync;
import com.officework.testing_profiles.realmDatabase.operationManager.RealmOperations;
import com.officework.testing_profiles.ui.fragment.RecieptInformationScreenFragment;
import com.officework.utils.Utilities;
import com.officework.utils.WebserviceUrls;
import com.officework.utils.socket.SocketConstants;
import com.officework.utils.socket.SocketHelper;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.UUID;

import io.socket.client.IO;
import retrofit2.Response;

public class WelcomeActivity extends BaseCompatActivity implements View.OnClickListener {

    final public int wifiSettingsRequestCode = 22;
    private Button beginBT;
    ProgressDialog progressBar;
    private Utilities utils;
    FrameLayout frame;
    RelativeLayout welc;
    final public int REQUEST_CODE_IMEI_PERMISSION = 504;
    private String androidId, diagnosresult;
    RealmOperations realmOperations;
    public BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            utils.addPreference(WelcomeActivity.this, JsonTags.MMR_68.name(), String.valueOf(level) + "%");
        }
    };

    @Override
    protected void initUI(Bundle savedInstanceState) {
        try {
            setContentView(R.layout.activity_welcome);

        }catch (Exception e){

        }
    }

    @Override
    protected void initVariable(Bundle savedInstanceState) {
        beginBT = (Button) findViewById(R.id.beginBT);
        frame = findViewById(R.id.frame);
        welc=findViewById(R.id.welc);
        beginBT.setOnClickListener(this);
        utils = Utilities.getInstance(WelcomeActivity.this);
        TitleBarFragment titleBarFragment = new TitleBarFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("WELCOME", true);
        titleBarFragment.setArguments(bundle);
        addFragment(R.id.headerContainer, titleBarFragment, FragmentTag.HEADER_FRAGMENT.name(),
                false);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
//                GetQRCodeFragment.getIMEI(true, Utilities.getInstance(this), WelcomeActivity.this,
//                        this);
//
//            } else {
//                checkRuntimePermisson();
//            }
//        } else {
//            GetQRCodeFragment.getIMEI(true, Utilities.getInstance(this), WelcomeActivity.this, this);
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                GetQRCodeFragment.getIMEI(true, Utilities.getInstance(this), WelcomeActivity.this, this);
            }


        }else {
            GetQRCodeFragment.getIMEI(true, Utilities.getInstance(this), WelcomeActivity.this, this);
        }
    }

    private void checkRuntimePermisson() {
        try {
            int hasWritePhoneReadPermission = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                hasWritePhoneReadPermission =
                        checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
                if (hasWritePhoneReadPermission != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
                            REQUEST_CODE_IMEI_PERMISSION);
                    return;
                }
            }
            GetQRCodeFragment.getIMEI(true, Utilities.getInstance(this), getApplicationContext(), WelcomeActivity.this);


        } catch (Exception e) {

        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.beginBT:
                GetQRCodeFragment.getDeviceInfoData(Utilities.getInstance(this), WelcomeActivity.this, this,
                        null);
                if (Utilities.isInternetWorking(WelcomeActivity.this)) {
                    if (ContextCompat.checkSelfPermission(WelcomeActivity.this,
                            Manifest.permission.READ_PHONE_STATE)
                            + ContextCompat.checkSelfPermission(WelcomeActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)
                            + ContextCompat.checkSelfPermission(WelcomeActivity.this,
                            Manifest.permission.CAMERA)
                            + ContextCompat.checkSelfPermission(WelcomeActivity.this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            + ContextCompat.checkSelfPermission(WelcomeActivity.this,
                            Manifest.permission.RECORD_AUDIO)
                            + ContextCompat.checkSelfPermission(WelcomeActivity.this,
                            Manifest.permission.READ_SMS)
                            != PackageManager.PERMISSION_GRANTED) {
                        Intent intent = new Intent(WelcomeActivity.this, RequestAllPermissionsActivity.class);
                        startActivity(intent);
                        WelcomeActivity.this.finish();
                    }else {
                        checkTradeAbility();
                    }
                }else
                {
                    androidx.appcompat.app.AlertDialog.Builder alert =
                            new androidx.appcompat.app.AlertDialog.Builder(WelcomeActivity.this);
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
                break;
        }
    }





    public HashMap<String, Object> saveTradabilityMap(){
        HashMap<String, Object> map = new HashMap<>();
        try {
            map.put("Make", utils.getSecurePreference(WelcomeActivity.this, "MMR_4", Build.MANUFACTURER));
            map.put("Model", utils.getSecurePreference(WelcomeActivity.this, "MMR_5", Build.MODEL));
            map.put("Capacity", utils.getSecurePreference(WelcomeActivity.this, "MMR_6", HardwareInfoFragment.getTotalInternalMemorySize()));
            map.put("IMEI", utils.getSecurePreference(WelcomeActivity.this, "MMR_1", ""));
        } catch (Exception E) {
                E.printStackTrace();
            Log.d("capacityNull",String.valueOf(E.getMessage()));
        }
        return map;
    }


    public void checkTradeAbility(){


        showProgressDialog();
        WebService webService=new WebService();
        APIInterface apiInterface =
                APIClient.getClient(WelcomeActivity.this).create(APIInterface.class);
        webService.apiCall(apiInterface.checkTradability(saveTradabilityMap()), WelcomeActivity.this, new WebServiceInterface<Response<SocketDataSync>>() {
            @Override
            public void apiResponse(Response<SocketDataSync> response) {
                progressBar.dismiss();
                progressBar=null;

                if(response.isSuccessful()) {

                    if (response.body().isSuccess()) {
                        if (response.body().getData().isDeviceTradable()) {

                            if (response.body().getData() != null && response.body().getData().getDevicePriceInfo() != null) {
                                utils.addPreference(WelcomeActivity.this, Constants.SKUID, response.body().getData().getDevicePriceInfo().getSKUID());
                                Log.w("SKUID", response.body().getData().getDevicePriceInfo().getSKUID());
                                utils.addPreference(WelcomeActivity.this, Constants.DEVICEPRICE, response.body().getData().getDevicePriceInfo().getDeviceFullPrice());
//                               utils.addPreference(WelcomeActivity.this, Constants.CURRENCYSYMBOL, response.body().getData().getDevicePriceInfo(). );


                            }

//                            Utilities.getInstance(WelcomeActivity.this).addPreferenceBoolean(WelcomeActivity.this, Constants.IS_DEVICE_TRADEABLE, true);

                            if(!utils.getPreference(WelcomeActivity.this,Constants.STOREID,"").equalsIgnoreCase(""))
                            {
//                                String storeid=utils.getPreference2(WelcomeActivity.this,Constants.STOREID,"");
//                                String enterprisePartnerID=utils.getPreference2(WelcomeActivity.this,Constants.ENTERPRISEPATNERID,"");
//
////                                utils.clearPreferences(WelcomeActivity.this);
//
//                                Utilities.getInstance(WelcomeActivity.this).addPreference(WelcomeActivity.this, Constants.QRCODEID, storeid);
//                                Utilities.getInstance(WelcomeActivity.this).addPreference(WelcomeActivity.this, Constants.STOREID, storeid);
//                                Utilities.getInstance(WelcomeActivity.this).addPreference(WelcomeActivity.this, Constants.ENTERPRISEPATNERID, enterprisePartnerID);

                                checkingDeviceActivity();
                            }
                            else {
                                changeActivity();
                            }
                        } else if(response.body().getData().isOrderCompleted()){
                            welc.setVisibility(View.GONE);
                            frame.setVisibility(View.VISIBLE);
                            Utilities.getInstance(WelcomeActivity.this).addPreference(WelcomeActivity.this, Constants.CURRENCYSYMBOL, response.body().getData().getOrderDetail().getCurrencySymbol());

                            RecieptInformationScreenFragment recieptInformationScreenFragment=new RecieptInformationScreenFragment();
                            Bundle bundle = new Bundle();

                            bundle.putString("OFFER_PRICE", utils.getPreference(WelcomeActivity.this, Constants.CURRENCYSYMBOL,"")+" "+response.body().getData().getOrderDetail().getQuotedPrice());
                            bundle.putString("ORDER_ID", response.body().getData().getOrderDetail().getOrderDetailID());

                            bundle.putString("CUSTOMER_ADDRESS1","");
                            bundle.putString("CUSTOMER_ADDRESS2","");
                            bundle.putString("CUSTOMER_ADDRESS3", "");

// bundle.putString("POSTAL_CODE", txtPostCode.getText().toString());
                            bundle.putString("CUSTOMER_CITY", "");

                            bundle.putString("ADDRESS1", "");
                            bundle.putString("ADDRESS2","");
                            bundle.putString("ADDRESS3", "");
                            bundle.putString("POSTAL_CODE", "");
                            bundle.putString("CITY", "");
                            bundle.putString("CREATED_DATE",response.body().getData().getOrderDetail().getCreatedDate());
                            bundle.putString("EMAIL",response.body().getData().getOrderDetail().getEmailAddress());
                            recieptInformationScreenFragment.setArguments(bundle);
                            replaceFragment(R.id.frame, recieptInformationScreenFragment,
                                    FragmentTag.MANUAL_TEST_FRAGMENT.name(), false);
                        }
                        else {

                            welc.setVisibility(View.GONE);
                            frame.setVisibility(View.VISIBLE);
                            Fragment fragment = new VerifyTradableManualFragment();
                            replaceFragment(R.id.frame, fragment,
                                    "", false);
                        }
                    }
                }else {
                    Toast.makeText(WelcomeActivity.this, getResources().getString(R.string.api_error_toast), Toast.LENGTH_LONG).show();
                }
            }


            @Override
            public void apiError(Response<SocketDataSync> response) {
                progressBar.dismiss();
                progressBar=null;
                Toast.makeText(WelcomeActivity.this, getResources().getString(R.string.api_error_toast), Toast.LENGTH_LONG).show();
            }

            @Override
            public void serverError(Throwable t) {
                progressBar.dismiss();
                progressBar=null;
                Toast.makeText(WelcomeActivity.this,getResources().getText(R.string.server_error_toast),Toast.LENGTH_LONG).show();
                if( t instanceof UnknownHostException)
                {
                    Toast.makeText(WelcomeActivity.this,getResources().getText(R.string.internet_error_toast),Toast.LENGTH_LONG).show(); }
// fabricLog("save_deviceinfo_api_exception",t.getMessage());
                utils.addPreferenceBoolean(WelcomeActivity.this, Constants.IS_SAVE_API_CALL, false);
            }
        });
    }






    public void showProgressDialog(){
        progressBar = new ProgressDialog(WelcomeActivity.this);
        try{
            progressBar.show();
        }catch (Exception e){
            e.printStackTrace();
        }
//                progressBar.setMessage("");
        progressBar.getWindow()
                .setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressBar.setContentView(R.layout.progressdialog);

//                View view = LayoutInflater.from(getActivity()).inflate(R.layout.progressdialog,null);
//                progressBar.addContentView(view,new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        progressBar.setCancelable(false);
        progressBar.setIndeterminate(true);
        progressBar.show();
    }











    private void changeActivity() {
        if (ContextCompat.checkSelfPermission(WelcomeActivity.this,
                Manifest.permission.READ_PHONE_STATE)
                + ContextCompat.checkSelfPermission(WelcomeActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                + ContextCompat.checkSelfPermission(WelcomeActivity.this,
                Manifest.permission.CAMERA)
                + ContextCompat.checkSelfPermission(WelcomeActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                + ContextCompat.checkSelfPermission(WelcomeActivity.this,
                Manifest.permission.RECORD_AUDIO)
                + ContextCompat.checkSelfPermission(WelcomeActivity.this,
                Manifest.permission.READ_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            if (Utilities.isInternetWorking(WelcomeActivity.this)) {
                Intent intent = new Intent(WelcomeActivity.this, LandingActivity.class);
                startActivity(intent);
                WelcomeActivity.this.finish();
            } else {
                androidx.appcompat.app.AlertDialog.Builder alert =
                        new androidx.appcompat.app.AlertDialog.Builder(WelcomeActivity.this);
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
            Intent intent = new Intent(WelcomeActivity.this, RequestAllPermissionsActivity.class);
            startActivity(intent);
            WelcomeActivity.this.finish();

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case wifiSettingsRequestCode:
                WifiManager wifiManager = (WifiManager)
                        getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if (wifiManager.isWifiEnabled()) {
                    checkTradeAbility();
                } else {
                    checkTradeAbility();
                }
                break;
        }
    }





    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        try {
            FragmentManager fm = getSupportFragmentManager();
            Fragment frag = fm.findFragmentById(R.id.container);
            switch (requestCode) {

                case REQUEST_CODE_IMEI_PERMISSION:
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // Permission Granted
                        GetQRCodeFragment.getIMEI(true, Utilities.getInstance(this), getApplicationContext(),
                                WelcomeActivity.this);
                    } else {
                        // Permission Denied
                        GetQRCodeFragment.getIMEI(false, Utilities.getInstance(this), getApplicationContext(),
                                WelcomeActivity.this);
                    }

                    break;
                default:
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                    break;
            }
        } catch (Exception e) {

        }

    }


    @Override
    public void onBackPressed() {
        Utilities.getInstance(this).showAlert(this, new Utilities.onAlertOkListener() {
            @Override
            public void onOkButtonClicked(String tag) {

                finishAffinity();
            }
        }, Html.fromHtml(getResources().getString(R.string.exit_msg)), getResources().getString(R.string.txtAlertTitleGreatAlert), "No", "Yes", "Sync");
    }


    public void checkingDeviceActivity(){
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
        }catch (Exception e)
        {e.printStackTrace();}

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
