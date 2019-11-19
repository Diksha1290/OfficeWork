package com.officework.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.officework.R;
import com.officework.constants.JsonTags;
import com.officework.models.drawerItemModel;
import com.officework.utils.Utilities;

import java.util.List;

/**
 * Created by girish.sharma on 7/28/2016.
 */
public class DrawerListAdapter extends ArrayAdapter<drawerItemModel> {

    Context ctx;
    List<drawerItemModel> objects;
    LayoutInflater mLayoutInflater;
    Utilities utilities;
    private int mSelectedPosition=-1;

    public DrawerListAdapter(Context context, int resource, List<drawerItemModel> objects) {
        super(context, resource, objects);
        ctx = context;
        this.objects = objects;
        mLayoutInflater = LayoutInflater.from(context);
        utilities = Utilities.getInstance(context);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.drawer_item_row, null);
            holder = new ViewHolder();
            holder.Name = (TextView) convertView.findViewById(R.id.txtTitle);
            holder.Icon = (ImageView) convertView.findViewById(R.id.imgIcon);
            holder.llDivider = (LinearLayout) convertView.findViewById(R.id.llDivider);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        drawerItemModel data = objects.get(position);
        holder.Icon.setImageResource(data.getResource());
        holder.Name.setText(data.getTitle());

       /* if (position == 3 || position == 6) {
            holder.llDivider.setVisibility(View.VISIBLE);
        } else {*/
            holder.llDivider.setVisibility(View.GONE);
       /* }*/

        /**
         * If user has declined terms and conditions then DeviceInfo should be show as disabled
         */
        if (utilities.getPreferenceBoolean(ctx, JsonTags.isUserDeclined.name(), true)) {
            if (position == mSelectedPosition) {
                convertView.setBackgroundColor(Color.parseColor("#33808080"));
            } else {
                convertView.setBackgroundColor(Color.parseColor("#ffffff"));
            }
        } else {
            convertView.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        holder.llDivider.setBackgroundColor(Color.parseColor("#ffffff"));
       /* if (utilities.getPreferenceBoolean(ctx, JsonTags.isUserDeclined.name(), false)) {


        }*/


        return convertView;
    }


    public void setItemChecked(int position){

        this.mSelectedPosition = position;
        notifyDataSetChanged();
    }
    public static class ViewHolder {
        TextView Name;
        ImageView Icon;
        LinearLayout llDivider;

    }
}
