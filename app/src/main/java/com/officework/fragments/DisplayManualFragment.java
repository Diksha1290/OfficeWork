package com.officework.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.officework.R;
import com.officework.activities.MainActivity;
import com.officework.base.BaseFragment;
import com.officework.constants.Constants;
import com.officework.customViews.CustomButton;
import com.officework.customViews.IconView;
import com.officework.interfaces.InterfaceButtonTextChange;
import com.officework.interfaces.TimerDialogInterface;
import com.officework.testing_profiles.Controller.TestController;
import com.officework.testing_profiles.utils.ConstantTestIDs;
import com.officework.utils.ExceptionLogUtils;
import com.officework.utils.ManualDataStable;
import com.officework.utils.TestResultUpdateToServer;
import com.officework.utils.Utilities;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

/**
 * Created by Girish on 8/9/2016.
 */

@SuppressLint("ValidFragment")
public class DisplayManualFragment extends BaseFragment implements TimerDialogInterface {
    public static boolean isTestPerform = false;
    View view;
    Utilities utils;
    Context ctx;
    TextView txtManualDeadPixel;
    Button txtManualDeadPixelStart;
    RelativeLayout txtDeadPixelScreen, txtDeadPixelMain;
    TitleBarFragment fragment = null;
    Handler handler = null;
    TestController testController;
    AlertDialog.Builder alertDialog;
    AlertDialog alert;
    boolean isTestRunning = false;
    //DialogInterface dialog;
    InterfaceButtonTextChange mCallBack;
    boolean isWhitePixel, isBlackPixel;
    ManualDataStable manualDataStable;
    LinearLayout layoutField;
    boolean isTestPerforming = false;
    boolean isTestResumedAfter = false;
    CountDownTimer cTimer = null;
    boolean isDialogShown = false;
    boolean isBackOnTestStart = false;
    boolean isTestDone = false;
    TestResultUpdateToServer testResultUpdateToServer;
    boolean isDialogShownOnce = false;
    CustomButton displayPass, displayFail;
    Snackbar snackbar;
    Integer[] colorArray = {R.color.RedColor, R.color.green, R.color.app_blue_color,
            R.color.white, R.color.Black};
    int i;
    int count = 0;
    //Handler nextButtonHandler = null;
    private Runnable runnable;
    private Handler handlerColor = new Handler();
    private MainActivity mainActivity;
    private LinearLayout mLinearLayout;
    private boolean alreadyShow;
    IconView displayImage;

    public DisplayManualFragment(InterfaceButtonTextChange callback) {
        mCallBack = callback;
    }

    public DisplayManualFragment() {
    }

    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {

        try {
            if (view == null) {
                view = inflater.inflate(R.layout.fragment_manual_display, null);
                manualDataStable = new ManualDataStable(mCallBack);
                utils = Utilities.getInstance(getActivity());
                ctx = getActivity();
                handler = new Handler();
                // Crashlytics.getInstance().log(FragmentTag.DISPLAY_FRAGMENT.name());
                testResultUpdateToServer = new TestResultUpdateToServer(utils, ctx, getActivity());
                timer(getActivity(), false, ConstantTestIDs.DISPLAY_ID, DisplayManualFragment.this);
                testController = TestController.getInstance(getActivity());
                testController.performOperation(ConstantTestIDs.DISPLAY_ID);
                initViews();
            }
            return view;
        } catch (Exception e) {
            logException(e, "DisplayManualFragment_initUI()");
            return null;
        }

    }

    /**
     * Initialize view
     */

