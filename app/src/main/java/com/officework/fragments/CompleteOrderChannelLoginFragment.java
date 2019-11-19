package com.officework.fragments;


import android.content.Context;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.officework.R;
import com.officework.base.BaseFragment;
import com.officework.constants.JsonTags;
import com.officework.interfaces.WebServiceCallback;
import com.officework.models.ChannelLoginModel;
import com.officework.utils.Utilities;
import com.officework.utils.WebServiceUrlsAbacusAPI;

import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class CompleteOrderChannelLoginFragment extends BaseFragment implements WebServiceCallback {

    View view;
    Utilities utils;
    Context ctx;
    int SERVICE_REQUEST_ID = -1;
    Button btnLoginChannel;
    EditText editTextChannelCode, editTextStoreCode;

    public CompleteOrderChannelLoginFragment() {
        // Required empty public constructor
    }

    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_complete_order_channel_login, null);
            utils = Utilities.getInstance(getActivity());
            ctx = getActivity();
         //   Crashlytics.getInstance().log(FragmentTag.COMPLETE_ORDER_CHANNEL_LOGIN.name());
            initViews();
        }
        return view;
    }

    private void initViews() {
        btnLoginChannel = (Button) view.findViewById(R.id.btnLoginChannel);
        btnLoginChannel.setOnClickListener(this);
        editTextStoreCode = (EditText) view.findViewById(R.id.editTextStoreCode);
        editTextChannelCode = (EditText) view.findViewById(R.id.editTextChannelCode);
    }

    /**
     * This is used to post result set to API
     * post json data to server
     */
    private void loginApiRequest() {

        try {
            if (utils.isInternetWorking(ctx)) {
                SERVICE_REQUEST_ID = 1;
                Utilities.serviceCallsAbacusAPI(ctx, WebServiceUrlsAbacusAPI.LoginApiUrl, false, "", true, SERVICE_REQUEST_ID,
                        (WebServiceCallback) this, false, returnChannelModelData());
            } else {
            }
        } catch (Exception e) {

        }

    }

    public ChannelLoginModel returnChannelModelData() {
        ChannelLoginModel channelLoginModel = new ChannelLoginModel();
        channelLoginModel.setPartnerCode(editTextChannelCode.getText().toString().trim());
        channelLoginModel.setStoreCode(editTextStoreCode.getText().toString().trim());
        return channelLoginModel;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLoginChannel:
                if (utils.isNullorEmpty(editTextChannelCode.getText().toString())) {
                    utils.showToast(ctx, ctx.getResources().getString(R.string.txtEnterChannelCode));
                } else if (utils.isNullorEmpty(editTextStoreCode.getText().toString())) {
                    utils.showToast(ctx, ctx.getResources().getString(R.string.txtEnterStoreCode));
                } else {
                    getToken();
                }

                break;
        }
        super.onClick(v);
    }

    @Override
    public void onServiceResponse(boolean serviceStatus, String response, int callbackID) {
        if (serviceStatus) {
            try {

                switch (callbackID) {

                    case 0:
                        JSONObject responseObj = new JSONObject(response);
                        utils.addPreference(ctx, JsonTags.access_token_abacus_api.name(), responseObj.getString(JsonTags.access_token.name()));
                        utils.addPreference(ctx, JsonTags.token_type_abacus_api.name(), responseObj.getString(JsonTags.token_type.name()));
                        loginApiRequest();
                        break;
                    case 1:

                        if (response == editTextStoreCode.getText().toString().trim()) {

                        }
                }
            } catch (Exception e) {
            }
        }
    }


    /**
     * This is an token request API
     * If network is not available
     * We simply intent to MainActivity
     * In main activity onNetworkChanged listener is register
     * This register monitor change in network state
     * Get token from server
     */

    private void getToken() {
        try {
            if (utils.isInternetWorking(ctx)) {
                SERVICE_REQUEST_ID = 0;
                Utilities.serviceCallsAbacusAPI(ctx, WebServiceUrlsAbacusAPI.DiagnosticDataTokenURL, true, "", true, SERVICE_REQUEST_ID,
                        (WebServiceCallback) this, true, null);

            } else {

            }
        } catch (Exception e) {

        }


    }

}
