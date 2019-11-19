package com.officework.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.officework.interfaces.InterfaceBatteryCallback;

/**
 * Created by Girish on 8/31/2016.
 */
public class BatteryReceiver extends BroadcastReceiver {
    InterfaceBatteryCallback batteryCallback;

    public BatteryReceiver(InterfaceBatteryCallback callback) {
        batteryCallback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        batteryCallback.onBatteryCallBack(intent);
    }
}
