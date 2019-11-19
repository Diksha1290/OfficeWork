package com.officework.constants;

import android.widget.Toast;

import com.officework.R;
import com.officework.asyncTasks.CountDownTimerTask;
import com.officework.fragments.NavigationDrawerFragment;
import com.officework.interfaces.InterfaceTimerCallBack;
import com.officework.testing_profiles.utils.ConstantTestIDs;

import java.util.Date;

/**
 * Created by girish.sharma on 7/28/2016.
 */
public class Constants {

    /**
     * Navigation drawer icons
     */
    public static final int[] arrDrawerResourcesActive = new int[]{
            R.drawable.ic_dash_nav, R.drawable.ic_auto_nav, R.drawable.ic_manual_nav, R.drawable.ic_devinfo_nav,/* R.drawable.ic_barcode_nav,*/
            /*R.drawable.ic_data_wipe,*/ R.drawable.ic_data_reset, R.drawable.ic_barcode_nav, R.drawable.ic_about_nav};

    /**
     * Automation Test icons
     */
    public static final int[] arrAutomatedTestResources = new int[]{
            R.drawable.ic_bluetooth2, R.drawable.ic_wifi2, R.drawable.ic_vibration2, /*R.drawable.ic_speaker,*/ R.drawable.ic_sim_card_removed2,
            R.drawable.ic_sd_card2, R.drawable.ic_killswitch2, R.drawable.ic_not_rooted2, R.drawable.ic_call_function2,R.drawable.ic_light_svg,R.drawable.ic_barometer_new};

    public static final int[] arrAutomatedTestResourcesId = new int[]{
            AsyncConstant.BLUETOOTH_TEST, AsyncConstant.WIFI_TEST, AsyncConstant.VIBRATION_TEST, /*R.drawable.ic_speaker,*/ AsyncConstant.SIMCARD_TEST,
            AsyncConstant.SDCARD_TEST, AsyncConstant.KILLSWITCH_TEST,AsyncConstant.FMIP, AsyncConstant.CALL_FUNCTION,AsyncConstant.LIGHT_SENSOR_FUNCTION,AsyncConstant.Barometer};
    /**
     * Manual Test icons
     */
    public static final int[] arrManualTestResources = new int[]{
            R.drawable.ic_jack, R.drawable.ic_volume, R.drawable.ic_power, R.drawable.ic_chargingport, R.drawable.ic_proximity,
            R.drawable.ic_home, R.drawable.ic_gyroscope, R.drawable.ic_light, R.drawable.ic_camera, R.drawable.ic_display, R.drawable.ic_gps,
            R.drawable.ic_speaker, R.drawable.ic_mic, R.drawable.ic_touch, R.drawable.ic_multitouch,};

    /**
     * Manual Test icons
     */
    public static final int[] arrManualTotalTestResources = new int[]{R.drawable.ic_camera_svg, R.drawable.ic_display_svg, R.drawable.ic_gps2_new,
            R.drawable.ic_mic_speaker_, R.drawable.ic_touch_screen_svg, R.drawable.ic_multitouch_svg,R.drawable.ic_devicecasing_svg_64,R.drawable.flashimg,R.drawable.compass,R.drawable.ic_accelerometer_new,R.drawable.ic_face_detection_new,R.drawable.fingerprint};
    public static final int[] arrManualTotalTestResourcesID= new int[]{ConstantTestIDs.CAMERA_ID, ConstantTestIDs.DISPLAY_ID, ConstantTestIDs.GPS_ID,
            ConstantTestIDs.SPEAKER_MIC, ConstantTestIDs.TOUCH_SCREEN_ID, ConstantTestIDs.MULTI_TOUCH_ID,ConstantTestIDs.DEVICE_CASING_ID,ConstantTestIDs.FLASH,ConstantTestIDs.COMPASS,ConstantTestIDs.ACCELEROMETER,ConstantTestIDs.FaceDetection,ConstantTestIDs.FINGERPRINT};
    public static final int[] arrDualTestResourses={R.drawable.ic_camera_svg,R.drawable.ic_camera_svg,R.drawable.ic_volume_up_blue_svg_168,R.drawable.ic_volume_down_blue_svg_168,R.drawable.ic_speaker_svg,R.drawable.ic_mic_blue_svg_128};
    public static final int[] arrDualTestResoursesID={ConstantTestIDs.FRONT_CAMERA,ConstantTestIDs.BACK_CAMERA,ConstantTestIDs.VOLUME_UP,ConstantTestIDs.VOLUME_DOWN,ConstantTestIDs.SPEAKER_ID,ConstantTestIDs.MIC_ID};

