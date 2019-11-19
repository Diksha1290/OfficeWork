package com.officework.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.officework.R;
import com.officework.base.BaseFragment;
import com.officework.broadcastReceivers.BatteryReceiver;
import com.officework.constants.AppConstantsTheme;
import com.officework.interfaces.InterfaceBatteryCallback;
import com.officework.utils.ExceptionLogUtils;
import com.officework.utils.Utilities;

import java.text.DecimalFormat;
import java.util.Locale;

/**
 * Created by Girish on 7/29/2016.
 */
public class BatteryInfoFragment extends BaseFragment implements InterfaceBatteryCallback {
    View view;
    Utilities utils;
    Context ctx;

    /*	Status*/
    TextView mtvLevel, mtvStatus, mtvTemperature,statusHeading;
    /*	Information*/
    TextView mtvTechnology, mtvHealth, mtvVoltage, mtvCurrent, mtvCapacity,informationHeading;
    BatteryReceiver batteryInfoReceiver;

    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        try {
            if (view == null) {
                view = inflater.inflate(R.layout.fragment_battery, null);
                utils = Utilities.getInstance(getActivity());
                ctx = getActivity();
                batteryInfoReceiver = new BatteryReceiver((InterfaceBatteryCallback) this);
            /*getActivity().registerReceiver(this.batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));*/
                getActivity().registerReceiver(this.batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                initViews();
             //   Crashlytics.getInstance().log(FragmentTag.BATTERY_INFO_FRAGMENT.name());
            }
            return view;
        } catch (Exception e) {
            logException(e, "BatteryInfoFragment_initUI()");
            return null;
        }

    }

    public BatteryInfoFragment() {
    }

    private void initViews() {


        try {
            mtvLevel = (TextView) view.findViewById(R.id.tvLevel);
            mtvStatus = (TextView) view.findViewById(R.id.tvStatus);
            mtvTemperature = (TextView) view.findViewById(R.id.tvTemperature);

        /*	Information*/
            mtvTechnology = (TextView) view.findViewById(R.id.tvTechnology);
            mtvHealth = (TextView) view.findViewById(R.id.tvHealth);
            mtvVoltage = (TextView) view.findViewById(R.id.tvVoltage);
            mtvCurrent = (TextView) view.findViewById(R.id.tvCurrent);
            mtvCapacity = (TextView) view.findViewById(R.id.tvCapacity);
            statusHeading=(TextView)view.findViewById(R.id.statusHeading);
            informationHeading=(TextView)view.findViewById(R.id.informationheading);
//            int color= Color.parseColor(new ThemeManager(getActivity()).getTheme());
            int color= AppConstantsTheme.getIconColor();
            statusHeading.setTextColor(color);
            informationHeading.setTextColor(color);
        } catch (Exception e) {
            logException(e, "BatteryInfoFragment_initViews()");
        }
        /*	Status*/

    }

