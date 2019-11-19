package com.officework.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.officework.R;
import com.officework.activities.MainActivity;
import com.officework.activities.WelcomeActivity;
import com.officework.adapters.ViewPagerAdapter;
import com.officework.base.BaseFragment;
import com.officework.constants.Constants;
import com.officework.constants.Preferences;
import com.officework.interfaces.AutomateTestStartInterface;
import com.officework.interfaces.InterfaceButtonTextChange;
import com.officework.interfaces.ViewPagerDisableInterface;
import com.officework.models.AutomatedTestListModel;
import com.officework.testing_profiles.Controller.ManualTestsOperation;
import com.officework.testing_profiles.Controller.TestController;
import com.officework.testing_profiles.realmDatabase.operationManager.RealmOperations;
import com.officework.testing_profiles.ui.fragment.AutomatedTestFragment;
import com.officework.testing_profiles.ui.fragment.SemiAutomaticTestsFragment;
import com.officework.testing_profiles.utils.ConstantTestIDs;
import com.officework.utils.ExceptionLogUtils;
import com.officework.utils.NonSwipeableViewPager;
import com.officework.utils.Utilities;
import com.officework.utils.socket.SocketConstants;
import com.officework.utils.socket.SocketHelper;
import com.officework.utils.socket.SocketListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;

/**
 * Created by Girish on 8/11/2016.
 */
