package com.vn.wassii.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vn.wassii.R;
import com.vn.wassii.activity.MainActivity;
import com.vn.wassii.fragment.EditCalendarFragment;
import com.vn.wassii.model.CalendarWassii;

import java.util.List;

/**
 * Created by rau muong on 10/01/2016.
 */
public class CalendarWassiiAdapter extends RecyclerView.Adapter {


    private List<CalendarWassii> MessageList;
    private MainActivity mainActivity;

    public CalendarWassiiAdapter(List<CalendarWassii> Messages, MainActivity mainActivity) {
        this.MessageList = Messages;
        this.mainActivity = mainActivity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_calendar_wassii, parent, false);

        vh = new CalendarWassiiViewHolder(v, mainActivity);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CalendarWassiiViewHolder) {

            CalendarWassii calendarWassii = (CalendarWassii) MessageList.get(position);

            ((CalendarWassiiViewHolder) holder).txt_location.setText(calendarWassii.getShippingStreet());
            String schedue = "";
            if (calendarWassii.getSchedule() == 0) {
                schedue = mainActivity.getResources().getString(R.string.st_once);
            } else if (calendarWassii.getSchedule() == 1) {
                schedue = mainActivity.getResources().getString(R.string.st_one_week);
            } else if (calendarWassii.getSchedule() == 2) {
                schedue = mainActivity.getResources().getString(R.string.st_two_week);

            } else if (calendarWassii.getSchedule() == 3) {
                schedue = mainActivity.getResources().getString(R.string.st_three_week);

            } else if (calendarWassii.getSchedule() == 3) {
                schedue = mainActivity.getResources().getString(R.string.st_four_week);

            }
            ((CalendarWassiiViewHolder) holder).txt_schedule.setText(schedue);
            ((CalendarWassiiViewHolder) holder).txt_time_receiver.setText(calendarWassii.getBillingHour() + " - " + calendarWassii.getBillingDate());
            ((CalendarWassiiViewHolder) holder).txt_time_return.setText(calendarWassii.getShippingHour() + " - " + calendarWassii.getShippingDate());
            ((CalendarWassiiViewHolder) holder).calendarWassii = calendarWassii;
            if (calendarWassii.getOrderStatus().equals("checkout")) {
                ((CalendarWassiiViewHolder) holder).image_edit.setVisibility(View.VISIBLE);
                ((CalendarWassiiViewHolder) holder).image_status.setImageResource(R.mipmap.ic_one_check);
            } else {
                if (calendarWassii.getOrderStatus().equals("pending")){
                    ((CalendarWassiiViewHolder) holder).image_status.setImageResource(R.mipmap.ic_two_check);
                }else{
                    ((CalendarWassiiViewHolder) holder).image_status.setImageResource(R.mipmap.ic_three_check);
                }
                ((CalendarWassiiViewHolder) holder).image_edit.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public int getItemCount() {
        if (MessageList != null) {
            return MessageList.size();
        }
        return 0;


    }

    //
    public class CalendarWassiiViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_schedule, txt_location, txt_time_receiver, txt_time_return;

        public CalendarWassii calendarWassii;
        public ImageView image_edit,image_status;

        public CalendarWassiiViewHolder(View v, final MainActivity activity) {
            super(v);
            txt_schedule = (TextView) v.findViewById(R.id.txt_schedule);

            txt_location = (TextView) v.findViewById(R.id.txt_location);
            txt_time_receiver = (TextView) v.findViewById(R.id.txt_time_receiver);
            txt_time_return = (TextView) v.findViewById(R.id.txt_time_return);
            image_edit = (ImageView) v.findViewById(R.id.image_edit);
            image_status = (ImageView) v.findViewById(R.id.image_status);
            image_edit.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    activity.selectFragment(EditCalendarFragment.newInstance(calendarWassii.getOrderId(), calendarWassii.getBillingStreet(), calendarWassii.getBillingDate(), calendarWassii.getBillingHour(), calendarWassii.getShippingDate(), calendarWassii.getShippingHour(), calendarWassii.getSchedule(), calendarWassii.getShippingStreet()));
                }
            });

        }
    }


}
