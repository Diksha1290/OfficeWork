package com.officework;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import com.officework.activities.MainActivity;
import com.officework.broadcastReceivers.DataBroadcastReceiver;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by Ashwani on 4/27/2017.
 */
@RunWith(AndroidJUnit4.class)
public class DataBroadCastReceiverTest extends ActivityInstrumentationTestCase2<MainActivity> {

    boolean isTestPassed = false;

    public DataBroadCastReceiverTest() {
        super(MainActivity.class);
    }

    @Test
    public void homeButtonClickTest() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        DataBroadcastReceiver dataBroadcastReceiver = new DataBroadcastReceiver(true);
        Intent intent = new Intent("com.megammr.GET_Data");

        dataBroadcastReceiver.onReceive(appContext, intent);
        Bundle intent1=intent.getExtras();
        if(intent1!=null){
            isTestPassed=true;
        }

        assertEquals(true, isTestPassed);


    }

}
