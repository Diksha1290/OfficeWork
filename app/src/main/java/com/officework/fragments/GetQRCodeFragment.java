package com.officework.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.app.role.RoleManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Handler;
import android.provider.Telephony;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.officework.models.IMEIModel;
import com.officework.testing_profiles.realmDatabase.operationManager.RealmOperations;
import com.officework.utils.SMS.CustomerIdProvider;
import com.officework.utils.SMS.MmsReceiver;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.officework.R;
import com.officework.activities.MainActivity;
import com.officework.base.BaseFragment;
import com.officework.broadcastReceivers.BatteryReceiver;
import com.officework.broadcastReceivers.DataBroadcastReceiver;
import com.officework.constants.AsyncConstant;
import com.officework.constants.Constants;
import com.officework.constants.FragmentTag;
import com.officework.constants.JsonTags;
import com.officework.constants.PreferenceConstants;
import com.officework.interfaces.InterfaceBatteryCallback;
import com.officework.interfaces.InterfaceButtonTextChange;
import com.officework.utils.ExceptionLogUtils;
import com.officework.utils.TelephonyInfo;
import com.officework.utils.Utilities;
import com.jaredrummler.android.device.DeviceName;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import static android.provider.Telephony.Sms.getDefaultSmsPackage;
import static com.officework.activities.MainActivity.clearPreferenceData;


/**
 * Created by Ashwani Goyal on 8/9/2016.
 */
public class GetQRCodeFragment extends BaseFragment implements InterfaceBatteryCallback, View.OnClickListener {
    View view;
    Utilities utils;
    Context ctx;
    ImageView mImgViewQRCode, mImgViewBarCode, mtxtViewBroadcast;
    InterfaceButtonTextChange mCallBack;
    TextView mtxtViewBarCode, mtxtViewTitle;
    int SERVICE_REQUEST_ID = -1;
    QRCodeWriter writer;
    public static BatteryReceiver batteryInfoReceiver = null;
    Display display;
    LinearLayout layoutNoConnectivity;
    NetworkInfoFragment networkInfoFragment;
    final public int REQUEST_CODE_ASK_PERMISSIONS = 123;
    DataBroadcastReceiver receiver = null;
    public boolean isbtnClicked = false;


    public static final int MESSAGE_CODE = 90;


    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        try {
            if (view == null) {
                view = inflater.inflate(R.layout.fragment_manual_qr_code, null);
                utils = Utilities.getInstance(getActivity());
                networkInfoFragment = new NetworkInfoFragment();
                ctx = getActivity();
                //   Crashlytics.getInstance().log(FragmentTag.GETQRCODE_FRAGMENT.imei_1());
                initViews();
            }
            return view;
        } catch (Exception e) {
            logException(e, "GetQRCodeFragment_initUI()");
            return null;
        }

    }

    public GetQRCodeFragment() {
    }

    /**
     * initialize view
     */
    private void initViews() {
        try {
            WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
            display = wm.getDefaultDisplay();
            mImgViewQRCode = (ImageView) view.findViewById(R.id.imgViewQRCode);
            mImgViewBarCode = (ImageView) view.findViewById(R.id.imgViewBarCode);
            mtxtViewBarCode = (TextView) view.findViewById(R.id.txtViewBarCode);
            mtxtViewBroadcast = (ImageView) view.findViewById(R.id.txtViewBroadcast);
            mtxtViewBroadcast.setOnClickListener(this);
            mtxtViewTitle = (TextView) view.findViewById(R.id.txtViewTitle);
            layoutNoConnectivity = (LinearLayout) view.findViewById(R.id.layoutNoConnectivity);
            mImgViewQRCode.getLayoutParams().width = display.getWidth();
            mImgViewQRCode.getLayoutParams().height = display.getWidth();
            writer = new QRCodeWriter();
            if (!utils.getPreference(ctx, JsonTags.udi.name(), "").equalsIgnoreCase("")) {
                generateBarCode(utils.getPreference(ctx, JsonTags.udi.name(), ""));
                broadcastData(true);
            } else {
                changeVisibility(false);
            }
        } catch (Exception e) {
            logException(e, "GetQRCodeFragment_initViews()");
        }

    }

    /**
     * Token API request
     */
//    private void getToken() {
//        try {
//            getDeviceInfoData(utils, getActivity(), ctx, (InterfaceBatteryCallback) this);
//            if (utils.isInternetWorking(getActivity())) {
//                SERVICE_REQUEST_ID = 0;
//                utils.serviceCalls(getActivity(), WebserviceUrls.DiagnosticDataTokenURL, true, createJson(utils, ctx).toString(), true, SERVICE_REQUEST_ID,
//                        (WebServiceCallback) this, true);
//            } else {
//                utils.showToast(getActivity(), getResources().getString(R.string.InternetError));
//                changeVisibility(false);
//            }
//        } catch (Exception e) {
//            logException(e, "GetQRCodeFragment_getToken()");
//        }
//
//
//    }

    /**
     * change visibility if network is not avilable
     *
     * @param isUDIGenerated
     */
    private void changeVisibility(boolean isUDIGenerated) {
        try {
            broadcastData(isUDIGenerated);
            layoutNoConnectivity.setVisibility(View.VISIBLE);
            mtxtViewTitle.setVisibility(View.GONE);
        } catch (Exception e) {
            logException(e, "GetQRCodeFragment_changeVisibility()");
        }

    }

    /**
     * onClick
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.txtViewBroadcast:
                    checkRuntimePermisson();
                    break;
            }
        } catch (Exception e) {
            logException(e, "GetQRCodeFragment_onClick()");
        }

        super.onClick(v);
    }

    /**
     * POST result data API request
     */
