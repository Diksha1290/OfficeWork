package com.officework;

import androidx.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;

import com.officework.activities.MainActivity;
import com.officework.fragments.MutitouchManualFragment;
import com.officework.interfaces.InterfaceButtonTextChange;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by Ashwani on 5/4/2017.
 */
@RunWith(AndroidJUnit4.class)
public class MultitouchTest extends ActivityInstrumentationTestCase2<MainActivity> implements InterfaceButtonTextChange {
    public MultitouchTest() {
        super(MainActivity.class);
    }

    @Test
    public void testMultiTouchScreen() {
        MutitouchManualFragment mutitouchManualFragment = new MutitouchManualFragment((InterfaceButtonTextChange) this);

        assertTrue(mutitouchManualFragment.testMultiTouch(2));
        assertFalse(mutitouchManualFragment.testMultiTouch(1));
    }

    @Override
    public void onChangeText(int text, boolean showButton) {

    }
}
