package com.vn.wassii.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.vn.wassii.adapter.CalendarWassiiAdapter;
import com.vn.wassii.model.CalendarWassii;
import com.vn.wassii.model.ListiCalendarWassii;
import com.vn.wassii.utils.Constants;
import com.vn.wassii.utils.UrlHelper;
import com.vn.wassii.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rau muong on 10/01/2016.
 */
public class CalendarWassiiFragment extends Fragment {
    private View mView;

    private MainActivity mainActivity;
    private RecyclerView recyleCalendar;
    private LinearLayoutManager mLayoutManager;
    private ArrayList<CalendarWassii> calendarWassiis;
    private ArrayList<CalendarWassii> tmpCalendarWassiis;

    private boolean isNetworkError, isNoConnection, isNoData, isUnAuthenticated;
    private TextView txtError;
    private ProgressBar pgLoading;
    private SharedPreferences sharedPref;
    private RequestQueue queue;
    private CalendarWassiiAdapter adapter;
    private RelativeLayout layoutNoCalendar;

    public static CalendarWassiiFragment newInstance() {
        CalendarWassiiFragment myFragment = new CalendarWassiiFragment();
        return myFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(mView==null){
            mainActivity = (MainActivity) getActivity();
            mView = inflater.inflate(R.layout.fragment_calendar_wassii, container, false);
            mainActivity.setToolbar((Toolbar)mView.findViewById(R.id.toolbar));
            sharedPref = mainActivity.getSharedPreferences(
                    getResources().getString(R.string.login_share_private),
                    Context.MODE_PRIVATE);
            txtError = (TextView) mView.findViewById(R.id.txtError);
            pgLoading = (ProgressBar) mView.findViewById(R.id.pgLoading);
            recyleCalendar = (RecyclerView) mView.findViewById(R.id.recycle_calendar);
            layoutNoCalendar = (RelativeLayout) mView.findViewById(R.id.layout_no_calendar);
            recyleCalendar.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(getActivity());
            mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            // use a linear layout manager
            recyleCalendar.setLayoutManager(mLayoutManager);

            calendarWassiis = new ArrayList<CalendarWassii>();
            tmpCalendarWassiis = new ArrayList<CalendarWassii>();
            adapter = new CalendarWassiiAdapter(calendarWassiis, mainActivity);
            recyleCalendar.setAdapter(adapter);
            initOnClickListener();
            showDoGetListCalendar();
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
                        showDoGetListCalendar();

                    }
                }
            }
        });
        layoutNoCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

                mainActivity.selectFragment(BookServiceFragment.newInstance());
            }
        });


    }


    private void doGetListCalendarFirst() {
        final RequestQueue queue = Constants.getQueue(mainActivity);
        Map<String, String> params = new HashMap<String, String>();
        try {
            params.put(Constants.AUTHORIZATION, sharedPref.getString(Constants.TOKEN, ""));
        } catch (Exception e) {

        }
        GsonRequest<ListiCalendarWassii> postRequest = new GsonRequest<ListiCalendarWassii>(
                Request.Method.GET, UrlHelper.GET_ORDERS, ListiCalendarWassii.class, params, null,
                new Response.Listener<ListiCalendarWassii>() {
                    public void onResponse(ListiCalendarWassii response) {
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

    private void processResponse(ListiCalendarWassii messages) {
        try {
            tmpCalendarWassiis.clear();
            if (messages.getException() == null) {
                if (messages.getOrders() != null) {
                    tmpCalendarWassiis.addAll(messages.getOrders());
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
                        txtError.setVisibility(View.VISIBLE);
                    } else if (isNoData) {
                        txtError.setVisibility(View.GONE);
                        layoutNoCalendar.setVisibility(View.VISIBLE);
                    } else if (isUnAuthenticated) {
                        txtError.setText(mainActivity.getResources().getString(R.string.un_authenticated));
                        txtError.setVisibility(View.VISIBLE);
                    } else {
                        txtError.setText(mainActivity.getResources().getString(R.string.error_retry));
                        txtError.setVisibility(View.VISIBLE);
                    }
                    pgLoading.setVisibility(View.GONE);
                    recyleCalendar.setVisibility(View.GONE);

                } else {
                    if (tmpCalendarWassiis.size() > 0) {
                        calendarWassiis.clear();
                        calendarWassiis.addAll(tmpCalendarWassiis);
                        adapter.notifyItemInserted(calendarWassiis.size());
                        pgLoading.setVisibility(View.GONE);
                        txtError.setVisibility(View.GONE);
                        recyleCalendar.setVisibility(View.VISIBLE);
                    } else {
                        txtError.setText(mainActivity.getResources().getString(R.string.no_data));
                        txtError.setVisibility(View.GONE);
                        pgLoading.setVisibility(View.GONE);
                        recyleCalendar.setVisibility(View.GONE);
                        layoutNoCalendar.setVisibility(View.VISIBLE);
                    }
                }
            }
        } catch (Exception ex) {
            if (mainActivity != null) {
            }
        }
    }

    private void showDoGetListCalendar() {
        if (Utils.hasConnection(mainActivity)) {
            doGetListCalendarFirst();
        } else {
            isNetworkError = true;
            isNoConnection = true;
            displayDataFirst();
        }
    }
}
