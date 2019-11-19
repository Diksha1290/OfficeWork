package com.officework.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.officework.R;
import com.officework.activities.MainActivity;
import com.officework.base.BaseFragment;
import com.officework.constants.Constants;
import com.officework.interfaces.InterfaceButtonTextChange;
import com.officework.interfaces.TimerDialogInterface;
import com.officework.testing_profiles.Controller.TestController;
import com.officework.testing_profiles.utils.ConstantTestIDs;
import com.officework.utils.ExceptionLogUtils;
import com.officework.utils.ManualDataStable;
import com.officework.utils.TestResultUpdateToServer;
import com.officework.utils.Utilities;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by Girish on 8/24/2016.
 */
@SuppressLint("ValidFragment")
public class MutitouchManualFragment extends BaseFragment implements Observer , TimerDialogInterface {
    View view;
    Utilities utils;
    Context ctx;
    MultitouchView multitouchView;
    InterfaceButtonTextChange mCallBack;
    RelativeLayout mLayout;
    ManualDataStable manualDataStable;
    TextView multiCount;
    int count = 1;
    public static boolean isTestPerform = false;
    boolean isTestPerformed = false;
   // Handler nextButtonHandler = null;
    CountDownTimer cTimer = null;
    boolean isDialogShown = false;
    TestResultUpdateToServer testResultUpdateToServer;
    boolean isShowToastOnTestPass = false;

    public MutitouchManualFragment(InterfaceButtonTextChange callback) {
        mCallBack = callback;
    }

    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        try {
            if (view == null) {
                view = inflater.inflate(R.layout.fragment_manual_mutitouch, null);
                manualDataStable = new ManualDataStable(mCallBack);
                utils = Utilities.getInstance(getActivity());
                ctx = getActivity();
                testResultUpdateToServer = new TestResultUpdateToServer(utils, ctx, getActivity());
                initViews();
            //    Crashlytics.getInstance().log(FragmentTag.MUTITOUCH_FRAGMENT.name());
            }
            return view;
        } catch (Exception e) {
            logException(e, "MultitouchManualFragment_initUI()");
            return null;
        }

    }

    public MutitouchManualFragment() {
    }

    /**
     * initialize view
     */
    private void initViews() {
        try {
            multiCount = (TextView) view.findViewById(R.id.multiCount);
            final Button btnNext = (Button) view.findViewById(R.id.btnNext);
            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    utils.showToast(ctx, "Clicked");
                }
            });
            mLayout = (RelativeLayout) view.findViewById(R.id.relativeLayout);
            multitouchView = new MultitouchView(ctx, null);
            isTestPerform = true;
            mLayout.addView(multitouchView);
            timer(getActivity(),false,ConstantTestIDs.MULTI_TOUCH_ID,MutitouchManualFragment.this);
        } catch (Exception e) {
            logException(e, "MultitouchManualFragment_initViews()");
        }

    }

    /**
     * Here change the header text
     ***/
    @Override
    public void onResume() {
        try {
            TitleBarFragment fragment = (TitleBarFragment) getFragment(R.id.headerContainer);
            if (fragment != null) {
//                fragment.setTitleBarVisibility(true);
//                fragment.setSyntextVisibilty(false);
//
//                fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string.MultiTouch), false, false, 0);
                // mCallBack.onChangeText(utils.BUTTON_SKIP, false);
                //nextButtonHandler = new Handler();
                if (Constants.isBackButton == true) {
                    if (isTestPerform) {
                        mCallBack.onChangeText(utils.BUTTON_NEXT, true);
                    } else {
                        mCallBack.onChangeText(utils.BUTTON_SKIP, true);
                    }
                    Constants.isBackButton = false;
                } else {
                    if (isTestPerformed) {
                        mCallBack.onChangeText(utils.BUTTON_NEXT, true);
//                        nextButtonHandler.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                nextButtonHandler.removeCallbacksAndMessages(null);
//                            }
//                        }, 1000);
                       onNextPress();

                    } else {
                        mCallBack.onChangeText(utils.BUTTON_SKIP, true);
                    }

                }
            }
            multiCount.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            logException(e, "MultitouchManualFragment_onResume()");
        }

        super.onResume();
    }

    /**
     * onPause
     */
    @Override
    public void onPause() {
        try {
           // nextButtonHandler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            logException(e, "MultitouchManualFragment_onPause()");
        }

        super.onPause();
    }

    @Override
    public void onStop() {

        mCallBack.onChangeText(utils.BUTTON_NEXT, false);

        super.onStop();
    }

    /**
     * Called when test is completed or user click on skip button
     */
    public void onNextPress() {
        try {
            timer(ctx,true,ConstantTestIDs.MULTI_TOUCH_ID,MutitouchManualFragment.this);
            MainActivity activity = (MainActivity) getActivity();
            //nextButtonHandler.removeCallbacksAndMessages(null);
//            activity.onChangeText(R.string.textSkip,false);
            if (Constants.isSkipButton) {
                isTestPerformed=true;
                utils.showToast(ctx, getResources().getString(R.string.txtManualFail));
               // utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_22.name(), AsyncConstant.TEST_FAILED);
               // updateResultToServer();
                isTestPerform = false;
            } else {
                isTestPerform = true;
            }
            boolean semi=false;
            nextPress(activity,semi);
//            MainActivity mainActivity = (MainActivity)getActivity();
//            if (Constants.isDoAllClicked && mainActivity.index != mainActivity.automatedTestListModels.size()) {
//                replaceFragment(R.id.container, ManualTestsOperation.launchScreens(mainActivity.automatedTestListModels.get(mainActivity.index).getTest_id(), mainActivity.automatedTestListModels.get(mainActivity.automatedTestListModels.size() - 1).getTest_id()), null, false);
//                mainActivity.index++;
//
//            }
//            if (Constants.isDoAllClicked && mainActivity.manualIndex != Manual2SemiAutomaticTestsFragment.testListModelList.size()){
//                replaceFragment(R.id.container, ManualTestsOperation.launchScreens(Manual2SemiAutomaticTestsFragment.testListModelList.get(mainActivity.manualIndex).getTest_id(), Manual2SemiAutomaticTestsFragment.testListModelList.get(Manual2SemiAutomaticTestsFragment.testListModelList.size() - 1).getTest_id()),null, false);
//                mainActivity.manualIndex++;
//
//            }
//            else {
////                clearAllStack();
////                replaceFragment(R.id.container, ManualTestsOperation.launchScreens(-1,mainActivity.automatedTestListModels.get(mainActivity.automatedTestListModels.size()-1).getTest_id()), FragmentTag.GPS_FRAGMENT.name(), true);
//                popFragment(R.id.container);
//            }

        } catch (Exception e) {
            logException(e, "MultitouchManualFragment_onNextPress()");
        }

    }

    /**
     * onBackPressed
     */
    public void onBackPress() {
        try {
            snackShow(mLayout);
        } catch (Exception e) {
            logException(e, "MultitouchManualFragment_onBackPress()");

        }

    }

    /**
     * show snack bar when user click on back button
     *
     * @param relativeLayout
     */


    /**
     * It is create to mock unit test on this class
     *
     * @param count
     * @return
     */
    public boolean testMultiTouch(int count) {
        if (count > 1) {
            return true;
        }
        return false;
    }

    /**
     * This method update data to backend API after performing each test
     * update Test Data
     */
