package com.officework.fragments;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

import com.officework.R;
import com.officework.activities.MainActivity;
import com.officework.base.BaseFragment;
import com.officework.constants.AsyncConstant;
import com.officework.constants.Constants;
import com.officework.customViews.IconView;
import com.officework.interfaces.FaceBiometricErrorInterface;
import com.officework.interfaces.InterfaceButtonTextChange;
import com.officework.interfaces.TimerDialogInterface;
import com.officework.models.AutomatedTestListModel;
import com.officework.testing_profiles.Controller.TestController;
import com.officework.testing_profiles.utils.ConstantTestIDs;
import com.officework.utils.TestResultUpdateToServer;
import com.officework.utils.Utilities;

import java.security.KeyStore;
import java.util.Observable;
import java.util.Observer;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

@SuppressLint("ValidFragment")
public class FingerPrintTestFragment extends BaseFragment implements Observer, FaceBiometricErrorInterface, TimerDialogInterface {
    private static final String KEY_NAME = "yourKey";
    View view;
    Utilities utils;
    Context ctx;
    AlertDialog alertDialog;
    //Handler nextButtonHandler;
    boolean isTestPerformed = false;
    InterfaceButtonTextChange mCallBack;
    TestResultUpdateToServer testResultUpdateToServer;
    IconView fingerimage;
    LinearLayout fingerLayout;
    MainActivity mainActivity;
    Handler testFailHandler;
    private TestController testController;
    private Cipher cipher;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private TextView textView;
    private FingerprintManager.CryptoObject cryptoObject;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;

    @SuppressLint("ValidFragment")
    public FingerPrintTestFragment(InterfaceButtonTextChange callback) {
        mCallBack = callback;
    }

    public FingerPrintTestFragment() {
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_manual_fingerprint, null);
            utils = Utilities.getInstance(getActivity());
            ctx = getActivity();

            //     Crashlytics.getInstance().log(FragmentTag.FINGERPRINT_TEST_FRAGMENT.name());

            testController = TestController.getInstance(getActivity(),(FaceBiometricErrorInterface)this);
            testController.addObserver(this);
            initViews();
        }
        return view;
    }

    @TargetApi(Build.VERSION_CODES.P)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initViews() {
        try {
            timer(getActivity(),false, ConstantTestIDs.FINGERPRINT, FingerPrintTestFragment.this);
            fingerLayout = (LinearLayout) view.findViewById(R.id.fingerprintlayout);
            fingerimage = (IconView) view.findViewById(R.id.fingerimgid);
            textView = (TextView) view.findViewById(R.id.txt2);


            mainActivity = (MainActivity) getActivity();
            mainActivity.onChangeText(R.string.textSkip, true);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Override
    public void onResume() {

        boolean isFingerEnrolled;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P){
            FingerprintManagerCompat fingerprintManager = FingerprintManagerCompat.from(getActivity());
            isFingerEnrolled=fingerprintManager.hasEnrolledFingerprints();
        } else{
            FingerprintManager fingerprintManager = (FingerprintManager)getActivity()
                    .getSystemService(Context.FINGERPRINT_SERVICE);
            isFingerEnrolled=fingerprintManager.hasEnrolledFingerprints();

        }
//        Log.d("is hardware",fingerprintManager.isHardwareDetected()+"");
        if (!isFingerEnrolled) {

            textView.setText(R.string.nofingerconfigured);

        } else {
            try {
                if(dialog==null || !dialog.isShowing()) {
                    testController.performOperation(ConstantTestIDs.FINGERPRINT);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        TitleBarFragment fragment = (TitleBarFragment) getFragment(R.id.headerContainer);
        if (fragment != null) {
//            fragment.setTitleBarVisibility(true);
//            fragment.setSyntextVisibilty(false);
//
//            fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string.fingerprint),
//                    false, false, 0);
            if (isTestPerformed) {
                mCallBack.onChangeText(Utilities.BUTTON_NEXT, true);

               onNextPress();

            } else {
                mCallBack.onChangeText(Utilities.BUTTON_SKIP, true);
            }
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        testController.unregisterFingerPrintSensor();
    }

    /**
     * Called when test is completed or user click on skip button
     */
    public void onNextPress() {
        try {
            timer(ctx,true,ConstantTestIDs.FINGERPRINT,FingerPrintTestFragment.this);
            MainActivity activity = (MainActivity) getActivity();
            if (testFailHandler != null) {
                testFailHandler.removeCallbacksAndMessages(null);
            }
            if (Constants.isSkipButton) {
                try {
                    testController.unregisterFingerPrintSensor();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                isTestPerformed = true;
                fingerimage.setImageDrawable(getResources().getDrawable(R.drawable.fingerprint),
                        false, getActivity());
                utils.showToast(ctx, getResources().getString(R.string.txtManualFail));

            }
            boolean semi = false;
            nextPress(activity, semi);

        } catch (Exception e) {

        }

    }

    /**
     * onBackPressed
     */
    public void onBackPress() {

        try {

            snackShow(fingerLayout);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void update(Observable observable, Object o) {
        try {

            AutomatedTestListModel automatedTestListModel = (AutomatedTestListModel) o;
            isTestPerformed = true;
            try {
                if (testFailHandler != null) {
                    testFailHandler.removeCallbacksAndMessages(null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (automatedTestListModel.getIsTestSuccess() == AsyncConstant.TEST_PASS) {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                fingerimage.setImageDrawable(getResources().getDrawable(R.drawable.fingerprint),
                        true, getActivity());
                mainActivity.onChangeText(R.string.textSkip, false);

                utils.showToast(ctx, getResources().getString(R.string.txtManualPass));

                onNextPress();
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            timer(getActivity(),true,ConstantTestIDs.FINGERPRINT,null);
            testController.deleteObserver(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {

            testController.unregisterFingerPrintSensor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (testFailHandler != null) {
            testFailHandler.removeCallbacksAndMessages(null);
        }

    }
    @Override
    public void timerFail() {
        isTestPerformed=true;
        fingerimage.setImageDrawable(getResources().getDrawable(R.drawable.fingerprint),false,getActivity());
        utils.showToast(ctx, getResources().getString(R.string.txtManualFail));
        onNextPress();
    }

    @Override
    public void ErrorData(int error_code, CharSequence errorMessage) {
        try {
            if (error_code == 9) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    textView.setText(R.string.finger_out_of_move);
                    }
                });

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
