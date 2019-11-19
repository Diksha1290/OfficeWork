package com.officework;

import android.content.Context;
import android.content.Intent;
import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import com.officework.activities.MainActivity;
import com.officework.broadcastReceivers.NetworkChangeReceiver;
import com.officework.interfaces.InterfaceNetworkChange;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by Ashwani on 4/26/2017.
 */
@RunWith(AndroidJUnit4.class)
public class NetworkChangeReceiverTest extends ActivityInstrumentationTestCase2<MainActivity> implements InterfaceNetworkChange {
    boolean isTestPerformed = false;

    public NetworkChangeReceiverTest() {
        super(MainActivity.class);
    }
    @Test
    public void networkChangeListener() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        NetworkChangeReceiver networkChangeReceiver=new NetworkChangeReceiver(appContext,(InterfaceNetworkChange)this);
        Intent intent = new Intent("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver.onReceive(appContext, intent);
        assertEquals(true, isTestPerformed);
       /* homeButtonReceiver.onReceive(appContext,intent);
        assertEquals(true, isTestPassed);*/
    }

    @Override
    public void onNetworkChange(Context context, boolean isChanged) {
        isTestPerformed=isChanged;
    }
}
