package com.officework.fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.officework.R;
import com.officework.base.BaseFragment;
import com.officework.gpstracker.GPSTracker;
import com.officework.interfaces.InterfaceAlertDissmiss;
import com.officework.interfaces.InterfaceButtonTextChange;
import com.officework.interfaces.InterfaceShowGPSDialog;
import com.officework.utils.ManualDataStable;
import com.officework.utils.TestResultUpdateToServer;
import com.officework.utils.Utilities;

/**
 * This map screen will show user his,her current location
 */
@SuppressLint("ValidFragment")
public class GpsMap extends BaseFragment {

    private GoogleMap mMap;
    private View view;
    private Context mContext;
    private GPSTracker gpsTracker;
    ImageView mImgViewSatellite, mImgViewCurrentPosition;
    private boolean toggleMapType = false;
    private static int mapType = 0;
    private static final int PERMISSION_REQUEST_CODE_LOCATION = 123;
    InterfaceButtonTextChange mCallBack;
    Utilities utils;
    Handler handler = null;
    Runnable runnable;
    LayoutInflater inflaterr;
    private CoordinatorLayout coordinatorLayout;
    ManualDataStable manualDataStable;
    private boolean isSecondTimeMap = false;
    public static boolean isNextTest = false;
    boolean isResumeNext = false;
    Snackbar snackbar;
    Handler nextButtonHandler = null;
    TextView gpsCount;
    CountDownTimer cTimer = null;
    boolean isDialogShown = false;
    InterfaceAlertDissmiss listernerDialog;
    AlertDialog alertDialog;
    TestResultUpdateToServer testResultUpdateToServer;
    boolean isResumeSkip = false;
    Button btnGpsFail, btnGpsPass;
    private boolean isFirstTime = false;

    public GpsMap(InterfaceButtonTextChange callback) {
        // Required empty public constructor
        mCallBack = callback;
    }

    public GpsMap() {
    }

    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        try {
            if (view == null) {
                view = inflater.inflate(R.layout.mapfragment, null);
                mContext = getActivity();
                /*gpsLayout = (FrameLayout) view.findViewById(R.id.gpsLayout);*/
           //     Crashlytics.getInstance().log(FragmentTag.GPS_FRAGMENT.name());
                utils = Utilities.getInstance(getActivity());
                //checkLocationPermission();
                testResultUpdateToServer = new TestResultUpdateToServer(utils, mContext, getActivity());
                initView();
            }
            return view;
        } catch (Exception e) {

            return null;
        }

    }

    /**
     * Initialize view
     */

    private void initView() {
        try {
            coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id
                    .coordinatorLayout);
            gpsCount = (TextView) view.findViewById(R.id.googleCount);
            mImgViewSatellite = (ImageView) view.findViewById(R.id.imgViewSatellite);
            mImgViewSatellite.setOnClickListener(this);
            mImgViewCurrentPosition = (ImageView) view.findViewById(R.id.imgViewCurrentPosition);
            mImgViewCurrentPosition.setOnClickListener(this);
            manualDataStable = new ManualDataStable(mCallBack);
            handler = new Handler();
            listernerDialog = (InterfaceAlertDissmiss) this;
            inflaterr = LayoutInflater.from(getActivity());
            isFirstTime = true;
            btnGpsFail = (Button) view.findViewById(R.id.btnGpsFail);
            btnGpsPass = (Button) view.findViewById(R.id.btnGpsPass);
            btnGpsPass.setOnClickListener(this);
            btnGpsFail.setOnClickListener(this);
            gpsTracker = new GPSTracker(mContext, (InterfaceAlertDissmiss) this, (InterfaceShowGPSDialog) this);
            utils.showAlert(mContext, (InterfaceAlertDissmiss) GpsMap.this, getResources().getString(R.string.txtGpsAlert), getResources().getString(R.string.txtPermissionRequiredAlert),
                    mContext.getResources().getString(R.string.Ok), "", 124, 0);

        } catch (Exception e) {
            //logException(e, "GpsMapManualFragment_initView()");
        }

    }

    /*  */

    /**
     * Check ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION permission
     */
    public void checkLocationPermission() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, getActivity(), getActivity())) {
                    if (checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, getActivity(), getActivity())) {
                        gpsTracker.getLocation();
                    } else {
                        requestPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION, PERMISSION_REQUEST_CODE_LOCATION, getActivity().getApplicationContext(),
                                getActivity());
                    }
                } else {
                    requestPermission(android.Manifest.permission.ACCESS_FINE_LOCATION, PERMISSION_REQUEST_CODE_LOCATION, getActivity().getApplicationContext(),
                            getActivity());
                }
            } else {
                gpsTracker.getLocation();
            }
        } catch (Exception e) {
            //    logException(e, "GpsMapManualFragment_checkLocationPermission()");
        }


    }

    /**
     * Check weather required permission is available or not
     */
    public static boolean checkPermission(String strPermission, Context _c, Activity _a) {
        try {
            int result = ContextCompat.checkSelfPermission(_c, strPermission);
            if (result == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
          /*  GpsMapManualFragment gpsMapManualFragment = new GpsMapManualFragment(null);
            gpsMapManualFragment.logException(e, "GpsMapManualFragment_checkPermission()");*/
            return false;
        }

    }

    /**
     * In case of Marshmallow we have ask the user of the permissions externally
     * and requesting the permission
     */
    public static void requestPermission(String strPermission, int perCode, Context context, Activity activity) {
        try {
            ActivityCompat.requestPermissions(activity, new String[]{strPermission}, perCode);
        } catch (Exception e) {
            GpsMapManualFragment gpsMapManualFragment = new GpsMapManualFragment(null);
            gpsMapManualFragment.logException(e, "GpsMapManualFragment_requestPermission()");
        }

    }


}
