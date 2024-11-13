package com.quectel.app.demo.bean;


import com.quectel.basic.common.entity.QuecBaseModel;

import java.io.Serializable;
import java.util.List;

public class OtaUpgradePlanModel implements Serializable, QuecBaseModel {


    private String deviceModel;//设备详情
    private long appointEndTime;
    private long appointStartTime;
    private int autoUpgrade;
    private List<ComVerListDTO> comVerList;
    private String dataType;
    private int deviceStatus;
    private String deviceStatusDesc;
    private long planEndTime;
    private int planId;
    private String planName;
    private long planStartTime;
    private int userConfirmStatus;
    private String userConfirmStatusDesc;
    /**qb_version表版本描述*/
    private String versionInfo;

    public long getAppointEndTime() {
        return appointEndTime;
    }

    public void setAppointEndTime(long appointEndTime) {
        this.appointEndTime = appointEndTime;
    }

    public long getAppointStartTime() {
        return appointStartTime;
    }

    public void setAppointStartTime(long appointStartTime) {
        this.appointStartTime = appointStartTime;
    }

    public int getAutoUpgrade() {
        return autoUpgrade;
    }

    public void setAutoUpgrade(int autoUpgrade) {
        this.autoUpgrade = autoUpgrade;
    }

    public List<ComVerListDTO> getComVerList() {
        return comVerList;
    }

    public void setComVerList(List<ComVerListDTO> comVerList) {
        this.comVerList = comVerList;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public int getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(int deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public String getDeviceStatusDesc() {
        return deviceStatusDesc;
    }

    public void setDeviceStatusDesc(String deviceStatusDesc) {
        this.deviceStatusDesc = deviceStatusDesc;
    }

    public long getPlanEndTime() {
        return planEndTime;
    }

    public void setPlanEndTime(long planEndTime) {
        this.planEndTime = planEndTime;
    }

    public int getPlanId() {
        return planId;
    }

    public void setPlanId(int planId) {
        this.planId = planId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public long getPlanStartTime() {
        return planStartTime;
    }

    public void setPlanStartTime(int planStartTime) {
        this.planStartTime = planStartTime;
    }

    public int getUserConfirmStatus() {
        return userConfirmStatus;
    }

    public void setUserConfirmStatus(int userConfirmStatus) {
        this.userConfirmStatus = userConfirmStatus;
    }

    public String getUserConfirmStatusDesc() {
        return userConfirmStatusDesc;
    }

    public void setUserConfirmStatusDesc(String userConfirmStatusDesc) {
        this.userConfirmStatusDesc = userConfirmStatusDesc;
    }

    public String getVersionInfo() {
        return versionInfo;
    }

    public void setVersionInfo(String versionInfo) {
        this.versionInfo = versionInfo;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public void setPlanStartTime(long planStartTime) {
        this.planStartTime = planStartTime;
    }

    public static class ComVerListDTO implements Serializable {
        private int battery;
        private String comNo;
        /**
         * 组件类型(0:模组，1：mcu)
         */
        private int comType;
        /**
         * 组件当前版本
         */
        private String cver;
        private int signal;
        private String sort;
        private int space;
        private Boolean success;
        /**
         * 源版本
         */
        private String sver;
        /**
         * 目标版本
         */
        private String tver;
        private int verId;
        private String versionInfo;
        private Boolean versionMatch;

        public int getBattery() {
            return battery;
        }

        public void setBattery(int battery) {
            this.battery = battery;
        }

        public String getComNo() {
            return comNo;
        }

        public void setComNo(String comNo) {
            this.comNo = comNo;
        }

        public int getComType() {
            return comType;
        }

        public void setComType(int comType) {
            this.comType = comType;
        }

        public String getCver() {
            return cver;
        }

        public void setCver(String cver) {
            this.cver = cver;
        }

        public int getSignal() {
            return signal;
        }

        public void setSignal(int signal) {
            this.signal = signal;
        }

        public String getSort() {
            return sort;
        }

        public void setSort(String sort) {
            this.sort = sort;
        }

        public int getSpace() {
            return space;
        }

        public void setSpace(int space) {
            this.space = space;
        }

        public Boolean getSuccess() {
            return success;
        }

        public void setSuccess(Boolean success) {
            this.success = success;
        }

        public String getSver() {
            return sver;
        }

        public void setSver(String sver) {
            this.sver = sver;
        }

        public String getTver() {
            return tver;
        }

        public void setTver(String tver) {
            this.tver = tver;
        }

        public int getVerId() {
            return verId;
        }

        public void setVerId(int verId) {
            this.verId = verId;
        }

        public String getVersionInfo() {
            return versionInfo;
        }

        public void setVersionInfo(String versionInfo) {
            this.versionInfo = versionInfo;
        }

        public Boolean getVersionMatch() {
            return versionMatch;
        }

        public void setVersionMatch(Boolean versionMatch) {
            this.versionMatch = versionMatch;
        }
    }
}