package com.officework.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.officework.R;
import com.officework.constants.Constants;
import com.officework.models.AutomatedTestListModel;

import java.util.ArrayList;
import java.util.List;


public class TestReportAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final Context mContext;
    private List<AutomatedTestListModel> mAutomatedTestModel;
    private InterfaceAdapterCallback adapterCallback;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtViewTestName;
        public ImageView imgViewStatus,testIV;
        public LinearLayout mRvMainParent;

        public MyViewHolder(View convertView) {
            super(convertView);
            txtViewTestName = (TextView) convertView.findViewById(R.id.txtViewTestName);
            imgViewStatus = (ImageView) convertView.findViewById(R.id.imgViewStatus);
            mRvMainParent = (LinearLayout) convertView.findViewById(R.id.main_parent);
            testIV=(ImageView)convertView.findViewById(R.id.testIV);
        }
    }
    public class MyViewHolder1 extends RecyclerView.ViewHolder {

        public MyViewHolder1(View convertView) {
            super(convertView);

        }
    }
    public TestReportAdapter(Context context, ArrayList<AutomatedTestListModel> automatedTestListModels) {
        mContext = context;
        mAutomatedTestModel = automatedTestListModels;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType){
            case 80:
                view = LayoutInflater.from(mContext).inflate(R.layout.item_test_report, parent, false);
                return new MyViewHolder(view);


            case 90:
                view = LayoutInflater.from(mContext).inflate(R.layout.blank_view, parent, false);
                return new MyViewHolder1(view);

        }
        return null;

    }




    @Override
    public int getItemViewType(int position) {

        AutomatedTestListModel testObject = mAutomatedTestModel.get(position);
        if( testObject.getIsTestSuccess() == Constants.TEST_NOT_EXIST){
            return 90;
        }else
            return 80;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        switch (getItemViewType(position)){
            case 80:
                AutomatedTestListModel products = mAutomatedTestModel.get(position);
                ((MyViewHolder)holder).txtViewTestName.setText(products.getName());
                ((MyViewHolder)holder).testIV.setImageResource(products.getResource());
                // holder.imgViewIcons.setImageResource(products.getResource());
                ((MyViewHolder)holder).txtViewTestName.setText(products.getName().substring(0,1).toUpperCase() + products.getName().substring(1).toLowerCase());




                switch (products.getIsTestSuccess()) {
                    case Constants.TEST_IN_QUEUE:
                        ((MyViewHolder)holder).imgViewStatus.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
                        break;
                    case Constants.TEST_PASS:
                        ((MyViewHolder)holder).imgViewStatus.setBackgroundResource(R.drawable.ic_icon_true);
                        break;
                    case Constants.TEST_IN_PROGRESS:
                        ((MyViewHolder)holder).imgViewStatus.setBackgroundResource(R.drawable.report_status_icon_partial);
                        break;
                    case Constants.TEST_FAILED:
                        ((MyViewHolder)holder).imgViewStatus.setBackgroundResource(R.drawable.ic_cross);
                        break;

                    case Constants.TEST_NOTPERFORMED:
                        ((MyViewHolder)holder).imgViewStatus.setBackgroundResource(R.drawable.ic_cross);
                        break;
                }



            case 90:


        }




    }

    @Override
    public int getItemCount() {
        return mAutomatedTestModel.size();
    }
}

