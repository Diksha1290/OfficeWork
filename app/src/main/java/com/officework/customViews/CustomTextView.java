package com.officework.customViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;

import com.officework.R;


/**
 * This class is used as Widget instead of TextView.This class has also a custom
 * attribute which is used in xml file.
 * <p/>
 * This attribute is customtypeface support string value pass name of typeface
 * of using in asses folder here. It will automatically set on TextView text.
 * </P>
 */
public class CustomTextView extends androidx.appcompat.widget.AppCompatTextView {

    public CustomTextView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        if (attrs != null) {
            if (!isInEditMode()) {
                TypedArray ta = context.obtainStyledAttributes(attrs,
                        R.styleable.CustomTextView);
                String typeface = ta
                        .getString(R.styleable.CustomTextView_customtypeface);
                String typeSize = ta
                        .getString(R.styleable.CustomTextView_customtextsize);
                String typeWidth = ta
                        .getString(R.styleable.CustomTextView_customwidth);
                String typeHeight = ta
                        .getString(R.styleable.CustomTextView_customheight);
                boolean isMandatory = ta.getBoolean(
                        R.styleable.CustomTextView_ismandatory, false);

                if (isMandatory) {
                    setMandatoryField();
                }
                if (!TextUtils.isEmpty(typeSize)) {
                    setTextViewSize(Float.valueOf(typeSize));
                }
                if (!TextUtils.isEmpty(typeWidth)
                        && !TextUtils.isEmpty(typeHeight)) {
                    setTextFieldSize(Float.valueOf(typeWidth),
                            Float.valueOf(typeHeight));
                }

                ta.recycle();
                if (!TextUtils.isEmpty(typeface)) {
                    Typeface tf = Typeface.createFromAsset(context.getAssets(),
                            typeface);
                    setTypeface(tf);
                }
            }
        }
    }

    /**
     * This method is used to setTextSize. In this float value is changed to
     * scaledDenisty and then set to the editText
     *
     * @param px float size of editText
     */
    public void setTextViewSize(float px) {
        float scaledDensity = getResources().getDisplayMetrics().scaledDensity;
        int sp = (int) (px / scaledDensity);
        setTextSize(sp);
    }

    public void setTextFieldSize(float width, float height) {
        float scaledDensity = getResources().getDisplayMetrics().densityDpi;
        int widthDp = (int) (width / scaledDensity);
        int heightDp = (int) (height / scaledDensity);
        setWidth(widthDp);
        setHeight(heightDp);
    }

    /**
     * Method to set textfield as mandatory fiels. It will highlight the * sign
     * in red color.
     */

    public void setMandatoryField() {
        try {

            String textToSpan = getText().toString().trim();
            Spannable str = new SpannableString(getText());
            int loc = textToSpan.indexOf("*");
            str.setSpan(new ForegroundColorSpan(Color.RED), loc,
                    textToSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            setText(str);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