    public static final int[] arrSemiAutomaticResources = new int[]{
            R.drawable.ic_jack_svg, R.drawable.ic_volume_svg, R.drawable.ic_power_svg, R.drawable.ic_chargingport_svg, R.drawable.ic_proximity,
            R.drawable.ic_home, R.drawable.ic_gyroscope_svg, R.drawable.ic_battery_new
    };

    public static final int[] arrSemiAutomaticResourcesID = new int[]{
            ConstantTestIDs.EAR_PHONE_ID, ConstantTestIDs.VOLUME_ID, ConstantTestIDs.POWER_ID, ConstantTestIDs.CHARGING_ID,ConstantTestIDs.PROXIMITY_ID,
           ConstantTestIDs.HOME_ID, ConstantTestIDs.GYROSCOPE_ID,ConstantTestIDs.Battery
    };
    public static final String[] arrAutomatedTestID = new String[]{
            JsonTags.MMR_34.name(), JsonTags.MMR_35.name(), JsonTags.MMR_39.name(),
            JsonTags.MMR_36.name(), JsonTags.MMR_16.name(),
            JsonTags.MMR_42.name(), JsonTags.MMR_49.name(), JsonTags.MMR_30.name(),
            JsonTags.MMR_28.name()
    };
    /*public static final int[] arrManualTestResources = new int[]{
            R.drawable.ic_camera_yell, R.drawable.ic_camera_red, R.drawable.ic_camera_gre, R.drawable.ic_camera_yell, R.drawable.ic_camera_red,
            R.drawable.ic_camera, R.drawable.ic_camera, R.drawable.ic_camera, R.drawable.ic_camera, R.drawable.ic_camera*//*, R.drawable.ic_power*//*,
            R.drawable.ic_camera, R.drawable.ic_camera, R.drawable.ic_camera*//*, R.drawable.ic_vibration*//*, R.drawable.ic_camera, R.drawable.ic_camera,
            R.drawable.ic_camera};*/

    /**
     * Constants for find device frequency
     */

    public static final String FILE_PATH_GOVERNOR = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_governor";
    public static final String FILE_PATH_MAX_FREQUENCY = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq";
    public static final String FILE_PATH_MIN_FREQUENCY = "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq";
    public static final String FILE_PATH_CURRENT_FREQUENCY = "/sys/devices/system/cpu/*/cpufreq/scaling_cur_freq";

    public static final int BLUETOOTH_ACTIVITY_RESULT = 0001;
    public static final String SMART_RUN = "isSmartRun";
    public static final int CALL_FUNCTION = 10;
    public static final String AUTOMATE = "Automate";
    public static final String MANUAL = "Manual";
    public static final String MANUAL1 = "Manual1";
    public static final long PAUSE_TIME = 1500;
    public static boolean IS_SMART_RUN = false;
    public static boolean isSkipButton = false;
    public static final String SCREEN_NAME = "ScreenName";
    public static final String SOCKET_ID = "SocketID";
    public static final String UNIQUE_ID = "UniqueID";
    public static final String SCROLL_TYPE="ScrollType";
    public static final String REQUEST_UNIQUE_ID = "RequestUniqueID";
    public static final String QRCODEID = "QrCodeID";
    public static final String UNIQUE_NO = "Unique";
    public static final String ORDER_ID = "OrderID";
    public static final String UDIID = "UDIID";
    public static String QR_CODE_RESULT = "Qr_code_result";
    public static String ANDROID_ID = "android_id";
    public static String StoreType = "storetype";

