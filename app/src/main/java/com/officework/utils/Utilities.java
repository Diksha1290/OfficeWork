package com.officework.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.fingerprint.FingerprintManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;

import com.officework.testing_profiles.Controller.TestController;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.material.snackbar.Snackbar;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.officework.R;
import com.officework.activities.MainScreen;
import com.officework.asyncTasks.Automated_Test_Asynctask;
import com.officework.asyncTasks.CountDownTimerTask;
import com.officework.constants.AsyncConstant;
import com.officework.constants.Constants;
import com.officework.constants.JsonTags;
import com.officework.constants.Preferences;
import com.officework.interfaces.InterfaceAlertDissmiss;
import com.officework.interfaces.InterfaceTimerCallBack;
import com.officework.interfaces.WebServiceCallback;
import com.officework.models.ChannelLoginModel;
import com.officework.models.WebServiceRequestModel;
import com.officework.serverConnection.WebServiceAsyncTaskAbacusAPI;
import com.officework.serverConnection.WebServiceAsynctask;
import com.officework.testing_profiles.realmDatabase.model.SubTestMapModel;
import com.officework.testing_profiles.realmDatabase.model.TestModel;
import com.officework.testing_profiles.realmDatabase.operationManager.RealmOperations;
import com.officework.testing_profiles.utils.ConstantTestIDs;
import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.FINGERPRINT_SERVICE;

/**
 * Created by girish.sharma on 1/14/2016.
 */
public class Utilities {

    private static Toast toast;
    public SharedPreferences mAppPreferences;
    public SharedPreferences.Editor mEditor;
    public static AlertDialog syncdialog;
    public static AlertDialog successSyncDialog;
    com.securepreferences.SecurePreferences mAppSecurePreferences;
    com.securepreferences.SecurePreferences.Editor mSecureEditor;
    public static final int REQUEST_CODE_GALLERY = 0x1;
    public static final int REQUEST_CODE_TAKE_PICTURE = 0x2;
    public static final int REQUEST_CODE_TAKE_PICTURE_FRONT = 0x4;
    public static final int REQUEST_CODE_CROPPER = 0x3;
    private final String TEMP_PHOTO_FILE = "temporary_holder.jpg";
    public static boolean isTestDataChanged = false;
    private static String PASSWORD = null;
    private static final String FILENAME = "securefile";
    SensorManager sensorManager;
    boolean faceexists = true;
    boolean isFingerprintExist = true;
    public static int BUTTON_NEXT = R.string.textNext;
    public static int BUTTON_SKIP = R.string.textSkip;
    public static Context context;

    private static Utilities instance;
    private Toast mToastToShow = null;
    String messageBeingDisplayed = "";

    /**
     * static instance for Utilities class
     *
     * @param contextt
     * @return
     */
    public static Utilities getInstance(Context contextt) {
        context = contextt;
        if (instance == null) {
            instance = new Utilities();
        }


        return instance;
    }

    public Utilities() {

    }

    public Utilities(Context context) {
        this.context = context;
    }



