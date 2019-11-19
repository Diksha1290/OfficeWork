package com.officework.activities;

import android.content.Context;
import android.content.SharedPreferences;

public class ThemeManager {


    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context context;
    int PRIVATE_MODE=0;

    private static final String PREF_NAME= "ThemePref";
    public static final String KEY_THEME_NAME = "themeName";
    public static final String KEY_ICON_NAME = "iconName";

    public ThemeManager(Context applicationContext) {
        this.context=applicationContext;
        pref=context.getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        editor=pref.edit();
    }

    public void setTheme(String theme){

        editor.putString(KEY_THEME_NAME,theme);
        editor.commit();
    }

    public String getTheme(){
      String theme = pref.getString(KEY_THEME_NAME,null);
        return theme;

    }
    public void setIconColor(String iconColor){

        editor.putString(KEY_ICON_NAME,iconColor);
        editor.commit();
    }
    public String getIconColor(){
        String iconColor = pref.getString(KEY_ICON_NAME,null);
        return iconColor;

    }
}
