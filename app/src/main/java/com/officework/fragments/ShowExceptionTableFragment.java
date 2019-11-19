package com.officework.fragments;


import android.annotation.SuppressLint;
import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.officework.R;
import com.officework.adapters.ExceptionTableAdapter;
import com.officework.base.BaseFragment;
import com.officework.constants.FragmentTag;
import com.officework.interfaces.InterfaceExceptionTableCallback;
import com.officework.models.LogExceptionModel;
import com.officework.utils.Utilities;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressLint("ValidFragment")
public class ShowExceptionTableFragment extends BaseFragment implements InterfaceExceptionTableCallback {
    private View view;
    private Context mContext;
    private Utilities utils;
    List<LogExceptionModel> listModel;
    ExceptionTableAdapter exceptionTableAdapter;
    RecyclerView recyclerView;

    public ShowExceptionTableFragment() {

    }

    public ShowExceptionTableFragment(List<LogExceptionModel> list) {
        this.listModel = list;
        // Required empty public constructor
    }

    @Override
    protected View initUI(LayoutInflater inflater, ViewGroup container) {
        mContext = getActivity();
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_show_exception_table, null);
            utils = Utilities.getInstance(getActivity());
            utils.HideKeyboard(getActivity(), mContext, view);
            initView();
        }
        return view;
    }

    private void initView() {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerExceptionTable);
        exceptionTableAdapter = new ExceptionTableAdapter(mContext, listModel, (InterfaceExceptionTableCallback) this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(exceptionTableAdapter);
        exceptionTableAdapter.notifyDataSetChanged();
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
                fragment.setHeaderTitleAndSideIcon(getResources().getString(R.string.txtExceptions), true, false, 0);
            }
        } catch (Exception e) {

        }

        super.onResume();
    }

    @Override
    public void onItemClickCallBack(int position, List<LogExceptionModel> logExceptionModels) {
        replaceFragment(R.id.container, new ShowExceptionDetailFragment(position, logExceptionModels), FragmentTag.EXCEPTIONDETAILFRAGMENT
                .name(), true);
    }
}
