package com.vn.wassii.model;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.List;

/**
 * Created by PC0280 on 1/7/2016.
 */
public class Support implements ParentListItem {
    private String menuId;
    private String title;
    private List<Content> content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Content> getDescriptions() {
        return content;
    }

    public void setDescriptions(List<Content> descriptions) {
        this.content = descriptions;
    }

    @Override
    public List<Content> getChildItemList() {
        return content;
    }


    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }
}
