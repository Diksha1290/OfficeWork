package com.officework.fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.officework.R;
import com.officework.activities.MainActivity;
import com.officework.base.BaseFragment;
import com.officework.constants.Constants;
import com.officework.customViews.CustomButton;
import com.officework.customViews.IconView;
import com.officework.interfaces.InterfaceButtonTextChange;
import com.officework.interfaces.TimerDialogInterface;
import com.officework.models.AutomatedTestListModel;
import com.officework.testing_profiles.Controller.TestController;
import com.officework.testing_profiles.utils.ConstantTestIDs;
import com.officework.utils.TestResultUpdateToServer;
import com.officework.utils.Utilities;

import java.util.Observable;
import java.util.Observer;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class DeviceCasingTestFragment extends BaseFragment implements Observer, TimerDialogInterface {

    View view;
    Utilities utils;
    Context ctx;
    InterfaceButtonTextChange mCallBack;
    LinearLayout layoutDeviceCase;
    TestResultUpdateToServer testResultUpdateToServer;
    Button btnDeviceCasingOk, btnDeviceCasingNotOk;
   // Handler nextButtonHandler;
    boolean isTestPerformed = false;
    private TestController testController;
    IconView devicecImage;

    public DeviceCasingTestFragment() {
    }

    public DeviceCasingTestFragment(InterfaceButtonTextChange callback) {
        mCallBack = callback;
    }

    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_device_casing_test, null);
            utils = Utilities.getInstance(getActivity());
            ctx = getActivity();
            initViews();
          //  Crashlytics.getInstance().log(FragmentTag.DEVICE_CASING_FRAGMENT.name());

            testController = TestController.getInstance(getActivity());
            testController.addObserver(this);
            timer(getActivity(),false,ConstantTestIDs.DEVICE_CASING_ID,DeviceCasingTestFragment.this);
            testController.performOperation(ConstantTestIDs.DEVICE_CASING_ID);
        }
        return view;
    }

    private void initViews() {
        MainActivity mainActivity = (MainActivity)getActivity();

        mainActivity.onChangeText(R.string.textSkip,true);
        testResultUpdateToServer = new TestResultUpdateToServer(utils, ctx, getActivity());
        layoutDeviceCase = (LinearLayout) view.findViewById(R.id.layoutDeviceCase);
        devicecImage=view.findViewById(R.id.dcImg);
        btnDeviceCasingNotOk = (CustomButton) view.findViewById(R.id.btnDeviceCasingNotOk);
        btnDeviceCasingOk = (CustomButton) view.findViewById(R.id.btnDeviceCasingOk);
        btnDeviceCasingOk.setOnClickListener(this);
        btnDeviceCasingNotOk.setOnClickListener(this);

      //  nextButtonHandler = new Handler();
        //updateResultToServer();
    }


    @Override
    public void onClick(View v) {
        btnDeviceCasingNotOk.setEnabled(false);
        btnDeviceCasingNotOk.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque30));
        btnDeviceCasingOk.setEnabled(false);
        btnDeviceCasingOk.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque30));
        isTestPerformed = true;
        switch (v.getId()) {
            case R.id.btnDeviceCasingNotOk:
                devicecImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_devicecasing_svg_64),false,getActivity());
                timer(getActivity(),true,ConstantTestIDs.DEVICE_CASING_ID,DeviceCasingTestFragment.this);
                testController.onServiceResponse(false,"Device Casing",ConstantTestIDs.DEVICE_CASING_ID);
               // utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_55.name(), 0);
                utils.showToast(ctx, getResources().getString(R.string.txtManualFail));
                onNextPress();

                break;
            case R.id.btnDeviceCasingOk:
                devicecImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_devicecasing_svg_64),true,getActivity());
                timer(getActivity(),true,ConstantTestIDs.DEVICE_CASING_ID,DeviceCasingTestFragment.this);
                testController.onServiceResponse(true,"Device Casing",ConstantTestIDs.DEVICE_CASING_ID);
                utils.showToast(ctx, getResources().getString(R.string.txtManualPass));
               // utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_55.name(), 1);
                onNextPress();

                break;
        }
        super.onClick(v);
    }

    /**
     * Here change the header text
     ***/
    @Override
    public void onResume() {
        TitleBarFragment fragment = (TitleBarFragment) getFragment(R.id.headerContainer);
        if (fragment != null) {
//            fragment.setTitleBarVisibility(true);
//            fragment.setSyntextVisibilty(false);
//
//            fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string.DeviceCasing), false, false, 0);
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
    //    nextButtonHandler.removeCallbacksAndMessages(null);
        super.onPause();
    }

    /**
     * Called when test is completed or user click on skip button
     */
    public void onNextPress() {
        try {
            timer(ctx,true,ConstantTestIDs.DEVICE_CASING_ID,DeviceCasingTestFragment.this);
            MainActivity activity = (MainActivity) getActivity();
            if (Constants.isSkipButton) {
                devicecImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_devicecasing_svg_64),false,getActivity());

                btnDeviceCasingNotOk.setEnabled(false);
                btnDeviceCasingOk.setEnabled(false);
                btnDeviceCasingNotOk.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque30));
                btnDeviceCasingOk.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque30));
                isTestPerformed=true;
                utils.showToast(ctx, getResources().getString(R.string.txtManualFail));
              //  utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_55.name(), AsyncConstant.TEST_FAILED);
               // updateResultToServer();

            }
            boolean semi=false;
            nextPress(activity,semi);

        } catch (Exception e) {

        }

    }

    /**
     * onBackPressed
     */
    public void onBackPress() {

        snackShow(layoutDeviceCase);

    }

    @Override
    public void update(Observable observable, Object o) {
        MainActivity mainActivity = (MainActivity)getActivity();

        mainActivity.onChangeText(R.string.textSkip,false);
        AutomatedTestListModel automatedTestListModel = (AutomatedTestListModel)o;


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            timer(getActivity(),true,ConstantTestIDs.DEVICE_CASING_ID,null);
            testController.deleteObserver(this);

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void timerFail() {
        devicecImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_devicecasing_svg_64),false,getActivity());

        btnDeviceCasingNotOk.setEnabled(false);
        btnDeviceCasingNotOk.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque30));
        btnDeviceCasingOk.setEnabled(false);
        btnDeviceCasingOk.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque30));
        isTestPerformed=true;
        utils.showToast(ctx, getResources().getString(R.string.txtManualFail));

        onNextPress();
    }
}
