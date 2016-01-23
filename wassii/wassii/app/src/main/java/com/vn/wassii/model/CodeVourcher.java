package com.vn.wassii.model;

/**
 * Created by PC0280 on 1/9/2016.
 */
public class CodeVourcher extends CommonReponse {
    private String discount;
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }
}
