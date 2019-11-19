package com.officework.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.officework.R;
import com.officework.constants.AsyncConstant;
import com.officework.models.AutomatedTestListModel;

import java.util.List;

/**
 * Created by Girish on 11/30/2016.
 */

public class SectionHeaderAdapter extends RecyclerView.Adapter<SectionHeaderAdapter.MyViewHolder> {
    private final Context mContext;
    private List<AutomatedTestListModel> mAutomatedTestModel;
    InterfaceAdapterCallback adapterCallback;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtViewTestName;
        public ImageView imgViewIcons, imgViewStatus;
        public TextView mtxtViewDot;
        public FrameLayout mFrameLayoutMain;

        public MyViewHolder(View convertView) {
            super(convertView);
            mFrameLayoutMain = (FrameLayout) convertView.findViewById(R.id.LinearLayoutMain);
            txtViewTestName = (TextView) convertView.findViewById(R.id.txtViewTestName);
            imgViewIcons = (ImageView) convertView.findViewById(R.id.imgIcon);
            mtxtViewDot = (TextView) convertView.findViewById(R.id.txtViewDot);
            imgViewStatus = (ImageView) convertView.findViewById(R.id.imgViewStatus);
        }
    }
    public SectionHeaderAdapter(Context context, List<AutomatedTestListModel> automatedTestListModels, InterfaceAdapterCallback callback) {
        mContext = context;
        mAutomatedTestModel = automatedTestListModels;
        adapterCallback = callback;
    }

    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.item_test_manual, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        AutomatedTestListModel products = mAutomatedTestModel.get(position);
        holder.txtViewTestName.setText(products.getName());
        holder.imgViewIcons.setImageResource(products.getResource());
        switch (products.getIsTestSuccess()) {
            case AsyncConstant.TEST_IN_QUEUE:
                holder.mtxtViewDot.setBackgroundResource(R.drawable.rounded_disabled);
                holder.imgViewStatus.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
                /*holder.mtxtViewStatus.setTextColor(mContext.getResources().getColor(R.color.product_gray));
                holder.mtxtViewStatus.setText("");*/
                break;
            case AsyncConstant.TEST_PASS:
            //    holder.mtxtViewDot.setBackgroundResource(R.drawable.rounded_green);
                holder.imgViewStatus.setBackgroundResource(R.drawable.ic_icon_true);
                /*holder.mtxtViewStatus.setTextColor(mContext.getResources().getColor(R.color.green_color));
                holder.mtxtViewStatus.setText(mContext.getResources().getString(R.string.txtPass));*/
                break;
            case AsyncConstant.TEST_IN_PROGRESS:
            //    holder.mtxtViewDot.setBackgroundResource(R.drawable.rounded_yellow);
                holder.imgViewStatus.setBackgroundResource(R.drawable.ic_icon_false);
                /*holder.mtxtViewStatus.setTextColor(mContext.getResources().getColor(R.color.yellow_font_Color));
                holder.mtxtViewStatus.setText(mContext.getResources().getString(R.string.txtInProgress));*/
                break;
            case AsyncConstant.TEST_FAILED:
            //    holder.mtxtViewDot.setBackgroundResource(R.drawable.rounded_red);
                holder.imgViewStatus.setBackgroundResource(R.drawable.ic_icon_false);
                /*holder.mtxtViewStatus.setTextColor(mContext.getResources().getColor(R.color.RedColor));
                holder.mtxtViewStatus.setText(mContext.getResources().getString(R.string.txtFail));*/
                break;
            case AsyncConstant.TEST_SKIP:
             //   holder.mtxtViewDot.setBackgroundResource(R.drawable.rounded_red);
                holder.imgViewStatus.setBackgroundResource(R.drawable.ic_icon_false);
                /*holder.mtxtViewStatus.setTextColor(mContext.getResources().getColor(R.color.RedColor));
                holder.mtxtViewStatus.setText(mContext.getResources().getString(R.string.txtFail));*/
                break;


        }
        holder.txtViewTestName.setTag(position);
        holder.mFrameLayoutMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapterCallback.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAutomatedTestModel.size();
    }
}
