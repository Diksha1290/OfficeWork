package com.officework.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

public class TaskRemovedService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Toast.makeText(getApplicationContext(), "App Killed By System", Toast.LENGTH_LONG).show();
        System.out.println("onTaskRemoved called");
        Log.i("appstate","appkilled");
        super.onTaskRemoved(rootIntent);
    }
}
