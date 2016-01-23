package com.vn.wassii.model;

/**
 * Created by rau muong on 10/01/2016.
 */
public class CalendarWassii  {
    private int orderId;
    private int schedule;
    private String shippingStreet;
    private String billingDate;
    private String billingHour;
    private String shippingDate;
    private String shippingHour;
    private String comments;
    private String orderStatus;
    private String billingStreet;

    public String getBillingStreet() {
        return billingStreet;
    }

    public void setBillingStreet(String billingStreet) {
        this.billingStreet = billingStreet;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getSchedule() {
        return schedule;
    }

    public void setSchedule(int schedule) {
        this.schedule = schedule;
    }

    public String getShippingStreet() {
        return shippingStreet;
    }

    public void setShippingStreet(String shippingStreet) {
        this.shippingStreet = shippingStreet;
    }

    public String getBillingDate() {
        return billingDate;
    }

    public void setBillingDate(String billingDate) {
        this.billingDate = billingDate;
    }

    public String getBillingHour() {
        return billingHour;
    }

    public void setBillingHour(String billingHour) {
        this.billingHour = billingHour;
    }

    public String getShippingDate() {
        return shippingDate;
    }

    public void setShippingDate(String shippingDate) {
        this.shippingDate = shippingDate;
    }

    public String getShippingHour() {
        return shippingHour;
    }

    public void setShippingHour(String shippingHour) {
        this.shippingHour = shippingHour;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
