package com.officework.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.officework.R;
import com.officework.activities.MainActivity;
import com.officework.base.BaseFragment;
import com.officework.camera.CameraPreview;
import com.officework.constants.Constants;
import com.officework.constants.FragmentTag;
import com.officework.constants.JsonTags;
import com.officework.customViews.IconView;
import com.officework.interfaces.InterfaceAlertDissmiss;
import com.officework.interfaces.InterfaceButtonTextChange;
import com.officework.interfaces.TimerDialogInterface;
import com.officework.testing_profiles.Controller.TestController;
import com.officework.testing_profiles.realmDatabase.operationManager.RealmOperations;
import com.officework.testing_profiles.utils.ConstantTestIDs;
import com.officework.utils.ExceptionLogUtils;
import com.officework.utils.ManualDataStable;
import com.officework.utils.TestResultUpdateToServer;
import com.officework.utils.Utilities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by Girish on 8/16/2016.
 */
@SuppressLint("ValidFragment")
public class CameraFragment extends BaseFragment implements View.OnClickListener,
        InterfaceAlertDissmiss, TimerDialogInterface {
    private static final int FOCUS_AREA_SIZE = 300;
    final public int REQUEST_CODE_ASK_PERMISSIONS_CAMERA = 123;
    final public int REQUEST_CODE_ASK_PERMISSIONS_STORAGE = 1234;
    View view;
    Utilities utils;
    Context ctx;
    int previousCmaeraStatus;
    int previousfrontcamerastatus;
    int previousrearcamerastatus;
    TextView mtxtViewCount, camera_desTV;
    ImageView mImgViewFrontCam, mImgViewBackCam;
    File pictureFileFront, pictureFileBack;
    Button btnNext, camera_yesTV;
    InterfaceButtonTextChange mCallBack;
    ImageView imgClickPic;
    ImageView imgClose;
    byte[] data;
    ManualDataStable manualDataStable;
    Handler handler = null;
    CountDownTimer cTimer = null;
    Handler nextButtonHandler = null;
    TextView countTimer;
    boolean isCameraFront = false;
    FrameLayout imgPreview;
    TextView imgRetake, imgOk;
    ImageView imgViewPreview;
    boolean isTestPerformed = false;
    ProgressDialog bar;
    Handler handlerProgress = null;
    boolean isFrontClose, isBackClose;
    TestResultUpdateToServer testResultUpdateToServer;
    RelativeLayout relativeLayoutCamera, camera_desRL;
    private boolean imgOkbollean = false;
    private Camera mCamera;
    private CameraPreview mPreview;
    private Camera.PictureCallback mPicture;
    /*private LinearLayout cameraPreview;*/
    private FrameLayout cameraPreview;
    private boolean cameraFront = true;
    private RelativeLayout layoutShowImage;
    private TestController testController;
    private boolean isCAmeraClosed = false;
    IconView cameraImage;
    private Camera.AutoFocusCallback mAutoFocusTakePictureCallback =
            new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    try {
                        if (success) {
                            // do something...
                            Log.i("tap_to_focus", "success!");
                        } else {
                            // do something...
                            Log.i("tap_to_focus", "fail!");
                        }
                    } catch (Exception e) {
                        logException(e, "CameraFragment_mAutoFocusTakePictureCallback");
                    }

                }
            };

    public CameraFragment(InterfaceButtonTextChange callback) {
        mCallBack = callback;
    }

    public CameraFragment() {

    }

    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        try {
            if (view == null) {
                view = inflater.inflate(R.layout.fragment_manual_camera, null);
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.onChangeText(R.string.textSkip, true);
                utils = Utilities.getInstance(getActivity());
                RealmOperations realmOperations = new RealmOperations();
                previousCmaeraStatus = realmOperations.fetchtestStatus(ConstantTestIDs.CAMERA_ID);
                previousfrontcamerastatus =
                        realmOperations.fetchtestStatus(ConstantTestIDs.FRONT_CAMERA);
                previousrearcamerastatus =
                        realmOperations.fetchtestStatus(ConstantTestIDs.BACK_CAMERA);
                manualDataStable = new ManualDataStable(mCallBack);
                ctx = getActivity();
                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                Crashlytics.log(FragmentTag.CAMERA_FRAGMENT.name());
                testResultUpdateToServer = new TestResultUpdateToServer(utils, ctx, getActivity());
                initViews();

                testController = TestController.getInstance(getActivity());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    testController.performOperation(ConstantTestIDs.CAMERA_ID);
                }
            }
            return view;
        } catch (Exception e) {
            logException(e, "CameraFragment_initUI()");
            return null;
        }

    }

    private void initViews() {
        try {
            timer(getActivity(), false, ConstantTestIDs.CAMERA_ID, CameraFragment.this);
            relativeLayoutCamera = (RelativeLayout) view.findViewById(R.id.relativeLayoutCamera);
            cameraPreview = (FrameLayout) view.findViewById(R.id.rlCameraPreview);
            imgClickPic = (ImageView) view.findViewById(R.id.imgClickPic);
            imgClickPic.setOnClickListener(this);
            imgClose = (ImageView) view.findViewById(R.id.imgClose);
            imgClose.setOnClickListener(this);
            layoutShowImage = (RelativeLayout) view.findViewById(R.id.layoutImage);
            imgPreview = (FrameLayout) view.findViewById(R.id.layoutImagePreview);
            imgViewPreview = (ImageView) view.findViewById(R.id.imgViewPreview);
            imgOk = (TextView) view.findViewById(R.id.imgOk);
            imgRetake = (TextView) view.findViewById(R.id.imgRetake);
            handlerProgress = new Handler();
            /*imgOk.setOnClickListener(this);*/
            imgRetake.setOnClickListener(this);
            mImgViewFrontCam = (ImageView) view.findViewById(R.id.imgViewFrontCam);
            mImgViewBackCam = (ImageView) view.findViewById(R.id.imgViewBackCam);
            countTimer = (TextView) view.findViewById(R.id.contTimer);
            btnNext = (Button) view.findViewById(R.id.btnNext);
            camera_yesTV = (Button) view.findViewById(R.id.camera_yesTV);

            camera_desRL = (RelativeLayout) view.findViewById(R.id.camera_desRL);
            camera_desTV = (TextView) view.findViewById(R.id.camera_desTV);
            bar = new ProgressDialog(ctx);
            mPreview = new CameraPreview(ctx, mCamera);
            mCallBack.onChangeText(Utilities.BUTTON_SKIP, true);
            cameraImage=view.findViewById(R.id.imgd);

            camera_yesTV.setOnClickListener(this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                camera_desTV.setText(Html.fromHtml(getResources().getString(R.string.camera_new_des), Html.FROM_HTML_MODE_COMPACT));
            } else {
                camera_desTV.setText(Html.fromHtml(getResources().getString(R.string.camera_new_des)));
            }

//            if (findFrontFacingCameraAvailable()) {
//                utils.showAlert(ctx, (InterfaceAlertDissmiss) CameraFragment.this,
//                        getResources().getString(R.string.txtPermissionMessageCamera),
//                        getResources().getString(R.string.txtPermissionRequiredAlert),
//                        Html.fromHtml(ctx.getResources().getString(R.string.Yes1)),
//                        Html.fromHtml(ctx.getResources().getString(R.string.Skip)), 124, 0);
//            } else {
//                utils.showAlert(ctx, (InterfaceAlertDissmiss) CameraFragment.this,
//                        getResources().getString(R.string.txtPermissionMessageCameraFrntNot),
//                        getResources().getString(R.string.txtPermissionRequiredAlert),
//                        Html.fromHtml(ctx.getResources().getString(R.string.Yes1)),
//                        Html.fromHtml(ctx.getResources().getString(R.string.Skip)), 124, 0);
//            }
        } catch (Exception e) {
            logException(e, "CameraFragment_initViews()");
        }

    }

    /**
     * on button click
     *
     * @param v
     */

    @Override
    public void onClick(View v) {
        super.onClick(v);

        try {
            switch (v.getId()) {
                case R.id.imgClickPic:
                    imgClose.setEnabled(false);
                    if (mCamera != null) {
                        /*mCamera.takePicture(shutterCallback, null, null, photoCallback);*/
                        timer(getActivity(), true, ConstantTestIDs.CAMERA_ID, null);
                        timer(getActivity(), false, ConstantTestIDs.CAMERA_ID, CameraFragment.this);
                        mCamera.takePicture(null, null, mPicture);
                        imgOk.setOnClickListener(this);
                        imgClickPic.setOnClickListener(null);
                        mPreview.setOnTouchListener(null);
                        /*imgClickPic.setEnabled(false);*/
                    }
                    break;

                case R.id.imgClose:
                    imgOkbollean = true;
                    imgClose.setEnabled(false);
                    imgClickPic.setEnabled(false);
                    mPreview.refreshCamera(mCamera);
                    releaseCamera();
                    if (isCameraFront) {
                        cameraFront = false;
                        pictureFileFront = null;
                        startCamera(false);
                        timer(getActivity(), true, ConstantTestIDs.CAMERA_ID, null);
                        timer(getActivity(), false, ConstantTestIDs.CAMERA_ID, CameraFragment.this);
                        if (pictureFileFront != null) {
                            mImgViewFrontCam.setImageURI(Uri.parse(pictureFileFront.getAbsolutePath()));
                            mImgViewFrontCam.setTag(1);
                            mImgViewFrontCam.setRotation(-90);
                            //  mCallBack.onChangeText(utils.BUTTON_NEXT, true);
                            // utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_38.name(),
                            // AsyncConstant.TEST_PASS);

                            testController.saveSkipResponse(1, ConstantTestIDs.FRONT_CAMERA);

                        } else {
                            isFrontClose = true;
                            testController.saveSkipResponse(-1, ConstantTestIDs.FRONT_CAMERA);


                        }
                    } else {
                        pictureFileBack = null;
                        cancelTimer();
                        isTestPerformed = true;
                        if (pictureFileFront != null) {
                            layoutShowImage.setVisibility(View.VISIBLE);
                        }


                        if (pictureFileBack != null) {
                            mImgViewBackCam.setImageURI(Uri.parse(pictureFileBack.getAbsolutePath()));
                            mImgViewBackCam.setTag(1);
                            testController.saveSkipResponse(1, ConstantTestIDs.BACK_CAMERA);

                        } else {
                            isBackClose = true;
                            testController.saveSkipResponse(-1, ConstantTestIDs.BACK_CAMERA);

                        }
                        submitResul();
                        handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                /*Constants.isManualIndividual = false;*/
                                handler.removeCallbacksAndMessages(null);
                                onNextPress();
                            }
                        }, (pictureFileFront == null && pictureFileBack == null) ? 1000 : 2000);


                        if (mImgViewFrontCam.getTag() == null && mImgViewBackCam.getTag() == null) {
                            testController.saveSkipResponse(-1, ConstantTestIDs.BACK_CAMERA);
                            testController.saveSkipResponse(-1, ConstantTestIDs.FRONT_CAMERA);
                            testController.onServiceResponse(false, "", ConstantTestIDs.CAMERA_ID);
                            utils.showToast(ctx,
                                    getResources().getString(R.string.txtCameraTestFail));
                            mCallBack.onChangeText(utils.BUTTON_SKIP, false);
                        } else if (mImgViewFrontCam.getTag() == null) {

                            if (findFrontFacingCameraAvailable()) {
                                utils.showToast(ctx,
                                        getResources().getString(R.string.txtFrontCameraTestFail));
                            }

                        } else if (mImgViewBackCam.getTag() == null) {

                            utils.showToast(ctx,
                                    getResources().getString(R.string.txtBackCameraTestFail));
                        } else {

                            utils.showToast(ctx, getResources().getString(R.string.txtManualPass));
                        }
                    }

                    break;
                case R.id.imgRetake:
                    imgPreview.setVisibility(View.GONE);
                    timer(getActivity(), true, ConstantTestIDs.CAMERA_ID, null);
                    timer(getActivity(), false, ConstantTestIDs.CAMERA_ID, CameraFragment.this);
                    imgOk.setOnClickListener(this);
                    imgClickPic.setOnClickListener(this);
                    cameraPreview.removeView(mPreview);
                    handlerProgress.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mPreview.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                        focusOnTouch(event);
                                    }
                                    return true;
                                }
                            });
                        }
                    }, 1500);

                    if (isCameraFront) {
                        startCamera(true);
                    } else {
                        startCamera(false);

                    }
                    break;
                case R.id.imgOk:
                    imgOkbollean = true;
                    if (!bar.isShowing()) {
                        showProgress();
                        imgOk.setOnClickListener(null);
                        imgClickPic.setOnClickListener(null);
                        imgPreview.setVisibility(View.GONE);
                        if (isCameraFront) {
                            cameraFront = false;
                            startCamera(false);

                            if (pictureFileFront != null) {
                                mImgViewFrontCam.setImageURI(Uri.parse(pictureFileFront.getAbsolutePath()));
                                mImgViewFrontCam.setRotation(-90);
                                mImgViewFrontCam.setTag(1);
                                mCallBack.onChangeText(utils.BUTTON_NEXT, true);
                                testController.saveSkipResponse(1, ConstantTestIDs.FRONT_CAMERA);

                            }
                        } else {
                            cancelTimer();
                            isTestPerformed = true;
                            layoutShowImage.setVisibility(View.VISIBLE);

                            if (pictureFileBack != null) {
                                mImgViewBackCam.setImageURI(Uri.parse(pictureFileBack.getAbsolutePath()));
                                mImgViewBackCam.setRotation(90);
                                mImgViewBackCam.setTag(1);
                                mCallBack.onChangeText(utils.BUTTON_NEXT, true);
                                testController.saveSkipResponse(1, ConstantTestIDs.BACK_CAMERA);

                                //
                            }
                            submitResul();
//
                            onNextPress();

                            if (pictureFileFront == null && pictureFileBack == null) {

                                utils.showToast(ctx,
                                        getResources().getString(R.string.txtCameraTestFail));
                                mCallBack.onChangeText(utils.BUTTON_SKIP, false);
                            } else if (pictureFileFront == null) {

                                if (findFrontFacingCameraAvailable()) {
                                    utils.showToast(ctx,
                                            getResources().getString(R.string.txtFrontCameraTestFail));
                                }

                            } else if (pictureFileBack == null) {

                                utils.showToast(ctx,
                                        getResources().getString(R.string.txtBackCameraTestFail));
                            } else {

                                utils.showToast(ctx,
                                        getResources().getString(R.string.txtManualPass));
                            }
                        }
                        break;
                    }

                case R.id.camera_yesTV:
                    timer(getActivity(), true, ConstantTestIDs.CAMERA_ID, null);
                    timer(getActivity(), false, ConstantTestIDs.CAMERA_ID, CameraFragment.this);
                    if (getActivity() != null) {
                        getActivity().findViewById(R.id.btnNext).setEnabled(false);
                    }
                    camera_desRL.setVisibility(View.GONE);

                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.onChangeText(R.string.textSkip, false);
                    onButtonClick(true, 121, 0);
                    break;
            }
        } catch (Exception e) {
            logException(e, "CameraFragment_onClick()");
        }


    }

    /**
     * show progress to save image in device
     */
    public void showProgress() {
        try {
            bar.setTitle("");
            bar.setCancelable(false);
            bar.setMessage(ctx.getResources().getString(R.string.MessageProgressCamera));
            bar.show();
            handlerProgress.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (bar.isShowing()) {
                        bar.dismiss();
                        mPreview.setOnTouchListener(new View.OnTouchListener() {
                            @Override
                            public boolean onTouch(View v, MotionEvent event) {
                                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                    focusOnTouch(event);
                                }
                                return true;
                            }
                        });
                    }

                }
            }, 1500);
        } catch (Exception e) {
            logException(e, "CameraFragment_showProgress()");
        }

    }

    public void submitResult2() {

        if (!imgOkbollean) {
//            testController.saveSkipResponse(previousCmaeraStatus,ConstantTestIDs.CAMERA_ID);

            testController.saveSkipResponse(previousfrontcamerastatus,
                    ConstantTestIDs.FRONT_CAMERA);
            testController.saveSkipResponse(previousrearcamerastatus, ConstantTestIDs.BACK_CAMERA);
        } else if (pictureFileBack != null && pictureFileFront != null) {
            testController.saveSkipResponse(1, ConstantTestIDs.CAMERA_ID);
        } else if (pictureFileFront != null || pictureFileBack != null) {
            testController.saveSkipResponse(0, ConstantTestIDs.CAMERA_ID);
        }
//        timer(getActivity(),true,ConstantTestIDs.DISPLAY_ID,CameraFragment.this);

    }

    /**
     * Release camers
     */
    private void releaseCamera() {
        // stop and release camera
        try {
            if (mCamera != null) {
                mCamera.release();
                mCamera = null;
            }
        } catch (Exception e) {
            logException(e, "CameraFragment_releaseCamera()");
        }

    }

    /**
     * check runtime permission for camera and write external storage
     *
     * @param cameraFront
     */
    private void checkRuntimePermisson(boolean cameraFront) {
        try {
            int hasCameraPermission = 0;
            int hasWriteExternalStoragePermission = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                hasCameraPermission = getActivity().checkSelfPermission(Manifest.permission.CAMERA);
                hasWriteExternalStoragePermission =
                        getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            REQUEST_CODE_ASK_PERMISSIONS_CAMERA);
                    return;
                } else if (hasWriteExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_CODE_ASK_PERMISSIONS_STORAGE);
                    return;
                }

            }

            if (cameraFront && findFrontFacingCameraAvailable()) {

                startCamera(true);
            } else {
                startCamera(false);
            }

        } catch (Exception e) {
            logException(e, "CameraFragment_checkRuntimePermisson()");
        }

    }

    /**
     * Handle request permission timerFail
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            switch (requestCode) {
                case REQUEST_CODE_ASK_PERMISSIONS_CAMERA:
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // Permission Granted
                        checkRuntimePermisson(cameraFront);
                    } else {
                        // Permission Denied
                        utils.showAlert(ctx, (InterfaceAlertDissmiss) CameraFragment.this,
                                ctx.getResources().getString(R.string.txtPermissionMessage2),
                                ctx.getResources().getString(R.string.txtPermissionRequiredAlert),
                                ctx.getResources().getString(R.string.Ok),
                                ctx.getResources().getString(R.string.textSkip), 123);
                    }
                    break;

                case REQUEST_CODE_ASK_PERMISSIONS_STORAGE:
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // Permission Granted
                        if (findFrontFacingCameraAvailable()) {

                            startCamera(true);
                        } else {
                            startCamera(false);
                        }
                    } else {
                        // Permission Denied
                        utils.showAlert(ctx, (InterfaceAlertDissmiss) CameraFragment.this,
                                ctx.getResources().getString(R.string.txtPermissionMessage3),
                                ctx.getResources().getString(R.string.txtPermissionRequiredAlert),
                                ctx.getResources().getString(R.string.Ok),
                                ctx.getResources().getString(R.string.textSkip), 123);
                    }
                    break;


            }
        } catch (Exception e) {
            logException(e, "CameraFragment_onRequestPermissionsResult()");
        }

    }

    /**
     * start camera
     *
     * @param isFront if isFront is true means start device front camera
     */
    private void startCamera(boolean isFront) {
        try {
            timer(getActivity(), false, ConstantTestIDs.CAMERA_ID, CameraFragment.this);
            releaseCamera();
            if (openCam(isFront)) {
                imgClose.setEnabled(true);
                imgClickPic.setEnabled(true);
                isCameraFront = true;
                FrameLayout.LayoutParams previewLayoutParams =
                        new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                                FrameLayout.LayoutParams.WRAP_CONTENT);
                cameraPreview.addView(mPreview, 0, previewLayoutParams);

                mtxtViewCount = (TextView) view.findViewById(R.id.txtViewCount);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPreview.invalidate();
                    }
                }, 100);

            } else {
                imgClose.setEnabled(true);
                imgClickPic.setEnabled(true);
                cameraPreview.removeView(mPreview);
                isCameraFront = false;
                FrameLayout.LayoutParams previewLayoutParams =
                        new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                                FrameLayout.LayoutParams.WRAP_CONTENT);
                cameraPreview.addView(mPreview, 0, previewLayoutParams);
                mPreview.refreshCamera(mCamera);
                /*mCamera.autoFocus(null);*/

                setCameraParams(mCamera);
                imgClickPic.setOnClickListener(this);
                mtxtViewCount = (TextView) view.findViewById(R.id.txtViewCount);
            }
        } catch (Exception e) {

        }


    }

    /**
     * start camera
     *
     * @param isFront
     * @return
     */

    private boolean openCam(boolean isFront) {
        try {
            boolean isFrontCam = false;
            int cameraId;
            if (isFront) {
                cameraId = findFrontFacingCamera();
                isFrontCam = true;
            } else {
                cameraId = 0;
                isFrontCam = false;
            }
            if (cameraId >= 0) {
                //open the backFacingCamera
                //set a picture timerFail
                //refresh the preview
                mCamera = Camera.open(cameraId);

                mCamera.setDisplayOrientation(90);
                mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);

                SurfaceHolder surfaceHolder = mPreview.getHolder();

                surfaceHolder.addCallback(new SurfaceHolder.Callback() {
                    @Override
                    public void surfaceCreated(SurfaceHolder holder) {

                        Log.w("surfaceCreated", "surfaceCreated");
                    }

                    @Override
                    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                               int height) {
                        Log.w("surfaceChanged", "surfaceChanged");

                    }

                    @Override
                    public void surfaceDestroyed(SurfaceHolder holder) {
                        Log.w("surfaceDestroyed", "surfaceDestroyed");

                        isCAmeraClosed = true;
                    }
                });
                mCamera.setPreviewDisplay(surfaceHolder);
                /*mCamera.autoFocus(null);*/
                setCameraParams(mCamera);
                /*utils.takePicture(ctx, true);*/
                /*isFrontCam = true;*/
            }
            return isFrontCam;
        } catch (Exception e) {

            return false;
        }

    }

    /**
     * set parameters for camera
     *
     * @param mCamera
     */

    private void setCameraParams(Camera mCamera) {
        try {
            Camera.Parameters params = mCamera.getParameters();

            List<Camera.Size> sizes = params.getSupportedPictureSizes();
            Camera.Size size = sizes.get(0);
            //Camera.Size size1 = sizes.get(0);
            for (int i = 0; i < sizes.size(); i++) {
                if (sizes.get(i).width > size.width)
                    size = sizes.get(i);
            }
            params.setPictureSize(size.width, size.height);
            /*params.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);*/
            /*params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);*/
            /*params.setSceneMode(Camera.Parameters.SCENE_MODE_AUTO);*/
            /*params.setWhiteBalance(Camera.Parameters.WHITE_BALANCE_AUTO);*/
            List<String> focusModes = params.getSupportedFocusModes();
            if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            }
            params.setExposureCompensation(50);
            params.setPictureFormat(ImageFormat.JPEG);
            params.setJpegQuality(50);
//            mCamera.setParameters(params);
        } catch (Exception e) {
            logException(e, "CameraFragment_setCameraParams()");
        }

    }

    /**
     * focus touch listener
     *
     * @param event
     */
    private void focusOnTouch(MotionEvent event) {
        try {
            if (mCamera != null) {
                Camera.Parameters parameters = mCamera.getParameters();
                if (parameters.getMaxNumMeteringAreas() > 0) {
                    Log.i("", "fancy !");
                    Rect rect = calculateFocusArea(event.getX(), event.getY());

                    List<String> focusModes = parameters.getSupportedFocusModes();
                    if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                        List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();
                        meteringAreas.add(new Camera.Area(rect, 800));
                        parameters.setFocusAreas(meteringAreas);

                        mCamera.setParameters(parameters);
                        mCamera.autoFocus(mAutoFocusTakePictureCallback);
                    }
                } else {
                    mCamera.autoFocus(mAutoFocusTakePictureCallback);
                }
            }
        } catch (Exception e) {
            logException(e, "CameraFragment_focusOnTouch()");
        }

    }

    private Rect calculateFocusArea(float x, float y) {
        try {
            int left = clamp(Float.valueOf((x / mPreview.getWidth()) * 2000 - 1000).intValue(),
                    FOCUS_AREA_SIZE);
            int top = clamp(Float.valueOf((y / mPreview.getHeight()) * 2000 - 1000).intValue(),
                    FOCUS_AREA_SIZE);

            return new Rect(left, top, left + FOCUS_AREA_SIZE, top + FOCUS_AREA_SIZE);
        } catch (Exception e) {
            logException(e, "CameraFragment_calculateFocusArea");
            return null;
        }


    }

    private int clamp(int touchCoordinateInCameraReper, int focusAreaSize) {

        try {
            int result;
            if (Math.abs(touchCoordinateInCameraReper) + focusAreaSize / 2 > 1000) {
                if (touchCoordinateInCameraReper > 0) {
                    result = 1000 - focusAreaSize / 2;
                } else {
                    result = -1000 + focusAreaSize / 2;
                }
            } else {
                result = touchCoordinateInCameraReper - focusAreaSize / 2;
            }
            return result;
        } catch (Exception e) {
            logException(e, "CameraFragment_clamp()");
            return 0;
        }

    }
