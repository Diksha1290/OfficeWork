package com.officework.testing_profiles.ui.fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.Vibrator;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.officework.R;
import com.officework.activities.MainActivity;
import com.officework.adapters.InterfaceAdapterCallback;
import com.officework.adapters.SectionHeaderAdapter;
import com.officework.base.BaseFragment;
import com.officework.constants.AsyncConstant;
import com.officework.constants.Constants;
import com.officework.constants.FragmentTag;
import com.officework.constants.JsonTags;
import com.officework.fragments.CameraFragment;
import com.officework.fragments.DashBoardFragment;
import com.officework.fragments.HomeButtonManualFragment;
import com.officework.fragments.ManualTestFragment;
import com.officework.fragments.TitleBarFragment;
import com.officework.interfaces.InterfaceAlertDissmiss;
import com.officework.interfaces.InterfaceButtonTextChange;
import com.officework.models.AutomatedTestListModel;
import com.officework.testing_profiles.Controller.ManualTestsOperation;
import com.officework.testing_profiles.Controller.TestController;
import com.officework.testing_profiles.realmDatabase.operationManager.RealmOperations;
import com.officework.testing_profiles.utils.ConstantTestIDs;
import com.officework.utils.ExceptionLogUtils;
import com.officework.utils.TestResultUpdateToServer;
import com.officework.utils.Utilities;
import com.officework.utils.socket.SocketConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class SemiAutomaticTestsFragment extends BaseFragment implements Observer,
        InterfaceAdapterCallback, AdapterView.OnItemClickListener, InterfaceAlertDissmiss {

    public  List<AutomatedTestListModel> testListModelList;
    public int pageNumber;
    public static boolean isHomeButtonTested = false;
    public Vibrator vibrator;
    public List<Integer> list = new ArrayList<>();
    View view;
    Utilities utils;

    private boolean is_volumeRunning;
    private AutomatedTestListModel automatedTestListModel;
    Context ctx;
    InterfaceButtonTextChange mCallBack;
    RecyclerView mRecyclerView;
    SectionHeaderAdapter mAdapter;
    BroadcastReceiver mRegistrationBroadcastReceiver;
    boolean isShowToast;
    int count = 0;
    FragmentManager fm;
    boolean faceexists = true;
    //Power Button Test
    IntentFilter filter;
    long pattern[] = {0, 100, 200};
    //Jack Receiver
    BroadcastReceiver mReceiverJack = null;
    boolean isVolIncrease = false, isVolDecrease = false;
    TestResultUpdateToServer testResultUpdateToServer;
    /**
     * Test performed or not
     */
    Fragment frag;
    boolean isBackGround = false;
    boolean isProximityExist = true;
    boolean ismMagnetometerExist = true;
    boolean isFingerprintExist = true;
    boolean isFaceDetectionExist;
    boolean proxyCheck = false;
    String androidId;
    private RealmOperations realmOperations;
    private TestController testController;
    private SensorManager sensorManager;
    private boolean isLightSensorExist;
    private boolean isBarrometerExists = true;
    public Runnable runnableTimer;
    public Handler handler;
    public static final String ARG_LIST = "ARG_LIST";
    public static final String ARG_PAGE_NO = "ARG_LIST";

    public SemiAutomaticTestsFragment() {
        // Required empty public constructor
    }

    public static SemiAutomaticTestsFragment newInstance(List<AutomatedTestListModel> newObjects,int pageNumber) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_LIST, (ArrayList<? extends Parcelable>) newObjects);
        args.putInt("YYYY", pageNumber);

        SemiAutomaticTestsFragment fragment = new SemiAutomaticTestsFragment();
        fragment.setArguments(args);
        return fragment;
    }
    public SemiAutomaticTestsFragment(InterfaceButtonTextChange callback) {
        mCallBack = callback;
    }

    /**
     * onDestroy view
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(runnableTimer);
        testController.unRegisterPowerButton();

    }

    @Override
    public void onStop() {
        super.onStop();
        /**
         * Detach Home Button Receivr
         */

        Log.d("Semi Manual ", "onStop");

        count = 0;
        isShowToast = true;

    }

    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        try {
            if (view == null) {
                view = inflater.inflate(R.layout.fragment_semi_automatic_tests, null);
                utils = Utilities.getInstance(getActivity());
                ctx = getActivity();
                realmOperations = new RealmOperations();
                // checkSensors();





                //  Crashlytics.log(FragmentTag.SEMI_AUTOMATIC_TESTS_FRAGMENT.name());
                testController = TestController.getInstance(getActivity());
                testController.addObserver(this);
                //  testController.performOperation(ConstantTestIDs.VOLUME_ID);
                //list =Arrays.asList(MainActivity.id_array);
                //  Integer[] static_Array=new Integer[]{17,54,43,42,22,25,38,19,20,68};
                if (getArguments() != null) {
                    testListModelList = getArguments().getParcelableArrayList(ARG_LIST);
                    pageNumber = getArguments().getInt("YYYY");
                }
//                Log.e("YYYSI",String.valueOf(testListModelList.size()));
//                testListModelList = realmOperations.fetchTestsList(MainActivity.id_array,
//                        Constants.MANUAL1, getActivity());

//                list = realmOperations.fetchallTestid(MainActivity.id_array, Constants.MANUAL1,
//                        getActivity());

                for(int index= 0;index<testListModelList.size();index++) {
                    list.add(testListModelList.get(index).getTest_id());
                    if (testListModelList.get(index).getTest_id() == ConstantTestIDs.VOLUME_ID) {
                        list.add(19);
                        list.add(20);
                    }
                }
//                list.add(19);
//                list.add(20);
//                if(proxyCheck==false){
//                    testController.performOperation(ConstantTestIDs.PROXIMITY_ID);
//                    proxyCheck=true;}


//                if(MainActivity.testListSemi.size()==0) {
//                    for (int i = 0; i < testListModelList.size(); i++) {
//                        MainActivity.testListSemi.put(i, false);
//                    }
//                }


                initViews();
                craeteRunnable();
            }
            return view;
        } catch (Exception e) {
            return null;
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initViews() {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.gridViewSemiAutomatic);

        Constants.isManualIndividual = false;
        if (Constants.isManualClickScroolController) {
            Constants.index = 0;
            Constants.isManualClickScroolController = false;
        }
        testResultUpdateToServer = new TestResultUpdateToServer(utils, ctx, getActivity());
        androidId = utils.getPreference(getActivity(), Constants.ANDROID_ID, "");
        setGridAdapter();

        intializeVibrator();


//        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//
//                // checking for type intent filter
//                if (intent.getAction().equals(Constants.onVolumeKeyPressed)) {
//
//                    Bundle extras = intent.getExtras();
//                    if (extras == null) {
//                        /*newString= null;*/
//                    } else {
//
//                        if (Constants.isSemiAutoVisible) {
//                            dispatchKeyEvent((KeyEvent) extras.get("event"));
//                        }
//
//
//                    }
//                }
//            }
//        };

    }


    /**
     * set grid adapter
     */
    private void setGridAdapter() {
        try {

//           testListModelList = realmOperations.fetchTestsList(MainActivity.id_array,Constants
//           .MANUAL,getActivity());




            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.automatedTestListModels = testListModelList;
            //Your RecyclerView
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new GridLayoutManager(ctx, 2));
            mAdapter = new SectionHeaderAdapter(ctx, testListModelList,
                    (InterfaceAdapterCallback) this);
            mRecyclerView.setAdapter(mAdapter);
        } catch (Exception e) {
        }

    }


    /**
     * Here change the header text
     ***/
    @Override
    public void onResume() {
        super.onResume();
        try {
            Log.e("YYYRESUME",String.valueOf(ManualTestFragment.viewPagerItemPOsition));

            TitleBarFragment fragment = (TitleBarFragment) getFragment(R.id.headerContainer);
             fm = getActivity().getSupportFragmentManager();
                  frag = fm.findFragmentById(R.id.container);
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity.map.size() == ManualTestFragment.testListModelList.size()  && mainActivity.shouldAdd && TitleBarFragment.progressBar == null) {
                if (fragment != null) {
                    fragment.setSyntextVisibilty(true);
                }
            }
            if (mainActivity.map.size() == ManualTestFragment.testListModelList.size() && !mainActivity.shouldAdd ) {
                mainActivity.shouldAdd = true;
                if (fragment != null) {
                    Handler handler=new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fragment.setSyntextVisibilty(false);
                           // fragment.showSyncDialog();
                            fragment.openManualSummary();

                        }
                    },100);
                }
            }
            if (fragment != null) {

//              if( frag instanceof SemiAutomaticTestsFragment){
//               fragment.setTitleBarVisibility(true);
//                fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string
//                .txtManualSemiAutomatic), true, false, 0);
//              }
                mainActivity.onChangeText(utils.BUTTON_SKIP, false);


                isBackGround = false;


            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }

