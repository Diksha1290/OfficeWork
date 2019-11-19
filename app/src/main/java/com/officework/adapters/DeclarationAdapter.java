package com.officework.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.officework.R;
import com.officework.customViews.IconView;
import com.officework.interfaces.DeclerationInterface;
import com.officework.models.DeclarationTest;

import java.util.ArrayList;
import java.util.HashSet;

public class DeclarationAdapter extends RecyclerView.Adapter<DeclarationAdapter.MyViewHolder> {
    
   private Context mContext;
    private ArrayList<DeclarationTest> list;

    DeclerationInterface declerationInterface;
    HashSet<String> hSet2 = new HashSet<String>();

    public DeclarationAdapter(ArrayList<DeclarationTest> list, Context context,DeclerationInterface declerationInterface) {
        this.list=list;
        this.mContext=context;
        this.declerationInterface=declerationInterface;
    }
    
    
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(mContext);
        View view=inflater.inflate(R.layout.item_declaration,null,false);
        return new MyViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {




        DeclarationTest model = list.get(position);
        String testid=list.get(position).getDeclarationID();
        int id = mContext.getResources().getIdentifier("img_"+testid, "drawable", mContext.getPackageName());
        holder.image.setImageResource(id);
        holder.txtheading.setText(model.getDeclarationTest());
        holder.txtdescription.setText(model.getDeclarationTestDetail());
        Spannable wordTwo = new SpannableString(" *");
        wordTwo.setSpan(new ForegroundColorSpan(Color.RED), 0, wordTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.txtheading.append(wordTwo);
        String helptetxt=mContext.getResources().getString(R.string.help_text);
        holder.txtdescription.append(helptetxt);

        if(model.isDeclarationValue()){

            holder.image.setColorFilter(ContextCompat.getColor(mContext, R.color.brown));
            holder.imgCheck.setColorFilter(ContextCompat.getColor(mContext, R.color.green));
            holder.txtheading.setTextColor(ContextCompat.getColor(mContext, R.color.brown));
            holder.txtdescription.setTextColor(ContextCompat.getColor(mContext, R.color.Black));
        }else{
            holder.image.setColorFilter(ContextCompat.getColor(mContext, R.color.disabledGray));
            holder.imgCheck.setColorFilter(ContextCompat.getColor(mContext, R.color.disabledGray));
            holder.txtheading.setTextColor(ContextCompat.getColor(mContext, R.color.disabledGray));
            holder.txtdescription.setTextColor(ContextCompat.getColor(mContext, R.color.disabledGray));
        }

        if(list.get(position).isDeclarationValue())
        {
            hSet2.add(list.get(position).getDeclarationID());


        }else {
            hSet2.remove(list.get(position).getDeclarationID());
            declerationInterface.decleration_proceess_complete(false);
        }

        if(hSet2.size() == list.size())

            declerationInterface.decleration_proceess_complete(true);

        else

            declerationInterface.decleration_proceess_complete(false);


    holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            if (model.isDeclarationValue()) {
                // mSelectedPosition=position;

                model.setDeclarationValue(false);
                hSet2.remove(model.getDeclarationID());

                holder.image.setColorFilter(ContextCompat.getColor(mContext, R.color.brown));
                holder.imgCheck.setColorFilter(ContextCompat.getColor(mContext, R.color.green));
                holder.txtheading.setTextColor(ContextCompat.getColor(mContext, R.color.brown));
                holder.txtdescription.setTextColor(ContextCompat.getColor(mContext, R.color.Black));

            } else if (!model.isDeclarationValue()) {

                model.setDeclarationValue(true);
                hSet2.add(model.getDeclarationID());
                holder.image.setColorFilter(ContextCompat.getColor(mContext, R.color.disabledGray));
                holder.imgCheck.setColorFilter(ContextCompat.getColor(mContext, R.color.disabledGray));
                holder.txtheading.setTextColor(ContextCompat.getColor(mContext, R.color.disabledGray));
                holder.txtdescription.setTextColor(ContextCompat.getColor(mContext, R.color.disabledGray));
            }
            if(hSet2.size()==list.size())
            {
                declerationInterface.decleration_proceess_complete(true);
            }else {
                declerationInterface.decleration_proceess_complete(false);
            }
            Log.d("valuemy", String.valueOf(hSet2.size()));

            notifyItemChanged(position);
        }
    });


    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
        
    }
    
//    public void setItemChecked(int position) {
//        this.mSelectedPosition = position;
//        notifyDataSetChanged();
//    }
    
    
    public class MyViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout relativeLayout;
        IconView image;
        TextView txtheading,txtdescription;
        ImageView imgCheck;
        
        public MyViewHolder(View itemView) {
            super(itemView);
            relativeLayout=(RelativeLayout) itemView.findViewById(R.id.rel_main);
            
            image=(IconView) itemView.findViewById(R.id.icon_memory);
            txtheading=(TextView)itemView.findViewById(R.id.txt_heading);
            txtdescription=(TextView)itemView.findViewById(R.id.txt_description);
            imgCheck=(ImageView)itemView.findViewById(R.id.icon_check);

        }
    }
}
