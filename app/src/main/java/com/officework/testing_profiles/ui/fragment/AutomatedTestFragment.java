package com.officework.testing_profiles.ui.fragment;


import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.officework.R;
import com.officework.activities.MainActivity;
import com.officework.adapters.AutomatedTestRecyclerAdapter;
import com.officework.adapters.InterfaceAdapterCallback;
import com.officework.base.BaseFragment;
import com.officework.constants.AsyncConstant;
import com.officework.constants.Constants;
import com.officework.constants.FragmentTag;
import com.officework.constants.JsonTags;
import com.officework.constants.Preferences;
import com.officework.customViews.ArcProgress;
import com.officework.customViews.CircularSeekBar;
import com.officework.fragments.ManualTestFragment;
import com.officework.fragments.SummaryFragment;
import com.officework.fragments.TitleBarFragment;
import com.officework.interfaces.ViewPagerDisableInterface;
import com.officework.models.AutomatedTestListModel;
import com.officework.testing_profiles.Controller.TestController;
import com.officework.testing_profiles.realmDatabase.operationManager.RealmOperations;
import com.officework.testing_profiles.utils.ConstantTestIDs;
import com.officework.utils.DeviceInformation;
import com.officework.utils.ExceptionLogUtils;
import com.officework.utils.TestResultUpdateToServer;
import com.officework.utils.Utilities;
import com.officework.utils.WebserviceUrls;
import com.officework.utils.socket.SocketConstants;
import com.officework.utils.socket.SocketHelper;
import com.officework.utils.socket.SocketListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.UUID;

import io.socket.client.IO;
import io.socket.client.Socket;

import static com.officework.utils.socket.SocketConstants.ON_DEVICE_SCAN;


/**
 * Created by Girish on 8/9/2016.
 */