// implement this timerFail

    private int findFrontFacingCamera() {


        int cameraId = -1;
        // Search for the front facing camera
        try {
            int numberOfCameras = Camera.getNumberOfCameras();
            for (int i = 0; i < numberOfCameras; i++) {
                Camera.CameraInfo info = new Camera.CameraInfo();
                Camera.getCameraInfo(i, info);
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    cameraId = i;
                    cameraFront = true;
                    cameraFront = true;
                    break;
                } else {
                    cameraFront = false;
                }
            }
            return cameraId;
        } catch (Exception e) {
            logException(e, "CameraFragment_findFrontFacingCamera()");
            return -1;
        }

    }

    /**
     * check front camera is available or not
     *
     * @return
     */
    private boolean findFrontFacingCameraAvailable() {

        try {
            int cameraId = -1;
            // Search for the front facing camera
            int numberOfCameras = Camera.getNumberOfCameras();
            for (int i = 0; i < numberOfCameras; i++) {
                Camera.CameraInfo info = new Camera.CameraInfo();
                Camera.getCameraInfo(i, info);
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    cameraId = i;
                    return cameraFront = true;
                }
            }
            return false;
        } catch (Exception e) {
            logException(e, "CameraFragment_findFrontFacingCameraAvailable()");
            return false;
        }

    }


    //make picture and save to a folder

    /**
     * store image in device
     *
     * @param isCameraFront
     * @return
     */
    private File getOutputMediaFile(boolean isCameraFront) throws Exception {
        //make a new file directory inside the "sdcard" folder
        /*File mediaStorageDir = new File("/sdcard/", "MegaMMR");*/
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory().toString(),
                "MegaMMR");

        //if this "JCGCamera folder does not exist
        if (!mediaStorageDir.exists()) {
            //if you cannot make this folder return
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        //take the current timeStamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        //and make a media file:
        /*mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_FRONTCAM" +
        timeStamp + ".jpg");*/
        if (isCameraFront) {
            mediaFile =
                    new File(mediaStorageDir.getPath() + File.separator + "IMG_FRONTCAM_" + timeStamp + ".jpeg");
            pictureFileFront = mediaFile;
            mediaFile.deleteOnExit();
        } else {
            mediaFile =
                    new File(mediaStorageDir.getPath() + File.separator + "IMG_BACKCAM_" + timeStamp + ".jpeg");
            pictureFileBack = mediaFile;
            mediaFile.deleteOnExit();
        }
        return mediaFile;
    }

    /**
     * delete image after test
     */
    public void deleteFile() {
        try {
            File mediaStorageDir = new File(Environment.getExternalStorageDirectory().toString(),
                    "MegaMMR");
            if (mediaStorageDir.isDirectory()) {
                String[] children = mediaStorageDir.list();
                if (children != null)
                    for (String child : children) {
                        if (child.endsWith(".jpeg") || child.endsWith(".jpeg"))
                            new File(mediaStorageDir, child).delete();
                    }
            }
        } catch (Exception e) {
            logException(e, "CameraFragment_deleteFile()");
        }

    }

    /**
     * onPause
     */
    @Override
    public void onPause() {
        super.onPause();

        try {
            cancelTimer();
            // releaseCamera();
            //when on Pause, release camera in order to be used from other applications
            if (handler != null) {
                handler.removeCallbacksAndMessages(null);
            }
        } catch (Exception e) {
            logException(e, "CameraFragment_onPause()");
        }

    }

    private boolean hasCamera(Context context) {

        try {
            //check if the device has camera
            if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            logException(e, "CameraFragment_hasCamera()");
            return false;
        }

    }

    /**
     * Handle timerFail after camera click image button
     *
     * @return
     */
    private Camera.PictureCallback getPictureCallback() {
        Camera.PictureCallback picture = new Camera.PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                File pictureFile = null;

                //make a new picture file
                try {
                    if (isCameraFront) {
                        getOutputMediaFile(isCameraFront);
                        pictureFile = pictureFileFront;
                    } else {
                        getOutputMediaFile(isCameraFront);
                        pictureFile = pictureFileBack;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    logException(e, "CameraFragment_getPictureCallback()");
                }
                if (pictureFile == null) {
                    return;
                }
                try {
                    //write the file
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();
//                    Toast toast = Toast.makeText(ctx,
//                            getResources().getString(R.string.txtPictureSaved) + " " + pictureFile.getName(), Toast.LENGTH_LONG);
//                    toast.show();
                    if (isCameraFront) {
                        imgPreview.setVisibility(View.VISIBLE);
                        if (pictureFileFront != null) {
                            //      imgViewPreview.setRotation(270);
                            imgViewPreview.setImageURI(Uri.parse(pictureFileFront.getAbsolutePath()));
                            imgViewPreview.setRotation(-90);
                            imgViewPreview.setTag(1);
                            //  mCallBack.onChangeText(utils.BUTTON_NEXT, true);
                            //  utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_38.name(),
                            //  AsyncConstant.TEST_PASS);
                        }

                    } else {
                        imgPreview.setVisibility(View.VISIBLE);
                        if (pictureFileBack != null) {
                            imgViewPreview.setImageURI(Uri.parse(pictureFileBack.getAbsolutePath()));
                            imgViewPreview.setRotation(90);
                            imgViewPreview.setTag(1);
                        }
                    }

                } catch (Exception e) {
                    logException(e, "CameraFragment_getPictureCallback()");
                }

                //refresh camera to continue preview
                mPreview.refreshCamera(mCamera);
            }
        };
        return picture;
    }

