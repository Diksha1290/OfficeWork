package com.officework.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.officework.R;
import com.officework.activities.MainActivity;
import com.officework.base.BaseFragment;
import com.officework.constants.AsyncConstant;
import com.officework.constants.Constants;
import com.officework.constants.JsonTags;
import com.officework.customViews.IconView;
import com.officework.interfaces.InterfaceButtonTextChange;
import com.officework.interfaces.TimerDialogInterface;
import com.officework.models.AutomatedTestListModel;
import com.officework.testing_profiles.Controller.TestController;
import com.officework.testing_profiles.utils.ConstantTestIDs;
import com.officework.utils.ExceptionLogUtils;
import com.officework.utils.ManualDataStable;
import com.officework.utils.TestResultUpdateToServer;
import com.officework.utils.Utilities;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by Girish on 8/9/2016.
 */
@SuppressLint("ValidFragment")
public class ChargingManualFragment extends BaseFragment implements Observer , TimerDialogInterface {
    View view;
    Utilities utils;
    Context ctx;
    IconView mImgViewCharger;
    InterfaceButtonTextChange mCallBack;
    LinearLayout chargingLayout;
    ManualDataStable manualDataStable;
    boolean isTestPerformed = false;
    //Handler nextButtonHandler = null;
    CountDownTimer cTimer = null;
    boolean isDialogShown = false;
    boolean isBackGround = false;
    AlertDialog alertDialog;
    TestResultUpdateToServer testResultUpdateToServer;
    private TestController testController;
    MainActivity activity;

    public ChargingManualFragment(InterfaceButtonTextChange callback) {
        mCallBack = callback;
    }

    public ChargingManualFragment() {
    }

    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {

        try {
            if (view == null) {
                view = inflater.inflate(R.layout.fragment_manual_charging, null);
                manualDataStable = new ManualDataStable(mCallBack);
                utils = Utilities.getInstance(getActivity());
                ctx = getActivity();
               // Crashlytics.getInstance().log(FragmentTag.CHARGING_PORT_FRAGMENT.name());

                 testController= TestController.getInstance(getActivity());
                testController.addObserver(this);


                testResultUpdateToServer = new TestResultUpdateToServer(utils, ctx, getActivity());
                initViews();

            }
            return view;
        } catch (Exception e) {
            logException(e, "ChargingManualFragment_initUI()");
            return null;

        }

    }

    /**
     * Initialize view
     */
    private void initViews() {
        try {
            activity = (MainActivity) getActivity();
            activity.onChangeText(R.string.textSkip,true);
            alertDialog = new AlertDialog.Builder(
                    ctx).create();
            mImgViewCharger = (IconView) view.findViewById(R.id.imgViewChargingPort);
            chargingLayout = (LinearLayout) view.findViewById(R.id.chargingLayout);
            timer(ctx,false,ConstantTestIDs.CHARGING_ID,ChargingManualFragment.this);
        } catch (Exception e) {
            logException(e, "ChargingManualFragment_initViews()");
        }

    }

    /**
     * Here change the header text
     ***/
    @Override
    public void onResume() {
        try {

            if(dialog==null || !dialog.isShowing()) {
                testController.performOperation(ConstantTestIDs.CHARGING_ID);
            }
//            testController.unRegisterPowerButton();
            TitleBarFragment fragment = (TitleBarFragment) getFragment(R.id.headerContainer);
            if (fragment != null) {
//                fragment.setTitleBarVisibility(true);
//                fragment.setSyntextVisibilty(false);
//
//                fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string.ChargingPort), false, false, 0);

                if (isTestPerformed) {
                    mCallBack.onChangeText(Utilities.BUTTON_NEXT, true);

                  onNextPress();

                } else {
                    mCallBack.onChangeText(Utilities.BUTTON_SKIP, true);
                }
            }
            isBackGround = false;

            if (Constants.isBackButton == true) {
                Constants.isBackButton = false;
                if (manualDataStable.checkSingleHardware(JsonTags.MMR_42.name(), ctx, utils) == 1) {
                    mCallBack.onChangeText(utils.BUTTON_NEXT, true);
                    Constants.isBackButton = false;
                } else {
                    mCallBack.onChangeText(utils.BUTTON_SKIP, true);
                    Constants.isBackButton = false;
                }

            }

        } catch (Exception e) {
            logException(e, "ChargingManualFragment_onResume()");
        }

        super.onResume();
    }

