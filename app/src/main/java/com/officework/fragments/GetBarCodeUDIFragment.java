package com.officework.fragments;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.CountDownTimer;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.officework.R;
import com.officework.activities.ThemeManager;
import com.officework.base.BaseFragment;
import com.officework.constants.AppConstantsTheme;
import com.officework.constants.Constants;
import com.officework.constants.FragmentTag;
import com.officework.constants.JsonTags;
import com.officework.constants.PreferenceConstants;
import com.officework.customViews.CustomButton;
import com.officework.interfaces.InterfaceButtonTextChange;
import com.officework.interfaces.WebServiceCallback;
import com.officework.testing_profiles.ui.fragment.AutomatedTestFragment;
import com.officework.utils.TestResultUpdateToServer;
import com.officework.utils.Utilities;


import org.json.JSONObject;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class GetBarCodeUDIFragment extends BaseFragment implements WebServiceCallback {
    View view;
    Utilities utils;
    CountDownTimer countDownTimer;
    Context ctx;
    InterfaceButtonTextChange mCallBack;
    ImageView imgViewBarCodeUDI;
    Display display;
    TextView txtViewUDIText;
    BroadcastReceiver mRegistrationBroadcastReceiver;
    CustomButton txtViewRefcode;
    TextView txtViewOrUse, txtViewTimerRefcode;
    int SERVICE_REQUEST_ID = -1;
    ThemeManager themeManager;
    int colorValue;
    FragmentManager fm;
    Fragment frag;
    TestResultUpdateToServer testResultUpdateToServer;
    private boolean isAllowed=false;

    public GetBarCodeUDIFragment() {
        // Required empty public constructor
    }

    public GetBarCodeUDIFragment(InterfaceButtonTextChange interfaceButtonTextChange) {
        mCallBack = interfaceButtonTextChange;
        // Required empty public constructor
    }


    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        try {
            if (view == null) {
                view = inflater.inflate(R.layout.fragment_get_bar_code_udi, null);
                utils = Utilities.getInstance(getActivity());
                ctx = getActivity();
             //   Crashlytics.getInstance().log(FragmentTag.MANUAL_TEST_FRAGMENT.name());
                initViews();
            }
            return view;
        } catch (Exception e) {

            return null;
        }

    }

    private void initViews() {
        fm = getActivity().getSupportFragmentManager();
        frag = fm.findFragmentById(R.id.container);
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        display = wm.getDefaultDisplay();
        //
        mCallBack.onChangeText(Utilities.BUTTON_SKIP, false);
        imgViewBarCodeUDI = (ImageView) view.findViewById(R.id.imgViewBarCodeUDI);
        txtViewUDIText = (TextView) view.findViewById(R.id.txtViewUDIText);
        txtViewOrUse = (TextView) view.findViewById(R.id.txtViewOrUse);
        txtViewRefcode = (CustomButton) view.findViewById(R.id.txtViewRefcode);
        txtViewTimerRefcode = (TextView) view.findViewById(R.id.txtViewTimerRefcode);
        themeManager=new ThemeManager(getActivity());
//        colorValue= R.color.brown;
       colorValue= AppConstantsTheme.getIconColor();
              //  Color.parseColor(themeManager.getTheme());
        generateBarCode(utils.getPreference(ctx, JsonTags.UDI.name(), ""));
        testResultUpdateToServer = new TestResultUpdateToServer(utils, ctx, getActivity());

      //  testResultUpdateToServer.updateTestResult(null, false, 1);

    }


    /**
     * onResume
     */
    @Override
    public void onResume() {
        try {
            TitleBarFragment fragment = (TitleBarFragment) getFragment(R.id.headerContainer);
            if (fragment != null) {
                if (frag instanceof AutomatedTestFragment) {
                    fragment.setTitleBarVisibility(true);
                    fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string.txtBarcode), true, false, -1);
                    mCallBack.onChangeText(utils.BUTTON_SKIP, false);
                }
                if(countDownTimer!=null) {
                    countDownTimer.cancel();
                }}

            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(Constants.onTimerCallbackReceiver));
            updateUIForRefCode();
            Constants.isDashBoardVisible = true;
        } catch (Exception e) {

        }

        super.onResume();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mRegistrationBroadcastReceiver);
        Constants.isDashBoardVisible = false;
        isAllowed=true;

        if(countDownTimer!=null) {
            countDownTimer.cancel();
        }
       // Constants.isTimerRunning=false;
        super.onPause();

    }

    /**
     * generate bar code from UDI
     *
     * @param UDI
     */
    private void generateBarCode(String UDI) {
        try {
            MultiFormatWriter writer = new MultiFormatWriter();
            int width = display.getWidth();
            String finaldata = Uri.encode(UDI, "utf-8");

            BitMatrix bm = null;

            bm = writer.encode(finaldata, BarcodeFormat.CODE_128, width, 100);

            Bitmap ImageBitmap = Bitmap.createBitmap(width, 100, Bitmap.Config.ARGB_8888);

            for (int i = 0; i < width; i++) {//width
                for (int j = 0; j < 100; j++) {//height
                    ImageBitmap.setPixel(i, j, bm.get(i, j) ? Color.BLACK : Color.WHITE);
                }
            }

            if (ImageBitmap != null) {
                Bitmap resized = Bitmap.createScaledBitmap(ImageBitmap, ImageBitmap.getWidth(), (int) (ImageBitmap.getHeight() * 1.5), true);

                imgViewBarCodeUDI.setImageBitmap(resized);
                PreferenceConstants.imageBitmap = resized;
                PreferenceConstants.udi = UDI;
                String repl = PreferenceConstants.udi.replaceAll("....(?!$)", "$0 ");
                txtViewUDIText.setText(repl);
           /* tvBarCode.setText(UDI);*/
            } else {
                //Toast.makeText(getApplicationContext(), getResources().getString(R.string.userInputError),
                //    .// Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            //  logException(e, "DashBoardFragment_generateBarCode()");
        }


    }

    public void updateUIForRefCode() {
        try {
            if (!utils.getPreference(ctx, JsonTags.refcode.name(), "").equalsIgnoreCase("")) {

                if (!Constants.isTimerRunning) {
                    txtViewRefcode.setTypeface(null, Typeface.BOLD);
                    txtViewRefcode.setVisibility(View.VISIBLE);
                    txtViewRefcode.setBackgroundColor(colorValue);
                    txtViewRefcode.setTextColor(getResources().getColor(R.color.white));
                    txtViewRefcode.setText(getResources().getString(R.string.txtGetNew));
                    txtViewOrUse.setText(getResources().getString(R.string.txtOrUse));
                    txtViewRefcode.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            isAllowed=true;
                            txtViewRefcode.setTypeface(null, Typeface.NORMAL);
                            txtViewRefcode.setTextColor(getResources().getColor(R.color.white));
                            txtViewRefcode.setText("");
                            txtViewRefcode.setBackground(null);
                            txtViewRefcode.setOnClickListener(null);
                          //  getTokenForRefCode();
                            getRefCode();
                        }
                    });
                    /*initializeTimer();*/
                } else {
                    txtViewRefcode.setOnClickListener(null);
                    txtViewRefcode.setBackground(null);
                    txtViewRefcode.setTypeface(null, Typeface.BOLD);
                    txtViewRefcode.setTextColor(getResources().getColor(R.color.carbonBlack));
                    txtViewRefcode.setVisibility(View.VISIBLE);
                    txtViewOrUse.setText(getResources().getString(R.string.txtOr));
                    txtViewRefcode.setText(utils.getPreference(ctx, JsonTags.refcode.name(), ""));
                }
            } else {
                txtViewRefcode.setTypeface(null, Typeface.NORMAL);
                txtViewTimerRefcode.setVisibility(View.INVISIBLE);
                txtViewOrUse.setText(getResources().getString(R.string.txtOr));
                txtViewRefcode.setVisibility(View.VISIBLE);
                txtViewRefcode.setBackgroundColor(colorValue);
                txtViewRefcode.setTextColor(getResources().getColor(R.color.white));
                txtViewRefcode.setText(getResources().getString(R.string.txtGetNew));
                txtViewRefcode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isAllowed=true;
                        txtViewRefcode.setText("");

                        txtViewRefcode.setTextColor(getResources().getColor(R.color.white));
                        txtViewRefcode.setBackground(null);
                        txtViewRefcode.setOnClickListener(null);
                       // getTokenForRefCode();
                        getRefCode();
                    }
                });

            }
        } catch (Exception e) {

        }

    }

    private void initializeTimer() {
        if(countDownTimer!=null) {
            countDownTimer.cancel();
        }

        try {
             countDownTimer=new CountDownTimer(120000,1000) {
                @Override
                public void onTick(long l) {
                    String time = String.format(Locale.getDefault(), "Time Remaining %02d min: %02d sec",
                            TimeUnit.MILLISECONDS.toMinutes(l) % 60,
                            TimeUnit.MILLISECONDS.toSeconds(l) % 60);

                    txtViewTimerRefcode.setVisibility(View.VISIBLE);
                    txtViewTimerRefcode.setText(String.valueOf(time));

                }

                @Override
                public void onFinish() {
                    txtViewRefcode.setTypeface(null, Typeface.NORMAL);
                    txtViewTimerRefcode.setVisibility(View.INVISIBLE);
                    txtViewRefcode.setText(getResources().getString(R.string.txtGetNew));
                    txtViewRefcode.setBackgroundColor(colorValue);
                    txtViewRefcode.setTextColor(getResources().getColor(R.color.white));
                    txtViewOrUse.setText(getResources().getString(R.string.txtOr));
                    txtViewRefcode.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                           isAllowed=true;
                            txtViewRefcode.setText("");

                            txtViewRefcode.setTextColor(getResources().getColor(R.color.white));
                            txtViewRefcode.setBackground(null);
                            txtViewRefcode.setOnClickListener(null);
                            // getTokenForRefCode();
                            getRefCode();
                        }
                    });
                }
            }.start();

