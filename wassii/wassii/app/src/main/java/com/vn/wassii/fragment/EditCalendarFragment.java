package com.vn.wassii.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.thao.mylibrary.GsonRequest;
import com.vn.wassii.R;
import com.vn.wassii.activity.MainActivity;
import com.vn.wassii.adapter.ScheduleAdapter;
import com.vn.wassii.dialog.PopupDateTimeEditPicker;
import com.vn.wassii.dialog.PopupDateTimePicker;
import com.vn.wassii.model.CheckLocation;
import com.vn.wassii.model.CommonReponse;
import com.vn.wassii.model.ScheduleReponse;
import com.vn.wassii.utils.Constants;
import com.vn.wassii.utils.UrlHelper;
import com.vn.wassii.utils.Utils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by rau muong on 10/01/2016.
 */
public class EditCalendarFragment extends Fragment implements PopupDateTimeEditPicker.OnPopupDateTimeEditListener {
    private View view;
    private TextView txtNext;
    private MainActivity activity;

    GoogleMap googleMap;
    MarkerOptions markerOptions;
    LatLng latLng;
    private EditText editTimeReceive, editTimeReturn, edit_location_receiver, edit_location_return;
    private SupportMapFragment supportMapFragment;
    private int type;
    private static final int RETURN = 0;
    private static final int RECEIVE = 1;
    private PopupDateTimeEditPicker dialog;
    private SharedPreferences sharedPref;
    private String[] listStringSchedule;
    private List<ScheduleReponse> listSchedule;
    private Spinner spinnerSchedule;
    private int idSchude, idOrders;
    private static final long THREE_DAY = 3 * 24 * 60 * 60 * 1000;
    private long milisecondReceiver;
    private long milisceondReturn;
    private String dateReceiver, dateReturn, timeReceiver, timeReturn, timeReceiverShow, location, shippingStreet;
    private TextView txt_note_book_service;
    SimpleDateFormat formatDateShow = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm");
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm yyyy-MM-dd");
    private Calendar calendarReceiver, calendarReturn;
    private ProgressDialog prDialog;
    private Timer timer = new Timer();
    private final long DELAY = 1000; // milliseconds
    private ImageView image_edit;
    private boolean isCheckLocation;
    private int hourReceiver, hourReturn;
    private static final long ONE = 1 * 24 * 60 * 60 * 1000;
    private static final long TWO = 2 * 24 * 60 * 60 * 1000;
    private int count;
    public static final EditCalendarFragment newInstance(int orderId, String billingStreet, String billingDate, String billingHour, String shippingDate, String shippingHour, int schedule, String shippingStreet) {
        EditCalendarFragment myFragment = new EditCalendarFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.ORDERID, orderId);
        bundle.putString(Constants.BILLINGSTREET, billingStreet);
        bundle.putString(Constants.BILLINGDATE, billingDate);
        bundle.putString(Constants.BILLINGHOUR, billingHour);
        bundle.putString(Constants.SHIPPINGDATE, shippingDate);
        bundle.putString(Constants.SHIPPINGHOUR, shippingHour);
        bundle.putInt(Constants.SCHEDULE, schedule);
        bundle.putString(Constants.SHIPPINGSTREET, shippingStreet);
        myFragment.setArguments(bundle);
        return myFragment;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_edit_calendar, container, false);
            activity = (MainActivity) getActivity();
            sharedPref = activity.getSharedPreferences(
                    getResources().getString(R.string.login_share_private),
                    Context.MODE_PRIVATE);
            txtNext = (TextView) view.findViewById(R.id.txt_next);
            FragmentManager fm = getChildFragmentManager();
            supportMapFragment = (SupportMapFragment) fm.findFragmentById(R.id.layout_map_book);
            if (supportMapFragment != null) {
                googleMap = supportMapFragment.getMap();
            }
            editTimeReceive = (EditText) view.findViewById(R.id.edit_time_receive);
            editTimeReturn = (EditText) view.findViewById(R.id.edit_time_return);
            edit_location_receiver = (EditText) view.findViewById(R.id.edit_location_receiver);
            edit_location_return = (EditText) view.findViewById(R.id.edit_location_return);
            spinnerSchedule = (Spinner) view.findViewById(R.id.spinner_calendar);
            image_edit = (ImageView) view.findViewById(R.id.image_edit_location);
            activity.setToolbar((Toolbar) view.findViewById(R.id.toolbar));
            txt_note_book_service = (TextView) view.findViewById(R.id.txt_note_book_service);
            listStringSchedule = activity.getResources().getStringArray(R.array.list_schedule);
            listSchedule = new ArrayList<ScheduleReponse>();
            for (int i = 0; i < listStringSchedule.length; i++) {
                ScheduleReponse scheduleReponse = new ScheduleReponse();
                scheduleReponse.setId(i);
                scheduleReponse.setSchedule(listStringSchedule[i]);
                listSchedule.add(scheduleReponse);
            }
            ScheduleAdapter adapter = new ScheduleAdapter(activity, R.layout.spinner_item, listSchedule);
            spinnerSchedule.setAdapter(adapter);
            spinnerSchedule.setSelection(idSchude);

            location = getArguments().getString(Constants.BILLINGSTREET, "");
            idSchude = getArguments().getInt(Constants.SCHEDULE);
            idOrders = getArguments().getInt(Constants.ORDERID);
            dateReceiver = getArguments().getString(Constants.BILLINGDATE, "");
            timeReceiver = getArguments().getString(Constants.BILLINGHOUR, "");
            dateReturn = getArguments().getString(Constants.SHIPPINGDATE, "");
            timeReturn = getArguments().getString(Constants.SHIPPINGHOUR, "");
            shippingStreet = getArguments().getString(Constants.SHIPPINGSTREET, "");
            calendarReceiver = Calendar.getInstance();
            calendarReturn = Calendar.getInstance();
            edit_location_receiver.setText(location);
            edit_location_return.setText(shippingStreet);
            initOnCLick();
            setTimeCurrentReceiver();
            setTimeCurrentReturn();
            if (location.equals("")) {
                if (googleMap != null) {
                    setMapCurrent();
                }
            } else {
                GeocoderTask geocoderTask = new GeocoderTask(0);
                geocoderTask.execute(location);
            }
        }
        count=0;


        return view;
    }

    private void setTimeCurrentReceiver() {
        try {
            calendarReceiver.setTime(sdf.parse(timeReceiver + " " + dateReceiver));
            milisecondReceiver = calendarReceiver.getTimeInMillis();
            int hour = calendarReceiver.get(Calendar.HOUR_OF_DAY);
            hourReceiver = hour;
            int minute = calendarReceiver.get(Calendar.MINUTE);
            int day = calendarReceiver.get(Calendar.DAY_OF_WEEK);
            dateReceiver = formatDate.format(calendarReceiver.getTime());
            timeReceiver = formatTime.format(calendarReceiver.getTime());
            String timeReturn = hour + ":" + minute + " - " + getDayOfWeek(day) + " - " + formatDateShow.format(calendarReceiver.getTime());
            editTimeReceive.setText(timeReturn);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private boolean checkTimeReceiver(long timeLong) {
        Calendar calendar = Calendar.getInstance();
        long time = calendar.getTimeInMillis();
        time = time + PopupDateTimePicker.TIME_AFTER_THIRTY;
        if (timeLong < time) {
            return false;
        }
        return true;
    }

    private void setTimeCurrentReturn(Calendar calendar) {

        long time = calendar.getTimeInMillis();
        time = time + PopupDateTimePicker.TIME_AFTER_THREE_DAY;
        milisceondReturn = time;
        calendar.setTimeInMillis(time);
        calendarReturn = calendar;
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        hourReturn = hour;
        int minute = calendar.get(Calendar.MINUTE);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        Utils.logE("thaond", "Day" + day);

        dateReturn = formatDate.format(calendar.getTime());
        timeReturn = formatTime.format(calendar.getTime());
        String timeReturnString = timeReturn + " - " + getDayOfWeek(day) + " - " + formatDateShow.format(calendar.getTime());
        editTimeReturn.setText(timeReturnString);
    }

    private void setTimeCurrentReturn() {

        try {
            calendarReturn.setTime(sdf.parse(timeReturn + " " + dateReturn));
            milisceondReturn = calendarReturn.getTimeInMillis();
            int hour = calendarReturn.get(Calendar.HOUR_OF_DAY);
            hourReturn=hour;
            int minute = calendarReturn.get(Calendar.MINUTE);
            int day = calendarReturn.get(Calendar.DAY_OF_WEEK);
            dateReturn = formatDate.format(calendarReturn.getTime());
            timeReturn = formatTime.format(calendarReturn.getTime());
            String timeReturn = hour + ":" + minute + " - " + getDayOfWeek(day) + " - " + formatDateShow.format(calendarReturn.getTime());
            editTimeReturn.setText(timeReturn);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private String getDayOfWeek(int day) {
        switch (day) {
            case 1:
                return activity.getResources().getString(R.string.st_sunday);
            case 2:
                return activity.getResources().getString(R.string.st_monday);
            case 3:
                return activity.getResources().getString(R.string.st_tuesday);
            case 4:
                return activity.getResources().getString(R.string.st_wednesday);
            case 5:
                return activity.getResources().getString(R.string.st_thursday);
            case 6:
                return activity.getResources().getString(R.string.st_friday);
            case 7:
                return activity.getResources().getString(R.string.st_aturday);
        }
        return "";
    }

    private void initOnCLick() {
        image_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit_location_return.setInputType(InputType.TYPE_CLASS_TEXT);
                edit_location_return.setFocusable(true);
                edit_location_return.setTextColor(activity.getResources().getColor(R.color.color_common));
                edit_location_return.setFocusableInTouchMode(true);
                edit_location_return.requestFocus();
                edit_location_return.setSelection(edit_location_return.getText().length());


            }
        });

        editTimeReceive.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    //do something
                    type = RECEIVE;
                    showDateTimeReceive();
                }
                return true;

            }
        });
        editTimeReturn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    //do something
                    type = RETURN;
                    showDateTimeReturn();
                }
                return true;

            }
        });


        txtNext.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View view) {
                                           if (Utils.hasConnection(activity)) {
                                               if (hourReturn >= 6 && hourReturn <= 21 && hourReceiver >= 6 && hourReceiver <= 21) {
                                                   doCheckLocation();
                                               } else {
                                                   Utils.showMessage(activity, activity.getResources().getString(R.string.st_about_hour));
                                               }
                                           } else {
                                               Utils.showMessage(activity, getString(R.string.no_connection_login));
                                           }


                                       }
                                   }

        );
        spinnerSchedule.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()

        {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                idSchude = listSchedule.get(pos).getId();
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        edit_location_receiver.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                edit_location_return.setText(edit_location_receiver.getText().toString());
                timer.cancel();
                timer = new Timer();
                timer.schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                GeocoderTask geocoderTask = new GeocoderTask(0);
                                geocoderTask.execute(edit_location_receiver.getText().toString());
                            }
                        },
                        DELAY
                );
            }
        });
        edit_location_return.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                timer.cancel();
                timer = new Timer();
                timer.schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                GeocoderTask geocoderTask = new GeocoderTask(1);
                                geocoderTask.execute(edit_location_return.getText().toString());
                            }
                        },
                        DELAY
                );
            }
        });
    }

    private void doBook() {
        location = edit_location_receiver.getText().toString();
        shippingStreet = edit_location_return.getText().toString();
        String headererIdOders = Constants.ORDERID + "=" + Utils.validateString(String.valueOf(idOrders));
        String headererBillingStreet = Constants.BILLINGSTREET + "=" + Utils.validateString(location);
        String headererBillingDate = Constants.BILLINGDATE + "=" + Utils.validateString(dateReceiver);
        String headererBillingHour = Constants.BILLINGHOUR + "=" + Utils.validateString(timeReceiver);
        String headererShippingDate = Constants.SHIPPINGDATE + "=" + Utils.validateString(dateReturn);
        String headererShippingHour = Constants.SHIPPINGHOUR + "=" + Utils.validateString(timeReturn);
        String headererSchedule = Constants.SCHEDULE + "=" + Utils.validateString(String.valueOf(idSchude));
        String headererShippingStreet = Constants.SHIPPINGSTREET + "=" + Utils.validateString(shippingStreet);
        String headererCouponcode = Constants.COUPONCODE + "=" + Utils.validateString("");

        RequestQueue queue = Constants.getQueue(activity);
        Map<String, String> params = new HashMap<String, String>();
        try {
            params.put(Constants.AUTHORIZATION, sharedPref.getString(Constants.TOKEN, ""));
        } catch (Exception e) {

        }
        String headers = "?" + headererIdOders + "&" + headererBillingStreet + "&" + headererBillingDate + "&" + headererBillingHour + "&" + headererShippingDate + "&" + headererShippingHour + "&" + headererSchedule + "&" + headererCouponcode + "&" + headererShippingStreet + "&" + "comments=" + "";
        Utils.logE("thaond", "heders" + headers);
        GsonRequest<CommonReponse> postRequest = new GsonRequest<CommonReponse>(
                Request.Method.GET, UrlHelper.UPDATE_ORDER + headers, CommonReponse.class, params, null,
                new Response.Listener<CommonReponse>() {
                    public void onResponse(CommonReponse response) {
                        processAfterBook(response);
                        showProgressDialog(false, "");
                        // processResponse(response);
                    }
                }, new Response.ErrorListener(

        ) {
            public void onErrorResponse(VolleyError error) {
                showProgressDialog(false, "");

                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null && networkResponse.statusCode == 401) {
                    Utils.showDialogTimeOut(activity);
                    // HTTP Status Code: 401 Unauthorized
                } else {
                    Utils.showMessage(activity,
                            getResources().getString(R.string.error));
                }
            }

        });
        queue.add(postRequest);
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constants.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void processAfterBook(CommonReponse commonReponse) {
        if (commonReponse.getException() == null) {
            activity.getSupportFragmentManager().popBackStack();
            activity.selectFragment(CalendarWassiiFragment.newInstance());
            Utils.showMessage(activity,
                    commonReponse.getMessage());
        } else {
            Utils.showMessage(activity,
                    commonReponse.getMessage());
        }
    }

    private void setMapCurrent() {
            // Do a null check to confirm that we have not already instantiated the map.

            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            googleMap.setMyLocationEnabled(true);
            // Check if we were successful in obtaining the map.


            googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

                @Override
                public void onMyLocationChange(Location location) {
                    // TODO Auto-generated method stub
                    if(count==0) {
                        if (location != null) {
                            try {
                                Geocoder geo = new Geocoder(activity.getApplicationContext(), Locale.getDefault());
                                List<Address> addresses = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 3);
                                for (int i = 0; i < addresses.size(); i++) {
                                    if (i == 0) {

                                        Address address = (Address) addresses.get(i);
//
                                        // Creating an instance of GeoPoint, to display in Google Map
                                        latLng = new LatLng(address.getLatitude(), address.getLongitude());
                                        String addressLine = address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "";
                                        addressLine = addressLine + " " + address.getSubAdminArea() + " " + address.getAdminArea() + " " + address.getCountryName();
                                        edit_location_receiver.setText(addressLine);
                                        edit_location_return.setText(addressLine);
                                        markerOptions = new MarkerOptions();
                                        markerOptions.position(latLng);
                                        markerOptions.title(addressLine);
                                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_location));
                                        googleMap.addMarker(markerOptions);
                                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
                                        count++;
                                    }

                                }

                            } catch (Exception e) {
                                e.printStackTrace(); // getFromLocation() may sometimes fail
                            }
                            count++;
                        }
                    }
                }
            });

    }

    private void showDateTimeReturn() {
        dialog = new PopupDateTimeEditPicker(activity, "", this, 1, calendarReturn);
        dialog.show();
    }

    private void showDateTimeReceive() {
        dialog = new PopupDateTimeEditPicker(activity, "", this, 0, calendarReceiver);
        dialog.show();
    }


    @Override
    public void onConfirmOK(String date, String time, long timeMiliSecond) {
        Calendar calendarReceiver = Calendar.getInstance();
        calendarReceiver.setTimeInMillis(timeMiliSecond);
        int day = calendarReceiver.get(Calendar.DAY_OF_WEEK);

        timeReturn = formatTime.format(calendarReceiver.getTime());
        String timeString = timeReturn + " - " + getDayOfWeek(day) + " - " + formatDateShow.format(calendarReceiver.getTime());
        if (type == RECEIVE) {
            hourReceiver = calendarReceiver.get(Calendar.HOUR_OF_DAY);
            if (checkTimeReceiver(timeMiliSecond)) {
                milisecondReceiver = timeMiliSecond;
                dateReceiver = date;
                timeReceiver = time;
                editTimeReceive.setText(timeString);
                setTimeCurrentReturn(calendarReceiver);
                dialog.dismiss();
            } else {
                Utils.showMessage(activity, activity.getResources().getString(R.string.st_error_time_receiver));
            }

        } else {
            hourReturn = calendarReceiver.get(Calendar.HOUR_OF_DAY);
            milisceondReturn = timeMiliSecond;
            if (!checkTimeReturn()) {
                dateReturn = date;
                timeReturn = time;
                editTimeReturn.setText(timeString);
                dialog.dismiss();
                Utils.logE("thaond", "milisecondReceiver" + milisecondReceiver);
                Utils.logE("thaond", "TWO" + TWO);
                Utils.logE("thaond", "milisceondReturn" + milisceondReturn);
                long timeRe = milisceondReturn - milisecondReceiver;
                if (timeRe <= TWO) {
                    Utils.logE("thaond", "run1");
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(milisceondReturn);
                    if (calendar.get(Calendar.HOUR_OF_DAY) < 22) {
                        Utils.logE("thaond", "run2");
                        txt_note_book_service.setVisibility(View.VISIBLE);
                        Utils.showMessage(activity, activity.getResources().getString(R.string.st_note_book_service));
                    } else {
                        Utils.logE("thaond", "run3");

                        txt_note_book_service.setVisibility(View.GONE);
                       // Utils.showMessage(activity, activity.getResources().getString(R.string.st_note_book_service));
                    }

                } else {
                    txt_note_book_service.setVisibility(View.GONE);
                }
            } else {
                Utils.showMessage(activity, activity.getResources().getString(R.string.st_error_time_return));

            }

        }

    }

    @Override
    public void onConfirmCancel() {
        dialog.dismiss();

    }

    private boolean checkTimeReturn() {
        long time = milisceondReturn - milisecondReceiver;
        if (time < ONE) {
            return true;
        }
        return false;
    }

    // An AsyncTask class for accessing the GeoCoding Web Service
    private class GeocoderTask extends AsyncTask<String, Void, List<Address>> {
        private int type;

        public GeocoderTask(int type) {
            this.type = type;
        }

        @Override
        protected List<Address> doInBackground(String... locationName) {
            // Creating an instance of Geocoder class
            Geocoder geocoder = new Geocoder(activity);
            List<Address> addresses = null;

            try {
                // Getting a maximum of 3 Address that matches the input text
                addresses = geocoder.getFromLocationName(locationName[0], 3);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return addresses;
        }

        @Override
        protected void onPostExecute(List<Address> addresses) {

            if (googleMap != null) {
                // Clears all the existing markers on the map
                googleMap.clear();
                // Adding Markers on Google Map for each matching address

                if (addresses != null) {
                    for (int i = 0; i < addresses.size(); i++) {
                        if (i == 0) {

                            Address address = (Address) addresses.get(i);
//
                            // Creating an instance of GeoPoint, to display in Google Map
                            latLng = new LatLng(address.getLatitude(), address.getLongitude());
                            String addressLine = address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "";
                            addressLine = addressLine + " " + address.getSubAdminArea() + " " + address.getAdminArea() + " " + address.getCountryName();
//                        if(type==0){
//                            edit_location_receiver.setText(addressLine);
//
//                        }else{
//                            edit_location_return.setText(addressLine);
//                        }
                            markerOptions = new MarkerOptions();
                            markerOptions.position(latLng);
                            markerOptions.title(addressLine);
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_location));
                            googleMap.addMarker(markerOptions);
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
                        }

                    }
                }
            }


        }
    }

    private void showProgressDialog(boolean show, String message) {
        if (show) {
            if (prDialog == null) {
                prDialog = new ProgressDialog(activity);
            }
            if (message != null) {
                prDialog.setMessage(message);
            }

            prDialog.setCancelable(false);
            prDialog.show();
        } else {
            if (prDialog != null) {
                prDialog.dismiss();

            }
        }
    }

    private void doCheckLocation() {
        location = edit_location_receiver.getText().toString();
        String headererBillingStreet = Constants.LOCATION + "=" + Utils.validateString(location);

        showProgressDialog(true, getString(R.string.contact_please_wait));
        RequestQueue queue = Constants.getQueue(activity);
        Map<String, String> params = new HashMap<String, String>();
        try {
            params.put(Constants.AUTHORIZATION, sharedPref.getString(Constants.TOKEN, ""));
        } catch (Exception e) {

        }
        String headers = "?" + headererBillingStreet;
        Utils.logE("thaond", "heders" + headers);
        GsonRequest<CheckLocation> postRequest = new GsonRequest<CheckLocation>(
                Request.Method.GET, UrlHelper.CHECK_LOCATION + headers, CheckLocation.class, params, null,
                new Response.Listener<CheckLocation>() {
                    public void onResponse(CheckLocation response) {
                        processAfterBook(response);
                        // processResponse(response);
                    }
                }, new Response.ErrorListener(

        ) {
            public void onErrorResponse(VolleyError error) {
                showProgressDialog(false, "");

                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null && networkResponse.statusCode == 401) {
                    Utils.showDialogTimeOut(activity);
                    // HTTP Status Code: 401 Unauthorized
                } else {
                    Utils.showMessage(activity,
                            getResources().getString(R.string.error));
                }
            }

        });
        queue.add(postRequest);
    }

    private void processAfterBook(CheckLocation reLocation) {
        isCheckLocation = reLocation.isResult();
        if (isCheckLocation == true) {
            if (edit_location_receiver.getText().toString().length() > 0 && editTimeReceive.getText().toString().length() > 0 && editTimeReturn.getText().toString().length() > 0) {
                if (Utils.hasConnection(activity)) {
                    doBook();
                } else {
                    Utils.showMessage(activity, getString(R.string.no_connection_login));
                }
            } else {
                Utils.showMessage(activity, activity.getResources().getString(R.string.common_message_fail));
            }
        } else {
            Utils.showMessage(activity, activity.getResources().getString(R.string.st_check_location));

        }

    }

}

