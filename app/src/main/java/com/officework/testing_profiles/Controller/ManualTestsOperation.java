package com.officework.testing_profiles.Controller;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.util.Log;

import com.officework.R;
import com.officework.activities.MainActivity;
import com.officework.constants.Constants;
import com.officework.fragments.AccelerometerTestFragment;
import com.officework.fragments.BaroMetereFragment;
import com.officework.fragments.BatteryMannualFragment;
import com.officework.fragments.CameraFragment;
import com.officework.fragments.ChargingManualFragment;
import com.officework.fragments.CompassTestFragment;
import com.officework.fragments.DeviceCasingTestFragment;
import com.officework.fragments.DisplayManualFragment;
import com.officework.fragments.EarJackManualFragment;
import com.officework.fragments.FaceDetectionFragment;
import com.officework.fragments.FingerPrintTestFragment;
import com.officework.fragments.FlashTestFragment;
import com.officework.fragments.GpsMapManualFragment;
import com.officework.fragments.HomeButtonManualFragment;
import com.officework.fragments.LightSensorManualFragment;
import com.officework.fragments.ManualTestFragment;
import com.officework.fragments.MicAndSpeakerManualFragment;
import com.officework.fragments.MutitouchManualFragment;
import com.officework.fragments.PhoneShakingManualFragment;
import com.officework.fragments.PowerButtonManualFragment;
import com.officework.fragments.ProximitySensorManualFragment;
import com.officework.fragments.SpeakerManualFragment;
import com.officework.fragments.TitleBarFragment;
import com.officework.fragments.TouchScreenCanvasManualFragment;
import com.officework.fragments.VolumeManualFragment;
import com.officework.interfaces.InterfaceButtonTextChange;
import com.officework.models.AutomatedTestListModel;
import com.officework.testing_profiles.utils.ConstantTestIDs;
import com.officework.utils.WebserviceUrls;
import com.officework.utils.socket.SocketConstants;
import com.officework.utils.socket.SocketHelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Diksha on 10/17/2018.
 */

public class ManualTestsOperation {

