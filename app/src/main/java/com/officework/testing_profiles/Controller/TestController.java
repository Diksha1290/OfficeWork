package com.officework.testing_profiles.Controller;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.officework.activities.MainActivity;
import com.officework.constants.AsyncConstant;
import com.officework.constants.Constants;
import com.officework.fragments.CompassTestFragment;
import com.officework.interfaces.FaceBiometricErrorInterface;
import com.officework.interfaces.WebServiceCallback;
import com.officework.models.AutomatedTestListModel;
import com.officework.testing_profiles.Model.TestRecevier;
import com.officework.testing_profiles.realmDatabase.operationManager.RealmOperations;
import com.officework.testing_profiles.utils.ConstantTestIDs;
import com.officework.utils.Utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;

/**
 * Created by Diksha on 10/16/2018.
 */

public class TestController extends Observable implements WebServiceCallback, FaceBiometricErrorInterface {


    public static ArrayList<Integer> nextPressList = new ArrayList<>();
    static TestRecevier testRecevier;
    private static String LOG_TAG = "TestController";
    private static Utilities utils;
    private static RealmOperations realmOperations;
    private static TestController testController;
    private static Context mContext;
    boolean testReults = false;
    int testId;
   static FaceBiometricErrorInterface faceBiometricErrorInterface;
    List<Integer> testList = new ArrayList<>();
    private AutomatedTestListModel testModel;
    private static Object object;
    private TestController(Context context) {


        testRecevier = new TestRecevier(context, this,this);

    }

    public static TestController getInstance(Context context,Object... o) {
        mContext = context;
        if(o.length>0) {
            object = o[0];
            if(object instanceof FaceBiometricErrorInterface){
                faceBiometricErrorInterface=(FaceBiometricErrorInterface)object;
            }
        }

        if (testController == null) {
            testController = new TestController(context);
            realmOperations = new RealmOperations();

        }
        utils = Utilities.getInstance(context);

        return testController;
    }


