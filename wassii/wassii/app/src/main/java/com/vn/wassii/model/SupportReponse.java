package com.vn.wassii.model;

import java.util.List;

/**
 * Created by PC0280 on 1/11/2016.
 */
public class SupportReponse extends CommonReponse {
    private List<Support> supportMenu;

    public List<Support> getSupportMenu() {
        return supportMenu;
    }

    public void setSupportMenu(List<Support> supportMenu) {
        this.supportMenu = supportMenu;
    }
}
