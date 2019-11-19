package com.officework.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.officework.R;
import com.officework.adapters.ViewPagerAdapter;
import com.officework.base.BaseCompatActivity;
import com.officework.broadcastReceivers.BatteryReceiver;
import com.officework.broadcastReceivers.DataBroadcastReceiver;
import com.officework.broadcastReceivers.NetworkChangeReceiver;
import com.officework.constants.AppConstantsTheme;
import com.officework.constants.AsyncConstant;
import com.officework.constants.Constants;
import com.officework.constants.FragmentTag;
import com.officework.constants.JsonTags;
import com.officework.constants.PreferenceConstants;
import com.officework.constants.Preferences;
import com.officework.fragments.AboutUsFragment;
import com.officework.fragments.AccelerometerTestFragment;
import com.officework.fragments.BaroMetereFragment;
import com.officework.fragments.BatteryMannualFragment;
import com.officework.fragments.CallFunctionManualFragment;
import com.officework.fragments.CameraFragment;
import com.officework.fragments.ChargingManualFragment;
import com.officework.fragments.CompassTestFragment;
import com.officework.fragments.DashBoardFragment;
import com.officework.fragments.DeviceCasingTestFragment;
import com.officework.fragments.DeviceInfoFragment;
import com.officework.fragments.DisplayManualFragment;
import com.officework.fragments.EarJackManualFragment;
import com.officework.fragments.ExpertModeFragment;
import com.officework.fragments.FaceDetectionFragment;
import com.officework.fragments.FingerPrintTestFragment;
import com.officework.fragments.FlashTestFragment;
import com.officework.fragments.GetQRCodeFragment;
import com.officework.fragments.GpsMapManualFragment;
import com.officework.fragments.HomeButtonManualFragment;
import com.officework.fragments.LightSensorManualFragment;
import com.officework.fragments.ManualTestFragment;
import com.officework.fragments.MicAndSpeakerManualFragment;
import com.officework.fragments.MicManualFragment;
import com.officework.fragments.MutitouchManualFragment;
import com.officework.fragments.NavigationDrawerFragment;
import com.officework.fragments.NetworkInfoFragment;
import com.officework.fragments.PaymentDetailFragment;
import com.officework.fragments.PhoneShakingManualFragment;
import com.officework.fragments.PowerButtonManualFragment;
import com.officework.fragments.ProximitySensorManualFragment;
import com.officework.fragments.SpeakerManualFragment;
import com.officework.fragments.SummaryFragment;
import com.officework.fragments.SystemInfoFragment;
import com.officework.fragments.TitleBarFragment;
import com.officework.fragments.TouchScreenCanvasManualFragment;
import com.officework.fragments.VolumeManualFragment;
import com.officework.interfaces.InterfaceAlertDissmiss;
import com.officework.interfaces.InterfaceBatteryCallback;
import com.officework.interfaces.InterfaceButtonTextChange;
import com.officework.interfaces.InterfaceNetworkChange;
import com.officework.interfaces.InterfaceWindowsOnFocusChanged;
import com.officework.interfaces.WebServiceCallback;
import com.officework.models.AutomatedTestListModel;
import com.officework.models.DiagnosticListPojo;
import com.officework.testing_profiles.Controller.TestController;
import com.officework.testing_profiles.realmDatabase.operationManager.RealmOperations;
import com.officework.testing_profiles.ui.fragment.AutomatedTestFragment;
import com.officework.testing_profiles.ui.fragment.CustomerInformationFragment;
import com.officework.testing_profiles.ui.fragment.DeclarationFragment;
import com.officework.testing_profiles.ui.fragment.RecieptInformationScreenFragment;
import com.officework.testing_profiles.ui.fragment.TestCompleteFragment;
import com.officework.testing_profiles.utils.ConstantTestIDs;
import com.officework.utils.AutomatedTestUtils;
import com.officework.utils.ExceptionLogUtils;
import com.officework.utils.TestResultUpdateToServer;
import com.officework.utils.Utilities;
import com.officework.utils.socket.SocketConstants;
import com.officework.utils.socket.SocketHelper;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.face.FaceDetector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import io.socket.client.IO;


