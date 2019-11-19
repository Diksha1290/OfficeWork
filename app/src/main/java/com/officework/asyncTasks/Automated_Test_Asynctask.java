package com.officework.asyncTasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.officework.constants.AsyncConstant;
import com.officework.fragments.GetQRCodeFragment;
import com.officework.fragments.SystemInfoFragment;
import com.officework.interfaces.WebServiceCallback;
import com.officework.models.WebServiceRequestModel;
import com.officework.testing_profiles.Controller.AutomateDeviceTest;
import com.officework.utils.AutomatedTestUtils;
import com.officework.utils.Utilities;


/**
 * Created by girish on 8/8/2016.
 */
public class Automated_Test_Asynctask extends AsyncTask<Void, Void, Void> {

    Context ctx;
    WebServiceCallback listener;
    ProgressDialog bar;
    WebServiceRequestModel requestModel;
    int callbackID = 0;
    boolean status = false;
    AutomatedTestUtils automatedTestUtils;
    Activity mActivity;
    Utilities utilities;

    public Automated_Test_Asynctask(Activity activity, Context ctx, WebServiceCallback listener,
                                    int callbackID) {
        this.ctx = ctx;
        this.listener = listener;
        this.requestModel = requestModel;
        this.callbackID = callbackID;
        automatedTestUtils = AutomatedTestUtils.getInstance(ctx);
        mActivity = activity;
        utilities = Utilities.getInstance(ctx);
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        bar = new ProgressDialog(ctx);
        /*bar.setTitle(requestModel.getDialogTitle());*/
        bar.setCancelable(false);
        /*bar.setMessage(TextUtils.isEmpty(requestModel.getDialogMessage()) ? ctx.getResources()
        .getString(R.string.loaderMsg) : requestModel.getDialogMessage());
        if (requestModel.isDialogEnabled())
            bar.show();*/
    }

    @Override
    protected Void doInBackground(Void... params) {
        switch (callbackID) {
            case AsyncConstant.VIBRATION_TEST:
                automatedTestUtils.checkVibrator(ctx, listener);
                break;

            case AsyncConstant.BLUETOOTH_TEST:
                status = automatedTestUtils.checkBluetooth(mActivity, listener);
                break;

            case AsyncConstant.BLUETOOTH__DISABLE_TEST:
                status = automatedTestUtils.BluetoothDisable();
                break;


            case AsyncConstant.WIFI_TEST:
                status = automatedTestUtils.checkWiFi(mActivity);
                break;

            case AsyncConstant.SIMCARD_TEST:
                status = automatedTestUtils.checkSimCard(mActivity);
                break;

            case AsyncConstant.SDCARD_TEST:
                status = automatedTestUtils.checkSDCard(ctx);
                break;

            case AsyncConstant.KILLSWITCH_TEST:
                if (GetQRCodeFragment.checkDeviceManger(mActivity) == AsyncConstant.TEST_FAILED) {
                    status = false;
                } else {
                    status = true;
                }
                break;

            case AsyncConstant.FMIP:
                /**
                 * This is check That is Device is not rooted
                 * if(Rooted)
                 * {false
                 * }else{
                 * true
                 * }
                 * */
                if (SystemInfoFragment.isDeviceRooted()) {
                    status = false;
                } else {
                    status = true;
                }
                break;

            case AsyncConstant.CALL_FUNCTION:
                if (AutomateDeviceTest.getSimCardStatus(mActivity, ctx, utilities)) {
                    status = AutomateDeviceTest.getDeviceNetworkAvailable(ctx);
                } else {
                    status = false;
                }
                break;
            case AsyncConstant.LIGHT_SENSOR_FUNCTION:
                status = AutomateDeviceTest.getLightSensorStatus(ctx);
                break;

            case AsyncConstant.Barometer:
                status = AutomateDeviceTest.getBarometerSensorStatus(ctx);
                break;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (bar.isShowing())
            bar.dismiss();
        if (callbackID == AsyncConstant.VIBRATION_TEST) {

        } else {
            listener.onServiceResponse(status, "", callbackID);
        }
    }
}
