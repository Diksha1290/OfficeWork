package com.officework.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.officework.R;
import com.officework.base.BaseCompatActivity;
import com.officework.constants.Constants;
import com.officework.constants.FragmentTag;
import com.officework.constants.Preferences;
import com.officework.fragments.CallFunctionManualFragment;
import com.officework.fragments.CameraFragment;
import com.officework.fragments.ChargingManualFragment;
import com.officework.fragments.DisplayManualFragment;
import com.officework.fragments.EarJackManualFragment;
import com.officework.fragments.GetQRCodeFragment;
import com.officework.fragments.GpsMapManualFragment;
import com.officework.fragments.HomeButtonManualFragment;
import com.officework.fragments.LightSensorManualFragment;
import com.officework.fragments.MicManualFragment;
import com.officework.fragments.MutitouchManualFragment;
import com.officework.fragments.NetworkInfoFragment;
import com.officework.fragments.PhoneShakingManualFragment;
import com.officework.fragments.PowerButtonManualFragment;
import com.officework.fragments.ProximitySensorManualFragment;
import com.officework.fragments.SpeakerManualFragment;
import com.officework.fragments.SystemInfoFragment;
import com.officework.fragments.TitleBarFragment;
import com.officework.fragments.TouchScreenCanvasManualFragment;
import com.officework.fragments.VolumeManualFragment;
import com.officework.interfaces.InterfaceAlertDissmiss;
import com.officework.interfaces.InterfaceButtonTextChange;
import com.officework.testing_profiles.ui.fragment.AutomatedTestFragment;
import com.officework.utils.Utilities;


public class AutomatedTestActivity extends BaseCompatActivity implements InterfaceAlertDissmiss, InterfaceButtonTextChange {

    private CharSequence mTitle;
    Context ctx;
    final public int REQUEST_CODE_ASK_PERMISSIONS = 123;
    Button btnNext;
    Utilities utils;
    boolean isCamera = false;

