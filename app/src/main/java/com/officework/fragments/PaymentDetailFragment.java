package com.officework.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.officework.ApiController.APIClient;
import com.officework.ApiController.APIInterface;
import com.officework.ApiController.WebService;
import com.officework.ApiController.WebServiceInterface;
import com.officework.R;
import com.officework.base.BaseFragment;
import com.officework.constants.Constants;
import com.officework.constants.FragmentTag;
import com.officework.constants.JsonTags;
import com.officework.customViews.FloatLabeledEditText;
import com.officework.models.DataPojo;
import com.officework.models.DeclarationTest;
import com.officework.models.PaymentTypePojo;
import com.officework.models.SocketDataSync;
import com.officework.testing_profiles.ui.fragment.RecieptInformationScreenFragment;
import com.officework.utils.FileUtilss;
import com.officework.utils.Utilities;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;

import static com.officework.testing_profiles.ui.fragment.CustomerInformationFragment.hideKeyboard;

/**
 * A simple {@link Fragment} subclass.
 */
public class PaymentDetailFragment extends BaseFragment implements View.OnClickListener {

    LinearLayout linearLayout;
    ArrayList<View> views;
    View view;
    ArrayList<PaymentTypePojo> paymentTypePojoArrayList;
    LinearLayout fieldsLayout,btnLayout;
    PaymentTypePojo selectedPaymentMode;
    Button nextButton,backButton;
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
    DataPojo dataPojo;
    ArrayList<DeclarationTest> declarationTestArrayList;
    ProgressBar progressBarMedium;
    boolean done=true;
    String refrenceCode;
    String customNote;
    String sortCode;
    String customerAccountNumber;
    String bankName;
    String customerSortCode;
    TextView amount;

    String pay_EmailAddress;
    String pay_Name;
    String pay_Address;
    String pay_MobileNumber;


    private final String paypal="Paypal";
    private String paypalRefField="<a>Reference Code <font color='#FF0000'> *</font>";
    private String paypalNoteField="Custom Note";
    private String beneficiary_email1="<a>Beneficiary Email <font color='#FF0000'> *</font>";

    private final String bankPayment="Bank Payment";
    private String beneficiary_adress="<a>Beneficiary Address <font color='#FF0000'> *</font>";
    private String customerName="<a>Customer Name <font color='#FF0000'> *</font>";


    private String bankAcctNoField="<a>Beneficiary Account Number <font color='#FF0000'> *</font>";
    private String bankNameField="<a>Beneficiary Bank Name <font color='#FF0000'> *</font>";
    private String bankRefVoField="<a>Payment Reference Number <font color='#FF0000'> *</font>";
    private String bankSortCodeField="<a>Beneficiary Sort Code <font color='#FF0000'> *</font>";

    private final String cheque="Cheque";
    private String benificary_bank_name="<a>Beneficiary Bank Name <font color='#FF0000'> *</font>";

    private String chequeAcctNoField="<a>Customer Account Number <font color='#FF0000'> *</font>";
    private String chequeBankNameField="<a>Customer  Name <font color='#FF0000'> *</font>";
    private String chequeRefNoField="<a>Reference Code <font color='#FF0000'> *</font>";
    private String chequeNoteField="Custom Note";

    private final String giftVoucher="Gift Voucher";

    private String gvRefNoField="<a>Reference Code <font color='#FF0000'> *</font>";
    private String gvNoteField="Custom Note";

    private String beneficiary_email="<a>Beneficiary Email <font color='#FF0000'> *</font>";
    private final String billPayment="Bill Payment";
    private String billRefNoField="<a>Reference Code <font color='#FF0000'> *</font>";
    private String billNoteField="<a>Notes <font color='#FF0000'> *</font>";
    private String beneficiary_number="<a>Beneficiary Mobile Number <font color='#FF0000'> *</font>";

    public static ProgressDialog progressBar;






