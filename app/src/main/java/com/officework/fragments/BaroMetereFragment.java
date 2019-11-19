package com.officework.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.RelativeLayout;
import android.widget.TextView;


import com.officework.R;
import com.officework.activities.MainActivity;
import com.officework.base.BaseFragment;
import com.officework.constants.AsyncConstant;
import com.officework.constants.Constants;

import com.officework.customViews.GaugeView;

import com.officework.interfaces.InterfaceButtonTextChange;
import com.officework.models.AutomatedTestListModel;
import com.officework.testing_profiles.Controller.TestController;
import com.officework.testing_profiles.utils.ConstantTestIDs;
import com.officework.utils.TestResultUpdateToServer;
import com.officework.utils.Utilities;

import java.util.Observable;
import java.util.Observer;


@SuppressLint("ValidFragment")
public class BaroMetereFragment extends BaseFragment implements Observer {
    View view;
    Utilities utils;
    Context ctx;
    TestController testController;
    TestResultUpdateToServer testResultUpdateToServer;
    //Handler nextButtonHandler;
    boolean isTestPerformed = false;
    InterfaceButtonTextChange mCallBack;
    AlertDialog alertDialog;
    RelativeLayout bmrLayout;
    MainActivity mainActivity;
    private GaugeView gaugeView;
    TextView barometerevalue;
    Runnable runnable;
    private Handler handler;

    @SuppressLint("NewApi")
    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        if (view == null) {
            view = inflater.inflate(R.layout.barometer_fragment, null);
            utils = Utilities.getInstance(getActivity());
            ctx = getActivity();
           // Crashlytics.getInstance().log(FragmentTag.BAROMETER_TEST_FRAGMENT.name());
            initViews();
            testController = TestController.getInstance(getActivity());
            testController.addObserver(this);

        }
        return view;
    }
    private void initViews() {
        mainActivity = (MainActivity)getActivity();
        mainActivity.onChangeText(R.string.textSkip,true);
        testResultUpdateToServer = new TestResultUpdateToServer(utils, ctx, getActivity());
        bmrLayout=(RelativeLayout) view.findViewById(R.id.barroLay);
        gaugeView = (GaugeView)view. findViewById(R.id.gauge_view);
        barometerevalue=view.findViewById(R.id.barovalue);
       // nextButtonHandler = new Handler();
    }
    @Override
    public void onResume() {
        TitleBarFragment fragment = (TitleBarFragment) getFragment(R.id.headerContainer);
        if (fragment != null) {
//            fragment.setTitleBarVisibility(true);
//            fragment.setSyntextVisibilty(false);
//            fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string.barometer), false, false, 0);
          //  nextButtonHandler = new Handler();
            if (isTestPerformed) {
                mCallBack.onChangeText(Utilities.BUTTON_NEXT, true);
               onNextPress();
            } else {
                mCallBack.onChangeText(Utilities.BUTTON_SKIP, true);
            }
        }
        handler = new Handler();
        handler.postDelayed(runnable=new Runnable() {
            @Override
            public void run() {
                testController.performOperation(ConstantTestIDs.Barometer);
            }
        }, 3000);
        super.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }
    public BaroMetereFragment(InterfaceButtonTextChange callback) {
        mCallBack = callback;
    }


    public BaroMetereFragment() {
    }

    /**
     * Called when test is completed or user click on skip button
     */
    public void onNextPress() {
        try {
            MainActivity activity = (MainActivity) getActivity();
            if (Constants.isSkipButton) {
                handler.removeCallbacks(runnable);
                isTestPerformed=true;
                utils.showToast(ctx, getResources().getString(R.string.txtManualFail));


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
        try {
            snackShow(bmrLayout);
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public void update(Observable observable, Object o) {
        try {
            AutomatedTestListModel automatedTestListModel = (AutomatedTestListModel)o;
            gaugeView.setTargetValue(Float.parseFloat(automatedTestListModel.getSensorValue()));
            barometerevalue.setText(automatedTestListModel.getSensorValue());
            Log.d("mnbvc",automatedTestListModel.getSensorValue());
            if (automatedTestListModel.getIsTestSuccess() == AsyncConstant.TEST_PASS) {
                mainActivity.onChangeText(R.string.textSkip,false);
                isTestPerformed = true;

                utils.showToast(ctx, getResources().getString(R.string.txtManualPass));

                onNextPress();

            }

            else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
handler.removeCallbacks(runnable);
        try {
            testController.deleteObserver(this);
        }catch (Exception e){
            e.printStackTrace();
        }

    }




}
