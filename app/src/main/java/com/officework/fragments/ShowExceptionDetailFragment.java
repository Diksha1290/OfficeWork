package com.officework.fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.officework.R;
import com.officework.base.BaseFragment;
import com.officework.models.LogExceptionModel;
import com.officework.utils.Utilities;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class ShowExceptionDetailFragment extends BaseFragment {
    private View view;
    private Context mContext;
    private Utilities utils;
    List<LogExceptionModel> listModel;
    int elementPosition;
    TextView txtViewUDIDetail, txtViewMethodNameDetail, txtViewTimeStampDetail, txtViewExceptionDetail, txtViewStackTrace;

    public ShowExceptionDetailFragment() {
        // Required empty public constructor
    }

    public ShowExceptionDetailFragment(int position, List<LogExceptionModel> logExceptionModelList) {
        this.elementPosition = position;
        this.listModel = logExceptionModelList;
        // Required empty public constructor
    }

    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        mContext = getActivity();
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_show_exception_detail, null);
            utils = Utilities.getInstance(getActivity());
            utils.HideKeyboard(getActivity(), mContext, view);
            initView();
        }
        return view;
    }

    private void initView() {
        txtViewUDIDetail = (TextView) view.findViewById(R.id.txtViewUDIDetail);
        txtViewMethodNameDetail = (TextView) view.findViewById(R.id.txtViewMethodNameDetail);
        txtViewTimeStampDetail = (TextView) view.findViewById(R.id.txtViewTimeStampDetail);
        txtViewExceptionDetail = (TextView) view.findViewById(R.id.txtViewExceptionDetail);
        txtViewStackTrace = (TextView) view.findViewById(R.id.txtViewStackTrace);
        txtViewUDIDetail.setText(listModel.get(elementPosition).getUDI());
        txtViewMethodNameDetail.setText(listModel.get(elementPosition).getMethodName());
        txtViewTimeStampDetail.setText(listModel.get(elementPosition).getExceptionDateTime());
        txtViewExceptionDetail.setText(listModel.get(elementPosition).getExceptionDetail());
        txtViewStackTrace.setText(listModel.get(elementPosition).getStackTrace());
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
                fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string.txtExceptionsDetail), true, false, 0);
            }
        } catch (Exception e) {

        }

        super.onResume();
    }

}
