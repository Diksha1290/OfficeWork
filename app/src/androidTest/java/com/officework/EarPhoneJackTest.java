package com.officework;

import android.content.Context;
import android.content.Intent;
import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import com.officework.activities.MainActivity;
import com.officework.broadcastReceivers.EarPhoneJackReceiver;
import com.officework.interfaces.InterfaceBroadcastCallback;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by Ashwani on 4/13/2017.
 */


@RunWith(AndroidJUnit4.class)
public class EarPhoneJackTest extends ActivityInstrumentationTestCase2<MainActivity> implements InterfaceBroadcastCallback {

    public static boolean isTestPassed = false;
    private MainActivity testingActivity;
    Intent intent=null;

    public EarPhoneJackTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        isTestPassed = false;
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        testingActivity = getActivity();

        getInstrumentation().waitForIdleSync();
    }

    @Test
    public void earPhoneJackTest() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        EarPhoneJackReceiver earPhoneJackReceiver = new EarPhoneJackReceiver((InterfaceBroadcastCallback) this);
        intent = new Intent(Intent.ACTION_HEADSET_PLUG);
        intent.putExtra("state", 1);
        earPhoneJackReceiver.onReceive(appContext, intent);
        assertEquals(true, isTestPassed);
    }

    @Override
    public void onBroadcastCallBack(boolean isPower, String status) {
        this.isTestPassed = isPower;

    }
}