    /**
     * Just to sleep the execution of process for sometime in between.
     */
    public void sleepThread(long pauseTime) {
        try {
            Thread.sleep(pauseTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
            logException(e, "Utilities_sleepThread", context);
        }
    }

    /**
     * convert string to base64 string
     *
     * @param string
     * @return
     */
    public String encodeStringToBase64(String string) {
        byte[] data = new byte[0];
        String base64;
        try {
            data = string.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        base64 = Base64.encodeToString(data, Base64.DEFAULT);
        Log.d("Pass Encode", base64);
        return base64;
    }

    /**
     * convert base64 string to string
     *
     * @param base64
     * @return
     */
    public String decodeBase64StringToString(String base64) {
        // Receiving side
        try {
            String text = null;
            byte[] data = Base64.decode(base64, Base64.DEFAULT);
            try {
                text = new String(data, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Log.d("Pass Encode", text);
            return text;
        } catch (Exception e) {
            logException(e, "Utilities_decodeBase64StringToString", context);
            return null;
        }

    }


    public void setButtonText(Button btn, int text) {
        btn.setText(text);
    }

    /**
     * clear both shared prefernces and secure shared preferences
     *
     * @param context
     */
    public void clearPreferences(Context context) {
        try {
            mAppPreferences = context.getSharedPreferences(Preferences.MEGAMMR_APP_PREF.name(), Context.MODE_PRIVATE);
            mEditor = mAppPreferences.edit();

            mSecureEditor.clear().commit();
            mEditor.clear().commit();
        } catch (Exception e) {
            e.printStackTrace();
            logException(e, "Utilities_clearPreferences", context);
        }

    }

    /**
     * Initialize secure shared preferences
     *
     * @param ctx
     */
    public void initializePreferences(Context ctx) {
        try {
            mAppSecurePreferences = new com.securepreferences.SecurePreferences(ctx, decodeBase64StringToString(ctx.getResources()
                    .getString(R.string.txtPassword)), "mega_mmr.xml");
            mSecureEditor = mAppSecurePreferences.edit();
        } catch (Exception e) {
            logException(e, "Utilities_initializePreferences", ctx);
        }

    }

    /**
     * show alert
     *
     * @param context
     */
    public void showAlertForChooseImageOptions(final Context context) {
        final CharSequence[] items = {"Take Photo", "Choose from Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Make your selection");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                if (item == 0) {
                    takePicture(context, false);
                } else if (item == 1) {
                    openGallery(context);
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * open gallery
     *
     * @param context
     */
    public void openGallery(Context context) {
        Intent i = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        ((Activity) context).startActivityForResult(Intent.createChooser(i, "Select Picture"), REQUEST_CODE_GALLERY);
    }

    /**
     * take picture from camera
     *
     * @param context
     * @param isFrontCam
     */

    public void takePicture(Context context, boolean isFrontCam) {
        if (isFrontCam) {
            Intent mIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            mIntent.putExtra("android.intent.extras.CAMERA_FACING", android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT);
            ((Activity) context).startActivityForResult(mIntent, REQUEST_CODE_TAKE_PICTURE_FRONT);
        } else {
            Intent mIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            mIntent.putExtra("android.intent.extras.CAMERA_FACING", 0);
            ((Activity) context).startActivityForResult(mIntent, REQUEST_CODE_TAKE_PICTURE);
        }
    }

    public Uri getTempUri(boolean isCreatingNewFile) {
        return Uri.fromFile(getTempFile(isCreatingNewFile));
    }

    public String getFilePath(boolean isCreatingNewFile) {
        File file = getTempFile(isCreatingNewFile);
        return file.getAbsolutePath();
    }

    /**
     * store current and last date time in preferences
     * This method is used to clear prefernces when app is active for
     * more tha 12 hour
     *
     * @param mContext
     * @param isLastTime
     */
    public static void storeDateTime(Context mContext, boolean isLastTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
            if (isLastTime) {
                instance.addPreferenceLong(mContext, Constants.lastDate_Time, 0L);

                SimpleDateFormat oldFormat = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
                Constants.lastDateTime = oldFormat.parse(sdf.format(new Date()));
                instance.addPreferenceLong(mContext, Constants.lastDate_Time, oldFormat.parse(sdf.format(new Date())).getTime());

            } else {
                instance.addPreferenceLong(mContext, Constants.currentDate_Time, 0L);

                SimpleDateFormat oldFormat = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
                Constants.currentDateTime = oldFormat.parse(sdf.format(new Date()));
                instance.addPreferenceLong(mContext, Constants.currentDate_Time, oldFormat.parse(sdf.format(new Date())).getTime());

            }
        } catch (Exception e) {
            Utilities utilities = new Utilities();
            utilities.logException(e, "storeDateTime()", mContext);
        }

    }



    public void showAlert(final Context ctx, final onAlertOkListener mListener,final Spanned Message, final String Title, final String positiveButtonText,
                          final String negativeButtonText, final String callbackID) {
        try {
            Handler handler = new Handler(Looper.getMainLooper());

            handler.post(new Runnable() {
                public void run() {
                    // Run your task here
                    AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
                    alert.setTitle(Title);
                    alert.setMessage(Message);
                    alert.setCancelable(false);
                    alert.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                            dialog.dismiss();

                        }
                    });
                    if (!TextUtils.isEmpty(negativeButtonText)) {
                        alert.setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (mListener != null)
                                    mListener.onOkButtonClicked(callbackID);
                                dialog.dismiss();
                            }
                        });
                    }
                  // alert.show();
                    AlertDialog dialog = alert.create();
                    dialog.show();
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setAllCaps(false);
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setAllCaps(false);

                }
            });


        } catch (Exception e) {
            Log.e(e.getMessage(), "compare_showAlert()");
        }

    }

    public void showAlert(final Context ctx, final onAlertOkListener mListener,final onAlertCancelListener mmListener2,final Spanned Message, final String Title, final String positiveButtonText,
                          final String negativeButtonText, final String callbackID) {
        try {
            Handler handler = new Handler(Looper.getMainLooper());

            handler.post(new Runnable() {
                public void run() {
                    // Run your task here
                    AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
                    alert.setTitle(Title);
                    alert.setMessage(Message);
                    alert.setCancelable(false);
                    alert.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (mmListener2 != null)
                                mmListener2.onOkButtonClicked1(callbackID);

                            dialog.dismiss();

                        }
                    });
                    if (!TextUtils.isEmpty(negativeButtonText)) {
                        alert.setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (mListener != null)
                                    mListener.onOkButtonClicked(callbackID);
                                dialog.dismiss();
                            }
                        });
                    }
                   // alert.show();
                    AlertDialog dialog = alert.create();
                    dialog.show();
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setAllCaps(false);
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setAllCaps(false);
                }
            });


        } catch (Exception e) {
            Log.e(e.getMessage(), "compare_showAlert()");
        }

    }

    /**
     * @ author girish.sharma
     * <p/>
     * This will check the difference between the two time.
     */

    public static long getTimeDifference(long startDate, long endDate) {
        try {
            //milliseconds
        /*long different = endDate.getTime() - startDate.getTime();*/
            long different = endDate - startDate;

            System.out.println("startDate : " + startDate);
            System.out.println("endDate : " + endDate);
            System.out.println("different : " + different);

            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = different / daysInMilli;
            different = different % daysInMilli;

            long elapsedHours = different / hoursInMilli;
            different = different % hoursInMilli;

            long elapsedMinutes = different / minutesInMilli;
            different = different % minutesInMilli;

            long elapsedSeconds = different / secondsInMilli;

            System.out.printf(
                    "%d days, %d hours, %d minutes, %d seconds%n",
                    elapsedDays,
                    elapsedHours, elapsedMinutes, elapsedSeconds);
            return elapsedHours;
        } catch (Exception e) {
            Utilities utilities = new Utilities();
            utilities.logException(e, "getTimeDifference()", context);
            return 0L;
        }


    }

    public int calculateInSampleSize(BitmapFactory.Options options,
                                     int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private File getTempFile(boolean isCreatingNewFile) {

        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {

            File file = new File(Environment.getExternalStorageDirectory(), TEMP_PHOTO_FILE);
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return file;
        } else {

            return null;
        }
    }

    /**
     * create an instanse of TelephonyManager class
     *
     * @param ctx
     * @return
     */
    private TelephonyManager getTelephonyManager(Context ctx) {
        TelephonyManager telephonyManager;
        telephonyManager = (TelephonyManager) ctx.getSystemService(ctx.TELEPHONY_SERVICE);
        return telephonyManager;
    }

    /**
     * return device id
     *
     * @param ctx
     * @return
     */
    public String getDeviceID(Context ctx) {
        return getTelephonyManager(ctx).getDeviceId();
    }

    /**
     * show alert
     *
     * @param ctx
     * @param mListener
     * @param Message
     * @param Title
     * @param positiveButtonText
     * @param negativeButtonText
     * @param callbackID
     */
    public void showAlert(Context ctx, final InterfaceAlertDissmiss mListener, String Message, String Title, String positiveButtonText,
                          String negativeButtonText, final int callbackID) {
        try {
            AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
            alert.setTitle(Title);
            alert.setMessage(Message);
            alert.setCancelable(false);
            alert.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mListener.onButtonClick(false, callbackID);
                }
            });
            if (!TextUtils.isEmpty(negativeButtonText)) {
                alert.setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        mListener.onButtonClick(true, callbackID);
                    }
                });
            }
            alert.show();
        } catch (Exception e) {
            logException(e, "compare_showAlert()", ctx);
        }

    }
    public static void showAlert(final Context ctx, final onAlertOkListener mListener, final String Message, final String Title, final String positiveButtonText,
                          final String negativeButtonText, final String callbackID) {
        try {
            Handler handler = new Handler(Looper.getMainLooper());

            handler.post(new Runnable() {
                public void run() {
                    // Run your task here
                    AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
                    alert.setTitle(Title);
                    alert.setMessage(Message);
                    alert.setCancelable(false);
                    alert.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (mListener != null)
                                mListener.onOkButtonClicked(callbackID);
                            dialog.dismiss();
//                            successSyncDialog= null;

                        }
                    });
                    if (!TextUtils.isEmpty(negativeButtonText)) {
                        alert.setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
//                                successSyncDialog= null;
                            }
                        });
                    }
