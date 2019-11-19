package com.officework.fragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.officework.ApiController.APIClient;
import com.officework.ApiController.APIInterface;
import com.officework.ApiController.WebService;
import com.officework.ApiController.WebServiceInterface;
import com.officework.R;
import com.officework.activities.MainActivity;
import com.officework.base.BaseFragment;
import com.officework.constants.Constants;
import com.officework.constants.FragmentTag;
import com.officework.constants.JsonTags;
import com.officework.interfaces.ViewPagerDisableInterface;
import com.officework.models.AutomatedTestListModel;
import com.officework.models.DiagonsticObject;
import com.officework.models.SocketDataSync;
import com.officework.models.TestObject;
import com.officework.testing_profiles.Controller.TestController;
import com.officework.testing_profiles.realmDatabase.operationManager.RealmOperations;
import com.officework.testing_profiles.ui.fragment.TestCompleteFragment;
import com.officework.testing_profiles.utils.ConstantTestIDs;
import com.officework.utils.DeviceInformation;
import com.officework.utils.ExceptionLogUtils;
import com.officework.utils.TelephonyInfo;
import com.officework.utils.Utilities;
import com.officework.utils.socket.SocketConstants;
import com.officework.utils.socket.SocketHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import io.socket.client.IO;
import retrofit2.Response;

public class TitleBarFragment extends BaseFragment implements View.OnLongClickListener {

    public static ProgressDialog progressBar;
    public TextView uniqueNo;
    Toolbar mToolbar;
    ImageView btnToggleSlider;
    RelativeLayout  switchLayout;
    TextView txtTitle;
    TextView txtSync;
    SwitchCompat switchDoAll;
    DeviceInformation deviceInformation;
    //  SwitchCompat switchDoAll;
    private View view;
    private ImageView cancel;
    private RealmOperations realmOperations;
    public TitleBarFragment() {
    }

