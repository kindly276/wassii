package com.vn.wassii.model;

import java.util.List;

/**
 * Created by rau muong on 10/01/2016.
 */
public class ListCategory extends CommonReponse {
    private List<Category>  catalogs;

    public List<Category> getCatalogs() {
        return catalogs;
    }

    public void setCatalogs(List<Category> catalogs) {
        this.catalogs = catalogs;
    }
}
