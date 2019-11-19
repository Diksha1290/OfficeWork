package com.officework.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.officework.fragments.GetQRCodeFragment;
import com.officework.utils.Utilities;

/**
 * @author Girish Sharma
 */
public class DataBroadcastReceiver extends BroadcastReceiver {
    boolean mIsUDIReceived;

    public DataBroadcastReceiver(boolean isUDIReceived) {
        mIsUDIReceived = isUDIReceived;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Utilities utils = Utilities.getInstance(context);
        if (mIsUDIReceived) {
            setResultData(GetQRCodeFragment.createJsonUDI(utils, context).toString());
        } else {
            setResultData(GetQRCodeFragment.createJson(utils, context).toString());
        }
    }
}