public class MainActivity extends BaseCompatActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks, InterfaceAlertDissmiss,
        View.OnClickListener, InterfaceButtonTextChange, InterfaceNetworkChange,
        InterfaceBatteryCallback, WebServiceCallback {
    private static final int WIPE_DATA_CALLBACK_ID = 12345;
    public static Integer[] id_array;
    public static Integer[] id_array2;
    public static Integer[] json_array;
    public static String qr_code_test;
//    public static JSONArray array;
    public static LinkedHashMap<Integer, Boolean> testListSemi;
    public static LinkedHashMap<Integer, Boolean> testListManual;
    final public int REQUEST_CODE_ASK_PERMISSIONS = 123;
    final public int REQUEST_CODE_IMEI_PERMISSION = 504;
    public boolean isVolIncrease = false;
    public boolean isVolDecrease = false;
    public int index = 0;
    public int semiIndex = 0;
    public int manualIndex = 0;
    public int semiBackIndex = 0;
    public int manualBackIndex = 0;
    public List<AutomatedTestListModel> automatedTestListModels = new ArrayList<>();
    public HashMap<String, Integer> map = new HashMap<>();
    public boolean isAllSkipDone = false;
    public boolean shouldAdd = false;
    public ArrayList<Integer> skipMapSemi = new ArrayList<>();
    public ArrayList<Integer> skipMapManual = new ArrayList<>();
    public ArrayList<DiagnosticListPojo> diagnosticListPojoArrayList;
    ExceptionLogUtils exceptionLogUtils;
    Context ctx;
    int SERVICE_NUMBER = 0;
    Utilities utils;
    Button btnNext;
    boolean isCamera = false;
    RealmOperations realmOperations;
    ProgressDialog bar;
    ProgressDialog progressBarAccept;
    Handler handler = null;
    TestResultUpdateToServer testResultUpdateToServer;
    NetworkChangeReceiver networkChangeReceiver;
    NetworkChangeReceiver networkChangeReceiver1;
    MyProgressTask myProgressTask;
    DataBroadcastReceiver receiver = null;
    String jsonArray;
    BatteryReceiver batteryInfoReceiver = null;
    Integer[] static_Array = new Integer[]{34, 35, 36, 16, 42, 49, 30, 39, 18, 56, 45, 44, 23, 26
            , 40, 28, 58, 57, 31, 32, 33, 24, 22, 55, 63, 29, 59, 62, 20, 21, 67, 66, 15, 37};
    boolean faceexists = true;
    private boolean isReceiverRegistered = false;
    private SensorManager sensorManager;
    public boolean isManualRetest=false;

    /**
     * clear shared preferences for application
     *
     * @param utils
     * @param ctx
     */
    public static void clearPreferenceData(Utilities utils, Context ctx,
                                           SharedPreferences sharedPreferences) {

        try {
            utils.isTestDataChanged = false;
            try {

                boolean isUserDeclined =
                        sharedPreferences.getBoolean(JsonTags.isUserDeclined.name(), false);

//                utils.clearPreferences(ctx);
                //sharedPreferences.edit().putBoolean(JsonTags.isUserDeclined.name(),
                // isUserDeclined).commit();

            } catch (Exception e) {
                e.printStackTrace();
            }


            AutomatedTestUtils.getInstance(ctx).nullifyObjects();
        } catch (Exception e) {
            MainActivity mainActivity = new MainActivity();
            mainActivity.logException(e, "MainActivity_clearPreferenceData()");
        }

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        try {
            FragmentManager fm = getSupportFragmentManager();
            Fragment frag = fm.findFragmentById(R.id.container);
            int action = event.getAction();
            int keyCode = event.getKeyCode();
            switch (keyCode) {
                case KeyEvent.KEYCODE_VOLUME_UP:
                    if (action == KeyEvent.ACTION_DOWN) {
                        if (frag instanceof ManualTestFragment) {
                            return super.dispatchKeyEvent(event);
                            // ((VolumeManualFragment) frag).dispatchKeyEvent(event);
                        } else if (frag instanceof VolumeManualFragment) {
                            /*((ManualTestFragment) frag).dispatchKeyEvent(event);*/
                            return super.dispatchKeyEvent(event);
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    if (frag instanceof ManualTestFragment) {

                        return super.dispatchKeyEvent(event);
                        // ((VolumeManualFragment) frag).dispatchKeyEvent(event);
                    } else if (frag instanceof VolumeManualFragment) {
                        /*((ManualTestFragment) frag).dispatchKeyEvent(event);*/
                        return super.dispatchKeyEvent(event);
                    }
                    break;

                default:
                    return super.dispatchKeyEvent(event);

            }


        } catch (Exception e) {
            logException(e, "MainActivity_dispatchKeyEvent()");
            return false;
        }

        return false;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        FragmentManager fm = getSupportFragmentManager();
        Fragment frag = fm.findFragmentById(R.id.container);
        super.onWindowFocusChanged(hasFocus);
        if (frag instanceof AccelerometerTestFragment) {
            ((InterfaceWindowsOnFocusChanged) frag).onWindowsFocusedChange(hasFocus);
        }
    }

    @Override
    protected void initUI(Bundle savedInstanceState) {

        int colorValue = Color.parseColor("#" + "1F4999");
        AppConstantsTheme.setColor(colorValue, colorValue, colorValue, colorValue, colorValue);
        ctx = this;
        utils = Utilities.getInstance(this);
        testResultUpdateToServer = new TestResultUpdateToServer(utils, ctx, MainActivity.this);

//        array = new JSONArray(getIntent().getStringExtra("jsonArray"));


        testListManual = new LinkedHashMap<>();
        testListSemi = new LinkedHashMap<>();


        diagnosticListPojoArrayList =
                (ArrayList<DiagnosticListPojo>) getIntent().getSerializableExtra("jsonArray");

        if (diagnosticListPojoArrayList != null) {
            try {
                id_array = new Integer[diagnosticListPojoArrayList.size()];
                for (int index = 0; index < diagnosticListPojoArrayList.size(); index++) {
                    id_array[index] = diagnosticListPojoArrayList.get(index).getTestID();
                }
                id_array2 = id_array;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            id_array = static_Array;
        }


        json_array = (Integer[]) id_array.clone();

        int temp;
        for (int i = 0; i < json_array.length; i++) {
            for (int j = i + 1; j < json_array.length; j++) {
                if (json_array[i] > json_array[j]) {
                    temp = json_array[i];
                    json_array[i] = json_array[j];
                    json_array[j] = temp;
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                GetQRCodeFragment.getIMEI(true, Utilities.getInstance(this), MainActivity.this,
                        this);

            } else {
                checkRuntimePermisson();
            }
        } else {
            GetQRCodeFragment.getIMEI(true, Utilities.getInstance(this), MainActivity.this, this);
        }
        GetQRCodeFragment.getDeviceInfoData(Utilities.getInstance(this), MainActivity.this, this,
                (InterfaceBatteryCallback) this);

        Log.d("json data", GetQRCodeFragment.createSaveDataJson(Utilities.getInstance(this),
                this) + "");
        testResultUpdateToServer.updateTestResultToServer(true, 0, 1);


        try {
            setContentView(R.layout.activity_main);
            //       wipeDataPermissions();
        } catch (Exception e) {
            logException(e, "MainActivity_initUI()");
        }


    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d("is saved", savedInstanceState.getBoolean("saved") + "");
        clearStackList();
        this.finish();
    }

    @Override
    protected void initVariable(Bundle savedInstanceState) {
        try {

            btnNext = (Button) findViewById(R.id.btnNext);
            btnNext.setOnClickListener(this);
            myProgressTask = new MyProgressTask();
            bar = new ProgressDialog(ctx);
            progressBarAccept = new ProgressDialog(ctx);
            progressBarAccept.setTitle("");
            progressBarAccept.setCancelable(false);
            progressBarAccept.setMessage(ctx.getResources().getString(R.string.txtLoading));
            progressBarAccept.setIndeterminate(true);
            replaceFragment(R.id.headerContainer, new TitleBarFragment(),
                    FragmentTag.HEADER_FRAGMENT.name(), false);
            getData();
            /**
             * Initialize TestResultUpdateToServer class so that when network is changed
             * API call can be made
             */
            exceptionLogUtils = new ExceptionLogUtils(utils, ctx, MainActivity.this);
            networkChangeReceiver = new NetworkChangeReceiver(ctx, (InterfaceNetworkChange) this);




            Log.d("navigation", "true");
            onNavigationDrawerItemSelected(1);
            if (savedInstanceState != null) {
                isCamera = false;
            } else {
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    /**
                     * Initialize secure shared preferences
                     */
                    utils.initializePreferences(ctx);
                }
            }).start();

        } catch (Exception e) {
            logException(e, "MainActivity_initVaialbes()");
        }

    }

    private void getData() {

        if (getIntent() != null) {
            qr_code_test = getIntent().getStringExtra(Constants.QR_CODE_RESULT);

            switchFragments(1);
            Log.i("qr_code_test", qr_code_test + "");

//            if (!StringUtil.isNullorEmpty(qr_code_test)) {
//                AutomatedTestFragment automationTestFragment = new AutomatedTestFragment();
//                Bundle bundle = new Bundle();
//                bundle.putString(Constants.QR_CODE_RESULT, qr_code_test);
//                automationTestFragment.setArguments(bundle);
//                //replaceFragment(R.id.container, new ManualTestFragment(), Constants.FragmentTag
//                // .MANUAL_TEST_FRAGMENT, false);
//
//                replaceFragment(R.id.container, automationTestFragment,
//                        FragmentTag.AUTOMATED_TEST_FRAGMENT.name(), false);
//            } else {
//                replaceFragment(R.id.container, new ManualTestFragment(),
//                        FragmentTag.AUTOMATED_TEST_FRAGMENT.name(), false);
//
//            }


        }
    }


    /**
     * Handle Network Change Event
     */

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        FragmentManager fm = getSupportFragmentManager();
        Fragment frag = fm.findFragmentById(R.id.container);
        if (SocketHelper.getInstance(null).socket.connected()) {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                if (frag instanceof VolumeManualFragment) {
                    TestController testController = TestController.getInstance(MainActivity.this);
                    testController.onServiceResponse(true, "VOLUME DOWN",
                            ConstantTestIDs.VOLUME_DOWN);
                    isVolDecrease = true;
                    //updateTestValues();
                    //Toast.makeText(MainActivity.this, "Volume Down Pressed", Toast.LENGTH_SHORT)
                    // .show();
                    if (isVolIncrease && isVolDecrease) {


                        //   updateTestValues();
                        // TestController testController = TestController.getInstance(this);
                        testController.onServiceResponse(true, "Volume", ConstantTestIDs.VOLUME_ID);
//                    if (frag instanceof VolumeManualFragment){
//                    utils.showToast(ctx, getResources().getString(R.string.txtManualPass));}
//
                        isVolDecrease = false;
                        isVolIncrease = false;
                    }
                }

                return true;
            }
        } else {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                if (frag instanceof VolumeManualFragment || frag instanceof ManualTestFragment) {
                    TestController testController = TestController.getInstance(MainActivity.this);
                    testController.onServiceResponse(true, "VOLUME DOWN",
                            ConstantTestIDs.VOLUME_DOWN);
                    isVolDecrease = true;
                    //updateTestValues();
                    //Toast.makeText(MainActivity.this, "Volume Down Pressed", Toast.LENGTH_SHORT)
                    // .show();
                    if (isVolIncrease && isVolDecrease) {


                        //   updateTestValues();
                        // TestController testController = TestController.getInstance(this);
                        testController.onServiceResponse(true, "Volume", ConstantTestIDs.VOLUME_ID);
//                    if (frag instanceof VolumeManualFragment){
//                    utils.showToast(ctx, getResources().getString(R.string.txtManualPass));}
//
                        isVolDecrease = false;
                        isVolIncrease = false;
                    }
                }

                return true;
            }
        }
        if (SocketHelper.getInstance(null).socket.connected()) {

            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                if (frag instanceof VolumeManualFragment) {
                    TestController testController = TestController.getInstance(MainActivity.this);
                    testController.onServiceResponse(true, "Volume Up", ConstantTestIDs.VOLUME_UP);
                    isVolIncrease = true;
                    //  updateTestValues();
                    // Toast.makeText(MainActivity.this, "Volume Up Pressed", Toast.LENGTH_SHORT)
                    // .show();
                    if (isVolIncrease && isVolDecrease) {
                        // updateTestValues();
                        //TestController testController = TestController.getInstance(this);
                        testController.onServiceResponse(true, "Volume", ConstantTestIDs.VOLUME_ID);
//                    if (frag instanceof VolumeManualFragment){
//                    utils.showToast(ctx, getResources().getString(R.string.txtManualPass));}
                        isVolIncrease = false;
                        isVolDecrease = false;
                    }
                }
                return true;
            } else {
                return super.onKeyDown(keyCode, event);
            }
        } else {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                if (frag instanceof VolumeManualFragment || frag instanceof ManualTestFragment) {
                    TestController testController = TestController.getInstance(MainActivity.this);
                    testController.onServiceResponse(true, "Volume Up", ConstantTestIDs.VOLUME_UP);
                    isVolIncrease = true;
                    //  updateTestValues();
                    // Toast.makeText(MainActivity.this, "Volume Up Pressed", Toast.LENGTH_SHORT)
                    // .show();
                    if (isVolIncrease && isVolDecrease) {
                        // updateTestValues();
                        //TestController testController = TestController.getInstance(this);
                        testController.onServiceResponse(true, "Volume", ConstantTestIDs.VOLUME_ID);
//                    if (frag instanceof VolumeManualFragment){
//                    utils.showToast(ctx, getResources().getString(R.string.txtManualPass));}
                        isVolIncrease = false;
                        isVolDecrease = false;
                    }
                }
                return true;
            } else {
                return super.onKeyDown(keyCode, event);
            }

        }
    }

    @Override
    public void onNetworkChange(Context context, boolean isChanged) {
        try {
            if (isChanged) {

                if (utils.getPreferenceBoolean(context, JsonTags.isTestDataChanged.name(), false)) {
                    //    testResultUpdateToServer.updateTestResult(null, true, 0);
                    Log.d("TestResultChanged ", "changed on network change");
                }
                if (utils.getPreferenceBoolean(ctx, JsonTags.isExceptionLogChanged.name(), false)) {
                    exceptionLogUtils.updateTestResult(null, true, 0);
                }
            } else {
            }
        } catch (Exception e) {
            logException(e, "MainActivity_onNetworkChange()");
        }
    }

    /**
     * onSaveInstance
     *
     * @param outState
     * @param outPersistentState
     */
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        try {
            outState.putBoolean("saved", true);
            //   super.onSaveInstanceState(outState, outPersistentState);

        } catch (Exception e) {
            logException(e, "MainActivity_onSaveInstanceState()");
        }

    }

    /**
     * Navigation Drawer Item Selected callback listener
     *
     * @param position
     */
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        try {
            FragmentManager fm = getSupportFragmentManager();
            Fragment frag = fm.findFragmentById(R.id.container);
            if (!isCamera) {
                if (frag instanceof CallFunctionManualFragment) {
                    if (CallFunctionManualFragment.isAllowBack) {
                        clearStackList();
                        //   switchFragments(position);
                    }
                } else if (!AutomatedTestFragment.isAutomatedTest) {
                    //  clearStackList();
                    // switchFragments(position);
                }
            }
        } catch (Exception e) {
            logException(e, "MainActivity_onNavigationDrawerItemSelected()");
        }
    }

    /**
     * Dismiss Keyboard when user click on navigation drawer items
     */
    private void dismisKeyboard() {
        try {
            View view = getCurrentFocus();
            if (view != null) {
                InputMethodManager imm =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception e) {
            logException(e, "MainActivity_dismissKeyboard()");
        }
    }

    /**
     * Navigaion Drawer CLick Handler
     * onNavigationDrawerItemSelected called switchFragmets() methods to switch fragment on
     * Navigation
     * Drawer item click
     *
     * @param position
     */

    private void switchFragments(int position) {
        try {


            Log.i("switch_fragment", position + "");

            dismisKeyboard();
            FragmentManager fm = getSupportFragmentManager();
            Fragment frag = fm.findFragmentById(R.id.container);
            switch (position) {
                case 0:
                    // Replace dasboard fragment
                    if (!(frag instanceof DashBoardFragment)) {
                        clearStackList();
                        replaceFragment(R.id.container,
                                new DashBoardFragment((InterfaceButtonTextChange) this),
                                FragmentTag.DASHBOARD_FRAGMENT.name(), false);
                    }

                    break;
                case 1:
                    //Replace Automation Test Fragment
                    if (!(frag instanceof AutomatedTestFragment)) {
                        clearStackList();
                        ManualTestFragment firstmanualTestFragment =
                                new ManualTestFragment((InterfaceButtonTextChange) MainActivity.this);
                        Bundle firstbundle = new Bundle();
                        firstbundle.putInt("Position", 0);

                        firstmanualTestFragment.setArguments(firstbundle);
                        replaceFragment(R.id.container, firstmanualTestFragment,
                                FragmentTag.MANUAL_TEST_FRAGMENT.name(), true);
                    }

                    break;
                case 2:

                    clearStackList();
//                        replaceFragment(R.id.container, new ManualTestFragment(
//                        (InterfaceButtonTextChange) this), FragmentTag.MANUAL_TEST_FRAGMENT
//                        .name(), true);
                    ManualTestFragment secondManualTestFragment =
                            new ManualTestFragment((InterfaceButtonTextChange) MainActivity.this);
                    Bundle secondBundle = new Bundle();
                    secondBundle.putInt("Position", 1);
                    secondManualTestFragment.setArguments(secondBundle);
                    replaceFragment(R.id.container, secondManualTestFragment,
                            FragmentTag.MANUAL_TEST_FRAGMENT.name(), true);
//                    }


                    break;
                case 3:
                    /**
                     * This is used to replace DeviceInfoFragment board
                     * If previousl user declined app terms and conditions
                     * Then this will show an alert to user where
                     * user can accept and decline terms and condition
                     * If user decline then nothing will happen
                     * If user accept then user device information is post to api
                     * and allow user to open device info fragment
                     */
                    if (!utils.getPreferenceBoolean(ctx, JsonTags.isUserDeclined.name(), false)) {
                        clearStackList();
                        replaceFragment(R.id.container, new DeviceInfoFragment(),
                                FragmentTag.DEVICE_INFO_FRAGMENT.name(), true);
                    } else {
                        utils.showAlert(ctx, (InterfaceAlertDissmiss) MainActivity.this,
                                Html.fromHtml(getResources().getString(R.string.txtAlertTermsAndCondition)),
                                getResources().getString(R.string.txtWelcometradeinpro_beta),
                                getResources().getString(R.string.txtAccept),
                                getResources().getString(R.string.txtDecline), 3);
                    }
                    break;

                case 4:
                    /**
                     * Reset application result data
                     * Shared preferences is cleared when user click on YES button on ALert
                     */
                    utils.showAlert(ctx, (InterfaceAlertDissmiss) MainActivity.this,
                            Html.fromHtml(getResources().getString(R.string.txtPermissionMessageWipe)),
                            getResources().getString(R.string.txtPermissionRequiredAlert),
                            ctx.getResources().getString(R.string.Yes),
                            ctx.getResources().getString(R.string.No), 125, 0);

                    break;
                case 5:

                    if ((!utils.getPreference(ctx, JsonTags.UDI.name(), "").equalsIgnoreCase(""))) {
                        clearStackList();
//                        replaceFragment(R.id.container, new GetBarCodeUDIFragment(
//                        (InterfaceButtonTextChange) this), FragmentTag.GETQRCODE_FRAGMENT.name
//                        (), true);
                        ManualTestFragment getBarCodeUDIFragment =
                                new ManualTestFragment((InterfaceButtonTextChange) MainActivity.this);
                        Bundle barCodeBundle = new Bundle();
                        barCodeBundle.putInt("Position", 3);
                        getBarCodeUDIFragment.setArguments(barCodeBundle);
                        replaceFragment(R.id.container, getBarCodeUDIFragment,
                                FragmentTag.GETQRCODE_FRAGMENT.name(), true);
                    } else {
                        utils.showToast(ctx, getResources().getString(R.string.txtInternetIssue));
                    }
                    break;
                case 6:
                    // Replace AboutUsFragment
                    clearStackList();
                    replaceFragment(R.id.container, new AboutUsFragment(),
                            FragmentTag.ABOUT_US_FRAGMENT.name(), true);

                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            logException(e, "MainActivity_switchFragments()");
        }

    }

    /**
     * This handle NEXT/SKIP button click for every fragment
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        super.onClick(view);
        try {
            switch (view.getId()) {
                case R.id.btnNext:
                    Constants.isSkipButton = true;
                    TestController testController = TestController.getInstance(MainActivity.this);
                    // testController.onServiceResponse(false,"",automatedTestListModels.get
                    // (index-1).getTest_id());
                    FragmentManager fm = getSupportFragmentManager();
                    Fragment frag = fm.findFragmentById(R.id.container);

//                    if (frag instanceof EarJackManualFragment || frag instanceof VolumeManualFragment ||
//                            frag instanceof PowerButtonManualFragment || frag instanceof ChargingManualFragment ||
//                            frag instanceof ProximitySensorManualFragment || frag instanceof PhoneShakingManualFragment ||
//                            frag instanceof HomeButtonManualFragment || frag instanceof BatteryMannualFragment) {
//
////                        testController.onServiceResponse(false, "",
////                                SemiAutomaticTestsFragment.testListModelList.get(semiIndex - 1)
////                                .getTest_id());
//
//
//                        testController.saveSkipResponse(-1,
//                                SemiAutomaticTestsFragment.testListModelList.get(semiIndex - 1).getTest_id());
//
//                    } else {
////                        testController.onServiceResponse(false, "",
////                                Manual2SemiAutomaticTestsFragment.testListModelList.get
////                                (manualIndex - 1).getTest_id());
//
//                        testController.saveSkipResponse(-1,
//                                Manual2SemiAutomaticTestsFragment.testListModelList.get(manualIndex - 1).getTest_id());
//                    }
                    testController.saveSkipResponse(-1,
                            ManualTestFragment.testListModelList.get(semiIndex - 1).getTest_id());
                    //  testController.onServiceResponse(false,"",
                    //  Manual2SemiAutomaticTestsFragment.testListModelList.get(manualIndex-1)
                    //  .getTest_id());

                    if (frag instanceof CameraFragment) {
                        ((CameraFragment) frag).onNextPress();
                    } else if (frag instanceof EarJackManualFragment) {
                        ((EarJackManualFragment) frag).onNextPress();
                    } else if (frag instanceof VolumeManualFragment) {
                        ((VolumeManualFragment) frag).onNextPress();
                    } else if (frag instanceof PowerButtonManualFragment) {
                        ((PowerButtonManualFragment) frag).onNextPress();
                    } else if (frag instanceof ChargingManualFragment) {
                        ((ChargingManualFragment) frag).onNextPress();
                    } else if (frag instanceof DisplayManualFragment) {
                        ((DisplayManualFragment) frag).onNextPress();
                    } else if (frag instanceof MutitouchManualFragment) {
                        ((MutitouchManualFragment) frag).onNextPress();
                    } else if (frag instanceof ProximitySensorManualFragment) {
                        ((ProximitySensorManualFragment) frag).onNextPress();
                    } else if (frag instanceof TouchScreenCanvasManualFragment) {
                        ((TouchScreenCanvasManualFragment) frag).onNextPress();
                    } else if (frag instanceof HomeButtonManualFragment) {
                        ((HomeButtonManualFragment) frag).onNextPress();
                    } else if (frag instanceof LightSensorManualFragment) {
                        ((LightSensorManualFragment) frag).onNextPress();
                    } else if (frag instanceof PhoneShakingManualFragment) {
                        ((PhoneShakingManualFragment) frag).onNextPress();
                    } else if (frag instanceof GpsMapManualFragment) {
                        ((GpsMapManualFragment) frag).onNextPress();
                    } else if (frag instanceof SpeakerManualFragment) {
                        ((SpeakerManualFragment) frag).onNextPress();
                    } else if (frag instanceof MicManualFragment) {
                        ((MicManualFragment) frag).onNextPress();
                    } else if (frag instanceof DeviceCasingTestFragment) {
                        ((DeviceCasingTestFragment) frag).onNextPress();
                    } else if (frag instanceof FlashTestFragment) {
                        ((FlashTestFragment) frag).onNextPress();
                    } else if (frag instanceof CompassTestFragment) {
                        ((CompassTestFragment) frag).onNextPress();
                    } else if (frag instanceof FingerPrintTestFragment) {
                        ((FingerPrintTestFragment) frag).onNextPress();
                    } else if (frag instanceof AccelerometerTestFragment) {
                        ((AccelerometerTestFragment) frag).onNextPress();
                    } else if (frag instanceof FaceDetectionFragment) {
                        ((FaceDetectionFragment) frag).onNextPress();
                    } else if (frag instanceof BaroMetereFragment) {
                        ((BaroMetereFragment) frag).onNextPress();
                    } else if (frag instanceof MicAndSpeakerManualFragment) {
                        ((MicAndSpeakerManualFragment) frag).onNextPress();
                    } else if (frag instanceof BatteryMannualFragment) {
                        ((BatteryMannualFragment) frag).onNextPress();
                    }
                    break;
            }
            Constants.isSkipButton = false;

        } catch (Exception e) {
            logException(e, "MainActivity_onClick()");
        }

    }

    /**
     * alert callback methods
     *
     * @param isCanceled
     * @param callbackID
     */
    @Override
    public void onButtonClick(boolean isCanceled, int callbackID) {
        try {
            switch (callbackID) {
                case 0:
                /*if (!isCanceled) {
                    killContactSyncService();
                    Utilities.getInstance(this).clearPreferences(this);
                    Utilities.getInstance(this).clearDB(this);
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                }*/
                    break;
                case 1:
                    if (!isCanceled)
                        finish();
                    break;
                case 2:
                    if (!isCanceled)
                        /*initiateFullSync();*/
                        break;

                case WIPE_DATA_CALLBACK_ID:
                    if (!isCanceled) {
                        //  wipeDeviceData();
                    }
                    break;
                case 3:
                    if (!isCanceled) {
                        if (utils.isInternetWorking(ctx)) {
                            //   myProgressTask.execute();
                            utils.addPreferenceBoolean(getApplicationContext(),
                                    JsonTags.isUserDeclined.name(), false);
                            PreferenceConstants.isUserDeclined = false;

                            checkRuntimePermisson();
                        } else {
                            utils.showToast(ctx, getResources().getString(R.string.InternetError));
                        }
                        clearStackList();
                        replaceFragment(R.id.container, new DeviceInfoFragment(),
                                FragmentTag.DEVICE_INFO_FRAGMENT.name(), true);

                    } else {
                        utils.addPreferenceBoolean(getApplicationContext(),
                                JsonTags.isUserDeclined.name(), true);
                    }
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            logException(e, "MainActivity_onButtonClick(boolean isCanceled, int callbackID)");
        }

    }

    /**
     * alert callback method
     *
     * @param isCanceled
     * @param callbackID
     * @param which
     */
    @Override
    public void onButtonClick(boolean isCanceled, int callbackID, int which) {
        try {
            switch (which) {
                case 0:
                    if (isCanceled) {
                        // clearStackList();
                        bar.setTitle("");
                        bar.setCancelable(false);
                        bar.setMessage(Html.fromHtml(ctx.getResources().getString(R.string.Message)));
                        bar.show();
                        Constants.isDoAllClicked = false;
                        Constants.isPagerElementTwoVisibleManual = false;
                        Constants.isSemiAutoVisible = true;
                        Constants.isManualTotalVisible = true;
                        resetDiagnostics();

                        PreferenceConstants.imageBitmap = null;
                        // checkSensors();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (bar.isShowing()) {
                                    bar.dismiss();
                                }
                                /*stopTimerTask(null, utils, ctx);*/

                                if (Constants.timerCallback != null) {
                                    Constants.timerCallback.cancel(true);
                                }
                                utils.showToast(ctx,
                                        ctx.getResources().getString(R.string.dataCleared));
//                                Intent intent = new Intent(MainActivity.this, SplashActivity
//                                .class);
//                                startActivity(intent);
//                                MainActivity.this.finish();
                                clearStackList();
                                replaceFragment(R.id.container, new DashBoardFragment(),
                                        FragmentTag.DASHBOARD_FRAGMENT.name(), false);

                            }
                        }, 1000);

                    }
                    break;
                case 1:
                    break;
            }
        } catch (Exception e) {
            logException(e, "MainActivity_onButtonClick(boolean isCanceled, int callbackID, int " +
                    "which)");
        }


    }

    public void resetDiagnostics() {
        SharedPreferences mAppPreferences1 =
                getSharedPreferences(Preferences.MEGAMMR_APP_PREF.name(), Context.MODE_PRIVATE);
        clearPreferenceData(utils, ctx, mAppPreferences1);
        //  utils.clearPreferences(ctx);
        //clearStackList();
        realmOperations = new RealmOperations();
        //    realmOperations.resetTestStatus();
        TestController testController = TestController.getInstance(MainActivity.this);
        testController.nextPressList.clear();
        testController.deleteObservers();
        //PreferenceConstants.udi = "";
        checkSensors();
    }

    public void checkSensors() {
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
            utils.addPreferenceBoolean(ctx,
                    ConstantTestIDs.FaceDetection + Constants.IS_TEST_EXIST, true);
            utils.addPreferenceInt(ctx, JsonTags.MMR_64.name(), AsyncConstant.TEST_NOT_EXIST);
            utils.compare_UpdatePreferenceInt(ctx, "MMR_64", AsyncConstant.TEST_NOT_EXIST);
            realmOperations.updateTestResult(ConstantTestIDs.FaceDetection,
                    AsyncConstant.TEST_NOT_EXIST);
        }
    }

    /**
     * Handle back button pressed for each fragment
     */
    @Override
    public void onBackPressed() {
        try {
            FragmentManager fm = getSupportFragmentManager();
            Fragment frag = fm.findFragmentById(R.id.container);
            if (frag instanceof ManualTestFragment) {
                ((ManualTestFragment) frag).onBackPress();
            } else if (frag instanceof SummaryFragment) {
                ((SummaryFragment) frag).onBackPress();
            } else if (frag instanceof AutomatedTestFragment)
                ((AutomatedTestFragment) frag).onBackPress();
            else if (frag instanceof CameraFragment)
                ((CameraFragment) frag).onBackPress();
            else if (frag instanceof GetQRCodeFragment) {
                ((GetQRCodeFragment) frag).onBackPress();
            } else if (frag instanceof DeviceInfoFragment) {
                ((DeviceInfoFragment) frag).onBackPress();
            } else if (frag instanceof AboutUsFragment) {
                ((AboutUsFragment) frag).onBackPress();
            } else if (frag instanceof ExpertModeFragment) {
                ((ExpertModeFragment) frag).onBackPress();
            } else if (frag instanceof CallFunctionManualFragment)
                ((CallFunctionManualFragment) frag).onBackPress();
            else if (frag instanceof DisplayManualFragment)
                ((DisplayManualFragment) frag).onBackPress();
            else if (frag instanceof HomeButtonManualFragment)
                ((HomeButtonManualFragment) frag).onBackPress();
            else if (frag instanceof LightSensorManualFragment)
                ((LightSensorManualFragment) frag).onBackPress();
            else if (frag instanceof EarJackManualFragment)
                ((EarJackManualFragment) frag).onBackPress();
            else if (frag instanceof VolumeManualFragment)
                ((VolumeManualFragment) frag).onBackPress();
            else if (frag instanceof PowerButtonManualFragment)
                ((PowerButtonManualFragment) frag).onBackPress();
            else if (frag instanceof ChargingManualFragment)
                ((ChargingManualFragment) frag).onBackPress();
            else if (frag instanceof MutitouchManualFragment)
                ((MutitouchManualFragment) frag).onBackPress();
            else if (frag instanceof ProximitySensorManualFragment)
                ((ProximitySensorManualFragment) frag).onBackPress();
            else if (frag instanceof TouchScreenCanvasManualFragment)
                ((TouchScreenCanvasManualFragment) frag).onBackPress();
            else if (frag instanceof GpsMapManualFragment)
                ((GpsMapManualFragment) frag).onBackPress();
            else if (frag instanceof PhoneShakingManualFragment)
                ((PhoneShakingManualFragment) frag).onBackPress();
            else if (frag instanceof SpeakerManualFragment)
                ((SpeakerManualFragment) frag).onBackPress();
            else if (frag instanceof MicManualFragment)
                ((MicManualFragment) frag).onBackPress();
            else if (frag instanceof DeviceCasingTestFragment)
                ((DeviceCasingTestFragment) frag).onBackPress();
            else if (frag instanceof FlashTestFragment)
                ((FlashTestFragment) frag).onBackPress();
            else if (frag instanceof CompassTestFragment)
                ((CompassTestFragment) frag).onBackPress();
            else if (frag instanceof FingerPrintTestFragment)
                ((FingerPrintTestFragment) frag).onBackPress();
            else if (frag instanceof AccelerometerTestFragment)
                ((AccelerometerTestFragment) frag).onBackPress();
            else if (frag instanceof FaceDetectionFragment)
                ((FaceDetectionFragment) frag).onBackPress();
            else if (frag instanceof BaroMetereFragment) {
                ((BaroMetereFragment) frag).onBackPress();
            } else if (frag instanceof MicAndSpeakerManualFragment) {
                ((MicAndSpeakerManualFragment) frag).onBackPress();
            } else if (frag instanceof BatteryMannualFragment) {
                ((BatteryMannualFragment) frag).onBackPress();
            } else if (frag instanceof DeclarationFragment) {
                ((DeclarationFragment) frag).onBackPress();
            }else if (frag instanceof PaymentDetailFragment) {
                ((PaymentDetailFragment) frag).onBackPress();
            }

            else if (frag instanceof RecieptInformationScreenFragment) {
                Utilities.getInstance(this).showAlert(this, new Utilities.onAlertOkListener() {
                            @Override
                            public void onOkButtonClicked(String tag) {

                                try {
                                    String androidId =
                                            Utilities.getInstance(MainActivity.this).getPreference(MainActivity.this, Constants.ANDROID_ID, "");

                                    if (Utilities.getInstance(MainActivity.this).isInternetWorking(MainActivity.this)) {
                                        IO.Options options = new IO.Options();
                                        SocketHelper socketHelper =
                                                new SocketHelper.Builder(SocketConstants.HOST_NAME1 + "?" + Constants.REQUEST_UNIQUE_ID + "=" + androidId, options)
                                                        .addEvent(SocketConstants.EVENT_CONNECTED)
                                                        .addListener(null)
                                                        .build();


                                        if (socketHelper != null) {
                                            socketHelper.destroy();
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                Intent startMain = new Intent(Intent.ACTION_MAIN);
                                startMain.addCategory(Intent.CATEGORY_HOME);
                                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(startMain);
                                finish();
                            }
                        }, getResources().getString(R.string.exit_app),
                        getResources().getString(R.string.exit), "Ok", "Cancel", "Sync");
            } else if (frag instanceof CustomerInformationFragment || frag instanceof TestCompleteFragment) {
                Utilities.getInstance(this).showAlert(this, new Utilities.onAlertOkListener() {
                            @Override
                            public void onOkButtonClicked(String tag) {


                                try {
                                    String androidId =
                                            Utilities.getInstance(MainActivity.this).getPreference(MainActivity.this, Constants.ANDROID_ID, "");

                                    if (Utilities.getInstance(MainActivity.this).isInternetWorking(MainActivity.this)) {
                                        IO.Options options = new IO.Options();
                                        SocketHelper socketHelper =
                                                new SocketHelper.Builder(SocketConstants.HOST_NAME1 + "?" + Constants.REQUEST_UNIQUE_ID + "=" + androidId, options)
                                                        .addEvent(SocketConstants.EVENT_CONNECTED)
                                                        .addListener(null)
                                                        .build();


                                        if (socketHelper != null) {
                                            socketHelper.destroy();
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                Intent intent=new Intent(MainActivity.this, WelcomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        }, Html.fromHtml(getResources().getString(R.string.rescan_msg)),
                        getResources().getString(R.string.rescan_head), "No", "Yes", "Sync");
            } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                popFragment(frag.getId());
            }

        } catch (Exception e) {
            logException(e, "MainActivity_onBackPressed()");
        }
    }

    /**
     * onActivity result calback
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            FragmentManager fm = getSupportFragmentManager();
            Fragment frag = fm.findFragmentById(R.id.container);
            if (frag instanceof CameraFragment)
                ((CameraFragment) frag).onActivityResult(requestCode, resultCode, data);

            else if (frag instanceof ManualTestFragment) {
                ViewPagerAdapter viewPagerAdapter =
                        (ViewPagerAdapter) ((ManualTestFragment) frag).pager.getAdapter();
                Fragment fragment = viewPagerAdapter.getItem(0);
                {
                    ((AutomatedTestFragment) fragment).onActivityResult(requestCode, resultCode,
                            data);
                }
            }
            if (requestCode == WIPE_DATA_CALLBACK_ID) {
                //  wipeDeviceData();
            }
        } catch (Exception e) {
            logException(e, "MainActivity_onActivityResult()");
        }

    }

    @Override
    public Intent getIntent() {
        try {
            return super.getIntent();
        } catch (Exception e) {
            logException(e, "MainActivity_getIntent()");
            return null;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {

            /**
             *Register Receiver for Network Change
             */
            if (!isReceiverRegistered) {
                isReceiverRegistered = true;
                registerReceiver(networkChangeReceiver, new IntentFilter("android.net.conn" +
                        ".CONNECTIVITY_CHANGE"));
                registerReceiver(networkChangeReceiver, new IntentFilter("android.net.wifi" +
                        ".WIFI_STATE_CHANGED"));
            }
            if (batteryInfoReceiver == null) {
                batteryInfoReceiver = new BatteryReceiver(this);
                registerReceiver(batteryInfoReceiver,
                        new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            }

            handler = new Handler();
            if (!utils.isNullorEmpty(utils.getPreference(ctx, JsonTags.udi.name(), ""))) {
                broadcastData(true);

            } else {
                broadcastData(false);
            }
        } catch (Exception e) {
            logException(e, "MainActivity_onResume()");
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            Log.d("OnDestroy", " : Called");
            utils.addPreference(ctx, JsonTags.refcode.name(), "");
        } catch (Exception e) {
            logException(e, "MainActivity_onDestroy()");
        }

    }

    /**
     * Permisssion callback
     * This also handle fragments request permission callback
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        try {
            FragmentManager fm = getSupportFragmentManager();
            Fragment frag = fm.findFragmentById(R.id.container);
            switch (requestCode) {
                case REQUEST_CODE_ASK_PERMISSIONS:
                    if (frag instanceof SystemInfoFragment) {
                        ((SystemInfoFragment) frag).onRequestPermissionsResult(requestCode,
                                permissions, grantResults);
                    } /*else if (frag instanceof CameraInfoFragment) {
                    ((CameraInfoFragment) frag).onRequestPermissionsResult(requestCode,
                    permissions, grantResults);}*/ else if (frag instanceof NetworkInfoFragment) {
                        ((NetworkInfoFragment) frag).onRequestPermissionsResult(requestCode,
                                permissions, grantResults);
                    } else if (frag instanceof GpsMapManualFragment) {
                        ((GpsMapManualFragment) frag).onRequestPermissionsResult(requestCode,
                                permissions, grantResults);
                    } else if (frag instanceof CameraFragment) {
                        ((CameraFragment) frag).onRequestPermissionsResult(requestCode,
                                permissions, grantResults);
                    } else if (frag instanceof GetQRCodeFragment) {
                        ((GetQRCodeFragment) frag).onRequestPermissionsResult(requestCode,
                                permissions, grantResults);
                    }

                    break;
                case REQUEST_CODE_IMEI_PERMISSION:
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // Permission Granted
                        GetQRCodeFragment.getIMEI(true, utils, getApplicationContext(),
                                MainActivity.this);
                    } else {
                        // Permission Denied
                        GetQRCodeFragment.getIMEI(false, utils, getApplicationContext(),
                                MainActivity.this);
                    }
                    testResultUpdateToServer.updateTestResultToServer(true, 0, 1);
                    break;
                default:
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                    break;
            }
        } catch (Exception e) {
            logException(e, "MainActivity_onRequestPermissionsResult()");
        }

    }

    /**
     * Handle NEXT/SKIP button for all fragments
     *
     * @param text
     * @param showButton
     */
    @Override
    public void onChangeText(int text, boolean showButton) {
        try {
            FragmentManager fm = getSupportFragmentManager();
            Fragment frag = fm.findFragmentById(R.id.container);
            if (showButton) {
                if (text == utils.BUTTON_NEXT) {
                    btnNext.setVisibility(View.GONE);
                    btnNext.setText(text);
                } else {
                    btnNext.setEnabled(true);
                    btnNext.setVisibility(View.VISIBLE);
                    btnNext.setText(text);
                }

            } else {
                btnNext.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            logException(e, "MainActivity_onChangeText()");
        }

    }

    /**
     * onStart method
     */
    @Override
    protected void onStart() {
        super.onStart();

    }

    /**
     * onPause Method
     */
    @Override
    protected void onPause() {
        try {
            /**
             *Unregister Receiver for Network Change
             */
            if (isReceiverRegistered) {
                isReceiverRegistered = false;
                unregisterReceiver(networkChangeReceiver);
            }

            /**
             * Unregister databroad cast receiver
             */
            if (receiver != null) {
                unregisterReceiver(receiver);
                receiver = null;

            }

            if (batteryInfoReceiver != null) {
                unregisterReceiver(batteryInfoReceiver);
                batteryInfoReceiver = null;
                Log.d("Battery Receiver", " Unregistered");
            }

            super.onPause();
        } catch (Exception e) {
            logException(e, "MainActivity_onPause()");
        }

    }

    /**
     * Check permission
     */
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
            GetQRCodeFragment.getIMEI(true, utils, getApplicationContext(), MainActivity.this);
            // testResultUpdateToServer.updateTestResult((WebServiceCallback) this, true, 0);
            testResultUpdateToServer.updateTestResultToServer(true, 0, 1);

        } catch (Exception e) {
            logException(e, "MainActivity_checkRuntimePermisson()");
        }


    }


    @Override
    public void onBatteryCallBack(Intent intent) {

        Log.d("Battery Level", intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0) + "%");

    }

    /**
     * @param isUDIGenerated if this variable is true means UDI is present in application
     *                       if udi is present then we only broadcast udi not data
     *                       if udi is not present then we broadcast whole data
     */
    public void broadcastData(boolean isUDIGenerated) {
        try {
            receiver = new DataBroadcastReceiver(isUDIGenerated);
            IntentFilter filter = new IntentFilter();
            filter.addAction("com.megammr.GET_Data");
            registerReceiver(receiver, filter);
        } catch (Exception e) {
            logException(e, "MainActivity_broadcastData()");
        }

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
            // logException(exp, "MainActivity_logException()");
        }

    }

    public void updateResultToServer() {
        try {
            testResultUpdateToServer.updateTestResultToServer(true, 0, 1);

        } catch (Exception e) {
            logException(e, "DashBoardFragment_updateResultToServer()");
        }


    }

    @Override
    public void onServiceResponse(boolean serviceStatus, String response, int callbackID) {
        if (callbackID == 0) {
            Log.d("save data response", response);
        }
    }


    /**
     * Async task for progress dialog
     */
    class MyProgressTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            progressBarAccept.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

}
