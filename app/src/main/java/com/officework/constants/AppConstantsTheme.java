package com.officework.constants;

import android.graphics.Color;

import com.officework.R;

/**
 * Created by Diksha on 9/24/2018.
 */

public class AppConstantsTheme {

    public static Theme theme=Theme.BLACK;

    public static Theme getTheme() {
        return theme;
    }
    public static StyleTheme styleTheme = new StyleTheme();

    public static void setStyleTheme(){

        theme = getTheme();
        switch (theme){
            case BLUE:
                setColor(R.color.stor1,R.color.stor1,R.color.white,R.color.stor1,R.color.white);
                break;

            case BLACK:
                setColor(R.color.Black,R.color.Black,R.color.white,R.color.app_blue_color,R.color.RedColor);
                break;

            case GREEN:
                setColor(R.color.stor2,R.color.stor2,R.color.white,R.color.stor2,R.color.white);
                break;

            case RED:
                setColor(R.color.RedColor,R.color.RedColor,R.color.white,R.color.green_color,R.color.white);
                break;
            case BROWN:
                setColor(R.color.brown,R.color.brown,R.color.white,R.color.brown,R.color.white);
                break;

        }
    }

    public static void setColor(int iconColor, int backgroundColor, int titleTextColor, int toolbarColor, int colorAccent) {
        styleTheme.iconColor= iconColor;
        styleTheme.backgroundColor= backgroundColor;
        styleTheme.titleTextColor= titleTextColor;
        styleTheme.toolbarColor= toolbarColor;
        styleTheme.colorAccent=colorAccent;
    }

    public static void setIconColor(String color){

        int colorValue = Color.parseColor(color);
        styleTheme.iconColor = colorValue;
        styleTheme.toolbarColor = colorValue;

    }
    public static int getIconColor(){
        int theme1=styleTheme.iconColor;

        return theme1;

    }
    public static void setTitleBarColor(String color){
        int colorValue = Color.parseColor(color);

    }

    public enum Theme{
        BLACK,
        BLUE,
        GREEN,
        RED,
        GRAY,
        BROWN
    }


}
