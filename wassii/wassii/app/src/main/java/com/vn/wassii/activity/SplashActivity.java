package com.vn.wassii.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.thao.mylibrary.GsonRequest;
import com.vn.wassii.R;
import com.vn.wassii.model.LoginReponse;
import com.vn.wassii.utils.Constants;
import com.vn.wassii.utils.UrlHelper;
import com.vn.wassii.utils.Utils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by thaond on 31/12/2015.
 */
public class SplashActivity extends AppCompatActivity {
    private SharedPreferences sharedPref;
    private String phone, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sharedPref = getSharedPreferences(
                getResources().getString(R.string.login_share_private),
                Context.MODE_PRIVATE);
        phone = sharedPref.getString(Constants.PHONE, "");
        password = sharedPref.getString(Constants.PASSWORD, "");
        Log.e("thaond","phone"+phone);
        Log.e("thaond","password"+password);
        doLogin(phone, password);
    }

    private void doLogin(String phoneNumber, String password) {
        Log.e("thaond", "Thu xem" + Utils.validateString(password));
        RequestQueue queue = Constants.getQueue(SplashActivity.this);
        Map<String, String> params = new HashMap<String, String>();
        try {
            params.put(Constants.AUTHORIZATION, Constants.AUTHORIZATION_ID);
        } catch (Exception e) {

        }
        String headers = "?" + Constants.PHONE + "=" + Utils.validateString(phoneNumber) + "&" + Constants.PASSWORD + "=" + Utils.validateString(password) + "&" + Constants.COMPANYID + "=" + Constants.COMPANY_ID_NUMBER;
        Log.e("thaond", "Thu xem" + headers);
        GsonRequest<LoginReponse> postRequest = new GsonRequest<LoginReponse>(
                Request.Method.GET, UrlHelper.Login + headers, LoginReponse.class, params, null,
                new Response.Listener<LoginReponse>() {
                    public void onResponse(LoginReponse response) {
                        procesAfterLogin(response);

                        // processResponse(response);
                    }
                }, new Response.ErrorListener(

        ) {
            public void onErrorResponse(VolleyError error) {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_in,
                        R.anim.anim_out);
                finish();

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

                if (response.isVerified() == true) {

                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    Utils.saveAddress(sharedPref, response);

                } else {
                    Intent intent = new Intent(SplashActivity.this, DoneRegisterActivity.class);
                    intent.putExtra(Constants.TYPE, 1);
                    startActivity(intent);
                }
                Utils.saveUser(sharedPref, response, password);
                String languageName=response.getLanguageId();
                if (languageName != null && !languageName.equals("")) {
                    if (languageName.equals(Constants.JA_LANGUAGE)) {
                        languageName = "ja";
                    } else if (languageName.equals(Constants.KO_LANGUAGE)) {
                        languageName = "ko";

                    } else if (languageName.equals(Constants.EN_LANGUAGE)) {
                        languageName = "en";
                    } else if (languageName.equals(Constants.VN_LANGUAGE)) {
                        languageName = "vi";
                    }
                    Locale locale = new Locale(languageName);
                    Locale.setDefault(locale);
                    Configuration config = new Configuration();
                    config.locale = locale;
                    getBaseContext().getResources().updateConfiguration(config,
                            getBaseContext().getResources().getDisplayMetrics());
                }
                overridePendingTransition(R.anim.anim_in,
                        R.anim.anim_out);
                finish();

            } catch (Exception e) {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_in,
                        R.anim.anim_out);
                finish();
            }
        } else {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_in,
                    R.anim.anim_out);
            finish();
        }
    }
}
