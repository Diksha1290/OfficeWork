package com.officework;

import android.app.Activity;

import com.officework.constants.JsonTags;
import com.officework.utils.Utilities;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;

/**
 * Created by Ashwani on 4/11/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class comparePreferencesResult {


    //TODO: Use Mockito to initialize UserPreferences
    public Utilities tUserPreferences = Mockito.mock(Utilities.class);
    private Activity tActivity;

    @Before
    public void setUp() {
        //TODO: Use Mockito to declare the return value of getSharedPreferences()
        Mockito.when(tUserPreferences.compare_UpdatePreferenceString(tActivity,
                JsonTags.MMR_20.name(),"")).thenReturn(true);
        Mockito.when(tUserPreferences.compare_UpdatePreferenceInt(tActivity,
                JsonTags.MMR_21.name(),-1)).thenReturn(false);

    }

    @Test
    public void sharedPreferencesTest_ReturnsTrue() {
        //TODO: Test
        org.junit.Assert.assertThat(tUserPreferences.compare_UpdatePreferenceString(tActivity,
                JsonTags.MMR_20.name(),""), is(true));
        org.junit.Assert.assertThat(tUserPreferences.compare_UpdatePreferenceInt(tActivity,
                JsonTags.MMR_20.name(),1), is(true));
    }


}