    private void initViews() {
        try {
            timer(getActivity(), false, ConstantTestIDs.DISPLAY_ID, DisplayManualFragment.this);
            mainActivity = (MainActivity) getActivity();
            mainActivity.onChangeText(R.string.textSkip, true);
            layoutField = view.findViewById(R.id.ll_display);
            displayFail = view.findViewById(R.id.btnDisplayFail);
            displayPass = view.findViewById(R.id.btnDisplayPass);
            txtManualDeadPixel = (TextView) view.findViewById(R.id.txtManualDeadPixel);
            txtManualDeadPixelStart = (Button) view.findViewById(R.id.txtDeadPixelStart);
            txtDeadPixelScreen = (RelativeLayout) view.findViewById(R.id.txtDeadPixelScreen);
            txtDeadPixelMain = (RelativeLayout) view.findViewById(R.id.relativeLayoutMain);
            displayImage=view.findViewById(R.id.imgd);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                txtManualDeadPixel.setText(Html.fromHtml(getResources().getString(R.string.txtManualDisplayDeadPixel), Html.FROM_HTML_MODE_COMPACT));
            } else {
                txtManualDeadPixel.setText(Html.fromHtml(getResources().getString(R.string.txtManualDisplayDeadPixel)));
            }

            txtManualDeadPixelStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getActivity() != null) {
                        getActivity().findViewById(R.id.btnNext).setEnabled(false);
                    }
                    TitleBarFragment titleBarFragment =
                            (TitleBarFragment) getFragment(R.id.headerContainer);
                    titleBarFragment.showSwitchLayout(false);
                    timer(getActivity(), true, ConstantTestIDs.DISPLAY_ID, null);
                    // timer(getActivity(),false,ConstantTestIDs.DISPLAY_ID,DisplayManualFragment
                    // .this);
                    mainActivity.onChangeText(R.string.textSkip, false);


                    // fullScreen();
                    count = 0;
                    isBackOnTestStart = false;
                    isTestPerform = true;
                    isTestPerforming = true;
                    isTestRunning = true;
                    mCallBack.onChangeText(utils.BUTTON_NEXT, false);
                    // txtDeadPixelMain.setVisibility(View.GONE);
//                    txtManualDeadPixelStart.setVisibility(View.GONE);
//                    txtManualDeadPixel.setVisibility(View.GONE);
                    txtDeadPixelMain.setVisibility(View.GONE);
                    //                    showDialogBoxWhite();
                    // hideShowTitleBar(false);
                    setColors();