    public static Fragment launchScreens(AutomatedTestListModel automatedTestListModel,
                                         String androidId, Context ctx) {

        int test_id = automatedTestListModel.getTest_id();
        if(test_id!=ConstantTestIDs.SPEAKER_MIC  && test_id!=ConstantTestIDs.VOLUME_ID && test_id!=ConstantTestIDs.CAMERA_ID) {
            JSONObject jsonObject = new JSONObject();
            try {

                jsonObject.put(Constants.QRCODEID, MainActivity.qr_code_test);
                jsonObject.put(Constants.UNIQUE_ID, androidId);
                jsonObject.put("TestName", automatedTestListModel.getName());
                jsonObject.put("TestStatus", SocketConstants.IN_PROGRESS);

                jsonObject.put("TestDescription", automatedTestListModel.getTestDes());


                jsonObject.put("TestId", test_id);
                if (automatedTestListModel.getTest_type().equals("Manual1")) {
                    jsonObject.put("TestType", Constants.MANUAL1);
                } else {
                    jsonObject.put("TestType", Constants.MANUAL2);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("test type", automatedTestListModel.getTest_type() + ".." + androidId);
            SocketHelper socketHelper =
                    SocketHelper.getInstance(new SocketHelper.Builder(WebserviceUrls.BaseUrl +
                            "?" + Constants.REQUEST_UNIQUE_ID + "=" + androidId, null));


            socketHelper.emitData(SocketConstants.EVENT_TEST_START, jsonObject);

        }
//        sendTestData(SocketConstants.EVENT_TEST_START, test_id,
//                Constants.IN_PROGRESS, automatedTestListModel.getName(),"",androidId,Constants
//                .MANUAL1);

        Fragment fragment = null;
        MainActivity mainActivity=(MainActivity)ctx;
        FragmentManager fm = mainActivity.getSupportFragmentManager();
        Fragment frag1 = fm.findFragmentById(R.id.headerContainer);
        TitleBarFragment frag = (TitleBarFragment) frag1;
        if (frag != null) {
            frag.setTitleBarVisibility(true);
            frag.setSyntextVisibilty(false);
            frag.setHeaderTitleAndSideIcon(automatedTestListModel.getName()
                    , false, false, 0);
        }

        switch (test_id) {
            case ConstantTestIDs.EAR_PHONE_ID:
                fragment = new EarJackManualFragment();
                return fragment;

            case ConstantTestIDs.VOLUME_ID:
            case ConstantTestIDs.VOLUME_UP:
            case ConstantTestIDs.VOLUME_DOWN:
                fragment = new VolumeManualFragment();
                return fragment;
            case ConstantTestIDs.POWER_ID:
                fragment = new PowerButtonManualFragment(new InterfaceButtonTextChange() {
                    @Override
                    public void onChangeText(int text, boolean showButton) {

                    }
                });
                return fragment;
            case ConstantTestIDs.CHARGING_ID:
                fragment = new ChargingManualFragment(new InterfaceButtonTextChange() {
                    @Override
                    public void onChangeText(int text, boolean showButton) {

                    }
                });
                return fragment;
            case ConstantTestIDs.PROXIMITY_ID:
                fragment = new ProximitySensorManualFragment(new InterfaceButtonTextChange() {
                    @Override
                    public void onChangeText(int text, boolean showButton) {

                    }
                });
                return fragment;
            case ConstantTestIDs.HOME_ID:
                fragment = new HomeButtonManualFragment(new InterfaceButtonTextChange() {
                    @Override
                    public void onChangeText(int text, boolean showButton) {

                    }
                });
                return fragment;
            case ConstantTestIDs.GYROSCOPE_ID:
                fragment = new PhoneShakingManualFragment(new InterfaceButtonTextChange() {
                    @Override
                    public void onChangeText(int text, boolean showButton) {

                    }
                });
                return fragment;

            case ConstantTestIDs.LIGHT_SENSOR_ID:
                fragment = new LightSensorManualFragment(new InterfaceButtonTextChange() {
                    @Override
                    public void onChangeText(int text, boolean showButton) {

                    }
                });
                return fragment;

            case ConstantTestIDs.CAMERA_ID:
            case ConstantTestIDs.FRONT_CAMERA:
            case ConstantTestIDs.BACK_CAMERA:
                fragment = new CameraFragment(new InterfaceButtonTextChange() {
                    @Override
                    public void onChangeText(int text, boolean showButton) {

                    }
                });
                return fragment;
            case ConstantTestIDs.DISPLAY_ID:
            case ConstantTestIDs.WHITE_DISPLAY:
            case ConstantTestIDs.BLACK_DISPLAY:
                fragment = new DisplayManualFragment(new InterfaceButtonTextChange() {
                    @Override
                    public void onChangeText(int text, boolean showButton) {

                    }
                });
                return fragment;
            case ConstantTestIDs.GPS_ID:
                fragment = new GpsMapManualFragment(new InterfaceButtonTextChange() {
                    @Override
                    public void onChangeText(int text, boolean showButton) {

                    }
                });
                return fragment;
            case ConstantTestIDs.SPEAKER_ID:
                fragment = new SpeakerManualFragment(new InterfaceButtonTextChange() {
                    @Override
                    public void onChangeText(int text, boolean showButton) {

                    }
                });
                return fragment;

            case ConstantTestIDs.SPEAKER_MIC:
                fragment = new MicAndSpeakerManualFragment(new InterfaceButtonTextChange() {
                    @Override
                    public void onChangeText(int text, boolean showButton) {

                    }
                });
                return fragment;
            case ConstantTestIDs.TOUCH_SCREEN_ID:
                fragment = new TouchScreenCanvasManualFragment(new InterfaceButtonTextChange() {
                    @Override
                    public void onChangeText(int text, boolean showButton) {

                    }
                });
                return fragment;

            case ConstantTestIDs.MULTI_TOUCH_ID:
                fragment = new MutitouchManualFragment(new InterfaceButtonTextChange() {
                    @Override
                    public void onChangeText(int text, boolean showButton) {

                    }
                });
                return fragment;

            case ConstantTestIDs.DEVICE_CASING_ID:
                fragment = new DeviceCasingTestFragment(new InterfaceButtonTextChange() {
                    @Override
                    public void onChangeText(int text, boolean showButton) {

                    }
                });
                return fragment;

            case ConstantTestIDs.FLASH:
                fragment = new FlashTestFragment(new InterfaceButtonTextChange() {
                    @Override
                    public void onChangeText(int text, boolean showButton) {

                    }
                });
                return fragment;

            case ConstantTestIDs.COMPASS:
                fragment = new CompassTestFragment(new InterfaceButtonTextChange() {
                    @Override
                    public void onChangeText(int text, boolean showButton) {

                    }
                });
                return fragment;

            case ConstantTestIDs.FINGERPRINT:
                fragment = new FingerPrintTestFragment(new InterfaceButtonTextChange() {
                    @Override
                    public void onChangeText(int text, boolean showButton) {

                    }
                });
                return fragment;
            case ConstantTestIDs.ACCELEROMETER:
                fragment = new AccelerometerTestFragment(new InterfaceButtonTextChange() {
                    @Override
                    public void onChangeText(int text, boolean showButton) {

                    }
                });
                return fragment;

            case ConstantTestIDs.Battery:
                fragment = new BatteryMannualFragment(new InterfaceButtonTextChange() {
                    @Override
                    public void onChangeText(int text, boolean showButton) {

                    }
                });
                return fragment;
            case ConstantTestIDs.FaceDetection:
                fragment = new FaceDetectionFragment(new InterfaceButtonTextChange() {
                    @Override
                    public void onChangeText(int text, boolean showButton) {

                    }
                });
                return fragment;
            case ConstantTestIDs.Barometer:
                fragment = new BaroMetereFragment(new InterfaceButtonTextChange() {
                    @Override
                    public void onChangeText(int text, boolean showButton) {

                    }
                });
                return fragment;
            case -1:
                fragment = new ManualTestFragment(new InterfaceButtonTextChange() {
                    @Override
                    public void onChangeText(int text, boolean showButton) {

                    }
                });
                return fragment;

            default:
                fragment = new ManualTestFragment(new InterfaceButtonTextChange() {
                    @Override
                    public void onChangeText(int text, boolean showButton) {

                    }
                });
                return fragment;

        }
    }


}
