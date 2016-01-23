package com.vn.wassii.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.vn.wassii.R;
import com.vn.wassii.activity.MainActivity;
import com.vn.wassii.model.ScheduleReponse;

import java.util.List;

/**
 * Created by rau muong on 09/01/2016.
 */
public class ScheduleAdapter extends ArrayAdapter<ScheduleReponse> {

    private List<ScheduleReponse> objects;
    private MainActivity context;

    public ScheduleAdapter(MainActivity context, int resourceId,
                           List<ScheduleReponse> objects) {
        super(context,resourceId,objects);
        this.objects = objects;
        this.context = context;
    }



    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater=(LayoutInflater) context.getSystemService(  Context.LAYOUT_INFLATER_SERVICE );
        View row=inflater.inflate(R.layout.spinner_item, parent, false);
        TextView label=(TextView)row.findViewById(R.id.txtnamecategory);
        label.setText(objects.get(position).getSchedule().toString());

        return row;
    }
}