                    handlerColor.post(runnable);

                }
            });
        } catch (Exception e) {
            logException(e, "DisplayManualFragment_initViews()");
        }

    }

    public void setColors() {

        hideShowTitleBar(false);

        runnable = new Runnable() {
            @Override
            public void run() {

                try {
                    if (count < 5) {
                        txtDeadPixelScreen.setBackgroundColor(getResources().getColor(colorArray[count]));
                        count++;
                        fullScreen();
                        txtDeadPixelScreen.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                handlerColor.removeCallbacks(runnable);
                                handlerColor.postDelayed(runnable, 0);
                            }
                        });
                        // if(count<5) {

                        handlerColor.removeCallbacks(runnable);
                        handlerColor.postDelayed(runnable, 3000);
                    } else if (count == 5) {
//                        showTitleBar(true);
//                        exitFullScreen();
                        isBackOnTestStart = true;
                        exitFullScreen();
                        isTestRunning = false;
                        if (!alreadyShow) {
                            layoutField.setVisibility(View.VISIBLE);
//                        mainActivity.onChangeText(R.string.textSkip,true);
                            alreadyShow = true;
                        }
                        displayFail.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                layoutField.setVisibility(View.INVISIBLE);
                                displayFail.setEnabled(false);
                                displayPass.setEnabled(false);
                                displayFail.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque));
                                displayPass.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque));
                                testController.onServiceResponse(false, "Display",
                                        ConstantTestIDs.DISPLAY_ID);
                                try {
                                    utils.showToast(ctx,
                                            getResources().getString(R.string.txtManualFail));

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                isTestDone = true;
                                //   updateResultToServer();
//                                  showTitleBar(true);
//                                  exitFullScreen();
                                onNextPress();
                            }
                        });

                        displayPass.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                layoutField.setVisibility(View.INVISIBLE);
                                displayFail.setEnabled(false);
                                displayPass.setEnabled(false);
                                displayFail.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque));
                                displayPass.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque));
                                testController.onServiceResponse(true, "Display",
                                        ConstantTestIDs.DISPLAY_ID);
                                try {
                                    utils.showToast(ctx,
                                            getResources().getString(R.string.txtManualPass));

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                isTestDone = true;
                                //updateResultToServer();
//                                  showTitleBar(true);
//                                  exitFullScreen();
                                onNextPress();
                            }
                        });


                    }
                } catch (Exception e) {
                    e.getLocalizedMessage();
                }

            }
        };


    }


    /**
     * On resume
     */

    @Override
    public void onResume() {
        try {
//            if (isTestPerforming) {
//            } else {
//                hideShowTitleBar(true);
//            }
            if (isTestRunning) {
//                if (handler != null) {
//
//                    handler.removeCallbacksAndMessages(null);
//                }
//                handlerColor.removeCallbacks(runnable);

                count = 0;
                setColors();


                handlerColor.post(runnable);
            } else if (!isBackOnTestStart) {
                hideShowTitleBar(true);
                //   fullScreen();
            }
            //            nextButtonHandler = new Handler();
            if (isTestDone) {

                onNextPress();

            }

        } catch (Exception e) {
            logException(e, "DisplayManualFragment_onResume()");
        }

        super.onResume();
    }

    /**
     * On Pause
     */
    @Override
    public void onPause() {
        try {
        } catch (Exception e) {
            logException(e, "DisplayManualFragment_onPause()");
        }

        try {

            if (handlerColor != null) {
                handlerColor.removeCallbacks(runnable);
            }
            if (handler != null) {

                handler.removeCallbacksAndMessages(null);
            }
        } catch (Exception e) {
            logException(e, "DisplayManualFragment_onDetach()");
        }

        super.onPause();
    }


    /**
     * On Detach
     */
    @Override
    public void onDetach() {
        super.onDetach();
        try {
            if (handler != null) {

                handler.removeCallbacksAndMessages(null);
            }
        } catch (Exception e) {
            logException(e, "DisplayManualFragment_onDetach()");
        }

    }


    /**
     * hide and show title bar
     *
     * @param visible
     */

    private void showTitleBar(Boolean visible) {
        fragment = (TitleBarFragment) getFragment(R.id.headerContainer);
        if (fragment != null) {
            fragment.setTitleBarVisibility(visible);
        }
    }

    private void hideShowTitleBar(boolean visible) {
        try {
            fragment = (TitleBarFragment) getFragment(R.id.headerContainer);
            if (fragment != null) {
                if (visible) {
                    fragment.setTitleBarVisibility(true);
                    fragment.setSyntextVisibilty(false);
                    fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string.txtManualDisplay), false, false, 0);
                    if (!isTestRunning) {
                        if (Constants.isBackButton) {
                            Constants.isBackButton = false;
                            if (isTestPerform) {
                                mCallBack.onChangeText(utils.BUTTON_NEXT, true);
                            } else {
                                mCallBack.onChangeText(utils.BUTTON_SKIP, true);
                            }

                        } else {
                            if (isTestResumedAfter) {
                                mCallBack.onChangeText(utils.BUTTON_NEXT, true);
                            } else {
                                mCallBack.onChangeText(utils.BUTTON_SKIP, true);
                            }
                        }
                    } else {
                        if (isTestResumedAfter) {
                            mCallBack.onChangeText(utils.BUTTON_NEXT, true);
                        } else {
                            mCallBack.onChangeText(utils.BUTTON_SKIP, true);
                        }

                    }
                } else {
                    //                      fullScreen();
                    fragment.setTitleBarVisibility(false);

                    // mCallBack.onChangeText(utils.BUTTON_SKIP, true);
                }
            }
        } catch (Exception e) {
            logException(e, "DisplayManualFragment_hideShowTitleBar()");
        }


    }

    /**
     * onBackPressed
     */
    public void onBackPress() {
        try {
            txtDeadPixelScreen.setOnClickListener(null);
            snackShow(txtDeadPixelScreen);

        } catch (Exception e) {
            logException(e, "DisplayManualFragment_onBackPress()");
        }


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (snackbar != null) {
            if (snackbar.isShown()) {
                snackbar.dismiss();
            }
        }
        timer(getActivity(), true, ConstantTestIDs.DISPLAY_ID, DisplayManualFragment.this);
        exitFullScreen();
    }

    /**
     * Called when test is completed or user click on skip button
     */
    public void onNextPress() {
        try {
            timer(ctx,true,ConstantTestIDs.DISPLAY_ID,DisplayManualFragment.this);
            isTestRunning = false;
            TitleBarFragment titleBarFragment =
                    (TitleBarFragment) getFragment(R.id.headerContainer);
            if(!mainActivity.isManualRetest) {
                titleBarFragment.showSwitchLayout(true);

            }
            if (Constants.isSkipButton) {
                displayImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_display_svg),false,getActivity());

                txtManualDeadPixelStart.setEnabled(false);
                txtManualDeadPixelStart.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque));
                isTestPerform = false;
                isTestDone = true;
                utils.showToast(ctx, getResources().getString(R.string.txtManualFail));
                TestController testController = TestController.getInstance(getActivity());

