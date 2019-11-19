package com.officework.listeners;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import com.officework.interfaces.InterfaceProximitySensorChanged;

/**
 * Created by Ashwani on 5/4/2017.
 */

public class ProximitySensorEventListener implements SensorEventListener {
    InterfaceProximitySensorChanged minterfaceProximitySensorChanged;

    public ProximitySensorEventListener(InterfaceProximitySensorChanged interfaceProximitySensorChanged) {
        minterfaceProximitySensorChanged = interfaceProximitySensorChanged;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            if (event.values[0] == 0) {
                minterfaceProximitySensorChanged.onProximityDetectedNear(true,"NEAR");

            } else {
                minterfaceProximitySensorChanged.onProximirtDetectFar(true,"FAR");

            }
        }
    }

    public void mockOnSensorChanged(int senorType, int[] value) {

        if (senorType == Sensor.TYPE_PROXIMITY) {
            if (value[0] == 0) {
                minterfaceProximitySensorChanged.onProximityDetectedNear(true,"NEAR");
            } else {
                minterfaceProximitySensorChanged.onProximityDetectedNear(true,"FAR");
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
