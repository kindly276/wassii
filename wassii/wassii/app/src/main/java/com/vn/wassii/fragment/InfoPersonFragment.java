package com.vn.wassii.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
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
import com.vn.wassii.activity.ChangePasswordActivity;
import com.vn.wassii.activity.LoginActivity;
import com.vn.wassii.activity.MainActivity;
import com.vn.wassii.dialog.PopupConfirm;
import com.vn.wassii.model.LoginReponse;
import com.vn.wassii.utils.Constants;
import com.vn.wassii.utils.UrlHelper;
import com.vn.wassii.utils.Utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by rau muong on 01/01/2016.
 */
public class InfoPersonFragment extends Fragment {
    private View view;
    private SharedPreferences sharedPref;
    private MainActivity mainActivity;
    private String name, phone, address;
    private EditText editName, editPhone, editAddress;
    private TextView txtLogout, txtEdit, txtSave, txtChange;
    private LinearLayout layout_change, layout_logout, layout_edit, layout_save;
    private ProgressDialog prDialog;
    private PopupConfirm confirm;
    GoogleMap googleMap;
    MarkerOptions markerOptions;
    LatLng latLng;
    private SupportMapFragment supportMapFragment;
    private Timer timer = new Timer();
    private final long DELAY = 1000; // milliseconds
    private int count;

