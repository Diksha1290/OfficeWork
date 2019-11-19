package com.officework.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.officework.R;
import com.officework.activities.MainActivity;
import com.officework.base.BaseFragment;

import com.officework.broadcastReceivers.HomeButtonReceiver;
import com.officework.constants.AsyncConstant;
import com.officework.constants.Constants;
import com.officework.customViews.IconView;
import com.officework.interfaces.InterfaceBroadcastCallback;
import com.officework.interfaces.InterfaceButtonTextChange;
import com.officework.interfaces.OnHomePressedListener;
import com.officework.interfaces.TimerDialogInterface;
import com.officework.listeners.HomeWatcher;
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
 * Created by Ashwani on 8/18/2016.
 */

/**
 * On this screen we have register both power button and home buton receiver
 * because power button receiver receive both power and home button event
 * to handle this we have register home button receiver which do nothing
 * but when power button is pressed on power button receiver is receiver and
 * home button receiver is rejected because we do nothing in home button receiver
 */


@SuppressLint("ValidFragment")
public class PowerButtonManualFragment extends BaseFragment implements InterfaceBroadcastCallback, OnHomePressedListener ,Observer, TimerDialogInterface {
    View view;
    Utilities utils;
    Context ctx;
    IconView mImgViewPowerBtn;
    HomeButtonReceiver homeButtonReceiver;
    HomeWatcher mHomeWatcher;
    InterfaceButtonTextChange mCallBack;
    private  boolean isPowerTestDone = false;
    RelativeLayout powerLayout;
    ManualDataStable manualDataStable;
    private IntentFilter mFilter;
    IntentFilter filter;
   // Handler nextButtonHandler = null;
    CountDownTimer cTimer = null;
    MainActivity activity;
    boolean isDialogShown = false;
    AlertDialog alertDialog;
    TestResultUpdateToServer testResultUpdateToServer;
    private TestController testController;
    boolean isNextPressAlready=false;
    boolean isUpdateCalled=false;
    public PowerButtonManualFragment(InterfaceButtonTextChange callback) {
        mCallBack = callback;
    }

    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        try {
            if (view == null) {

                view = inflater.inflate(R.layout.fragment_manual_power_button, null);
                manualDataStable = new ManualDataStable(mCallBack);
                utils = Utilities.getInstance(getActivity());
                ctx = getActivity();
                 testController = TestController.getInstance(getActivity());
                testController.addObserver(this);
                testController.performOperation(ConstantTestIDs.POWER_ID);
          //      Crashlytics.getInstance().log(FragmentTag.POWER_BUTTON_FRAGMENT.name());
                testResultUpdateToServer = new TestResultUpdateToServer(utils, ctx, getActivity());
                initViews();
            }
            return view;
        } catch (Exception e) {
            logException(e, "PowerButtonManualFragment_initUI()");
            return null;
        }
    }

    public PowerButtonManualFragment() {
    }

    /**
     * Initialize view
     */
    private void initViews() {
        try {
            TextView textview;
            alertDialog = new AlertDialog.Builder(
                    ctx).create();
            mImgViewPowerBtn = (IconView) view.findViewById(R.id.imgViewPowerBtn);
            powerLayout = (RelativeLayout) view.findViewById(R.id.powerLayout);
            textview=view.findViewById(R.id.power);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                textview.setText(Html.fromHtml(getResources().getString(R.string.txtManualPower_test),Html.FROM_HTML_MODE_COMPACT));
            }
            else {
                textview.setText(Html.fromHtml(getResources().getString(R.string.txtManualPower_test)));
            }
            filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            activity = (MainActivity) getActivity();
            activity.onChangeText(R.string.textSkip,true);
            timer(ctx,false,ConstantTestIDs.POWER_ID,PowerButtonManualFragment.this);
        } catch (Exception e) {
            logException(e, "PowerButtonManualFragment_initViews()");
        }

    }

