package com.officework.utils;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;


import com.officework.constants.Constants;
import com.officework.constants.JsonTags;

import java.io.File;
import java.lang.reflect.Field;

/**
 * Created by Diksha on 8/27/2018.
 */

public class DeviceInformation {

    /**
     * This method return the Mobile Build
     *
     * @return String
     */

    public String getMobileBuild() {
        try {
            String myDeviceBuid = Build.FINGERPRINT;
            String forwardSlash = "/";
            String osReleaseVersion = Build.VERSION.RELEASE + forwardSlash;
            myDeviceBuid = myDeviceBuid.substring(myDeviceBuid.indexOf(osReleaseVersion));  //"5.1.1/LMY48Y/2364368:user/release-keys”
            myDeviceBuid = myDeviceBuid.replace(osReleaseVersion, "");  //"LMY48Y/2364368:user/release-keys”
            myDeviceBuid = myDeviceBuid.substring(0, myDeviceBuid.indexOf(forwardSlash));
            return myDeviceBuid;//"LMY48Y"
        } catch (Exception e) {

            return null;
        }


    }
    /**
     *
     */
    /**
     * This will fetch device basic information and set in the controls.
     * OS Section
     *
     * @param permission
     */
    public String getIMEI(boolean permission, Context context) {
        try {

            String imei_no = "";
        /*telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);*/
            if (permission) {
                if ((TelephonyInfo.getInstance((Activity)context).getImsiSIM1()) != null) {
                    imei_no= TelephonyInfo.getInstance((Activity)context).getImsiSIM1();
                }

                if ((TelephonyInfo.getInstance((Activity)context).getImsiSIM2()) != null) {
                    imei_no= TelephonyInfo.getInstance((Activity)context).getImsiSIM2();
                }

            } else {
                imei_no = "";

            }

            return imei_no;
        } catch (Exception e) {
            Log.e(e.getMessage(), "SystemInfoFragment_deviceInfo()");
        }

        return "";
    }

    /**
     * fetch device OS name
     *
     * @return
     */
    public String getAndroidOSName() {
        try {
            String fieldName = "";
            Field[] fields = Build.VERSION_CODES.class.getFields();
            for (Field field : fields) {
                fieldName = field.getName();
            }
            return fieldName;
        } catch (Exception e) {
            Log.e("OS_NAME", "SystemInfoFragment_getAndroidOSName()");
            return "";
        }

    }


    public long  getAvailableInfo(){
        File path  = Environment.getExternalStorageDirectory();
        StatFs statFs = new StatFs(path.getPath());
        long blockSize = statFs.getBlockSize();
        long blockCount = statFs.getBlockCount();

        return blockCount*blockSize;

    }
    public static String getFileSize(long size) {

        try {
            if (size <= 0)
                return "0";

            final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
            int digitGroups = (int) (Math.log10(size) / Math.log10(1024));

            double value = size / Math.pow(1024, digitGroups);

            if (units[digitGroups].equalsIgnoreCase("GB")) {

                int space = (int) value;
                if (space <= 8) {
                    return "8 GB";
                } else if (space > 8 && space <= 16) {
                    return "16 GB";
                } else if (space > 16 && space <= 32) {
                    return "32 GB";
                } else if (space > 32 && space <= 64) {
                    return "64 GB";
                } else if (space > 64 && space <= 128) {
                    return "128 GB";
                } else if (space > 128 && space <= 256) {
                    return "256 GB";
                } else if (space > 256 && space <= 512) {
                    return "512 GB";
                } else if (space > 512 && space <= 1024) {
                    return "1 TB";
                } else {
                    return value + " " + units[digitGroups];
                }
            } else {
                return value + " " + units[digitGroups];
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return "0";
    }
    /** Returns the consumer friendly device name */
    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }

    public boolean isLightsensorExist(Context context){
        boolean isLightSensorExist = true;
      SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        Sensor LightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (LightSensor == null) {
             isLightSensorExist = false;

            Utilities.getInstance(context).compare_UpdatePreferenceInt(context, JsonTags.MMR_28.name(), Constants.TEST_NOT_EXIST);

            Utilities.getInstance(context).addPreferenceBoolean(context, Constants.IS_LIGHT_SENSOR_NOT_EXIST, true);
            Utilities.getInstance(context).addPreferenceInt(context, JsonTags.MMR_28.name(), Constants.TEST_NOT_EXIST);
        }
        return isLightSensorExist;
    }

    public boolean isProximityExist(Context context){
        boolean isProximityExist = true;
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        Sensor proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if (proximitySensor == null) {
            isProximityExist = false;
            Utilities.getInstance(context).compare_UpdatePreferenceInt(context, JsonTags.MMR_23.name(), Constants.TEST_NOT_EXIST);

            Utilities.getInstance(context).addPreferenceBoolean(context, Constants.IS_PROXIMITY_SENSOR__NOT_EXIST, true);
            Utilities.getInstance(context).addPreferenceInt(context, JsonTags.MMR_23.name(), Constants.TEST_NOT_EXIST);
        }
        return isProximityExist;
    }
}
