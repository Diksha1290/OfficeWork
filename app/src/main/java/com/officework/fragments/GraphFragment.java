package com.officework.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.dlazaro66.wheelindicatorview.WheelIndicatorItem;
import com.dlazaro66.wheelindicatorview.WheelIndicatorView;
import com.officework.R;
import com.officework.base.BaseFragment;
import com.officework.utils.Utilities;

/**
 * Created by Girish on 8/5/2016.
 */
public class GraphFragment extends BaseFragment {
    View view;
    Utilities utils;
    Context ctx;
    WheelIndicatorView wheelIndicatorView;

    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_graph, null);
            utils = Utilities.getInstance(getActivity());
            ctx = getActivity();
            initViews();
        }
        return view;
    }

    public GraphFragment() {
    }

    private void initViews() {
        wheelIndicatorView = (WheelIndicatorView) view.findViewById(R.id.wheel_indicator_view);
        animateWheel();
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
