package com.officework.utils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Vibrator;
import androidx.core.content.ContextCompat;
import android.telephony.TelephonyManager;

import com.officework.constants.AsyncConstant;
import com.officework.constants.Constants;
import com.officework.interfaces.WebServiceCallback;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by Girish on 8/10/2016.
 */
public class AutomatedTestUtils {
    private static AutomatedTestUtils instance;
    BluetoothAdapter bluetoothadapter;
    Intent bluetoothIntent;
    /**
     * Wifi
     */
    WifiManager wifiManager;
    Utilities utilities;
    boolean isBlueToothAlerady = false;
    static Context mContext;
    boolean isWifiAlreadyEnabled = false;
    int callbackID = 0;
    boolean status = false;


    public static AutomatedTestUtils getInstance(Context context) {
        mContext = context;
        if (instance == null) {
            instance = new AutomatedTestUtils();
        }
        return instance;
    }

    public void nullifyObjects(){
        instance = null;
    }

    public boolean checkSDCard(Context context) {
        /*boolean isSDAvailable = false;
        if (Environment.isExternalStorageRemovable()) {
            isSDAvailable = true;

        }*/

        if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.M) {
            return hasRealRemovableSdCard(context);
        } else {
            return isRemovebleSDCardMounted();
        }

    }

    public boolean hasRealRemovableSdCard(Context context) {
        try {
            if(ContextCompat.getExternalFilesDirs(context, null).length >= 2){
                return false;
            }


            //return ContextCompat.getExternalFilesDirs(context, null).length >= 2;
        } catch (Exception e) {
            return true;
        }

return true;
    }

    public boolean isRemovebleSDCardMounted() {
        boolean flag = true;
        try {
            File file = new File("/sys/class/block/");
            File[] files = file.listFiles(new MmcblkFilter("mmcblk\\d$"));

            for (File mmcfile : files) {
                File scrfile = new File(mmcfile, "device/scr");
                if (scrfile.exists()) {
                    flag = false;
                    break;
                }
            }
        } catch (Exception e) {
            logException(e, "AutomatedTestUtils_isRemovebleSDCardMounted())");
        }

        return flag;
    }

    static class MmcblkFilter implements FilenameFilter {
        private String pattern;

        public MmcblkFilter(String pattern) {
            this.pattern = pattern;

        }

        @Override
        public boolean accept(File dir, String filename) {

            try {
                if (filename.matches(pattern)) {
                    return true;
                }
            } catch (Exception e) {
                AutomatedTestUtils automatedTestUtils = new AutomatedTestUtils();
                automatedTestUtils.logException(e, "AutomatedTestUtils_accept)");
            }

            return false;
        }

    }


    public boolean checkSimCard(Activity activity) {
        boolean isSimAvailable = false;
        try {
            TelephonyManager tm = (TelephonyManager) activity.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null) {
                //gets the current TelephonyManager
                if (tm.getSimState() == TelephonyManager.SIM_STATE_ABSENT) {
                    isSimAvailable = true;
                } else if (tm.getSimState() == TelephonyManager.SIM_STATE_READY) {
                    isSimAvailable = false;
                } else if (tm.getSimState() == TelephonyManager.SIM_STATE_UNKNOWN) {
                    isSimAvailable = true;
                }
            }

        } catch (Exception e) {
            logException(e, "AutomatedTestUtils_checkSimCard()");
        }

        return isSimAvailable;
    }

   /* public boolean checkWiFi(Activity activity) {
        boolean checkNetwork = false;
        boolean isWifiPermission = false;
        try {
            utilities = Utilities.getInstance(activity);
            wifiManager = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (!wifiManager.isWifiEnabled()) {
                isWifiAlreadyEnabled = false;
                wifiManager.setWifiEnabled(true);
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                isWifiAlreadyEnabled = true;
            }

            while (!checkNetwork) {
                switch (wifiManager.getWifiState()) {
                    case WifiManager.WIFI_STATE_ENABLING:
                        checkNetwork = false;
                        isWifiPermission = false;
                        break;

                    case WifiManager.WIFI_STATE_ENABLED:
                        checkNetwork = true;
                        isWifiPermission = true;
                        break;

                    case WifiManager.WIFI_STATE_DISABLED:
                        checkNetwork = true;
                        isWifiPermission = false;
                        break;
                }
            }

            *//*checkNetwork = utilities.isInternetWorking(activity);*//*
            if (checkNetwork && isWifiPermission) {
                checkNetwork = true;
            } else {
                checkNetwork = false;
            }
            if (!isWifiAlreadyEnabled) {
                wifiManager.setWifiEnabled(false);
            }

        } catch (Exception e) {
            logException(e, "AutomatedTestUtils_checkWiFi()");
        }

        return checkNetwork;
    }*/


    public boolean checkWiFi(Activity activity) {
        boolean checkNetwork = false;
        try {
            utilities = Utilities.getInstance(activity);
            wifiManager = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (!wifiManager.isWifiEnabled()){
                checkNetwork= false;
            }else{
                checkNetwork= true;
            }

        } catch (Exception e) {
            logException(e, "AutomatedTestUtils_checkWiFi()");
        }

        return checkNetwork;
    }

    public boolean checkBluetooth(Activity activity, WebServiceCallback listener) {
        try {
            bluetoothadapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothadapter.isEnabled()) {
                isBlueToothAlerady = true;
            }else {
                isBlueToothAlerady = false;
            }


          //  return  BluetoothEnable(activity,listener);
        } catch (Exception e) {
            logException(e, "AutomatedTestUtils_checkBluetooth()");
        }

        return isBlueToothAlerady;
    }

    public boolean BluetoothEnable(Activity activity, final WebServiceCallback listener) {
        try {
            if (bluetoothadapter.isEnabled()) {
                isBlueToothAlerady = true;
               // listener.onServiceResponse(true, "", callbackID);
                return  isBlueToothAlerady;
            }
//            if(!((Activity) activity).isFinishing())
//            {
//                //show dialog
//            }
//
//            if(activity.isFinishing()){
//                listener.onServiceResponse(false, "", callbackID);
//
//            }else {
//                final AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
//                alert.setTitle(R.string.txtBluetooth);
//                alert.setMessage(R.string.txtBluetoothPermission);
//                alert.setCancelable(true);
//                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        bluetoothadapter.enable();
//
//                        listener.onServiceResponse(true, "", callbackID);
//
//                    }
//                });
//                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        listener.onServiceResponse(false, "", callbackID);
//                    }
//                });
//                alert.show();
//            }


              bluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
             activity.startActivityForResult(bluetoothIntent, Constants.BLUETOOTH_ACTIVITY_RESULT);

        } catch (Exception e) {
            e.printStackTrace();
        }


        return false;
    }

    public boolean BluetoothDisable() {
        try {
            if (isBlueToothAlerady) {
                isBlueToothAlerady = false;

            } else {
                bluetoothadapter.disable();
            }

        } catch (Exception e) {
            logException(e, "AutomatedTestUtils_BluetoothDisable()");
        }

        return false;
    }

    public boolean checkVibrator(Context ctx, WebServiceCallback listener) {
        boolean status = false;
        try {
            Vibrator vibrator = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator.hasVibrator()) {
                for (int i = 0; i < 3; i++) { //repeat the pattern 3 times
                    startVibrate(vibrator);
                    try {
                        Thread.sleep(1000); //the time, the complete pattern needs
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if(i ==2)
                        listener.onServiceResponse(true,"vibration", AsyncConstant.VIBRATION_TEST);


                }
                status = true;
            } else {
                listener.onServiceResponse(false,"vibration", AsyncConstant.VIBRATION_TEST);
                status = false;
            }
            return status;
        } catch (Exception e) {
            logException(e, "AutomatedTestUtils_checkVibrator()");
            return status;

        }

    }

    public void startVibrate(Vibrator vibrator) {
        try {
            long pattern[] = {0, 100, 200, 300, 400};
            vibrator.vibrate(pattern, -1);
        } catch (Exception e) {
            logException(e, "AutomatedTestUtils_startVibrate()");
        }

    }

    public void stopVibrate(Vibrator vibrator) {
        try {
            vibrator.cancel();
        } catch (Exception e) {
            logException(e, "AutomatedTestUtils_stopVibrate()");
        }

    }

    public void logException(Exception e, String methodName) {
        try {
            Utilities utilities = Utilities.getInstance(mContext);
            ExceptionLogUtils exceptionLogUtils = new ExceptionLogUtils(utilities, mContext, null);
            exceptionLogUtils.addExceptionLog(utilities, mContext, null, null, null, e, methodName);
        } catch (Exception exp) {
            // logException(exp, "AutomatedTestUtils_logException()");
        }

    }
}
