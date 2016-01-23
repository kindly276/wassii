package com.vn.wassii.model;

import java.util.List;

/**
 * Created by rau muong on 10/01/2016.
 */
public class ListiCalendarWassii extends CommonReponse {
    List<CalendarWassii> orders;

    public List<CalendarWassii> getOrders() {
        return orders;
    }

    public void setOrders(List<CalendarWassii> orders) {
        this.orders = orders;
    }
}
