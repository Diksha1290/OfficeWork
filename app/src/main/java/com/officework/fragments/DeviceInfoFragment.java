package com.officework.fragments;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.officework.R;
import com.officework.base.BaseFragment;
import com.officework.constants.FragmentTag;
import com.officework.interfaces.InterfaceButtonTextChange;
import com.officework.utils.ExceptionLogUtils;
import com.officework.utils.Utilities;

import java.lang.reflect.Field;

/**
 * Created by Girish on 7/28/2016.
 * <p>
 * Device info screen shows information of device based on the sub categories.
 */
public class DeviceInfoFragment extends BaseFragment {
    View view;
    Utilities utils;
    Context ctx;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public static boolean permission = false;
    BottomNavigationView mBottomNav;

    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        try {
            if (view == null) {
                view = inflater.inflate(R.layout.fragment_device_info, null);
                utils = Utilities.getInstance(getActivity());
                ctx = getActivity();
             //   Crashlytics.getInstance().log(FragmentTag.DEVICE_INFO_FRAGMENT.name());
                initViews();
            }
            return view;
        } catch (Exception e) {
            logException(e, "DeviceInfoFragment_initUI()");
            return null;
        }

    }

    public DeviceInfoFragment() {
    }

    private void initViews() {



        mBottomNav = (BottomNavigationView) view.findViewById(R.id.bottom_navigation);

//        int color= Color.parseColor(new ThemeManager(getActivity()).getTheme());

        disableShiftMode(mBottomNav);
        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                selectFragment(item);
                return true;
            }
        });
        MenuItem selectedItem = mBottomNav.getMenu().getItem(0);
        selectFragment(selectedItem);
    }

    private void selectFragment(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_hardware:
                replaceChildFragments(R.id.btm_nav_container, new HardwareInfoFragment(), FragmentTag.HARDWARE_INFO_FRAGMENT.name(), false);
                break;
            case R.id.menu_system:
                replaceChildFragments(R.id.btm_nav_container, new SystemInfoFragment(), FragmentTag.SYSTEM_INFO_FRAGMENT.name(), false);
                break;
            case R.id.menu_battery:
                replaceChildFragments(R.id.btm_nav_container, new BatteryInfoFragment(), FragmentTag.BATTERY_INFO_FRAGMENT.name(), false);
                break;
            case R.id.menu_network:
                replaceChildFragments(R.id.btm_nav_container, new NetworkInfoFragment(), FragmentTag.NETWORK_INFO_FRAGMENT.name(), false);
                break;
            case R.id.menu_camera:
                replaceChildFragments(R.id.btm_nav_container, new CameraInfoFragment(), FragmentTag.CAMERA_INFO_FRAGMENT.name(), false);
                break;
        }

    }
    public static void disableShiftMode(BottomNavigationView view) {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
            shiftingMode.setAccessible(true);
            shiftingMode.setBoolean(menuView, false);
            shiftingMode.setAccessible(false);
            for (int i = 0; i < menuView.getChildCount(); i++) {
                BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                //noinspection RestrictedApi
             //   item.setShiftingMode(false);
                // set once again checked value, so view will be updated
                //noinspection RestrictedApi
              //  item.setDeclarationValue(item.getItemData().isDeclarationValue());
            }
        } catch (NoSuchFieldException e) {
            Log.e("BNVHelper", "Unable to get shift mode field", e);
        } catch (IllegalAccessException e) {
            Log.e("BNVHelper", "Unable to change value of shift mode", e);
        }
    }
    public void onBackPress() {
        try {
            clearAllStack();
            replaceFragment(R.id.container, new DashBoardFragment((InterfaceButtonTextChange) getActivity()), FragmentTag.DASHBOARD_FRAGMENT.name(), false);
        } catch (Exception e) {
            logException(e, "DeviceInfoFragment_onBackPress()");
        }
        /*popFragment(R.id.container);*/

    }

   /* *//**
     * Initialize view
     *//*
    private void initViews() {
        try {
            viewPager = (ViewPager) view.findViewById(R.id.viewpager);
            setupViewPager(viewPager);

            tabLayout = (TabLayout) view.findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(viewPager);
        } catch (Exception e) {
            logException(e, "DeviceInfoFragment_initViews()");
        }

    }*/

  /*  *//**
     * Initialize view pager adapter
     *
     * @param viewPager
     *//*
    private void setupViewPager(ViewPager viewPager) {
        try {
            ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
            adapter.addFragment(new HardwareInfoFragment(), getResources().getString(R.string.txtTabNamesHardware));
            adapter.addFragment(new SystemInfoFragment(), getResources().getString(R.string.txtTabNamesSystem));
            adapter.addFragment(new BatteryInfoFragment(), getResources().getString(R.string.txtTabNamesBattery));
            adapter.addFragment(new NetworkInfoFragment(), getResources().getString(R.string.txtTabNamesNetwork));
            adapter.addFragment(new CameraInfoFragment(), getResources().getString(R.string.txtTabNamesCamera));
            viewPager.setAdapter(adapter);
        } catch (Exception e) {
            logException(e, "DeviceInfoFragment_setupViewPager()");
        }

    }*/

    /**
     * Here change the header text
     ***/
    @Override
    public void onResume() {
        try {
            TitleBarFragment fragment = (TitleBarFragment) getFragment(R.id.headerContainer);
            if (fragment != null) {
                fragment.setTitleBarVisibility(true);
                fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string.txtDeviceInfo), true, false, 0);
            }
        } catch (Exception e) {
            logException(e, "DeviceInfoFragment_onResume()");
        }

        super.onResume();

    }

    public void logException(Exception e, String methodName) {
        try {
            Utilities utilities = Utilities.getInstance(getActivity());
            Activity activity = getActivity();
            Context context = getActivity();
            ExceptionLogUtils exceptionLogUtils = new ExceptionLogUtils(utilities, context, activity);
            exceptionLogUtils.addExceptionLog(utilities, context, activity, null, null, e, methodName);
        } catch (Exception exp) {
            //logException(exp, "DeviceInfoFragment_logException()");
        }

    }
    protected final void replaceChildFragments(int replaceId, Fragment fragment,
                                         String tag, boolean isBackStack) {
//        if (getActivity() == null)
//            return;
        FragmentTransaction ft = getChildFragmentManager()
                .beginTransaction();
        if (!Utilities.getInstance(getContext()).isNullorEmpty(tag)) {
            ft.replace(replaceId, fragment, tag);
        } else {
            ft.replace(replaceId, fragment);
        }
        if (isBackStack) {
            ft.addToBackStack(tag);
        }
        // ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
        //ft.commit();

    }
}