@SuppressLint("ValidFragment")
public class AutomatedTestFragment extends BaseFragment implements InterfaceAdapterCallback,
        Observer, SocketListener {
    public static ArrayList<AutomatedTestListModel> testModelArrayList = new ArrayList<>();
    public static boolean isAutomatedTest = false;
    static String mUniqueNo = null;
    public List<Integer> list = new ArrayList<>();
    View view;
    boolean que = false;
    Utilities utils;
    Context ctx;
    RecyclerView recyclerView;
    AutomatedTestRecyclerAdapter adapter;
    boolean isAllowBack = false;
    ArcProgress arcProgress;
    int progress = 0;
    TestResultUpdateToServer testResultUpdateToServer;
    ArrayList<String> arrayListTests;
    HashMap<Integer, Integer> testIdMap = new HashMap<>();
    FragmentManager fm;
    Fragment frag;
    int scrollPosition = 0;
    ViewPagerDisableInterface viewPagerDisableInterface;
    Handler nextScreenHandler;
    DeviceInformation deviceInformation;
    boolean isFirstTime = false;
    boolean isDeviceRemoved = false;
    Handler handler = null;
    CircularSeekBar pcircularSeekBar;
    private boolean isPermission;
    private JSONArray jsonArray;
    private JSONArray manual2_jsonArray;
    private JSONArray manual1_jsonArray;
    private ImageView mImgViewRested;
    private SensorManager sensorManager;
    private SocketHelper socketHelper;
    private String androidId;
    private RealmOperations realmOperations;
    private TestController testController;
    private boolean is_start_run = false;
    private BluetoothAdapter bAdapter;
    private WifiManager wifiManager;
    private static AutomatedTestFragment instance = null;



    public static AutomatedTestFragment getInstance() {
        return instance;
    }
    @SuppressLint("ValidFragment")
    public AutomatedTestFragment(ViewPagerDisableInterface viewPagerDisableInterface) {
        this.viewPagerDisableInterface = viewPagerDisableInterface;

    }

    public AutomatedTestFragment() {
    }

    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        try {
            if (view == null) {
                instance = this;
                view = inflater.inflate(R.layout.fragment_automated, null);
                bAdapter = BluetoothAdapter.getDefaultAdapter();
                wifiManager =
                        (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                initObjects();
                //  Integer[] static_Array = new Integer[]{37, 32, 33, 34, 15, 8, 40, 47, 28, 26};
                list = realmOperations.fetchallTestid(MainActivity.id_array, Constants.AUTOMATE,
                        getActivity());
                //   list = Arrays.asList(MainActivity.json_array);
                checkSensors();
                initViews();
            } else {
                testController = TestController.getInstance(getActivity());
                testController.addObserver(this);
            }
            return view;
        } catch (Exception e) {
            logException(e, "AutomatedTestFragment_initUI()");
            return null;
        }

    }

    private void initObjects() {
        utils = Utilities.getInstance(getActivity());
        ctx = getActivity();
        realmOperations = new RealmOperations();
        testController = TestController.getInstance(getActivity());
        testController.addObserver(this);
        deviceInformation = new DeviceInformation();
        getData();
        //  testResultUpdateToServer = new TestResultUpdateToServer(utils, ctx, getActivity());
        ArcProgress.isShowProgressText = true;
        arrayListTests = new ArrayList<>();
    }

    public void checkSensors() {
        try {
            sensorManager = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
            Sensor LightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            if (LightSensor == null) {
//            isLightSensorExist = false;
                utils.addPreferenceBoolean(ctx,
                        ConstantTestIDs.LIGHT_SENSOR_ID + Constants.IS_TEST_EXIST, true);
                utils.addPreferenceInt(ctx, JsonTags.MMR_26.name(), AsyncConstant.TEST_NOT_EXIST);
                utils.compare_UpdatePreferenceInt(getActivity(), "MMR_26",
                        AsyncConstant.TEST_NOT_EXIST);
                realmOperations.updateTestResult(ConstantTestIDs.LIGHT_SENSOR_ID,
                        AsyncConstant.TEST_NOT_EXIST);

            }

            Sensor barrometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
            if (barrometerSensor == null) {
                utils.addPreferenceBoolean(ctx,
                        AsyncConstant.Barometer + Constants.IS_TEST_EXIST,
                        true);
                utils.addPreferenceInt(ctx, JsonTags.MMR_63.name(),
                        AsyncConstant.TEST_NOT_EXIST);
                utils.compare_UpdatePreferenceInt(ctx, "MMR_63",
                        AsyncConstant.TEST_NOT_EXIST);
                realmOperations.updateTestResult(AsyncConstant.Barometer,
                        AsyncConstant.TEST_NOT_EXIST);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getData() {
        if (getArguments() != null) {
            if (utils.getPreference(getActivity(), Constants.ANDROID_ID, "").isEmpty()) {
                //  androidId = Settings.Secure.getString(getActivity().getContentResolver(),
                //  Settings.Secure.ANDROID_ID);
                androidId = UUID.randomUUID().toString();
                utils.addPreference(getActivity(), Constants.ANDROID_ID, androidId);
            } else {
                androidId = utils.getPreference(getActivity(), Constants.ANDROID_ID, "");
            }


        }
    }

    /**
     * Initialize view
     */
    private void initViews() {
        try {
            fm = getActivity().getSupportFragmentManager();
            frag = fm.findFragmentById(R.id.container);
            Log.d("check automate", is_start_run + "");

//            if(!utils.getPreferenceBoolean(getActivity(),"IS_RUN",false)){
//
//            }
            recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewAutomatedTest);
            mImgViewRested = (ImageView) view.findViewById(R.id.imgViewRested);
            pcircularSeekBar = view.findViewById(R.id.progressBar11);

            pcircularSeekBar.setEnabled(false);
            arcProgress = (ArcProgress) view.findViewById(R.id.arc_progress);

            mImgViewRested.setOnClickListener(this);
            setRecylerAdapter();
            if (testModelArrayList.size() > 0 && testModelArrayList.get(0).getIsTestSuccess() == AsyncConstant.TEST_IN_QUEUE) {
                mImgViewRested.setVisibility(View.GONE);
            } else {
                updateArcProgress(true);
                viewPagerDisableInterface.enableViewPager(true);
                isAllowBack = true;
                ArcProgress.isShowProgressText = false;
                mImgViewRested.setVisibility(View.VISIBLE);
//                arcProgress.setBottomText(ctx.getResources().getString(R.string.txtRetest));

            }
            createConnection(true);
        } catch (Exception e) {
            logException(e, "AutomatedTestFragment_initViews()");
        }
    }


    /**
     * on button click
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        super.onClick(v);
        try {
            switch (v.getId()) {
                case R.id.imgViewRested:
                    Constants.isArcFirsTime = false;
                    mImgViewRested.setEnabled(false);
                    mImgViewRested.setVisibility(View.GONE);
                    ArcProgress.isShowProgressText = true;
                    arcProgress.setProgress(0);
                    pcircularSeekBar.setProgress(0);
                    // updateArcProgress(Constants.default_weightage, true);

                    scrollPosition = 0;
                    if (nextScreenHandler != null) {
                        nextScreenHandler.removeCallbacksAndMessages(null);
                    }
                    startAutomatedTest();
                    break;
            }
        } catch (Exception e) {
            logException(e, "AutomatedTestFragment_onClick()");
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
                if (frag instanceof AutomatedTestFragment) {
                    fragment.setTitleBarVisibility(true);
                    fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string.txtAutomated), true, false, 0);
                    /*   handler = new Handler();*/

                    fragment.uniqueNo.setText(mUniqueNo);

                }
            }
            super.onResume();
        } catch (Exception e) {
            logException(e, "AutomatedTestFragment_onResume()");
        }

    }

    /**
     * Initialize recycler Adapter
     */
    private void setRecylerAdapter() {
        try {

            testModelArrayList.clear();


            testModelArrayList = realmOperations.fetchTestsList(MainActivity.id_array,
                    Constants.AUTOMATE, getActivity());

            adapter = new AutomatedTestRecyclerAdapter(ctx, testModelArrayList, this);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            logException(e, "AutomatedTestFragment_setRecylerAdapter()");
        }

    }


    /**
     * Show user the Snack bar till the time automated test wont complete.
     */
    public void onBackPress() {
        try {
            if (!isAllowBack) {
                Toast.makeText(getActivity(), R.string.txtAlertBackPress, Toast.LENGTH_SHORT).show();
            } else {
                clearAllStack();
            }
        } catch (Exception e) {
            logException(e, "AutomatedTestFragment_onBackPress()");
        }

    }


    /**
     * @param scrollToPosition -Scroll to position on display
     * @param testId           - Test Name
     * @param testStatus       -Status of Test
     */
    public void testStatusUpdate(final int scrollToPosition, final int testId,
                                 final int testStatus) {
        try {

            recyclerView.smoothScrollToPosition(scrollToPosition);
            sendTestData(SocketConstants.EVENT_TEST_START, testId, testStatus);
            updateList(testId, testStatus);
            testController.performOperation(testId);
            if (testId != testModelArrayList.get(testModelArrayList.size() - 1).getTest_id())
                scrollPosition++;
        } catch (Exception e) {
            logException(e, "AutomatedTestFragment_testStatusUpdate()");
        }
    }

