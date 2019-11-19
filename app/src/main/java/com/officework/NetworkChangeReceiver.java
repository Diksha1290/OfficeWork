package com.officework;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;


public class NetworkChangeReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(final Context context, final Intent intent) {
        final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        final android.net.NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final android.net.NetworkInfo mobile = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifi.isAvailable() || mobile.isAvailable()) {
            // Do something
try {
    new Handler().post(new Runnable() {
        @Override
        public void run() {
            Toast.makeText(context,"gfgfhgsajfk",Toast.LENGTH_SHORT).show();
        }
    });
}catch (Exception e){
    e.printStackTrace();
}


            Log.d("Network Available ", "Flag No 1");
        }
    }
}