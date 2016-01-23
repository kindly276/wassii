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
import com.vn.wassii.model.LoginReponse;
import com.vn.wassii.utils.Constants;
import com.vn.wassii.utils.UrlHelper;
import com.vn.wassii.utils.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rau muong on 06/01/2016.
 */
public class ChangePasswordActivity extends AppCompatActivity {
    private TextView txtDone;
    private EditText editRePassword, editPassword, editNewPassword;
    private ProgressDialog prDialog;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        sharedPref = getSharedPreferences(
                getResources().getString(R.string.login_share_private),
                Context.MODE_PRIVATE);
        txtDone = (TextView) findViewById(R.id.txt_done);
        editRePassword = (EditText) findViewById(R.id.edit_re_password);
        editPassword = (EditText) findViewById(R.id.edit_passs);
        editNewPassword = (EditText) findViewById(R.id.edit_password_new);
        initOnClick();
    }

    private void initOnClick() {
        txtDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateChange(editPassword.getText().toString(), editNewPassword.getText().toString(), editRePassword.getText().toString());

            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_in_back, R.anim.anim_out_back);
    }


    private void validateChange(String password, String newPassword, String rePassword) {
        if (Utils.hasConnection(ChangePasswordActivity.this)) {
            if (password.trim().length() > 0
                    && rePassword.length() > 0 && newPassword.length() > 0) {
                if (newPassword.equals(rePassword)) {
                    doChange(password, rePassword);
                } else {
                    Utils.showMessage(ChangePasswordActivity.this, getString(R.string.pass_coincides));
                }


            } else {
                Utils.showMessage(ChangePasswordActivity.this,
                        getString(R.string.common_message_fail));
            }
        } else {
            Utils.showMessage(ChangePasswordActivity.this, getString(R.string.no_connection_login));
        }
    }

    private void doChange(String password, String newPassword) {
        showProgressDialog(true, getString(R.string.contact_please_wait));
        RequestQueue queue = Constants.getQueue(ChangePasswordActivity.this);
        Map<String, String> params = new HashMap<String, String>();
        try {
            params.put(Constants.AUTHORIZATION, sharedPref.getString(Constants.TOKEN, ""));
        } catch (Exception e) {

        }
        String headers = "?" + "oldPass" + "=" + Utils.validateString(password) + "&" + "newPass" + "=" + Utils.validateString(newPassword);
        Log.e("thaond", "headers" + headers);
        GsonRequest<LoginReponse> postRequest = new GsonRequest<LoginReponse>(
                Request.Method.GET, UrlHelper.CHANGE_PASSWORD + headers, LoginReponse.class, params, null,
                new Response.Listener<LoginReponse>() {
                    public void onResponse(LoginReponse response) {
                        procesAfterChange(response);
                        showProgressDialog(false, "");
                        // processResponse(response);
                    }
                }, new Response.ErrorListener(

        ) {
            public void onErrorResponse(VolleyError error) {
                showProgressDialog(false, "");
                Utils.showMessage(ChangePasswordActivity.this,
                        getResources().getString(R.string.error));

            }
        });
        queue.add(postRequest);
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constants.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void procesAfterChange(LoginReponse response) {
        Log.e("thaond", "response" + response.getException());
        if (response.getException() == null) {

            try {
                showProgressDialog(false, "");
                Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                //Utils.saveAddress(sharedPref, response);
                Utils.savePass(sharedPref, editNewPassword.getText().toString());
                finish();
                overridePendingTransition(R.anim.anim_in,
                        R.anim.anim_out);

            } catch (Exception e) {
                Utils.showMessage(ChangePasswordActivity.this,
                        getResources().getString(R.string.error));
            }
        } else {
            Utils.showMessage(ChangePasswordActivity.this,
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