//    /**
//     * Update Arc Progress Value
//     *
//     * @param isRetest
//     */
//    public void updateArcProgress(boolean isRetest) {
//        try {
//            ObjectAnimator animation;
//
//            if (isRetest) {
//                animation = ObjectAnimator.ofInt(arcProgress, "progress", progress, 100); // see
//                // this max value coming back here, we animale towards that value
//
//            } else {
//                animation = ObjectAnimator.ofInt(arcProgress, "progress", progress,
//                        progress + (100 / testModelArrayList.size())); // see this max value
//                // coming back here, we animale towards that value
//                progress = progress + (100 / testModelArrayList.size());
//
//            }
//            animation.setDuration(500); //in milliseconds
//            animation.setInterpolator(new DecelerateInterpolator());
//            animation.start();
//
//        } catch (Exception e) {
//            logException(e, "AutomatedTestFragment_updateArcProgress()");
//        }
//
//
//    }


    /**
     * Update Arc Progress Value
     *
     * @param isRetest
     */
    public void updateArcProgress(boolean isRetest) {
        try {
            ObjectAnimator animation;
            ObjectAnimator animation2;
            if (isRetest) {
                animation = ObjectAnimator.ofInt(arcProgress, "progress", progress, 100); // see
                // this max value coming back here, we animale towards that value
                animation2 = ObjectAnimator.ofFloat(pcircularSeekBar, "progress", progress, 100); // see


            } else {
                animation = ObjectAnimator.ofInt(arcProgress, "progress", progress,
                        progress + (100 / testModelArrayList.size())); // see this max value
                // coming back here, we animale towards that value
                animation2 = ObjectAnimator.ofFloat(pcircularSeekBar, "progress", progress,
                        progress + (100 / testModelArrayList.size()));
                progress = progress + (100 / testModelArrayList.size());

            }
            animation.setDuration(500); //in milliseconds
            animation.setInterpolator(new DecelerateInterpolator());
            animation.start();
            animation2.setDuration(500); //in milliseconds
            animation2.setInterpolator(new DecelerateInterpolator());
            animation2.start();
        } catch (Exception e) {
            logException(e, "AutomatedTestFragment_updateArcProgress()");
        }


    }


    public void sendTestData(String event, int testId, int result) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.QRCODEID, MainActivity.qr_code_test);
            jsonObject.put(Constants.UNIQUE_ID, androidId);

            if (utils.getPreference(getActivity(), Constants.UNIQUE_NO, "").isEmpty()) {
                socketHelper.emitData(SocketConstants.ON_SEND_NUMBER, jsonObject);
            }
            jsonObject.put("TestName", getTestName(testId));
            jsonObject.put("TestStatus", getTestProgress(result));
            jsonObject.put("TestProgress", progress);
            jsonObject.put("TestType", Constants.AUTOMATE);
            socketHelper.emitData(event, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private String getTestName(int testId) {
        switch (testId) {
            case AsyncConstant.BLUETOOTH_TEST:
                return Constants.BLUETOOTH_TEST_NAEM;
            case AsyncConstant.WIFI_TEST:
                return Constants.WIFI_TEST_NAEM;
            case AsyncConstant.VIBRATION_TEST:
                return Constants.VIBRATION_TEST_NAEM;
            case AsyncConstant.SIMCARD_TEST:
                return Constants.SIMCARD_TEST_NAEM;
            case AsyncConstant.SDCARD_TEST:
                return Constants.SDCARD_TEST_NAEM;
            case AsyncConstant.KILLSWITCH_TEST:
                return Constants.KILLSWITCH_TEST_NAEM;
            case AsyncConstant.FMIP:
                return Constants.FMIP_NAEM;
            case AsyncConstant.CALL_FUNCTION:
                return Constants.CALL_FUNCTION_NAEM;
//            case Constants.BLUETOOTH__DISABLE_TEST:
//                return Constants.BLUETOOTH__DISABLE_TEST_NAEM;
            case ConstantTestIDs.LIGHT_SENSOR_ID:
                return Constants.LIGHT_SENSOR_NAEM;
            case AsyncConstant.Barometer:
                return Constants.BAROMETER_SENSOR_NAEM;
            default:
                return "TEST";


        }
    }


    private String getTestProgress(int result) {
        switch (result) {
            case 0:
                return Constants.FAILED;
            case 1:
                return Constants.COMPLETED;
            case 2:
                return Constants.PENDING;
            case 3:
                return Constants.IN_PROGRESS;
            default:
                return "start";
        }
    }

    /**
     * Updating List data for all the automation test
     */
    public int updateList(int type, int currentStatus) {
        int position = 0;
        try {
            for (int index = 0; index < testModelArrayList.size(); index++) {
                if (type == testModelArrayList.get(index).getTest_id()) {
                    position = index;
                    break;
                }

            }
            AutomatedTestListModel object_SD = testModelArrayList.get(position);
            object_SD.setIsTestSuccess(currentStatus);


            testModelArrayList.remove(position);
            testModelArrayList.add(position, object_SD);
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });

        } catch (Exception e) {
            logException(e, "AutomatedTestFragment_updateList()");
        }

        return position;

    }


    public void createConnection(boolean isPermission) {


        try {
            this.isPermission = isPermission;
            androidId = utils.getPreference(getActivity(), Constants.ANDROID_ID, "");
            if (!isPermission) {
                //  checkRuntimePermisson();
                return;
            }

            if (Utilities.getInstance(getActivity()).isInternetWorking(getActivity())) {


                IO.Options options = new IO.Options();
                socketHelper =
                        new SocketHelper.Builder(WebserviceUrls.BaseUrl + "?" + Constants.REQUEST_UNIQUE_ID + "=" + androidId, options)
                                .addEvent(SocketConstants.EVENT_CONNECTED)
                                .addListener(this)
                                .build();

                if (socketHelper != null && socketHelper.socket.connected()) {
                    sendDeviceScan();
                } else {
                    socketHelper.connect();
                }

            } else {
                //  showNetworkDialog();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * on Activity result
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        boolean status;
        try {
            int responseStatus;
            if (resultCode == -1) {
                status = true;
                responseStatus = AsyncConstant.TEST_PASS;
            } else {
                status = false;
                responseStatus = AsyncConstant.TEST_FAILED;
            }
            testController.onServiceResponse(status, "Bluetooth", AsyncConstant.BLUETOOTH_TEST);
            updateList(AsyncConstant.BLUETOOTH_TEST, responseStatus);
            utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_34.name(), responseStatus);
            checkIndividualTestResult(AsyncConstant.BLUETOOTH_TEST);
            if (arrayListTests.size() == 0) {
                mImgViewRested.setEnabled(true);
            } else {
                arrayListTests.remove("Bluetooth");
                if (arrayListTests.size() == 0) {
                    mImgViewRested.setEnabled(true);
                }
            }
            sendTestData(SocketConstants.EVENT_TEST_END, Constants.BLUETOOTH_TEST,
                    responseStatus);


        } catch (Exception e) {

            logException(e, "AutomatedTestFragment_onActivityResult()");
        }

    }


    private void checkIndividualTestResult(int callbackID) {
        Set<Integer> keySet = testIdMap.keySet();

        if (keySet.size() == 0) {
            mImgViewRested.setEnabled(true);
        } else {
            keySet.remove(callbackID);
            if (keySet.size() == 0) {
                mImgViewRested.setEnabled(true);
            }
        }
    }

    /**
     * Callback of click event of RecycleView
     */
    @Override
    public void onItemClick(int position) {
        try {
            if (!que) {
                que = true;
                if (isAllowBack) {
                    isAutomatedTest = false;
                    testIdMap.put(testModelArrayList.get(position).getTest_id(), position);
                    progress = 100;
                    if (nextScreenHandler != null) {
                        nextScreenHandler.removeCallbacksAndMessages(null);
                    }
                    testStatusUpdate(position, testModelArrayList.get(position).getTest_id(),
                            AsyncConstant.TEST_IN_PROGRESS);
                    mImgViewRested.setEnabled(false);
                    viewPagerDisableInterface.enableViewPager(false);

                }
            }
        } catch (Exception e) {
            logException(e, "AutomatedTestFragment_onItemClick()");
        }

    }

    /**
     * This method update data to backend API after performing each test
     * update Test Data
     */

    public void updateResultToServer(int testId) {
        try {
            if (utils.getPreferenceBoolean(ctx, JsonTags.isTestDataChanged.name(), false)) {
                // testResultUpdateToServer.updateTestResult(null, true, 1);
                testResultUpdateToServer.updateTestResultToServer(false, testId, 1);
            }
        } catch (Exception e) {
            logException(e, "AutomatedTestFragment_updateResultToServer()");
        }


    }

    public void logException(Exception e, String methodName) {
        try {
            Utilities utilities = Utilities.getInstance(getActivity());
            Activity activity = getActivity();
            Context context = getActivity();
            ExceptionLogUtils exceptionLogUtils = new ExceptionLogUtils(utilities, context,
                    activity);
            exceptionLogUtils.addExceptionLog(utilities, context, activity, null, null, e,
                    methodName);
        } catch (Exception exp) {
        }

    }


    @Override
    public void update(Observable observable, Object o) {
        try {
            AutomatedTestListModel testModel = (AutomatedTestListModel) o;
            if (!list.contains(testModel.getTest_id()))
                return;
            int testId = testModel.getTest_id();
            updateList(testId, testModel.getIsTestSuccess());

            if (testModel.getIsTestSuccess() == 1) {
                utils.compare_UpdatePreferenceInt(getActivity(), "MMR_" + testId,
                        AsyncConstant.TEST_PASS);
            } else {
                utils.compare_UpdatePreferenceInt(getActivity(), "MMR_" + testId,
                        AsyncConstant.TEST_FAILED);
            }
//            if (isAutomatedTest) {
//                updateArcProgress(false);
//            }
            //   sendTestData(SocketConstants.EVENT_TEST_END, testId, testModel.getIsTestSuccess());
            //   updateResultToServer(testId);
            if (isAutomatedTest) {
                updateArcProgress(false);
                if (testId != testModelArrayList.get(testModelArrayList.size() - 1).getTest_id()) {

                    testStatusUpdate(scrollPosition + 1,
                            testModelArrayList.get(scrollPosition).getTest_id(),
                            AsyncConstant.TEST_IN_PROGRESS);
                }

                /*Updating arc on Sim Card result*/
                /*SDCard Checking test.*/
            } else {
                ViewPager pager = (ViewPager) ((ManualTestFragment) frag).pager;
                if (pager.getCurrentItem() == 0) {
                    //  if (frag instanceof AutomatedTestFragment) {

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            utils.showToast(ctx, getResources().getString(R.string.testCompleted));
                            que = false;
                          //  viewPagerDisableInterface.enableViewPager(true);

                        }
                    });

                }

            }

            if (isAutomatedTest && testId == testModelArrayList.get(testModelArrayList.size() - 1).getTest_id()) {
                /*Updating arc on Sd Card result*/

                progress = 0;
                isAutomatedTest = false;
                isAllowBack = true;
//                arcProgress.setBottomText("");
                arcProgress.setProgress(100);
                pcircularSeekBar.setProgress(100);
                handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        utils.showToast(ctx,
//                                ctx.getResources().getString(R.string.txtAutomationCompleted));
                        arcProgress.setProgress(100);
                        pcircularSeekBar.setProgress(100);
                        Log.d("thread", Thread.currentThread().getName());
                        ArcProgress.isShowProgressText = false;
                        mImgViewRested.setVisibility(View.VISIBLE);
                        mImgViewRested.setEnabled(true);
                        // arcProgress.setBottomText("c");
//                        if (viewPagerDisableInterface != null) {
//                            viewPagerDisableInterface.enableViewPager(true);
//                        }
                        utils.addPreferenceBoolean(ctx, Preferences.isAutomatedTestFirst.name(),
                                false);
                        onResume();
                    }
                }, 1500);
                Constants.isArcFirsTime = false;

                // visible summary fragment


                FragmentManager fm = getActivity().getSupportFragmentManager();
                Fragment frag = fm.findFragmentById(R.id.container);

                final ViewPager pager = (ViewPager) ((ManualTestFragment) frag).pager;
                nextScreenHandler = new Handler(Looper.getMainLooper());
                nextScreenHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        setTitle(R.string.auto_summary);
                        addFragment(R.id.container,
                                SummaryFragment.newInstance(Constants.AUTOMATE),
                                FragmentTag.SUMMARY_FRAGMENT.name(), false);

