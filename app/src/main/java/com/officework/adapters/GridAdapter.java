package com.officework.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.officework.R;
import com.officework.constants.AsyncConstant;
import com.officework.models.AutomatedTestListModel;

import java.util.List;

/**
 * Created by Girish on 7/29/2016.
 */
public class GridAdapter extends BaseAdapter {
    private Context mContext;
    private ViewHolder holder;
    private LayoutInflater mInflater;
    private List<AutomatedTestListModel> mAutomatedTestModel;

    public GridAdapter(Context ctx, List<AutomatedTestListModel> automatedTestListModels) {
        mContext = ctx;
        mInflater = LayoutInflater.from(ctx);
        mAutomatedTestModel = automatedTestListModels;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mAutomatedTestModel.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            holder = new ViewHolder();

            convertView = mInflater.inflate(R.layout.item_test_manual,
                    null);

            holder.txtViewTestName = (TextView) convertView
                    .findViewById(R.id.txtViewTestName);
            holder.imgViewIcons = (ImageView) convertView.findViewById(R.id.imgIcon);
            holder.mtxtViewDot = (TextView) convertView.findViewById(R.id.txtViewDot);
            holder.imgViewStatus = (ImageView) convertView.findViewById(R.id.imgViewStatus);
            /*holder.mtxtManual = (FrameLayout) convertView.findViewById(R.id.frameManual);*/

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

       /* if (convertView == null || convertView.getTag() == null || !(convertView.getTag() instanceof ViewHolder)) {
            convertView = mInflater.inflate(R.layout.grid_item, null);
            holder = new ViewHolder();
            holder.mTextView = (TextView) convertView.findViewById(R.id.text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }*/



        AutomatedTestListModel products = mAutomatedTestModel.get(position);
        holder.txtViewTestName.setText(products.getName());
        holder.imgViewIcons.setImageResource(products.getResource());
        switch (products.getIsTestSuccess()) {
            case AsyncConstant.TEST_IN_QUEUE:
                holder.mtxtViewDot.setBackgroundResource(R.drawable.rounded_disabled);
                holder.imgViewStatus.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
              /*  holder.mtxtViewStatus.setTextColor(mContext.getResources().getColor(R.color.product_gray));
                holder.mtxtViewStatus.setText("");*/
                break;
            case AsyncConstant.TEST_PASS:
                holder.mtxtViewDot.setBackgroundResource(R.drawable.rounded_green);
                holder.imgViewStatus.setBackgroundResource(R.drawable.ic_thump_up);
             /*   holder.mtxtViewStatus.setTextColor(mContext.getResources().getColor(R.color.green_color));
                holder.mtxtViewStatus.setText(mContext.getResources().getString(R.string.txtPass));*/
                break;
            case AsyncConstant.TEST_IN_PROGRESS:
                holder.mtxtViewDot.setBackgroundResource(R.drawable.rounded_yellow);
                holder.imgViewStatus.setBackgroundResource(R.drawable.ic_thumb_partial);
               /* holder.mtxtViewStatus.setTextColor(mContext.getResources().getColor(R.color.yellow_font_Color));
                holder.mtxtViewStatus.setText(mContext.getResources().getString(R.string.txtInProgress));*/
                break;
            case AsyncConstant.TEST_FAILED:
                holder.mtxtViewDot.setBackgroundResource(R.drawable.rounded_red);
                holder.imgViewStatus.setBackgroundResource(R.drawable.ic_thump_down);
               /* holder.mtxtViewStatus.setTextColor(mContext.getResources().getColor(R.color.RedColor));
                holder.mtxtViewStatus.setText(mContext.getResources().getString(R.string.txtFail));*/
                break;
        }
        if (position == 0 || position == 6) {
            holder.mtxtManual.setBackgroundResource(R.drawable.rounded_opaque_dark);
        } else {
            holder.mtxtManual.setBackgroundResource(R.drawable.rounded_opaque);
        }
        holder.txtViewTestName.setTag(position);
        return convertView;
    }

    private class ViewHolder {
        public TextView txtViewTestName;
        public ImageView imgViewIcons, imgViewStatus;
        public TextView mtxtViewDot;
        public FrameLayout mtxtManual;
        public TextView mTxtSeprator;
    }
}


