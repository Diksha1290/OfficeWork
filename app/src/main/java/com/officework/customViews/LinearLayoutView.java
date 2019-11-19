package com.officework.customViews;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.officework.constants.AppConstantsTheme;

public class LinearLayoutView extends LinearLayout {


    public LinearLayoutView(Context context) {
        super(context);
    }

    public LinearLayoutView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        GradientDrawable gradientDrawable =(GradientDrawable) getBackground();
      //  gradientDrawable.setStroke(4,getResources().getColor(AppConstantsTheme.styleTheme.iconColor));
        gradientDrawable.setStroke(4,AppConstantsTheme.styleTheme.iconColor);
    }

    public LinearLayoutView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public LinearLayoutView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
