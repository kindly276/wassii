package com.vn.wassii.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.vn.wassii.model.CommonReponse;
import com.vn.wassii.model.LoginReponse;
import com.vn.wassii.utils.Constants;
import com.vn.wassii.utils.UrlHelper;
import com.vn.wassii.utils.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by thaond on 1/7/2016.
 */
public class ResetPasswordActivity extends AppCompatActivity {
    private TextView txtGetOtp, txtDone;
    private EditText editOtp, editPassWord, editRePass;
    private ProgressDialog prDialog;
    private SharedPreferences sharedPref;
    private String phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);
        phone = getIntent().getStringExtra(Constants.PHONE);
        sharedPref = getSharedPreferences(
                getResources().getString(R.string.login_share_private),
                Context.MODE_PRIVATE);
        txtGetOtp = (TextView) findViewById(R.id.txt_get_otp);
        editOtp = (EditText) findViewById(R.id.edit_otp);
        editPassWord = (EditText) findViewById(R.id.edit_password_new);
        editRePass = (EditText) findViewById(R.id.edit_re_password);
        txtDone = (TextView) findViewById(R.id.txt_done);
        initOnClick();
    }

    private void initOnClick() {
        txtGetOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.hasConnection(ResetPasswordActivity.this)) {
                    doSendVerify(phone);
                } else {
                    Utils.showMessage(ResetPasswordActivity.this, ResetPasswordActivity.this.getResources().getString(R.string.no_connection_login));
                }
            }
        });
        txtDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.hasConnection(ResetPasswordActivity.this)) {
                    validate(editOtp.getText().toString(), editPassWord.getText().toString(), editRePass.getText().toString());
                } else {
                    Utils.showMessage(ResetPasswordActivity.this, ResetPasswordActivity.this.getResources().getString(R.string.no_connection_login));
                }
            }
        });
    }

    private void validate(String otp, String newPass, String rePass) {
        if (otp.length() > 0 && newPass.length() > 0 && rePass.length() > 0) {
            if (newPass.endsWith(rePass)) {
                doVerifyResetPass(otp, newPass);
            } else {
                Utils.showMessage(ResetPasswordActivity.this, ResetPasswordActivity.this.getResources().getString(R.string.pass_coincides));

            }

        } else {
            Utils.showMessage(ResetPasswordActivity.this, ResetPasswordActivity.this.getResources().getString(R.string.common_message_fail));

        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_in_back, R.anim.anim_out_back);
    }


    private void doVerifyResetPass(String otp, final String newPass) {
        showProgressDialog(true, getString(R.string.contact_please_wait));
        RequestQueue queue = Constants.getQueue(ResetPasswordActivity.this);
        Map<String, String> params = new HashMap<String, String>();
        try {
            params.put(Constants.AUTHORIZATION, Constants.AUTHORIZATION_ID);
        } catch (Exception e) {

        }
        String header = "?" + Constants.COMPANYID + "=" + Constants.COMPANY_ID_NUMBER + "&" + Constants.PHONE + "=" + Utils.validateString(phone) + "&" + Constants.CODE + "=" + Utils.validateString(otp) + "&" + Constants.NEWPASS + "=" + Utils.validateString(newPass);
        Log.e("thaond", "header" + header);
        GsonRequest<LoginReponse> postRequest = new GsonRequest<LoginReponse>(
                Request.Method.GET, UrlHelper.VERIFY_RESET_PASSWORD + header, LoginReponse.class, params, null,
                new Response.Listener<LoginReponse>() {
                    public void onResponse(LoginReponse response) {
                        processAfterVerifyResetPass(response,newPass);
                        showProgressDialog(false, "");
                        // processResponse(response);
                    }
                }, new Response.ErrorListener(

        ) {
            public void onErrorResponse(VolleyError error) {
                showProgressDialog(false, "");
                Utils.showMessage(ResetPasswordActivity.this,
                        getResources().getString(R.string.error));

            }
        });
        queue.add(postRequest);
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constants.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void processAfterVerifyResetPass(LoginReponse response,String paass) {
        if (response.getException() == null) {
            try {
                Utils.savePassAndPhone(sharedPref, paass,phone);
                Intent intent = new Intent(ResetPasswordActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_in,
                        R.anim.anim_out);
                Utils.showMessage(ResetPasswordActivity.this,
                        response.getMessage());

            } catch (Exception e) {
                Utils.showMessage(ResetPasswordActivity.this,
                        getResources().getString(R.string.error));
            }
        } else {
            Utils.showMessage(ResetPasswordActivity.this,
                    response.getMessage());
        }
    }

    private void doSendVerify(String phone) {
        showProgressDialog(true, getString(R.string.contact_please_wait));
        RequestQueue queue = Constants.getQueue(ResetPasswordActivity.this);
        Map<String, String> params = new HashMap<String, String>();
        try {
            params.put(Constants.AUTHORIZATION, Constants.AUTHORIZATION_ID);
        } catch (Exception e) {

        }
        String header = "?" + Constants.COMPANYID + "=" + Constants.COMPANY_ID_NUMBER + "&" + Constants.PHONE + "=" + Utils.validateString(phone);
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
                Utils.showMessage(ResetPasswordActivity.this,
                        getResources().getString(R.string.error));

            }
        });
        queue.add(postRequest);
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
            Utils.showMessage(ResetPasswordActivity.this,
                    reponse.getMessage());

        } else {
            Utils.showMessage(ResetPasswordActivity.this,
                    reponse.getMessage());

        }

    }
}
