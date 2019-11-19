package com.officework.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.RequiresApi;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.officework.R;
import com.officework.activities.MainActivity;
import com.officework.base.BaseFragment;
import com.officework.constants.Constants;
import com.officework.customViews.IconView;
import com.officework.interfaces.InterfaceButtonTextChange;
import com.officework.interfaces.TimerDialogInterface;
import com.officework.models.AutomatedTestListModel;
import com.officework.testing_profiles.Controller.TestController;
import com.officework.testing_profiles.utils.ConstantTestIDs;
import com.officework.utils.TestResultUpdateToServer;
import com.officework.utils.Utilities;

import java.util.Observable;
import java.util.Observer;

import static com.officework.testing_profiles.utils.ConstantTestIDs.FLASH;

@SuppressLint("ValidFragment")
public class FlashTestFragment extends BaseFragment implements Observer, TimerDialogInterface {
    View view;
    Utilities utils;
    Context ctx;
    private TestController testController;
    Button flash , ok ,notok;
    CameraManager cameraManager;
    private String cameraId;
    AlertDialog alertDialog;
    //Handler nextButtonHandler;
    boolean isTestPerformed = false;
    InterfaceButtonTextChange mCallBack;
    TestResultUpdateToServer testResultUpdateToServer;
    IconView flashimage;
    TextView flashText;
    LinearLayout flashLayout;
    MainActivity mainActivity;
    TextView textView;

    @SuppressLint("ValidFragment")
    public FlashTestFragment(InterfaceButtonTextChange callback) {
        mCallBack = callback;
    }

    // private TestController testController;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_flashtest_layout, null);
            utils = Utilities.getInstance(getActivity());
            ctx = getActivity();

        //    Crashlytics.getInstance().log(FragmentTag.FLASH_TEST_FRAGMENT.name());
            initViews();

            testController = TestController.getInstance(getActivity());
            testController.addObserver(this);

        }
        return view;
    }

    public FlashTestFragment() {
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initViews() {
        try {
            timer(getActivity(),false, FLASH,FlashTestFragment.this);
            alertDialog = new AlertDialog.Builder(
                    ctx).create();
            mainActivity = (MainActivity)getActivity();
            textView=view.findViewById(R.id.txta);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                textView.setText(Html.fromHtml(getResources().getString(R.string.flashlightTest),Html.FROM_HTML_MODE_COMPACT));
            }
            else {
                textView.setText(Html.fromHtml(getResources().getString(R.string.flashlightTest)));
            }



            mainActivity.onChangeText(R.string.textSkip,true);
            flashimage = (IconView) view.findViewById(R.id.flashimg);
           // flashText=(TextView)view.findViewById(R.id.flashtxt);
            flashLayout=(LinearLayout)view.findViewById(R.id.flashlayout);
            flash=(Button)view.findViewById(R.id.flashbtn);
            ok=(Button)view.findViewById(R.id.ok);
            notok=(Button)view.findViewById(R.id.no);

            flash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    timer(getActivity(),true,ConstantTestIDs.FLASH,FlashTestFragment.this);
                    timer(getActivity(),false,ConstantTestIDs.FLASH,FlashTestFragment.this);
                    flash.setEnabled(false);
                    flash.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque));
                    flash.setVisibility(view.INVISIBLE);

                    flashimage.setVisibility(view.VISIBLE);
                   new Thread(new Runnable() {
                       @Override
                       public void run() {
                           testController.performOperation(FLASH);
                           new Handler(Looper.getMainLooper()).post(new Runnable() {
                               @Override
                               public void run() {
                                   ok.setVisibility(view.VISIBLE);
                                   notok.setVisibility(view.VISIBLE);
                               }
                           });
                       }
                   }).start();

                }
            });


        }catch (Exception e){

            e.printStackTrace();
        }

    }

    @Override
    public void onResume() {
        TitleBarFragment fragment = (TitleBarFragment) getFragment(R.id.headerContainer);
        if (fragment != null) {
//            fragment.setTitleBarVisibility(true);
//            fragment.setSyntextVisibilty(false);
//
//            fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string.flash), false, false, 0);
          //  nextButtonHandler = new Handler();
            if (isTestPerformed) {
                mCallBack.onChangeText(Utilities.BUTTON_NEXT, true);

               onNextPress();

            } else {
                mCallBack.onChangeText(Utilities.BUTTON_SKIP, true);
            }
        }
        try{
            ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    testController.onServiceResponse(true, "Flash", FLASH);
                    flashimage.setImageDrawable(getResources().getDrawable(R.drawable.flashimg),true,getActivity());
                    flashimage.setVisibility(view.VISIBLE);
                    ok.setEnabled(false);
                    notok.setEnabled(false);
                    ok.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque));
                    notok.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque));
                    // flashimage.getDrawable().setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.MULTIPLY);
                    mainActivity.onChangeText(R.string.textSkip,false);

                            isTestPerformed = true;

                            utils.showToast(ctx, getResources().getString(R.string.txtManualPass));
                            if (alertDialog.isShowing()) {
                                alertDialog.dismiss();
                            }
                            onNextPress();

                }
            });
            notok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    testController.onServiceResponse(false, "Flash", FLASH);
                    flashimage.setVisibility(view.VISIBLE);
                    ok.setEnabled(false);
                    notok.setEnabled(false);
                    ok.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque));
                    notok.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque));
                    flashimage.setImageDrawable(getResources().getDrawable(R.drawable.flashimg),false,getActivity());
                    // flashimage.getDrawable().setColorFilter(getResources().getColor(R.color.RedColor), PorterDuff.Mode.MULTIPLY);
                    utils.showToast(ctx, getResources().getString(R.string.txtManualFail));

                            onNextPress();

                }
            });
        }
        catch (Exception e){e.printStackTrace();}

        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * Called when test is completed or user click on skip button
     */
    public void onNextPress() {
        try {
            timer(ctx,true, FLASH,FlashTestFragment.this);
            MainActivity activity = (MainActivity) getActivity();
//            activity.onChangeText(R.string.textSkip,false);
            if (Constants.isSkipButton) {
                flash.setEnabled(false);
                flash.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque));

                ok.setEnabled(false);
                notok.setEnabled(false);
                ok.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque));
                notok.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque));
                isTestPerformed=true;
                utils.showToast(ctx, getResources().getString(R.string.txtManualFail));
                flashimage.setImageDrawable(getResources().getDrawable(R.drawable.flashimg),false,getActivity());

            }
            boolean semi=false;
            nextPress(activity,semi);


        } catch (Exception e) {

        }

    }

    /**
     * onBackPressed
     */
    public void onBackPress() {
        try {

            snackShow(flashLayout);
        }catch (Exception e){
           e.printStackTrace();
        }

    }



    @Override
    public void update(Observable observable, Object o) {
        try {
            isTestPerformed=true;

            AutomatedTestListModel automatedTestListModel = (AutomatedTestListModel)o;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            timer(getActivity(),true, FLASH,this);
            testController.deleteObserver(this);

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void timerFail() {
        flash.setEnabled(false);
        flash.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque));
        ok.setEnabled(false);
        notok.setEnabled(false);
        ok.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque));
        notok.setBackgroundColor(getResources().getColor(R.color.disabledGray_opaque));
        isTestPerformed=true;
        flashimage.setImageDrawable(getResources().getDrawable(R.drawable.flashimg),false,getActivity());
        utils.showToast(ctx, getResources().getString(R.string.txtManualFail));
        onNextPress();
    }
}
