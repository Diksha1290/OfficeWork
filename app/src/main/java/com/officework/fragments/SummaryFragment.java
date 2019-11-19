package com.officework.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.officework.R;
import com.officework.activities.MainActivity;
import com.officework.activities.WelcomeActivity;
import com.officework.adapters.SummaryAdapter;
import com.officework.base.BaseFragment;
import com.officework.constants.Constants;
import com.officework.constants.FragmentTag;
import com.officework.interfaces.SummaryAdapterCallback;
import com.officework.models.AutomatedTestListModel;
import com.officework.testing_profiles.Controller.TestController;
import com.officework.testing_profiles.realmDatabase.operationManager.RealmOperations;
import com.officework.testing_profiles.ui.fragment.AutomatedTestFragment;
import com.officework.utils.Utilities;
import com.officework.utils.socket.SocketConstants;
import com.officework.utils.socket.SocketHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.socket.client.IO;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SummaryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SummaryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SummaryFragment extends BaseFragment implements View.OnClickListener,
        SummaryAdapterCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String TEST_TYPE = "test_type";
    private static final String ARG_PARAM2 = "param2";
    JSONArray jsonArray = new JSONArray();
    ArrayList<AutomatedTestListModel> objects = new ArrayList<>();
    // TODO: Rename and change types of parameters
    private String test_type;
    private String mParam2;
    private RecyclerView summeryRV;
    private Button nextBT, retestBT;
    private TextView totaltestTV, passedtestTV, failedtestTV;
    private OnFragmentInteractionListener mListener;
    private RealmOperations realmOperations;
    //private MainActivity mainActivity;
    private SummaryAdapterCallback summaryAdapterCallback;
    private int failtestIndex;
    private int totalTestCount;
    private TextView mTvprivacy;
    private LinearLayout summaryLL;

    public SummaryFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static SummaryFragment newInstance(String param1) {
        SummaryFragment fragment = new SummaryFragment();
        Bundle args = new Bundle();
        args.putString(TEST_TYPE, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            test_type = getArguments().getString(TEST_TYPE);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.fragment_summary, container, false);
        summeryRV = (RecyclerView) view.findViewById(R.id.summeryRV);
        nextBT = (Button) view.findViewById(R.id.nextBT);
        retestBT = (Button) view.findViewById(R.id.retestBT);
        totaltestTV = (TextView) view.findViewById(R.id.totaltestTV);
        passedtestTV = (TextView) view.findViewById(R.id.passedtestTV);
        failedtestTV = (TextView) view.findViewById(R.id.failedtestTV);
        mTvprivacy = (TextView) view.findViewById(R.id.privacy);
        summaryLL=(LinearLayout)view.findViewById(R.id.summaryLL);
        nextBT.setOnClickListener(this);
        nextBT.setEnabled(true);
        retestBT.setOnClickListener(this);
        summaryLL.setOnClickListener(null);
        //mainActivity = (MainActivity) view.getContext();
        Log.d("test type", test_type);
        summaryAdapterCallback = (SummaryAdapterCallback) this;
        if (test_type.equals(Constants.MANUAL)) {
            setTitle(R.string.manual_summary);
            mTvprivacy.setText(getResources().getString(R.string.reperform_text_manual));
        } else {
            setTitle(R.string.auto_summary);
            mTvprivacy.setText(getResources().getString(R.string.reperform_text));

        }
        setSummaryData();
        return view;
    }


    public void setTitle(Integer i) {
        try {
            TitleBarFragment fragment = (TitleBarFragment) getFragment(R.id.headerContainer);
            if (fragment != null) {
                fragment.setTitleBarVisibility(true);
                fragment.showSwitchLayout(false);
                fragment.setSyntextVisibilty(false);
                fragment.setHeaderTitleAndSideIcon(getResources().getString(i), true, true, 0);
//                if (i == R.string.txtAutomated) {
//                    fragment.setSyntextVisibilty(false);
//                } else {
//                    fragment.setSyntextVisibilty(true);
//                }
                //  mCallBack.onChangeText(utils.BUTTON_SKIP, false);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void setSummaryData() {
        realmOperations = new RealmOperations();
        int failedTest;
        if (test_type.equals(Constants.MANUAL)) {

            unregisterSensors();

            totalTestCount = realmOperations.fetchTestsList(MainActivity.id_array,
                    Constants.MANUAL, getActivity()).size();
            totalTestCount += realmOperations.fetchTestsList(MainActivity.id_array,
                    Constants.MANUAL1, getActivity()).size();

            objects = realmOperations.fetchManualFailTestsList(MainActivity.id_array
                    , getActivity());

//            objects.addAll(realmOperations.fetchFailTestsList(MainActivity.id_array,
//                    Constants.MANUAL1, getActivity()));

            ArrayList<AutomatedTestListModel> listModels =
                    realmOperations.fetchpassTestswithTestTypeListAllList(MainActivity.id_array,
                            Constants.MANUAL, getActivity());
            failedTest = listModels.size();

            listModels
                    = realmOperations.fetchpassTestswithTestTypeListAllList(MainActivity.id_array,
                    Constants.MANUAL1, getActivity());
            failedTest += listModels.size();

        } else {
            totalTestCount = realmOperations.fetchTestsList(MainActivity.id_array,
                    test_type, getActivity()).size();

            objects = realmOperations.fetchFailTestsList(MainActivity.id_array,
                    test_type, getActivity());
            failedTest =
                    realmOperations.fetchpassTestswithTestTypeListAllList(MainActivity.id_array,
                            Constants.AUTOMATE, getActivity()).size();
            realmOperations.fetchpassTestswithTestTypeListAllList(MainActivity.id_array,
                    Constants.MANUAL1, getActivity()).size();
//            totaltestTV.setText(objects.size() + "");
//            failedtestTV.setText(failedTest + "");
//            passedtestTV.setText((objects.size() - failedTest) + "");
        }
        totaltestTV.setText(totalTestCount + "");
        failedtestTV.setText((totalTestCount - failedTest) + "");
        passedtestTV.setText(failedTest + "");


        if ((totalTestCount - failedTest) == 0) {
            retestBT.setVisibility(View.GONE);
            mTvprivacy.setText(getResources().getString(R.string.tapnext));
        }
        for (int index = 0; index < objects.size(); index++) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("TestStatus", objects.get(index).getIsTestSuccess());
                jsonObject.put("TestName", objects.get(index).getName());
                jsonArray.put(jsonObject);

//                if(test_type.equals(Constants.MANUAL) && objects.get(index).getTest_type()  ==
//                Constants.MANUAL1) {
//
//                    if (objects.get(index).getIsTestSuccess() == 1)
//                        MainActivity.testListSemi.put(index, true);
//                    else {
//                        MainActivity.testListSemi.put(index, false);
//                        activity.skipMapSemi.add(index);
//                    }
//
//                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        SummaryAdapter adapter = new SummaryAdapter(getActivity(), objects,
                (summaryAdapterCallback), test_type);

        switch (test_type) {
            case Constants.AUTOMATE:
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                summeryRV.setLayoutManager(mLayoutManager);

                break;
            default:

                summeryRV.setLayoutManager(new GridLayoutManager(getActivity(), 2));

                break;
        }

        summeryRV.setItemAnimator(new DefaultItemAnimator());
        summeryRV.setAdapter(adapter);
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    public void onBackPress() {

        if (test_type.equals(Constants.MANUAL) || test_type.equals(Constants.AUTOMATE)) {

            String androidId = Utilities.getInstance(getActivity()).getPreference(getActivity(),
                    Constants.ANDROID_ID, "");

            Utilities.getInstance(getActivity()).showAlert(getActivity(),
                    new Utilities.onAlertOkListener() {
                        @Override
                        public void onOkButtonClicked(String tag) {
                            if (Utilities.getInstance(getActivity()).isInternetWorking(getActivity())) {
                                IO.Options options = new IO.Options();
                                SocketHelper socketHelper =
                                        new SocketHelper.Builder(SocketConstants.HOST_NAME1 + "?" + Constants.REQUEST_UNIQUE_ID + "=" + androidId, options)
                                                .addEvent(SocketConstants.EVENT_CONNECTED)
                                                .addListener(null)
                                                .build();


                                if (socketHelper != null) {
                                    socketHelper.destroy();
                                }
                            }
                            Intent intent = new Intent(getActivity(), WelcomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
//                            getActivity().setResult(Activity.RESULT_OK);
//                            getActivity().finish();

                        }
                    }, Html.fromHtml(getResources().getString(R.string.rescan_msg)),
                    getResources().getString(R.string.rescan_head), "No", "Yes", "Sync");
        } else {

            getActivity().getSupportFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.container)).commit();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nextBT:
                nextBT.setEnabled(false);
                try {
                    if (test_type.equals(Constants.AUTOMATE)) {
                        AutomatedTestFragment.getInstance().retestPerform2();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

//                if(test_type.equals(Constants.MANUAL)){
//                    setShowSyncTitle(true);
//                }
//                mainActivity.onSummaryButtonClick(true, test_type);

                // if(status){

//                replaceFragment(R.id.container, ManualTestsOperation.launchScreens
//                (SemiAutomaticTestsFragment.testListModelList.get(0)
//                        , Utilities.getInstance(getActivity()).getPreference(getActivity(),
//                        Constants.ANDROID_ID, ""),getActivity()),
//                        SemiAutomaticTestsFragment.testListModelList.get(0).getName(), true);
                if (!test_type.equals(Constants.MANUAL)) {
                    getActivity().getSupportFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.container)).commit();
                }

                ManualTestFragment fragment =
                        (ManualTestFragment) getActivity().getSupportFragmentManager().findFragmentByTag(String.valueOf(FragmentTag.MANUAL_TEST_FRAGMENT));
                fragment.moveNext(test_type);

                break;

            case R.id.retestBT:

//                if(test_type.equals(Constants.MANUAL)){
//                    setShowSyncTitle(false
//                    );
//                }
                Constants.isDoAllClicked = true;

                ManualTestFragment fragment1 =
                        (ManualTestFragment) getActivity().getSupportFragmentManager().findFragmentByTag(String.valueOf(FragmentTag.MANUAL_TEST_FRAGMENT));


                if (test_type.equals(Constants.AUTOMATE)) {
                    fragment1.retestPerform(test_type);
                    // mainActivity.onSummaryButtonClick(false, test_type);
                    getActivity().getSupportFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.container)).commit();

                } else {
                    final MainActivity activity = (MainActivity) getActivity();
                    activity.isManualRetest = true;
                    activity.semiIndex = 0;
                    activity.isAllSkipDone = false;

                    Set<Integer> keys = MainActivity.testListSemi.keySet();

                    if (keys.size() > 0) {
                        for (Integer key : keys) {
                            if (activity.skipMapSemi.contains(key))
                                continue;

                            activity.semiIndex = key + 1;
                            fragment1.openEditTest(ManualTestFragment.testListModelList.get(key));
                            return;
                        }
                    }
//                    if(activity.semiIndex == 0){
//                        Set<Integer> keys1  = MainActivity.testListManual.keySet();
//
//                        if(keys1.size()>0) {
//                            for (Integer key : keys1) {
//                                if(activity.skipMapManual.contains(key))
//                                    continue;
//
//                                activity.manualIndex = key+1;
//                                fragment1.openEditTest(Manual2SemiAutomaticTestsFragment
//                                .testListModelList.get(key));
//                                return;
//                            }
//                        }
//
//                    }


                    // mainActivity.editTest(automatedTestListModel);
                }
                // mainActivity.onSummaryButtonClick(false, test_type);
//                ManualTestFragment manualTestFragment = (ManualTestFragment) getActivity()
//                .getSupportFragmentManager().findFragmentByTag(String.valueOf(FragmentTag
//                .MANUAL_TEST_FRAGMENT));
//                manualTestFragment.retestPerform(test_type);
//                getActivity().getSupportFragmentManager().beginTransaction().remove
//                (getFragmentManager().findFragmentById(R.id.container)).commit();

                //     summaryCallback.onSummaryButtonClick(false);
                break;
        }
    }

    public void setShowSyncTitle(boolean shouldShowSync) {
        try {
            TitleBarFragment fragment = (TitleBarFragment) getFragment(R.id.headerContainer);
            if (fragment != null) {
                fragment.setTitleBarVisibility(true);
                fragment.setSyntextVisibilty(true);
                if (fragment.switchDoAll.isEnabled()) {
                    Constants.isDoAllClicked = true;
                } else {
                    Constants.isDoAllClicked = false;
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void editClick(AutomatedTestListModel automatedTestListModel) {
        Constants.isDoAllClicked = false;
        ManualTestFragment fragment =
                (ManualTestFragment) getActivity().getSupportFragmentManager().findFragmentByTag(String.valueOf(FragmentTag.MANUAL_TEST_FRAGMENT));


        final MainActivity activity = (MainActivity) getActivity();


        if (test_type.equals(Constants.AUTOMATE)) {
            fragment.retestPerform(test_type);
            // mainActivity.onSummaryButtonClick(false, test_type);
            getActivity().getSupportFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.container)).commit();

        } else {
            activity.isManualRetest = true;
            activity.semiIndex = checkIndexOfTestId(ManualTestFragment.testListModelList,
                    automatedTestListModel) + 1;

//            switch (automatedTestListModel.getTest_type()){
//                case Constants.MANUAL1:
//                    activity.semiIndex = checkIndexOfTestId(SemiAutomaticTestsFragment
//                    .testListModelList,automatedTestListModel)+1;
//                    break;
//                case Constants.MANUAL:
//                    activity.manualIndex = checkIndexOfTestId(Manual2SemiAutomaticTestsFragment
//                    .testListModelList,automatedTestListModel)+1;
//
//                    break;
//            }
            fragment.openEditTest(automatedTestListModel);
            // mainActivity.editTest(automatedTestListModel);
        }
        Log.d("click", automatedTestListModel.getName() + "");
    }

    private int checkIndexOfTestId(List<AutomatedTestListModel> testListModelList,
                                   AutomatedTestListModel automatedTestListModel) {
        final MainActivity activity = (MainActivity) getActivity();

        int value = 1;

        for (int index = 0; index < testListModelList.size(); index++) {
            if (testListModelList.get(index).getTest_id() == automatedTestListModel.getTest_id()) {
                value = index;
                break;
            }
        }
        return value;
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    private void unregisterSensors() {
        try {
            TestController testController = TestController.getInstance(getActivity());

            testController.unregisterGyroScope2();
            testController.unRegisterCharging();
            testController.unregisterBattery();
            testController.unregisterProximity();
            testController.unRegisterHome();
            testController.unRegisterEarJack();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
