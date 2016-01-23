package com.vn.wassii.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.thao.mylibrary.GsonRequest;
import com.vn.wassii.R;
import com.vn.wassii.dialog.PopupCancel;
import com.vn.wassii.dialog.PopupConfirm;
import com.vn.wassii.fragment.BookServiceFragment;
import com.vn.wassii.fragment.CalendarWassiiFragment;
import com.vn.wassii.fragment.CategoryFragment;
import com.vn.wassii.fragment.HelpUserFragment;
import com.vn.wassii.fragment.InfoPersonFragment;
import com.vn.wassii.fragment.ListLanguageFragment;
import com.vn.wassii.fragment.VourcherAccountFragment;
import com.vn.wassii.model.CheckVersion;
import com.vn.wassii.utils.Constants;
import com.vn.wassii.utils.UrlHelper;
import com.vn.wassii.utils.Utils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private Toolbar toolbar;
    private SharedPreferences sharedPref;
    private String url;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // enabling action bar app icon and behaving it as toggle button
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeButtonEnabled(true);
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        toggle.setDrawerIndicatorEnabled(false);
        toolbar.setNavigationIcon(R.mipmap.ic_menu);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.LEFT);
            }
        });

        sharedPref = MainActivity.this.getSharedPreferences(
                getResources().getString(R.string.login_share_private),
                Context.MODE_PRIVATE);
        doCheckVerion();
        selectFragment(BookServiceFragment.newInstance());
        refresh();
        getSupportActionBar().setTitle("");
        //getSupportActionBar().setIcon(R.mipmap.ic_menu);
    }

    public void refresh() {
        View headerLayout =navigationView.getHeaderView(0);
        TextView panel = (TextView)headerLayout.findViewById(R.id.txt_description);
        panel.setText(getResources().getString(R.string.st_description));
        Menu menuNav = navigationView.getMenu();
        menuNav.getItem(0).setTitle(getResources().getString(R.string.st_order));
        menuNav.getItem(1).setTitle(getResources().getString(R.string.st_info_person));
        menuNav.getItem(2).setTitle(getResources().getString(R.string.st_vourcher_account));
        menuNav.getItem(3).setTitle(getResources().getString(R.string.st_calendar));
        menuNav.getItem(4).setTitle(getResources().getString(R.string.st_list_price));
        menuNav.getItem(5).setTitle(getResources().getString(R.string.st_help));
        menuNav.getItem(6).setTitle(getResources().getString(R.string.st_language));



    }

    private void doCheckVerion() {
        RequestQueue queue = Constants.getQueue(MainActivity.this);
        PackageInfo pInfo = null;
        Map<String, String> params = new HashMap<String, String>();
        try {
            params.put(Constants.AUTHORIZATION, sharedPref.getString(Constants.TOKEN, ""));
            pInfo = MainActivity.this.getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (Exception e) {

        }

        String version = pInfo.versionName;
        String headers = "?" + Constants.VERSION + "=" + Utils.validateString(version) + "&" + Constants.TYPE + "=" + "1";
        GsonRequest<CheckVersion> postRequest = new GsonRequest<CheckVersion>(
                Request.Method.GET, UrlHelper.CHECK_VERSION + headers, CheckVersion.class, params, null,
                new Response.Listener<CheckVersion>() {
                    public void onResponse(CheckVersion response) {
                        procesAfterLogin(response);

                        // processResponse(response);
                    }
                }, new Response.ErrorListener(

        ) {
            public void onErrorResponse(VolleyError error) {


            }
        });
        queue.add(postRequest);
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constants.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }

    PopupCancel popupCancel;
    PopupConfirm popupConfirm;

    private void procesAfterLogin(CheckVersion response) {
        if (response.isHasNew() && response.isRequireUpdate()) {
            url = response.getUpdateURL();
            showCancel();
        } else if (response.isHasNew() && !response.isRequireUpdate()) {
            url = response.getUpdateURL();
            showConfirm();
        }
    }

    private void showCancel() {
        popupCancel = new PopupCancel(MainActivity.this, getResources().getString(R.string.st_version_update), new PopupCancel.OnPopupConfirmListener() {
            @Override
            public void onConfirmOK() {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri
                            .parse(url)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(url)));
                }
                popupCancel.dismiss();
            }
        });
        popupCancel.show();
    }

    private void showConfirm() {
        popupConfirm = new PopupConfirm(MainActivity.this, getResources().getString(R.string.st_version_update), new PopupConfirm.OnPopupConfirmListener() {
            @Override
            public void onConfirmOK() {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri
                            .parse(url)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(url)));
                }
                popupConfirm.dismiss();
            }

            @Override
            public void onConfirmCancel() {
                popupConfirm.dismiss();
            }
        });
        popupConfirm.show();
    }

    PopupConfirm popup;

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            Fragment myFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
            if (myFragment instanceof BookServiceFragment || myFragment instanceof CalendarWassiiFragment || myFragment instanceof CategoryFragment || myFragment instanceof HelpUserFragment || myFragment instanceof InfoPersonFragment || myFragment instanceof ListLanguageFragment || myFragment instanceof VourcherAccountFragment) {
                popup = new PopupConfirm(MainActivity.this, MainActivity.this.getResources().getString(R.string.st_confirm_logout), new PopupConfirm.OnPopupConfirmListener() {
                    @Override
                    public void onConfirmOK() {
                        finish();
                        popup.dismiss();
                    }

                    @Override
                    public void onConfirmCancel() {
                        popup.dismiss();
                    }
                });
                popup.show();
            } else {
                super.onBackPressed();

            }
        } else {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                popup = new PopupConfirm(MainActivity.this, MainActivity.this.getResources().getString(R.string.st_confirm_logout), new PopupConfirm.OnPopupConfirmListener() {
                    @Override
                    public void onConfirmOK() {
                        finish();
                        popup.dismiss();
                    }

                    @Override
                    public void onConfirmCancel() {
                        popup.dismiss();
                    }
                });
                popup.show();


            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        if (id == R.id.nav_info) {
            // Handle the camera action
            selectFragment(InfoPersonFragment.newInstance());
        } else if (id == R.id.nav_order) {
            selectFragment(BookServiceFragment.newInstance());
        } else if (id == R.id.nav_help) {
            selectFragment(HelpUserFragment.newInstance());
        } else if (id == R.id.nav_vourcher) {
            selectFragment(VourcherAccountFragment.newInstance());
        } else if (id == R.id.nav_calendar) {
            selectFragment(CalendarWassiiFragment.newInstance());
        } else if (id == R.id.nav_price) {
            selectFragment(CategoryFragment.newInstance());
        } else if (id == R.id.nav_language) {
            selectFragment(ListLanguageFragment.newInstance());
        }
// else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void selectFragment(Fragment fragment) {
        String languageName = sharedPref.getString(Constants.LANGUAGEID, "");
        if (languageName != null && !languageName.equals("")) {
            if (languageName.equals(Constants.JA_LANGUAGE)) {
                languageName = "ja";
            } else if (languageName.equals(Constants.KO_LANGUAGE)) {
                languageName = "ko";

            } else if (languageName.equals(Constants.EN_LANGUAGE)) {
                languageName = "en";
            } else if (languageName.equals(Constants.VN_LANGUAGE)) {
                languageName = "vi";
                Utils.logE("thaond","languageName"+languageName);

            }
            Locale locale = new Locale(languageName);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
        }
        toolbar.setNavigationIcon(R.mipmap.ic_menu);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void setToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);

        // enabling action bar app icon and behaving it as toggle button
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeButtonEnabled(true);
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        toggle.setDrawerIndicatorEnabled(false);
        toolbar.setNavigationIcon(R.mipmap.ic_menu);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.LEFT);
                InputMethodManager inputMethodManager = (InputMethodManager) MainActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(), 0);
            }
        });
        getSupportActionBar().setTitle("");
    }
}
