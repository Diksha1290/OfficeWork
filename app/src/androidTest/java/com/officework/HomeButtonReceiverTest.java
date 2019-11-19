package com.officework;

import androidx.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import com.officework.activities.MainActivity;
import com.officework.interfaces.OnHomePressedListener;

import org.junit.runner.RunWith;

/**
 * Created by Ashwani on 4/27/2017.
 */
@RunWith(AndroidJUnit4.class)
public class HomeButtonReceiverTest extends ActivityInstrumentationTestCase2<MainActivity> implements OnHomePressedListener {

    boolean isTestPassed = false;

    public HomeButtonReceiverTest() {
        super(MainActivity.class);
    }


    @Override
    public void onHomePressed() {
        isTestPassed = true;

    }

    @Override
    public void onHomeLongPressed() {
        isTestPassed = true;
    }
}
