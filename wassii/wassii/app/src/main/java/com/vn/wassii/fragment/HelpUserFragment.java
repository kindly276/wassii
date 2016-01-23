package com.vn.wassii.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.thao.mylibrary.GsonRequest;
import com.vn.wassii.R;
import com.vn.wassii.activity.LoginActivity;
import com.vn.wassii.activity.MainActivity;
import com.vn.wassii.adapter.HelpUserAdapter;
import com.vn.wassii.model.Support;
import com.vn.wassii.model.SupportReponse;
import com.vn.wassii.utils.Constants;
import com.vn.wassii.utils.UrlHelper;
import com.vn.wassii.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by PC0280 on 1/7/2016.
 */
public class HelpUserFragment extends Fragment {
    private View mView;

    private MainActivity mainActivity;
    private RecyclerView recyleHelp;
    private List<Support> tmpInfoList;
    private List<ParentListItem> listItemList;

    private boolean isNetworkError, isNoConnection, isNoData, isUnAuthenticated;
    private TextView txtError;
    private ProgressBar pgLoading;
    private SharedPreferences sharedPref;
    private RequestQueue queue;
    private HelpUserAdapter adapter;
    public static HelpUserFragment newInstance() {
        HelpUserFragment myFragment = new HelpUserFragment();
        return myFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(mView==null){

            mainActivity = (MainActivity) getActivity();

            mView = inflater.inflate(R.layout.fragment_help_user, container, false);
            mainActivity.setToolbar((Toolbar)mView.findViewById(R.id.toolbar));
            sharedPref = mainActivity.getSharedPreferences(
                    getResources().getString(R.string.login_share_private),
                    Context.MODE_PRIVATE);
            txtError = (TextView) mView.findViewById(R.id.txtError);
            pgLoading = (ProgressBar) mView.findViewById(R.id.pgLoading);
            recyleHelp = (RecyclerView) mView.findViewById(R.id.recycler_help_user);
            // use a linear layout manager
            recyleHelp.setLayoutManager(new LinearLayoutManager(mainActivity));

            tmpInfoList = new ArrayList<Support>();
            listItemList=new ArrayList<ParentListItem>();

            initOnClickListener();
            showDoGetListSupport();
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
                        showDoGetListSupport();

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
        GsonRequest<SupportReponse> postRequest = new GsonRequest<SupportReponse>(
                Request.Method.GET, UrlHelper.GET_SUPPORT_MENU, SupportReponse.class, params, null,
                new Response.Listener<SupportReponse>() {
                    public void onResponse(SupportReponse response) {
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

    private void processResponse(SupportReponse messages) {
        try {
            tmpInfoList.clear();
            if (messages.getException() == null) {
                if (messages.getSupportMenu() != null) {
                    tmpInfoList.addAll(messages.getSupportMenu());
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
                    recyleHelp.setVisibility(View.GONE);

                } else {
                    if (tmpInfoList.size() > 0) {
                        listItemList.clear();
                        listItemList.addAll(tmpInfoList);
                        adapter = new HelpUserAdapter(mainActivity,listItemList);
                        recyleHelp.setAdapter(adapter);
                        pgLoading.setVisibility(View.GONE);
                        txtError.setVisibility(View.GONE);
                        recyleHelp.setVisibility(View.VISIBLE);
                    } else {
                        txtError.setText(mainActivity.getResources().getString(R.string.no_data));
                        txtError.setVisibility(View.VISIBLE);
                        pgLoading.setVisibility(View.GONE);
                        recyleHelp.setVisibility(View.GONE);
                    }
                }
            }
        } catch (Exception ex) {
            if (mainActivity != null) {
            }
        }
    }

    private void showDoGetListSupport() {
        if (Utils.hasConnection(mainActivity)) {
            doGetListPrice();
        } else {
            isNetworkError = true;
            isNoConnection = true;
            displayDataFirst();
        }
    }
}