//    /**
//     * onResume
//     */
//    @Override
//    public void onResume() {
//        try {
//                TitleBarFragment fragment = (TitleBarFragment) getFragment(R.id.headerContainer);
//                if (fragment != null) {
//                    fragment.setTitleBarVisibility(true);
//                    fragment.setSyntextVisibilty(false);
//                    fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string.PowerButton), false, false, 0);
//                }
//
//                if (isPowerTestDone) {
//
//                    mCallBack.onChangeText(Utilities.BUTTON_NEXT, true);
//
//                    if (homeButtonReceiver != null) {
//                        ctx.unregisterReceiver(homeButtonReceiver);
//                        homeButtonReceiver = null;
//                    }
//
////                        if(!isUpdateCalled){
////                        onNextPress();
////                             }
//         //           onNextPress();
//                } else {
//                    mCallBack.onChangeText(Utilities.BUTTON_SKIP, true);
//
//
//                }
//
//
//        } catch (Exception e) {
//            logException(e, "PowerButtonManualFragment_onResume()");
//        }
//
//        super.onResume();
//    }






    /**
     * onResume
     */
    @Override
    public void onResume() {
        try {
//            TitleBarFragment fragment = (TitleBarFragment) getFragment(R.id.headerContainer);
//            if (fragment != null) {
//                fragment.setTitleBarVisibility(true);
//                fragment.setSyntextVisibilty(false);
//                fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string.PowerButton), false, false, 0);
//            }

            if (isPowerTestDone) {

                mCallBack.onChangeText(Utilities.BUTTON_NEXT, true);

                if (homeButtonReceiver != null) {
                    ctx.unregisterReceiver(homeButtonReceiver);
                    homeButtonReceiver = null;
                }

                if(!isUpdateCalled){
                    onNextPress();
                }
            } else {
                mCallBack.onChangeText(Utilities.BUTTON_SKIP, true);


            }


        } catch (Exception e) {
            logException(e, "PowerButtonManualFragment_onResume()");
        }

        super.onResume();
    }
    /**
     * onPause
     */
    @Override
    public void onPause() {
        try {
        } catch (Exception e) {
            logException(e, "PowerButtonManualFragment_onPause()");
        }

        super.onPause();
    }

    /**
     * onDetach
     */

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            /**
             * unregister power button receiver
             */


            /**
             * unregister home button receiver
             */
            if (homeButtonReceiver != null) {
                ctx.unregisterReceiver(homeButtonReceiver);
                homeButtonReceiver = null;
            }
        } catch (Exception e) {
            logException(e, "PowerButtonManualFragment_onDetach()");
        }

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
    }

    /**
     * callback receiver for broadcast
     *
     * @param isPower
     * @param status
     */
    @Override
    public void onBroadcastCallBack(boolean isPower, String status) {
        try {
            if (isPower) {
              //  isPowerTestDone = true;
                mImgViewPowerBtn.setImageResource(R.drawable.ic_power_green_svg_128);
                mCallBack.onChangeText(Utilities.BUTTON_NEXT, true);
                utils.showToast(ctx, getResources().getString(R.string.txtManualPass));
            } else {
                isPowerTestDone = false;
                mCallBack.onChangeText(Utilities.BUTTON_SKIP, true);

            }
        } catch (Exception e) {
            logException(e, "PowerButtonManualFragment_onBroadcastCallBack()");
        }

    }
    @Override
    public void onStop() {

        mCallBack.onChangeText(Utilities.BUTTON_NEXT, false);

        super.onStop();
    }
    /**
     * Called when test is completed or user click on skip button
     */
    public void onNextPress() {
        try {
            timer(ctx,true,ConstantTestIDs.POWER_ID,PowerButtonManualFragment.this);
            isNextPressAlready=true;
            MainActivity activity = (MainActivity) getActivity();
            if (Constants.isSkipButton) {
                isPowerTestDone=true;
                isUpdateCalled=false;
                mImgViewPowerBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_power_green_svg_128),false,getActivity());
                utils.showToast(ctx, getResources().getString(R.string.txtManualFail));

            }

            if (homeButtonReceiver != null) {
                ctx.unregisterReceiver(homeButtonReceiver);
                homeButtonReceiver = null;
            }

            boolean semi=true;
                  nextPress(activity,semi);

        } catch (Exception e) {
            logException(e, "PowerButtonManualFragment_onNextPress()");
        }


    }

    /**
     * onBackPressed
     */
    public void onBackPress() {
        try {
            snackShow(powerLayout);
        } catch (Exception e) {
            logException(e, "PowerButtonManualFragment_onBackPress()");
        }

    }


    @Override
    public void onHomePressed() {
        Log.d("Home Button : "," True");
    }

    @Override
    public void onHomeLongPressed() {
        Log.d("Home Button : "," True");
    }


    public void logException(Exception e, String methodName) {
        try {
            Utilities utilities = Utilities.getInstance(getActivity());
            Activity activity = getActivity();
            Context context = getActivity();
            ExceptionLogUtils exceptionLogUtils = new ExceptionLogUtils(utilities, context, activity);
            exceptionLogUtils.addExceptionLog(utilities, context, activity, null, null, e, methodName);
        } catch (Exception exp) {
            logException(exp, "PowerButtonManualFragment_logException()");
        }

    }

    @Override
    public void update(Observable observable, Object o) {
        try {
            timer(ctx,false,ConstantTestIDs.POWER_ID,PowerButtonManualFragment.this);
             isUpdateCalled=false;
            AutomatedTestListModel automatedTestListModel = (AutomatedTestListModel)o;
            if (automatedTestListModel.getIsTestSuccess() == AsyncConstant.TEST_PASS && automatedTestListModel.getTest_id() == ConstantTestIDs.POWER_ID) {
                mImgViewPowerBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_power_green_svg_128),true,getActivity());
                activity.onChangeText(R.string.textSkip, false);
                isPowerTestDone = true;

                if (alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
                FragmentManager fm = getActivity().getSupportFragmentManager();
                Fragment frag = fm.findFragmentById(R.id.container);
                if(frag.isVisible()){
                    utils.showToast(ctx, getResources().getString(R.string.txtManualPass));

                    onNextPress();

              }


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
        timer(ctx,true,ConstantTestIDs.POWER_ID,PowerButtonManualFragment.this);
       testController.unRegisterPowerButton();
        try {
            testController.deleteObserver(this);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        ViewGroup mContainer = (ViewGroup) getActivity().findViewById(R.id.container);
        mContainer.removeAllViews();
        super.onDestroyView();

    }
    @Override
    public void timerFail() {
        mImgViewPowerBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_power_green_svg_128), false, getActivity());
        isPowerTestDone = true;
        isUpdateCalled=false;

        utils.showToast(ctx, getResources().getString(R.string.txtManualFail));
        onNextPress();
    }
}

