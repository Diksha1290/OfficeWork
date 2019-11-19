package com.officework.asyncTasks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import com.officework.constants.Constants;
import com.officework.constants.JsonTags;
import com.officework.interfaces.InterfaceTimerCallBack;
import com.officework.utils.AutomatedTestUtils;
import com.officework.utils.Utilities;

/**
 * Created by Ashwani on 5/30/2017.
 */

/**
 * Created by girish on 8/8/2016.
 */
public class CountDownTimerTask extends AsyncTask<Void, Void, Void> {

    Context ctx;
    InterfaceTimerCallBack listener;
    private long totalTimeCountInMilliseconds; // total count down time in

    int callbackID = 0;
    boolean status = false;
    AutomatedTestUtils automatedTestUtils;
    Activity mActivity;
    Utilities utilities;
    int isUserWantStopAsyncTask = 0;
    CountDownTimer countDownTimer;

    public CountDownTimerTask(Activity activity, Context ctx, InterfaceTimerCallBack listener, int callbackID) {
        this.ctx = ctx;
        this.listener = listener;

        this.callbackID = callbackID;
        automatedTestUtils = AutomatedTestUtils.getInstance(ctx);
        mActivity = activity;
        utilities = Utilities.getInstance(ctx);
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Void doInBackground(Void... params) {

        initalizeTimer();
        if (isCancelled()) {
            Log.d("CountDownTimer", "Stop");
        }

        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                countDownTimer = new CountDownTimer(utilities.getPreferenceLong(ctx, JsonTags.ttl.name(), 0L), 1000) {
                    // 500 means, onTick function will be called at every 500
                    // milliseconds

                    @Override
                    public void onTick(long leftTimeInMilliseconds) {

                        if (isCancelled()) {
                            countDownTimer.cancel();
                            Constants.isTimerFinished = true;
                            Constants.isTimerRunning = false;
                            Log.d("Time interval", "Stop");
                        } else {
                            Constants.isTimerFinished = false;
                            Constants.isTimerRunning = true;
                            Constants.secondsRemaining = leftTimeInMilliseconds / 1000;
                            Log.d("Timer", "running");
                            onProgressUpdate();
                        }
                        /*boolean isCancel = isCancelled();
                        if (isCancel) {
                            isUserWantStopAsyncTask = 1;
                        } else {
                            isUserWantStopAsyncTask = 0;
                        }
                        switch (isUserWantStopAsyncTask) {
                            case 1:
                                break;

                        }*/

                    }

                    @Override
                    public void onFinish() {
                        boolean isCancel = isCancelled();
                        /*if (isCancel) {
                            isUserWantStopAsyncTask = 1;
                        } else {
                            isUserWantStopAsyncTask = 0;
                        }
                        switch (isUserWantStopAsyncTask) {
                            case 1:
                                break;

                        }*/
                        if (isCancelled()) {
                            countDownTimer.cancel();
                            Constants.isTimerFinished = true;
                            Constants.isTimerRunning = false;
                        } else {
                            Constants.isTimerRunning = false;
                            Constants.isTimerFinished = true;
                            Intent pushNotification = new Intent(Constants.onTimerCallbackReceiver);
                            pushNotification.putExtra("time", Constants.secondsRemaining);
                            pushNotification.putExtra("isDone", true);
                            utilities.addPreference(ctx, JsonTags.refcode.name(), "");
                            LocalBroadcastManager.getInstance(ctx).sendBroadcast(pushNotification);
                            Log.d("Timer", "stop");
                        }

                    }

                }.start();
            }
        });


        return null;
    }

    private void initalizeTimer() {
        int time = 2;
      ///  Constants.totalTimeCountInMilliseconds = 60 * time * 1000;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);

        Intent pushNotification = new Intent(Constants.onTimerCallbackReceiver);
        pushNotification.putExtra("time", Constants.secondsRemaining);
        pushNotification.putExtra("isDone", false);
        LocalBroadcastManager.getInstance(ctx).sendBroadcast(pushNotification);

        /*Constants.interfaceTimerCallBack.onTimerCallBack(false, Constants.secondsRemaining);*/
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        /*Constants.interfaceTimerCallBack.onTimerFinish(status, Constants.totalTimeCountInMilliseconds, Constants.isTimerFinished);*/
    }
}