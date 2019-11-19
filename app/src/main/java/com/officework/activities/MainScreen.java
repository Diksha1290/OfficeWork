package com.officework.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.officework.R;
import com.officework.constants.AppConstantsTheme;
import com.officework.constants.Constants;
import com.officework.constants.JsonTags;
import com.officework.fragments.GetQRCodeFragment;
import com.officework.interfaces.WebServiceCallback;
import com.officework.utils.Utilities;
import com.officework.utils.WebserviceUrls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainScreen extends AppCompatActivity implements WebServiceCallback {
    LinearLayout linearLayout, linearLayoutB2C;
    int SERVICE_REQUEST_ID = -1;
    JSONObject jsonObject;
    int[] static_Array;
    ThemeManager themeManager;
    private  static final int REQUEST_CAMERA_PERMISSION=201;
    private IntentIntegrator qrScan;
    String qrCodeData;
    String SubscriberCode;
    String username;
    String password;
    String grant_type;
    String SubscriberProductID;
    Utilities utils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        linearLayout = findViewById(R.id.linearLayout);
        linearLayoutB2C = findViewById(R.id.linearLayoutB2C);
        Intent intent = getIntent();
        themeManager = new ThemeManager(MainScreen.this);
        qrScan = new IntentIntegrator(this);
        utils = Utilities.getInstance(this);
        linearLayoutB2C.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                AppConstantsTheme.theme = AppConstantsTheme.Theme.BROWN;
//                AppConstantsTheme.setStyleTheme();

                Intent intent1 = new Intent(MainScreen.this,MainActivity.class);
               startActivity(intent1);
                String themeColor="#1F4999";
                //String iconColor="#7F2B2680";
                String iconColor="#1F4999";
                int iconcolorValue = Color.parseColor(iconColor);
                int colorValue=Color.parseColor((themeColor));
                AppConstantsTheme.setColor(iconcolorValue,colorValue,R.color.white,colorValue,colorValue);
                themeManager.setIconColor(iconColor);
                themeManager.setTheme(themeColor);
                Constants.isDoAllClicked=false;

            }
        });

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Constants.isDoAllClicked=false;

                    if (ActivityCompat.checkSelfPermission(MainScreen.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        startScanner();
//                        Intent intent1=new Intent(MainScreen.this,QrCodeScanActivity.class);
//                        startActivity(intent1);
                    } else {
                        ActivityCompat.requestPermissions(MainScreen.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                        if (ActivityCompat.checkSelfPermission(MainScreen.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            startScanner();
//                            Intent intent1=new Intent(MainScreen.this,QrCodeScanActivity.class);
//                            startActivity(intent1);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }



            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
               // Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                try {
                    //converting the data to json

                  //  JSONArray jsonArray= new JSONArray(String.valueOf(result.getContents()));

                    JSONObject jsonObject=new JSONObject(String.valueOf(result.getContents()));
                  //  JSONObject jsonObject = jsonArray.getJSONObject(0);
                    qrCodeData=jsonObject.getString("DiagnosticProfile");
                    SubscriberCode=jsonObject.getString("SubscriberCode");
                    //setting values to textviews
                    username=jsonObject.getString("SubscriberCode");
                    password=jsonObject.getString("SubscriberProductCode");
                    SubscriberProductID=jsonObject.getString("SubscriberProductID");
                    grant_type="password";
                    //utils.addPreference(getApplicationContext(), JsonTags.SubscriberProductID.name(), SubscriberProductID);
                   barcodeRequest();

                } catch (JSONException e) {
                    e.printStackTrace();
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void getToken() {
        try {
           // GetQRCodeFragment.getDeviceInfoData(utils, this, getApplicationContext(), (InterfaceBatteryCallback) this);
            if (utils.isInternetWorking(getApplicationContext())) {
                SERVICE_REQUEST_ID = 0;

//                utils.serviceCalls(SplashActivity.this, WebserviceUrls.newToken, true, GetQRCodeFragment.createJson2(utils, getApplicationContext()).toString(), true, SERVICE_REQUEST_ID,
//                        (WebServiceCallback) this, true);
                utils.serviceCalls(MainScreen.this, WebserviceUrls.newToken, true,"grant_type="+grant_type+"&UserName="+username+"&Password="+password , true, SERVICE_REQUEST_ID,
                        (WebServiceCallback) this, true);

//                utils.serviceCalls(SplashActivity.this, WebserviceUrls.DiagnosticDataTokenURL, true, GetQRCodeFragment.createJson(utils, getApplicationContext()).toString(), true, SERVICE_REQUEST_ID,
//                        (WebServiceCallback) this, true);

            } else {

            }
        } catch (Exception e) {

        }


    }
    private void postDiagnosticsData() {
        try {
            if (utils.isInternetWorking(getApplicationContext())) {
                SERVICE_REQUEST_ID = 1;
//                utils.serviceCalls(SplashActivity.this, WebserviceUrls.DiagnosticDataURL, true, GetQRCodeFragment.createJson(utils, getApplicationContext()).toString(), true, SERVICE_REQUEST_ID,
//                        (WebServiceCallback) this, false);
                utils.serviceCalls(MainScreen.this, WebserviceUrls.GenerateUdid, false,SubscriberProductID, true, SERVICE_REQUEST_ID,
                        (WebServiceCallback) this, false);
            }
        } catch (Exception e) {

        }

    }

    public void barcodeRequest(){

//        if (Utilities.isInternetWorking(MainScreen.this)) {
//            Utilities.getInstance(MainScreen.this).serviceCalls(MainScreen.this, WebserviceUrls.TestUrl + "/" +SubscriberCode+"/"+ qrCodeData , false, "", true, SERVICE_REQUEST_ID, ((MainScreen.this)), false);
//        }
        if (Utilities.isInternetWorking(MainScreen.this)) {
            SERVICE_REQUEST_ID = 2;
            Utilities.getInstance(MainScreen.this).serviceCalls(MainScreen.this, WebserviceUrls.GetDiagnosticTests + "/"+SubscriberProductID , false, "", true, SERVICE_REQUEST_ID, ((MainScreen.this)), false);
        }

    }
    @Override
    public void onServiceResponse(boolean serviceStatus, String response, int callbackID) {

        if (serviceStatus) {

            try {
                JSONObject responseObj = new JSONObject(response);
                switch (callbackID) {
                    case 0:
                        utils.addPreference(getApplicationContext(), JsonTags.access_token.name(), responseObj.getString(JsonTags.access_token.name()));
                        utils.addPreference(getApplicationContext(), JsonTags.token_type.name(), responseObj.getString(JsonTags.token_type.name()));
                        GetQRCodeFragment.unRegisterBatteryReceiver(MainScreen.this);
                        postDiagnosticsData();
                        break;
                    case 1:
                        if (responseObj.getBoolean("IsSuccess")){
                            JSONObject data= responseObj.getJSONObject("Data");
                            String UDI= data.getString(JsonTags.UDI.name());
                            utils.addPreference(getApplicationContext(), JsonTags.UDI.name(),UDI);
                            barcodeRequest();
                        }

//                        if (responseObj.getBoolean(JsonTags.result.name())) {
//                            utils.addPreference(getApplicationContext(), JsonTags.udi.name(), responseObj.getString(JsonTags.udi.name()));
//                            utils.addPreference(getApplicationContext(), JsonTags.refcode.name(), responseObj.getString(JsonTags.refcode.name()));
//                            utils.addPreferenceLong(getApplicationContext(), JsonTags.ttl.name(), responseObj.getLong(JsonTags.ttl.name()));
//                            if (!utils.getPreference(getApplicationContext(), JsonTags.udi.name(), "").equalsIgnoreCase("")) {
//                                /*generateBarCode(utils.getPreference(getApplicationContext(), JsonTags.udi.name(), ""));*/
//                                PreferenceConstants.udi = responseObj.getString(JsonTags.udi.name());
//                                PreferenceConstants.imageBitmap = null;
//                                utils.addPreferenceBoolean(getApplicationContext(), JsonTags.isTestDataChanged.name(), false);
//                                barcodeRequest();
//                            }
                       // }
                        else {
                            utils.addPreference(getApplicationContext(), JsonTags.udi.name(), "");
                            barcodeRequest();


                        }

                    case 2:
                        //JSONObject jsonObject = new JSONObject(response);

                        JSONObject data=responseObj.getJSONObject("Data");
                        JSONArray DiagnosticTestsList=data.getJSONArray("DiagnosticTestsList");

//                        responseObj = jsonObject.getJSONObject("SubscriberDetails");
//                        String color = responseObj.getString("Color");
//
//
//                        JSONArray jsonArray = jsonObject.getJSONArray("lstTestSequence");


                        // AppConstantsTheme.setIconColor("#"+color);
                        Toast.makeText(MainScreen.this, "Scanned Successfully", Toast.LENGTH_SHORT).show();

//                String themeColor="#"+color;
//                String iconColor="#"+color;
//                int colorValue = Color.parseColor(themeColor);
//                int iconColorValue = Color.parseColor(iconColor);
//                AppConstantsTheme.setColor(iconColorValue,colorValue,R.color.White,colorValue,colorValue);
//                themeManager.setTheme(themeColor);
//                themeManager.setIconColor(iconColor);


                        Intent intent = new Intent(MainScreen.this, MainActivity.class);
                        intent.putExtra("jsonArray", DiagnosticTestsList.toString());
                        startActivity(intent);

                        break;


                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else {
            Answers.getInstance().logCustom(new CustomEvent("MainScreen_Api_error")
                    .putCustomAttribute("response",response));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            String permission = permissions[0];

           if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
               startScanner();
//               Intent intent1=new Intent(MainScreen.this,QrCodeScanActivity.class);
//               startActivity(intent1);
           }

            else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                boolean showRationale = shouldShowRequestPermissionRationale(permission);
                if (!showRationale) {

                    new AlertDialog.Builder(MainScreen.this)
                            .setTitle("Permission Needed")
                            .setMessage("Click ok to go to the app settings")
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create().show();


                } else if (Manifest.permission.CAMERA.equals(permission)) {
                    //showRationale(permission, R.string.permission_denied_contacts);
                    new AlertDialog.Builder(MainScreen.this)
                            .setTitle("Permission Needed")
                            .setMessage("Permission is needed to access camera")
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(MainScreen.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                                }
                            })
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create().show();

                }

            }
        }
    }
    public void startScanner(){
    try {

        if (Utilities.isInternetWorking(MainScreen.this)) {
               qrScan.initiateScan();
        }
        }
    catch (Exception e){e.printStackTrace();}}
    }


