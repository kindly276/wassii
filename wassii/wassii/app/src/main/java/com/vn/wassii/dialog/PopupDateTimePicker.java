package com.vn.wassii.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.vn.wassii.R;
import com.vn.wassii.activity.MainActivity;
import com.vn.wassii.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import tw.g35g.widget.OnWheelChangedListener;
import tw.g35g.widget.WheelView;
import tw.g35g.widget.adapters.ArrayWheelAdapter;
import tw.g35g.widget.adapters.NumericWheelAdapter;


/**
 * Created by PC0280 on 1/9/2016.
 */
public class PopupDateTimePicker extends Dialog {

    private String des;

    private Context context;
    public static final long TIME_AFTER_THIRTY = 30 * 60 * 1000;
    public static final long ONE_DAY = 1 * 24 * 60 * 60 * 1000;
    public static final long TIME_AFTER_THREE_DAY = 3 * 24 * 60 * 60 * 1000;
    // Declare listener
    private OnPopupDateTimeListener listener;
    private int type;
    private long milisecondReceiver;
    private  WheelView monthView;
    private WheelView wheelView;
    private WheelView dayView;
    private WheelView hoursView;
    private WheelView minsView;
    private WheelView yearView;
    public PopupDateTimePicker(MainActivity context, String des, OnPopupDateTimeListener listener, int type) {
        super(context);
        this.context = context;
        this.des = des;
        this.listener = listener;
        this.type = type;
    }
    public PopupDateTimePicker(MainActivity context, String des, OnPopupDateTimeListener listener, int type,long milisecondReceiver) {
        super(context);
        this.context = context;
        this.des = des;
        this.listener = listener;
        this.type = type;
        this.milisecondReceiver=milisecondReceiver;
    }

