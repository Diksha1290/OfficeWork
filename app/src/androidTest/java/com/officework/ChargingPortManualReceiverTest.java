package com.officework;

import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import com.officework.activities.MainActivity;
import com.officework.broadcastReceivers.ChargingPortTestReceiver;
import com.officework.interfaces.InterfaceChargingPortCallBack;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by Ashwani on 5/4/2017.
 */
@RunWith(AndroidJUnit4.class)
public class ChargingPortManualReceiverTest extends ActivityInstrumentationTestCase2<MainActivity> implements InterfaceChargingPortCallBack{
    public boolean isTestPerformed = false;
    public ChargingPortManualReceiverTest(){
        super(MainActivity.class);
    }

    @Test
    public void phoneShakeEventTest() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        ChargingPortTestReceiver chargingPortTestReceiver=new ChargingPortTestReceiver((InterfaceChargingPortCallBack)this);


        Intent intent = new Intent(Intent.ACTION_BATTERY_CHANGED);
        intent.putExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_CHARGING);
        chargingPortTestReceiver.onReceive(appContext,intent);
        assertEquals(true,isTestPerformed);

        intent.putExtra(BatteryManager.EXTRA_STATUS,BatteryManager.BATTERY_STATUS_NOT_CHARGING);
        chargingPortTestReceiver.onReceive(appContext,intent);
        assertEquals(false,isTestPerformed);
        /* homeButtonReceiver.onReceive(appContext,intent);
        assertEquals(true, isTestPassed);*/
    }

    @Override
    public void onChargingEventListener(boolean status) {
        isTestPerformed=status;
    }
}
