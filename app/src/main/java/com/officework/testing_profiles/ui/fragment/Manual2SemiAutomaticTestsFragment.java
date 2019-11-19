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
import android.os.Vibrator;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
public class Manual2SemiAutomaticTestsFragment extends BaseFragment implements Observer,
        InterfaceAdapterCallback, AdapterView.OnItemClickListener, InterfaceAlertDissmiss {

    public static List<AutomatedTestListModel> testListModelList;
    public static boolean isHomeButtonTested = false;
    public Vibrator vibrator;
    public List<Integer> list = new ArrayList<>();
    View view;
    Utilities utils;
    Context ctx;
    InterfaceButtonTextChange mCallBack;
    RecyclerView mRecyclerView;
    SectionHeaderAdapter mAdapter;
    BroadcastReceiver mRegistrationBroadcastReceiver;
    FragmentManager fm;
    Fragment frag;
    boolean isShowToast;
    int count = 0;
    boolean faceexists = true;
    //Power Button Test
    IntentFilter filter;
    long pattern[] = {0, 100, 200};
    //Jack Receiver
    BroadcastReceiver mReceiverJack = null;
    boolean isVolIncrease = false, isVolDecrease = false;
    TestResultUpdateToServer testResultUpdateToServer;
    boolean is_topmove, isbottom_move = true;
    /**
     * Test performed or not
     */

    boolean isBackGround = false;
    boolean proxyCheck = false;
    String androidId;
    private RealmOperations realmOperations;
    private TestController testController;
    private SensorManager sensorManager;
    private boolean isLightSensorExist;
    private boolean isBaroMetereExist;
    private GridLayoutManager gridlauoutManager;

    public Manual2SemiAutomaticTestsFragment() {
        // Required empty public constructor
    }


    public Manual2SemiAutomaticTestsFragment(InterfaceButtonTextChange callback) {
        mCallBack = callback;
    }

    /**
     * onDestroy view
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();

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

                //Crashlytics.log(FragmentTag.SEMI_AUTOMATIC_TESTS_FRAGMENT.name());
                testController = TestController.getInstance(getActivity());
                testController.addObserver(this);
                list = realmOperations.fetchallTestid(MainActivity.id_array, Constants.MANUAL,
                        getActivity());
                initViews();
            }
            return view;
        } catch (Exception e) {
            return null;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initViews() {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.gridViewSemiAutomatic);

        androidId = utils.getPreference(getActivity(), Constants.ANDROID_ID, "");
        Constants.isManualIndividual = false;
        if (Constants.isManualClickScroolController) {
            Constants.index = 0;
            Constants.isManualClickScroolController = false;
        }
        gridlauoutManager = new GridLayoutManager(ctx, 2);
        setGridAdapter();
        testResultUpdateToServer = new TestResultUpdateToServer(utils, ctx, getActivity());
        //create instance of sensor manager and get system service to interact with Sensor


        //Gyroscope Receiver
        intializeVibrator();


        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Constants.onVolumeKeyPressed)) {

                    Bundle extras = intent.getExtras();
                    if (extras == null) {
                        /*newString= null;*/
                    } else {

                        if (Constants.isSemiAutoVisible) {
                            dispatchKeyEvent((KeyEvent) extras.get("event"));
                        }


                    }
                }
            }
        };


        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d("position",gridlauoutManager.findLastVisibleItemPosition()+".."+gridlauoutManager.findFirstVisibleItemPosition());
                if (gridlauoutManager.findLastVisibleItemPosition() == gridlauoutManager.getItemCount() - 1) {
                    Log.d("bottom", "bootom");
                    if (isbottom_move) {
                        isbottom_move = false;
                        socketHelper.emitScrollChangeEvent(SocketConstants.SCROLL_DOWN,
                                utils.getPreference(getActivity(),
                                        Constants.ANDROID_ID, ""),
                                MainActivity.qr_code_test);
                        is_topmove = true;
                    }
                }
                else if (gridlauoutManager.findFirstVisibleItemPosition() == 0  ) {
                    if (is_topmove) {
                        is_topmove=false;
                        socketHelper.emitScrollChangeEvent(SocketConstants.SCROLL_UP,
                                utils.getPreference(getActivity(),
                                        Constants.ANDROID_ID, ""),
                                MainActivity.qr_code_test);
                        isbottom_move=true;
                    }
                }

