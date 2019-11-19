package com.officework.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.officework.interfaces.WebServiceCallback;

/**
 * Created by Ashwani on 8/25/2016.
 */
public class EarPhoneJackReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {


        if (Intent.ACTION_HEADSET_PLUG.equals(intent.getAction())) {
            int state = intent.getIntExtra("state", -1);
            switch (state) {
                case 0:
                    /*Log.d(TAG, "Headset is unplugged");*/
                    break;
                case 1:
                    mCallback.onServiceResponse(true, Intent.ACTION_HEADSET_PLUG,mTestid);
                   // context.unregisterReceiver(this);
                    /*Log.d(TAG, "Headset is plugged");*/
                    break;
                default:
                    /*Log.d("BroadCast", "I have no idea what the headset state is");*/
            }
        }
    }
    private  WebServiceCallback  mCallback;
    private  int  mTestid;

    public EarPhoneJackReceiver( WebServiceCallback callback,int testId) {
        mCallback = callback;
        mTestid = testId;

    }
}
