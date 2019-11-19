package com.officework.fragments;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.officework.R;
import com.officework.base.BaseFragment;
import com.officework.constants.AppConstantsTheme;
import com.officework.utils.ExceptionLogUtils;
import com.officework.utils.Utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Girish on 7/29/2016.
 */
public class CameraInfoFragment extends BaseFragment {
    View view;
    Utilities utils;
    Context ctx;
    Camera camera = null;
    public static int BACK_CAMERA_ID = 0;
    final public int REQUEST_CODE_ASK_PERMISSIONS = 1230;
    Camera cameraFront = null;
    boolean cameraFrontAvailable = false;

    public CameraInfoFragment() {
    }

    /*	Camera*/
    TextView mtvResolution, mtvFocalLength, mtvFocusModes, mtvJPEGQuality, mtvImageFormat, mtvSecondaryFocusMode, mtvSecondaryResolution,primraryHeading,secondaryHeding;
    /*	Sensors*/
    TextView mtvSensorInfo, mtvAccelerometer, mtvLightSensor, mtvProximitySensor,sensorsHeading;

    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {

        try {
            if (view == null) {
                view = inflater.inflate(R.layout.fragment_camera, null);
                utils = Utilities.getInstance(getActivity());
                ctx = getActivity();
            //    Crashlytics.getInstance().log(FragmentTag.CAMERA_INFO_FRAGMENT.name());
                initViews();
          /*  checkRuntimePermisson();*/
            }
            return view;
        } catch (Exception e) {
            logException(e, "CameraInfoFragment_initUI()");
            return null;
        }

    }

    private void initViews() {
        try {
            /*	Camera*/
            mtvResolution = (TextView) view.findViewById(R.id.tvResolution);
            mtvFocalLength = (TextView) view.findViewById(R.id.tvFocalLength);
            mtvFocusModes = (TextView) view.findViewById(R.id.tvFocusModes);
            mtvJPEGQuality = (TextView) view.findViewById(R.id.tvJPEGQuality);
            mtvImageFormat = (TextView) view.findViewById(R.id.tvImageFormat);
            mtvSecondaryResolution = (TextView) view.findViewById(R.id.tvSecondaryResolution);
            mtvSecondaryFocusMode = (TextView) view.findViewById(R.id.tvSecondaryFocusModes);
            primraryHeading=(TextView)view.findViewById(R.id.primraryHeading);
            secondaryHeding=(TextView)view.findViewById(R.id.secondaryheading);
            sensorsHeading=(TextView)view.findViewById(R.id.sensorsheading);

        /*	Sensors*/
            mtvSensorInfo = (TextView) view.findViewById(R.id.tvSensorInfo);
            mtvAccelerometer = (TextView) view.findViewById(R.id.tvAccelerometer);
            mtvLightSensor = (TextView) view.findViewById(R.id.tvLightSensor);
            mtvProximitySensor = (TextView) view.findViewById(R.id.tvProximitySensor);
//            int color= Color.parseColor(new ThemeManager(getActivity()).getTheme());
            int color= AppConstantsTheme.getIconColor();
            primraryHeading.setTextColor(color);
            secondaryHeding.setTextColor(color);
            sensorsHeading.setTextColor(color);
            findFrontFacingCamera();

            cameraInfo();
            sensorInfo();
        } catch (Exception e) {
            logException(e, "CameraInfoFragment_initViews()");
        }

    }

    /**
     * This method will find the camera detail
     */
    private void cameraInfo() {
        try {
            /**
             *  releaseCameraAndPreview() method is used to release Camera hardware if it is used by another hardware
             */
            //Camera resolution
            if (DeviceInfoFragment.permission) {
                mtvResolution.setText(String.valueOf(getBackCameraResolutionInMp()) + "MP");
                mtvSecondaryResolution.setText(String.valueOf(getFrontCamResolution()) + "MP");
            } else {
                mtvResolution.setText("N/A");
            }

            mtvSecondaryFocusMode.setText(getFrontFocusMode());
            //Focus Modes
            mtvFocusModes.setText(getFocusMode());
        } catch (Exception e) {
            logException(e, "CameraInfoFragment_cameraInfo()");
        }


    }

    /**
     * get focus mode for available camera
     *
     * @return
     */

