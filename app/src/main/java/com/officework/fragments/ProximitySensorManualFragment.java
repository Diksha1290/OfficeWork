package com.officework.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.hardware.Sensor;
import android.os.Build;
import android.os.CountDownTimer;
import androidx.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

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
 * Created by Ashwani on 8/18/2016.
 */
@SuppressLint("ValidFragment")
public class ProximitySensorManualFragment extends BaseFragment implements  Observer
        , TimerDialogInterface
{
    View view;
    Utilities utils;
    Context ctx;
    IconView mImgViewProximity;
    //SensorManager lets you access the device's sensors
    //declare Variables
    boolean isShowToast;
    RelativeLayout proxyLayout;
    ManualDataStable manualDataStable;
    boolean isTestPerformed = false;
    boolean semi=true;
    int count = 0;
    boolean isDialogShown = false;
    AlertDialog alertDialog;
    InterfaceButtonTextChange mCallBack;
    Sensor proximitySensor;
    //Handler nextButtonHandler = null;
    CountDownTimer cTimer = null;
    TestResultUpdateToServer testResultUpdateToServer;
    private TestController testController;

    public ProximitySensorManualFragment(InterfaceButtonTextChange callback) {
        mCallBack = callback;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        try {
            if (view == null) {
                view = inflater.inflate(R.layout.fragment_manual_proximity_sensor, null);
                manualDataStable = new ManualDataStable(mCallBack);
                utils = Utilities.getInstance(getActivity());
                ctx = getActivity();
                testResultUpdateToServer = new TestResultUpdateToServer(utils, ctx, getActivity());
                 testController = TestController.getInstance(getActivity());
                testController.addObserver(
                        this
                );

              //  Crashlytics.getInstance().log(FragmentTag.PROXIMITY_SENSOR_FRAGMENT.name());
                initViews();
            }
            return view;
        }catch (Exception e){
            logException(e, "ProximitySensorManualFragment_initUI()");
            return null;
        }


    }

    public ProximitySensorManualFragment() {
    }

    /**
     * Initialize view
     */
    private void initViews() {
        try {
            alertDialog = new AlertDialog.Builder(
                    ctx).create();

            MainActivity mainActivity = (MainActivity)getActivity();
            mainActivity.onChangeText(R.string.textSkip,true);

            mImgViewProximity = (IconView) view.findViewById(R.id.imgViewProximity);
            //create instance of sensor manager and get system service to interact with Sensor
            proxyLayout = (RelativeLayout) view.findViewById(R.id.proxyLayout);
            timer(ctx,false,ConstantTestIDs.PROXIMITY_ID,ProximitySensorManualFragment.this);
        }catch (Exception e){
            logException(e, "ProximitySensorManualFragment_initViews()");
        }

    }

    /**
     * Here change the header text
     ***/
    @Override
    public void onResume() {
        try {
//            testController.unRegisterPowerButton();
            if(dialog==null || !dialog.isShowing()) {
               testController.performOperation(ConstantTestIDs.PROXIMITY_ID);
           }
            isShowToast = false;
            TitleBarFragment fragment = (TitleBarFragment) getFragment(R.id.headerContainer);
            if (fragment != null) {
//                fragment.setTitleBarVisibility(true);
//                fragment.setSyntextVisibilty(false);
//                fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string.ProximitySensor), false, false, 0);
                //nextButtonHandler = new Handler();
                if (Constants.isBackButton == true) {
                    if (manualDataStable.checkSingleHardware(JsonTags.MMR_22.name(), ctx, utils) == 1) {
                        mCallBack.onChangeText(utils.BUTTON_NEXT, true);
                        Constants.isBackButton = false;
                    } else {
                        mCallBack.onChangeText(utils.BUTTON_SKIP, true);
                        Constants.isBackButton = false;
                    }
                } else {
                    if (isTestPerformed) {
                        mCallBack.onChangeText(utils.BUTTON_NEXT, true);

                      onNextPress();
                    } else {
                        mCallBack.onChangeText(utils.BUTTON_SKIP, true);
                    }

                }

            }

        }catch (Exception e){
            logException(e, "ProximitySensorManualFragment_onResume()");
        }

        super.onResume();
    }

    /**
     * onPause
     */
    @Override
    public void onPause() {
        super.onPause();
        try {
testController.unregisterProximity();
            /**
             * unregister receiver
             */
        }catch (Exception e){
            logException(e, "ProximitySensorManualFragment_onPause()");
        }

    }


    /**
     * Called when test is completed or user click on skip button
     */
    public void onNextPress() {
        try {
            timer(ctx,true,ConstantTestIDs.PROXIMITY_ID,null);
            MainActivity activity = (MainActivity) getActivity();
            if (Constants.isSkipButton) {
                try {
                    testController.unregisterProximity();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                isTestPerformed=true;
                mImgViewProximity.setImageDrawable(getResources().getDrawable(R.drawable.ic_proximity),false,getActivity());
                utils.showToast(ctx, getResources().getString(R.string.txtManualFail));

            }
            nextPress(activity,semi);

        }catch (Exception e){
            logException(e, "ProximitySensorManualFragment_onNextPress()");
        }



    } @Override
    public void onStop() {

        mCallBack.onChangeText(utils.BUTTON_NEXT, false);

        super.onStop();
    }

    /**
     * onBackPressed
     */
    public void onBackPress() {
        try {
            snackShow(proxyLayout);
        }catch (Exception e){
            logException(e, "ProximitySensorManualFragment_onBackPress()");
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
           // logException(exp, "ProximitySensorManualFragment_logException()");
        }

    }

    @Override
    public void update(Observable observable, Object o) {
        try {

            AutomatedTestListModel automatedTestListModel = (AutomatedTestListModel)o;
            isTestPerformed=true;
            if (automatedTestListModel.getIsTestSuccess() == AsyncConstant.TEST_PASS) {
                MainActivity mainActivity = (MainActivity)getActivity();
                mainActivity.onChangeText(R.string.textSkip,false);

                mImgViewProximity.setImageDrawable(getResources().getDrawable(R.drawable.ic_proximity),true,getActivity());

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
        timer(ctx,true,ConstantTestIDs.PROXIMITY_ID,null);
        super.onDestroyView();
        try {
            testController.deleteObserver(this);

        }
        catch (Exception e){
            e.printStackTrace();
        }        testController.unregisterProximity();

    }

    @Override
    public void timerFail() {
        isTestPerformed = true;

        mImgViewProximity.setImageDrawable(getResources().getDrawable(R.drawable.ic_proximity), false, getActivity());
        utils.showToast(ctx, getResources().getString(R.string.txtManualFail));


        onNextPress();
    }
}

