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
import java.util.Locale;
import java.util.Map;

/**
 * Created by rau muong on 31/12/2015.
 */
public class RegisterAccountActivity extends AppCompatActivity {
    private TextView txtGetOtp;
    private EditText editName, editPhoneNumber, editRePassword, editPassword;
    private ProgressDialog prDialog;
    private SharedPreferences sharedPref;
    private String language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_account);
        sharedPref = getSharedPreferences(
                getResources().getString(R.string.login_share_private),
                Context.MODE_PRIVATE);
        txtGetOtp = (TextView) findViewById(R.id.txt_get_otp);
        editName = (EditText) findViewById(R.id.edit_name);
        editPhoneNumber = (EditText) findViewById(R.id.edit_phone_number);
        editRePassword = (EditText) findViewById(R.id.edit_re_password);
        editPassword = (EditText) findViewById(R.id.edit_password);
        language = Locale.getDefault().getLanguage();
        if (language.equals("ja")) {
            language = Constants.JA_LANGUAGE;
        } else if (language.equals("ko")) {
            language = Constants.KO_LANGUAGE;

        } else if (language.equals("en")) {
            language = Constants.EN_LANGUAGE;
        } else if (language.equals("vi")) {
            language = Constants.VN_LANGUAGE;
        } else {
            language = Constants.EN_LANGUAGE;
        }
        initOnClick();
    }

    private void initOnClick() {
        txtGetOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateRegister(editName.getText().toString(), editRePassword.getText().toString(), editPhoneNumber.getText().toString(), editPassword.getText().toString());

            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_in_back, R.anim.anim_out_back);
    }

    private void validateRegister(String name, String rePassword, String phoneNumber, String password) {
        if (Utils.hasConnection(RegisterAccountActivity.this)) {
            if (phoneNumber.trim().length() > 0
                    && password.length() > 0 && name.length() > 0 && password.length() > 0) {
                if (password.equals(rePassword)) {
                    doRegister(name, phoneNumber, password);
                } else {
                    Utils.showMessage(RegisterAccountActivity.this, getString(R.string.pass_coincides));
                }


            } else {
                Utils.showMessage(RegisterAccountActivity.this,
                        getString(R.string.register_message_fail));
            }
        } else {
            Utils.showMessage(RegisterAccountActivity.this, getString(R.string.no_connection_login));
        }
    }

    private void doRegister(String fullName, String phone, String password) {
        showProgressDialog(true, getString(R.string.contact_please_wait));
        RequestQueue queue = Constants.getQueue(RegisterAccountActivity.this);
        Map<String, String> params = new HashMap<String, String>();
        try {
            params.put(Constants.AUTHORIZATION, Constants.AUTHORIZATION_ID);
        } catch (Exception e) {

        }
        String headers = "?" + Constants.FULLNAME + "=" + Utils.validateString(fullName) + "&" + Constants.PHONE + "=" + Utils.validateString(phone) + "&" + Constants.PASSWORD + "=" + Utils.validateString(password) + "&" + Constants.COMPANYID + "=" + Constants.COMPANY_ID_NUMBER + "&" + Constants.LANGUAGEID + "=" + Utils.validateString(language);
        Log.e("thaond", "headers" + headers);
        GsonRequest<LoginReponse> postRequest = new GsonRequest<LoginReponse>(
                Request.Method.GET, UrlHelper.Sigup + headers, LoginReponse.class, params, null,
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
                Utils.showMessage(RegisterAccountActivity.this,
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
                Intent intent = new Intent(RegisterAccountActivity.this, DoneRegisterActivity.class);
                intent.putExtra(Constants.PHONE, editPassword.getText().toString());
                startActivity(intent);
                overridePendingTransition(R.anim.anim_in,
                        R.anim.anim_out);
                Utils.showMessage(RegisterAccountActivity.this,
                        response.getMessage());
                Utils.saveAddress(sharedPref, response);
                Utils.saveUser(sharedPref, response, editPassword.getText().toString());
            } catch (Exception e) {
                Utils.showMessage(RegisterAccountActivity.this,
                        getResources().getString(R.string.error));
            }
        } else {
            Utils.showMessage(RegisterAccountActivity.this,
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
