package com.vn.wassii.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vn.wassii.R;
import com.vn.wassii.activity.MainActivity;

/**
 * Created by rau muong on 02/01/2016.
 */
public class DoneBookServiceFragment extends Fragment {
    private View view;
    private MainActivity mainActivity;
    private Toolbar toolbar;

    public static final DoneBookServiceFragment newInstance(){
        DoneBookServiceFragment myFragment=new DoneBookServiceFragment();
        return myFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(view==null){
            view=inflater.inflate(R.layout.fragment_order_confirm,container,false);
            mainActivity=(MainActivity)getActivity();
            toolbar=(Toolbar)view.findViewById(R.id.toolbar);


            mainActivity.setToolbar(toolbar);
        }

        return view;
    }

}
