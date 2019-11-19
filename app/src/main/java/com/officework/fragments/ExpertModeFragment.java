package com.officework.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Vibrator;
import com.google.android.material.snackbar.Snackbar;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.officework.R;
import com.officework.activities.MainActivity;
import com.officework.base.BaseFragment;
import com.officework.broadcastReceivers.HomeButtonReceiver;
import com.officework.constants.AsyncConstant;
import com.officework.constants.Constants;
import com.officework.constants.FragmentTag;
import com.officework.constants.JsonTags;
import com.officework.interfaces.InterfaceBroadcastCallback;
import com.officework.interfaces.InterfaceButtonTextChange;
import com.officework.interfaces.InterfacePowerButtonCallBack;
import com.officework.interfaces.OnHomePressedListener;
import com.officework.listeners.HomeWatcher;
import com.officework.listeners.ShakeEventListener;
import com.officework.utils.Utilities;

/**
 * Created by Girish on 29/11/2016.
 */
public class ExpertModeFragment extends BaseFragment implements InterfaceBroadcastCallback, SensorEventListener, OnHomePressedListener, InterfacePowerButtonCallBack {
    View view;
    Utilities utils;
    Context ctx;
    FrameLayout parentLayout;
    boolean isTestPassed = false;
    BroadcastReceiver mReceiverJack = null;
    BroadcastReceiver mReceiver = null;
    boolean isVolIncrease = false, isVolDecrease = false;
    int count = 0;
    int lightCount = 0;
    MultitouchView multitouchView;
    //SensorManager lets you access the device's sensors
    //declare Variables
    private SensorManager sensorManager;
    Sensor proximitySensor;
    // For the toast of proximity sensor
    boolean isShowToast;
    // Home Button Test
    HomeButtonReceiver homeButtonReceiver;
    private IntentFilter mFilter;
    Intent intent;
    public static boolean isHomeButtonTested = false;
    HomeWatcher mHomeWatcher;
    // Gyroscope Test
    private ShakeEventListener mSensorListener;
    public Vibrator vibrator;
    long pattern[] = {0, 100, 200};
    boolean isPowerTestPerformed = false;
    boolean isProximityTestPerformed = false;
    boolean isGyroscopeTestPerformed = false;
    boolean isJackTestPerformes = false;
    boolean isMultitouchPerformed = false;
    boolean isChargingTestPerformed = false;
    boolean isLightSensorPerformed = false;
    boolean isBackGround = false;
    boolean isHomeButtonDone = false;
    //Power Button Test
    private IntentFilter mFilterPower;
    IntentFilter filter;
    private TextView mtxtViewJack, mtxtViewVolume, mtxtViewPower, mtxtViewCharging, mtxtViewMutitouch, mtxtViewProximity, mtxtViewHome, mtxtViewGyroscope, mtxtViewLightSensor;


    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_expert_mode, null);
            utils = Utilities.getInstance(getActivity());
            ctx = getActivity();
      //      Crashlytics.getInstance().log(FragmentTag.EXPERT_MODE_FRAGMENT.name());
            initViews();
        }
        return view;
    }

    public ExpertModeFragment() {
    }

    private void initViews() {
        parentLayout = (FrameLayout) view.findViewById(R.id.parentLayout);
        mtxtViewJack = (TextView) view.findViewById(R.id.txtViewJackTest);
        mtxtViewVolume = (TextView) view.findViewById(R.id.txtViewVolume);
        mtxtViewPower = (TextView) view.findViewById(R.id.txtViewPowerTest);
        mtxtViewCharging = (TextView) view.findViewById(R.id.txtViewChargingTest);
        mtxtViewMutitouch = (TextView) view.findViewById(R.id.txtViewMutitouchTest);
        mtxtViewProximity = (TextView) view.findViewById(R.id.txtViewProximityTest);
        mtxtViewHome = (TextView) view.findViewById(R.id.txtViewHomeTest);
        mtxtViewGyroscope = (TextView) view.findViewById(R.id.txtViewGyroscopeTest);
        mtxtViewLightSensor = (TextView) view.findViewById(R.id.txtViewLightTest);

        multitouchView = new MultitouchView(ctx, null);
        if (!isMultitouchPerformed) {
            parentLayout.addView(multitouchView);
        }
        //create instance of sensor manager and get system service to interact with Sensor
        sensorManager = (SensorManager) ctx.getSystemService(Context.SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if (proximitySensor == null) {
            utils.showToast(ctx, getResources().getString(R.string.txtNoProximitySensor));
           // utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_23.name(), AsyncConstant.TEST_FAILED);
            mtxtViewProximity.setTextColor(getResources().getColor(R.color.RedColor));
        }
        //Power Button
        filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);

        // Home Button watcher intialize
        mHomeWatcher = new HomeWatcher(getActivity());

        Sensor LightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (!isLightSensorPerformed) {
            if (LightSensor != null) {
                //textLIGHT_available.setText("Sensor.TYPE_LIGHT Available");
                sensorManager.registerListener(
                        LightSensorListener,
                        LightSensor,
                        SensorManager.SENSOR_DELAY_NORMAL);

            } else {
                //textLIGHT_available.setText("Sensor.TYPE_LIGHT NOT Available");
            }
        }

        mSensorListener = new ShakeEventListener();
        intializeVibrator();
        mSensorListener.setOnShakeListener(new ShakeEventListener.OnShakeListener() {

            public void onShake() {
                startVibrate();
                /*utils.showToast(ctx, getResources().getString(R.string.txtPhoneShaked));*/
               // utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_40.name(), AsyncConstant.TEST_PASS);

                utils.showToast(ctx, getResources().getString(R.string.txtGyroscopePass));
                mtxtViewGyroscope.setTextColor(getResources().getColor(R.color.green_color));
                sensorManager.unregisterListener(mSensorListener);
            }
        });


    }




    /**
     * Charging Jack Receiver
     */
    private BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0) == BatteryManager.BATTERY_STATUS_CHARGING) {
                isTestPassed = true;
                if (!isBackGround) {
                    isChargingTestPerformed = true;
                    utils.showToast(ctx, getResources().getString(R.string.txtChargingPort));
                    mtxtViewCharging.setTextColor(getResources().getColor(R.color.green_color));
                   // utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_44.name(), AsyncConstant.TEST_PASS);
                    if (batteryInfoReceiver != null) {
                        getActivity().unregisterReceiver(batteryInfoReceiver);
                        batteryInfoReceiver = null;
                    }
                }
                /*isChargingTestPerformed = true;*/
                /**
                 * Charger Receiver Unregister
                 * */
               /* if (batteryInfoReceiver != null) {
                    getActivity().unregisterReceiver(batteryInfoReceiver);
                    batteryInfoReceiver = null;
                }*/


            } /*else if (intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0) == BatteryManager.BATTERY_STATUS_NOT_CHARGING) {
                utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_44.name(), AsyncConstant.TEST_FAILED);
                *//**
             * Charger Receiver Unregister
             * *//*
            }*/
        }
    };

    /**
     * Here change the header text
     ***/
    @Override
    public void onResume() {
        isShowToast = false;
        if (utils.getPreferenceLong(ctx, Constants.lastDate_Time, 0L) != 0L && utils.getPreferenceLong(ctx, Constants.currentDate_Time, 0L) != 0L) {
            if (utils.getTimeDifference(utils.getPreferenceLong(ctx, Constants.lastDate_Time, 0L),
                    utils.getPreferenceLong(ctx, Constants.currentDate_Time, 0L)) >= 12) {
               // clearPreferenceData(utils, ctx);
                utils.showToast(ctx, getResources().getString(R.string.txtDataExpired));
                utils.addPreferenceLong(ctx, Constants.lastDate_Time, 0L);
                utils.addPreferenceLong(ctx, Constants.currentDate_Time, 0L);
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
            } else {
                utils.storeDateTime(ctx, true);
                utils.addLog(ctx, "Log Last Date", Constants.lastDateTime + "");
                TitleBarFragment fragment = (TitleBarFragment) getFragment(R.id.headerContainer);
                if (fragment != null) {
                    fragment.setTitleBarVisibility(true);
                    fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string.ExpertMode), true, false, 0);
                    isBackGround = false;

                    /**
                     * Register Charging Jack Receiver
                     * */
                    if (!isChargingTestPerformed) {
                        getActivity().registerReceiver(batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                    }

                    /**
                     * Register Head Jack Receiver
                     * */


                    /**
                     * Register Sensor Manager Receiver
                     * */
                    if (!isProximityTestPerformed) {
                        sensorManager.registerListener(ExpertModeFragment.this, proximitySensor,
                                SensorManager.SENSOR_DELAY_NORMAL);
                    }

                    /**
                     * Register Home Button Receiver.
                     * */


                    if (isHomeButtonTested) {
                        isHomeButtonTested = false;
                        utils.showToast(ctx, getResources().getString(R.string.txtHomeButtonPass));
                        mtxtViewHome.setTextColor(getResources().getColor(R.color.green_color));

                        if (homeButtonReceiver != null) {
                            ctx.unregisterReceiver(homeButtonReceiver);
                            homeButtonReceiver = null;
                            mFilter = null;
                        }
                    } else {

                    }

                    //power Button Initializer
                    if (isPowerTestPerformed) {
                        if (mReceiver != null) {
                            ctx.unregisterReceiver(mReceiver);
                            mReceiver = null;
                        }
                    } else {


                    }


                    /**
                     * Register Gyroscope Receiver.
                     * */
                    sensorManager.registerListener(mSensorListener,
                            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                            SensorManager.SENSOR_DELAY_UI);

                }
            }
        } else {
            utils.storeDateTime(ctx, true);
            utils.addLog(ctx, "Log Last Date", Constants.lastDateTime + "");
            TitleBarFragment fragment = (TitleBarFragment) getFragment(R.id.headerContainer);
            if (fragment != null) {
                fragment.setTitleBarVisibility(true);
                fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string.ExpertMode), true, false, 0);
                isBackGround = false;

                /**
                 * Register Charging Jack Receiver
                 * */
                if (!isChargingTestPerformed) {
                    getActivity().registerReceiver(batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                }

                /**
                 * Register Head Jack Receiver
                 * */


                /**
                 * Register Sensor Manager Receiver
                 * */
                if (!isProximityTestPerformed) {
                    sensorManager.registerListener(ExpertModeFragment.this, proximitySensor,
                            SensorManager.SENSOR_DELAY_NORMAL);
                }

                /**
                 * Register Home Button Receiver.
                 * */


                if (isHomeButtonTested) {
                    isHomeButtonTested = false;
                    utils.showToast(ctx, getResources().getString(R.string.txtHomeButtonPass));
                    mtxtViewHome.setTextColor(getResources().getColor(R.color.green_color));

                    if (homeButtonReceiver != null) {
                        ctx.unregisterReceiver(homeButtonReceiver);
                        homeButtonReceiver = null;
                        mFilter = null;
                    }
                } else {

                }

                //power Button Initializer
                if (isPowerTestPerformed) {
                    if (mReceiver != null) {
                        ctx.unregisterReceiver(mReceiver);
                        mReceiver = null;
                    }
                } else {


                }


                /**
                 * Register Gyroscope Receiver.
                 * */
                sensorManager.registerListener(mSensorListener,
                        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                        SensorManager.SENSOR_DELAY_UI);

            }
        }


        /*TitleBarFragment fragment = (TitleBarFragment) getFragment(R.id.headerContainer);
        if (fragment != null) {
            fragment.setTitleBarVisibility(true);
            fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string.ExpertMode), true, false, 0);
            isBackGround = false;

            *//**
             * Register Charging Jack Receiver
             * *//*
            if (!isChargingTestPerformed) {
                getActivity().registerReceiver(batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            }

            *//**
             * Register Head Jack Receiver
             * *//*
            if (!isJackTestPerformes) {
                registerJackReceiver();
            }

            *//**
             * Register Sensor Manager Receiver
             * *//*
            if (!isProximityTestPerformed) {
                sensorManager.registerListener(ExpertModeFragment.this, proximitySensor,
                        SensorManager.SENSOR_DELAY_NORMAL);
            }

            *//**
             * Register Home Button Receiver.
             * *//*


            if (isHomeButtonTested) {
                isHomeButtonTested = false;
                utils.showToast(ctx, getResources().getString(R.string.txtHomeButtonPass));
                mtxtViewHome.setTextColor(getResources().getColor(R.color.green_color));

                if (homeButtonReceiver != null) {
                    ctx.unregisterReceiver(homeButtonReceiver);
                    homeButtonReceiver = null;
                    mFilter = null;
                }
            } else {
                if (!isHomeButtonDone) {
                    if (homeButtonReceiver == null) {
                        mFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                        homeButtonReceiver = new HomeButtonReceiver((OnHomePressedListener) this);
                        ctx.registerReceiver(homeButtonReceiver, mFilter);
                    }
                }
            }

            //power Button Initializer
            if (isPowerTestPerformed) {
                if (mReceiver != null) {
                    ctx.unregisterReceiver(mReceiver);
                    mReceiver = null;
                }
            } else {

                if (mReceiver == null) {
                    mReceiver = new PowerButtonReceiver((InterfacePowerButtonCallBack) this);
                    ctx.registerReceiver(mReceiver, filter);
                }
            }


            *//**
             * Register Gyroscope Receiver.
             * *//*
            sensorManager.registerListener(mSensorListener,
                    sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_UI);

        }*/
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        isBackGround = true;

        /**
         * Unregister Power Button Receiver
         */
      /*  if (mReceiver != null) {
            ctx.unregisterReceiver(mReceiver);
            mReceiver = null;
        }*/
        /**
         * Charger Receiver Unregister
         * */


        /**
         * Unregister Jack Receiver
         * */
        /**
         * Unregister SensorManager of Proximity Sensor
         * */
        if (!isProximityTestPerformed) {
            sensorManager.unregisterListener(this);
        }
        sensorManager.unregisterListener(mSensorListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(LightSensorListener);
    }

    @Override
    public void onDetach() {
        if (batteryInfoReceiver != null) {
            getActivity().unregisterReceiver(this.batteryInfoReceiver);
            batteryInfoReceiver = null;
        }

        if (homeButtonReceiver != null) {
            ctx.unregisterReceiver(homeButtonReceiver);
            homeButtonReceiver = null;
            mFilter = null;
        }
        if (mReceiver != null) {
            ctx.unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        super.onDetach();
    }

    public void onBackPress() {
        snackShow(parentLayout);
    }

    private void reopenScreen() {
        intent = new Intent(ctx, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    public void snackShow(FrameLayout relativeLayout) {
        Snackbar snackbar = Snackbar
                .make(relativeLayout, getResources().getString(R.string.txtBackPressed), Snackbar.LENGTH_LONG)
                .setAction(getResources().getString(R.string.txtBackPressedYes), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                      /*  Constants.isNextHandler = true;
                        Constants.isBackButton = true;
                        *//*popFragment(R.id.container);*//*
                        ManualTestFragment.iSManual = false;*/
                        if (homeButtonReceiver != null) {
                            ctx.unregisterReceiver(homeButtonReceiver);
                            homeButtonReceiver = null;
                            mFilter = null;
                        }
                        if (mReceiver != null) {
                            ctx.unregisterReceiver(mReceiver);
                            mReceiver = null;
                        }

                        if (batteryInfoReceiver != null) {
                            getActivity().unregisterReceiver(batteryInfoReceiver);
                            batteryInfoReceiver = null;
                        }

                        /**
                         * Unregister Jack Receiver
                         * */
                        if (mReceiverJack != null) {
                            ctx.unregisterReceiver(mReceiverJack);
                        }
                        sensorManager.unregisterListener(LightSensorListener);


                        /**
                         * Unregister SensorManager of Proximity Sensor
                         * */

                        sensorManager.unregisterListener(mSensorListener);
                        replaceFragment(R.id.container, new ManualTestFragment((InterfaceButtonTextChange) getActivity()), FragmentTag.MANUAL_TEST_FRAGMENT.name(), false);
                    }
                });
        // Changing message text color
        snackbar.setActionTextColor(Color.WHITE);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    /**
     * Head Jack Callback
     */
    @Override
    public void onBroadcastCallBack(boolean isPower, String status) {
        if (isPower) {
            utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_18.name(), AsyncConstant.TEST_PASS);
            utils.showToast(ctx, getResources().getString(R.string.txtJackPort));
            mtxtViewJack.setTextColor(getResources().getColor(R.color.green_color));
            isJackTestPerformes = true;
           /* if (mReceiverJack != null) {
                ctx.unregisterReceiver(mReceiverJack);
            }*/
        } else {
            utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_18.name(), AsyncConstant.TEST_FAILED);
        }
    }

    public void dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_UP) {
                    if (event.getEventTime() - event.getDownTime() > ViewConfiguration.getLongPressTimeout()) {
                        //TODO long click action
                        //  utils.showToast(ctx, getResources().getString(R.string.VolumeUp));
                    } else {
                        //TODO click action
                        //   utils.showToast(ctx, getResources().getString(R.string.VolumeUp));
                    }
                    if (!isVolIncrease) {

                        utils.showToast(ctx, getResources().getString(R.string.txtVolumeUp));
                        isVolIncrease = true;
                    }

                    if (isVolIncrease && isVolDecrease) {
                        mtxtViewVolume.setTextColor(getResources().getColor(R.color.green_color));
                    } else {
                        mtxtViewVolume.setTextColor(getResources().getColor(R.color.yellow_font_Color));
                    }
                    //updateTestValues();
                //    utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_20.name(), AsyncConstant.TEST_PASS);
                } else {
                    isVolIncrease = false;
                    //    mCallBack.onChangeText(utils.BUTTON_SKIP, true);
               //     utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_20.name(), AsyncConstant.TEST_FAILED);
                }
                break;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_UP) {
                    if (event.getEventTime() - event.getDownTime() > ViewConfiguration.getLongPressTimeout()) {
                        //TODO long click action
                        // utils.showToast(ctx, getResources().getString(R.string.VolumeDown));
                    } else {
                        //TODO click action
                        // utils.showToast(ctx, getResources().getString(R.string.VolumeDown));
                    }
                    if (!isVolDecrease) {
                        isVolDecrease = true;
                        utils.showToast(ctx, getResources().getString(R.string.txtVolumeDown));
                    }
                    if (isVolIncrease && isVolDecrease) {
                        mtxtViewVolume.setTextColor(getResources().getColor(R.color.green_color));
                    } else {
                        mtxtViewVolume.setTextColor(getResources().getColor(R.color.yellow_font_Color));
                    }
                    utils.showToast(ctx, getResources().getString(R.string.txtVolumeDown));
                  //  updateTestValues();
                  //  utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_21.name(), AsyncConstant.TEST_PASS);
                } else {
                    isVolDecrease = false;
                    //   mCallBack.onChangeText(utils.BUTTON_SKIP, true);
                  //  utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_21.name(), AsyncConstant.TEST_FAILED);
                }
                break;
        }
    }

