package com.vn.wassii.model;

/**
 * Created by thaond on 1/11/2016.
 */
public class Language {
    private boolean isSelect;
    private int drawablenormal;
    private int drawablepress;
    private String name;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setIsSelect(boolean isSelect) {
        this.isSelect = isSelect;
    }

    public int getDrawablenormal() {
        return drawablenormal;
    }

    public void setDrawablenormal(int drawablenormal) {
        this.drawablenormal = drawablenormal;
    }

    public int getDrawablepress() {
        return drawablepress;
    }

    public void setDrawablepress(int drawablepress) {
        this.drawablepress = drawablepress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
