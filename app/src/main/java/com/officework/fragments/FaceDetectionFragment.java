package com.officework.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;

import androidx.core.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.officework.R;
import com.officework.activities.MainActivity;
import com.officework.base.BaseFragment;
import com.officework.constants.AsyncConstant;
import com.officework.constants.Constants;
import com.officework.customViews.CameraOverlay;
import com.officework.customViews.CameraSurfacePreview;
import com.officework.interfaces.FaceDetectionInterface;
import com.officework.interfaces.InterfaceButtonTextChange;
import com.officework.models.AutomatedTestListModel;
import com.officework.testing_profiles.Controller.TestController;
import com.officework.testing_profiles.utils.ConstantTestIDs;
import com.officework.utils.ExceptionLogUtils;
import com.officework.utils.TestResultUpdateToServer;
import com.officework.utils.Utilities;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;


import com.crashlytics.android.Crashlytics;
import com.officework.constants.FragmentTag;


@SuppressLint("ValidFragment")
public class FaceDetectionFragment extends BaseFragment implements FaceDetectionInterface, Observer {
    private static final int PERMISSION_REQUEST_CODE = 200;
    View view;
    Utilities utils;
    Context ctx;
    TestResultUpdateToServer testResultUpdateToServer;
    InterfaceButtonTextChange mCallBack;
    private static final String TAG = "FaceTracker";
    private CameraSource mCameraSource = null;
    private CameraSurfacePreview mPreview;
    private CameraOverlay cameraOverlay;
    private static final int RC_HANDLE_GMS = 9001;
    RelativeLayout fcclayout;
    private FaceDetector detector;
    boolean isTestPerformed = false;
    boolean shouldChange = false;
    Handler nextButtonHandler = null;
    boolean backCamera=false;
    boolean isfrontCameraPassed =false;
    Handler backCameraHandler;
    Handler finishHandler;
    Thread autoBackThread;
    boolean autBackFinished=false;
    boolean callBackFinished=false;
    Thread callBackThread;
    boolean firstTimeStart=true;
    Runnable backCameraRunnable;
    Handler delayHandler;
    Runnable delayRunnable;
    Handler autoBackExit;
    Runnable autoExitReunnable;
    TestController testController;
    MainActivity mainActivity;
    boolean isfronttoastPrinted=false;
    boolean isBackToastPrinted=false;
    private boolean already=true;

