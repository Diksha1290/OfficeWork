package com.officework.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import com.google.android.material.snackbar.Snackbar;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.officework.R;
import com.officework.base.BaseFragment;
import com.officework.constants.AsyncConstant;
import com.officework.constants.Constants;
import com.officework.constants.FragmentTag;
import com.officework.constants.JsonTags;
import com.officework.interfaces.InterfaceButtonTextChange;
import com.officework.interfaces.WebServiceCallback;
import com.officework.asyncTasks.Call_Function_Asynctask;
import com.officework.utils.ManualDataStable;
import com.officework.utils.Utilities;
import com.romainpiel.shimmer.ShimmerTextView;

/**
 * Created by Ashwani on 8/22/2016.
 */
@SuppressLint("ValidFragment")
public class CallFunctionManualFragment extends BaseFragment implements WebServiceCallback {
    View view;
    Utilities utils;
    Context ctx;
    TelephonyManager telMgr;
    public static boolean isAllowBack = false;
    LinearLayout callFunLayout;
    boolean isResumed = false;
    ManualDataStable manualDataStable;
    static boolean isNextPressed = false;
    Handler nextButtonHandler = null;
    CountDownTimer cTimer = null;
    TextView callCount;
    boolean isDialogShown = false;

    /*	Mobile*/
    ShimmerTextView mtvStatusMobileManual, mtvOperatorManual, mtvOperatorCodeManual, mtvPhoneTypeManual, mtvNetworkTypeManual;
    InterfaceButtonTextChange mCallBack;

