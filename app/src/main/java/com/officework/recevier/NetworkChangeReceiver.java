package com.officework.recevier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.widget.Toast;

import com.officework.R;


public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        final android.net.NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final android.net.NetworkInfo netInfo = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);



       if ((netInfo != null && netInfo.isConnected()) || (wifi !=null && wifi.isConnected())){
           new Handler().post(new Runnable() {
               @Override
               public void run() {
                   Toast.makeText(context,context.getResources().getString(R.string.net_working),Toast.LENGTH_SHORT).show();
               }
           });
       }
       else {
           new Handler().post(new Runnable() {
               @Override
               public void run() {
                   Toast.makeText(context,context.getResources().getString(R.string.please_check_internet_Connection),Toast.LENGTH_SHORT).show();
               }
           });
       }

    }
}