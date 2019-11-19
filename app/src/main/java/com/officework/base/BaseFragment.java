package com.officework.base;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.officework.fragments.AccelerometerTestFragment;
import com.officework.fragments.BatteryMannualFragment;
import com.officework.fragments.ChargingManualFragment;
import com.officework.fragments.CompassTestFragment;
import com.officework.fragments.EarJackManualFragment;
import com.officework.fragments.FingerPrintTestFragment;
import com.officework.fragments.HomeButtonManualFragment;
import com.officework.fragments.ManualTestFragment;
import com.officework.fragments.PhoneShakingManualFragment;
import com.officework.fragments.PowerButtonManualFragment;
import com.officework.fragments.ProximitySensorManualFragment;
import com.officework.fragments.TitleBarFragment;
import com.officework.interfaces.InterfaceAlertDissmiss;
import com.officework.interfaces.InterfaceButtonTextChange;
import com.officework.interfaces.TimerDialogInterface;
import com.officework.testing_profiles.utils.ConstantTestIDs;
import com.google.android.material.snackbar.Snackbar;
import com.officework.R;
import com.officework.activities.MainActivity;
import com.officework.constants.Constants;
import com.officework.fragments.MicAndSpeakerManualFragment;
import com.officework.fragments.VolumeManualFragment;
import com.officework.models.AutomatedTestListModel;
import com.officework.testing_profiles.Controller.ManualTestsOperation;
import com.officework.testing_profiles.Controller.TestController;
import com.officework.testing_profiles.ui.fragment.AutomatedTestFragment;
import com.officework.testing_profiles.ui.fragment.Manual2SemiAutomaticTestsFragment;
import com.officework.utils.Utilities;
import com.officework.utils.WebserviceUrls;
import com.officework.utils.socket.SocketConstants;
import com.officework.utils.socket.SocketHelper;
import com.officework.utils.socket.SocketListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;

import static com.officework.fragments.ManualTestFragment.viewPagerItemPOsition;


/**
 * This is customized abstract Fragment class. This class has one abstarct
 * method which is forcefully override from this fragment.
 * <p>
 * android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 *
 * @author girish.sharma
 */
