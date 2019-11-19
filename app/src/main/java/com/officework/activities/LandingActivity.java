package com.officework.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.officework.fragments.GetQRCodeFragment;
import com.officework.fragments.PaymentDetailFragment;
import com.officework.fragments.VerifyTradableManualFragment;
import com.officework.interfaces.InterfaceBatteryCallback;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.officework.R;
import com.officework.fragments.AboutUsFragment;
import com.officework.fragments.LandingFragment;
import com.officework.utils.Utilities;

public class LandingActivity extends BaseActivity implements InterfaceBatteryCallback {
    Toolbar toolbar;
    FrameLayout frame;
    NavigationView navigation;
    public DrawerLayout drawerLayout;
    public TextView title;
     NavigationView navView;
    final public int REQUEST_CODE_IMEI_PERMISSION = 504;
    public boolean isDeviceTradeableApiHit=false;
    public ActionBarDrawerToggle mDrawerToggle;
    @Override
    public void onBackPressed() {

        if(getSupportFragmentManager().findFragmentById(R.id.frame) instanceof LandingFragment || getSupportFragmentManager().findFragmentById(R.id.frame) instanceof VerifyTradableManualFragment){

            Utilities.getInstance(this).showAlert(this, new Utilities.onAlertOkListener() {
                @Override
                public void onOkButtonClicked(String tag) {

                    finishAffinity();
                }
            }, Html.fromHtml(getResources().getString(R.string.exit_msg)), getResources().getString(R.string.txtAlertTitleGreatAlert), "No", "Yes", "Sync");
        }
        else {
            title.setText(getResources().getString(R.string.txtDASHBOARD));
            Fragment f = new LandingFragment();
            replaceFragment(R.id.frame,f,"Landing",false);
            navView.getMenu().getItem(0).setChecked(true);
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        toolbar=findViewById(R.id.toolbar);
        frame=findViewById(R.id.frame);
        navigation=findViewById(R.id.navigation);
        drawerLayout=findViewById(R.id.drawer_layout);
        title=findViewById(R.id.title);
        configureToolbar();
        configureNavigationDrawer();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                GetQRCodeFragment.getIMEI(true, Utilities.getInstance(this), LandingActivity.this,
                        this);

            } else {
                checkRuntimePermisson();
            }
        } else {
            GetQRCodeFragment.getIMEI(true, Utilities.getInstance(this), LandingActivity.this, this);
        }
        GetQRCodeFragment.deviceMarketName(this);
        GetQRCodeFragment.getDeviceInfoData(Utilities.getInstance(this), LandingActivity.this, this,
                (InterfaceBatteryCallback) this);
    }
    private void checkRuntimePermisson() {
        try {
            int hasWritePhoneReadPermission = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                hasWritePhoneReadPermission =
                        checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
                if (hasWritePhoneReadPermission != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
                            REQUEST_CODE_IMEI_PERMISSION);
                    return;
                }
            }
            GetQRCodeFragment.getIMEI(true, Utilities.getInstance(this), getApplicationContext(), LandingActivity.this);


        } catch (Exception e) {

        }


    }

    private  void configureToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("");
        title.setText(getResources().getString(R.string.txtDASHBOARD));
         mDrawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(false);
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_icon_menu, getTheme());
        mDrawerToggle.setHomeAsUpIndicator(drawable);
        drawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });
        replaceFragment(R.id.frame,new LandingFragment(),"Landing",false);

    }

    private void configureNavigationDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navView = (NavigationView) findViewById(R.id.navigation);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Fragment f = null;
                int itemId = menuItem.getItemId();
                if (itemId == R.id.home) {

                        title.setText(getResources().getString(R.string.txtDASHBOARD));
                        f = new LandingFragment();
                        replaceFragment(R.id.frame, f, "Landing", false);

                }
//                else if (itemId == R.id.order_detail) {
//                    title.setText(menuItem.getTitle());
//                    f = new TestCompleteFragment();
//                    replaceFragment(R.id.frame,f,"dd",true);
//
//
//
//                }
                else if(itemId == R.id.about_us){
                    title.setText(menuItem.getTitle());
                    f = new AboutUsFragment();
                    if(getSupportFragmentManager().findFragmentById(R.id.frame) instanceof LandingFragment) {
                        replaceFragment(R.id.frame, f, "dsf", true);
                    }else {
                        replaceFragment(R.id.frame, f, "dsf", false);

                    }
                }

                drawerLayout.closeDrawers();
                return true;

            }
        });

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                try {
                    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frame);
                    if (fragment instanceof LandingFragment) {
                        title.setText(getResources().getString(R.string.app_name));
                    } else if (fragment instanceof LandingFragment) {
                        title.setText(getResources().getString(R.string.about_us));
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        try {
            FragmentManager fm = getSupportFragmentManager();
            Fragment frag = fm.findFragmentById(R.id.container);
            switch (requestCode) {

                case REQUEST_CODE_IMEI_PERMISSION:
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // Permission Granted
                        GetQRCodeFragment.getIMEI(true, Utilities.getInstance(this), getApplicationContext(),
                                LandingActivity.this);
                    } else {
                        // Permission Denied
                        GetQRCodeFragment.getIMEI(false, Utilities.getInstance(this), getApplicationContext(),
                                LandingActivity.this);
                    }

                    break;
                default:
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                    break;
            }
        } catch (Exception e) {

        }

    }



    @Override
    protected void setUpLayout() {

    }

    @Override
    protected void setDataInViewObjects() {

    }

    @Override
    protected void setUpToolBar() {

    }

    @Override
    public void onBatteryCallBack(Intent intent) {

    }

    public void setToolbarTitle(String header){
        if(title != null) {
            title.setText(header);
            toolbar.setNavigationIcon(null);
        }
    }



}