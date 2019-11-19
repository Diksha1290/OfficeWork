package com.officework;

import android.test.ActivityInstrumentationTestCase2;
import android.view.KeyEvent;
import android.widget.TextView;

import com.officework.activities.MainActivity;
import com.officework.fragments.VolumeManualFragment;
import com.officework.interfaces.InterfaceButtonTextChange;

import org.junit.Test;

/**
 * Created by Ashwani on 4/12/2017.
 */

public class FragmentTest extends ActivityInstrumentationTestCase2<MainActivity> implements InterfaceButtonTextChange {

    private MainActivity testingActivity;
    private VolumeManualFragment testFragment;

    public FragmentTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        // Starts the activity under test using
        // the default Intent with:
        // action = {@link Intent#ACTION_MAIN}
        // flags = {@link Intent#FLAG_ACTIVITY_NEW_TASK}
        // All other fields are null or empty.
        testingActivity = getActivity();

        testFragment = new VolumeManualFragment((InterfaceButtonTextChange) this);
        testingActivity.getSupportFragmentManager().beginTransaction().add(R.id.container, testFragment, null).commit();
        /**
         * Synchronously wait for the application to be idle.  Can not be called
         * from the main application thread -- use {@link #start} to execute
         * instrumentation in its own thread.
         *
         * Without waitForIdleSync(); our test would have nulls in fragment references.
         */
        getInstrumentation().waitForIdleSync();
    }

    @Test
    public void testGameFragmentsTextViews() {

        String empty = "";
        TextView textView = (TextView) testFragment.getView().findViewById(R.id.tvVolumeMessge);
        assertTrue("Empty stuff", (textView.getText().equals(testingActivity.getResources().getString(R.string.txtTitleVolume_Btn))));
    }

    @Test
    public void testVolumeKeyUpTest() {
        KeyEvent event = new KeyEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_VOLUME_UP);
        assertEquals(true, testFragment.dispatchKeyEventTest(event));
    }
    @Test
    public void testVolumeKeyDownTest() {
        KeyEvent event = new KeyEvent(KeyEvent.ACTION_UP,KeyEvent.KEYCODE_VOLUME_DOWN);
        assertEquals(true, testFragment.dispatchKeyEventTest(event));
    }

    @Override
    public void onChangeText(int text, boolean showButton) {

    }
}
