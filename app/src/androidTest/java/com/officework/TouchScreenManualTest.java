package com.officework;

import android.content.Context;
import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.view.MotionEvent;

import com.officework.activities.MainActivity;
import com.officework.fragments.TouchScreenCanvasManualFragment;
import com.officework.interfaces.InterfaceButtonTextChange;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by Ashwani on 5/5/2017.
 */
@RunWith(AndroidJUnit4.class)
public class TouchScreenManualTest extends ActivityInstrumentationTestCase2<MainActivity> implements InterfaceButtonTextChange {

    public TouchScreenManualTest() {
        super(MainActivity.class);
    }

    @Test
    public void touchScreenTest() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        TouchScreenCanvasManualFragment touchScreenCanvasManualFragment = new TouchScreenCanvasManualFragment((InterfaceButtonTextChange) this);
        assertTrue(touchScreenCanvasManualFragment.mockOnTouchEvent(MotionEvent.ACTION_DOWN,61));
        assertFalse(touchScreenCanvasManualFragment.mockOnTouchEvent(MotionEvent.ACTION_DOWN,60));
        assertFalse(touchScreenCanvasManualFragment.mockOnTouchEvent(MotionEvent.ACTION_UP,61));


       /* homeButtonReceiver.onReceive(appContext,intent);
        assertEquals(true, isTestPassed);*/
    }

    @Override
    public void onChangeText(int text, boolean showButton) {

    }
}
