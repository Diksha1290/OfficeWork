package com.officework.fragments;

/**
 * Created by Ashwani on 8/18/2016.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.os.Vibrator;
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
import com.officework.listeners.ShakeEventListener;
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
public class PhoneShakingManualFragment extends BaseFragment implements Observer , TimerDialogInterface {
    View view;
    Utilities utils;
    Context ctx;
    ManualDataStable manualDataStable;

    //ImageView mImgViewPhoneShake;
    IconView  mImgViewPhoneShake;
    private ShakeEventListener mSensorListener;
    public Vibrator vibrator;
    long pattern[] = {0, 100, 200};
    RelativeLayout phoneShakeLayout;
   // Handler nextButtonHandler = null;
    CountDownTimer cTimer = null;

    InterfaceButtonTextChange mCallBack;
    boolean isTestPerformed = false;

    boolean isDialogShown = false;
    AlertDialog alertDialog;
    TestResultUpdateToServer testResultUpdateToServer;
    private TestController testController;
    MainActivity mainActivity;

    public PhoneShakingManualFragment(InterfaceButtonTextChange callback) {
        mCallBack = callback;
    }

    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        try {
            if (view == null) {
                view = inflater.inflate(R.layout.fragment_manual_phone_shaking, null);
                utils = Utilities.getInstance(getActivity());
                manualDataStable = new ManualDataStable(mCallBack);
                ctx = getActivity();
            //    Crashlytics.getInstance().log(FragmentTag.PHONE_SHAKE_FRAGMENT.name());
                testResultUpdateToServer = new TestResultUpdateToServer(utils, ctx, getActivity());
                 testController = TestController.getInstance(getActivity());
                testController.addObserver(this);


                initViews();
            }
            return view;
        } catch (Exception e) {
            logException(e, "PhoneShakingManualFragment_initUI()");
            return null;
        }

    }

    public PhoneShakingManualFragment() {
    }

    /**
     * Initialize view
     */
    private void initViews() {
        try {
            alertDialog = new AlertDialog.Builder(
                    ctx).create();
            mainActivity = (MainActivity)getActivity();
            mainActivity.onChangeText(R.string.textSkip,true);
            mImgViewPhoneShake = (IconView) view.findViewById(R.id.imgViewPhoneShake);
            mSensorListener = new ShakeEventListener();
            phoneShakeLayout = (RelativeLayout) view.findViewById(R.id.phoneShakeLayout);
            intializeVibrator();
            timer(ctx,false,ConstantTestIDs.GYROSCOPE_ID,PhoneShakingManualFragment.this);
        } catch (Exception e) {
            logException(e, "PhoneShakingManualFragment_initViews()");
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
                testController.performOperation(ConstantTestIDs.GYROSCOPE_ID);
            }
            TitleBarFragment fragment = (TitleBarFragment) getFragment(R.id.headerContainer);
//            if (fragment != null) {
//                fragment.setTitleBarVisibility(true);
//                fragment.setSyntextVisibilty(false);
//                fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string.Gyroscope), false, false, 0);
//            }
            //nextButtonHandler = new Handler();
            if (Constants.isBackButton) {
                if (manualDataStable.checkSingleHardware(JsonTags.MMR_38.name(), ctx, utils) == 1) {
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

        } catch (Exception e) {
            logException(e, "PhoneShakingManualFragment_onResume()");
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
testController.unregisterGyroScope2();
        } catch (Exception e) {
            logException(e, "PhoneShakingManualFragment_onPause()");

        }

    }

    /**
     * Initialize vibrator
     */
    public void intializeVibrator() {
        try {
            vibrator = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
        } catch (Exception e) {
            logException(e, "PhoneShakingManualFragment_intializeVibrator()");
        }

    }

    public void startVibrate() {
        try {
            vibrator.vibrate(pattern, -1);
        } catch (Exception e) {
            logException(e, "PhoneShakingManualFragment_startVibrate()");
        }

    }

    public void stopVibrate() {
        try {
            vibrator.cancel();
        } catch (Exception e) {
            logException(e, "PhoneShakingManualFragment_stopVibrate()");
        }

    } @Override
    public void onStop() {

        mCallBack.onChangeText(utils.BUTTON_NEXT, false);

        super.onStop();
    }

    /**
     * Called when test is completed or user click on skip button
     */
    public void onNextPress() {
        try {
            timer(ctx,true,ConstantTestIDs.GYROSCOPE_ID,null);
            MainActivity activity = (MainActivity) getActivity();
//            activity.onChangeText(R.string.textSkip,false);
            if (Constants.isSkipButton) {
                try{
                    testController.unregisterGyroScope2();

                }
                catch (Exception e){
                    e.printStackTrace();
                }

                isTestPerformed=true;

                mImgViewPhoneShake.setImageDrawable(getResources().getDrawable(R.drawable.ic_gyroscope_green_svg_168),false,getActivity());
                utils.showToast(ctx, getResources().getString(R.string.txtManualFail));

            }
            boolean semi=true;
            nextPress(activity,semi);

        } catch (Exception e) {
            logException(e, "PhoneShakingManualFragment_onNextPress()");
        }


    }

    /**
     * onBackPressed
     */
    public void onBackPress() {
        try {
            snackShow(phoneShakeLayout);
        } catch (Exception e) {
            logException(e, "PhoneShakingManualFragment_onBackPress()");
        }

    }

//    }

    public void logException(Exception e, String methodName) {
        try {
            Utilities utilities = Utilities.getInstance(getActivity());
            Activity activity = getActivity();
            Context context = getActivity();
            ExceptionLogUtils exceptionLogUtils = new ExceptionLogUtils(utilities, context, activity);
            exceptionLogUtils.addExceptionLog(utilities, context, activity, null, null, e, methodName);
        } catch (Exception exp) {
        }

    }

    @Override
    public void update(Observable observable, Object o) {
        AutomatedTestListModel automatedTestListModel = (AutomatedTestListModel)o;
        isTestPerformed=true;
        timer(ctx,false,ConstantTestIDs.GYROSCOPE_ID,PhoneShakingManualFragment.this);
        if (automatedTestListModel.getIsTestSuccess() == AsyncConstant.TEST_PASS) {
             isTestPerformed=true;
            startVibrate();

            //   utils.showToast(ctx, getResources().getString(R.string.txtPhoneShaked));
            isTestPerformed = true;
            mCallBack.onChangeText(utils.BUTTON_NEXT, true);
            if (alertDialog.isShowing()) {
                alertDialog.dismiss();
            }

            mainActivity.onChangeText(R.string.textSkip,false);
            utils.showToast(ctx, getResources().getString(R.string.txtManualPass));

            mImgViewPhoneShake.setImageDrawable(getResources().getDrawable(R.drawable.ic_gyroscope_green_svg_168),true,getActivity());

           onNextPress();
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
            timer(ctx,true,ConstantTestIDs.GYROSCOPE_ID,null);
            try {
                testController.deleteObserver(this);

            }
            catch (Exception e){
                e.printStackTrace();
            }
            testController.unregisterGyroScope2();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    @Override
    public void timerFail() {
        mImgViewPhoneShake.setImageDrawable(getResources().getDrawable(R.drawable.ic_gyroscope_green_svg_168), false, getActivity());
        isTestPerformed = true;

        utils.showToast(ctx, getResources().getString(R.string.txtManualFail));
        onNextPress();
    }
}