//        try {
//            if (Utilities.syncdialog == null  && TitleBarFragment.progressBar == null) {
//                if (!socketHelper.socket.connected()) {
//                    performAllManualTest();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (!socketHelper.socket.connected()) {
//                performAllManualTest();
//            }
//        }
        super.onResume();
    }
    private void performAllManualTest() {
        testController=TestController.getInstance(getActivity());
        testController.performOperation(ConstantTestIDs.EAR_PHONE_ID);
        testController.performOperation(ConstantTestIDs.POWER_ID);
        testController.performOperation(ConstantTestIDs.GYROSCOPE_ID);
        testController.performOperation(ConstantTestIDs.HOME_ID);
        testController.performOperation(ConstantTestIDs.CHARGING_ID);
        testController.performOperation(ConstantTestIDs.Battery);
        testController.performOperation(ConstantTestIDs.PROXIMITY_ID);
    }
    /**
     * Vibrator initializer
     */
    public void intializeVibrator() {
        try {
            vibrator = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
        } catch (Exception e) {
            logException(e, "DashBoardFragment_intializeVibrator()");
        }

    }

    public void startVibrate() {
        try {
            vibrator.vibrate(pattern, -1);
        } catch (Exception e) {
            logException(e, "DashBoardFragment_startVibrate()");
        }

    }

    public void stopVibrate() {
        try {
            vibrator.cancel();
        } catch (Exception e) {
            logException(e, "DashBoardFragment_stopVibrate()");
        }

    }

    /**
     * onDestroy
     */
    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            testController.deleteObserver(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onPause() {
//        try {
//            testController.unregisterGyroScope2();
//            testController.unRegisterCharging();
//            testController.unregisterBattery();
//            testController.unregisterProximity();
//            testController.unRegisterHome();
//            testController.unRegisterEarJack();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            logException(e, "DashBoardFragment_onPause()");
//        }
        try {
            Log.e("YYYPAUSE",String.valueOf(ManualTestFragment.viewPagerItemPOsition));

            isBackGround = true;


        } catch (Exception e) {
            e.printStackTrace();
            logException(e, "DashBoardFragment_onPause()");
        }
        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("MANUALTEST", "ondetach");
    }

    /**
     * Home Button Event Receiver
     */
    public void onHomePressed() {
        try {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            Fragment frag = fm.findFragmentById(R.id.container);
            if (Constants.isSemiAutoVisible) {

                isHomeButtonTested = true;
                //utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_25.name(), AsyncConstant
                // .TEST_PASS);

                if (getActivity() != null)

                    if (frag instanceof HomeButtonManualFragment) {
                        utils.showToastLong(ctx,
                                getResources().getString(R.string.txtManualHomeTestWait));
                        reopenScreen();
                    }
            }


        } catch (Exception e) {
            logException(e, "DashBoardFragment_onHomePressed()");
        }

    }

    /**
     * automatically reopen activity while performing test
     */
    private void reopenScreen() {
        try {
            Intent intent = new Intent(ctx, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        } catch (Exception e) {
            logException(e, "DashBoardFragment_reopenScreen()");
        }

    }

    /**
     * onBackPressed
     */
    public void onBackPress() {
        try {
            clearAllStack();
            replaceFragment(R.id.container,
                    new DashBoardFragment((InterfaceButtonTextChange) getActivity()),
                    FragmentTag.DASHBOARD_FRAGMENT.name(), false);
        } catch (Exception e) {
            logException(e, "DashBoardFragment_onBackPress");
        }


    }

    @Override
    public void onButtonClick(boolean isCanceled, int callbackID) {
    }

    @Override
    public void onButtonClick(boolean isCanceled, int callbackID, int which) {
        try {
            if (isCanceled) {
                Constants.isManualIndividual = false;
                replaceFragment(R.id.container,
                        new CameraFragment((InterfaceButtonTextChange) getActivity()),
                        FragmentTag.CAMERA_FRAGMENT.name(), true);
            }
        } catch (Exception e) {
            logException(e, "DashBoardFragment_onButtonClick()");
        }

    }

    @Override
    public void onItemClick(int position) {
        if(handler!=null)
        {
            handler.removeCallbacks(runnableTimer);
        }
        is_volumeRunning=false;
        testController.unRegisterPowerButton();

        MainActivity mainActivity = (MainActivity) getActivity();

//        int positionData = testListModelList.get(position).getPosition();
        mainActivity.semiBackIndex = position;
        AutomatedTestListModel automatedTestListModel = testListModelList.get(position);
        if (automatedTestListModel.getTest_id() == 68) {
            automatedTestListModel.setTestDes(automatedTestListModel.getTestDes() + "<br/><br/> " +
                    "Battery Level:" + utils.getPreference(ctx, JsonTags.MMR_68.name(), ""));
        }
        replaceFragment(R.id.container, ManualTestsOperation.launchScreens(automatedTestListModel
                , androidId,getActivity()), testListModelList.get(position).getName(), true);
        mainActivity.index = position + 1;
//        mainActivity.semiIndex = position + 1;
        if(ManualTestFragment.viewPagerItemPOsition==1){
            mainActivity.semiIndex = position + 1;
        }else  if(ManualTestFragment.viewPagerItemPOsition==2){
            mainActivity.semiIndex = position + 1+partitionSize;
        }else  if(ManualTestFragment.viewPagerItemPOsition==3){
            mainActivity.semiIndex = position + 1+partitionSize+partitionSize;
        }


        Constants.isPagerElementTwoVisibleManual = false;

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
            //   logException(exp, "DashBoardFragment_ManualTestFragment()");
        }

    }


    /**
     * Volume Keys Event Handler
     */
    public void dispatchKeyEvent(KeyEvent event) {
        try {
            int action = event.getAction();
            int keyCode = event.getKeyCode();
            switch (keyCode) {
                case KeyEvent.KEYCODE_VOLUME_UP:
                    if (action == KeyEvent.ACTION_DOWN) {
                        if (!isVolIncrease) {
                            // utils.showToast(ctx, getResources().getString(R.string.txtVolumeUp));
                            isVolIncrease = true;
                            //updateTestValues();
                        }

                    } else {
                        //updateTestValues();
                    }
                    break;
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    if (action == KeyEvent.ACTION_DOWN) {
                        if (!isVolDecrease) {
                            isVolDecrease = true;
                            //   utils.showToast(ctx, getResources().getString(R.string
                            //   .txtVolumeDown));
                            //   updateTestValues();
                        }

                    } else {
                        // updateTestValues();
                    }
                    break;
            }
        } catch (Exception e) {
            logException(e, "DashBoardFragment_dispatchKeyEvent()");
        }

    }

    /**
     * This method update data to backend API after performing each test
     * update Test Data
     */
    public void updateResultToServer(int testId) {
        try {
            if (utils.getPreferenceBoolean(ctx, JsonTags.isTestDataChanged.name(), false)) {
                // testResultUpdateToServer.updateTestResult(null, true, 0);
                testResultUpdateToServer.updateTestResultToServer(false, testId, 1);

            }
        } catch (Exception e) {
            logException(e, "DashBoardFragment_updateResultToServer()");
        }


    }

    @Override
    public void update(Observable observable, Object o) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        Fragment frag = fm.findFragmentById(R.id.container);
        final AutomatedTestListModel automatedTestListModel = (AutomatedTestListModel) o;
        this.automatedTestListModel=automatedTestListModel;
        if (!list.contains(automatedTestListModel.getTest_id()))
            return;
        try {
            ViewPager pager = (ViewPager) ((ManualTestFragment) frag).pager;
            if (pager.getCurrentItem() == 0)
                return;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        if(frag instanceof ManualTestFragment) {

            if (automatedTestListModel.getTest_id() == ConstantTestIDs.VOLUME_ID && (automatedTestListModel.getIsTestSuccess() == AsyncConstant.TEST_PASS || automatedTestListModel.getIsTestSuccess() == AsyncConstant.TEST_FAILED))
                return;

            if (automatedTestListModel.getTest_id() == ConstantTestIDs.VOLUME_ID && automatedTestListModel.getIsTestSuccess() == AsyncConstant.TEST_FAILED) {
                isVolIncrease = false;
                isVolDecrease = false;
            }


            if (automatedTestListModel.getTest_id() == ConstantTestIDs.VOLUME_DOWN || automatedTestListModel.getTest_id() == ConstantTestIDs.VOLUME_UP) {
                implementVolumeFunctionality(automatedTestListModel);
            }
        }else {

            if (automatedTestListModel.getTest_id() == ConstantTestIDs.VOLUME_ID && automatedTestListModel.getIsTestSuccess() == AsyncConstant.TEST_FAILED) {
                isVolIncrease = false;
                isVolDecrease = false;
            }
        }
        if (automatedTestListModel.getTest_id() == ConstantTestIDs.HOME_ID && automatedTestListModel.getIsTestSuccess() == 1)
            onHomePressed();
        int testId = automatedTestListModel.getTest_id();


        if (automatedTestListModel.getIsTestSuccess() == 1) {
            if (frag instanceof ManualTestFragment) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!(automatedTestListModel.getName().contains("Volume Up") || automatedTestListModel.getName().contains("Volume Down"))) {


                            utils.showToast(ctx,
                                    automatedTestListModel.getName() + " " + ctx.getResources().getString(R.string.txtManualPass).toLowerCase());
                        }
                    }
                });

            }
            utils.compare_UpdatePreferenceInt(getActivity(), "MMR_" + testId,
                    AsyncConstant.TEST_PASS);
            sendTestData(SocketConstants.EVENT_TEST_END, testId,
                    Constants.COMPLETED, automatedTestListModel.getName(), "", androidId,
                    Constants.MANUAL1);
            testController.unRegisterSensor(testId);

        } else {
            if(!(automatedTestListModel.getName().contains("Volume Up")||automatedTestListModel.getName().contains("Volume Down"))) {

//                utils.showToast(ctx,
//                        automatedTestListModel.getName() + " " + ctx.getResources().getString(R.string.txtManualFail).toLowerCase());
                utils.compare_UpdatePreferenceInt(getActivity(), "MMR_" + testId,
                        AsyncConstant.TEST_FAILED);
                sendTestData(SocketConstants.EVENT_TEST_END, testId,
                        Constants.FAILED, automatedTestListModel.getName(), "", androidId,
                        Constants.MANUAL1);
            }
        }
        if (automatedTestListModel.getTest_id() == ConstantTestIDs.GYROSCOPE_ID)
            startVibrate();


        if (automatedTestListModel.getTest_id() == ConstantTestIDs.VOLUME_DOWN || automatedTestListModel.getTest_id() == ConstantTestIDs.VOLUME_UP)
            return;

        if (automatedTestListModel != null) {
            updateTestResult(automatedTestListModel.getTest_id(),
                    automatedTestListModel.getIsTestSuccess());
            updateResultToServer(testId);


        }
    }