    public PaymentDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         view =inflater.inflate(R.layout.fragment_payment_detail, container, false);
//        chipGroup=view.findViewById(R.id.chipGrp);
        if (getArguments() != null) {
            dataPojo =  getArguments().getParcelable("OFFER_PRICE_OBJECT");
            //declarationTestArrayList = new ArrayList<>(dfslist);
            getFullBundel();
        }
        linearLayout=view.findViewById(R.id.llayt);
        fieldsLayout=view.findViewById(R.id.fieldsLyt);
        btnLayout=view.findViewById(R.id.btnlyt);
        nextButton=view.findViewById(R.id.submit);
        progressBarMedium=view.findViewById(R.id.progressBarMediuma);
        amount=view.findViewById(R.id.order_amount);
        amount.setText(offer_price);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                next();
            }
        });
        backButton=view.findViewById(R.id.cancel);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPress();
            }
        });

        views=new ArrayList<>();
       getPaymentTypes();
        if(getActivity()!=null)
            hideKeyboard(getActivity());
//        addArrayTest();
//        for(int i=0;i<arrayList.size();i++){
//            addChip(arrayList.get(i));
//        }
        // Inflate the layout for this fragment
        return view;
    }

    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        return null;
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
        declarationTestArrayList=getArguments().getParcelableArrayList("Declarations");




    }
    public void addChip(String chipName){

        TextView textView=new TextView(getActivity());
        textView.setBackground(getResources().getDrawable(R.drawable.rectangle_border_drawable));
        textView.setTextSize(15);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(15,15,15,15);
        textView.setLayoutParams(params);
        textView.setText(chipName);
        textView.setSingleLine(true);
        textView.setTag(chipName);
        textView.setOnClickListener(this);

        views.add(textView);

    }


    private void populateText(LinearLayout ll, ArrayList<View> views , Context mContext) {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        ll.removeAllViews();
        int maxWidth = display.getWidth() - 20;

        LinearLayout.LayoutParams params;
        LinearLayout newLL = new LinearLayout(mContext);
        newLL.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        newLL.setGravity(Gravity.LEFT);
        newLL.setOrientation(LinearLayout.HORIZONTAL);

        int widthSoFar = 0;

        for (int i = 0 ; i < views.size() ; i++ ){
            LinearLayout LL = new LinearLayout(getActivity());
            LL.setOrientation(LinearLayout.HORIZONTAL);
            LL.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM);
            LinearLayout.LayoutParams ll1paramsv = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ll1paramsv.setMargins(15,15,15,15);
            LL.setLayoutParams(ll1paramsv);

            LinearLayout.LayoutParams paramsv = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            paramsv.setMargins(15,0,15,0);
            views.get(i).setLayoutParams(paramsv);
            views.get(i).measure(0,0);
            params = new LinearLayout.LayoutParams(views.get(i).getMeasuredWidth(),
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            //params.setMargins(5, 0, 5, 0);  // YOU CAN USE THIS
            //LL.addView(TV, params);
            LL.addView(views.get(i), paramsv);
            LL.measure(0, 0);
            widthSoFar += views.get(i).getMeasuredWidth()+80;// YOU MAY NEED TO ADD THE MARGINS
            if (widthSoFar >= maxWidth) {
                ll.addView(newLL);
                LinearLayout.LayoutParams ll2paramsv = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                ll2paramsv.setMargins(15,15,15,15);
                newLL = new LinearLayout(getActivity());
                newLL.setLayoutParams(ll2paramsv);
                newLL.setOrientation(LinearLayout.HORIZONTAL);
                newLL.setGravity(Gravity.CENTER);
                params = new LinearLayout.LayoutParams(LL
                        .getMeasuredWidth(), LL.getMeasuredHeight());
                newLL.addView(LL, params);
                widthSoFar = LL.getMeasuredWidth();
            } else {
                newLL.addView(LL);
            }
        }
        ll.addView(newLL);
    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        if(selectedPaymentMode==null || !v.getTag().toString().equals(selectedPaymentMode.getPaymentType())) {
            for (int i = 0; i < paymentTypePojoArrayList.size(); i++) {
                if (v.getTag().toString().equals(paymentTypePojoArrayList.get(i).getPaymentType())) {
                    selectItem(paymentTypePojoArrayList.get(i).getPaymentType());
                    return;
                }

            }
        }
    }

    public void selectItem(String selctedItem){

        for(int i=0;i<paymentTypePojoArrayList.size();i++){

            TextView textView=view.findViewWithTag(paymentTypePojoArrayList.get(i).getPaymentType());
            if(paymentTypePojoArrayList.get(i).getPaymentType().equals(selctedItem)) {
                selectedPaymentMode=paymentTypePojoArrayList.get(i);
                btnLayout.setVisibility(View.VISIBLE);
                textView.setTextColor(getResources().getColor(R.color.app_blue_color));
                textView.setBackground(getResources().getDrawable(R.drawable.rectangle_border_blue));
                addCorrsFields(paymentTypePojoArrayList.get(i).getPaymentType());
            }else{
                textView.setTextColor(getResources().getColor(R.color.gray_color));
                textView.setBackground(getResources().getDrawable(R.drawable.rectangle_border_drawable));
            }
        }

    }


    public void getPaymentTypes(){

        WebService webService = new WebService();
        APIInterface apiInterface = APIClient.getClient(getActivity()).create(APIInterface.class);



        progressBar = new ProgressDialog(getActivity());
        try{
            progressBar.show();
        }catch (Exception e){
            e.printStackTrace();
        }
        progressBar.getWindow()
                .setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressBar.setContentView(R.layout.progressdialog);
        progressBar.setCancelable(false);
        progressBar.setIndeterminate(true);
        progressBar.show();








        webService.apiCall(apiInterface.getPaymentModes(paymentMap()), getActivity(), new WebServiceInterface<Response<SocketDataSync>>() {
            @Override
            public void apiResponse(Response<SocketDataSync> response) {
              paymentTypePojoArrayList=new ArrayList<>();
              paymentTypePojoArrayList.clear();
                for(int i=0;i<response.body().getData().getPaymentTypeList().size();i++){
                    PaymentTypePojo paymentTypePojo=new PaymentTypePojo();
                    paymentTypePojo.setPaymentConfigrationId(response.body().getData().getPaymentTypeList().get(i).getPaymentConfigrationId());
                    paymentTypePojo.setPaymentType(response.body().getData().getPaymentTypeList().get(i).getPaymentType());
                    paymentTypePojo.setPaymentTypeId(response.body().getData().getPaymentTypeList().get(i).getPaymentTypeId());
                    paymentTypePojoArrayList.add(paymentTypePojo);
                    addChip(paymentTypePojoArrayList.get(i).getPaymentType());

                    if (progressBar.isShowing()) {
                        progressBar.dismiss();

                    }
                }
                populateText(linearLayout,views,getActivity());

                selectItem(paymentTypePojoArrayList.get(0).getPaymentType());



            }

            @Override
            public void apiError(Response<SocketDataSync> response) {
                Toast.makeText(getActivity(), getResources().getString(R.string.api_error_toast), Toast.LENGTH_LONG).show();
                if (progressBar.isShowing()) {
                    progressBar.dismiss();

                }
                onBackPress();


            }

            @Override
            public void serverError(Throwable t) {
                if (progressBar.isShowing()) {
                    progressBar.dismiss();

                }
                if (t instanceof UnknownHostException) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.internet_error_toast), Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.api_error_toast), Toast.LENGTH_LONG).show();
                }
                onBackPress();
            }
        });

    }
    private HashMap<String, Object> paymentMap() {
        HashMap<String, Object> map = new HashMap<>();
        try {

//            map.put("EnterprisePartnerID",  1184);

            map.put("EnterprisePartnerID", Utilities.getInstance(getActivity()).getPreference(getActivity(), Constants.ENTERPRISEPATNERID, ""));

        } catch (Exception E) {
            E.printStackTrace();
        }
        return map;
    }


    public void addFields(String hint){

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.paymentfield_layout,null);


        FloatLabeledEditText floatLabeledEditText=(FloatLabeledEditText)view.findViewById(R.id.field_prent);

        EditText editText=(EditText)view.findViewById(R.id.field_child);



