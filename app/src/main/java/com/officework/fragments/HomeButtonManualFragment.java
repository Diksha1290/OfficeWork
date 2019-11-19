package com.officework.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.CountDownTimer;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
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
@SuppressLint("ValidFragment")
public class HomeButtonManualFragment extends BaseFragment implements Observer, TimerDialogInterface {
    public boolean isHomeButtonTested = false;
    public String androidId;
    View view;
    Utilities utils;
    Context ctx;
    IconView mImgViewHomeBtn;
    boolean activitySwitchFlag = false;
    HomeWatcher mHomeWatcher;
    InterfaceButtonTextChange mCallBack;
    RelativeLayout homeManualLayout;
    TitleBarFragment fragment;
    MainActivity mainActivity;
    ManualDataStable manualDataStable;
    Intent intent;
    boolean isTimerDialogShow=false;
    //Handler nextButtonHandler = null;
    CountDownTimer cTimer = null;
    boolean isDialogShown = false;
    AlertDialog alertDialog;
    TestResultUpdateToServer testResultUpdateToServer;
    boolean isHomePassed = false;
    private IntentFilter mFilter;
    private TestController testController;

    public HomeButtonManualFragment(InterfaceButtonTextChange callback) {
        mCallBack = callback;
    }

    public HomeButtonManualFragment() {
    }

    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        try {
            if (view == null) {
                mainActivity = new MainActivity();
                view = inflater.inflate(R.layout.fragment_manual_home_button, null);
                utils = Utilities.getInstance(getActivity());
                manualDataStable = new ManualDataStable(mCallBack);
                ctx = getActivity();
              //  Crashlytics.log(FragmentTag.HOME_FRAGMENT.name());

                testController = TestController.getInstance(getActivity());
                testController.addObserver(this);
                testController.performOperation(ConstantTestIDs.HOME_ID);
                testResultUpdateToServer = new TestResultUpdateToServer(utils, ctx, getActivity());
                initViews();
            }
            return view;
        } catch (Exception e) {
            logException(e, "HomeButtonManualFragment_initUI()");
            return null;
        }

    }

    /**
     * Initialize view
     */
    private void initViews() {
        try {
            TextView textview;
            textview=view.findViewById(R.id.home);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                textview.setText(Html.fromHtml(getResources().getString(R.string.txtManualHomeButton_test),Html.FROM_HTML_MODE_COMPACT));
            }
            else {
                textview.setText(Html.fromHtml(getResources().getString(R.string.txtManualHomeButton_test)));
            }

            MainActivity activity = (MainActivity) getActivity();
            activity.onChangeText(R.string.textSkip, true);
            alertDialog = new AlertDialog.Builder(
                    ctx).create();
            mImgViewHomeBtn = (IconView) view.findViewById(R.id.imgViewHomeButton);
            mHomeWatcher = new HomeWatcher(getActivity());
            homeManualLayout = (RelativeLayout) view.findViewById(R.id.homeManualLayout);
            if (Constants.isBackButton == true) {
                if (manualDataStable.checkSingleHardware(JsonTags.MMR_25.name(), ctx, utils) == 1) {
                    mCallBack.onChangeText(utils.BUTTON_NEXT, true);
                    Constants.isBackButton = false;
                } else {
                    mCallBack.onChangeText(utils.BUTTON_SKIP, true);
                    Constants.isBackButton = false;
                }

            } else {
                Constants.isBackButton = false;
                mCallBack.onChangeText(utils.BUTTON_SKIP, true);
            }
            timer(ctx,false,ConstantTestIDs.HOME_ID,HomeButtonManualFragment.this, new InterfaceButtonTextChange() {
                @Override
                public void onChangeText(int text, boolean showButton) {

                    isTimerDialogShow=true;
                }
            });
        } catch (Exception e) {
            logException(e, "HomeButtonManualFragment_initViews()");
        }


    }

