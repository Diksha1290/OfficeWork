package com.officework;

import android.content.Context;
import android.hardware.Sensor;
import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import com.officework.activities.MainActivity;
import com.officework.interfaces.InterfaceProximitySensorChanged;
import com.officework.listeners.ProximitySensorEventListener;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by Ashwani on 5/4/2017.
 */
@RunWith(AndroidJUnit4.class)
public class ProximitySensorEventTest extends ActivityInstrumentationTestCase2<MainActivity> implements InterfaceProximitySensorChanged {
    public boolean isTestPerformed = false;
    String string = "";

    public ProximitySensorEventTest() {
        super(MainActivity.class);
    }


    @Test
    public void proximitySensorTestTest() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        ProximitySensorEventListener proximitySensorEventListener = new ProximitySensorEventListener((InterfaceProximitySensorChanged) this);
        proximitySensorEventListener.mockOnSensorChanged(Sensor.TYPE_PROXIMITY, new int[]{0, 1});
        assertEquals(true, isTestPerformed);
        assertEquals("NEAR",string);
        proximitySensorEventListener.mockOnSensorChanged(Sensor.TYPE_PROXIMITY, new int[]{1, 1});
        assertEquals(true, isTestPerformed);
        assertEquals("FAR",string);
       /* homeButtonReceiver.onReceive(appContext,intent);
        assertEquals(true, isTestPassed);*/
    }

    @Override
    public void onProximityDetectedNear(boolean result, String str) {
        isTestPerformed = result;
        string = str;
    }

    @Override
    public void onProximirtDetectFar(boolean result, String str) {
        isTestPerformed = result;
        string = str;
    }
}
