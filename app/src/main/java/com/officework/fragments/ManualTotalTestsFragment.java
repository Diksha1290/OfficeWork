package com.officework.fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.SensorManager;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.officework.R;
import com.officework.adapters.InterfaceAdapterCallback;
import com.officework.adapters.SectionHeaderAdapter;
import com.officework.base.BaseFragment;
import com.officework.constants.AsyncConstant;
import com.officework.constants.Constants;
import com.officework.constants.FragmentTag;
import com.officework.constants.JsonTags;
import com.officework.constants.ManualConstants;
import com.officework.interfaces.InterfaceButtonTextChange;
import com.officework.models.AutomatedTestListModel;
import com.officework.utils.Utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class ManualTotalTestsFragment extends BaseFragment implements InterfaceButtonTextChange, InterfaceAdapterCallback {
    View view;
    Utilities utils;
    Context ctx;
    List<AutomatedTestListModel> objects;
    InterfaceButtonTextChange mCallBack;
    RecyclerView mRecyclerView;
    SectionHeaderAdapter mAdapter;
    boolean isBaroMetereExist;
    SensorManager sensorManager;
    public ManualTotalTestsFragment() {
        // Required empty public constructor
    }

    public ManualTotalTestsFragment(InterfaceButtonTextChange callback) {
        mCallBack = callback;
    }

    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        try {
            if (view == null) {
                view = inflater.inflate(R.layout.fragment_manual_total_tests, null);
                utils = Utilities.getInstance(getActivity());
                ctx = getActivity();
              //  Crashlytics.getInstance().log(FragmentTag.MANUAL_TOTAL_TESTS_FRAGMENT.name());
                initViews();
            }
            return view;
        } catch (Exception e) {
            return null;
        }

    }


    private void initViews() {
        CheckSensors();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.gridViewManualTotal);
        setGridAdapter();
    }

    private void CheckSensors() {

    }

    /**
     * set grid adapter
     */
    private void setGridAdapter() {
        try {
            objects = new ArrayList<>();
            for (int i = 0; i < Constants.arrManualTotalTestResources.length; i++) {
                AutomatedTestListModel object = new AutomatedTestListModel();
                object.setResource(Constants.arrManualTotalTestResources[i]);
                object.setName(getResources().getStringArray(R.array.arrManualTotalTestName)[i]);
                object.setTest_type(Constants.MANUAL);
                object.setIsTestSuccess(getDiagnoseStatus(i));
                objects.add(object);
            }
            //Your RecyclerView
            mRecyclerView.setHasFixedSize(true);
            mRecyclerView.setLayoutManager(new GridLayoutManager(ctx, 2));
            mAdapter = new SectionHeaderAdapter(ctx, objects, (InterfaceAdapterCallback) this);
            mRecyclerView.setAdapter(mAdapter);
        } catch (Exception e) {

        }

    }

    /**
     * get diagnose result from shared preferences
     *
     * @param testID
     * @return
     */
    private int getDiagnoseStatus(int testID) {
        try {
            int testStatus = AsyncConstant.TEST_IN_QUEUE;
            switch (testID) {


                case 0:
                    testStatus = checkMutipleHardware(JsonTags.MMR_37.name(), JsonTags.MMR_38.name());
                    break;
                case 1:
                    testStatus = checkMutipleHardware(JsonTags.MMR_47.name(), JsonTags.MMR_46.name());
                    break;
                case 2:
                    testStatus = checkSingleHardware(JsonTags.MMR_31.name());
                    break;

                case 3:
                    testStatus = checkSingleHardware(JsonTags.MMR_32.name());
                    break;
                case 4:
                    testStatus = checkSingleHardware(JsonTags.MMR_33.name());
                    break;

                case 5:
                    testStatus = checkSingleHardware(JsonTags.MMR_24.name());
                    break;
                case 6:
                    testStatus = checkSingleHardware(JsonTags.MMR_22.name());
                    break;
                case 7:
                    testStatus = checkSingleHardware(JsonTags.MMR_55.name());
                    break;
              //  case 8:
                  //  testStatus=checkSingleHardware(JsonTags.MMR_67.name());
                  //  break;

            /*case 11:
                testStatus = checkSingleHardware(JsonTags.MMR_30.name());
                break;*/


            }
            return testStatus;
        } catch (Exception e) {

            return -1;
        }

    }

    /**
     * check result for single test
     *
     * @param pref_Key
     * @return
     */
    public int checkSingleHardware(String pref_Key) {
        try {
            int testStatus;
            if (utils.getPreferenceInt(ctx, pref_Key, 0) == -1) {
                testStatus = AsyncConstant.TEST_IN_QUEUE;
            } else if (utils.getPreferenceInt(ctx, pref_Key, 0) == 0) {
                testStatus = AsyncConstant.TEST_FAILED;
            } else {
                testStatus = AsyncConstant.TEST_PASS;
            }
            return testStatus;
        } catch (Exception e) {

            return -1;
        }

    }

    /**
     * check result for combine test
     * EXP volume button test
     * Camera TEST
     *
     * @param pref_Key_1
     * @param pref_Key_2
     * @return
     */
    private int checkMutipleHardware(String pref_Key_1, String pref_Key_2) {
        try {
            int testStatus;
            if (utils.getPreferenceInt(ctx, pref_Key_1, 0) == -1 && utils.getPreferenceInt(ctx, pref_Key_2, 0) == -1) {
                testStatus = AsyncConstant.TEST_IN_QUEUE;
            } else if (utils.getPreferenceInt(ctx, pref_Key_1, 0) == 0 && utils.getPreferenceInt(ctx, pref_Key_2, 0) == 0) {
                testStatus = AsyncConstant.TEST_FAILED;
            } else if (utils.getPreferenceInt(ctx, pref_Key_1, 0) == 0 || utils.getPreferenceInt(ctx, pref_Key_2, 0) == 0) {
                testStatus = AsyncConstant.TEST_IN_PROGRESS;
            } else if (utils.getPreferenceInt(ctx, pref_Key_1, 0) == 1 && utils.getPreferenceInt(ctx, pref_Key_2, 0) == -2) {
                testStatus = AsyncConstant.TEST_PASS;
            } else {
                testStatus = AsyncConstant.TEST_PASS;
            }
            return testStatus;
        } catch (Exception e) {
            return -1;
        }

    }

    /**
     * Here change the header text
     ***/
    @Override
    public void onResume() {
      /*  try {
            TitleBarFragment fragment = (TitleBarFragment) getFragment(R.id.headerContainer);
            if (fragment != null) {
                fragment.setTitleBarVisibility(true);
                fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string.txtManualTestScreen), true, false, 0);
            }

        } catch (Exception e) {

        }*/
        super.onResume();

    }

    @Override
    public void onItemClick(int position) {
        Constants.isPagerElementTwoVisibleManual = true;
        switch (position) {
            case ManualConstants.MANUAL_CAMERA:
                Constants.index = 0;
                replaceFragment(R.id.container, new CameraFragment((InterfaceButtonTextChange) getActivity()), FragmentTag.CAMERA_FRAGMENT.name(), true);
                break;
            case ManualConstants.MANUAL_DISPLAY:
                Constants.index = 1;
                replaceFragment(R.id.container, new DisplayManualFragment((InterfaceButtonTextChange) getActivity()), FragmentTag.DISPLAY_FRAGMENT.name(), true);
                break;
            case ManualConstants.MANUAL_GPS:
                Constants.index = 2;
                replaceFragment(R.id.container, new GpsMapManualFragment((InterfaceButtonTextChange) getActivity()), FragmentTag.GPS_FRAGMENT.name(), true);
                break;
            case ManualConstants.MANUAL_SPEAKER:
                Constants.index = 3;
                replaceFragment(R.id.container, new SpeakerManualFragment((InterfaceButtonTextChange) getActivity()), FragmentTag.SPEAKER_MANUAL_FRAGMENT.name(), true);
                break;
            case ManualConstants.MANUAL_MIC:
                Constants.index = 4;
                replaceFragment(R.id.container, new MicManualFragment((InterfaceButtonTextChange) getActivity()), FragmentTag.MIC_MANUAL_FRAGMENT.name(), true);
                break;
            case ManualConstants.MANUAL_SCREEN_TOUCH:
                Constants.index = 5;
                replaceFragment(R.id.container, new TouchScreenCanvasManualFragment((InterfaceButtonTextChange) getActivity()), FragmentTag.SCREEN_TOUCH_FRAGMENT.name(), true);
                break;
            case ManualConstants.MANUAL_MUTITOUCH:
                Constants.index = 6;
                replaceFragment(R.id.container, new MutitouchManualFragment((InterfaceButtonTextChange) getActivity()), FragmentTag.MUTITOUCH_FRAGMENT.name(), true);
                break;
            case ManualConstants.DEVICE_CASING:
                Constants.index = 7;
                replaceFragment(R.id.container, new DeviceCasingTestFragment((InterfaceButtonTextChange) getActivity()), FragmentTag.DEVICE_CASING_FRAGMENT.name(), true);
                break;
            case ManualConstants.MANUAL_FLASH:
                Constants.index = 8;
                replaceFragment(R.id.container, new FlashTestFragment((InterfaceButtonTextChange) getActivity()), FragmentTag.FLASH_TEST_FRAGMENT.name(), true);
                break;
            case ManualConstants.MANUAL_COMPASS:
                Constants.index = 9;
                replaceFragment(R.id.container, new CompassTestFragment((InterfaceButtonTextChange) getActivity()), FragmentTag.COMPASS_TEST_FRAGMENT.name(), true);
                break;
            case ManualConstants.MANUAL_FINGERPRINT:
                Constants.index = 10;
                replaceFragment(R.id.container, new FingerPrintTestFragment((InterfaceButtonTextChange) getActivity()), FragmentTag.FINGERPRINT_TEST_FRAGMENT.name(), true);
                break;
            case ManualConstants.MANUAL_ACCELEROMETER:
                Constants.index = 11;
                replaceFragment(R.id.container, new AccelerometerTestFragment((InterfaceButtonTextChange) getActivity()), FragmentTag.ACCELEROMETER_TEST_FRAGMENT.name(), true);
                break;
            case ManualConstants.MANUAL_FaceDetection:
                Constants.index=12;
                replaceFragment(R.id.container,new FaceDetectionFragment((InterfaceButtonTextChange)getActivity()),FragmentTag.FACEDETECTION_TEST_FRAGMENT.name(),true);
                break;
            case ManualConstants.MANUAL_Barometer:
                Constants.index=13;
                replaceFragment(R.id.container,new BaroMetereFragment((InterfaceButtonTextChange)getActivity()),FragmentTag.BAROMETER_TEST_FRAGMENT.name(),true);
                break;

        }
    }

    @Override
    public void onChangeText(int text, boolean showButton) {

    }

    @Override
    public void onPause() {
        super.onPause();
    }

}