//            MainActivity mainActivity = new MainActivity();
//            mainActivity.startTimerTask(null, utils, ctx);
        } catch (Exception e) {

        }

    }



    @Override
    public void onServiceResponse(boolean serviceStatus, String response, int callbackID) {
        if (serviceStatus) {
            try {
                JSONObject responseObj = new JSONObject(response);
                switch (callbackID) {

                    case 5:

                        if (responseObj.getBoolean("IsSuccess")) {
                            JSONObject data=responseObj.getJSONObject("Data");
                             if(isAllowed){
                            utils.addPreference(ctx, JsonTags.RefCode.name(), data.getString(JsonTags.RefCode.name()));

                           // utils.addPreference(ctx, JsonTags.RefCode.name(), responseObj.getString(JsonTags.RefCode.name()));
                           // utils.addPreferenceLong(ctx, JsonTags.ttl.name(), responseObj.getLong(JsonTags.ttl.name()));
                            txtViewRefcode.setText(data.getString(JsonTags.RefCode.name()));
                            txtViewOrUse.setText(getResources().getString(R.string.txtOrUse));
                            txtViewRefcode.setBackground(null);
                            txtViewRefcode.setTypeface(null, Typeface.BOLD);
                            txtViewRefcode.setTextColor(getResources().getColor(R.color.carbonBlack));
                            initializeTimer();}
                        } else {
                            txtViewRefcode.setTypeface(null, Typeface.NORMAL);
                            txtViewRefcode.setText(getResources().getString(R.string.txtGetNew));
                            txtViewRefcode.setBackgroundColor(colorValue);
                            txtViewRefcode.setTextColor(getResources().getColor(R.color.white));
                            txtViewOrUse.setText(getResources().getString(R.string.txtOr));
                            txtViewRefcode.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    isAllowed=true;
                                    txtViewRefcode.setText("");

                                    txtViewRefcode.setTextColor(getResources().getColor(R.color.white));
                                    txtViewRefcode.setBackground(null);
                                    txtViewRefcode.setOnClickListener(null);
                                    txtViewRefcode.setTypeface(null, Typeface.BOLD);
                                    getRefCode();
                                   // getTokenForRefCode();
                                }
                            });
                        }


                        break;

                }
            } catch (Exception e) {

                if (callbackID == 5) {
                    txtViewRefcode.setTypeface(null, Typeface.NORMAL);
                    txtViewRefcode.setText(getResources().getString(R.string.txtGetNew));
                    txtViewRefcode.setBackgroundColor(colorValue);
                    txtViewRefcode.setTextColor(getResources().getColor(R.color.white));
                    txtViewOrUse.setText(getResources().getString(R.string.txtOr));
                    txtViewRefcode.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            isAllowed=true;
                            txtViewRefcode.setText("");

                            txtViewRefcode.setTypeface(null, Typeface.BOLD);
                            txtViewRefcode.setTextColor(getResources().getColor(R.color.white));
                            txtViewRefcode.setBackground(null);
                            txtViewRefcode.setOnClickListener(null);
                            getRefCode();
                          //  getTokenForRefCode();
                        }
                    });
                } else if (callbackID == 4) {
                    txtViewRefcode.setTypeface(null, Typeface.NORMAL);
                    txtViewRefcode.setText(getResources().getString(R.string.txtGetNew));
                    txtViewRefcode.setBackgroundColor(colorValue);
                    txtViewRefcode.setTextColor(getResources().getColor(R.color.white));
                    txtViewOrUse.setText(getResources().getString(R.string.txtOr));
                    txtViewRefcode.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            isAllowed=true;
                            txtViewRefcode.setText("");

                            txtViewRefcode.setTypeface(null, Typeface.BOLD);
                            txtViewRefcode.setTextColor(getResources().getColor(R.color.white));
                            txtViewRefcode.setBackground(null);
                            txtViewRefcode.setOnClickListener(null);
                            getRefCode();
                         //   getTokenForRefCode();
                        }
                    });
                }

                e.printStackTrace();


            }
        } else {

            if (callbackID == 5) {
                txtViewRefcode.setTypeface(null, Typeface.NORMAL);
                txtViewRefcode.setText(getResources().getString(R.string.txtGetNew));
                txtViewRefcode.setBackgroundColor(colorValue);
                txtViewRefcode.setTextColor(getResources().getColor(R.color.white));
                txtViewOrUse.setText(getResources().getString(R.string.txtOr));
                txtViewRefcode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isAllowed=true;
                        txtViewRefcode.setText("");

                        txtViewRefcode.setTypeface(null, Typeface.BOLD);
                        txtViewRefcode.setTextColor(getResources().getColor(R.color.white));
                        txtViewRefcode.setBackground(null);
                        txtViewRefcode.setOnClickListener(null);
                        getRefCode();
                        // getTokenForRefCode();
                    }
                });
            } else if (callbackID == 4) {
                txtViewRefcode.setTypeface(null, Typeface.NORMAL);
                txtViewRefcode.setText(getResources().getString(R.string.txtGetNew));
                txtViewRefcode.setBackgroundColor(colorValue);
                txtViewRefcode.setTextColor(getResources().getColor(R.color.white));
                txtViewOrUse.setText(getResources().getString(R.string.txtOr));
                txtViewRefcode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isAllowed=true;
                        txtViewRefcode.setText("");

                        txtViewRefcode.setTypeface(null, Typeface.BOLD);
                        txtViewRefcode.setTextColor(getResources().getColor(R.color.white));
                        txtViewRefcode.setBackground(null);
                        txtViewRefcode.setOnClickListener(null);
                        getRefCode();
                       // getTokenForRefCode();
                    }
                });
            }


        }

    }

    /**
     * POST result data API request
     */
    private void getRefCode() {
        try {
            if (utils.isInternetWorking(ctx)) {
                SERVICE_REQUEST_ID = 5;
//                utils.serviceCalls(ctx, WebserviceUrls.GetRefCode, false, utils.getPreference(ctx, JsonTags.udi.name(), ""), false, SERVICE_REQUEST_ID,
//                        (WebServiceCallback) this, false);
//                utils.serviceCalls(ctx, WebserviceUrls.GetNewRefCode, true, GetQRCodeFragment.createRefCodeJson(utils,getActivity()).toString(), false, SERVICE_REQUEST_ID,
//                        (WebServiceCallback) this, false);
            } else {
                txtViewRefcode.setTypeface(null, Typeface.NORMAL);
                txtViewRefcode.setText(getResources().getString(R.string.txtGetNew));
                txtViewRefcode.setBackgroundColor(colorValue);
                txtViewRefcode.setTextColor(getResources().getColor(R.color.white));
                txtViewOrUse.setText(getResources().getString(R.string.txtOr));
                txtViewRefcode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isAllowed=true;
                        txtViewRefcode.setText("");

                        txtViewRefcode.setTypeface(null, Typeface.BOLD);
                        txtViewRefcode.setTextColor(getResources().getColor(R.color.white));
                        txtViewRefcode.setBackground(null);
                        txtViewRefcode.setOnClickListener(null);
                        getRefCode();
                       // getTokenForRefCode();
                    }
                });
            }
        } catch (Exception e) {

        }


    }

    public void onBackPress() {
        try {
            clearAllStack();
            replaceFragment(R.id.container, new DashBoardFragment((InterfaceButtonTextChange) getActivity()), FragmentTag.DASHBOARD_FRAGMENT.name(), false);
        } catch (Exception e) {

        }


    }

    public void stopTimer(){
        try{
            isAllowed=false;
        if(countDownTimer!=null) {
            countDownTimer.cancel();
        }
        updateUIForRefCode();

    }
    catch (Exception e){
            e.printStackTrace();
    }
    }

}
