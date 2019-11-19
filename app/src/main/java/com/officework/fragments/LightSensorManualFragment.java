package com.officework.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.officework.R;
import com.officework.activities.MainActivity;
import com.officework.base.BaseFragment;
import com.officework.constants.Constants;
import com.officework.constants.JsonTags;
import com.officework.customViews.IconView;
import com.officework.interfaces.InterfaceButtonTextChange;
import com.officework.models.AutomatedTestListModel;
import com.officework.testing_profiles.Controller.ManualTestsOperation;
import com.officework.testing_profiles.Controller.TestController;
import com.officework.testing_profiles.utils.ConstantTestIDs;
import com.officework.utils.ExceptionLogUtils;
import com.officework.utils.ManualDataStable;
import com.officework.utils.TestResultUpdateToServer;
import com.officework.utils.Utilities;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by Ashwani on 8/18/2016.
 */
@SuppressLint("ValidFragment")
public class LightSensorManualFragment extends BaseFragment implements Observer {
    View view;
    Utilities utils;
    Context ctx;
    IconView mImgViewLight;
    InterfaceButtonTextChange mCallBack;
    RelativeLayout lighFunLayout;
    ManualDataStable manualDataStable;
    boolean isTestPerformed = false;
    Handler nextButtonHandler = null;

    CountDownTimer cTimer = null;
    boolean isDialogShown = false;
    AlertDialog alertDialog;
    TestResultUpdateToServer testResultUpdateToServer;
    private TestController testController;
    MainActivity mainActivity;

    @Override
    public void onStop() {

        mCallBack.onChangeText(utils.BUTTON_NEXT, false);

        super.onStop();
    }
    public LightSensorManualFragment(InterfaceButtonTextChange callback) {
        mCallBack = callback;
    }

    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        try {
            if (view == null) {
                view = inflater.inflate(R.layout.fragment_manual_light_sensor, null);
                utils = Utilities.getInstance(getActivity());
                manualDataStable = new ManualDataStable(mCallBack);
                ctx = getActivity();
            //    Crashlytics.getInstance().log(FragmentTag.LIGHT_SENSOR_FRAGMENT.name());
                testResultUpdateToServer = new TestResultUpdateToServer(utils, ctx, getActivity());
                 testController = TestController.getInstance(getActivity());
                testController.addObserver(this);
                testController.performOperation(ConstantTestIDs.LIGHT_SENSOR_ID);
                initViews();
            }
            return view;
        }catch (Exception e){
            logException(e, "LightSensorManualFragment_initUI()");
            return null;
        }

    }

    /**
     * Initialize view
     */

    private void initViews() {
        try {

            mainActivity = (MainActivity)getActivity();
            mainActivity.onChangeText(R.string.textSkip,true);
            alertDialog = new AlertDialog.Builder(
                    ctx).create();


            mImgViewLight = (IconView) view.findViewById(R.id.imgViewLigtSensor);
            lighFunLayout = (RelativeLayout) view.findViewById(R.id.lighFunLayout);


        } catch (Exception e) {
            logException(e, "LightSensorManualFragment_initViews()");
        }

    }

    public LightSensorManualFragment() {
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
                fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string.LightSensor), false, false, 0);
                nextButtonHandler = new Handler();
                if (Constants.isBackButton == true) {
                    if (manualDataStable.checkSingleHardware(JsonTags.MMR_26.name(), ctx, utils) == 1) {
                        mCallBack.onChangeText(utils.BUTTON_NEXT, true);
                        Constants.isBackButton = false;
                    } else {
                        mCallBack.onChangeText(utils.BUTTON_SKIP, true);
                        Constants.isBackButton = false;
                    }
                } else {

                }
            }

        } catch (Exception e) {
            logException(e, "LightSensorManualFragment_onResume()");
        }


        super.onResume();
    }

    /**
     * onPause
     */
    @Override
    public void onPause() {
        try {
            nextButtonHandler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            logException(e, "LightSensorManualFragment_onPause()");
        }

        super.onPause();
    }




    /**
     * Called when test is completed or user click on skip button
     */
    public void onNextPress() {
        try {
            MainActivity mainActivity = (MainActivity)getActivity();
            mainActivity.onChangeText(R.string.textSkip,false);
            if (Constants.isSkipButton) {
                mImgViewLight.setImageDrawable(getResources().getDrawable(R.drawable.ic_light_green_svg_168),false,getActivity());
                utils.showToast(ctx, getResources().getString(R.string.txtManualFail));
               // utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_28.name(), AsyncConstant.TEST_FAILED);
              //  updateResultToServer();
                mCallBack.onChangeText(utils.BUTTON_SKIP, false);

            }
            mCallBack.onChangeText(utils.BUTTON_NEXT, false);

            if (Constants.isDoAllClicked && mainActivity.index != mainActivity.automatedTestListModels.size()) {


                AutomatedTestListModel automatedTestListModel= mainActivity.automatedTestListModels.get(mainActivity.index);
                replaceFragment(R.id.container, ManualTestsOperation.launchScreens(automatedTestListModel, utils.getPreference(getActivity(), Constants.ANDROID_ID, ""),getActivity()), null, false);
                mainActivity.index++;

            } else {
//                clearAllStack();
//                replaceFragment(R.id.container, ManualTestsOperation.launchScreens(-1,mainActivity.automatedTestListModels.get(mainActivity.automatedTestListModels.size()-1).getTest_id()), FragmentTag.GPS_FRAGMENT.name(), true);

                popFragment(R.id.container);
            }
        } catch (Exception e) {
            logException(e, "LightSensorManualFragment_onNextPress()");
        }


    }

    /**
     * onBackPressed
     */
    public void onBackPress() {
        try {
            snackShow(lighFunLayout);
        } catch (Exception e) {
            logException(e, "LightSensorManualFragment_onBackPress()");
        }

    }

    /**
     * show snack bar when user click on back button
     *

    /**
     * This method update data to backend API after performing each test
     * update Test Data
     */

