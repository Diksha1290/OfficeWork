package com.officework.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;


import com.officework.constants.Constants;
import com.officework.utils.Utilities;
import com.officework.utils.WebserviceUrls;
import com.officework.utils.socket.SocketConstants;
import com.officework.utils.socket.SocketHelper;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.IO;

public class OnClearFromRecentService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("ClearFromRecentService", "Service Started");
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ClearFromRecentService", "Service Destroyed");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        try {
            Log.e("ClearFromRecentService", "END");
            //Code here
            String androidId = Utilities.getInstance(this).getPreference(this, Constants.ANDROID_ID, "");
            String qrCodeiD = Utilities.getInstance(this).getPreference(this, Constants.QRCODEID, "");
            IO.Options options = new IO.Options();

            SocketHelper socketHelper = new SocketHelper.Builder(WebserviceUrls.BaseUrl + "?" + Constants.REQUEST_UNIQUE_ID + "=" + androidId, options)
                    .addEvent(SocketConstants.EVENT_CONNECTED)
                    .addListener(null)
                    .build();

            if (socketHelper != null && socketHelper.socket.connected()) {
                try {

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(Constants.UNIQUE_ID, androidId);
                    jsonObject.put(Constants.QRCODEID, qrCodeiD);
                    socketHelper.emitData(SocketConstants.EVENT_DEVICE_DISCONNECT, jsonObject);

                    socketHelper.destroy();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            stopSelf();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}