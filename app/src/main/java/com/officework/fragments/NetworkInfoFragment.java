package com.officework.fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.officework.R;
import com.officework.base.BaseFragment;
import com.officework.constants.AppConstantsTheme;
import com.officework.utils.ExceptionLogUtils;
import com.officework.utils.Utilities;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

/**
 * Created by Girish on 7/29/2016.
 */
public class NetworkInfoFragment extends BaseFragment {
    View view;
    Utilities utils;
    Context ctx;
    final public int REQUEST_CODE_ASK_PERMISSIONS = 123;
    final public int REQUEST_CODE_ASK_PERMISSIONS_CAMERA = 1235;

    /*	Wifi*/
    TextView mtvStatus, mtvNetwork, mtvBSSID, mtvDHCPServer, mtvGateway, mIPAddress, mtvLinkSpeed, mtvMacAddress, mtvSignalStrength, mtvPublicIP,wifiHeading;
    /*	Mobile*/
    TextView mtvStatusMobile, mtvOperator, mtvOperatorCode, mtvRoaming, mtvPhoneType, mtvNetworkType,mobileHeading;
    //Telephone Manager
    TelephonyManager telMgr;


    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_network, null);
            utils = Utilities.getInstance(getActivity());
            ctx = getActivity();
         //   Crashlytics.getInstance().log(FragmentTag.NETWORK_INFO_FRAGMENT.name());
            initViews();
        }
        return view;
    }

    public NetworkInfoFragment() {
    }

    /**
     * Initialize view
     */
    private void initViews() {
        try {
            /*	Wifi*/
            mtvStatus = (TextView) view.findViewById(R.id.tvStatus);
            mtvNetwork = (TextView) view.findViewById(R.id.tvNetwork);
            mtvBSSID = (TextView) view.findViewById(R.id.tvBSSID);
            mtvDHCPServer = (TextView) view.findViewById(R.id.tvDHCPServer);
            mtvGateway = (TextView) view.findViewById(R.id.tvGateway);
            mIPAddress = (TextView) view.findViewById(R.id.tvIPAdress);
            mtvLinkSpeed = (TextView) view.findViewById(R.id.tvLinkSpeed);
            mtvMacAddress = (TextView) view.findViewById(R.id.tvMacAddress);
            mtvSignalStrength = (TextView) view.findViewById(R.id.tvSignalStrength);
            mtvPublicIP = (TextView) view.findViewById(R.id.tvPublicIP);

            /*	Mobile*/
            mtvStatusMobile = (TextView) view.findViewById(R.id.tvStatusMobile);
            mtvOperator = (TextView) view.findViewById(R.id.tvOperator);
            mtvOperatorCode = (TextView) view.findViewById(R.id.tvOperatorCode);
            mtvRoaming = (TextView) view.findViewById(R.id.tvRoaming);
            mtvPhoneType = (TextView) view.findViewById(R.id.tvPhoneType);
            mtvNetworkType = (TextView) view.findViewById(R.id.tvNetworkType);
            wifiHeading=(TextView)view.findViewById(R.id.wifiheading);
            mobileHeading=(TextView)view.findViewById(R.id.mobileHeading);
//            int color= Color.parseColor(new ThemeManager(getActivity()).getTheme());
            int color= AppConstantsTheme.getIconColor();
            wifiHeading.setTextColor(color);
            mobileHeading.setTextColor(color);
            checkRuntimePermisson(2);
        } catch (Exception e) {
            logException(e, "NetworkInfoFragment_initViews()");
        }

    }


    /**
     * This will check weather has ACCESS_COARSE_LOCATION and ACCESS_FINE_LOCATION permission or not.
     * ON the devices above API level 23
     */
    private void checkRuntimePermisson(int which) {
        try {
            int hasCameraPermission = 0;
            int hasWriteExternalStoragePermission = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                hasCameraPermission = getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
                hasWriteExternalStoragePermission = getActivity().checkSelfPermission(Manifest.permission.CAMERA);
                switch (which) {
                    case 0:
                        if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_CODE_ASK_PERMISSIONS);
                            return;
                        }
                        break;
                    case 1:
                        if (hasWriteExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.CAMERA},
                                    REQUEST_CODE_ASK_PERMISSIONS_CAMERA);
                            return;
                        }
                        break;

                    case 2:
                        if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_CODE_ASK_PERMISSIONS);
                            return;
                        } else if (hasWriteExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.CAMERA},
                                    REQUEST_CODE_ASK_PERMISSIONS_CAMERA);
                            return;
                        }
                        break;
                }
            }
            networkInfo(true);
            mobileInfo();
            DeviceInfoFragment.permission = true;
        } catch (Exception e) {
            logException(e, "NetworkInfoFragment_checkRuntimePermisson()");
        }

    }

    /**
     * permission callback
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        try {
            switch (requestCode) {
                case REQUEST_CODE_ASK_PERMISSIONS:
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // Permission Granted
                        networkInfo(true);
                        mobileInfo();
                    } else {
                        // Permission Denied
                        networkInfo(false);
                        mobileInfo();
                    }
                    checkRuntimePermisson(1);
                    break;

                case REQUEST_CODE_ASK_PERMISSIONS_CAMERA:
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // Permission Granted
                        DeviceInfoFragment.permission = true;
                    } else {
                        // Permission Denied
                        DeviceInfoFragment.permission = false;
                    }
                    break;
                default:
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        } catch (Exception e) {
            logException(e, "NetworkInfoFragment_onRequestPermissionsResult()");
        }

    }


    /* * This will fetch the mobile network information
     * wifi Section*/

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void networkInfo(boolean permission) {
        try {
            mtvStatus.setText(getConnectivityStatusString(getActivity()));

            //Network name
            WifiManager wifiMgr = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
            mtvNetwork.setText(wifiInfo.getSSID());
            // Wifi Speed
            if (wifiInfo != null) {
                mtvLinkSpeed.setText(String.valueOf(wifiInfo.getLinkSpeed()));
            }
            mtvBSSID.setText(getBSSID(permission));
            //Mac Address of Device
            mtvMacAddress.setText(getMacAddr());
            // Device Gateway Information and IP Address
            DhcpInfo dhcpInfo = wifiMgr.getDhcpInfo();
            mtvDHCPServer.setText(Formatter.formatIpAddress(Integer.parseInt(String.valueOf(dhcpInfo.gateway))));
            mtvGateway.setText(Formatter.formatIpAddress(Integer.parseInt(String.valueOf(dhcpInfo.gateway))));
            mIPAddress.setText(Formatter.formatIpAddress(Integer.parseInt(String.valueOf(dhcpInfo.ipAddress))));

            //Signal Strength of Device in Dbm

            ConnectivityManager connectivitymanager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo[] networkInfo = connectivitymanager.getAllNetworkInfo();

            for (NetworkInfo netInfo : networkInfo) {

                if (netInfo.getTypeName().equalsIgnoreCase(getResources().getString(R.string.txtWif)))

                    if (netInfo.isConnected()) {
                        WifiManager wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);

                        //Set Text

                        mtvSignalStrength.setText(String.valueOf(wifiManager.getConnectionInfo().getRssi() + getResources().getString(R.string.txtDbm)));
                    }


                if (netInfo.getTypeName().equalsIgnoreCase(getResources().getString(R.string.txtNet)))

                    if (netInfo.isConnected()) {
                        try {
                            final TelephonyManager tm = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
                            for (final CellInfo info : tm.getAllCellInfo()) {
                                if (info instanceof CellInfoGsm) {
                                    final CellSignalStrengthGsm gsm = ((CellInfoGsm) info).getCellSignalStrength();
                                    mtvSignalStrength.setText(String.valueOf(gsm.getDbm() + getResources().getString(R.string.txtDbm)));
                                    // do what you need
                                } else if (info instanceof CellInfoCdma) {
                                    final CellSignalStrengthCdma cdma = ((CellInfoCdma) info).getCellSignalStrength();
                                    mtvSignalStrength.setText(String.valueOf(cdma.getDbm() + getResources().getString(R.string.txtDbm)));

                                    // do what you need
                                } else if (info instanceof CellInfoLte) {
                                    final CellSignalStrengthLte lte = ((CellInfoLte) info).getCellSignalStrength();
                                    mtvSignalStrength.setText(String.valueOf(lte.getDbm() + getResources().getString(R.string.txtDbm)));
                                } else if (info instanceof CellInfoWcdma) {
                                    final CellSignalStrengthWcdma wcdma = ((CellInfoWcdma) info).getCellSignalStrength();
                                    mtvSignalStrength.setText(String.valueOf(wcdma.getDbm() + getResources().getString(R.string.txtDbm)));
                                } else {
                                    throw new Exception("Unknown type of cell signal!");
                                }
                            }
                        } catch (Exception e) {
                            // Log.e(TAG, "Unable to obtain cell signal information", e);
                        }


                    }
            }
            WifiManager wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);

            mtvSignalStrength.setText(String.valueOf(wifiManager.getConnectionInfo().getRssi() + getResources().getString(R.string.txtDbm)));
        } catch (Exception e) {
            logException(e, "NetworkInfoFragment_networkInfo()");
        }
        //Status

    }

    /**
     * get mac address of device
     *
     * @return
     */
    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            NetworkInfoFragment networkInfoFragment = new NetworkInfoFragment();
            networkInfoFragment.logException(ex, "NetworkInfoFragment_getMacAddr()");
        }
        return "02:00:00:00:00:00";
    }

    // Status
    /*getConnectivityStatusString() method uses thin method to fetch the connectivity status of mobile network

     */
    public static int getConnectivityStatus(Context context) {
        int TYPE_WIFI = 1;
        int TYPE_MOBILE = 2;
        int TYPE_NOT_CONNECTED = 0;

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    /**
     * Fetch the connectivity status of mobile network
     */

    public String getConnectivityStatusString(Context context) {
        try {
            int conn = getConnectivityStatus(context);
            int TYPE_WIFI = 1;
            int TYPE_MOBILE = 2;
            int TYPE_NOT_CONNECTED = 0;
            String status = null;
            if (conn == TYPE_WIFI) {
                status = getResources().getString(R.string.txtWiEnable);
            } else if (conn == TYPE_MOBILE) {
                status = getResources().getString(R.string.txtMobEnable);
            } else if (conn == TYPE_NOT_CONNECTED) {
                status = getResources().getString(R.string.txtNotEnable);
            }
            return status;
        } catch (Exception e) {
            return "";
        }

    }

    //Status ends
    /* This method fetch the device connected sim information
     * Mobile section
     * */
    private void mobileInfo() {
        try {
            telMgr = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
            //Sim card status
            mtvStatusMobile.setText(getSimCardStatus());
            //operator name of inserted sim
            mtvOperator.setText(getOperatorName());
            //operator code of inserted sim
            mtvOperatorCode.setText(telMgr.getNetworkOperator());
            //Check Network Roaming
            mtvRoaming.setText(getNetworkRoaming());
            //Check Phone type
            mtvPhoneType.setText(getPhoneType(getActivity()));
            //Check Network Type
            mtvNetworkType.setText(getNetworkType(getActivity()));
        } catch (Exception e) {
            logException(e, "NetworkInfoFragment_mobileInfo()");
        }

    }


    /**
     * This method detect sim card is in Roaming or not
     *
     * @return String
     */

    public String getNetworkRoaming() {
        try {
            ConnectivityManager connManager = (ConnectivityManager) getActivity()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnected()) {
                return getResources().getString(R.string.txtRoamNotAvail);
            } else {


                if (Build.VERSION.SDK_INT < 17) {

                    return (Settings.System.getInt(ctx.getContentResolver(), Settings.Secure.DATA_ROAMING, 0) == 1) ?
                            (getResources().getString(R.string.txtRoamEnable))
                            : (getResources().getString(R.string.txtRoamDisabled));
                } else {
                    return (Settings.Global.getInt(ctx.getContentResolver(), Settings.Global.DATA_ROAMING, 0) == 1) ?
                            (getResources().getString(R.string.txtRoamEnable))
                            : (getResources().getString(R.string.txtRoamDisabled));
                }
            }


        } catch (Exception e)

        {
            logException(e, "NetworkInfoFragment_getNetworkRoaming()");
            return "";
        }

    }

    /**
     * This method Return The phone type
     *
     * @return String
     */

    public static String getPhoneType(Activity activity) {
        int phoneType = ((TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE)).getPhoneType();

        switch (phoneType) {
            case (TelephonyManager.PHONE_TYPE_CDMA):
                return activity.getResources().getString(R.string.txtTypeCDMA);
            // your code

            case (TelephonyManager.PHONE_TYPE_GSM):
                return activity.getResources().getString(R.string.txtTypeGSM);
            // your code

            case (TelephonyManager.PHONE_TYPE_NONE):
                return activity.getResources().getString(R.string.txtTypeNone);
            // your code

        }
        return null;
    }

    /**
     * This method return network type
     *
     * @return String
     */

    public static String getNetworkType(Activity activity) {
        try {
            String unknown = activity.getResources().getString(R.string.txtUnknown);
            int networkType = ((TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE)).getNetworkType();
            switch (networkType) {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return "1xRTT";
                // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return "CDMA";
                // ~ 14-64 kbps
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return "EDGE";
                // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return "EVDO_0";
                // ~ 400-1000 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return "EDVO_A";
                // ~ 600-1400 kbps
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return "GPRS";
                // ~ 100 kbps
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return "HSDPA";
                // ~ 2-14 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return "HSPA";
                // ~ 700-1700 kbps
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return "HSUPA";
                // ~ 1-23 Mbps
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return "UMTS";
                // ~ 400-7000 kbps
                /*
                 * Above API level 7, make sure to set android:targetSdkVersion
                 * to appropriate level to use these
                 */
                case TelephonyManager.NETWORK_TYPE_EHRPD: // API level 11
                    return "EHRPD";

                case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    return "EVDO_B";// API level 9
                // ~ 5 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return "HSPAP";// API level 13
                // ~ 10-20 Mbps
                case TelephonyManager.NETWORK_TYPE_IDEN: // API level 8
                    return "IDEN";
                // ~25 kbps
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return "LTE";// API level 11
                // ~ 10+ Mbps
                // Unknown
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:

                    return unknown;
                default:
                    return (activity.getResources().getString(R.string.txtSimNone));
            }
        } catch (Exception e) {
            NetworkInfoFragment networkInfoFragment = new NetworkInfoFragment();

            networkInfoFragment.logException(e, "NetworkInfoFragment_getNetworkType()");
            return "";
        }

    }

    /**
     * This method return operator name
     *
     * @return String
     */
    public String getOperatorName() {

        try {
            if ((telMgr.getNetworkOperatorName()).isEmpty()) {
                return (getResources().getString(R.string.txtSimNone));


            } else {
                return (telMgr.getNetworkOperatorName());
            }
        } catch (Exception e) {
            logException(e, "NetworkInfoFragment_getOperatorName()");
            return "";
        }


    }

    /**
     * This method return sim card status
     *
     * @return String
     */
    private String getSimCardStatus() {
        try {
            int simState = telMgr.getSimState();

            switch (simState) {
                case TelephonyManager.SIM_STATE_ABSENT:

                    return (getResources().getString(R.string.txtSimDisconnect));

                case TelephonyManager.SIM_STATE_NETWORK_LOCKED:


                case TelephonyManager.SIM_STATE_PIN_REQUIRED:


                case TelephonyManager.SIM_STATE_PUK_REQUIRED:


                case TelephonyManager.SIM_STATE_READY:

                    return (getResources().getString(R.string.txtSimConnect));

                case TelephonyManager.SIM_STATE_UNKNOWN:


            }
        } catch (Exception e) {
            logException(e, "NetworkInfoFragment_getSimCardStatus()");
        }

        return null;
    }

    private String getBSSID(boolean permission) {
        String bssid = "";
        try {
            if (permission) {
                //BSSID Address
                WifiManager wifiManagerr = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
                List<ScanResult> results = wifiManagerr.getScanResults();

                if (results != null) {
                    final int size = results.size();
                    if (size == 0) {
                    } else {
                        ScanResult bestSignal = results.get(0);
                        for (ScanResult result : results) {
                            //Set Text
                            /*bssid.concat(result.BSSID);*/
                            bssid = bssid + result.BSSID;
                            if (WifiManager.compareSignalLevel(bestSignal.level,
                                    result.level) < 0) {
                                bestSignal = result;
                            }
                        }
                    }
                }
            } else {
                return (getResources().getString(R.string.txtPermissionDenied));
            }
        } catch (Exception e) {
            logException(e, "NetworkInfoFragment_getBSSID()");
        }

        return bssid;
    }

    public void logException(Exception e, String methodName) {
        try {
            Utilities utilities = Utilities.getInstance(getActivity());
            Activity activity = getActivity();
            Context context = getActivity();
            ExceptionLogUtils exceptionLogUtils = new ExceptionLogUtils(utilities, context, activity);
            exceptionLogUtils.addExceptionLog(utilities, context, activity, null, null, e, methodName);
        } catch (Exception exp) {
           // logException(exp, "NetworkInfoFragment_logException()");
        }

    }
}