//    public void updateResultToServer() {
//        try {
//            if (utils.getPreferenceBoolean(ctx, JsonTags.isTestDataChanged.name(), false)) {
//                testResultUpdateToServer.updateTestResult(null, true, 0);
//            }
//        } catch (Exception e) {
//            logException(e, "LightSensorManualFragment_updateResultToServer()");
//        }
//
//
//    }

    public void logException(Exception e, String methodName) {
        try {
            Utilities utilities = Utilities.getInstance(getActivity());
            Activity activity = getActivity();
            Context context = getActivity();
            ExceptionLogUtils exceptionLogUtils = new ExceptionLogUtils(utilities, context, activity);
            exceptionLogUtils.addExceptionLog(utilities, context, activity, null, null, e, methodName);
        } catch (Exception exp) {
         //   logException(exp, "LightSensorManualFragment_logException()");
        }

    }

    @Override
    public void update(Observable observable, Object o) {

        if (!isTestPerformed) {
            mImgViewLight.setImageDrawable(getResources().getDrawable(R.drawable.ic_light_green_svg_168),true,getActivity());
            isTestPerformed = true;
            mCallBack.onChangeText(utils.BUTTON_NEXT, true);
           // utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_28.name(), AsyncConstant.TEST_PASS);
           // updateResultToServer();
            mainActivity.onChangeText(R.string.textSkip,false);
            utils.showToast(ctx, getResources().getString(R.string.txtManualPass));
            nextButtonHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Constants.isManualIndividual = false;
                    nextButtonHandler.removeCallbacksAndMessages(null);
                    onNextPress();
                }
            }, 2000);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            testController.deleteObserver(this);

        }
        catch (Exception e){
            e.printStackTrace();
        }        testController.unregisterLightSensor();

    }
}

