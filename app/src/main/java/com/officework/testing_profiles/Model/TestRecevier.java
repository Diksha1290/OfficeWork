package com.officework.testing_profiles.Model;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.biometric.BiometricPrompt;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.officework.R;
import com.officework.broadcastReceivers.EarPhoneJackReceiver;
import com.officework.broadcastReceivers.HomeButtonReceiver;
import com.officework.broadcastReceivers.PowerButtonReceiver;
import com.officework.constants.Constants;
import com.officework.fragments.CompassTestFragment;
import com.officework.interfaces.CompassValuesInterface;
import com.officework.interfaces.FaceBiometricErrorInterface;
import com.officework.interfaces.WebServiceCallback;
import com.officework.listeners.ShakeEventListener;
import com.officework.testing_profiles.utils.ConstantTestIDs;
import com.officework.utils.test.FingerprintHandler;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import static android.content.Context.FINGERPRINT_SERVICE;
import static android.content.Context.KEYGUARD_SERVICE;
import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by Diksha on 10/18/2018.
 */

public class TestRecevier implements SensorEventListener,FaceBiometricErrorInterface {
    Handler handler;
    final String KEY_NAME = "yourKey";
    Cipher cipher;
    KeyStore keyStore;
    KeyGenerator keyGenerator;
    boolean haveSensor = false, haveSensor2 = false;
    SensorEventListener sensorEventListener;
    FingerprintHandler helper;
    CompassValuesInterface compassValuesInterface;
    int mAzimuth;
    float[] rMat = new float[9];
    float[] orientation = new float[3];
    boolean isOneQuarter, isTwoQuarter, isThreeQuarter, isFourQuarter;
    SensorManager lightsensorManager;
    Sensor lightSensor;
    SensorEventListener lightSensorListner;
    SensorEventListener proximitySensorListner;
    SensorManager proximitySensorManager;
    PowerButtonReceiver powerButtonReceiver;
    EarPhoneJackReceiver earPhoneJackReceiver;
    HashMap<Integer, Object> sensorMap;
    private FaceBiometricErrorInterface faceBiometricErrorInterface;
    private Context mContext;
    private WebServiceCallback webServiceCallback;
    BroadcastReceiver mRegistrationBroadcastReceiver = new

            BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    // checking for type intent filter
                    if (intent.getAction().equals(Constants.onVolumeKeyPressed)) {

                        Bundle extras = intent.getExtras();
                        if (extras == null) {
                            /*newString= null;*/
                        } else {


                            dispatchKeyEvent((KeyEvent) extras.get("event"));


                        }


                    }
                }
            };
    private ArrayList<BroadcastReceiver> receiverArrayList;
    private Sensor proximitySensor;
    private ShakeEventListener mGyroscopeSensorListener;
    private SensorManager mSensorManager;
    private SensorManager accSensorManager;
    private ShakeEventListener accSensorListner;
    private SensorManager sensorManager;
    private Sensor pressureSensor;
    private Sensor mRotationV, mAccelerometer, mMagnetometer;
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    private HomeButtonReceiver homeButtonReceiver;

    private BroadcastReceiver batteryStateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // Get the battery status indicator integer value
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            // Determine the battery charging status
            boolean isCharging =
                    status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
            //|| status == BatteryManager.BATTERY_STATUS_FULL;
            if (isCharging) {
                // Display the battery charging states
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        webServiceCallback.onServiceResponse(true, "Battery", ConstantTestIDs.Battery);
                    }
                }, 300);
            }
        }
    };
    /**
     * Charging Jack Receiver
     */
    private BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {
        boolean stop = false;

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) == BatteryManager.BATTERY_PLUGGED_AC
                        || intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) == BatteryManager.BATTERY_PLUGGED_USB) {

                    webServiceCallback.onServiceResponse(true, "Charging",
                            ConstantTestIDs.CHARGING_ID);
                    // context.unregisterReceiver(this);
                    //stop = true;
                }
//                if (!stop){
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        BatteryManager batteryManager = (BatteryManager) context
//                        .getSystemService(Context.BATTERY_SERVICE);
//                        if (batteryManager.isCharging()) {
//                            //webServiceCallback.onServiceResponse(true, "Charging",
//                            ConstantTestIDs.CHARGING_ID);
//                        }
//                    }
                //  }