public abstract class BaseFragment extends Fragment implements OnClickListener,
        OnItemClickListener {

    public SocketHelper socketHelper = null;
    protected View mView;
    boolean isDeviceRemoved = false;
    Utilities utils;
    private int requestCode;


    boolean firsttime=true;

    Handler timerHandler;
    Runnable runnable;
    TimerDialogInterface timerDialogInterface1;
    AlertDialog.Builder alert;
    public AlertDialog dialog;
    Snackbar snackbar;
    //    android.support.v4.app.FragmentManager fm;
//    android.support.v4.app.Fragment frag;
    FragmentManager fm ;
    Fragment frag;
    public static int partitionSize;

    /**
     * This method is used to getCameraResult
     */
    protected void getCameraResult(Uri uri, boolean isOk, int requestCode) {

    }

    /**
     * This method is used to getGalleryResult
     */
    protected void getGalleryResult(Uri uri, boolean isOk, int requestCode) {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        utils = Utilities.getInstance(getActivity());
        if (getFragmentManager().findFragmentById(R.id.container) instanceof AutomatedTestFragment) {

        } else {
            createConnection(null);
        }
        return initUI(inflater, container);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * Inflate View in this method back end called
     * {@link BaseFragment#onCreateView(LayoutInflater, ViewGroup, Bundle)}
     */
    protected abstract View initUI(LayoutInflater inflater, ViewGroup container);

    public final <T extends View> T findViewById(int id) {
        T view = null;
        View genericView = mView.findViewById(id);
        try {
            view = (T) (genericView);
        } catch (Exception ex) {
            String message = "Can't cast view (" + id + ") to a "
                    + view.getClass() + ".  Is actually a "
                    + genericView.getClass() + ".";
            throw new ClassCastException(message);
        }

        return view;
    }

    /**
     * This method is used to startActivity with using inbuild Flag
     * Intent.FLAG_ACTIVITY_REORDER_TO_FRONT. Here bundle is also passed if want
     * to send data to another activity
     *
     * @param bundle send data to another activity
     * @param uri
     */
    protected final void startActivity(Bundle bundle, Class<?> uri) {
        Intent intent = new Intent(getActivity(), uri)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }


    public int checkSingleHardware(String pref_Key) {
        int testStatus = -1;
        try {
            Utilities utils = Utilities.getInstance(getActivity());
            if (utils.getPreferenceInt(getActivity(), pref_Key, 0) == -1 || utils.getPreferenceInt(getActivity(), pref_Key, 0) == -2) {
                testStatus = Constants.TEST_IN_QUEUE;
            } else if (utils.getPreferenceInt(getActivity(), pref_Key, 0) == 0) {
                testStatus = Constants.TEST_FAILED;
            } else {
                testStatus = Constants.TEST_PASS;
            }
            return testStatus;
        } catch (Exception e) {

            return testStatus;
        }

    }

    /**
     * This method is used to startActivityForResult. In this the Result to
     * called activity callback to the calling activity
     *
     * @param uri         class to be called
     * @param requestCode uniqueCode
     */
    protected final void startActivityForResult(Class<?> uri, int requestCode) {
        startActivityForResult(
                new Intent(getActivity(), uri).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP), requestCode);

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
        if (getActivity() == null)
            return;
        FragmentTransaction ft = getActivity().getSupportFragmentManager()
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
        ft.commitAllowingStateLoss();
        //ft.commit();
        //ft.commit();

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
        if (getActivity() == null)
            return;
        try {
            FragmentTransaction ft = getActivity().getSupportFragmentManager()
                    .beginTransaction();
            if (!Utilities.getInstance(getContext()).isNullorEmpty(tag)) {
                ft.add(replaceId, fragment, tag);
            } else {
                ft.add(replaceId, fragment);
            }
            if (isBackStack) {
                ft.addToBackStack(tag);
            }
// ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            ft.commitAllowingStateLoss();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to popFragment from stack
     */
    protected final void popFragment(int replaceId) {
        if (getActivity() == null)
            return;
        if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {

            if (getActivity().getSupportFragmentManager().isStateSaved()) {
                return;
            }
            FragmentTransaction fragTrans = getActivity()
                    .getSupportFragmentManager().beginTransaction();
            fragTrans.remove(getActivity().getSupportFragmentManager()
                    .findFragmentById(replaceId));
            fragTrans.commitAllowingStateLoss();
            getActivity().getSupportFragmentManager().popBackStackImmediate();
        }
    }

    /**
     * This method is used to popFragment from stack
     */
    protected final void popFragment(String tag) {
        if (getActivity() == null)
            return;
        if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
            FragmentTransaction fragTrans = getActivity()
                    .getSupportFragmentManager().beginTransaction();
            fragTrans.remove(getActivity().getSupportFragmentManager()
                    .findFragmentByTag(tag));
            fragTrans.commit();
            getActivity().getSupportFragmentManager().popBackStackImmediate();
        }
    }

    /**
     * This method is used to clear all the fragments from stack
     */
    protected final void clearBackStack() {
        try {
            if (getActivity() == null)
                return;
            FragmentManager fm = getActivity().getSupportFragmentManager();
            for (int i = 0; i < fm.getBackStackEntryCount(); i++) {
                String fragTag = fm.getBackStackEntryAt(i).getName();
                Fragment fragment = fm.findFragmentByTag(fragTag);
                FragmentTransaction fragTrans = getActivity()
                        .getSupportFragmentManager().beginTransaction();
                fragTrans.remove(fragment);
                fragTrans.commit();
                fm.popBackStack();
            }

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to remove all the stack in async
     */
    protected final void clearAllStack() {
        try {
            if (getActivity() == null)
                return;
            FragmentManager fm = getActivity().getSupportFragmentManager();
            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is used to remove all the stack in sync
     */
    protected final void clearAllStackImmediate() {
        if (getActivity() == null)
            return;
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    /**
     * This method is used to get the top fragmnet on the stack
     *
     * @return {@link Fragment}
     */
    protected final Fragment getTopFragmentStack() {
        if (getActivity() == null)
            return null;
        FragmentManager fm = getActivity().getSupportFragmentManager();
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
        if (getActivity() == null)
            return null;
        List<String> stackList = new ArrayList<String>();
        stackList.clear();
        FragmentManager fm = getFragmentManager();
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
    public  Fragment getFragment(int id) {
        if (getActivity() == null)
            return null;
        return getActivity().getSupportFragmentManager().findFragmentById(id);
    }

    /**
     * This method is used to get the fragment
     *
     * @param tag set UniqueTag
     * @return {@link Fragment}
     */
    protected Fragment getFragment(String tag) {
        if (getActivity() == null)
            return null;
        return getActivity().getSupportFragmentManager().findFragmentByTag(tag);
    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View arg1,
                            int position, long arg3) {
        // TODO Auto-generated method stub

    }


    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//		TestController testController = TestController.getInstance(getActivity());
//		testController.removeAllRegisteredReceviers();

    }

    public String checkMutipleHardware1(String pref_Key_1, String pref_Key_2) {
        String testStatus = "";
        try {

            if (Utilities.getInstance(getActivity()).getPreferenceInt(getActivity(), pref_Key_1,
                    0) == -1 && Utilities.getInstance(getActivity()).getPreferenceInt(getActivity(), pref_Key_2, 0) == -1) {
                testStatus = "";
            } else if (Utilities.getInstance(getActivity()).getPreferenceInt(getActivity(),
                    pref_Key_1, 0) == 0 && Utilities.getInstance(getActivity()).getPreferenceInt(getActivity(), pref_Key_2, 0) == 0) {
                testStatus = Constants.FAILED;
            } else if (Utilities.getInstance(getActivity()).getPreferenceInt(getActivity(),
                    pref_Key_1, 0) == 1 && Utilities.getInstance(getActivity()).getPreferenceInt(getActivity(), pref_Key_2, 0) == 1) {
                testStatus = Constants.COMPLETED;
            } else if (Utilities.getInstance(getActivity()).getPreferenceInt(getActivity(),
                    pref_Key_1, 0) == 1 || Utilities.getInstance(getActivity()).getPreferenceInt(getActivity(), pref_Key_2, 0) == 0) {
                testStatus = Constants.FAILED;
            } else if (Utilities.getInstance(getActivity()).getPreferenceInt(getActivity(),
                    pref_Key_1, 0) == 0 || Utilities.getInstance(getActivity()).getPreferenceInt(getActivity(), pref_Key_2, 0) == 1) {
                testStatus = Constants.FAILED;
            }/* else {
                testStatus = AsyncConstant.TEST_PASS;
            }*/
            return testStatus;
        } catch (Exception e) {

            return testStatus;

        }
    }

    public String checkSingleHardware1(String pref_Key) {
        String testStatus = "";
        try {

            Utilities utils = Utilities.getInstance(getActivity());
            if (utils.getPreferenceInt(getActivity(), pref_Key, 0) == -1 || utils.getPreferenceInt(getActivity(), pref_Key, 0) == -2) {
                testStatus = "";
            } else if (utils.getPreferenceInt(getActivity(), pref_Key, 0) == 0) {
                testStatus = Constants.FAILED;
            } else {
                testStatus = Constants.COMPLETED;
            }
            return testStatus;
        } catch (Exception e) {

            return testStatus;
        }

    }

    protected final void snackShow(View relativeLayout) {
        try {
            Snackbar snackbar = Snackbar
                    .make(relativeLayout, getResources().getString(R.string.txtBackPressed),
                            Snackbar.LENGTH_LONG)
                    .setAction(getResources().getString(R.string.txtBackPressedYes),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    try {

                                        TitleBarFragment titleBarFragment =
                                                (TitleBarFragment) getFragment(R.id.headerContainer);
                                        titleBarFragment.showSwitchLayout(true);
                                        FragmentManager fm =
                                                getActivity().getSupportFragmentManager();
                                        Fragment frag = fm.findFragmentById(R.id.container);
                                        if (frag instanceof VolumeManualFragment) {
                                            ((VolumeManualFragment) frag).backbutton();
                                        }
//                                        else if (frag instanceof CameraFragment) {
//                                            ((CameraFragment) frag).backbutton();
//
//                                        }
                                        else if (frag instanceof MicAndSpeakerManualFragment) {
                                            ((MicAndSpeakerManualFragment) frag).backbutton();
                                        }
                                        final MainActivity activity = (MainActivity) getActivity();
//                                        if (Constants.isDoAllClicked) {
//                                            if (viewPagerItemPOsition == 1) {
//                                                AutomatedTestListModel automatedTestListModel =
//                                                        SemiAutomaticTestsFragment.testListModelList.get(activity.semiIndex - 1);
//                                                sendTestData(SocketConstants.EVENT_TEST_END,
//                                                        automatedTestListModel.getTest_id(),
//                                                        checkSingleHardware1("MMR_" + automatedTestListModel.getTest_id()),
//                                                        automatedTestListModel.getName(), "",
//                                                        Utilities.getInstance(getActivity()).getPreference(getActivity(), Constants.ANDROID_ID, ""), Constants.MANUAL1);
//
//                                            } else {
//                                                AutomatedTestListModel automatedTestListModel =
//                                                        Manual2SemiAutomaticTestsFragment.testListModelList.get(activity.manualIndex - 1);
//                                                sendTestData(SocketConstants.EVENT_TEST_END,
//                                                        automatedTestListModel.getTest_id(),
//                                                        checkSingleHardware1("MMR_" + automatedTestListModel.getTest_id()),
//                                                        automatedTestListModel.getName(), "",
//                                                        Utilities.getInstance(getActivity()).getPreference(getActivity(), Constants.ANDROID_ID, ""), Constants.MANUAL2);
//                                            }
//
//
//                                        } else {
//                                            if (viewPagerItemPOsition == 1) {
//                                                AutomatedTestListModel automatedTestListModel =
//                                                        SemiAutomaticTestsFragment.testListModelList.get(activity.semiBackIndex);
//                                                sendTestData(SocketConstants.EVENT_TEST_END,
//                                                        automatedTestListModel.getTest_id(),
//                                                        checkSingleHardware1("MMR_" + automatedTestListModel.getTest_id()),
//                                                        automatedTestListModel.getName(), "",
//                                                        Utilities.getInstance(getActivity()).getPreference(getActivity(), Constants.ANDROID_ID, ""), Constants.MANUAL1);
//
//                                            } else {
//                                                AutomatedTestListModel automatedTestListModel =
//                                                        Manual2SemiAutomaticTestsFragment.testListModelList.get(activity.manualBackIndex);
//                                                sendTestData(SocketConstants.EVENT_TEST_END,
//                                                        automatedTestListModel.getTest_id(),
//                                                        checkSingleHardware1("MMR_" + automatedTestListModel.getTest_id()),
//                                                        automatedTestListModel.getName(), "",
//                                                        Utilities.getInstance(getActivity()).getPreference(getActivity(), Constants.ANDROID_ID, ""), Constants.MANUAL2);
//                                            }
//                                        }
                                        try {
                                            if (getActivity() != null) {
                                                getActivity().getSupportFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.container)).commit();
                                                getActivity().getSupportFragmentManager().popBackStackImmediate();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
            // Changing message text color
            snackbar.setActionTextColor(Color.WHITE);
            View sbView = snackbar.getView();
            TextView textView =
                    (TextView) sbView.findViewById(R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            (snackbar.getView()).getLayoutParams().width =ViewGroup.LayoutParams.MATCH_PARENT;
            snackbar.show();
        } catch (Exception e) {
            //logException(e, "EarJackManualFragment_snackShow()");
        }

    }












    protected final void snackShow(View relativeLayout, final InterfaceAlertDissmiss... alertDissmisses) {
        try {
            snackbar = Snackbar
                    .make(relativeLayout, getResources().getString(R.string.txtBackPressed), Snackbar.LENGTH_LONG)
                    .setAction(getResources().getString(R.string.txtBackPressedYes), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            try {
                                if (alertDissmisses != null && alertDissmisses.length>0) {
                                    alertDissmisses[0].onButtonClick(true, 23);
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            /*Constants.isBackButton = true;*/
                            //                            clearAllStack();
                            //                            replaceFragment(R.id.container, new ManualTestFragment((InterfaceButtonTextChange) getActivity()), FragmentTag.MANUAL_TEST_FRAGMENT.name(), false);
                            try {
                                if (getActivity() != null) {
                                    getActivity().getSupportFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.container)).commit();
                                    getActivity().getSupportFragmentManager().popBackStackImmediate();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
            // Changing message text color
            snackbar.setActionTextColor(Color.WHITE);
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            (snackbar.getView()).getLayoutParams().width =ViewGroup.LayoutParams.MATCH_PARENT;
            snackbar.show();
        } catch (Exception e) {
            //logException(e, "EarJackManualFragment_snackShow()");
        }

    }

    protected final void nextPress(MainActivity activity1, final boolean semi) {
        activity1.onChangeText(R.string.textSkip,false);
        activity1.findViewById(R.id.btnNext).setEnabled(false);

        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {


                    final MainActivity activity = (MainActivity) getActivity();


                    TestController testController = TestController.getInstance(getActivity());


                    if (activity != null) {

                        if (Constants.isDoAllClicked) {


                            if (activity.semiIndex == ManualTestFragment.testListModelList.size()) {

                                if(activity.isAllSkipDone){
//                                    semiFinish2(activity,activity.semiIndex);
                                  }
                                else {
                                    MainActivity.testListSemi.put(activity.semiIndex - 1, true);

                                    if (!activity.skipMapSemi.contains(activity.semiIndex - 1)) {
                                        activity.skipMapSemi.add(activity.semiIndex - 1);
                                    }

                                    if (activity.skipMapSemi.size()  != ManualTestFragment.testListModelList.size()) {


                                        activity.semiIndex = 0;


                                        int index = checkIfTestPerformed2(activity.semiIndex);
                                        if (index == 999) {

                                            int nextTest = checkIfTestPerformed2(0);
                                            semiTests2(activity, nextTest+partitionSize);

                                        } else {
                                            semiTests2(activity,index);
                                        }
                                    } else {
                                        resetSkip2(activity);
                                    }
                                }
                            } else if (activity.semiIndex != ManualTestFragment.testListModelList.size()) {
                                if(activity.isAllSkipDone){
                                    semiTests2(activity,activity.semiIndex);
                                }
                                else {
                                    MainActivity.testListSemi.put(activity.semiIndex - 1, true);
                                    if (!activity.skipMapSemi.contains(activity.semiIndex - 1)) {
                                        activity.skipMapSemi.add(activity.semiIndex - 1);
                                    }
                                    if (activity.skipMapSemi.size() != ManualTestFragment.testListModelList.size()) {

                                        int nextTest = checkIfTestPerformed2(activity.semiIndex);

                                        if (nextTest == 999) {
                                            activity.semiIndex = 0;
                                            int index = checkIfTestPerformed2(0);
                                            semiTests2(activity, index);

                                        } else {

                                            semiTests2(activity, nextTest);
                                        }
                                    } else {
                                        resetSkip2(activity);


                                    }
                                }
                            }


                        }
                        else{
                            MainActivity.testListSemi.put(activity.semiIndex - 1, true);
                            if (!activity.skipMapSemi.contains(activity.semiIndex - 1)) {
                                activity.skipMapSemi.add(activity.semiIndex - 1);
                            }
                            try {
                                if (getActivity() != null) {
                                    getActivity().getSupportFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.container)).commit();
                                    getActivity().getSupportFragmentManager().popBackStackImmediate();
                                  if(activity.map.size() == ManualTestFragment.testListModelList.size()) {
                                      resetSkip2(activity);
                                  }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }


                    }
                }
            },1000);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    public void semiTests2(MainActivity activity,int nextTest){
// MainActivity.testListSemi.replace(activity.semiIndex-1,true);


// int nextTest=checkIfTestPerformed(activity.semiIndex,Constants.MANUAL1);
        activity.semiIndex=nextTest;


        AutomatedTestListModel automatedTestListModel =
                ManualTestFragment.testListModelList.get(activity.semiIndex);


        replaceFragment(R.id.container,
                ManualTestsOperation.launchScreens(automatedTestListModel,
                        utils.getPreference(getActivity(),
                                Constants.ANDROID_ID, ""),getActivity()), null, false);
        if(activity.semiIndex<partitionSize){
            viewPagerItemPOsition=1;
        }else if(activity.semiIndex>partitionSize-1 && activity.semiIndex<partitionSize+partitionSize){
            viewPagerItemPOsition=2;
        }else if(activity.semiIndex>partitionSize+partitionSize-1){
            viewPagerItemPOsition=3;
        }
        activity.semiIndex++;
//        viewPagerItemPOsition = 1;


    }



    public void semiFinish2(MainActivity activity,int i){
        activity.semiIndex = 0;
// MainActivity.testListSemi.replace(activity.semiIndex,true);



// int index=checkIfTestPerformed(activity.manualIndex,Constants.MANUAL);
// activity.manualIndex=index;


        AutomatedTestListModel automatedTestListModel =
                Manual2SemiAutomaticTestsFragment.testListModelList.get(activity.manualIndex);
        utils = Utilities.getInstance(getActivity());
        replaceFragment(R.id.container,
                ManualTestsOperation.launchScreens(automatedTestListModel,
                        utils.getPreference(getActivity(),
                                Constants.ANDROID_ID, ""),getActivity()), null, false);
        activity.manualIndex++;
        viewPagerItemPOsition = 1;
    }



    public int checkIfTestPerformed2(int index) {
        int i = index;

        try {

            while (MainActivity.testListSemi.get(i)) {

                i++;
                if(ManualTestFragment.testListModelList.size()==i){
                    i=999;
                    break;
                }
            }
//
//            if (s.equals(Constants.MANUAL1)) {
//
//                while (MainActivity.testListSemi.get(i)) {
//
//                    i++;
//                    if(SemiAutomaticTestsFragment.testListModelList.size()==i){
//                        i=999;
//                        break;
//                    }
//                }
//            }else if(s.equals(Constants.MANUAL)){
//                while (MainActivity.testListManual.get(i)) {
//
//                    i++;
//                    if(Manual2SemiAutomaticTestsFragment.testListModelList.size()==i){
//                        i=999;
//                        break;
//                    }
//                }
//
//            }
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return i;

    }



    public void resetSkip2(MainActivity mainActivity){

//        mainActivity.skipMapManual.clear();
        mainActivity.skipMapSemi.clear();
      try {
          for (int i = 0; i < ManualTestFragment.testListModelList.size(); i++) {
              if (ManualTestFragment.testListModelList.get(i).getIsTestSuccess() == 1) {
                  MainActivity.testListSemi.put(i, true);
                  if (!mainActivity.skipMapSemi.contains(i)) {
                      mainActivity.skipMapSemi.add(i);
                  }

              } else {
                  MainActivity.testListSemi.put(i, false);
              }

          }
      }
      catch (Exception e){
          e.printStackTrace();
      }
//        for(int i=0;i<Manual2SemiAutomaticTestsFragment.testListModelList.size();i++){
//            if(Manual2SemiAutomaticTestsFragment.testListModelList.get(i).getIsTestSuccess() == 1){
//                MainActivity.testListManual.put(i,true);
//                if(!mainActivity.skipMapManual.contains(i)) {
//                    mainActivity.skipMapManual.add(i);
//                }
//
//            }else {
//                MainActivity.testListManual.put(i, false);
//            }
//        }


        try {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.container)).commit();
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mainActivity.isAllSkipDone=true;
    }








//
//
//
//    public void semiTests(MainActivity activity,int nextTest){
//
//        activity.semiIndex=nextTest;
//
//
//        AutomatedTestListModel automatedTestListModel =
//                SemiAutomaticTestsFragment.testListModelList.get(activity.semiIndex);
//
//
//        replaceFragment(R.id.container,
//                ManualTestsOperation.launchScreens(automatedTestListModel,
//                        utils.getPreference(getActivity(),
//                                Constants.ANDROID_ID, ""),getActivity()), null, false);
//        activity.semiIndex++;
//        viewPagerItemPOsition = 1;
//
//    }
//
//
//    public void semiFinish(MainActivity activity,int i){
//        activity.manualIndex = 0;
//
//        AutomatedTestListModel automatedTestListModel =
//                Manual2SemiAutomaticTestsFragment.testListModelList.get(activity.manualIndex);
//        utils = Utilities.getInstance(getActivity());
//        replaceFragment(R.id.container,
//                ManualTestsOperation.launchScreens(automatedTestListModel,
//                        utils.getPreference(getActivity(),
//                                Constants.ANDROID_ID, ""),getActivity()), null, false);
//        activity.manualIndex++;
//        viewPagerItemPOsition = 1;
//    }
//
//
//    public void manualTests(MainActivity activity,int nextIndex){
//
//        activity.manualIndex=nextIndex;
//
//
//        AutomatedTestListModel automatedTestListModel =
//                Manual2SemiAutomaticTestsFragment.testListModelList.get(activity.manualIndex);
//        utils = Utilities.getInstance(getActivity());
//        replaceFragment(R.id.container,
//                ManualTestsOperation.launchScreens(automatedTestListModel,
//                        utils.getPreference(getActivity(),
//                                Constants.ANDROID_ID, ""),getActivity()), null, false);
//        activity.manualIndex++;
//        viewPagerItemPOsition = 2;
//
//    }
//
//    public void manualFinish(MainActivity activity,int i){
//
//        try {
//
//
//            getActivity().getSupportFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.container)).commit();
//            getActivity().getSupportFragmentManager().popBackStackImmediate();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//




//
//    protected final void nextPress(MainActivity activity1, final boolean semi) {
//        activity1.onChangeText(R.string.textSkip, false);
//
//
//        try {
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//
//                    final MainActivity activity = (MainActivity) getActivity();
//                    TestController testController = TestController.getInstance(getActivity());
//                    if (testController.nextPressList.size() == SemiAutomaticTestsFragment.testListModelList.size() + Manual2SemiAutomaticTestsFragment.testListModelList.size() + AutomatedTestFragment.testModelArrayList.size()) {
//                        viewPagerItemPOsition = 3;
//                    }
//                    if (activity != null) {
////activity.onChangeText(R.string.textSkip, false);
//                        if (semi && Constants.isDoAllClicked) {
//                            if (activity.semiIndex == SemiAutomaticTestsFragment.testListModelList.size()) {
//
//                                if(activity.isAllSkipDone){
//                                    semiFinish(activity,activity.manualIndex);
//                                }
//                                else {
//                                    MainActivity.testListSemi.put(activity.semiIndex - 1, true);
//
//                                    if (!activity.skipMapSemi.contains(activity.semiIndex - 1)) {
//                                        activity.skipMapSemi.add(activity.semiIndex - 1);
//                                    }
//
//                                    if (activity.skipMapSemi.size() + activity.skipMapManual.size() != Manual2SemiAutomaticTestsFragment.testListModelList.size() + SemiAutomaticTestsFragment.testListModelList.size()) {
//
//
//                                        activity.manualIndex = 0;
//
//
//                                        int index = checkIfTestPerformed(activity.manualIndex, Constants.MANUAL);
//                                        if (index == 999) {
//
//                                            int nextTest = checkIfTestPerformed(0, Constants.MANUAL1);
//                                            semiTests(activity, nextTest);
//
//                                        } else {
//                                            manualTests(activity,index);
//// semiFinish(activity, index);
//                                        }
//                                    } else {
//                                        resetSkip(activity);
//
//
//                                    }
//                                }
//
//                            } else if (activity.semiIndex != SemiAutomaticTestsFragment.testListModelList.size()) {
//
//                                if(activity.isAllSkipDone){
//                                    semiTests(activity,activity.semiIndex);
//                                }
//                                else {
//                                    MainActivity.testListSemi.put(activity.semiIndex - 1, true);
//                                    if (!activity.skipMapSemi.contains(activity.semiIndex - 1)) {
//                                        activity.skipMapSemi.add(activity.semiIndex - 1);
//                                    }
//                                    if (activity.skipMapSemi.size() + activity.skipMapManual.size() != Manual2SemiAutomaticTestsFragment.testListModelList.size() + SemiAutomaticTestsFragment.testListModelList.size()) {
//
//                                        int nextTest = checkIfTestPerformed(activity.semiIndex, Constants.MANUAL1);
//
//                                        if (nextTest == 999) {
//                                            int index = checkIfTestPerformed(0, Constants.MANUAL);
//                                            if (index == 999) {
//                                                int i = checkIfTestPerformed(0, Constants.MANUAL1);
//                                                semiTests(activity, i);
//                                            } else {
//                                                manualTests(activity, index);
//                                            }
//                                        } else {
//
//                                            semiTests(activity, nextTest);
//                                        }
//                                    } else {
//                                        resetSkip(activity);
//
//
//                                    }
//                                }
//                            }
//                        } else if (Constants.isDoAllClicked && activity.manualIndex != Manual2SemiAutomaticTestsFragment.testListModelList.size()) {
//
//                            if(activity.isAllSkipDone){
//                                manualTests(activity,activity.manualIndex);
//                            }
//                            else {
//
//                                MainActivity.testListManual.put(activity.manualIndex - 1, true);
//                                if (!activity.skipMapManual.contains(activity.manualIndex - 1)) {
//                                    activity.skipMapManual.add(activity.manualIndex - 1);
//                                }
//
//                                if (activity.skipMapSemi.size() + activity.skipMapManual.size() != Manual2SemiAutomaticTestsFragment.testListModelList.size() + SemiAutomaticTestsFragment.testListModelList.size()) {
//
//                                    int nextIndex = checkIfTestPerformed(activity.manualIndex, Constants.MANUAL);
//
//                                    if (nextIndex == 999) {
//                                        int index = checkIfTestPerformed(0, Constants.MANUAL1);
//                                        if (index == 999) {
//                                            int i = checkIfTestPerformed(0, Constants.MANUAL);
//                                            manualTests(activity, i);
//                                        } else {
//                                            semiTests(activity, index);
//                                        }
//                                    } else {
//
//                                        manualTests(activity, nextIndex);
//                                    }
//                                } else {
//                                    resetSkip(activity);
//
//                                }
//                            }
//
//                        } else if (Constants.isDoAllClicked && activity.manualIndex == Manual2SemiAutomaticTestsFragment.testListModelList.size()) {
//
//// try {
//                            if (activity.isAllSkipDone) {
//                                manualFinish(activity, 0);
//                            }
//                            else{
//                                MainActivity.testListManual.put(activity.manualIndex - 1, true);
//                                if (!activity.skipMapManual.contains(activity.manualIndex - 1)) {
//                                    activity.skipMapManual.add(activity.manualIndex - 1);
//                                }
//                                if (activity.skipMapSemi.size() + activity.skipMapManual.size() != Manual2SemiAutomaticTestsFragment.testListModelList.size() + SemiAutomaticTestsFragment.testListModelList.size()) {
//
//                                    activity.semiIndex = 0;
//
//                                    int nextTest = checkIfTestPerformed(activity.semiIndex, Constants.MANUAL1);
//
//                                    if (nextTest == 999) {
//                                        int index = checkIfTestPerformed(0, Constants.MANUAL);
//                                        manualTests(activity, index);
//
//                                    } else {
//
//                                        semiTests(activity, nextTest);
//                                    }
//                                } else {
//                                    resetSkip(activity);
//
//                                }
//
//                            }
//                        } else if (!Constants.isDoAllClicked) {
//                            try {
//                                if (getActivity() != null) {
//                                    getActivity().getSupportFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.container)).commit();
//                                    getActivity().getSupportFragmentManager().popBackStackImmediate();
//                                    resetSkip(activity);
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }
//            }, 1000);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//



//    public void resetSkip(MainActivity mainActivity){
//
//        mainActivity.skipMapManual.clear();
//        mainActivity.skipMapSemi.clear();
//        for(int i=0;i<SemiAutomaticTestsFragment.testListModelList.size();i++){
//            if(SemiAutomaticTestsFragment.testListModelList.get(i).getIsTestSuccess() == 1){
//                MainActivity.testListSemi.put(i,true);
//                if(!mainActivity.skipMapSemi.contains(i)) {
//                    mainActivity.skipMapSemi.add(i);
//                }
//
//            }else {
//                MainActivity.testListSemi.put(i, false);
//            }
//
//        }
//        for(int i=0;i<Manual2SemiAutomaticTestsFragment.testListModelList.size();i++){
//            if(Manual2SemiAutomaticTestsFragment.testListModelList.get(i).getIsTestSuccess() == 1){
//                MainActivity.testListManual.put(i,true);
//                if(!mainActivity.skipMapManual.contains(i)) {
//                    mainActivity.skipMapManual.add(i);
//                }
//
//            }else {
//                MainActivity.testListManual.put(i, false);
//            }
//        }
//
//
//        try {
//            if (getActivity() != null) {
//                getActivity().getSupportFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.container)).commit();
//                getActivity().getSupportFragmentManager().popBackStackImmediate();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        mainActivity.isAllSkipDone=true;
//    }



//
//    public int checkIfTestPerformed(int index,String s) {
//        int i = index;
//
//        try {
//
//
//
//            if (s.equals(Constants.MANUAL1)) {
//
//                while (MainActivity.testListSemi.get(i)) {
//
//                    i++;
//                    if(SemiAutomaticTestsFragment.testListModelList.size()==i){
//                        i=999;
//                        break;
//                    }
//                }
//            }else if(s.equals(Constants.MANUAL)){
//                while (MainActivity.testListManual.get(i)) {
//
//                    i++;
//                    if(Manual2SemiAutomaticTestsFragment.testListModelList.size()==i){
//                        i=999;
//                        break;
//                    }
//                }
//
//            }
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
//
//        return i;
//
//    }










    public SocketHelper createConnection(SocketListener socketListener) {
        try {
            String androidId = Utilities.getInstance(getActivity()).getPreference(getActivity(),
                    Constants.ANDROID_ID, "");

            if (Utilities.getInstance(getActivity()).isInternetWorking(getActivity())) {
                if (!androidId.isEmpty()) {
                    IO.Options options = new IO.Options();
                    Log.d("base Socket", androidId);
                    socketHelper =
                            new SocketHelper.Builder(WebserviceUrls.BaseUrl + "?" + Constants.REQUEST_UNIQUE_ID + "=" + androidId, options)
                                    .addEvent(SocketConstants.EVENT_CONNECTED)
                                    .addListener((socketListener == null) ? null : socketListener)
                                    .build();
                    socketHelper.connect();
                }
            } else {
                //showNetworkDialog();
            }
            return socketHelper;
        } catch (Exception e) {
            fabricLog("create_Socket_Connection_base_fragment_exception",e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject sendTestData(String event, int testId, String result, String testName,
                                   String desc, String androidId, String testType) {
        JSONObject jsonObject = new JSONObject();
        if (!AutomatedTestFragment.isAutomatedTest) {

            try {
                String mQrCodeId =
                        Utilities.getInstance(getActivity()).getPreference(getActivity(),
                                Constants.QRCODEID, "");
                androidId = Utilities.getInstance(getActivity()).getPreference(getActivity(),
                        Constants.ANDROID_ID, "");

                jsonObject.put(Constants.QRCODEID, mQrCodeId);
                jsonObject.put(Constants.UNIQUE_ID, androidId);
                jsonObject.put("TestName", testName);
                jsonObject.put("TestStatus", result);
                jsonObject.put("TestDescription", desc);
                jsonObject.put("TestId", testId);
                jsonObject.put("TestType", testType);
            } catch (JSONException e) {
                fabricLog("base_fragment_send_test_data_exception",e.getMessage());
                e.printStackTrace();
            }
            if (socketHelper != null && (!testName.equalsIgnoreCase(Constants.VOLUME_BUTTONS) && !testName.equalsIgnoreCase(Constants.CAMERA) && testId != (Constants.SPEAKER_MIC))) {
                socketHelper.emitData(event, jsonObject);
            }

        }
        return jsonObject;
    }


    public void sendTestDataWithSubArray(String event, int testId, String result, String testName
            , String desc, String mQrCodeId, String androidId, String testType, JSONArray array) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = sendTestData(event, testId, result, testName, desc, androidId, testType);
            jsonObject.put("TestArray", array);
        } catch (JSONException e) {
            fabricLog("base_fragment_sendTestDataWithSubArray_exception",e.getMessage());
            e.printStackTrace();
        }
        if (socketHelper != null) {
            socketHelper.emitData(event, jsonObject);
        }
    }

    public void sendTestDataWihtlatlong(String event, int testId, String result, String testName,
                                        String desc, String androidId, String testType,
                                        double lattitude, double longitude) {
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject = sendTestData(event, testId, result, testName, desc, androidId, testType);

            jsonObject.put("latitude", lattitude);
            jsonObject.put("longitude", longitude);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (socketHelper != null) {
            socketHelper.emitData(event, jsonObject);
        }


    }












    protected final void timer(final Context ctx, boolean isStop, final int testid, final TimerDialogInterface timerDialogInterface, final InterfaceButtonTextChange...interfaceButtonTextChanges)
    {

        if(timerDialogInterface1 ==null) {
            this.timerDialogInterface1 = timerDialogInterface;
        }
        final TestController testController = TestController.getInstance(getActivity());


        if(!isStop) {
            if(timerHandler!=null){
                timerHandler.removeCallbacks(runnable);
            }
            timerHandler=null;

            timerHandler = new Handler();

            runnable=null;

            runnable = new Runnable() {
                @Override
                public void run() {
                    if(interfaceButtonTextChanges!= null && interfaceButtonTextChanges.length!=0 ) {
                        interfaceButtonTextChanges[0].onChangeText(0, true);
                    }



//	timerHandler.removeCallbacks(runnable);
                    alert = new AlertDialog.Builder(ctx);
                    alert.setTitle(R.string.txtManualDisplayAlert);
                    alert.setMessage(R.string.txtCount_test_Dialog);
                    alert.setCancelable(false);
                    alert.setPositiveButton(R.string.txtNo, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if(testid == ConstantTestIDs.CAMERA_ID || testid == ConstantTestIDs.SPEAKER_ID || testid == ConstantTestIDs.VOLUME_ID ||testid==ConstantTestIDs.GPS_ID){}else {
                                testController.onServiceResponse(false, "", testid);
                            }
                            timerDialogInterface1.timerFail();
                        }
                    });
                    alert.setNegativeButton(R.string.txtYes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            timerHandler.removeCallbacks(runnable);
                            timerHandler.postDelayed(runnable, 15000);
                            if(testid!=ConstantTestIDs.FLASH) {
                                testController.performOperation(testid);
                            }
                            if((frag instanceof AccelerometerTestFragment))
                            {
                                FragmentManager manager=getActivity().getSupportFragmentManager();
                                Fragment fragment=manager.findFragmentById(R.id.container);
                                AccelerometerTestFragment fragment1=(AccelerometerTestFragment)fragment;
                                fragment1.rigsterAccelerometer();
                            }
                            if((frag instanceof CompassTestFragment))
                            {
                                FragmentManager manager=getActivity().getSupportFragmentManager();
                                Fragment fragment=manager.findFragmentById(R.id.container);
                                CompassTestFragment fragment1=(CompassTestFragment)fragment;
                                testController.performOperation(testid,fragment1);
                            }





                        }
                    });

// dialog =	alert.show();
                    dialog = alert.create();
                    dialog.show();
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setAllCaps(false);
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setAllCaps(false);



                   try {


                    if(dialog.isShowing())
                    {
                        if(getActivity()!=null)
                        fm = getActivity().getSupportFragmentManager();
                        frag = fm.findFragmentById(R.id.container);
                        if((frag instanceof PhoneShakingManualFragment))
                        {
                            testController.unregisterGyroScope2();
                        }
                        if((frag instanceof HomeButtonManualFragment))
                        {
                            testController.unRegisterHome();

                        }
                        if((frag instanceof PowerButtonManualFragment))
                        {
                            testController.unRegisterPowerButton();

                        }
                        if((frag instanceof ProximitySensorManualFragment))
                        {
                            testController.unregisterProximity();
                        }
                        if((frag instanceof ChargingManualFragment))
                        {
                            testController.unRegisterCharging();
                        }
                        if((frag instanceof BatteryMannualFragment))
                        {
                            testController.unregisterBattery();
                        }
                        if((frag instanceof EarJackManualFragment))
                        {
                            testController.unRegisterEarJack();
                        }
                        if((frag instanceof CompassTestFragment))
                        {
                            testController.unregisterCompass();
                        }
                        if((frag instanceof FingerPrintTestFragment))
                        {
                            testController.unregisterFingerPrintSensor();
                        }
                        if((frag instanceof AccelerometerTestFragment))
                        {
                            FragmentManager manager=getActivity().getSupportFragmentManager();
                            Fragment fragment=manager.findFragmentById(R.id.container);
                            AccelerometerTestFragment fragment1=(AccelerometerTestFragment)fragment;
                            fragment1.unrigsterAccelerometer();
                        }
                    }
                   }catch (Exception e){
                       e.printStackTrace();
                   }
                }
            };


            timerHandler.removeCallbacks(null);
            timerHandler.postDelayed(runnable, 15000);

        }
        else{
            try {
                if (dialog != null) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
            if(timerHandler!=null) {
                timerHandler.removeCallbacks(runnable);
                timerHandler = null;
                runnable = null;
            }
        }


    }








    public void deviceRemoved() {
        if (isDeviceRemoved) {
            return;
        }
        isDeviceRemoved = true;
        try {
            Utilities.getInstance(getActivity()).showAlert(getActivity(), new Utilities.onAlertOkListener() {
                @Override
                public void onOkButtonClicked(String tag) {
                    try {
                        getActivity().finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, getResources().getString(R.string.device_removed), getResources().getString(R.string.Sync), "Ok", "", "Sync");
        } catch (Exception e) {
            e.printStackTrace();
            fabricLog("base_fragment_device_remove_exception",e.getMessage());
            isDeviceRemoved = false;
        }
    }
    public void fabricLog(String event,String error)
    {
        String cutString = error.substring(0, 98);
        try {
            Answers.getInstance().logCustom(new CustomEvent(event)
                    .putCustomAttribute("error", cutString));
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}






