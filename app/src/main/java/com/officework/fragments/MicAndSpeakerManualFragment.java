package com.officework.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import androidx.core.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.officework.R;
import com.officework.activities.MainActivity;
import com.officework.base.BaseFragment;
import com.officework.calculators.AudioCalculator;
import com.officework.constants.Constants;
import com.officework.customViews.CustomButton;
import com.officework.customViews.IconView;
import com.officework.interfaces.InterfaceButtonTextChange;
import com.officework.interfaces.TimerDialogInterface;
import com.officework.models.AutomatedTestListModel;
import com.officework.testing_profiles.Controller.TestController;
import com.officework.testing_profiles.realmDatabase.operationManager.RealmOperations;
import com.officework.testing_profiles.utils.ConstantTestIDs;
import com.officework.utils.Utilities;
import com.officework.utils.socket.SocketConstants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

@SuppressLint("ValidFragment")
public class MicAndSpeakerManualFragment extends BaseFragment implements TimerDialogInterface {
    private static boolean isFirstTime = true;
    final public int REQUEST_CODE_ASK_PERMISSIONS_RECORD_AUDIO = 121;
    final public int REQUEST_CODE_ASK_PERMISSIONS_STORAGE = 1234;
    View view;
    Utilities utils;
    Context ctx;
    TestController testController;
    MainActivity mainActivity;
    // Handler nextButtonHandler;
    boolean isTestPerformed = false;
    InterfaceButtonTextChange mCallBack;
    LinearLayout micSpkrLayout;
    MediaPlayer mPlayer;
    CustomButton start;
    Boolean isNextFirst = true;
    Handler mediaHandler;
    Runnable runnable;
    AudioRecord recorder;
    boolean shouldPlay = true;
    boolean isFirstPassed, isSecondPasse, isThirdPassed, isFourthPassed;
    boolean isFirstPLayed, isSecondPlayed, isThirdPlayed, isFourthPlayed;
    IconView lowFrequencyimg, midFrequencyImg, highFrequencyImg, sHighFrequencyImg;
    String[] progressArray = {Constants.IN_PROGRESS, Constants.IN_PROGRESS, Constants.IN_PROGRESS
            , Constants.IN_PROGRESS};
    private AudioCalculator audioCalculator;
    private Handler handler;
    private int audioSource = MediaRecorder.AudioSource.DEFAULT;
    private int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    private int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    private int sampleRate = 44100;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private Thread thread;
    private Runnable runnableHz;
    private TextView low_frequency_textView,mid_frequency_textView,high_frequency_textView,shigh_frequency_textView;

    @SuppressLint("ValidFragment")
    public MicAndSpeakerManualFragment(InterfaceButtonTextChange interfaceButtonTextChange) {
        this.mCallBack = interfaceButtonTextChange;
    }

    public MicAndSpeakerManualFragment() {
    }

    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        if (view == null) {
            view = inflater.inflate(R.layout.mic_speaker_fragment, null);
            utils = Utilities.getInstance(getActivity());
            ctx = getActivity();
           // Crashlytics.getInstance().log(FragmentTag.SPEAKER_MIC_MANUAL_FRAGMENT.name());
            initViews();
            testController = TestController.getInstance(getActivity());


        }
        return view;
    }

    private void initViews() {


        CheckingPermission();
        mainActivity = (MainActivity) getActivity();
        mainActivity.onChangeText(R.string.textSkip, true);
        micSpkrLayout = (LinearLayout) view.findViewById(R.id.micspkrlayout);
        //nextButtonHandler = new Handler();
        audioCalculator = new AudioCalculator();
        handler = new Handler(Looper.getMainLooper());
        lowFrequencyimg = view.findViewById(R.id.lowfimg);
        midFrequencyImg = view.findViewById(R.id.midfimg);
        highFrequencyImg = view.findViewById(R.id.highfimg);
        sHighFrequencyImg = view.findViewById(R.id.shighfimg);


        setFrequenyData();
        start = view.findViewById(R.id.start);
        AudioManager am =
                (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        am.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                am.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                0);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer(getActivity(),true,ConstantTestIDs.SPEAKER_MIC,MicAndSpeakerManualFragment.this);
                start.setEnabled(false);
                start.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque30));

                intializeMediaPlayer(1);
                start();