    public CallFunctionManualFragment(InterfaceButtonTextChange callback) {
        mCallBack = callback;
    }

    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_manual_call_function, null);
            utils = Utilities.getInstance(getActivity());
            manualDataStable = new ManualDataStable(mCallBack);
            ctx = getActivity();
            initViews();
         //   Crashlytics.getInstance().log(FragmentTag.CALL_FUNCTION_FRAGMENT.name());
        }
        return view;
    }

    public CallFunctionManualFragment() {
    }

    void startTimer() {
        cTimer = new CountDownTimer(Constants.countTimerLong, 1000) {
            public void onTick(long millisUntilFinished) {
                callCount.setText(String.valueOf(millisUntilFinished / 1000));
            }

            public void onFinish() {
                showAlertDialog(getResources().getString(R.string.txtAlertTitleGreatAlert), getResources().getString(R.string.txtCount_test_Dialog), getResources().getString(R.string.Yes),
                        getResources().getString(R.string.No));
            }
        };
        cTimer.start();

    }

    public void showAlertDialog(String dialogTitle, String dialogMessage, String btnTextPositive, String btnTextNegative) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                ctx);

        // Setting Dialog Title
        alertDialog.setTitle(dialogTitle);

        // Setting Dialog Message
        alertDialog.setMessage(dialogMessage);
        alertDialog.setCancelable(false);
        // Setting Icon to Dialog
        /*alertDialog.setIcon(R.drawable.tick);*/

        // Setting OK Button
        alertDialog.setPositiveButton(btnTextPositive
                , new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        isDialogShown = false;

                        startTimer();
                        // Write your code here to execute after dialog closed
                    }
                });
        alertDialog.setNegativeButton(btnTextNegative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_30.name(), AsyncConstant.TEST_FAILED);
                isDialogShown = false;

                if(Constants.isManualIndividual){
                    cancelTimer();
                    nextButtonHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            Constants.isManualIndividual = false;
                            nextButtonHandler.removeCallbacksAndMessages(null);
                            replaceFragment(R.id.container, new ManualTestFragment((InterfaceButtonTextChange) getActivity()), FragmentTag.MANUAL_TEST_FRAGMENT.name(), false);
                        }
                    },2000);

                }
                else {
                   // onNextPress();
                }

            }
        });

        // Showing Alert Message
        if (!isDialogShown) {
            isDialogShown = true;
            alertDialog.show();
        }
    }


    //cancel timer
    void cancelTimer() {
        if (cTimer != null)
            cTimer.cancel();
    }

    private void initViews() {
        telMgr = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        callFunLayout = (LinearLayout) view.findViewById(R.id.callFunLayout);
        callCount = (TextView) view.findViewById(R.id.callCount);

          /*	Mobile*/
        mtvStatusMobileManual = (ShimmerTextView) view.findViewById(R.id.tvStatusMobileManual);
        mtvOperatorManual = (ShimmerTextView) view.findViewById(R.id.tvOperatorManual);
        mtvOperatorCodeManual = (ShimmerTextView) view.findViewById(R.id.tvOperatorCodeManual);
        mtvPhoneTypeManual = (ShimmerTextView) view.findViewById(R.id.tvPhoneTypeManual);
        mtvNetworkTypeManual = (ShimmerTextView) view.findViewById(R.id.tvNetworkTypeManual);
        mobileInfo();
    }

    private void mobileInfo() {
        isAllowBack = false;
        //Sim card status
        callSequencialAsync(0, mtvStatusMobileManual);
    }

    private void callSequencialAsync(int which, ShimmerTextView txShimmerTextView) {
        new Call_Function_Asynctask(ctx, (WebServiceCallback) this, which, txShimmerTextView).execute();
    }

    @Override
    public void onServiceResponse(boolean serviceStatus, String response, int callbackID) {
        switch (callbackID) {
            case 0:
                mtvStatusMobileManual.setText(getSimCardStatus());
                mtvStatusMobileManual.setTextColor(getColor());
                callSequencialAsync(1, mtvOperatorManual);
                break;

            case 1:
                mtvOperatorManual.setText(getOperatorName());
                mtvOperatorManual.setTextColor(getColor());
                callSequencialAsync(2, mtvOperatorCodeManual);
                break;

            case 2:
                mtvOperatorCodeManual.setText(telMgr.getNetworkOperator());
                mtvOperatorCodeManual.setTextColor(getColor());
                callSequencialAsync(3, mtvPhoneTypeManual);
                break;

            case 3:
                mtvPhoneTypeManual.setText(getPhoneType());
                mtvPhoneTypeManual.setTextColor(getColor());
                callSequencialAsync(4, mtvNetworkTypeManual);
                break;

            case 4:
                mtvNetworkTypeManual.setText(getNetworkType());
                mtvNetworkTypeManual.setTextColor(getColor());
                isAllowBack = true;
                isResumed = true;
                mCallBack.onChangeText(utils.BUTTON_NEXT, true);
                if (manualDataStable.checkSingleHardware(JsonTags.MMR_30.name(), ctx, utils) == 1){
                    utils.showToast(ctx,getResources().getString(R.string.txtManualPass));
                }else {
                    utils.showToast(ctx,getResources().getString(R.string.txtManualFail));
                }

                if(Constants.isManualIndividual){
                    cancelTimer();
                    nextButtonHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            Constants.isManualIndividual = false;
                            nextButtonHandler.removeCallbacksAndMessages(null);
                            replaceFragment(R.id.container, new ManualTestFragment((InterfaceButtonTextChange) getActivity()), FragmentTag.MANUAL_TEST_FRAGMENT.name(), false);
                        }
                    },2000);

                }
                else {
                  //  onNextPress();
                      }


                break;
        }
    }

    private int getColor() {
        return ctx.getResources().getColor(R.color.yellow_font_Color);
    }

    /**
     * Here change the header text
     ***/
    @Override
    public void onResume() {
        TitleBarFragment fragment = (TitleBarFragment) getFragment(R.id.headerContainer);
        if (fragment != null) {
            fragment.setTitleBarVisibility(true);
            fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string.txtManualCallFunction), true, false, 0);
            if (isResumed) {
                mCallBack.onChangeText(utils.BUTTON_NEXT, true);

            }
            if (Constants.isBackButton) {
                mCallBack.onChangeText(utils.BUTTON_NEXT, true);
            }
            // mCallBack.onChangeText(utils.BUTTON_SKIP, false);
        }
        nextButtonHandler = new Handler();
        if (!isDialogShown) {
            if (isResumed) {
                if (Constants.isManualIndividualResume) {
                    Constants.isManualIndividualResume = false;
                    cancelTimer();
                    nextButtonHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            Constants.isManualIndividual = false;
                            nextButtonHandler.removeCallbacksAndMessages(null);
                            replaceFragment(R.id.container, new ManualTestFragment((InterfaceButtonTextChange) getActivity()), FragmentTag.MANUAL_TEST_FRAGMENT.name(), false);
                        }
                    }, 1000);

                } else {
                    //onNextPress();
                }
            } else {
                startTimer();
            }


        }

        super.onResume();
    }

    /**
     * This method Return The phone type
     *
     * @return String
     */

    private String getPhoneType() {
        int phoneType = telMgr.getPhoneType();

        switch (phoneType) {
            case (TelephonyManager.PHONE_TYPE_CDMA):
                return getResources().getString(R.string.txtTypeCDMA);
            // your code

            case (TelephonyManager.PHONE_TYPE_GSM):
                return (getResources().getString(R.string.txtTypeGSM));
            // your code

            case (TelephonyManager.PHONE_TYPE_NONE):
                return (getResources().getString(R.string.txtTypeNone));
            // your code
        }
        return null;
    }

    /**
     * This method return network type
     *
     * @return String
     */
    private String getNetworkType() {
        int networkType = telMgr.getNetworkType();
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return "1xRTT";
            // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return "CDMA";
            // ~ 14-64 kbps
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return "EDGE";
            // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return "EVDO_0";
            // ~ 400-1000 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return "EDVO_A";
            // ~ 600-1400 kbps
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return "GPRS";
            // ~ 100 kbps
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return "HSDPA";
            // ~ 2-14 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return "HSPA";
            // ~ 700-1700 kbps
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return "HSUPA";
            // ~ 1-23 Mbps
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return "UMTS";
            // ~ 400-7000 kbps
            /*
             * Above API level 7, make sure to set android:targetSdkVersion
             * to appropriate level to use these
             */
            case TelephonyManager.NETWORK_TYPE_EHRPD: // API level 11
                return "EHRPD";

            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return "EVDO_B";// API level 9
            // ~ 5 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "HSPAP";// API level 13
            // ~ 10-20 Mbps
            case TelephonyManager.NETWORK_TYPE_IDEN: // API level 8
                return "IDEN";
            // ~25 kbps
            case TelephonyManager.NETWORK_TYPE_LTE:
                return "LTE";// API level 11
            // ~ 10+ Mbps
            // Unknown
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return "UNKNOWN";
            default:
                return "NONE";
        }
    }

    /**
     * This method return operator name
     *
     * @return String
     */
    private String getOperatorName() {
        if ((telMgr.getNetworkOperatorName()).isEmpty()) {
            return (getResources().getString(R.string.txtSimNone));


        } else {
            return (telMgr.getNetworkOperatorName());
        }

    }

    /**
     * This method return sim card status
     *
     * @return String
     */
    private String getSimCardStatus() {
        int simState = telMgr.getSimState();
        switch (simState) {
            case TelephonyManager.SIM_STATE_ABSENT:
                utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_30.name(), AsyncConstant.TEST_FAILED);

                return (getResources().getString(R.string.txtSimDisconnect));

            case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_30.name(), AsyncConstant.TEST_FAILED);
                return (getResources().getString(R.string.txtSimNetworkLock));

            case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_30.name(), AsyncConstant.TEST_FAILED);
                return (getResources().getString(R.string.txtSimPinRequired));

            case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_30.name(), AsyncConstant.TEST_FAILED);

                return (getResources().getString(R.string.txtSimPukRequired));
            case TelephonyManager.SIM_STATE_READY:
                utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_30.name(), AsyncConstant.TEST_PASS);
                return (getResources().getString(R.string.txtSimConnect));

            case TelephonyManager.SIM_STATE_UNKNOWN:
                utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_30.name(), AsyncConstant.TEST_FAILED);
                return (getResources().getString(R.string.txtSimUnkown));


        }
        return null;
    }


    /**
     * Show user the Snack bar till the time automated test wont complete.
     */
    public void onBackPress() {
        if (!isAllowBack) {
            utils.showSnackBar(view, getResources().getString(R.string.txtAlertBackPress), getResources().getColor(R.color.yellow_font_Color));
        } else {
            snackShow(callFunLayout);
        }
    }

    public void snackShow(LinearLayout relativeLayout) {
        Snackbar snackbar = Snackbar
                .make(relativeLayout, getResources().getString(R.string.txtBackPressed), Snackbar.LENGTH_LONG)
                .setAction(getResources().getString(R.string.txtBackPressedYes), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Constants.isNextHandler = true;

                        isAllowBack = false;
                        Constants.isBackButton = true;
                        popFragment(R.id.container);
                    }
                });
        // Changing message text color
        snackbar.setActionTextColor(Color.WHITE);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);

        snackbar.show();
    }

  /*  public void onNextPress() {
        cancelTimer();
        final MainActivity mainActivity = (MainActivity)getActivity();
        if (Constants.isNextHandler) {
            Constants.isNextHandler = false;

            if (Constants.isSkipButton) {
                utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_30.name(), AsyncConstant.TEST_FAILED);
            }
            isResumed = false;
            Constants.isBackButton = false;
            isNextPressed = true;
            mCallBack.onChangeText(utils.BUTTON_NEXT, false);

            if(mainActivity.index == mainActivity.automatedTestListModels.size()){
                clearAllStack();

                replaceFragment(R.id.container, ManualTestsOperation.launchScreens(-1,mainActivity.automatedTestListModels.get(mainActivity.automatedTestListModels.size()-1).getTest_id()), FragmentTag.GPS_FRAGMENT.name(), true);

            }else {

                replaceFragment(R.id.container, ManualTestsOperation.launchScreens(mainActivity.automatedTestListModels.get(mainActivity.index).getTest_id(), mainActivity.automatedTestListModels.get(mainActivity.automatedTestListModels.size() - 1).getTest_id()), FragmentTag.GPS_FRAGMENT.name(), true);
                mainActivity.index++;
            }

        } else {
            nextButtonHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (Constants.isSkipButton) {
                        utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_30.name(), AsyncConstant.TEST_FAILED);
                    }
                    isResumed = false;
                    Constants.isBackButton = false;
                    isNextPressed = true;
                    mCallBack.onChangeText(utils.BUTTON_NEXT, false);

                    if(mainActivity.index == mainActivity.automatedTestListModels.size()){
                        clearAllStack();

                        replaceFragment(R.id.container, ManualTestsOperation.launchScreens(-1,mainActivity.automatedTestListModels.get(mainActivity.automatedTestListModels.size()-1).getTest_id()), FragmentTag.GPS_FRAGMENT.name(), true);

                    }else {
                        replaceFragment(R.id.container, ManualTestsOperation.launchScreens(mainActivity.automatedTestListModels.get(mainActivity.index).getTest_id(), mainActivity.automatedTestListModels.get(mainActivity.automatedTestListModels.size() - 1).getTest_id()), FragmentTag.GPS_FRAGMENT.name(), true);
                        mainActivity.index++;
                    }
                }
            }, 2000);
        }
    }*/

    @Override
    public void onPause() {
        cancelTimer();
        nextButtonHandler.removeCallbacksAndMessages(null);
        super.onPause();
    }
}


