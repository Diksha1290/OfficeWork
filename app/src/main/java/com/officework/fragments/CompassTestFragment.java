package com.officework.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
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
import com.officework.customViews.IconView;
import com.officework.interfaces.CompassValuesInterface;
import com.officework.interfaces.InterfaceButtonTextChange;
import com.officework.interfaces.TimerDialogInterface;
import com.officework.models.AutomatedTestListModel;
import com.officework.testing_profiles.Controller.TestController;
import com.officework.testing_profiles.utils.ConstantTestIDs;
import com.officework.utils.TestResultUpdateToServer;
import com.officework.utils.Utilities;

import java.util.Observable;
import java.util.Observer;

import static android.content.Context.SENSOR_SERVICE;

@SuppressLint("ValidFragment")
public class CompassTestFragment extends BaseFragment implements Observer,CompassValuesInterface, TimerDialogInterface
//        , SensorEventListener
{
   View view;
   Utilities utils;
   Context ctx;
   TestController testController;

    TextView txt;
    AlertDialog alertDialog;
    //Handler nextButtonHandler;
    boolean isTestPerformed = false;
    InterfaceButtonTextChange mCallBack;
    TestResultUpdateToServer testResultUpdateToServer;
    int mAzimuth;
    private SensorManager mSensorManager;
    private Sensor mRotationV, mAccelerometer, mMagnetometer;
    boolean haveSensor = false, haveSensor2 = false;
    float[] rMat = new float[9];
    float[] orientation = new float[3];
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    RelativeLayout compassLayout;
    IconView compassPinImage ,compassImage;
    MainActivity mainActivity;

    boolean isOneQuarter,isTwoQuarter,isThreeQuarter,isFourQuarter;

    @SuppressLint("ValidFragment")
    public CompassTestFragment(InterfaceButtonTextChange callback ) {
        mCallBack = callback;
    }

    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_compasstest_layout, null);
            utils = Utilities.getInstance(getActivity());
            ctx = getActivity();
          //  Crashlytics.getInstance().log(FragmentTag.COMPASS_TEST_FRAGMENT.name());
            initViews();
            testController = TestController.getInstance(getActivity());
            testController.addObserver(this);
            timer(getActivity(),false,ConstantTestIDs.COMPASS,CompassTestFragment.this);
        }
        return view;
    }

    private void initViews() {
       mainActivity = (MainActivity)getActivity();
        mainActivity.onChangeText(R.string.textSkip,true);
        testResultUpdateToServer = new TestResultUpdateToServer(utils, ctx, getActivity());
        compassLayout=(RelativeLayout) view.findViewById(R.id.compasslayout);
        mSensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        compassImage=(IconView) view.findViewById(R.id.compassimg);
        compassPinImage =(IconView) view.findViewById(R.id.compasspin);
        txt=(TextView)view.findViewById(R.id.txt);

    }


    @Override
    public void onResume() {
        if(dialog==null || !dialog.isShowing()) {
            testController.performOperation(ConstantTestIDs.COMPASS, CompassTestFragment.this);
        }
        TitleBarFragment fragment = (TitleBarFragment) getFragment(R.id.headerContainer);
        if (fragment != null) {
//            fragment.setTitleBarVisibility(true);
//            fragment.setSyntextVisibilty(false);
//
//            fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string.compass), false, false, 0);
           // nextButtonHandler = new Handler();
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
        testController.unregisterCompass();
    }

    /**
     * Called when test is completed or user click on skip button
     */
    public void onNextPress() {
        try {
            timer(getActivity(),true,ConstantTestIDs.COMPASS,null);
            MainActivity activity = (MainActivity) getActivity();
//            activity.onChangeText(R.string.textSkip,false);
            if (Constants.isSkipButton) {
                try {
                    testController.unregisterCompass();
                }catch (Exception e){
                    e.printStackTrace();
                }
                isTestPerformed=true;
                 compassPinImage.setImageDrawable(getResources().getDrawable(R.drawable.compass_pin),false,getActivity());
                 compassImage.setImageDrawable(getResources().getDrawable(R.drawable.compass1),false,getActivity());
                 utils.showToast(ctx, getResources().getString(R.string.txtManualFail));

            }
            boolean semi=false;
            nextPress(activity,semi);


        } catch (Exception e) {

        }

    }

    public CompassTestFragment() {
    }

    /**
     * onBackPressed
     */
    public void onBackPress() {
        try {
            snackShow(compassLayout);
        }catch (Exception e){
           e.printStackTrace();
        }

    }



    @Override
    public void update(Observable observable, Object o) {
        try {
            AutomatedTestListModel automatedTestListModel = (AutomatedTestListModel)o;
            isTestPerformed=true;

            if (automatedTestListModel.getIsTestSuccess() == AsyncConstant.TEST_PASS) {
                compassPinImage.setColorFilter(getResources().getColor(R.color.green));
                compassImage.setImageDrawable(getResources().getDrawable(R.drawable.compass1), true, getActivity());
                MainActivity mainActivity = (MainActivity)getActivity();
                mainActivity.onChangeText(R.string.textSkip,false);

                utils.showToast(ctx, getResources().getString(R.string.txtManualPass));

                onNextPress();

            }

            else {
              //  utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_29.name(), AsyncConstant.TEST_FAILED);
               // updateResultToServer();
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
            timer(getActivity(),true,ConstantTestIDs.COMPASS,null);
            testController.deleteObserver(this);

        }
        catch (Exception e){
            e.printStackTrace();
        }
        try {
            testController.unregisterCompass();
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    @Override
    public void compassValues(String values) {
        txt.setText(values);
        String a=values;
        String numberOnly= a.replaceAll("[^0-9]", "");
        int m=Integer.parseInt(numberOnly);
        compassPinImage.setRotation(-m);


    }
    @Override
    public void timerFail() {
        isTestPerformed=true;
        testController.saveSkipResponse(0,ConstantTestIDs.COMPASS);
        utils.showToast(ctx, getResources().getString(R.string.txtManualFail));
        compassPinImage.setImageDrawable(getResources().getDrawable(R.drawable.compass_pin),false,getActivity());
        compassImage.setImageDrawable(getResources().getDrawable(R.drawable.compass1),false,getActivity());
        onNextPress();
    }
}