//                if(intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)==100){
//                    if(intent.getIntExtra(BatteryManager.EXTRA_PLUGGED,-1) ==BatteryManager
//                    .BATTERY_PLUGGED_AC|| intent.getIntExtra(BatteryManager.EXTRA_PLUGGED,-1)
//                    ==BatteryManager.BATTERY_PLUGGED_USB){
//                        webServiceCallback.onServiceResponse(true, "Charging", ConstantTestIDs
//                        .CHARGING_ID);
//                    }
//                }
            } catch (Exception e) {
            }

        }
    };


    public TestRecevier(Context context, WebServiceCallback webServiceCallback,FaceBiometricErrorInterface faceBiometricErrorInterface) {
        this.mContext = context;
        this.webServiceCallback = webServiceCallback;
        this.faceBiometricErrorInterface=faceBiometricErrorInterface;
        receiverArrayList = new ArrayList<>();
        mSensorManager = (SensorManager) mContext.getSystemService(SENSOR_SERVICE);

    }

    public void registerEarJackReceiver(int testId) {
        try {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_HEADSET_PLUG);
            earPhoneJackReceiver = new EarPhoneJackReceiver(webServiceCallback, testId);
            if (!receiverArrayList.contains(earPhoneJackReceiver)) {

                mContext.registerReceiver(earPhoneJackReceiver, intentFilter);
                receiverArrayList.add(earPhoneJackReceiver);
            }
        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    public void registerVolumeRecevier(int testId) {

        LocalBroadcastManager.getInstance(mContext).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Constants.onVolumeKeyPressed));
        if (!receiverArrayList.contains(mRegistrationBroadcastReceiver)) {

            receiverArrayList.add(mRegistrationBroadcastReceiver);

        }

    }

    public void registerPowerRecevier(int testId) {
        try {


            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_USER_PRESENT);
            try {


                if (powerButtonReceiver != null) {
                    mContext.unregisterReceiver(powerButtonReceiver);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            powerButtonReceiver = new PowerButtonReceiver(webServiceCallback, testId, mContext);
            if (!receiverArrayList.contains(powerButtonReceiver)) {
                mContext.registerReceiver(powerButtonReceiver, filter);
                receiverArrayList.add(powerButtonReceiver);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void registerHomeRecevier(int testId) {
        IntentFilter filter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        homeButtonReceiver = new HomeButtonReceiver(webServiceCallback, testId);
        if (!receiverArrayList.contains(homeButtonReceiver)) {
            mContext.registerReceiver(homeButtonReceiver, filter);
            receiverArrayList.add(homeButtonReceiver);
        }
    }


    public void registerChargingRecevier(int testId) {
        if(batteryInfoReceiver!=null)
        {
            unRegisterCharging();
        }
        if (!receiverArrayList.contains(batteryInfoReceiver)) {
            mContext.registerReceiver(batteryInfoReceiver,
                    new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            //  receiverArrayList.add(batteryInfoReceiver);

        }
    }
//
//    public void registerProximityRecevier(int testId) {
//        proximitySensorManager = (SensorManager) mContext.getSystemService(SENSOR_SERVICE);
//
//        proximitySensor = proximitySensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
//
//        if (proximitySensor == null) {
////            webServiceCallback.onServiceResponse(false, "Proximtity", ConstantTestIDs
////            .PROXIMITY_ID);
//
//        } else {
//            proximitySensorManager.registerListener(proximitySensorListner =
//                            new SensorEventListener() {
//
//
//                                @Override
//                                public void onSensorChanged(SensorEvent event) {
//                                    if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
//                                        if (event.values[0] == proximitySensor.getMinDelay()) {
//                                            webServiceCallback.onServiceResponse(true, "Charging",
//                                                    ConstantTestIDs.PROXIMITY_ID);
//
//                                            // proximitySensorManager.unregisterListener
//                                            // (proximitySensorListner);
//                                        }
////                                                       else {
////
////                                                          // webServiceCallback
////                                                          .onServiceResponse(false, "Charging",
////                                                          ConstantTestIDs.PROXIMITY_ID);
////
////                                                       }
//                                    }
//                                }
//
//                                @Override
//                                public void onAccuracyChanged(Sensor sensor, int i) {
//
//                                }
//                            }, proximitySensor,
//                    SensorManager.SENSOR_DELAY_NORMAL);
//        }
//
//    }




    public void registerProximityRecevier(int testId){
        proximitySensorManager = (SensorManager) mContext.getSystemService(SENSOR_SERVICE);

        proximitySensor = proximitySensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        final int SENSOR_SENSITIVITY = 4;

        if(proximitySensor == null){
//            webServiceCallback.onServiceResponse(false, "Proximtity", ConstantTestIDs.PROXIMITY_ID);

        }else {
            proximitySensorManager.registerListener(proximitySensorListner=new SensorEventListener() {


                        @Override
                        public void onSensorChanged(SensorEvent event) {
                            if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                                if (event.values[0] < proximitySensor.getMaximumRange()) {
                                    // if (event.values[0] >= -SENSOR_SENSITIVITY && event.values[0] <= SENSOR_SENSITIVITY) {
                                    webServiceCallback.onServiceResponse(true, "Charging", ConstantTestIDs.PROXIMITY_ID);

                                    // proximitySensorManager.unregisterListener(proximitySensorListner);
                                }
//                                                       else {
//
//                                                          // webServiceCallback.onServiceResponse(false, "Charging", ConstantTestIDs.PROXIMITY_ID);
//
//                                                       }
                            }
                        }

                        @Override
                        public void onAccuracyChanged(Sensor sensor, int i) {

                        }
                    }, proximitySensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void registerFlashReciever(int testId) {
        try {
            CameraManager cameraManager;
            String cameraId = null;
            cameraManager = (CameraManager) mContext.getSystemService(mContext.CAMERA_SERVICE);

            try {
                cameraId = cameraManager.getCameraIdList()[0];
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }

            Camera mCam = null;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {

                mCam = Camera.open();
            }

            if (mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                String s = "010101";
                for (int i = 0; i < s.length(); i++) {

                    if (s.charAt(i) == '0') {
                        try {

                            CameraCharacteristics cameraCharacteristics =
                                    cameraManager.getCameraCharacteristics("0");
                            boolean flashAvailable =
                                    cameraCharacteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                            if (flashAvailable) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    cameraManager.setTorchMode(cameraId, true);
                                } else {
                                    Camera.Parameters p = mCam.getParameters();
                                    p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                                    mCam.setParameters(p);
                                    SurfaceTexture mPreviewTexture = new SurfaceTexture(0);
                                    try {
                                        mCam.setPreviewTexture(mPreviewTexture);
                                    } catch (IOException ex) {
// Ignore
                                    }
                                    mCam.startPreview();

                                }
                            }
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                cameraManager.setTorchMode(cameraId, false);
                            } else {
                                Camera.Parameters p = mCam.getParameters();
                                p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                                mCam.setParameters(p);
                                SurfaceTexture mPreviewTexture = new SurfaceTexture(0);
                                try {
                                    mCam.setPreviewTexture(mPreviewTexture);
                                } catch (IOException ex) {
// Ignore
                                }
                                mCam.stopPreview();
                            }
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    }

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // webServiceCallback.onServiceResponse(true, "Flash", ConstantTestIDs.FLASH);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerFingerPrintReciever(int testId) {
        //TextView textView;
        FingerprintManager.CryptoObject cryptoObject;
        FingerprintManager fingerprintManager;
        KeyguardManager keyguardManager;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            keyguardManager =
                    (KeyguardManager) mContext.getSystemService(KEYGUARD_SERVICE);
            fingerprintManager =
                    (FingerprintManager) mContext.getSystemService(FINGERPRINT_SERVICE);

            // textView = (TextView)view. findViewById(R.id.txt1);


            if (!fingerprintManager.isHardwareDetected()) {
                webServiceCallback.onServiceResponse(false, "FingerPrint",
                        ConstantTestIDs.FINGERPRINT);
                //textView.setText("Your device doesn't support fingerprint authentication");
                Toast.makeText(mContext, "Your device doesn't support fingerprint authentication"
                        , Toast.LENGTH_LONG).show();
            }

            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {

                //  textView.setText("Please enable the fingerprint permission");
            }


            if (!fingerprintManager.hasEnrolledFingerprints()) {
                // webServiceCallback.onServiceResponse(false, "FingerPrint", ConstantTestIDs
                // .FINGERPRINT);
                // Toast.makeText(mContext,"No fingerprint configured. Please register at least
                // one fingerprint in your device's Settings",Toast.LENGTH_LONG).show();
                // textView.setText("No fingerprint configured. Please register at least one
                // fingerprint in your device's Settings");
            }

            //Check that the lockscreen is secured//
            if (!keyguardManager.isKeyguardSecure()) {

                //  textView.setText("Please enable lockscreen security in your device's Settings");
            } else {
                try {
                    generateKey();
                } catch (FingerprintException e) {
                    e.printStackTrace();
                }

                if (initCipher()) {

                    cryptoObject = new FingerprintManager.CryptoObject(cipher);


                    helper = new FingerprintHandler(mContext, webServiceCallback,faceBiometricErrorInterface);
                    helper.startAuth(fingerprintManager, cryptoObject);

                }

            }
        }


    }

    public void registerBioMetricPrintReciever(int testId) {
        Executor executor = Executors.newSingleThreadExecutor();

         BiometricPrompt biometricPrompt = new BiometricPrompt((FragmentActivity) mContext, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Log.d("onAuthenticationError",errorCode+".."+errString);
                faceBiometricErrorInterface.ErrorData(errorCode,errString);
                if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                    // user clicked negative button

                } else {
                    //TODO: Called when an unrecoverable error has been encountered and the operation is complete.
                }
                webServiceCallback.onServiceResponse(false, "FingerPrint", ConstantTestIDs.FINGERPRINT);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Log.d("auth success",result.toString());
                webServiceCallback.onServiceResponse(true, "FingerPrint", ConstantTestIDs.FINGERPRINT);
                //TODO: Called when a biometric is recognized.
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Log.d("auth fail","fail");
                webServiceCallback.onServiceResponse(true, "FingerPrint", ConstantTestIDs.FINGERPRINT);
                //TODO: Called when a biometric is valid but not recognized.
            }
        });
         BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle(mContext.getString(R.string.app_name))
                .setNegativeButtonText("Negative Button")
                .build();
        biometricPrompt.authenticate(promptInfo);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void generateKey() throws FingerprintException {
        try {

            keyStore = KeyStore.getInstance("AndroidKeyStore");


            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,
                    "AndroidKeyStore");


            keyStore.load(null);


            keyGenerator.init(new


                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)


                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());


            keyGenerator.generateKey();

        } catch (KeyStoreException
                | NoSuchAlgorithmException
                | NoSuchProviderException
                | InvalidAlgorithmParameterException
                | CertificateException
                | IOException exc) {
            exc.printStackTrace();
            throw new FingerprintException(exc);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean initCipher() {
        try {

            cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);

            return true;
        } catch (KeyPermanentlyInvalidatedException e) {


            return false;
        } catch (KeyStoreException | CertificateException
                | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    public void registerLightSensorRecevier(int testId) {
        // lightsensorManager=new LightSensorListner(mContext,webServiceCallback);
        lightsensorManager = (SensorManager) mContext.getSystemService(SENSOR_SERVICE);

        lightSensor = lightsensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        if (lightSensor == null) {
            webServiceCallback.onServiceResponse(false, "LightSensor",
                    ConstantTestIDs.LIGHT_SENSOR_ID);

        } else {

            lightsensorManager.registerListener(lightSensorListner = new SensorEventListener() {


                        @Override
                        public void onSensorChanged(SensorEvent event) {
                            if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                                if (event.values[0] > 100) {
                                    webServiceCallback.onServiceResponse(true, "Lightsensor",
                                            ConstantTestIDs.LIGHT_SENSOR_ID);

                                    lightsensorManager.unregisterListener(lightSensorListner);
                                }
                            }
                        }

                        @Override
                        public void onAccuracyChanged(Sensor sensor, int i) {

                        }
                    }, lightSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }

    }

    public void registerAcclerometerReciever(int testId) {
        accSensorListner = new ShakeEventListener();
        accSensorManager = (SensorManager) mContext.getSystemService(SENSOR_SERVICE);

        accSensorManager.registerListener(accSensorListner,
                accSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);
        accSensorListner.setOnShakeListener(new ShakeEventListener.OnShakeListener() {

            public void onShake() {

                webServiceCallback.onServiceResponse(true, "Acceleraometer",
                        ConstantTestIDs.ACCELEROMETER);

                accSensorManager.unregisterListener(accSensorListner);

                //    accSensorListner.unregisterListener();

            }

        });
    }

    public void registerBarometerReciever(int testId) {
        sensorManager = (SensorManager) mContext.getSystemService(SENSOR_SERVICE);
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        if (pressureSensor == null) {
            webServiceCallback.onServiceResponse(false, "PressureSensor",
                    ConstantTestIDs.Barometer);
        } else {
            sensorManager.registerListener(sensorEventListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent sensorEvent) {
                    float[] values = sensorEvent.values;
                    double a = 0.09 * 1050;
                    sensorManager.unregisterListener(sensorEventListener);
                    webServiceCallback.onServiceResponse(true, String.valueOf(values[0] / 10),
                            ConstantTestIDs.Barometer);
//                gaugeView.setTargetValue(values[0]/10);
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int i) {

                }
            }, pressureSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void registerCompassReciever(int testId, CompassTestFragment compassTestFragment) {
        compassValuesInterface = compassTestFragment;
        mSensorManager = (SensorManager) mContext.getSystemService(SENSOR_SERVICE);
        start();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        String where = "NW";
        try {

            if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                SensorManager.getRotationMatrixFromVector(rMat, event.values);
                mAzimuth =
                        (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;
            }

            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
                mLastAccelerometerSet = true;
            } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                System.arraycopy(event.values, 0, mLastMagnetometer, 0, event.values.length);
                mLastMagnetometerSet = true;
            }
            if (mLastAccelerometerSet && mLastMagnetometerSet) {
                SensorManager.getRotationMatrix(rMat, null, mLastAccelerometer, mLastMagnetometer);
                SensorManager.getOrientation(rMat, orientation);
                mAzimuth =
                        (int) (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360;
            }

            mAzimuth = Math.round(mAzimuth);
//            webServiceCallback.onServiceResponse(true, String.valueOf(-mAzimuth),
//            ConstantTestIDs.COMPASS);
//            compassPinImage.setRotation(-mAzimuth);


            if (mAzimuth >= 350 || mAzimuth <= 10)
                where = "N";
            if (mAzimuth < 350 && mAzimuth > 280)
                where = "NW";
            if (mAzimuth <= 280 && mAzimuth > 260)
                where = "W";
            if (mAzimuth <= 260 && mAzimuth > 190)
                where = "SW";
            if (mAzimuth <= 190 && mAzimuth > 170)
                where = "S";
            if (mAzimuth <= 170 && mAzimuth > 100)
                where = "SE";
            if (mAzimuth <= 100 && mAzimuth > 80)
                where = "E";
            if (mAzimuth <= 80 && mAzimuth > 10)
                where = "NE";

            if (mAzimuth >= 0 || mAzimuth < 90)
                isOneQuarter = true;
            if (mAzimuth >= 90 && mAzimuth < 180)
                isTwoQuarter = true;
            if (mAzimuth >= 180 && mAzimuth < 270)
                isThreeQuarter = true;
            if (mAzimuth >= 270 && mAzimuth < 360)
                isFourQuarter = true;
//            txt.setText(mAzimuth + "° " + where);

            compassValuesInterface.compassValues(mAzimuth + "° " + where);
            // webServiceCallback.onServiceResponse(null,mAzimuth + "° " + where ,ConstantTestIDs
            // .COMPASS);
            if (isOneQuarter && isTwoQuarter && isThreeQuarter && isFourQuarter) {
                webServiceCallback.onServiceResponse(true, mAzimuth + "° " + where,
                        ConstantTestIDs.COMPASS);
                mSensorManager.unregisterListener(this);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public void start() {
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) == null) {
            if ((mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null) || (mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) == null)) {
//                noSensorsAlert();
            } else {
                mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                haveSensor = mSensorManager.registerListener(this, mAccelerometer,
                        SensorManager.SENSOR_DELAY_UI);
                haveSensor2 = mSensorManager.registerListener(this, mMagnetometer,
                        SensorManager.SENSOR_DELAY_UI);
            }
        } else {
            mRotationV = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            haveSensor = mSensorManager.registerListener(this, mRotationV,
                    SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void registerPhoneShakeListener(int testId) {
        mGyroscopeSensorListener = new ShakeEventListener();

        mSensorManager.registerListener(mGyroscopeSensorListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI);
        mGyroscopeSensorListener.setOnShakeListener(new ShakeEventListener.OnShakeListener() {

            public void onShake() {

                webServiceCallback.onServiceResponse(true, "Shaking", ConstantTestIDs.GYROSCOPE_ID);

                //  mSensorManager.unregisterListener(mGyroscopeSensorListener);

                //  mGyroscopeSensorListener.unregisterListener();

            }

        });
        // mSensorManager.unregisterListener(mSensorListener);
    }

    /**
     * Volume Keys Event Handler
     */
    public void dispatchKeyEvent(KeyEvent event) {
        try {
            int action = event.getAction();
            int keyCode = event.getKeyCode();
            switch (keyCode) {
                case KeyEvent.KEYCODE_VOLUME_UP:
                    if (action == KeyEvent.ACTION_DOWN) {
                        webServiceCallback.onServiceResponse(true, "Volume",
                                ConstantTestIDs.VOLUME_UP);


                    }
                    break;
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    if (action == KeyEvent.ACTION_DOWN) {
                        webServiceCallback.onServiceResponse(true, "Volume",
                                ConstantTestIDs.VOLUME_DOWN);


                    }
                    break;
            }
        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    public void unRegisterReceviers() {

        for (int index = 0; index < receiverArrayList.size(); index++) {
            try {
                mContext.unregisterReceiver(receiverArrayList.get(index));
                receiverArrayList.remove(index);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    public void batteryChargingStateReceiver(int testId) {
        handler=new Handler();
        if(batteryStateReceiver!=null)
        {
            unRegisterBattery();
        }
        if (!receiverArrayList.contains(batteryStateReceiver)) {
            mContext.registerReceiver(batteryStateReceiver,
                    new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        }
    }

    public void unregisterHomeandCharging() {

        try {
//           try {
//            accSensorManager.unregisterListener(accSensorListner);}
//           catch (Exception e){}

            try {
                mContext.unregisterReceiver(homeButtonReceiver);
            } catch (Exception e) {
            }
            try {
                mContext.unregisterReceiver(batteryInfoReceiver);
            } catch (Exception e) {
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void unRegisterHome() {
        try {
            mContext.unregisterReceiver(homeButtonReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unRegisterCharging() {
        try {
            mContext.unregisterReceiver(batteryInfoReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unRegisterPowerButton() {
        try {
            mContext.unregisterReceiver(powerButtonReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unRegisterEarjackReciever() {
        try {
            mContext.unregisterReceiver(earPhoneJackReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unregisterGyroScope2() {
        try {
            mSensorManager.unregisterListener(mGyroscopeSensorListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unregistrProximity() {
        try {
            proximitySensorManager.unregisterListener(proximitySensorListner);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unregisterLightSensor() {
        try {
            lightsensorManager.unregisterListener(lightSensorListner);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unregisterCompass() {
        try {
            mSensorManager.unregisterListener(this);
            isOneQuarter = false;
            isTwoQuarter = false;
            isThreeQuarter = false;
            isFourQuarter = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unregisterFingerPrintSensor() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                helper.stopFingerPrint();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public void unRegisterSensor(int testId)
//    {
//
//
//            Object value=(Object) sensorMap.get(testId);
//
//
//
//    }
//
//    public void createSensorMap(int testId,Object sensor) {
//         sensorMap = new HashMap<Integer, Object>();
//        sensorMap.put(testId, sensor);
//    }

    public void unRegisterBattery() {
        try {
            mContext.unregisterReceiver(batteryStateReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void ErrorData(int error_code, CharSequence errorMessage) {
        faceBiometricErrorInterface.ErrorData(error_code,errorMessage);
    }

    private class FingerprintException extends Exception {
        public FingerprintException(Exception e) {
            super(e);
        }
    }


}
