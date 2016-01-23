package com.vn.wassii.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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
import com.vn.wassii.adapter.PriceProductAdapter;
import com.vn.wassii.model.ListProductPrice;
import com.vn.wassii.model.PriceProduct;
import com.vn.wassii.utils.Constants;
import com.vn.wassii.utils.SpacesItemDecoration;
import com.vn.wassii.utils.UrlHelper;
import com.vn.wassii.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rau muong on 10/01/2016.
 */
public class ListPriceFragment extends Fragment {
    private View mView;

    private MainActivity mainActivity;
    private RecyclerView recylePrice;
    private GridLayoutManager mLayoutManager;
    private ArrayList<PriceProduct> priceProducts;
    private ArrayList<PriceProduct> tmpPriceProduct;

    private boolean isNetworkError, isNoConnection, isNoData, isUnAuthenticated;
    private TextView txtError;
    private ProgressBar pgLoading;
    private SharedPreferences sharedPref;
    private RequestQueue queue;
    private PriceProductAdapter adapter;
    private int id;

    public static ListPriceFragment newInstance(int id) {
        ListPriceFragment myFragment = new ListPriceFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.CATEGORYID, id);
        myFragment.setArguments(bundle);
        return myFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(mainActivity==null){
            mainActivity = (MainActivity) getActivity();
            Utils.logE("thaond","list");
            mView = inflater.inflate(R.layout.fragment_list_price, container, false);
            id = getArguments().getInt(Constants.CATEGORYID);
            sharedPref = mainActivity.getSharedPreferences(
                    getResources().getString(R.string.login_share_private),
                    Context.MODE_PRIVATE);
            txtError = (TextView) mView.findViewById(R.id.txtError);
            pgLoading = (ProgressBar) mView.findViewById(R.id.pgLoading);
            recylePrice = (RecyclerView) mView.findViewById(R.id.recycle_price_product);
            recylePrice.setHasFixedSize(true);
            mLayoutManager = new GridLayoutManager(getActivity(), 2);
            // use a linear layout manager
            recylePrice.setLayoutManager(mLayoutManager);

            priceProducts = new ArrayList<PriceProduct>();
            tmpPriceProduct = new ArrayList<PriceProduct>();
            adapter = new PriceProductAdapter(priceProducts, mainActivity);
            recylePrice.setAdapter(adapter);
            int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.diliver);
            recylePrice.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
            initOnClickListener();
            showDoGetListPrice();
        }


        return mView;
    }

    private void initOnClickListener() {
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
                        showDoGetListPrice();

                    }
                }
            }
        });


    }


    private void doGetListPrice() {
        final RequestQueue queue = Constants.getQueue(mainActivity);
        Map<String, String> params = new HashMap<String, String>();
        try {
            params.put(Constants.AUTHORIZATION, sharedPref.getString(Constants.TOKEN, ""));
        } catch (Exception e) {

        }
        GsonRequest<ListProductPrice> postRequest = new GsonRequest<ListProductPrice>(
                Request.Method.GET, UrlHelper.GET_PRODUCTS + "?" + Constants.CATEGORYID + "=" + id, ListProductPrice.class, params, null,
                new Response.Listener<ListProductPrice>() {
                    public void onResponse(ListProductPrice response) {
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

    private void processResponse(ListProductPrice messages) {
        try {
            tmpPriceProduct.clear();
            if (messages.getException() == null) {
                if (messages.getProducts() != null) {
                    tmpPriceProduct.addAll(messages.getProducts());
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

    private void displayDataFirst() {
        processDisplayDataFirst();
    }

    private void processDisplayDataFirst() {
        try {
            if (mainActivity != null) {
                if (isNetworkError) {
                    if (isNoConnection) {
                        txtError.setText(mainActivity.getResources().getString(R.string.no_connection));

                    } else if (isNoData) {
                        txtError.setVisibility(View.GONE);
                    } else if (isUnAuthenticated) {
                        txtError.setText(mainActivity.getResources().getString(R.string.un_authenticated));
                    } else {
                        txtError.setText(mainActivity.getResources().getString(R.string.error_retry));
                    }
                    txtError.setVisibility(View.VISIBLE);
                    pgLoading.setVisibility(View.GONE);
                    recylePrice.setVisibility(View.GONE);

                } else {
                    if (tmpPriceProduct.size() > 0) {
                        priceProducts.clear();
                        priceProducts.addAll(tmpPriceProduct);
                        adapter.notifyItemInserted(priceProducts.size());
                        pgLoading.setVisibility(View.GONE);
                        txtError.setVisibility(View.GONE);
                        recylePrice.setVisibility(View.VISIBLE);
                    } else {
                        txtError.setText(mainActivity.getResources().getString(R.string.no_data));
                        txtError.setVisibility(View.VISIBLE);
                        pgLoading.setVisibility(View.GONE);
                        recylePrice.setVisibility(View.GONE);
                    }
                }
            }
        } catch (Exception ex) {
            if (mainActivity != null) {
            }
        }
    }

    private void showDoGetListPrice() {
        if (Utils.hasConnection(mainActivity)) {
            doGetListPrice();
        } else {
            isNetworkError = true;
            isNoConnection = true;
            displayDataFirst();
        }
    }
}
