package com.officework.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.officework.ApiController.APIClient;
import com.officework.ApiController.APIInterface;
import com.officework.ApiController.WebService;
import com.officework.ApiController.WebServiceInterface;
import com.officework.R;
import com.officework.activities.CheckingDeviceActivity;
import com.officework.activities.LandingActivity;
import com.officework.base.BaseFragment;
import com.officework.constants.JsonTags;
import com.officework.models.SocketDataSync;
import com.officework.utils.Utilities;

import java.net.UnknownHostException;
import java.util.HashMap;

import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link VerifyTradableManualFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VerifyTradableManualFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VerifyTradableManualFragment extends BaseFragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView nextTV;
            CheckBox checkbox;
            ProgressBar progressDialog;
    private OnFragmentInteractionListener mListener;

    public VerifyTradableManualFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VerifyTradableManualFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VerifyTradableManualFragment newInstance(String param1, String param2) {
        VerifyTradableManualFragment fragment = new VerifyTradableManualFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



        if(getActivity() instanceof CheckingDeviceActivity) {
            CheckingDeviceActivity checkingDeviceActivity = (CheckingDeviceActivity) getActivity();
            if (checkingDeviceActivity != null)
                checkingDeviceActivity.setToolbarTitle(getResources().getString(R.string.app_name));
                checkingDeviceActivity.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
             //   checkingDeviceActivity.mDrawerToggle.setHomeAsUpIndicator(null);

        }
        else if(getActivity() instanceof LandingActivity){
            LandingActivity landingActivity=(LandingActivity)getActivity();
            if(landingActivity!=null){
                landingActivity.title.setText(getResources().getString(R.string.app_name));
                landingActivity.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                landingActivity.mDrawerToggle.setHomeAsUpIndicator(null);
            }
        }


    }







    public void onBackPress() {

        Utilities.getInstance(getActivity()).showAlert(getActivity(), new Utilities.onAlertOkListener() {
            @Override
            public void onOkButtonClicked(String tag) {
                try {
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Utilities.onAlertCancelListener() {
            @Override
            public void onOkButtonClicked1(String tag) {

            }
        }, Html.fromHtml(getResources().getString(R.string.rescan_msgg)), getResources().getString(R.string.rescan_head), "No", "Yes", "Sync");
    }

    public void addToTradeble(String email) {
        try {
            WebService webService=new WebService();
            APIInterface apiInterface =
                    APIClient.getClient(getActivity()).create(APIInterface.class);
            HashMap<String,String> map = new HashMap<>();
            map.put("UDI",Utilities.getInstance(getActivity()).getPreference(getActivity(), JsonTags.UDI.name(), ""));
            map.put("EmailID",email);
            map.put("DeviceMake", Build.MANUFACTURER);
//            map.put("DeviceModel",Utilities.getInstance(getActivity()).getSecurePreference(getActivity(), "MMR_4", Build.MANUFACTURER) + Utilities.getInstance(getActivity()).getSecurePreference(getActivity(), "MMR_5", Build.MODEL)+
//                    Utilities.getInstance(getActivity()).getSecurePreference(getActivity(), "MMR_6", HardwareInfoFragment.getTotalInternalMemorySize()));
            map.put("DeviceModel",Utilities.getInstance(getActivity()).getSecurePreference(getActivity(), "MMR_5", Build.MODEL));
            map.put("DeviceCapacity", HardwareInfoFragment.getTotalInternalMemorySize());
            webService.apiCall(apiInterface.addToTradeble(map),getActivity(), new WebServiceInterface<Response<SocketDataSync>>() {
                @Override
                public void apiResponse(Response<SocketDataSync> response) {

                    Log.d("verify response", response.message());
                    if (response.body().isSuccess()) {
                        if(progressDialog.isShown())
                        {
                            progressDialog.setVisibility(View.GONE);
                        }
                        Utilities.getInstance(getActivity()).showToast(getActivity(), "Mail Sent");
                        Handler handler=new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(getActivity()!=null)
                                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                if(getActivity() instanceof CheckingDeviceActivity) {
                                    CheckingDeviceActivity checkingDeviceActivity = (CheckingDeviceActivity) getActivity();
                                    if (checkingDeviceActivity != null)
                                        checkingDeviceActivity.setToolbarTitle2(getResources().getString(R.string.txtDASHBOARD));
                                    Fragment f = new LandingFragment();
                                    replaceFragment(R.id.frame,f,"Landing",false);
                                }

//                                    checkingDeviceActivity.setToolbarTitle(getResources().getString(R.string.txtDASHBOARD));

                            }
                        },1600);

                    } else {
                        if(getActivity()!=null)
                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        if(progressDialog.isShown())
                        {
                            progressDialog.setVisibility(View.GONE);
                        }
                        Toast.makeText(getActivity(),getResources().getText(R.string.api_error_toast),Toast.LENGTH_LONG).show();

                    }
                }

                @Override
                public void apiError(Response<SocketDataSync> response) {
                    if(getActivity()!=null)
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    if(progressDialog.isShown())
                    {
                        progressDialog.setVisibility(View.GONE);
                    }
                    Toast.makeText(getActivity(),getResources().getText(R.string.api_error_toast),Toast.LENGTH_LONG).show();
                }

                @Override
                public void serverError(Throwable t) {
                    if(getActivity()!=null)
                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    if(progressDialog.isShown())
                    {
                        progressDialog.setVisibility(View.GONE);                    }
                    Toast.makeText(getActivity(),getResources().getText(R.string.server_error_toast),Toast.LENGTH_LONG).show();
                    if( t instanceof UnknownHostException)
                    {
                        Toast.makeText(getActivity(),getResources().getText(R.string.internet_error_toast),Toast.LENGTH_LONG).show();                    }

                }
            });




        } catch (Exception e) {
            if(getActivity()!=null)
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            if(progressDialog.isShown())
            {
                progressDialog.setVisibility(View.GONE);
            }
        }


    }



    @Override
    public void onResume() {
        super.onResume();


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_verify_tradable_manual, container, false);
        checkbox=view.findViewById(R.id.checkbox_verify);
        progressDialog=view.findViewById(R.id.progressBarMediumaa);
        checkbox.setOnClickListener(this);
        return view;
    }

    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        return null;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
           case R.id.checkbox_verify:
if(checkbox.isChecked())
{
    showEmailDialog();

}
           break;
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



    public void showEmailDialog(){
        Dialog dialog=new Dialog(getActivity());
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.password_dialog_layout);
        EditText pass=dialog.findViewById(R.id.passedtx);
        Button cancel=dialog.findViewById(R.id.canclDb);
        TextView txt=dialog.findViewById(R.id.dialogtxt);
            txt.setText(getResources().getString(R.string.emaill));
            pass.setHint("Email*");
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkbox.setChecked(false);
                dialog.cancel();

            }
        });
        Button enter=dialog.findViewById(R.id.entrpass);
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Utilities.validation(pass, getString(R.string.error_mail))&& Utilities.EmailValidation( pass, getString(R.string.error_valid_mail))){
                    progressDialog.setVisibility(View.VISIBLE);
                    if(getActivity()!=null) {
                        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                    addToTradeble(pass.getText().toString());
                    checkbox.setChecked(false);
                    dialog.dismiss();
                }

            }
        });
        dialog.show();

    }
}
