package com.officework.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.face.FaceDetector;
import com.officework.R;
import com.officework.activities.MainActivity;
import com.officework.base.BaseFragment;
import com.officework.broadcastReceivers.NetworkChangeReceiver;
import com.officework.constants.AsyncConstant;
import com.officework.constants.Constants;
import com.officework.constants.FragmentTag;
import com.officework.constants.JsonTags;
import com.officework.constants.PreferenceConstants;
import com.officework.databasehelper.DatabaseHelper;
import com.officework.interfaces.InterfaceAlertDissmiss;
import com.officework.interfaces.InterfaceBatteryCallback;
import com.officework.interfaces.InterfaceButtonTextChange;
import com.officework.interfaces.InterfaceNetworkChange;
import com.officework.interfaces.WebServiceCallback;
import com.officework.models.LogExceptionModel;
import com.officework.testing_profiles.Controller.TestController;
import com.officework.testing_profiles.realmDatabase.operationManager.RealmOperations;
import com.officework.testing_profiles.utils.ConstantTestIDs;
import com.officework.utils.TestResultUpdateToServer;
import com.officework.utils.Utilities;
import com.officework.utils.WebserviceUrls;

import org.json.JSONObject;

import java.util.List;

import static android.content.Context.FINGERPRINT_SERVICE;

/**
 * Created by Girish on 7/28/2016.
 */
