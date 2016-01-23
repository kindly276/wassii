package com.vn.wassii.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
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
import com.vn.wassii.adapter.CategoryPagerAdapter;
import com.vn.wassii.model.Category;
import com.vn.wassii.model.ListCategory;
import com.vn.wassii.utils.Constants;
import com.vn.wassii.utils.SlidingTabLayoutUser;
import com.vn.wassii.utils.UrlHelper;
import com.vn.wassii.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rau muong on 10/01/2016.
 */
public class CategoryFragment extends Fragment {
    private View mView;

    private MainActivity mainActivity;
    private RelativeLayout relativeLayout;
    private ArrayList<Category> categories;
    private ArrayList<Category> tmpCategory;

    private boolean isNetworkError, isNoConnection, isNoData, isUnAuthenticated;
    private TextView txtError;
    private ProgressBar pgLoading;
    private SharedPreferences sharedPref;
    private RequestQueue queue;
    private CategoryPagerAdapter adapter;
    private ViewPager viewPager;
    private SlidingTabLayoutUser tableLayout;

    public static CategoryFragment newInstance() {
        CategoryFragment myFragment = new CategoryFragment();
        return myFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainActivity = (MainActivity) getActivity();
        mView = inflater.inflate(R.layout.fragment_list_category, container, false);
        mainActivity.setToolbar((Toolbar) mView.findViewById(R.id.toolbar));
        sharedPref = mainActivity.getSharedPreferences(
                getResources().getString(R.string.login_share_private),
                Context.MODE_PRIVATE);
        txtError = (TextView) mView.findViewById(R.id.txtError);
        pgLoading = (ProgressBar) mView.findViewById(R.id.pgLoading);
        relativeLayout = (RelativeLayout) mView.findViewById(R.id.layout_content);
        viewPager = (ViewPager) mView.findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(5);
        tableLayout = (SlidingTabLayoutUser) mView.findViewById(R.id.tablayout);
        tableLayout.setDistributeEvenly(true);
        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tableLayout.setCustomTabColorizer(new SlidingTabLayoutUser.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.switch_thumb_material_light);
            }
        });
        categories = new ArrayList<Category>();
        tmpCategory = new ArrayList<Category>();
        initOnClickListener();
        showDoGetListCategory();

        return mView;
    }

    private void initOnClickListener() {
        txtError.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isUnAuthenticated) {
                    Intent intent = new Intent(mainActivity, LoginActivity.class);
                    ((Activity) mainActivity).overridePendingTransition(R.anim.slide_up, R.anim.scale_down);
                    ((Activity) mainActivity).finish();
                } else {
                    if (!isNoData) {
                        isNetworkError = false;
                        isNoConnection = false;
                        txtError.setVisibility(View.GONE);
                        pgLoading.setVisibility(View.VISIBLE);
                        showDoGetListCategory();

                    }
                }
            }
        });


    }


    private void doGetListCategory() {
        final RequestQueue queue = Constants.getQueue(mainActivity);
        Map<String, String> params = new HashMap<String, String>();
        try {
            params.put(Constants.AUTHORIZATION, sharedPref.getString(Constants.TOKEN, ""));
        } catch (Exception e) {

        }
        GsonRequest<ListCategory> postRequest = new GsonRequest<ListCategory>(
                Request.Method.GET, UrlHelper.GET_CATALOGS, ListCategory.class, params, null,
                new Response.Listener<ListCategory>() {
                    public void onResponse(ListCategory response) {
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

    private void processResponse(ListCategory messages) {
        try {
            tmpCategory.clear();
            if (messages.getException() == null) {
                if (messages.getCatalogs() != null) {
                    tmpCategory.addAll(messages.getCatalogs());
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
                        txtError.setText(mainActivity.getResources().getString(R.string.error));
                    }
                    txtError.setVisibility(View.VISIBLE);
                    pgLoading.setVisibility(View.GONE);
                    relativeLayout.setVisibility(View.GONE);

                } else {
                    if (tmpCategory.size() > 0) {
                        Utils.logE("thaond","lay category");
                        categories.clear();
                        categories.addAll(tmpCategory);
                        adapter = new CategoryPagerAdapter(getChildFragmentManager(), categories);
                        viewPager.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        tableLayout.setViewPager(viewPager);

                        pgLoading.setVisibility(View.GONE);
                        txtError.setVisibility(View.GONE);
                        relativeLayout.setVisibility(View.VISIBLE);
                    } else {
                        txtError.setText(mainActivity.getResources().getString(R.string.no_data));
                        txtError.setVisibility(View.VISIBLE);
                        pgLoading.setVisibility(View.GONE);
                        relativeLayout.setVisibility(View.GONE);
                    }
                }
            }
        } catch (Exception ex) {
            if (mainActivity != null) {
            }
        }
    }

    private void showDoGetListCategory() {
        if (Utils.hasConnection(mainActivity)) {
            doGetListCategory();
        } else {
            isNetworkError = true;
            isNoConnection = true;
            displayDataFirst();
        }
    }
}