    @Override
    protected void initUI(Bundle savedInstanceState) {
        // getWindow().getDecorView().setBackgroundResource(R.drawable.bg_for_all);
        setContentView(R.layout.activity_test_automated);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);


    }

    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putBoolean("saved", true);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void initVariable(Bundle savedInstanceState) {
        ctx = this;
        utils = Utilities.getInstance(this);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnNext.setOnClickListener(this);
        addFragment(R.id.headerContainer, new TitleBarFragment(), FragmentTag.HEADER_FRAGMENT.name(), false);
        replaceFragment(R.id.container, new AutomatedTestFragment(null), FragmentTag.AUTOMATED_TEST_FRAGMENT.name(), false);
        if (savedInstanceState != null) {
            isCamera = true;
            initNextButton();
        } else {
            initNextButton();
            if (getIntent().hasExtra(Constants.SMART_RUN)) {
                if (getIntent().getExtras().getBoolean(Constants.SMART_RUN)) {
                      showAlertDialog(getResources().getString(R.string.txtAlertTitleGreat), getResources().getString(R.string.txtDialogMessageAutomationDone), getResources().getString(R.string.txtContinue),
                            getResources().getString(R.string.Cancel));
                }
            }
        }

    }
    private void dismisKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void showAlertDialog(String dialogTitle, String dialogMessage, String btnTextPositive, String btnTextNegative) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                ctx);

        // Setting Dialog Title
        alertDialog.setTitle(dialogTitle);

        // Setting Dialog Message
        alertDialog.setMessage(dialogMessage);
        alertDialog.setCancelable(false);
        // Setting Icon to Dialog
        /*alertDialog.setIcon(R.drawable.tick);*/

        // Setting OK Button
        alertDialog.setPositiveButton(btnTextPositive
                , new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to execute after dialog closed
                        Constants.IS_SMART_RUN = false;
                        replaceFragment(R.id.container, new CameraFragment((InterfaceButtonTextChange) AutomatedTestActivity.this), FragmentTag.CAMERA_FRAGMENT.name(), true);
                    }
                });
        alertDialog.setNegativeButton(btnTextNegative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Constants.IS_SMART_RUN = false;
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.btnNext:
                FragmentManager fm = getSupportFragmentManager();
                Fragment frag = fm.findFragmentById(R.id.container);
                if (frag instanceof CameraFragment) {
                    ((CameraFragment) frag).onNextPress();
                } else if (frag instanceof EarJackManualFragment) {
                    ((EarJackManualFragment) frag).onNextPress();
                } else if (frag instanceof VolumeManualFragment) {
                    ((VolumeManualFragment) frag).onNextPress();
                } else if (frag instanceof PowerButtonManualFragment) {
                    ((PowerButtonManualFragment) frag).onNextPress();
                } else if (frag instanceof ChargingManualFragment) {
                    ((ChargingManualFragment) frag).onNextPress();
                } else if (frag instanceof DisplayManualFragment) {
                    ((DisplayManualFragment) frag).onNextPress();
                } else if (frag instanceof MutitouchManualFragment) {
                    ((MutitouchManualFragment) frag).onNextPress();
                } else if (frag instanceof ProximitySensorManualFragment) {
                    ((ProximitySensorManualFragment) frag).onNextPress();
                } else if (frag instanceof TouchScreenCanvasManualFragment) {
                    ((TouchScreenCanvasManualFragment) frag).onNextPress();
                } else if (frag instanceof HomeButtonManualFragment) {
                    ((HomeButtonManualFragment) frag).onNextPress();
                } else if (frag instanceof LightSensorManualFragment) {
                    ((LightSensorManualFragment) frag).onNextPress();
                } else if (frag instanceof PhoneShakingManualFragment) {
                    ((PhoneShakingManualFragment) frag).onNextPress();
                } else if (frag instanceof GpsMapManualFragment) {
                    ((GpsMapManualFragment) frag).onNextPress();
                } else if (frag instanceof SpeakerManualFragment) {
                    ((SpeakerManualFragment) frag).onNextPress();
                } else if (frag instanceof MicManualFragment) {
                    ((MicManualFragment) frag).onNextPress();
                }
                break;
        }
    }




    @Override
    public void onButtonClick(boolean isCanceled, int callbackID) {
        switch (callbackID) {
            case 0:
                /*if (!isCanceled) {
                    killContactSyncService();
                    Utilities.getInstance(this).clearPreferences(this);
                    Utilities.getInstance(this).clearDB(this);
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                }*/
                break;
            case 1:
                if (!isCanceled)
                    finish();
                break;
            case 2:
                if (!isCanceled)
                    /*initiateFullSync();*/
                    break;
            default:
                break;
        }
    }

    @Override
    public void onButtonClick(boolean isCanceled, int callbackID, int which) {

    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment frag = fm.findFragmentById(R.id.container);
        if (frag instanceof CallFunctionManualFragment)
            ((CallFunctionManualFragment) frag).onBackPress();
        else if (frag instanceof DisplayManualFragment)
            ((DisplayManualFragment) frag).onBackPress();
        else if (frag instanceof HomeButtonManualFragment)
            ((HomeButtonManualFragment) frag).onBackPress();
        else if (frag instanceof LightSensorManualFragment)
            ((LightSensorManualFragment) frag).onBackPress();
        else if (frag instanceof EarJackManualFragment)
            ((EarJackManualFragment) frag).onBackPress();
        else if (frag instanceof VolumeManualFragment)
            ((VolumeManualFragment) frag).onBackPress();
        else if (frag instanceof PowerButtonManualFragment)
            ((PowerButtonManualFragment) frag).onBackPress();
        else if (frag instanceof ChargingManualFragment)
            ((ChargingManualFragment) frag).onBackPress();
        else if (frag instanceof MutitouchManualFragment)
            ((MutitouchManualFragment) frag).onBackPress();
        else if (frag instanceof ProximitySensorManualFragment)
            ((ProximitySensorManualFragment) frag).onBackPress();
        else if (frag instanceof TouchScreenCanvasManualFragment)
            ((TouchScreenCanvasManualFragment) frag).onBackPress();
        else if (frag instanceof GpsMapManualFragment)
            ((GpsMapManualFragment) frag).onBackPress();
        else if (frag instanceof PhoneShakingManualFragment)
            ((PhoneShakingManualFragment) frag).onBackPress();
        else if (frag instanceof SpeakerManualFragment)
            ((SpeakerManualFragment) frag).onBackPress();
        else if (frag instanceof MicManualFragment)
            ((MicManualFragment) frag).onBackPress();
        else if (frag instanceof AutomatedTestFragment)
            ((AutomatedTestFragment) frag).onBackPress();
        else if (frag instanceof GetQRCodeFragment) {
            ((GetQRCodeFragment) frag).onBackPress();
        } else if (frag instanceof EarJackManualFragment) {
            if (getFragment(FragmentTag.CAMERA_FRAGMENT.name()) != null) {
                replaceFragment(R.id.container, new CameraFragment((InterfaceButtonTextChange) this), FragmentTag.CAMERA_FRAGMENT.name(), false);
            } else {
                popFragment(frag.getId());
            }
        }else if (frag instanceof HomeButtonManualFragment) {
            if (getFragment(FragmentTag.SCREEN_TOUCH_FRAGMENT.name()) != null) {
                replaceFragment(R.id.container, new TouchScreenCanvasManualFragment((InterfaceButtonTextChange) this), FragmentTag.SCREEN_TOUCH_FRAGMENT.name(), false);
            } else {
                popFragment(frag.getId());
            }
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            popFragment(frag.getId());
        } else if (frag instanceof AutomatedTestFragment)
            ((AutomatedTestFragment) frag).onBackPress();
        else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            popFragment(frag.getId());
        } else {
            /*Utilities.getInstance(AutomatedTestActivity.this).showAlert(this, (InterfaceAlertDissmiss) this,
                    getResources().getString(R.string.quit_app), "", getResources().getString(R.string.Yes),
                    getResources().getString(R.string.No), 1);*/
            this.finish();
        }
    }

