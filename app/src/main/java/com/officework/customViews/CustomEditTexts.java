package com.officework.customViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;

import com.officework.R;

/**
 * Created by girish.sharma on 7/28/2016.
 */

public class CustomEditTexts extends androidx.appcompat.widget.AppCompatEditText {

    public CustomEditTexts(Context context) {
        super(context);
    }

    public CustomEditTexts(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (attrs != null) {
            if (!isInEditMode()) {
                if (InputType.TYPE_TEXT_VARIATION_PASSWORD == getInputType()) {
                    setTransformationMethod(new PasswordTransformationMethod());
                }
                TypedArray ta = context.obtainStyledAttributes(attrs,
                        R.styleable.CustomTextView);
                String typeface = ta
                        .getString(R.styleable.CustomTextView_customtypeface);
                ta.recycle();
                if (!TextUtils.isEmpty(typeface)) {
                    Typeface tf = Typeface.createFromAsset(context.getAssets(),
                            typeface);
                    setTypeface(tf);
                }
                addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence s, int start,
                                              int before, int count) {
                        // setBackgroundResource(R.drawable.selector_edittext);

                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start,
                                                  int count, int after) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

            }
        }

    }

    /**
     * **********************************************************************
     * This method is used to setTextSize. In this float value is changed to
     * scaledDenisty and then set to the editText
     *
     * @param px float size of editText
     *           <p/>
     *           *************************************************************************
     */
    public void setTextViewSize(float px) {
        float scaledDensity = getResources().getDisplayMetrics().scaledDensity;
        int sp = (int) (px / scaledDensity);
        setTextSize(sp);
    }
}
