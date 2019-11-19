package com.officework.adapters;


import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.officework.R;
import com.officework.customViews.IconView;
import com.officework.models.RequestPermissionModel;


import java.util.ArrayList;


public class PermissionsScreenAdapter extends RecyclerView.Adapter<PermissionsScreenAdapter.RecyclerViewHolder>{

    Context mContext;
    ArrayList<RequestPermissionModel> list;



    public PermissionsScreenAdapter(ArrayList<RequestPermissionModel> list, Context requestAllPermissionsActivity) {
        this.list=list;
        this.mContext=requestAllPermissionsActivity;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(mContext);
        View view=inflater.inflate(R.layout.permissionscreen_rv,null,false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
       holder.reimage.setImageResource(list.get(position).getImg());
       holder.txt.setText(list.get(position).getName());
       holder.desc.setText(list.get(position).getDescription());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder{
      IconView reimage;
        TextView txt,desc;
        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            reimage=(IconView) itemView.findViewById(R.id.img);
            txt=(TextView)itemView.findViewById(R.id.txt);
            desc=(TextView)itemView.findViewById(R.id.desc);

        }


    }
}