    @SuppressLint("ResourceType")
    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        try {
            if (view == null) {
                view = inflater.inflate(R.layout.layout_toolbar, null);
                mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
                cancel = (ImageView) view.findViewById(R.id.cancel);
                cancel.setOnClickListener(this);
                ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
                ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
                mToolbar.setContentInsetsAbsolute(0, 0);
                txtTitle = (TextView) view.findViewById(R.id.txtTitle);
                txtSync = (TextView) view.findViewById(R.id.sync);

                deviceInformation = new DeviceInformation();
                realmOperations = new RealmOperations();
                txtTitle.setOnLongClickListener(this);
                txtSync.setOnClickListener(this);
                switchDoAll = (SwitchCompat) view.findViewById(R.id.switchDoAll);
                switchDoAll.setChecked(true);
                btnToggleSlider = (ImageView) view.findViewById(R.id.btnToggleSlider);
                uniqueNo = (TextView) view.findViewById(R.id.unique_no);
                switchLayout = view.findViewById(R.id.switchLayout);
                setHeaderTitleAndSideIcon(getResources().getString(R.string.txtAutomated), true,
                        true, 0);
                TextView textView = view.findViewById(R.id.stxt);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    textView.setText(Html.fromHtml(getResources().getString(R.string.switch_desc),
                            Html.FROM_HTML_MODE_COMPACT));
                } else {
                    textView.setText(Html.fromHtml(getResources().getString(R.string.switch_desc)));
                }

                //  btnToggleSlider.setVisibility(View.GONE);
                btnToggleSlider.setOnClickListener(this);
                switchDoAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            emitPrivacyEvent(true);
                            Constants.isDoAllClicked = true;
                        } else {
                            emitPrivacyEvent(false);
                            Constants.isDoAllClicked = false;
                        }
                    }
                });


                try {
                    if (getArguments() != null) {
                        boolean SHOULDHIDE = getArguments().getBoolean("SHOULDHIDE");
                        if (SHOULDHIDE) {
                            hideTitleAndSideIcon(getResources().getString(R.string.permissionrqrd));
                            setTitleBarVisibility(true);
                            showSwitchLayout(false);
                        }
                        boolean welcome = getArguments().getBoolean("WELCOME");
                        if (welcome) {
                            hideTitleAndSideIcon(getResources().getString(R.string.app_name));
                            setTitleBarVisibility(true);
                            showSwitchLayout(false);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            } else {
                ((ViewGroup) view.getParent()).removeView(view);
            }
//
            String serial_no = Utilities.getInstance(getActivity()).getPreference(getActivity(),
                    Constants.UNIQUE_NO, "");
            uniqueNo.setText(serial_no);
            return view;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public void showSwitchLayout(boolean show) {
        if (show) {
            switchLayout.setVisibility(View.VISIBLE);
        } else {
            switchLayout.setVisibility(View.GONE);
        }
    }

    private void emitPrivacyEvent(boolean fieldValue) {
        try {
            String qrCodeiD = Utilities.getInstance(getActivity()).getPreference(getActivity(),
                    Constants.QRCODEID, "");

            JSONObject jsonObject = new JSONObject();
            String androidId = Utilities.getInstance(getActivity()).getPreference(getActivity(),
                    Constants.ANDROID_ID, "");

            jsonObject.put(Constants.UNIQUE_ID, androidId);
            jsonObject.put(Constants.QRCODEID, qrCodeiD);
            jsonObject.put("Status", fieldValue);


            if (socketHelper != null && socketHelper.socket.connected()) {
                socketHelper.emitData(SocketConstants.EVENT_TERMS_CONDITION, jsonObject);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

    }

    private void setUpToolbar() {


    }

    public void hideTitleAndSideIcon(String title) {
        try {
            txtTitle.setText(title);
            //    txtSync.setVisibility(View.GONE);
            uniqueNo.setVisibility(View.GONE);
//            titlebarLogo.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            //logException(e, "TitleBarFragment_setHeaderTitleAndSideIcon()");
        }

    }

    public void hideNavBarButton() {
        btnToggleSlider.setVisibility(View.INVISIBLE);

    }

    @SuppressLint("NewApi")
    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.btnToggleSlider:
                    //  onToggleButtonClick();
                    break;

                case R.id.switchDoAll:


                    /*onDoneClick(v);*/
                    break;

                case R.id.sync:
                    openManualSummary();
                    //   showSyncDialog();
                    break;

                case R.id.cancel:
                    try {
                        Intent startMain = new Intent(Intent.ACTION_MAIN);
                        startMain.addCategory(Intent.CATEGORY_HOME);
                        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(startMain);
                        getActivity().finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                default:
                    break;
            }
        } catch (Exception e) {

        }

    }

    public void setTitle(Integer i) {
        try {
            TitleBarFragment fragment = (TitleBarFragment) getFragment(R.id.headerContainer);
            if (fragment != null) {
                fragment.setTitleBarVisibility(true);
                fragment.setHeaderTitleAndSideIcon(getResources().getString(i), true, true, 0);
//                if (i == R.string.txtAutomated) {
//                    fragment.setSyntextVisibilty(false);
//                } else {
//                    fragment.setSyntextVisibilty(true);
//                }
                //  mCallBack.onChangeText(utils.BUTTON_SKIP, false);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openManualSummary() {
        FragmentManager fm = getFragmentManager();
        ManualTestFragment fragm = (ManualTestFragment)fm.findFragmentById(R.id.container);
        fragm.enableViewPager(false);

        setTitle(R.string.manual_summary);
        addFragment(R.id.container,
                SummaryFragment.newInstance(Constants.MANUAL),
                FragmentTag.SUMMARY_FRAGMENT.name(), false);
    }


    public void showSyncDialog() {
        try {
            if (Utilities.getInstance(getActivity()).isInternetWorking(getActivity())) {
                Utilities.getInstance(getActivity()).allunrigster(getActivity());
                progressBar = new ProgressDialog(getActivity());
                try{
                    progressBar.show();
                    txtTitle.setText(getString(R.string.manual_summary));
                }catch (Exception e){
                    e.printStackTrace();
                }
//                progressBar.setMessage("");
                progressBar.getWindow()
                        .setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                progressBar.setContentView(R.layout.progressdialog);

//                View view = LayoutInflater.from(getActivity()).inflate(R.layout.progressdialog,null);
//                progressBar.addContentView(view,new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                progressBar.setCancelable(false);
                progressBar.setIndeterminate(true);
                progressBar.show();
                submitResult();
            } else {
//                if (progressBar.isShowing()) {
//                    progressBar.dismiss();
//                    progressBar = null;
//                }
                txtTitle.setText(getString(R.string.manual_summary));
                Toast.makeText(getActivity(),
                        getResources().getString(R.string.please_check_internet_connection),
                        Toast.LENGTH_SHORT).show();
//                txtSync.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void performAllManualTest() {
        try {
            TestController testController = TestController.getInstance(getActivity());
            testController.performOperation(ConstantTestIDs.EAR_PHONE_ID);
            testController.performOperation(ConstantTestIDs.POWER_ID);
            testController.performOperation(ConstantTestIDs.GYROSCOPE_ID);
            testController.performOperation(ConstantTestIDs.HOME_ID);
            testController.performOperation(ConstantTestIDs.CHARGING_ID);
            testController.performOperation(ConstantTestIDs.Battery);
            testController.performOperation(ConstantTestIDs.PROXIMITY_ID);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void submitResult() {

        APIInterface apiInterface =
                APIClient.getClient(getActivity()).create(APIInterface.class);
        HashMap<String, Object> map = createDiagnoseMap();
        WebService webService = new WebService();
        webService.apiCall(apiInterface.getOfferPrice(map), getActivity(),
                new WebServiceInterface<Response<SocketDataSync>>() {
                    @Override
                    public void apiResponse(Response<SocketDataSync> response) {
                        Log.d("Success", response.toString());
                        try {
                            if (progressBar.isShowing()) {
                                progressBar.dismiss();
                                progressBar = null;

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            if (response.body().isSuccess()) {
                                final SocketDataSync socketDataSync = response.body();

                                TestCompleteFragment fragment =
                                        new TestCompleteFragment();
                                Bundle bundle = new Bundle();
                                bundle.putParcelable("OFFER_PRICE_OBJECT",
                                        socketDataSync.getData());


                                fragment.setArguments(bundle);
                                replaceFragment(R.id.container, fragment,
                                        FragmentTag.AUTOMATION_TEST_FRAGMENT.name(), false);
                                uniqueNo.setVisibility(View.GONE);
                                txtSync.setVisibility(View.GONE);
                                txtTitle.setText(getResources().getString(R.string.report));
                                try {
                                    String qrCodeiD =
                                            Utilities.getInstance(getActivity()).getPreference(getActivity(), Constants.QRCODEID, "");

                                    JSONObject jsonObject = new JSONObject();
                                    String androidId =
                                            Utilities.getInstance(getActivity()).getPreference(getActivity(), Constants.ANDROID_ID, "");

                                    jsonObject.put(Constants.UNIQUE_ID, androidId);
                                    jsonObject.put(Constants.QRCODEID, qrCodeiD);
                                    IO.Options options = new IO.Options();
                                    socketHelper =
                                            new SocketHelper.Builder(SocketConstants.HOST_NAME1 +
                                            "?" + Constants.REQUEST_UNIQUE_ID + "=" + androidId,
                                                    options)
                                            .addEvent(SocketConstants.EVENT_CONNECTED)
                                            .addListener(null)
                                            .build();
                                    if (socketHelper != null && socketHelper.socket.connected()) {
                                        socketHelper.emitScreenChangeEvent(SocketConstants.REPORT,
                                                androidId, qrCodeiD);
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            } else {
                                if (!socketHelper.socket.connected()) {
                                    performAllManualTest();
                                }
//                                txtSync.setVisibility(View.VISIBLE);
                                fabricLog("title_Frag_Api_sync_error", "error");
                                Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                                Log.w("errorSKUID",
                                        Utilities.getInstance(getActivity()).getPreference(getActivity(),
                                                Constants.SKUID, ""));
                            }
                        } catch (Exception e) {
//                            txtSync.setVisibility(View.VISIBLE);
                            fabricLog("title_Frag_Api_sync_exception", e.getMessage());
                            e.printStackTrace();
                            if (progressBar.isShowing()) {
                                progressBar.dismiss();
                                progressBar = null;
                            }
                        }
                    }

                    @Override
                    public void apiError(Response<SocketDataSync> response) {
                        if (!socketHelper.socket.connected()) {
                            performAllManualTest();
                        }
                        if (progressBar.isShowing()) {
                            progressBar.dismiss();
                            progressBar = null;
                        }
//                        txtSync.setVisibility(View.VISIBLE);
                        fabricLog("title_Frag_Api_sync_error", "error");
                        Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                        Log.w("errorSKUID",
                                Utilities.getInstance(getActivity()).getPreference(getActivity(),
                                        Constants.SKUID, ""));
                    }

                    @Override
                    public void serverError(Throwable t) {
                        fabricLog("title_Frag_Api_sync_exception", t.getMessage());
//                        txtSync.setVisibility(View.VISIBLE);
                        try {
                            if (t instanceof UnknownHostException) {
                                Toast.makeText(getActivity(), "There is something went wrong with" +
                                        " your " +
                                        "Internet Connection Please reset your Internet " +
                                        "Connection and " +
                                        "try again", Toast.LENGTH_LONG).show();
                            }
                            if (progressBar.isShowing()) {

                                progressBar.dismiss();
                                progressBar = null;
                                if (!socketHelper.socket.connected()) {
                                    performAllManualTest();
                                }

                            }

                            try {
                                ManualTestFragment manualTestFragment =
                                        (ManualTestFragment) getFragment
                                        (R.id.container);
                                if (manualTestFragment != null) {
// manualTestFragment.progressBarMedium.setVisibility(View.GONE);

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            Log.d("error", t.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    public HashMap<String, Object> createDiagnoseMap() {
        HashMap<String, Object> map = new HashMap<>();
        try {
            Utilities utils = Utilities.getInstance(getActivity());
            ArrayList<DiagonsticObject> diagonsticArrayList = diagonsticArrayList();
            map.put("DiagnosticData", diagonsticArrayList);
            //    map.put("QrCodeID", utils.getPreference(getActivity(), Constants.QRCODEID, ""));
            //   map.put("deviceid", utils.getPreference(getActivity(), Constants.ANDROID_ID, ""));
            map.put("SubscriberProductID", utils.getPreference(getActivity(),
                    JsonTags.SubscriberProductID.name(), ""));
            map.put("UDI", utils.getPreference(getActivity(), JsonTags.UDI.name(), ""));
            map.put("SKUID", utils.getPreference(getActivity(), Constants.SKUID, ""));
            map.put("EnterprisePartnerID", utils.getPreference(getActivity(),
                    Constants.ENTERPRISEPATNERID, ""));
            map.put("StoreID", utils.getPreference(getActivity(), Constants.QRCODEID, ""));
            map.put("CacheManagerID", utils.getPreference(getActivity(), Constants.CACHEMANAGERID
                    , ""));
            map.put("LocationID", utils.getPreference(getActivity(), Constants.LOCATION_ID, ""));
            map.put("UserID", utils.getPreference(getActivity(), Constants.USERID, ""));

//            map.put("deviceInfo", new DeviceInfoModel(Build.MANUFACTURER, Build.MODEL,
//                    deviceInformation.getFileSize(deviceInformation.getAvailableInfo())));

        } catch (Exception E) {
            E.printStackTrace();
        }
        return map;
    }


    private ArrayList<DiagonsticObject> diagonsticArrayList() {

        ArrayList<DiagonsticObject> testObjectArrayList = new ArrayList<>();
        ArrayList<AutomatedTestListModel> testModelArrayList = new ArrayList<>();
        MainActivity mainActivity = (MainActivity) getActivity();
        testModelArrayList =
                realmOperations.fetchTestsListAllList2(mainActivity.diagnosticListPojoArrayList,
                        getActivity());
//        testModelArrayList = realmOperations.fetchTestsListAllList(MainActivity.id_array2,
//                getActivity());
        for (int i = 0; i < testModelArrayList.size(); i++) {
            if (testModelArrayList.get(i).isDuel()) {
                ArrayList<Integer> parentidlist =
                        realmOperations.fetchParentTestId(testModelArrayList.get(i).getTest_id());
                for (int j = 0; j < parentidlist.size(); j++) {
                    AutomatedTestListModel automatedTestListModel =
                            realmOperations.fetchTestType(parentidlist.get(i));
                    testObjectArrayList.add(creatediagonsticObject(automatedTestListModel.getTest_id(), automatedTestListModel.getIsTestSuccess()));
                }
            } else {

                testObjectArrayList.add(creatediagonsticObject(testModelArrayList.get(i).getTest_id()
                        , testModelArrayList.get(i).getIsTestSuccess()));
            }
        }


        return testObjectArrayList;
    }

    private HashMap<String, Object> createMap() {
        HashMap<String, Object> map = new HashMap<>();

        try {
            DeviceInformation deviceInformation = new DeviceInformation();

            Utilities utils = Utilities.getInstance(getActivity());

            ArrayList<TestObject> testObjectArrayList = createTestArray();
            map.put("type", "Android");
            map.put("make", Build.MANUFACTURER);
            map.put("model", Build.MODEL);
            map.put("capacity",
                    deviceInformation.getFileSize(deviceInformation.getAvailableInfo()));
            map.put("os", Build.VERSION.RELEASE);
            map.put("TestArray", testObjectArrayList);
            map.put("QrCodeID", utils.getPreference(getActivity(), Constants.QRCODEID, ""));
            map.put("deviceid", utils.getPreference(getActivity(), Constants.ANDROID_ID, ""));
            if ((TelephonyInfo.getInstance(getActivity()).getImsiSIM1()) != null) {
                map.put("IMEI1", TelephonyInfo.getInstance(getActivity()).getImsiSIM1());
            } else {
                map.put("IMEI1", "");
            }

            if ((TelephonyInfo.getInstance(getActivity()).getImsiSIM2()) != null) {
                map.put("IMEI2", TelephonyInfo.getInstance(getActivity()).getImsiSIM2());
            } else {
                map.put("IMEI2", "");
            }


        } catch (Exception E) {
            E.printStackTrace();
        }
        return map;
    }

    private ArrayList<TestObject> createTestArray() {

        Utilities utils = Utilities.getInstance(getActivity());

        ArrayList<TestObject> testObjectArrayList = new ArrayList<>();
        TestObject testObject;
        testObjectArrayList.add(createObject("Bluetooth", 34,
                utils.getPreferenceInt(getActivity(), JsonTags.MMR_34.name(), -1)));
        testObjectArrayList.add(createObject("Wifi", 35, utils.getPreferenceInt(getActivity(),
                JsonTags.MMR_35.name(), -1)));
        testObjectArrayList.add(createObject("Vibration", 39,
                utils.getPreferenceInt(getActivity(), JsonTags.MMR_39.name(), -1)));
        testObjectArrayList.add(createObject("Sim Card Removed", 36,
                utils.getPreferenceInt(getActivity(), JsonTags.MMR_36.name(), -1)));
        testObjectArrayList.add(createObject("SD Card Removed", 16,
                utils.getPreferenceInt(getActivity(), JsonTags.MMR_16.name(), -1)));
        testObjectArrayList.add(createObject("Kill Switch Disabled", 42,
                utils.getPreferenceInt(getActivity(), JsonTags.MMR_42.name(), -1)));
        testObjectArrayList.add(createObject("Rooted", 49, utils.getPreferenceInt(getActivity(),
                JsonTags.MMR_49.name(), -1)));
        testObjectArrayList.add(createObject("Call Function", 30,
                utils.getPreferenceInt(getActivity(), JsonTags.MMR_30.name(), -1)));
        testObjectArrayList.add(createObject("Jack", 18, utils.getPreferenceInt(getActivity(),
                JsonTags.MMR_18.name(), -1)));
        testObjectArrayList.add(createObject("Volume Buttons", 56,
                checkMutipleHardware2(JsonTags.MMR_20.name(), JsonTags.MMR_21.name())));
        testObjectArrayList.add(createObject("Camera", 58,
                checkMutipleHardware2(JsonTags.MMR_37.name(), JsonTags.MMR_38.name())));
        testObjectArrayList.add(createObject("Display", 57,
                checkMutipleHardware2(JsonTags.MMR_46.name(), JsonTags.MMR_47.name())));
        testObjectArrayList.add(createObject("Power Button", 45,
                utils.getPreferenceInt(getActivity(), JsonTags.MMR_45.name(), -1)));
        testObjectArrayList.add(createObject("Charging Port", 44,
                utils.getPreferenceInt(getActivity(), JsonTags.MMR_44.name(), -1)));
        testObjectArrayList.add(createObject("Home", 26, utils.getPreferenceInt(getActivity(),
                JsonTags.MMR_26.name(), -1)));
        testObjectArrayList.add(createObject("Proximity Sensor", 23,
                utils.getPreferenceInt(getActivity(), JsonTags.MMR_23.name(), -1)));
        testObjectArrayList.add(createObject("Gyroscope", 40,
                utils.getPreferenceInt(getActivity(), JsonTags.MMR_40.name(), -1)));
        testObjectArrayList.add(createObject("Light Sensor", 28,
                utils.getPreferenceInt(getActivity(), JsonTags.MMR_28.name(), -1)));
        testObjectArrayList.add(createObject("GPS", 31, utils.getPreferenceInt(getActivity(),
                JsonTags.MMR_31.name(), -1)));
        testObjectArrayList.add(createObject("Speaker", 32, utils.getPreferenceInt(getActivity(),
                JsonTags.MMR_32.name(), -1)));
        testObjectArrayList.add(createObject("MIC", 33, utils.getPreferenceInt(getActivity(),
                JsonTags.MMR_33.name(), -1)));
        testObjectArrayList.add(createObject("Touch Screen", 24,
                utils.getPreferenceInt(getActivity(), JsonTags.MMR_24.name(), -1)));
        testObjectArrayList.add(createObject("Multi Touch", 22,
                utils.getPreferenceInt(getActivity(), JsonTags.MMR_22.name(), -1)));
        testObjectArrayList.add(createObject("Device Casing", 55,
                utils.getPreferenceInt(getActivity(), JsonTags.MMR_55.name(), -1)));

        return testObjectArrayList;
    }

    private TestObject createObject(String name, int id, int preferenceInt) {
        TestObject testObject = new TestObject();
        testObject.setTestName(name);
        testObject.setTestID(id);
        testObject.setTestStatus(String.valueOf(preferenceInt));
        return testObject;
    }

    private DiagonsticObject creatediagonsticObject(int id, int preferenceInt) {
        DiagonsticObject diagonsticObject = new DiagonsticObject();
        diagonsticObject.setDiagnosticID(id);
        diagonsticObject.setDiagnosticResult(String.valueOf(preferenceInt));

        return diagonsticObject;
    }


    public int checkMutipleHardware2(String pref_Key_1, String pref_Key_2) {
        int testStatus = -1;
        try {

            if (Utilities.getInstance(getActivity()).getPreferenceInt(getActivity(), pref_Key_1,
                    0) == -1 && Utilities.getInstance(getActivity()).getPreferenceInt(getActivity(), pref_Key_2, 0) == -1) {
                testStatus = -1;
            } else if (Utilities.getInstance(getActivity()).getPreferenceInt(getActivity(),
                    pref_Key_1, 0) == 0 && Utilities.getInstance(getActivity()).getPreferenceInt(getActivity(), pref_Key_2, 0) == 0) {
                testStatus = 0;
            } else if (Utilities.getInstance(getActivity()).getPreferenceInt(getActivity(),
                    pref_Key_1, 0) == 1 && Utilities.getInstance(getActivity()).getPreferenceInt(getActivity(), pref_Key_2, 0) == 1) {
                testStatus = 1;
            } else if (Utilities.getInstance(getActivity()).getPreferenceInt(getActivity(),
                    pref_Key_1, 0) == 1 || Utilities.getInstance(getActivity()).getPreferenceInt(getActivity(), pref_Key_2, 0) == 0) {
                testStatus = 0;
            } else if (Utilities.getInstance(getActivity()).getPreferenceInt(getActivity(),
                    pref_Key_1, 0) == 0 || Utilities.getInstance(getActivity()).getPreferenceInt(getActivity(), pref_Key_2, 0) == 1) {
                testStatus = 0;
            }/* else {
                testStatus = AsyncConstant.TEST_PASS;
            }*/
            return testStatus;
        } catch (Exception e) {

            return testStatus;

        }
    }


    public void setCancelVisible() {
        try {
            cancel.setVisibility(View.VISIBLE);
        } catch (Exception e) {

        }

    }

    public void setHeaderTitleAndSideIcon(String title, boolean isLeftAvailable,
                                          boolean isRightAvailable, int icon) {
        try {


          /*  if (isLeftAvailablWee)
                btnToggleSlider.setVisibility(View.VISIBLE);
            else
                btnToggleSlider.setVisibility(View.INVISIBLE);
            */

            txtTitle.setText(title);

        } catch (Exception e) {

        }


    }


    private void onDisableToggleButton() {
        Fragment frag = getActivity().getSupportFragmentManager().findFragmentById(R.id.container);
//        if (frag instanceof AutomatedTestFragment)
//            ((AutomatedTestFragment) frag).showSnackBar();
    }

    private void onDoneClick(View v) {
        try {
            Fragment frag =
                    getActivity().getSupportFragmentManager().findFragmentById(R.id.container);

            if (frag instanceof ManualTestFragment)
                ((ManualTestFragment) frag).onAutoTest(v);

        /*if (frag instanceof PhoneBookContactsFragment)
            ((PhoneBookContactsFragment) frag).onMenuClick(v);
        else if (frag instanceof TrainingContentFragment) {
            ((TrainingContentFragment) frag).onUploadClick(v);
        } else if (frag instanceof SelfTrainingFragment) {
            ((SelfTrainingFragment) frag).onDialogButtonClick(v);
        }else if (frag instanceof RecommendedContactsFragment) {
            ((RecommendedContactsFragment) frag).onDoneClick(v);
        }*/
        } catch (Exception e) {
            logException(e, "TitleBarFragment_onDoneClick()");
        }

    }


    public void setTitleBarVisibility(boolean isVisible) {
        try {
            if (isVisible) {
                mToolbar.setVisibility(View.VISIBLE);
            } else {
                mToolbar.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            logException(e, "TitleBarFragment_setTitleBarVisibility()");
        }

    }


    public void setSyntextVisibilty(boolean isVisible) {
        try {
            if (isVisible) {
                txtSync.setVisibility(View.GONE);
            } else {
                txtSync.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            logException(e, "TitleBarFragment_setTitleBarVisibility()");
        }

    }

    public void setTitleBarColor(int color) {
        try {
            mToolbar.setBackgroundColor(color);
        } catch (Exception e) {
            logException(e, "TitleBarFragment_setTitleBarColor()");
        }

    }


    public void setStatusBarColor(Activity activity) {
        try {
            Window window = activity.getWindow();

            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            // finally change the color
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(activity.getResources().getColor(R.color.themeGreen));
            }
        } catch (Exception e) {
            logException(e, "TitleBarFragment_setStatusBarColor()");

        }

    }


    public void setHeaderTitleAndSideIcon(String title) {
        try {
            txtTitle.setText(title);
            btnToggleSlider.setVisibility(View.INVISIBLE);

        } catch (Exception e) {
            logException(e, "TitleBarFragment_setHeaderTitleAndSideIcon()");
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
            //   logException(exp, "TitleBarFragment_logException()");
        }

    }

    @Override
    public boolean onLongClick(View v) {
        try {
            switch (v.getId()) {

                case R.id.txtTitle:
                    onLongClickCallback(v);

                    /*onDoneClick(v);*/
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            logException(e, "TitleBarFragment_onClick()");
        }
        return true;
    }

    private void onLongClickCallback(View v) {
        try {
            Fragment frag =
                    getActivity().getSupportFragmentManager().findFragmentById(R.id.container);

            if (frag instanceof DashBoardFragment)
                ((DashBoardFragment) frag).onTitleLongClick(v);

        /*if (frag instanceof PhoneBookContactsFragment)
            ((PhoneBookContactsFragment) frag).onMenuClick(v);
        else if (frag instanceof TrainingContentFragment) {
            ((TrainingContentFragment) frag).onUploadClick(v);
        } else if (frag instanceof SelfTrainingFragment) {
            ((SelfTrainingFragment) frag).onDialogButtonClick(v);
        }else if (frag instanceof RecommendedContactsFragment) {
            ((RecommendedContactsFragment) frag).onDoneClick(v);
        }*/
        } catch (Exception e) {
            logException(e, "TitleBarFragment_onLongClickCallback()");
        }

    }
}
