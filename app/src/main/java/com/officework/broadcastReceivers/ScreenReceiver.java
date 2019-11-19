package com.officework.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.officework.interfaces.InterfaceBroadcastCallback;

/**
 * Created by Ashwani on 8/19/2016.
 */
public class ScreenReceiver extends BroadcastReceiver {

    private InterfaceBroadcastCallback mCallback;

    public ScreenReceiver(InterfaceBroadcastCallback callback) {
        mCallback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            // DO WHATEVER YOU NEED TO DO HERE
            mCallback.onBroadcastCallBack(true, Intent.ACTION_SCREEN_OFF);

        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            // AND DO WHATEVER YOU NEED TO DO HERE
            mCallback.onBroadcastCallBack(true, Intent.ACTION_SCREEN_OFF);
        }
    }
}