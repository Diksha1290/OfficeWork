package com.officework.customViews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;


public class GilroyLightTextView extends TextView {

    private static Typeface mTypeface;

    public GilroyLightTextView(final Context context) {
        this(context, null);
    }

    public GilroyLightTextView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GilroyLightTextView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);

        if (mTypeface == null) {
            mTypeface = Typeface.createFromAsset(context.getAssets(), "fonts/Gilroy-Light.ttf");
        }
        setTypeface(mTypeface);
    }

}