//    private void postDiagnosticsData() {
//        try {
//            if (utils.isInternetWorking(getActivity())) {
//                SERVICE_REQUEST_ID = 1;
//                utils.serviceCalls(getActivity(), WebserviceUrls.DiagnosticDataURL, true, createJson(utils, ctx).toString(), true, SERVICE_REQUEST_ID,
//                        (WebServiceCallback) this, false);
//            } else {
//                utils.showToast(getActivity(), getResources().getString(R.string.InternetError));
//                changeVisibility(false);
//            }
//        } catch (Exception e) {
//            logException(e, "GetQRCodeFragment_postDiagnosticsData()");
//        }
//
//
//    }

    /**
     * get IMEI number from device
     * if permission is true means user has granted run time permission
     *
     * @param permission
     * @param utils
     * @param mContext
     * @param activity
     */
    public static boolean getIMEI(boolean permission, Utilities utils, Context mContext,
                                  Activity activity) {
        try {
            RealmOperations realmOperations = new RealmOperations();
            if (TextUtils.isEmpty(getData(mContext))) {
                if (ContextCompat.checkSelfPermission(mContext,
                        Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {

                    realmOperations.saveIMEIInDatabase(new IMEIModel("1",
                            TelephonyInfo.getInstance(activity).getImsiSIM1()));
                    realmOperations.saveIMEIInDatabase(new IMEIModel("2",
                            TelephonyInfo.getInstance(activity).getImsiSIM2()));
                    utils.compare_UpdateSecurePreferenceString(mContext, JsonTags.MMR_1.name(),
                            TelephonyInfo.getInstance(activity).getImsiSIM1());
                    utils.compare_UpdateSecurePreferenceString(mContext, JsonTags.MMR_2.name(),
                            TelephonyInfo.getInstance(activity).getImsiSIM2());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        if (mContext.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                            utils.compare_UpdateSecurePreferenceString(mContext,
                                    JsonTags.MMR_3.name(), getSerialNumber());
                            realmOperations.saveIMEIInDatabase(new IMEIModel("3",
                                    getSerialNumber()));
                        }
                    } else {
                        utils.compare_UpdateSecurePreferenceString(mContext,
                                JsonTags.MMR_3.name(), Build.SERIAL);

                        realmOperations.saveIMEIInDatabase(new IMEIModel("3", Build.SERIAL));
                    }

                } else {
                    utils.compare_UpdateSecurePreferenceString(mContext, JsonTags.MMR_1.name(),
                            activity.getResources().getString(R.string.txtPermissionDenied));
                    utils.compare_UpdateSecurePreferenceString(mContext, JsonTags.MMR_2.name(),
                            activity.getResources().getString(R.string.txtPermissionDenied));
                }

            }
            if (TextUtils.isEmpty(utils.getSecurePreference(mContext, JsonTags.MMR_1.name(), "")) || utils.getSecurePreference(mContext, JsonTags.MMR_1.name(), "").equalsIgnoreCase("N/A")) {
                if (TextUtils.isEmpty(realmOperations.getIMEI_One("0")) || realmOperations.getIMEI_One("0").equalsIgnoreCase("N/A") || realmOperations.getIMEI_One("0").equals("")) {
                    TelephonyInfo.clearInstance();


// Utilities.showAlert(mContext, new Utilities.onAlertOkListener() {
// @Override
// public void onOkButtonClicked(String tag) {
// messageApp(mContext);
// }
// }, mContext.getResources().getString(R.string.message_notify_text), mContext
// .getResources().getString(R.string.app_name), "Ok", "", "Sync");


                    return false;
                }
            } else {
                saveDataContentValue(mContext);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;

    }


    private static void messageApp(Context mContext) {
        try {
            if (!getDefaultSmsPackage(mContext).equals(mContext.getPackageName())) {
                RoleManager roleManager = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    roleManager = mContext.getSystemService(RoleManager.class);
              //  }

              //  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (roleManager.isRoleAvailable(RoleManager.ROLE_SMS)) {
                        if (roleManager.isRoleHeld(RoleManager.ROLE_SMS)) {
                            Log.d("role", "role");
                        } else {
                            Intent roleRequestIntent = roleManager.createRequestRoleIntent(
                                    RoleManager.ROLE_SMS);
                            ((Activity) mContext).startActivityForResult(roleRequestIntent, MESSAGE_CODE);
                        }
                    }
                }
                else {
                     Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                   intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,
                       mContext.getPackageName());
                   ((Activity) mContext).startActivityForResult(intent, MESSAGE_CODE);
                }



            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Generate QR code for UDi
     *
     * @param data
     * @param imgView
     */
    private void generateQRCode(String data, ImageView imgView) {
        try {
            Writer writer = new QRCodeWriter();
            String finaldata = Uri.encode(data);
            int width = display.getWidth();
            BitMatrix bm = null;
            try {
                bm = writer.encode(data, BarcodeFormat.QR_CODE, width, width);
            } catch (WriterException e) {
                e.printStackTrace();
            }
            Bitmap ImageBitmap = Bitmap.createBitmap(width, width, Bitmap.Config.RGB_565);

            for (int i = 0; i < width; i++) {//width
                for (int j = 0; j < width; j++) {//height
                    ImageBitmap.setPixel(i, j, bm.get(i, j) ? Color.BLACK : Color.WHITE);
                }
            }

            if (ImageBitmap != null) {
                imgView.setImageBitmap(ImageBitmap);
            } else {
                //Toast.makeText(getApplicationContext(), getResources().getString(R.string.userInputError),
                //   Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            logException(e, "GetQRCodeFragment_generateQRCode()");
        }

    }

    /**
     * Generate BARCODE for UDI
     *
     * @param UDI
     */
    private void generateBarCode(String UDI) {
        try {
            MultiFormatWriter writer = new MultiFormatWriter();
            int width = display.getWidth();
            String finaldata = Uri.encode(UDI, "utf-8");

            BitMatrix bm = null;
            try {
                bm = writer.encode(finaldata, BarcodeFormat.CODE_128, width, 100);
            } catch (WriterException e) {
                e.printStackTrace();
            }
            Bitmap ImageBitmap = Bitmap.createBitmap(width, 100, Bitmap.Config.ARGB_8888);

            for (int i = 0; i < width; i++) {//width
                for (int j = 0; j < 100; j++) {//height
                    ImageBitmap.setPixel(i, j, bm.get(i, j) ? Color.BLACK : Color.WHITE);
                }
            }

            if (ImageBitmap != null) {
                mImgViewBarCode.setImageBitmap(ImageBitmap);
                mtxtViewBarCode.setText(UDI);
            } else {
                //Toast.makeText(getApplicationContext(), getResources().getString(R.string.userInputError),
                //    .// Toast.LENGTH_SHORT).show();
            }
            broadcastData(true);
        } catch (Exception e) {
            logException(e, "GetQRCodeFragment_generateBarCode()");
        }

    }

    /**
     * Check runtime permission for fetch IMEI number
     */

    private void checkRuntimePermisson() {
        try {
            int hasWritePhoneReadPermission = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                hasWritePhoneReadPermission = getActivity().checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
                if (hasWritePhoneReadPermission != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
                            REQUEST_CODE_ASK_PERMISSIONS);
                    return;
                }
            }
            getIMEI(true, utils, ctx, getActivity());
            // getToken();
        } catch (Exception e) {
            logException(e, "GetQRCodeFragment_checkRuntimePermisson()");
        }

    }

    /**
     * @param isUDIGenerated if this variable is true means UDI is present in application
     *                       if udi is present then we only broadcast udi not data
     *                       if udi is not present then we broadcast whole data
     */
    public void broadcastData(boolean isUDIGenerated) {
        try {
            receiver = new DataBroadcastReceiver(isUDIGenerated);
            IntentFilter filter = new IntentFilter();
            filter.addAction("com.megammr.GET_Data");
            getActivity().registerReceiver(receiver, filter);
        } catch (Exception e) {
            logException(e, "GetQRCodeFragment_broadcastData()");
        }

    }

    /**
     * permission request callback
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
                        getIMEI(true, utils, ctx, getActivity());
                    } else {
                        // Permission Denied
                        getIMEI(false, utils, ctx, getActivity());
                    }
                    //  getToken();
                    break;
                default:
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        } catch (Exception e) {
            logException(e, "GetQRCodeFragment_onRequestPermissionsResult()");
        }

    }

    public static void deviceMarketName(Context ctx) {
        DeviceName.with(ctx).request(new DeviceName.Callback() {

            @Override
            public void onFinished(DeviceName.DeviceInfo info, Exception error) {
                Utilities.getInstance(ctx).compare_UpdateSecurePreferenceString(ctx, JsonTags.MMR_5.name(), info.marketName); //Build.MODEL

//                Utilities.getInstance(ctx).compare_UpdateSecurePreferenceString(ctx, JsonTags.MMR_5.name(),"ABC"); //Build.MODEL

            }
        });
    }

    /**
     * Fetch device information and store in sharedpreferences
     *
     * @param utils
     * @param activity
     * @param ctx
     * @param interfaceBatteryCallback
     * @return
     */


    @SuppressLint("NewApi")
    public static String getDeviceInfoData(Utilities utils, Activity activity, Context ctx, InterfaceBatteryCallback interfaceBatteryCallback
    ) {

        try {
            if (!PreferenceConstants.isAppActive) {

              /*  batteryInfoReceiver = new BatteryReceiver(interfaceBatteryCallback);
                activity.registerReceiver(batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));*/
                utils.compare_UpdatePreferenceString(ctx, JsonTags.LicenceNumber.name(), "");


                utils.compare_UpdateSecurePreferenceString(ctx, JsonTags.MMR_4.name(), Build.MANUFACTURER);
                utils.compare_UpdatePreferenceString(ctx, JsonTags.LGE.name(), "");
                // deviceMarketName(ctx);

                //   utils.compare_UpdateSecurePreferenceString(ctx, JsonTags.MMR_5.imei_1(),Build.MODEL ); //Build.MODEL
                utils.compare_UpdateSecurePreferenceString(ctx, JsonTags.MMR_6.name(), HardwareInfoFragment.getTotalInternalMemorySize());// This method will return device memory in bytes
                utils.compare_UpdateSecurePreferenceString(ctx, JsonTags.MMR_7.name(), NetworkInfoFragment.getMacAddr());
                utils.compare_UpdateSecurePreferenceString(ctx, JsonTags.MMR_67.name(), HardwareInfoFragment.getUsedInternalStorage());// This method will return device memory in bytes
                /**
                 * utils.compare_UpdatePreferenceString(ctx, JsonTags.MMR_8.imei_1(), getAndroidOSName());
                 */
                utils.compare_UpdateSecurePreferenceString(ctx, JsonTags.MMR_8.name(), "Android");
                utils.compare_UpdateSecurePreferenceString(ctx, JsonTags.MMR_9.name(), "v" + Build.VERSION.RELEASE + "(" + getAndroidOSName() + ")");
                utils.compare_UpdatePreferenceString(ctx, JsonTags.MMR_10.name(), "");
                utils.compare_UpdatePreferenceString(ctx, JsonTags.MMR_11.name(), "");
                utils.compare_UpdateSecurePreferenceString(ctx, JsonTags.MMR_12.name(), NetworkInfoFragment.getPhoneType(activity));
                utils.compare_UpdatePreferenceString(ctx, JsonTags.MMR_13.name(), "");
                utils.compare_UpdatePreferenceString(ctx, JsonTags.MMR_14.name(), "");
                //utils.compare_UpdatePreferenceString(ctx, JsonTags.MMR_15.imei_1(), "");
                //utils.compare_UpdateSecurePreferenceInt(ctx, JsonTags.MMR_42.imei_1(), checkDeviceManger(activity));
                //utils.compare_UpdateSecurePreferenceInt(ctx, JsonTags.MMR_42.imei_1(), -1);
                utils.compare_UpdatePreferenceInt(ctx, JsonTags.RequestTypeID.name(), 2);
                /*utils.compare_UpdatePreferenceString(ctx, JsonTags.BatteryCondition.imei_1(), null);*/// In Broadcast Receiver
                //  utils.compare_UpdatePreferenceString(ctx, JsonTags.MMR_25.imei_1(), "");
                //  utils.compare_UpdatePreferenceString(ctx, JsonTags.MMR_29.imei_1(), "");
                return "-----------Qr Code----";
            } else {

            }
        } catch (Exception e) {
            GetQRCodeFragment getQRCodeFragment = new GetQRCodeFragment();
            getQRCodeFragment.logException(e, "GetQRCodeFragment_getDeviceInfoData()");
        }

        return "-----------Qr Code----";
    }


    /**
     * This receive call back from GETQRCODEFAGMENT where we register receiver
     * for find battery level
     * This is called while when we call getDeviceInfoData() method of GETQRCODEFAGMENT class
     * battery information callback
     *
     * @param intent
     */
    @Override
    public void onBatteryCallBack(Intent intent) {
       /* try {
            utils.compare_UpdatePreferenceString(ctx, JsonTags.MMR_17.imei_1(),
                    intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0) + "%");
            *//*BatteryInfoFragment.getBatteryHealthString(intent.getIntExtra(BatteryManager.EXTRA_HEALTH, 0), ctx));*//*
        } catch (Exception e) {
            logException(e, "GetQRCodeFragment_onBatteryCallBack()");
        }*/

    }


    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getSerialNumber() {
        String serialNumber;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);

            serialNumber = (String) get.invoke(c, "gsm.sn1");
            if (serialNumber.equals(""))
                serialNumber = (String) get.invoke(c, "ril.serialnumber");
            if (serialNumber.equals(""))
                serialNumber = (String) get.invoke(c, "ro.serialno");
            if (serialNumber.equals(""))
                serialNumber = (String) get.invoke(c, "sys.serialnumber");

            if (serialNumber.equals(""))
                serialNumber = Build.getSerial();
            if (serialNumber.equals(""))
                serialNumber = Build.SERIAL;

// If none of the methods above worked
            if (serialNumber.equals(""))
                serialNumber = null;
        } catch (Exception e) {
            e.printStackTrace();
            serialNumber = null;
        }

        return serialNumber;
    }

    /**
     * This will @return device Os Name
     */
    private static String getAndroidOSName() {
        String fieldName = "";
        try {
            Field[] fields = Build.VERSION_CODES.class.getFields();
            for (Field field : fields) {
                fieldName = field.getName();
            }
        } catch (Exception e) {
            GetQRCodeFragment getQRCodeFragment = new GetQRCodeFragment();
            getQRCodeFragment.logException(e, "GetQRCodeFragment_getAndroidOSName()");
        }


        return fieldName;
    }

    /**
     * This will check weather user has enabled the Device Manager or not
     */
    public static int checkDeviceManger(Activity activity) {
        int deviceManager = AsyncConstant.TEST_PASS;

        try {
            DevicePolicyManager activeDevicePolicyManager = (DevicePolicyManager) activity.getSystemService(Context.DEVICE_POLICY_SERVICE);
            List<ComponentName> activeAdmins = activeDevicePolicyManager.getActiveAdmins();
            if (activeAdmins != null && !activeAdmins.isEmpty()) {
                for (int index = 0; index < activeAdmins.size(); index++) {
                /*Log.i(LOG_TAG, "flattenToShortString: " + activeAdmins.get(index).flattenToShortString());
                Log.i(LOG_TAG, "flattenToString: " + activeAdmins.get(index).flattenToString());
                Log.i(LOG_TAG, "getClassName: " + activeAdmins.get(index).getClassName());
                Log.i(LOG_TAG, "getPackageName: " + activeAdmins.get(index).getPackageName());
                Log.i(LOG_TAG, "getShortClassName: " + activeAdmins.get(index).getShortClassName());
                Log.i(LOG_TAG, "toShortString: " + activeAdmins.get(index).toShortString());*/
                    deviceManager = AsyncConstant.TEST_FAILED;
                }
            } else {
                /*Log.i(LOG_TAG, "No Active Device Policy Manager");*/
                deviceManager = AsyncConstant.TEST_PASS;
            }
        } catch (Exception e) {
            GetQRCodeFragment getQRCodeFragment = new GetQRCodeFragment();
            getQRCodeFragment.logException(e, "GetQRCodeFragment_checkDeviceMangerI()");
        }

        return deviceManager;
    }

    /**
     * create json for broadcast data when UDI is avaialble
     *
     * @param utils
     * @param context
     * @return
     */
    public static JSONObject createJsonUDI(Utilities utils, Context context) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(JsonTags.UDI.name(), utils.getPreference(context, JsonTags.udi.name(), ""));
        } catch (Exception e) {
            e.printStackTrace();
            GetQRCodeFragment getQRCodeFragment = new GetQRCodeFragment();
            getQRCodeFragment.logException(e, "GetQRCodeFragment_createJsonUDI()");
        }
        return jsonObject;
    }

    /**
     * create json for api request and broadcast data
     * isUserDeclined check user has accept terms and condition or not
     * if used has declined trms and conditions then device info is not passed to API
     * only result data is posted to API
     *
     * @param utils
     * @param
     * @return
     */

    public static JSONObject createJson2(Utilities utils, Context ctx) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("grant_type", "password");
            jsonObject.put("UserName", "CA");
            jsonObject.put("SubscriberProductCode", "CAP1");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;

    }


    public static JSONObject createRefCodeJson(Utilities utils, Context ctx) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(JsonTags.SubscriberProductID.name(), utils.getPreference(ctx, JsonTags.SubscriberProductID.name(), ""));
            jsonObject.put(JsonTags.UDI.name(), utils.getPreference(ctx, JsonTags.UDI.name(), ""));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;

    }

    public static JSONObject createJson(Utilities utils, Context ctx) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (!utils.getPreferenceBoolean(ctx, JsonTags.isUserDeclined.name(), true)) {
                utils.addPreference(ctx, JsonTags.DeviceTime.name(), getCurrentTime());
                jsonObject.put(JsonTags.DeviceTime.name(), getCurrentTime());
                jsonObject.put(JsonTags.UDI.name(), utils.getPreference(ctx, JsonTags.udi.name(), ""));
                jsonObject.put(JsonTags.MMR_1.name(), utils.getSecurePreference(ctx, JsonTags.MMR_1.name(), ctx.getResources().getString(R.string.txtPermissionDenied)));
                jsonObject.put(JsonTags.MMR_2.name(), utils.getSecurePreference(ctx, JsonTags.MMR_2.name(), ctx.getResources().getString(R.string.txtPermissionDenied)));
                jsonObject.put(JsonTags.MMR_3.name(), utils.getSecurePreference(ctx, JsonTags.MMR_3.name(), ""));
                jsonObject.put(JsonTags.MMR_4.name(), utils.getSecurePreference(ctx, JsonTags.MMR_4.name(), ""));
                jsonObject.put(JsonTags.LGE.name(), utils.getPreference(ctx, JsonTags.LGE.name(), ""));
                jsonObject.put(JsonTags.MMR_5.name(), utils.getSecurePreference(ctx, JsonTags.MMR_5.name(), ""));
                jsonObject.put(JsonTags.MMR_6.name(), utils.getSecurePreference(ctx, JsonTags.MMR_6.name(), ""));
                //jsonObject.put(JsonTags.MMR_48.imei_1(), utils.getPreferenceInt(ctx, JsonTags.MMR_48.imei_1(), 0));
                jsonObject.put(JsonTags.MMR_8.name(), utils.getSecurePreference(ctx, JsonTags.MMR_8.name(), ""));
                jsonObject.put(JsonTags.MMR_9.name(), utils.getSecurePreference(ctx, JsonTags.MMR_9.name(), ""));
                jsonObject.put(JsonTags.MMR_10.name(), utils.getPreference(ctx, JsonTags.MMR_10.name(), ""));
                jsonObject.put(JsonTags.MMR_11.name(), utils.getPreference(ctx, JsonTags.MMR_11.name(), ""));
                jsonObject.put(JsonTags.MMR_12.name(), utils.getSecurePreference(ctx, JsonTags.MMR_12.name(), ""));
                jsonObject.put(JsonTags.MMR_13.name(), utils.getPreference(ctx, JsonTags.MMR_13.name(), ""));
                jsonObject.put(JsonTags.MMR_14.name(), utils.getPreference(ctx, JsonTags.MMR_14.name(), ""));
                jsonObject.put(JsonTags.MMR_15.name(), utils.getPreference(ctx, JsonTags.MMR_15.name(), ""));
                jsonObject.put(JsonTags.MMR_49.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_49.name(), 0));
                jsonObject.put(JsonTags.MMR_42.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_42.name(), 0));
                jsonObject.put(JsonTags.RequestTypeID.name(), utils.getPreferenceInt(ctx, JsonTags.RequestTypeID.name(), 2));
                jsonObject.put(JsonTags.MMR_17.name(), utils.getPreference(ctx, JsonTags.MMR_17.name(), ""));
                jsonObject.put(JsonTags.MMR_19.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_19.name(), 0));
                jsonObject.put(JsonTags.MMR_25.name(), utils.getPreference(ctx, JsonTags.MMR_25.name(), ""));
                jsonObject.put(JsonTags.MMR_29.name(), utils.getPreference(ctx, JsonTags.MMR_29.name(), ""));
                jsonObject.put(JsonTags.MMR_31.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_31.name(), 0));
                jsonObject.put(JsonTags.MMR_32.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_32.name(), 0));
                jsonObject.put(JsonTags.MMR_33.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_33.name(), 0));
                jsonObject.put(JsonTags.MMR_18.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_18.name(), 0));
                jsonObject.put(JsonTags.MMR_16.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_16.name(), 0));
                jsonObject.put(JsonTags.MMR_20.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_20.name(), 0));
                jsonObject.put(JsonTags.MMR_21.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_21.name(), 0));
                jsonObject.put(JsonTags.MMR_44.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_44.name(), 0));
                jsonObject.put(JsonTags.MMR_22.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_22.name(), 0));
                jsonObject.put(JsonTags.MMR_23.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_23.name(), 0));
                jsonObject.put(JsonTags.MMR_24.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_24.name(), 0));
                jsonObject.put(JsonTags.MMR_26.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_26.name(), 0));
                jsonObject.put(JsonTags.MMR_28.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_28.name(), 0));
                jsonObject.put(JsonTags.MMR_30.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_30.name(), 0));
                jsonObject.put(JsonTags.MMR_34.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_34.name(), 0));
                jsonObject.put(JsonTags.MMR_35.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_35.name(), 0));
                jsonObject.put(JsonTags.MMR_36.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_36.name(), 0));
                jsonObject.put(JsonTags.MMR_38.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_38.name(), 0));
                jsonObject.put(JsonTags.MMR_37.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_37.name(), 0));
                jsonObject.put(JsonTags.MMR_46.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_46.name(), 0));
                jsonObject.put(JsonTags.MMR_47.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_47.name(), 0));
                jsonObject.put(JsonTags.MMR_39.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_39.name(), 0));
                jsonObject.put(JsonTags.MMR_40.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_40.name(), 0));
                jsonObject.put(JsonTags.MMR_41.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_41.name(), 0));
                jsonObject.put(JsonTags.MMR_45.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_45.name(), 0));
                jsonObject.put(JsonTags.MMR_54.name(), 1);
                jsonObject.put(JsonTags.MMR_55.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_55.name(), 0));
                jsonObject.put(JsonTags.MMR_59.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_59.name(), 0));
                jsonObject.put(JsonTags.MMR_29.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_29.name(), 0));
                // jsonObject.put(JsonTags.MMR_60.imei_1(), utils.getPreferenceInt(ctx, JsonTags.MMR_60.imei_1(), 0));
                jsonObject.put(JsonTags.MMR_62.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_62.name(), 0));
                jsonObject.put(JsonTags.MMR_63.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_63.name(), 0));
                jsonObject.put(JsonTags.MMR_56.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_56.name(), 0));
                // jsonObject.put(JsonTags.MMR_61.imei_1(), utils.getPreferenceInt(ctx, JsonTags.MMR_61.imei_1(), 0));
            } else {
                utils.addPreference(ctx, JsonTags.DeviceTime.name(), getCurrentTime());
                jsonObject.put(JsonTags.DeviceTime.name(), getCurrentTime());
                jsonObject.put(JsonTags.UDI.name(), utils.getPreference(ctx, JsonTags.udi.name(), ""));
                jsonObject.put(JsonTags.MMR_1.name(), ctx.getResources().getString(R.string.txtPermissionDenied));
                jsonObject.put(JsonTags.MMR_2.name(), ctx.getResources().getString(R.string.txtPermissionDenied));
                jsonObject.put(JsonTags.MMR_3.name(), "");
                jsonObject.put(JsonTags.MMR_4.name(), "");
                jsonObject.put(JsonTags.LGE.name(), utils.getPreference(ctx, JsonTags.LGE.name(), ""));
                jsonObject.put(JsonTags.MMR_5.name(), "");
                jsonObject.put(JsonTags.MMR_6.name(), "");
                //jsonObject.put(JsonTags.MMR_48.imei_1(), utils.getPreferenceInt(ctx, JsonTags.MMR_48.imei_1(), 0));
                jsonObject.put(JsonTags.MMR_8.name(), "");
                jsonObject.put(JsonTags.MMR_9.name(), "");
                jsonObject.put(JsonTags.MMR_10.name(), "");
                jsonObject.put(JsonTags.MMR_11.name(), utils.getPreference(ctx, JsonTags.MMR_11.name(), ""));
                jsonObject.put(JsonTags.MMR_12.name(), "");
                jsonObject.put(JsonTags.MMR_13.name(), utils.getPreference(ctx, JsonTags.MMR_13.name(), ""));
                jsonObject.put(JsonTags.MMR_14.name(), utils.getPreference(ctx, JsonTags.MMR_14.name(), ""));
                jsonObject.put(JsonTags.MMR_15.name(), utils.getPreference(ctx, JsonTags.MMR_15.name(), ""));
                jsonObject.put(JsonTags.MMR_49.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_49.name(), 0));
                jsonObject.put(JsonTags.MMR_42.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_42.name(), 0));
                jsonObject.put(JsonTags.RequestTypeID.name(), utils.getPreferenceInt(ctx, JsonTags.RequestTypeID.name(), 2));
                jsonObject.put(JsonTags.MMR_17.name(), utils.getPreference(ctx, JsonTags.MMR_17.name(), ""));
                jsonObject.put(JsonTags.MMR_19.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_19.name(), 0));
                jsonObject.put(JsonTags.MMR_25.name(), utils.getPreference(ctx, JsonTags.MMR_25.name(), ""));
                jsonObject.put(JsonTags.MMR_29.name(), utils.getPreference(ctx, JsonTags.MMR_29.name(), ""));
                jsonObject.put(JsonTags.MMR_31.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_31.name(), 0));
                jsonObject.put(JsonTags.MMR_32.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_32.name(), 0));
                jsonObject.put(JsonTags.MMR_33.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_33.name(), 0));
                jsonObject.put(JsonTags.MMR_18.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_18.name(), 0));
                jsonObject.put(JsonTags.MMR_16.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_16.name(), 0));
                jsonObject.put(JsonTags.MMR_20.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_20.name(), 0));
                jsonObject.put(JsonTags.MMR_21.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_21.name(), 0));
                jsonObject.put(JsonTags.MMR_44.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_44.name(), 0));
                jsonObject.put(JsonTags.MMR_22.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_22.name(), 0));
                jsonObject.put(JsonTags.MMR_23.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_23.name(), 0));
                jsonObject.put(JsonTags.MMR_24.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_24.name(), 0));
                jsonObject.put(JsonTags.MMR_26.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_26.name(), 0));
                jsonObject.put(JsonTags.MMR_28.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_28.name(), 0));
                jsonObject.put(JsonTags.MMR_30.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_30.name(), 0));
                jsonObject.put(JsonTags.MMR_34.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_34.name(), 0));
                jsonObject.put(JsonTags.MMR_35.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_35.name(), 0));
                jsonObject.put(JsonTags.MMR_36.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_36.name(), 0));
                jsonObject.put(JsonTags.MMR_38.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_38.name(), 0));
                jsonObject.put(JsonTags.MMR_37.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_37.name(), 0));
                jsonObject.put(JsonTags.MMR_46.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_46.name(), 0));
                jsonObject.put(JsonTags.MMR_47.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_47.name(), 0));
                jsonObject.put(JsonTags.MMR_39.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_39.name(), 0));
                jsonObject.put(JsonTags.MMR_40.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_40.name(), 0));
                jsonObject.put(JsonTags.MMR_41.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_41.name(), 0));
                jsonObject.put(JsonTags.MMR_45.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_45.name(), 0));
                jsonObject.put(JsonTags.MMR_54.name(), 1);
                jsonObject.put(JsonTags.MMR_55.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_55.name(), 0));
                jsonObject.put(JsonTags.MMR_59.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_59.name(), 0));
                jsonObject.put(JsonTags.MMR_29.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_29.name(), 0));
                // jsonObject.put(JsonTags.MMR_60.imei_1(), utils.getPreferenceInt(ctx, JsonTags.MMR_60.imei_1(), 0));
                jsonObject.put(JsonTags.MMR_62.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_62.name(), 0));
                jsonObject.put(JsonTags.MMR_63.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_63.name(), 0));
                jsonObject.put(JsonTags.MMR_56.name(), utils.getPreferenceInt(ctx, JsonTags.MMR_56.name(), 0));
                //jsonObject.put(JsonTags.MMR_61.imei_1(), utils.getPreferenceInt(ctx, JsonTags.MMR_61.imei_1(), 0));

            }
        } catch (JSONException e) {
            e.printStackTrace();
            GetQRCodeFragment getQRCodeFragment = new GetQRCodeFragment();
            getQRCodeFragment.logException(e, "GetQRCodeFragment_createJson()");
        }
        return jsonObject;
    }

    public static JSONObject createSaveDataJson(Utilities utils, Context ctx) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(JsonTags.SubscriberProductID.name(), utils.getPreference(ctx, JsonTags.SubscriberProductID.name(), ""));
            jsonObject.put(JsonTags.UDI.name(), utils.getPreference(ctx, JsonTags.UDI.name(), ""));

            JSONArray DiagnosticData = new JSONArray();

            if (utils.getPreferenceBoolean(ctx, JsonTags.isUserDeclined.name(), true)) {

                for (int i = 1; i <= 9; i++) {
                    String testId = String.valueOf(i);
                    createInfoJson(testId, utils.getSecurePreference(ctx, "MMR_" + testId, ""), DiagnosticData);

                }

                createInfoJson("10", utils.getSecurePreference(ctx, JsonTags.MMR_10.name(), ""), DiagnosticData);
                createInfoJson("11", utils.getPreference(ctx, JsonTags.MMR_11.name(), ""), DiagnosticData);
                createInfoJson("12", utils.getSecurePreference(ctx, JsonTags.MMR_12.name(), ""), DiagnosticData);
                createInfoJson("16", utils.getPreference(ctx, JsonTags.MMR_68.name(), ""), DiagnosticData);
                createInfoJson("67", utils.getSecurePreference(ctx, JsonTags.MMR_67.name(), ""), DiagnosticData);

            } else {

                createInfoJson("1", ctx.getResources().getString(R.string.txtPermissionDenied), DiagnosticData);
                createInfoJson("2", ctx.getResources().getString(R.string.txtPermissionDenied), DiagnosticData);

//                JSONObject obj1=new JSONObject();
//                obj1.put(JsonTags.MMR_1.imei_1(), ctx.getResources().getString(R.string.txtPermissionDenied));
//                DiagnosticData.put(obj1);
//                JSONObject obj2=new JSONObject();
//                obj2.put(JsonTags.MMR_2.imei_1(), ctx.getResources().getString(R.string.txtPermissionDenied));
//                DiagnosticData.put(obj2);

                for (int i = 3; i <= 9; i++) {
                    String testId = String.valueOf(i);

                    createInfoJson(testId, "", DiagnosticData);

//                    String testId=String.valueOf(i);
//                    JSONObject jsonObject1=new JSONObject();
//                    jsonObject1.put( testId, "");
//
//
//                    DiagnosticData.put(jsonObject1);
                }
                createInfoJson("10", "", DiagnosticData);
                createInfoJson("11", "", DiagnosticData);
                createInfoJson("12", "", DiagnosticData);
                createInfoJson("67", "", DiagnosticData);
//                DiagnosticData.put(jsonObject5);
            }


            jsonObject.put("DiagnosticData", DiagnosticData);


        } catch (JSONException e) {
            e.printStackTrace();
            GetQRCodeFragment getQRCodeFragment = new GetQRCodeFragment();
            getQRCodeFragment.logException(e, "GetQRCodeFragment_createJson()");
        }
        return jsonObject;
    }

    public static JSONObject creatDiagnocticDataJson(Utilities utils, Context ctx) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(JsonTags.SubscriberProductID.name(), utils.getPreference(ctx, JsonTags.SubscriberProductID.name(), ""));
            jsonObject.put(JsonTags.UDI.name(), utils.getPreference(ctx, JsonTags.UDI.name(), ""));

            JSONArray DiagnosticData = new JSONArray();


            for (int i = 12; i < MainActivity.json_array.length; i++) {

                if (MainActivity.json_array[i] != 67) {
                    JSONObject jsonObject1 = new JSONObject();
                    //jsonObject1.put( MainActivity.id_array[i].toString(), utils.getPreferenceInt(ctx, "MMR_"+MainActivity.id_array[i].toString(), 0));
                    jsonObject1.put("DiagnosticID", MainActivity.json_array[i].toString());
                    jsonObject1.put("DiagnosticResult", utils.getPreferenceInt(ctx, "MMR_" + MainActivity.json_array[i].toString(), 0));
                    DiagnosticData.put(jsonObject1);
                }
            }
            jsonObject.put("DiagnosticData", DiagnosticData);


        } catch (JSONException e) {
            e.printStackTrace();
            GetQRCodeFragment getQRCodeFragment = new GetQRCodeFragment();
            getQRCodeFragment.logException(e, "GetQRCodeFragment_createJson()");
        }
        return jsonObject;
    }


    public static JSONObject createSingleTestJson(Utilities utils, Context ctx, int testId) {

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put(JsonTags.SubscriberProductID.name(), utils.getPreference(ctx, JsonTags.SubscriberProductID.name(), ""));

            jsonObject.put(JsonTags.UDI.name(), utils.getPreference(ctx, JsonTags.UDI.name(), ""));

            JSONArray DiagnosticData = new JSONArray();
            JSONObject jsonObject1 = new JSONObject();
            //  JSONObject jsonObject2 = new JSONObject();

//            if(testId== ConstantTestIDs.CHARGING_ID|| testId==ConstantTestIDs.EAR_PHONE_ID){
//                if( utils.getPreferenceInt(ctx, JsonTags.MMR_17.imei_1(), 0)==1 &&  utils.getPreferenceInt(ctx, JsonTags.MMR_42.imei_1(), 0)==1)
//                {
//                    utils.compare_UpdatePreferenceInt(ctx,JsonTags.MMR_61.imei_1(),AsyncConstant.TEST_PASS);
//                    jsonObject2.put("DiagnosticID","61");
//                    jsonObject2.put("DiagnosticResult", 1);
//                    DiagnosticData.put(jsonObject2);
//
//                }
//            }
//            if(testId== ConstantTestIDs.HOME_ID|| testId==ConstantTestIDs.POWER_ID || testId==ConstantTestIDs.VOLUME_ID){
//                if( utils.getPreferenceInt(ctx, JsonTags.MMR_25.imei_1(), 0)==1 &&  utils.getPreferenceInt(ctx, JsonTags.MMR_54.imei_1(), 0)==1 &&  utils.getPreferenceInt(ctx, JsonTags.MMR_43.imei_1(), 0)==1)
//                {
//                    utils.compare_UpdatePreferenceInt(ctx,JsonTags.MMR_62.imei_1(),AsyncConstant.TEST_PASS);
//                    jsonObject2.put("DiagnosticID", "62");
//                    jsonObject2.put("DiagnosticResult", 1);
//                    DiagnosticData.put(jsonObject2);
//
//                }
//            }
            jsonObject1.put("DiagnosticID", String.valueOf(testId));
            jsonObject1.put("DiagnosticResult", utils.getPreferenceInt(ctx, "MMR_" + String.valueOf(testId), 0));
            DiagnosticData.put(jsonObject1);

            jsonObject.put("DiagnosticData", DiagnosticData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private static void createInfoJson(String key, String result, JSONArray diagnosticData) {
        JSONObject obj1 = new JSONObject();
        try {
            obj1.put("DiagnosticID", key);
            obj1.put("DiagnosticResult", result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        diagnosticData.put(obj1);
    }


    /**
     * get Device Current Time an date with specified format
     *
     * @return
     */

    public static String getCurrentTime() {


        try {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedDate = dateFormat.format(calendar.getTime());
            return formattedDate;

        } catch (Exception e) {
            GetQRCodeFragment getQRCodeFragment = new GetQRCodeFragment();
            getQRCodeFragment.logException(e, "GetQRCodeFragment_getCurrentTime()");
            return "";
        }


    }

    /**
     * unregister battery receiver
     *
     * @param context
     */
    public static void unRegisterBatteryReceiver(Context context) {
        /*try {
            if (batteryInfoReceiver != null) {
                context.unregisterReceiver(batteryInfoReceiver);
                Log.d("Battery Receiver", " Unregistered");
            }
        } catch (Exception e) {
            GetQRCodeFragment getQRCodeFragment = new GetQRCodeFragment();
            getQRCodeFragment.logException(e, "GetQRCodeFragment_unRegisterBatteryReceiver");
        }*/

    }

    /**
     * onDestroy
     */
    @Override
    public void onDestroy() {

        try {
            /**
             * unregister battery receiver
             * @param context
             */
            if (batteryInfoReceiver != null) {
                getActivity().unregisterReceiver(batteryInfoReceiver);
            }

            /**
             * unregister broadcast data receiver
             */
            if (receiver != null) {
                getActivity().unregisterReceiver(receiver);
                receiver = null;
            }
        } catch (Exception e) {
            logException(e, "GetQRCodeFragment_onDestroy");
        }

        super.onDestroy();
    }

    /**
     * Here change the header text
     ***/
    @Override
    public void onResume() {

        try {
            batteryInfoReceiver = null;
            if (utils.getPreferenceLong(ctx, Constants.lastDate_Time, 0L) != 0L && utils.getPreferenceLong(ctx, Constants.currentDate_Time, 0L) != 0L) {
                if (utils.getTimeDifference(utils.getPreferenceLong(ctx, Constants.lastDate_Time, 0L), utils.getPreferenceLong(ctx, Constants.currentDate_Time, 0L)) >= 12) {
                    clearPreferenceData(utils, ctx, null);
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
                        fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string.txtQRCode_Title), true, false, 0);
                    }
                }
            } else {
                utils.storeDateTime(ctx, true);
                utils.addLog(ctx, "Log Last Date", Constants.lastDateTime + "");
                TitleBarFragment fragment = (TitleBarFragment) getFragment(R.id.headerContainer);
                if (fragment != null) {
                    fragment.setTitleBarVisibility(true);
                    fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string.txtQRCode_Title), true, false, 0);
                }
            }
        } catch (Exception e) {
            logException(e, "GetQRCodeFragment_onResume");
        }

        super.onResume();
    }

    /**
     * Handle API response
     *
     * @param serviceStatus
     * @param response
     * @param callbackID
     */
