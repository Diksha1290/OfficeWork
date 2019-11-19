package com.officework.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.officework.R;
import com.officework.activities.MainActivity;
import com.officework.base.BaseFragment;
import com.officework.constants.AppConstantsTheme;
import com.officework.constants.Constants;
import com.officework.constants.JsonTags;
import com.officework.customViews.CustomButton;
import com.officework.customViews.IconView;
import com.officework.interfaces.InterfaceButtonTextChange;
import com.officework.models.AutomatedTestListModel;
import com.officework.testing_profiles.Controller.ManualTestsOperation;
import com.officework.testing_profiles.Controller.TestController;
import com.officework.testing_profiles.ui.fragment.Manual2SemiAutomaticTestsFragment;
import com.officework.testing_profiles.utils.ConstantTestIDs;
import com.officework.utils.ExceptionLogUtils;
import com.officework.utils.ManualDataStable;
import com.officework.utils.TestResultUpdateToServer;
import com.officework.utils.Utilities;

/**
 * Created by Girish on 9/5/2016.
 */

@SuppressLint("ValidFragment")
public class SpeakerManualFragment extends BaseFragment implements View.OnClickListener {
    public static boolean isNext = false;
    View view;
    Utilities utils;
    Context ctx;
    IconView imgViewSpeaker;
    InterfaceButtonTextChange mCallBack;
    MediaPlayer mPlayer;
    Handler handler = null;
    Runnable runnable;
    LinearLayout speakerManual;
    ManualDataStable manualDataStable;
    boolean isTestPerformed = false;
    Handler nextButtonHandler = null;
    boolean check = false;
    CountDownTimer cTimer = null;
    boolean isDialogShown = false;
    TestResultUpdateToServer testResultUpdateToServer;
    CustomButton btnSpeakerOk, btnSpeakerNotOk;
    private boolean isPlaying = false;


    public SpeakerManualFragment(InterfaceButtonTextChange callback) {
        mCallBack = callback;
    }

    public SpeakerManualFragment() {
    }

    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        try {
            if (view == null) {
                view = inflater.inflate(R.layout.fragment_manual_speaker, null);
                utils = Utilities.getInstance(getActivity());
                manualDataStable = new ManualDataStable(mCallBack);
                ctx = getActivity();
                testResultUpdateToServer = new TestResultUpdateToServer(utils, ctx, getActivity());
            //    Crashlytics.getInstance().log(FragmentTag.SPEAKER_MANUAL_FRAGMENT.name());
                initViews();
            }
        } catch (Exception e) {
            logException(e, "SpeakerManualFragment_initViews()");
        }

