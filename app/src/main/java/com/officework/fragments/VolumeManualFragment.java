package com.officework.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.os.CountDownTimer;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.officework.R;
import com.officework.activities.MainActivity;
import com.officework.base.BaseFragment;
import com.officework.constants.AsyncConstant;
import com.officework.constants.Constants;
import com.officework.constants.JsonTags;
import com.officework.customViews.IconView;
import com.officework.interfaces.InterfaceAlertDissmiss;
import com.officework.interfaces.TimerDialogInterface;
import com.officework.models.AutomatedTestListModel;
import com.officework.testing_profiles.Controller.TestController;
import com.officework.testing_profiles.realmDatabase.operationManager.RealmOperations;
import com.officework.testing_profiles.utils.ConstantTestIDs;
import com.officework.utils.ExceptionLogUtils;
import com.officework.utils.ManualDataStable;
import com.officework.utils.TestResultUpdateToServer;
import com.officework.utils.Utilities;
import com.officework.utils.socket.SocketConstants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by Girish on 8/9/2016.
 */
@SuppressLint("ValidFragment")
public class VolumeManualFragment extends BaseFragment implements Observer , TimerDialogInterface {
    View view;
    Utilities utils;
    Context ctx;
    TextView textView;
    IconView mImgViewVolumeUp, mImgViewVolumeDown;
    ConstraintLayout volumeLayout;
    boolean isVolIncrease = false, isVolDecrease = false;
    EarJackManualFragment earJackManualFragment;
    ManualDataStable manualDataStable;
    CountDownTimer cTimer = null;
    boolean isDialogShown = false;
    AlertDialog alert11;
    AlertDialog.Builder alertDialog;
    boolean isTestPerformed = false;
    int SERVICE_REQUEST_ID = -1;
    TestResultUpdateToServer testResultUpdateToServer;
    boolean isTestPerform = false;
    String[] progressArray = {Constants.IN_PROGRESS, Constants.IN_PROGRESS};
    private TestController testController;

    public VolumeManualFragment() {
    }

