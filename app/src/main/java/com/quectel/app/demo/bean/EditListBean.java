package com.quectel.app.demo.bean;

public class EditListBean {
    public EditListBean() {

    }

    public EditListBean(String name) {
        this.name = name;
    }

    private String name;
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
