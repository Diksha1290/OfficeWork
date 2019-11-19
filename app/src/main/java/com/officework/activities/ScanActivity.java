package com.officework.activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.officework.R;
import com.officework.Services.OnClearFromRecentService;
import com.officework.constants.AsyncConstant;
import com.officework.constants.Constants;
import com.officework.constants.JsonTags;
import com.officework.interfaces.WebServiceCallback;
import com.officework.testing_profiles.realmDatabase.operationManager.RealmOperations;
import com.officework.testing_profiles.utils.ConstantTestIDs;
import com.officework.utils.Utilities;
import com.officework.utils.WebserviceUrls;
import com.officework.utils.permissionhelper.PermissionCallbacks;
import com.officework.utils.permissionhelper.PermissionHelper;
import com.officework.utils.socket.SocketConstants;
import com.officework.utils.socket.SocketHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import io.socket.client.IO;

import static com.officework.constants.Constants.REQ_CODE_DEFAULT_SETTINGS;

public class ScanActivity extends BaseActivity implements PermissionCallbacks, WebServiceCallback {
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private static final int SCAN_REQUEST_CODE = 789;
    private static final int DIAGNOSE_REQUEST_CODE = 45;
    private static final int SETTING_REQUEST_CODE = 132;
    Utilities utils;
    Context ctx;
    FrameLayout frame;
    AlertDialog mAlertDialog;
    boolean isShowing;
    SensorManager sensorManager;
    boolean faceexists = true;
    boolean isFingerprintExist = true;
    private AlertDialog.Builder builder;
    private IntentIntegrator integrator;
    private String androidId, diagnosresult;
    private RealmOperations realmOperations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scan);
        frame = findViewById(R.id.frame);
        checkRunTimeCameraPermssion();
        ctx = getApplicationContext();
        utils = Utilities.getInstance(this);
        startService(new Intent(getBaseContext(), OnClearFromRecentService.class));
