package com.officework.testing_profiles.ui.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.officework.ApiController.APIClient;
import com.officework.ApiController.APIInterface;
import com.officework.R;
import com.officework.base.BaseFragment;
import com.officework.constants.Constants;
import com.officework.constants.JsonTags;
import com.officework.fragments.TitleBarFragment;
import com.officework.models.SocketDataSync;
import com.officework.utils.Utilities;
import com.officework.utils.socket.SocketConstants;
import com.officework.utils.socket.SocketHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.socket.client.IO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.officework.utils.Utilities.getInstance;


/**
 * Created by igniva-android-17 on 25/5/18.
 */
public class OrderInformationFragment extends BaseFragment {

    Unbinder unbinder;
    @BindView(R.id.message)
    TextView message;
    @BindView(R.id.price)
    TextView price;
    @BindView(R.id.order_id)
    TextView orderId;


    private String TAG = "ChatActivity";
    private String offer_price;
    private String order_Id;
    private String qrCodeiD;
    private String androidId;
    private ProgressDialog mProgressDialog;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //init Presenter
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_order, container, false);
        unbinder = ButterKnife.bind(this, view);
        if (getArguments() != null) {

            offer_price = getArguments().getString("OFFER_PRICE");
            order_Id = getArguments().getString("ORDER_ID");
            price.setText(offer_price);
            orderId.setText(order_Id);

        }
        try {
            TitleBarFragment fragment = (TitleBarFragment) getFragment(R.id.headerContainer);
            if (fragment != null) {
                fragment.setCancelVisible();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        emitScanCompleteEvent();
        return view;
    }

    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        return null;
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


    @OnClick(R.id.complete)
    public void onViewClicked() {
        try{
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
            getActivity().finish();
        }catch (Exception e){
            e.printStackTrace();
        }
//       completeOrderApiCall();
    }
    void completeOrderApiCall() {
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setMessage("Syncing");

        mProgressDialog.show();
        APIInterface apiInterface = APIClient.getClient(getActivity()).create(APIInterface.class);

        HashMap<String, Object> map = createMap();
        Call<SocketDataSync> call1 = apiInterface.completeOrder(map);

        call1.enqueue(new Callback<SocketDataSync>() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onResponse(Call<SocketDataSync> call1, Response<SocketDataSync> response) {
                Log.d("Success", response.toString());
                try {
                    if (response.code() == 200) {


                        if (response.body().isSuccess()) {


                            Utilities.getInstance(getActivity()).showAlert(getActivity(),
                                    new Utilities.onAlertOkListener() {
                                        @Override
                                        public void onOkButtonClicked(String tag) {


                                            try {
                                                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                                    mProgressDialog.dismiss();
                                                }
                                                if (socketHelper != null && socketHelper.socket.connected()) {
                                                    JSONObject jsonObject = new JSONObject();

                                                    jsonObject.put(Constants.UNIQUE_ID, androidId);
                                                    jsonObject.put(Constants.QRCODEID, qrCodeiD);
                                                    jsonObject.put(Constants.ORDER_ID, order_Id);
                                                    socketHelper.emitData(SocketConstants.ORDER_MAIL_SENT, jsonObject);
                                                }
                                                if (socketHelper != null) {
                                                    socketHelper.destroy();
                                                }
                                                Intent startMain = new Intent(Intent.ACTION_MAIN);
                                                startMain.addCategory(Intent.CATEGORY_HOME);
                                                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(startMain);
                                                getActivity().finish();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }, getResources().getString(R.string.order_confirmed),
                                    getResources().getString(R.string.app_name), "Ok", "", "");


                        }else
                        {
                            fabricLog("OrderInforamtion_frag_complete_order_api_eror",response.toString());
                        }

                    }


                } catch (Exception e) {
                    fabricLog("OrderInforamtion_frag_complete_order_api_exception",e.getMessage());
                    requestFailure();
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<SocketDataSync> call1, Throwable t) {

                try {
                    requestFailure();

                    fabricLog("OrderInforamtion_frag_complete_order_api_exception",t.getMessage());
                    Log.d("error", t.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void requestFailure() {
        Utilities.getInstance(getActivity()).showAlert(getActivity(),
                new Utilities.onAlertOkListener() {
                    @Override
                    public void onOkButtonClicked(String tag) {
                        try {
                            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                                mProgressDialog.dismiss();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, getResources().getString(R.string.api_error_toast),
                getResources().getString(R.string.app_name), "Ok", "", "");

    }


    private HashMap<String, Object> createMap() {
        HashMap<String, Object> map = new HashMap<>();

        try {
            String qrCodeiD = Utilities.getInstance(getActivity()).getPreference(getActivity(),
                    Constants.QRCODEID, "");

            String androidId = Utilities.getInstance(getActivity()).getPreference(getActivity(),
                    Constants.ANDROID_ID, "");

            map.put("UniqueID", androidId);
            map.put("QrCodeID", qrCodeiD);
            map.put("OrderID", order_Id);
            map.put("UDI",Utilities.getInstance(getActivity()).getPreference(getActivity(), JsonTags.UDI.name(),""));
            map.put("SubscriberProductCode","CAP2");


        } catch (Exception E) {
            E.printStackTrace();
        }
        return map;
    }

    public void emitScanCompleteEvent() {
        try {
            qrCodeiD = getInstance(getActivity()).getPreference(getActivity(),
                    Constants.QRCODEID, "");

            JSONObject jsonObject = new JSONObject();
            androidId = getInstance(getActivity()).getPreference(getActivity(),
                    Constants.ANDROID_ID, "");

            jsonObject.put(Constants.UNIQUE_ID, androidId);
            jsonObject.put(Constants.QRCODEID, qrCodeiD);
            jsonObject.put(Constants.ORDER_ID, order_Id);
            IO.Options options = new IO.Options();

            socketHelper =
                    new SocketHelper.Builder(SocketConstants.HOST_NAME1 + "?" + Constants.REQUEST_UNIQUE_ID + "=" + androidId, options)
                            .addEvent(SocketConstants.EVENT_CONNECTED)
                            .addListener(null)
                            .build();
            if (socketHelper != null && socketHelper.socket.connected()) {
                socketHelper.emitData(SocketConstants.DEVICE_SCAN_COMPLETE, jsonObject);
            }
            //  socketHelper.destroy();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}