    public static String TradeInValidityDays = "tradeinvaliditydays";
    public static String QuoteExpireDays = "quoteexpiredays";
    public static final String FULL_NAME = "FirstName";
    public static final String MIDDLE_NAME = "MiddleName";
    public static final String LAST_NAME = "LastName";
    public static final String EMAIL = "EmailAddress";
    public static final String PHONE_NO = "ContactNumber";
    public static final String ADDRESS1 = "Address1";
    public static final String ADDRESS2 = "Address2";
    public static final String ADDRESS3 = "Address3";
    public static final String CITY = "City";
    public static final String POSTAL_CODE = "PostCode";
    public static final String ID_TYPE = "ID_Type";
    public static final String ID = "ID_value";
    public static final String IDENTITY_NUMBER="IdentityNumber";
    /**
     * Weightage for arc progress in automation test
     */
    public static int default_weightage = 0;
    public static int bluetooth_weightage = 15;
    public static int wifi_weightage = 15;
    public static int vibration_weightage = 15;
    public static int simcard_weightage = 15;
    public static int sdcard_weightage = 10;
    public static int killswitch_weightage = 10;
    public static int rooted_weightage = 10;
    public static int call_function_weightage = 10;
    public static boolean isBackButton = false;
    public static boolean isNextHandler = false;
    public static boolean isManualIndividualResume = false;
    public static final int REQ_CODE_DEFAULT_SETTINGS = 16061;
    /**
     * Constants for storing app lastdate and current date time
     * last date is stored on onPause method
     * current date is stored on onStart method
     */
    public static String lastDate_Time = "lastDate_Time";
    public static String currentDate_Time = "currentDate_Time";

    public static Date lastDateTime = null;
    public static Date currentDateTime = null;
    public static boolean isArcFirsTime = false;
    public static boolean isManualIndividual = false;
    public static int countTimerNormal = 10000;
    public static int countTimerLong = 15000;
    public static int countTimerLongExtra = 15000;
    public static boolean isWifiPermission = false;
    public static boolean isIndividualperforming = false;



    public static final String IN_PROGRESS = "inprogress";
    public static final String START = "Start";
    public static final String COMPLETED = "completed";
    public static final String FAILED = "failure";
    public static final String PENDING = "pending";


    public static final int TEST_PASS = 1;
    public static final int TEST_IN_QUEUE = 2;
    public static final int TEST_IN_PROGRESS = 3;
    public static final int TEST_FAILED = 0;
    public static final int TEST_NOTPERFORMED = -1;
    public static final int TEST_NOT_EXIST = -2;
    /**
     * This control toggle button on Manual Test screen is enable or disabled
     */
    public static boolean isDoAllClicked = true;

    /**
     * This flag is set to true when no scroll is required on Manual Test screen
     * in Manual Test Fragment if this flag is true then value of
     * index variable is set to 0
     */
    public static boolean isManualClickScroolController = false;
    /**
     * index point auto scrool value in Manual Test screen
     * index =0 means no scrool is required
     * if index=6 or any value then screen will automatically scroll to that position
     */
    public static int index = 0;
    public static boolean isSecondPage=false;

    /**
     * Its is used to perform operation in NavigationDrawerFragment
     * when we are in fragments
     * This is initialize in MainActivity
     */
    public static int  RESPONSE_SUCCESS=200;
    public static String ENTERPRISEPATNERID="EnterprisePartnerID";
    public static String USERID="UserID";
    public static String STOREID="StoreID";
    public static String SKUID="SkuID";
    public static String DEVICEPRICE="device_price";
    public static String LOCATION_ID="LocationId";
    public static String CACHEMANAGERID="CacheManagerID";
    public static String CURRENCYSYMBOL="CurrencySymbol";
    public static NavigationDrawerFragment navigationDrawerFragment = null;
    public static long totalTimeCountInMilliseconds;
    public static boolean isDashBoardVisible = false;
    public static boolean isTimerFinished = false;
    public static long secondsRemaining;
    public static InterfaceTimerCallBack interfaceTimerCallBack = null;
    public static String onTimerCallbackReceiver = "countDownTimer";
    public static String onVolumeKeyPressed = "android.media.VOLUME_CHANGED_ACTION";
    public static boolean isTimerRunning = false;
    public static CountDownTimerTask timerCallback = null;
    public static Toast toast = null;
    public static boolean isPagerElementTwoVisibleManual = false;
    public static boolean isSemiAutoVisible = true;
    public static boolean isManualTotalVisible = true;
    public static boolean isAutoVisible = false;
    public static boolean isBarcodeVisible=false;
    public static final String IS_TEST_EXIST="_NOT_EXIST";

    public static final String IS_LIGHT_SENSOR_NOT_EXIST = "islightsensorexist";
    public static final String IS_PROXIMITY_SENSOR__NOT_EXIST = "isproximitysensorexist";

    public static final String isAutomatedTestFirst = "isAutomatedTestFirst";
    public static final String automationTestProgress = "automationTestProgress";
    public static final int VIBRATION_TEST = 39;
    public static final int BLUETOOTH_TEST = 34;
    public static final int WIFI_TEST = 35;
    public static final int SIMCARD_TEST = 36;
    public static final int SDCARD_TEST = 16;
    public static final int BLUETOOTH__DISABLE_TEST = 8;
    public static final int KILLSWITCH_TEST = 42;
    public static final int FMIP = 41;
    public static final int ILIGHT_SENSOR = 28;