//    private void updateTestValues() {
//        if (isVolIncrease && isVolDecrease) {
//            utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_20.name(), AsyncConstant.TEST_PASS);
//            utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_21.name(), AsyncConstant.TEST_PASS);
//        } else {
//            if (isVolIncrease) {
//                utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_20.name(), AsyncConstant.TEST_PASS);
//                utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_21.name(), AsyncConstant.TEST_FAILED);
//            } else if (isVolDecrease) {
//                utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_20.name(), AsyncConstant.TEST_FAILED);
//                utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_21.name(), AsyncConstant.TEST_PASS);
//            } else {
//                utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_20.name(), AsyncConstant.TEST_FAILED);
//                utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_21.name(), AsyncConstant.TEST_FAILED);
//            }
//        }
//    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // The Proximity sensor returns a single value either 0 or 5(also 1 depends on Sensor manufacturer).
        // 0 for near and 5 for far
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            if (event.values[0] == 0) {
               /* count++;
                if (count >= 2) {*/
               // utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_23.name(), AsyncConstant.TEST_PASS);
                utils.showToast(ctx, getResources().getString(R.string.txtManualProximityWorking));
                mtxtViewProximity.setTextColor(getResources().getColor(R.color.green_color));
                isProximityTestPerformed = true;
                /**
                 * Unregister SensorManager of Proximity Sensor
                 * */
                sensorManager.unregisterListener(this);
            /*    }*/
                /*sensorManager.unregisterListener(mSensorListener);*/

            } else {
                if (isShowToast) {
                    count++;
                    if (count >= 2) {
                        isProximityTestPerformed = true;
                        utils.showToast(ctx, getResources().getString(R.string.txtManualProximityWorking));
                        mtxtViewProximity.setTextColor(getResources().getColor(R.color.green_color));
                      //  utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_23.name(), AsyncConstant.TEST_PASS);
                        /**
                         * Unregister SensorManager of Proximity Sensor
                         * */
                        sensorManager.unregisterListener(this);
                        /*sensorManager.unregisterListener(mSensorListener);*/
                    }
                } else {
                    isShowToast = true;
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onHomePressed() {
        utils.showToastLong(ctx, getResources().getString(R.string.txtManualHomeTestWait));
        isHomeButtonTested = true;
        isHomeButtonDone = true;
      //  utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_26.name(), AsyncConstant.TEST_PASS);
        reopenScreen();
    }

    @Override
    public void onHomeLongPressed() {
        isHomeButtonTested = true;
        isHomeButtonDone = true;
        utils.showToastLong(ctx, getResources().getString(R.string.txtManualHomeTestWait));
      //  utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_26.name(), AsyncConstant.TEST_PASS);
        reopenScreen();
    }

    /**
     * Light Sensor
     */
    private final SensorEventListener LightSensorListener = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
                if (event.values[0] > 500) {
                   // utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_28.name(), AsyncConstant.TEST_PASS);
                    isLightSensorPerformed = true;
                    sensorManager.unregisterListener(LightSensorListener);
                    if (lightCount == 0) {
                        lightCount++;
                        utils.showToast(ctx, getResources().getString(R.string.txtLightSensorPass));
                    }
                    mtxtViewLightSensor.setTextColor(getResources().getColor(R.color.green_color));
                }
            }
        }
    };

    public void intializeVibrator() {
        vibrator = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void startVibrate() {
        vibrator.vibrate(pattern, -1);
    }

    public void stopVibrate() {
        vibrator.cancel();
    }

    @Override
    public void onPowerButtonCallBack(boolean isPower, String status) {

        if (isPower) {
            isPowerTestPerformed = true;
            utils.showToast(ctx, getResources().getString(R.string.txtPowerPass));
            mtxtViewPower.setTextColor(getResources().getColor(R.color.green_color));
          //  utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_45.name(), AsyncConstant.TEST_PASS);
        } else {
            isPowerTestPerformed = true;
           // utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_45.name(), AsyncConstant.TEST_FAILED);
        }

    }

    /**
     * Mutitouch Test Canvas.
     */
    public class MultitouchView extends View {
        private static final int SIZE = 60;
        private Context mcontext;

        private SparseArray<PointF> mActivePointers;
        private Paint mPaint;
        private int[] colors = {Color.BLUE, Color.GREEN, Color.MAGENTA,
                Color.BLACK, Color.CYAN, Color.GRAY, Color.RED, Color.DKGRAY,
                Color.LTGRAY, Color.YELLOW};

        private Paint textPaint;
        private boolean isDialogShow = true;
        private boolean isShowToast = false;
        private Utilities utils;
        Handler handler;

        public MultitouchView(Context context, AttributeSet attrs) {
            super(context, attrs);
            mcontext = context;
            utils = Utilities.getInstance(context);
            initView();
        }

        private void initView() {
            /*utils.showToast(mcontext, getResources().getString(R.string.txtManualMititouchToast));*/
            mActivePointers = new SparseArray<PointF>();
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            // set painter color to a color you like
            mPaint.setColor(Color.BLUE);
            mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            textPaint.setTextSize(20);
            handler = new Handler();
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {

            // get pointer index from the event object
            int pointerIndex = event.getActionIndex();

            // get pointer ID
            int pointerId = event.getPointerId(pointerIndex);

            // get masked (not specific to a pointer) action
            int maskedAction = event.getActionMasked();

            switch (maskedAction) {
                case MotionEvent.ACTION_DOWN:
                    if (mActivePointers.size() < 3)
                        isShowToast = true;
                case MotionEvent.ACTION_POINTER_DOWN: {
                    // We have a new pointer. Lets add it to the list of pointers

                    PointF f = new PointF();
                    f.x = event.getX(pointerIndex);
                    f.y = event.getY(pointerIndex);
                    mActivePointers.put(pointerId, f);
                    break;
                }
                case MotionEvent.ACTION_MOVE: { // a pointer was moved
                    for (int size = event.getPointerCount(), i = 0; i < size; i++) {
                        PointF point = mActivePointers.get(event.getPointerId(i));
                        if (point != null) {
                            point.x = event.getX(i);
                            point.y = event.getY(i);
                        }
                    }
                    break;
                }
                case MotionEvent.ACTION_UP:

                case MotionEvent.ACTION_POINTER_UP:
                    checkClick();
                case MotionEvent.ACTION_CANCEL: {
                    mActivePointers.remove(pointerId);
                    break;
                }
            }
            invalidate();
            return true;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            // draw all pointers
            for (int size = mActivePointers.size(), i = 0; i < size; i++) {
                PointF point = mActivePointers.valueAt(i);
                if (point != null)
                    mPaint.setColor(colors[i % 9]);
                canvas.drawCircle(point.x, point.y, SIZE, mPaint);
            }
/*canvas.drawText("Total pointers: " + mActivePointers.size(), 10, 40, textPaint);*/
            if (isDialogShow) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        checkClick();
                    }
                }, 2000);
            }
        }

        public void checkClick() {
            if (mActivePointers.size() > 1) {
                if (isDialogShow) {
                    isDialogShow = true;
                    isShowToast = false;
                    if (count < 2) {
                        count++;
                    /*showDialog(mcontext, mActivePointers.size(), mcontext.getResources().getString(R.string.textNext));*/
                      /*  utils.showToast(mcontext, getResources().getString(R.string.txtManualMititouchCongratsToast));*/
                    }
                    parentLayout.removeView(multitouchView);
                    isMultitouchPerformed = true;
               //     utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_22.name(), AsyncConstant.TEST_PASS);
                    utils.showToast(ctx, getResources().getString(R.string.txtMutitouchPass));
                    mtxtViewMutitouch.setTextColor(getResources().getColor(R.color.green_color));
                }
            } else {
                if (isShowToast) {
                   // utils.showToast(mcontext, getResources().getString(R.string.txtManualMititouchRetryToast));
                    isShowToast = false;
                }
            }
        }
    }
}