    @Override
    public void onStop() {


        super.onStop();
    }

    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        try {
            if (view == null) {
                view = inflater.inflate(R.layout.fragment_manual_volume, null);
                utils = Utilities.getInstance(getActivity());
                manualDataStable = new ManualDataStable(null);
                earJackManualFragment = new EarJackManualFragment();
                ctx = getActivity();
                alertDialog = new AlertDialog.Builder(
                        ctx);
                alert11 = alertDialog.create();
               // Crashlytics.getInstance().log(FragmentTag.VOLUME_BUTTON_FRAGMENT.name());
                testResultUpdateToServer = new TestResultUpdateToServer(utils, ctx, getActivity());

                testController = TestController.getInstance(getActivity());
                testController.addObserver(this);
                testController.performOperation(ConstantTestIDs.VOLUME_ID);

                initViews();
            }
            return view;
        } catch (Exception e) {
            logException(e, "VolumeManualFragment_initUI()");
            return null;
        }

    }

    /**
     * initialize view
     */
    private void initViews() {
        try {
            textView = view.findViewById(R.id.tvVolumeMessge);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                textView.setText(Html.fromHtml(getResources().getString(R.string.txtTitleVolume_Btn), Html.FROM_HTML_MODE_COMPACT));
            } else {
                textView.setText(Html.fromHtml(getResources().getString(R.string.txtTitleVolume_Btn)));
            }
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.isVolDecrease = false;
            mainActivity.isVolIncrease = false;
            mainActivity.onChangeText(R.string.textSkip, true);
            mImgViewVolumeUp = (IconView) view.findViewById(R.id.imgViewVolumeUp);
            mImgViewVolumeDown = (IconView) view.findViewById(R.id.imgViewVolumeDown);
            volumeLayout = (ConstraintLayout) view.findViewById(R.id.volumeLayout);
            timer(ctx,false,ConstantTestIDs.VOLUME_ID,VolumeManualFragment.this);
        } catch (Exception e) {
            logException(e, "VolumeManualFragment_initViews()");
        }
        try {
            JSONArray jsonArray = createVolumeJsonArray();

            sendTestDataWithSubArray(SocketConstants.EVENT_TEST_START, Constants.IVOLUME,
                    Constants.IN_PROGRESS, Constants.VOLUME_BUTTONS, "",
                    utils.getPreference(getActivity(), Constants.ANDROID_ID, ""),
                    MainActivity.qr_code_test, Constants.MANUAL1, jsonArray);

        } catch (Exception e) {
            e.printStackTrace();
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
//                fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string.txtManualVolume_Btn), false, false, 0);
                //nextButtonHandler = new Handler();
                if (Constants.isBackButton == true) {
                    Constants.isBackButton = false;
                    if ((manualDataStable.checkSingleHardware(JsonTags.MMR_19.name(), ctx, utils) == 1) && (manualDataStable.checkSingleHardware(JsonTags.MMR_20.name(), ctx, utils) == 1)) {
                        Constants.isBackButton = false;
                    } else {
                        Constants.isBackButton = false;
                    }
                } else {
                    if (isTestPerform) {

                        Constants.isManualIndividual = false;

                      onNextPress();

                    } else {
                    }
                }
            }

        } catch (Exception e) {
            logException(e, "VolumeManualFragment_onResume()");
        }

        super.onResume();
    }

    /**
     * onPause
     */
    @Override
    public void onPause() {
        try {
        } catch (Exception e) {
            logException(e, "VolumeManualFragment_onPause()");
        }

        super.onPause();
    }

    private JSONArray createVolumeJsonArray() {
        JSONArray array = new JSONArray();
        String[] arr_volume = getResources().getStringArray(R.array.Volume);

        int[] volumeArray = {ConstantTestIDs.VOLUME_UP, ConstantTestIDs.VOLUME_DOWN};
        for (int index = 0; index < arr_volume.length; index++) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("TestName", arr_volume[index]);
                jsonObject.put("TestId",volumeArray[index]);
                jsonObject.put("TestStatus", progressArray[index]);
                array.put(jsonObject);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return array;
    }

    /**
     * Called when test is completed or user click on skip button
     */
    public void onNextPress() {
        try {
            RealmOperations  realmOperations = new RealmOperations();
            timer(ctx,true,ConstantTestIDs.VOLUME_ID,null);
            MainActivity activity = (MainActivity) getActivity();
            if (Constants.isSkipButton) {
                isTestPerform = true;
               // updateTestValues(false);
                if (activity.isVolDecrease) {

                    progressArray[0] = Constants.FAILED;
                    JSONArray jsonArray = createVolumeJsonArray();
                    sendTestDataWithSubArray(SocketConstants.EVENT_TEST_END, Constants.IVOLUME,
                            Constants.FAILED, Constants.VOLUME_BUTTONS, "",
                            utils.getPreference(getActivity(),
                            Constants.ANDROID_ID, ""),
                            MainActivity.qr_code_test, Constants.MANUAL1, jsonArray);

                } else if (activity.isVolIncrease) {

                    progressArray[1] = Constants.FAILED;
                    JSONArray jsonArray = createVolumeJsonArray();
                    sendTestDataWithSubArray(SocketConstants.EVENT_TEST_END, Constants.IVOLUME,
                            Constants.FAILED, Constants.VOLUME_BUTTONS, "",
                            utils.getPreference(getActivity(),
                            Constants.ANDROID_ID, ""),
                            MainActivity.qr_code_test, Constants.MANUAL1, jsonArray);

                }
                if (isVolIncrease && isVolDecrease) {

                    JSONArray jsonArray = createVolumeJsonArray();

                    sendTestDataWithSubArray(SocketConstants.EVENT_TEST_END, Constants.IVOLUME,
                            Constants.COMPLETED, Constants.VOLUME_BUTTONS, "",
                            utils.getPreference(getActivity(),
                            Constants.ANDROID_ID, ""),
                            MainActivity.qr_code_test, Constants.MANUAL1, jsonArray);

                } else if (!activity.isVolIncrease && !activity.isVolDecrease) {
                    progressArray[0] = Constants.FAILED;
                    progressArray[1] = Constants.FAILED;

                    JSONArray jsonArray = createVolumeJsonArray();
                    RealmOperations parseRemoteToLocal=new RealmOperations();
                    AutomatedTestListModel automatedTestListModel=parseRemoteToLocal.fetchTestType(Constants.IVOLUME);
                    sendTestDataWithSubArray(SocketConstants.EVENT_TEST_END, Constants.IVOLUME, Constants.FAILED, Constants.VOLUME_BUTTONS, "", utils.getPreference(getActivity(),
                            Constants.ANDROID_ID, automatedTestListModel.getTestDes()),
                            MainActivity.qr_code_test, Constants.MANUAL1, jsonArray);


//                    testController.saveSkipResponse(-1,ConstantTestIDs.VOLUME_UP);
//                    testController.saveSkipResponse(-1,ConstantTestIDs.VOLUME_DOWN);



                }

                if(!isVolDecrease && !isVolIncrease){
//                    utils.showToast(ctx, getResources().getString(R.string.txtManualFail));
                    realmOperations.updateTestResult(ConstantTestIDs.VOLUME_UP, AsyncConstant.TEST_SKIP);
                    realmOperations.updateTestResult(ConstantTestIDs.VOLUME_DOWN, AsyncConstant.TEST_SKIP);
                    mImgViewVolumeDown.setImageDrawable(getResources().getDrawable(R.drawable.ic_volume_down_green_svg_168), false, getActivity());
                    mImgViewVolumeUp.setImageDrawable(getResources().getDrawable(R.drawable.ic_volume_up_green_svg_168), false, getActivity());

                }else if(isVolDecrease && !isVolIncrease)
                {
//                    utils.showToast(ctx, getResources().getString(R.string.oops));
                    realmOperations.updateTestResult(ConstantTestIDs.VOLUME_UP, AsyncConstant.TEST_SKIP);
                    realmOperations.updateTestResult(ConstantTestIDs.VOLUME_DOWN, AsyncConstant.TEST_PASS);
                    mImgViewVolumeDown.setImageDrawable(getResources().getDrawable(R.drawable.ic_volume_down_green_svg_168), true, getActivity());
                    mImgViewVolumeUp.setImageDrawable(getResources().getDrawable(R.drawable.ic_volume_up_green_svg_168), false, getActivity());
                }else if(!isVolDecrease && isVolIncrease)
                {
//                    utils.showToast(ctx, getResources().getString(R.string.oops2));

                    realmOperations.updateTestResult(ConstantTestIDs.VOLUME_UP, AsyncConstant.TEST_PASS);
                    realmOperations.updateTestResult(ConstantTestIDs.VOLUME_DOWN, AsyncConstant.TEST_SKIP);
                    mImgViewVolumeDown.setImageDrawable(getResources().getDrawable(R.drawable.ic_volume_down_green_svg_168), false, getActivity());
                    mImgViewVolumeUp.setImageDrawable(getResources().getDrawable(R.drawable.ic_volume_up_green_svg_168), true, getActivity());
                }

                utils.showToast(ctx, getResources().getString(R.string.txtManualFail));
              //  testController.onServiceResponse(false, "Volume", ConstantTestIDs.VOLUME_ID);
                testController.saveSkipResponse(0,ConstantTestIDs.VOLUME_ID);



            }else{

                JSONArray jsonArray = createVolumeJsonArray();

                sendTestDataWithSubArray(SocketConstants.EVENT_TEST_END, Constants.IVOLUME, Constants.COMPLETED, Constants.VOLUME_BUTTONS, "", utils.getPreference(getActivity(),
                        Constants.ANDROID_ID, ""),
                        MainActivity.qr_code_test, Constants.MANUAL1, jsonArray);
            }
            boolean semi = true;
            nextPress(activity, semi);


        } catch (Exception e) {
            logException(e, "VolumeManualFragment_onNextPress()");
        }

    }

    public void updateTestValues(boolean isBeckPress) {
        try {
            if (isVolIncrease && isVolDecrease) {

                JSONArray jsonArray = createVolumeJsonArray();

                sendTestDataWithSubArray(SocketConstants.EVENT_TEST_END, Constants.IVOLUME, Constants.COMPLETED, Constants.VOLUME_BUTTONS, "",   utils.getPreference(getActivity(),
                        Constants.ANDROID_ID, ""),
                        MainActivity.qr_code_test, Constants.MANUAL1, jsonArray);

                utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_19.name(), Constants.TEST_PASS);
                utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_20.name(), Constants.TEST_PASS);
            } else {
                if (isVolIncrease) {
                    progressArray[1] = Constants.FAILED;

                    JSONArray jsonArray = createVolumeJsonArray();

                    sendTestDataWithSubArray(SocketConstants.EVENT_TEST_END, Constants.IVOLUME, Constants.FAILED, Constants.VOLUME_BUTTONS, "",   utils.getPreference(getActivity(),
                            Constants.ANDROID_ID, ""),
                            MainActivity.qr_code_test, Constants.MANUAL1, jsonArray);

//                    utils.showToast(ctx, getResources().getString(R.string.txtManualVolumeUp));
                    utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_19.name(), Constants.TEST_PASS);
                    utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_20.name(), Constants.TEST_FAILED);
                } else if (isVolDecrease) {
                    progressArray[0] = Constants.FAILED;

                    JSONArray jsonArray = createVolumeJsonArray();

                    sendTestDataWithSubArray(SocketConstants.EVENT_TEST_END, Constants.IVOLUME, Constants.FAILED, Constants.VOLUME_BUTTONS, "",   utils.getPreference(getActivity(),
                            Constants.ANDROID_ID, ""),
                            MainActivity.qr_code_test, Constants.MANUAL1, jsonArray);


//                    utils.showToast(ctx, getResources().getString(R.string.txtManualVolumeDown));
                    utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_19.name(), Constants.TEST_FAILED);
                    utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_20.name(), Constants.TEST_PASS);
                } else if(isBeckPress){

                    if(!isVolIncrease)
                    {
                        testController.saveSkipResponse(-1,ConstantTestIDs.VOLUME_UP);
                    }
                    if(!isVolDecrease)
                    {
                        testController.saveSkipResponse(-1,ConstantTestIDs.VOLUME_DOWN);
                    }
                    JSONArray jsonArray = createVolumeJsonArray();
                    sendTestDataWithSubArray(SocketConstants.EVENT_TEST_END, Constants.IVOLUME, checkMutipleHardware1(JsonTags.MMR_20.name(),JsonTags.MMR_21.name()), Constants.VOLUME_BUTTONS, "",  utils.getPreference(getActivity(),
                            Constants.ANDROID_ID, ""),
                            MainActivity.qr_code_test, Constants.MANUAL1, jsonArray);

                }else {
                    progressArray[0] = Constants.FAILED;
                    progressArray[1] = Constants.FAILED;

                    JSONArray jsonArray = createVolumeJsonArray();

                    sendTestDataWithSubArray(SocketConstants.EVENT_TEST_END, Constants.IVOLUME, Constants.FAILED, Constants.VOLUME_BUTTONS, "",   utils.getPreference(getActivity(),
                            Constants.ANDROID_ID, ""),
                            MainActivity.qr_code_test, Constants.MANUAL1, jsonArray);

                    utils.showToast(ctx, getResources().getString(R.string.txtManualFail));
                    utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_19.name(), Constants.TEST_FAILED);
                    utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_20.name(), Constants.TEST_FAILED);
                }
            }
        } catch (Exception e) {

        }

    }

    public void backbutton(){
        updateTestValues(true);
//        utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_19.name(), Constants.TEST_NOTPERFORMED);
//        utils.compare_UpdatePreferenceInt(ctx, JsonTags.MMR_20.name(), Constants.TEST_NOTPERFORMED);

//        JSONArray jsonArray = createVolumeJsonArray();
//
//        sendTestDataWithSubArray(SocketConstants.EVENT_TEST_END, Constants.IVOLUME, checkMutipleHardware1(JsonTags.MMR_19.name(),JsonTags.MMR_20.name()), Constants.VOLUME_BUTTONS, "", utils.getPreference(getActivity(),
//                Constants.ANDROID_ID, ""),
//                MainActivity.qr_code_test, Constants.MANUAL1, jsonArray);

    }
