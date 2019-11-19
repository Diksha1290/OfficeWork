package com.officework.testing_profiles.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.officework.R;
import com.officework.base.BaseFragment;
import com.officework.constants.Constants;
import com.officework.constants.JsonTags;
import com.officework.fragments.TitleBarFragment;
import com.officework.utils.Utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.officework.testing_profiles.ui.fragment.CustomerInformationFragment.hideKeyboard;

public class RecieptInformationScreenFragment extends BaseFragment {

    Utilities utils;

    Unbinder unbinder;
    Context ctx;

    @BindView(R.id.email_address_valueTV)
    TextView emailAddress;
    @BindView(R.id.imei_valueTV)
    TextView txtImei;

    @BindView(R.id.order_date_valueTV)
    TextView txtOrderDate;
    @BindView(R.id.device_name_valueTV)
    TextView deviceName;
    @BindView(R.id.offer_valueTV)
    TextView txtPrice;
    @BindView(R.id.txt_transection_value)
    TextView transectionValue;
    @BindView(R.id.txt_finish)
    Button finish;
    Display display;
    private String offer_price;
    private String order_Id,email;
    private String address1;
    private String address2;
    private String address3;
    private String city;
    private String customer_address1;
    private String customer_address2;
    private String customer_address3;
    private String customer_city;
    private String postalCode;
    private String createdDate;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //init Presenter
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_reciept, container, false);
        unbinder = ButterKnife.bind(this, view);
        if (getArguments() != null) {

            offer_price = getArguments().getString("OFFER_PRICE");
            order_Id = getArguments().getString("ORDER_ID");
            address1 = getArguments().getString("ADDRESS1");
            address2 = getArguments().getString("ADDRESS2");
            address3 = getArguments().getString("ADDRESS3");
            city = getArguments().getString("CITY");
            postalCode = getArguments().getString("POSTAL_CODE");
            customer_address1 = getArguments().getString("CUSTOMER_ADDRESS1");
            customer_address2 = getArguments().getString("CUSTOMER_ADDRESS2");
            customer_address3 = getArguments().getString("CUSTOMER_ADDRESS3");
            customer_city = getArguments().getString("CUSTOMER_CITY");
            createdDate = getArguments().getString("CREATED_DATE");
            email=getArguments().getString("EMAIL");

        }
        if(getActivity()!=null)
            hideKeyboard(getActivity());
        utils = Utilities.getInstance(getActivity());
        ctx = getActivity();
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);

        display = wm.getDefaultDisplay();

        setOrderDetails();


        return view;
    }


    @OnClick({R.id.txt_finish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.txt_finish:
                try {
                    getActivity().finishAffinity();
//                    android.os.Process.killProcess(android.os.Process.myPid());
//                    System.exit(1);
//                    Intent startMain = new Intent(Intent.ACTION_MAIN);
//                    startMain.addCategory(Intent.CATEGORY_HOME);
//                    startMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(startMain);
//
//
//
//
//
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        if(getActivity()!=null)
//                        getActivity().finishAndRemoveTask();
//                    }else {
//                        if(getActivity()!=null)
//                            getActivity().finish();
//                    }
//


                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        return null;
    }

    public void setOrderDetails() {
      //  txtPOrderDetailId.setText(order_Id);
        String imei = utils.getSecurePreference(ctx, JsonTags.MMR_1.name(),
                ctx.getResources().getString(R.string.txtPermissionDenied));
        txtImei.setText(imei);
//        txtAdress.setText(address1 + " " + address2);
//        txtCity.setText(city + ", " + postalCode);
//        txtCountry.setText("");

//        txtCustomerAddress.setText(customer_address1 + " " + customer_address2);
//        txtCustomerCity.setText(customer_city + ", " + postalCode);
//        txtCustomerCountry.setText("");
        emailAddress.setText(email);
        transectionValue.setText(order_Id);
        deviceName.setText(utils.getSecurePreference(ctx, JsonTags.MMR_5.name(), ""));
        txtPrice.setText(offer_price);
        txtOrderDate.setText(changeDateFormat(createdDate));
//        generateBarCode(order_Id, true);
//        generateBarCode(imei, false);

    }

    String changeDateFormat(String datedata){
        String date1="";
        try {
            SimpleDateFormat spf=new SimpleDateFormat("MMM dd yyyy hh:mmaaa");
            Date newDate = spf.parse(datedata);
            spf= new SimpleDateFormat("MMM dd yyyy");
            date1 = spf.format(newDate);
            System.out.println(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date1;
    }



  /*  private void generateBarCode(String UDI, boolean orderid) {
        try {
            MultiFormatWriter writer = new MultiFormatWriter();
            int width = display.getWidth();
            String finaldata = Uri.encode(UDI, "utf-8");

            BitMatrix bm = null;

            bm = writer.encode(finaldata, BarcodeFormat.CODE_128, width, 100);

            Bitmap ImageBitmap = Bitmap.createBitmap(width, 100, Bitmap.Config.ARGB_8888);

            for (int i = 0; i < width; i++) {//width
                for (int j = 0; j < 100; j++) {//height
                    ImageBitmap.setPixel(i, j, bm.get(i, j) ? Color.BLACK : Color.WHITE);
                }
            }

            if (ImageBitmap != null) {
                Bitmap resized = Bitmap.createScaledBitmap(ImageBitmap, ImageBitmap.getWidth(),
                        (int) (ImageBitmap.getHeight() * 1.5), true);
                if (orderid) {
                    orderDetailBarcode.setImageBitmap(resized);

                } else {
                    imeiBarcode.setImageBitmap(resized);
                }

            } else {
                //Toast.makeText(getApplicationContext(), getResources().getString(R.string
                // .userInputError),
                //    .// Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            //  logException(e, "DashBoardFragment_generateBarCode()");
        }


    }*/


    @Override
    public void onResume() {
        super.onResume();
        try {
            TitleBarFragment fragment = (TitleBarFragment) getFragment(R.id.headerContainer);
            if (fragment != null) {
                fragment.setTitleBarVisibility(true);
                fragment.showSwitchLayout(false);
                fragment.setSyntextVisibilty(false);
                fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string.receipt),
                        true, false, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