//    private int getColor() {
//        try {
//            return getResources().getColor(R.color.White);
//        } catch (Exception e) {
//            logException(e, "CameraFragment_getColor()");
//            return 0;
//        }
//
//    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(),
                    inImage, "Title", null);
            return Uri.parse(path);
        } catch (Exception e) {
            logException(e, "CameraFragment_getImageUri()");
            return null;
        }

    }

    public String getRealPathFromURI(Uri uri) {
        try {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        } catch (Exception e) {
            logException(e, "CameraFragment_getRealPathFromURI()");
            return "";
        }

    }

    @Override
    public void onStop() {
        if (mCallBack != null) {
            mCallBack.onChangeText(utils.BUTTON_NEXT, false);
        }


        super.onStop();
    }

    /**
     * Here change the header text
     ***/
    @Override
    public void onResume() {
        try {
            TitleBarFragment fragment = (TitleBarFragment) getFragment(R.id.headerContainer);
//            if (fragment != null) {
//                fragment.setTitleBarVisibility(true);
//                fragment.setSyntextVisibilty(false);
//                fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string
//                .txtManualCamera), false, false, 0);
//                // nextButtonHandler = new Handler();
//            }
            if (isCAmeraClosed) {
                if (cameraFront && findFrontFacingCameraAvailable()) {

                    startCamera(true);
                } else {
                    startCamera(false);
                }


            }
            //checkRuntimePermisson(cameraFront);
//            TitleBarFragment fragment = (TitleBarFragment) getFragment(R.id.headerContainer);
//            if (fragment != null) {
//                fragment.setTitleBarVisibility(true);
//                fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string
//                .txtManualCamera), false, false, 0);
            if (Constants.isBackButton == true) {
                if ((manualDataStable.checkSingleHardware(JsonTags.MMR_35.name(), ctx, utils) == 1) && (manualDataStable.checkSingleHardware(JsonTags.MMR_36.name(), ctx, utils) == 1)) {
                    mCallBack.onChangeText(utils.BUTTON_NEXT, true);
                    Constants.isBackButton = false;
                }
            }
            if (isTestPerformed) {
                Log.d("msg", "true");
                onNextPress();


            } else {
                //startTimer();}
            }

//            }
        } catch (Exception e) {
            logException(e, "CameraFragment_onResume()");

        }
        super.onResume();


//        checkRuntimePermisson(cameraFront);
    }

    /**
     * start timer
     */
    void startTimer() {
        try {
            cTimer = new CountDownTimer(Constants.countTimerLongExtra, 1000) {
                public void onTick(long millisUntilFinished) {
                    countTimer.setText(String.valueOf(millisUntilFinished / 1000));
                }

                public void onFinish() {
                    showAlertDialog(getResources().getString(R.string.txtAlertTitleGreatAlert),
                            getResources().getString(R.string.txtCount_test_Dialog),
                            getResources().getString(R.string.Yes),
                            getResources().getString(R.string.No));
                }
            };
            cTimer.start();
        } catch (Exception e) {
            logException(e, "CameraFragment_startTimer()");
        }


    }

    /**
     * Alert view
     * This is shown after time completition
     *
     * @param dialogTitle
     * @param dialogMessage
     * @param btnTextPositive
     * @param btnTextNegative
     */
    public void showAlertDialog(String dialogTitle, String dialogMessage, String btnTextPositive,
                                String btnTextNegative) {
        try {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(
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
                            startTimer();
                            // Write your code here to execute after dialog closed
                        }
                    });
            alertDialog.setNegativeButton(btnTextNegative, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //   utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_44.name(),
                    //   AsyncConstant.TEST_FAILED);

                    if (Constants.isManualIndividual) {
                        cancelTimer();
                        nextButtonHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                Constants.isManualIndividual = false;
                                nextButtonHandler.removeCallbacksAndMessages(null);
                                replaceFragment(R.id.container,
                                        new ManualTestFragment((InterfaceButtonTextChange) getActivity()), FragmentTag.MANUAL_TEST_FRAGMENT.name(), false);
                            }
                        }, 2000);

                    } else {
                        cancelTimer();

                        onNextPress();
                    }

                }
            });

            // Showing Alert Message
            alertDialog.show();
        } catch (Exception e) {
            logException(e, "CameraFragment_showAlertDialog()");
        }

    }


    //cancel timer
    void cancelTimer() {
        try {
            if (cTimer != null)
                cTimer.cancel();
        } catch (Exception e) {
            logException(e, "CameraFragment_cancelTimer()");
        }

    }

    /**
     * call after test completion or user click on skip button
     */
    public void onNextPress() {
        try {
            if (Constants.isSkipButton) {
                cameraImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_camera_svg),false,getActivity());
                utils.showToast(ctx, getResources().getString(R.string.txtManualFail));
                camera_yesTV.setEnabled(false);
                camera_yesTV.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque));

            }
            timer(getActivity(), true, ConstantTestIDs.CAMERA_ID, null);
            cancelTimer();
            MainActivity mainActivity = (MainActivity) getActivity();
            boolean semi = false;
            nextPress(mainActivity, semi);


        } catch (Exception e) {
            logException(e, "CameraFragment_onNextPress()");
        }


    }

    @Override
    public void onButtonClick(boolean isCanceled, int callbackID) {
        try {
            if (!isCanceled) {
//                checkRuntimePermisson(cameraFront);
                replaceFragment(R.id.container,
                        new ManualTestFragment((InterfaceButtonTextChange) getActivity()),
                        FragmentTag.MANUAL_TEST_FRAGMENT.name(), false);
            } else {

//                replaceFragment(R.id.container, new ManualTestFragment(
//                (InterfaceButtonTextChange) getActivity()), FragmentTag.MANUAL_TEST_FRAGMENT
//                .name(), false);
                TestController testController = TestController.getInstance(getActivity());
                testController.saveSkipResponse(0, ConstantTestIDs.CAMERA_ID);
                isTestPerformed = true;
                utils.showToast(ctx, getResources().getString(R.string.txtManualFail));
                onNextPress();
            }
        } catch (Exception e) {
            logException(e, "CameraFragment_onButtonClick(boolean isCanceled, int callbackID)");
        }

    }

    @Override
    public void onDestroyView() {
        try {
            deleteFile();
            timer(getActivity(), true, ConstantTestIDs.CAMERA_ID, null);

            if (handler != null) {
                handler.removeCallbacksAndMessages(null);
            }
            releaseCamera();
        } catch (Exception e) {
            logException(e, "CameraFragment_onDestroyView()");
        }

        super.onDestroyView();
    }

    @Override
    public void onButtonClick(boolean isCanceled, int callbackID, int which) {
        try {
            if (isCanceled) {
                checkRuntimePermisson(cameraFront);
            } else {
                TestController testController = TestController.getInstance(getActivity());
                imgClose.setEnabled(false);
                testController.saveSkipResponse(0, ConstantTestIDs.CAMERA_ID);
                //  testController.saveSkipResponse(-1, ConstantTestIDs.BACK_CAMERA);
                //  testController.saveSkipResponse(0, ConstantTestIDs.CAMERA_ID);
                utils.showToast(ctx, getResources().getString(R.string.txtManualFail));
                isTestPerformed = true;
                onNextPress();
            }
        } catch (Exception e) {
            logException(e, "CameraFragment_onButtonClick(boolean isCanceled, int callbackID, int" +
                    " which)");
        }

    }

    /**
     * on BackPressed
     */

    public void onBackPress() {


        snackShow(relativeLayoutCamera, new InterfaceAlertDissmiss() {
            @Override
            public void onButtonClick(boolean isCanceled, int callbackID) {
                submitResult2();
            }

            @Override
            public void onButtonClick(boolean isCanceled, int callbackID, int which) {

            }
        });
       /* try {
            clearAllStack();
            replaceFragment(R.id.container, new ManualTestFragment((InterfaceButtonTextChange)
            getActivity()), FragmentTag.MANUAL_TEST_FRAGMENT.name(), false);
        } catch (Exception e) {
            logException(e, "CameraFragment_onBackPress");
        }*/

    }

    void back() {

        if (pictureFileBack != null && pictureFileFront != null) {
            testController.saveSkipResponse(1, ConstantTestIDs.FRONT_CAMERA);
            testController.saveSkipResponse(1, ConstantTestIDs.BACK_CAMERA);
            testController.saveSkipResponse(1, ConstantTestIDs.CAMERA_ID);

        } else if (pictureFileFront != null) {
            testController.saveSkipResponse(1, ConstantTestIDs.FRONT_CAMERA);
            testController.saveSkipResponse(-1, ConstantTestIDs.BACK_CAMERA);
            testController.saveSkipResponse(0, ConstantTestIDs.CAMERA_ID);
        } else if (pictureFileBack != null) {
            testController.saveSkipResponse(-1, ConstantTestIDs.FRONT_CAMERA);
            testController.saveSkipResponse(1, ConstantTestIDs.BACK_CAMERA);
            testController.saveSkipResponse(0, ConstantTestIDs.CAMERA_ID);
        }
//        if(pictureFileBack==null  && pictureFileFront== null){
//            testController.saveSkipResponse(-1,ConstantTestIDs.FRONT_CAMERA);
//            testController.saveSkipResponse(-1,ConstantTestIDs.BACK_CAMERA);
//            testController.saveSkipResponse(-1,ConstantTestIDs.CAMERA_ID);
//
//        }
        if (pictureFileBack != null && pictureFileFront != null) {
            testController.saveSkipResponse(1, ConstantTestIDs.CAMERA_ID);
        } else if (pictureFileFront != null || pictureFileBack != null) {
            testController.saveSkipResponse(0, ConstantTestIDs.CAMERA_ID);

        }

    }


    public void submitResul() {
        if (pictureFileBack != null && pictureFileFront != null) {
            testController.saveSkipResponse(1, ConstantTestIDs.CAMERA_ID);
        } else if (pictureFileFront != null || pictureFileBack != null) {
            testController.saveSkipResponse(0, ConstantTestIDs.CAMERA_ID);

        } else {
            //  testController.saveSkipResponse(0,ConstantTestIDs.CAMERA_ID);
        }
//        timer(getActivity(),true,ConstantTestIDs.DISPLAY_ID,CameraFragment.this);

    }


