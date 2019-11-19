package com.officework.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;

import android.os.CountDownTimer;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import com.officework.R;
import com.officework.activities.MainActivity;
import com.officework.base.BaseFragment;
import com.officework.constants.AsyncConstant;
import com.officework.constants.Constants;
import com.officework.constants.JsonTags;
import com.officework.customViews.IconView;
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
public class EarJackManualFragment extends BaseFragment implements Observer, TimerDialogInterface {
    View view;
    Utilities utils;
    Context ctx;
    IconView mImgViewEarPhone;
    BroadcastReceiver mReceiver = null;

    ConstraintLayout earjackLayout;
     ManualDataStable manualDataStable;
    boolean isTestPerformed = false;
    //Handler nextButtonHandler = null;
    CountDownTimer cTimer = null;

    boolean isDialogShown = false;
    AlertDialog alertDialog;
    TestResultUpdateToServer testResultUpdateToServer;
    private TestController testController;
    MainActivity activity;
    Toolbar toolbar;

    public EarJackManualFragment() {
    }

    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {

        try {
            if (view == null) {
                view = inflater.inflate(R.layout.fragment_manual_ear_jack, null);
                manualDataStable = new ManualDataStable(null);
                utils = Utilities.getInstance(getActivity());
                ctx = getActivity();
               // Crashlytics.getInstance().log(FragmentTag.EAR_JACK_FRAGMENT.name());
                 testController = TestController.getInstance(getActivity());
                testController.addObserver(this);

                testResultUpdateToServer = new TestResultUpdateToServer(utils, ctx, getActivity());

                initViews();

            }
            return view;
        } catch (Exception e) {
            logException(e, "EarJackManualFragment_initUI()");
            return null;
        }

    }

    /**
     * Initialize view
     */
    private void initViews() {
        try {
            alertDialog = new AlertDialog.Builder(
                    ctx).create();
            mImgViewEarPhone = (IconView) view.findViewById(R.id.mImgViewEarJack);

            earjackLayout = (ConstraintLayout) view.findViewById(R.id.earjackLayout);
            activity = (MainActivity) getActivity();

            activity.onChangeText(R.string.textSkip,true);
            timer(ctx,false,ConstantTestIDs.EAR_PHONE_ID,EarJackManualFragment.this);
        } catch (Exception e) {
            logException(e, "EarJackManualFragment_initViews()");
        }


    }

    /**
     * Here change the header text
     ***/
    @Override
    public void onResume() {
        try {
            if(dialog==null || !dialog.isShowing()) {
                testController.performOperation(ConstantTestIDs.EAR_PHONE_ID);
            }
//            testController.unRegisterPowerButton();
            TitleBarFragment fragment = (TitleBarFragment) getFragment(R.id.headerContainer);
            if (fragment != null) {
//                fragment.setTitleBarVisibility(true);
//                fragment.setSyntextVisibilty(false);
//
//                fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string.EarphoneJack), false, false, 0);

                //nextButtonHandler = new Handler();
                if (Constants.isBackButton == true) {
                    if (manualDataStable.checkSingleHardware(JsonTags.MMR_17.name(), ctx, utils) == 1) {
                        Constants.isBackButton = false;
                    } else {
                        Constants.isBackButton = false;
                    }
                } else {
                    if (isTestPerformed) {

                        Constants.isManualIndividual = false;
                    onNextPress();
                    } else {
                    }
                }
            }

        } catch (Exception e) {
            logException(e, "EarJackManualFragment_onResume()");
        }


        super.onResume();
    }

    /**
     * onPause
     */
    @Override
    public void onPause() {
        try {
//            if (mReceiver != null)
//                ctx.unregisterReceiver(mReceiver);
            testController.unRegisterEarJack();
        } catch (Exception e) {
            logException(e, "EarJackManualFragment_onPause()");
        }

        super.onPause();
    }



    /**
     * Callled when test is completed or user click on skip button
     */
    public void onNextPress() {
        try {
            timer(ctx,true,ConstantTestIDs.EAR_PHONE_ID,null);
            MainActivity activity = (MainActivity) getActivity();
            if (Constants.isSkipButton) {
                try{
                    testController.unRegisterEarJack();

                }
                catch (Exception e){
                    e.printStackTrace();
                }
                isTestPerformed=true;
                mImgViewEarPhone.setImageDrawable(getResources().getDrawable(R.drawable.ic_earphone_green_svg_168),false,getActivity());
                utils.showToast(ctx, getResources().getString(R.string.txtManualFail));

            }
            boolean semi=true;
            nextPress(activity,semi);

        } catch (Exception e) {
            logException(e, "EarJackManualFragment_onNextPress()");
        }

    }

    /**
     * onBackPressed
     */
    public void onBackPress() {
        try {
            snackShow(earjackLayout);
        } catch (Exception e) {
            logException(e, "EarJackManualFragment_onBackPress()");
        }

    }
    @Override
    public void onStop() {


        super.onStop();
    }



    public void logException(Exception e, String methodName) {
        try {
            Utilities utilities = Utilities.getInstance(getActivity());
            Activity activity = getActivity();
            Context context = getActivity();
            ExceptionLogUtils exceptionLogUtils = new ExceptionLogUtils(utilities, context, activity);
            exceptionLogUtils.addExceptionLog(utilities, context, activity, null, null, e, methodName);
        } catch (Exception exp) {
           // logException(exp, "EarJackManualFragment_logException()");
        }

    }

    @Override
    public void update(Observable observable, Object o) {

        try {

            AutomatedTestListModel automatedTestListModel = (AutomatedTestListModel)o;
            if (automatedTestListModel.getIsTestSuccess() == AsyncConstant.TEST_PASS) {
                     mImgViewEarPhone.setImageDrawable(getResources().getDrawable(R.drawable.ic_earphone_green_svg_168),true,getActivity());

                    isTestPerformed = true;

                    activity.onChangeText(R.string.textSkip,false);
                    utils.showToast(ctx, getResources().getString(R.string.txtManualPass));
                    if (alertDialog.isShowing()) {
                        alertDialog.dismiss();
                    }

                onNextPress();

            }

             else {
                mImgViewEarPhone.setImageDrawable(getResources().getDrawable(R.drawable.ic_earphone_red__svg_168),false,getActivity());

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
        timer(ctx,true,ConstantTestIDs.EAR_PHONE_ID,null);
       testController.unRegisterEarJack();
        try {
            testController.deleteObserver(this);

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void timerFail() {
        mImgViewEarPhone.setImageDrawable(getResources().getDrawable(R.drawable.ic_earphone_red__svg_168),false,getActivity());
        mImgViewEarPhone.setImageDrawable(getResources().getDrawable(R.drawable.ic_earphone_green_svg_168),false,getActivity());
        utils.showToast(ctx, getResources().getString(R.string.txtManualFail));
        isTestPerformed = true;

        onNextPress();
    }
}