    @Override
    public void onStop() {

        mCallBack.onChangeText(utils.BUTTON_NEXT, false);

        super.onStop();
    }

    /**
     * onPause
     */
    @Override
    public void onPause() {

        super.onPause();
        try {
testController.unRegisterCharging();
            isBackGround = true;
        } catch (Exception e) {
            logException(e, "ChargingManualFragment_onPause()");
        }

    }

    /**
     * unregister charging receiver
     */


    /**
     * Callled when test is completed or user click on skip button
     */
    public void onNextPress() {

        try {
            timer(ctx,true,ConstantTestIDs.CHARGING_ID,null);
            MainActivity activity = (MainActivity) getActivity();
//            activity.onChangeText(R.string.textSkip,false);
            activity.onChangeText(R.string.textSkip,false);

            if (Constants.isSkipButton) {
                try {
                    testController.unRegisterCharging();

                }
                catch (Exception e){
                    e.printStackTrace();
                }

                isTestPerformed=true;
                mImgViewCharger.setImageDrawable(getResources().getDrawable(R.drawable.ic_charging_green_svg_128),false,getActivity());
                utils.showToast(ctx, getResources().getString(R.string.txtManualFail));

            }
            boolean semi=true;
            nextPress(activity,semi);

        } catch (Exception e) {
            logException(e, "ChargingManualFragment_onNextPress()");
        }


    }

    /**
     * onBackPressed
     */
    public void onBackPress() {
        try {
            snackShow(chargingLayout);
        } catch (Exception e) {
            logException(e, "ChargingManualFragment_onBackPress()");
        }

    }



    public void logException(Exception e, String methodName) {
        try {
            Utilities utilities = Utilities.getInstance(getActivity());
            Activity activity = getActivity();
            Context context = getActivity();
            ExceptionLogUtils exceptionLogUtils = new ExceptionLogUtils(utilities, context, activity);
            exceptionLogUtils.addExceptionLog(utilities, context, activity, null, null, e, methodName);
        } catch (Exception exp) {
            //  logException(exp, "ChargingManualFragment_logException()");
        }

    }


    @Override
    public void update(Observable observable, Object o) {
        try {

            AutomatedTestListModel automatedTestListModel = (AutomatedTestListModel)o;
            isTestPerformed=true;
            if (automatedTestListModel.getIsTestSuccess() == AsyncConstant.TEST_PASS) {
                mImgViewCharger.setImageDrawable(getResources().getDrawable(R.drawable.ic_charging_green_svg_128),true,getActivity());

                activity.onChangeText(R.string.textSkip,false);
                utils.showToast(ctx, getResources().getString(R.string.txtManualPass));
                if (alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }

                onNextPress();

            }

            else {

            }
        } catch (Exception e) {
            logException(e, "EarJackManualFragment_onBroadcastCallBack()");
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();

       testController.unRegisterCharging();
        try {
            timer(ctx,true,ConstantTestIDs.CHARGING_ID,null);
            testController.deleteObserver(this);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void timerFail() {
        isTestPerformed=true;

        mImgViewCharger.setImageDrawable(getResources().getDrawable(R.drawable.ic_charging_green_svg_128),false,getActivity());

        activity.onChangeText(R.string.textSkip,false);
        utils.showToast(ctx, getResources().getString(R.string.txtManualFail));


        onNextPress();
    }
}