//    public void submitResul(){
//
//
//
//        if(pictureFileBack!=null  && pictureFileFront!= null){
//            testController.saveSkipResponse(1,ConstantTestIDs.FRONT_CAMERA);
//            testController.saveSkipResponse(1,ConstantTestIDs.BACK_CAMERA);
//            testController.saveSkipResponse(1,ConstantTestIDs.CAMERA_ID);
//
//        }
//       else if(pictureFileFront!= null){
//            testController.saveSkipResponse(1,ConstantTestIDs.FRONT_CAMERA);
//            testController.saveSkipResponse(-1,ConstantTestIDs.BACK_CAMERA);
//            testController.saveSkipResponse(0,ConstantTestIDs.CAMERA_ID);
//        }
//       else if(pictureFileBack!= null)
//       {
//           testController.saveSkipResponse(-1,ConstantTestIDs.FRONT_CAMERA);
//           testController.saveSkipResponse(1,ConstantTestIDs.BACK_CAMERA);
//           testController.saveSkipResponse(0,ConstantTestIDs.CAMERA_ID);
//       }
////        if(pictureFileBack==null  && pictureFileFront== null){
////            testController.saveSkipResponse(-1,ConstantTestIDs.FRONT_CAMERA);
////            testController.saveSkipResponse(-1,ConstantTestIDs.BACK_CAMERA);
////            testController.saveSkipResponse(-1,ConstantTestIDs.CAMERA_ID);
////
////        }
//        if(pictureFileBack!=null  && pictureFileFront!= null){
//            testController.saveSkipResponse(1,ConstantTestIDs.CAMERA_ID);
//        }else if(pictureFileFront!= null || pictureFileBack!= null){
//            testController.saveSkipResponse(0,ConstantTestIDs.CAMERA_ID);
//
//        }
//
//        else {
//            //  testController.saveSkipResponse(0,ConstantTestIDs.CAMERA_ID);
//        }
////        timer(getActivity(),true,ConstantTestIDs.DISPLAY_ID,CameraFragment.this);
//
//    }


    public void logException(Exception e, String methodName) {
        try {
            Utilities utilities = Utilities.getInstance(getActivity());
            Activity activity = getActivity();
            Context context = getActivity();
            ExceptionLogUtils exceptionLogUtils = new ExceptionLogUtils(utilities, context,
                    activity);
            exceptionLogUtils.addExceptionLog(utilities, context, activity, null, null, e,
                    methodName);
        } catch (Exception exp) {
            // logException(exp, "CameraFragment_logException()");
        }

    }

    /**
     * show snack bar when user click on back button
     */


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void timerFail() {


        camera_yesTV.setEnabled(false);
        camera_yesTV.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque));
        imgClickPic.setEnabled(false);
        imgClose.setEnabled(false);
        imgOk.setEnabled(false);
        imgRetake.setEnabled(false);
        if (pictureFileBack == null && !isBackClose)
            testController.saveSkipResponse(0, ConstantTestIDs.BACK_CAMERA);

        if (pictureFileFront == null && !isFrontClose)
            testController.saveSkipResponse(0, ConstantTestIDs.FRONT_CAMERA);

        testController.saveSkipResponse(0, ConstantTestIDs.CAMERA_ID);

        isTestPerformed = true;
        cameraImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_camera_svg),false,getActivity());
        utils.showToast(ctx, getResources().getString(R.string.txtManualFail));
        onNextPress();

    }
}


