package com.officework.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.officework.interfaces.WebServiceCallback;
//
///**
// * Created by Girish on 9/19/2016.
// */
//public class HomeButtonReceiver extends BroadcastReceiver {
//
//    final String SYSTEM_DIALOG_REASON_KEY = "reason";
//    final String SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions";
//    // final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
//    final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
//    WebServiceCallback mListener;
//    int mTestId;
//
//    public HomeButtonReceiver(WebServiceCallback listener,int testId) {
//        mListener = listener;
//        mTestId = testId;
//    }
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        String action = intent.getAction();
//        if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
//            String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
//            if (reason != null) {
//                if (mListener != null) {
//                    if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
//                        mListener.onServiceResponse(true, Intent.ACTION_SCREEN_ON,mTestId);
//                       // context.unregisterReceiver(this);
//
//                    }
//                }
//            }
//        }
//    }
//}


/**
 * Created by Girish on 9/19/2016.
 */
public class HomeButtonReceiver extends BroadcastReceiver {

    final String SYSTEM_DIALOG_REASON_KEY = "reason";
    final String SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions";
    // final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
    final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
    WebServiceCallback mListener;
    int mTestId;

    public HomeButtonReceiver(WebServiceCallback listener,int testId) {
        mListener = listener;
        mTestId = testId;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
            String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
            if (reason != null && reason.equals("recentapps")) {
                try{
                    context.unregisterReceiver(this);
                }
                catch (Exception e)
                {

                    e.printStackTrace();
                }
            }
            else if (reason != null && !reason.equals("recentapps")) {
                if (mListener != null) {
                    if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                        mListener.onServiceResponse(true, Intent.ACTION_SCREEN_ON,mTestId);
//                        context.unregisterReceiver(this);

                    }
                }
            }


        }
    }
}
