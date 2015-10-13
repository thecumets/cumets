package com.dciets.cumets.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.dciets.cumets.R;
import com.dciets.cumets.model.Roommate;
import com.dciets.cumets.utils.CircleTransform;
import com.dciets.cumets.utils.DatabaseHelper;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by marc-antoinehinse on 2015-10-10.
 */
public class RoommateAdapter extends ArrayAdapter<Roommate> {
    private Context mContext;
    private LayoutInflater mInflater = null;

    private ArrayList<Roommate> roommates;

    public RoommateAdapter(Context context, ArrayList<Roommate> roommates) {
        super(context, R.layout.roommate_item, roommates);
        mContext = context;
        this.roommates = roommates;
    }

    static class ViewHolder {
        public TextView name;
        public ImageView avatar;
        public CheckBox checkBox;
    }

    private LayoutInflater getInflater(){
        if(mInflater == null)
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        return mInflater;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;

        if(convertView == null){ // Only inflating if necessary is great for performance
            rowView = getInflater().inflate(R.layout.roommate_item, parent, false);

            ViewHolder holder = new ViewHolder();
            holder.name = (TextView) rowView.findViewById(R.id.name);
            holder.avatar = (ImageView) rowView.findViewById(R.id.avatar);
            holder.checkBox = (CheckBox) rowView.findViewById(R.id.checkBox);
            rowView.setTag(holder);
        } else{
            rowView = convertView;
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();

        Roommate r = getItem(position);

        holder.name.setText(r.getName());


        if(r.getProfilePictureUrl() != null) {
            Picasso.with(getContext()).load(r.getProfilePictureUrl()).transform(new CircleTransform()).into(holder.avatar);
        }

        holder.checkBox.setChecked(DatabaseHelper.getInstance().isMonitor(r.getProfileId()));

        return rowView;
    }
}