    public static final int IJACK = 18;
    public static final int IVOLUME = 54;
    public static final int IPOWER = 45;
    public static final int ICHARGING = 44;
    public static final int IPROXIMITY = 23;
    public static final int IHOME = 26;
    public static final int IGYROSCOPE = 40;
    public static final int ICAMERA = 56;
    public static final int IDISPLAY = 57;
    public static final int IGPS = 31;
    public static final int ISPEAKER = 32;
    public static final int IMIC = 33;
    public static final int ITOUCH_SCREEN = 24;
    public static final int IMULTI_TOUCH = 22;
    public static final int IDEVICE_CASTING = 55;

    public static final int IDISPLAY_ONE = 46;
    public static final int IDISPLAY_TWO = 47;
    public static final int IFLASH =63;

    public static final int SPEAKER_MIC =71;
    public static final int SPEAKER_ONE =72;
    public static final int SPEAKER_TWO =73;
    public static final int SPEAKER_THREE =74;
    public static final int SPEAKER_FOUR =75;

    public static final String JACK = "Jack";
    public static final String VOLUME_BUTTONS = "Volume Buttons";
    public static final String POWER_BUTTON = "Power Button";
    public static final String CHARGING_PORT = "Charging Port";
    public static final String HOME = "Home";
    public static final String PROXIMITY_SENSON = "Proximity Sensor";
    public static final String GYROSCOPE = "Gyroscope";
    public static final String CAMERA = "Camera";
    public static final String DISPLAY = "Display";
    public static final String SPEAKER = "Speaker";
    public static final String MIC = "Speaker and Mic";
    public static final String GPS = "GPS";
    public static final String MULTITOUCH = "Multi Touch";
    public static final String TOUCH_SCREEN = "Touch Screen";
    public static final String DEVICE_CASING = "Device Casing";
    public static final String LIGHT_SENSOR = "Light Sensor";
    public static final String VOLUME_UP = "Volume_up";
    public static final String VOLUME_DOWN = "Volume_down";
    public static final String PARTIAL = "partial";
    public static final String ENTER_FIRST_NAME = "Enter First Name";
    public static final String ENTER_LAST_NAME = "Enter Last Name";
    public static final String ENTER_Email = "Enter Email Id";
    public static final String ENTER_PHONE = "Enter Phone No";
    public static final String ENTER_ID = "Enter Unique Identification Id";
    public static final String VALID_EMAIL = "Enter Valid email";

    public static String MANUAL2 = "Manual2";

    public static final String VIBRATION_TEST_NAEM = "Vibration";
    public static final String BLUETOOTH_TEST_NAEM = "Bluetooth";
    public static final String WIFI_TEST_NAEM = "Wifi";
    public static final String SIMCARD_TEST_NAEM = "Sim Card Removed";
    public static final String SDCARD_TEST_NAEM = "SD Card Removed";
    public static final String BLUETOOTH__DISABLE_TEST_NAEM = "Bluetooth Disable";
    public static final String KILLSWITCH_TEST_NAEM = "Kill Switch Disabled";
    public static final String FMIP_NAEM = "Not Rooted";
    public static final String CALL_FUNCTION_NAEM = "Call Function";
    public static final String LIGHT_SENSOR_NAEM = "Light Sensor";
    public static final String IS_SAVE_API_CALL="isSaveapicall";
    public static final String BAROMETER_SENSOR_NAEM = "Barometer";
    public static final String FIELD_NAME = "Field_Name" ;
    public static final String FIELD_VALUE = "Field_Value" ;
    public static String TrackingID="TrackingID";

    public interface ManualTestConstant {
        /**
         * Manual Test Constants
         */
        int EAR_JACK = 0;
        int VOLUME = 1;
        int POWER = 2;
        int CHARGING = 3;
        int PROXIMITY = 4;
        int HOME = 5;
        int GYROSCOPE = 6;
        int LIGHT_SENSOR = 7;
        int CAMERA = 0;
        int DISPLAY = 1;
        int GPS = 2;
        int SPEAKER = 3;
        int SCREEN_TOUCH = 4;
        int MULTITOUCH = 5;
        int CASING = 6;

        //added by ajit
        int FLASH = 8;

    }
}
