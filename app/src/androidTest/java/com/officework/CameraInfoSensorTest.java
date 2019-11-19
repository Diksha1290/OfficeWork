package com.officework;

import android.content.Context;
import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import com.officework.activities.MainActivity;
import com.officework.fragments.CameraInfoFragment;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by Ashwani on 5/5/2017.
 */
@RunWith(AndroidJUnit4.class)
public class CameraInfoSensorTest extends ActivityInstrumentationTestCase2<MainActivity> {
    int Proximity = 1;
    int Light = 0;
    int accelerometer=2;

    public CameraInfoSensorTest() {
        super(MainActivity.class);
    }

    @Test
    public void cameraSensorTest() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        CameraInfoFragment cameraInfoFragment = new CameraInfoFragment();
        assertTrue(cameraInfoFragment.sensorInfoTest(Light,appContext));
        assertTrue(cameraInfoFragment.sensorInfoTest(Proximity,appContext));
        assertTrue(cameraInfoFragment.sensorInfoTest(accelerometer,appContext));


       /* homeButtonReceiver.onReceive(appContext,intent);
        assertEquals(true, isTestPassed);*/
    }
}
