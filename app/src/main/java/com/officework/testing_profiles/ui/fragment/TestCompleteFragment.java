package com.officework.testing_profiles.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.officework.ApiController.APIClient;
import com.officework.ApiController.APIInterface;
import com.officework.ApiController.WebService;
import com.officework.ApiController.WebServiceInterface;
import com.officework.R;
import com.officework.activities.MainActivity;
import com.officework.activities.WelcomeActivity;
import com.officework.adapters.TestReportAdapter;
import com.officework.base.BaseFragment;
import com.officework.constants.Constants;
import com.officework.constants.FragmentTag;
import com.officework.constants.JsonTags;
import com.officework.fragments.TitleBarFragment;
import com.officework.models.AutomatedTestListModel;
import com.officework.models.DataPojo;
import com.officework.models.ScannedDevicesRequestModel;
import com.officework.models.SocketDataSync;
import com.officework.testing_profiles.realmDatabase.operationManager.RealmOperations;
import com.officework.utils.Utilities;
import com.officework.utils.socket.SocketConstants;
import com.officework.utils.socket.SocketHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.socket.client.IO;
import retrofit2.Response;


public class TestCompleteFragment extends BaseFragment {


    @BindView(R.id.price)
    TextView price;
    @BindView(R.id.deductions)
    TextView deductions;
    @BindView(R.id.offer)
    TextView offer;
//    @BindView(R.id.card_view)
//    CardView cardView;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.cancel)
    Button cancel;
    @BindView(R.id.proceed)
    Button proceed;
    @BindView(R.id.termslayout)
    LinearLayout termLayout;
    @BindView(R.id.main_parent)
    LinearLayout mLvMainParent;
    @BindView(R.id.no_record)
    TextView mTvNoRecordFound;
    @BindView(R.id.order_id)
    TextView mTvOrderId;
    @BindView(R.id.order_id_text)
    TextView mTvOrderText;
    @BindView(R.id.progressBarMedium)
    ProgressBar progressBar;
    ArrayList<AutomatedTestListModel> objects = new ArrayList<>();
    private Unbinder unbinder;
    private TestReportAdapter adapter;
    String order_id,udi_id;
    private String net_price,offer_price,price_deduction;
    private String deduction_amount;
    private String deduction_group;
    JSONArray jsonArray = new JSONArray();


