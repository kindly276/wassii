package com.vn.wassii.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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
 * Created by rau muong on 01/01/2016.
 */
public class DoneRegisterActivity extends AppCompatActivity {
    private TextView txtDoneRegister, txtReSendOtp, txtBack;
    private EditText editCode;
    private ProgressDialog prDialog;
    private SharedPreferences sharedPref;
    private String passsword;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done_register);
        passsword = getIntent().getStringExtra(Constants.PASSWORD);
        type = getIntent().getIntExtra(Constants.TYPE, 0);
        sharedPref = getSharedPreferences(
                getResources().getString(R.string.login_share_private),
                Context.MODE_PRIVATE);
        txtDoneRegister = (TextView) findViewById(R.id.txt_done_register);
        txtReSendOtp = (TextView) findViewById(R.id.txt_re_send_otp);
        txtBack = (TextView) findViewById(R.id.txt_back_register);
        txtReSendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doSendVerify(sharedPref.getString(Constants.TOKEN, ""));
            }
        });
        String languageName = sharedPref.getString(Constants.LANGUAGEID, "");
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
        editCode = (EditText) findViewById(R.id.edit_code);


        txtDoneRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editCode.getText().toString().length() > 0) {
                    doVerify(editCode.getText().toString(), sharedPref.getString(Constants.TOKEN, ""));
                } else {
                    Utils.showMessage(DoneRegisterActivity.this, getString(R.string.st_please_input_otp));
                }
            }
        });
        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if (type == 1) {
            doSendVerify(sharedPref.getString(Constants.TOKEN, ""));
            txtBack.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_in_back, R.anim.anim_out_back);
    }

    private void doSendVerify(String token) {
        showProgressDialog(true, getString(R.string.contact_please_wait));
        RequestQueue queue = Constants.getQueue(DoneRegisterActivity.this);
        Map<String, String> params = new HashMap<String, String>();
        try {
            params.put(Constants.AUTHORIZATION, token);
        } catch (Exception e) {

        }
        GsonRequest<LoginReponse> postRequest = new GsonRequest<LoginReponse>(
                Request.Method.GET, UrlHelper.SEND_VERIFY_CODE, LoginReponse.class, params, null,
                new Response.Listener<LoginReponse>() {
                    public void onResponse(LoginReponse response) {
                        procesSendVerrify(response);
                        showProgressDialog(false, "");
                        // processResponse(response);
                    }
                }, new Response.ErrorListener(

        ) {
            public void onErrorResponse(VolleyError error) {
                showProgressDialog(false, "");
                Utils.showMessage(DoneRegisterActivity.this,
                        getResources().getString(R.string.error));

            }
        });
        queue.add(postRequest);
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constants.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void procesSendVerrify(LoginReponse reponse) {

        Utils.showMessage(DoneRegisterActivity.this,
                reponse.getMessage());

    }

    private void doVerify(String otp, String token) {
        showProgressDialog(true, getString(R.string.contact_please_wait));
        RequestQueue queue = Constants.getQueue(DoneRegisterActivity.this);
        Map<String, String> params = new HashMap<String, String>();
        try {
            params.put(Constants.AUTHORIZATION, token);
        } catch (Exception e) {

        }
        String headers = "?" + Constants.CODE + "=" + Utils.validateString(otp);
        Log.e("thaond", "headers" + headers);
        GsonRequest<LoginReponse> postRequest = new GsonRequest<LoginReponse>(
                Request.Method.GET, UrlHelper.VERIFY_ACCOUNT + headers, LoginReponse.class, params, null,
                new Response.Listener<LoginReponse>() {
                    public void onResponse(LoginReponse response) {
                        procesAfterRegister(response);
                        showProgressDialog(false, "");
                        // processResponse(response);
                    }
                }, new Response.ErrorListener(

        ) {
            public void onErrorResponse(VolleyError error) {
                showProgressDialog(false, "");
                Utils.showMessage(DoneRegisterActivity.this,
                        getResources().getString(R.string.error));

            }
        });
        queue.add(postRequest);
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constants.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void procesAfterRegister(LoginReponse response) {
        Log.e("thaond", "response" + response.getException());
        if (response.getException() == null) {
            try {
                Intent intent = new Intent(DoneRegisterActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                showProgressDialog(false, "");
                finish();
                overridePendingTransition(R.anim.anim_in,
                        R.anim.anim_out);
                Utils.showMessage(DoneRegisterActivity.this,
                        response.getMessage());

            } catch (Exception e) {
                Utils.showMessage(DoneRegisterActivity.this,
                        getResources().getString(R.string.error));
            }
        } else {
            Utils.showMessage(DoneRegisterActivity.this,
                    response.getMessage());
        }
    }

    private void showProgressDialog(boolean show, String message) {
        if (show) {
            if (prDialog == null) {
                prDialog = new ProgressDialog(this);
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
}