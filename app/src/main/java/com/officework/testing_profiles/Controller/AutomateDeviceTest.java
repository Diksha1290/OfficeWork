package com.officework.testing_profiles.Controller;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.officework.testing_profiles.ui.fragment.AutomatedTestFragment;
import com.officework.utils.Utilities;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by Diksha on 10/17/2018.
 */

public class AutomateDeviceTest {

    /**
     * This method return sim card status
     *
     * @return String
     */

    public static boolean getSimCardStatus(Activity activity, Context ctx, Utilities utils) {
        try {
            int simState =
                    ((TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE)).getSimState();
            switch (simState) {
                case TelephonyManager.SIM_STATE_ABSENT:
                    return false;
                case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                    return false;

                case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                    return false;

                case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                    return false;
                case TelephonyManager.SIM_STATE_READY:
                    return true;

                case TelephonyManager.SIM_STATE_UNKNOWN:
                    return false;
            }
            return false;
        } catch (Exception e) {
            Log.d("automation exe",e.getMessage());
            AutomatedTestFragment automatedTestFragment = new AutomatedTestFragment(null);

            automatedTestFragment.logException(e, "AutomatedTestFragment_getSimCardStatus()");
            return false;
        }


    }
    public static boolean getBarometerSensorStatus(Context ctx) {
        final boolean[] status = {true};

        SensorManager sensorManager;
        Sensor pressureSensor;
        sensorManager = (SensorManager) ctx.getSystemService(SENSOR_SERVICE);
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        if (pressureSensor == null) {
            status[0] = false;

        } else {
            sensorManager.registerListener(new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent sensorEvent) {
                    status[0] = true;
                    sensorManager.unregisterListener(this);
//                    float[] values = sensorEvent.values;
//                    double a = 0.09 * 1050;
//                    sensorManager.unregisterListener(sensorEventListener);
//                    webServiceCallback.onServiceResponse(true, String.valueOf(values[0] / 10),
//                            ConstantTestIDs.Barometer);
////                gaugeView.setTargetValue(values[0]/10);
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int i) {

                }
            }, pressureSensor, SensorManager.SENSOR_DELAY_UI);
        }
        return status[0];

    }

    /**
     *
     */
    public static boolean getDeviceNetworkAvailable(Context ctx) {
        try {
            TelephonyManager tel = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
            return (tel.getNetworkOperator() != null && !tel.getNetworkOperator().equals(""));
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public static boolean getLightSensorStatus(Context ctx) {
        final boolean[] status = {true};
        final SensorManager lightsensorManager =
                (SensorManager) ctx.getSystemService(SENSOR_SERVICE);
        Sensor lightSensor = lightsensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (lightSensor == null) {
            return false;
        } else {

            lightsensorManager.registerListener(new SensorEventListener() {
                                                    @Override
                                                    public void onSensorChanged(SensorEvent event) {
                                                        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                                                            if (event.values[0] > 100) {
                                                                status[0] = true;
                                                                lightsensorManager.unregisterListener(this);
                                                            }
                                                        }
                                                    }

                                                    @Override
                                                    public void onAccuracyChanged(Sensor sensor, int i) {

                                                    }
                                                }, lightSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);

        }

        return status[0];
    }

}
