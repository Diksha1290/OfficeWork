package com.officework.testing_profiles.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.officework.ApiController.WebService;
import com.officework.ApiController.WebServiceInterface;
import com.officework.activities.WelcomeActivity;
import com.officework.adapters.SpinnerAdapter;

import com.officework.customViews.FloatLabeledEditText;
import com.officework.customViews.TouchImageView;
import com.officework.fragments.TitleBarFragment;
import com.officework.models.DataPojo;
import com.officework.models.DocumentsTypePojo;
import com.officework.utils.CameraImagePickerHelper;
import com.officework.utils.EmailValidator;
import com.officework.utils.FileUtilss;
import com.officework.utils.NoMenuEditText;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.officework.ApiController.APIClient;
import com.officework.ApiController.APIInterface;
import com.officework.R;
import com.officework.base.BaseFragment;
import com.officework.constants.Constants;
import com.officework.constants.FragmentTag;
import com.officework.models.SocketDataSync;
import com.officework.utils.Utilities;
import com.officework.utils.socket.SocketConstants;
import com.officework.utils.socket.SocketHelper;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.socket.client.IO;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;


/**
 * Created by igniva-android-17 on 25/5/18.
 */
public class CustomerInformationFragment extends BaseFragment implements AdapterView.OnItemSelectedListener, View.OnFocusChangeListener {

    private static final int PERMISSION_REQUEST_CODE = 105;
    Unbinder unbinder;
    @BindView(R.id.Email)
    EditText email;
    @BindView(R.id.txtEmail)
    FloatLabeledEditText txtEmail;
    @BindView(R.id.ConfirmEmail)
    NoMenuEditText txtConfirmEmail;
    @BindView(R.id.first_name)
    EditText txtFirstName;
    @BindView(R.id.last_name)
    EditText txtLastName;
    @BindView(R.id.contact_no)
    EditText txtContactNo;
    @BindView(R.id.post_code)
    EditText txtPostCode;

    @BindView(R.id.state_code)
    EditText txtstateCode;
    //    @BindView(R.id.txtIdValue)
//    FloatLabeledEditText txtIdValue;
    @BindView(R.id.address_one)
    EditText txtAddressOne;
    @BindView(R.id.address_two)
    EditText txtAddressTwo;
    @BindView(R.id.address_three)
    EditText txtAddressThree;
    @BindView(R.id.city)
    EditText txtCity;
    @BindView(R.id.btn_next)
    TextView btnNext;
    //    @BindView(R.id.select_image)
//    TextView btnSelect;
//    @BindView(R.id.show_image)
//    ImageView showImage;
    @BindView(R.id.spinner)
    Spinner spinner;
    //    @BindView(R.id.btn_close)
//    ImageButton crossimage;
    @BindView(R.id.spinner_text)
    TextView spinnerText;
    String Path;
    String[] idValueArray = {"Select ID", "Driving Licence", "Passport", "ID Card"};
    //    @BindView(R.id.idValue)
//    EditText idValue;
    @BindView(R.id.progressBarMedium)
    ProgressBar progressBarMedium;
    EditText pass;
    View dialogView;
    LinearLayout camera, gallery, cancel;
    BottomSheetDialog bottom_dialog;
    boolean isClicked = false;
    File mediaStorageDir;
    private String TAG = "ChatActivity";
    int indexOfMyView2;
    @BindView(R.id.imgsllayout)
    LinearLayout imagesLLayout;
    public ArrayList<String> imageArrayList;
    public ArrayList<Uri> imageArrayListUri;

    public ArrayList<Boolean> value2;

    ArrayList<DocumentsTypePojo> documentsList;
    private String offer_price;
    private String order_id, udi_id;
    private int customer_id;
    private DataPojo dataPojo;
    Uri selectedImage = null;
    Uri Galleryimage1=null;
    Uri Galleryimage2=null;
    private Bitmap bitmap;
    private File destination = null;
    private InputStream inputStreamImg;
    private String imgPath = null;
    private String filePath1 = "";
    private final int PICK_IMAGE_CAMERA = 1, PICK_IMAGE_GALLERY = 2;
    private ByteArrayOutputStream bytes;
    private View view;
    boolean isAlredyRegistered = false;
    boolean iLogedIn = false;
    Dialog passDialog;
    ProgressBar dialogProgress;
    boolean isSecretCodeEntered = false;
    boolean isFocusSet = false;
    boolean isSecretCodeAlreadyGot = false;
    String emailMain = "";
    int IdentityTypeID;
    SpinnerAdapter arrayAdapter;
    @BindView(R.id.txtfirstnamee)
    FloatLabeledEditText hintFistname;
    @BindView(R.id.txtlastnamee)
    FloatLabeledEditText hintLastname;
    @BindView(R.id.txtcontactnumbere)
    FloatLabeledEditText hintContactNumber;
    @BindView(R.id.txtpostcode)
    FloatLabeledEditText hintPostCode;
    @BindView(R.id.txtstatecode)
    FloatLabeledEditText hintStateNumber;
    @BindView(R.id.txtadreessss)
    FloatLabeledEditText hintAdress1;
    @BindView(R.id.txtcity)
    FloatLabeledEditText hintCity;
    @BindView(R.id.txtconfirmEmail)
    FloatLabeledEditText hintConfirmEmail;
    static final String[] CONTENT_ORIENTATION = new String[]{
            MediaStore.Images.ImageColumns.ORIENTATION
    };
    private boolean value;
    private boolean alredylogin = false;
    Uri imageUri = null;
    boolean isLoogedIn;

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm =
                (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token
        // from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //init Presenter
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        if (view == null) {
            view = inflater.inflate(R.layout.fragment_custumer_information, container, false);
            unbinder = ButterKnife.bind(this, view);
            if (getArguments() != null) {
                dataPojo = (DataPojo) getArguments().getParcelable("OFFER_PRICE_OBJECT");
                offer_price = getArguments().getString("OFFER_PRICE");
                order_id = getArguments().getString("ORDER_ID");
                udi_id = getArguments().getString("UDIID");
            }
            String androidId = Utilities.getInstance(getActivity()).getPreference(getActivity(), Constants.ANDROID_ID, "");
            IO.Options options = new IO.Options();
            setHint();
            //  txtEmail.setHint(txtEmail.getHint()+" "+Html.fromHtml(getResources().getString(R.string.asteriskred)));
//        SpannableString redSpannable = new SpannableString("*");
//        redSpannable.setSpan(new ForegroundColorSpan(Color.RED), 0, 1, 0);
//        Spanned hint = Html.fromHtml(txtEmail.getHint().toString() + redSpannable);
//        txtEmail.setHint(hint);
            socketHelper = new SocketHelper.Builder(SocketConstants.HOST_NAME1 + "?" + Constants.REQUEST_UNIQUE_ID + "=" + androidId, options)
                    .addEvent(SocketConstants.EVENT_CONNECTED)
                    .addListener(null)
                    .build();
//        CustomAdapter customAdapter=new CustomAdapter(getActivity(),country);
//        spinner.setAdapter(customAdapter);
            //Creating the ArrayAdapter instance having the country list
            documentsList = new ArrayList<>();
            DocumentsTypePojo documentsTypePojo = new DocumentsTypePojo();
            documentsTypePojo.setIdentityTypeID(0);
            documentsTypePojo.setIdentityValue(getString(R.string.proof_of_identity));
            documentsList.add(documentsTypePojo);

            arrayAdapter = new SpinnerAdapter(getActivity(),
                    android.R.layout.simple_spinner_item, documentsList);
            // arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //Setting the ArrayAdapter data on the Spinner
            spinner.setAdapter(arrayAdapter);
            spinner.setOnItemSelectedListener(this);
            getDocumentsType();
//            crossimage.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    showImage.setVisibility(View.GONE);
//                    btnSelect.setVisibility(View.VISIBLE);
//                    crossimage.setVisibility(View.GONE);
//                    filePath1 = "";
//
//
//                }
//            });
//            showImage.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    showImage();
//                }
//            });
            spinnerText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isClicked = true;
                    spinner.performClick();


                }
            });
            // spinner.setOnItemSelectedListener(this);


            //        fullName.addTextChangedListener(new CustomTextWatcher(fullName,"FirstName"));
//        lastName.addTextChangedListener(new CustomTextWatcher(lastName,"LastName"));
//        phoneNo.addTextChangedListener(new CustomTextWatcher(phoneNo,"PhoneNo"));
//        email.addTextChangedListener(new CustomTextWatcher(email,"Email"));
//        idValue.addTextChangedListener(new CustomTextWatcher(idValue,"IdentityNumber"));
            email.setOnFocusChangeListener(this::onFocusChange);
            txtConfirmEmail.setOnFocusChangeListener(this::onFocusChange);
            imageArrayList = new ArrayList<String>();
            imageArrayListUri = new ArrayList<Uri>();
            value2= new ArrayList<Boolean>();
            setFocus();
            addImageView();

        }

        txtConfirmEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

