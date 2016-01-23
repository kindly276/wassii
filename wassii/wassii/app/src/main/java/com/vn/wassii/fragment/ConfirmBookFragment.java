package com.vn.wassii.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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
import com.vn.wassii.model.CodeVourcher;
import com.vn.wassii.model.CommonReponse;
import com.vn.wassii.model.InfoAccount;
import com.vn.wassii.utils.Constants;
import com.vn.wassii.utils.UrlHelper;
import com.vn.wassii.utils.Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by thaond on 03/01/2016.
 */
public class ConfirmBookFragment extends Fragment {
    private View mView;
    private MainActivity mainActivity;
    private Toolbar toolbar;
    private SharedPreferences sharedPref;
    private String monney;
    private boolean isNetworkError, isRefresh, isNoConnection, isNoData, isUnAuthenticated;
    private ScrollView layoutContent;
    private TextView txtError, txtCurrentAccountMoney, txtOk, txtPromotionCodeMoney,txtPromotionCode;
    private ProgressBar pgLoading;
    private EditText editVoucher;
    private TextView txtDone;
    private ProgressDialog prDialog;
    private String billingStreet, billingDate, billingHour, shippingDate, shippingHour, schedule, shippingStreet;
    private String couponCode;

    public static final ConfirmBookFragment newInstance(String billingStreet, String billingDate, String billingHour, String shippingDate, String shippingHour, String schedule, String shippingStreet) {
        ConfirmBookFragment myFragment = new ConfirmBookFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BILLINGSTREET, billingStreet);
        bundle.putString(Constants.BILLINGDATE, billingDate);
        bundle.putString(Constants.BILLINGHOUR, billingHour);
        bundle.putString(Constants.SHIPPINGDATE, shippingDate);
        bundle.putString(Constants.SHIPPINGHOUR, shippingHour);
        bundle.putString(Constants.SCHEDULE, schedule);
        bundle.putString(Constants.SHIPPINGSTREET, shippingStreet);
        myFragment.setArguments(bundle);
        return myFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(mView==null){
            mView = inflater.inflate(R.layout.fragment_order_confirm, container, false);
            mainActivity = (MainActivity) getActivity();
            sharedPref = mainActivity.getSharedPreferences(
                    getResources().getString(R.string.login_share_private),
                    Context.MODE_PRIVATE);
            toolbar = (Toolbar) mView.findViewById(R.id.toolbar);
            mainActivity.setToolbar(toolbar);
            txtError = (TextView) mView.findViewById(R.id.txtError);
            txtOk = (TextView) mView.findViewById(R.id.txt_ok);
            mainActivity.setToolbar((Toolbar) mView.findViewById(R.id.toolbar));
            txtCurrentAccountMoney = (TextView) mView.findViewById(R.id.txt_promotion_money);
            txtPromotionCodeMoney = (TextView) mView.findViewById(R.id.txt_promotion_code_money);
            txtPromotionCode = (TextView) mView.findViewById(R.id.txt_promotion_code);
            txtPromotionCodeMoney.setVisibility(View.GONE);
            txtPromotionCode.setVisibility(View.GONE);
            txtDone = (TextView) mView.findViewById(R.id.txt_done_register);
            layoutContent = (ScrollView) mView.findViewById(R.id.layout_content);
            editVoucher = (EditText) mView.findViewById(R.id.edit_vourcher);
            pgLoading = (ProgressBar) mView.findViewById(R.id.pgLoading);
            getData();
            initOnClick();
            showDoGetData();
        }
        return mView;
    }

    private void getData() {
        billingStreet = getArguments().getString(Constants.BILLINGSTREET);
        billingDate = getArguments().getString(Constants.BILLINGDATE);
        billingHour = getArguments().getString(Constants.BILLINGHOUR);
        shippingDate = getArguments().getString(Constants.SHIPPINGDATE);
        shippingHour = getArguments().getString(Constants.SHIPPINGHOUR);
        schedule = getArguments().getString(Constants.SCHEDULE);
        shippingStreet = getArguments().getString(Constants.SHIPPINGSTREET);
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
        txtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.hasConnection(mainActivity)) {
                    if (editVoucher.getText().toString().length() > 0) {
                        doGetCouponInfo(editVoucher.getText().toString());
                    } else {
                        Utils.showMessage(mainActivity, mainActivity.getResources().getString(R.string.common_message_fail));
                    }
                } else {
                    Utils.showMessage(mainActivity, mainActivity.getResources().getString(R.string.no_connection_login));
                }
            }
        });
        txtDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Utils.hasConnection(mainActivity)) {
                    doBook();
                } else {
                    Utils.showMessage(mainActivity, getString(R.string.no_connection_login));
                }
            }
        });
    }

    private void doBook() {

        showProgressDialog(true, getString(R.string.contact_please_wait));

        String headererBillingStreet = Constants.BILLINGSTREET + "=" + Utils.validateString(billingStreet);
        String headererBillingDate = Constants.BILLINGDATE + "=" + Utils.validateString(billingDate);
        String headererBillingHour = Constants.BILLINGHOUR + "=" + Utils.validateString(billingHour);
        String headererShippingDate = Constants.SHIPPINGDATE + "=" + Utils.validateString(shippingDate);
        String headererShippingHour = Constants.SHIPPINGHOUR + "=" + Utils.validateString(shippingHour);
        String headererSchedule = Constants.SCHEDULE + "=" + Utils.validateString(schedule);
        String headerershippingStreet = Constants.SHIPPINGSTREET + "=" + Utils.validateString(shippingStreet);
        String headererCouponcode = Constants.COUPONCODE + "=" + Utils.validateString(couponCode);

        RequestQueue queue = Constants.getQueue(mainActivity);
        Map<String, String> params = new HashMap<String, String>();
        try {
            params.put(Constants.AUTHORIZATION, sharedPref.getString(Constants.TOKEN, ""));
        } catch (Exception e) {

        }
        String headers = "?" + headererBillingStreet + "&" + headererBillingDate + "&" + headererBillingHour + "&" + headererShippingDate + "&" + headererShippingHour + "&" + headererSchedule + "&" + headerershippingStreet + "&" + headererCouponcode+"&"+"comments="+"";
        Utils.logE("thaond", "heders" + headers);
        GsonRequest<CommonReponse> postRequest = new GsonRequest<CommonReponse>(
                Request.Method.GET, UrlHelper.MAKE_ORDER + headers, CommonReponse.class, params, null,
                new Response.Listener<CommonReponse>() {
                    public void onResponse(CommonReponse response) {
                        processAfterBook(response);
                        showProgressDialog(false, "");
                        // processResponse(response);
                    }
                }, new Response.ErrorListener(

        ) {
            public void onErrorResponse(VolleyError error) {
                showProgressDialog(false, "");

                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null && networkResponse.statusCode == 401) {
                    Utils.showDialogTimeOut(mainActivity);
                    // HTTP Status Code: 401 Unauthorized
                } else {
                    Utils.showMessage(mainActivity,
                            getResources().getString(R.string.error));
                }
            }

        });
        queue.add(postRequest);
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constants.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void processAfterBook(CommonReponse commonReponse) {
        if (commonReponse.getException() == null) {
            Utils.showMessage(mainActivity,
                    commonReponse.getMessage());
            mainActivity.selectFragment(CalendarWassiiFragment.newInstance());
        } else {
            Utils.showMessage(mainActivity,
                    commonReponse.getMessage());
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

        RequestQueue queue = Constants.getQueue(mainActivity);
        Map<String, String> params = new HashMap<String, String>();
        try {
            params.put(Constants.AUTHORIZATION, sharedPref.getString(Constants.TOKEN, ""));
        } catch (Exception e) {

        }
        GsonRequest<InfoAccount> postRequest = new GsonRequest<InfoAccount>(
                Request.Method.GET, UrlHelper.GET_ACCOUNT_INFO, InfoAccount.class, params, null,
                new Response.Listener<InfoAccount>() {
                    public void onResponse(InfoAccount response) {
                        showProgressDialog(false, getString(R.string.contact_please_wait));
                        processResponse(response);
                    }
                }, new Response.ErrorListener(

        ) {
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null && networkResponse.statusCode == 401) {
                    isUnAuthenticated = true;
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
                txtCurrentAccountMoney.setText(monney + " " + mainActivity.getResources().getString(R.string.st_money));
                txtError.setVisibility(View.GONE);
                pgLoading.setVisibility(View.GONE);
                layoutContent.setVisibility(View.VISIBLE);
            }
        } catch (Exception ex) {
        }
    }

    private void doGetCouponInfo(String couponcode) {
        showProgressDialog(true, getString(R.string.contact_please_wait));
        RequestQueue queue = Constants.getQueue(mainActivity);
        Map<String, String> params = new HashMap<String, String>();
        try {
            params.put(Constants.AUTHORIZATION, sharedPref.getString(Constants.TOKEN, ""));
        } catch (Exception e) {

        }

        String headers = "?" + Constants.COUPONCODE + "=" + Utils.validateString(couponcode);
        GsonRequest<CodeVourcher> postRequest = new GsonRequest<CodeVourcher>(
                Request.Method.GET, UrlHelper.GET_COUPON_INFO + headers, CodeVourcher.class, params, null,
                new Response.Listener<CodeVourcher>() {
                    public void onResponse(CodeVourcher response) {
                        showProgressDialog(false, getString(R.string.contact_please_wait));
                        processtCouponInfo(response);
                    }
                }, new Response.ErrorListener(

        ) {
            public void onErrorResponse(VolleyError error) {
                showProgressDialog(false, getString(R.string.contact_please_wait));
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null && networkResponse.statusCode == 401) {
                    Utils.showDialogTimeOut(mainActivity);
                    // HTTP Status Code: 401 Unauthorized
                }
                txtPromotionCodeMoney.setVisibility(View.GONE);
                txtPromotionCode.setVisibility(View.GONE);

            }
        });
        queue.add(postRequest);
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constants.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void processtCouponInfo(CodeVourcher response) {
        if (response.getException() == null) {
            try {
                couponCode = response.getCode();
                txtPromotionCodeMoney.setText(response.getDiscount() + " " + mainActivity.getResources().getString(R.string.st_money));
                editVoucher.setText("");
                txtPromotionCodeMoney.setVisibility(View.VISIBLE);
                txtPromotionCode.setVisibility(View.VISIBLE);
                Utils.showMessage(mainActivity,
                        response.getMessage());
                InputMethodManager inputMethodManager = (InputMethodManager) mainActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(mainActivity.getCurrentFocus().getWindowToken(), 0);
            } catch (Exception e) {
                txtPromotionCodeMoney.setVisibility(View.GONE);
                txtPromotionCode.setVisibility(View.GONE);

                Utils.showMessage(mainActivity,
                        getResources().getString(R.string.error));
            }
        } else {
            txtPromotionCodeMoney.setVisibility(View.GONE);
            txtPromotionCode.setVisibility(View.GONE);

            Utils.showMessage(mainActivity,
                    response.getMessage());
        }
    }
}
