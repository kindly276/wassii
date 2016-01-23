package com.vn.wassii.model;

/**
 * Created by rau muong on 10/01/2016.
 */
public class PriceProduct {
    private String productId;
    private String smallImageURL;
    private String price;
    private String largeImageURL;
    private String name;
    private String mediumImageURL;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getSmallImageURL() {
        return smallImageURL;
    }

    public void setSmallImageURL(String smallImageURL) {
        this.smallImageURL = smallImageURL;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getLargeImageURL() {
        return largeImageURL;
    }

    public void setLargeImageURL(String largeImageURL) {
        this.largeImageURL = largeImageURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMediumImageURL() {
        return mediumImageURL;
    }

    public void setMediumImageURL(String mediumImageURL) {
        this.mediumImageURL = mediumImageURL;
    }
}
