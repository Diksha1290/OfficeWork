package com.officework.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import com.officework.constants.AsyncConstant;
import com.officework.interfaces.InterfaceButtonTextChange;

/**
 * Created by Ashwani on 9/13/2016.
 */

@SuppressLint("ValidFragment")
public class ManualDataStable
 {

    InterfaceButtonTextChange mCallBack;

    public ManualDataStable(InterfaceButtonTextChange callback) {
        mCallBack = callback;
    }

    public int checkSingleHardware(String pref_Key, Context ctx, Utilities utils) {
        int testStatus;
        if (utils.getPreferenceInt(ctx, pref_Key, 0) == -1) {
            testStatus = AsyncConstant.TEST_IN_QUEUE;
        }
        else if (utils.getPreferenceInt(ctx, pref_Key, 0) == -2) {
            testStatus = AsyncConstant.TEST_NOT_EXIST;
        }else if (utils.getPreferenceInt(ctx, pref_Key, 0) == 0) {
            testStatus = AsyncConstant.TEST_FAILED;
        } else {
            testStatus = AsyncConstant.TEST_PASS;
        }
        return testStatus;
    }

    public int checkMutipleHardware(String pref_Key_1, String pref_Key_2, Context ctx, Utilities utils) {
        int testStatus;
        if (utils.getPreferenceInt(ctx, pref_Key_1, 0) == -1 && utils.getPreferenceInt(ctx, pref_Key_1, 0) == -1) {
            testStatus = AsyncConstant.TEST_IN_QUEUE;
        } else if (utils.getPreferenceInt(ctx, pref_Key_1, 0) == 0 && utils.getPreferenceInt(ctx, pref_Key_2, 0) == 0) {
            testStatus = AsyncConstant.TEST_FAILED;
        } else if (utils.getPreferenceInt(ctx, pref_Key_1, 0) == 0 || utils.getPreferenceInt(ctx, pref_Key_2, 0) == 0) {
            testStatus = AsyncConstant.TEST_IN_PROGRESS;
        } else {
            testStatus = AsyncConstant.TEST_PASS;
        }
        return testStatus;
    }
}