@SuppressLint("ValidFragment")
public class DashBoardFragment extends BaseFragment implements AdapterView.OnItemClickListener, View.OnClickListener,
        InterfaceNetworkChange, InterfaceBatteryCallback, InterfaceAlertDissmiss ,WebServiceCallback{
    View view;
    Utilities utils;
    Context ctx;
    LinearLayout mLayoutDeviceInfo, mLayoutAutomatedTest, mLayoutManualTest, mLayoutQRCode, layoutBarCodeVisibilty, mLayoutBarCode, mLayoutCompleteOrder;

    private LinearLayout mDotsLayout;
    private int mDotsCount;
    static TextView mDotsText[];
    ImageView imgViewBarCode;
    TextView tvBarCode;
    ImageView mImgViewSyncData;
    TextView mTxtViewSyncData, mTxtViewShowServerTime, txtViewRandomText, txtViewOr, txtViewShowTimer;
    ProgressDialog progressBarAccept;
    final public int REQUEST_CODE_IMEI_PERMISSION = 504;
    MainActivity mainActivity;
    boolean isRequestPermission = false;
    Button btnGetNew;
    TextView txtViewTermCondition;


    private int mPosition = 0;
    InterfaceButtonTextChange mCallBack;
    Display display;
    NetworkChangeReceiver networkChangeReceiver;
    private boolean isReceiverRegistered = false;
    TestResultUpdateToServer testResultUpdateToServer;
    int SERVICE_REQUEST_ID = -1;
    NavigationDrawerFragment mNavigationDrawerFragment;

    BroadcastReceiver mRegistrationBroadcastReceiver;
    DatabaseHelper databaseHelper;
    RealmOperations realmOperations;
    SensorManager sensorManager;
    boolean faceexists=true;

    public DashBoardFragment(InterfaceButtonTextChange callback) {
        mCallBack = callback;
    }

    public DashBoardFragment() {

    }

    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        try {
            if (view == null) {
                view = inflater.inflate(R.layout.fragment_dashboard_new_ui, null);
                utils = Utilities.getInstance(getActivity());
                ctx = getActivity();
                realmOperations=new RealmOperations();

                networkChangeReceiver = new NetworkChangeReceiver(ctx, (InterfaceNetworkChange) this);
                testResultUpdateToServer = new TestResultUpdateToServer(utils, ctx, getActivity());
            //    Crashlytics.getInstance().log(FragmentTag.DASHBOARD_FRAGMENT.name());
                mNavigationDrawerFragment = new NavigationDrawerFragment();
                mainActivity = new MainActivity();
                databaseHelper = new DatabaseHelper(ctx);
                initViews();


            }
            return view;
        } catch (Exception e) {
            logException(e, "DashBoardFragment_initUI()");
            return null;
        }

    }

    public void onBackPress() {
        try {
            utils.showAlert(ctx, (InterfaceAlertDissmiss) this, getResources().getString(R.string.quit_app),
                    "", getResources().getString(R.string.Yes),
                    getResources().getString(R.string.No), 1);

        } catch (Exception e) {
            logException(e, "DashBoardFragment_onBackPress()");
        }

    }

    private void initViews() {
        try {

            mLayoutAutomatedTest = (LinearLayout) view.findViewById(R.id.layoutAutomatedTest);
            mLayoutAutomatedTest.setOnClickListener(this);
            mLayoutManualTest = (LinearLayout) view.findViewById(R.id.layoutManualTest);
            mLayoutManualTest.setOnClickListener(this);
            mLayoutBarCode = (LinearLayout) view.findViewById(R.id.layoutGetBarCode);
            mLayoutBarCode.setOnClickListener(this);

            chechDattIfReset();

        } catch (Exception e) {
            logException(e, "DashBoardFragment_initViews()");
        }


    }

    private void chechDattIfReset() {
       if( utils.getPreference(ctx, JsonTags.UDI.name(), "").equalsIgnoreCase("")){
           utils.addPreference(getActivity(), JsonTags.access_token.name(),PreferenceConstants.Token);
           utils.addPreference(getActivity(), JsonTags.token_type.name(),PreferenceConstants.TokenType);
           utils.addPreference(getActivity(),JsonTags.SubscriberProductID.name(),PreferenceConstants.SubscriberProductID);
           utils.addPreference(getActivity(), JsonTags.UDI.name(),PreferenceConstants.Udid);
           utils.addPreferenceBoolean(getActivity(), JsonTags.isUserDeclined.name(), PreferenceConstants.isUserDeclined);

           GetQRCodeFragment.getIMEI(true, Utilities.getInstance(getActivity()), getActivity(), getActivity());

           GetQRCodeFragment.getDeviceInfoData(Utilities.getInstance(getActivity()), getActivity(), getActivity(), (InterfaceBatteryCallback) this);

           testResultUpdateToServer.updateTestResultToServer(true, 0,1);

           // fetchUdid();
       }


    }

    private void fetchUdid() {
        try {
            if (utils.isInternetWorking(getActivity())) {
                SERVICE_REQUEST_ID = 1;
//                utils.serviceCalls(SplashActivity.this, WebserviceUrls.DiagnosticDataURL, true, GetQRCodeFragment.createJson(utils, getApplicationContext()).toString(), true, SERVICE_REQUEST_ID,
//                        (WebServiceCallback) this, false);
                utils.serviceCalls(getActivity(), WebserviceUrls.GenerateUdid, false,PreferenceConstants.SubscriberProductID, true, SERVICE_REQUEST_ID,
                        (WebServiceCallback) this, false);
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View arg1, int position, long arg3) {
        super.onItemClick(adapterView, arg1, position, arg3);
        try {
            switch (position) {
                case 0:
                    break;

                case 1:
                    break;

                case 2:
                    // Device Info
                    replaceFragment(R.id.container, new DeviceInfoFragment(), FragmentTag.DEVICE_INFO_FRAGMENT.name(), true);
                    break;

                case 3:
                    break;
            }
        } catch (Exception e) {
            logException(e, "DashBoardFragment_onItemClick()");
        }

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        try {
            switch (v.getId()) {
                case R.id.layoutDeviceInfoFrag:
                    try {
                        // Device Info
                        if (!utils.getPreferenceBoolean(ctx, JsonTags.isUserDeclined.name(), false)) {
                            replaceFragment(R.id.container, new DeviceInfoFragment(), FragmentTag.DEVICE_INFO_FRAGMENT.name(), true);
                        }
                        else {
                            /*showDeviceInfoClickCustomDialog();*/
                            utils.showAlert(ctx, (InterfaceAlertDissmiss) this,  Html.fromHtml(getResources().getString(R.string.txtAlertTermsAndCondition)),
                                    getResources().getString(R.string.txtWelcometradeinpro_beta), getResources().getString(R.string.txtAccept),
                                    getResources().getString(R.string.txtDecline), 3);

                        }
                        break;
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                case R.id.layoutAutomatedTest:

                    ManualTestFragment firstmanualTestFragment = new ManualTestFragment((InterfaceButtonTextChange) getActivity());
                    Bundle firstbundle = new Bundle();
                    firstbundle.putInt("Position",0);
                    firstmanualTestFragment.setArguments(firstbundle);
                    replaceFragment(R.id.container, firstmanualTestFragment, FragmentTag.MANUAL_TEST_FRAGMENT.name(), true);
                    break;
                case R.id.layoutManualTest:

                    ManualTestFragment secondManualTestFragment = new ManualTestFragment((InterfaceButtonTextChange) getActivity());
                    Bundle secondBundle = new Bundle();
                    secondBundle.putInt("Position",1);
                    secondManualTestFragment.setArguments(secondBundle);
                    /*replaceFragment(R.id.container, new ManualTestFragment((InterfaceButtonTextChange) getActivity()), FragmentTag.MANUAL_TEST_FRAGMENT.name(), true);*/
                    replaceFragment(R.id.container, secondManualTestFragment, FragmentTag.MANUAL_TEST_FRAGMENT.name(), true);
                    break;
                case R.id.layoutGetBarCode:
                    if ((!utils.getPreference(ctx, JsonTags.UDI.name(), "").equalsIgnoreCase(""))) {
                        ManualTestFragment getBarCodeUDIFragment = new ManualTestFragment((InterfaceButtonTextChange) getActivity());
                        Bundle barCodeBundle = new Bundle();
                        barCodeBundle.putInt("Position",3);
                        getBarCodeUDIFragment.setArguments(barCodeBundle);
                        replaceFragment(R.id.container, getBarCodeUDIFragment, FragmentTag.GETQRCODE_FRAGMENT.name(), true);
                    } else {

                        utils.showToast(ctx, getResources().getString(R.string.txtInternetIssue));
                    }

                    break;
                case R.id.layoutGetQRCode:
                    clearAllStack();
                    replaceFragment(R.id.container, new GetQRCodeFragment(), FragmentTag.GETQRCODE_FRAGMENT.name(), true);

                    break;
                case R.id.btnGetNew:
                    btnGetNew.setVisibility(View.INVISIBLE);
                    break;

            }
        } catch (Exception e) {
            logException(e, "DashBoardFragment_onClick()");
        }

    }

    /**
     * Here change the header text
     ***/
    @Override
    public void onResume() {
        try {
            TitleBarFragment fragment = (TitleBarFragment) getFragment(R.id.headerContainer);
            if (fragment != null) {
                fragment.setTitleBarVisibility(true);

                fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string.app_name), true, false, 0);
                if (mCallBack != null) {
                    mCallBack.onChangeText(utils.BUTTON_NEXT, false);
                }

            }

        } catch (Exception e) {
            logException(e, "DashBoardFragment_onResume()");
        }


        super.onResume();
    }


    @Override
    public void onPause() {

        Log.d("OnPause", "Called");
        super.onPause();


    }



    @Override
    public void onNetworkChange(Context context, boolean isChanged) {
        try {
            if (isChanged) {
                if (utils.getPreferenceBoolean(ctx, JsonTags.isTestDataChanged.name(), false)) {
             /*   testResultUpdateToServer.updateTestResult((WebServiceCallback)this);*/
                    updateTestResult();


                }
            } else {
           /* Toast.makeText(context, "Disconnected", Toast.LENGTH_LONG).show();*/
            }
        } catch (Exception e) {
            logException(e, "DashBoardFragment_onNetworkChange()");
        }


    }

    public void updateTestResult() {
        try {
           // getToken();
        } catch (Exception e) {
            logException(e, "DashBoardFragment_updateTestResult()");
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TitleBarFragment fragment = (TitleBarFragment) getFragment(R.id.headerContainer);
        if (fragment != null) {
//            fragment.setTitleBarVisibility(true);
      //      fragment.showHomeButton(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();


        TitleBarFragment fragment = (TitleBarFragment) getFragment(R.id.headerContainer);
        if (fragment != null) {
//            fragment.setTitleBarVisibility(true);
//            fragment.showHomeButton(View.GONE);
        }
    }


    @Override
    public void onServiceResponse(boolean serviceStatus, String response, int callbackID) {
        /*GetQRCodeFragment.batteryInfoReceiver = null;*/

                if (serviceStatus) {
                    try {
                        JSONObject responseObj = new JSONObject(response);

                        switch (callbackID) {
                            case 1:

                                if (responseObj.getBoolean("IsSuccess")) {
                                    JSONObject data = responseObj.getJSONObject("Data");
                                    String UDI = data.getString(JsonTags.UDI.name());
                                    utils.addPreference(getActivity(), JsonTags.UDI.name(), UDI);
//


                                    break;
                                }

                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
    }


    @Override
    public void onStart() {
        super.onStart();
        TitleBarFragment fragment = (TitleBarFragment) getFragment(R.id.headerContainer);
        if (fragment != null) {
//            fragment.showHomeButton(View.GONE);
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
           // utils.compare_UpdatePreferenceString(ctx, JsonTags.MMR_17.name(),
                  //  intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0) + "%");
                /*BatteryInfoFragment.getBatteryHealthString(intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0), ctx));*/
        } catch (Exception e) {
            logException(e, "DashBoardFragment_onBatteryCallBack()");
        }

    }

    /**
     * Custom Alert View
     */


    /**
     * Check permission
     */
    private void checkRuntimePermisson() {
        try {
            int hasWritePhoneReadPermission = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                hasWritePhoneReadPermission = getActivity().checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
                if (hasWritePhoneReadPermission != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
                            REQUEST_CODE_IMEI_PERMISSION);
                    return;
                }
            }

            GetQRCodeFragment.getIMEI(true, utils, ctx, getActivity());
          //  testResultUpdateToServer.updateTestResult((WebServiceCallback) this, true, 0);
            testResultUpdateToServer.updateTestResultToServer(true, 0,1);

        } catch (Exception e) {
            logException(e, "DashBoardFragment_checkRuntimePermisson()");
        }


    }

    /**
     * Imei Permission
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        try {
            switch (requestCode) {
                case REQUEST_CODE_IMEI_PERMISSION:

                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // Permission Granted

                        GetQRCodeFragment.getIMEI(true, utils, ctx, getActivity());
                    } else {

                        // Permission Denied
                        GetQRCodeFragment.getIMEI(false, utils, ctx, getActivity());
                    }
                  //  testResultUpdateToServer.updateTestResult((WebServiceCallback) this, true, 0);
                    testResultUpdateToServer.updateTestResultToServer(true, 0,1);

                    break;
                default:
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        } catch (Exception e) {
            logException(e, "DashBoardFragment_onRequestPermissionsResult()");
        }

    }

    @Override
    public void onButtonClick(boolean isCanceled, int callbackID) {
        try {
            switch (callbackID) {
                case 3:
                    if (!isCanceled) {
                        if (utils.isInternetWorking(ctx)) {
                            utils.addPreferenceBoolean(ctx, JsonTags.isUserDeclined.name(), false);
                            if (isReceiverRegistered) {
                                isReceiverRegistered = false;
                                isRequestPermission = true;
                                ctx.unregisterReceiver(networkChangeReceiver);
                            }
                            progressBarAccept.show();
                            checkRuntimePermisson();
                        } else {
                            utils.showToast(ctx, getResources().getString(R.string.InternetError));
                        }
                    } else {
                        utils.addPreferenceBoolean(ctx, JsonTags.isUserDeclined.name(), true);
                    }
                    break;
                case 1:
                    if (!isCanceled) {
                        if (Constants.timerCallback != null) {
                            Constants.timerCallback.cancel(true);
                        }
                        utils.addPreference(ctx, JsonTags.refcode.name(), "");
//                        RealmOperations realmOperations=new RealmOperations();
//                        realmOperations.resetTestStatus();
                           resetDiagnostics();

                        try {

                        } catch (Exception e) {
                            logException(e, "Utilities_clearPreferences");
                        }

                        getActivity().finish();
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logException(e, "DashBoardFragment_onButtonClick(boolean isCanceled, int callbackID)");
        }

    }



    public void resetDiagnostics(){

//            utils.clearPreferences(ctx);
            //  utils.clearPreferences(ctx);
            //clearStackList();

            RealmOperations realmOperations=new RealmOperations();
          //  realmOperations.resetTestStatus();
            TestController testController = TestController.getInstance(getActivity());
            testController.nextPressList.clear();
            PreferenceConstants.udi = "";
            checkSensors();
    }
    public void checkSensors() {

        sensorManager = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
        Sensor LightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (LightSensor == null) {
            utils.addPreferenceBoolean(ctx, ConstantTestIDs.LIGHT_SENSOR_ID+Constants.IS_TEST_EXIST, true);
            utils.addPreferenceInt(ctx, JsonTags.MMR_26.name(), AsyncConstant.TEST_NOT_EXIST);
            utils.compare_UpdatePreferenceInt(ctx,"MMR_26",AsyncConstant.TEST_NOT_EXIST);
            realmOperations.updateTestResult(ConstantTestIDs.LIGHT_SENSOR_ID,AsyncConstant.TEST_NOT_EXIST);

        }
        Sensor proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if (proximitySensor == null) {
            utils.addPreferenceBoolean(ctx,ConstantTestIDs.PROXIMITY_ID+Constants.IS_TEST_EXIST, true);
            utils.addPreferenceInt(ctx, JsonTags.MMR_22.name(), AsyncConstant.TEST_NOT_EXIST);
            utils.compare_UpdatePreferenceInt(ctx,"MMR_22",AsyncConstant.TEST_NOT_EXIST);
            realmOperations.updateTestResult(ConstantTestIDs.PROXIMITY_ID,AsyncConstant.TEST_NOT_EXIST);

        }
        Sensor mMagnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (mMagnetometer == null) {
            utils.addPreferenceBoolean(ctx,ConstantTestIDs.COMPASS+Constants.IS_TEST_EXIST, true);
            utils.addPreferenceInt(ctx, JsonTags.MMR_27.name(), AsyncConstant.TEST_NOT_EXIST);
            utils.compare_UpdatePreferenceInt(ctx,"MMR_27",AsyncConstant.TEST_NOT_EXIST);
            realmOperations.updateTestResult(ConstantTestIDs.COMPASS,AsyncConstant.TEST_NOT_EXIST);

        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            utils.addPreferenceBoolean(ctx,ConstantTestIDs.FINGERPRINT+Constants.IS_TEST_EXIST, true);
            utils.addPreferenceInt(ctx, JsonTags.MMR_57.name(), AsyncConstant.TEST_NOT_EXIST);
            utils.compare_UpdatePreferenceInt(ctx,"MMR_57",AsyncConstant.TEST_NOT_EXIST);
            realmOperations.updateTestResult(ConstantTestIDs.FINGERPRINT,AsyncConstant.TEST_NOT_EXIST);

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            FingerprintManager fingerprintManager =
                    (FingerprintManager) ctx.getSystemService(FINGERPRINT_SERVICE);
            if ((fingerprintManager == null || !fingerprintManager.isHardwareDetected()) ) {
                utils.addPreferenceBoolean(ctx,ConstantTestIDs.FINGERPRINT+Constants.IS_TEST_EXIST, true);
                utils.addPreferenceInt(ctx, JsonTags.MMR_57.name(), AsyncConstant.TEST_NOT_EXIST);
                utils.compare_UpdatePreferenceInt(ctx,"MMR_57",AsyncConstant.TEST_NOT_EXIST);
                realmOperations.updateTestResult(ConstantTestIDs.FINGERPRINT,AsyncConstant.TEST_NOT_EXIST);

            }
        }
        Sensor barrometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        if (barrometerSensor == null) {
            utils.addPreferenceBoolean(ctx,ConstantTestIDs.Barometer+Constants.IS_TEST_EXIST, true);
            utils.addPreferenceInt(ctx, JsonTags.MMR_63.name(), AsyncConstant.TEST_NOT_EXIST);
            utils.compare_UpdatePreferenceInt(ctx,"MMR_63",AsyncConstant.TEST_NOT_EXIST);
            realmOperations.updateTestResult(ConstantTestIDs.Barometer,AsyncConstant.TEST_NOT_EXIST);
        }
        int v=0;
        try {
            v = ctx.getPackageManager().getPackageInfo(GoogleApiAvailability.GOOGLE_PLAY_SERVICES_PACKAGE, 0 ).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        FaceDetector detector = new FaceDetector.Builder(ctx)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();
        if(!detector.isOperational())
        {
            faceexists=false;
        }
        if(7899470>v || !faceexists)
        {

            utils.addPreferenceBoolean(ctx, ConstantTestIDs.FaceDetection+Constants.IS_TEST_EXIST, true);
            utils.addPreferenceInt(ctx, JsonTags.MMR_64.name(), AsyncConstant.TEST_NOT_EXIST);
            utils.compare_UpdatePreferenceInt(ctx,"MMR_64",AsyncConstant.TEST_NOT_EXIST);
            realmOperations.updateTestResult(ConstantTestIDs.FaceDetection,AsyncConstant.TEST_NOT_EXIST);
        }


    }

    @Override
    public void onButtonClick(boolean isCanceled, int callbackID, int which) {

    }


    @Override
    public void onStop() {

        super.onStop();
        Log.d("OnStop", "Called");
    }

    public void logException(Exception e, String methodName) {

    }

    public void onTitleLongClick(View v) {

        List<LogExceptionModel> list = databaseHelper.getAllLoggedException();
        if (list.size() != 0) {
            replaceFragment(R.id.container, new ShowExceptionTableFragment(list), FragmentTag.EXCEPTIONTABLEFRAGMENT.name(), true);
        }
    }
}