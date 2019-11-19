package com.officework.activities;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import com.officework.utils.StringUtil;


/**
 * Created by Diksha on 8/23/2018.
 */

abstract class BaseActivity extends AppCompatActivity {
    protected abstract void setUpLayout();

    protected abstract void setDataInViewObjects();

    protected abstract void setUpToolBar();


    /**
     * This method is used to replaceFragment with another fragment
     *
     * @param replaceId   Set id of the view on which fragment is to replaced
     * @param fragment    fragment which is to called
     * @param tag         Set tag if needed otherwise set null
     * @param isBackStack Set true if need backStack else false
     */
    protected final void replaceFragment(int replaceId, Fragment fragment,
                                         String tag, boolean isBackStack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (!StringUtil.isNullorEmpty(tag)) {
            ft.replace(replaceId, fragment, tag);
        } else {
            ft.replace(replaceId, fragment);
        }
        if (isBackStack) {
            ft.addToBackStack(tag);
        }
        // ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    /**
     * This method is used to addFragment for the first time
     *
     * @param replaceId   Set id of the view on which fragment is to replaced
     * @param fragment    fragment which is to called
     * @param tag         Set tag if needed otherwise set null
     * @param isBackStack Set true if need backStack else false
     */
    protected final void addFragment(int replaceId, Fragment fragment,
                                     String tag, boolean isBackStack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (!StringUtil.isNullorEmpty(tag)) {
            ft.add(replaceId, fragment, tag);
        } else {
            ft.add(replaceId, fragment);
        }
        if (isBackStack) {
            ft.addToBackStack(tag);
        }
        // ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }



}