//    /**
//     * Here change the header text
//     ***/
//    public void onResume() {
//        try {
//
//            fragment = (TitleBarFragment) getFragment(R.id.headerContainer);
//            if (fragment != null) {
////           /* if (!isDialogShown) {
////                startTimer();
////            }*/
////
//                fragment.setTitleBarVisibility(true);
//                fragment.setSyntextVisibilty(false);
//
//                fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string.HomeButton),
//                        false, false, 0);
//                if (isHomeButtonTested) {
//                    isHomeButtonTested = false;
//
//                    onNextPress();
//                    mCallBack.onChangeText(utils.BUTTON_NEXT, true);
//                }
////                else {
////                    mCallBack.onChangeText(utils.BUTTON_SKIP, true);
////                }
////            }
//
//                else if (!isTimerDialogShow) {
//                    testController.unRegisterHome();
//                    testController.performOperation(ConstantTestIDs.HOME_ID);
//                }
//            }
//        } catch (Exception e) {
//            logException(e, "HomeButtonManualFragment_onResume()");
//        }
//
//        super.onResume();
//    }


    /**
     * Here change the header text
     ***/
    @Override
    public void onResume() {
        try {
            fragment = (TitleBarFragment) getFragment(R.id.headerContainer);
            if (fragment != null) {
           /* if (!isDialogShown) {
                startTimer();
            }*/
//                fragment.setSyntextVisibilty(false);
//                fragment.setTitleBarVisibility(true);
//                fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string.HomeButton),
//                        false, false, 0);
                if (isHomeButtonTested) {
                //    isHomeButtonTested = true;
                    onNextPress();
                    mCallBack.onChangeText(utils.BUTTON_NEXT, true);
                }
                else if(!isTimerDialogShow) {
                    testController.unRegisterHome();
                    testController.performOperation(ConstantTestIDs.HOME_ID);
                }

//                else {
//                    mCallBack.onChangeText(utils.BUTTON_SKIP, true);
//                }
            }

        } catch (Exception e) {
            logException(e, "HomeButtonManualFragment_onResume()");
        }

        super.onResume();
    }













    /**
     * onPause
     */
    @Override
    public void onPause() {
        super.onPause();

    }

    /**
     * onDetach
     */
    @Override
    public void onDetach() {
        try {


        } catch (Exception e) {
            logException(e, "HomeButtonManualFragment_onDetach()");
        }

        super.onDetach();

    }

    /**
     * automatically reopen activity while performing test
     */
    private void reopenScreen() {
        try {
            intent = new Intent(ctx, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
        } catch (Exception e) {
            logException(e, "HomeButtonManualFragment_reopenScreen()");
        }

    }

    /**
     * Callled when test is completed or user click on skip button
     */
    public void onNextPress() {
        try {
            timer(ctx,true,ConstantTestIDs.HOME_ID,null);
            MainActivity activity = (MainActivity) getActivity();
//            activity.onChangeText(R.string.textSkip,false);
            if (Constants.isSkipButton) {
                try {
                    isHomeButtonTested = true;

                    testController.unRegisterHome();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                mImgViewHomeBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_home),
                        false, getActivity());

                utils.showToast(ctx, getResources().getString(R.string.txtManualFail));

            }
            if (isHomePassed) {
                utils.showToast(ctx, getResources().getString(R.string.txtManualPass));
                isHomePassed = false;

            }
            boolean semi = true;
            nextPress(activity, semi);

        } catch (Exception e) {
            logException(e, "HomeButtonManualFragment_onNextPress()");
        }


    }

    /**
     * onBackPressed
     */
    public void onBackPress() {
        try {
            snackShow(homeManualLayout);
        } catch (Exception e) {
            logException(e, "HomeButtonManualFragment_onBackPress()");
        }

    }

    /**
     * show snack bar when user click on back button
     * <p>
     * <p>
     * <p>
     * /**
     * callback for home buttom press
     */
    public void onHomePressed() {
        try {
            mImgViewHomeBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_home), true
                    , getActivity());
            isHomeButtonTested = true;
            mCallBack.onChangeText(utils.BUTTON_NEXT, true);
            if (alertDialog.isShowing()) {
                alertDialog.dismiss();
            }

            reopenScreen();

        } catch (Exception e) {
            logException(e, "HomeButtonManualFragment_onHomePressed()");
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
            //   logException(exp, "HomeButtonManualFragment_logException()");
        }

    }

    @Override
    public void onStop() {

        mCallBack.onChangeText(utils.BUTTON_NEXT, false);

        super.onStop();
    }

    @Override
    public void update(Observable observable, Object o) {
        try {


            MainActivity activity = (MainActivity) getActivity();
            activity.onChangeText(R.string.textSkip, false);
            AutomatedTestListModel automatedTestListModel = (AutomatedTestListModel) o;
            if (automatedTestListModel.getIsTestSuccess() == AsyncConstant.TEST_PASS) {
                isHomePassed = true;
                onHomePressed();
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
        timer(ctx,true,ConstantTestIDs.HOME_ID,null);
        testController.unRegisterHome();
        try {
            testController.deleteObserver(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroyView();
    }
    @Override
    public void timerFail() {
        isHomeButtonTested=true;
        mImgViewHomeBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_home),
                false, getActivity());
        utils.showToast(ctx, getResources().getString(R.string.txtManualFail));
        onNextPress();
    }
}