    public void setInstance() {
        testController = null;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void performOperation(final int testId) {
        try {
            testList = Arrays.asList(MainActivity.id_array);

            if (!testList.contains(testId)) {
                Utilities.getInstance(mContext).compare_UpdatePreferenceInt(mContext,
                        "MMR_" + testId, -2);
                return;
            }
            testModel = realmOperations.fetchTestType(testId);
            if (testModel != null) {
                switch (testModel.getTest_type()) {
                    case Constants.AUTOMATE:
                        Handler pauseTimeHandler = new Handler(Looper.getMainLooper());
                        pauseTimeHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                utils.automatedTestAsync((Activity) mContext, mContext, testId,
                                        TestController.this);
                            }
                        }, Constants.PAUSE_TIME);
                        break;
                    case Constants.MANUAL:
                        switch (testId) {
//                       case ConstantTestIDs.EAR_PHONE_ID:
//                           testRecevier.registerEarJackReceiver(testId);
//                           break;
//                       case ConstantTestIDs.VOLUME_ID:
//                           realmOperations.updateTestResult(ConstantTestIDs.VOLUME_DOWN,
//                           AsyncConstant.TEST_IN_QUEUE);
//                           realmOperations.updateTestResult(ConstantTestIDs.VOLUME_UP,
//                           AsyncConstant.TEST_IN_QUEUE);
//
//                           testRecevier.registerVolumeRecevier(testId);
//
//                           break;
//                       case ConstantTestIDs.POWER_ID:
//                           testRecevier.registerPowerRecevier(testId);
//
//                           break;
//                       case ConstantTestIDs.CHARGING_ID:
//                           testRecevier.registerChargingRecevier(testId);
//
//                           break;
//                       case ConstantTestIDs.PROXIMITY_ID:
//                           testRecevier.registerProximityRecevier(testId);
//                           break;
//                       case ConstantTestIDs.HOME_ID:
//                           testRecevier.registerHomeRecevier(testId);
//                           break;
//                       case ConstantTestIDs.GYROSCOPE_ID:
//                           testRecevier.registerPhoneShakeListener(testId);
//                           break;
////                       case ConstantTestIDs.LIGHT_SENSOR_ID:
////                           testRecevier.registerLightSensorRecevier(testId);
//                           break;
                            case ConstantTestIDs.FLASH:
                                testRecevier.registerFlashReciever(testId);
                                break;
                            case ConstantTestIDs.COMPASS:
                                // testRecevier.registerCompassReciever(testId,
                                // compassTestFragment);
                                break;

                            case ConstantTestIDs.ACCELEROMETER:
//                           testRecevier.registerAcclerometerReciever(testId);
                                break;
                            case ConstantTestIDs.Barometer:
                                testRecevier.registerBarometerReciever(testId);
                                break;
                            case ConstantTestIDs.CAMERA_ID:
                                Utilities.getInstance(mContext).compare_UpdatePreferenceInt(mContext,"MMR_"+ConstantTestIDs.FRONT_CAMERA,-1);
                                Utilities.getInstance(mContext).compare_UpdatePreferenceInt(mContext,"MMR_"+ConstantTestIDs.BACK_CAMERA,-1);

                                realmOperations.updateTestResult(ConstantTestIDs.FRONT_CAMERA, AsyncConstant.TEST_SKIP);
                                realmOperations.updateTestResult(ConstantTestIDs.BACK_CAMERA, AsyncConstant.TEST_SKIP);
                                break;
                            case ConstantTestIDs.DISPLAY_ID:
                                realmOperations.updateTestResult(ConstantTestIDs.WHITE_DISPLAY,
                                        AsyncConstant.TEST_IN_QUEUE);
                                realmOperations.updateTestResult(ConstantTestIDs.BLACK_DISPLAY,
                                        AsyncConstant.TEST_IN_QUEUE);
                                break;
                            case ConstantTestIDs.GPS_ID:
                                break;
                            case ConstantTestIDs.SPEAKER_ID:
                                break;
                            case ConstantTestIDs.MIC_ID:
                                break;
                            case ConstantTestIDs.TOUCH_SCREEN_ID:
                                break;
                            case ConstantTestIDs.MULTI_TOUCH_ID:
                                break;
                            case ConstantTestIDs.DEVICE_CASING_ID:
                                break;
                            case ConstantTestIDs.FaceDetection:
                                break;
                            case ConstantTestIDs.FINGERPRINT:
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                                    testRecevier.registerBioMetricPrintReciever(testId);
//                                } else {
                                    testRecevier.registerFingerPrintReciever(testId);
                              //  }

                                break;
                        }
                        break;
                    case Constants.MANUAL1:
                        switch (testId) {
                            case ConstantTestIDs.EAR_PHONE_ID:
                                testRecevier.registerEarJackReceiver(testId);
                                break;
                            case ConstantTestIDs.VOLUME_ID:
//                                realmOperations.updateTestResult(ConstantTestIDs.VOLUME_DOWN,
//                                        AsyncConstant.TEST_IN_QUEUE);
//                                realmOperations.updateTestResult(ConstantTestIDs.VOLUME_UP,
//                                        AsyncConstant.TEST_IN_QUEUE);
                                testRecevier.registerVolumeRecevier(testId);

                                break;
                            case ConstantTestIDs.POWER_ID:
                                testRecevier.registerPowerRecevier(testId);

                                break;
                            case ConstantTestIDs.CHARGING_ID:
                                testRecevier.registerChargingRecevier(testId);

                                break;
                            case ConstantTestIDs.PROXIMITY_ID:
                                testRecevier.registerProximityRecevier(testId);
                                break;
                            case ConstantTestIDs.HOME_ID:
                                testRecevier.registerHomeRecevier(testId);
                                break;
                            case ConstantTestIDs.GYROSCOPE_ID:
                                testRecevier.registerPhoneShakeListener(testId);
                                break;
                            case ConstantTestIDs.Battery:
                                testRecevier.batteryChargingStateReceiver(testId);
                                break;
                        }
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void performOperation(int compass, CompassTestFragment compassTestFragment) {
        testRecevier.registerCompassReciever(compass, compassTestFragment);
    }


    @Override
    public void onServiceResponse(boolean serviceStatus, String response, int callbackID) {
        try {
            int responseStatus;
            if (serviceStatus) {
                responseStatus = AsyncConstant.TEST_PASS;

            } else {
                responseStatus = AsyncConstant.TEST_FAILED;
            }
            if (callbackID != ConstantTestIDs.FRONT_CAMERA && callbackID != ConstantTestIDs.BACK_CAMERA && callbackID != ConstantTestIDs.VOLUME_DOWN && callbackID != ConstantTestIDs.VOLUME_UP && callbackID != ConstantTestIDs.SPEAKER_ID) {
                if (responseStatus == 0 || responseStatus == 1) {
                    if (!nextPressList.contains(callbackID))
                        nextPressList.add(callbackID);
                }
            }
            testId = callbackID;

            // if(responseStatus!=0){
            ArrayList<Integer> subTestId = realmOperations.fetchParentTestId(callbackID);

            if (subTestId != null && subTestId.size() > 0) {
                AutomatedTestListModel automatedTestListModel1 =
                        realmOperations.fetchTestType(subTestId.get(0));
                AutomatedTestListModel automatedTestListModel2 =
                        realmOperations.fetchTestType(subTestId.get(1));
                int status = checkMultipleTestStatus(automatedTestListModel1.getIsTestSuccess(),
                        automatedTestListModel2.getIsTestSuccess());
                testModel = realmOperations.updateTestResult(callbackID, status);
                setChanged();
                notifyObservers(testModel);
            } else {
                //  boolean isCHildTest = realmOperations.fetchIsChildTest(callbackID);
                testModel = realmOperations.updateTestResult(callbackID, responseStatus);
                testModel.setSensorValue(response);
                testModel.setmAzimuth(response);

                setChanged();
                notifyObservers(testModel);


            }
//              if(responseStatus==1){
//            switch (callbackID) {
//                case ConstantTestIDs.GYROSCOPE_ID:
//                    unRegisterHomeAndCharging();
//                    break;
//                case ConstantTestIDs.PROXIMITY_ID:
//                    unregisterProximity();
//                    break;
//            }
//            }


        } catch (Exception e) {

            Log.e(LOG_TAG, e.getMessage());
        }

    }

//    public int checkMultipleTestStatus(int isTestStatus1, int isTestStatus2) {
//
//        if (isTestStatus1 == AsyncConstant.TEST_PASS && isTestStatus2 == AsyncConstant.TEST_PASS) {
//            return AsyncConstant.TEST_PASS;
//        } else if ((isTestStatus1 == 1 && isTestStatus1 == -2) || (isTestStatus1 == -2 && isTestStatus2 == 1)) {
//            return 1;
//        } else if (isTestStatus1 == -1 && isTestStatus2 == -1) {
//            return -1;
//        } else {
//            return AsyncConstant.TEST_FAILED;
//        }
//    }



    public int checkMultipleTestStatus(int isTestStatus1, int isTestStatus2) {

        if(isTestStatus1 == AsyncConstant.TEST_PASS && isTestStatus2 == AsyncConstant.TEST_PASS){
            return AsyncConstant.TEST_PASS;
        }
        else if((isTestStatus1==1 && isTestStatus1 ==-2) || (isTestStatus1==-2&&isTestStatus2 ==1)){
            return 1;
        }
        else if(isTestStatus1 == -1 && isTestStatus2 == -1 ){
            return 0;
        }
        else {
            return AsyncConstant.TEST_FAILED;
        }
    }


    public void removeAllRegisteredReceviers() {
        testRecevier.unRegisterReceviers();
    }

    public void unRegisterHomeAndCharging() {
        testRecevier.unregisterHomeandCharging();
    }

    public void unregisterGyroScope2() {
        testRecevier.unregisterGyroScope2();
    }

    public void unRegisterCharging() {
        testRecevier.unRegisterCharging();
    }

    public void unRegisterHome() {
        testRecevier.unRegisterHome();
    }

    public void unRegisterPowerButton() {
        testRecevier.unRegisterPowerButton();
    }

    public void unRegisterEarJack() {
        testRecevier.unRegisterEarjackReciever();
    }

    public void unregisterProximity() {
        testRecevier.unregistrProximity();
    }

    public void unregisterLightSensor() {
        testRecevier.unregisterLightSensor();
    }

    public void unregisterCompass() {
        testRecevier.unregisterCompass();
    }

    public void unRegisterSensor(int testId) {
        switch (testId) {
            case ConstantTestIDs.GYROSCOPE_ID:
                testRecevier.unregisterGyroScope2();
                break;
            case ConstantTestIDs.PROXIMITY_ID:
                testRecevier.unregistrProximity();
                break;
            case ConstantTestIDs.EAR_PHONE_ID:
                testRecevier.unRegisterEarjackReciever();
                break;
            case ConstantTestIDs.HOME_ID:
                testRecevier.unRegisterHome();
                break;
//            case ConstantTestIDs.POWER_ID:
//                testRecevier.unRegisterPowerButton();
//                break;

            case ConstantTestIDs.CHARGING_ID:
                testRecevier.unRegisterCharging();
                break;
            case ConstantTestIDs.Battery:
                testRecevier.unRegisterBattery();
                break;
        }

    }

    public void unregisterFingerPrintSensor() {
        testRecevier.unregisterFingerPrintSensor();
    }


    public void unregisterBattery() {
        testRecevier.unRegisterBattery();
    }

    @Override
    public void ErrorData(int error_code, CharSequence errorMessage) {
        faceBiometricErrorInterface.ErrorData(error_code,errorMessage);
    }




    public void saveSkipResponse( int  responseStatus, int callbackID) {
        try {
//            Utilities.getInstance(mContext).compare_UpdatePreferenceInt(mContext,"MMR_"+callbackID,responseStatus);

            if(callbackID!=ConstantTestIDs.FRONT_CAMERA&&callbackID!=ConstantTestIDs.BACK_CAMERA&&callbackID!=ConstantTestIDs.VOLUME_DOWN&&callbackID!=ConstantTestIDs.VOLUME_UP&&callbackID!=ConstantTestIDs.WHITE_DISPLAY&&callbackID!=ConstantTestIDs.BLACK_DISPLAY&&callbackID!=ConstantTestIDs.SPEAKER_ID&&callbackID!=ConstantTestIDs.MIC_ID){
                if (!nextPressList.contains(callbackID))
                    nextPressList.add(callbackID);
            }
            testId =callbackID;
            ArrayList<Integer> subTestId = realmOperations.fetchParentTestId(callbackID);
            if(subTestId!= null && subTestId.size()>0) {
                AutomatedTestListModel automatedTestListModel1 = realmOperations.fetchTestType(subTestId.get(0));
                AutomatedTestListModel automatedTestListModel2 = realmOperations.fetchTestType(subTestId.get(1));
                int status =checkMultipleTestStatus(automatedTestListModel1.getIsTestSuccess(), automatedTestListModel2.getIsTestSuccess());
                testModel =  realmOperations.updateTestResult(callbackID,status);
                Utilities.getInstance(mContext).compare_UpdatePreferenceInt(mContext,"MMR_"+callbackID,status);
                setChanged();
                notifyObservers(testModel);
            }
            else {
                Utilities.getInstance(mContext).compare_UpdatePreferenceInt(mContext,"MMR_"+callbackID,responseStatus);
                testModel = realmOperations.updateTestResult(callbackID, responseStatus);
                testModel.setIsTestSuccess(responseStatus);
//                SubTestMapModel subTestMapModel = realmOperations.fetchchildParentTestId(callbackID);
//                if(subTestMapModel != null) {
//                    //  boolean isCHildTest = realmOperations.fetchIsChildTest(callbackID);
//                    ArrayList<Integer> subTestId1 = realmOperations.fetchParentTestId(subTestMapModel.getTest_id());
//                    if(subTestId1!= null && subTestId1.size()>0) {
//                        AutomatedTestListModel automatedTestListModel1 = realmOperations.fetchTestType(subTestId1.get(0));
//                        AutomatedTestListModel automatedTestListModel2 = realmOperations.fetchTestType(subTestId1.get(1));
//
//                        int parent_status =  checkMultipleTestStatus(automatedTestListModel1.getIsTestSuccess(), automatedTestListModel2.getIsTestSuccess());
//
//                        testModel = realmOperations.updateTestResult(subTestMapModel.getTest_id(), parent_status);
//                        Utilities.getInstance(mContext).compare_UpdatePreferenceInt(mContext,"MMR_"+testModel.getTest_id(),parent_status);
//                        testModel.setIsTestSuccess(parent_status);
//                    }
                setChanged();
                notifyObservers(testModel);
                //   }
//

            }
            //              if(responseStatus==1){
            //            switch (callbackID) {
            //                case ConstantTestIDs.GYROSCOPE_ID:
            //                    unRegisterHomeAndCharging();
            //                    break;
            //                case ConstantTestIDs.PROXIMITY_ID:
            //                    unregisterProximity();
            //                    break;
            //            }
            //            }


        } catch (Exception e) {
            Log.e(LOG_TAG,e.getMessage());
        }

    }



}
