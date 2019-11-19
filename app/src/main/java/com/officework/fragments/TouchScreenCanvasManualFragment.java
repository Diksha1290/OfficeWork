package com.officework.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import androidx.core.content.ContextCompat;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.officework.R;
import com.officework.activities.MainActivity;
import com.officework.base.BaseFragment;
import com.officework.constants.Constants;
import com.officework.constants.JsonTags;
import com.officework.customViews.IconView;
import com.officework.interfaces.InterfaceButtonTextChange;
import com.officework.interfaces.TimerDialogInterface;
import com.officework.models.RectangleCanvasModel;
import com.officework.testing_profiles.Controller.TestController;
import com.officework.testing_profiles.utils.ConstantTestIDs;
import com.officework.utils.ExceptionLogUtils;
import com.officework.utils.ManualDataStable;
import com.officework.utils.TestResultUpdateToServer;
import com.officework.utils.Utilities;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.RequiresApi;

/**
 * Created by Girish on 8/24/2016.
 */
@SuppressLint("ValidFragment")
public class TouchScreenCanvasManualFragment extends BaseFragment implements TimerDialogInterface {
    View view;
    Utilities utils;
    Context ctx;
    CanvasView canvasView;
    TextView mtxtViewCompleteMsg;
    InterfaceButtonTextChange mCallBack;
    RelativeLayout mLayout,desRL;
    boolean isDialogEnable = true;
    ManualDataStable manualDataStable;
    boolean isPaused = false;
    // Handler nextButtonHandler = null;
    TextView countCanvasManual;
    CountDownTimer cTimer = null;
    boolean isTestPerformed = false;
    TestResultUpdateToServer testResultUpdateToServer;
    Button starttouchTV;
    boolean is_instruction=true;
    IconView touchImage;

    private MainActivity mainActivity;
    // isTouchEnabled is to enable disable touch when dialog is visible on screen
    boolean isTouchEnabled = true;
    TextView textView;

    public TouchScreenCanvasManualFragment(InterfaceButtonTextChange callback) {
        mCallBack = callback;
    }

    public TouchScreenCanvasManualFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        try {
            if (view == null) {
                view = inflater.inflate(R.layout.fragment_manual_touchscreencanvas, null);
                manualDataStable = new ManualDataStable(mCallBack);
                utils = Utilities.getInstance(getActivity());
                ctx = getActivity();
                testResultUpdateToServer = new TestResultUpdateToServer(utils, ctx, getActivity());
                //  Crashlytics.getInstance().log(FragmentTag.SCREEN_TOUCH_FRAGMENT.name());
                initViews();
            }
            return view;
        } catch (Exception e) {
            logException(e, "TouchScreenCanvasManualFragment_initUI()");
            return null;
        }

    }

    /**
     * Initialize view
     */
    private void initViews() {
        try {
            timer(getActivity(), false, ConstantTestIDs.TOUCH_SCREEN_ID, TouchScreenCanvasManualFragment.this);
            mLayout = (RelativeLayout) view.findViewById(R.id.layoutMain);
            touchImage=view.findViewById(R.id.imgd);
            countCanvasManual = (TextView) view.findViewById(R.id.countCanvasManual);
            starttouchTV=(Button)view.findViewById(R.id.starttouchTV);
            desRL=(RelativeLayout)view.findViewById(R.id.desRL);
            mainActivity = (MainActivity) getActivity();
            mainActivity.onChangeText(R.string.textSkip, true);
            mtxtViewCompleteMsg = (TextView) view.findViewById(R.id.txtViewCompleteMsg);
            //  utils.showToast(ctx, getResources().getString(R.string.txtManualScreenTouchHelp));
            canvasView = new CanvasView(ctx);
            starttouchTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mLayout.setBackgroundColor(getResources().getColor(R.color.dark_grey));
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            timer(getActivity(), true, ConstantTestIDs.TOUCH_SCREEN_ID,
                                    null);
                            canvasView.showDialog(true);
                            if (getActivity() != null) {
                                getActivity().findViewById(R.id.btnNext).setEnabled(false);
                            }
                            mainActivity.onChangeText(R.string.textSkip, false);
                            is_instruction=false;
                            TitleBarFragment titleBarFragment =
                                    (TitleBarFragment) getFragment(R.id.headerContainer);
                            titleBarFragment.showSwitchLayout(false);
                            hideShowTitleBar(false);
                            mLayout.addView(canvasView);
                        }
                    }, 100);

                    desRL.setVisibility(View.GONE);
                }
            });

        } catch (Exception e) {
            logException(e, "TouchScreenCanvasManualFragment_initViews()");
        }

    }

    /**
     * Here change the header text
     ***/
    @Override
    public void onResume() {
        try {
            // hideShowTitleBar(false);
            //  nextButtonHandler = new Handler();
            if (Constants.isBackButton == true) {
                if (manualDataStable.checkSingleHardware(JsonTags.MMR_23.name(), ctx, utils) == 1) {
                    mCallBack.onChangeText(utils.BUTTON_NEXT, true);
                    hideShowTitleBar(true);
                    Constants.isBackButton = false;
                } else {
                    hideShowTitleBar(false);
                    canvasView.invalidate();
                    Constants.isBackButton = false;
                    mCallBack.onChangeText(utils.BUTTON_SKIP, true);
                }

            } else {
                if (isPaused) {
                    mCallBack.onChangeText(utils.BUTTON_NEXT, true);
                    hideShowTitleBar(true);
                } else {
                    mCallBack.onChangeText(utils.BUTTON_SKIP, false);
                    hideShowTitleBar(false);
                    canvasView.invalidate();

                }
            }
            if(is_instruction){

                hideShowTitleBar(true);
            }
            if (isTestPerformed) {

                onNextPress();

            }
        } catch (Exception e) {
            logException(e, "TouchScreenCanvasManualFragment_onResume()");
        }

        super.onResume();
    }

    /**
     * onPause
     */
    @Override
    public void onPause() {
        super.onPause();
        try {
            //nextButtonHandler.removeCallbacksAndMessages(null);

            canvasView.handler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            logException(e, "TouchScreenCanvasManualFragment_onPause()");
        }

    }

    @SuppressLint("NewApi")
    public void hideShowTitleBar(boolean visible) {
        try {
            TitleBarFragment fragment = (TitleBarFragment) getFragment(R.id.headerContainer);
            if (fragment != null) {
                if (visible) {
                    fragment.setTitleBarVisibility(true);
                    fragment.setSyntextVisibilty(false);
                    fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string.txtManualTouchScreenDisplay), false, false, 0);
                    mCallBack.onChangeText(utils.BUTTON_NEXT, true);
                    exitFullScreen();

                } else {

                    FullScreen();
                    fragment.setTitleBarVisibility(false);
                    //  mCallBack.onChangeText(utils.BUTTON_SKIP, false);
                }
            }
        } catch (Exception e) {
            logException(e, "TouchScreenCanvasManualFragment_hideShowTitleBar()");
        }


    }


    public void exitFullScreen(){
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;
                getActivity().getWindow().setAttributes(lp);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        View decorView = getActivity().getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.STATUS_BAR_VISIBLE);
        decorView.invalidate();
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT_WATCH)
    private void FullScreen() {

        getActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        try {
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.P) {
                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
                getActivity().getWindow().setAttributes(lp);
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    @Override
    public void onStop() {
        mCallBack.onChangeText(utils.BUTTON_NEXT, false);
        exitFullScreen();
        super.onStop();
    }

    /**
     * Called when test is completed or user click on skip button
     */
    public void onNextPress() {
        try {
            exitFullScreen();
            MainActivity activity = (MainActivity) getActivity();
            TitleBarFragment titleBarFragment =
                    (TitleBarFragment) getFragment(R.id.headerContainer);
            if(!activity.isManualRetest) {
                titleBarFragment.showSwitchLayout(true);

            }
            if (Constants.isSkipButton) {
                starttouchTV.setEnabled(false);
                touchImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_touch_screen_svg),false,getActivity());

                starttouchTV.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque));
                utils.showToast(ctx, getResources().getString(R.string.txtManualFail));

            }
            canvasView.handler.removeCallbacksAndMessages(null);
            boolean semi=false;
            nextPress(activity,semi);

        } catch (Exception e) {
            logException(e, "TouchScreenCanvasManualFragment_onNextPress()");
        }
    }

    /**
     * onBackPressed
     */
    public void onBackPress() {
        try {
            snackShow(mLayout);

        } catch (Exception e) {
            logException(e, "TouchScreenCanvasManualFragment_onBackPress()");
        }

    }


    /**
     * This is used to mock unit test on this class
     *
     * @param event
     * @param arrayCount
     * @return
     */
    public boolean mockOnTouchEvent(int event, int arrayCount) {
        try {
            if (event == MotionEvent.ACTION_DOWN) {
                if (arrayCount > 60) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }


    }

    /**
     * Canvas Custom View To draw Rectangles on screen
     */
    public class CanvasView extends View {
        private  int statusBarHeight;
        public int width;
        public int height;
        private Path mPath;
        Context context;
        private Paint mPaint;
        private float mX, mY;
        private static final float TOLERANCE = 5;
        Rect rectangle;
        Display display;

        private List<RectangleCanvasModel> rectangles;
        private List<RectangleCanvasModel> touchedRectangles;
        boolean isRefresh = false;
        boolean isDrawingNeeded = false;
        Utilities utils;
        private RectangleCanvasModel currentRectangle;
        private boolean isShowToast = true;
        Handler handler = null;
        AlertDialog.Builder alertDialog;
        AlertDialog alert;


        public CanvasView(Context c) {
            super(c);
            try {
                context = c;
                handler = new Handler();
                alertDialog = new AlertDialog.Builder(getActivity());
                utils = Utilities.getInstance(context);
                // we set a new Path
                mPath = new Path();
                rectangle = new Rect();

                Window window = getActivity().getWindow();
                window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
                int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
                if (resourceId > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        statusBarHeight = getNavBarHeight(getActivity())+getStatusBarHeight();}
                    else{
                        statusBarHeight = getNavBarHeight(getActivity());

                    }
                }
                //statusBarHeight = 100;
                // and we set a new Paint with the desired attributes
                mPaint = new Paint();
                mPaint.setAntiAlias(true);
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(getResources().getColor(R.color.Black));
                mPaint.setStrokeWidth(1f);
                WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                display = wm.getDefaultDisplay();
                rectangles = new ArrayList<RectangleCanvasModel>();
                touchedRectangles = new ArrayList<RectangleCanvasModel>();
//                showDialog(true);



            } catch (Exception e) {
                logException(e, "TouchScreenCanvasManualFragment_CanvasView()");
            }

        }

        // override onSizeChanged
        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);

        }

        // override onDraw
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            try {
                if (isRefresh) {
                    drawFunction(canvas, isRefresh);
                    isRefresh = true;
                } else {
                    drawFunction(canvas, isRefresh);
                }
            } catch (Exception e) {
                logException(e, "TouchScreenCanvasManualFragment_onDraw()");
            }

        }

        private void AddTouchedRectangle(RectangleCanvasModel rectangle) {
            try {
                if (touchedRectangles.size() == 0) {
                    touchedRectangles.add(rectangle);
                } else {
                    boolean found = false;
                    for (int i = 0; i < touchedRectangles.size(); i++) {
                        RectangleCanvasModel item = touchedRectangles.get(i);
                        if (item.isInsideBounds(rectangle.getCoordX(), rectangle.getCoordY())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        touchedRectangles.add(rectangle);
                        textView = new TextView(getActivity());
                        textView.setX(rectangle.getCoordX());
                        textView.setY(rectangle.getCoordY());
                        textView.setShadowLayer(1.5f, -1, 1, Color.LTGRAY);
                        textView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.app_blue_color));
                        textView.setHeight(rectangle.getDimensionHeight());
                        textView.setWidth(rectangle.getDimensionWidth());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            textView.setElevation(100);
                        }
                        mLayout.addView(textView);
                        setAnimationOnTextView(textView);
                    }
                }
            } catch (Exception e) {
                logException(e, "TouchScreenCanvasManualFragment_AddTouchedRectangle()");
            }

        }

        private void drawFunction(Canvas canvas, boolean isRefresh) {
            try {
                if (!isRefresh) {
                    int x = 0;
                    int yIncrement = ((display.getHeight()+statusBarHeight) -10) / 10;
                    int y = (display.getHeight() +statusBarHeight)/ 10;
                    for (int i = 0; i < 9; i++) {
                        canvas.drawLine(x, y, display.getWidth(), y, mPaint);
                        y = y + yIncrement;
                    }
                    int yWidth = 0;
                    int xWidth = (display.getWidth() - 6) / 6;
                    int xIncrement = display.getWidth() / 6;
                    for (int j = 0; j < 6; j++) {
                        canvas.drawLine(xWidth, yWidth, xWidth, (display.getHeight()+statusBarHeight), mPaint);
                        xWidth = xWidth + xIncrement;
                    }
                    rectangles.clear();
                    int xRect = 0;
                    for (int cols = 0; cols < 6; cols++) {
                        int yRect = 0;
                        for (int rows = 0; rows < 10; rows++) {
                            rectangles.add(new RectangleCanvasModel(xRect, yRect, xIncrement, yIncrement));
                            yRect += yIncrement + 1;
                        }
                        xRect += xIncrement + 1;
                    }

                }
                if (isDrawingNeeded) {
                    Paint paint = new Paint();
                    // border
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setColor(Color.WHITE);
                    paint.setStrokeWidth(1f);
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(Color.WHITE);

                    for (int i = 0; i < touchedRectangles.size(); i++) {
                        RectangleCanvasModel r = touchedRectangles.get(i);
                        canvas.drawRect(r.getCoordX(), r.getCoordY(), (r.getDimensionWidth() + r.getCoordX()) + 1, (r.getDimensionHeight() + r.getCoordY()) + 1, paint);
                    }
                }
                if (touchedRectangles.size() >= 60) {
                    if (isShowToast) {
                        isShowToast = false;
                        isTestPerformed = true;
                        handler.removeCallbacksAndMessages(null);
                        isPaused = true;
                        mCallBack.onChangeText(utils.BUTTON_NEXT, true);
                        utils.showToast(context, getResources().getString(R.string.txtManualPass));
                        // utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_24.name(), AsyncConstant.TEST_PASS);
                        TestController testController = TestController.getInstance(getActivity());
                        testController.onServiceResponse(true,"TouchScreen", ConstantTestIDs.TOUCH_SCREEN_ID);
                        // updateResultToServer();
                        hideShowTitleBar(true);

                        onNextPress();
                    }
                }
            } catch (Exception e) {
                logException(e, "TouchScreenCanvasManualFragment_AddTouchedRectangle()");
            }

        }


        // when ACTION_DOWN start touch according to the x,y values
        private void startTouch(float x, float y) {
            try {
                mPath.moveTo(x, y);
                mX = x;
                mY = y;
            } catch (Exception e) {
                logException(e, "TouchScreenCanvasManualFragment_startTouch()");
            }

        }

        // when ACTION_MOVE move touch according to the x,y values
        private void moveTouch(float x, float y) {
            try {
                float dx = Math.abs(x - mX);
                float dy = Math.abs(y - mY);
                if (dx >= TOLERANCE || dy >= TOLERANCE) {
                    mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                    mX = x;
                    mY = y;
                }
            } catch (Exception e) {
                logException(e, "TouchScreenCanvasManualFragment_moveTouch()");
            }

        }

        public void clearCanvas() {
            try {
                mPath.reset();
                invalidate();
            } catch (Exception e) {
                logException(e, "TouchScreenCanvasManualFragment_clearCanvas()");
            }

        }

        // when ACTION_UP stop touch
        private void upTouch() {
            try {
                mPath.lineTo(mX, mY);
            } catch (Exception e) {
                logException(e, "TouchScreenCanvasManualFragment_upTouch()");
            }

        }

        protected RectangleCanvasModel GetRectangle(int x, int y) {
            try {
                RectangleCanvasModel returnValue = null;
                for (Iterator<RectangleCanvasModel> sit = rectangles.iterator(); sit.hasNext(); ) {
                    RectangleCanvasModel current = sit.next();
                    if (current.isInsideBounds(x, y)) {
                        returnValue = current;
                        break;
                    }
                }
                return returnValue;
            } catch (Exception e) {
                logException(e, "TouchScreenCanvasManualFragment_GetRectangle()");
                return null;
            }

        }

        //override the onTouchEvent
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            try {
                if (isTouchEnabled) {
                    int x = (int) event.getX();
                    int y = (int) event.getY();
                    //dtermine if the button is pressed
                    //check if the current rectangle is set
                    //
                    if (event.getButtonState() == MotionEvent.ACTION_DOWN) {
                        if (currentRectangle == null) {
                            currentRectangle = GetRectangle(x, y);
                            AddTouchedRectangle(currentRectangle);
                            isDrawingNeeded = true;
                        } else {
                            RectangleCanvasModel reported = GetRectangle(x, y);
                            if (reported != null) {
                                if (reported.getCoordX() != currentRectangle.getCoordX() || reported.getCoordY() != currentRectangle.getCoordY()) {
                                    //we are in new rectangle-do the stuff here - e.g paint background
                                    /*Toast.makeText(context, "Rectangle Changed: [X - " + reported.coordX + ", Y - " + reported.coordY + "]", Toast.LENGTH_SHORT).show();*/

                                    currentRectangle = reported;
                                    AddTouchedRectangle(reported);
                                    isDrawingNeeded = true;
                                }
                            }
                        }
                    } else {
                        currentRectangle = null;
                    }
                    invalidate();
                }
                return true;
            } catch (Exception e) {
                logException(e, "TouchScreenCanvasManualFragment_onTouchEvent()");
                return false;
            }

        }

        public void showDialog(boolean isDialogEnabled) {
            try {
                if (isDialogEnabled) {
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isTouchEnabled = false;
                            alertDialog.setTitle(getResources().getString(R.string.txtManualDisplayAlert));
                            alertDialog.setMessage(getResources().getString(R.string.txtManualTouchScreen));
                            alertDialog.setPositiveButton(getResources().getString(R.string.txtManualDisplayNo), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {







                                    /*Hiding Title Bar*/
// utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_24.name(), AsyncConstant.TEST_FAILED);
                                    TestController testController = TestController.getInstance(getActivity());
                                    testController.onServiceResponse(false,"TouchScreen", ConstantTestIDs.TOUCH_SCREEN_ID);
// updateResultToServer();
                                    utils.showToast(ctx, getResources().getString(R.string.txtManualFail));
                                    isTestPerformed=true;



                                    mCallBack.onChangeText(utils.BUTTON_SKIP, true);
                                    handler.removeCallbacksAndMessages(null);
                                    dialog.cancel();
                                    hideShowTitleBar(true);
// nextButtonHandler.postDelayed(new Runnable() {
// @Override
// public void run() {
// nextButtonHandler.removeCallbacksAndMessages(null);
// onNextPress();
// }
// }, 2000);
                                    touchImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_touch_screen_svg),false,getActivity());


                                    onNextPress();
                                }
                            });


                            alertDialog.setNegativeButton(getResources().getString(R.string.txtManualDisplayYes), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    isTouchEnabled = true;
                                    handler.removeCallbacksAndMessages(null);
                                    if(!is_instruction) {
                                        hideShowTitleBar(false);
                                    }
                                    canvasView.invalidate();

                                    showDialog(true);
                                }
                            });
                            alert = alertDialog.create();