//    @Override
//    public void onServiceResponse(boolean serviceStatus, String response, int callbackID) {
//        if (serviceStatus) {
//            try {
//                JSONObject responseObj = new JSONObject(response);
//                switch (callbackID) {
//                    case 0:
//                        utils.addPreference(ctx, JsonTags.access_token.imei_1(), responseObj.getString(JsonTags.access_token.imei_1()));
//                        utils.addPreference(ctx, JsonTags.token_type.imei_1(), responseObj.getString(JsonTags.token_type.imei_1()));
//                        postDiagnosticsData();
//                        break;
//                    case 1:
//                        if (responseObj.getBoolean(JsonTags.result.imei_1())) {
//                            utils.compare_UpdatePreferenceString(ctx, JsonTags.udi.imei_1(), responseObj.getString(JsonTags.udi.imei_1()));
//                            /*generateQRCode(createJson().toString(), mImgViewQRCode);*/
//                            if (!utils.getPreference(ctx, JsonTags.udi.imei_1(), "").equalsIgnoreCase("")) {
//                                generateBarCode(utils.getPreference(ctx, JsonTags.udi.imei_1(), ""));
//                                utils.addPreferenceBoolean(ctx, JsonTags.isTestDataChanged.imei_1(), false);
//                            }
//                        } else {
//                            utils.compare_UpdatePreferenceString(ctx, JsonTags.udi.imei_1(), "");
//
//                            /*generateQRCode(createJson().toString(), mImgViewQRCode);*/
//                        }
//                        break;
//
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                logException(e, "GetQRCodeFragment_onServiceResponse");
//                changeVisibility(false);
//            }
//        } else {
//            changeVisibility(false);
//        }
//    }

    /**
     * onBackPressed
     */
    public void onBackPress() {
        try {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            Fragment frag = fm.findFragmentById(R.id.container);
            if (frag instanceof DashBoardFragment) {
                popFragment(R.id.container);
            } else {
                clearAllStack();
                replaceFragment(R.id.container, new DashBoardFragment((InterfaceButtonTextChange) getActivity()), FragmentTag.DASHBOARD_FRAGMENT.name(), false);
            }
        } catch (Exception e) {
            logException(e, "GetQRCodeFragment_onBackPress()");
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
            //  logException(exp, "GetQRCodeFragment_GpsMapManualFragment()");
        }

    }

    static void saveDataContentValue(Context context) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                ContentValues values = new ContentValues();
                values.put(CustomerIdProvider.imei_1, Utilities.getInstance(context).getSecurePreference(context, JsonTags.MMR_1.name(), ""));
                values.put(CustomerIdProvider.imei_2, Utilities.getInstance(context).getSecurePreference(context, JsonTags.MMR_2.name(), ""));
                values.put(CustomerIdProvider.serial_no, Utilities.getInstance(context).getSecurePreference(context, JsonTags.MMR_3.name(), ""));
                context.getContentResolver().delete(CustomerIdProvider.CONTENT_URI, "", null);
                context.getContentResolver().insert(CustomerIdProvider.CONTENT_URI, values);

                return null;
            }
        }.execute();

    }

    public static String getData(Context context) {
        String cus_id = null;
        Uri contentUri = Uri.parse("content://com.officework/customeridprovider");
        Cursor cursor = context.getContentResolver().query(contentUri, null, "", null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            cus_id = cursor.getString(cursor.getColumnIndex(CustomerIdProvider.imei_1));
            Utilities.getInstance(context).compare_UpdateSecurePreferenceString(context, JsonTags.MMR_1.name(), cus_id);
            Utilities.getInstance(context).compare_UpdateSecurePreferenceString(context, JsonTags.MMR_2.name(), cursor.getString(cursor.getColumnIndex(CustomerIdProvider.imei_2)));
            Utilities.getInstance(context).compare_UpdateSecurePreferenceString(context, JsonTags.MMR_3.name(), cursor.getString(cursor.getColumnIndex(CustomerIdProvider.serial_no)));
            Log.d("custmer", cus_id);

        }
        if(cursor!=null)
        cursor.close();


        return cus_id;
    }

    public static void restoreDefault(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                final PackageManager manager = context.getPackageManager();
                final ComponentName component = new ComponentName(context, MmsReceiver.class);
                manager.setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        manager.setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

                    }
                },2000);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
