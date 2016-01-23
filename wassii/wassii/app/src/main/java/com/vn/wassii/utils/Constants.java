package com.vn.wassii.utils;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by rau muong on 04/01/2016.
 */
public class Constants {
//    public static RequestQueue queue;

    public static RequestQueue getQueue(Context context) {
        RequestQueue queue = null;
        queue = Volley.newRequestQueue(context);
        return queue;
    }
    public static final int MY_SOCKET_TIMEOUT_MS = 5000;
    public static final String AUTHORIZATION = "AUTHORIZATION";
    public static final String AUTHORIZATION_ID = "Basic d2Fzc2lpYXBwOndhc3NpaS5jb21AMTI=";
    public static final String PHONE = "phone";
    public static final String LANGUAGEID = "languageId";
    public static final String FULLNAME = "fullName";
    public static final String TOKEN = "token";
    public static final String PASSWORD = "password";
    public static final String COMPANYID = "companyId";
    public static final String COMPANY_ID_NUMBER = "20155";
    public static final String VN_LANGUAGE = "vi_VN";
    public static final String JA_LANGUAGE = "ja_JP";
    public static final String KO_LANGUAGE = "ko_KR";
    public static final String EN_LANGUAGE = "en_GB";
    public static final String CODE = "code";
    public static final String TYPE = "type";
    public static final String ADDRESS = "address";
    public static final String NEWPASS = "newPass";
    public static final String UNAUTHENTICATED = "newPass";
    public static final String COUPONCODE = "couponCode";
    public static final String LOCATION = "location";

    public static final String BILLINGSTREET = "billingStreet";
    public static final String BILLINGDATE = "billingDate";
    public static final String BILLINGHOUR = "billingHour";
    public static final String SHIPPINGDATE = "shippingDate";
    public static final String SHIPPINGHOUR = "shippingHour";
    public static final String SCHEDULE = "schedule";
    public static final String SHIPPINGSTREET = "shippingStreet";
    public static final String ORDERID = "orderId";
    public static final String CATEGORYID = "categoryId";
    public static final String VERSION = "version";

}
