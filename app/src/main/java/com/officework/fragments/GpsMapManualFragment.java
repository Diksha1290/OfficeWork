package com.officework.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;

import com.crashlytics.android.Crashlytics;
import com.officework.interfaces.TimerDialogInterface;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.officework.R;
import com.officework.activities.MainActivity;
import com.officework.base.BaseFragment;
import com.officework.constants.Constants;
import com.officework.constants.FragmentTag;
import com.officework.constants.JsonTags;
import com.officework.gpstracker.GPSTracker;
import com.officework.interfaces.InterfaceAlertDissmiss;
import com.officework.interfaces.InterfaceButtonTextChange;
import com.officework.interfaces.InterfaceShowGPSDialog;
import com.officework.testing_profiles.Controller.TestController;
import com.officework.testing_profiles.utils.ConstantTestIDs;
import com.officework.utils.ExceptionLogUtils;
import com.officework.utils.ManualDataStable;
import com.officework.utils.TestResultUpdateToServer;
import com.officework.utils.Utilities;

import java.util.Observable;
import java.util.Observer;

/**
 * This map screen will show user his,her current location
 */
@SuppressLint("ValidFragment")
public class GpsMapManualFragment extends BaseFragment implements OnMapReadyCallback, View.OnClickListener,
        AdapterView.OnItemClickListener, InterfaceAlertDissmiss, InterfaceShowGPSDialog, Observer, TimerDialogInterface {
    private GoogleMap mMap;
    private View view;
    private Context mContext;
    private GPSTracker gpsTracker;
    private static int Service = -1;
    ImageView mImgViewSatellite, mImgViewCurrentPosition;
    private boolean toggleMapType = false;
    // mapType = 0 (Normal Map) and 1(Satellite Map)
    private static int mapType = 0;
    static Utilities utils;
    BroadcastReceiver receiver;
    private static final int PERMISSION_REQUEST_CODE_LOCATION = 123;
    InterfaceButtonTextChange mCallBack;
    FrameLayout gpsLayout;
    AlertDialog alertGPSDialog;
    AlertDialog alert;
    AlertDialog alertPermission;
    Handler handler = null;
    boolean progressshow=false;
    Runnable runnable;
    LayoutInflater inflaterr;
    private RelativeLayout coordinatorLayout;
    ManualDataStable manualDataStable;
    private boolean isSecondTimeMap = false;
    TestController testController;
    public static boolean isNextTest = false;
    boolean isResumeNext = false;
    boolean isResumeSkip = false;
    Snackbar snackbar;
    Handler nextButtonHandler = null;
    TextView gpsCount;
    CountDownTimer cTimer = null;
    boolean isDialogShown = false;
    InterfaceAlertDissmiss listernerDialog;
    AlertDialog alertDialog;
    TestResultUpdateToServer testResultUpdateToServer;
    boolean isInfoDialogShown = true;
    ProgressDialog bar;
    boolean isSkipPressed=false;
    boolean isTestPerformed=false;
    Context ctx;


    //isFirstTime flag is used to handle event when perform test we check user test is already passed or not but if user test again it should always be false ie onBackPressed when we press NO it always be fail
    private boolean isFirstTime = false;
    Button btnGpsFail, btnGpsPass;
    private LinearLayout locationLayout;

    public GpsMapManualFragment(InterfaceButtonTextChange callback) {
        mCallBack = callback;
    }

    public GpsMapManualFragment() {
    }

    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        try {
            if (view == null) {
                view = inflater.inflate(R.layout.mapfragment, null);
                mContext = getActivity();
                Crashlytics.log(FragmentTag.GPS_FRAGMENT.name());
                utils = Utilities.getInstance(getActivity());
                testResultUpdateToServer = new TestResultUpdateToServer(utils, mContext, getActivity());
                ctx=getActivity();
                initView();
                testController = TestController.getInstance(getActivity());
                testController.addObserver(this);
            }
            return view;
        } catch (Exception e) {
            logException(e, "GpsMapManualFragment_initUI()");
            return null;
        }

    }

    /**
     * Initialize view
     */

    private void initView() {
        try {
            MainActivity mainActivity = (MainActivity)getActivity();

            mainActivity.onChangeText(R.string.textSkip,true);
            GPSTracker.isDialogShowing=false;
            coordinatorLayout = (RelativeLayout) view.findViewById(R.id
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

            locationLayout=(LinearLayout)view.findViewById(R.id.ll_location);

            gpsTracker = new GPSTracker(mContext, (InterfaceAlertDissmiss) this, (InterfaceShowGPSDialog) this);
            bar = new ProgressDialog(getContext());
            bar.setTitle("");
            bar.setCancelable(false);
            bar.setMessage("Fetching Location");
            SupportMapFragment mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
            mapFragment.getMapAsync(this);

            if(Utilities.isInternetWorking(getActivity())){

                btnGpsPass.setOnClickListener(this);
                btnGpsFail.setOnClickListener(this);

                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mCallBack.onChangeText(Utilities.BUTTON_SKIP, true);
                            isInfoDialogShown = false;

                            checkLocationPermission();
                        } catch (Exception e) {
                        }
                    }
                });



            }else {

                btnGpsFail.setOnClickListener(this);
                btnGpsPass.setOnClickListener(this);

                Toast.makeText(getActivity(),getResources().getString(R.string.please_check_internet_Connection),Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            logException(e, "GpsMapManualFragment_initView()");
        }

        /*LocationManager manager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE );
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(statusOfGPS){
            btnGpsFail.setEnabled(true);
            btnGpsPass.setEnabled(true);
            //locationLayout.setVisibility(View.VISIBLE);
        }else {
            btnGpsFail.setEnabled(false);
            btnGpsPass.setEnabled(false);
            //locationLayout.setVisibility(View.GONE);
        }*/
    }

    /**
     * Check ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION permission
     */
    public void checkLocationPermission() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, getActivity(), getActivity())) {
                    if (checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, getActivity(), getActivity())) {
                        gpsTracker.getLocation();
                        fetchLocationData();
                        progressshow=true;

                    } else {
                        requestPermission(Manifest.permission.ACCESS_COARSE_LOCATION, PERMISSION_REQUEST_CODE_LOCATION, getActivity().getApplicationContext(),
                                getActivity());
                    }
                } else {

                    requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, PERMISSION_REQUEST_CODE_LOCATION, getActivity().getApplicationContext(),
                            getActivity());
                }
            } else {
                progressshow=true;
                gpsTracker.getLocation();
                fetchLocationData();

            }
        } catch (Exception e) {
            logException(e, "GpsMapManualFragment_checkLocationPermission()");
        }


    }

    /**
     * This method is used to handle click event on the view
     ***/
    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.imgViewCurrentPosition:
                    /**
                     * API Level 23(Marshmallow) and above.
                     * We have to externally ask user permissions to ACCESS_FINE_LOCATION.
                     * */
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, getActivity(), getActivity())) {
                            fetchLocationData();

                        } else {
                            requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, PERMISSION_REQUEST_CODE_LOCATION, getActivity().getApplicationContext(),
                                    getActivity());
                        }
                    } else {
                        fetchLocationData();

                    }
                    break;

                case R.id.imgViewSatellite:
                    // To toggle the mapType between Satellite and Normal as per user need.
                    if (toggleMapType) {
                        mapType = 0;
                        toggleMapType = false;
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    } else {
                        mapType = 1;
                        toggleMapType = true;
                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    }
                    break;
                case R.id.btnGpsFail:
                    MainActivity mainActivity = (MainActivity)getActivity();
                    isTestPerformed=true;
                    mainActivity.onChangeText(R.string.textSkip,false);
                    btnGpsPass.setEnabled(false);
                    btnGpsFail.setEnabled(false);
                    timer(getActivity(),true,ConstantTestIDs.GPS_ID,GpsMapManualFragment.this);
                    mCallBack.onChangeText(Utilities.BUTTON_NEXT, true);
                    utils.showToast(mContext, getResources().getString(R.string.txtManualFail));
                    TestController testController = TestController.getInstance(getActivity());
                    testController.onServiceResponse(false,"GPS", ConstantTestIDs.GPS_ID);
                    isResumeSkip = true;
                    handler.removeCallbacksAndMessages(null);
                    btnGpsFail.setEnabled(false);
                    btnGpsPass.setEnabled(false);
                    btnGpsFail.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque));
                    btnGpsPass.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque));

                    onNextPress();

                    break;
                case R.id.btnGpsPass:
                    MainActivity mainActivity1 = (MainActivity)getActivity();
                    timer(getActivity(),true,ConstantTestIDs.GPS_ID,GpsMapManualFragment.this);
                    mainActivity1.onChangeText(R.string.textSkip,false);
                    btnGpsPass.setEnabled(false);
                    btnGpsFail.setEnabled(false);
                    isTestPerformed=true;

                    mCallBack.onChangeText(Utilities.BUTTON_NEXT, true);
                    utils.showToast(mContext, getResources().getString(R.string.txtManualPass));
                    TestController testController1 = TestController.getInstance(getActivity());
                    testController1.onServiceResponse(true,"GPS", ConstantTestIDs.GPS_ID);
                    handler.removeCallbacksAndMessages(null);
                    isNextTest = true;
                    isResumeNext = true;
                    btnGpsFail.setEnabled(false);
                    btnGpsPass.setEnabled(false);
                    btnGpsFail.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque));
                    btnGpsPass.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque));

                    onNextPress();

                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            logException(e, "GpsMapManualFragment_onClick()");
        }

    }


    /**
     * Fetching current location of the user and showing position on the Map.
     */
    private void fetchLocationData() {
        try {
            gpsTracker.getLocation();
            LatLng currentPosition = new LatLng(gpsTracker.getLatitude(), gpsTracker.longitude);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 10), 6000, null);
            this.mMap.getUiSettings().setMyLocationButtonEnabled(true);
            if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            this.mMap.setMyLocationEnabled(true);
            this.mMap.getUiSettings().setMapToolbarEnabled(true);
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    utils.HideKeyboard(getActivity(), mContext, view);


                }
            });

            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    /*showSnackBar();*/
                    timer(getActivity(),false,ConstantTestIDs.GPS_ID,GpsMapManualFragment.this);
                }
            });
        } catch (Exception e) {
            logException(e, "GpsMapManualFragment_fetchLocationData()");
        }

    }

    /**
     * Here change the header text
     **/
    @Override
    public void onResume() {
        try {
            if (gpsTracker.IsGPSEnabled()) {
               if(GPSTracker.isDialogShowing)
               {
                   if (alertGPSDialog != null) {
                       if (alertGPSDialog.isShowing()) {
                           alertGPSDialog.dismiss();
                       }
                   }
               }
            }

            TitleBarFragment fragment = (TitleBarFragment) getFragment(R.id.headerContainer);
            // initView();
            if (fragment != null) {
//                fragment.setTitleBarVisibility(true);
//                fragment.setSyntextVisibilty(false);
//
//                fragment.setHeaderTitleAndSideIcon(getActivity().getResources().getString(R.string.GPS), false, false, 0);
                if (Constants.isBackButton) {
                    if (manualDataStable.checkSingleHardware(JsonTags.MMR_31.name(), getContext(), utils) == 1) {
                        Constants.isBackButton = false;
                    } else {
                        Constants.isBackButton = false;
                    }
                } else {
                    if (CallFunctionManualFragment.isNextPressed) {

                    }
                }
                if (isSecondTimeMap && !isInfoDialogShown && !GPSTracker.isDialogShowing && checkGooglePlayServices()) {
                    //    bar.show();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (gpsTracker.IsGPSEnabled()) {
                                if (bar.isShowing()) {
                                    bar.dismiss();
                                }

                                if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                        ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    return;
                                }
                                gpsTracker.getLocation();
                                LatLng currentPosition = new LatLng(gpsTracker.getLatitude(), gpsTracker.longitude);
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 10), 6000, null);
                            }
                        }
                    }, 2000);

                } else {
                    isSecondTimeMap = true;
                }
                if (isResumeNext) {

                    onNextPress();

                } else if (isResumeSkip) {
                }
                if (!isResumeNext) {
                }
            }
            if(isTestPerformed){
             onNextPress();
            }
            gpsCount.setVisibility(View.INVISIBLE);
            nextButtonHandler = new Handler();

        } catch (Exception e) {
            logException(e, "GpsMapManualFragment_onResume()");
        }

        LocationManager manager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE );
        boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER) || manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if(statusOfGPS){

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    locationLayout.setVisibility(View.VISIBLE);
                }
            }, 4000);

        }else {
            locationLayout.setVisibility(View.GONE);
        }


        super.onResume();
    }



    private boolean checkGooglePlayServices() {
        final int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(mContext);
        return status == ConnectionResult.SUCCESS;
    }

    /**
     * onPause
     */
    @Override
    public void onPause() {
        try {
            cancelTimer();
            nextButtonHandler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            logException(e, "GpsMapManualFragment_onPause()");
        }

        super.onPause();
    }

    /**
     * Manipulates the map once available.
     * This timerFail is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            mMap = googleMap;
            if (mapType == 1) {
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }
            LatLng currentPosition = new LatLng(gpsTracker.getLatitude(), gpsTracker.longitude);

            this.mMap.getUiSettings().setMyLocationButtonEnabled(true);
            if(getActivity()!=null) {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            this.mMap.setMyLocationEnabled(true);
            this.mMap.getUiSettings().setMapToolbarEnabled(true);
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    utils.HideKeyboard(getActivity(), mContext, view);

                }
            });

        } catch (Exception e) {
            logException(e, "GpsMapManualFragment_onMapReady()");
        }

    }

    /**
     * start timer
     */
    public void startTimer() {
        try {
            cTimer = new CountDownTimer(Constants.countTimerLong, 1000) {
                public void onTick(long millisUntilFinished) {
                    gpsCount.setText(String.valueOf(millisUntilFinished / 1000));
                }

                public void onFinish() {
                    showAlertDialog(getResources().getString(R.string.txtAlertTitleGreatAlert), getResources().getString(R.string.txtCount_test_Dialog), getResources().getString(R.string.Yes),
                            getResources().getString(R.string.No));
                }
            };
            cTimer.start();

        } catch (Exception e) {
            logException(e, "GpsMapManualFragment_startTimer()");
        }

    }

    /**
     * show alert after time completition
     *
     * @param dialogTitle
     * @param dialogMessage
     * @param btnTextPositive
     * @param btnTextNegative
     */
    public void showAlertDialog(String dialogTitle, String dialogMessage, String btnTextPositive, String btnTextNegative) {
        try {
            alertDialog = new AlertDialog.Builder(
                    mContext).create();

            // Setting Dialog Title
            alertDialog.setTitle(dialogTitle);

            // Setting Dialog Message
            alertDialog.setMessage(dialogMessage);
            alertDialog.setCancelable(false);

            // Setting Icon to Dialog
            /*alertDialog.setIcon(R.drawable.tick);*/

            // Setting OK Button
            alertDialog.setButton(btnTextPositive
                    , new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            isDialogShown = false;
                            checkLocationPermission();
                            gpsTracker.IsGPSEnabled();
                            startTimer();
                            // Write your code here to execute after dialog closed
                        }
                    });
            alertDialog.setButton2(btnTextNegative, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // utils.compare_UpdatePreferenceInt(getActivity(), JsonTags.MMR_31.name(), 0);
                    // updateResultToServer();
                    isDialogShown = false;
                    utils.showToast(mContext, getResources().getString(R.string.txtManualFail));
                    TestController testController = TestController.getInstance(getActivity());
                    testController.onServiceResponse(false,"GPS", ConstantTestIDs.GPS_ID);
                    if (alertGPSDialog != null) {
                        if (alertGPSDialog.isShowing()) {
                            alertGPSDialog.dismiss();
                        }
                    }
                    if (alertDialog != null) {
                        if (alertDialog.isShowing()) {
                            alertDialog.dismiss();
                        }
                    }
                    if (alertPermission != null) {
                        if (alertPermission.isShowing()) {
                            alertPermission.dismiss();
                        }
                    }
                    if (Constants.isManualIndividual) {
                        cancelTimer();
                        nextButtonHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Constants.isManualIndividual = false;
                                nextButtonHandler.removeCallbacksAndMessages(null);
                                replaceFragment(R.id.container, new ManualTestFragment((InterfaceButtonTextChange) getActivity()), FragmentTag.MANUAL_TEST_FRAGMENT.name(), false);
                            }
                        }, 2000);
                    } else {
                        onNextPress();
                    }
                }
            });

            // Showing Alert Message
            if (!isDialogShown) {
                isDialogShown = true;
                alertDialog.show();
            }
        } catch (Exception e) {
            logException(e, "GpsMapManualFragment_showAlertDialog()");
        }

    }

    //cancel timer
    public void cancelTimer() {
        try {
            if (cTimer != null)
                cTimer.cancel();
        } catch (Exception e) {
            logException(e, "GpsMapManualFragment_cancelTimer()");
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
            GpsMapManualFragment gpsMapManualFragment = new GpsMapManualFragment(null);
            gpsMapManualFragment.logException(e, "GpsMapManualFragment_checkPermission()");
            return false;
        }

    }

    /**
     * timerFail of the asked permission from the user
     * In this case we have asked for the ACCESS_FINE_LOCATION
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        try {
            switch (requestCode) {
                case PERMISSION_REQUEST_CODE_LOCATION:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        gpsTracker.getLocation();
                        fetchLocationData();
                    } else {
                        // Permission Denied
                        showAlert(mContext, (InterfaceAlertDissmiss) GpsMapManualFragment.this, mContext.getResources().getString(R.string.txtPermissionMessageGps),
                                mContext.getResources().getString(R.string.txtPermissionRequiredAlert),
                                mContext.getResources().getString(R.string.Ok), mContext.getResources().getString(R.string.textSkip), 123);
//                        utils.showToast(mContext, "Permission Denied, You cannot access location data.");
                    }
                    break;
            }
        } catch (Exception e) {
            logException(e, "GpsMapManualFragment_onRequestPermissionsResult()");
        }

    }

    /**
     * show permission alert
     *
     * @param ctx
     * @param mListener
     * @param Message
     * @param Title
     * @param positiveButtonText
     * @param negativeButtonText
     * @param callbackID
     */
    public void showAlert(Context ctx, final InterfaceAlertDissmiss mListener, String Message, String Title, String positiveButtonText,
                          String negativeButtonText, final int callbackID) {
        try {
            alertPermission = new AlertDialog.Builder(ctx).create();
            alertPermission.setTitle(Title);
            alertPermission.setMessage(Message);
            alertPermission.setButton(positiveButtonText, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mListener.onButtonClick(false, callbackID);
                }
            });
            if (!TextUtils.isEmpty(negativeButtonText)) {
                alertPermission.setButton2(negativeButtonText, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onButtonClick(true, callbackID);
                    }
                });
            }
            alertPermission.show();
        } catch (Exception e) {
            logException(e, "GpsMapManualFragment_showAlert()");
        }

    }

    @Override
    public void onStop() {
        if(mCallBack!=null) {
            mCallBack.onChangeText(utils.BUTTON_NEXT, false);
        }
        super.onStop();
    }


    /**
     * Called when test is completed or user click on skip button
     */
    public void onNextPress() {
        try {
            isSkipPressed=true;
            try {
                alertGPSDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            cancelTimer();
            MainActivity mainActivity = (MainActivity) getActivity();
            if (Constants.isSkipButton) {
                isTestPerformed=true;
                btnGpsFail.setEnabled(false);
                btnGpsPass.setEnabled(false);
                btnGpsFail.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque));
                btnGpsPass.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque));
                TestController testController = TestController.getInstance(getActivity());
                //testController.onServiceResponse(false, "GPS", ConstantTestIDs.GPS_ID);
                utils.showToast(mContext, getResources().getString(R.string.txtManualFail));

            }
            boolean semi = false;
            nextPress(mainActivity, semi);

            try {
                alertGPSDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }


        } catch (Exception e) {
            logException(e, "GpsMapManualFragment_onNextPress()");
        }


    }

    /**
     * onBackPressed
     */
    public void onBackPress() {
        try {
            showSnackBarBack();
        } catch (Exception e) {
            logException(e, "GpsMapManualFragment_onBackPress()");
        }

    }

    /**
     * show snack bar when user click on back button
     *
     * @param
     */

    private void showSnackBarBack() {
        mCallBack.onChangeText(utils.BUTTON_NEXT, false);
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, getResources().getString(R.string.txtBackPressed), 3000)
                .setAction(getResources().getString(R.string.txtBackPressedYes), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Constants.isNextHandler = true;
                        Constants.isBackButton = true;
                        if (handler != null) {
                            handler.removeCallbacksAndMessages(null);
                        }
                        isNextTest = false;
                        if(getActivity()!=null) {
                            getActivity().getSupportFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.container)).commit();

                            getActivity().getSupportFragmentManager().popBackStackImmediate();
                        }

                    }
                });
        snackbar.setActionTextColor(Color.WHITE);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        (snackbar.getView()).getLayoutParams().width =ViewGroup.LayoutParams.MATCH_PARENT;

        snackbar.show();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isNextTest) {
                    mCallBack.onChangeText(utils.BUTTON_NEXT, true);
                } else {
                    mCallBack.onChangeText(utils.BUTTON_SKIP, true);
                }
            }
        }, 3000);


    }

    /**
     * alert click timerFail
     *
     * @param isCanceled
     * @param callbackID
     */

    @Override
    public void onButtonClick(boolean isCanceled, int callbackID) {
        try {
            switch (callbackID) {
                case PERMISSION_REQUEST_CODE_LOCATION:
                    if (!isCanceled) {
//                        checkLocationPermission();
                        if(alertGPSDialog!=null) {
                            if (alertGPSDialog.isShowing()) {
                                alertGPSDialog.dismiss();
                            }
                        }
                        replaceFragment(R.id.container, new ManualTestFragment((InterfaceButtonTextChange) getActivity()), FragmentTag.MANUAL_TEST_FRAGMENT.name(), false);
                    } else {
//                        mCallBack.onChangeText(utils.BUTTON_SKIP, true);
                        if(alertGPSDialog!=null)
                        {
                            if (alertGPSDialog.isShowing()) {
                                alertGPSDialog.dismiss();
                            }
                        }
                        TestController testController = TestController.getInstance(getActivity());
                        testController.saveSkipResponse(-1, ConstantTestIDs.GPS_ID);
                        utils.showToast(mContext, getResources().getString(R.string.txtManualFail));
                        onNextPress();

                    }
                    break;

                case GPSTracker.dialogCallBack:
                    if (isCanceled) {
                        if (alertPermission != null) {
                            if (alertPermission.isShowing()) {
                                alertPermission.dismiss();
                            }
                        }
                        this.mMap.setMyLocationEnabled(false);
                        this.mMap.getUiSettings().setMapToolbarEnabled(false);
                        mCallBack.onChangeText(utils.BUTTON_SKIP, true);
                        TestController testController = TestController.getInstance(getActivity());
                        testController.onServiceResponse(false, "GPS", ConstantTestIDs.GPS_ID);
                        isResumeSkip = true;
                        if (handler != null) {
                            handler.removeCallbacksAndMessages(runnable);
                        }
                        if (mMap != null) {
                            mMap.setOnMapLoadedCallback(null);
                        }
                        if (snackbar != null) {
                            snackbar.dismiss();
                        }
                        handler = null;
                    } else {
                        mCallBack.onChangeText(utils.BUTTON_SKIP, false);
                        /*showSnackBar();*/
                        this.mMap.setMyLocationEnabled(true);
                        this.mMap.getUiSettings().setMapToolbarEnabled(true);
                        if (alertPermission != null) {
                            if (alertPermission.isShowing()) {
                                alertPermission.dismiss();
                            }
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            logException(e, "GpsMapManualFragment_onButtonClick(boolean isCanceled, int callbackID)");
        }

    }

    /**
     * alert click timerFail
     *
     * @param isCanceled
     * @param callbackID
     */
    @Override
    public void onButtonClick(boolean isCanceled, int callbackID, int which) {
        try {
            mCallBack.onChangeText(Utilities.BUTTON_SKIP, true);
            isInfoDialogShown = false;
            checkLocationPermission();
        } catch (Exception e) {
            //  logException(e, "GpsMapManualFragment_onButtonClick()");
        }

    }


    /**
     * show GPS enable dialog
     *
     * @param isShow
     */
    @Override
    public void onShowDialog(boolean isShow, int type) {
        if (bar.isShowing()) {
            bar.dismiss();
        }
        try {
            if(!isSkipPressed) {
                switch (type) {
                    case 0:
                        showSettingsAlert();
                        break;
                    case 1:
                        showNetworkAlert();
                        break;
                }
            }
        } catch (Exception e) {
            logException(e, "GpsMapManualFragment_onShowDialog()");
        }
    }

    /**
     * Function to show settings alert dialog
     */
    public void showNetworkAlert() {
        try {

            alertGPSDialog = new AlertDialog.Builder(mContext).create();
            alertGPSDialog.setTitle("Internet Error");
            alertGPSDialog.setMessage("Internet disabled! Please turn it on.");
            alertGPSDialog.setCancelable(false);
            alertGPSDialog.setButton("Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    GPSTracker.isDialogShowing = false;
                    listernerDialog.onButtonClick(false, GPSTracker.dialogCallBack);
                    showWifiScreenSafe();
                }
            });
            alertGPSDialog.setButton2("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        dialog.cancel();
                        mCallBack.onChangeText(Utilities.BUTTON_SKIP, true);
                        GPSTracker.isDialogShowing = false;
                        utils.showToast(mContext, getResources().getString(R.string.txtManualFail));

                        onNextPress();
                        listernerDialog.onButtonClick(true, GPSTracker.dialogCallBack);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
            alertGPSDialog.show();
        } catch (Exception e) {
            logException(e, "GpsMapManualFragment_showSettingsAlert()");
        }
    }

    public void showWifiScreenSafe() {
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.wifi.WifiSettings");
            intent.setComponent(cn);
            mContext.startActivity(intent);
        } catch (Exception e) {
        }
    }


    /**
     * Function to show settings alert dialog
     */
    public void showSettingsAlert() {
        try {
            alertGPSDialog = new AlertDialog.Builder(mContext).create();
            alertGPSDialog.setTitle("GPS Error");
            alertGPSDialog.setMessage("GPS disabled! Please turn it on.");
            alertGPSDialog.setCancelable(false);
            alertGPSDialog.setButton("Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    GPSTracker.isDialogShowing = false;
                    listernerDialog.onButtonClick(false, GPSTracker.dialogCallBack);
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    mContext.startActivity(intent);
                }
            });
            alertGPSDialog.setButton2("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        dialog.cancel();
                        mCallBack.onChangeText(Utilities.BUTTON_SKIP, true);
                        GPSTracker.isDialogShowing = false;
                        listernerDialog.onButtonClick(true, GPSTracker.dialogCallBack);
                        utils.showToast(mContext, getResources().getString(R.string.txtManualFail));
                        onNextPress();
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
            });
            alertGPSDialog.show();
        } catch (Exception e) {
            logException(e, "GpsMapManualFragment_showSettingsAlert()");
        }

    }

    public void logException(Exception e, String methodName) {
        try {
            Utilities utilities = Utilities.getInstance(getActivity());
            Activity activity = getActivity();
            Context context = getActivity();
            ExceptionLogUtils exceptionLogUtils = new ExceptionLogUtils(utilities, context, activity);
            exceptionLogUtils.addExceptionLog(utilities, context, activity, null, null, e, methodName);
        } catch (Exception exp) {
            //       logException(exp, "GpsMapManualFragment_logException()");
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        timer(getActivity(),true,ConstantTestIDs.GPS_ID,GpsMapManualFragment.this);
    }

    @Override
    public void update(Observable o, Object arg) {

    }

    @Override
    public void timerFail() {

        btnGpsFail.setEnabled(false);
        btnGpsPass.setEnabled(false);
        btnGpsFail.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque));
        btnGpsPass.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque));
        testController.onServiceResponse(false,"",ConstantTestIDs.GPS_ID);
        onNextPress();
        utils.showToast(mContext, getResources().getString(R.string.txtManualFail));
    }
}


