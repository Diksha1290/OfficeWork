package com.officework.customViews;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.core.content.ContextCompat;
import android.util.AttributeSet;

import com.officework.constants.AppConstantsTheme;

/**
 * Created by girish.sharma on 7/28/2016.
 */
public class CustomButton extends androidx.appcompat.widget.AppCompatButton {

    public CustomButton(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        //  this.setBackgroundResource(AppConstantsTheme.styleTheme.backgroundColor);
        try {
            //   this.setTextColor(ContextCompat.getColor(context,AppConstantsTheme.styleTheme
            //   .titleTextColor));
            this.setBackgroundColor(AppConstantsTheme.styleTheme.backgroundColor);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("ResourceType")
    public void setButtonColor(Context context, Boolean value) {


        if (value) {
            this.setBackgroundResource(ContextCompat.getColor(context, (AppConstantsTheme.styleTheme.backgroundColor)));
        }


    }
}