    public FaceDetectionFragment(InterfaceButtonTextChange callback) {
        mCallBack = callback;
    }
    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        try {
            if (view == null) {
                view = inflater.inflate(R.layout.fragment_manual_face_detection, null);
                utils = Utilities.getInstance(getActivity());
                ctx = getActivity();
                Crashlytics.getInstance().log(FragmentTag.FACEDETECTION_TEST_FRAGMENT.name());
                initViews();
                testController = TestController.getInstance(getActivity());
                testController.addObserver(this);
                testController.performOperation(ConstantTestIDs.FaceDetection);
                initViews();
            }
            return view;
        } catch (Exception e) {
            logException(e, "DisplayManualFragment_initUI()");
            return null;
        }
    }

    public FaceDetectionFragment() {
    }

    private void initViews() {
        mainActivity = (MainActivity)getActivity();
        mainActivity.onChangeText(R.string.textSkip,true);
        testResultUpdateToServer = new TestResultUpdateToServer(utils, ctx, getActivity());
        mPreview = (CameraSurfacePreview)view.findViewById(R.id.preview);
        cameraOverlay = (CameraOverlay)view.findViewById(R.id.faceOverlay);
        fcclayout=(RelativeLayout)view.findViewById(R.id.face_detection_Layout);
        CheckingPermission();
        nextButtonHandler = new Handler();
    }
    private void CheckingPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
                Toast.makeText(getActivity(), "Camera permission is required", Toast.LENGTH_SHORT).show();
            }
            else {
                startCamera();
            }

        }else {
            startCamera();
        }

    }
    private void createCameraSource() {
        Context context = getActivity();
        detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();
        detector.setProcessor(
                new MultiProcessor.Builder<>(new FaceDetectionFragment.GraphicFaceTrackerFactory())
                        .build());
        if (!detector.isOperational()) {
            Log.e(TAG, "Face detector dependencies are not yet available.");
            Toast.makeText(getActivity(), "Your Test Is Failed", Toast.LENGTH_SHORT).show();

        }


       /* DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;*/
        mCameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(30.0f)
                .build();
    }

    @SuppressLint("WrongConstant")
    public void startCamera(){
        createCameraSource();
        startCameraSource();
        try {
            if (getActivity() != null) {
                if(!isfronttoastPrinted){
                    utils.showToast(ctx,getString(R.string.faceDetectionFront));

                    //  Toast.makeText(getActivity(), "please aim face to front camera", Toast.LENGTH_SHORT).show();
                    isfronttoastPrinted=true;
                }

            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        backCameraHandler= new Handler();
        backCameraHandler.postDelayed(backCameraRunnable=new Runnable() {
            @Override
            public void run() {

                try{
                    if(getActivity()!= null) {
                        if(!isBackToastPrinted){
                            utils.showToast(ctx,getString(R.string.faceDetectionBack));
                            isBackToastPrinted=true;}
                    }}catch (Exception e)
                {e.printStackTrace();}
                autoBackCamer();
            }
        },10000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(grantResults[0]!=PackageManager.PERMISSION_GRANTED) {
                        if (shouldShowRequestPermissionRationale(permissions[0])) {
                            showmessage(getResources().getString(R.string.txtPermissionMessage2),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                                                requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CODE);
                                                replaceFragment(R.id.container, new ManualTestFragment((InterfaceButtonTextChange) getActivity()), FragmentTag.MANUAL_TEST_FRAGMENT.name(), false);
                                            }
                                        }
                                    }, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            TestController testController = TestController.getInstance(getActivity());
                                            testController.saveSkipResponse(-1, ConstantTestIDs.FaceDetection);
                                            utils.showToast(ctx, getResources().getString(R.string.txtManualFail));
                                            onNextPress();
                                        }
                                    });
                        }
                        else {
                            showmessage(getResources().getString(R.string.txtPermissionMessage2), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
//
                                    replaceFragment(R.id.container, new ManualTestFragment((InterfaceButtonTextChange) getActivity()), FragmentTag.MANUAL_TEST_FRAGMENT.name(), false);
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    TestController testController = TestController.getInstance(getActivity());
                                    testController.saveSkipResponse(-1, ConstantTestIDs.FaceDetection);
                                    utils.showToast(ctx, getResources().getString(R.string.txtManualFail));
                                    onNextPress();
                                }
                            });
                        }
                    }
                    else
                    {
                        startCamera();
                    }



                }
                break;
        }
    }
    private void showmessage(String message, DialogInterface.OnClickListener onClickListener,DialogInterface.OnClickListener onClickListener2) {
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.txtAlertTitleGreatAlert)
                .setMessage(message)
                . setCancelable(false)
                .setPositiveButton("OK",onClickListener)
                .setNegativeButton("Skip",onClickListener2)
                .create().
                show();
    }


    public void autoBackCamer(){
        if(!autBackFinished) {
            firstTimeStart=false;
            autoBackThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Log.d("checkdetails", String.valueOf(mCameraSource.getCameraFacing()));
                        try {
                            if (mCameraSource != null) {
                                mCameraSource.release();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Context context = getActivity();
                        FaceDetector detector = new FaceDetector.Builder(context)
                                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                                .build();
                        detector.setProcessor(
                                new MultiProcessor.Builder<>(new FaceDetectionFragment.GraphicFaceTrackerFactory())
                                        .build());
                        if (mCameraSource.getCameraFacing() == 1) {


                            mCameraSource = new CameraSource.Builder(context, detector)
                                    .setRequestedPreviewSize(640, 480)
                                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                                    .setRequestedFps(30.0f)
                                    .build();
                            startCameraSource();
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }}
            });
            autoBackThread.start();
//autoBackThread.stop();

            autoBackExit= new Handler();
            autoBackExit.postDelayed(autoExitReunnable=new Runnable() {
                @Override
                public void run() {
                    try {

                        if(mCallBack!= null) {
                            mCallBack.onChangeText(utils.BUTTON_NEXT, true);
                        }                        utils.showToast(ctx, getResources().getString(R.string.txtManualFail));
                        // utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_64.name(), AsyncConstant.TEST_FAILED);
                        testController.onServiceResponse(false, "FaceDetection", ConstantTestIDs.FaceDetection);

                        onNextPress();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            },10000);

        }

    }


    @Override
    public void callback() {
        try{
// if(!shouldChange) {
// shouldChange = true;
            if (mCameraSource.getCameraFacing() == 1) {
                firstTimeStart=false;
                backCameraHandler.removeCallbacks(backCameraRunnable);
                callBackThread = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            Log.d("checkdetails", String.valueOf(mCameraSource.getCameraFacing()));
                            mCameraSource.release();
                            Context context = getActivity();
                            FaceDetector detector = new FaceDetector.Builder(context)
                                    .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                                    .build();
                            detector.setProcessor(
                                    new MultiProcessor.Builder<>(new FaceDetectionFragment.GraphicFaceTrackerFactory())
                                            .build());
                            if (mCameraSource.getCameraFacing() == 1) {
                                autBackFinished = true;
                                isfrontCameraPassed = true;

                                DisplayMetrics displayMetrics = new DisplayMetrics();
                                getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                                int width = displayMetrics.widthPixels;
                                int height = displayMetrics.heightPixels;
                                Log.d(TAG,"Metrics Preview width and height="+width+" "+height);
                                mCameraSource = new CameraSource.Builder(context, detector)
                                        //.setRequestedPreviewSize(1920, 1080)
                                        .setRequestedPreviewSize(640, 480)
                                        //  .setRequestedPreviewSize(width, height)
                                        .setFacing(CameraSource.CAMERA_FACING_BACK)
                                        .setRequestedFps(30.0f)
                                        .build();
                                startCameraSource();
                                callBackFinished = true;
                                Handler T=new Handler(Looper.getMainLooper());
                                T.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try{
                                            if(getActivity()!=null) {
                                                if(!isBackToastPrinted){
                                                    //  Toast.makeText(getActivity(), "Please aim face to back camera", Toast.LENGTH_SHORT).show();
                                                    utils.showToast(ctx,getString(R.string.faceDetectionBack));

                                                    isBackToastPrinted=true;}
                                            }}catch (Exception e)
                                        {
                                            e.printStackTrace();
                                        }
                                    }
                                });


                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
                callBackThread.start();


                delayHandler= new Handler(Looper.getMainLooper());
                delayHandler.postDelayed(delayRunnable=new Runnable() {
                    @Override
                    public void run() {
                        finishcallBack(false);
                    }
                },10000);

            }
// }else {
            else if(mCameraSource.getCameraFacing() == CameraSource.CAMERA_FACING_BACK){
                if(backCameraHandler!=null)
                {backCameraHandler.removeCallbacks(backCameraRunnable);}
                if(delayHandler!=null){
                    delayHandler.removeCallbacks(delayRunnable);}
                if(autoBackExit!=null){
                    autoBackExit.removeCallbacks(autoExitReunnable);
                }
                if(isfrontCameraPassed){
                    if(already) {
                        already=false;
                        finishcallBack(true);
                    }}
                else{finishcallBack(false);}
            }
// }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void finishcallBack(final Boolean value) {
        try{
            final Handler handler=new Handler(Looper.getMainLooper());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(mCallBack!= null) {
                                mCallBack.onChangeText(utils.BUTTON_NEXT, true);
                            }
                            if(value){
                                try {
                                    utils.showToast(ctx, getResources().getString(R.string.txtManualPass));

                                }
                                catch (Exception e){
                                    e.printStackTrace();
                                }
                                // utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_64.name(), AsyncConstant.TEST_PASS);
                                testController.onServiceResponse(true, "FaceDetection", ConstantTestIDs.FaceDetection);}
                            else{
                                try {
                                    utils.showToast(getActivity(), getResources().getString(R.string.txtManualFail));
                                }
                                catch (Exception e){
                                    e.printStackTrace();
                                }
                                // utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_64.name(), AsyncConstant.TEST_FAILED);
                                testController.onServiceResponse(false, "FaceDetection", ConstantTestIDs.FaceDetection);
                            }

                            onNextPress();
                        }
                    });
                }
            }).start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        try {
            nextButtonHandler.removeCallbacksAndMessages(null);
/**
 * stop perview
 */
            mPreview.stop();
        } catch (Exception e) {
            logException(e, "FaceDetectionManualFragment_onPause()");
        }

    }
    @Override
    public void onResume() {
        try {
            TitleBarFragment fragment = (TitleBarFragment) getFragment(R.id.headerContainer);
//            if (fragment != null) {
//                fragment.setTitleBarVisibility(true);
//                fragment.setSyntextVisibilty(false);
//                fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string.facedetection), false, false, 0);
//                // nextButtonHandler = new Handler();
//            }



//            TitleBarFragment fragment = (TitleBarFragment) getFragment(R.id.headerContainer);
//            if (fragment != null) {
//                fragment.setTitleBarVisibility(true);
//                fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string.facedetection), false, false, 0);
//            }
            nextButtonHandler = new Handler();
            if (isTestPerformed) {
                if(mCallBack!= null) {
                    mCallBack.onChangeText(utils.BUTTON_NEXT, true);
                }
                nextButtonHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        nextButtonHandler.removeCallbacksAndMessages(null);
                       onNextPress();
                    }
                }, 1000);

            } else {
                if(mCallBack!= null) {
                    mCallBack.onChangeText(utils.BUTTON_SKIP, true);
                }            }


            startCameraSource();
        } catch (Exception e) {
            logException(e, "PhoneShakingManualFragment_onResume()");
        }
        super.onResume();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (mCameraSource != null) {
                mCameraSource.release();
            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public void onStop() {
        if(mCallBack!=null) {
            mCallBack.onChangeText(utils.BUTTON_NEXT, false);
        }
        super.onStop();
    }

    /**
     * onBackPressed
     */
    public void onBackPress() {
        try {
            snackShow(fcclayout);
        } catch (Exception e) {
            logException(e, "FaceDetectionManualFragment_onBackPress()");
        }
    }
    /**
     * Called when test is completed or user click on skip button
     */


    @Override
    public void update(Observable observable, Object o) {
        try {
            AutomatedTestListModel automatedTestListModel = (AutomatedTestListModel)o;
            if (automatedTestListModel.getIsTestSuccess() == AsyncConstant.TEST_PASS) {
                mainActivity.onChangeText(R.string.textSkip,false);
                //  isTestPerformed = true;

       //         utils.showToast(ctx, getResources().getString(R.string.txtManualPass));

                //  onNextPress();

            }
            else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void onNextPress() {
        try {
            removeAllThreads();
            MainActivity activity = (MainActivity) getActivity();
//            activity.onChangeText(R.string.textSkip,false);
            if (Constants.isSkipButton) {
                isTestPerformed=true;
                utils.showToast(ctx, "Test Failed");
            }
            boolean semi=false;
            nextPress(activity,semi);
//   popFragment(R.id.container);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



/**
 * show snack bar when user click on back button
 *
 * @param relativeLayout
 */


    /**
     * This method update data to backend API after performing each test
     * update Test Data
     */



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data==null)
        {
            CheckingPermission();
        }
    }



    private void startCameraSource() {
        try {
            int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                    getActivity());
            if (code != ConnectionResult.SUCCESS) {
                Dialog dlg =
                        GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), code, RC_HANDLE_GMS);
                dlg.show();
            }

            if (mCameraSource != null) {
                try {
                    mPreview.start(mCameraSource, cameraOverlay);
                } catch (IOException e) {
                    Log.e(TAG, "Unable to start camera source.", e);
                    mCameraSource.release();
                    mCameraSource = null;
                }
            }

            if(firstTimeStart){


            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {

        if(backCameraHandler!=null)
        {backCameraHandler.removeCallbacks(backCameraRunnable);}
        if(delayHandler!=null){
            delayHandler.removeCallbacks(delayRunnable);}
        if(autoBackExit!=null){
            autoBackExit.removeCallbacks(autoExitReunnable);
        }
        try {
            testController.deleteObserver(this);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        super.onDestroyView();
        try {
            testController.deleteObserver(this);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new FaceDetectionFragment.GraphicFaceTracker(cameraOverlay);
        }
    }

    private class GraphicFaceTracker extends Tracker<Face> {
        private CameraOverlay mOverlay;
        private FaceOverlayGraphics faceOverlayGraphics;


        GraphicFaceTracker(CameraOverlay overlay) {
            mOverlay = overlay;
            faceOverlayGraphics = new FaceOverlayGraphics(overlay,FaceDetectionFragment.this);
        }

        @Override
        public void onNewItem(int faceId, Face item) {
            faceOverlayGraphics.setId(faceId);



        }
        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
            mOverlay.add(faceOverlayGraphics);
            faceOverlayGraphics.updateFace(face);
        }

        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            mOverlay.remove(faceOverlayGraphics);
        }
        @Override
        public void onDone() {
            mOverlay.remove(faceOverlayGraphics);
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
        }

    }

    public void removeAllThreads(){
        try {
            if (backCameraHandler != null) {
                backCameraHandler.removeCallbacks(backCameraRunnable);
            }

            if (delayHandler != null) {
                delayHandler.removeCallbacks(delayRunnable);
            }
            if (autoBackExit != null) {
                autoBackExit.removeCallbacks(autoExitReunnable);
            }

            if (nextButtonHandler != null) {
                nextButtonHandler.removeCallbacksAndMessages(null);
            }
            if (autoBackThread != null && autoBackThread.isAlive()) {
                autoBackThread.stop();
            }
            if (callBackThread != null && callBackThread.isAlive()) {
                callBackThread.stop();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
}