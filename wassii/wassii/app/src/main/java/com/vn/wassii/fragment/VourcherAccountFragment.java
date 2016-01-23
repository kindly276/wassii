package com.vn.wassii.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.thao.mylibrary.GsonRequest;
import com.vn.wassii.R;
import com.vn.wassii.activity.LoginActivity;
import com.vn.wassii.activity.MainActivity;
import com.vn.wassii.model.InfoAccount;
import com.vn.wassii.utils.Constants;
import com.vn.wassii.utils.UrlHelper;
import com.vn.wassii.utils.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by thaond on 09/01/2016.
 */
public class VourcherAccountFragment extends Fragment {
    private View mView;
    private MainActivity mainActivity;
    private SharedPreferences sharedPref;
    private String monney;
    private boolean isNetworkError, isRefresh, isNoConnection, isNoData, isUnAuthenticated;
    private ScrollView layoutContent;
    private TextView txtError,txtCurrentAccountMoney;
    private ProgressBar pgLoading;
    public static final VourcherAccountFragment newInstance() {

        VourcherAccountFragment myFragment = new VourcherAccountFragment();
        return myFragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(mView==null){
            mainActivity = (MainActivity) getActivity();
            sharedPref = mainActivity.getSharedPreferences(
                    getResources().getString(R.string.login_share_private),
                    Context.MODE_PRIVATE);
            mView = inflater.inflate(R.layout.fragment_vourcher_account, container, false);
            txtError=(TextView)mView.findViewById(R.id.txtError);
            mainActivity.setToolbar((Toolbar)mView.findViewById(R.id.toolbar));
            txtCurrentAccountMoney=(TextView)mView.findViewById(R.id.txt_current_account_money);
            layoutContent=(ScrollView)mView.findViewById(R.id.layout_content);
            pgLoading=(ProgressBar)mView.findViewById(R.id.pgLoading);
            initOnClick();
            showDoGetData();
        }

        return mView;
    }

    private void initOnClick() {
        txtError.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isUnAuthenticated) {
                    Intent intent = new Intent(mainActivity, LoginActivity.class);
                    ((Activity) mainActivity).startActivity(intent);
                    ((Activity) mainActivity).overridePendingTransition(R.anim.slide_up, R.anim.scale_down);
                    ((Activity) mainActivity).finish();
                } else {
                    if (!isNoData) {
                        isNetworkError = false;
                        isNoConnection = false;
                        txtError.setVisibility(View.GONE);
                        pgLoading.setVisibility(View.VISIBLE);
                        showDoGetData();
                    }
                }
            }
        });
    }

    private void showDoGetData() {
        if (Utils.hasConnection(mainActivity)) {
            doGetVourcherAccount();
        } else {
            isNetworkError = true;
            isNoConnection = true;
            displayDataFirst();
        }
    }

    private void doGetVourcherAccount() {
        final RequestQueue queue = Constants.getQueue(mainActivity);
        Map<String, String> params = new HashMap<String, String>();
        try {
            params.put(Constants.AUTHORIZATION, sharedPref.getString(Constants.TOKEN, ""));
        } catch (Exception e) {

        }
        GsonRequest<InfoAccount> postRequest = new GsonRequest<InfoAccount>(
                Request.Method.GET, UrlHelper.GET_ACCOUNT_INFO, InfoAccount.class, params, null,
                new Response.Listener<InfoAccount>() {
                    public void onResponse(InfoAccount response) {
                        processResponse(response);
                    }
                }, new Response.ErrorListener(

        ) {
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null && networkResponse.statusCode == 401) {
                    isUnAuthenticated=true;
                    // HTTP Status Code: 401 Unauthorized
                }

                isNetworkError = true;
                // isNoConnection = true;
                displayDataFirst();

            }
        });
        queue.add(postRequest);
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constants.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void displayDataFirst() {
        processDisplayDataFirst();
    }

    private void processResponse(InfoAccount reponse) {
        try {
            if (reponse.getException() == null) {
                if (reponse.getAccountBalance() != null) {
                    monney = reponse.getAccountBalance();
                    isNetworkError = false;
                } else {
                    isNoData = true;
                    isNetworkError = true;
                }

            } else {
                isNetworkError = true;
            }
        } catch (Exception e) {
            isNetworkError = true;
        } finally {
            displayDataFirst();
        }

    }
    private void processDisplayDataFirst() {

        try {
            if (isNetworkError) {
                if (isNoConnection) {
                    txtError.setText(getResources().getString(R.string.no_connection));
                } else if (isNoData) {
                    txtError.setText(getResources().getString(R.string.no_data));
                } else if (isUnAuthenticated) {
                    txtError.setText(getResources().getString(R.string.un_authenticated));
                } else {
                    txtError.setText(getResources().getString(R.string.error_retry));
                }
                txtError.setVisibility(View.VISIBLE);
                pgLoading.setVisibility(View.GONE);
                layoutContent.setVisibility(View.GONE);

            } else {
                    txtCurrentAccountMoney.setText(monney+" "+mainActivity.getResources().getString(R.string.st_money));
                    txtError.setVisibility(View.GONE);
                    pgLoading.setVisibility(View.GONE);
                    layoutContent.setVisibility(View.VISIBLE);
            }
        } catch (Exception ex) {
        }
    }

}
