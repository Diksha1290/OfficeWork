package com.officework.testing_profiles.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.officework.R;
import com.officework.adapters.DeclarationAdapter;
import com.officework.base.BaseFragment;
import com.officework.constants.FragmentTag;
import com.officework.fragments.PaymentDetailFragment;
import com.officework.fragments.TitleBarFragment;
import com.officework.interfaces.DeclerationInterface;
import com.officework.models.DataPojo;
import com.officework.models.DeclarationTest;
import com.officework.testing_profiles.Model.DeclarationScreenModel;
import com.officework.utils.Utilities;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.officework.testing_profiles.ui.fragment.CustomerInformationFragment.hideKeyboard;

public class DeclarationFragment extends BaseFragment implements DeclerationInterface {

    Unbinder unbinder;

    @BindView(R.id.rec_main)
    RecyclerView recyclerView;
    @BindView(R.id.checkbox)
    AppCompatCheckBox checkbox;
    private DeclarationAdapter declarationAdapter;
    private ArrayList<DeclarationScreenModel> list;
    private String[] Heading;
    private int[] iconImage;
    private String[] description;
    private String[] heading;
    DataPojo dataPojo;
    private int[]checkImage;
    ArrayList<DeclarationTest> dfslist=new ArrayList<>();
    ArrayList<DeclarationTest> declarationTestArrayList=new ArrayList<>();
    @BindView(R.id.btn_nexta)
    Button proceed;
    @BindView(R.id.btn_previousa)
    Button cancel;
    @BindView(R.id.progressBarMediuma)
    ProgressBar progressBarMedium;
    DeclarationTest declarationTest;
    boolean done=true;
    int mCurrentSelectedPosition = 0;
    public String email;
    public String statecode;
    public String confirm_email;
    public String first_name;
    public String last_name;
    public String contact_number;
    public String post_code;
    public String adres1;
    public String adres2;
    public String adres3;
    public String city;
    public String id_type;
    public String id_value;
    public String filepath;
    public String offer_price;
    private String order_id;
    private String udi_id;
    private int customer_id;
    ArrayList<String> imagesList;
    boolean firtTime;
    int IdentityTypeID;
    View view;
    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        if(view==null) {
            view = inflater.inflate(R.layout.fragment_declaration, container, false);
            unbinder = ButterKnife.bind(this, view);
            if (getArguments() != null) {
                dataPojo = getArguments().getParcelable("OFFER_PRICE_OBJECT");
                dfslist = dataPojo.getDeclarationTestArrayList();
                //declarationTestArrayList = new ArrayList<>(dfslist);
                getFullBundel();
            }
            if (getActivity() != null)
                hideKeyboard(getActivity());
////        Heading = new String[]{getString(R.string.txt_memory_heading), getString(R.string.txt_sim_heading), getString(R.string.txt_factory_reset)};
//        iconImage = new int[]{R.drawable.img_5, R.drawable.img_6,
//                R.drawable.img_7};
//        heading = new String[]{};
//        description = new String[]{};
//       /* heading = new String[]{getString(R.string.txt_memory_heading), getString(R.string.txt_sim_heading), getString(R.string.txt_factory_reset)};
//        description = new String[]{getString(R.string.txt_memory_card), getString(R.string.txt_sim_card), getString(R.string.txt_factory_data)};*/
//
//        checkImage = new int[]{R.drawable.ic_icon_check_grey, R.drawable.ic_icon_check_grey,
//                R.drawable.ic_icon_check_grey};
//
//        list = new ArrayList<>();
//        for (int i = 0; i < iconImage.length; i++) {
//            DeclarationScreenModel object = new DeclarationScreenModel();
//            object.setImg(iconImage[i]);
////            object.setName(getResources().getStringArray(R.array.declareHeading)[i]);
//
////            object.setDescription(getResources().getStringArray(R.array.declareDescription)[i]);
//            object.setCheckImage(checkImage[i]);
//            list.add(object);
//        }
            try {
                for (int index = 0; index < dfslist.size(); index++) {
                    try {
                        declarationTestArrayList.add(dfslist.get(index).clone());
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                }
                declarationAdapter = new DeclarationAdapter(declarationTestArrayList, getActivity(), DeclarationFragment.this);
                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView.setAdapter(declarationAdapter);
                declarationAdapter.notifyDataSetChanged();

            } catch (Exception e) {
                e.printStackTrace();
            }


            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b && !firtTime) {
                        proceed.setBackground(getResources().getDrawable(R.drawable.green_bg));
                    } else {
                        proceed.setBackground(getResources().getDrawable(R.drawable.blue_bg));

                    }
                }
            });
            return view;

        }
        return view;
    }

    private void getFullBundel() {
        email=getArguments().getString("email");
        confirm_email=getArguments().getString("confirm_email");
        first_name =getArguments().getString("first_name");
        last_name  =getArguments().getString("last_name");
        contact_number =getArguments().getString("contact_number");
        statecode=getArguments().getString("state_code");
        post_code  =getArguments().getString("post_code");
        adres1   =getArguments().getString("adres1");
        adres2  =getArguments().getString("adres2");
        adres3    =getArguments().getString("adres3");
        city   =getArguments().getString("city");
        id_type =getArguments().getString("id_type");
//        id_value =getArguments().getString("id_value");
//        filepath=getArguments().getString("filepath");
        offer_price=getArguments().getString("offer_price");
        order_id=getArguments().getString("order_id");
        udi_id=getArguments().getString("udi_id");
        customer_id = getArguments().getInt("customer_id");
        imagesList=getArguments().getStringArrayList("image_list");
        IdentityTypeID=getArguments().getInt("IdentityTypeID");
    }

    public void onBackPress() {

        if(done) {
            try {
                if (getActivity() != null) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    Fragment frag = fm.findFragmentById(R.id.container);
                    if (frag instanceof DeclarationFragment) {
                        popFragment(R.id.container);
                    }
                }
            } catch (Exception e) {
                if (getActivity() != null)
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                if (progressBarMedium.isShown()) {
                    progressBarMedium.setVisibility(View.GONE);
                }
                Toast.makeText(getActivity(), R.string.somethingwentwrong, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }

    }











    public void submitInfo() {

//        if (!Utilities.EmailValidation(email, getString(R.string.error_valid_mail))||!Utilities.EmailValidation(txtConfirmEmail, getString(R.string.error_confirm_mail)) ||!Utilities.confirm(txtConfirmEmail,getString(R.string.error_confirm),mail,confirm) ||!Utilities.validation(txtFirstName, getString(R.string.error_first_name))||!Utilities.validation(txtLastName, getString(R.string.error_last_name))  || !Utilities.validation(txtContactNo, getString(R.string.error_contact))|| !Utilities.validContact(txtContactNo, getString(R.string.error_valid_contact))|| !Utilities.validation(txtPostCode, getString(R.string.error_post))||!Utilities.validation(txtAddressOne, getString(R.string.error_address_one))||!Utilities.validation(txtCity, getString(R.string.error_city))|| !Utilities.validationSpinner(spinnerText.getText().toString(), getString(R.string.selectid))    ||!Utilities.validation(idValue, getString(R.string.identityValue))||!Utilities.validationofImage(filePath1, getString(R.string.error_image))) {
//
//        }else {


        try {
//                txtConfirmEmail.setEnabled(false);
//                email.setEnabled(false);
            dataPojo = getArguments().getParcelable("OFFER_PRICE_OBJECT");
            Fragment paymentDetailFragment = new PaymentDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("OFFER_PRICE_OBJECT", dataPojo);
            bundle.putString("email", email);
            bundle.putString("confirm_email",confirm_email);
            bundle.putString("first_name", first_name);
            bundle.putString("last_name", last_name);
            bundle.putString("contact_number", contact_number);
            bundle.putString("post_code", post_code);
            bundle.putString("state_code", statecode);
            bundle.putString("adres1", adres1);
            bundle.putString("adres2", adres2);
            bundle.putString("adres3", adres3);
            bundle.putString("city",city);
            bundle.putString("id_type", id_type);
//                bundle.putString("id_value",idValue.getText().toString().trim());
            // bundle.putString("filepath",filePath1);
            bundle.putString("offer_price", offer_price);
            bundle.putString("order_id", order_id);
            bundle.putString("udi_id", udi_id);
            bundle.putInt("customer_id", customer_id);
            bundle.putStringArrayList("image_list", imagesList);
            bundle.putInt("IdentityTypeID", IdentityTypeID);
            bundle.putParcelableArrayList("Declarations",declarationTestArrayList);

            paymentDetailFragment.setArguments(bundle);
         //   clearBackStack();
            replaceFragment(R.id.container, paymentDetailFragment,
                    FragmentTag.MANUAL_TEST_FRAGMENT.name(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }

//            try {
//                progressBarMedium.setVisibility(View.VISIBLE);
//                if(getActivity()!=null)
//                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                done=false;
//                submitResult();
//
//            } catch (Exception e) {
//                done=true;
//                if(getActivity()!=null)
//                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                if(progressBarMedium.isShown())
//                {
//                    progressBarMedium.setVisibility(View.GONE);
//                }
//                Toast.makeText(getActivity(),R.string.somethingwentwrong,Toast.LENGTH_LONG).show();
//                e.printStackTrace();
//            }
        // }
    }









//    public void submitResult() {
//
//        MultipartBody.Part[] imagesToUpload = new MultipartBody.Part[imagesList.size()];
//        RequestBody filename=null;
//
//
//
//        for (int index = 0; index < imagesList.size(); index++) {
//
//            try {
//                Log.d("NNNN", "requestUploadSurvey: survey image " + index + " " + imagesList.get(index));
//// File file = new File(imagesList.get(index));
//                File file = FileUtilss.getCompressed(getActivity(),imagesList.get(index),index+"_");
//                RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
//                imagesToUpload[index] = MultipartBody.Part.createFormData("image", file.getName(), requestBody);
//                filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//
//
//
//        APIInterface apiInterface =
//                APIClient.getClient(getActivity()).create(APIInterface.class);
//
//// HashMap<String, Object> map = createMap();
//// Call<SocketDataSync> call1 = apiInterface.submitInfo(map,fileToUpload, filename);
//
//        Utilities utils=Utilities.getInstance(getActivity());
//        WebService webService=new WebService();
//        HashMap<String,ArrayList<DeclarationTest>> map1 = new HashMap<>();
//        map1.put("Declarations",declarationTestArrayList);
//        webService.apiCall(apiInterface.submitInfo(Utilities.getInstance(getActivity()).getPreference(getActivity(),
//                Constants.QRCODEID, ""),
//                Utilities.getInstance(getActivity()).getPreference(getActivity(),
//                        Constants.ANDROID_ID, ""), order_id, udi_id, first_name, "", last_name
//                , customer_id, email, contact_number, adres1, dataPojo.getPercentageDeduction(), adres2, adres3,
//                post_code, city, IdentityTypeID, dataPojo.getMcheckInformationID(),
//                dataPojo.getQuotedPrice(), utils.getSecurePreference(getActivity(), "MMR_3", ""), dataPojo.getIMEI(), dataPojo.getFailTestId(), dataPojo.getPassTestId(),
//                dataPojo.getUserID(), dataPojo.getStoreID(), dataPojo.getEnterprisePartnerID(), dataPojo.getSKUID(), dataPojo.getCatalogPartnerPricingID(), dataPojo.getLocationID(),
//                imagesToUpload, utils.getSecurePreference(getActivity(), "MMR_4", ""), utils.getSecurePreference(getActivity(), "MMR_5", ""), utils.getSecurePreference(getActivity(), "MMR_6", ""), Utilities.getInstance(getActivity()).getPreference(getActivity(), Constants.DEVICEPRICE, ""),Utilities.getInstance(getActivity()).getPreference(getActivity(), Constants.TradeInValidityDays,"") ,Utilities.getInstance(getActivity()).getPreference(getActivity(), Constants.QuoteExpireDays,""),map1,Utilities.getInstance(getActivity()).getPreference(getActivity(), Constants.StoreType,""))
//                , getActivity() ,new WebServiceInterface<Response<SocketDataSync>>() {
//                    @Override
//                    public void apiResponse(Response<SocketDataSync> response) {
//                        try {
//                            progressBarMedium.setVisibility(View.INVISIBLE);
//                            if (response.isSuccessful()) {
//
//                                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                                SocketDataSync socketDataSync = response.body();
//                                if (socketDataSync != null) {
//                                    if (socketDataSync.isSuccess()) {
//                                        Toast.makeText(getContext(),R.string.sucess,Toast.LENGTH_LONG).show();
//
//                                        DataPojo dataPojo = socketDataSync.getData();
//                                        String order_id = dataPojo.getOrderDetailID();
//
//// OrderInformationFragment orderInformationFragment =
//// new OrderInformationFragment();
//
//                                        RecieptInformationScreenFragment recieptInformationScreenFragment=new RecieptInformationScreenFragment();
//                                        Bundle bundle = new Bundle();
//
//                                        bundle.putString("OFFER_PRICE", offer_price);
//                                        bundle.putString("ORDER_ID", order_id);
//
//                                        bundle.putString("CUSTOMER_ADDRESS1",adres1);
//                                        bundle.putString("CUSTOMER_ADDRESS2",adres2);
//                                        bundle.putString("CUSTOMER_ADDRESS3", adres3);
//
//// bundle.putString("POSTAL_CODE", txtPostCode.getText().toString());
//                                        bundle.putString("CUSTOMER_CITY", city);
//
//                                        bundle.putString("ADDRESS1", dataPojo.getAddress1());
//                                        bundle.putString("ADDRESS2", dataPojo.getAddress2());
//                                        bundle.putString("ADDRESS3", dataPojo.getAddress3());
//                                        bundle.putString("POSTAL_CODE", dataPojo.getPostCode());
//                                        bundle.putString("CITY", dataPojo.getCity());
//                                        bundle.putString("CREATED_DATE",dataPojo.getCreatedDate());
//                                        recieptInformationScreenFragment.setArguments(bundle);
//                                        replaceFragment(R.id.container, recieptInformationScreenFragment,
//                                                FragmentTag.MANUAL_TEST_FRAGMENT.name(), false);
//                                        TitleBarFragment fragment =
//                                                (TitleBarFragment) getFragment(R.id.headerContainer);
//                                        if (fragment != null) {
//                                            fragment.setTitleBarVisibility(true);
//                                            fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string.declaration), true, false, 0);
//                                        }
//                                    } else {
//                                        Toast.makeText(getContext(),socketDataSync.getMessage(),Toast.LENGTH_LONG).show();
//                                        done=true;
//                                        if (getActivity() != null)
//                                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                                        fabricLog("CustomerInfo_Frag_Submit_Customer_info_api_error",response.toString());
//                                    }
//                                }
//
//                            }
//
//
//                        } catch (Exception e) {
//                            if (getActivity() != null)
//                                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                            done=true;
//                            fabricLog("CustomerInfo_Frag_Submit_Customer_info_api_exception", e.getMessage());
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void apiError(Response<SocketDataSync> response) {
//                        try {
//                            progressBarMedium.setVisibility(View.GONE);
//                            SocketDataSync socketDataSync = response.body();
//                            done = true;
//                            Toast.makeText(getContext(), socketDataSync.getMessage(), Toast.LENGTH_LONG).show();
//                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//
//                        }catch (Exception e){
//                            e.printStackTrace();
//                        }
//
//                    }
//
//                    @Override
//                    public void serverError(Throwable t) {
//                        try {
//
//                            done=true;
//                            if( t instanceof UnknownHostException)
//                            {
//                                Toast.makeText(getActivity(),"There is something went wrong with your Internet Connection Please reset your Internet Connection and try again",Toast.LENGTH_LONG).show();
//                            }
//                            progressBarMedium.setVisibility(View.GONE);
//                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                            Toast.makeText(getContext(),t.getMessage(),Toast.LENGTH_LONG).show();
//                            fabricLog("CustomerInfo_Frag_Submit_Customer_info_api_exception",t.getMessage());
//                            Log.d("error", t.toString());
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                });
//
//
//    }














    @OnClick({R.id.btn_previousa, R.id.btn_nexta})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_previousa:
                try {
                    if(getActivity()!=null){
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        Fragment frag = fm.findFragmentById(R.id.container);
                        if (frag instanceof DeclarationFragment) {
                            //popFragment(R.id.container);
                            //  declarationTest.setDeclarationValue(false);
                            fm.popBackStackImmediate();
                        }
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }

                break;
            case R.id.btn_nexta:

                if(firtTime) {
                    Toast.makeText(getActivity(), "Please check all the mandatory checkboxes",
                            Toast.LENGTH_SHORT).show();
                } else if(!checkbox.isChecked() && !firtTime)
                {
                    Toast.makeText(getActivity(), "Please click the confirmation checkbox.",
                            Toast.LENGTH_SHORT).show();
                }

                if (!firtTime && checkbox.isChecked()) {
                    //hit Api
                    hideKeyboard(getActivity());

                    if (Utilities.getInstance(getActivity()).isInternetWorking(getActivity())) {
                        submitInfo();
                    } else {
                        Toast.makeText(getActivity(),
                                getResources().getString(R.string.please_check_internet_connection),
                                Toast.LENGTH_SHORT).show();
                    }
                }
//                 else {
//                    Toast.makeText(getActivity(), "Please Confirm information",
//                            Toast.LENGTH_SHORT).show();
//                }
                break;

        }
    }



    @Override
    public void onResume() {
        super.onResume();
        TitleBarFragment fragment = (TitleBarFragment) getFragment(R.id.headerContainer);
        if (fragment != null) {
            fragment.setTitleBarVisibility(true);
            fragment.showSwitchLayout(false);
            fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string.declaration), true, false, 0);
        }
    }

    @Override
    public void decleration_proceess_complete(boolean value) {

        if(value) {
            firtTime=false;
            if(checkbox.isChecked())
            {
                proceed.setBackground(getResources().getDrawable(R.drawable.green_bg));
            }else {
                proceed.setBackground(getResources().getDrawable(R.drawable.blue_bg));
            }
        }else {
            firtTime=true;
            proceed.setBackground(getResources().getDrawable(R.drawable.blue_bg));
        }
    }
}
