package com.officework;

import androidx.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import com.officework.activities.MainActivity;
import com.officework.interfaces.InterfacePowerButtonCallBack;

import org.junit.runner.RunWith;

/**
 * Created by Ashwani on 4/26/2017.
 */
@RunWith(AndroidJUnit4.class)
public class PowerButtonTest extends ActivityInstrumentationTestCase2<MainActivity> implements InterfacePowerButtonCallBack {
    boolean isTestPerformed = false;

    public PowerButtonTest() {
        super(MainActivity.class);
    }



    @Override
    public void onPowerButtonCallBack(boolean isPower, String status) {
        isTestPerformed = true;
    }
}