    public static final InfoPersonFragment newInstance() {

        InfoPersonFragment myFragment = new InfoPersonFragment();
        return myFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            mainActivity = (MainActivity) getActivity();
            sharedPref = mainActivity.getSharedPreferences(
                    getResources().getString(R.string.login_share_private),
                    Context.MODE_PRIVATE);
            view = inflater.inflate(R.layout.fragment_info_person, container, false);
            FragmentManager fm = getChildFragmentManager();
            supportMapFragment = (SupportMapFragment) fm.findFragmentById(R.id.layout_map_info);
            if (supportMapFragment != null) {
                googleMap = supportMapFragment.getMap();
            }
            mainActivity.setToolbar((Toolbar) view.findViewById(R.id.toolbar));
            name = sharedPref.getString(Constants.FULLNAME, "");
            phone = sharedPref.getString(Constants.PHONE, "");
            address = sharedPref.getString(Constants.ADDRESS, "");
            editName = (EditText) view.findViewById(R.id.edit_name);
            editPhone = (EditText) view.findViewById(R.id.txt_phone);
            editAddress = (EditText) view.findViewById(R.id.txt_address);
            layout_change = (LinearLayout) view.findViewById(R.id.layout_change);
            layout_edit = (LinearLayout) view.findViewById(R.id.layout_edit);
            layout_logout = (LinearLayout) view.findViewById(R.id.layout_logout);
            layout_save = (LinearLayout) view.findViewById(R.id.layout_save);
            txtLogout = (TextView) view.findViewById(R.id.txtLogout);
            txtEdit = (TextView) view.findViewById(R.id.txt_edit);
            txtSave = (TextView) view.findViewById(R.id.txt_save);
            txtChange = (TextView) view.findViewById(R.id.txt_change);
            layout_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editName.setInputType(InputType.TYPE_CLASS_TEXT);
                    editAddress.setInputType(InputType.TYPE_CLASS_TEXT);
                    editName.setBackgroundResource(R.drawable.bg_login_corner_user_name);
                    editAddress.setBackgroundResource(R.drawable.bg_login_corner_user_name);
                    editName.setPadding(10, 10, 10, 10);
                    editAddress.setPadding(10, 10, 10, 10);
                    layout_edit.setVisibility(View.GONE);
                    layout_save.setVisibility(View.VISIBLE);
                }
            });
            layout_change.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mainActivity, ChangePasswordActivity.class);
                    intent.putExtra(Constants.TYPE, 1);
                    startActivity(intent);
                    ((MainActivity) mainActivity).overridePendingTransition(R.anim.anim_in,
                            R.anim.anim_out);
                }
            });
            layout_logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialogLogoutConfirm();
                }
            });

            layout_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    doSave(editName.getText().toString(), editAddress.getText().toString());
                }
            });
            editName.setText(name);
            editPhone.setText(phone);
            editAddress.setText(address);
            Utils.logE("thaond", "address" + address);
            if (address.equals("")) {
                if (googleMap != null) {
                    setMapCurrent();
                }
            } else if (address != null && !address.equals("") && googleMap != null) {
                new GeocoderTask().execute(address);
            }
            editAddress.addTextChangedListener(new TextWatcher() {
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
                                    GeocoderTask geocoderTask = new GeocoderTask();
                                    geocoderTask.execute(editAddress.getText().toString());
                                }
                            },
                            DELAY
                    );
                }
            });
        }
        count = 0;

        return view;
    }

    private void doSave(String fullName, String address) {
        showProgressDialog(true, getString(R.string.contact_please_wait));
        RequestQueue queue = Constants.getQueue(mainActivity);
        Map<String, String> params = new HashMap<String, String>();
        try {
            params.put(Constants.AUTHORIZATION, sharedPref.getString(Constants.TOKEN, ""));
        } catch (Exception e) {

        }


        String headers = "?" + Constants.FULLNAME + "=" + Utils.validateString(fullName) + "&" + Constants.ADDRESS + "=" + Utils.validateString(address) + "&" + Constants.LANGUAGEID + "=" + Constants.VN_LANGUAGE;
        Log.e("thaond", "headers" + headers);
        GsonRequest<LoginReponse> postRequest = new GsonRequest<LoginReponse>(
                Request.Method.GET, UrlHelper.UPDATE_USER_INFO + headers, LoginReponse.class, params, null,
                new Response.Listener<LoginReponse>() {
                    public void onResponse(LoginReponse response) {
                        procesAfterLogin(response);
                        showProgressDialog(false, "");
                        // processResponse(response);
                    }
                }, new Response.ErrorListener(

        ) {
            public void onErrorResponse(VolleyError error) {
                showProgressDialog(false, "");
                Utils.showMessage(mainActivity,
                        getResources().getString(R.string.error));

            }
        });
        queue.add(postRequest);
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constants.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void procesAfterLogin(LoginReponse response) {
        Log.e("thaond", "response" + response.getException());
        if (response.getException() == null) {
            try {
                editAddress.setInputType(InputType.TYPE_NULL);
                editName.setInputType(InputType.TYPE_NULL);
                editAddress.setBackgroundColor(mainActivity.getResources().getColor(R.color.white));
                editName.setBackgroundColor(mainActivity.getResources().getColor(R.color.white));
                editName.setPadding(20, 10, 10, 10);
                editAddress.setPadding(20, 10, 10, 10);
                layout_edit.setVisibility(View.VISIBLE);
                layout_save.setVisibility(View.GONE);
                Utils.showMessage(mainActivity,
                        response.getMessage());
                Utils.saveAddress(sharedPref, response);
            } catch (Exception e) {
                Utils.showMessage(mainActivity,
                        getResources().getString(R.string.error));
            }
        } else {
            Utils.showMessage(mainActivity,
                    response.getMessage());
        }
    }

    private void showProgressDialog(boolean show, String message) {
        if (show) {
            if (prDialog == null) {
                prDialog = new ProgressDialog(mainActivity);
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

    private void showDialogLogoutConfirm() {

        confirm = new PopupConfirm(mainActivity, mainActivity.getResources().getString(R.string.des_popup_confirm_logout), new PopupConfirm.OnPopupConfirmListener() {
            @Override
            public void onConfirmOK() {
                Intent intent = new Intent(mainActivity, LoginActivity.class);
                mainActivity.startActivity(intent);
                ((Activity) mainActivity).overridePendingTransition(R.anim.anim_in_back,
                        R.anim.anim_out_back);
                ((Activity) mainActivity).finish();
                confirm.dismiss();
                Utils.saveUser(sharedPref);
            }

            @Override
            public void onConfirmCancel() {
                confirm.dismiss();
            }
        });
        confirm.show();
    }

    private class GeocoderTask extends AsyncTask<String, Void, List<Address>> {

        @Override
        protected List<Address> doInBackground(String... locationName) {
            // Creating an instance of Geocoder class
            Geocoder geocoder = new Geocoder(mainActivity);
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
                if (addresses != null && addresses.size() >= 0) {
                    for (int i = 0; i < addresses.size(); i++) {
                        if (i == 0) {
                            Address address = (Address) addresses.get(i);
//
                            // Creating an instance of GeoPoint, to display in Google Map
                            latLng = new LatLng(address.getLatitude(), address.getLongitude());
                            String addressLine = address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "";
                            addressLine = addressLine + " " + address.getSubAdminArea() + " " + address.getAdminArea() + " " + address.getCountryName();
                            //edit_location.setText(addressLine);
                            markerOptions = new MarkerOptions();
                            markerOptions.position(latLng);
                            markerOptions.title(addressLine);
                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_location));
                            googleMap.addMarker(markerOptions);
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
                        }
                    }
                }

                // Adding Markers on Google Map for each matching address
            }


        }
    }

    private void setMapCurrent() {
        // Do a null check to confirm that we have not already instantiated the map.

        if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                if (count == 0) {
                    if (location != null) {
                        try {
                            Geocoder geo = new Geocoder(mainActivity.getApplicationContext(), Locale.getDefault());
                            List<Address> addresses = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 3);
                            for (int i = 0; i < addresses.size(); i++) {
                                if (i == 0) {
                                    Address address = (Address) addresses.get(i);
//
                                    // Creating an instance of GeoPoint, to display in Google Map
                                    latLng = new LatLng(address.getLatitude(), address.getLongitude());
                                    String addressLine = address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "";
                                    addressLine = addressLine + " " + address.getSubAdminArea() + " " + address.getAdminArea() + " " + address.getCountryName();
                                    editAddress.setText(addressLine);
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
}