//                String mail=email.getText().toString();
//                String confirm=txtConfirmEmail.getText().toString();
//                if (!Utilities.EmailValidation(email, getString(R.string.error_valid_mail))||!Utilities.EmailValidation(txtConfirmEmail, getString(R.string.error_confirm_mail)) ||!Utilities.confirm(txtConfirmEmail,getString(R.string.error_confirm),mail,confirm) ) {
//
//                }
//                else{
//                    checkEmail();
//                }

//                if(txtConfirmEmail.getText().toString().equals(email.getText().toString())){
//                  checkEmail();
//
//                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        return view;
    }


    private void setHint() {
        String Email = "<a>Email <font color='#FF0000'> *</font>";
        String Confirm = "<a>Confirm Email <font color='#FF0000'> *</font>";
        String First = "<a>First Name <font color='#FF0000'> *</font>";
        String Last = "<a>Last Name <font color='#FF0000'> *</font>";
        String Contact = "<a>Contact Number <font color='#FF0000'> *</font>";
        String Post = "<a>Post Code <font color='#FF0000'> *</font>";
        String Address = "<a>Address Line 1 <font color='#FF0000'> *</font>";
        String City = "<a>City <font color='#FF0000'> *</font>";
        String State = "<a>State Code <font color='#FF0000'> *</font>";
        txtEmail.setHint(Html.fromHtml(Email));
        hintConfirmEmail.setHint(Html.fromHtml(Confirm));
        hintFistname.setHint(Html.fromHtml(First));
        hintLastname.setHint(Html.fromHtml(Last));
        hintContactNumber.setHint(Html.fromHtml(Contact));
        hintPostCode.setHint(Html.fromHtml(Post));
        hintAdress1.setHint(Html.fromHtml(Address));
        hintCity.setHint(Html.fromHtml(City));
        hintStateNumber.setHint(Html.fromHtml(State));
    }

    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        TitleBarFragment fragment =
                (TitleBarFragment) getFragment(R.id.headerContainer);
        if (fragment != null) {
            fragment.setTitleBarVisibility(true);
            fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string.customer_info), true, false, 0);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        unbinder.unbind();
    }

    public void submitInfo() {
//        String mail=email.getText().toString();
//        String confirm=txtConfirmEmail.getText().toString();
//        if (!Utilities.EmailValidation(email, getString(R.string.error_valid_mail))||!Utilities.EmailValidation(txtConfirmEmail, getString(R.string.error_confirm_mail)) ||!Utilities.confirm(txtConfirmEmail,getString(R.string.error_confirm),mail,confirm) ||!Utilities.validation(txtFirstName, getString(R.string.error_first_name))||!Utilities.validation(txtLastName, getString(R.string.error_last_name))  || !Utilities.validation(txtContactNo, getString(R.string.error_contact))|| !Utilities.validContact(txtContactNo, getString(R.string.error_valid_contact))|| !Utilities.validation(txtPostCode, getString(R.string.error_post))||!Utilities.validation(txtAddressOne, getString(R.string.error_address_one))||!Utilities.validation(txtCity, getString(R.string.error_city))|| !Utilities.validationSpinner(spinnerText.getText().toString(), getString(R.string.selectid))    ||!Utilities.validation(idValue, getString(R.string.identityValue))||!Utilities.validationofImage(filePath1, getString(R.string.error_image))) {
//
//        }else {
//            try {
//                progressBarMedium.setVisibility(View.VISIBLE);
//                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//
//                submitResult();
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }


        String mail = email.getText().toString().trim();
        String confirm = txtConfirmEmail.getText().toString().trim();
        if (!Utilities.EmailValidation(email, getString(R.string.error_valid_mail)) || !Utilities.EmailValidation(txtConfirmEmail, getString(R.string.error_confirm_mail)) || !Utilities.confirm(txtConfirmEmail, getString(R.string.error_confirm), mail, confirm) || !Utilities.validation(txtFirstName, getString(R.string.error_first_name)) || !Utilities.validation(txtLastName, getString(R.string.error_last_name)) || !Utilities.validation(txtContactNo, getString(R.string.error_contact)) || !Utilities.validContact(txtContactNo, getString(R.string.error_valid_contact))   || !Utilities.validation(txtAddressOne, getString(R.string.error_address_one)) || !Utilities.validation(txtCity, getString(R.string.error_city)) ||!Utilities.validation(txtPostCode, getString(R.string.error_post)) || !Utilities.validation(txtstateCode, getString(R.string.error_post2)) || !Utilities.validationSpinner(spinner, getString(R.string.selectid)) || !Utilities.validationofImage(imageArrayList, getString(R.string.error_image))) {

        } else {
            try {
//                txtConfirmEmail.setEnabled(false);
//                email.setEnabled(false);
                dataPojo = getArguments().getParcelable("OFFER_PRICE_OBJECT");
                Fragment declarationFragment = new DeclarationFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("OFFER_PRICE_OBJECT", dataPojo);
                bundle.putString("email", email.getText().toString());
                bundle.putString("confirm_email", txtConfirmEmail.getText().toString());
                bundle.putString("first_name", txtFirstName.getText().toString().trim());
                bundle.putString("last_name", txtLastName.getText().toString().trim());
                bundle.putString("contact_number", txtContactNo.getText().toString().trim());
                bundle.putString("post_code", txtPostCode.getText().toString().trim());
                bundle.putString("state_code", txtstateCode.getText().toString().trim());
                bundle.putString("adres1", txtAddressOne.getText().toString().trim());
                bundle.putString("adres2", txtAddressTwo.getText().toString().trim());
                bundle.putString("adres3", txtAddressThree.getText().toString().trim());
                bundle.putString("city", txtCity.getText().toString().trim());
                bundle.putString("id_type", spinnerText.getText().toString().trim());
//                bundle.putString("id_value",idValue.getText().toString().trim());
                // bundle.putString("filepath",filePath1);
                bundle.putString("offer_price", offer_price);
                bundle.putString("order_id", order_id);
                bundle.putString("udi_id", udi_id);
                bundle.putInt("customer_id", customer_id);
                bundle.putStringArrayList("image_list", imageArrayList);
                bundle.putInt("IdentityTypeID", IdentityTypeID);

                declarationFragment.setArguments(bundle);
                replaceFragment(R.id.container, declarationFragment,
                        FragmentTag.MANUAL_TEST_FRAGMENT.name(), true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    private void clearData() {
        txtFirstName.setText("");
        txtLastName.setText("");
        txtConfirmEmail.setText("");
        email.setText("");
        txtAddressOne.setText("");
        txtContactNo.setText("");
        txtPostCode.setText("");
        txtstateCode.setText("");
        txtAddressTwo.setText("");
        txtAddressThree.setText("");
        txtCity.setText("");
//        idValue.setText("");
        spinner.setSelection(0);
        arrayAdapter.notifyDataSetChanged();

    }

    public void submitResult() {
        File file = new File(filePath1);
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("image", file.getName(), requestBody);
        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());
        APIInterface apiInterface =
                APIClient.getClient(getActivity()).create(APIInterface.class);

//        HashMap<String, Object> map = createMap();
//        Call<SocketDataSync> call1 = apiInterface.submitInfo(map,fileToUpload, filename);


        Utilities utils = Utilities.getInstance(getActivity());


//        Call<SocketDataSync> call1 = apiInterface.submitInfo(Utilities.getInstance(getActivity()).getPreference(getActivity(),
//                Constants.QRCODEID, ""),
//                Utilities.getInstance(getActivity()).getPreference(getActivity(),
//                        Constants.ANDROID_ID, ""),order_id,udi_id,txtFirstName.getText().toString(),"",txtLastName.getText().toString(),
//                email.getText().toString(),txtContactNo.getText().toString(),txtAddressOne.getText().toString(),dataPojo.getPercentageDeduction(),txtAddressTwo.getText().toString(),txtAddressThree.getText().toString(),
//                txtPostCode.getText().toString(),txtCity.getText().toString(),idValue.getText().toString(),spinnerText.getText().toString(),dataPojo.getMcheckInformationID(),
//                dataPojo.getQuotedPrice(),utils.getSecurePreference(getActivity(), "MMR_3", ""),dataPojo.getIMEI(),dataPojo.getFailTestId(),dataPojo.getPassTestId(),
//                dataPojo.getUserID(),dataPojo.getStoreID(),dataPojo.getEnterprisePartnerID(),dataPojo.getSKUID(),dataPojo.getCatalogPartnerPricingID(),dataPojo.getLocationID(),
//                fileToUpload, filename, utils.getSecurePreference(getActivity(), "MMR_4", ""), utils.getSecurePreference(getActivity(), "MMR_5", ""), utils.getSecurePreference(getActivity(), "MMR_6", ""),Utilities.getInstance(getActivity()).getPreference(getActivity(),Constants.DEVICEPRICE,""),);
//


//        call1.enqueue(new Callback<SocketDataSync>() {
//            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
//            @Override
//            public void onResponse(Call<SocketDataSync> call1, Response<SocketDataSync> response) {
//                Log.d("Success", response.toString());
//                try {
//                    if (response.isSuccessful()) {
//                        progressBarMedium.setVisibility(View.GONE);
//                        SocketDataSync socketDataSync = response.body();
//                        if (socketDataSync != null) {
//                            if (socketDataSync.isSuccess()) {
//                                DataPojo dataPojo = socketDataSync.getData();
//                                String order_id = dataPojo.getOrderDetailID();
//
//                                OrderInformationFragment orderInformationFragment =
//                                        new OrderInformationFragment();
//                                Bundle bundle = new Bundle();
//
//                                bundle.putString("OFFER_PRICE", offer_price);
//                                bundle.putString("ORDER_ID", order_id);
//                                orderInformationFragment.setArguments(bundle);
//
//                                replaceFragment(R.id.container, orderInformationFragment,
//                                        FragmentTag.MANUAL_TEST_FRAGMENT.name(), false);
//                                TitleBarFragment fragment =
//                                        (TitleBarFragment) getFragment(R.id.headerContainer);
//                                if (fragment != null) {
//                                    fragment.setTitleBarVisibility(true);
//                                    fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string.confirmation), true, false, 0);
//
//                                }
//                            }  else {
//                                fabricLog("CustomerInfo_Frag_Submit_Customer_info_api_error",response.toString());
//                            }
//                        }
//
//                    }
//
//
//                } catch (Exception e) {
//                    fabricLog("CustomerInfo_Frag_Submit_Customer_info_api_exception",e.getMessage());
//                    e.printStackTrace();
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<SocketDataSync> call1, Throwable t) {
//
//                try {
//                    progressBarMedium.setVisibility(View.GONE);
//                    fabricLog("CustomerInfo_Frag_Submit_Customer_info_api_exception",t.getMessage());
//                    Log.d("error", t.toString());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });


//        call1.enqueue(new Callback<SocketDataSync>() {
//            @Override
//            public void onResponse(Call<SocketDataSync> call, Response<SocketDataSync> response) {
//
//                try {
//                    progressBarMedium.setVisibility(View.INVISIBLE);
//                    if (response.isSuccessful()) {
//
//                        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                        SocketDataSync socketDataSync = response.body();
//                        if (socketDataSync != null) {
//                            if (socketDataSync.isSuccess()) {
//                                Toast.makeText(getContext(),R.string.sucess,Toast.LENGTH_LONG).show();
//
//                                DataPojo dataPojo = socketDataSync.getData();
//                                String order_id = dataPojo.getOrderDetailID();
//
//                                OrderInformationFragment orderInformationFragment =
//                                        new OrderInformationFragment();
//                                Bundle bundle = new Bundle();
//
//                                bundle.putString("OFFER_PRICE", offer_price);
//                                bundle.putString("ORDER_ID", order_id);
//                                orderInformationFragment.setArguments(bundle);
//
//                                replaceFragment(R.id.container, orderInformationFragment,
//                                        FragmentTag.MANUAL_TEST_FRAGMENT.name(), false);
//                                TitleBarFragment fragment =
//                                        (TitleBarFragment) getFragment(R.id.headerContainer);
//                                if (fragment != null) {
//                                    fragment.setTitleBarVisibility(true);
//                                    fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string.confirmation), true, false, 0);
//
//                                }
//                            }  else {
//                                Toast.makeText(getContext(),socketDataSync.getMessage(),Toast.LENGTH_LONG).show();
//
//                                fabricLog("CustomerInfo_Frag_Submit_Customer_info_api_error",response.toString());
//                            }
//                        }
//
//                    }
//
//
//                } catch (Exception e) {
//                    fabricLog("CustomerInfo_Frag_Submit_Customer_info_api_exception", e.getMessage());
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<SocketDataSync> call, Throwable t) {
//                try {
//                    progressBarMedium.setVisibility(View.GONE);
//                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//                    Toast.makeText(getContext(),t.getMessage(),Toast.LENGTH_LONG).show();
//                    fabricLog("CustomerInfo_Frag_Submit_Customer_info_api_exception",t.getMessage());
//                    Log.d("error", t.toString());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });

    }

    private HashMap<String, Object> createMap() {
        HashMap<String, Object> map = new HashMap<>();

        try {
            String qrCodeiD = Utilities.getInstance(getActivity()).getPreference(getActivity(),
                    Constants.QRCODEID, "");

            JSONObject jsonObject = new JSONObject();
            String androidId = Utilities.getInstance(getActivity()).getPreference(getActivity(),
                    Constants.ANDROID_ID, "");

            map.put(Constants.UNIQUE_ID, androidId);
            map.put(Constants.QRCODEID, qrCodeiD);
            map.put(Constants.ORDER_ID, order_id);
            map.put(Constants.UDIID, udi_id);
            map.put(Constants.FULL_NAME, txtFirstName.getText().toString());
            map.put(Constants.MIDDLE_NAME, "");
            map.put(Constants.LAST_NAME, txtLastName.getText().toString());
            map.put(Constants.EMAIL, email.getText().toString());
            map.put(Constants.PHONE_NO, txtContactNo.getText().toString());
            map.put(Constants.ADDRESS1, txtAddressOne.getText().toString());
            map.put(Constants.ADDRESS2, txtAddressTwo.getText().toString());
            map.put(Constants.ADDRESS3, txtAddressThree.getText().toString());
            map.put(Constants.POSTAL_CODE, txtPostCode.getText().toString());
            map.put(Constants.CITY, txtCity.getText().toString());
//            map.put(Constants.IDENTITY_NUMBER,idValue.getText().toString());
            map.put("McheckInformationID", dataPojo.getMcheckInformationID());
            map.put("QuotedPrice", dataPojo.getQuotedPrice());
            map.put("IMEI", dataPojo.getIMEI());
            map.put("SerialNumber", dataPojo.getSerialNumber());
            map.put("FailTestId", dataPojo.getFailTestId());
            map.put("PassTestId", dataPojo.getPassTestId());
            map.put("UserID", dataPojo.getUserID());
            map.put("StoreID", dataPojo.getStoreID());
            map.put("EnterprisePartnerID", dataPojo.getEnterprisePartnerID());
            map.put("SKUID", dataPojo.getSKUID());
            map.put("CatalogPartnerPricingID", dataPojo.getCatalogPartnerPricingID());
            map.put("LocationID", dataPojo.getLocationID());

        } catch (Exception E) {
            E.printStackTrace();
        }
        return map;
    }

    @OnClick({R.id.btn_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
//            case R.id.cancel:
//
//                hideKeyboard(getActivity());
//                getActivity().setResult(Activity.RESULT_OK);
//                getActivity().finish();
//                break;
            case R.id.btn_next:

                hideKeyboard(getActivity());

                if (Utilities.getInstance(getActivity()).isInternetWorking(getActivity())) {
//                  if(email.isFocused() || txtConfirmEmail.isFocused()){
                    String mail = email.getText().toString().trim();
                    String confirm = txtConfirmEmail.getText().toString().trim();
                    if (!emailMain.equalsIgnoreCase(mail)) {

                        if (!Utilities.EmailValidation(email, getString(R.string.error_valid_mail))
                                || !Utilities.EmailValidation(txtConfirmEmail, getString(R.string.error_confirm_mail))
                                || !Utilities.confirm(txtConfirmEmail, getString(R.string.error_confirm), mail, confirm)) {
                        } else {

                            checkEmail(true);
                        }
                    } else {

                        if (!Utilities.EmailValidation(email, getString(R.string.error_valid_mail))
                                || !Utilities.EmailValidation(txtConfirmEmail, getString(R.string.error_confirm_mail))
                                || !Utilities.confirm(txtConfirmEmail, getString(R.string.error_confirm), mail, confirm)
                                || !Utilities.validation(txtFirstName, getString(R.string.error_first_name))
                                || !Utilities.validation(txtLastName, getString(R.string.error_last_name))
                                || !Utilities.validation(txtContactNo, getString(R.string.error_contact))
                                || !Utilities.validContact(txtContactNo, getString(R.string.error_valid_contact))
                                || !Utilities.validation(txtAddressOne, getString(R.string.error_address_one))
                                || !Utilities.validation(txtCity, getString(R.string.error_city))
                                || !Utilities.validation(txtPostCode, getString(R.string.error_post))
                                || !Utilities.validation(txtstateCode, getString(R.string.error_post2))
                                || !Utilities.validationSpinner(spinner, getString(R.string.selectid))
//                                  ||!Utilities.validation(idValue, getString(R.string.identityValue))
                                || !Utilities.validationofImage(imageArrayList, getString(R.string.error_image))) {
                        } else {
                            if (isSecretCodeEntered) {
                                validateAddressApi(true);
//                            submitInfo();

                            }else {
                                validateAddressApi(false);
                            }
                        }



//                        if (isSecretCodeEntered) {
//                           validateAddressApi(true);
////                            submitInfo();
//
//                        }
//
//                        else {
//
//                            if (!Utilities.EmailValidation(email, getString(R.string.error_valid_mail))
//                                    || !Utilities.EmailValidation(txtConfirmEmail, getString(R.string.error_confirm_mail))
//                                    || !Utilities.confirm(txtConfirmEmail, getString(R.string.error_confirm), mail, confirm)
//                                    || !Utilities.validation(txtFirstName, getString(R.string.error_first_name))
//                                    || !Utilities.validation(txtLastName, getString(R.string.error_last_name))
//                                    || !Utilities.validation(txtContactNo, getString(R.string.error_contact))
//                                    || !Utilities.validContact(txtContactNo, getString(R.string.error_valid_contact))
//                                    || !Utilities.validation(txtAddressOne, getString(R.string.error_address_one))
//                                    || !Utilities.validation(txtCity, getString(R.string.error_city))
//                                    || !Utilities.validation(txtPostCode, getString(R.string.error_post))
//                                    || !Utilities.validation(txtstateCode, getString(R.string.error_post2))
//                                    || !Utilities.validationSpinner(spinner, getString(R.string.selectid))
////                                  ||!Utilities.validation(idValue, getString(R.string.identityValue))
//                                    || !Utilities.validationofImage(imageArrayList, getString(R.string.error_image))) {
//                            } else {
//                                validateAddressApi(false);
////                                if (!isSecretCodeAlreadyGot) {
////                                    getSecretCode(true);
////                                } else {
////                                    showPasswordDialog(false, true);
////                                }
//                            }
//                        }
                    }
                } else {
                    Toast.makeText(getActivity(),
                            getResources().getString(R.string.please_check_internet_connection),
                            Toast.LENGTH_SHORT).show();
                }


                break;
//            case
//                R.id.select_image:
//                selectImage();
//            break;
        }
    }

//    // Select image from camera and gallery
//    private void selectImage() {
//        try {
//            PackageManager pm = getActivity().getPackageManager();
//            int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, getActivity().getPackageName());
//            if (hasPerm == PackageManager.PERMISSION_GRANTED) {
//                final CharSequence[] options = {"Take Photo", "Choose From Gallery","Cancel"};
//               AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                builder.setTitle("Select Option");
//                builder.setItems(options, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int item) {
//                        if (options[item].equals("Take Photo")) {
//                            dialog.dismiss();
//                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                            startActivityForResult(intent, PICK_IMAGE_CAMERA);
//                        } else if (options[item].equals("Choose From Gallery")) {
//                            dialog.dismiss();
//                            Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                            startActivityForResult(pickPhoto, PICK_IMAGE_GALLERY);
//                        } else if (options[item].equals("Cancel")) {
//                            dialog.dismiss();
//                        }
//                    }
//                });
//                builder.show();
//            } else
//                Toast.makeText(getActivity(), "Camera Permission error", Toast.LENGTH_SHORT).show();
//        } catch (Exception e) {
//            Toast.makeText(getActivity(), "Camera Permission error", Toast.LENGTH_SHORT).show();
//            e.printStackTrace();
//        }
//    }
//

    // Select image from camera and gallery
    private void selectImage() {
        try {
            PackageManager pm = getActivity().getPackageManager();
            int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, getActivity().getPackageName());
            if (hasPerm == PackageManager.PERMISSION_GRANTED) {
//                final CharSequence[] options = {"Take Photo", "Choose From Gallery","Cancel"};
//               AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                builder.setTitle("Select Option");
//                builder.setItems(options, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int item) {
//                        if (options[item].equals("Take Photo")) {
//                            dialog.dismiss();
//                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                            startActivityForResult(intent, PICK_IMAGE_CAMERA);
//                        } else if (options[item].equals("Choose From Gallery")) {
//                            dialog.dismiss();
//                            Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                            startActivityForResult(pickPhoto, PICK_IMAGE_GALLERY);
//                        } else if (options[item].equals("Cancel")) {
//                            dialog.dismiss();
//                        }
//                    }
//                });
//                builder.show();


                dialogView = getLayoutInflater().inflate(R.layout.bottom_sheet, null);
                bottom_dialog = new BottomSheetDialog(getActivity());
                bottom_dialog.setContentView(dialogView);
                bottom_dialog.setTitle("Select Option");
                camera = dialogView.findViewById(R.id.camer_sheet);
                gallery = dialogView.findViewById(R.id.gallery_sheet);
                cancel = dialogView.findViewById(R.id.cancel_sheet);
                camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cameraIntent();
                    }
                });
                gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto, PICK_IMAGE_GALLERY);
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (getActivity() != null)
                            bottom_dialog.dismiss();
                    }
                });
                bottom_dialog.show();
            } else
                Toast.makeText(getActivity(), "Camera Permission error", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Camera Permission error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void cameraIntent() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()).getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()).getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()).getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,}, PERMISSION_REQUEST_CODE);
            }

        }
    }

    private void startCamera() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = timeStamp + ".jpg";

        // Create parameters for Intent with filename
        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.TITLE, fileName);
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image capture by camera");

        // imageUri is the current activity attribute, define and save it for later usage

        if (getActivity() != null)
            imageUri = getActivity().getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//            imageUri = getImageUri();

        /**** EXTERNAL_CONTENT_URI : style URI for the "primary" external storage volume. ****/


        // Standard Intent action that can be sent to have the camera
        // application capture an image and return it.
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, PICK_IMAGE_CAMERA);
    }


    private void getWirtePermissionAndCreateDir(String... data) {
        if (Build.VERSION.SDK_INT < 23) {
            createDir();
        } else {
            if (getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                createDir();
            } else {
                final String[] PERMISSIONS_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                //Asking request Permissions
                requestPermissions(PERMISSIONS_STORAGE, 9);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

//        boolean writeAccepted = false;
//        switch (requestCode) {
//            case 9:
//                writeAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                break;
//        }
//        if (writeAccepted) {
//
//            createDir();
//        } else {
//            Toast.makeText(getActivity(), "You don't assign permission.", Toast.LENGTH_LONG).show();
//        }

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED || grantResults[2] != PackageManager.PERMISSION_GRANTED) {
                        if (shouldShowRequestPermissionRationale(permissions[0]) || shouldShowRequestPermissionRationale(permissions[1]) || shouldShowRequestPermissionRationale(permissions[2])) {
                            showmessage(getResources().getString(R.string.txtPermissionMessage2),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                                            }
                                        }
                                    });
                        } else {
                            showmessage(getResources().getString(R.string.txtPermissionMessage2), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                                    intent.setData(uri);
                                    startActivityForResult(intent, 1);
                                }
                            });
                        }
                    }