//    /**
//     * onBackPressed
//     */
//    public void onBackPress() {
//        try {
//            snackShow(volumeLayout);
//        } catch (Exception e) {
//            logException(e, "VolumeManualFragment_onBackPress()");
//        }
//
//    }



    /**
     * onBackPressed
     */
    public void onBackPress() {
        try {
            snackShow(volumeLayout, new InterfaceAlertDissmiss() {
                @Override
                public void onButtonClick(boolean isCanceled, int callbackID) {
                    if(isVolDecrease && isVolIncrease){
                        testController.saveSkipResponse(1,ConstantTestIDs.VOLUME_ID);
                    }else if(isVolIncrease || isVolDecrease){
                        testController.saveSkipResponse(0,ConstantTestIDs.VOLUME_ID);
                    }
                    if(isVolDecrease&&!isVolIncrease)
                    {
                        testController.saveSkipResponse(-1,ConstantTestIDs.VOLUME_UP);
                        testController.saveSkipResponse(0,ConstantTestIDs.VOLUME_ID);

                    }
                    if(!isVolDecrease&&isVolIncrease)
                    {
                        testController.saveSkipResponse(-1,ConstantTestIDs.VOLUME_DOWN);
                        testController.saveSkipResponse(0,ConstantTestIDs.VOLUME_ID);

                    }

                }

                @Override
                public void onButtonClick(boolean isCanceled, int callbackID, int which) {

                }
            });
        } catch (Exception e) {
            logException(e, "VolumeManualFragment_onBackPress()");
        }

    }





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
            //logException(exp, "VolumeManualFragment_logException()");
        }

    }


    @Override
    public void update(Observable observable, Object o) {
        if (!isTestPerform) {
            AutomatedTestListModel automatedTestListModel = (AutomatedTestListModel) o;
            if (automatedTestListModel.getTest_id() == ConstantTestIDs.VOLUME_UP) {
                timer(ctx,false,ConstantTestIDs.VOLUME_ID,VolumeManualFragment.this);
                if (automatedTestListModel.getIsTestSuccess() == 1) {
                    if (!isVolIncrease) {
                        isVolIncrease = true;
                        mImgViewVolumeUp.setImageDrawable(getResources().getDrawable(R.drawable.ic_volume_up_green_svg_168), true, getActivity());

                        progressArray[0] = Constants.COMPLETED;

                        JSONArray jsonArray = createVolumeJsonArray();

                        sendTestDataWithSubArray(SocketConstants.EVENT_TEST_END,
                                Constants.IVOLUME, Constants.IN_PROGRESS,
                                Constants.VOLUME_BUTTONS, "", utils.getPreference(getActivity(),
                                        Constants.ANDROID_ID, ""),
                                MainActivity.qr_code_test, Constants.MANUAL1, jsonArray);


                        if (isVolIncrease && isVolDecrease) {
                            utils.showToast(ctx, getResources().getString(R.string.txtManualPass));
                            alert11.dismiss();
                            isTestPerform = true;
                            Constants.isManualIndividual = false;
                            onNextPress();
                        } else {

                        }
                    }
                }

            } else if (automatedTestListModel.getTest_id() == ConstantTestIDs.VOLUME_DOWN) {
                timer(ctx,false,ConstantTestIDs.VOLUME_ID,VolumeManualFragment.this);
                if (automatedTestListModel.getIsTestSuccess() == 1) {
                    if (!isVolDecrease) {
                        isVolDecrease = true;
                        mImgViewVolumeDown.setImageDrawable(getResources().getDrawable(R.drawable.ic_volume_down_green_svg_168), true, getActivity());
                        progressArray[1] = Constants.COMPLETED;
                        JSONArray jsonArray = createVolumeJsonArray();
                        sendTestDataWithSubArray(SocketConstants.EVENT_TEST_END,
                                Constants.IVOLUME, Constants.IN_PROGRESS,
                                Constants.VOLUME_BUTTONS, "", utils.getPreference(getActivity(),
                                        Constants.ANDROID_ID, ""),
                                MainActivity.qr_code_test, Constants.MANUAL1, jsonArray);

                        if (isVolIncrease && isVolDecrease) {
                            utils.showToast(ctx,
                                    getResources().getString(R.string.txtManualPass));
                            alert11.dismiss();
                            isTestPerform = true;
                            Constants.isManualIndividual = false;
                            onNextPress();
                        } else {

                        }

                    }
                }

            }
        }
    }

    @Override
    public void timerFail() {
        if(!isVolIncrease) {
            mImgViewVolumeUp.setImageDrawable(getResources().getDrawable(R.drawable.ic_volume_up_green_svg_168), false, getActivity());

            testController.onServiceResponse(false, "", ConstantTestIDs.VOLUME_UP);
        }

        if(!isVolDecrease) {
            mImgViewVolumeDown.setImageDrawable(getResources().getDrawable(R.drawable.ic_volume_down_green_svg_168), false,getActivity());
            testController.onServiceResponse(false, "", ConstantTestIDs.VOLUME_DOWN);
        }

        testController.saveSkipResponse(0,ConstantTestIDs.VOLUME_ID);

        utils.showToast(ctx, getResources().getString(R.string.txtManualFail));
        isTestPerform = true;

        onNextPress();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        timer(ctx,true,ConstantTestIDs.VOLUME_ID,null);
        try {
            testController.deleteObserver(this);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