    private String getFrontFocusMode() {
        try {
            if (cameraFrontAvailable) {

                try {
                    Camera.CameraInfo info = new Camera.CameraInfo();
                    Camera.getCameraInfo(1, info);

                    if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                        releaseCameraAndPreview();
                        if (cameraFront == null) {
                            cameraFront = Camera.open(1);
                        }
                        Camera.Parameters parameters = cameraFront.getParameters();
                        List<String> supportedFocusModes = parameters.getSupportedFocusModes();
                        String focusMode = selectFocusMode(supportedFocusModes);
                        return focusMode;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                return "N/A";
            }

            return null;
        } catch (Exception e) {
            logException(e, "CameraInfoFragment_cameraInfo()");
            return "N/A";
        }


    }

    /**
     * check front cam is available or not
     */
    private void findFrontFacingCamera() {
        try {
            int cameraId = -1;
            // Search for the front facing camera
            int numberOfCameras = Camera.getNumberOfCameras();
            for (int i = 0; i < numberOfCameras; i++) {
                Camera.CameraInfo info = new Camera.CameraInfo();
                Camera.getCameraInfo(i, info);
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    cameraId = i;
                    cameraFrontAvailable = true;
                    break;
                }
            }
        } catch (Exception e) {
            logException(e, "CameraInfoFragment_findFrontFacingCamera()");
        }


    }

    /**
     * get resolution for front camera
     *
     * @return
     */
    public int getFrontCamResolution() {
        try {
            if (cameraFrontAvailable) {
                releaseCameraAndPreview();
                if (cameraFront == null) {
                    cameraFront = Camera.open(1);
                }


                // For Back Camera
                android.hardware.Camera.Parameters params = cameraFront.getParameters();
                List sizes = params.getSupportedPictureSizes();
                Camera.Size result = null;

                ArrayList<Integer> arrayListForWidth = new ArrayList<Integer>();
                ArrayList<Integer> arrayListForHeight = new ArrayList<Integer>();
/*
        for (int i = 0; i < sizes.size(); i++) {
            result = (Camera.Size) sizes.get(i);
            arrayListForWidth.add(result.width);
            arrayListForHeight.add(result.height);
            //Log.debug("PictureSize", "Supported Size: " + result.width + "height : " + result.height);
            System.out.println("BACK PictureSize Supported Size: " + result.width + "height : " + result.height);
        }
        if (arrayListForWidth.size() != 0 && arrayListForHeight.size() != 0) {
            System.out.println("Back max W :" + Collections.max(arrayListForWidth));              // Gives Maximum Width
            System.out.println("Back max H :" + Collections.max(arrayListForHeight));                 // Gives Maximum Height
            System.out.println("Back Megapixel :" + (((Collections.max(arrayListForWidth)) * (Collections.max(arrayListForHeight))) / 1024000));

        }
        camera.release();

        arrayListForWidth.clear();
        arrayListForHeight.clear();*/

                //  cameraFront = Camera.open(1);        //  For Front Camera
                android.hardware.Camera.Parameters params1 = cameraFront.getParameters();
                List sizes1 = params1.getSupportedPictureSizes();
                Camera.Size result1 = null;
                for (int i = 0; i < sizes1.size(); i++) {
                    result1 = (Camera.Size) sizes1.get(i);
                    arrayListForWidth.add(result1.width);
                    arrayListForHeight.add(result1.height);
                    //  Log.debug("PictureSize", "Supported Size: " + result1.width + "height : " + result1.height);
                /*System.out.println("FRONT PictureSize Supported Size: " + result1.width + "height : " + result1.height);*/
                }
                if (arrayListForWidth.size() != 0 && arrayListForHeight.size() != 0) {
             /*   System.out.println("FRONT max W :" + Collections.max(arrayListForWidth));
                System.out.println("FRONT max H :" + Collections.max(arrayListForHeight));
                System.out.println("FRONT Megapixel :" + (((Collections.max(arrayListForWidth)) * (Collections.max(arrayListForHeight))) / 1000000));*/
                    return ((Collections.max(arrayListForWidth)) * (Collections.max(arrayListForHeight)) / 1000000);
                }
            }


            return 0;
        } catch (Exception e) {
            logException(e, "CameraInfoFragment_getFrontCamResolution()");
            return 0;
        }

    }


    /**
     * This method fetch the focus mode of camera
     *
     * @return String
     */

    private String getFocusMode() {
        try {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(BACK_CAMERA_ID, info);
            releaseCameraAndPreview();
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                if (camera == null) {
                    camera = Camera.open(BACK_CAMERA_ID);
                }
                Camera.Parameters parameters = camera.getParameters();
                List<String> supportedFocusModes = parameters.getSupportedFocusModes();
                String focusMode = selectFocusMode(supportedFocusModes);
                return focusMode;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logException(e, "CameraInfoFragment_getFocusMode()");
            return "N/A";

        }
        return null;

    }

    /**
     * releaseCameraAndPreview() method is used to release Camera hardware if it is used by another hardware
     */

