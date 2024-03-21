package com.quectel.app.demo.bean;

import com.quectel.sdk.smart.config.api.bean.DeviceBean;

public class SmartConfigDevice {

    private DeviceBean deviceBean;
    private int bindResult;
    private String message;

    public DeviceBean getDeviceBean() {
        return deviceBean;
    }

    public void setDeviceBean(DeviceBean deviceBean) {
        this.deviceBean = deviceBean;
    }

    public int getBindResult() {
        return bindResult;
    }

    public void setBindResult(int bindResult) {
        this.bindResult = bindResult;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
