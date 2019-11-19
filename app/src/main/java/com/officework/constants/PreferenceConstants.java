package com.officework.constants;

import android.content.Context;
import android.graphics.Bitmap;

import com.officework.utils.Utilities;

/**
 * Created by Ashwani on 3/23/2017.
 */

public class PreferenceConstants {
    public static boolean isAppActive = false;
    public static boolean isTestPerformed;
    public static int LicenceNumber = -1;
    public static String udi = "";
    public static String MMR_1 = "";
    public static String MMR_2 = "";
    public static String MMR_3 = "";
    public static String MMR_4 = "";
    public static String LGE = "";
    public static String MMR_5 = "";
    public static String MMR_6 = "";
    public static String MMR_7 = "";
    public static String MMR_8 = "";
    public static String MMR_9 = "";
    public static String MMR_10 = "";
    public static String MMR_11 = "";
    public static String MMR_12 = "";
    public static String MMR_13 = "";
    public static String MMR_14 = "";
    public static String MMR_15 = "";
    public static int MMR_48 = -1;
    public static int MMR_49 = -1;
    public static int MMR_42 = -1;
    public static int RequestTypeID = 2;
    public static String MMR_17 = "";
    public static int MMR_19 = -1;
    public static String MMR_25 = "";
    public static String MMR_29 = "";
    public static int MMR_31 = -1;
    public static int MMR_32 = -1;
    public static int MMR_33 = -1;
    public static int MMR_18 = -1;
    public static int MMR_16 = -1;
    public static int MMR_20 = -1;
    public static int MMR_43 = -1;
    public static int MMR_21 = -1;
    public static int MMR_44 = -1;
    public static int MMR_22 = -1;
    public static int MMR_23 = -1;
    public static int MMR_24 = -1;
    public static int MMR_26 = -1;
    public static int MMR_28 = -1;
    public static int MMR_30 = -1;
    public static int MMR_27 = -1;
    public static int MMR_34 = -1;
    public static int MMR_35 = -1;
    public static int MMR_36 = -1;
    public static int MMR_38 = -1;
    public static int MMR_37 = -1;
    public static int MMR_46 = -1;
    public static int MMR_47 = -1;
    public static int MMR_39 = -1;
    public static int MMR_40 = -1;
    public static int MMR_45 = -1;
    public static int MMR_41 = -1;
    public static Bitmap imageBitmap = null;
    public static String Token="";
    public static String TokenType="";
    public static String SubscriberProductID="";
    public static String Udid="";
    public static boolean isUserDeclined=true;
    public static void compareIntegerValues(int key, int value, Utilities utils, Context context) {
        if (key != value) {
            key = value;
            isTestPerformed = true;
            utils.addPreferenceInt(context, JsonTags.LicenceNumber.name(), value);
            utils.addPreferenceBoolean(context, JsonTags.isTestDataChanged.name(), true);
        }

    }

    public static void compareStringValues(String key, String value, Utilities utils, Context context) {
        if (key != value) {
            key = value;
            isTestPerformed = true;
            utils.addPreference(context, JsonTags.LicenceNumber.name(), value);
            utils.addPreferenceBoolean(context, JsonTags.isTestDataChanged.name(), true);
        }
    }

}
