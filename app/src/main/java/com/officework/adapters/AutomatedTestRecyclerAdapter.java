package com.officework.adapters;

/**
 * Created by girish.sharma on 27-May-16.
 */

import android.animation.ObjectAnimator;
import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.officework.R;
import com.officework.constants.AsyncConstant;
import com.officework.customViews.DotProgressBar;
import com.officework.customViews.MyCircularSeekBar;
import com.officework.models.AutomatedTestListModel;

import java.util.List;

public class AutomatedTestRecyclerAdapter extends RecyclerView.Adapter<AutomatedTestRecyclerAdapter.MyViewHolder> {

    private List<AutomatedTestListModel> mAutomatedTestModel;
    Context mContext;
    InterfaceAdapterCallback adapterCallback;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout mLinearLayoutMain;
        public TextView txtViewTestName;
        public ImageView imgViewIcons;
        public TextView mtxtViewDot;
        public TextView mtxtViewStatus;
        public ImageView imgViewStatus;
        public DotProgressBar dotProgressBar;
        public RelativeLayout rv_layout;
        public MyCircularSeekBar circularSeekBar;

        public MyViewHolder(View view) {
            super(view);
            mLinearLayoutMain = (LinearLayout) view.findViewById(R.id.LinearLayoutMain);
            txtViewTestName = (TextView) view.findViewById(R.id.txtViewTestName);
            imgViewIcons = (ImageView) view.findViewById(R.id.imgIcon);
            mtxtViewDot = (TextView) view.findViewById(R.id.txtViewDot);
            mtxtViewStatus = (TextView) view.findViewById(R.id.txtTextStatus);
            imgViewStatus = (ImageView) view.findViewById(R.id.imgViewStatus);
            dotProgressBar=view.findViewById(R.id.dot_progress_barq);
            circularSeekBar= view.findViewById(R.id.progressBar);
            rv_layout=view.findViewById(R.id.rv_layout);
        }
    }

    public AutomatedTestRecyclerAdapter(Context ctx, List<AutomatedTestListModel> automatedTestListModels, InterfaceAdapterCallback callback) {
        this.mAutomatedTestModel = automatedTestListModels;
        mContext = ctx;
        adapterCallback = callback;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.automated_test_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        AutomatedTestListModel products = mAutomatedTestModel.get(position);
        holder.txtViewTestName.setText(products.getName());
        holder.imgViewIcons.setImageResource(products.getResource());
        holder.circularSeekBar.setEnabled(false);
        switch (products.getIsTestSuccess()) {

            case AsyncConstant.TEST_IN_QUEUE:
                holder.mtxtViewDot.setBackgroundResource(R.drawable.rounded_disabled);
                holder.mtxtViewStatus.setTextColor(mContext.getResources().getColor(R.color.product_gray));
                holder.mtxtViewStatus.setText("");
                holder.mtxtViewStatus.setVisibility(View.GONE);
                holder.circularSeekBar.setProgress(0);
                break;
            case AsyncConstant.TEST_PASS:

                holder.rv_layout.setVisibility(View.GONE);
                holder.imgViewStatus.setVisibility(View.VISIBLE);
                holder.mtxtViewDot.setBackgroundResource(R.drawable.rounded_green);
                holder.mtxtViewStatus.setTextColor(mContext.getResources().getColor(R.color.green_color));
                /*holder.mtxtViewStatus.setText(mContext.getResources().getString(R.string.txtPass));*/
                holder.mtxtViewStatus.setVisibility(View.GONE);
                holder.imgViewStatus.setBackgroundResource(R.drawable.ic_icon_true);
                holder.circularSeekBar.setProgress(100);
                break;
            case AsyncConstant.TEST_IN_PROGRESS:
//                holder.mtxtViewDot.setBackgroundResource(R.drawable.rounded_yellow);
//                holder.mtxtViewStatus.setVisibility(View.GONE);
//                //holder.mtxtViewStatus.setText(mContext.getResources().getString(R.string.txtInProgress));
//                holder.mtxtViewStatus.setTextColor(mContext.getResources().getColor(R.color.yellow_font_Color));
//                holder.imgViewStatus.setBackgroundResource(R.drawable.ic_thumb_process);

                ObjectAnimator animation = ObjectAnimator.ofFloat(holder.circularSeekBar, "progress", 0, 100);
                if(products.getTest_id()!=37){
                    animation.setDuration(1500);
                }
                else{
                    animation.setDuration(5200);

                }
                animation.setInterpolator(new DecelerateInterpolator());
                animation.start();



                holder.mtxtViewStatus.setVisibility(View.GONE);
                holder.imgViewStatus.setVisibility(View.GONE);
                holder.rv_layout.setVisibility(View.VISIBLE);


                break;
            case AsyncConstant.TEST_FAILED:

                holder.rv_layout.setVisibility(View.GONE);
                holder.imgViewStatus.setVisibility(View.VISIBLE);
                holder.mtxtViewDot.setBackgroundResource(R.drawable.rounded_red);
                holder.mtxtViewStatus.setTextColor(mContext.getResources().getColor(R.color.RedColor));
                holder.mtxtViewStatus.setVisibility(View.GONE);
                holder.imgViewStatus.setBackgroundResource(R.drawable.ic_icon_false);
                holder.circularSeekBar.setProgress(100);
                break;
        }
        holder.mLinearLayoutMain.setOnClickListener(new View.OnClickListener() {
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
    //    @Override
//    public long getItemId(int position) {
//        return super.getItemId(position);
//    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