// Showing Alert Message
                            alert.show();
                            alert.getButton(AlertDialog.BUTTON_POSITIVE).setAllCaps(false);
                            alert.getButton(AlertDialog.BUTTON_NEGATIVE).setAllCaps(false);
                            alert.setCancelable(false);

                        }
                    }, 30000);

                }
            } catch (Exception e) {
                logException(e, "TouchScreenCanvasManualFragment_showDialog()");
            }

        }
    }

    public void logException(Exception e, String methodName) {
        try {
            Utilities utilities = Utilities.getInstance(getActivity());
            Activity activity = getActivity();
            Context context = getActivity();
            ExceptionLogUtils exceptionLogUtils = new ExceptionLogUtils(utilities, context, activity);
            exceptionLogUtils.addExceptionLog(utilities, context, activity, null, null, e, methodName);
        } catch (Exception exp) {
            // logException(exp, "TouchScreenCanvasManualFragment_logException()");
        }

    }
    public int getStatusBarHeight(){

        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }
    public static int getNavBarHeight(Context context) {
        Point appUsableSize = getAppUsableScreenSize(context);
        Point realScreenSize = getRealScreenSize(context);

// navigation bar on the side
        if (appUsableSize.x < realScreenSize.x) {
            return appUsableSize.y;
// new Point(realScreenSize.x - appUsableSize.x, appUsableSize.y);
        }

// navigation bar at the bottom
        if (appUsableSize.y < realScreenSize.y) {
            return realScreenSize.y - appUsableSize.y;
// new Point(appUsableSize.x, realScreenSize.y - appUsableSize.y);
        }

// navigation bar is not present
        return 0;
//new Point();
    }
    public static Point getAppUsableScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }
    public static Point getRealScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();

        if (Build.VERSION.SDK_INT >= 17) {
            display.getRealSize(size);
        } else if (Build.VERSION.SDK_INT >= 14) {
            try {
                size.x = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
                size.y = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
            } catch (IllegalAccessException e) {} catch (InvocationTargetException e) {} catch (NoSuchMethodException e) {}
        }

        return size;
    }
    private boolean isTablet(Context c) {
        return (c.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
    private void setAnimationOnTextView(final TextView textView) {
        final AlphaAnimation fadeIn = new AlphaAnimation(0.0f , 1.0f ) ;
        final AlphaAnimation fadeOut = new AlphaAnimation( 1.0f , 0.0f ) ;
        textView.startAnimation(fadeIn);
        textView.startAnimation(fadeOut);
        fadeIn.setDuration(400);
        fadeIn.setFillAfter(true);
        fadeOut.setDuration(400);
        fadeOut.setFillAfter(true);
        fadeOut.setStartOffset(400+fadeIn.getStartOffset());
    }




    @Override
    public void timerFail() {
        starttouchTV.setEnabled(false);
        starttouchTV.setBackgroundColor(ctx.getResources().getColor(R.color.disabledGray_opaque));
// updateResultToServer();
        touchImage.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_touch_screen_svg),false,getActivity());
        utils.showToast(ctx, ctx.getResources().getString(R.string.txtManualFail));
        onNextPress();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        timer(ctx,true,ConstantTestIDs.TOUCH_SCREEN_ID,TouchScreenCanvasManualFragment.this);
    }
}

