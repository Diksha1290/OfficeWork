package com.officework;

import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import com.officework.activities.MainActivity;
import com.officework.broadcastReceivers.BatteryReceiver;
import com.officework.interfaces.InterfaceBatteryCallback;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by Ashwani on 4/27/2017.
 */
@RunWith(AndroidJUnit4.class)
public class BattreryReceiverTest extends ActivityInstrumentationTestCase2<MainActivity> implements InterfaceBatteryCallback {
    int isPlugged;

    public BattreryReceiverTest() {

        super(MainActivity.class);
    }

    @Test
    public void batteryReceiverTestCase() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        BatteryReceiver batteryReceiver = new BatteryReceiver((InterfaceBatteryCallback) this);
        Intent intent = new Intent(Intent.ACTION_BATTERY_CHANGED);
        intent.putExtra("status",BatteryManager.BATTERY_STATUS_FULL);
        batteryReceiver.onReceive(appContext, intent);
        assertEquals(5,isPlugged);
    }

    @Override
    public void onBatteryCallBack(Intent intent) {
        /*assertEquals(BatteryManager.BATTERY_STATUS_CHARGING, intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0));*/
        isPlugged = intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0);
    }
}