//        Utilities.getInstance(ScanActivity.this).serviceCalls(ScanActivity.this,
//                WebserviceUrls.GetDiagnosticTests + "/" + Utilities.getInstance(ScanActivity.this).getPreference(ScanActivity.this,
//                        SubscriberProductID.name(), ""), false, "", false, 2,
//                ((ScanActivity.this)), false);
        realmOperations = new RealmOperations();
        realmOperations.resetTestStatus();
        utils.loadDatabase(this);
        checkSensors();
    }


    private void fetchAndroidId() {

       // Constants.isDoAllClicked = false;
        if (Utilities.getInstance(ScanActivity.this).getPreference(ScanActivity.this,
                Constants.ANDROID_ID, "").isEmpty()) {
            //  androidId = Settings.Secure.getString(getActivity().getContentResolver(),
            //  Settings.Secure.ANDROID_ID);
            androidId = UUID.randomUUID().toString();
            Utilities.getInstance(ScanActivity.this).addPreference(ScanActivity.this,
                    Constants.ANDROID_ID, androidId);

        } else {
//            String android_Id =
//                    Utilities.getInstance(ScanActivity.this).getPreference(ScanActivity.this,
//                    Constants.ANDROID_ID, "");
//            String qrCodeID =
//                    Utilities.getInstance(ScanActivity.this).getPreference(ScanActivity.this,
//                            Constants.QRCODEID, "");
//            String udi = Utilities.getInstance(ScanActivity.this).getPreference(ScanActivity.this,
//                    JsonTags.UDI.name(), "");
//            String subscribeID =
//                    Utilities.getInstance(ScanActivity.this).getPreference(ScanActivity.this,
//                    SubscriberProductID.name(), "");

//            Boolean isSaveApiCall= Utilities.getInstance(ScanActivity.this).getPreferenceBoolean(this,Constants.IS_SAVE_API_CALL,false);
//            String accesstoken =
//                    Utilities.getInstance(ScanActivity.this).getPreference(ScanActivity.this,
//                            JsonTags.access_token.name(), "");
//            String tokentype =
//                    Utilities.getInstance(ScanActivity.this).getPreference(ScanActivity.this,
//                            JsonTags.token_type.name(), "");

          //  Log.d("scan before android id", android_Id);
         //   Utilities.getInstance(ScanActivity.this).clearPreferences(ScanActivity.this);
//            Utilities.getInstance(ScanActivity.this).addPreference(ScanActivity.this,
//                    Constants.ANDROID_ID, android_Id);
//
//            Utilities.getInstance(ScanActivity.this).addPreferenceBoolean(ScanActivity.this,Constants.IS_SAVE_API_CALL,isSaveApiCall);
//            Utilities.getInstance(ScanActivity.this).addPreference(ScanActivity.this,
//                    Constants.QRCODEID, qrCodeID);
//
//            Utilities.getInstance(ScanActivity.this).addPreference(ScanActivity.this,
//                    JsonTags.access_token.name(), accesstoken);
//            Utilities.getInstance(ScanActivity.this).addPreference(ScanActivity.this,
//                    JsonTags.token_type.name(), tokentype);
//
//            Utilities.getInstance(ScanActivity.this).addPreference(ScanActivity.this,
//                    JsonTags.UDI.name(), udi);
//            Utilities.getInstance(ScanActivity.this).addPreference(ScanActivity.this,
//                    SubscriberProductID.name(), subscribeID);

            this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        }

    }
    private BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            utils.addPreference(ctx, JsonTags.MMR_68.name(), String.valueOf(level) + "%");

        }
    };
    private void checkSensors() {
        sensorManager = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
        Sensor LightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (LightSensor == null) {
            utils.addPreferenceBoolean(ctx,
                    ConstantTestIDs.LIGHT_SENSOR_ID + Constants.IS_TEST_EXIST, true);
            utils.addPreferenceInt(ctx, JsonTags.MMR_26.name(), AsyncConstant.TEST_NOT_EXIST);
            utils.compare_UpdatePreferenceInt(ctx, "MMR_26", AsyncConstant.TEST_NOT_EXIST);
            realmOperations.updateTestResult(ConstantTestIDs.LIGHT_SENSOR_ID,
                    AsyncConstant.TEST_NOT_EXIST);

        }
        Sensor proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if (proximitySensor == null) {
            utils.addPreferenceBoolean(ctx,
                    ConstantTestIDs.PROXIMITY_ID + Constants.IS_TEST_EXIST, true);
            utils.addPreferenceInt(ctx, JsonTags.MMR_22.name(), AsyncConstant.TEST_NOT_EXIST);
            utils.compare_UpdatePreferenceInt(ctx, "MMR_22", AsyncConstant.TEST_NOT_EXIST);
            realmOperations.updateTestResult(ConstantTestIDs.PROXIMITY_ID,
                    AsyncConstant.TEST_NOT_EXIST);

        }
        Sensor mMagnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (mMagnetometer == null) {
            utils.addPreferenceBoolean(ctx, ConstantTestIDs.COMPASS + Constants.IS_TEST_EXIST,
                    true);
            utils.addPreferenceInt(ctx, JsonTags.MMR_27.name(), AsyncConstant.TEST_NOT_EXIST);
            utils.compare_UpdatePreferenceInt(ctx, "MMR_27", AsyncConstant.TEST_NOT_EXIST);
            realmOperations.updateTestResult(ConstantTestIDs.COMPASS, AsyncConstant.TEST_NOT_EXIST);

        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            isFingerprintExist = false;
            utils.addPreferenceBoolean(ctx, ConstantTestIDs.FINGERPRINT + Constants.IS_TEST_EXIST
                    , true);
            utils.addPreferenceInt(ctx, JsonTags.MMR_57.name(), AsyncConstant.TEST_NOT_EXIST);
            utils.compare_UpdatePreferenceInt(ctx, "MMR_57", AsyncConstant.TEST_NOT_EXIST);
            realmOperations.updateTestResult(ConstantTestIDs.FINGERPRINT,
                    AsyncConstant.TEST_NOT_EXIST);

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            FingerprintManager fingerprintManager =
                    (FingerprintManager) ctx.getSystemService(FINGERPRINT_SERVICE);
            if ((fingerprintManager == null || !fingerprintManager.isHardwareDetected())) {
                isFingerprintExist = false;
                utils.addPreferenceBoolean(ctx,
                        ConstantTestIDs.FINGERPRINT + Constants.IS_TEST_EXIST, true);
                utils.addPreferenceInt(ctx, JsonTags.MMR_57.name(), AsyncConstant.TEST_NOT_EXIST);
                utils.compare_UpdatePreferenceInt(ctx, "MMR_57", AsyncConstant.TEST_NOT_EXIST);
                realmOperations.updateTestResult(ConstantTestIDs.FINGERPRINT,
                        AsyncConstant.TEST_NOT_EXIST);

            }
        }
        Sensor barrometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        if (barrometerSensor == null) {
            utils.addPreferenceBoolean(ctx, ConstantTestIDs.Barometer + Constants.IS_TEST_EXIST,
                    true);
            utils.addPreferenceInt(ctx, JsonTags.MMR_63.name(), AsyncConstant.TEST_NOT_EXIST);
            utils.compare_UpdatePreferenceInt(ctx, "MMR_63", AsyncConstant.TEST_NOT_EXIST);
            realmOperations.updateTestResult(ConstantTestIDs.Barometer,
                    AsyncConstant.TEST_NOT_EXIST);
        }
        int v = 0;
        try {
            v = ctx.getPackageManager().getPackageInfo(GoogleApiAvailability.GOOGLE_PLAY_SERVICES_PACKAGE, 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        FaceDetector detector = new FaceDetector.Builder(ctx)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();
        if (!detector.isOperational()) {
            faceexists = false;
        }
        if (7899470 > v || !faceexists) {
            //isFaceDetectionExist = false;
            utils.addPreferenceBoolean(ctx,
                    ConstantTestIDs.FaceDetection + Constants.IS_TEST_EXIST, true);
            utils.addPreferenceInt(ctx, JsonTags.MMR_64.name(), AsyncConstant.TEST_NOT_EXIST);
            utils.compare_UpdatePreferenceInt(ctx, "MMR_64", AsyncConstant.TEST_NOT_EXIST);
            realmOperations.updateTestResult(ConstantTestIDs.FaceDetection,
                    AsyncConstant.TEST_NOT_EXIST);
        }


    }



    private void checkRunTimeCameraPermssion() {
        String[] perms = {Manifest.permission.CAMERA};
        final String stringRationale = "Need To Access Device Camera";
        if (PermissionHelper.hasPermissions(ScanActivity.this, perms)) {
            startScan();
        } else {
            PermissionHelper.requestPermissions(ScanActivity.this, stringRationale,
                    MY_CAMERA_REQUEST_CODE, perms);
        }
    }

    @Override

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionHelper.onRequestPermissionsResult(this, requestCode, permissions, grantResults,
                this);
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    private void startScan() {
        try {
            if (Utilities.getInstance(this).getPreference(this, Constants.ANDROID_ID, "").isEmpty()) {
                //  androidId = Settings.Secure.getString(getActivity().getContentResolver(),
                //  Settings.Secure.ANDROID_ID);
                androidId = UUID.randomUUID().toString();
                Utilities.getInstance(this).addPreference(this, Constants.ANDROID_ID, androidId);
            } else {
                androidId = Utilities.getInstance(this).getPreference(this, Constants.ANDROID_ID, "");
            }
            if (Utilities.getInstance(ScanActivity.this).isInternetWorking(this)) {
                try {
                    String androidId = Utilities.getInstance(this).getPreference(this,
                            Constants.ANDROID_ID, "");
                    String qrCodeiD = Utilities.getInstance(this).getPreference(this,
                            Constants.QRCODEID, "");
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

            checkConnection();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void checkConnection() {
        if (Utilities.getInstance(ScanActivity.this).isInternetWorking(this)) {
            if (integrator == null) {
                Constants.isPagerElementTwoVisibleManual = false;

                integrator = new IntentIntegrator(ScanActivity.this);

                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                integrator.setPrompt("");
                integrator.setBeepEnabled(true);
                integrator.setOrientationLocked(true);

                integrator.setBarcodeImageEnabled(true);
                integrator.setCaptureActivity(ToolbarCaptureActivity.class).initiateScan();


            } else {
                Toast.makeText(this, "integrator null", Toast.LENGTH_SHORT);
            }
        } else {

            showSettingDialog();

        }
    }

    private void showSettingDialog() {
        Utilities.getInstance(ScanActivity.this).showAlert(ScanActivity.this,
                new Utilities.onAlertOkListener() {
                    @Override
                    public void onOkButtonClicked(String tag) {

                        startActivityForResult(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS)
                                , 89);

                    }
                }, getResources().getString(R.string.please_check_internet_connection),
                getResources().getString(R.string.internet_connection), "Open Settings", "",
                "Permission");

    }

    @Override
    protected void setUpLayout() {

    }

    @Override
    protected void setDataInViewObjects() {

    }

    @Override
    protected void setUpToolBar() {

    }

    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            switch (requestCode) {

                case REQ_CODE_DEFAULT_SETTINGS:
                    checkRunTimeCameraPermssion();
                    break;

                case DIAGNOSE_REQUEST_CODE:

                    integrator = null;

                    startScan();

                    break;

                case 89:
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            integrator = null;

                            startScan();

                        }
                    }, 1000);
                    break;

                default:
                    try {
                    if (resultCode == RESULT_OK) {
                        IntentResult result = IntentIntegrator.parseActivityResult(requestCode,
                                resultCode, data);
                        if (result != null) {
                            //if qrcode has nothing in it
                            if (result.getContents() == null) {
                                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
                            } else {
                                //if qr contains data

                                //if control comes here
                                //that means the encoded format not matches
                                //in this case you can display whatever data is available on the qrcode
                                //to a toast

                              try {
                                  Uri uri = Uri.parse(result.getContents());
                                  Set<String> keys = uri.getQueryParameterNames();
                                  if(keys!= null && keys.size()>0&& keys.contains(Constants.ENTERPRISEPATNERID) && keys.contains(Constants.STOREID)){





                                     String  enterprisePartnerID = uri.getQueryParameter(Constants.ENTERPRISEPATNERID).trim();
                                     String storeID = uri.getQueryParameter(Constants.STOREID);


                                      Utilities.getInstance(ScanActivity.this).addPreference(ScanActivity.this, Constants.QRCODEID, storeID);
                                      Utilities.getInstance(ScanActivity.this).addPreference(ScanActivity.this, Constants.STOREID, storeID);
                                      Utilities.getInstance(ScanActivity.this).addPreference(ScanActivity.this, Constants.ENTERPRISEPATNERID, enterprisePartnerID);

                                      if (Utilities.getInstance(ScanActivity.this).isInternetWorking(ScanActivity.this)) {
                                          Intent intent = new Intent(ScanActivity.this, CheckingDeviceActivity.class);
//                                          intent.putExtra(QR_CODE_RESULT, result.getContents());
                                          startActivityForResult(intent, DIAGNOSE_REQUEST_CODE);
                                          unregisterReceiver(this.mBatInfoReceiver);
                                          this.finish();
                                      } else {
                                          showSettingDialog();
                                      }
                                  } else {
                                      Toast.makeText(getApplicationContext(), "Wrong QR Code", Toast.LENGTH_LONG).show();
                                      unregisterReceiver(this.mBatInfoReceiver);
                                      finish();

                                  }
                              }catch (Exception e){
                                  e.printStackTrace();
                              }

                            }
                        } else {
                            startScan();
                            Toast.makeText(this, "Result Not Found.....", Toast.LENGTH_LONG).show();

                            super.onActivityResult(requestCode, resultCode, data);
                        }
                    } else {
                        finish();
                    }
            }catch (Exception e)
                    {
                        e.printStackTrace();
                        Log.e("cvbx",e.getMessage());
                    }
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        String[] parmList = {Manifest.permission.CAMERA};
        final String stringRationale = "Need To Access Device Camera";
        PermissionHelper.requestPermissions(ScanActivity.this, stringRationale,
                MY_CAMERA_REQUEST_CODE, parmList);
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        startScan();
        Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onServiceResponse(boolean serviceStatus, String response, int callbackID) {

        if (serviceStatus) {

            try {
                JSONObject responseObj = new JSONObject(response);
                JSONObject data = responseObj.getJSONObject("Data");
                JSONArray DiagnosticTestsList = data.getJSONArray("DiagnosticTestsList");
                diagnosresult = DiagnosticTestsList.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            Answers.getInstance().logCustom(new CustomEvent("Diagonestictestlist_error")
                    .putCustomAttribute("response",response));
        }
    }


}
