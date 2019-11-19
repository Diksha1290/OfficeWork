package com.officework.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.legacy.app.ActionBarDrawerToggle;
import androidx.fragment.app.Fragment;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.officework.R;
import com.officework.adapters.DrawerListAdapter;
import com.officework.constants.Constants;
import com.officework.models.drawerItemModel;

import java.util.ArrayList;
import java.util.List;

public class NavigationDrawerFragment extends Fragment {

    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    private NavigationDrawerCallbacks mCallbacks;
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    ImageView imgProfilePic;
    private View mFragmentContainerView;
    TextView txtName;

    int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    List<drawerItemModel> objects;
    DrawerListAdapter adapter;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        objects = new ArrayList<>();
        for (int i = 0; i < Constants.arrDrawerResourcesActive.length; i++) {
            drawerItemModel object = new drawerItemModel();
            object.setResource(Constants.arrDrawerResourcesActive[i]);
            object.setTitle(getResources().getStringArray(R.array.arrDrawerNames)[i]);
            objects.add(object);
        }

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = false;
        }

        selectItem(0);
        // Select either the default item (0) or the last selected item.

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //  setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = (View) inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        mDrawerListView = (ListView) v.findViewById(R.id.list);
        imgProfilePic = (ImageView) v.findViewById(R.id.imgProfilePic);
        txtName = (TextView) v.findViewById(R.id.txtName);
        /*String name = Utilities.getInstance(getActivity()).getPreference(getActivity(), Preferences.FIRST_NAME.name(), "") + " " + Utilities.getInstance(getActivity()).getPreference(getActivity(), Preferences.LAST_NAME.name(), "");
        txtName.setText(TextUtils.isEmpty(name.trim()) ? "N/A" : name);*/
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });
        adapter = new DrawerListAdapter(getActivity(), R.layout.drawer_item_row, objects);
        mDrawerListView.setAdapter(adapter);
     //   mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
        return v;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawer(Gravity.START);
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerLayout.setScrimColor(getResources().getColor(R.color.Drawer_Shadow_Color));

        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }
                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                if (!isAdded()) {
                    return;
                }
              /*  Picasso.with(getActivity()).load(Utilities.getInstance(getActivity()).getPreference(getActivity(), Preferences.USER_IMAGE.name(), getString(R.string.BaseUrl))).
                        placeholder(R.drawable.profile_pic).error(R.drawable.profile_pic).noFade().fit().into(imgProfilePic);*/
                if (!mUserLearnedDrawer) {
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }
               // getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,per the navigation drawer design guidelines.
      /*  if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }*/

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    /**
     * navigation drawer item touch listerne
     * by defaut position 0 is clicked when mainactivity ia loaded
     * @param position
     */
    private void selectItem(int position) {
        try {
            mCurrentSelectedPosition = position;
            if (adapter != null) {
              //  adapter.setItemChecked(position);

            }
            if (mDrawerListView != null) {
                mDrawerListView.setItemChecked(position, true);
            }
            if (mDrawerLayout != null) {
                mDrawerLayout.closeDrawer(mFragmentContainerView);
            }
            if (mCallbacks != null) {
                mCallbacks.onNavigationDrawerItemSelected(position);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // enable or disable slide menu
    public void setDrawerLayoutEnable(boolean what) {
        if (what) {
            // Drawer will be open through swipe by user
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        } else {
            // User will not be able to open Drawer but it can be open from application programmatically
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return ((AppCompatActivity) getActivity()).getSupportActionBar();
    }

    public void onToggleButtonClick() {
        mDrawerLayout.openDrawer(mFragmentContainerView);
    }



    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }

    public void SetUserDetails() {

    }

    public void notifyDataChanged() {
        mDrawerListView.invalidateViews();
    }

}
