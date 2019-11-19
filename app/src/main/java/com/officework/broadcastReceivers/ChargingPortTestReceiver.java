package com.officework.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Build;
import android.util.Log;

import com.officework.interfaces.InterfaceChargingPortCallBack;

/**
 * Created by Ashwani on 5/4/2017.
 */

public class ChargingPortTestReceiver extends BroadcastReceiver {

    InterfaceChargingPortCallBack minterfaceChargingPortCallBack;

    public ChargingPortTestReceiver(InterfaceChargingPortCallBack interfaceChargingPortCallBack) {
        minterfaceChargingPortCallBack = interfaceChargingPortCallBack;
    }

//    @Override
//    public void onReceive(Context context, Intent intent) {
//
//        if (intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0) == BatteryManager.BATTERY_STATUS_CHARGING) {
//            minterfaceChargingPortCallBack.onChargingEventListener(true);
//
//        } else if (intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0) == BatteryManager.BATTERY_STATUS_NOT_CHARGING) {
//            minterfaceChargingPortCallBack.onChargingEventListener(false);
//        }
//
//        Log.d("Change Charging Port", String.valueOf(intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0)));
//
//    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            BatteryManager batteryManager = (BatteryManager) context.getSystemService(Context.BATTERY_SERVICE);
            if(batteryManager.isCharging()){
                minterfaceChargingPortCallBack.onChargingEventListener(true);
            }
//            if(!batteryManager.isCharging()){
//                Log.i("CHARGING STATUS",false +"");
//                minterfaceChargingPortCallBack.onChargingEventListener(false);
//              //  context.sendBroadcast(new Intent("FINISH"));
//
//                // Toast.makeText(context, "The device is not charging", Toast.LENGTH_SHORT).show();
//
//            }
        }

        // else {
        if (intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0) == BatteryManager.BATTERY_STATUS_CHARGING) {
            minterfaceChargingPortCallBack.onChargingEventListener(true);

        } else if (intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0) == BatteryManager.BATTERY_STATUS_NOT_CHARGING) {
            minterfaceChargingPortCallBack.onChargingEventListener(false);
        }

        if(intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)==100){
             if(intent.getIntExtra(BatteryManager.EXTRA_PLUGGED,-1) ==BatteryManager.BATTERY_PLUGGED_AC|| intent.getIntExtra(BatteryManager.EXTRA_PLUGGED,-1) ==BatteryManager.BATTERY_PLUGGED_USB){
            minterfaceChargingPortCallBack.onChargingEventListener(true);
        }
        }
        Log.d("Change Charging Port", String.valueOf(intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0)));
        // }


    }
}
