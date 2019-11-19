package com.officework.fragments;

/**
 * Created by Girish on 9/5/2016.
 */

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.officework.R;
import com.officework.activities.MainActivity;
import com.officework.base.BaseFragment;
import com.officework.constants.AppConstantsTheme;
import com.officework.constants.Constants;
import com.officework.customViews.CustomButton;
import com.officework.customViews.IconView;
import com.officework.interfaces.InterfaceAlertDissmiss;
import com.officework.interfaces.InterfaceButtonTextChange;
import com.officework.testing_profiles.Controller.TestController;
import com.officework.testing_profiles.utils.ConstantTestIDs;
import com.officework.utils.ExceptionLogUtils;
import com.officework.utils.TestResultUpdateToServer;
import com.officework.utils.Utilities;

import java.io.File;
import java.io.IOException;

/**
 * Created by Ashwani on 8/18/2016.
 */
@SuppressLint("ValidFragment")
public class MicManualFragment extends BaseFragment implements View.OnClickListener, InterfaceAlertDissmiss {
    View view;
    Utilities utils;
    Context ctx;
    IconView  mImgViewMicPlay;
    IconView mImgViewMicRecord;
    TextView mtxtViewDuration;
    InterfaceButtonTextChange mCallBack;
    private static final String LOG_TAG = "AudioRecordTest";
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;
    boolean mStartRecording = false;
    boolean mStartPlaying = false;
    int timerDuration = 6000;
    private CountDownTimer timer;
    LinearLayout micManual;
    final public int REQUEST_CODE_ASK_PERMISSIONS_RECORD_AUDIO = 121;
    final public int REQUEST_CODE_ASK_PERMISSIONS_STORAGE = 1234;
    private File audioFile;
    private static boolean isFirstTime = true;
    Handler nextButtonHandler = null;
    TextView txtViewStopDescription, txtViewInstructions;
    CountDownTimer cTimer = null;
    boolean isDialogShown = false;
    TestResultUpdateToServer testResultUpdateToServer;
    LinearLayout layoutOkNotOkButton;
    CustomButton btnMicStop, btnMicOk, btnMicNotOk;
    boolean isStoppedPress = false;
    long timeOnStopPress = 0;
    boolean isRecordingDone = false;
    boolean isListeningDone = false;
    boolean started=false;
   TestController testController;
    public MicManualFragment(InterfaceButtonTextChange callback) {
        mCallBack = callback;
    }

    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        try {
            if (view == null) {
                view = inflater.inflate(R.layout.fragment_manual_mic, null);
                utils = Utilities.getInstance(getActivity());
                ctx = getActivity();
           //     Crashlytics.log(FragmentTag.MIC_MANUAL_FRAGMENT.name());
                testResultUpdateToServer = new TestResultUpdateToServer(utils, ctx, getActivity());

                initViews();
            }
            return view;

        } catch (Exception e) {
            logException(e, "MicManualFragment_initUI()");
            return null;
        }

    }

    public MicManualFragment() {
    }

    /**
     * Initialize view
     */




    private void initViews() {
        try {
            MainActivity mainActivity = (MainActivity)getActivity();

            mainActivity.onChangeText(R.string.textSkip,true);
            if (isFirstTime) {
                isFirstTime = false;
                checkRuntimePermisson(false);
            }
            testController = TestController.getInstance(getActivity());
            txtViewInstructions = (TextView) view.findViewById(R.id.txtViewInstructions);
            txtViewStopDescription = (TextView) view.findViewById(R.id.txtViewStopDescription);
            layoutOkNotOkButton = (LinearLayout) view.findViewById(R.id.layoutOkNotOkButton);
            btnMicStop = (CustomButton) view.findViewById(R.id.btnMicStop);
            btnMicOk = (CustomButton) view.findViewById(R.id.btnMicOk);
            btnMicNotOk = (CustomButton) view.findViewById(R.id.btnMicNotOk);
            btnMicOk.setOnClickListener(this);
            btnMicNotOk.setOnClickListener(this);
            btnMicStop.setOnClickListener(this);
            btnMicStop.setEnabled(false);
            btnMicStop.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque30));
            btnMicNotOk.setEnabled(false);
            btnMicOk.setEnabled(false);
            btnMicOk.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque30));
            btnMicNotOk.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque30));
            micManual = (LinearLayout) view.findViewById(R.id.micManual);
            mImgViewMicRecord = (IconView) view.findViewById(R.id.imgViewMicRecord);
            mImgViewMicRecord.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic_speaker));
            mImgViewMicRecord.setEnabled(true);
            mImgViewMicRecord.setOnClickListener(this);
            mImgViewMicPlay = (IconView) view.findViewById(R.id.imgViewMicPlay);
            mImgViewMicPlay.setOnClickListener(this);
            layoutOkNotOkButton.setVisibility(View.INVISIBLE);
            mImgViewMicPlay.setEnabled(true);
            mImgViewMicPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_speaker_blue_svg_128));
            mtxtViewDuration = (TextView) view.findViewById(R.id.txtViewDuration);
            mtxtViewDuration.setText("00:00");
            audioFile = getOutputMediaFile();

            if (isRecordingDone) {
                layoutOkNotOkButton.setVisibility(View.VISIBLE);
                // txtViewStopDescription.setVisibility(View.INVISIBLE);
            } /*else {
            txtViewStopDescription.setVisibility(View.VISIBLE);
        }*/
       /* if (isRecordingDone && !isListeningDone) {
            layoutOkNotOkButton.setVisibility(View.INVISIBLE);
            btnMicNotOk.setEnabled(false);
            btnMicOk.setEnabled(false);
            btnMicOk.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque30));
            btnMicNotOk.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque30));
        } else if (isListeningDone) {
            mImgViewMicPlay.setEnabled(false);
            layoutOkNotOkButton.setVisibility(View.VISIBLE);
        }*/
        } catch (Exception e) {
            logException(e, "MicManualFragment_initViews()");

        }

    }

    /**
     * onResume
     */
    @Override
    public void onResume() {
        try {
            TitleBarFragment fragment = (TitleBarFragment) getFragment(R.id.headerContainer);
            nextButtonHandler = new Handler();

            if (fragment != null) {
                fragment.setTitleBarVisibility(true);
                fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string.Mic), true, false, 0);
                mCallBack.onChangeText(utils.BUTTON_SKIP, true);
                initViews();

                mImgViewMicRecord.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic_speaker),"blue",getActivity());


            }


        } catch (Exception e) {
            logException(e, "MicManualFragment_onResume()");
        }

        super.onResume();
    }

    /**
     * check WRITE_EXTERNAL_STORAGE and RECORD_AUDIO permission
     *
     * @param flag
     */
    private void checkRuntimePermisson(boolean flag) {
        try {
            int hasRecordAudioPermission = 0;
            int hasWriteExternalStoragePermission = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                hasWriteExternalStoragePermission = getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                hasRecordAudioPermission = getActivity().checkSelfPermission(Manifest.permission.RECORD_AUDIO);
                if (hasWriteExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_CODE_ASK_PERMISSIONS_STORAGE);
                    return;
                } else if (hasRecordAudioPermission != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},
                            REQUEST_CODE_ASK_PERMISSIONS_RECORD_AUDIO);
                    return;
                }
            }
            if (flag) {
                intializeRecorder();
            }
        } catch (Exception e) {
            logException(e, "MicManualFragment_checkRuntimePermisson()");
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
                case R.id.imgViewMicRecord:
                    started=true;
                    timeOnStopPress = 0;
                    isStoppedPress = false;
                    checkRuntimePermisson(true);
                    break;
                case R.id.imgViewMicPlay:
                    txtViewStopDescription.setVisibility(View.VISIBLE);
                    Log.d("Stop Pressed", String.valueOf(timeOnStopPress));
                    mStartPlaying = true;
                    if (mPlayer == null || !mPlayer.isPlaying()) {
                        onPlay(mStartPlaying);
                        if (mStartPlaying) {
                            mImgViewMicPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_speaker_green_svg_128),true,getActivity());
                            mImgViewMicPlay.setEnabled(false);
                            //setText("Stop playing");
                        } else {
                            mImgViewMicPlay.getDrawable().setColorFilter(getResources().getColor(R.color.blue), PorterDuff.Mode.MULTIPLY);
                            // setText("Start playing");
                        }
                        mStartPlaying = !mStartPlaying;
                    } else {
                        utils.showToast(ctx, getResources().getString(R.string.txtPleaseWait));
                    }
                    mStartPlaying = !mStartPlaying;
                    break;
                case R.id.btnMicNotOk:
                    timer.cancel();
                    MainActivity mainActivity = (MainActivity)getActivity();

                    mainActivity.onChangeText(R.string.textSkip,false);
                    btnMicOk.setEnabled(false);
                    btnMicNotOk.setEnabled(false);
                    btnMicNotOk.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque30));
                    btnMicOk.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque30));
                   mImgViewMicPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_speaker_green_svg_128),false,getActivity());
                    mCallBack.onChangeText(utils.BUTTON_NEXT, true);
                    utils.showToast(ctx, getResources().getString(R.string.txtManualFail));
                    TestController testController = TestController.getInstance(getActivity());
                    testController.onServiceResponse(false,"Mic", ConstantTestIDs.MIC_ID);
                    testController.onServiceResponse(false,"SPEAKER", ConstantTestIDs.SPEAKER_ID);
                   // utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_33.name(), 0);
                    //updateResultToServer();
                    mStartRecording = true;
                    //mImgViewMicRecord.setVisibility(View.VISIBLE);
                    // mImgViewMicRecord.setImageDrawable(getResources().getDrawable(R.drawable.mic_grey));
                    //  mImgViewMicPlay.setVisibility(View.GONE);
                    nextButtonHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            nextButtonHandler.removeCallbacksAndMessages(null);
                            onNextPress();
                        }
                    }, 2000);
                    break;
                case R.id.btnMicOk:
                    MainActivity mainActivity1 = (MainActivity)getActivity();

                    mainActivity1.onChangeText(R.string.textSkip,false);
                    timer.cancel();
                    btnMicOk.setEnabled(false);
                    btnMicOk.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque30));
                    btnMicNotOk.setEnabled(false);
                    btnMicNotOk.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque30));
                    mImgViewMicPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_speaker_green_svg_128),true,getActivity());
                    //utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_33.name(), 1);
                   // updateResultToServer();
                    mCallBack.onChangeText(utils.BUTTON_NEXT, true);
                    utils.showToast(ctx, getResources().getString(R.string.txtManualPass));
                    TestController testController1 = TestController.getInstance(getActivity());
                    testController1.onServiceResponse(true,"MIC", ConstantTestIDs.MIC_ID);
                    testController1.onServiceResponse(true,"SPEAKER", ConstantTestIDs.SPEAKER_ID);
                    mStartRecording = true;
                    mImgViewMicPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_speaker_blue_svg_128));
                    nextButtonHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            nextButtonHandler.removeCallbacksAndMessages(null);
                            onNextPress();
                        }
                    }, 2000);
                    break;
                case R.id.btnMicStop:
                    layoutOkNotOkButton.setVisibility(View.VISIBLE);
                    // txtViewStopDescription.setVisibility(View.INVISIBLE);
                    txtViewStopDescription.setText(getResources().getString(R.string.txtMicokNotOkText));
                   txtViewInstructions.setText(getResources().getString(R.string.txtMicPlayInstructions));
                    isRecordingDone = true;
                    timer.cancel();
                    isStoppedPress = true;
                    btnMicStop.setEnabled(false);
                    btnMicStop.setVisibility(View.INVISIBLE);
                    mtxtViewDuration.setText("00:00");
                    onRecord(mStartRecording);
                    utils.showToast(ctx, getResources().getString(R.string.txtRecordingStop));
                    mImgViewMicRecord.setVisibility(View.GONE);
                    mImgViewMicPlay.setVisibility(View.VISIBLE);
                    mImgViewMicRecord.setEnabled(true);
            }
        } catch (Exception e) {
            logException(e, "MicManualFragment_onClick()");
        }

    }

    /**
     * store recoded audio
     *
     * @return
     */
    private static File getOutputMediaFile() {
        try {
            File mediaStorageDir = new File(Environment.getExternalStorageDirectory().toString(), "MegaMMR");
            //if this "JCGCamera folder does not exist
            if (!mediaStorageDir.exists()) {
                //if you cannot make this folder return
                if (!mediaStorageDir.mkdirs()) {
                    return null;
                }
            }
            File mediaFile;
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "MegaMMR.3gp");
            return mediaFile;
        } catch (Exception e) {
            MicManualFragment micManualFragment = new MicManualFragment(null);
            micManualFragment.logException(e, "MicManualFragment_intializeRecorder()");

            return null;
        }

    }

    /**
     * Initialize recorder
     */
    private void intializeRecorder() {
        try {
            mStartRecording = true;
            onRecord(mStartRecording);
            if (mStartRecording) {
                mImgViewMicRecord.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic_speaker),true,getActivity());
            } else {
                mImgViewMicRecord.getDrawable().setColorFilter(getResources().getColor(R.color.blue), PorterDuff.Mode.MULTIPLY);
            }
            mStartRecording = !mStartRecording;
            mImgViewMicRecord.setEnabled(false);
        } catch (Exception e) {
            logException(e, "MicManualFragment_intializeRecorder()");
        }

    }

    /**
     * start playing after recording
     * user click on play button on screen
     */
    private void startPlaying() {
        try {
            mPlayer = new MediaPlayer();
            try {
                mPlayer.setDataSource(audioFile.getAbsolutePath());
                mPlayer.prepare();
                mPlayer.start();
            } catch (IOException e) {
                Log.e(LOG_TAG, "prepare() failed");
            }
            showTimer(false);
        } catch (Exception e) {
            logException(e, "MicManualFragment_startPlaying()");
        }

    }

    /**
     * handle recording
     *
     * @param start
     */
    private void onRecord(boolean start) {
        try {
            if (start) {
                startRecording();
            } else {
                stopRecording();
            }
        } catch (Exception e) {
            logException(e, "MicManualFragment_onRecord()");
        }

    }

    /**
     * stop playing
     */
    private void stopPlaying() {
        try {
            mPlayer.release();
            mPlayer = null;

        } catch (Exception e) {
            logException(e, "MicManualFragment_stopPlaying()");
        }

    }

    /**
     * start recording
     */
    private void startRecording() {
        try {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mRecorder.setOutputFile(audioFile.getAbsolutePath());
            mRecorder.setMaxDuration(timerDuration);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.prepare();
            mRecorder.start();
            showTimer(true);
        } catch (Exception e) {
            logException(e, "MicManualFragment_startRecording()");
        }

    }

    /**
     * show timer for recording
     *
     * @param isRecorder
     */
    private void showTimer(final boolean isRecorder) {
        try {
            long timerForCountDown = 0;
            if (isStoppedPress) {
                timerForCountDown = timeOnStopPress;
            } else {
                timerForCountDown = timerDuration;
            }

            timer = new CountDownTimer(timerForCountDown, 1000) {
                int time = 0;
                int timerCount = 0;

                @Override
                public void onFinish() {
                    mtxtViewDuration.setText("00:00");
                    if (isRecorder) {
// Stop Recording
                        onRecord(mStartRecording);
                        utils.showToast(ctx, getResources().getString(R.string.txtRecordingStop));
                        mImgViewMicRecord.setVisibility(View.GONE);
                        mImgViewMicPlay.setVisibility(View.VISIBLE);
                        mImgViewMicRecord.setEnabled(true);
                        txtViewStopDescription.setText(getResources().getString(R.string.txtMicokNotOkText));
                        txtViewInstructions.setText(getResources().getString(R.string.txtMicPlayInstructions));
                        btnMicStop.setVisibility(View.INVISIBLE);
                        layoutOkNotOkButton.setVisibility(View.VISIBLE);
                        isRecordingDone = true;
                    } else {
                        mImgViewMicPlay.setEnabled(true);
                        if (mPlayer == null || !mPlayer.isPlaying()) {
                            stopPlaying();
                        }
                        mImgViewMicPlay.setEnabled(false);
                        mImgViewMicPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_speaker_blue_svg_128));
                        btnMicNotOk.setBackgroundColor(AppConstantsTheme.styleTheme.backgroundColor);
                        btnMicNotOk.setEnabled(true);
                        btnMicOk.setBackgroundColor(AppConstantsTheme.styleTheme.backgroundColor);
                        btnMicOk.setEnabled(true);
                        isListeningDone = true;

/* showAlertDialog(getResources().getString(R.string.txtAlertTitleGreat), getResources().getString(R.string.txtManualMic_test_Dialog), getResources().getString(R.string.Yes),
getResources().getString(R.string.No));*/
                    }
                }

                @Override
                public void onTick(long millisUntilFinished) {
                    timeOnStopPress = timerDuration - millisUntilFinished + 1000;
                    if (isRecorder) {
                        String seconds = String.valueOf(millisUntilFinished / 1000);
                        if (seconds.length() > 1) {
                            mtxtViewDuration.setText("00:" + seconds);
                        } else {
                            mtxtViewDuration.setText("00:0" + seconds);
                        }
                        if (timerCount == 2) {
                            btnMicStop.setEnabled(true);
                            btnMicStop.setBackgroundColor(AppConstantsTheme.styleTheme.backgroundColor);
                        } else {
                            timerCount++;
                        }
                    } else {
                        ++time;
                        if (time > 9) {
                            mtxtViewDuration.setText("00:" + time);
                        } else {
                            mtxtViewDuration.setText("00:0" + time);
                        }
                        if (time >= 2) {
                            btnMicOk.setBackgroundColor(AppConstantsTheme.styleTheme.backgroundColor);
                            btnMicOk.setEnabled(true);

                        }
                    }
                }

            }.start();
        } catch (Exception e) {

        }


    }

    /**
     * show alert after listening
     * which ask user has listen something or not
     *
     * @param dialogTitle
     * @param dialogMessage
     * @param btnTextPositive
     * @param btnTextNegative
     */
    public void showAlertDialog(String dialogTitle, String dialogMessage, String btnTextPositive, String btnTextNegative) {
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
                          //  utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_33.name(), 1);
                           // updateResultToServer();
                            mCallBack.onChangeText(utils.BUTTON_NEXT, true);
                            utils.showToast(ctx, getResources().getString(R.string.txtManualPass));
                            TestController testController = TestController.getInstance(getActivity());
                            testController.onServiceResponse(true,"TouchScreen", ConstantTestIDs.MIC_ID);
                            testController.onServiceResponse(true,"Speaker", ConstantTestIDs.SPEAKER_ID);
                            mStartRecording = true;
                            mImgViewMicPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_speaker_blue_svg_128));
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
                    testController.onServiceResponse(false,"Mic", ConstantTestIDs.MIC_ID);
                    testController.onServiceResponse(false,"Speaker", ConstantTestIDs.SPEAKER_ID);
                    //utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_33.name(), 0);
                   // updateResultToServer();
                    mStartRecording = true;
                    mImgViewMicRecord.setVisibility(View.VISIBLE);
                    mImgViewMicRecord.getDrawable().setColorFilter(getResources().getColor(R.color.blue), PorterDuff.Mode.MULTIPLY);
                    mImgViewMicPlay.setVisibility(View.GONE);
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
            logException(e, "MicManualFragment_showAlertDialog()");
        }

    }

    /**
     * stop recording
     */
    private void stopRecording() {
        try {
            if (mRecorder != null) {
                mRecorder.stop();
                mRecorder.release();
                mRecorder = null;
            }
        } catch (Exception e) {
            logException(e, "MicManualFragment_stopRecording()");
        }

    }

    /**
     * onPause
     */
    @Override
    public void onPause() {
        super.onPause();
        try {
            /**
             * stop recording onPAuse
             */
            if (mRecorder != null) {
                mRecorder.release();
                mRecorder = null;
                mStartRecording = true;
                timer.cancel();
            }

            /**
             * stop playing
             */
            if (mPlayer != null) {
                mPlayer.release();
                mPlayer = null;
                mStartPlaying = true;
                timer.cancel();
            }
            nextButtonHandler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            logException(e, "MicManualFragment_onPause()");
        }


    }

    /**
     * handle play music
     *
     * @param start
     */
    private void onPlay(boolean start) {
        try {
            if (start) {
                startPlaying();
            } else {
                stopPlaying();
            }
        } catch (Exception e) {
            logException(e, "MicManualFragment_onPlay");
        }

    }

    @Override
    public void onStop() {

        mCallBack.onChangeText(utils.BUTTON_NEXT, false);

        super.onStop();
    }

    /**
     * Here change the header text
     ***/


    public void onNextPress() {
        try {
            MainActivity activity = (MainActivity) getActivity();
//            activity.onChangeText(R.string.textSkip,false);
            if (Constants.isSkipButton) {
                mImgViewMicPlay.setImageDrawable(getResources().getDrawable(R.drawable.ic_mic_blue_svg_128),false,getActivity());
                mImgViewMicRecord.setImageDrawable(getResources().getDrawable(R.drawable.ic_speaker_red_svg_128),false,getActivity());
                testController.onServiceResponse(false,"Mic", ConstantTestIDs.MIC_ID);
                testController.onServiceResponse(false,"Speaker", ConstantTestIDs.SPEAKER_ID);
                 utils.showToast(ctx, getResources().getString(R.string.txtManualFail));
               // utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_33.name(), AsyncConstant.TEST_FAILED);

               // updateResultToServer();
            }
                 boolean semi=false;
                nextPress(activity,semi);
//            MainActivity mainActivity = (MainActivity)getActivity();
////            if (Constants.isDoAllClicked && mainActivity.index != mainActivity.automatedTestListModels.size()) {
//
//                replaceFragment(R.id.container, ManualTestsOperation.launchScreens(mainActivity.automatedTestListModels.get(mainActivity.index).getTest_id(), mainActivity.automatedTestListModels.get(mainActivity.automatedTestListModels.size() - 1).getTest_id()),null, false);
//                mainActivity.index++;
//
//            }
//            if (Constants.isDoAllClicked && activity.manualIndex != Manual2SemiAutomaticTestsFragment.testListModelList.size()){
//                replaceFragment(R.id.container, ManualTestsOperation.launchScreens(Manual2SemiAutomaticTestsFragment.testListModelList.get(activity.manualIndex).getTest_id(), Manual2SemiAutomaticTestsFragment.testListModelList.get(Manual2SemiAutomaticTestsFragment.testListModelList.size() - 1).getTest_id()),null, false);
//                activity.manualIndex++;
//
//            }
//            else {
////                clearAllStack();
////                replaceFragment(R.id.container, ManualTestsOperation.launchScreens(-1,mainActivity.automatedTestListModels.get(mainActivity.automatedTestListModels.size()-1).getTest_id()), FragmentTag.GPS_FRAGMENT.name(), true);
//                popFragment(R.id.container);
//            }
        } catch (Exception e) {
            logException(e, "MicManualFragment_onNextPress()");
        }


    }

    /**
     * onBackPressed
     */
    public void onBackPress() {
        try {
            snackShow(micManual);
        } catch (Exception e) {
            logException(e, "MicManualFragment_onBackPress()");
        }

    }

    /**
     * show snack bar when user click on back button
     *


    /**
     * handle permission callback
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        try {
            switch (requestCode) {
                case REQUEST_CODE_ASK_PERMISSIONS_RECORD_AUDIO:
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // Permission Granted
                        checkRuntimePermisson(false);
                    } else {
                        // Permission Denied
                        utils.showAlert(ctx, (InterfaceAlertDissmiss) MicManualFragment.this, ctx.getResources().getString(R.string.txtPermissionMessageMic), ctx.getResources().getString(R.string.txtPermissionRequired),
                                ctx.getResources().getString(R.string.txtRetry), ctx.getResources().getString(R.string.Cancel), 121);
                    }
                    break;

                case REQUEST_CODE_ASK_PERMISSIONS_STORAGE:
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // Permission Granted
                        checkRuntimePermisson(false);
                    } else {
                        // Permission Denied
                        utils.showAlert(ctx, (InterfaceAlertDissmiss) MicManualFragment.this, ctx.getResources().getString(R.string.txtPermissionMessageMic), ctx.getResources().getString(R.string.txtPermissionRequired),
                                ctx.getResources().getString(R.string.txtRetry), ctx.getResources().getString(R.string.Cancel), 1234);
                    }
                    break;

                default:
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        } catch (Exception e) {
            logException(e, "MicManualFragment_onRequestPermissionsResult()");
        }

    }

    /**
     * handle alert click
     *
     * @param isCanceled
     * @param callbackID
     */
    @Override
    public void onButtonClick(boolean isCanceled, int callbackID) {
        try {
            if (!isCanceled) {
                checkRuntimePermisson(false);
            } else {
                mCallBack.onChangeText(utils.BUTTON_SKIP, true);
            /*ctx.startActivity(new Intent(getActivity(), MainActivity.class));
            getActivity().finish();*/
            }
        } catch (Exception e) {
            logException(e, "MicManualFragment_onButtonClick(boolean isCanceled, int callbackID)");
        }

    }

    /**
     * handle alert click
     *
     * @param isCanceled
     * @param callbackID
     */
    @Override
    public void onButtonClick(boolean isCanceled, int callbackID, int which) {

    }

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
//            logException(e, "MicManualFragment_updateResultToServer()");
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
            //  logException(exp, "MicManualFragment_logException()");
        }

    }

}

