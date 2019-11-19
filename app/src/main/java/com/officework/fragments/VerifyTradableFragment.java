package com.officework.fragments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.officework.ApiController.APIClient;
import com.officework.ApiController.APIInterface;
import com.officework.ApiController.WebService;
import com.officework.ApiController.WebServiceInterface;
import com.officework.R;
import com.officework.Services.TestNameUpdateService;
import com.officework.activities.CheckingDeviceActivity;
import com.officework.activities.MainActivity;
import com.officework.activities.WelcomeActivity;
import com.officework.base.BaseFragment;
import com.officework.constants.Constants;
import com.officework.constants.JsonTags;
import com.officework.constants.PreferenceConstants;
import com.officework.customViews.DotProgressBar;
import com.officework.customViews.DotProgressBarBuilder;
import com.officework.models.DiagnosticListPojo;
import com.officework.models.DiagonsticObject;
import com.officework.models.DiagonsticSyncModel;
import com.officework.models.SocketDataSync;
import com.officework.models.StoreVerifyModel;
import com.officework.utils.Utilities;
import com.officework.utils.WebserviceUrls;
import com.officework.utils.socket.SocketConstants;
import com.officework.utils.socket.SocketHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import io.socket.client.IO;
import retrofit2.Response;

import static com.officework.constants.Constants.QR_CODE_RESULT;
import static com.officework.constants.JsonTags.SubscriberProductID;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link VerifyTradableFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VerifyTradableFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VerifyTradableFragment extends BaseFragment  {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
Utilities utils;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String enterprisePartnerID, storeID;
    private OnFragmentInteractionListener mListener;
boolean gonext=true;
    WebService webService;
    ArrayList<DiagnosticListPojo> DiagnosticTestsList;
    public VerifyTradableFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VerifyTradableFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VerifyTradableFragment newInstance(String param1, String param2) {
        VerifyTradableFragment fragment = new VerifyTradableFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        utils=Utilities.getInstance(getActivity());
    }

    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_verify_tradable, container, false);
        enterprisePartnerID = getArguments().getString(Constants.ENTERPRISEPATNERID);
        storeID = getArguments().getString(Constants.STOREID);
        webService=new WebService();
