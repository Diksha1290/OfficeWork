package com.officework.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jaredrummler.android.device.DeviceName;
import com.officework.R;
import com.officework.base.BaseFragment;
import com.officework.constants.AppConstantsTheme;
import com.officework.constants.JsonTags;
import com.officework.utils.ExceptionLogUtils;
import com.officework.utils.TelephonyInfo;
import com.officework.utils.Utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Girish on 7/29/2016.
 */
public class SystemInfoFragment extends BaseFragment {
    View view;
    Utilities utils;
    Context ctx;
    /*	Device*/
    TextView mtvModel, mtvCodename, mtvManufacturer, mtvSerialNumber, mtvBootLoader, mtvRadio, mtvIMEI, mtvIMEI2, mtvDeviceID,systemHeading;
    /*	Operating System*/
    TextView mtvRootAccess, mtvBusyBox, mtvAndroidVersion, mtvBuild, mtvSDK, mtvKernel, mtvOsName,operatingSystemHeading;

    //Ashwani System Device
    TelephonyManager telephonyManager;

    //Ashwani Operating System
    String myDeviceBuid;
    String FILENAME_PROC_VERSION = "/proc/version";

    final public int REQUEST_CODE_ASK_PERMISSIONS = 123;

    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        try {
            if (view == null) {
                view = inflater.inflate(R.layout.fragment_system, null);
                utils = Utilities.getInstance(getActivity());
                ctx = getActivity();
             //   Crashlytics.getInstance().log(FragmentTag.SYSTEM_INFO_FRAGMENT.name());
                initViews();
            }
            return view;
        } catch (Exception e) {
            logException(e, "SystemInfoFragment_initUI()");
            return null;
        }

    }

    public SystemInfoFragment() {
    }

    /**
     * Initialize view
     */
    private void initViews() {
        try {
            /*	Device*/
            mtvModel = (TextView) view.findViewById(R.id.tvModel);
            mtvCodename = (TextView) view.findViewById(R.id.tvCodeName);
            mtvManufacturer = (TextView) view.findViewById(R.id.tvManufacturer);
            mtvSerialNumber = (TextView) view.findViewById(R.id.tvSerialNumber);
            mtvBootLoader = (TextView) view.findViewById(R.id.tvBootLoader);
            mtvRadio = (TextView) view.findViewById(R.id.tvRadio);
            mtvIMEI = (TextView) view.findViewById(R.id.tvIMEI);
            mtvIMEI2 = (TextView) view.findViewById(R.id.tvIMEI2);
            mtvDeviceID = (TextView) view.findViewById(R.id.tvDeviceID);

        /*	Operating System*/
            mtvOsName = (TextView) view.findViewById(R.id.tvOsName);
            mtvRootAccess = (TextView) view.findViewById(R.id.tvRootAccess);
            mtvBusyBox = (TextView) view.findViewById(R.id.tvBusyBox);
            mtvAndroidVersion = (TextView) view.findViewById(R.id.tvAndroidVersion);
            mtvBuild = (TextView) view.findViewById(R.id.tvBuild);
            mtvSDK = (TextView) view.findViewById(R.id.tvSDK);
            mtvKernel = (TextView) view.findViewById(R.id.tvKernel);
//            int color= Color.parseColor(new ThemeManager(getActivity()).getTheme());
            int color= AppConstantsTheme.getIconColor();
            systemHeading=(TextView)view.findViewById(R.id.systemHeading);
            operatingSystemHeading=(TextView)view.findViewById(R.id.operatingSystemHeading);
            systemHeading.setTextColor(color);
            operatingSystemHeading.setTextColor(color);
            checkRuntimePermisson();
        } catch (Exception e) {
            logException(e, "SystemInfoFragment_initViews()");
        }

    }


    /**
     * This will check weather has READ_PHONE_STATE permission or not.
     * ON the devices above API level 23
     */
    private void checkRuntimePermisson() {
        try {
            int hasWritePhoneReadPermission = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                hasWritePhoneReadPermission = getActivity().checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
                if (hasWritePhoneReadPermission != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
                            REQUEST_CODE_ASK_PERMISSIONS);
                    return;
                }
            }
            deviceInfo(true);
            osInfo(true);
        } catch (Exception e) {
            logException(e, "SystemInfoFragment_checkRuntimePermisson()");
        }

    }




    /**
     * fetch device OS name
     *
     * @return
     */
    private String getAndroidOSName() {
        try {
            String fieldName = "";
            Field[] fields = Build.VERSION_CODES.class.getFields();
            for (Field field : fields) {
                fieldName = field.getName();
            }
            return fieldName;
        } catch (Exception e) {
            logException(e, "SystemInfoFragment_getAndroidOSName()");
            return "";
        }

    }

    /**
     * This will fetch device basic information and set in the controls.
     * OS Section
     *
     * @param permission
     */
    @SuppressLint("MissingPermission")
    private void deviceInfo(boolean permission) {

        try {
            DeviceName.with(getActivity()).request(new DeviceName.Callback() {

                @Override public void onFinished(DeviceName.DeviceInfo info, Exception error) {
                    String manufacturer = info.manufacturer;  // "Samsung"
                    String name = info.marketName;            // "Galaxy S8+"
                    String model = info.model;                // "SM-G955W"
                    String codename = info.codename;          // "dream2qltecan"
                    String deviceName = info.getName();       // "Galaxy S8+"
                    // FYI: We are on the UI thread.

                    if(model==null){
                        mtvModel.setText(Build.MODEL);
                        utils.compare_UpdateSecurePreferenceString(ctx, JsonTags.MMR_5.name(), "|"+Build.MODEL);

                    }else {
                        if (model.equalsIgnoreCase(name)) {
                            mtvModel.setText(model);
                            utils.compare_UpdateSecurePreferenceString(ctx, JsonTags.MMR_5.name(), "|"+model);

                        } else {
                            mtvModel.setText(manufacturer + " " + name + " | " + model);
                            utils.compare_UpdateSecurePreferenceString(ctx, JsonTags.MMR_5.name(),manufacturer + " " + name + "|" + model);

                        }

                    }
                }
            });



            mtvManufacturer.setText(Build.MANUFACTURER);
            mtvRadio.setText(Build.getRadioVersion());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mtvSerialNumber.setText(Build.getSerial());

            }
           else{
            mtvSerialNumber.setText(Build.SERIAL);
            }
            mtvBootLoader.setText(Build.BOOTLOADER);
            mtvDeviceID.setText(Settings.Secure.getString(getContext().getContentResolver(),
                    Settings.Secure.ANDROID_ID));
            mtvCodename.setText(Build.DEVICE);
        /*telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);*/
            if (permission) {
                if ((TelephonyInfo.getInstance(getActivity()).getImsiSIM1()) != null) {
                    mtvIMEI.setText(TelephonyInfo.getInstance(getActivity()).getImsiSIM1());
                } else {
                    mtvIMEI.setText(getResources().getString(R.string.textNotAvailable));
                }

                if ((TelephonyInfo.getInstance(getActivity()).getImsiSIM2()) != null) {
                    mtvIMEI2.setText(TelephonyInfo.getInstance(getActivity()).getImsiSIM2());
                } else {
                    mtvIMEI2.setText(getResources().getString(R.string.textNotAvailable));
                }
            } else {
                mtvIMEI.setText(getResources().getString(R.string.txtPermissionDenied));
                mtvIMEI2.setText(getResources().getString(R.string.txtPermissionDenied));
            }
        } catch (Exception e) {
            logException(e, "SystemInfoFragment_deviceInfo()");
        }

    }

   /* public static String serialNumber() {
        String serial = null;

        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");
            return serial;
        } catch (Exception ignored) {
        }
        return "";
    }

    public static String getManufacturerSerialNumber() {
        String serial = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            serial = (String) get.invoke(c, "ril.serialnumber", "unknown");
        } catch (Exception ignored) {}
        return serial;
    }*/


    public static String getIMEINUMBER(Activity activity) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
            return ((TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        } catch (Exception e) {
            SystemInfoFragment systemInfoFragment = new SystemInfoFragment();
            systemInfoFragment.logException(e, "SystemInfoFragment_getIMEINUMBER()");

            return "";
        }

    }

    /**
     * This will fetch the Android OS Info and show to the user.
     *
     * @param permission
     */
    private void osInfo(boolean permission) {
        try {
            mtvAndroidVersion.setText(Build.VERSION.RELEASE);
            mtvOsName.setText(getAndroidOSName());
            mtvSDK.setText(Build.VERSION.SDK);
            mtvKernel.setText(getFormattedKernelVersion());
            //Device build
            mtvBuild.setText(getMobileBuild());
            //root check
            mtvRootAccess.setText(getRootStatus());
        } catch (Exception e) {
            logException(e, "SystemInfoFragment_osInfo()");
        }

    }

    /**
     * This method return the Mobile Build
     *
     * @return String
     */

    private String getMobileBuild() {
        try {
            myDeviceBuid = Build.FINGERPRINT;
            String forwardSlash = "/";
            String osReleaseVersion = Build.VERSION.RELEASE + forwardSlash;
            myDeviceBuid = myDeviceBuid.substring(myDeviceBuid.indexOf(osReleaseVersion));  //"5.1.1/LMY48Y/2364368:user/release-keys”
            myDeviceBuid = myDeviceBuid.replace(osReleaseVersion, "");  //"LMY48Y/2364368:user/release-keys”
            myDeviceBuid = myDeviceBuid.substring(0, myDeviceBuid.indexOf(forwardSlash));
            return myDeviceBuid;//"LMY48Y"
        } catch (Exception e) {
            utils.addLog(ctx, getClass().getName(), "Exception");
            logException(e, "SystemInfoFragment_getMobileBuild()");
            return null;
        }

    }

    /**
     * This method return the root status
     *
     * @return String
     */
    private String getRootStatus() {
        try {
            boolean rootcheckstatus = isDeviceRooted();
            if (rootcheckstatus == true) {
                return (getResources().getString(R.string.txtPermissionRoot));
            } else {
                return (getResources().getString(R.string.txtPermissionRootNot));
            }
        } catch (Exception e) {
            logException(e, "SystemInfoFragment_getRootStatus()");
            return "";
        }

    }

    /**
     * This method return the kernal information
     *
     * @return String
     */
    private String getFormattedKernelVersion() {
        String procVersionStr;
        try {
            procVersionStr = readLine(FILENAME_PROC_VERSION);
            final String PROC_VERSION_REGEX =
                    "\\w+\\s+" + /* ignore: Linux */
                            "\\w+\\s+" + /* ignore: version */
                            "([^\\s]+)\\s+" + /* group 1: 2.6.22-omap1 */
                            "\\(([^\\s@]+(?:@[^\\s.]+)?)[^)]*\\)\\s+" + /* group 2: (xxxxxx@xxxxx.constant) */
                            "\\((?:[^(]*\\([^)]*\\))?[^)]*\\)\\s+" + /* ignore: (gcc ..) */
                            "([^\\s]+)\\s+" + /* group 3: #26 */
                            "(?:PREEMPT\\s+)?" + /* ignore: PREEMPT (optional) */
                            "(.+)"; /* group 4: date */
            Pattern p = Pattern.compile(PROC_VERSION_REGEX);
            Matcher m = p.matcher(procVersionStr);
            if (!m.matches()) {

                return getResources().getString(R.string.txtUnavailable);
            } else if (m.groupCount() < 4) {

                return getResources().getString(R.string.txtUnavailable);
            } else {
                return (new StringBuilder(m.group(1)).append("\n").append(
                        m.group(2)).append(" ").append(m.group(3)).append("\n")
                        .append(m.group(4))).toString();
            }
        } catch (IOException e) {
            logException(e, "SystemInfoFragment_getFormattedKernelVersion()");
            return getResources().getString(R.string.txtUnavailable);
        }
    }

    private String readLine(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename), 256);
        try {
            return reader.readLine();
        } finally {
            reader.close();
        }
    }
    // kernal info ends

    /**
     * This method return the root status
     *
     * @return String
     */
    public static boolean isDeviceRooted() {
        try {
            return checkRootMethod1() || checkRootMethod2() || checkRootMethod3();
        } catch (Exception e) {
            SystemInfoFragment systemInfoFragment = new SystemInfoFragment();
            systemInfoFragment.logException(e, "SystemInfoFragment_isDeviceRooted()");
            return false;
        }

    }

    private static boolean checkRootMethod1() {
        try {
            String buildTags = Build.TAGS;
            return buildTags != null && buildTags.contains("test-keys");
        } catch (Exception e) {
            SystemInfoFragment systemInfoFragment = new SystemInfoFragment();
            systemInfoFragment.logException(e, "SystemInfoFragment_checkRootMethod1()");
            return false;
        }

    }

    private static boolean checkRootMethod2() {
        try {
            String[] paths = {"/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
                    "/system/bin/failsafe/su", "/data/local/su"};
            for (String path : paths) {
                if (new File(path).exists()) return true;
            }
            return false;
        } catch (Exception e) {
            SystemInfoFragment systemInfoFragment = new SystemInfoFragment();
            systemInfoFragment.logException(e, "SystemInfoFragment_checkRootMethod2()");
            return false;
        }

    }

    private static boolean checkRootMethod3() {
        try {
            Process process = null;
            try {
                process = Runtime.getRuntime().exec(new String[]{"/system/xbin/which", "su"});
                BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
                if (in.readLine() != null) return true;
                return false;
            } catch (Throwable t) {
                return false;
            } finally {
                if (process != null) process.destroy();
            }
        } catch (Exception e) {
            SystemInfoFragment systemInfoFragment = new SystemInfoFragment();
            systemInfoFragment.logException(e, "SystemInfoFragment_checkRootMethod3()");
            return false;
        }

    }
    //Root Check End

    /**
     * This method check the runtime permission
     *
     * @return result
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        try {
            switch (requestCode) {
                case REQUEST_CODE_ASK_PERMISSIONS:
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // Permission Granted
                        deviceInfo(true);
                        osInfo(true);
                    } else {
                        // Permission Denied
                        deviceInfo(false);
                        osInfo(false);
                    }
                    break;
                default:
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        } catch (Exception e) {
            logException(e, "SystemInfoFragment_onRequestPermissionsResult()");
        }

    }


   /* public static String serialNumber() {
        String serial = null;

        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");
            return serial;
        } catch (Exception ignored) {
        }
        return "";
    }

    */

    /**
     * used to find serial number from galaxy samsung
     *
     * @return
     *//*
    public static String getManufacturerSerialNumber() {
        String serial = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            serial = (String) get.invoke(c, "ril.serialnumber", "unknown");
        } catch (Exception ignored) {}
        return serial;
    }*/
    public void logException(Exception e, String methodName) {
        try {
            Utilities utilities = Utilities.getInstance(getActivity());
            Activity activity = getActivity();
            Context context = getActivity();
            ExceptionLogUtils exceptionLogUtils = new ExceptionLogUtils(utilities, context, activity);
            exceptionLogUtils.addExceptionLog(utilities, context, activity, null, null, e, methodName);
        } catch (Exception exp) {
       //     logException(exp, "SystemInfoFragment_logException()");
        }

    }
}
