package com.officework.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.officework.R;
import com.officework.constants.AsyncConstant;
import com.officework.constants.Constants;
import com.officework.interfaces.InterfaceExceptionTableCallback;
import com.officework.interfaces.SummaryAdapterCallback;
import com.officework.models.AutomatedTestListModel;

import java.util.List;

public class SummaryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context mContext;
    InterfaceExceptionTableCallback categoryClick;
    SummaryAdapterCallback SummaryAdapterCallback;
    private List<AutomatedTestListModel> mAutomatedTestModel;
    private String testType;

    public SummaryAdapter(Context ctx, List<AutomatedTestListModel> mAutomatedTestModel,
                          SummaryAdapterCallback summaryAdapterCallback,String testType) {
        this.mAutomatedTestModel = mAutomatedTestModel;
        this.SummaryAdapterCallback = summaryAdapterCallback;
        mContext = ctx;
        this.testType = testType;
    }


    @Override
    public int getItemViewType(int position) {
        switch (mAutomatedTestModel.get(position).getTest_type()){
            case Constants.AUTOMATE:
                return 0;
            case Constants.MANUAL:
                return 1;
            case Constants.MANUAL1:
                return 1;
        }
        return super.getItemViewType(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView;
        switch ((viewType)){
            case 0:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.summary_row, parent, false);
                return new MyViewHolder(itemView);

            case 1:

                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_test_manual, parent, false);

                return new MyManualViewHolder(itemView);

        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        switch (getItemViewType(position)){
            case 0:
                AutomatedTestListModel products = mAutomatedTestModel.get(position);
                ((SummaryAdapter.MyViewHolder) holder).txtViewTestName.setText(products.getName());
                ((SummaryAdapter.MyViewHolder) holder).testIV.setImageResource(products.getResource());
                // holder.imgViewIcons.setImageResource(products.getResource());
                //((SummaryAdapter.MyViewHolder) holder).txtViewTestName.setText(products.getName().substring(0, 1).toUpperCase() + products.getName().substring(1).toLowerCase());

                if(testType.equalsIgnoreCase(Constants.AUTOMATE)){
                    ((MyViewHolder) holder).editIV.setVisibility(View.INVISIBLE);
                }else {
                    ((MyViewHolder) holder).editIV.setVisibility(View.VISIBLE);

                }

                switch (products.getIsTestSuccess()) {

                    case Constants.TEST_PASS:
                        ((SummaryAdapter.MyViewHolder) holder).imgViewStatus.setBackgroundResource(R.drawable.ic_icon_true);
                        break;

                    case Constants.TEST_FAILED:
                        ((SummaryAdapter.MyViewHolder) holder).imgViewStatus.setBackgroundResource(R.drawable.ic_icon_false);
                        break;
                    case Constants.TEST_NOTPERFORMED:
                        ((SummaryAdapter.MyViewHolder) holder).imgViewStatus.setBackgroundResource(R.drawable.ic_icon_false);
                        break;

                }

                ((SummaryAdapter.MyViewHolder) holder).editIV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

//                        SummaryAdapterCallback.editClick(products);
                    }
                });


                break;

            case 1:
                AutomatedTestListModel automatedTestListModel = mAutomatedTestModel.get(position);
                ((MyManualViewHolder)holder).txtViewTestName.setText(automatedTestListModel.getName());
                ((MyManualViewHolder)holder).imgViewIcons.setImageResource(automatedTestListModel.getResource());
                switch (automatedTestListModel.getIsTestSuccess()) {
                    case AsyncConstant.TEST_IN_QUEUE:
                        ((MyManualViewHolder)holder).mtxtViewDot.setBackgroundResource(R.drawable.rounded_disabled);
                        ((MyManualViewHolder)holder).imgViewStatus.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
                /*holder.mtxtViewStatus.setTextColor(mContext.getResources().getColor(R.color.product_gray));
                holder.mtxtViewStatus.setText("");*/
                        break;
                    case AsyncConstant.TEST_PASS:
                  //      ((MyManualViewHolder)holder).mtxtViewDot.setBackgroundResource(R.drawable.rounded_green);
                        ((MyManualViewHolder)holder).imgViewStatus.setBackgroundResource(R.drawable.ic_icon_true);
                /*holder.mtxtViewStatus.setTextColor(mContext.getResources().getColor(R.color.green_color));
                holder.mtxtViewStatus.setText(mContext.getResources().getString(R.string.txtPass));*/
                        break;
                    case AsyncConstant.TEST_IN_PROGRESS:
                    //    ((MyManualViewHolder)holder).mtxtViewDot.setBackgroundResource(R.drawable.rounded_yellow);
                        ((MyManualViewHolder)holder).imgViewStatus.setBackgroundResource(R.drawable.ic_icon_false);
                /*holder.mtxtViewStatus.setTextColor(mContext.getResources().getColor(R.color.yellow_font_Color));
                holder.mtxtViewStatus.setText(mContext.getResources().getString(R.string.txtInProgress));*/
                        break;
                    case AsyncConstant.TEST_FAILED:
                        //((MyManualViewHolder)holder).mtxtViewDot.setBackgroundResource(R.drawable.rounded_red);
                        ((MyManualViewHolder)holder).imgViewStatus.setBackgroundResource(R.drawable.ic_icon_false);
                /*holder.mtxtViewStatus.setTextColor(mContext.getResources().getColor(R.color.RedColor));
                holder.mtxtViewStatus.setText(mContext.getResources().getString(R.string.txtFail));*/
                        break;
                    case AsyncConstant.TEST_SKIP:
                       // ((MyManualViewHolder)holder).mtxtViewDot.setBackgroundResource(R.drawable.rounded_red);
                        ((MyManualViewHolder)holder).imgViewStatus.setBackgroundResource(R.drawable.ic_icon_false);
                /*holder.mtxtViewStatus.setTextColor(mContext.getResources().getColor(R.color.RedColor));
                holder.mtxtViewStatus.setText(mContext.getResources().getString(R.string.txtFail));*/
                        break;


                }
                ((MyManualViewHolder)holder).txtViewTestName.setTag(position);
                ((MyManualViewHolder)holder).mFrameLayoutMain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SummaryAdapterCallback.editClick(automatedTestListModel);
                    }
                });

                break;
        }

    }

    @Override
    public int getItemCount() {
        return mAutomatedTestModel.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtViewTestName;
        public ImageView imgViewStatus, testIV, editIV;
        public LinearLayout mRvMainParent;

        public MyViewHolder(View convertView) {
            super(convertView);
            txtViewTestName = (TextView) convertView.findViewById(R.id.txtViewTestName);
            imgViewStatus = (ImageView) convertView.findViewById(R.id.imgViewStatus);
            editIV = (ImageView) convertView.findViewById(R.id.editIV);
            mRvMainParent = (LinearLayout) convertView.findViewById(R.id.main_parent);
            testIV = (ImageView) convertView.findViewById(R.id.testIV);
        }
    }

    public class MyManualViewHolder extends RecyclerView.ViewHolder {
        public TextView txtViewTestName;
        public ImageView imgViewIcons, imgViewStatus;
        public TextView mtxtViewDot;
        public FrameLayout mFrameLayoutMain;

        public MyManualViewHolder(View convertView) {
            super(convertView);
            mFrameLayoutMain = (FrameLayout) convertView.findViewById(R.id.LinearLayoutMain);
            txtViewTestName = (TextView) convertView.findViewById(R.id.txtViewTestName);
            imgViewIcons = (ImageView) convertView.findViewById(R.id.imgIcon);
            mtxtViewDot = (TextView) convertView.findViewById(R.id.txtViewDot);
            imgViewStatus = (ImageView) convertView.findViewById(R.id.imgViewStatus);
        }
    }
}
