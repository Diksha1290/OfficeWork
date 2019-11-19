package com.officework.base;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.AdapterView;

import com.officework.utils.Utilities;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by girish.sharma on 7/28/2016.
 */
public abstract class BaseCompatActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener
{
    /**
     * This method is used to initalizeUI of the layout
     */
    protected abstract void initUI(Bundle savedInstanceState);

    /**
     * This method is used to initalizeVariable
     */
    protected abstract void initVariable(Bundle savedInstanceState);
    private boolean isActivityVisible=true;

    /**
     * This method is used to show layout.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub

                super.onCreate(savedInstanceState);
        if (!isTaskRoot()
                && getIntent().hasCategory(Intent.CATEGORY_LAUNCHER)
                && getIntent().getAction() != null
                && getIntent().getAction().equals(Intent.ACTION_MAIN)) {
            finish();
            return;
        }
                initUI(savedInstanceState);
                initVariable(savedInstanceState);

        }



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
        try{
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (!Utilities.getInstance(getApplicationContext()).isNullorEmpty(tag)) {
            ft.replace(replaceId, fragment, tag);
        } else {
            ft.replace(replaceId, fragment);
        }
        if (isBackStack) {
            ft.addToBackStack(tag);
        }
        // ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();}
        catch (Exception e)
        {
            e.printStackTrace();
        }
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
        try {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if (!Utilities.getInstance(getApplicationContext()).isNullorEmpty(tag)) {
                ft.add(replaceId, fragment, tag);
            } else {
                ft.add(replaceId, fragment);
            }
            if (isBackStack) {
                ft.addToBackStack(tag);
            }
            // ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.commit();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to get the top fragmnet on the stack
     *
     * @return {@link Fragment}
     */
    protected final Fragment getTopFragmentStack() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = null;
        for (int entry = 0; entry < fm.getBackStackEntryCount(); entry++) {
            fragment = fm.findFragmentByTag(fm.getBackStackEntryAt(entry)
                    .getName());
        }
        return fragment;
    }

    /**
     * This method is used to get List of backstack fragments
     *
     * @return {@link List}
     */
    protected final List<String> getStackList() {
        List<String> stackList = new ArrayList<String>();
        stackList.clear();
        FragmentManager fm = getSupportFragmentManager();
        for (int entry = 0; entry < fm.getBackStackEntryCount(); entry++) {
            stackList.add(fm.getBackStackEntryAt(entry).getName());
        }
        return stackList;
    }

    /**
     * This method is used to get the fragment
     *
     * @param id set UniqueId
     * @return {@link Fragment}
     */
    protected Fragment getFragment(int id) {
        return getSupportFragmentManager().findFragmentById(id);
    }

    /**
     * This method is used to get the fragment
     *
     * @param tag set UniqueTag
     * @return {@link Fragment}
     */
    protected Fragment getFragment(String tag) {
        return getSupportFragmentManager().findFragmentByTag(tag);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    public void clearStackList() {
        try {
            FragmentManager fm = getSupportFragmentManager();
            for (int entry = 0; entry < fm.getBackStackEntryCount(); entry++) {
                fm.popBackStack(fm.getBackStackEntryAt(entry).getName(), 0);
            }
        }catch (Exception e){
            e.printStackTrace();

        }
    }

    /**
     * This method is used to popFragment from stack
     */
    protected final void popFragment(int replaceId) {
        try
        {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                FragmentTransaction fragTrans = getSupportFragmentManager()
                        .beginTransaction();
                fragTrans.remove(getSupportFragmentManager().findFragmentById(
                        replaceId));
                fragTrans.commit();
                getSupportFragmentManager().popBackStackImmediate();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    protected final void popFragmentWithChild(int replaceId) {
        try
        {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                FragmentTransaction fragTrans = getSupportFragmentManager()
                        .beginTransaction();
                fragTrans.remove(getSupportFragmentManager().findFragmentById(
                        replaceId));
                fragTrans.commit();
                getSupportFragmentManager().popBackStackImmediate();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putBoolean("saved", true);
        super.onSaveInstanceState(outState, outPersistentState);
    }

}
