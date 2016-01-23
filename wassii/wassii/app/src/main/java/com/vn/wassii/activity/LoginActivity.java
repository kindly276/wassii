package com.vn.wassii.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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
public class LoginActivity extends AppCompatActivity {
    TextView txtSignUp, txtLogin, txtForgetPass;
    private SharedPreferences sharedPref;
    private EditText editUsername, editPassword;
    private ProgressDialog prDialog;
    //    private ImageView imageEngland,imageViet;
    private int type;
    private static final int TYPE_VIET = 0;
    private static final int TYPE_ENG = 1;
    private String languageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        txtSignUp = (TextView) findViewById(R.id.txtSignUp);
        txtLogin = (TextView) findViewById(R.id.txt_login);
        txtForgetPass = (TextView) findViewById(R.id.txt_forget_password);
        editUsername = (EditText) findViewById(R.id.edit_username);
        editPassword = (EditText) findViewById(R.id.edit_password);
        sharedPref = getSharedPreferences(
                getResources().getString(R.string.login_share_private),
                Context.MODE_PRIVATE);
        languageName = sharedPref.getString(Constants.LANGUAGEID, "");
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
        initOnClick();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.anim_in_back, R.anim.anim_out_back);
    }

    private void initOnClick() {
        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(LoginActivity.this, RegisterAccountActivity.class));
                overridePendingTransition(R.anim.anim_in,
                        R.anim.anim_out);
            }
        });
        txtForgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ForgetPassActivity.class));
                overridePendingTransition(R.anim.anim_in,
                        R.anim.anim_out);

            }
        });

        editPassword
                .setOnEditorActionListener(new EditText.OnEditorActionListener() {

                    @Override
                    public boolean onEditorAction(TextView v, int actionId,
                                                  KeyEvent event) {
                        // TODO Auto-generated method stub
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            // do here your stuff f
                            validateLogin(editUsername.getText().toString(),
                                    editPassword.getText().toString());
                            return true;
                        }
                        return false;
                    }
                });
        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //doLogin("01649566858", "123456");
                validateLogin(editUsername.getText().toString(), editPassword.getText().toString());
            }
        });
//        imageEngland.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(type==TYPE_ENG){
//                    imageEngland.setBackgroundResource(R.mipmap.ic_england);
//                }
//
//
//            }
//        });
//        imageViet.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                imageEngland.setBackgroundResource(R.mipmap.ic_england);
//                imageViet.setBackgroundResource(R.mipmap.ic_vietnam_press);
//            }
//        });
    }

    private void validateLogin(String phoneNumber, String password) {
        if (Utils.hasConnection(LoginActivity.this)) {
            if (phoneNumber != null && phoneNumber.trim().length() > 0
                    && password != null && password.length() > 0) {
                doLogin(phoneNumber, password);
            } else {
                Utils.showMessage(LoginActivity.this,
                        getString(R.string.login_message_fail));
            }
        } else {
            Utils.showMessage(LoginActivity.this, getString(R.string.no_connection_login));
        }
    }

    private void doLogin(String phoneNumber, String password) {
        Log.e("thaond", "Thu xem" + Utils.validateString(password));
        showProgressDialog(true, getString(R.string.contact_please_wait));
        RequestQueue queue = Constants.getQueue(LoginActivity.this);
        Map<String, String> params = new HashMap<String, String>();
        try {
            params.put(Constants.AUTHORIZATION, Constants.AUTHORIZATION_ID);
        } catch (Exception e) {

        }
        String headers = "?" + Constants.PHONE + "=" + Utils.validateString(phoneNumber) + "&" + Constants.PASSWORD + "=" + Utils.validateString(password) + "&" + Constants.COMPANYID + "=" + Constants.COMPANY_ID_NUMBER;
        GsonRequest<LoginReponse> postRequest = new GsonRequest<LoginReponse>(
                Request.Method.GET, UrlHelper.Login + headers, LoginReponse.class, params, null,
                new Response.Listener<LoginReponse>() {
                    public void onResponse(LoginReponse response) {
                        showProgressDialog(false, "");
                        procesAfterLogin(response);

                        // processResponse(response);
                    }
                }, new Response.ErrorListener(

        ) {
            public void onErrorResponse(VolleyError error) {
                showProgressDialog(false, "");
                Utils.showMessage(LoginActivity.this,
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

                if (response.isVerified() == true) {

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    showProgressDialog(false, "");
                    finish();
                    Utils.saveAddress(sharedPref, response);

                } else {
                    Intent intent = new Intent(LoginActivity.this, DoneRegisterActivity.class);
                    intent.putExtra(Constants.PASSWORD, editPassword.getText().toString());
                    intent.putExtra(Constants.TYPE, 1);
                    startActivity(intent);
                }
                Utils.saveUser(sharedPref, response, editPassword.getText().toString());
                String languageName=response.getLanguageId();
                if (languageName != null && !languageName.equals("")) {
                    if (languageName.equals(Constants.JA_LANGUAGE)) {
                        languageName = "ja";
                    } else if (languageName.equals(Constants.KO_LANGUAGE)) {
                        languageName = "ko";

                    } else if (languageName.equals(Constants.EN_LANGUAGE)) {
                        languageName = "en";
                    } else if (languageName.equals(Constants.VN_LANGUAGE)) {
                        languageName = "vn";
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
                Utils.showMessage(LoginActivity.this,
                        response.getMessage());

            } catch (Exception e) {
                Utils.showMessage(LoginActivity.this,
                        getResources().getString(R.string.error));
            }
        } else {
            Utils.showMessage(LoginActivity.this,
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
