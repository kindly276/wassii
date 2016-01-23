package com.vn.wassii.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.vn.wassii.fragment.ListPriceFragment;
import com.vn.wassii.model.Category;

import java.util.List;

/**
 * Created by rau muong on 10/01/2016.
 */
public class CategoryPagerAdapter extends FragmentStatePagerAdapter {
    private List<Category> categoryList;
    public CategoryPagerAdapter(FragmentManager fm,List<Category> categoryList) {
        super(fm);
        this.categoryList=categoryList;
    }

    @Override
    public int getCount() {
        return categoryList.size();
    }

    @Override
    public Fragment getItem(int position) {
        return ListPriceFragment.newInstance(categoryList.get(position).getCategoryId());
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return categoryList.get(position).getName();
    }
}