//                        if (pager.getCurrentItem() == 0) {
//                                que=false;
//
//
//                            pager.setCurrentItem(1);
//                            testController.performOperation(ConstantTestIDs.Battery);
//                            testController.performOperation(ConstantTestIDs.CHARGING_ID);
//                            socketHelper.emitScreenChangeEvent("Manual1", androidId,
//                                    MainActivity.qr_code_test);
//
//                        }
                    }
                }, 700);


            } else if (!isAutomatedTest) {
                if (frag instanceof AutomatedTestFragment) {
                    utils.showToast(ctx, getResources().getString(R.string.testCompleted));
                }

            }
            checkIndividualTestResult(testId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void retestPerform() {
        testModelArrayList.clear();
        testModelArrayList.addAll(realmOperations.fetchFailTestsList(MainActivity.id_array, Constants.AUTOMATE,
                getActivity()));
        setAdapter();
        mImgViewRested.performClick();
    }



    public void retestPerform2() {
        testModelArrayList.clear();
        testModelArrayList.addAll(realmOperations.fetchTestsList(MainActivity.id_array,
                Constants.AUTOMATE, getActivity()));
                setAdapter();
//        mImgViewRested.performClick();
    }



    public void setAdapter(){
        if(adapter != null){
            adapter.notifyDataSetChanged();
        }else {
            adapter = new AutomatedTestRecyclerAdapter(ctx, testModelArrayList, this);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(adapter);
        }
    }


    public void moveNext() {
        final ViewPager pager = (ViewPager) ((ManualTestFragment) frag).pager;
        if (pager.getCurrentItem() == 0) {
            que = false;


            pager.setCurrentItem(1);
            testController.performOperation(ConstantTestIDs.Battery);
            testController.performOperation(ConstantTestIDs.CHARGING_ID);
            socketHelper.emitScreenChangeEvent("Manual1", androidId,
                    MainActivity.qr_code_test);

        }
    }

    public void setTitle(Integer i) {
        try {
            TitleBarFragment fragment = (TitleBarFragment) getFragment(R.id.headerContainer);
            if (fragment != null) {
                fragment.setTitleBarVisibility(true);
                fragment.setHeaderTitleAndSideIcon(getResources().getString(i), true, true, 0);
//                if (i == R.string.txtAutomated) {
//                    fragment.setSyntextVisibilty(false);
//                } else {
//                    fragment.setSyntextVisibilty(true);
//                }
                //  mCallBack.onChangeText(utils.BUTTON_SKIP, false);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Start automation test
     * It test one test after another
     */
    public void startAutomatedTest() {
        try {

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!bAdapter.isEnabled() || !wifiManager.isWifiEnabled()) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
                        alert.setMessage(R.string.txtAutomationPermission);
                        alert.setCancelable(false);
                        alert.setPositiveButton(R.string.Done,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startTest();
                                    }
                                });
//                        alert.show();
                        AlertDialog dialog= alert.create();
                        dialog.show();
                        dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setAllCaps(false);
                    } else {
                        startTest();
                    }
                }
            });


        } catch (Exception e) {
            logException(e, "AutomatedTestFragment_startAutomatedTest()");
        }
    }

    private void startTest() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                arcProgress.setBottomText(ctx.getResources().getString(R.string.txtDiagnose));
                arcProgress.setBottomTextSize(ctx.getResources().getDimension(R.dimen.text_medium));
                isAutomatedTest = true;
                isAllowBack = false;
                if (viewPagerDisableInterface != null) {
                    viewPagerDisableInterface.enableViewPager(false);
                }
                progress = 0;
                scrollPosition = 0;
                testStatusUpdate(scrollPosition, testModelArrayList.get(0).getTest_id(),
                        AsyncConstant.TEST_IN_PROGRESS);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            if (nextScreenHandler != null) {
                nextScreenHandler.removeCallbacksAndMessages(null);
            }
            testController.deleteObserver(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

//    @Override
//    public void automateTestStart() {
//        startAutomatedTest();
//    }

    private JSONObject getDeviceInformation(boolean isPermission) throws JSONException {

        ArrayList<AutomatedTestListModel> automateModelArrayList = new ArrayList<>();
        automateModelArrayList = realmOperations.fetchTestsListAllNotDualList(MainActivity.id_array,
                getActivity());

        jsonArray = new JSONArray();
        manual1_jsonArray = new JSONArray();
        manual2_jsonArray = new JSONArray();

        for (int i = 0; i < automateModelArrayList.size(); i++) {
            Log.d("data", automateModelArrayList.get(i).getTest_type() + ".." + i);
            if (automateModelArrayList.get(i).getTest_type().equals(Constants.AUTOMATE)) {
                createJsonObject(jsonArray, automateModelArrayList.get(i).getName(), "",
                        automateModelArrayList.get(i).getTest_id(), "desc",
                        Constants.AUTOMATE, true);
            } else if (automateModelArrayList.get(i).getTest_type().equals(Constants.MANUAL1)) {
                createJsonObject(manual1_jsonArray, automateModelArrayList.get(i).getName(), "",
                        automateModelArrayList.get(i).getTest_id(),
                        automateModelArrayList.get(i).getTestDes(), Constants.MANUAL1, true);
            } else if (automateModelArrayList.get(i).getTest_type().equals(Constants.MANUAL)) {
                createJsonObject(manual2_jsonArray, automateModelArrayList.get(i).getName(), "",
                        automateModelArrayList.get(i).getTest_id(),
                        automateModelArrayList.get(i).getTestDes(), Constants.MANUAL2, true);
            }
        }

        JSONObject jsonObject = new JSONObject();

        jsonObject.put(Constants.QRCODEID, MainActivity.qr_code_test);

        jsonObject.put(Constants.UNIQUE_ID, androidId);
        jsonObject.put("IMEI", deviceInformation.getIMEI(isPermission, getActivity()));
        jsonObject.put("Make", Build.MANUFACTURER);
        jsonObject.put("Model", Build.MODEL);
        jsonObject.put("Capacity",
                deviceInformation.getFileSize(deviceInformation.getAvailableInfo()));
        jsonObject.put("DeviceType", "android");
        jsonObject.put("TestProgress", progress);
        jsonObject.put(Constants.SOCKET_ID, socketHelper.socket.id());

        jsonObject.put("TestData", jsonArray);
        jsonObject.put("Manual_test1", manual1_jsonArray);
        jsonObject.put("Manual_test2", manual2_jsonArray);


        return jsonObject;
    }

    private void createJsonObject(JSONArray jsonArray, String testName, String progress, int id,
                                  String desc, String testType, boolean b) {

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(Constants.UNIQUE_ID, androidId);
            jsonObject.put(Constants.QRCODEID, MainActivity.qr_code_test);

            jsonObject.put(Constants.SOCKET_ID, socketHelper.socket.id());
            if (testName.equals("Speaker & Mic")) {
                testName = Constants.MIC;
            }
            jsonObject.put("TestName", testName);
            jsonObject.put("TestStatus", progress);
            jsonObject.put("TestDescription", desc);
            jsonObject.put("TestId", id);
            jsonObject.put("TestType", testType);
            jsonArray.put(jsonObject);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void sendDeviceScan() {
        if (Utilities.getInstance(getActivity()).isInternetWorking(getActivity()) && !isFirstTime) {
            isFirstTime = true;
            final JSONObject jsonObject1;
            try {
                jsonObject1 = getDeviceInformation(isPermission);

                socketHelper.emitData(ON_DEVICE_SCAN, jsonObject1);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (!isFirstTime) {

            //showNetworkDialog();

        }
    }

    @Override
    public void onSocketReceived(String event, JSONObject jsonObject) {
        Log.i("onSocketRecevied111", event.toString() + "," + jsonObject);

        switch (event) {

            case ON_DEVICE_SCAN:
                Log.i("onSocketReceived", event.toString());
                if (Utilities.getInstance(getActivity()).isInternetWorking(getActivity())) {
                    socketHelper.emitScreenChangeEvent("Automate", androidId,
                            MainActivity.qr_code_test);
                    if (!is_start_run) {
                        is_start_run = true;
                        startAutomatedTest();
                    }

                } else {

                    //  showNetworkDialog();


                }
                break;


            case SocketConstants.EVENT_DEVICE_NO:
                try {
                    if (!TextUtils.isEmpty(jsonObject.getString("SrNumber"))) {
                        mUniqueNo = jsonObject.getString("SrNumber");
                    }

                    if (!TextUtils.isEmpty(mUniqueNo))
                        utils.addPreference(getActivity(), Constants.UNIQUE_NO,
                                String.valueOf(mUniqueNo));

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TitleBarFragment fragment =
                                    (TitleBarFragment) getFragment(R.id.headerContainer);
                            if (fragment != null) {

                                fragment.uniqueNo.setVisibility(View.VISIBLE);
                                fragment.uniqueNo.setText(mUniqueNo);
                            }
                        }
                    });


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case Socket.EVENT_DISCONNECT:
            case Socket.EVENT_RECONNECT_ERROR:
                try {
                    //   showNetworkDialog();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;


            /**
             *
             */
            case Socket.EVENT_CONNECT:

                sendDeviceScan();

                break;


            case SocketConstants.EVENT_MAX_LIMIT:


                Utilities.getInstance(getActivity()).showAlert(getActivity(),
                        new Utilities.onAlertOkListener() {
                            @Override
                            public void onOkButtonClicked(String tag) {


                                try {
                                    if (socketHelper != null) {
                                        socketHelper.destroy();
                                    }
                                    getActivity().finish();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, getResources().getString(R.string.max_limit),
                        getResources().getString(R.string.Sync), "Ok", "", "Sync");


                break;

            case SocketConstants.On_RJECTED:

                Utilities.getInstance(getActivity()).showAlert(getActivity(),
                        new Utilities.onAlertOkListener() {
                            @Override
                            public void onOkButtonClicked(String tag) {


                                try {
                                    if (socketHelper != null) {
                                        socketHelper.destroy();
                                    }
                                    getActivity().finish();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, getResources().getString(R.string.max_limit),
                        getResources().getString(R.string.Sync), "Ok", "", "Sync");


                break;

            case SocketConstants.EVENT_DEVICE_REMOVED:

                if (!isDeviceRemoved) {
                    isDeviceRemoved = true;
                    deviceRemoved();
                }
                break;

        }
    }


}
