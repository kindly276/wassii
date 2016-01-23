package com.vn.wassii.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.vn.wassii.R;
import com.vn.wassii.activity.LoginActivity;
import com.vn.wassii.dialog.PopupConfirm;
import com.vn.wassii.model.LoginReponse;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by rau muong on 04/01/2016.
 */
public class Utils implements PopupConfirm.OnPopupConfirmListener {
    private static PopupConfirm popup;

    public static boolean hasConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiNetwork = cm
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            return true;
        }

        NetworkInfo mobileNetwork = cm
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected()) {
            return true;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        }

        return false;
    }

    public static void showMessage(Context context, String message) {
        if (message != null && !message.equalsIgnoreCase("")) {
            Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }

    }

    public static void saveUser(SharedPreferences sharedPref) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constants.PHONE, "");
        editor.putString(Constants.LANGUAGEID, "");
        editor.putString(Constants.FULLNAME, "");
        editor.putString(Constants.TOKEN, "");
        editor.putString(Constants.PASSWORD, "");
        editor.commit();

    }

    public static void saveUser(SharedPreferences sharedPref, LoginReponse loginReponse, String password) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constants.PHONE, loginReponse.getPhone());
        editor.putString(Constants.LANGUAGEID, loginReponse.getLanguageId());
        editor.putString(Constants.FULLNAME, loginReponse.getFullName());
        editor.putString(Constants.TOKEN, loginReponse.getToken());
        editor.putString(Constants.PASSWORD, password);
        editor.commit();

    }
    public static void savePass(SharedPreferences sharedPref, String password) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constants.PASSWORD, password);
        editor.commit();

    }
    public static void savePassAndPhone(SharedPreferences sharedPref, String password,String phone) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constants.PASSWORD, password);
        editor.putString(Constants.PHONE, phone);

        editor.commit();

    }


    public static void saveUser(SharedPreferences sharedPref, LoginReponse loginReponse) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(Constants.PHONE, loginReponse.getPhone());
        editor.putString(Constants.LANGUAGEID, loginReponse.getLanguageId());
        editor.putString(Constants.FULLNAME, loginReponse.getFullName());
        editor.putString(Constants.TOKEN, loginReponse.getToken());
        editor.commit();

    }

    public static void saveAddress(SharedPreferences sharedPref, LoginReponse addressesReponse) {
        if (addressesReponse.getAddresses() != null && addressesReponse.getAddresses().size() > 0) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(Constants.ADDRESS, addressesReponse.getAddresses().get(0).getStreet1());
            editor.putString(Constants.FULLNAME, addressesReponse.getFullName());
            editor.commit();
        } else {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(Constants.ADDRESS, "");
            editor.putString(Constants.FULLNAME, addressesReponse.getFullName());
            editor.commit();
        }

    }

    public static void saveLanguage(SharedPreferences sharedPref, String langueage) {
        if (langueage != null && langueage.length() > 0) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(Constants.LANGUAGEID, langueage);
            editor.commit();
        }

    }


    public static final String validateString(final String name) {
        if (name != null) {
            String nameReturn = "";
            try {
                nameReturn = URLEncoder.encode(name, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            nameReturn = nameReturn.replaceAll(" ", "%20").replaceAll("/", "%2F");
            return nameReturn;
        } else {
            return "";
        }

    }

    public static void logE(String tag, String contentLogE) {
    }

    public static void showDialogTimeOut(final Activity activity) {
        popup = new PopupConfirm(activity, activity.getResources().getString(R.string.un_authenticated), new PopupConfirm.OnPopupConfirmListener() {
            @Override
            public void onConfirmOK() {
                Intent intent = new Intent(activity, LoginActivity.class);
                activity.startActivity(intent);
                ((Activity) activity).overridePendingTransition(R.anim.anim_in_back,
                        R.anim.anim_out_back);
                popup.dismiss();
            }

            @Override
            public void onConfirmCancel() {
                popup.dismiss();
            }
        });
        popup.show();
    }

    @Override
    public void onConfirmOK() {

    }

    @Override
    public void onConfirmCancel() {

    }
    public static void loadImage(final Context activity, final ImageView image) {
        Picasso.with(activity).load(R.mipmap.ic_laucher).into(image);
    }
    public static void loadImage(final Context activity, final ImageView image, final String url) {
        Picasso.with(activity)
                .load(url)
                .networkPolicy(NetworkPolicy.OFFLINE).fit()
                .into(image, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        //Try again online if cache failed
                        Picasso.with(activity)
                                .load(url).error(activity.getResources().getDrawable(R.mipmap.ic_laucher)).fit()
                                .into(image, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                    }

                                    @Override
                                    public void onError() {
                                    }
                                });
                    }
                });
    }


    public static JSONObject getLocationInfo(double lat, double lng) {

        HttpGet httpGet = new HttpGet("http://maps.google.com/maps/api/geocode/json?latlng=" + lat + "," + lng + "&sensor=true");
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static JSONObject getLocationInfo(String address) {
        StringBuilder stringBuilder = new StringBuilder();
        try {

            address = address.replaceAll(" ", "%20");

            HttpPost httppost = new HttpPost("http://maps.google.com/maps/api/geocode/json?address=" + address + "&sensor=false");
            HttpClient client = new DefaultHttpClient();
            HttpResponse response;
            stringBuilder = new StringBuilder();


            response = client.execute(httppost);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jsonObject;
    }

    public static List<Address> getAddrByWeb(JSONObject jsonObject) {
        List<Address> res = new ArrayList<Address>();
        try {
            JSONArray array = (JSONArray) jsonObject.get("results");
            for (int i = 0; i < array.length(); i++) {
                Double lon = new Double(0);
                Double lat = new Double(0);
                String name = "";
                try {
                    lon = array.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lng");

                    lat = array.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                    name = array.getJSONObject(i).getString("formatted_address");
                    Address addr = new Address(Locale.getDefault());
                    addr.setLatitude(lat);
                    addr.setLongitude(lon);
                    addr.setAddressLine(0, name != null ? name : "");
                    res.add(addr);
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        } catch (JSONException e) {
            e.printStackTrace();

        }

        return res;
    }
}
