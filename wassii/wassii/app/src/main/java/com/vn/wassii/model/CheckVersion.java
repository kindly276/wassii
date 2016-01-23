package com.vn.wassii.model;

/**
 * Created by rau muong on 11/01/2016.
 */
public class CheckVersion extends CommonReponse {
    private boolean hasNew;
    private String updateURL;
    private boolean requireUpdate;

    public boolean isHasNew() {
        return hasNew;
    }

    public void setHasNew(boolean hasNew) {
        this.hasNew = hasNew;
    }

    public String getUpdateURL() {
        return updateURL;
    }

    public void setUpdateURL(String updateURL) {
        this.updateURL = updateURL;
    }

    public boolean isRequireUpdate() {
        return requireUpdate;
    }

    public void setRequireUpdate(boolean requireUpdate) {
        this.requireUpdate = requireUpdate;
    }
}
