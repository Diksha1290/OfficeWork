package com.officework.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.officework.R;
import com.officework.models.DocumentsTypePojo;

import java.util.ArrayList;

public class SpinnerAdapter extends ArrayAdapter {

    private Context context;
    //ArrayList<JobCardModel> spinnerModelList;

    ArrayList<DocumentsTypePojo> documentsTypePojosList;
    LayoutInflater inflter;

    public SpinnerAdapter(Context context, int textViewResourceId, ArrayList<DocumentsTypePojo> itemList) {

        super(context, textViewResourceId,itemList);
        this.context=context;
        this.documentsTypePojosList=itemList;
        inflter = (LayoutInflater.from(context));

    }
    @Override
    public boolean isEnabled(int position) {
        if (position == 0) {
            // Disable the first item from Spinner
            // First item will be use for hint
            return false;
        } else {
            return true;
        }
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView label = (TextView) super.getView(position, convertView, parent);

//        View view = super.getView(position, convertView, parent);
//        TextView tv = (TextView) view;

        label.setText(documentsTypePojosList.get(position).getIdentityValue());


        return label;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        View view=  inflter.inflate(R.layout.spinner_layout, null);
        TextView label = (TextView) view.findViewById(R.id.spin_text);
//        label.setTextColor(Color.BLACK);
        label.setText(documentsTypePojosList.get(position).getIdentityValue());

        return view;
    }


    @Override
    public int getCount(){
        return documentsTypePojosList.size();
    }
}
