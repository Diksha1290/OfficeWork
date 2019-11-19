package com.officework.adapters;

import android.content.Context;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.officework.R;
import com.officework.interfaces.InterfaceExceptionTableCallback;
import com.officework.models.LogExceptionModel;

import java.util.List;

/**
 * Created by Ashwani on 7/3/2017.
 */

public class ExceptionTableAdapter extends RecyclerView.Adapter<ExceptionTableAdapter.MyViewHolder> {

    private List<LogExceptionModel> mListExceptionModel;
    Context mContext;
    InterfaceExceptionTableCallback categoryClick;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtViewUDI;
        public TextView txtViewMethodName;
        public TextView txtViewTimeStamp;
        public CardView cardView;

        public MyViewHolder(View view) {
            super(view);
            txtViewUDI = (TextView) view.findViewById(R.id.txtViewUDI);
            txtViewMethodName = (TextView) view.findViewById(R.id.txtViewMethodName);
            txtViewTimeStamp = (TextView) view.findViewById(R.id.txtViewTimeStamp);
            cardView = (CardView) view.findViewById(R.id.cardViewExceptionTable);
        }
    }


    public ExceptionTableAdapter(Context ctx, List<LogExceptionModel> logExceptionModels,
                                 InterfaceExceptionTableCallback adapterCategoryClick) {
        this.mListExceptionModel = logExceptionModels;
        categoryClick = adapterCategoryClick;
        mContext = ctx;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exception_table, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final LogExceptionModel products = mListExceptionModel.get(position);
        holder.txtViewUDI.setText(products.getUDI());
        holder.txtViewMethodName.setText(products.getMethodName());
        holder.txtViewTimeStamp.setText(products.getExceptionDateTime());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryClick.onItemClickCallBack(position, mListExceptionModel);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListExceptionModel.size();
    }
}
