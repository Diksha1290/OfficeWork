package com.officework.utils;

import android.content.Context;


import com.officework.R;
import com.officework.activities.MainActivity;
import com.officework.constants.Constants;
import com.officework.constants.JsonTags;
import com.officework.fragments.TitleBarFragment;
import com.officework.models.AutomatedTestListModel;
import com.officework.testing_profiles.Model.ManualTestListModel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Diksha on 11/20/2018.
 */

public class TestObjectsUtils {

    private static TestObjectsUtils testObjectsUtils;
    private static Context mContext;
    public static HashMap<Integer, ArrayList<AutomatedTestListModel>> childTestMap;

    private TestObjectsUtils(){}

    public static TestObjectsUtils getInstance(Context context){
        if(testObjectsUtils== null){
            testObjectsUtils = new TestObjectsUtils();

        }
     //   createMap(context);
        mContext = context;
        return testObjectsUtils;
    }
    public ManualTestListModel initializeDataSet(int position) {
        ManualTestListModel object = null;

        try {

            DeviceInformation deviceInformation = new DeviceInformation();

            boolean isLightSensorExist = deviceInformation.isLightsensorExist(mContext);
            boolean isProximityExist = deviceInformation.isProximityExist(mContext);

            MainActivity mainActivity = (MainActivity) mContext;

            switch (position) {
                case Constants.ManualTestConstant.EAR_JACK:
                    object = new ManualTestListModel(mContext.getResources().getStringArray(R.array.arrSemiAutomaticTestName)[position], Constants.arrSemiAutomaticResources[position],
                            getDiagnoseStatus(position), position, mContext.getResources().getString(R.string.txtManualEarJack_test));
                    break;
                case Constants.ManualTestConstant.VOLUME:
                    object = new ManualTestListModel(mContext.getResources().getStringArray(R.array.arrSemiAutomaticTestName)[position], Constants.arrSemiAutomaticResources[position],
                            getDiagnoseStatus(position), position, mContext.getResources().getString(R.string.txtManualVolume_Btn));
                    break;
                case Constants.ManualTestConstant.POWER:
                    object = new ManualTestListModel(mContext.getResources().getStringArray(R.array.arrSemiAutomaticTestName)[position], Constants.arrSemiAutomaticResources[position],
                            getDiagnoseStatus(position), position, mContext.getResources().getString(R.string.txtManualPower_test));
                    break;
                case Constants.ManualTestConstant.CHARGING:
                    object = new ManualTestListModel(mContext.getResources().getStringArray(R.array.arrSemiAutomaticTestName)[position], Constants.arrSemiAutomaticResources[position],
                            getDiagnoseStatus(position), position, mContext.getResources().getString(R.string.txtTitleCharging));
                    break;
                case Constants.ManualTestConstant.PROXIMITY:
                    if (isProximityExist) {
                        object = new ManualTestListModel(mContext.getResources().getStringArray(R.array.arrSemiAutomaticTestName)[position], Constants.arrSemiAutomaticResources[position],
                                getDiagnoseStatus(position), position, mContext.getResources().getString(R.string.txtManualProximitySensor_test));


                    }else {
                        mainActivity.map.put("proximity",Constants.TEST_FAILED);

                    }
                    break;
                case Constants.ManualTestConstant.HOME:
                    object = new ManualTestListModel(mContext.getResources().getStringArray(R.array.arrSemiAutomaticTestName)[position], Constants.arrSemiAutomaticResources[position],
                            getDiagnoseStatus(position), position, mContext.getResources().getString(R.string.txtManualHomeButton_test));
                    break;
                case Constants.ManualTestConstant.GYROSCOPE:
                    object = new ManualTestListModel(mContext.getResources().getStringArray(R.array.arrSemiAutomaticTestName)[position], Constants.arrSemiAutomaticResources[position],
                            getDiagnoseStatus(position), position, mContext.getResources().getString(R.string.txtManualGyroscope_test));
                    break;
                case Constants.ManualTestConstant.LIGHT_SENSOR:
                    if (isLightSensorExist) {
                        object = new ManualTestListModel(mContext.getResources().getStringArray(R.array.arrSemiAutomaticTestName)[position], Constants.arrSemiAutomaticResources[position],
                                getDiagnoseStatus(position), position, mContext.getResources().getString(R.string.txtManualLightSensor_test));


                    }else {

                        mainActivity.map.put("light sensor",Constants.TEST_FAILED);
                    }
                    break;
            }
            if (object!= null && (object.getIsTestSuccess() == Constants.TEST_PASS || object.getIsTestSuccess() == Constants.TEST_FAILED) && !mainActivity.shouldAdd) {
                mainActivity.map.put(object.getName(), object.getIsTestSuccess());
            }



            if(mainActivity.map.size() == (Constants.arrSemiAutomaticResources.length+Constants.arrManualTotalTestResources.length) && !mainActivity.shouldAdd){
                mainActivity.shouldAdd= true;
                TitleBarFragment fragment = (TitleBarFragment) ((MainActivity) mContext).getSupportFragmentManager().findFragmentById(R.id.headerContainer);
                if (fragment != null) {
                    fragment.openManualSummary();
             //    fragment.showSyncDialog();
                    fragment.setSyntextVisibilty(true);
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return object;
    }

    /**
     * get diagnose result from shared preferences
     *
     * @param testID
     * @return
     */
    public int getDiagnoseStatus(int testID) {
        int testStatus = Constants.TEST_IN_QUEUE;
        try {

            switch (testID) {

                case 0:
                    testStatus = checkSingleHardware(JsonTags.MMR_18.name());
                    break;
                case 1:
                    testStatus = checkMutipleHardware(JsonTags.MMR_20.name(), JsonTags.MMR_21.name());
                    break;
                case 2:
                    testStatus = checkSingleHardware(JsonTags.MMR_45.name());
                    break;
                case 3:
                    testStatus = checkSingleHardware(JsonTags.MMR_44.name());
                    break;

                case 4:
                    testStatus = checkSingleHardware(JsonTags.MMR_23.name());
                    break;
                case 5:
                    testStatus = checkSingleHardware(JsonTags.MMR_26.name());
                    break;
                case 6:
                    testStatus = checkSingleHardware(JsonTags.MMR_40.name());
                    break;
                case 7:
                    testStatus = checkSingleHardware(JsonTags.MMR_28.name());
                    break;
                case 8:
                    testStatus = checkMutipleHardware(JsonTags.MMR_37.name(), JsonTags.MMR_38.name());
                    break;
                case 9:
                    testStatus = checkMutipleHardware(JsonTags.MMR_47.name(), JsonTags.MMR_46.name());
                    break;
                case 10:
                    testStatus = checkSingleHardware(JsonTags.MMR_31.name());
                    break;

                case 11:
                    testStatus = checkSingleHardware(JsonTags.MMR_32.name());
                    break;
                case 12:
                    testStatus = checkSingleHardware(JsonTags.MMR_33.name());
                    break;

                case 13:
                    testStatus = checkSingleHardware(JsonTags.MMR_24.name());
                    break;
                case 14:
                    testStatus = checkSingleHardware(JsonTags.MMR_22.name());
                    break;

                case 15:
                    testStatus = checkSingleHardware(JsonTags.MMR_55.name());
                    break;

            /*case 11:
                testStatus = checkSingleHardware(JsonTags.MMR_30.name());
                break;*/


            }
            return testStatus;
        } catch (Exception e) {

            return testStatus;
        }

    }

    /**
     * check result for single test
     *
     * @param pref_Key
     * @return
     */
    public int checkSingleHardware(String pref_Key) {
        int testStatus = -1;
        try {

            Utilities utils = Utilities.getInstance(mContext);
            if (utils.getPreferenceInt(mContext, pref_Key, 0) == -1 ) {
                testStatus = Constants.TEST_IN_QUEUE;
            } else if (utils.getPreferenceInt(mContext, pref_Key, 0) == 0) {
                testStatus = Constants.TEST_FAILED;
            }else if(utils.getPreferenceInt(mContext, pref_Key, 0) == -2){
                testStatus = Constants.TEST_NOT_EXIST;
            } else {
                testStatus = Constants.TEST_PASS;
            }
            return testStatus;
        } catch (Exception e) {

            return testStatus;
        }

    }

    /**
     * check result for combine test
     * EXP volume button test
     * Camera TEST
     *
     * @param pref_Key_1
     * @param pref_Key_2
     * @return
     */
    public int checkMutipleHardware(String pref_Key_1, String pref_Key_2) {
        int testStatus = -1;
        try {

            if (Utilities.getInstance(mContext).getPreferenceInt(mContext, pref_Key_1, 0) == -1 && Utilities.getInstance(mContext).getPreferenceInt(mContext, pref_Key_2, 0) == -1) {
                testStatus = Constants.TEST_IN_QUEUE;
            } else if (Utilities.getInstance(mContext).getPreferenceInt(mContext, pref_Key_1, 0) == 0 && Utilities.getInstance(mContext).getPreferenceInt(mContext, pref_Key_2, 0) == 0) {
                testStatus = Constants.TEST_FAILED;
            } else if (Utilities.getInstance(mContext).getPreferenceInt(mContext, pref_Key_1, 0) == 1 && Utilities.getInstance(mContext).getPreferenceInt(mContext, pref_Key_2, 0) == 1) {
                testStatus = Constants.TEST_PASS;
            } else if (Utilities.getInstance(mContext).getPreferenceInt(mContext, pref_Key_1, 0) == 1 || Utilities.getInstance(mContext).getPreferenceInt(mContext, pref_Key_2, 0) == 0) {
                testStatus = Constants.TEST_IN_PROGRESS;
            } else if (Utilities.getInstance(mContext).getPreferenceInt(mContext, pref_Key_1, 0) == 0 || Utilities.getInstance(mContext).getPreferenceInt(mContext, pref_Key_2, 0) == 1) {
                testStatus = Constants.TEST_IN_PROGRESS;
            }/* else {
                testStatus = AsyncConstant.TEST_PASS;
            }*/
            return testStatus;
        } catch (Exception e) {

            return testStatus;

        }}




       /* public static  void createMap(Context context){
             childTestMap = new HashMap<>();

            ArrayList<AutomatedTestListModel> automatedTestListModels = new ArrayList<>();
            AutomatedTestListModel automatedTestListModel = new AutomatedTestListModel();
            automatedTestListModel.setName("Volume Up");
            automatedTestListModel.setResource(R.drawable.ic_volume_svg);
            automatedTestListModel.setTest_id(JsonTags.MMR_20.name());
            automatedTestListModels.add(automatedTestListModel);

             automatedTestListModel = new AutomatedTestListModel();
            automatedTestListModel.setName("Volume Down");
            automatedTestListModel.setResource(R.drawable.ic_volume_svg);
            automatedTestListModel.setTest_id(JsonTags.MMR_21.name());
            automatedTestListModels.add(automatedTestListModel);

            childTestMap.put(1,automatedTestListModels);

           automatedTestListModels = new ArrayList<>();
            automatedTestListModel = new AutomatedTestListModel();
            automatedTestListModel.setName("Front Camera");
            automatedTestListModel.setResource(R.drawable.ic_volume_svg);
            automatedTestListModel.setTest_id(JsonTags.MMR_37.name());
            automatedTestListModels.add(automatedTestListModel);

            automatedTestListModel = new AutomatedTestListModel();
            automatedTestListModel.setName("Back Camera");
            automatedTestListModel.setResource(R.drawable.ic_volume_svg);
            automatedTestListModel.setTest_id(JsonTags.MMR_38.name());
            automatedTestListModels.add(automatedTestListModel);

            childTestMap.put(8,automatedTestListModels);

            automatedTestListModels = new ArrayList<>();
            automatedTestListModel = new AutomatedTestListModel();
            automatedTestListModel.setName("White Display");
            automatedTestListModel.setResource(R.drawable.ic_volume_svg);
            automatedTestListModel.setTest_id(JsonTags.MMR_47.name());
            automatedTestListModels.add(automatedTestListModel);

            automatedTestListModel = new AutomatedTestListModel();
            automatedTestListModel.setName("Black Display");
            automatedTestListModel.setResource(R.drawable.ic_volume_svg);
            automatedTestListModel.setTest_id(JsonTags.MMR_46.name());
            automatedTestListModels.add(automatedTestListModel);

            childTestMap.put(9,automatedTestListModels);



        }*/


}
