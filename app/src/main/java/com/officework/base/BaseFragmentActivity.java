package com.officework.base;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;

import com.officework.utils.Utilities;

import java.util.ArrayList;
import java.util.List;


/**
 * This is customized abstract activity class. This class has two abstarct
 * method which is forcefully override from this activity.
 *
 * @method initUI() method for initialize User Interface widgets
 * @method initVariable() method for initalize Variable These method is called
 *         after on create called.
 * @method getCameraResult(Uri uri, boolean isOk) method called when camera
 *         image is clicked.
 * @method getGalleryResult(Uri uri, boolean isOk)method called when image
 *         selected from gallery OnActivityResult the orientation of image is
 *         checked here if orientation is different here it is made to 90 degree
 *         and then return to the specific method
 *
 *
 * @author amit.singh
 *
 */
public abstract class BaseFragmentActivity extends FragmentActivity implements
		OnClickListener, OnItemClickListener {
	/**
	 * This method is used to initalizeUI of the layout
	 */
	protected abstract void initUI();

	/**
	 * This method is used to initalizeVariable
	 */
	protected abstract void initVariable();




	/**
	 * This method is used to show layout.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initUI();
		initVariable();

	}



	/**
	 * This method is used to replaceFragment with another fragment
	 *
	 * @param replaceId
	 *            Set id of the view on which fragment is to replaced
	 * @param fragment
	 *            fragment which is to called
	 * @param tag
	 *            Set tag if needed otherwise set null
	 * @param isBackStack
	 *            Set true if need backStack else false
	 */
	protected final void replaceFragment(int replaceId, Fragment fragment,
										 String tag, boolean isBackStack) {
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
		ft.commit();
	}

	/**
	 * This method is used to addFragment for the first time
	 *
	 * @param replaceId
	 *            Set id of the view on which fragment is to replaced
	 * @param fragment
	 *            fragment which is to called
	 * @param tag
	 *            Set tag if needed otherwise set null
	 * @param isBackStack
	 *            Set true if need backStack else false
	 */
	protected final void addFragment(int replaceId, Fragment fragment,
									 String tag, boolean isBackStack) {
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
	 * @param id
	 *            set UniqueId
	 * @return {@link Fragment}
	 */
	protected Fragment getFragment(int id) {
		return getSupportFragmentManager().findFragmentById(id);
	}

	/**
	 * This method is used to get the fragment
	 *
	 * @param tag
	 *            set UniqueTag
	 * @return {@link Fragment}
	 */
	protected Fragment getFragment(String tag) {
		return getSupportFragmentManager().findFragmentByTag(tag);
	}

}
