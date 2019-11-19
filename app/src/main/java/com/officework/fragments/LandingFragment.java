package com.officework.fragments;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.officework.activities.CheckingDeviceActivity;
import com.officework.R;
import com.officework.Services.OnClearFromRecentService;
import com.officework.activities.ScanActivity;
import com.officework.base.BaseFragment;
import com.officework.constants.Constants;
import com.officework.constants.JsonTags;
import com.officework.testing_profiles.realmDatabase.operationManager.RealmOperations;
import com.officework.utils.Utilities;
import com.officework.utils.WebserviceUrls;
import com.officework.utils.socket.SocketConstants;
import com.officework.utils.socket.SocketHelper;

import java.util.UUID;

import io.socket.client.IO;


public class LandingFragment extends BaseFragment {

    SensorManager sensorManager;
    boolean faceexists = true;
    boolean isFingerprintExist = true;
    private ImageView imageView3;
    private Button scan;
    private Utilities utils;
    private String store_ID;
    private RealmOperations realmOperations;
    private String androidId, diagnosresult;
    int SERVICE_REQUEST_ID = -1;
    String SubscriberProductID;
    ProgressDialog progressBar;
    public BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctxt, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            utils.addPreference(getActivity(), JsonTags.MMR_68.name(), String.valueOf(level) + "%");
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //init Presenter
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_landing, container, false);
        utils = Utilities.getInstance(getActivity());
        imageView3 = (ImageView) view.findViewById(R.id.imageView3);
        scan = (Button) view.findViewById(R.id.scan);
//        try {
//            LandingActivity landingActivity = (LandingActivity) getActivity();
//            if (landingActivity != null) {
//                if (!landingActivity.isDeviceTradeableApiHit) {
//                    checkTradeAbility();
//                }
//            }
//        }catch (Exception e)
//        {
//            e.printStackTrace();
//        }
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent i= new Intent(getActivity(), ScanActivity.class);
                startActivity(i);

            }
        });
        SubscriberProductID= utils.getPreference(getActivity(), JsonTags.SubscriberProductID.name(), "");
        store_ID = utils.getPreference(getActivity(), Constants.STOREID, "");// store id
//        if (store_ID != null && store_ID.length() > 0) {
//            if (Utilities.getInstance(getActivity()).isInternetWorking(getActivity())) {
//                startScan();
//                utils.loadDatabase(getActivity());
//                getActivity().startService(new Intent(getActivity().getBaseContext(),
//                        OnClearFromRecentService.class));
//                realmOperations = new RealmOperations();
//                realmOperations.resetTestStatus();
//                utils.checkSensors(getActivity());
//                Utilities.getInstance(getActivity()).addPreference(getActivity(),
//                        Constants.QRCODEID, store_ID);
////                Intent intent = new Intent(getActivity(), CheckingDeviceActivity.class);
////                intent.putExtra("jsonArray", diagnosresult);
////                getActivity().startActivity(intent);
//                getActivity().unregisterReceiver(this.mBatInfoReceiver);
//            }
//        }



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

    }


    private void startScan() {
try {
    if (Utilities.getInstance(getActivity()).getPreference(getActivity(),
            Constants.ANDROID_ID, "").isEmpty()) {
        androidId = UUID.randomUUID().toString();
        Utilities.getInstance(getActivity()).addPreference(getActivity(),
                Constants.ANDROID_ID, androidId);

    } else {
        androidId = Utilities.getInstance(getActivity()).getPreference(getActivity(),
                Constants.ANDROID_ID, "");

    }
    if (Utilities.getInstance(getActivity()).isInternetWorking(getActivity())) {
        try {
            String androidId = Utilities.getInstance(getActivity()).getPreference(getActivity(),
                    Constants.ANDROID_ID, "");
            String qrCodeiD = Utilities.getInstance(getActivity()).getPreference(getActivity(),
                    JsonTags.StoreID.name(), "");
            IO.Options options = new IO.Options();
            Log.d("scan android id", androidId);
            SocketHelper socketHelper =
                    new SocketHelper.Builder(WebserviceUrls.BaseUrl + "?" + Constants.REQUEST_UNIQUE_ID + "=" + androidId, options)
                            .addEvent(SocketConstants.EVENT_CONNECTED)
                            .addListener(null)
                            .build();

            if (socketHelper != null && socketHelper.socket.connected()) {

                socketHelper.emitScreenChangeEvent("scanner", androidId, qrCodeiD);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    fetchAndroidId();
}catch (Exception e)
{e.printStackTrace();}

    }

    private void fetchAndroidId() {

//        Constants.isDoAllClicked = false;
        if (Utilities.getInstance(getActivity()).getPreference(getActivity(),
                Constants.ANDROID_ID, "").isEmpty()) {
            androidId = UUID.randomUUID().toString();
            Utilities.getInstance(getActivity()).addPreference(getActivity(),
                    Constants.ANDROID_ID, androidId);

        } else {
            getActivity().registerReceiver(this.mBatInfoReceiver,
                    new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        }

    }


    public void onBackPress() {
        try {
            Utilities.getInstance(getActivity()).showAlert(getActivity(), new Utilities.onAlertOkListener() {
                @Override
                public void onOkButtonClicked(String tag) {

                    getActivity().finishAffinity();
                }
            }, Html.fromHtml(getResources().getString(R.string.exit_msg)), getResources().getString(R.string.txtAlertTitleGreatAlert),  "No", "Yes", "Sync");
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }









}