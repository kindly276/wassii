package com.vn.wassii.model;

import java.util.List;

/**
 * Created by rau muong on 04/01/2016.
 */
public class LoginReponse extends CommonReponse{
    private String phone;
    private boolean verified;
    private String languageId;
    private String fullName;
    private String userId;
    private String token;
    private List<ListAddressesReponse> addresses;

    public List<ListAddressesReponse> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<ListAddressesReponse> addresses) {
        this.addresses = addresses;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getLanguageId() {
        return languageId;
    }

    public void setLanguageId(String languageId) {
        this.languageId = languageId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
