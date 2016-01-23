package com.vn.wassii.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.vn.wassii.model.CommonReponse;
import com.vn.wassii.utils.Constants;
import com.vn.wassii.utils.UrlHelper;
import com.vn.wassii.utils.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rau muong on 05/01/2016.
 */
public class ForgetPassActivity extends AppCompatActivity {
    private TextView txtGetOtp, txtBackLogin;
    private EditText editPhone;
    private ProgressDialog prDialog;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        sharedPref = getSharedPreferences(
                getResources().getString(R.string.login_share_private),
                Context.MODE_PRIVATE);
        txtGetOtp = (TextView) findViewById(R.id.txt_get_otp);
        editPhone = (EditText) findViewById(R.id.edit_phone);
        txtBackLogin = (TextView) findViewById(R.id.txt_back_login);
        initOnClick();
    }

    private void initOnClick() {
        txtGetOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.hasConnection(ForgetPassActivity.this)) {
                    if (editPhone.getText().toString().length() > 0) {
                        doSendVerify(editPhone.getText().toString());
                    } else {
                        Utils.showMessage(ForgetPassActivity.this, ForgetPassActivity.this.getResources().getString(R.string.common_message_fail));
                    }
                } else {
                    Utils.showMessage(ForgetPassActivity.this, ForgetPassActivity.this.getResources().getString(R.string.no_connection_login));
                }
            }
        });
        txtBackLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_in_back, R.anim.anim_out_back);
    }

    private void doSendVerify(String phone) {
        showProgressDialog(true, getString(R.string.contact_please_wait));
        RequestQueue queue = Constants.getQueue(ForgetPassActivity.this);
        Map<String, String> params = new HashMap<String, String>();
        try {
            params.put(Constants.AUTHORIZATION, Constants.AUTHORIZATION_ID);
        } catch (Exception e) {

        }
        String header = "?" + Constants.COMPANYID + "=" + Constants.COMPANY_ID_NUMBER + "&" + Constants.PHONE + "=" +Utils.validateString(phone) ;
        Utils.logE("thaond","header"+header);
        GsonRequest<CommonReponse> postRequest = new GsonRequest<CommonReponse>(
                Request.Method.GET, UrlHelper.RESET_PASSWORD + header, CommonReponse.class, params, null,
                new Response.Listener<CommonReponse>() {
                    public void onResponse(CommonReponse response) {
                        procesSendVerrify(response);
                        showProgressDialog(false, "");
                        // processResponse(response);
                    }
                }, new Response.ErrorListener(

        ) {
            public void onErrorResponse(VolleyError error) {
                showProgressDialog(false, "");
                Utils.showMessage(ForgetPassActivity.this,
                        getResources().getString(R.string.error));

            }
        });
        queue.add(postRequest);
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constants.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
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

    private void procesSendVerrify(CommonReponse reponse) {
        if (reponse.getException() == null) {
            Intent intent = new Intent(ForgetPassActivity.this, ResetPasswordActivity.class);
            intent.putExtra(Constants.PHONE, editPhone.getText().toString());
            startActivity(intent);
            overridePendingTransition(R.anim.anim_in,
                    R.anim.anim_out);
            Utils.showMessage(ForgetPassActivity.this,
                    reponse.getMessage());

        } else {
            Utils.showMessage(ForgetPassActivity.this,
                    reponse.getMessage());

        }

    }
}
