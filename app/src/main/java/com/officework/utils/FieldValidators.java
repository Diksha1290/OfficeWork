package com.officework.utils;

import android.app.Activity;
import android.content.Context;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FieldValidators {

    private final Context context;

    public FieldValidators(Context context) {
        this.context = context;
    }

    /**
     * @param editText - EditText field which need to be validated
     * @return - Returns true if editText is Null or empty
     */
    public static boolean isNullOrEmpty(EditText editText) {
        return editText.getText() == null
                || editText.getText().toString().trim().length() == 0;
    }

    public static boolean isNullOrEmptyText(TextView textView) {
        return textView.getText() == null
                || textView.getText().toString().trim().length() == 0;
    }

    public static boolean containsAlphabet(EditText editText) {
        return editText.getText() == null
                || Pattern.matches("[a-zA-Z]+", editText.getText().toString().trim());

            }

    /**
     * @param autoCompleteTextView - AutoCompleteTextView field which need to be validated
     * @return Returns true if AutoCompleteTextView is Null or empty
     */
    public static boolean isNullOrEmpty(
            AutoCompleteTextView autoCompleteTextView) {
        return autoCompleteTextView.getText() == null
                || autoCompleteTextView.getText().toString().length() == 0;
    }



    public static boolean isEmailValid(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9\\._%+-]+(\\.[_A-Za-z0-9-]+\\.)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }






    public static boolean isNullOrEmpty(Activity activity, EditText editText, String error, int min_limit) {
        if( editText.getText() != null){
            if((editText.getText().toString().trim().length() < min_limit)){
                //editText.setError(error);

              //  Utilities.getInstance(activity).showToast(activity,error);
                editText.requestFocus();
                editText.setError(error);
                editText.setFocusable(true);
                return true;
            }else {
                return false;
            }
        }else {
            return true;
        }

    }

    public static boolean isValidEmail(Activity activity, EditText emailView, String error) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(emailView.getText().toString());
        if(matcher.matches()){
            return true;
        }else {
          //  Utilities.getInstance(activity).showToast(activity,error);
            emailView.requestFocus();
            emailView.setError(error);
            return false;

        }

    }
}