//        floatLabeledEditText.setHint(Html.fromHtml(hint));
        floatLabeledEditText.setHint(Html.fromHtml(hint));
        editText.setTag(hint+"edtxt");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(15,15,15,15);
        floatLabeledEditText.setLayoutParams(params);
        if(hint.contains("Reference Code ") || hint.contains("Payment Reference Number ")){
            String refCode=getResources().getString(R.string.creditfordevice)+Utilities.getInstance(getActivity()).getSecurePreference(getActivity(), JsonTags.MMR_1.name(),"");
            editText.setText(refCode);
//            editText.setEnabled(false);
        }
        if(hint.contains("Beneficiary Email")){
            editText.setText(email);
        }
        if(hint.contains("Customer Name")){
            editText.setText(first_name+" "+last_name);
        }
        if(hint.contains("Beneficiary Address")){
            editText.setText(adres1+" "+adres2+" "+adres3);

        }
        if(hint.contains("Beneficiary Mobile Number")){
            editText.setText(contact_number);
        }
        //floatLabeledEditText.addView(editText);
        fieldsLayout.addView(view);



    }



    public void addCorrsFields(String type){
        fieldsLayout.removeAllViews();

        switch (type){

            case paypal:
                addFields(beneficiary_email1);
                addFields(paypalRefField);
                addFields(paypalNoteField);

                break;

            case bankPayment:
                addFields(bankAcctNoField);
                addFields(bankNameField);
                addFields(bankRefVoField);
                addFields(bankSortCodeField);
                addFields(beneficiary_adress);
                addFields(customerName);
                break;

            case cheque:
                addFields(chequeAcctNoField);
                addFields(chequeBankNameField);
                addFields(chequeRefNoField);
                addFields(chequeNoteField);
                addFields(benificary_bank_name);
                break;

            case giftVoucher:
                addFields(beneficiary_email);
                addFields(gvRefNoField);
                addFields(gvNoteField);
                break;

            case billPayment:
                addFields(billRefNoField);
                addFields(billNoteField);
                addFields(beneficiary_number);
                break;

        }



    }


    public void next(){

        clearStrings();
        switch (selectedPaymentMode.getPaymentType()) {

            case paypal:

                EditText refrenceEdittxtp = view.findViewWithTag(paypalRefField + "edtxt");
                EditText noteEdittxtp = view.findViewWithTag(paypalNoteField + "edtxt");
                EditText Pay_EmailAddress = view.findViewWithTag(beneficiary_email1 + "edtxt");


                if (!Utilities.EmailValidation(Pay_EmailAddress, getString(R.string.errorbenificiarymail))) {

                }
               else {
                if (!Utilities.validation(refrenceEdittxtp, getString(R.string.error_refrence_code))) {
                } else {
                    refrenceCode = refrenceEdittxtp.getText().toString();
                    customNote = noteEdittxtp.getText().toString();
                    pay_EmailAddress = Pay_EmailAddress.getText().toString();
                    submitResult();

                    Log.w(refrenceCode,refrenceCode);
                    Log.w(customNote,customNote);
                    Log.w(pay_EmailAddress,pay_EmailAddress);

                }
        }
                break;

            case bankPayment:
                EditText accountNumber=view.findViewWithTag(bankAcctNoField+"edtxt");
                EditText bankNameEdittxt=view.findViewWithTag(bankNameField+"edtxt");
                EditText refrenceNumberEdittxt=view.findViewWithTag(bankRefVoField+"edtxt");
                EditText sortEdittxt=view.findViewWithTag(bankSortCodeField+"edtxt");
                EditText Pay_Name=view.findViewWithTag(customerName+"edtxt");
                EditText Pay_Address=view.findViewWithTag(beneficiary_adress+"edtxt");

               // Pay_Name,Pay_Address



                if (  !Utilities.validation(accountNumber, getString(R.string.error_account_number))
                        || !Utilities.validation(bankNameEdittxt, getString(R.string.error_bank_name))
                        || !Utilities.validation(refrenceNumberEdittxt, getString(R.string.error_refrence_number))
                        || !Utilities.validation(sortEdittxt, getString(R.string.error_sort_code))
                        || !Utilities.validation(Pay_Name, getString(R.string.paynameerror))
                        || !Utilities.validation(Pay_Address, getString(R.string.error_address_one))

                ) {
                }
                else{
                    customerAccountNumber=accountNumber.getText().toString();
                    bankName=bankNameEdittxt.getText().toString();
                    refrenceCode=refrenceNumberEdittxt.getText().toString();
                    customerSortCode=sortEdittxt.getText().toString();
                        pay_Name=Pay_Name.getText().toString();
                      pay_Address=Pay_Address.getText().toString();

                    submitResult();

                    Log.w(customerAccountNumber,customerAccountNumber);
                    Log.w(bankName,bankName);
                    Log.w(refrenceCode,refrenceCode);
                    Log.w(customerSortCode,customerSortCode);
                    Log.w(pay_Name,pay_Name);
                    Log.w(pay_Address,pay_Address);

                }

                break;

            case cheque:
                EditText customeraccountNumber=view.findViewWithTag(chequeAcctNoField+"edtxt");

                EditText customerBankNameEdittxt=view.findViewWithTag(chequeBankNameField+"edtxt");
                EditText refrenceCodeEdittxt=view.findViewWithTag(chequeRefNoField+"edtxt");
                EditText customNoteEdittext=view.findViewWithTag(chequeNoteField+"edtxt");
                EditText Pay_Name1=view.findViewWithTag(benificary_bank_name+"edtxt");
                if ( !Utilities.validation(customeraccountNumber, getString(R.string.eerror_customer_account_number))
                        || !Utilities.validation(customerBankNameEdittxt, getString(R.string.payname))
                        || !Utilities.validation(refrenceCodeEdittxt, getString(R.string.error_refrence_code))
                        || !Utilities.validation(Pay_Name1, getString(R.string.benebank))
                ) {
                }
                else{
                    customerAccountNumber=customeraccountNumber.getText().toString();
                    bankName=customerBankNameEdittxt.getText().toString();
                    refrenceCode=refrenceCodeEdittxt.getText().toString();
                    customNote=customNoteEdittext.getText().toString();
                    pay_Name=Pay_Name1.getText().toString();
                    submitResult();

                    Log.w(customerAccountNumber,customerAccountNumber);
                    Log.w(bankName,bankName);
                    Log.w(refrenceCode,refrenceCode);
                    Log.w(customNote,customNote);
                    Log.w(pay_Name,pay_Name);


                }

                break;

            case giftVoucher:
                boolean emailvalid1=false;
                EditText refrenceEdittxtv=view.findViewWithTag(gvRefNoField+"edtxt");
                EditText noteEdittxtv=view.findViewWithTag(gvNoteField+"edtxt");
                EditText payemail=view.findViewWithTag(beneficiary_email+"edtxt");

                if(!Utilities.EmailValidation(payemail, getString(R.string.error_mail)))
            {
            }else {

                    if (!Utilities.validation(refrenceEdittxtv, getString(R.string.error_refrence_code))) {
                    } else {
                        refrenceCode = refrenceEdittxtv.getText().toString();
                        customNote = noteEdittxtv.getText().toString();
                        pay_EmailAddress = payemail.getText().toString();
                        submitResult();
                        Log.w(refrenceCode,refrenceCode);
                        Log.w(customNote,customNote);
                        Log.w(pay_EmailAddress,pay_EmailAddress);

                    }
                }
                break;

            case billPayment:

//                EditText mobileNumberEdittxt=view.findViewWithTag("Mobile Number"+"edtxt");
                EditText refrenceCodeEdittext=view.findViewWithTag(billRefNoField+"edtxt");
                EditText notesEdittext=view.findViewWithTag(billNoteField+"edtxt");
                EditText benificarynumber=view.findViewWithTag(beneficiary_number+"edtxt");
                if (  !Utilities.validation(refrenceCodeEdittext, getString(R.string.error_refrence_code))
                        || !Utilities.validation(notesEdittext, getString(R.string.error_note))

                        || !Utilities.validation(benificarynumber, getString(R.string.errorbenificiarynumber))
                ) {
                }
                else{
                    refrenceCode=refrenceCodeEdittext.getText().toString();
                    customNote=notesEdittext.getText().toString();
                   pay_MobileNumber=benificarynumber.getText().toString();
                    submitResult();
                    Log.w(refrenceCode,refrenceCode);
                    Log.w(customNote,customNote);
                    Log.w(pay_MobileNumber,pay_MobileNumber);

                }


                break;

        }


    }



    public void submitResult() {
//        File file = new File(filepath);
//        try {
//            file = FileUtilss.getCompressed(getActivity(),filepath);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
//        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("image", file.getName(), requestBody);
//        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                      WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        progressBarMedium.setVisibility(View.VISIBLE);

        MultipartBody.Part[] imagesToUpload = new MultipartBody.Part[imagesList.size()];
        RequestBody filename=null;



        for (int index = 0; index < imagesList.size(); index++) {

            try {
                Log.d("NNNN", "requestUploadSurvey: survey image " + index + " " + imagesList.get(index));
// File file = new File(imagesList.get(index));
                File file = FileUtilss.getCompressed(getActivity(),imagesList.get(index),index+"_");
                RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
                imagesToUpload[index] = MultipartBody.Part.createFormData("image", file.getName(), requestBody);
                filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());
            } catch (Exception e) {
                e.printStackTrace();

            }
        }




        APIInterface apiInterface =
                APIClient.getClient(getActivity()).create(APIInterface.class);