    /**
     * This will get the battery voltage and show user in a particular format.
     *
     * @param intent
     */
    public String getBatteryVoltage(Intent intent) {
        try {
            String batteryVolt = "0";
            // Get the battery voltage
            // Current battery voltage in Millivolts
            int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);

            // Convert Millivolts to Volts
            double volt = voltage * 0.001;

            // Initialize a new DecimalFormat instance
        /*DecimalFormat newFormat = new DecimalFormat("#.###");*/
            DecimalFormat newFormat = (DecimalFormat) DecimalFormat.getInstance(Locale.ENGLISH);
            newFormat.applyPattern("#.###");

            // Format the decimal value to one decimal position
            double Volt = Double.valueOf(newFormat.format(volt));
            batteryVolt = String.valueOf(Volt + " " + getResources().getString(R.string.txtVoltageV));
            return batteryVolt;
        } catch (Exception e) {
            logException(e, "BatteryInfoFragment_getBatteryVoltage()");
            return "0";
        }

    }

    /*Battery Status*/
    private String getBatteryStatusString(int status, int PlugInType) {
        try {
            String statusString = getResources().getString(R.string.txtUnknown);
            switch (status) {
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    statusString = getResources().getString(R.string.txtStatusCharging) + " " + getBatteryPlugTypeString(PlugInType);
                    break;
                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                    statusString = getResources().getString(R.string.txtStatusDischarging);
                    break;
                case BatteryManager.BATTERY_STATUS_FULL:
                    statusString = getResources().getString(R.string.txtStatusFull);
                    break;
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                    statusString = getResources().getString(R.string.txtStatusNot_Charging);
                    break;
            }
            return statusString;
        } catch (Exception e) {
            logException(e, "BatteryInfoFragment_getBatteryStatusString()");
            return getResources().getString(R.string.txtUnknown);
        }

    }

    /*Health Type*/
    public static String getBatteryHealthString(int health, Context ctx) {
        try {
            String healthString = ctx.getResources().getString(R.string.txtUnknown);
            switch (health) {
                case BatteryManager.BATTERY_HEALTH_DEAD:
                    healthString = ctx.getResources().getString(R.string.txtHealthDead);
                    break;
                case BatteryManager.BATTERY_HEALTH_GOOD:
                    healthString = ctx.getResources().getString(R.string.txtHealthGood);
                    break;
                case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                    healthString = ctx.getResources().getString(R.string.txtHealthOver_Voltage);
                    break;
                case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                    healthString = ctx.getResources().getString(R.string.txtHealthOver_Heat);
                    break;
                case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                    healthString = ctx.getResources().getString(R.string.txtHealthFailure);
                    break;
            }
            return healthString;
        } catch (Exception e) {
            BatteryInfoFragment batteryInfoFragment = new BatteryInfoFragment();
            batteryInfoFragment.logException(e, "BatteryInfoFragment_getBatteryHealthString()");
            return ctx.getResources().getString(R.string.txtUnknown);
        }


    }

    /*Plug Type*/
    public String getBatteryPlugTypeString(int plugged) {
        try {
            String plugType = getResources().getString(R.string.txtUnknown);
            switch (plugged) {
                case BatteryManager.BATTERY_PLUGGED_AC:
                    plugType = getResources().getString(R.string.txtPlugInTypeAC);
                    break;
                case BatteryManager.BATTERY_PLUGGED_USB:
                    plugType = getResources().getString(R.string.txtPlugInTypeUSB);
                    break;
            }
            return plugType;
        } catch (Exception e) {
            logException(e, "BatteryInfoFragment_getBatteryPlugTypeString()");
            return ctx.getResources().getString(R.string.txtUnknown);
        }

    }

    /**
     * To fetch the Capacity of a battery.
     */
    public String getBatteryCapacity() {

        try {
            Object mPowerProfile_ = null;
            final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";

            try {

                mPowerProfile_ = Class.forName(POWER_PROFILE_CLASS)
                        .getConstructor(Context.class).newInstance(getActivity());
            } catch (Exception e) {
                e.printStackTrace();
            //    Crashlytics.getInstance().log(FragmentTag.BATTERY_INFO_FRAGMENT.name());
            }
            double batteryCapacity = 0;
            try {
                batteryCapacity = (Double) Class
                        .forName(POWER_PROFILE_CLASS)
                        .getMethod("getAveragePower", java.lang.String.class)
                        .invoke(mPowerProfile_, "battery.capacity");
            } catch (Exception e) {
                e.printStackTrace();
              //  Crashlytics.getInstance().log(FragmentTag.BATTERY_INFO_FRAGMENT.name());
            }
            String battery_Capacity = String.valueOf(batteryCapacity);
            String battery[] = battery_Capacity.split("\\.");
            return battery[0] + " mAh";
        } catch (Exception e) {
            logException(e, "BatteryInfoFragment_getBatteryCapacity()");
            return ctx.getResources().getString(R.string.txtUnknown);
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (batteryInfoReceiver != null)
                getActivity().unregisterReceiver(this.batteryInfoReceiver);
        } catch (Exception e) {
            logException(e, "BatteryInfoFragment_onDestroy()");
        }

    }

    @Override
    public void onBatteryCallBack(Intent intent) {
        try {
            int icon_small = intent.getIntExtra(BatteryManager.EXTRA_ICON_SMALL, 0);
            boolean present = intent.getExtras().getBoolean(BatteryManager.EXTRA_PRESENT);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);

            mtvLevel.setText(intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0) + "%");
            mtvStatus.setText(getBatteryStatusString(intent.getIntExtra(BatteryManager.EXTRA_STATUS, 0), intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)));
            mtvTemperature.setText(String.valueOf(intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10) + "\u2103");
            mtvTechnology.setText(intent.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY));
            mtvHealth.setText(getBatteryHealthString(intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0), ctx));
            mtvVoltage.setText(getBatteryVoltage(intent));
            mtvCurrent.setText(getResources().getString(R.string.txtNotAvailable));
            mtvCapacity.setText(getBatteryCapacity());
        } catch (Exception e) {
            logException(e, "BatteryInfoFragment_onBatteryCallBack()");
        }

    }

    public void logException(Exception e, String methodName) {
        try {
            Utilities utilities = Utilities.getInstance(getActivity());
            Activity activity = getActivity();
            Context context = getActivity();
            ExceptionLogUtils exceptionLogUtils = new ExceptionLogUtils(utilities, context, activity);
            exceptionLogUtils.addExceptionLog(utilities, context, activity, null, null, e, methodName);
        } catch (Exception exp) {
            //logException(exp, "BatteryInfoFragment_logException()");
        }

    }
}