@SuppressLint("ValidFragment")
public class ManualTestFragment extends BaseFragment implements InterfaceButtonTextChange,
        ViewPagerDisableInterface {
    public static boolean iSManual = false;
    public static boolean isHomeButtonTested = false;
    public static int viewPagerItemPOsition = 0;
    public static int testIndex = 0;
    public static ArrayList<AutomatedTestListModel> testListModelList;
    private static String androidId;
    //    private ViewPager pager;
    public NonSwipeableViewPager pager;
    View view;
    Utilities utils;
    Context ctx;
    InterfaceButtonTextChange mCallBack;
    ViewPagerAdapter adapter;
    int position;
    AutomatedTestFragment automatedTestFragment;
    TestController testController;
    boolean isBackPressenabled = true;
    TitleBarFragment titleBarFragment;
    //  GetBarCodeUDIFragment getBarCodeUDIFragment;
    // public int viewPagerItemPOsition =0;
    AutomateTestStartInterface automateTestStartInterface;
    public ViewPager.OnPageChangeListener pageChangeListener =
            new ViewPager.OnPageChangeListener() {
                int currentPosition = position;

                @Override
                public void onPageSelected(int newPosition) {

                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    Fragment frag = fm.findFragmentById(R.id.headerContainer);
                    TitleBarFragment titleBarFragment = (TitleBarFragment) frag;
                    viewPagerItemPOsition = newPosition;
                    if (newPosition == 0) {
                        setTitle(R.string.txtAutomated);
                        ((TitleBarFragment) frag).showSwitchLayout(false);
//                        switchLayout.setVisibility(View.GONE);
                        if (utils.getPreferenceBoolean(ctx, Preferences.isAutomatedTestFirst.name(),
                                true)) {
                            if (automateTestStartInterface != null) {
                                automateTestStartInterface.automateTestStart();
                            }
                        }
                    } else if (newPosition == 1) {
                        setTitle(R.string.txtManual);
                        ((TitleBarFragment) frag).showSwitchLayout(true);

//                        switchLayout.setVisibility(View.VISIBLE);
                    } else if (newPosition == 2) {
                        setTitle(R.string.txtManual);
                        ((TitleBarFragment) frag).showSwitchLayout(true);

//                        switchLayout.setVisibility(View.VISIBLE);

                    } else {
//                        setTitle(R.string.txtBarcode);
//                        switchLayout.setVisibility(View.GONE);
//                        try {
//                            getBarCodeUDIFragment.stopTimer();
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
                    }
                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {

                }

                public void onPageScrollStateChanged(int arg0) {
                }
            };
    SocketListener socketListener = new SocketListener() {
        @Override
        public void onSocketReceived(String event, JSONObject jsonObject) {
            switch (event) {


                case SocketConstants.EVENT_DESKTOP_SCREEN_CHANGE:
                    try {

                        if (jsonObject != null && TextUtils.equals(jsonObject.getString(
                                "ScreenName"), Constants.MANUAL1)) {
                            pager.setCurrentItem(0);

                        } else if (jsonObject != null && TextUtils.equals(jsonObject.getString(
                                "ScreenName"), Constants.MANUAL2)) {
                            pager.setCurrentItem(1);

                        }
                        break;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;

                case SocketConstants.EVENT_TEST_START_RESPONSE:

                    if (jsonObject != null) {

                        try {
                            Fragment fragment = adapter.getItem(pager.getCurrentItem());

                            if (fragment instanceof SemiAutomaticTestsFragment) {
                                ((SemiAutomaticTestsFragment) fragment).onItemClick(jsonObject.getInt("TestId"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                case SocketConstants.EVENT_DEVICE_REMOVED:


                    //deviceRemoved();
                    break;
            }
        }
    };
    private List<Fragment> fragmentList = new ArrayList<>();
    private List<List<AutomatedTestListModel>> newList = new ArrayList<>();
    private TabLayout tabLayout;

    public ManualTestFragment(InterfaceButtonTextChange callback) {
        mCallBack = callback;
    }

    public ManualTestFragment(AutomateTestStartInterface automateTestStartInterface) {
        this.automateTestStartInterface = automateTestStartInterface;
    }

    public ManualTestFragment() {

    }

    public AutomateTestStartInterface getAutomateTestStartInterface() {
        return automateTestStartInterface;
    }

    public void setAutomateTestStartInterface(AutomateTestStartInterface automateTestStartInterface) {
        this.automateTestStartInterface = automateTestStartInterface;

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        try {
            if (view == null) {
                titleBarFragment = (TitleBarFragment) getFragment(R.id.headerContainer);
                view = inflater.inflate(R.layout.fragment_test_manual, null);
                utils = Utilities.getInstance(getActivity());
                ctx = getActivity();
                //    Crashlytics.getInstance().log(FragmentTag.MANUAL_TEST_FRAGMENT.name());
                if (getArguments() != null) {
                    viewPagerItemPOsition = getArguments().getInt("Position", 0);
                }
                socketHelper = createConnection(socketListener);
                androidId = utils.getPreference(getActivity(), Constants.ANDROID_ID, "");
                initViews();

                try {
                    final FragmentManager fragmentManager =
                            getActivity().getSupportFragmentManager();
                    fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
                        @Override
                        public void onBackStackChanged() {
                            Fragment fragment = fragmentManager.findFragmentById(R.id.container);
                            if (fragment instanceof ManualTestFragment) {
                                switch (((ManualTestFragment) fragment).pager.getCurrentItem()) {
                                    case 0:
                                        setTitle(R.string.txtAutomated);
                                        titleBarFragment.showSwitchLayout(false);
                                        break;

                                    case 1:
                                        setTitle(R.string.txtManual);
                                        break;

                                    case 2:
                                        setTitle(R.string.txtManual);
                                        break;
                                }
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return view;
        } catch (Exception e) {
            logException(e, "DashBoardFragment_initUI()");
            return null;
        }
    }

    /**
     * Initialize view
     */
    private void initViews() {
        try {

            RealmOperations realmOperations = new RealmOperations();
            testListModelList = realmOperations.fetchAllInteractiveTestList(MainActivity.id_array
                    , getActivity());
            if (MainActivity.testListSemi.size() == 0) {
                for (int i = 0; i < testListModelList.size(); i++) {
                    MainActivity.testListSemi.put(i, false);
                }
            }
            pager = (NonSwipeableViewPager) view.findViewById(R.id.photos_viewpager);
            setupViewPager(pager);
            setTitle(R.string.txtAutomated);
            tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
            tabLayout.setupWithViewPager(pager, true);
            pager.setCurrentItem(viewPagerItemPOsition);

            automatedTestFragment = new AutomatedTestFragment(ManualTestFragment.this);

            final SwitchCompat switchDoAll = (SwitchCompat) view.findViewById(R.id.switchDoAll);
            switchDoAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        emitPrivacyEvent(true);
                        //  switchDoAll.getTrackDrawable().setColorFilter(ContextCompat.getColor
                        //  (getActivity(), R.color.lightGray), PorterDuff.Mode.SRC_IN);
                        Constants.isDoAllClicked = true;

                    } else {
                        emitPrivacyEvent(false);
                        Constants.isDoAllClicked = false;
                    }
                }
            });
            pagerChange();

            switchDoAll.setChecked(Constants.isDoAllClicked);
            if (getArguments().getInt("Position") == 0) {
                setTitle(R.string.txtAutomated);
                titleBarFragment.showSwitchLayout(false);

            } else if (getArguments().getInt("Position") == 1) {
                setTitle(R.string.txtManual);

            }

            // getBarCodeUDIFragment = (GetBarCodeUDIFragment) adapter.getItem(3);
        } catch (Exception e) {
            logException(e, "DashBoardFragment_initViews()");
        }


    }

    private void emitPrivacyEvent(boolean fieldValue) {
        try {
            String qrCodeiD = Utilities.getInstance(getActivity()).getPreference(getActivity(),
                    Constants.QRCODEID, "");

            JSONObject jsonObject = new JSONObject();
            String androidId = Utilities.getInstance(getActivity()).getPreference(getActivity(),
                    Constants.ANDROID_ID, "");

            jsonObject.put(Constants.UNIQUE_ID, androidId);
            jsonObject.put(Constants.QRCODEID, qrCodeiD);
            jsonObject.put("Status", fieldValue);

            if (socketHelper != null && socketHelper.socket.connected()) {
                socketHelper.emitData(SocketConstants.EVENT_TOGGLE, jsonObject);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void pagerChange() {
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    setTitle(R.string.txtAutomated);

                    socketHelper.emitScreenChangeEvent("Automate", androidId,
                            MainActivity.qr_code_test);
                } else if (position == 1) {
                    socketHelper.emitScreenChangeEvent("Manual1", androidId,
                            MainActivity.qr_code_test);
                } else if (position == 2) {
                    socketHelper.emitScreenChangeEvent("Manual2", androidId,
                            MainActivity.qr_code_test);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

//    private void setupViewPager(ViewPager viewPager) {
//        try {
//            adapter = new ViewPagerAdapter(getChildFragmentManager());
//            adapter.addFragment(new AutomatedTestFragment(ManualTestFragment.this), "");
//            adapter.addFragment(new SemiAutomaticTestsFragment((InterfaceButtonTextChange)
//            getActivity()), "");
//            adapter.addFragment(new Manual2SemiAutomaticTestsFragment(
//            (InterfaceButtonTextChange) getActivity()), "");
//            viewPager.setAdapter(adapter);
//            //    viewPager.setOffscreenPageLimit(2);
//            viewPager.setOnPageChangeListener(pageChangeListener);
//            setTitle(R.string.txtAutomated);
//
//        } catch (Exception e) {
//
//        }
//
//    }


    private void setupViewPager(ViewPager viewPager) {
        try {
            createListAccordinfToScreenSize();

            fragmentList.add(new AutomatedTestFragment(ManualTestFragment.this));
            for (int i = 0; i < newList.size(); i++) {
                fragmentList.add(SemiAutomaticTestsFragment.newInstance(newList.get(i), i + 1));
            }
            // fragmentList.add(new ServiceBookingFragment());

            adapter = new ViewPagerAdapter(getChildFragmentManager(), fragmentList);
            //
//            adapter.addFragment(new AutomatedTestFragment(ManualTestFragment.this), "");
//            adapter.addFragment(new SemiAutomaticTestsFragment((InterfaceButtonTextChange)
//            getActivity()), "");
//            adapter.addFragment(new Manual2SemiAutomaticTestsFragment(
//            (InterfaceButtonTextChange) getActivity()), "");
//            adapter.addFragment(new ServiceBookingFragment(), "");

            viewPager.setOffscreenPageLimit(4);

            viewPager.setAdapter(adapter);
            viewPager.setOnPageChangeListener(pageChangeListener);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void createListAccordinfToScreenSize() {
        int screenSize =
                getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;

        switch (screenSize) {
            case Configuration.SCREENLAYOUT_SIZE_LARGE:


                createPartitionList(8);
                partitionSize = 8;
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                createPartitionList(8);
                partitionSize = 8;

                break;
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                createPartitionList(6);
                partitionSize = 6;

                break;
                    /*default:
                        newList.add(new ArrayList<AutomatedTestListModel>(objects.subList(0, 8)));
                        newList.add(new ArrayList<AutomatedTestListModel>(objects.subList(8, 16)));
                        newList.add(new ArrayList<AutomatedTestListModel>(objects.subList(16,
                        objects.size())));*/
        }

        Log.e("YYSe", String.valueOf(partitionSize));
    }

    private void createPartitionList(int partition) {
//        newList.add(new ArrayList<AutomatedTestListModel>(testListModelList.subList(0,
//        testListModelList.size()/2)));
//        newList.add(new ArrayList<AutomatedTestListModel>(testListModelList.subList
//        (testListModelList.size()/2, testListModelList.size())));
//        return;

        for (int index = 0; index < testListModelList.size(); index = index + partition) {

            if (index + partition > testListModelList.size()) {
                newList.add(new ArrayList<AutomatedTestListModel>(testListModelList.subList(index
                        , testListModelList.size())));
                break;

            }
            newList.add(new ArrayList<AutomatedTestListModel>(testListModelList.subList(index,
                    index + partition)));

        }
    }


    public void retestPerform(String test_type) {
        if (test_type.equals(Constants.AUTOMATE)) {
            setTitle(R.string.txtAutomated);
            AutomatedTestFragment.getInstance().retestPerform();
        } else {
            enableViewPager(true);
            setTitle(R.string.txtManual);
// manual test
        }
    }

    public void moveNext(String test_type) {
        enableViewPager(true);
        if (test_type.equals(Constants.AUTOMATE)) {
            setTitle(R.string.txtAutomated);
            AutomatedTestFragment.getInstance().moveNext();
            startNextTest();
        } else {
            setTitle(R.string.txtManual);

            TitleBarFragment fragment =
                    (TitleBarFragment) ((MainActivity) getActivity()).getSupportFragmentManager().findFragmentById(R.id.headerContainer);
            fragment.showSyncDialog();
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
     * onResume
     */
    @Override
    public void onResume() {

//        MainActivity activity = (MainActivity) getActivity();
        pager.setCurrentItem(viewPagerItemPOsition);
        //  pagerChange();
        if (viewPagerItemPOsition == 1) {
            socketHelper.emitScreenChangeEvent("Manual1", androidId,
                    MainActivity.qr_code_test);
        } else if (viewPagerItemPOsition == 2) {
            socketHelper.emitScreenChangeEvent("Manual2", androidId,
                    MainActivity.qr_code_test);
        }

        try {
            if (Utilities.syncdialog == null && TitleBarFragment.progressBar == null) {
                if (!socketHelper.socket.connected()) {
                    performAllManualTest();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (!socketHelper.socket.connected()) {
                performAllManualTest();
            }
        }
        super.onResume();
    }

    private void performAllManualTest() {
        testController = TestController.getInstance(getActivity());
        testController.performOperation(ConstantTestIDs.EAR_PHONE_ID);
        testController.performOperation(ConstantTestIDs.POWER_ID);
        testController.performOperation(ConstantTestIDs.GYROSCOPE_ID);
        testController.performOperation(ConstantTestIDs.HOME_ID);
        testController.performOperation(ConstantTestIDs.CHARGING_ID);
        testController.performOperation(ConstantTestIDs.Battery);
        testController.performOperation(ConstantTestIDs.PROXIMITY_ID);
    }

    private void unregisterSensors() {
        try {
            testController.unregisterGyroScope2();
            testController.unRegisterCharging();
            testController.unregisterBattery();
            testController.unregisterProximity();
            testController.unRegisterHome();
            testController.unRegisterEarJack();

        } catch (Exception e) {
            e.printStackTrace();
            logException(e, "DashBoardFragment_onPause()");
        }
    }


    @Override
    public void onPause() {
        super.onPause();

        unregisterSensors();

    }

    @Override
    public void onStop() {
        mCallBack.onChangeText(utils.BUTTON_NEXT, false);
        super.onStop();
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
            // logException(exp, "DashBoardFragment_ManualTestFragment()");
        }

    }

    public void startNextTest() {

        Fragment fragment = adapter.getItem(pager.getCurrentItem());

        if (fragment instanceof SemiAutomaticTestsFragment) {
            ((SemiAutomaticTestsFragment) fragment).onItemClick(0);
        }
    }

    public void openEditTest(AutomatedTestListModel automatedTestListModel) {
        replaceFragment(R.id.container, ManualTestsOperation.launchScreens(automatedTestListModel
                , androidId, getActivity()), automatedTestListModel.getName(), true);
    }

    @Override
    public void onChangeText(int text, boolean showButton) {

    }

    public void onBackPress() {

        if (viewPagerItemPOsition == 0 && !isBackPressenabled) {
            Toast.makeText(getActivity(), R.string.txtAlertBackPress, Toast.LENGTH_SHORT).show();
        } else {
            //  getActivity().finish();

            Utilities.getInstance(getActivity()).showAlert(getActivity(),
                    new Utilities.onAlertOkListener() {
                        @Override
                        public void onOkButtonClicked(String tag) {
                            if (Utilities.getInstance(getActivity()).isInternetWorking(getActivity())) {
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

                            Intent intent = new Intent(getActivity(), WelcomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

//                            getActivity().setResult(Activity.RESULT_OK);
//                            getActivity().finish();
                        }
                    }, Html.fromHtml(getResources().getString(R.string.rescan_msg)),
                    getResources().getString(R.string.rescan_head), "No", "Yes", "Sync");

//            clearAllStack();
//            replaceFragment(R.id.container,
//                    new DashBoardFragment((InterfaceButtonTextChange) getActivity()),
//                    FragmentTag.DASHBOARD_FRAGMENT.name(), false);
        }
    }

    public void onAutoTest(View v) {
    }

    void disabletabeLayout() {
        LinearLayout tabStrip = ((LinearLayout) tabLayout.getChildAt(0));
        for (int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }
    }

    void enabletabeLayout() {
        LinearLayout tabStrip = ((LinearLayout) tabLayout.getChildAt(0));
        for (int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return false;
                }
            });
        }
    }


    @Override
    public void enableViewPager(boolean value) {
        if (!value) {
            disabletabeLayout();
        } else {
            enabletabeLayout();
        }
        tabLayout.setEnabled(value);
        pager.setShouldScroll(value);
        isBackPressenabled = value;

    }


}
