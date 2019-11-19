package com.officework.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.officework.R;
import com.officework.activities.MainActivity;
import com.officework.fragments.ManualTestFragment;
import com.officework.interfaces.WebServiceCallback;

/**
 * Created by Ashwani on 11/30/2016.
 */
public class PowerButtonReceiver extends BroadcastReceiver {

    private WebServiceCallback mCallback;
    private int mTestId;
    Context mContext;

    public PowerButtonReceiver(WebServiceCallback callback, int testId, Context mContext) {
        mCallback = callback;
        mTestId = testId;
        this.mContext=mContext;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
       MainActivity mainActivity=(MainActivity) mContext;

        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            // DO WHATEVER YOU NEED TO DO HERE
          //  mCallback.onPowerButtonCallBack(false, Intent.ACTION_SCREEN_OFF);


        }
        if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            FragmentManager fm = mainActivity.getSupportFragmentManager();
            Fragment frag = fm.findFragmentById(R.id.container);
              if(frag instanceof ManualTestFragment){
                  mCallback.onServiceResponse(true, Intent.ACTION_SCREEN_ON,mTestId);
                  context.unregisterReceiver(this);
              }

        }
     if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
            // AND DO WHATEVER YOU NEED TO DO HERE
            mCallback.onServiceResponse(true, Intent.ACTION_SCREEN_ON,mTestId);
            context.unregisterReceiver(this);
        }



    }
}