//                    else
//                    {
//                        startCamera();
//                    }
                }
                break;
        }
    }


    private void showmessage(String message, DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage(message)
                .setPositiveButton("OK", onClickListener)
                .create().
                show();
    }


    private void createDir() {
        mediaStorageDir = new File(Environment.getExternalStorageDirectory().toString(), "FrankMobile");
        mediaStorageDir.mkdirs();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        destination = new File(mediaStorageDir.getPath() + File.separator + "frank_img" + timeStamp + ".jpeg");
        FileOutputStream fo;
        try {
//                    destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        filePath1 = destination.getAbsolutePath();
//        btnSelect.setVisibility(View.GONE);
//        showImage.setVisibility(View.VISIBLE);
//        crossimage.setVisibility(View.VISIBLE);
//        Uri select=Uri.parse(filePath1);
//        showImage.setImageURI(select);
//        Drawable d = Drawable.createFromPath(filePath1);
//        showImage.setBackground(d);
//        showImage.getBackground().setAlpha(60);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (bottom_dialog != null) {
                if (bottom_dialog.isShowing()) {
                    bottom_dialog.dismiss();
                }
            }
            if (requestCode == PICK_IMAGE_CAMERA) {

                try {
                    //      if (data != null) {

                    if (resultCode == RESULT_OK) {
                        if (getActivity() != null) {
                            /*********** Load Captured Image And Data Start ****************/

//                            convertImageUriToFile(imageUri, getActivity());


                            //  Create and excecute AsyncTask to load capture image

//                            new LoadImagesFromSDCard().execute("" + imageId);
//                          LoadImagesFromSDCard();

                            setImage(true);

                            /*********** Load Captured Image And Data End ****************/


                        } else {

//                            Toast.makeText(getActivity(), " Picture was not taken ", Toast.LENGTH_SHORT).show();
                        }

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == PICK_IMAGE_GALLERY) {
                try {
                    if (data != null) {
                        selectedImage = data.getData();
//                        File file = new File(filePath1);
//                        mediaStorageDir = new File(Environment.getExternalStorageDirectory().toString(), "FrankMobile");
//                        mediaStorageDir.mkdirs();
//                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//                        destination= new File(mediaStorageDir.getPath() + File.separator + "IMG_FRONTCAM_" + timeStamp + ".jpeg");
//                        file.renameTo(destination);
//                        filePath1 = destination.getPath();
                        String[] filePath = {MediaStore.Images.Media.DATA};
                        Cursor c = getActivity().getContentResolver().query(selectedImage, filePath, null, null, null);
                        c.moveToFirst();
                        int columnIndex = c.getColumnIndex(filePath[0]);
                        filePath1 = c.getString(columnIndex);
                        c.close();

                        int a=CameraImagePickerHelper.getRotationFromGallery(getActivity(),selectedImage);
                        Log.d("imagesssssss",String.valueOf(a));
//                        btnSelect.setVisibility(View.GONE);
//                        showImage.setVisibility(View.VISIBLE);
//                        crossimage.setVisibility(View.VISIBLE);
//                        showImage.setImageURI(selectedImage);
//                        Drawable d = Drawable.createFromPath(filePath1);
//                        showImage.setBackground(d);
//                        showImage.getBackground().setAlpha(60);

                        imageArrayList.add(indexOfMyView2, filePath1);
                        if(imageArrayListUri.size()==0) {
                            imageArrayListUri.add(0, selectedImage);
                        }else {
                            imageArrayListUri.add(1, selectedImage);
                        }
                        setImage(false);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /************ Convert Image Uri path to physical path **************/

    public void convertImageUriToFile(Uri imageUri, Activity activity) {

        Cursor cursor = null;
        int imageID = 0;

        try {

            /*********** Which columns values want to get *******/
            String[] proj = {
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Thumbnails._ID,
                    MediaStore.Images.ImageColumns.ORIENTATION
            };

            cursor = activity.managedQuery(

                    imageUri,         //  Get data for specific image URI
                    proj,             //  Which columns to return
                    null,             //  WHERE clause; which rows to return (all rows)
                    null,             //  WHERE clause selection arguments (none)
                    null              //  Order-by clause (ascending by name)

            );

            //  Get Query Data

            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int columnIndexThumb = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);
            int file_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            //int orientation_ColumnIndex = cursor.
            //    getColumnIndexOrThrow(MediaStore.Images.ImageColumns.ORIENTATION);

            int size = cursor.getCount();

            /*******  If size is 0, there are no images on the SD Card. *****/

            if (size == 0) {


                //imageDetails.setText("No Image");
            } else {

                int thumbID = 0;
                if (cursor.moveToFirst()) {

                    /**************** Captured image details ************/

                    /*****  Used to show image on view in LoadImagesFromSDCard class ******/
                    imageID = cursor.getInt(columnIndex);

                    thumbID = cursor.getInt(columnIndexThumb);

                    Path = cursor.getString(file_ColumnIndex);

                    //String orientation =  cursor.getString(orientation_ColumnIndex);

                    String CapturedImageDetails = " CapturedImageDetails : \n\n"
                            + " ImageID :" + imageID + "\n"
                            + " ThumbID :" + thumbID + "\n"
                            + " Path :" + Path + "\n";

                    // Show Captured Image detail on activity
                    //    imageDetails.setText( CapturedImageDetails );

                }
            }
            //   if (cursor != null) {
//                cursor.close();
            cursor = null;
            //  cursor=null;
            //   }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //finally {

        // }

        // Return Captured Image ImageID ( By this ImageID Image will load from sdcard )

//        return ""+imageID;
    }


    public void showImage(int position,ArrayList<Boolean> value) {
        Dialog builder = new Dialog(getActivity(), R.style.CodeFont);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.BLACK));
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
//nothing;
            }
        });

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.touch_image, null);

        builder.setContentView(view);


        TouchImageView imageView = (TouchImageView) view.findViewById(R.id.imageView);
        ImageView close = view.findViewById(R.id.btn_close1);
// Uri select=Uri.parse(filePath1);
//        Uri select = Uri.parse(imageArrayList.get(position));
//
//        imageView.setImageURI(select);

        if(value2.get(position)) {
            CameraImagePickerHelper cameraImagePickerHelper = new CameraImagePickerHelper();
            File file = new File(imageArrayList.get(position));
            Uri uri = Uri.fromFile(file);
            Bitmap bitmap = null;
            try {
                bitmap = cameraImagePickerHelper.handleSamplingAndRotationBitmap(getActivity(), uri, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
//        int angleToRotate = cameraImagePickerHelper.RoatationAngle(getActivity(), Camera.CameraInfo.CAMERA_FACING_BACK);
//        Bitmap bitmapImage = cameraImagePickerHelper.rotate(bitmap, angleToRotate);
            imageView.setImageBitmap(bitmap);


        }else{
            CameraImagePickerHelper cameraImagePickerHelper = new CameraImagePickerHelper();
//    File file=new File(imageArrayList.get(indexOfMyView2));
//    Uri uri = Uri.fromFile(file);
//    Bitmap bitmap = null;
            try {

                if(imageArrayListUri.size()==1)
                {
                    bitmap = cameraImagePickerHelper.handleSamplingAndRotationBitmap(getActivity(), imageArrayListUri.get(0),null);
                }else if(imageArrayListUri.size()>1) {
                    bitmap = cameraImagePickerHelper.handleSamplingAndRotationBitmap(getActivity(), imageArrayListUri.get(position),null);
                }



//                int angleToRotate = getRoatationAngle(getActivity(), Camera.CameraInfo.CAMERA_FACING_BACK);
//            //    Bitmap bitmapImage = rotate(bitmap, angleToRotate);
                Bitmap bitmapImage=null;
                if (bitmapImage != null && !bitmapImage.isRecycled()) {
                    bitmapImage.recycle();
                    bitmapImage = null;
                }
                //  bitmapImage= cameraImagePickerHelper.rotateImageIfRequired(getActivity(), bitmap, uri);



                int a=0;

                if(imageArrayListUri.size()==1)
                {
                    a=cameraImagePickerHelper.getRotationFromGallery(getActivity(),imageArrayListUri.get(0));


                }else if(imageArrayListUri.size()>1) {
                    a=cameraImagePickerHelper.getRotationFromGallery(getActivity(),imageArrayListUri.get(position));
                }

                if(a==0)
                {
                    imageView.setRotation(360);
                }
                else if(a==90)
                {
                    imageView.setRotation(90);
                }
                else if(a==180)
                {
                    imageView.setRotation(180);
                }
                else if(a==270)
                {
                    imageView.setRotation(270);
                }
                else if(a==360)
                {
                    imageView.setRotation(360);
                }
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        builder.show();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (builder != null) {
                    if (builder.isShowing()) {
                        builder.dismiss();
                    }
                }
            }
        });

    }









   /* public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = getActivity().managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }*/

    public void onBackPress() {

        Utilities.getInstance(getActivity()).showAlert(getActivity(),
                new Utilities.onAlertOkListener() {
                    @Override
                    public void onOkButtonClicked(String tag) {
                        Intent intent=new Intent(getActivity(), WelcomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
//                        getActivity().setResult(RESULT_OK);
//                        getActivity().finish();
                    }
                }, Html.fromHtml(getResources().getString(R.string.rescan_msg)),
                getResources().getString(R.string.rescan_head), "No", "Yes", "Sync");

    }


    private void emitFormEvent(String fieldName, String fieldValue) {
        try {
            String qrCodeiD = Utilities.getInstance(getActivity()).getPreference(getActivity(), Constants.QRCODEID, "");

            JSONObject jsonObject = new JSONObject();
            String androidId = Utilities.getInstance(getActivity()).getPreference(getActivity(), Constants.ANDROID_ID, "");

            jsonObject.put(Constants.UNIQUE_ID, androidId);
            jsonObject.put(Constants.QRCODEID, qrCodeiD);
            jsonObject.put(Constants.FIELD_NAME, fieldName);
            jsonObject.put(Constants.FIELD_VALUE, fieldValue);
            if (socketHelper != null && socketHelper.socket.connected()) {
                socketHelper.emitData(SocketConstants.EVENT_CUSTOMER_FIELD, jsonObject);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        spinnerText.setText(documentsList.get(position).getIdentityValue());
        listclear();
        emitFormEvent("IdentityType", documentsList.get(position).getIdentityValue());
        if (position > 0) {
            IdentityTypeID = documentsList.get(position).getIdentityTypeID();
//            txtIdValue.setHint("");
//          etHint(null);
            String str = "Enter " + documentsList.get(position).getIdentityValue() + "<a> Number <font color='#FF0000'>*</font>";
//            txtIdValue.setPadding(0,0,0,0);
//            txtIdValue.setHint(Html.fromHtml(str));
//            idValue.setFocusable(true);
//            idValue.setEnabled(true);
//            idValue.setFocusableInTouchMode(true);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void setFocus() {
        isFocusSet = true;
        txtFirstName.setOnFocusChangeListener(this::onFocusChange);
        txtLastName.setOnFocusChangeListener(this::onFocusChange);
        txtAddressOne.setOnFocusChangeListener(this::onFocusChange);
        txtAddressTwo.setOnFocusChangeListener(this::onFocusChange);
        txtAddressThree.setOnFocusChangeListener(this::onFocusChange);
        txtContactNo.setOnFocusChangeListener(this::onFocusChange);
        txtCity.setOnFocusChangeListener(this::onFocusChange);
        txtPostCode.setOnFocusChangeListener(this::onFocusChange);
        txtstateCode.setOnFocusChangeListener(this::onFocusChange);
        spinner.setOnFocusChangeListener(this::onFocusChange);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        if (v.getId() != R.id.Email && v.getId() != R.id.ConfirmEmail) {
            String mail = email.getText().toString().trim();
            String confirm = txtConfirmEmail.getText().toString().trim();

            if (EmailValidator.getInstance().validate(mail.trim()) && EmailValidator.getInstance().validate(confirm.trim()) && mail.equalsIgnoreCase(confirm)) {
                if (!emailMain.equalsIgnoreCase(mail)) {
                    checkEmail(false);
                }

            }
//        if (!Utilities.EmailValidation(email, getString(R.string.error_valid_mail))||!Utilities.EmailValidation(txtConfirmEmail, getString(R.string.error_confirm_mail)) ||!Utilities.confirm(txtConfirmEmail,getString(R.string.error_confirm),mail,confirm) ) {
//
//        }
//        else{
//            checkEmail();
//        }
        } else {
            if (!isFocusSet) {
                setFocus();
            }
        }
    }

    public void removeFocus() {
        isFocusSet = false;
        txtFirstName.setOnFocusChangeListener(null);
        txtLastName.setOnFocusChangeListener(null);
        txtAddressOne.setOnFocusChangeListener(null);
        txtAddressTwo.setOnFocusChangeListener(null);
        txtAddressThree.setOnFocusChangeListener(null);
        txtContactNo.setOnFocusChangeListener(null);
        txtCity.setOnFocusChangeListener(null);
        txtPostCode.setOnFocusChangeListener(null);
        txtstateCode.setOnFocusChangeListener(null);
        spinner.setOnFocusChangeListener(null);
    }

    class CustomTextWatcher implements TextWatcher {
        private EditText mEditText;
        private String mFieldName;

        public CustomTextWatcher(EditText e, String fieldname) {
            mEditText = e;
            mFieldName = fieldname;
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {


            emitFormEvent((String) mFieldName, s.toString());

        }
    }


    public void checkEmail(boolean isSubmitBtnClicked) {


        progressBarMedium.setVisibility(View.VISIBLE);

        WebService webService = new WebService();

        APIInterface apiInterface = APIClient.getClient(getActivity()).create(APIInterface.class);

        webService.apiCall(apiInterface.checkEmail(saveEmailMap()), getActivity(), new WebServiceInterface<Response<SocketDataSync>>() {
            @Override
            public void apiResponse(Response<SocketDataSync> response) {

// if(progressBar.isShowing())
// {
// progressBar.dismiss();
// }

                if (response.isSuccessful()) {
                    isSecretCodeEntered = false;
                    isSecretCodeAlreadyGot = false;
                    if (response.body().getData().isStatus()) {
                        isAlredyRegistered = true;
                        getSecretCode(isSubmitBtnClicked);
//                        showPasswordDialog(true);
                    } else {
                        isAlredyRegistered = false;
                        isSecretCodeEntered = false;
// txtConfirmEmail.setEnabled(false);
// txtEmail.setEnabled(false);
                        clearTextData();
                    }

                    removeFocus();
                    emailMain = txtConfirmEmail.getText().toString().trim();


                } else {
                    fabricLog("save_deviceinfo_api_eroor", response.toString());
                }
                progressBarMedium.setVisibility(View.GONE);
            }

            @Override
            public void apiError(Response<SocketDataSync> response) {
                progressBarMedium.setVisibility(View.GONE);

            }

            @Override
            public void serverError(Throwable t) {
                progressBarMedium.setVisibility(View.GONE);
                if (t instanceof UnknownHostException) {
                    Toast.makeText(getActivity(), "There is something went wrong with your Internet Connection Please reset your Internet Connection and try again", Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    public void showPasswordDialog(boolean isRegistered, boolean isSubmitBtnClicked) {
        passDialog = new Dialog(getActivity());
        passDialog.setCancelable(true);
        passDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        passDialog.setContentView(R.layout.password_dialog_layoutas);
        dialogProgress = passDialog.findViewById(R.id.dialogProgressBar);
        EditText pass = passDialog.findViewById(R.id.passedtx);
        Button cancel = passDialog.findViewById(R.id.canclDb);
        TextView txt = passDialog.findViewById(R.id.dialogtxt);
        TextView resendPassword = passDialog.findViewById(R.id.resend_paswd);
        TextView dummytxt = passDialog.findViewById(R.id.dummytxt);
//          if(!isRegistered){
//              txt.setText(getResources().getString(R.string.txtSecretCode));
//              pass.setHint("Secret Code");
//          }
//          else{
//             // cancel.setVisibility(View.GONE);
//              //dummytxt.setVisibility(View.GONE);
//              resendPassword.setVisibility(View.VISIBLE);
//          }
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alredylogin = false;
                isLoogedIn = false;
                passDialog.cancel();
            }
        });
        Button enter = passDialog.findViewById(R.id.entrpass);


        resendPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (dialogProgress != null) {
                    dialogProgress.setVisibility(View.VISIBLE);
                }
                getSecretCode(isSubmitBtnClicked);
            }
        });


        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.getInstance(getActivity()).validation(pass, getResources().getString(R.string.error_secret))) {
                    if (!pass.getText().toString().equals("")) {
                        checkSecretCode(pass.getText().toString(), isSubmitBtnClicked);
                        //  passDialog.cancel();

                    }
                }
//                 if(isRegistered) {
//                     if(Utilities.getInstance(getActivity()).validation(pass,"Please Enter Password"))
//                     {
//                         if (!pass.getText().toString().equals("")) {
//                             checkSecretCode(pass.getText().toString(),isSubmitBtnClicked);
////                             checkPassword(pass.getText().toString());
//                         }
//                     }
//
//                 }
//                 else{
//                     if(Utilities.getInstance(getActivity()).validation(pass,"Please Enter Secret Code")) {
//                         if (!pass.getText().toString().equals("")) {
//                             checkSecretCode(pass.getText().toString(),isSubmitBtnClicked);
//                           //  passDialog.cancel();
//
//                         }
//                     }
//                 }
            }
        });


        passDialog.show();

    }

    private void HitResendPasswordApi() {

        WebService webService = new WebService();
        APIInterface apiInterface = APIClient.getClient(getActivity()).create(APIInterface.class);

        webService.apiCall(apiInterface.resendPassword(saveEmailMap()), getActivity(), new WebServiceInterface<Response<SocketDataSync>>() {
            @Override
            public void apiResponse(Response<SocketDataSync> response) {
                if (dialogProgress != null && dialogProgress.isShown()) {
                    dialogProgress.setVisibility(View.GONE);
                }
                if (response.isSuccessful()) {
                    if (response.body().getData().isSuccess()) {
                        Toast.makeText(getActivity(), response.body().getData().getEmailSent(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void apiError(Response<SocketDataSync> response) {
                if (dialogProgress != null && dialogProgress.isShown()) {
                    dialogProgress.setVisibility(View.GONE);
                }
            }

            @Override
            public void serverError(Throwable t) {
                if (dialogProgress != null && dialogProgress.isShown()) {
                    dialogProgress.setVisibility(View.GONE);
                }
                if (t instanceof UnknownHostException) {
                    Toast.makeText(getActivity(), "There is something went wrong with your Internet Connection Please reset your Internet Connection and try again", Toast.LENGTH_LONG).show();
                }
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }

    public void checkPassword(String password) {

        dialogProgress.setVisibility(View.VISIBLE);

        WebService webService = new WebService();
        APIInterface apiInterface = APIClient.getClient(getActivity()).create(APIInterface.class);

        webService.apiCall(apiInterface.clientLogin(saveLoginMap(password)), getActivity(), new WebServiceInterface<Response<SocketDataSync>>() {
            @Override
            public void apiResponse(Response<SocketDataSync> response) {

// if(progressBar.isShowing())
// {
// progressBar.dismiss();
// }

                if (response.isSuccessful()) {
// response.body().getData().
                    if (response.body().isSuccess()) {
                        if (response.body().getData().isValidUser()) {
                            isLoogedIn = true;
                            clearTextData();
                            setText(txtFirstName, response.body().getData().getName());
                            setText(txtFirstName, response.body().getData().getName());
                            setText(txtLastName, response.body().getData().getLastName());
                            setText(txtContactNo, response.body().getData().getContactNumber());
                            setText(txtPostCode, response.body().getData().getPostCode());
                            setText(txtAddressOne, response.body().getData().getAddress1());
                            setText(txtAddressTwo, response.body().getData().getAddress2());
                            setText(txtAddressThree, response.body().getData().getAddress3());
                            setText(txtCity, response.body().getData().getCity());
                            customer_id = response.body().getData().getCustomerID();


// setText(idValue,response.body().getData());
// txtEmail.setEnabled(false);
// txtConfirmEmail.setEnabled(false);
                            passDialog.cancel();
                            removeFocus();


                        }

                    } else {
                        Toast.makeText(getActivity(), "Wrong password", Toast.LENGTH_SHORT).show();


                    }
                }
                dialogProgress.setVisibility(View.GONE);
            }

            @Override
            public void apiError(Response<SocketDataSync> response) {
                Toast.makeText(getActivity(), "Wrong password", Toast.LENGTH_SHORT).show();
                dialogProgress.setVisibility(View.GONE);

            }

            @Override
            public void serverError(Throwable t) {
                dialogProgress.setVisibility(View.GONE);
                if (t instanceof UnknownHostException) {
                    Toast.makeText(getActivity(), "There is something went wrong with your Internet Connection Please reset your Internet Connection and try again", Toast.LENGTH_LONG).show();
                }

            }
        });


    }

    public void listclear() {
        imageArrayList.clear();
        imageArrayListUri.clear();
        value2.clear();
        for (int i = imagesLLayout.getChildCount() - 1; i >= 0; i--) {
            imagesLLayout.removeViewAt(i);
        }
        addImageView();
    }

    public void setText(EditText ed, String value) {
        if (value != null) {
            ed.setText(value);
        }

    }

    public void clearTextData() {
        txtFirstName.setText("");
        txtLastName.setText("");
        txtContactNo.setText("");
        txtPostCode.setText("");
        txtstateCode.setText("");
        txtAddressOne.setText("");
        txtAddressTwo.setText("");
        txtAddressThree.setText("");
        txtCity.setText("");
        txtPostCode.setText("");
        txtstateCode.setText("");
//        idValue.setText("");
        spinner.setSelection(0);
//        idValue.setEnabled(false);
//        idValue.setHint("");
//        idValue.setHint("Select Your ID");
        listclear();
//        filePath1="";
//        imageArrayList.clear();
//
//        for(int i=imagesLLayout.getChildCount()-1;i>=0;i--){
//            imagesLLayout.removeViewAt(i);
//        }
//        addImageView();
//        btnSelect.setVisibility(View.VISIBLE);
//        showImage.setVisibility(View.GONE);
//        crossimage.setVisibility(View.GONE);
        arrayAdapter.notifyDataSetChanged();
    }
//
//
//    public void showSecretCodeDialog(){
//
//        Dialog dialog=new Dialog(getActivity());
//        dialog.setCancelable(false);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.password_dialog_layout);
//        pass=dialog.findViewById(R.id.passedtx);
//        Button enter=dialog.findViewById(R.id.entrpass);
//        enter.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(!pass.getText().toString().equals("")){
//                    checkPassword(pass.getText().toString());
//                }
//            }
//        });
//
//
//
//    }

    public void getSecretCode(boolean isSubmitBtnClicked) {
        progressBarMedium.setVisibility(View.VISIBLE);


        WebService webService = new WebService();

        APIInterface apiInterface = APIClient.getClient(getActivity()).create(APIInterface.class);

        webService.apiCall(apiInterface.getSecretCode(saveEmailMap()), getActivity(), new WebServiceInterface<Response<SocketDataSync>>() {
            @Override
            public void apiResponse(Response<SocketDataSync> response) {

                if (response.isSuccessful()) {
                    if (response.body().getData().isStatus()) {
                        Toast.makeText(getActivity(),getResources().getText(R.string.resendpassword),Toast.LENGTH_LONG).show();
                        isSecretCodeAlreadyGot = true;
                        if (passDialog == null || !passDialog.isShowing())
                            showPasswordDialog(false, isSubmitBtnClicked);

                    }


                } else {
                    fabricLog("save_deviceinfo_api_eroor", response.toString());
                    Toast.makeText(getActivity(),response.body().getData().getMessage(),Toast.LENGTH_LONG).show();
                }
                progressBarMedium.setVisibility(View.GONE);
                if (dialogProgress != null) {
                    dialogProgress.setVisibility(View.GONE);
                }
            }

            @Override
            public void apiError(Response<SocketDataSync> response) {
                progressBarMedium.setVisibility(View.GONE);

            }

            @Override
            public void serverError(Throwable t) {
                progressBarMedium.setVisibility(View.GONE);
                if (t instanceof UnknownHostException) {
                    Toast.makeText(getActivity(), "There is something went wrong with your Internet Connection Please reset your Internet Connection and try again", Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    public void checkSecretCode(String secretCode, boolean isSubmitBtnClicked) {
        dialogProgress.setVisibility(View.VISIBLE);

        WebService webService = new WebService();
        APIInterface apiInterface = APIClient.getClient(getActivity()).create(APIInterface.class);

        webService.apiCall(apiInterface.checkSecretCode(saveSecretCodeMap(secretCode)), getActivity(), new WebServiceInterface<Response<SocketDataSync>>() {
            @Override
            public void apiResponse(Response<SocketDataSync> response) {



                if (response.isSuccessful()) {
                    if (response.body().isSuccess()) {
                        if (response.body().getData().isStatus()) {
                            isSecretCodeEntered = true;
                            if (passDialog.isShowing()) {
                                passDialog.cancel();
                            }
                            if (response.body().getData().getCustomerDetail() != null) {
                                isLoogedIn = true;
                                clearTextData();
                                setText(txtFirstName, response.body().getData().getCustomerDetail().getFirstName());
                                setText(txtLastName, response.body().getData().getCustomerDetail().getLastName());
                                setText(txtContactNo, response.body().getData().getCustomerDetail().getContactNumber());
                                setText(txtPostCode, response.body().getData().getCustomerDetail().getPostCode());
                                setText(txtAddressOne, response.body().getData().getCustomerDetail().getAddress1());
                                setText(txtAddressTwo, response.body().getData().getCustomerDetail().getAddress2());
                                setText(txtAddressThree, response.body().getData().getCustomerDetail().getAddress3());
                                setText(txtCity, response.body().getData().getCustomerDetail().getCity());
                                setText(txtstateCode,response.body().getData().getCustomerDetail().getStateCode());

                                customer_id = Integer.valueOf(response.body().getData().getCustomerDetail().getCustomerID());
                                passDialog.cancel();
                                removeFocus();


                            }

                            if (isSubmitBtnClicked) {
                                submitInfo();
                            }
                        }
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.incorect_code), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    fabricLog("save_deviceinfo_api_eroor", response.toString());
                }
                dialogProgress.setVisibility(View.GONE);
            }

            @Override
            public void apiError(Response<SocketDataSync> response) {
                Toast.makeText(getActivity(), getResources().getString(R.string.incorect_code), Toast.LENGTH_SHORT).show();
              //  Toast.makeText(getActivity(), getResources().getString(R.string.api_error_toast), Toast.LENGTH_SHORT).show();
                dialogProgress.setVisibility(View.GONE);

            }

            @Override
            public void serverError(Throwable t) {
                dialogProgress.setVisibility(View.GONE);
                if (t instanceof UnknownHostException) {
                    Toast.makeText(getActivity(), "There is something went wrong with your Internet Connection Please reset your Internet Connection and try again", Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    private HashMap<String, Object> saveLoginMap(String password) {
        HashMap<String, Object> map = new HashMap<>();
        try {

            map.put("UserName", txtConfirmEmail.getText().toString());
            map.put("Password", password);

        } catch (Exception E) {
            E.printStackTrace();
        }
        return map;
    }

    private HashMap<String, Object> saveEmailMap() {
        HashMap<String, Object> map = new HashMap<>();
        try {
            map.put("EmailAddress", txtConfirmEmail.getText().toString());
            map.put("EnterprisePartnerID", Utilities.getInstance(getActivity()).getPreference(getActivity(), Constants.ENTERPRISEPATNERID, ""));

        } catch (Exception E) {
            E.printStackTrace();
        }
        return map;
    }


    private HashMap<String, Object> saveSecretCodeMap(String secretCode) {
        HashMap<String, Object> map = new HashMap<>();
        try {
            map.put("EmailAddress", txtConfirmEmail.getText().toString());
            map.put("SecretCode", secretCode);
            map.put("IsValidate", "true");
            if (isAlredyRegistered) {
                map.put("IsLogin", "true");
            } else {
                map.put("IsLogin", "false");
            }
            map.put("EnterprisePartnerID", Utilities.getInstance(getActivity()).getPreference(getActivity(), Constants.ENTERPRISEPATNERID, ""));
        } catch (Exception E) {
            E.printStackTrace();
        }
        return map;
    }


    /**
     * Async task for loading the images from the SD card.
     *
     * @author Android Example
     */

    // Class with extends AsyncTask class
    public void LoadImagesFromSDCard() {


        filePath1 = Path;
        File file = null;
        try {
            file = FileUtilss.getCompressed(getActivity(), Path, "defult");
            filePath1 = file.getPath();
        } catch (IOException e) {
            e.printStackTrace();
        }

        imageArrayList.add(indexOfMyView2, filePath1);
        setImage(true);


    }


    public void addImageView() {
        View layout2 = LayoutInflater.from(getActivity()).inflate(R.layout.image_list_item_layout, imagesLLayout, false);

        imagesLLayout.addView(layout2);
        TextView selectImg = layout2.findViewById(R.id.select_image_item);
        ImageView showImg = layout2.findViewById(R.id.show_image_item);
        ImageView closeImg = layout2.findViewById(R.id.btn_close_item);
        selectImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View fl = ((FrameLayout) view.getParent());
                indexOfMyView2 = ((LinearLayout) fl.getParent()).indexOfChild(fl);
                selectImage();
            }
        });
        closeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View fl = ((FrameLayout) view.getParent());
                indexOfMyView2 = ((LinearLayout) fl.getParent()).indexOfChild(fl);
                showImg.setVisibility(View.GONE);
                selectImg.setVisibility(View.VISIBLE);
                closeImg.setVisibility(View.GONE);
                imageArrayList.remove(indexOfMyView2);

                if(!value2.get(indexOfMyView2)) {
                    if (imageArrayListUri != null && imageArrayListUri.size() >= 1) {
                        if (imageArrayListUri.size() == 1) {
                            imageArrayListUri.remove(0);
                            //  value2.remove(0);
                        } else {
                            imageArrayListUri.remove(indexOfMyView2);
                            // value2.remove(indexOfMyView2);
                        }
                    }
                }
                value2.remove(indexOfMyView2);
                imagesLLayout.removeViewAt(indexOfMyView2);
                if (indexOfMyView2 == 0 && imageArrayList.size() == 0) {

                } else {
                    addImageView();

                }



                filePath1 = "";

            }
        });

        showImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View fl = ((RelativeLayout) view.getParent());
                View f = (FrameLayout) fl.getParent();

                int position = ((LinearLayout) f.getParent()).indexOfChild(f);
                Log.e("TTYY", String.valueOf(position));
                showImage(position,value2);
            }
        });

    }


    public void setImage(boolean isCamera) {
        FrameLayout fm = (FrameLayout) imagesLLayout.getChildAt(indexOfMyView2);
        ImageView im = fm.findViewById(R.id.show_image_item);
        TextView select = fm.findViewById(R.id.select_image_item);
        ImageView cross = fm.findViewById(R.id.btn_close_item);
        value2.add(indexOfMyView2,isCamera);
        Log.d("valuesssssssss", String.valueOf(indexOfMyView2));
        if (!isCamera) {
            CameraImagePickerHelper cameraImagePickerHelper = new CameraImagePickerHelper();
            File file=new File(imageArrayList.get(indexOfMyView2));

            Uri uri = Uri.fromFile(file);
            Bitmap bitmap = null;
            try {
                bitmap = cameraImagePickerHelper.handleSamplingAndRotationBitmap(getActivity(), uri,null);
                int a=cameraImagePickerHelper.getRotationFromGallery(getActivity(),selectedImage);
                if(a==0)
                {
                    im.setRotation(360);
                }
                else if(a==90)
                {
                    im.setRotation(90);
                }
                else if(a==180)
                {
                    im.setRotation(180);
                }
                else if(a==270)
                {
                    im.setRotation(270);
                }
                else if(a==360)
                {
                    im.setRotation(360);
                }

                im.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            CameraImagePickerHelper cameraImagePickerHelper = new CameraImagePickerHelper();
            try {
                Bitmap bitmap = cameraImagePickerHelper.handleSamplingAndRotationBitmap(getActivity(), imageUri,getActivity());

                im.setImageBitmap(bitmap);
                imageArrayList.add(indexOfMyView2, cameraImagePickerHelper.storeImage(bitmap, getContext()).getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        select.setVisibility(View.GONE);
        im.setVisibility(View.VISIBLE);
        cross.setVisibility(View.VISIBLE);
        if (indexOfMyView2 == 0) {
            addImageView();
        }


    }


    public void getDocumentsType() {

        WebService webService = new WebService();
        APIInterface apiInterface = APIClient.getClient(getActivity()).create(APIInterface.class);

        webService.apiCall(apiInterface.getDocumentsType(Utilities.getInstance(getActivity()).getPreference(getActivity(), Constants.ENTERPRISEPATNERID, "")), getActivity(), new WebServiceInterface<Response<SocketDataSync>>() {
            @Override
            public void apiResponse(Response<SocketDataSync> response) {
                if (response.body().isSuccess()) {
                    for (int i = 0; i < response.body().getData().getDocumentsTypeList().size(); i++) {
                        DocumentsTypePojo documentsTypePojo1 = new DocumentsTypePojo();
                        documentsTypePojo1.setIdentityValue(response.body().getData().getDocumentsTypeList().get(i).getIdentityValue());
                        documentsTypePojo1.setIdentityTypeID(response.body().getData().getDocumentsTypeList().get(i).getIdentityTypeID());
                        documentsList.add(documentsTypePojo1);

                    }
                    arrayAdapter.notifyDataSetChanged();
                }


            }

            @Override
            public void apiError(Response<SocketDataSync> response) {

            }

            @Override
            public void serverError(Throwable t) {

            }
        });


    }


    private Uri getImageUri() {
        Uri m_imgUri = null;
        try {
            SimpleDateFormat m_sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String m_curentDateandTime = m_sdf.format(new Date());
            String m_imagePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/Android/data/" + getActivity().getPackageName() + "/TempFiles" + File.separator + m_curentDateandTime + ".jpg";
            File m_file = new File(m_imagePath);
            m_imgUri = Uri.fromFile(m_file);
        } catch (Exception p_e) {
        }
        return m_imgUri;
    }


    public void deleteDirectory() {


        File dir1 = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/Android/data/" + getActivity().getPackageName() + "/TempFiles");
        if(dir1.isDirectory())

        {
            String[] children = dir1.list();
            for (int i = 0; i < children.length; i++) {
                new File(dir1, children[i]).delete();
            }
        }
    }




    public void validateAddressApi(boolean isSubmit) {

        progressBarMedium.setVisibility(View.VISIBLE);


        WebService webService = new WebService();

        APIInterface apiInterface = APIClient.getClient(getActivity()).create(APIInterface.class);

        webService.apiCall(apiInterface.validateAddress(checkAddressMap()), getActivity(), new WebServiceInterface<Response<SocketDataSync>>() {
            @Override
            public void apiResponse(Response<SocketDataSync> response) {

                if (response.isSuccessful()) {
                    if (response.body().getData().isSuccess()) {

                        if(isSubmit){
                            submitInfo();
                        }else {
                            if (!isSecretCodeAlreadyGot) {
                                getSecretCode(true);
                            } else {
                                showPasswordDialog(false, true);
                            }
                        }


                    }
                    else{
                        Toast.makeText(getActivity(),response.body().getMessage(),Toast.LENGTH_LONG).show();

                    }


                } else {
                    Toast.makeText(getActivity(),response.body().getMessage(),Toast.LENGTH_LONG).show();
                }
                progressBarMedium.setVisibility(View.GONE);
                if (dialogProgress != null) {
                    dialogProgress.setVisibility(View.GONE);
                }
            }

            @Override
            public void apiError(Response<SocketDataSync> response) {
                progressBarMedium.setVisibility(View.GONE);
                Toast.makeText(getActivity(),response.body().getMessage(),Toast.LENGTH_LONG).show();

            }

            @Override
            public void serverError(Throwable t) {
                progressBarMedium.setVisibility(View.GONE);
                if (t instanceof UnknownHostException) {
                    Toast.makeText(getActivity(), "There is something went wrong with your Internet Connection Please reset your Internet Connection and try again", Toast.LENGTH_LONG).show();
                }
            }
        });


    }


    private HashMap<String, Object> checkAddressMap() {
        HashMap<String, Object> map = new HashMap<>();
        try {
            map.put("City", txtCity.getText().toString());
            map.put("State", txtstateCode.getText().toString());
            map.put("PostCode", txtPostCode.getText().toString());

        } catch (Exception E) {
            E.printStackTrace();
        }
        return map;
    }

}