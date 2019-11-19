package com.officework.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.officework.interfaces.InterfaceNetworkChange;
import com.officework.utils.NetworkChangeUtils;

/**
 * Created by Ashwani on 3/27/2017.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {
    Context mContext;
    InterfaceNetworkChange interfaceNetworkChange;

    public NetworkChangeReceiver(Context context, InterfaceNetworkChange interfaceNetworkChange) {
        this.interfaceNetworkChange = interfaceNetworkChange;

    }


    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;

        boolean status = NetworkChangeUtils.getConnectivityStatusString(context);
        Log.e("Receiver ", "" + status);
        if (status) {
            interfaceNetworkChange.onNetworkChange(context, true);
            Log.e("Receiver ", "Connction");// your code when internet lost
        } else {
            interfaceNetworkChange.onNetworkChange(context, false);
            Log.e("Receiver ", "Disconnected");//your code when internet connection come back
        }

    }
}