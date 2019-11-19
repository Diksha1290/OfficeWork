package com.officework.activities;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.officework.R;
import com.officework.constants.Constants;
import com.officework.fragments.AboutUsFragment;
import com.officework.fragments.LandingFragment;
import com.officework.fragments.VerifyTradableFragment;
import com.officework.fragments.VerifyTradableManualFragment;
import com.officework.utils.Utilities;
import com.google.android.material.navigation.NavigationView;

public class CheckingDeviceActivity extends BaseActivity {

    private Toolbar toolbar;
    private FrameLayout frame;
    private TextView title;
    public static String qr_code_test;
    private String enterprisePartnerID,storeID;
    public DrawerLayout drawerLayout;
    NavigationView navigation;
    NavigationView navView;
    public ActionBarDrawerToggle mDrawerToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checking_device);
        toolbar=findViewById(R.id.toolbar);
        frame=findViewById(R.id.frame);
        title=findViewById(R.id.title);
        title.setText(R.string.checking_device);
        navigation=findViewById(R.id.navigation);
        drawerLayout=findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
try {
    enterprisePartnerID =Utilities.getInstance(CheckingDeviceActivity.this).getPreference(CheckingDeviceActivity.this,Constants.ENTERPRISEPATNERID,"");
      storeID = Utilities.getInstance(CheckingDeviceActivity.this).getPreference(CheckingDeviceActivity.this,Constants.QRCODEID,"");
    Bundle bundle = new Bundle();
    bundle.putString(Constants.ENTERPRISEPATNERID, enterprisePartnerID);
    bundle.putString(Constants.STOREID, storeID);
    Fragment f = new VerifyTradableFragment();
    f.setArguments(bundle);
    replaceFragment(R.id.frame, f, "Verify", false);
}catch (Exception e)
{
    e.printStackTrace();
    Toast.makeText(getApplicationContext(),"Wrong QR Code",Toast.LENGTH_LONG).show();
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
    public void onBackPressed() {
        Fragment frag = getSupportFragmentManager().findFragmentById(R.id.frame);
        if (frag instanceof VerifyTradableFragment) {
            ((VerifyTradableFragment) frag).onBackPress();
        }
        if (frag instanceof VerifyTradableManualFragment) {
            ((VerifyTradableManualFragment) frag).onBackPress();
        }
        if (frag instanceof LandingFragment) {
            ((LandingFragment) frag).onBackPress();
        }
        if (frag instanceof AboutUsFragment) {
            ((AboutUsFragment) frag).onBackPress();
        }

    }
    public void setToolbarTitle(String header){
        if(title != null) {
            title.setText(header);
        }
    }
    public void setToolbarTitle2(String header){
        if(title != null) {
            title.setText(header);
            configureToolbar();
            configureNavigationDrawer();
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
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
                    replaceFragment(R.id.frame,f,"Landing",false);
                }
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








}
