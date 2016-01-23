package com.vn.wassii.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.vn.wassii.R;

/**
 * Created by rau muong on 07/01/2016.
 */
public class SampleActivity extends FragmentActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vidu);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if(supportMapFragment!=null){
            GoogleMap googleMap=supportMapFragment.getMap();
        }
    }

}
