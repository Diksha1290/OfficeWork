package com.officework.fragments;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.dlazaro66.wheelindicatorview.WheelIndicatorItem;
import com.dlazaro66.wheelindicatorview.WheelIndicatorView;
import com.officework.R;
import com.officework.activities.AutomatedTestActivity;
import com.officework.base.BaseFragment;
import com.officework.constants.Constants;
import com.officework.utils.Utilities;

/**
 * Created by Girish on 8/5/2016.
 */
public class MegaSmartRunFragment extends BaseFragment implements View.OnClickListener {
    View view;
    Utilities utils;
    Context ctx;
    WheelIndicatorView wheelIndicatorView;
    ImageButton imgBtnSmartRun;

    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_mega_smartrun, null);
            utils = Utilities.getInstance(getActivity());
            ctx = getActivity();
            initViews();
        }
        return view;
    }

    public MegaSmartRunFragment() {
    }

    private void initViews() {
        wheelIndicatorView = (WheelIndicatorView) view.findViewById(R.id.wheel_indicator_view);
        imgBtnSmartRun = (ImageButton) view.findViewById(R.id.btnStartMegaRun);
        imgBtnSmartRun.setOnClickListener(this);
        /*animateWheel();*/
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btnStartMegaRun:
                Constants.IS_SMART_RUN = true;
                /*startActivity(new Intent(ctx, AutomatedTestActivity.class));*/
                Intent intent = new Intent(getActivity(), AutomatedTestActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
        }
    }

    /**
     * This will load the WheelIndicator
     */
    private void animateWheel() {
        // dummy data
        float dailyKmsTarget = 4.0f; // 4.0Km is the user target, for example
        float totalKmsDone = 4.0f; // User has done 3 Km
        int percentageOfExerciseDone = (int) (totalKmsDone / dailyKmsTarget * 100); //

        wheelIndicatorView.setFilledPercent(percentageOfExerciseDone);

        WheelIndicatorItem bikeActivityIndicatorItem = new WheelIndicatorItem(1.0f, getResources().getColor(R.color.Black));
        WheelIndicatorItem walkingActivityIndicatorItem = new WheelIndicatorItem(1.0f, getResources().getColor(R.color.purple));
        WheelIndicatorItem runningActivityIndicatorItem = new WheelIndicatorItem(1.0f, getResources().getColor(R.color.disabledGray));
        WheelIndicatorItem waitingActivityIndicatorItem = new WheelIndicatorItem(1.0f, getResources().getColor(R.color.colorAccent));

        wheelIndicatorView.addWheelIndicatorItem(bikeActivityIndicatorItem);
        wheelIndicatorView.addWheelIndicatorItem(walkingActivityIndicatorItem);
        wheelIndicatorView.addWheelIndicatorItem(runningActivityIndicatorItem);
        wheelIndicatorView.addWheelIndicatorItem(waitingActivityIndicatorItem);

        // Or you can add it as
        //wheelIndicatorView.setWheelIndicatorItems(Arrays.asList(runningActivityIndicatorItem,walkingActivityIndicatorItem,bikeActivityIndicatorItem));
        wheelIndicatorView.startItemsAnimation(); // Animate!
    }
}