//    private void implementVolumeFunctionality(AutomatedTestListModel automatedTestListModel) {
//
//        if(automatedTestListModel.getTest_id()==ConstantTestIDs.VOLUME_UP){
//            isVolIncrease=true;
//
////timer(ctx, false, ConstantTestIDs.VOLUME_ID, SemiAutomaticTestsFragment.this);
//            handler.removeCallbacks(runnableTimer);
//
//            handler.postDelayed(runnableTimer, 15000);
//        }
//        else if(automatedTestListModel.getTest_id() == ConstantTestIDs.VOLUME_DOWN){
//            isVolDecrease=true;
//
//            handler.removeCallbacks(runnableTimer);
//
////timer(ctx, false, ConstantTestIDs.VOLUME_ID, SemiAutomaticTestsFragment.this);
//            handler.postDelayed(runnableTimer, 15000);
//        }else {
//            handler.removeCallbacks(runnableTimer);
//        }
//
//        if(isVolIncrease && isVolDecrease) {
//            handler.removeCallbacks(runnableTimer);
//
//            testController.saveSkipResponse(1,ConstantTestIDs.VOLUME_ID);
//// testController.saveSkipResponse(1,ConstantTestIDs.VOLUME_UP);
//// testController.saveSkipResponse(1,ConstantTestIDs.VOLUME_DOWN);
//            realmOperations.updateTestResult(ConstantTestIDs.VOLUME_UP, AsyncConstant.TEST_PASS);
//            realmOperations.updateTestResult(ConstantTestIDs.VOLUME_DOWN, AsyncConstant.TEST_PASS);
//            updateTestResult(ConstantTestIDs.VOLUME_ID, AsyncConstant.TEST_PASS);
//
//            updateResultToServer(ConstantTestIDs.VOLUME_ID);
//            utils.showToast(ctx, getResources().getString(R.string.txtManualVolume_Btn)+ " " +getResources().getString(R.string.txtManualPass));
//
//
//        }else
//// if(!isVolIncrease && !isVolDecrease){
//            if(isVolDecrease || isVolIncrease){
//                handler.removeCallbacks(runnableTimer);
//                realmOperations.updateTestResult(ConstantTestIDs.VOLUME_UP, AsyncConstant.TEST_FAILED);
//                realmOperations.updateTestResult(ConstantTestIDs.VOLUME_DOWN, AsyncConstant.TEST_FAILED);
//                realmOperations.updateTestResult(ConstantTestIDs.VOLUME_ID, AsyncConstant.TEST_FAILED);
//                updateTestResult(ConstantTestIDs.VOLUME_ID, AsyncConstant.TEST_FAILED);
//            }else{
//                updateTestResult(ConstantTestIDs.VOLUME_ID, AsyncConstant.TEST_FAILED);
//            }
//
//
//
//
//    }








    private void implementVolumeFunctionality(AutomatedTestListModel automatedTestListModel) {

        if (automatedTestListModel.getTest_id() == ConstantTestIDs.VOLUME_UP) {
            isVolIncrease = true;

//timer(ctx, false, ConstantTestIDs.VOLUME_ID, SemiAutomaticTestsFragment.this);
            handler.removeCallbacks(runnableTimer);

            is_volumeRunning = true;
            handler.postDelayed(runnableTimer, 15000);
        } else if (automatedTestListModel.getTest_id() == ConstantTestIDs.VOLUME_DOWN) {
            isVolDecrease = true;

            handler.removeCallbacks(runnableTimer);

//timer(ctx, false, ConstantTestIDs.VOLUME_ID, SemiAutomaticTestsFragment.this);
            is_volumeRunning = true;
            handler.postDelayed(runnableTimer, 15000);
        } else {
            handler.removeCallbacks(runnableTimer);
        }

        if (isVolIncrease && isVolDecrease) {
            handler.removeCallbacks(runnableTimer);
            is_volumeRunning = false;

            realmOperations.updateTestResult(ConstantTestIDs.VOLUME_ID, AsyncConstant.TEST_PASS);
            realmOperations.updateTestResult(ConstantTestIDs.VOLUME_UP, AsyncConstant.TEST_PASS);
            realmOperations.updateTestResult(ConstantTestIDs.VOLUME_DOWN, AsyncConstant.TEST_PASS);
            updateTestResult(ConstantTestIDs.VOLUME_ID, AsyncConstant.TEST_PASS);

            updateResultToServer(ConstantTestIDs.VOLUME_ID);
            utils.showToast(ctx,
                    getResources().getString(R.string.txtManualVolume_Btn) + " " + getResources().getString(R.string.txtManualPass));


        } else

// if(!isVolIncrease && !isVolDecrease){
            if (isVolDecrease || isVolIncrease) {
// is_volumeRunning=false;
// handler.removeCallbacks(runnableTimer);
                realmOperations.updateTestResult(ConstantTestIDs.VOLUME_UP,
                        AsyncConstant.TEST_FAILED);
                realmOperations.updateTestResult(ConstantTestIDs.VOLUME_DOWN,
                        AsyncConstant.TEST_FAILED);
                realmOperations.updateTestResult(ConstantTestIDs.VOLUME_ID,
                        AsyncConstant.TEST_FAILED);
                updateTestResult(ConstantTestIDs.VOLUME_ID, AsyncConstant.TEST_FAILED);
            } else {
                updateTestResult(ConstantTestIDs.VOLUME_ID, AsyncConstant.TEST_FAILED);
            }


    }





    public void craeteRunnable() {
        if (handler == null) {
            handler = new Handler();
        }
        runnableTimer = new Runnable() {
            public void run() {
                is_volumeRunning = false;
                Log.d("running", "timeer");
                handler.removeCallbacks(runnableTimer);
                updateData();
                MainActivity mainActivity = (MainActivity) getActivity();
                if ( mainActivity.map.size() == ManualTestFragment.testListModelList.size() ) {
                    Log.d("stop", automatedTestListModel.getName()+".."+automatedTestListModel.getTest_id());
                    updateTestResult(automatedTestListModel.getTest_id(),
                            automatedTestListModel.getIsTestSuccess());
                }
            }
        };
    }
    private void updateData() {

        if(!isVolIncrease) {
            Utilities.getInstance(getActivity()).compare_UpdatePreferenceInt(getActivity(), "MMR_" + ConstantTestIDs.VOLUME_UP, 0);
//testController.saveSkipResponse(-1,ConstantTestIDs.VOLUME_UP);
            realmOperations.updateTestResult(ConstantTestIDs.VOLUME_UP, AsyncConstant.TEST_FAILED);
        }

        if(!isVolDecrease) {
            Utilities.getInstance(getActivity()).compare_UpdatePreferenceInt(getActivity(), "MMR_" + ConstantTestIDs.VOLUME_DOWN, 0);
// testController.saveSkipResponse(-1,ConstantTestIDs.VOLUME_DOWN);
            realmOperations.updateTestResult(ConstantTestIDs.VOLUME_DOWN, AsyncConstant.TEST_FAILED);

        }

        testController.saveSkipResponse(0,ConstantTestIDs.VOLUME_ID);

        updateResultToServer(ConstantTestIDs.VOLUME_ID);

        isVolIncrease = false;
        isVolDecrease = false;

        Utilities.getInstance(getActivity()).compare_UpdatePreferenceInt(getActivity(),"MMR_"+ConstantTestIDs.VOLUME_UP,-1);
        Utilities.getInstance(getActivity()).compare_UpdatePreferenceInt(getActivity(),"MMR_"+ConstantTestIDs.VOLUME_DOWN,-1);
        realmOperations.updateTestResult(ConstantTestIDs.VOLUME_UP, AsyncConstant.TEST_FAILED);
        realmOperations.updateTestResult(ConstantTestIDs.VOLUME_DOWN, AsyncConstant.TEST_FAILED);
        utils.showToast(ctx, getResources().getString(R.string.txtManualVolume_Btn)+ " "+getResources().getString(R.string.txtManualFail));

    }


    /**
     * Update Adapter on result change
     */
    public void updateTestResult(int testID, int teststauts) {
        try {

            int position = -1;
            for (int index = 0; index < testListModelList.size(); index++) {
                if (testID == testListModelList.get(index).getTest_id()) {

                    position = index;
                    break;
                }
            }
            if (position >= 0) {
                AutomatedTestListModel automatedTestListModel = testListModelList.get(position);
                automatedTestListModel.setIsTestSuccess(teststauts);
                testListModelList.remove(position);

                testListModelList.add(position, automatedTestListModel);
                mAdapter.notifyItemChanged(position);

                MainActivity mainActivity = (MainActivity) getActivity();

                if(pageNumber==1){
                    MainActivity.testListSemi.put(position,true);
                if(!mainActivity.skipMapSemi.contains(position)) {
                    mainActivity.skipMapSemi.add(position);
                }
                }else if(pageNumber==2){
                    MainActivity.testListSemi.put(position+partitionSize,true);
                    if(!mainActivity.skipMapSemi.contains(position+partitionSize)) {
                        mainActivity.skipMapSemi.add(position+partitionSize);
                    }
                }else if(pageNumber==3){
                    MainActivity.testListSemi.put(position+partitionSize+partitionSize,true);
                    if(!mainActivity.skipMapSemi.contains(position+partitionSize+partitionSize)) {
                        mainActivity.skipMapSemi.add(position+partitionSize+partitionSize);
                    }

                }





                if (!mainActivity.shouldAdd)
                    mainActivity.map.put(automatedTestListModel.getName(), automatedTestListModel.getTest_id());

                if (is_volumeRunning && mainActivity.map.size() == ManualTestFragment.testListModelList.size()) {
                    Log.d("stop", "stop");
                    return;
                }


//                TitleBarFragment fragment1 =
//                        (TitleBarFragment) ( getActivity()).getSupportFragmentManager().findFragmentById(R.id.headerContainer);
//                if (fragment1 != null) {
//
//                    ArrayList<DiagonsticObject> list = (ArrayList<DiagonsticObject>) fragment1.createDiagnoseMap().get("DiagnosticData");
//
//                    for(int index = 0;index<list.size();index++){
//                        Log.i("final_response_index", list.get(index).getDiagnosticID() + list.get(index).getDiagnosticResult());
//
//                    }
//
//                }
                fm = getActivity().getSupportFragmentManager();
                frag = fm.findFragmentById(R.id.container);
                if(  mainActivity.map.size()==ManualTestFragment.testListModelList.size() && !mainActivity.shouldAdd && (frag instanceof ManualTestFragment)){
                    mainActivity.shouldAdd = true;
                    TitleBarFragment fragment =
                            (TitleBarFragment) ( getActivity()).getSupportFragmentManager().findFragmentById(R.id.headerContainer);
                    if (fragment != null) {
                        Handler handler=new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                    //fragment.showSyncDialog();
                                    fragment.setSyntextVisibilty(false);
                                    fragment.openManualSummary();

                            }
                        },100);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