//                    socketHelper.emitScrollChangeEvent(SocketConstants.SCROLL_DOWN, utils
//                    .getPreference(getActivity(),
//                            Constants.ANDROID_ID, ""),
//                            MainActivity.qr_code_test);


            }
        });


    }


    /**
     * set grid adapter
     */
    private void setGridAdapter() {
        try {
            testListModelList = realmOperations.fetchTestsList(MainActivity.id_array,
                    Constants.MANUAL, getActivity());
            MainActivity mainActivity = (MainActivity) getActivity();

            if(MainActivity.testListManual.size()==0) {
                for (int i = 0; i < testListModelList.size(); i++) {
                    MainActivity.testListManual.put(i, false);
                }
            }

            mainActivity.automatedTestListModels = testListModelList;
            //Your RecyclerView
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(gridlauoutManager);
            fm = getActivity().getSupportFragmentManager();
            frag = fm.findFragmentById(R.id.container);
            mAdapter = new SectionHeaderAdapter(ctx, testListModelList,
                    (InterfaceAdapterCallback) this);
            mRecyclerView.setAdapter(mAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * Here change the header text
     ***/
    @Override
    public void onResume() {
        super.onResume();
        try {
            TitleBarFragment fragment = (TitleBarFragment) getFragment(R.id.headerContainer);
            FragmentManager fm = getActivity().getSupportFragmentManager();
            frag = fm.findFragmentById(R.id.container);
            MainActivity mainActivity = (MainActivity) getActivity();
            if(mainActivity.map.size()==ManualTestFragment.testListModelList.size() && mainActivity.shouldAdd && TitleBarFragment.progressBar == null){
                if (fragment != null) {
                    fragment.setSyntextVisibilty(true);
                }
            }
            if (mainActivity.map.size() == ManualTestFragment.testListModelList.size()  && !mainActivity.shouldAdd ) {
                mainActivity.shouldAdd = true;
                if (fragment != null) {
                    Handler handler=new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fragment.setSyntextVisibilty(false);
                            fragment.openManualSummary();
                            //fragment.showSyncDialog();

                        }
                    },100);
                }
            }
            if (fragment != null) {
                mCallBack.onChangeText(utils.BUTTON_SKIP, false);
                isBackGround = false;

            }

//            socketHelper.emitScreenChangeEvent("Manual2", utils.getPreference(getActivity(),
//            Constants.ANDROID_ID, ""),
//                    MainActivity.qr_code_test);
            // performAllManualTest();
            super.onResume();
        } catch (Exception e) {

        }

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

        try {

            isBackGround = true;

//            testController.unRegisterHomeAndCharging();


        } catch (Exception e) {
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
                utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_26.name(),
                        AsyncConstant.TEST_PASS);

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
        testController.unRegisterPowerButton();

        MainActivity mainActivity = (MainActivity) getActivity();

        int positionData = testListModelList.get(position).getPosition();
        mainActivity.manualBackIndex = position;
        AutomatedTestListModel automatedTestListModel = testListModelList.get(position);
        replaceFragment(R.id.container, ManualTestsOperation.launchScreens(automatedTestListModel
                , utils.getPreference(getActivity(), Constants.ANDROID_ID, ""),getActivity()),
                testListModelList.get(position).getName(), true);
        mainActivity.index = position + 1;
        mainActivity.manualIndex = position + 1;
        final GridLayoutManager layoutManager =
                (GridLayoutManager) (mRecyclerView.getLayoutManager());
        /**
         * Scroll to position on index value
         */
        layoutManager.scrollToPosition(position);
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
                        // updateTestValues();
                    }
                    break;
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    if (action == KeyEvent.ACTION_DOWN) {
                        if (!isVolDecrease) {
                            isVolDecrease = true;
                            //   utils.showToast(ctx, getResources().getString(R.string
                            //   .txtVolumeDown));
                            //  updateTestValues();
                        }

                    } else {
                        //  updateTestValues();
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
                //testResultUpdateToServer.updateTestResult(null, true, 0);
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
        AutomatedTestListModel automatedTestListModel = (AutomatedTestListModel) o;
        if (!list.contains(automatedTestListModel.getTest_id()))
            return;
        if (automatedTestListModel.getTest_id() == ConstantTestIDs.HOME_ID && automatedTestListModel.getIsTestSuccess() == 1)
            onHomePressed();
        int testId = automatedTestListModel.getTest_id();
        if (automatedTestListModel.getIsTestSuccess() == 1) {
            if (frag instanceof SemiAutomaticTestsFragment) {
                utils.showToast(ctx,
                        automatedTestListModel.getName() + " " + ctx.getResources().getString(R.string.txtManualPass).toLowerCase());
            }
            utils.compare_UpdatePreferenceInt(getActivity(), "MMR_" + testId,
                    AsyncConstant.TEST_PASS);

            sendTestData(SocketConstants.EVENT_TEST_END, testId,
                    Constants.COMPLETED, automatedTestListModel.getName(), "", androidId,
                    Constants.MANUAL2);

        } else {
            utils.compare_UpdatePreferenceInt(getActivity(), "MMR_" + testId,
                    AsyncConstant.TEST_FAILED);
            sendTestData(SocketConstants.EVENT_TEST_END, testId,
                    Constants.FAILED, automatedTestListModel.getName(), "", androidId,
                    Constants.MANUAL2);
        }
        if (automatedTestListModel.getTest_id() == ConstantTestIDs.GYROSCOPE_ID)
            startVibrate();


        if (automatedTestListModel.getTest_id() == ConstantTestIDs.VOLUME_DOWN || automatedTestListModel.getTest_id() == ConstantTestIDs.VOLUME_UP)
            return;

        if (automatedTestListModel != null) {
            updateTestResult(automatedTestListModel.getTest_id(),
                    automatedTestListModel.getIsTestSuccess(),frag);
            updateResultToServer(testId);


        }


    }


    /**
     * Update Adapter on result change
     */
    public void updateTestResult(int testID, int teststauts,Fragment frag1) {
        try {

            int position = -1;
            for (int index = 0; index < testListModelList.size(); index++) {
                if (testID == testListModelList.get(index).getTest_id()) {

                    position = index;
                    break;
                }
            }

//
//            TitleBarFragment fragment1 =
//                    (TitleBarFragment) ( getActivity()).getSupportFragmentManager().findFragmentById(R.id.headerContainer);
//            if (fragment1 != null) {
//
//                ArrayList<DiagonsticObject> list = (ArrayList<DiagonsticObject>) fragment1.createDiagnoseMap().get("DiagnosticData");
//
//                for(int index = 0;index<list.size();index++){
//                    Log.i("final_response_index"+index, list.get(index).getDiagnosticID() + list.get(index).getDiagnosticResult());
//
//                }
//
//            }

            if (position >= 0) {

                AutomatedTestListModel automatedTestListModel = testListModelList.get(position);
                automatedTestListModel.setIsTestSuccess(teststauts);
                testListModelList.remove(position);

                testListModelList.add(position, automatedTestListModel);
                mAdapter.notifyItemChanged(position);

                MainActivity mainActivity = (MainActivity) getActivity();

                MainActivity.testListManual.put(position,true);
                if(!mainActivity.skipMapManual.contains(position)) {
                    mainActivity.skipMapManual.add(position);
                }
                if (!mainActivity.shouldAdd)
                    mainActivity.map.put(automatedTestListModel.getName(),
                            automatedTestListModel.getTest_id());


                fm = getActivity().getSupportFragmentManager();
                frag = fm.findFragmentById(R.id.container);
                if (mainActivity.map.size() == ManualTestFragment.testListModelList.size()  && !mainActivity.shouldAdd && (frag instanceof ManualTestFragment)) {
                    mainActivity.shouldAdd = true;
                    TitleBarFragment fragment =
                            (TitleBarFragment) (getActivity()).getSupportFragmentManager().findFragmentById(R.id.headerContainer);
                    if (fragment != null) {
                        Handler handler=new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                    fragment.setSyntextVisibilty(false);
                                    fragment.openManualSummary();
                                  //  fragment.showSyncDialog();

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