    private  RealmOperations realmOperations;
    private DataPojo socketDataSync;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //init Presenter
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_test_complete, container, false);

        unbinder = ButterKnife.bind(this, view);
       MainActivity mainActivity=(MainActivity)getActivity();
        mainActivity.onChangeText(R.string.textSkip, false);

        if (getArguments() != null) {
             socketDataSync = (DataPojo) getArguments().getParcelable("OFFER_PRICE_OBJECT");
            Utilities utils = Utilities.getInstance(getActivity());

            if(socketDataSync != null){
                price.setText(socketDataSync.getDevicePrice());
                deductions.setText(socketDataSync.getPercentageDeduction()+ "%");
                offer.setText(socketDataSync.getQuotedPrice());
            }
          //  price.setVisibility(View.GONE);
            price_deduction = socketDataSync.getPercentageDeduction();
            net_price = Utilities.getInstance(getActivity()).getPreference(getActivity(),Constants.DEVICEPRICE,"");
            offer_price = socketDataSync.getQuotedPrice();
            //order_id = getArguments().getString("ORDER_ID");
            deduction_amount = socketDataSync.getPercentageDeduction();
           // deduction_group = getArguments().getString("DeductionGroup");

            udi_id=getArguments().getString("UDIID");
            String currency=Utilities.getInstance(getActivity()).getPreference(getActivity(),Constants.CURRENCYSYMBOL, "");
            Log.d("currency==",currency);
            price.setText(currency+" "+net_price);
            deductions.setText(price_deduction + "%");
            offer.setText(currency+" "+offer_price);

            mTvOrderId.setVisibility(View.GONE);
            mTvOrderText.setVisibility(View.GONE);
            mTvNoRecordFound.setVisibility(View.GONE);
            mLvMainParent.setVisibility(View.VISIBLE);
            initList();
        } else {

            termLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            mTvNoRecordFound.setVisibility(View.VISIBLE);


            if (Utilities.getInstance(getActivity()).isInternetWorking(getActivity())) {
                getOrderDetail();
            } else {
                Toast.makeText(getActivity(),
                        getResources().getString(R.string.please_check_internet_connection),
                        Toast.LENGTH_SHORT).show();

                progressBar.setVisibility(View.GONE);
                mTvNoRecordFound.setVisibility(View.VISIBLE);
                mTvNoRecordFound.setText(getResources().getString(R.string.noInternet));
                mLvMainParent.setVisibility(View.GONE);
            }

        }

        realmOperations = new RealmOperations();


        initAdapter();
        emitReportEvent();

        return view;
    }

    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        return null;
    }


    public void initList() {




    }

    private void initAdapter() {

        objects=  realmOperations.fetchfailTestsListAllList(MainActivity.json_array,getActivity());


        for(int index = 0;index<objects.size();index++){
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("TestStatus", objects.get(index).getIsTestSuccess());
                jsonObject.put("TestName", objects.get(index).getName());
                jsonArray.put(jsonObject);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        adapter = new TestReportAdapter(getActivity(), objects);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

//        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if (b) {
//                    emitPrivacyEvent(true);
//                    proceed.setBackground(getResources().getDrawable(R.drawable.blue_bg));
//                } else {
//                    emitPrivacyEvent(false);
//                    proceed.setBackground(getResources().getDrawable(R.drawable.gray_bg));
//
//                }
//            }
//        });
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void getOrderDetail() {
        WebService webService=new WebService();
        if (Utilities.getInstance(getActivity()).getPreference(getActivity(),
                Constants.ANDROID_ID, "").isEmpty()) {
// androidId = Settings.Secure.getString(getActivity().getContentResolver(),
// Settings.Secure.ANDROID_ID);
            String androidId = UUID.randomUUID().toString();
            Utilities.getInstance(getActivity()).addPreference(getActivity(),
                    Constants.ANDROID_ID, androidId);
        }
        String androidId = Utilities.getInstance(getActivity()).getPreference(getActivity(),
                Constants.ANDROID_ID, "");
        APIInterface apiInterface = APIClient.getClient(getActivity()).create(APIInterface.class);

        webService.apiCall(apiInterface.getOrderdetailBydeviceId(new ScannedDevicesRequestModel("CA", Utilities.getInstance(getActivity()).getPreference(getActivity(), JsonTags.UDI.name(), ""))),getActivity() , new WebServiceInterface<Response<SocketDataSync>>() {
            @Override
            public void apiResponse(Response<SocketDataSync> response) {
                Log.d("Success", response.toString());
                try {
                    progressBar.setVisibility(View.GONE);
                    if (response.isSuccessful()) {
                        SocketDataSync socketDataSync = response.body();
                        if (socketDataSync != null) {
                            if (socketDataSync.isSuccess()) {

                                mTvNoRecordFound.setVisibility(View.GONE);
                                mLvMainParent.setVisibility(View.VISIBLE);
                                DataPojo dataPojo = socketDataSync.getData();
// objects.addAll(dataPojo.getTestResult());

                                adapter.notifyDataSetChanged();

                                price.setText("$"+ dataPojo.getDevicePrice());
                                deductions.setText(dataPojo.getDeductionPercentage() + "%");
                                offer.setText("$"+dataPojo.getOfferPrice());
                                mTvOrderId.setText(dataPojo.getOrderID());

                            } else {
                                mTvNoRecordFound.setVisibility(View.VISIBLE);
                                mTvNoRecordFound.setText(getResources().getString(R.string.no_record_found));
                                mLvMainParent.setVisibility(View.GONE);
                            }
                        }

                    }else {
                        fabricLog("Test_complete_order_fragmet_order_detail_by_using_id api_error",response.toString());

                    }


                } catch (Exception e) {
                    fabricLog("Test_complete_order_fragmet_order_detail_by_using_id api_exception",e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void apiError(Response<SocketDataSync> response) {
                mTvNoRecordFound.setVisibility(View.VISIBLE);
                mTvNoRecordFound.setText(getResources().getString(R.string.no_record_found));
                mLvMainParent.setVisibility(View.GONE);
            }

            @Override
            public void serverError(Throwable t) {
                try {
                    if( t instanceof UnknownHostException)
                    {
                        Toast.makeText(getActivity(),"There is something went wrong with your Internet Connection Please reset your Internet Connection and try again",Toast.LENGTH_LONG).show();
                    }
                    progressBar.setVisibility(View.GONE);
                    mTvNoRecordFound.setText(getResources().getString(R.string.no_record_found));
                    mTvNoRecordFound.setVisibility(View.VISIBLE);
                    fabricLog("Test_complete_order_fragmet_order_detail_by_using_id api_exception",t.getMessage());
                    Log.d("error", t.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    public void onBackPress() {
        Utilities.getInstance(getActivity()).showAlert(getActivity(), new Utilities.onAlertOkListener() {
            @Override
            public void onOkButtonClicked(String tag) {
                Intent intent=new Intent(getActivity(), WelcomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
//                getActivity().setResult(Activity.RESULT_OK);
//                getActivity().finish();
            }
        }, Html.fromHtml(getResources().getString(R.string.rescan_msg)), getResources().getString(R.string.rescan_head), "No", "Yes", "Sync");

    }

    @OnClick({R.id.cancel, R.id.proceed})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                Utilities.getInstance(getActivity()).showAlert(getActivity(),
                        new Utilities.onAlertOkListener() {
                            @Override
                            public void onOkButtonClicked(String tag) {
                                Intent intent=new Intent(getActivity(), WelcomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
//                                getActivity().setResult(Activity.RESULT_OK);
//                                getActivity().finish();
                            }
                        }, Html.fromHtml(getResources().getString(R.string.rescan_msg)), getResources().getString(R.string.rescan_head),
                        "No", "Yes", "Sync");


                break;
            case R.id.proceed:



                    CustomerInformationFragment customerInformationFragment =
                            new CustomerInformationFragment();

                    Bundle bundle = new Bundle();

                    bundle.putParcelable("OFFER_PRICE_OBJECT",socketDataSync);

                    bundle.putString("OFFER_PRICE", offer.getText().toString());
//                    bundle.putString("ORDER_ID",order_id);
//                    bundle.putString("UDIID",udi_id);
                    customerInformationFragment.setArguments(bundle);
                    replaceFragment(R.id.container, customerInformationFragment,
                            FragmentTag.MANUAL_TEST_FRAGMENT.name(), false);


                    TitleBarFragment fragment =
                            (TitleBarFragment) getFragment(R.id.headerContainer);
                    if (fragment != null) {

                        fragment.setTitleBarVisibility(true);
                        fragment.showSwitchLayout(false);
                        fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string.customer_info), true, false, 0);

                    }

                    emitScreenEvent();


                break;

        }
    }
    private void emitScreenEvent() {
        try {
            String qrCodeiD = Utilities.getInstance(getActivity()).getPreference(getActivity(), Constants.QRCODEID, "");

            JSONObject jsonObject = new JSONObject();
            String  androidId = Utilities.getInstance(getActivity()).getPreference(getActivity(), Constants.ANDROID_ID, "");

            jsonObject.put(Constants.UNIQUE_ID, androidId);
            jsonObject.put(Constants.QRCODEID, qrCodeiD);
            IO.Options options = new IO.Options();


            if(socketHelper != null && socketHelper.socket.connected()) {
                socketHelper.emitScreenChangeEvent(SocketConstants.CUSTOMER_INFO, androidId,qrCodeiD);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }




    private void emitReportEvent() {
        try {
            String qrCodeiD = Utilities.getInstance(getActivity()).getPreference(getActivity(), Constants.QRCODEID, "");

            JSONObject jsonObject = new JSONObject();
            String  androidId = Utilities.getInstance(getActivity()).getPreference(getActivity(), Constants.ANDROID_ID, "");

            jsonObject.put(Constants.UNIQUE_ID, androidId);
            jsonObject.put(Constants.QRCODEID, qrCodeiD);
            jsonObject.put("OrderID",order_id);
            jsonObject.put("DevicePrice",net_price);
            jsonObject.put("DeductionPercentage",price_deduction);
            jsonObject.put("DeductionAmount",deduction_amount);
            jsonObject.put("DeductionGroup",deduction_group);
            jsonObject.put("OfferPrice",offer_price);
            jsonObject.put("TestResult",jsonArray);


            IO.Options options = new IO.Options();

            socketHelper = new SocketHelper.Builder(SocketConstants.HOST_NAME1+"?"+Constants.REQUEST_UNIQUE_ID+"="+androidId, options)
                    .addEvent(SocketConstants.EVENT_CONNECTED)
                    .addListener(null)
                    .build();
            if(socketHelper != null && socketHelper.socket.connected()) {
                socketHelper.emitData(SocketConstants.EVENT_SENDREPORT,jsonObject);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void emitPrivacyEvent(boolean fieldValue) {
        try {
            String qrCodeiD = Utilities.getInstance(getActivity()).getPreference(getActivity(), Constants.QRCODEID, "");

            JSONObject jsonObject = new JSONObject();
            String androidId = Utilities.getInstance(getActivity()).getPreference(getActivity(), Constants.ANDROID_ID, "");

            jsonObject.put(Constants.UNIQUE_ID, androidId);
            jsonObject.put(Constants.QRCODEID, qrCodeiD);
            jsonObject.put("Status", fieldValue);




            if(socketHelper != null && socketHelper.socket.connected()) {
                socketHelper.emitData(SocketConstants.EVENT_TERMS_CONDITION,jsonObject);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
