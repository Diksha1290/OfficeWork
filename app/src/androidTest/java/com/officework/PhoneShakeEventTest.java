package com.officework;

import android.content.Context;
import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import com.officework.activities.MainActivity;
import com.officework.listeners.ShakeEventListener;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by Ashwani on 5/4/2017.
 */
@RunWith(AndroidJUnit4.class)
public class PhoneShakeEventTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public boolean isTestPerformed = false;

    public PhoneShakeEventTest() {
        super(MainActivity.class);
    }

    @Test
    public void phoneShakeEventTest() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        ShakeEventListener shakeEventListener = new ShakeEventListener();

        shakeEventListener.setOnShakeListener(new ShakeEventListener.OnShakeListener() {
            @Override
            public void onShake() {
                isTestPerformed = true;
            }
        });
        shakeEventListener.mockOnSensorChaged(new int[]{5,5,5});
        assertEquals(true, isTestPerformed);
        /* homeButtonReceiver.onReceive(appContext,intent);
        assertEquals(true, isTestPassed);*/
    }
}