//                successSyncDialog =
                        alert.show();


                }
            });


        } catch (Exception e) {
            Log.e(e.getMessage(), "compare_showAlert()");
        }

    }










    public void showAlert(Context ctx, final InterfaceAlertDissmiss mListener, Spanned Message, String Title, String positiveButtonText,
                          String negativeButtonText, final int callbackID) {
        try {
            AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
            alert.setTitle(Title);
            alert.setMessage(Message);
            alert.setCancelable(false);
            alert.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mListener.onButtonClick(false, callbackID);
                }
            });
            if (!TextUtils.isEmpty(negativeButtonText)) {
                alert.setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        mListener.onButtonClick(true, callbackID);
                    }
                });
            }
            alert.show();
        } catch (Exception e) {
            logException(e, "compare_showAlert()", ctx);
        }

    }





    /**
     * show alert
     *
     * @param ctx
     * @param mListener
     * @param Message
     * @param Title
     * @param positiveButtonText
     * @param negativeButtonText
     * @param callbackID
     * @param id
     */
    public void showAlert(Context ctx, final InterfaceAlertDissmiss mListener, String Message, String Title, String positiveButtonText,
                          String negativeButtonText, final int callbackID, final int id) {
        try {
            AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
            alert.setTitle(Title);
            alert.setMessage(Message);
            alert.setCancelable(false);
            alert.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mListener.onButtonClick(true, callbackID, id);
                }
            });
            if (!TextUtils.isEmpty(negativeButtonText)) {
                alert.setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onButtonClick(false, callbackID, id);
                    }
                });
            }
            alert.show();
        } catch (Exception e) {
            logException(e, "compare_showAlert()", ctx);
        }

    }

    public void showAlert(Context ctx, final InterfaceAlertDissmiss mListener, String Message, String Title, Spanned positiveButtonText,
                          Spanned negativeButtonText, final int callbackID, final int id) {
        try {
            AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
            alert.setTitle(Title);
            alert.setMessage(Message);
            alert.setCancelable(false);
            alert.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mListener.onButtonClick(true, callbackID, id);
                }
            });
            if (!TextUtils.isEmpty(negativeButtonText)) {
                alert.setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onButtonClick(false, callbackID, id);
                    }
                });
            }
            alert.show();

        } catch (Exception e) {
            logException(e, "compare_showAlert()", ctx);
        }

    }



    public void showAlert(Context ctx, final InterfaceAlertDissmiss mListener, Spanned Message, String Title, String positiveButtonText,
                          String negativeButtonText, final int callbackID, final int id) {
        try {
            AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
            alert.setTitle(Title);
            alert.setMessage(Message);
            alert.setCancelable(false);
            alert.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mListener.onButtonClick(true, callbackID, id);
                }
            });
            if (!TextUtils.isEmpty(negativeButtonText)) {
                alert.setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onButtonClick(false, callbackID, id);
                    }
                });
            }
            alert.show();
        } catch (Exception e) {
            logException(e, "compare_showAlert()", ctx);
        }

    }


    /**
     * show short toast
     *
     * @param ctx
     * @param Message
     */
    public void showToast(Context ctx, String Message) {

        try {
            int toastDuration = 1000; // in MilliSeconds

            final Toast mToast = Toast.makeText(ctx, Message, Toast.LENGTH_SHORT);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mToast.show();
            }
        });




            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    mToast.cancel();
                }
            },800);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        /*showCustomToast(ctx,Message,1000);*/
    }

    /**
     * show long toast
     *
     * @param ctx
     * @param Message
     */
    public void showToastLong(Context ctx, String Message) {
        try {
            Toast.makeText(ctx, Message, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            logException(e, "compare_showToastLong()", ctx);
        }

    }

    /**
     * Show Toast message for a specific duration, does not show again if the message is same
     *
     * @param message     The Message to display in toast
     * @param timeInMSecs Time in ms to show the toast
     */
    public void showCustomToast(Context context, String message, int timeInMSecs) {
        if (mToastToShow != null && message == messageBeingDisplayed) {
            Log.d("DEBUG", "Not Showing another Toast, Already Displaying");
            return;
        } else {
            Log.d("DEBUG", "Displaying Toast");
        }
        messageBeingDisplayed = message;
        // Set the toast and duration
        int toastDurationInMilliSeconds = timeInMSecs;
        mToastToShow = Toast.makeText(context, message, Toast.LENGTH_LONG);

        // Set the countdown to display the toast
        CountDownTimer toastCountDown;
        toastCountDown = new CountDownTimer(toastDurationInMilliSeconds, timeInMSecs /*Tick duration*/) {
            public void onTick(long millisUntilFinished) {
                if (mToastToShow != null) {
                    mToastToShow.show();
                }
            }

            public void onFinish() {
                if (mToastToShow != null) {
                    mToastToShow.cancel();
                }
                // Making the Toast null again
                mToastToShow = null;
                // Emptying the message to compare if its the same message being displayed or not
                messageBeingDisplayed = "";
            }
        };

        // Show the toast and starts the countdown
        mToastToShow.show();
        toastCountDown.start();
    }

    /**
     * show snack bar
     *
     * @param view
     * @param message
     * @param color
     */
    public void showSnackBar(final View view, final String message, final int color) {
        try {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    Snackbar snackbar = Snackbar
                            .make(view, message, Snackbar.LENGTH_LONG);

                    // Changing action button text color
                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
                    textView.setTextColor(color);
                    textView.setGravity(Gravity.CENTER);
                    snackbar.show();
                }
            });

        } catch (Exception e) {
            logException(e, "compare_showSnackBar()", context);
        }

    }

    /**
     * capitalize first letter of input string
     *
     * @param input
     * @return
     */
    public String capFirstLetter(String input) {
        try {
            return input.substring(0, 1).toUpperCase() + input.substring(1, input.length());
        } catch (Exception e) {
            logException(e, "compare_addLog()", context);
            return input;
        }

    }

    /**
     * add log message
     *
     * @param ctx
     * @param Tag
     * @param Message
     */
    public void addLog(Context ctx, String Tag, String Message) {
        try {
            Log.d(Tag, Message);
        } catch (Exception e) {
            logException(e, "compare_addLog()", context);
        }

    }

    /**
     * This method update and compare value of secure shared prefernces strings
     *
     * @param context
     * @param pref_field
     * @param pref_value
     */

    public void compare_UpdateSecurePreferenceString(Context context, String pref_field, String pref_value) {
        try {
            String currentValue = getSecurePreference(context, pref_field, "");
            if (!currentValue.equalsIgnoreCase(pref_value)) {
                addSecurePreference(context, pref_field, pref_value);
            }
        } catch (Exception e) {
            logException(e, "compare_UpdateSecurePreferenceInt()", context);
        }

    }

    /**
     * This method update and compare value of secure shared prefernces INT
     *
     * @param context
     * @param pref_field
     * @param pref_value
     */
    public void compare_UpdateSecurePreferenceInt(Context context, String pref_field, int pref_value) {

        try {
            int currentValue = getSecurePreferenceInt(context, pref_field, 0);
            if (currentValue != pref_value) {
                addSecurePreferenceInt(context, pref_field, pref_value);
            }
        } catch (Exception e) {
            logException(e, "compare_UpdateSecurePreferenceInt()", context);
        }
    }

    /**
     * This method add value tosecure shared prefernces string
     *
     * @param context
     * @param pref_field
     * @param pref_value
     */
    public String addSecurePreference(Context context, String pref_field, String pref_value) {
        try {
            mSecureEditor.putString(pref_field, pref_value);
            mSecureEditor.apply();
            return mAppSecurePreferences.getString(pref_field, "");
        } catch (Exception e) {
            logException(e, "Utilities_addSecurePreference()", context);
            return "";
        }

    }

    /**
     * This method add value tosecure shared prefernces integer
     *
     * @param context
     * @param pref_field
     * @param pref_value
     */
    public int addSecurePreferenceInt(Context context, String pref_field, int pref_value) {
        try {
            mSecureEditor.putInt(pref_field, pref_value);
            mSecureEditor.apply();
            return mAppSecurePreferences.getInt(pref_field, 0);
        } catch (Exception e) {
            logException(e, "Utilities_addSecurePreferenceInt()", context);
            return 0;
        }

    }

    /**
     * This method get value from secure shared prefernces string
     *
     * @param context
     * @param pref_field
     * @param def_value
     */
    public String getSecurePreference(Context context, String pref_field, String def_value) {

        try {
            return mAppSecurePreferences.getString(pref_field, def_value);
        } catch (Exception e) {
            logException(e, "Utilities_getSecurePreference()", context);
            return def_value;
        }

    }

    /**
     * This method get value from secure shared prefernces int
     *
     * @param context
     * @param pref_field
     * @param def_value
     */
    public int getSecurePreferenceInt(Context context, String pref_field, int def_value) {
        try {
            return mAppSecurePreferences.getInt(pref_field, -1);
        } catch (Exception e) {
            logException(e, "Utilities_getSecurePreferenceInt()", context);
            return -1;
        }

    }

    /**
     * This method update and compare value of sshared prefernces string
     *
     * @param context
     * @param pref_field
     * @param pref_value
     */
    public boolean compare_UpdatePreferenceString(Context context, String pref_field, String pref_value) {
        try {
            String currentValue = getPreference(context, pref_field, "");
            if (!currentValue.equalsIgnoreCase(pref_value)) {
                addPreference(context, pref_field, pref_value);
                isTestDataChanged = true;
                addPreferenceBoolean(context, JsonTags.isTestPerformedDataChanged.name(), true);
                addPreferenceBoolean(context, JsonTags.isTestDataChanged.name(), true);
                return true;
            }
        } catch (Exception e) {
            logException(e, "Utilities_compare_UpdatePreferenceString()", context);
        }

        return false;
    }

    /**
     * This method update and compare value of sshared prefernces integer
     *
     * @param context
     * @param pref_field
     * @param pref_value
     */
    public boolean compare_UpdatePreferenceInt(Context context, String pref_field, int pref_value) {
        try {
            int currentValue = getPreferenceInt(context, pref_field, 0);
            if (currentValue != pref_value) {
                addPreferenceInt(context, pref_field, pref_value);
                isTestDataChanged = true;
                /**
                 * This value help us to find is any test status or value needs to be updated on the
                 * Server or not.
                 * */
                addPreferenceBoolean(context, JsonTags.isTestPerformedDataChanged.name(), true);
                addPreferenceBoolean(context, JsonTags.isTestDataChanged.name(), true);
                return true;
            }
        } catch (Exception e) {
            logException(e, "Utilities_compare_UpdatePreferenceInt()", context);
        }

        return false;
    }

    /**
     * this method is used to store long shared preferences
     *
     * @param context
     * @param pref_field
     * @param pref_value
     * @return
     */
    public Long addPreferenceLong(Context context, String pref_field, Long pref_value) {
        try {
            mAppPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            mEditor = mAppPreferences.edit();
            mEditor.putLong(pref_field, pref_value);
            mEditor.apply();
            return mAppPreferences.getLong(pref_field, 0L);
        } catch (Exception e) {
            logException(e, "Utilities_addPreferenceLong()", context);
            return pref_value;
        }

    }

    /**
     * this method is used to get long shared preferences
     *
     * @param context
     * @param pref_field
     * @param def_value
     * @return
     */
    public Long getPreferenceLong(Context context, String pref_field, Long def_value) {
        try {
            mAppPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            return mAppPreferences.getLong(pref_field, def_value);
        } catch (Exception e) {
            logException(e, "Utilities_getPreferenceLong()", context);
            return def_value;
        }

    }

    /**
     * this method is used to add string shared preferences
     *
     * @param context
     * @param pref_field
     * @param pref_value
     * @return
     */

    public String addPreference(Context context, String pref_field, String pref_value) {
        try {
            mAppPreferences = context.getSharedPreferences(Preferences.MEGAMMR_APP_PREF.name(), Context.MODE_PRIVATE);
            mEditor = mAppPreferences.edit();
            mEditor.putString(pref_field, pref_value);
            mEditor.apply();
            return mAppPreferences.getString(pref_field, "");
        } catch (Exception e) {
            logException(e, "Utilities_getPreference()", context);
            return "";
        }

    }


    /**
     * this method is used to add integer shared preferences
     *
     * @param context
     * @param pref_field
     * @param pref_value
     * @return
     */
    public int addPreferenceInt(Context context, String pref_field, int pref_value) {
        try {
            mAppPreferences = context.getSharedPreferences(Preferences.MEGAMMR_APP_PREF.name(), Context.MODE_PRIVATE);
            mEditor = mAppPreferences.edit();
            mEditor.putInt(pref_field, pref_value);
            mEditor.apply();
            return mAppPreferences.getInt(pref_field, 0);
        } catch (Exception e) {
            logException(e, "Utilities_getPreference()", context);
            return -1;
        }

    }

    /**
     * this method is used to add boolean shared preferences
     *
     * @param context
     * @param pref_field
     * @param pref_value
     * @return
     */
    public boolean addPreferenceBoolean(Context context, String pref_field, Boolean pref_value) {

        try {
            mAppPreferences = context.getSharedPreferences(Preferences.MEGAMMR_APP_PREF.name(), Context.MODE_PRIVATE);
            mEditor = mAppPreferences.edit();
            mEditor.putBoolean(pref_field, pref_value);
            mEditor.apply();
            return mAppPreferences.getBoolean(pref_field, false);
        } catch (Exception e) {
            logException(e, "Utilities_getPreference()", context);
            return false;
        }

    }

    /**
     * this method is used to get string shared preferences
     *
     * @param context
     * @param pref_field
     * @param def_value
     * @return
     */
    public String getPreference(Context context, String pref_field, String def_value) {
        try {
            mAppPreferences = context.getSharedPreferences(Preferences.MEGAMMR_APP_PREF.name(), Context.MODE_PRIVATE);
            return mAppPreferences.getString(pref_field, def_value);
        } catch (Exception e) {
            logException(e, "Utilities_getPreference()", context);
            return def_value;
        }


    }

    /**
     * this method is used to get integer shared preferences
     *
     * @param context
     * @param pref_field
     * @param def_value
     * @return
     */
    public int getPreferenceInt(Context context, String pref_field, int def_value) {
        try {
            mAppPreferences = context.getSharedPreferences(Preferences.MEGAMMR_APP_PREF.name(), Context.MODE_PRIVATE);
            return mAppPreferences.getInt(pref_field, -1);
        } catch (Exception e) {
            logException(e, "Utilities_getPreferenceInt()", context);
            return -1;
        }

    }

    /**
     * this method is used to get boolean shared preferences
     *
     * @param context
     * @param pref_field
     * @param def_value
     * @return
     */
    public boolean getPreferenceBoolean(Context context, String pref_field, Boolean def_value) {
        try {
            mAppPreferences = context.getSharedPreferences(Preferences.MEGAMMR_APP_PREF.name(), Context.MODE_PRIVATE);
            return mAppPreferences.getBoolean(pref_field, def_value);
        } catch (Exception e) {
            logException(e, "Utilities_getPreferenceBoolean()", context);
            return false;
        }

    }

    /**
     * verify input email is valid or not
     *
     * @param email
     * @return
     */
    public boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * verify input phone number is valid or not
     *
     * @param mMobile
     * @return
     */

    public boolean validatePhoneNumber(String mMobile) {
        boolean isValid = false;
        if (mMobile.length() != 10)
            return false;
        char firstChar = mMobile.charAt(0);
        switch (firstChar) {
            case '9':
                if (mMobile.equalsIgnoreCase("9999999999"))
                    isValid = false;
                else
                    isValid = true;
                break;
            case '8':
                if (mMobile.equalsIgnoreCase("8888888888"))
                    isValid = false;
                else
                    isValid = true;
                break;
            case '7':
                if (mMobile.equalsIgnoreCase("7777777777"))
                    isValid = false;
                else
                    isValid = true;
                break;
            default:
                isValid = false;
        }
        return isValid;
    }


    public void AlertDialogFinish(final Activity context, String Title, String Message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(Title);
        alert.setMessage(Message);
        alert.setCancelable(false);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        //		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
        //
        //			@Override
        //			public void onClick(DialogInterface dialog, int which) {
        //				dialog.dismiss();
        //			}
        //		});
        alert.show();
    }

    /**
     * It create drawable from view
     *
     * @param context
     * @param view
     * @return
     */
    public Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    public Bitmap writeTextOnDrawable(int drawableId, String text, Context ctx) {

        Bitmap bm = BitmapFactory.decodeResource(ctx.getResources(), drawableId)
                .copy(Bitmap.Config.ARGB_8888, true);

        Typeface tf = Typeface.create("Helvetica", Typeface.NORMAL);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.GRAY);
        paint.setTypeface(tf);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(convertToPixels(ctx, 10));

        Rect textRect = new Rect();
        paint.getTextBounds(text, 0, text.length(), textRect);

        Canvas canvas = new Canvas(bm);

        //If the text is bigger than the canvas , reduce the font size
        if (textRect.width() >= (canvas.getWidth() - 4))     //the padding on either sides is considered as 4, so as to appropriately fit in the text
            paint.setTextSize(convertToPixels(ctx, 7));        //Scaling needs to be used for different dpi's

        //Calculate the positions
        int xPos = (canvas.getWidth() / 2) - 2;     //-2 is for regulating the x position offset

        //"- ((paint.descent() + paint.ascent()) / 2)" is the distance from the baseline to the center.
        int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2));

        canvas.drawText(text, xPos, yPos, paint);

        return bm;
    }

    public int convertToPixels(Context context, int nDP) {
        final float conversionScale = context.getResources().getDisplayMetrics().density;
        return (int) ((nDP * conversionScale) + 0.5f);
    }

    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = null;
        try {
            stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stream.toByteArray();
    }

    /**
     * hide keyboard
     *
     * @param activity
     * @param ctx
     * @param view
     */
    public void HideKeyboard(Activity activity, Context ctx, View view) {
        try {
            activity.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        } catch (Exception e) {
            logException(e, "Utilities_HideKeyboard()", ctx);
        }

    }

    /**
     * verify input string is null or empty
     *
     * @param str
     * @return
     */
    public boolean isNullorEmpty(String str) {
        try {
            return !(!TextUtils.isEmpty(str) && !str.equals("null"));
        } catch (Exception e) {
            logException(e, "Utilities_isNullorEmpty()", context);
            return false;
        }

    }

    /**
     * check internet is working or not
     *
     * @param context
     * @return
     */
    public static  Boolean isInternetWorking(Context context) {

        try {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = connectivity.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()) {
                return true;
            }
            else {

                if(context instanceof MainScreen) {
                    if (toast == null || !toast.getView().isShown()) {
                        toast = Toast.makeText(context,
                                R.string.please_check_internet_Connection, Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
//                Toast.makeText(context,R.string.please_check_internet_Connection,Toast.LENGTH_SHORT).show();
            }
            return false;
        } catch (Exception e) {
            //logException(e, "Utilities_serviceCalls()", context);
            return false;
        }

    }

    /**
     * check GPS is enabled or not
     *
     * @param context
     * @return
     */
    public boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    public SpannableStringBuilder setMandatoryStringBuilder(String string, String textColor) {

        SpannableStringBuilder builder = new SpannableStringBuilder();
        Spannable basicText = new SpannableString(string);
        basicText.setSpan(new ForegroundColorSpan(Color.parseColor(textColor)), 0, string.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        Spannable astric = new SpannableString("*");
        astric.setSpan(new ForegroundColorSpan(Color.parseColor("#990000")), 0, astric.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        builder.append(basicText);
        builder.append(astric);

        return builder;
    }

    /**
     * generic method for service call
     *
     * @param ctx
     * @param url
     * @param isPost
     * @param Json
     * @param isDialogEnable
     * @param callbackID
     * @param listener
     * @param isTokenRequest
     */
    public static void serviceCalls(Context ctx, String url, boolean isPost, String Json, boolean isDialogEnable, int callbackID, WebServiceCallback listener, boolean isTokenRequest) {
        WebServiceRequestModel request = new WebServiceRequestModel();
        try {

//            if((WebserviceUrls.BaseUrl+url).equals("http://Api.cellde.com/api/GetSubscriberTestsByCode/MStack/Test_User"))
//            {
//                AppConstantsTheme.theme = AppConstantsTheme.Theme.BLUE;
//                AppConstantsTheme.setStyleTheme();
//            }
//            else if ((WebserviceUrls.BaseUrl+url).equals("http://Api.cellde.com/api/GetSubscriberTestsByCode/MCheck/Test_User")) {
//                AppConstantsTheme.theme = AppConstantsTheme.Theme.GREEN;
//                AppConstantsTheme.setStyleTheme();
//            }
            /*request.setUrl(ctx.getResources().getString(R.string.BASE_URL) + url);*/
            request.setUrl(WebserviceUrls.BaseUrl + url);
            request.setIsDialogEnabled(isDialogEnable);
            request.setJsonData(Json.toString());
            request.setIsPost(isPost);
            WebServiceAsynctask webCall = new WebServiceAsynctask(ctx, listener, request, callbackID, isTokenRequest);
            webCall.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (Exception e) {
            e.printStackTrace();
            Utilities utilities = new Utilities(ctx);
            utilities.logException(e, "Utilities_serviceCalls()", ctx);
        }
    }

    /**
     * generic method for service call
     *
     * @param ctx
     * @param url
     * @param isPost
     * @param Json
     * @param isDialogEnable
     * @param callbackID
     * @param listener
     * @param isTokenRequest
     */
    public static void serviceCallsAbacusAPI(Context ctx, String url, boolean isPost, String Json, boolean isDialogEnable, int callbackID, WebServiceCallback listener, boolean isTokenRequest
            , ChannelLoginModel channelLoginModel) {
        WebServiceRequestModel request = new WebServiceRequestModel();
        try {
            /*request.setUrl(ctx.getResources().getString(R.string.BASE_URL) + url);*/
            request.setUrl(WebServiceUrlsAbacusAPI.BaseUrl + url);
            request.setIsDialogEnabled(isDialogEnable);
            request.setJsonData(Json);
            request.setIsPost(isPost);
            WebServiceAsyncTaskAbacusAPI webCall = new WebServiceAsyncTaskAbacusAPI(ctx, listener, request, callbackID, isTokenRequest, channelLoginModel);
            webCall.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (Exception e) {
            e.printStackTrace();
            Utilities utilities = new Utilities(ctx);
            utilities.logException(e, "Utilities_serviceCalls()", ctx);
        }
    }

    /**
     * async task initializer for Automation Test
     *
     * @param activity
     * @param ctx
     * @param callbackID
     * @param listener
     */

    public static void automatedTestAsync(Activity activity, Context ctx, int callbackID, WebServiceCallback listener) {
        try {
            Automated_Test_Asynctask webCall = new Automated_Test_Asynctask(activity, ctx, listener, callbackID);
            webCall.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (Exception e) {
            Utilities utilities = new Utilities(ctx);
            utilities.logException(e, "Utilities_countDownTimerAsyncTask()", ctx);
        }

    }



    public static void countDownTimerAsyncTask(Activity activity, Context ctx, int callbackID, InterfaceTimerCallBack listener) {
        try {
            Constants.timerCallback = new CountDownTimerTask(activity, ctx, listener, callbackID);
            Constants.timerCallback.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } catch (Exception e) {
            Utilities utilities = new Utilities(ctx);
            utilities.logException(e, "Utilities_countDownTimerAsyncTask()", ctx);
        }

    }

    public static void stopCountDownTimerAsyncTask(Activity activity, Context ctx, int callbackID, InterfaceTimerCallBack listener) {

        try {
            CountDownTimerTask timerCallback = new CountDownTimerTask(activity, ctx, listener, callbackID);
            timerCallback.cancel(true);
        } catch (Exception e) {
            Utilities utilities = new Utilities(ctx);
            utilities.logException(e, "Utilities_stopCountDownTimerAsyncTask()", ctx);
        }

    }


    public boolean isSDCardPresent() {
        try {
            return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        } catch (Exception e) {
            logException(e, "Utilities_isSDCardPresent()", context);
            return false;
        }

    }

    /**
     * make direcrory in device
     *
     * @param ctx
     * @param path
     */
    public void makeDirectory(Context ctx, String path) {
        if (Utilities.getInstance(ctx).isSDCardPresent()) {
            try {
                File mainFolder = new File(path);
                if (!mainFolder.exists())
                    mainFolder.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
                logException(e, "Utilities_makeDirectory()", ctx);
            }
        }
    }

    /**
     * verify directory exist or not
     *
     * @param ctx
     * @param path
     * @return
     */
    public boolean isDirectoryExist(Context ctx, String path) {
        boolean isExist = false;
        if (Utilities.getInstance(ctx).isSDCardPresent()) {
            try {
                File mainFolder = new File(path);
                if (!mainFolder.exists())
                    isExist = false;
                else
                    isExist = true;
            } catch (Exception e) {
                e.printStackTrace();
                isExist = false;
                logException(e, "Utilities_isDirectoryExist()", ctx);
            }
        }
        return isExist;
    }

    /**
     * hide or show keyboard
     *
     * @param context
     * @param isShow
     */
    public void showHideKeyboard(Context context, boolean isShow) {
        try {

            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (isShow) {
                imm.showSoftInput(((Activity) context).getCurrentFocus(), InputMethodManager.SHOW_FORCED);
            } else {
                if (imm.isAcceptingText()) {
                    imm.hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(), 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logException(e, "Utilities_showHideKeyboard()", context);
        }

    }

    public void clearDB(Context context) {

    }

    public String getInfo(String filePath) {
        StringBuffer sb = new StringBuffer();
        if (new File(filePath).exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(new File(filePath)));
                String aLine;
                while ((aLine = br.readLine()) != null) {
                    sb.append(aLine);
                }
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                logException(e, "Utilities_getInfo()", context);
            }
        }
        return sb.toString();
    }

    public void logException(Exception e, String methodName, Context context) {
        try {
            Utilities utilities = Utilities.getInstance(context);
            ExceptionLogUtils exceptionLogUtils = new ExceptionLogUtils(utilities, context, null);
            exceptionLogUtils.addExceptionLog(utilities, context, null, null, null, e, methodName);
        } catch (Exception exp) {
         //   logException(exp, "Utilities_logException()", context);
        }

    }
    public void showAlert(final Context ctx, final onAlertOkListener mListener,final onAlertCancelListener mmListener2,final String  Message, final String Title, final String positiveButtonText,
                          final String negativeButtonText, final String callbackID) {
        try {
            Handler handler = new Handler(Looper.getMainLooper());

            handler.post(new Runnable() {
                public void run() {
                    // Run your task here
                    AlertDialog.Builder alert = new AlertDialog.Builder(ctx);
                    alert.setTitle(Title);
                    alert.setMessage(Message);
                    alert.setCancelable(false);
                    alert.setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if (mListener != null)
                                mListener.onOkButtonClicked(callbackID);

                            dialog.dismiss();
                            syncdialog = null;

                        }
                    });
                    if (!TextUtils.isEmpty(negativeButtonText)) {
                        alert.setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (mmListener2 != null)
                                    mmListener2.onOkButtonClicked1(callbackID);
                                dialog.dismiss();
                                syncdialog = null;

                            }
                        });
                    }
                    syncdialog=	alert.show();
//                    try {
//                    if(syncdialog.isShowing()) {
//                        SemiAutomaticTestsFragment fragment = (SemiAutomaticTestsFragment) ctx.getSupportFragmentManager().findFragmentById(R.id.container);
//                            if (fragment!=null) {
//                                allunrigster(ctx);
//                            }
//                        }
//                    }catch (Exception e)
//                    {
//                        e.printStackTrace();
//                    }

                }
            });


        } catch (Exception e) {
            Log.e(e.getMessage(), "compare_showAlert()");
        }

    }








    public void checkSensors(Context getActivity) {
        RealmOperations realmOperations=new RealmOperations();
        sensorManager = (SensorManager) getActivity.getSystemService(Context.SENSOR_SERVICE);
        Sensor LightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (LightSensor == null) {
            addPreferenceBoolean(getActivity,
                    ConstantTestIDs.LIGHT_SENSOR_ID + Constants.IS_TEST_EXIST, true);
            addPreferenceInt(getActivity, JsonTags.MMR_26.name(),
                    AsyncConstant.TEST_NOT_EXIST);
            compare_UpdatePreferenceInt(getActivity, "MMR_26",
                    AsyncConstant.TEST_NOT_EXIST);
            realmOperations.updateTestResult(ConstantTestIDs.LIGHT_SENSOR_ID,
                    AsyncConstant.TEST_NOT_EXIST);

        }
        Sensor proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if (proximitySensor == null) {
            addPreferenceBoolean(getActivity,
                    ConstantTestIDs.PROXIMITY_ID + Constants.IS_TEST_EXIST, true);
            addPreferenceInt(getActivity, JsonTags.MMR_22.name(),
                    AsyncConstant.TEST_NOT_EXIST);
            compare_UpdatePreferenceInt(getActivity, "MMR_22",
                    AsyncConstant.TEST_NOT_EXIST);
            realmOperations.updateTestResult(ConstantTestIDs.PROXIMITY_ID,
                    AsyncConstant.TEST_NOT_EXIST);

        }
        Sensor mMagnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (mMagnetometer == null) {
            addPreferenceBoolean(getActivity,
                    ConstantTestIDs.COMPASS + Constants.IS_TEST_EXIST,
                    true);
            addPreferenceInt(getActivity, JsonTags.MMR_27.name(),
                    AsyncConstant.TEST_NOT_EXIST);
            compare_UpdatePreferenceInt(getActivity, "MMR_27",
                    AsyncConstant.TEST_NOT_EXIST);
            realmOperations.updateTestResult(ConstantTestIDs.COMPASS, AsyncConstant.TEST_NOT_EXIST);

        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            isFingerprintExist = false;
            addPreferenceBoolean(getActivity,
                    ConstantTestIDs.FINGERPRINT + Constants.IS_TEST_EXIST
                    , true);
            addPreferenceInt(getActivity, JsonTags.MMR_57.name(),
                    AsyncConstant.TEST_NOT_EXIST);
            compare_UpdatePreferenceInt(getActivity, "MMR_57",
                    AsyncConstant.TEST_NOT_EXIST);
            realmOperations.updateTestResult(ConstantTestIDs.FINGERPRINT,
                    AsyncConstant.TEST_NOT_EXIST);

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            FingerprintManager fingerprintManager =
                    (FingerprintManager) getActivity.getSystemService(FINGERPRINT_SERVICE);
            if ((fingerprintManager == null || !fingerprintManager.isHardwareDetected())) {
                isFingerprintExist = false;
                addPreferenceBoolean(getActivity,
                        ConstantTestIDs.FINGERPRINT + Constants.IS_TEST_EXIST, true);
                addPreferenceInt(getActivity, JsonTags.MMR_57.name(),
                        AsyncConstant.TEST_NOT_EXIST);
                compare_UpdatePreferenceInt(getActivity, "MMR_57",
                        AsyncConstant.TEST_NOT_EXIST);
                realmOperations.updateTestResult(ConstantTestIDs.FINGERPRINT,
                        AsyncConstant.TEST_NOT_EXIST);

            }
        }
        Sensor barrometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        if (barrometerSensor == null) {
            addPreferenceBoolean(getActivity,
                    ConstantTestIDs.Barometer + Constants.IS_TEST_EXIST,
                    true);
            addPreferenceInt(getActivity, JsonTags.MMR_63.name(),
                    AsyncConstant.TEST_NOT_EXIST);
            compare_UpdatePreferenceInt(getActivity, "MMR_63",
                    AsyncConstant.TEST_NOT_EXIST);
            realmOperations.updateTestResult(ConstantTestIDs.Barometer,
                    AsyncConstant.TEST_NOT_EXIST);
        }
        int v = 0;
        try {
            v = getActivity.getPackageManager().getPackageInfo(GoogleApiAvailability.GOOGLE_PLAY_SERVICES_PACKAGE, 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        FaceDetector detector = new FaceDetector.Builder(getActivity)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();
        if (!detector.isOperational()) {
            faceexists = false;
        }
        if (7899470 > v || !faceexists) {
            //isFaceDetectionExist = false;
            addPreferenceBoolean(getActivity,
                    ConstantTestIDs.FaceDetection + Constants.IS_TEST_EXIST, true);
            addPreferenceInt(getActivity, JsonTags.MMR_64.name(),
                    AsyncConstant.TEST_NOT_EXIST);
            compare_UpdatePreferenceInt(getActivity, "MMR_64",
                    AsyncConstant.TEST_NOT_EXIST);
            realmOperations.updateTestResult(ConstantTestIDs.FaceDetection,
                    AsyncConstant.TEST_NOT_EXIST);
        }


    }



















    public void allunrigster(Context ctx) {
        try {
            TestController testController=TestController.getInstance(ctx);
            testController.unregisterGyroScope2();
            testController.unRegisterCharging();
            testController.unregisterBattery();
            testController.unregisterProximity();
            testController.unRegisterHome();
            testController.unRegisterEarJack();
            testController.unRegisterPowerButton();

        } catch (Exception e) {
            e.printStackTrace();
            //     logException(e, "DashBoardFragment_onPause()");
        }
    }

    public interface onAlertOkListener {

        public void onOkButtonClicked(String tag);
    }

    public interface onAlertCancelListener {

        public void onOkButtonClicked1(String tag);
    }
    public void loadDatabase(Context context) {

        RealmOperations realmOperations = new RealmOperations();


        for (int i = 0; i < Constants.arrAutomatedTestResources.length; i++) {
            TestModel object = new TestModel();
            object.setTestDrawable(Constants.arrAutomatedTestResources[i]);
            object.setTest_name(context.getResources().getStringArray(R.array.arrAutomationTestName)[i]);
            object.setTestStstus(AsyncConstant.TEST_IN_QUEUE);
            object.setTestType(Constants.AUTOMATE);
            object.setTest_id(Constants.arrAutomatedTestResourcesId[i]);
            object.setDuel(false);
            realmOperations.saveTestInDatabase(object);
        }
        for (int i = 0; i < Constants.arrSemiAutomaticResources.length; i++) {
            TestModel object = new TestModel();
            object.setTestDrawable(Constants.arrSemiAutomaticResources[i]);
            object.setTest_name(context.getResources().getStringArray(R.array.arrSemiAutomaticTestName)[i]);
            object.setTestStstus(AsyncConstant.TEST_IN_QUEUE);
//            object.setTestType(Constants.MANUAL);
            object.setTestType(Constants.MANUAL1);
            object.setTestDes(context.getResources().getStringArray(R.array.arrSemiAutomaticTestdesc)[i]);
            object.setTest_id(Constants.arrSemiAutomaticResourcesID[i]);
            object.setDuel(false);
            object.setRequireRegistration(true);
            realmOperations.saveTestInDatabase(object);
        }
        for (int i = 0; i < Constants.arrManualTotalTestResources.length; i++) {
            TestModel object = new TestModel();
            object.setTestDrawable(Constants.arrManualTotalTestResources[i]);
            object.setTest_name(context.getResources().getStringArray(R.array.arrManualTotalTestName)[i]);
            object.setTestStstus(AsyncConstant.TEST_IN_QUEUE);
            object.setTestType(Constants.MANUAL);
            object.setTestDes(context.getResources().getStringArray(R.array.arrManualTotalTestdesc)[i]);
            object.setDuel(false);
            object.setTest_id(Constants.arrManualTotalTestResourcesID[i]);
            object.setRequireRegistration(true);
            realmOperations.saveTestInDatabase(object);
        }
        for (int i = 0; i < Constants.arrDualTestResourses.length; i++) {
            TestModel object = new TestModel();
            object.setTestDrawable(Constants.arrDualTestResourses[i]);
            object.setTest_name(context.getResources().getStringArray(R.array.arrSubTestName)[i]);
            object.setTestStstus(AsyncConstant.TEST_IN_QUEUE);
            object.setTestType(Constants.MANUAL);
            object.setDuel(true);
            object.setTest_id(Constants.arrDualTestResoursesID[i]);
            object.setRequireRegistration(true);
            realmOperations.saveTestInDatabase(object);
        }

        SubTestMapModel subTestMapModel = new SubTestMapModel();
        subTestMapModel.setTest_id(ConstantTestIDs.CAMERA_ID);
        subTestMapModel.setSub_test_id(ConstantTestIDs.FRONT_CAMERA);
        realmOperations.saveTestInDatabase(subTestMapModel);

        subTestMapModel = new SubTestMapModel();
        subTestMapModel.setTest_id(ConstantTestIDs.CAMERA_ID);
        subTestMapModel.setSub_test_id(ConstantTestIDs.BACK_CAMERA);
        realmOperations.saveTestInDatabase(subTestMapModel);

        subTestMapModel = new SubTestMapModel();
        subTestMapModel.setTest_id(Constants.SPEAKER_MIC);
        subTestMapModel.setSub_test_id(ConstantTestIDs.SPEAKER_ID);
        realmOperations.saveTestInDatabase(subTestMapModel);

        subTestMapModel = new SubTestMapModel();
        subTestMapModel.setTest_id(Constants.SPEAKER_MIC);
        subTestMapModel.setSub_test_id(ConstantTestIDs.MIC_ID);
        realmOperations.saveTestInDatabase(subTestMapModel);


        subTestMapModel = new SubTestMapModel();
        subTestMapModel.setTest_id(ConstantTestIDs.VOLUME_ID);
        subTestMapModel.setSub_test_id(ConstantTestIDs.VOLUME_UP);
        realmOperations.saveTestInDatabase(subTestMapModel);

        subTestMapModel = new SubTestMapModel();
        subTestMapModel.setTest_id(ConstantTestIDs.VOLUME_ID);
        subTestMapModel.setSub_test_id(ConstantTestIDs.VOLUME_DOWN);
        realmOperations.saveTestInDatabase(subTestMapModel);

    }

    public static Boolean validation(EditText editText, String error) {
        if (editText.getText().toString().trim().isEmpty()) {
            editText.requestFocus();
            editText.setError(error);
            return false;
        }
        return true;
    }

    public static boolean validationofImage(String  txtimagepath, String string) {
        if (txtimagepath.isEmpty()) {
            Toast.makeText(context,string,Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }


    public static boolean validationofImage(ArrayList<String> imagesList, String string){
        if(imagesList.size()==0 || imagesList.isEmpty()){
            Toast.makeText(context,string,Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public static Boolean EmailValidation(TextInputEditText editText, String error) {
        if (!EmailValidator.getInstance().validate(editText.getText().toString().trim())) {
            editText.setError(error);
            editText.requestFocus();

            return false;
        }
        return true;
    }










    public static Boolean EmailValidation(EditText editText, String error) {
        if (!EmailValidator.getInstance().validate(editText.getText().toString().trim())) {
            editText.setError(error);
            editText.requestFocus();

            return false;
        }
        return true;
    }









    public static boolean validationSpinner(String id, String string) {
        if (id.isEmpty() || id.equals("Select ID")) {
            Toast.makeText(context,string,Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public static boolean validationSpinner(Spinner id, String string) {
        if ( id.getSelectedItemPosition()==0) {
            Toast.makeText(context,string,Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }



    public static Boolean confirm(EditText editText,String error,String email,String confirm){

        if (!email.equalsIgnoreCase(confirm)) {
            editText.requestFocus();
            editText.setError(error);
            return false;
        }
        return true;
    }
    public boolean validCellPhone(String number)
    {
        return android.util.Patterns.PHONE.matcher(number).matches();
    }

    public static Boolean validContact(EditText editText, String error) {
        if (editText.getText().toString().trim().length() < 10 || editText.getText().toString().trim().length() > 15) {
            editText.setError(error);
            editText.requestFocus();
            return false;

        }
        return true;
    }
}