    public PopupDateTimePicker(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        // No Window Title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setCancelable(false);
        // Set Layout
        this.setContentView(R.layout.popup_date_time);
        monthView = (WheelView) findViewById(R.id.month);
        wheelView = (WheelView) findViewById(R.id.year);
        dayView = (WheelView) findViewById(R.id.day);
        hoursView = (WheelView) findViewById(R.id.hour);
        minsView = (WheelView) findViewById(R.id.mins);
        yearView = (WheelView) findViewById(R.id.year);

        NumericWheelAdapter hourAdapter = new NumericWheelAdapter(context, 0, 23);
        hourAdapter.setItemResource(R.layout.wheel_text_item);
        hourAdapter.setItemTextResource(R.id.text);
        hoursView.setViewAdapter(hourAdapter);

        final WheelView mins = (WheelView) findViewById(R.id.mins);
        NumericWheelAdapter minAdapter = new NumericWheelAdapter(context, 0, 59, "%02d");
        minAdapter.setItemResource(R.layout.wheel_text_item);
        minAdapter.setItemTextResource(R.id.text);
        mins.setViewAdapter(minAdapter);
        mins.setCyclic(true);

        ArrayWheelAdapter<String> ampmAdapter =
                new ArrayWheelAdapter<String>(context, new String[] {"AM", "PM"});
        ampmAdapter.setItemResource(R.layout.wheel_text_item);
        ampmAdapter.setItemTextResource(R.id.text);
        setDateCurrent();


        TextView ok = (TextView) findViewById(R.id.btn_dialog_note_ok);
        TextView cancel = (TextView) findViewById(R.id.btn_dialog_note_cancel);

        // Set on clicked
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int day = dayView.getCurrentItem()+1;
                int month = monthView.getCurrentItem();
                int year = yearView.getCurrentItem();
                int hour = hoursView.getCurrentItem();
                int minute = minsView.getCurrentItem();
                calendar.set(year, month, day, hour, minute,0);
                SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm");
                String date = formatDate.format(calendar.getTime());
                String time = formatTime.format(calendar.getTime());
                Utils.logE("thaond", "date" + date);
                Utils.logE("thaond", "time" + time);
                if (hour >= 6 && hour <= 21) {
                    listener.onConfirmOK(date, time, calendar.getTimeInMillis());
                } else {
                    Utils.showMessage(context, context.getResources().getString(R.string.st_about_hour));
                }

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listener.onConfirmCancel();
            }
        });

    }

    private String getDayOfWeek(int day) {
        switch (day) {
            case 0:
                return context.getResources().getString(R.string.st_sunday);
            case 1:
                return context.getResources().getString(R.string.st_monday);
            case 2:
                return context.getResources().getString(R.string.st_tuesday);
            case 3:
                return context.getResources().getString(R.string.st_wednesday);
            case 4:
                return context.getResources().getString(R.string.st_thursday);
            case 5:
                return context.getResources().getString(R.string.st_friday);
            case 6:
                return context.getResources().getString(R.string.st_aturday);
        }
        return "";
    }

    private void setDateCurrent() {
        OnWheelChangedListener listener = new OnWheelChangedListener() {
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                updateDays(yearView, monthView, dayView);
            }
        };
        Calendar c;
        if (type == 0) {
            c = getTimeAfterThirty();
        } else {
            c = getTimeAfterThreeDay();
        }
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        //monthView
        String months[] = context.getResources().getStringArray(R.array.list_month);
        monthView.setViewAdapter(new DateArrayAdapter(context, months, month));
        monthView.setCurrentItem(month);
        monthView.addChangingListener(listener);

        // year
        yearView.setViewAdapter(new DateNumericAdapter(context, 0, year + 10, 0));
        yearView.setCurrentItem(year);
        yearView.addChangingListener(listener);

        //day
        updateDays(yearView, monthView, dayView);
        dayView.setCurrentItem(day-1);

        hoursView.setCurrentItem(c.get(Calendar.HOUR_OF_DAY));
        minsView.setCurrentItem(c.get(Calendar.MINUTE));
    }


    private Calendar getTimeAfterThirty() {
        Calendar c = Calendar.getInstance();
        long time = c.getTimeInMillis();
        time = time + TIME_AFTER_THIRTY;
        c.setTimeInMillis(time);
        return c;
    }

    private Calendar getTimeAfterThreeDay() {
        Calendar c = Calendar.getInstance();
        long time=0;
        if(milisecondReceiver>0){
            time = milisecondReceiver;
        }else{
            time = c.getTimeInMillis();

        }
        time = time + TIME_AFTER_THREE_DAY;
        c.setTimeInMillis(time);
        return c;
    }


    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public interface OnPopupDateTimeListener {
        void onConfirmOK(String date, String time, long timeMiliSecond);

        void onConfirmCancel();
    }

    private class DateArrayAdapter extends ArrayWheelAdapter<String> {
        // Index of current item
        int currentItem;
        // Index of item to be highlighted
        int currentValue;

        /**
         * Constructor
         */
        public DateArrayAdapter(Context context, String[] items, int current) {
            super(context, items);
            this.currentValue = current;
            setTextSize(14);
        }

        @Override
        protected void configureTextView(TextView view) {
            super.configureTextView(view);
            if (currentItem == currentValue) {
                view.setTextColor(0xFF0000F0);
            }
            view.setTypeface(Typeface.SANS_SERIF);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            currentItem = index;
            return super.getItem(index, cachedView, parent);
        }
    }

    /**
     * Adapter for numeric wheels. Highlights the current value.
     */
    private class DateNumericAdapter extends NumericWheelAdapter {
        // Index of current item
        int currentItem;
        // Index of item to be highlighted
        int currentValue;

        /**
         * Constructor
         */
        public DateNumericAdapter(Context context, int minValue, int maxValue, int current) {
            super(context, minValue, maxValue);
            this.currentValue = current;
            setTextSize(14);
        }

        @Override
        protected void configureTextView(TextView view) {
            super.configureTextView(view);
            if (currentItem == currentValue) {
                view.setTextColor(0xFF0000F0);
            }
            view.setTypeface(Typeface.SANS_SERIF);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            currentItem = index;
            return super.getItem(index, cachedView, parent);
        }
    }
    /**
     * Updates day wheel. Sets max days according to selected month and year
     */
    void updateDays(WheelView year, WheelView month, WheelView day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + year.getCurrentItem());
        calendar.set(Calendar.MONTH, month.getCurrentItem());

        int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        day.setViewAdapter(new DateNumericAdapter(context, 1, maxDays, calendar.get(Calendar.DAY_OF_MONTH) - 1));
        int curDay = Math.min(maxDays, day.getCurrentItem() + 1);
        day.setCurrentItem(curDay - 1, true);
    }

}