    private void releaseCameraAndPreview() {
        try {
            if (camera != null) {
                camera.stopPreview();
                camera.release();
                camera = null;
            }
            if (cameraFront != null) {
                cameraFront.stopPreview();
                cameraFront.release();
                cameraFront = null;
            }
        } catch (Exception e) {
            logException(e, "CameraInfoFragment_releaseCameraAndPreview()");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            releaseCameraAndPreview();
        } catch (Exception e) {
            logException(e, "CameraInfoFragment_onDetach()");
        }

    }

    /**
     * This method fetch the focus mode supported by camera
     *
     * @return String
     */

    protected String selectFocusMode(final List<String> supportedFocusModes) {

        try {
            if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_FIXED) && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_INFINITY) && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                return getResources().getString(R.string.txtFocusFIA);
            } else if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_INFINITY) && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_FIXED)) {
                return getResources().getString(R.string.txtFocusFI);
            } else if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO) && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_INFINITY)) {
                return getResources().getString(R.string.txtFocusAI);
            } else if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO) && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_FIXED)) {
                return getResources().getString(R.string.txtFocusAF);
            } else if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                return getResources().getString(R.string.txtFocusAuto);
            } else if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_INFINITY)) {
                return getResources().getString(R.string.txtFocusInfinity);
            } else if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_FIXED)) {
                return getResources().getString(R.string.txtFocusFixed);
            }
            return " ";
        } catch (Exception e) {

            logException(e, "CameraInfoFragment_selectFocusMode()");
            return " ";
        }

    }

    /**
     * This methods gives sensor information
     */
    private void sensorInfo() {
        try {
            //Light sensor
            SensorManager sensorManager = (SensorManager) getActivity().getSystemService(getActivity().SENSOR_SERVICE);
            Sensor light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
            if (light != null) {
                mtvLightSensor.setText(light.getName() + "\n" + light.getVendor() + "\n" + light.getVersion());
            } else {
                mtvLightSensor.setText("NA");
            }

            //profinity sensor
            Sensor proxinity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            if (proxinity != null)

            {
                mtvProximitySensor.setText(proxinity.getName() + "\n" + proxinity.getVendor() + "\n" + proxinity.getVersion());
            } else {
                mtvProximitySensor.setText("NA");
            }

            //Accelerometer sensor
            Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            if (accelerometer != null) {
                mtvAccelerometer.setText(accelerometer.getName() + "\n" + accelerometer.getVendor() + "\n" + accelerometer.getVersion());
            } else {
                mtvAccelerometer.setText("NA");
            }
        } catch (Exception e) {
            logException(e, "CameraInfoFragment_sensorInfo()");
        }

    }


    /**
     * This method will find the camera resolution in pixels
     */
    public float getBackCameraResolutionInMp() {

        try {
            int noOfCameras = Camera.getNumberOfCameras();
            float maxResolution = -1;
            long pixelCount = -1;
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(BACK_CAMERA_ID, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                try {
                    releaseCameraAndPreview();
                    if (camera == null) {
                        camera = Camera.open(BACK_CAMERA_ID);
                    }
                    Camera.Parameters cameraParams = camera.getParameters();
                    for (int j = 0; j < cameraParams.getSupportedPictureSizes().size(); j++) {
                        long pixelCountTemp = cameraParams.getSupportedPictureSizes().get(j).width * cameraParams.getSupportedPictureSizes().get(j).height; // Just changed i to j in this loop
                        if (pixelCountTemp > pixelCount) {
                            pixelCount = pixelCountTemp;
                            maxResolution = ((float) pixelCountTemp) / (1024000.0f);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return maxResolution;
        } catch (Exception e) {
            logException(e, "CameraInfoFragment_getBackCameraResolutionInMp()");
            return -1;
        }

    }

    /**
     * This methods gives sensor information
     */
    public boolean sensorInfoTest(int testId, Context context) {
        try {
            boolean result = false;
            SensorManager sensorManager = (SensorManager) context.getSystemService(getActivity().SENSOR_SERVICE);
            //Light sensor
            switch (testId) {

                case 0:
                    Sensor light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
                    if (light != null) {
                        result = true;
                    } else {
                        result = false;
                    }
                    break;
                case 1:
                    //profinity sensor
                    Sensor proxinity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
                    if (proxinity != null) {
                        result = true;
                    } else {
                        result = false;
                    }
                    //Accelerometer sensor
                    break;
                case 2:
                    Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                    if (accelerometer != null) {
                        result = true;
                    } else {
                        result = false;
                    }
                    break;
            }
            return result;
        } catch (Exception e) {
            logException(e, "CameraInfoFragment_sensorInfoTest()");
            return false;
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
         //   logException(exp, "CameraInfoFragment_logException()");
        }

    }
}
