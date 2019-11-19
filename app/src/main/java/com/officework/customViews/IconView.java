package com.officework.customViews;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import android.util.AttributeSet;

import com.officework.R;
import com.officework.constants.AppConstantsTheme;

/**
 * A customized squared image view class.
 *
 * Extra functions:

 * {@link @setBitmap}
 * <p/>
 * The rest of the things is exactly the same as {@link android.widget.ImageView}
 * <p/>
 * Created by amintavassolian on 15-02-12.
 */

public class IconView extends androidx.appcompat.widget.AppCompatImageView {
    boolean theme_color = true;

    public IconView(Context context) {
        this(context, null);
    }

    public IconView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.icon);
    }

    public IconView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.IconView, 0, 0);
        try {
            theme_color = ta.getBoolean(R.styleable.IconView_theme_color, true);
        } finally {
            ta.recycle();
        }

        if(theme_color) {

           // this.setColorFilter(ContextCompat.getColor(context, Color.parseColor("#F6F#F6")));
            this.setColorFilter(AppConstantsTheme.styleTheme.iconColor);

        }


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }





    public void setImageDrawable(@Nullable Drawable drawable,boolean selected,Context context) {
        super.setImageDrawable(drawable);

        if(selected) {
            this.setColorFilter(ContextCompat.getColor(context, R.color.green));
        }else {
            this.setColorFilter(ContextCompat.getColor(context, R.color.RedColor));

        }


    }

    public void setImageDrawable(@Nullable Drawable drawable,String selected,Context context) {
        super.setImageDrawable(drawable);

        if(selected.equals("blue")) {
            this.setColorFilter(AppConstantsTheme.styleTheme.iconColor);
        }


    }

    public void setImageDrawable(@Nullable Drawable drawable,Context context) {
        super.setImageDrawable(drawable);

        this.setImageDrawable(drawable);
        this.setColorFilter(null);



    }

    public int getIconColor(){
        return AppConstantsTheme.styleTheme.iconColor;
    }

    public void setImageDrawable(boolean b, Drawable drawable, Context activity) {
        if(b) {
            this.setColorFilter(ContextCompat.getColor(activity, R.color.app_blue_color));
        }else {
            this.setColorFilter(ContextCompat.getColor(activity, R.color.RedColor));

        }
    }
}