//                testController.saveSkipResponse(-1, ConstantTestIDs.DISPLAY_ID);


            }
            MainActivity mainActivity = (MainActivity) getActivity();
            boolean semi = false;
            nextPress(mainActivity, semi);

        } catch (Exception e) {
            logException(e, "DisplayManualFragment_onNextPress()");
        }


    }

    /**
     * show snack bar when user click on back button
     *
     * @param relativeLayout
     */
    public void snackShow(RelativeLayout relativeLayout) {
        try {
            // handlerColor.removeCallbacks(runnable);
            snackbar = Snackbar.make(relativeLayout,
                    getResources().getString(R.string.txtBackPressed), Snackbar.LENGTH_LONG).setAction(getResources().getString(R.string.txtBackPressedYes), new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                        TitleBarFragment titleBarFragment =
                                (TitleBarFragment) getFragment(R.id.headerContainer);

                        if(!mainActivity.isManualRetest) {
                            titleBarFragment.showSwitchLayout(true);

                        }
                    if (getFragment(R.id.container) instanceof DisplayManualFragment) {


                        if (isTestRunning) {
                            isBackOnTestStart = true;
                            exitFullScreen();
                            timer(getActivity(), false, ConstantTestIDs.DISPLAY_ID,
                                    DisplayManualFragment.this);
                            mainActivity.onChangeText(R.string.textSkip, true);
                            handlerColor.removeCallbacks(runnable);
                            txtDeadPixelScreen.setBackgroundColor(ctx.getResources().getColor(R.color.white));
                            txtDeadPixelMain.setVisibility(View.VISIBLE);

                            //txtDeadPixelMain.setVisibility(View.VISIBLE);
//                            txtManualDeadPixelStart.setVisibility(View.VISIBLE);
//                            txtManualDeadPixel.setVisibility(View.VISIBLE);
                            /*startTimer();*/
                            hideShowTitleBar(true);

                            if (handler != null) {
                                handler.removeCallbacksAndMessages(null);
                            }
                            isTestRunning = false;
                            mCallBack.onChangeText(utils.BUTTON_SKIP, true);
                        } else {
                            if (getActivity() != null) {
                                getActivity().getSupportFragmentManager().beginTransaction().remove(Objects.requireNonNull(getFragmentManager().findFragmentById(R.id.container))).commit();

                                getActivity().getSupportFragmentManager().popBackStackImmediate();
                            }
                        }

                    }
                }
            });
            // Changing message text color
            snackbar.setActionTextColor(Color.WHITE);
            View sbView = snackbar.getView();
            TextView textView =
                    (TextView) sbView.findViewById(R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            (snackbar.getView()).getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;

            snackbar.show();

        } catch (Exception e) {
            logException(e, "DisplayManualFragment_snackShow()");
        }

    }

    @Override
    public void onStop() {
        if (mCallBack != null) {
            mCallBack.onChangeText(utils.BUTTON_NEXT, false);

        }
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
            //  logException(exp, "DisplayManualFragment_logException()");
        }

    }

    public void fullScreen() {

        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getActivity().getWindow().setAttributes(lp);
        }
    }

    public void exitFullScreen() {

        try {


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.layoutInDisplayCutoutMode =
                        WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;
                getActivity().getWindow().setAttributes(lp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            View decorView = getActivity().getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.STATUS_BAR_VISIBLE);
            decorView.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void timerFail() {
        displayFail.setEnabled(false);
        displayPass.setEnabled(false);
        displayFail.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque));
        displayPass.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque));
        txtManualDeadPixelStart.setEnabled(false);
        txtManualDeadPixelStart.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque));

        isTestPerform = false;
        exitFullScreen();
        displayImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_display_svg),false,getActivity());
        utils.showToast(ctx, getResources().getString(R.string.txtManualFail));

        onNextPress();

    }
}