        return view;
    }

    /**
     * Initialize view
     */
    private void initViews() {
        try {
            MainActivity mainActivity = (MainActivity) getActivity();

            mainActivity.onChangeText(R.string.textSkip, true);
            imgViewSpeaker = (IconView) view.findViewById(R.id.mImgViewSpeaker);
            btnSpeakerOk = (CustomButton) view.findViewById(R.id.btnSpeakerOk);
            btnSpeakerNotOk = (CustomButton) view.findViewById(R.id.btnSpeakerNotOk);
            btnSpeakerNotOk.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque30));
            btnSpeakerOk.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque30));
            btnSpeakerOk.setOnClickListener(this);
            btnSpeakerNotOk.setOnClickListener(this);
            imgViewSpeaker.setOnClickListener(this);
            handler = new Handler();
            speakerManual = (LinearLayout) view.findViewById(R.id.speakerManual);
            /*intializeMediaPlayer();*/
        } catch (Exception e) {
            logException(e, "SpeakerManualFragment_initViews()");
        }

    }

    /**
     * Here change the header text
     ***/
    @Override
    public void onResume() {
        try {
            TitleBarFragment fragment = (TitleBarFragment) getFragment(R.id.headerContainer);
            nextButtonHandler = new Handler();

            if (fragment != null) {
                fragment.setTitleBarVisibility(true);
                fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string.Speaker),
                        true, false, 0);
                //  mCallBack.onChangeText(utils.BUTTON_SKIP, true);
                if (Constants.isBackButton == true) {
                    if (manualDataStable.checkSingleHardware(JsonTags.MMR_32.name(), ctx, utils) == 1) {
                        mCallBack.onChangeText(utils.BUTTON_NEXT, true);
                        Constants.isBackButton = false;
                    } else if (isNext) {
                        isNext = false;
                        mCallBack.onChangeText(utils.BUTTON_NEXT, true);
                    } else {
                        mCallBack.onChangeText(utils.BUTTON_SKIP, true);
                        Constants.isBackButton = false;
                    }

                } else {
                    if (isTestPerformed) {
                        mCallBack.onChangeText(utils.BUTTON_NEXT, true);
                        nextButtonHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                nextButtonHandler.removeCallbacksAndMessages(null);
                                onNextPress();
                            }
                        }, 1000);
                    } else {
                        mCallBack.onChangeText(utils.BUTTON_SKIP, true);
                    }
                }
            }
            imgViewSpeaker.setOnClickListener(this);
            imgViewSpeaker.setImageDrawable(getResources().getDrawable(R.drawable.ic_speaker_blue_svg_128));
            if (check == false) {
                btnSpeakerNotOk.setEnabled(false);
                btnSpeakerOk.setEnabled(false);
                btnSpeakerNotOk.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque30));
                btnSpeakerOk.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque30));
            } else {
                btnSpeakerNotOk.setEnabled(true);
                btnSpeakerOk.setEnabled(true);
                btnSpeakerNotOk.setBackgroundColor(AppConstantsTheme.styleTheme.backgroundColor);
                btnSpeakerOk.setBackgroundColor(AppConstantsTheme.styleTheme.backgroundColor);

                imgViewSpeaker.setImageDrawable(getResources().getDrawable(R.drawable.ic_speaker_blue_svg_128), true, getActivity());

            }


        } catch (Exception e) {
            logException(e, "SpeakerManualFragment_onResume()");
        }

        super.onResume();
    }

    /**
     * onPause
     */
    @Override
    public void onPause() {
        try {
/**
 * stop Playing
 */
            if (mPlayer != null) {
                mPlayer.release();
                mPlayer = null;
                isPlaying = false;
                handler.removeCallbacksAndMessages(null);
            }
            nextButtonHandler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            logException(e, "SpeakerManualFragment_onPause()");
        }


        super.onPause();

    }

    /**
     * Initialize media player
     */
    private void intializeMediaPlayer() {

        try {
//            mPlayer = new MediaPlayer();
//            Uri video = Uri.parse("android.resource://com.megammr/raw/megammr");
//            mPlayer.setDataSource(getActivity(), video);
            mPlayer = MediaPlayer.create(getActivity(), R.raw.megammr);
            mPlayer.prepare();
        } catch (Exception e) {
            logException(e, "SpeakerManualFragment_intializeMediaPlayer()");
            e.printStackTrace();
        }
    }

    /**
     * onClick
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        super.onClick(v);
        try {
            switch (v.getId()) {
                case R.id.mImgViewSpeaker:
                    if (!isPlaying) {
                        isPlaying = true;
                        imgViewSpeaker.setImageDrawable(getResources().getDrawable(R.drawable.ic_speaker_green_svg_128), true, getActivity());
                        intializeMediaPlayer();
                        mPlayer.start();
                        handler.postDelayed(runnable = new Runnable() {
                            @Override
                            public void run() {
                                btnSpeakerOk.setEnabled(true);
                                btnSpeakerOk.setBackgroundColor(AppConstantsTheme.styleTheme.backgroundColor);
                                check = true;
                            }
                        }, 2000);
                        handler.postDelayed(runnable = new Runnable() {
                            @Override
                            public void run() {
                                imgViewSpeaker.setOnClickListener(null);
                                mPlayer.stop();
                                isPlaying = false;
                                btnSpeakerNotOk.setEnabled(true);
                                btnSpeakerNotOk.setBackgroundColor(AppConstantsTheme.styleTheme.backgroundColor);

                            }
                        }, 7200);
                  /*  handler.postDelayed(runnable = new Runnable() {
                        @Override
                        public void run() {
                            imgViewSpeaker.setOnClickListener(null);
                            mPlayer.stop();
                            isPlaying = false;
                            showAlertDialog(getResources().getString(R.string.txtAlertTitleGreat)
                            , getResources().getString(R.string.txtManualSpeaker_test_Dialog),
                            getResources().getString(R.string.Yes),
                                    getResources().getString(R.string.No));
                        }
                    }, 7200);*/
                    } else {
                        utils.showToast(ctx, getResources().getString(R.string.txtPleaseWait));
                    }
                    break;
                case R.id.btnSpeakerOk:
                    MainActivity mainActivity = (MainActivity) getActivity();

                    mainActivity.onChangeText(R.string.textSkip, false);
                    imgViewSpeaker.setOnClickListener(null);
                    try {
                        mPlayer.stop();
                        isPlaying = false;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    imgViewSpeaker.setImageDrawable(getResources().getDrawable(R.drawable.ic_speaker_green_svg_128), true, getActivity());
                    // utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_32.name(), 1);
                    //updateResultToServer();
                    mCallBack.onChangeText(utils.BUTTON_NEXT, true);
                    isNext = true;
                    isTestPerformed = true;
                    handler.removeCallbacksAndMessages(null);
                    btnSpeakerNotOk.setEnabled(false);
                    btnSpeakerNotOk.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque30));
                    btnSpeakerOk.setEnabled(false);
                    btnSpeakerOk.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque30));
                    utils.showToast(ctx, getResources().getString(R.string.txtManualPass));

                    nextButtonHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {


                            nextButtonHandler.removeCallbacksAndMessages(null);
                            onNextPress();
                        }
                    }, 2000);

                    TestController testController = TestController.getInstance(getActivity());
                    testController.onServiceResponse(true, "Speaker", ConstantTestIDs.SPEAKER_ID);

                    break;
                case R.id.btnSpeakerNotOk:
                    MainActivity mainActivity1 = (MainActivity) getActivity();

                    mainActivity1.onChangeText(R.string.textSkip, false);
                    handler.removeCallbacksAndMessages(null);
                    imgViewSpeaker.setImageDrawable(getResources().getDrawable(R.drawable.ic_speaker_green_svg_128), false, getActivity());
                    btnSpeakerNotOk.setEnabled(false);
                    btnSpeakerNotOk.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque30));
                    btnSpeakerOk.setEnabled(false);
                    btnSpeakerOk.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque30));
                    mCallBack.onChangeText(utils.BUTTON_NEXT, true);
                    utils.showToast(ctx, getResources().getString(R.string.txtManualFail));
                    TestController testController1 = TestController.getInstance(getActivity());
                    testController1.onServiceResponse(false, "Speaker", ConstantTestIDs.SPEAKER_ID);
                    // utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_32.name(), 0);
                    //  updateResultToServer();
                    isNext = true;
                    nextButtonHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {


                            nextButtonHandler.removeCallbacksAndMessages(null);
                            onNextPress();
                        }
                    }, 2000);

                    break;
            }
        } catch (Exception e) {
            logException(e, "SpeakerManualFragment_onClick()");
        }


    }

    /**
     * show alert after playing completition
     *
     * @param dialogTitle
     * @param dialogMessage
     * @param btnTextPositive
     * @param btnTextNegative
     */
    public void showAlertDialog(String dialogTitle, String dialogMessage, String btnTextPositive,
                                String btnTextNegative) {
        try {
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                    ctx);

            // Setting Dialog Title
            alertDialog.setTitle(dialogTitle);

            // Setting Dialog Message
            alertDialog.setMessage(dialogMessage);
            alertDialog.setCancelable(false);
            // Setting Icon to Dialog
            /*alertDialog.setIcon(R.drawable.tick);*/

            // Setting OK Button
            alertDialog.setPositiveButton(btnTextPositive
                    , new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to execute after dialog closed
                            // utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_32.name(), 1);
                            //  updateResultToServer();
                            mCallBack.onChangeText(utils.BUTTON_NEXT, true);
                            isNext = true;
                            isTestPerformed = true;
                            utils.showToast(ctx, getResources().getString(R.string.txtManualPass));
                            TestController testController =
                                    TestController.getInstance(getActivity());
                            testController.onServiceResponse(true, "Speaker",
                                    ConstantTestIDs.SPEAKER_ID);
                            nextButtonHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {


                                    nextButtonHandler.removeCallbacksAndMessages(null);
                                    onNextPress();
                                }
                            }, 2000);
                        }
                    });
            alertDialog.setNegativeButton(btnTextNegative, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mCallBack.onChangeText(utils.BUTTON_NEXT, true);
                    utils.showToast(ctx, getResources().getString(R.string.txtManualFail));
                    TestController testController = TestController.getInstance(getActivity());
                    testController.onServiceResponse(false, "Speaker", ConstantTestIDs.SPEAKER_ID);
                    // utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_32.name(), 0);
                    // updateResultToServer();
                    isNext = true;
                    nextButtonHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {


                            nextButtonHandler.removeCallbacksAndMessages(null);
                            onNextPress();
                        }
                    }, 2000);
                }
            });

            // Showing Alert Message
            alertDialog.show();
        } catch (Exception e) {
            logException(e, "SpeakerManualFragment_showAlertDialog()");
        }

    }

    /**
     * Called when test is completed or user click on skip button
     */
    public void onNextPress() {
        try {
            MainActivity activity = (MainActivity) getActivity();
            activity.onChangeText(R.string.textSkip, false);
            if (Constants.isSkipButton) {
                imgViewSpeaker.setImageDrawable(getResources().getDrawable(R.drawable.ic_speaker_green_svg_128), false, getActivity());
                utils.showToast(ctx, getResources().getString(R.string.txtManualFail));
                // utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_32.name(), AsyncConstant
                // .TEST_FAILED);
                // updateResultToServer();
            }

            MainActivity mainActivity = (MainActivity) getActivity();
//            if (Constants.isDoAllClicked && mainActivity.index != mainActivity
//            .automatedTestListModels.size()) {
//
//                replaceFragment(R.id.container, ManualTestsOperation.launchScreens(mainActivity
//                .automatedTestListModels.get(mainActivity.index).getTest_id(), mainActivity
//                .automatedTestListModels.get(mainActivity.automatedTestListModels.size() - 1)
//                .getTest_id()), null, false);
//                mainActivity.index++;
//
//            }
            if (Constants.isDoAllClicked && activity.manualIndex != Manual2SemiAutomaticTestsFragment.testListModelList.size()) {
                AutomatedTestListModel automatedTestListModel =
                        Manual2SemiAutomaticTestsFragment.testListModelList.get(activity.manualIndex);

                replaceFragment(R.id.container,
                        ManualTestsOperation.launchScreens(automatedTestListModel,
                                utils.getPreference(getActivity(), Constants.ANDROID_ID, ""),getActivity()),
                        null, false);
                activity.manualIndex++;

            } else {
//                clearAllStack();
//                replaceFragment(R.id.container, ManualTestsOperation.launchScreens(-1,
//                mainActivity.automatedTestListModels.get(mainActivity.automatedTestListModels
//                .size()-1).getTest_id()), FragmentTag.GPS_FRAGMENT.name(), true);
                popFragment(R.id.container);
            }
        } catch (Exception e) {
            logException(e, "SpeakerManualFragment_onNextPress()");
        }


    }

    @Override
    public void onStop() {

        mCallBack.onChangeText(utils.BUTTON_NEXT, false);

        super.onStop();
    }

    /**
     * onBackPressed
     */
    public void onBackPress() {
        try {
            snackShow(speakerManual);
        } catch (Exception e) {
            logException(e, "SpeakerManualFragment_onBackPress()");
        }

    }

    /**
     * show snack bar when user click on back button
     * <p>
     * <p>
     * <p>
     * /**
     * This method update data to backend API after performing each test
     * update Test Data
     */

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
            //    logException(exp, "SpeakerManualFragment_logException()");
        }

    }
}