//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        FragmentManager fm = getSupportFragmentManager();
//        Fragment frag = fm.findFragmentById(R.id.container);
//        if (frag instanceof VolumeManualFragment) {
//            ((VolumeManualFragment) frag).dispatchKeyEvent(event);
//        }
//        return super.dispatchKeyEvent(event);
//    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment frag = fm.findFragmentById(R.id.container);
        /*if (frag instanceof PowerButtonManualFragment) {
            ((PowerButtonManualFragment) frag).onKeyDown(keyCode, event);
        }*/
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FragmentManager fm = getSupportFragmentManager();
        Fragment frag = fm.findFragmentById(R.id.container);

        if (frag instanceof CameraFragment)
            ((CameraFragment) frag).onActivityResult(requestCode, resultCode, data);
        else if (frag instanceof AutomatedTestFragment)
            ((AutomatedTestFragment) frag).onActivityResult(requestCode, resultCode, data);


    }

    @Override
    public Intent getIntent() {
        /*if (getIntent() != null) {
            if (getIntent().hasExtra(Extras.IS_SYNC_TIME.name()))
                Utilities.getInstance(this).AlertDialogFinish(this, "", getIntent().getExtras().getString(Extras.ALARM_TEXT.name()));
        }*/
        return super.getIntent();
    }

    private void initNextButton() {
       /* FragmentManager fm = getSupportFragmentManager();
        Fragment frag = fm.findFragmentById(R.id.container);
        if (frag instanceof CameraFragment) {
            btnNext.setVisibility(View.VISIBLE);
        } else {
            btnNext.setVisibility(View.GONE);
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        initNextButton();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment frag = fm.findFragmentById(R.id.container);
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (frag instanceof SystemInfoFragment) {
                    ((SystemInfoFragment) frag).onRequestPermissionsResult(requestCode, permissions, grantResults);
                } else if (frag instanceof NetworkInfoFragment) {
                    ((NetworkInfoFragment) frag).onRequestPermissionsResult(requestCode, permissions, grantResults);
                } else if (frag instanceof GpsMapManualFragment) {
                    ((GpsMapManualFragment) frag).onRequestPermissionsResult(requestCode, permissions, grantResults);
                } else if (frag instanceof CameraFragment) {
                    ((CameraFragment) frag).onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onChangeText(int text, boolean showButton) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment frag = fm.findFragmentById(R.id.container);


        if (text == utils.BUTTON_SKIP) {
            Constants.isSkipButton = true;
        } else {
            Constants.isSkipButton = false;
        }
        if (showButton) {
            btnNext.setVisibility(View.VISIBLE);
            btnNext.setText(text);
        } else {
            btnNext.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();


        utils.storeDateTime(this, false);
        utils.addLog(this, "Log Current Date", Constants.currentDateTime + "");
        /*This will navigate the user to the Login screen if user open the app after 24 hours
        * else
        * Normal flow will work*/

        if (utils.getPreferenceLong(this, Constants.lastDate_Time, 0L) != 0L && utils.getPreferenceLong(this, Constants.currentDate_Time, 0L) != 0L) {
            if (utils.getTimeDifference(utils.getPreferenceLong(this, Constants.lastDate_Time, 0L),
                    utils.getPreferenceLong(this, Constants.currentDate_Time, 0L)) >= 1) {
                SharedPreferences mAppPreferences1 = getSharedPreferences(Preferences.MEGAMMR_APP_PREF.name(), Context.MODE_PRIVATE);

                MainActivity.clearPreferenceData(utils, ctx,mAppPreferences1);

                utils.addPreferenceLong(this, Constants.lastDate_Time, 0L);
                utils.addPreferenceLong(this, Constants.currentDate_Time, 0L);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        /*SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
        Constants.lastDateTime = null;
        try {
            SimpleDateFormat oldFormat = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
            Constants.lastDateTime = oldFormat.parse(sdf.format(new Date()));
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        utils.storeDateTime(this, true);
        utils.addLog(this, "Log Last Date", Constants.lastDateTime + "");
    }
}