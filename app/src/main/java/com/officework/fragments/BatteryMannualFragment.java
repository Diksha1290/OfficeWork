package com.officework.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

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


@SuppressLint("ValidFragment")
public class BatteryMannualFragment extends BaseFragment implements Observer, TimerDialogInterface {
    
    View view;
    Utilities utils;
    Context ctx;
    IconView mImgViewCharger;
    InterfaceButtonTextChange mCallBack;
    LinearLayout chargingLayout;
    TextView batteryLevel;
    ManualDataStable manualDataStable;
    boolean isTestPerformed = false;
    CountDownTimer cTimer = null;
    boolean isDialogShown = false;
    boolean isBackGround = false;
    AlertDialog alertDialog;
    TestResultUpdateToServer testResultUpdateToServer;
    MainActivity activity;
    BroadcastReceiver mBatteryInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            Log.d("battery_info==", String.valueOf(level));
            batteryLevel.setText("Battery Level: " + String.valueOf(level) + "%");
        }
    };
    private TestController testController;
    
    public BatteryMannualFragment(InterfaceButtonTextChange callback) {
        mCallBack = callback;
    }
    
    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        
        try {
            if (view == null) {
                view = inflater.inflate(R.layout.fragment_mannual_item_battery, null);
                utils = Utilities.getInstance(getActivity());
                ctx = getActivity();
              //  Crashlytics.getInstance().log(FragmentTag.CHARGING_PORT_FRAGMENT.name());
                
                testController = TestController.getInstance(getActivity());
                testController.addObserver(this);

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
            activity.onChangeText(R.string.textSkip, true);
            alertDialog = new AlertDialog.Builder(ctx).create();
            mImgViewCharger = (IconView) view.findViewById(R.id.imgViewBatteryPort);
            chargingLayout = (LinearLayout) view.findViewById(R.id.batteryLayout);
            batteryLevel = (TextView) view.findViewById(R.id.txt_batteryLevel);
            //testRecevier=new TestRecevier(this);
            IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            getActivity().registerReceiver(mBatteryInfoReceiver, iFilter);
            timer(ctx,false,ConstantTestIDs.Battery,BatteryMannualFragment.this);
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
                testController.performOperation(ConstantTestIDs.Battery);
            }
            TitleBarFragment fragment = (TitleBarFragment) getFragment(R.id.headerContainer);
            if (fragment != null) {
//                fragment.setTitleBarVisibility(true);
//                fragment.setSyntextVisibilty(false);
//                fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string.BatteryTest)
//                        , false, false, 0);
                
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
                    Constants.isBackButton = false; }
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
            testController.unregisterBattery();
            isBackGround = true;
            // nextButtonHandler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            logException(e, "ChargingManualFragment_onPause()");
        }
        
    }
    
    /**
     * Callled when test is completed or user click on skip button
     */
    public void onNextPress() {
        try {
            timer(ctx,true,ConstantTestIDs.Battery,null);
            MainActivity activity = (MainActivity) getActivity();
            //            activity.onChangeText(R.string.textSkip,false);
            activity.onChangeText(R.string.textSkip, false);
            
            if (Constants.isSkipButton) {
                try {
                    testController.unregisterBattery();
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                isTestPerformed = true;
                mImgViewCharger.setImageDrawable(getResources().getDrawable(R.drawable.ic_battery_new), false, getActivity());
                utils.showToast(ctx, getResources().getString(R.string.txtManualFail));
                
            }
            boolean semi = true;
            nextPress(activity, semi);
            
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
            AutomatedTestListModel automatedTestListModel = (AutomatedTestListModel) o;
            isTestPerformed = true;
            if (automatedTestListModel.getIsTestSuccess() == AsyncConstant.TEST_PASS) {
                mImgViewCharger.setImageDrawable(getResources().getDrawable(R.drawable.ic_battery_new), true, getActivity());
                activity.onChangeText(R.string.textSkip, false);
                
                utils.showToast(ctx, getResources().getString(R.string.txtManualPass));
                if (alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
                onNextPress();
            } else {
            
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
        testController.unregisterBattery();
        try {
            timer(ctx, true, ConstantTestIDs.Battery, null);
            testController.deleteObserver(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    public void timerFail() {
        isTestPerformed = true;
        mImgViewCharger.setImageDrawable(getResources().getDrawable(R.drawable.ic_battery_new), false, getActivity());
        utils.showToast(ctx, getResources().getString(R.string.txtManualFail));
        onNextPress();
    }
}