//        utils.addPreference(getActivity(), Constants.STOREID, "");
//        if(utils.getPreference(getActivity(),JsonTags.UDI.name(),"").isEmpty() || utils.getPreference(getActivity(),JsonTags.UDI.name(),"").equals(""))
//
//        {
//            postDiagnosticsData();
        verifyStoreApiCall();

     //   }




        return view;
    }

    public void onBackPress() {
        gonext=false;
        Utilities.getInstance(getActivity()).showAlert(getActivity(), new Utilities.onAlertOkListener() {
            @Override
            public void onOkButtonClicked(String tag) {
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();

            }
        }, new Utilities.onAlertCancelListener() {
            @Override
            public void onOkButtonClicked1(String tag) {
                try {
                    gonext=true;
                    verifyStoreApiCall();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        },Html.fromHtml(getResources().getString(R.string.rescan_msgg)), getResources().getString(R.string.rescan_head), "No", "Yes", "Sync");
    }
    private void getDiagnosticTestApiCall() {
        try {
            new DotProgressBarBuilder(getActivity())
                    .setDotAmount(3)
                    .setStartColor(Color.GRAY)
                    .setAnimationDirection(DotProgressBar.LEFT_DIRECTION)
                    .build();


            WebService webService=new WebService();

            APIInterface apiInterface =
                    APIClient.getClient(getActivity()).create(APIInterface.class);

            webService.apiCall(apiInterface.getDiagnosticTest(Utilities.getInstance(getActivity()).getPreference(getActivity(),
                    SubscriberProductID.name(), "")), getActivity() ,new WebServiceInterface<Response<DiagonsticSyncModel>>() {
                @Override
                public void apiResponse(Response<DiagonsticSyncModel> response) {
                    try {

                        Log.d("response m",response.body().isSuccess()+"");
                        if(response.body().isSuccess()){
                            DiagnosticTestsList = response.body().getData().getDiagnosticTestsList();
                             update(DiagnosticTestsList);
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if(gonext) {
                                        saveDeviceInfomationApiCall();
                                    }
                                }
                            }, 1000);

                        }


                    } catch (Exception e) {
                        Log.d("response e",e.getMessage());
                        e.printStackTrace();
                    }
                }

                @Override
                public void apiError(Response<DiagonsticSyncModel> response) {
                    Toast.makeText(getActivity(),getResources().getText(R.string.api_error_toast),Toast.LENGTH_LONG).show();
                    CheckingDeviceActivity checkingDeviceActivity = (CheckingDeviceActivity) getActivity();
                    if(checkingDeviceActivity != null)
                        checkingDeviceActivity.setToolbarTitle2(getResources().getString(R.string.txtDASHBOARD));
                    Fragment f = new LandingFragment();
                    replaceFragment(R.id.frame,f,"Landing",false);


                }

                @Override
                public void serverError(Throwable t) {
                    if(getActivity()!=null)
                        Toast.makeText(getActivity(),getResources().getText(R.string.server_error_toast),Toast.LENGTH_LONG).show();
                    CheckingDeviceActivity checkingDeviceActivity = (CheckingDeviceActivity) getActivity();
                    if(checkingDeviceActivity != null)
                        checkingDeviceActivity.setToolbarTitle2(getResources().getString(R.string.txtDASHBOARD));
                    Fragment f = new LandingFragment();
                    replaceFragment(R.id.frame,f,"Landing",false);

                    Log.d("response e",t.getMessage());
                    if( t instanceof UnknownHostException)
                    {
                        Toast.makeText(getActivity(),getResources().getText(R.string.internet_error_toast),Toast.LENGTH_LONG).show();
                    }
                }
            });

        } catch (Exception e) {

        }
    }





    private HashMap<String, Object> saveDataMap() {
        HashMap<String, Object> map = new HashMap<>();
        try {
            JSONObject jsonObject =
                    new JSONObject(GetQRCodeFragment.createSaveDataJson(Utilities.getInstance(getActivity()), getActivity()).toString());

            map.put(JsonTags.SubscriberProductID.name(),
                    jsonObject.getString(JsonTags.SubscriberProductID.name()));
            map.put(JsonTags.UDI.name(), jsonObject.getString(JsonTags.UDI.name()));
            map.put("Make", utils.getSecurePreference(getActivity(), "MMR_4", Build.MANUFACTURER));
            map.put("Model", utils.getSecurePreference(getActivity(), "MMR_5", Build.MODEL));
            map.put("Capacity",  utils.getSecurePreference(getActivity(), "MMR_6", HardwareInfoFragment.getTotalInternalMemorySize()));
            //map.put("Capacity",  128 +"GB");
            map.put("IMEI",  utils.getSecurePreference(getActivity(), "MMR_1",""));













            map.put("DiagnosticData", diagonsticArrayList(jsonObject.getJSONArray("DiagnosticData")));

        } catch (Exception E) {
            E.printStackTrace();
        }
        return map;
    }
    private ArrayList<DiagonsticObject> diagonsticArrayList(JSONArray jsonArray) {
        ArrayList<DiagonsticObject> testObjectArrayList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            try {


                JSONObject innInnerObj = jsonArray.getJSONObject(i);
                testObjectArrayList.add(creatediagonsticObject(innInnerObj.getString("DiagnosticID")
                        , innInnerObj.getString("DiagnosticResult")));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        return testObjectArrayList;
    }
    private DiagonsticObject creatediagonsticObject(String id, String preferenceInt) {
        DiagonsticObject diagonsticObject = new DiagonsticObject();
        diagonsticObject.setDiagnosticID(Integer.parseInt(id));
        diagonsticObject.setDiagnosticResult(String.valueOf(preferenceInt));

        return diagonsticObject;
    }











    public void postDiagnosticsData() {
        try {
            if (utils.isInternetWorking(getActivity())) {
                try {
                    WebService webService=new WebService();

                    APIInterface apiInterface =
                            APIClient.getClient(getActivity()).create(APIInterface.class);

                    webService.apiCall(apiInterface.getUDI(Utilities.getInstance(getActivity()).getPreference(getActivity(), JsonTags.SubscriberProductID.name(), "")), getActivity() ,new WebServiceInterface<Response<SocketDataSync>>() {
                        @Override
                        public void apiResponse(Response<SocketDataSync> response) {
                            Log.d("UDI", response.body().getData().getUDI());
                            if (response.body().isSuccess()) {
                                String UDI= response.body().getData().getUDI();
                                utils.addPreference(getActivity(), JsonTags.UDI.name(),UDI);
                                Log.e("UDI",UDI);
                                PreferenceConstants.Udid=UDI;
                                getDiagnosticTestApiCall();

                            }
                        }

                        @Override
                        public void apiError(Response<SocketDataSync> response) {
                            if(getActivity()!=null) {
                                Toast.makeText(getActivity(),getResources().getText(R.string.api_error_toast),Toast.LENGTH_LONG).show();
                            }
                            CheckingDeviceActivity checkingDeviceActivity = (CheckingDeviceActivity) getActivity();
                            if(checkingDeviceActivity != null)
                                checkingDeviceActivity.setToolbarTitle2(getResources().getString(R.string.txtDASHBOARD));
                            Fragment f = new LandingFragment();
                            replaceFragment(R.id.frame,f,"Landing",false);


                        }

                        @Override
                        public void serverError(Throwable t) {
                            if(getActivity()!=null) {
                                Toast.makeText(getActivity(),getResources().getText(R.string.server_error_toast),Toast.LENGTH_LONG).show();
                            }
                            CheckingDeviceActivity checkingDeviceActivity = (CheckingDeviceActivity) getActivity();
                            if(checkingDeviceActivity != null)
                                checkingDeviceActivity.setToolbarTitle2(getResources().getString(R.string.txtDASHBOARD));
                            Fragment f = new LandingFragment();
                            replaceFragment(R.id.frame,f,"Landing",false);

                            if( t instanceof UnknownHostException)
                            {
                                Toast.makeText(getActivity(),getResources().getText(R.string.internet_error_toast),Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }
                catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } catch (Exception e) {

        }

    }


    void saveDeviceInfomationApiCall() {
        APIInterface apiInterface = APIClient.getClient(getActivity()).create(APIInterface.class);

        webService.apiCall(apiInterface.saveDaigonsticInfo(saveDataMap()),getActivity(), new WebServiceInterface<Response<SocketDataSync>>() {
            @Override
            public void apiResponse(Response<SocketDataSync> response) {
                if (response.isSuccessful()) {

//                        checkTradeAbility();

                            if (getActivity() != null) {
//                                    update(DiagnosticTestsList);

                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                intent.putExtra(QR_CODE_RESULT, storeID);
                                intent.putExtra("jsonArray", DiagnosticTestsList);
                                getActivity().startActivity(intent);
                                getActivity().finish();
                            }




                }else {
                    fabricLog("save_deviceinfo_api_eroor",response.toString());
                }
            }

            @Override
            public void apiError(Response<SocketDataSync> response) {
                Toast.makeText(getActivity(),getResources().getText(R.string.api_error_toast),Toast.LENGTH_LONG).show();

                CheckingDeviceActivity checkingDeviceActivity = (CheckingDeviceActivity) getActivity();
                if(checkingDeviceActivity != null)
                    checkingDeviceActivity.setToolbarTitle2(getResources().getString(R.string.txtDASHBOARD));
                Fragment f = new LandingFragment();
                replaceFragment(R.id.frame,f,"Landing",false);



            }

            @Override
            public void serverError(Throwable t) {
                if(getActivity()!=null) {
                    Toast.makeText(getActivity(), "Something went wrong Please Try again Later", Toast.LENGTH_LONG).show();
                }
                CheckingDeviceActivity checkingDeviceActivity = (CheckingDeviceActivity) getActivity();
                if(checkingDeviceActivity != null)
                    checkingDeviceActivity.setToolbarTitle2(getResources().getString(R.string.txtDASHBOARD));
                Fragment f = new LandingFragment();
                replaceFragment(R.id.frame,f,"Landing",false);

                if( t instanceof UnknownHostException)
                {
                    Toast.makeText(getActivity(),getResources().getText(R.string.internet_error_toast),Toast.LENGTH_LONG).show();                }
                fabricLog("save_deviceinfo_api_exception",t.getMessage());
                utils.addPreferenceBoolean(getActivity(), Constants.IS_SAVE_API_CALL, false);
            }
        });

    }


    public void checkTradeAbility(){
Log.d("verify check tradeable",utils.getPreference(getActivity(),Constants.SKUID,""));
        if(!utils.getPreference(getActivity(),Constants.SKUID,"").equalsIgnoreCase("")){
            if (getActivity() != null) {
//                                    update(DiagnosticTestsList);
                getDiagnosticTestApiCall();
//                Intent intent = new Intent(getActivity(), MainActivity.class);
//                intent.putExtra(QR_CODE_RESULT, storeID);
//                intent.putExtra("jsonArray", DiagnosticTestsList);
//                getActivity().startActivity(intent);
//                getActivity().finish();
            }
        }

        else {
            WebService webService = new WebService();
            APIInterface apiInterface =
                    APIClient.getClient(getActivity()).create(APIInterface.class);
            webService.apiCall(apiInterface.checkTradability(saveTradabilityMap()), getActivity(), new WebServiceInterface<Response<SocketDataSync>>() {
                @Override
                public void apiResponse(Response<SocketDataSync> response) {

                    if (response.isSuccessful()) {

                        if (response.body().isSuccess()) {


                            if (response.body().getData().isDeviceTradable()) {


                                getDiagnosticTestApiCall();

                                if (response.body().getData() != null && response.body().getData().getDevicePriceInfo() != null) {
                                    utils.addPreference(getActivity(), Constants.SKUID, response.body().getData().getDevicePriceInfo().getSKUID());
                                    Log.w("SKUID", response.body().getData().getDevicePriceInfo().getSKUID());
                                    utils.addPreference(getActivity(), Constants.DEVICEPRICE, response.body().getData().getDevicePriceInfo().getDeviceFullPrice());
                                }




                            } else {
                                Fragment fragment = new VerifyTradableManualFragment();
                                replaceFragment(R.id.frame, fragment,
                                        "", false);
                            }
                        }
                    } else {
                        Fragment fragment = new VerifyTradableManualFragment();
                        replaceFragment(R.id.frame, fragment,
                                "", false);
                    }
                }


                @Override
                public void apiError(Response<SocketDataSync> response) {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), "Something went wrong Please Try again Later", Toast.LENGTH_LONG).show();
                    }
                    CheckingDeviceActivity checkingDeviceActivity = (CheckingDeviceActivity) getActivity();
                    if (checkingDeviceActivity != null)
                        checkingDeviceActivity.setToolbarTitle2(getResources().getString(R.string.txtDASHBOARD));
                    Fragment f = new LandingFragment();
                    replaceFragment(R.id.frame, f, "Landing", false);

                }

                @Override
                public void serverError(Throwable t) {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), getResources().getText(R.string.server_error_toast), Toast.LENGTH_LONG).show();
                    }
                    CheckingDeviceActivity checkingDeviceActivity = (CheckingDeviceActivity) getActivity();
                    if (checkingDeviceActivity != null)
                        checkingDeviceActivity.setToolbarTitle2(getResources().getString(R.string.txtDASHBOARD));
                    Fragment f = new LandingFragment();
                    replaceFragment(R.id.frame, f, "Landing", false);

                    if (t instanceof UnknownHostException) {
                        Toast.makeText(getActivity(), getResources().getText(R.string.internet_error_toast), Toast.LENGTH_LONG).show();
                    }
                    fabricLog("save_deviceinfo_api_exception", t.getMessage());
                    utils.addPreferenceBoolean(getActivity(), Constants.IS_SAVE_API_CALL, false);
                }
            });
        }
    }

    public HashMap<String, Object> saveTradabilityMap(){
        HashMap<String, Object> map = new HashMap<>();
        try {

            map.put("Make", utils.getSecurePreference(getActivity(), "MMR_4", Build.MANUFACTURER));
            map.put("Model", utils.getSecurePreference(getActivity(), "MMR_5", Build.MODEL));
            map.put("Capacity", utils.getSecurePreference(getActivity(), "MMR_6", HardwareInfoFragment.getTotalInternalMemorySize()));
            map.put("IMEI", utils.getSecurePreference(getActivity(), "MMR_1", ""));


        } catch (Exception E) {
            E.printStackTrace();
        }
        return map;
    }







    public void verifyStoreApiCall() {
        try {
            WebService webService=new WebService();
            APIInterface apiInterface =
                    APIClient.getClient(getActivity()).create(APIInterface.class);

            webService.apiCall(apiInterface.verifyStore(new StoreVerifyModel(storeID,
                    enterprisePartnerID)), getActivity() ,new WebServiceInterface<Response<SocketDataSync>>() {
                @Override
                public void apiResponse(Response<SocketDataSync> response) {
                    Log.d("verify response", response.message());
                    if (response.body().isSuccess()) {
                        setSocket(true);
                        Utilities.getInstance(getActivity()).addPreference(getActivity(), Constants.TradeInValidityDays,response.body().getData().getTradeInValidityDays());
                        Utilities.getInstance(getActivity()).addPreference(getActivity(), Constants.QuoteExpireDays,response.body().getData().getQuoteExpireDays());
                        Utilities.getInstance(getActivity()).addPreference(getActivity(), Constants.LOCATION_ID,response.body().getData().getLocationID());
                        Utilities.getInstance(getActivity()).addPreference(getActivity(), Constants.CACHEMANAGERID,response.body().getData().getCacheManagerID());
                        Utilities.getInstance(getActivity()).addPreference(getActivity(), Constants.CURRENCYSYMBOL,response.body().getData().getCurrencySymbol());
                        Utilities.getInstance(getActivity()).addPreference(getActivity(), Constants.USERID,response.body().getData().getUserID());

//                        postDiagnosticsData();
                        checkTradeAbility();
                       // getDiagnosticTestApiCall();
                    } else {
                        setSocket(false);
                        Fragment f = new VerifyTradableManualFragment();
                        replaceFragment(R.id.frame, f, "VerifyManual", false);

                    }
                }

                @Override
                public void apiError(Response<SocketDataSync> response) {
                    setSocket(false);
                    if(getActivity()!=null)
                        Toast.makeText(getActivity(),"Something went wrong Please Try again Later",Toast.LENGTH_LONG).show();
                    CheckingDeviceActivity checkingDeviceActivity = (CheckingDeviceActivity) getActivity();
                    if(checkingDeviceActivity != null)
                        checkingDeviceActivity.setToolbarTitle2(getResources().getString(R.string.txtDASHBOARD));
                    Fragment f = new LandingFragment();
                    replaceFragment(R.id.frame, f, "VerifyManual", false);

                }

                @Override
                public void serverError(Throwable t) {
                    if(getActivity()!=null)
                        Toast.makeText(getActivity(),getResources().getText(R.string.server_error_toast),Toast.LENGTH_LONG).show();
                    CheckingDeviceActivity checkingDeviceActivity = (CheckingDeviceActivity) getActivity();
                    if(checkingDeviceActivity != null)
                        checkingDeviceActivity.setToolbarTitle2(getResources().getString(R.string.txtDASHBOARD));
                    Fragment f = new LandingFragment();
                    replaceFragment(R.id.frame,f,"Landing",false);

                    if( t instanceof UnknownHostException)
                    {
                        Toast.makeText(getActivity(),getResources().getText(R.string.internet_error_toast),Toast.LENGTH_LONG).show();                    }
                }
            });
        } catch (Exception e) {

        }


    }

    void setSocket(boolean istradable){
        try {
            String androidId = Utilities.getInstance(getActivity()).getPreference(getActivity(),
                    Constants.ANDROID_ID, "");
            IO.Options options = new IO.Options();
            SocketHelper socketHelper =
                    new SocketHelper.Builder(WebserviceUrls.BaseUrl + "?" + Constants.REQUEST_UNIQUE_ID + "=" + androidId, options)
                            .addEvent(SocketConstants.EVENT_CONNECTED)
                            .addListener(null)
                            .build();
            socketHelper.setSocketConnect(false);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    public void update(ArrayList<DiagnosticListPojo> diagnos) {
        Log.d("scheduler==","okkk");
        Intent intent = new Intent(getActivity(), TestNameUpdateService.class);
        intent.putExtra("jsonArray", diagnos);
        getActivity().startService(intent);

    }
}
