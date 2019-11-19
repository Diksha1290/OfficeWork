package com.officework.fragments;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.officework.R;
import com.officework.base.BaseFragment;
import com.officework.interfaces.InterfaceVibrateCompletion;
import com.officework.utils.Utilities;

/**
 * Created by Girish on 8/9/2016.
 * Screen to detect the device vibrator is working fine or not
 */
public class VibrateAutomatedFragment extends BaseFragment implements InterfaceVibrateCompletion {
    View view;
    Utilities utils;
    Context ctx;
    public Vibrator vibrator;
    long pattern[] = {0, 100, 200, 300, 400};
    InterfaceVibrateCompletion vibrateCompletion;
    private static final int OnSuccess = 0;
    private static final int OnFailure = 1;

    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_automated_vibrate, null);
            utils = Utilities.getInstance(getActivity());
            ctx = getActivity();
            vibrateCompletion = this;
         //   Crashlytics.getInstance().log(FragmentTag.VIBRATE_AUTOMATED_TEST.name());
            initViews();
        }
        return view;
    }

    public VibrateAutomatedFragment() {
    }

    private void initViews() {
        intializeVibrator();
        new Thread() {
            @Override
            public void run() {
                Message msg = Message.obtain();
                if (vibrator.hasVibrator()) {
                    for (int i = 0; i < 3; i++) { //repeat the pattern 3 times
                        startVibrate();
                        try {
                            Thread.sleep(1000); //the time, the complete pattern needs
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    msg.what = OnSuccess;
                    handler.sendMessage(msg);
                    /*vibrateCompletion.onVibrateCompleted(true);*/
                } else {
                    msg.what = OnSuccess;
                    handler.sendMessage(msg);
                    /*vibrateCompletion.onVibrateCompleted(false);*/
                }
            }
        }.start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case OnSuccess:
                    utils.showToast(ctx, getResources().getString(R.string.txtVibrateSuccess));
                    break;
                case OnFailure:
                    // do final steps;
                    utils.showToast(ctx, getResources().getString(R.string.txtVibrateFailed));
                    break;
            }
        }
    };

    public void intializeVibrator() {
        vibrator = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void startVibrate() {
        vibrator.vibrate(pattern, -1);
    }

    public void stopVibrate() {
        vibrator.cancel();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopVibrate();
    }

    @Override
    public void onVibrateCompleted(boolean isSuccess) {
        if (isSuccess) {
            utils.showToast(ctx, getResources().getString(R.string.txtVibrateSuccess));
        } else {
            utils.showToast(ctx, getResources().getString(R.string.txtVibrateFailed));
        }
    }

    /**
     * Here change the header text
     ***/
    @Override
    public void onResume() {
        TitleBarFragment fragment = (TitleBarFragment) getFragment(R.id.headerContainer);
        if (fragment != null) {
            fragment.setTitleBarVisibility(true);
            fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string.txtAutomated), false, false, 0);
        }
        super.onResume();
    }
}
