package com.vn.wassii.model;

import java.util.List;

/**
 * Created by rau muong on 10/01/2016.
 */
public class ListProductPrice extends CommonReponse {
    private List<PriceProduct> products;

    public List<PriceProduct> getProducts() {
        return products;
    }

    public void setProducts(List<PriceProduct> products) {
        this.products = products;
    }
}