// HashMap<String, Object> map = createMap();
// Call<SocketDataSync> call1 = apiInterface.submitInfo(map,fileToUpload, filename);

        Utilities utils=Utilities.getInstance(getActivity());
        WebService webService=new WebService();
        HashMap<String,ArrayList<DeclarationTest>> map1 = new HashMap<>();
        map1.put("Declarations",declarationTestArrayList);
        webService.apiCall(apiInterface.submitInfo(Utilities.getInstance(getActivity()).getPreference(getActivity(),
                Constants.QRCODEID, ""),
                Utilities.getInstance(getActivity()).getPreference(getActivity(),
                        Constants.ANDROID_ID, ""), order_id, udi_id, first_name, "", last_name
                , customer_id, email, contact_number, adres1, dataPojo.getPercentageDeduction(), adres2, adres3,
                post_code, city, IdentityTypeID, dataPojo.getMcheckInformationID(),
                dataPojo.getQuotedPrice(), utils.getSecurePreference(getActivity(), "MMR_3", ""), dataPojo.getIMEI(), dataPojo.getFailTestId(), dataPojo.getPassTestId(),
                dataPojo.getUserID(), dataPojo.getStoreID(), dataPojo.getEnterprisePartnerID(), dataPojo.getSKUID(), dataPojo.getCatalogPartnerPricingID(), dataPojo.getLocationID(),
                imagesToUpload, utils.getSecurePreference(getActivity(), "MMR_4", ""),
                utils.getSecurePreference(getActivity(), "MMR_5", ""),
                utils.getSecurePreference(getActivity(), "MMR_6", ""),
                Utilities.getInstance(getActivity()).getPreference(getActivity(), Constants.DEVICEPRICE, ""),
                Utilities.getInstance(getActivity()).getPreference(getActivity(), Constants.TradeInValidityDays,"") ,
                Utilities.getInstance(getActivity()).getPreference(getActivity(), Constants.QuoteExpireDays,""),map1,
                Utilities.getInstance(getActivity()).getPreference(getActivity(), Constants.StoreType,""),
                selectedPaymentMode.getPaymentConfigrationId(),
                refrenceCode,customNote,customerAccountNumber,customerSortCode,bankName,
                Utilities.getInstance(getActivity()).getPreference(getActivity(), Constants.CURRENCYSYMBOL,"")
                ,statecode,pay_EmailAddress,pay_Name,pay_Address,pay_MobileNumber)
                , getActivity() ,new WebServiceInterface<Response<SocketDataSync>>() {
                    @Override
                    public void apiResponse(Response<SocketDataSync> response) {
                        try {
                            progressBarMedium.setVisibility(View.INVISIBLE);
                            if (response.isSuccessful()) {
                                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                SocketDataSync socketDataSync = response.body();
                                if (socketDataSync != null) {
                                    if (socketDataSync.isSuccess()) {
                                        Toast.makeText(getContext(),R.string.sucess,Toast.LENGTH_LONG).show();

                                        DataPojo dataPojo = socketDataSync.getData();
                                        String order_id = dataPojo.getOrderDetailID();

// OrderInformationFragment orderInformationFragment =
// new OrderInformationFragment();

                                        utils.addPreference(getActivity(),JsonTags.UDI.name(),"");
                                        RecieptInformationScreenFragment recieptInformationScreenFragment=new RecieptInformationScreenFragment();
                                        Bundle bundle = new Bundle();

                                        bundle.putString("OFFER_PRICE", offer_price);
                                        bundle.putString("ORDER_ID", order_id);

                                        bundle.putString("CUSTOMER_ADDRESS1",adres1);
                                        bundle.putString("CUSTOMER_ADDRESS2",adres2);
                                        bundle.putString("CUSTOMER_ADDRESS3", adres3);

// bundle.putString("POSTAL_CODE", txtPostCode.getText().toString());
                                        bundle.putString("CUSTOMER_CITY", city);

                                        bundle.putString("ADDRESS1", dataPojo.getAddress1());
                                        bundle.putString("ADDRESS2", dataPojo.getAddress2());
                                        bundle.putString("ADDRESS3", dataPojo.getAddress3());
                                        bundle.putString("POSTAL_CODE", dataPojo.getPostCode());
                                        bundle.putString("CITY", dataPojo.getCity());
                                        bundle.putString("CREATED_DATE",dataPojo.getCreatedDate());
                                        bundle.putString("EMAIL",email);
                                        recieptInformationScreenFragment.setArguments(bundle);
                                        replaceFragment(R.id.container, recieptInformationScreenFragment,
                                                FragmentTag.MANUAL_TEST_FRAGMENT.name(), false);
                                        TitleBarFragment fragment =
                                                (TitleBarFragment) getFragment(R.id.headerContainer);
                                        if (fragment != null) {
                                            fragment.setTitleBarVisibility(true);
                                            fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string.declaration), true, false, 0);
                                        }
                                    } else {
                                        Toast.makeText(getContext(),socketDataSync.getMessage(),Toast.LENGTH_LONG).show();
                                        done=true;
                                        if (getActivity() != null)
                                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                        fabricLog("CustomerInfo_Frag_Submit_Customer_info_api_error",response.toString());
                                    }
                                }

                            }


                        } catch (Exception e) {
                            if (getActivity() != null)
                                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            done=true;
                            fabricLog("CustomerInfo_Frag_Submit_Customer_info_api_exception", e.getMessage());
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void apiError(Response<SocketDataSync> response) {
                        try {
                            progressBarMedium.setVisibility(View.GONE);
                            SocketDataSync socketDataSync = response.body();
                            done = true;
                            Toast.makeText(getContext(), socketDataSync.getMessage(), Toast.LENGTH_LONG).show();
                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void serverError(Throwable t) {
                        try {

                            done=true;
                            if( t instanceof UnknownHostException)
                            {
                                Toast.makeText(getActivity(),"There is something went wrong with your Internet Connection Please reset your Internet Connection and try again",Toast.LENGTH_LONG).show();
                            }
                            progressBarMedium.setVisibility(View.GONE);
                            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            Toast.makeText(getContext(),t.getMessage(),Toast.LENGTH_LONG).show();
                            fabricLog("CustomerInfo_Frag_Submit_Customer_info_api_exception",t.getMessage());
                            Log.d("error", t.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });


    }


    public void clearStrings(){
       refrenceCode="";
        customNote="";
        sortCode="";
        customerAccountNumber="";
        bankName="";
       customerSortCode="";
    }




    @Override
    public void onResume() {
        super.onResume();
        TitleBarFragment fragment =
                (TitleBarFragment) getFragment(R.id.headerContainer);
        if (fragment != null) {
            fragment.setTitleBarVisibility(true);
            fragment.showSwitchLayout(false);
            fragment.setSyntextVisibilty(false);
            fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string.payment_details), true, false, 0);
        }

    }

    public void onBackPress() {

        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        try {
            if (getActivity() != null) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                Fragment frag = fm.findFragmentById(R.id.container);
                if (frag instanceof PaymentDetailFragment) {
                    popFragment(R.id.container);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


// Utilities.getInstance(getActivity()).showAlert(getActivity(),
// new Utilities.onAlertOkListener() {
// @Override
// public void onOkButtonClicked(String tag) {
//
// getActivity().setResult(RESULT_OK);
// getActivity().finish();
// }
// }, Html.fromHtml(getResources().getString(R.string.rescan_msg)),
// getResources().getString(R.string.rescan_head), "No", "Yes", "Sync");

    }

}


