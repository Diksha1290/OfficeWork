package com.officework.utils;

import android.text.TextUtils;

public final class StringUtil {

    /**
     * Empty Constructor
     * not called
     */
    private StringUtil() {
    }

    /**
     * verify input string is null or empty
     *
     * @param str
     * @return
     */
    public static boolean isNullorEmpty(String str) {
        return !(!TextUtils.isEmpty(str) && !str.equals("null"));
    }

}