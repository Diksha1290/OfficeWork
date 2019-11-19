package com.officework.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.officework.R;
import com.officework.activities.MainActivity;
import com.officework.base.BaseFragment;
import com.officework.constants.AsyncConstant;
import com.officework.constants.Constants;
import com.officework.customViews.IconView;
import com.officework.interfaces.InterfaceButtonTextChange;
import com.officework.interfaces.InterfaceWindowsOnFocusChanged;
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
public class AccelerometerTestFragment extends BaseFragment implements Observer, InterfaceWindowsOnFocusChanged,SensorEventListener , TimerDialogInterface {
    View view;
    Utilities utils;
    Context ctx;
    TestController testController;
    TestResultUpdateToServer testResultUpdateToServer;
    //Handler nextButtonHandler;
    boolean isTestPerformed = false;
    InterfaceButtonTextChange mCallBack;
    MainActivity mainActivity;
    RelativeLayout accLayout,accelerometer_desRL;
    SensorManager mSensorManager;
    Sensor mAccelerometer;
    ConstraintLayout relativeLayout;
    private AnimatedView mAnimatedView = null;
    TextView txt1,tx2,tx4;
    int[] tx1cr;
    int[] tx2cr;
    Rect rc1, rc2, rc3,rc4;
    int[] tx4cr;
    int txt1_x,txt1_y,tx2_x,tx2_y,txt4_x,txt4_y;
    int topOffset;
    boolean ball1Gone=false;
    boolean ball2Gone=false;
    boolean ball3Gone=false;
    Rect rectFirstView;
    Rect rectSecondView;
    Rect rectThirdView;
    Rect rectTargetView;
    ImageView mDrawable;
    AnimatedView animatedView;
    private TextView tx3;
    private Button startacc_BT;
    ImageView mImageView;
    Bitmap bitmap;
    IconView accImage;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_manual_accelerometer, null);
            view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            utils = Utilities.getInstance(getActivity());
            ctx = getActivity();
            //  Crashlytics.getInstance().log(FragmentTag.ACCELEROMETER_TEST_FRAGMENT.name());
            initViews();

        }
        return view;
    }

    public AccelerometerTestFragment() {
    }

    private void initViews() {
        timer(getActivity(),false, ConstantTestIDs.ACCELEROMETER ,AccelerometerTestFragment.this);
        mainActivity = (MainActivity) getActivity();
        mainActivity.onChangeText(R.string.textSkip, true);
        testResultUpdateToServer = new TestResultUpdateToServer(utils, ctx, getActivity());
        accLayout=(RelativeLayout)view.findViewById(R.id.accLayout);
        accelerometer_desRL=(RelativeLayout)view.findViewById(R.id.accelerometer_desRL);
        accImage=view.findViewById(R.id.imgid);
        //nextButtonHandler = new Handler();
        mSensorManager = (SensorManager) getActivity().getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mAnimatedView = new AnimatedView(ctx);
        relativeLayout=(ConstraintLayout)view.findViewById(R.id.linear);
        relativeLayout.addView(mAnimatedView);
        txt1=view.findViewById(R.id.txt1);
        tx2=view.findViewById(R.id.tx2);
        tx4=view.findViewById(R.id.txt4);
        startacc_BT=(Button)view.findViewById(R.id.startacc_BT);
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        topOffset = dm.heightPixels -view.getMeasuredHeight();
        testController = TestController.getInstance(getActivity());
        testController.addObserver(this);
        tx3=new TextView(getActivity());
        tx3.setBackgroundResource(R.drawable.ball);
        relativeLayout.addView(tx3);

        startacc_BT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer(getActivity(),true, ConstantTestIDs.ACCELEROMETER ,null);
                timer(getActivity(),false, ConstantTestIDs.ACCELEROMETER ,AccelerometerTestFragment.this);
                accelerometer_desRL.setVisibility(View.GONE);
                    accelerometrStart();
            }
        });
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mAnimatedView.onSensorEvent(event);
        }
        getCoordinates();
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    @Override
    public void onWindowsFocusedChange(boolean a) {
        if(a)
        {
            getCoordinates();
        }

    }

    public void getCoordinates(){
        tx1cr = new int[2];
        txt1.getLocationOnScreen(tx1cr);
        txt1_x=tx1cr[0];
        txt1_y=tx1cr[1]-topOffset;
        tx2cr = new int[2];
        tx2.getLocationOnScreen(tx2cr);
        tx2_x=tx2cr[0];
        tx2_y=tx2cr[1]-topOffset;
        tx4cr = new int[2];
        tx4.getLocationOnScreen(tx4cr);
        txt4_x=tx4cr[0];
        txt4_y=tx4cr[1]-topOffset;
    }


    public class AnimatedView extends View {

        private final int CIRCLE_RADIUS = (int) getResources().getDimension(R.dimen.circle_radius); //pixels
        private Paint mPaint;
        private int x;
        private int y;
        private int viewWidth;
        private int viewHeight;
        int radius;
        int padding;
        Canvas canvas;
        boolean yo=true;

        public AnimatedView(Context context) {
            super(context);
            mPaint = new Paint();
            mPaint.setColor(getResources().getColor(R.color.colorPrimary));


        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            viewWidth = w;
            viewHeight = h;
        }

        public void onSensorEvent(SensorEvent event) {
            x = x - (int) event.values[0] * 6;
            y = y + (int) event.values[1] * 6;

            if (x <= 0 + CIRCLE_RADIUS) {
                x = 0 + CIRCLE_RADIUS;
            }
            if (x >= viewWidth - CIRCLE_RADIUS) {
                x = viewWidth - CIRCLE_RADIUS;
            }
            if (y <= 0 + CIRCLE_RADIUS) {
                y = 0 + CIRCLE_RADIUS;
            }
            if (y >= viewHeight - CIRCLE_RADIUS) {
                y = viewHeight - CIRCLE_RADIUS;
            }


            tx3.setX(x-CIRCLE_RADIUS);
            tx3.setY(y-CIRCLE_RADIUS);
            tx3.invalidate();



        }

        private void startAnimation1(final TextView txt,final Rect rc) {
            Animation scale_up_animation = AnimationUtils.loadAnimation(getActivity(), R.anim.out_anim);
            if(txt.getId()==R.id.tx2){
                txt.setBackgroundResource(R.drawable.ball_yellow);
            }else if(txt.getId()==R.id.txt4){
                txt.setBackgroundResource(R.drawable.ball_red);
            }else if(txt.getId()==R.id.txt1){
                txt.setBackgroundResource(R.drawable.ballgreen);
            }
            scale_up_animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    txt.setVisibility(View.VISIBLE);
                }
                @Override
                public void onAnimationEnd(Animation animation) {
                    txt.setVisibility(View.GONE);
                    rc.setEmpty();

                }
                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            txt.startAnimation(scale_up_animation);

        }

        @SuppressLint("NewApi")
        @Override
        public void onDraw(Canvas canvas) {

            int[] targerPosition = new int[2];
            tx3.getLocationOnScreen(targerPosition);
            rc1 = new Rect(targerPosition[0], targerPosition[1],
                    targerPosition[0] + tx3.getMeasuredWidth(), targerPosition[1] + tx3.getMeasuredHeight());

            int[] firstPosition = new int[2];
            int[] secondPosition = new int[2];
            int[] thirdPoisition = new int[2];
            txt1.getLocationOnScreen(firstPosition);
            tx2.getLocationOnScreen(secondPosition);
            tx4.getLocationOnScreen(thirdPoisition);
            if(yo) {
                rc2 = new Rect(secondPosition[0], secondPosition[1],
                        secondPosition[0] + tx2.getMeasuredWidth(), secondPosition[1] + tx2.getMeasuredHeight());

                rc4 = new Rect(thirdPoisition[0], thirdPoisition[1],
                        thirdPoisition[0] + tx4.getMeasuredWidth(), thirdPoisition[1] + tx4.getMeasuredHeight());
                rc3 = new Rect(firstPosition[0], firstPosition[1],
                        firstPosition[0] + txt1.getMeasuredWidth(), firstPosition[1] + txt1.getMeasuredHeight());
                yo=false;
            }


            if (rc1.intersect(rc2)) {
                ball2Gone=true;
                startAnimation1(tx2,rc2);
                timer(getActivity(),true,  ConstantTestIDs.ACCELEROMETER,AccelerometerTestFragment.this);
                timer(getActivity(),false,ConstantTestIDs.ACCELEROMETER,AccelerometerTestFragment.this);
            }

            if (rc1.intersect(rc3)) {
                ball3Gone=true;
                startAnimation1(txt1,rc3);
                timer(getActivity(),true, ConstantTestIDs.ACCELEROMETER, AccelerometerTestFragment.this);
                timer(getActivity(),false, ConstantTestIDs.ACCELEROMETER,AccelerometerTestFragment.this);
            }

            if (rc1.intersect(rc4)) {
                ball1Gone=true;
                startAnimation1(tx4,rc4);
                timer(getActivity(),true, ConstantTestIDs.ACCELEROMETER,AccelerometerTestFragment.this);
                timer(getActivity(),false, ConstantTestIDs.ACCELEROMETER,AccelerometerTestFragment.this);
            }
            if (ball1Gone && ball2Gone && ball3Gone) {
                if(!isTestPerformed) {
                    testController.onServiceResponse(true, "Accelerometer", ConstantTestIDs.ACCELEROMETER);
                }
                isTestPerformed = true;
                mCallBack.onChangeText(utils.BUTTON_NEXT, true);
                mSensorManager.unregisterListener(AccelerometerTestFragment.this);
                //nextButtonHandler.removeCallbacksAndMessages(this);
            }

        }


    }


    void accelerometrStart()
    {
        if(dialog==null || !dialog.isShowing()) {
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        }
    }
    @Override
    public void onResume() {

        TitleBarFragment fragment = (TitleBarFragment) getFragment(R.id.headerContainer);
        if (fragment != null) {
//                fragment.setTitleBarVisibility(true);
//                fragment.setSyntextVisibilty(false);
//                fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string.acclerometer), false, false, 0);
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
        mSensorManager.unregisterListener(this);
        // nextButtonHandler.removeCallbacksAndMessages(null);
        super.onPause();
    }
    public AccelerometerTestFragment(InterfaceButtonTextChange callback) {
        mCallBack = callback;
    }

    public void onNextPress () {
        try {
            MainActivity activity = (MainActivity) getActivity();
//                    activity.onChangeText(R.string.textSkip, false);
            if (Constants.isSkipButton) {
                accImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_accelerometer_new),false,getActivity());

                startacc_BT.setEnabled(false);
                startacc_BT.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque));
                try {
                    mSensorManager.unregisterListener(this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                utils.showToast(ctx, getResources().getString(R.string.txtManualFail));
                isTestPerformed=true;

            }
            boolean semi=false;
            nextPress(activity,semi);

        } catch (Exception e) {

        }

    }

    /**
     * onBackPressed
     */
    public void onBackPress () {

        try {

            snackShow(accLayout);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Override
    public void update (Observable observable, Object o){
        try {
            AutomatedTestListModel automatedTestListModel = (AutomatedTestListModel) o;
            if (automatedTestListModel.getIsTestSuccess() == AsyncConstant.TEST_PASS) {
                mainActivity.onChangeText(R.string.textSkip, false);
                isTestPerformed = true;

                utils.showToast(ctx, getResources().getString(R.string.txtManualPass));
                testController.deleteObserver(this);

                onNextPress();

            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onDestroyView () {
        super.onDestroyView();
        try {
            timer(getActivity(),true,ConstantTestIDs.ACCELEROMETER,this);
            testController.deleteObserver(this);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        try {
            mSensorManager.unregisterListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unrigsterAccelerometer()
    {
        mSensorManager.unregisterListener(this);
    }
    public void rigsterAccelerometer()
    {
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    }
    @Override
    public void timerFail() {
        startacc_BT.setEnabled(false);
        startacc_BT.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque));
        isTestPerformed=true;
        accImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_accelerometer_new),false,getActivity());
        utils.showToast(ctx, getResources().getString(R.string.txtManualFail));
        onNextPress();
    }
}