//                if (checkWriteExternalMicPermission()){
//                    start.setEnabled(false);
//                    start.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque30));
//                    intializeMediaPlayer(1);
//                    start();
//                }else{
//                    checkRuntimePermisson(true);
//                }




            }
        });
        try {
           RealmOperations parseRemoteToLocal=new RealmOperations();
           AutomatedTestListModel automatedTestListModel=parseRemoteToLocal.fetchTestType(Constants.SPEAKER_MIC);
            JSONArray jsonArray = createSpeakerJsonArray();
            sendTestDataWithSubArray(SocketConstants.EVENT_TEST_START, Constants.SPEAKER_MIC,
                    Constants.IN_PROGRESS, Constants.MIC, automatedTestListModel.getTestDes(),
                    utils.getPreference(getActivity(), Constants.ANDROID_ID, ""),
                    MainActivity.qr_code_test, Constants.MANUAL2, jsonArray);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setFrequenyData() {

        low_frequency_textView =view.findViewById(R.id.textView5);
        mid_frequency_textView =view.findViewById(R.id.textView6);
        high_frequency_textView =view.findViewById(R.id.textView7);
        shigh_frequency_textView =view.findViewById(R.id.textView8);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            low_frequency_textView.setText(Html.fromHtml(getResources().getString(R.string.lowfrequency),Html.FROM_HTML_MODE_COMPACT));
        }
        else {
            low_frequency_textView.setText(Html.fromHtml(getResources().getString(R.string.lowfrequency)));
        }



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mid_frequency_textView.setText(Html.fromHtml(getResources().getString(R.string.midfrequency),Html.FROM_HTML_MODE_COMPACT));
        }
        else {
            mid_frequency_textView.setText(Html.fromHtml(getResources().getString(R.string.midfrequency)));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            high_frequency_textView.setText(Html.fromHtml(getResources().getString(R.string.highfrequency),Html.FROM_HTML_MODE_COMPACT));
        }
        else {
            high_frequency_textView.setText(Html.fromHtml(getResources().getString(R.string.highfrequency)));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            shigh_frequency_textView.setText(Html.fromHtml(getResources().getString(R.string.shighfrequency),Html.FROM_HTML_MODE_COMPACT));
        }
        else {
            shigh_frequency_textView.setText(Html.fromHtml(getResources().getString(R.string.shighfrequency)));
        }

    }

    private void CheckingPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_REQUEST_CODE);
            }
            else {

                timer(getActivity(),false,ConstantTestIDs.SPEAKER_MIC,MicAndSpeakerManualFragment.this);
            }
        }
        else {

            timer(getActivity(),false,ConstantTestIDs.SPEAKER_MIC,MicAndSpeakerManualFragment.this);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        try {
            switch (requestCode) {
                case PERMISSION_REQUEST_CODE:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if(grantResults[0]!= PackageManager.PERMISSION_GRANTED) {
                            if (shouldShowRequestPermissionRationale(permissions[0])) {
                                showmessage(getResources().getString(R.string.mic_permission),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                                                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_REQUEST_CODE);
                                                    sendTestData(SocketConstants.EVENT_TEST_END, ConstantTestIDs.SPEAKER_MIC, checkSingleHardware1("MMR_" + ConstantTestIDs.SPEAKER_MIC),
                                                            getString(R.string.txtSpeakerMic), "", Utilities.getInstance(getActivity()).getPreference(getActivity(), Constants.ANDROID_ID, ""), Constants.MANUAL2);

      onNextPress();
                                                 //   replaceFragment(R.id.container, new ManualTestFragment((InterfaceButtonTextChange) getActivity()), FragmentTag.MANUAL_TEST_FRAGMENT.name(), false);

                                                }
                                            }
                                        }, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                TestController testController = TestController.getInstance(getActivity());
                                                testController.saveSkipResponse(0, ConstantTestIDs.SPEAKER_MIC);

                                                utils.showToast(ctx, getResources().getString(R.string.txtManualFail));
                                                onNextPress();
                                            }
                                        });
                            }
                            else {
                                showmessage(getResources().getString(R.string.mic_permission), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        sendTestData(SocketConstants.EVENT_TEST_END, ConstantTestIDs.SPEAKER_MIC, checkSingleHardware1("MMR_" + ConstantTestIDs.SPEAKER_MIC),
                                                getString(R.string.txtSpeakerMic), "", Utilities.getInstance(getActivity()).getPreference(getActivity(), Constants.ANDROID_ID, ""), Constants.MANUAL2);

                                        onNextPress();
                                   //     replaceFragment(R.id.container, new ManualTestFragment((InterfaceButtonTextChange) getActivity()), FragmentTag.MANUAL_TEST_FRAGMENT.name(), false);
                                    }
                                }, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        TestController testController = TestController.getInstance(getActivity());
                                        testController.saveSkipResponse(0, ConstantTestIDs.SPEAKER_MIC);
                                        utils.showToast(ctx, getResources().getString(R.string.txtManualFail));
                                        onNextPress();
                                    }
                                });
                            }
                        }
                        else
                        {
//                        startCamera();
                        }



                    }
                    break;
            }
        } catch (Exception e) {
            // logException(e, "MicManualFragment_onRequestPermissionsResult()");
        }

    }
    private void showmessage(String message, DialogInterface.OnClickListener onClickListener,DialogInterface.OnClickListener onClickListener2) {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.txtAlertTitleGreatAlert)
                .setMessage(message).setCancelable(false)
                .setPositiveButton("OK",onClickListener)
                .setNegativeButton("Skip",onClickListener2)
                .create().
                show();
    }

    @Override
    public void onResume() {
        TitleBarFragment fragment = (TitleBarFragment) getFragment(R.id.headerContainer);
        if (fragment != null) {
//            fragment.setTitleBarVisibility(true);
//            fragment.setSyntextVisibilty(false);
//
//            fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string.txtSpeakerMic),
//                    false, false, 0);
            if (isTestPerformed) {
                mCallBack.onChangeText(Utilities.BUTTON_NEXT, true);

               onNextPress();

            } else {
                if(mCallBack!=null) {
                    mCallBack.onChangeText(Utilities.BUTTON_SKIP, true);
                }
                start.setEnabled(true);
                start.setBackgroundColor(getResources().getColor(R.color.brown));
            }
        }
        super.onResume();
    }

    @Override
    public void onPause() {

        super.onPause();


        if (mediaHandler != null) {
            mediaHandler.removeCallbacks(runnable);
        }
        stop();
        stopPlayer();
        lowFrequencyimg.setImageDrawable(true, getResources().getDrawable(R.drawable.ic_low_frequency), getActivity());
        midFrequencyImg.setImageDrawable(true, getResources().getDrawable(R.drawable.ic_mid_frequency), getActivity());
        highFrequencyImg.setImageDrawable(true, getResources().getDrawable(R.drawable.ic_high_frequency), getActivity());
        sHighFrequencyImg.setImageDrawable(true, getResources().getDrawable(R.drawable.ic_s_high_frequency), getActivity());
        resetplaylist();
    }
    void resetplaylist()
    {
        isFirstPLayed=false;
        isSecondPlayed=false;
        isThirdPlayed=false;
        isFourthPlayed=false;
        isFirstPassed=false;
        isFourthPassed=false;
        isThirdPassed=false;
        isSecondPasse=false;
    }

    private JSONArray createSpeakerJsonArray() {
        JSONArray array = new JSONArray();
        String[] arr_volume = getResources().getStringArray(R.array.Speaker);

        int[] speakersubArray = {Constants.SPEAKER_ONE, Constants.SPEAKER_TWO,
                Constants.SPEAKER_THREE, Constants.SPEAKER_FOUR};
        for (int index = 0; index < arr_volume.length; index++) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("TestName", arr_volume[index]);
                jsonObject.put("TestId", speakersubArray[index]);
                jsonObject.put("TestStatus", progressArray[index]);
                array.put(jsonObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return array;
    }

    @Override
    public void onDestroyView() {
        timer(getActivity(),true,ConstantTestIDs.SPEAKER_MIC,MicAndSpeakerManualFragment.this);
        if (mediaHandler != null) {
            mediaHandler.removeCallbacks(runnable);
        }
        stop();
        stopPlayer();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer(getActivity(),true,ConstantTestIDs.SPEAKER_MIC,MicAndSpeakerManualFragment.this);
    }
    @Override
    public void timerFail() {
        start.setEnabled(false);
        start.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque));
        testController.saveSkipResponse(0,ConstantTestIDs.MIC_ID);
        testController.saveSkipResponse(0,ConstantTestIDs.SPEAKER_ID);
        testController.saveSkipResponse(0,ConstantTestIDs.SPEAKER_MIC);
        isTestPerformed=true;

        lowFrequencyimg.setImageDrawable(false, getResources().getDrawable(R.drawable.ic_low_frequency), getActivity());
        midFrequencyImg.setImageDrawable(false, getResources().getDrawable(R.drawable.ic_mid_frequency), getActivity());
        highFrequencyImg.setImageDrawable(false, getResources().getDrawable(R.drawable.ic_high_frequency), getActivity());
        sHighFrequencyImg.setImageDrawable(false, getResources().getDrawable(R.drawable.ic_s_high_frequency), getActivity());

        utils.showToast(ctx, getResources().getString(R.string.txtManualFail));

        onNextPress();
    }
    private void stopPlayer() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.reset();
            mPlayer.release();
            mPlayer = null;
        }
    }

    public void onBackPress() {
        try {
            snackShow(micSpkrLayout);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public void start() {
        if (thread != null) return;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_AUDIO);

                    int minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig,
                            audioEncoding);
                    recorder = new AudioRecord(audioSource, sampleRate, channelConfig,
                            audioEncoding, minBufferSize);

                    if (recorder.getState() == AudioRecord.STATE_UNINITIALIZED) {
                        Thread.currentThread().interrupt();
                        return;
                    } else {
                    }
                    byte[] buffer = new byte[minBufferSize];
                    recorder.startRecording();

                    while (thread != null && !thread.isInterrupted() && recorder.read(buffer, 0,
                            minBufferSize) > 0) {
                        calculate(buffer);
                    }
                    recorder.stop();
                    recorder.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public void stop() {
        if (thread != null) {
            thread.interrupt();
            thread = null;
            if (recorder != null) {
                recorder.stop();
                recorder.release();
            }
            if (handler != null) {
                handler.removeCallbacksAndMessages(null);
            }
        }
    }

    public void calculate(byte[] buffer) {
        audioCalculator.setBytes(buffer);

        final double frequency = audioCalculator.getFrequency();


        final String hz = String.valueOf(frequency + " Hz");

        handler.post(runnableHz = new Runnable() {
            @Override
            public void run() {

                if (!isFirstPLayed) {
                    if (frequency > 490 && frequency < 501) {
                        lowFrequencyimg.setImageDrawable(getResources().getDrawable(R.drawable.ic_low_frequency), true, getActivity());
                        isFirstPassed = true;


                    }
                }
                if (!isSecondPlayed) {
                    if (frequency > 1990 && frequency < 2001) {
                        midFrequencyImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_mid_frequency), true, getActivity());
                        isSecondPasse = true;


                    }
                }
                if (!isThirdPlayed) {
                    if (frequency > 7800 && frequency < 8001) {

                        highFrequencyImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_high_frequency), true, getActivity());
                        isThirdPassed = true;
                    }
                }
                if (!isFourthPlayed) {
                    if (frequency > 11800 && frequency < 12001) {
                        sHighFrequencyImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_s_high_frequency), true, getActivity());
                        isFourthPassed = true;
                        stop();
                        mainActivity.onChangeText(R.string.textSkip, false);
                        if (isFirstPassed && isSecondPasse && isThirdPassed && isFourthPassed) {
                            testController.saveSkipResponse(1, ConstantTestIDs.SPEAKER_ID);
                            testController.saveSkipResponse(1, ConstantTestIDs.MIC_ID);
                            testController.saveSkipResponse(1, ConstantTestIDs.SPEAKER_MIC);
                            isTestPerformed = true;
                            utils.showToast(ctx, getResources().getString(R.string.txtManualPass));
                            progressArray[3] = Constants.COMPLETED;
                            JSONArray jsonArray = createSpeakerJsonArray();
                            sendTestDataWithSubArray(SocketConstants.EVENT_TEST_END,
                                    Constants.SPEAKER_MIC,
                                    Constants.COMPLETED, Constants.MIC, "",
                                    utils.getPreference(getActivity(),
                                            Constants.ANDROID_ID, ""),
                                    MainActivity.qr_code_test, Constants.MANUAL2, jsonArray);
                            if (isNextFirst) {
                                onNextPress();
                            }
                        }
                    }
                } else {
                    //  Log.e("hz",hz);
                }
            }
        });
    }


    void checkMicandSpeaker() {
        if (isFirstPassed) {
            progressArray[0] = Constants.COMPLETED;
            JSONArray jsonArray = createSpeakerJsonArray();
            sendTestDataWithSubArray(SocketConstants.EVENT_TEST_END,
                    Constants.SPEAKER_MIC,
                    Constants.FAILED, Constants.MIC, "",
                    utils.getPreference(getActivity(),
                            Constants.ANDROID_ID, ""),
                    MainActivity.qr_code_test, Constants.MANUAL2, jsonArray);
        } else if (isSecondPasse) {
            progressArray[1] = Constants.COMPLETED;
            JSONArray jsonArray = createSpeakerJsonArray();
            sendTestDataWithSubArray(SocketConstants.EVENT_TEST_END,
                    Constants.SPEAKER_MIC,
                    Constants.FAILED, Constants.MIC, "",
                    utils.getPreference(getActivity(),
                            Constants.ANDROID_ID, ""),
                    MainActivity.qr_code_test, Constants.MANUAL2, jsonArray);
        } else if (isThirdPassed) {
            progressArray[2] = Constants.COMPLETED;
            JSONArray jsonArray = createSpeakerJsonArray();
            sendTestDataWithSubArray(SocketConstants.EVENT_TEST_END,
                    Constants.SPEAKER_MIC,
                    Constants.FAILED, Constants.MIC, "",
                    utils.getPreference(getActivity(),
                            Constants.ANDROID_ID, ""),
                    MainActivity.qr_code_test, Constants.MANUAL2, jsonArray);
        } else if (isFirstPassed && isSecondPasse && isThirdPassed && isFourthPassed) {
            progressArray[3] = Constants.COMPLETED;
            JSONArray jsonArray = createSpeakerJsonArray();
            sendTestDataWithSubArray(SocketConstants.EVENT_TEST_END,
                    Constants.SPEAKER_MIC,
                    Constants.COMPLETED, Constants.MIC, "",
                    utils.getPreference(getActivity(),
                            Constants.ANDROID_ID, ""),
                    MainActivity.qr_code_test, Constants.MANUAL2, jsonArray);
        }

    }


    private void intializeMediaPlayer(final int value) {
        try {
            stopPlayer();

            if (shouldPlay) {
                if (value == 1) {
                    mPlayer = new MediaPlayer();
                    mPlayer.setDataSource(getActivity(),
                            Uri.parse("android.resource://" + getActivity().getPackageName() +
                                    "/" + R.raw.frequencyone));
                    try {
                        mPlayer.prepare();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            try {
                                mPlayer.start();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            isFirstPLayed = true;
                            if (!isFirstPassed)
                                lowFrequencyimg.setImageDrawable(getResources().getDrawable(R.drawable.ic_low_frequency), false, getActivity());
                            else {
                                lowFrequencyimg.setImageDrawable(getResources().getDrawable(R.drawable.ic_low_frequency), true, getActivity());
                            }
                            intializeMediaPlayer(2);
                        }
                    });

                } else {
                    stopPlayer();
                    mediaHandler = new Handler();
                    mediaHandler.postDelayed(runnable = new Runnable() {
                        @Override
                        public void run() {
                            try {
                                switch (value) {

                                    case 2:
                                        initializeSecondMediaPlayer();
                                        break;
                                    case 3:
                                        initializeThirdMediaPlayer();
                                        break;
                                    case 4:
                                        initializeFourthMediaPlayer();
                                        break;
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, 1000);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void initializeSecondMediaPlayer() {
        stopPlayer();
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(getActivity(),
                    Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.frequencytwo));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mPlayer.prepare();

        } catch (Exception e) {
            e.printStackTrace();
        }
        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                try {
                    if (isFirstPassed) {
                        progressArray[0] = Constants.COMPLETED;
                        JSONArray jsonArray = createSpeakerJsonArray();
                        sendTestDataWithSubArray(SocketConstants.EVENT_TEST_END,
                                Constants.SPEAKER_MIC,
                                Constants.FAILED, Constants.MIC, "",
                                utils.getPreference(getActivity(),
                                        Constants.ANDROID_ID, ""),
                                MainActivity.qr_code_test, Constants.MANUAL2, jsonArray);
                    }
                    mPlayer.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                isSecondPlayed = true;
                if (!isSecondPasse) {
                    midFrequencyImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_mid_frequency), false, getActivity());
                    progressArray[1] = Constants.FAILED;
                    JSONArray jsonArray = createSpeakerJsonArray();
                    sendTestDataWithSubArray(SocketConstants.EVENT_TEST_END,
                            Constants.SPEAKER_MIC,
                            Constants.FAILED, Constants.MIC, "",
                            utils.getPreference(getActivity(),
                                    Constants.ANDROID_ID, ""),
                            MainActivity.qr_code_test, Constants.MANUAL2, jsonArray);
                }
                intializeMediaPlayer(3);
            }
        });
    }

    public void initializeThirdMediaPlayer() {
        stopPlayer();
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(getActivity(),
                    Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.frequencythree));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mPlayer.prepare();

        } catch (Exception e) {
            e.printStackTrace();
        }
        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                try {
                    if (isSecondPasse) {
                        progressArray[1] = Constants.COMPLETED;
                        JSONArray jsonArray = createSpeakerJsonArray();
                        sendTestDataWithSubArray(SocketConstants.EVENT_TEST_END,
                                Constants.SPEAKER_MIC,
                                Constants.FAILED, Constants.MIC, "",
                                utils.getPreference(getActivity(),
                                        Constants.ANDROID_ID, ""),
                                MainActivity.qr_code_test, Constants.MANUAL2, jsonArray);
                    }
                    mPlayer.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                isThirdPlayed = true;
                if (!isThirdPassed) {
                    highFrequencyImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_high_frequency), false, getActivity());
                    progressArray[2] = Constants.FAILED;
                    JSONArray jsonArray = createSpeakerJsonArray();
                    sendTestDataWithSubArray(SocketConstants.EVENT_TEST_END,
                            Constants.SPEAKER_MIC,
                            Constants.FAILED, Constants.MIC, "",
                            utils.getPreference(getActivity(),
                                    Constants.ANDROID_ID, ""),
                            MainActivity.qr_code_test, Constants.MANUAL2, jsonArray);
                }
                intializeMediaPlayer(4);
            }
        });
    }

    public void initializeFourthMediaPlayer() {
        stopPlayer();
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(getActivity(),
                    Uri.parse("android.resource://" + getActivity().getPackageName() + "/" + R.raw.frequencyfour));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mPlayer.prepare();

        } catch (Exception e) {
            e.printStackTrace();
        }
        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                try {

                    if (isThirdPassed) {
                        progressArray[2] = Constants.COMPLETED;
                        JSONArray jsonArray = createSpeakerJsonArray();
                        sendTestDataWithSubArray(SocketConstants.EVENT_TEST_END,
                                Constants.SPEAKER_MIC,
                                Constants.FAILED, Constants.MIC, "",
                                utils.getPreference(getActivity(),
                                        Constants.ANDROID_ID, ""),
                                MainActivity.qr_code_test, Constants.MANUAL2, jsonArray);
                    }
                    mPlayer.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                isTestPerformed = true;
                isFourthPlayed = true;
                if (!isFourthPassed) {
                    sHighFrequencyImg.setImageDrawable(getResources().getDrawable(R.drawable.ic_s_high_frequency), false, getActivity());
                }
                if (!isFirstPassed || !isSecondPasse || !isThirdPassed || !isFourthPassed) {
                    testController.saveSkipResponse(0, ConstantTestIDs.SPEAKER_ID);
                    testController.saveSkipResponse(0, ConstantTestIDs.MIC_ID);


                    testController.saveSkipResponse(0, ConstantTestIDs.SPEAKER_MIC);
                    utils.showToast(ctx, getResources().getString(R.string.txtManualFail));
                    progressArray[3] = Constants.FAILED;
                    JSONArray jsonArray = createSpeakerJsonArray();
                    sendTestDataWithSubArray(SocketConstants.EVENT_TEST_END,
                            Constants.SPEAKER_MIC,
                            Constants.FAILED, Constants.MIC, "",
                            utils.getPreference(getActivity(),
                                    Constants.ANDROID_ID, ""),
                            MainActivity.qr_code_test, Constants.MANUAL2, jsonArray);
                    if (isNextFirst) {
                        onNextPress();
                    }
                }
            }
        });
    }

    public void onNextPress() {
        try {
            isTestPerformed = true;
            shouldPlay = false;
            isNextFirst = false;

            final MainActivity activity = (MainActivity) getActivity();
            mainActivity.onChangeText(R.string.textSkip, false);

            if (Constants.isSkipButton) {
                testController.saveSkipResponse(-1,ConstantTestIDs.SPEAKER_ID);
                testController.saveSkipResponse(-1,ConstantTestIDs.MIC_ID);
                testController.saveSkipResponse(0,  ConstantTestIDs.SPEAKER_MIC);
                start.setEnabled(false);
                start.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque30));
                isTestPerformed = true;
                stop();
                if (mediaHandler != null) {
                    mediaHandler.removeCallbacks(runnable);
                }
                try {
                    if (mPlayer != null) {
                        mPlayer.stop();
                        mPlayer.release();
                        mPlayer = null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                lowFrequencyimg.setImageDrawable(false, getResources().getDrawable(R.drawable.ic_low_frequency), getActivity());
                midFrequencyImg.setImageDrawable(false, getResources().getDrawable(R.drawable.ic_mid_frequency), getActivity());
                highFrequencyImg.setImageDrawable(false, getResources().getDrawable(R.drawable.ic_high_frequency), getActivity());
                sHighFrequencyImg.setImageDrawable(false, getResources().getDrawable(R.drawable.ic_s_high_frequency), getActivity());

                mainActivity.onChangeText(R.string.textSkip, false);
                utils.showToast(ctx, getResources().getString(R.string.txtManualFail));
                progressArray[0] = Constants.FAILED;
                progressArray[1] = Constants.FAILED;
                progressArray[2] = Constants.FAILED;
                progressArray[3] = Constants.FAILED;
                JSONArray jsonArray = createSpeakerJsonArray();
                sendTestDataWithSubArray(SocketConstants.EVENT_TEST_END,
                        Constants.SPEAKER_MIC,
                        Constants.FAILED, Constants.MIC, "",
                        utils.getPreference(getActivity(),
                                Constants.ANDROID_ID, ""),
                        MainActivity.qr_code_test, Constants.MANUAL2, jsonArray);
            }

            boolean semi = false;
            nextPress(activity, semi);

        } catch (Exception e) {

        }

    }
    public void backbutton() {

        utils.compare_UpdatePreferenceInt(ctx, Constants.MIC, Constants.TEST_NOTPERFORMED);
        JSONArray jsonArray = createSpeakerJsonArray();
        sendTestDataWithSubArray(SocketConstants.EVENT_TEST_END, Constants.SPEAKER_MIC,
                checkSingleHardware1("MMR_" + 71), Constants.MIC, "",
                utils.getPreference(getActivity(), Constants.ANDROID_ID, ""),
                MainActivity.qr_code_test, Constants.MANUAL2, jsonArray);

    }
}
