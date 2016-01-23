package com.vn.wassii.adapter;

import android.annotation.SuppressLint;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder;
import com.vn.wassii.R;
import com.vn.wassii.activity.MainActivity;
import com.vn.wassii.model.Content;
import com.vn.wassii.model.Support;

import java.util.List;

/**
 * Created by thaond on 1/7/2016.
 */
public class HelpUserAdapter extends ExpandableRecyclerAdapter<HelpUserAdapter.HelperParentViewHolder, HelpUserAdapter.HelperChildViewHolder> {

    private LayoutInflater mInflater;
    public HelpUserAdapter(MainActivity context, List<ParentListItem> itemList) {
        super(itemList);
        Log.e("thaond","abc"+itemList.size());
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public HelperParentViewHolder onCreateParentViewHolder(ViewGroup viewGroup) {
        View view = mInflater.inflate(R.layout.item_list_helper_user, viewGroup, false);
        return new HelperParentViewHolder(view);
    }

    @Override
    public HelperChildViewHolder onCreateChildViewHolder(ViewGroup viewGroup) {
        View view = mInflater.inflate(R.layout.item_list_helper_description, viewGroup, false);
        return new HelperChildViewHolder(view);
    }

    @Override
    public void onBindParentViewHolder(HelperParentViewHolder crimeParentViewHolder, int i, ParentListItem parentListItem) {
        Support info = (Support) parentListItem;
        crimeParentViewHolder.TitleTextView.setText(info.getTitle().toString());
    }


    @Override
    public void onBindChildViewHolder(HelperChildViewHolder crimeChildViewHolder, int i, Object childListItem) {
        Content childListItem1 = (Content) childListItem;
        if(childListItem1.getContent()==null){
            crimeChildViewHolder.textView.setText(Html.fromHtml("<b>"+childListItem1.getQuestion()+"</b>"+"<br>"+childListItem1.getAnwser()));
        }else{
            crimeChildViewHolder.textView.setText(childListItem1.getContent());

        }

    }
    public class HelperParentViewHolder extends ParentViewHolder {
        public static final float INITIAL_POSITION = 0.0f;
        public static final float ROTATED_POSITION = 180f;
        public  final boolean HONEYCOMB_AND_ABOVE = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;

        public TextView TitleTextView;

        public HelperParentViewHolder(View itemView) {
            super(itemView);
            TitleTextView = (TextView) itemView.findViewById(R.id.txt_title_help);
        }

        @SuppressLint("NewApi")
        @Override
        public void setExpanded(boolean expanded) {
            super.setExpanded(expanded);
            if (!HONEYCOMB_AND_ABOVE) {
                return;
            }
        }
    }
    public class HelperChildViewHolder extends ChildViewHolder {

        public TextView textView;

        public HelperChildViewHolder(View itemView) {
            super(itemView);

            textView = (TextView) itemView.findViewById(R.id.txt_title_des);
        }
    }
}