//    public void updateResultToServer() {
//
//        try {
//            if (utils.getPreferenceBoolean(ctx, JsonTags.isTestDataChanged.name(), false)) {
//                testResultUpdateToServer.updateTestResult(null, true, 0);
//            }
//
//        } catch (Exception e) {
//            logException(e, "MultitouchManualFragment_updateResultToServer()");
//        }
//
//    }

    @Override
    public void update(Observable observable, Object o) {
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.onChangeText(R.string.textSkip, false);
    }

    /**
     * Multi touch view
     */
    public class MultitouchView extends View {

        private static final int SIZE = 60;
        private Context mcontext;

        private SparseArray<PointF> mActivePointers;
        private Paint mPaint;
        private int[] colors = {Color.BLUE, Color.GREEN, Color.MAGENTA,
                Color.BLACK, Color.CYAN, Color.GRAY, Color.RED, Color.DKGRAY,
                Color.LTGRAY, Color.YELLOW};

        private Paint textPaint;
        private boolean isDialogShow = true;
        private boolean isShowToast = false;
        private Utilities utils;
        Handler handler;

        public MultitouchView(Context context, AttributeSet attrs) {
            super(context, attrs);
            mcontext = context;
            utils = Utilities.getInstance(context);
            initView();
        }

        private void initView() {
            try {

                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.onChangeText(R.string.textSkip, true);
                //  utils.showToast(mcontext, getResources().getString(R.string.txtManualMititouchToast));
                mActivePointers = new SparseArray<PointF>();
                mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                // set painter color to a color you like
                mPaint.setColor(Color.BLUE);
                mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                textPaint.setTextSize(20);
                handler = new Handler();
            } catch (Exception e) {
                logException(e, "MultitouchManualFragment_initView()");
            }

        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            try {
                // get pointer index from the event object
                int pointerIndex = event.getActionIndex();

                // get pointer ID
                int pointerId = event.getPointerId(pointerIndex);

                // get masked (not specific to a pointer) action
                int maskedAction = event.getActionMasked();

                switch (maskedAction) {
                    case MotionEvent.ACTION_DOWN:
                        if (mActivePointers.size() < 3)
                            isShowToast = true;
                    case MotionEvent.ACTION_POINTER_DOWN: {
                        // We have a new pointer. Lets add it to the list of pointers

                        PointF f = new PointF();
                        f.x = event.getX(pointerIndex);
                        f.y = event.getY(pointerIndex);
                        mActivePointers.put(pointerId, f);
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: { // a pointer was moved
                        for (int size = event.getPointerCount(), i = 0; i < size; i++) {
                            PointF point = mActivePointers.get(event.getPointerId(i));
                            if (point != null) {
                                point.x = event.getX(i);
                                point.y = event.getY(i);
                            }
                        }
                        break;
                    }
                    case MotionEvent.ACTION_UP:

                    case MotionEvent.ACTION_POINTER_UP:
                        checkClick();
                    case MotionEvent.ACTION_CANCEL: {
                        mActivePointers.remove(pointerId);
                        break;
                    }
                }
                invalidate();
                return true;
            } catch (Exception e) {
                logException(e, "MultitouchManualFragment_onTouchEvent()");
                return false;
            }

        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            try {
                // draw all pointers
                for (int size = mActivePointers.size(), i = 0; i < size; i++) {
                    PointF point = mActivePointers.valueAt(i);
                    if (point != null)
                        mPaint.setColor(colors[i % 9]);
                    canvas.drawCircle(point.x, point.y, SIZE, mPaint);
                }
/*canvas.drawText("Total pointers: " + mActivePointers.size(), 10, 40, textPaint);*/
                if (isDialogShow) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            checkClick();
                        }
                    }, 2000);
                }
            } catch (Exception e) {
                logException(e, "MultitouchManualFragment_onDraw()");
            }

        }


        public void checkClick() {
            try {
                if (mActivePointers.size() > 1) {
                    if (!isTestPerformed) {
                        if (isDialogShow) {
                            isDialogShow = true;
                            isShowToast = false;
                            if (count < 2) {
                                count++;
                    /*showDialog(mcontext, mActivePointers.size(), mcontext.getResources().getString(R.string.textNext));*/
                      /*  utils.showToast(mcontext, getResources().getString(R.string.txtManualMititouchCongratsToast));*/
                            }
                            isTestPerformed = true;
                            mCallBack.onChangeText(utils.BUTTON_NEXT, true);
                            TestController testController = TestController.getInstance(getActivity());
                            testController.onServiceResponse(true, "Multitouch", ConstantTestIDs.MULTI_TOUCH_ID);
                          //  utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_22.name(), AsyncConstant.TEST_PASS);
                          //  updateResultToServer();
                            utils.showToast(ctx, getResources().getString(R.string.txtManualPass));
                            MainActivity mainActivity = (MainActivity) getActivity();
                            mainActivity.onChangeText(R.string.textSkip, false);
//                            nextButtonHandler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//
//                            /*Constants.isManualIndividual = false;*/
//                                    nextButtonHandler.removeCallbacksAndMessages(null);
//                                    onNextPress();
//                                }
//                            }, 2000);
                            onNextPress();

                        }
                    }
                } else {
                    if (isShowToast) {
                        if (!isTestPerformed) {
                          //  utils.showToast(mcontext, getResources().getString(R.string.txtManualMititouchRetryToast));
                            isShowToast = false;
                        }
                    }
                }
            } catch (Exception e) {
                logException(e, "MultitouchManualFragment_checkClick()");
            }

        }
    }
    @Override
    public void timerFail() {
        isTestPerform=false;
        isTestPerformed=true;
        utils.showToast(ctx, getResources().getString(R.string.txtManualFail));

        onNextPress();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        timer(getActivity(),true,ConstantTestIDs.MULTI_TOUCH_ID,MutitouchManualFragment.this);
    }

    public void logException(Exception e, String methodName) {
            try {
                Utilities utilities = Utilities.getInstance(getActivity());
                Activity activity = getActivity();
                Context context = getActivity();
                ExceptionLogUtils exceptionLogUtils = new ExceptionLogUtils(utilities, context, activity);
                exceptionLogUtils.addExceptionLog(utilities, context, activity, null, null, e, methodName);
            } catch (Exception exp) {
                // logException(exp, "MultitouchManualFragment_logException()");
            }

        }

}
