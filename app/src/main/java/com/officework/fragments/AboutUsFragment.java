package com.officework.fragments;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.officework.BuildConfig;
import com.officework.R;
import com.officework.base.BaseFragment;
import com.officework.constants.AppConstantsTheme;
import com.officework.constants.FragmentTag;
import com.officework.interfaces.InterfaceButtonTextChange;
import com.officework.utils.ExceptionLogUtils;
import com.officework.utils.Utilities;

/**
 * Created by Girish on 8/24/2016.
 */
public class AboutUsFragment extends BaseFragment {
    View view;
    Utilities utils;
    Context ctx;
    TextView webViewAboutUs;
    TextView txtViewFurtherUrl, txtViewProductionUrl;
    LinearLayout linearLayout;
    LinearLayout mainLayout;
    int noOfTouchs=0;

    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        try {
            if (view == null) {
                view = inflater.inflate(R.layout.fragment_aboutus, null);
                utils = Utilities.getInstance(getActivity());
                ctx = getActivity();
               // Crashlytics.log(FragmentTag.ABOUT_US_FRAGMENT.name());
                initViews();
            }
            return view;
        } catch (Exception e) {
            logException(e, "AboutUsFragment_initUI()");
            return null;
        }

    }

    public AboutUsFragment() {
    }

    private void initViews() {
        try {
            mainLayout=(LinearLayout)view.findViewById(R.id.aboutmain);
            linearLayout=(LinearLayout)view.findViewById(R.id.aboutlayout);

           // int color= Color.parseColor(new ThemeManager(getActivity()).getTheme());
            int color= AppConstantsTheme.getIconColor();
            linearLayout.setBackgroundColor(color);
            txtViewFurtherUrl = (TextView) view.findViewById(R.id.txtFurtherDetail);
           // txtViewProductionUrl = (TextView) view.findViewById(R.id.txtProductionDetail);
            webViewAboutUs = (TextView) view.findViewById(R.id.webViewAboutUs);
            txtViewFurtherUrl.setMovementMethod(LinkMovementMethod.getInstance());
            //txtViewProductionUrl.setMovementMethod(LinkMovementMethod.getInstance());
            String Furtertext = "sales@veridic.co.uk";
           // String ProductionText = "<a href='http:/fairworld.in'> fairworld.in </a>";
            txtViewFurtherUrl.setText(Html.fromHtml(Furtertext));
           // txtViewProductionUrl.setText(Html.fromHtml(ProductionText));
            webViewAboutUs.setText(Html.fromHtml(getString(R.string.aboutus_des)));

            webViewAboutUs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    noOfTouchs++;
                    if(noOfTouchs==3){
                        androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(ctx);
                        alert.setTitle(getResources().getString(R.string.app_name));
                        alert.setMessage(getResources().getString(R.string.build_version)+" "+ BuildConfig.VERSION_NAME);
                        alert.setCancelable(true);
                        alert.create();
                        alert.show();
                        noOfTouchs=0;
                    }
                }
            });
        } catch (Exception e) {
            logException(e, "AboutUsFragment_initViews()");
        }

    }

    /**
     * OnBackPressed is han
     */

 /*   public void onBackPress() {
        try {
            popFragment(R.id.container);
        } catch (Exception e) {
            logException(e, "AboutUsFragment_onBackPress()");
        }

    }*/

    public void onBackPress() {
        try {
            clearAllStack();
            replaceFragment(R.id.container, new DashBoardFragment((InterfaceButtonTextChange) getActivity()), FragmentTag.DASHBOARD_FRAGMENT.name(), false);
        } catch (Exception e) {
         //   logException(e, "DeviceInfoFragment_onBackPress()");
        }
        /*popFragment(R.id.container);*/

    }

    /**
     * Here change the header text
     ***/
    @Override
    public void onResume() {
        try {
            TitleBarFragment fragment = (TitleBarFragment) getFragment(R.id.headerContainer);
            if (fragment != null) {
                fragment.setTitleBarVisibility(true);
                fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string.txtManualAboutUs), true, false, 0);
            }
            super.onResume();
        } catch (Exception e) {
            logException(e, "AboutUsFragment_onResume()");
        }

    }

    public void logException(Exception e, String methodName) {
        try {
            Utilities utilities = Utilities.getInstance(getActivity());
            Activity activity = getActivity();
            Context context = getActivity();
            ExceptionLogUtils exceptionLogUtils = new ExceptionLogUtils(utilities, context, activity);
            exceptionLogUtils.addExceptionLog(utilities, context, activity, null, null, e, methodName);
        } catch (Exception exp) {
          //  logException(exp, "AboutUsFragment_logException()");
        }

    }
}
