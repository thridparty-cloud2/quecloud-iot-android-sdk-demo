package com.quectel.app.demo.adapter;

import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.quectel.app.demo.R;
import com.quectel.app.demo.bean.DeviceOtaModel;
import com.quectel.sdk.ota.upgrade.model.OtaUpgradeStatus;

import java.util.List;

public class DeviceOtaAdapter extends BaseQuickAdapter<DeviceOtaModel, BaseViewHolder> {

    private Context mContext;

    public DeviceOtaAdapter(Context context, List data) {
        super(R.layout.device_ota_item, data);
        this.mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, final DeviceOtaModel waitUpgradeDevice) {
        helper.setText(R.id.tv_device_name, waitUpgradeDevice.getDeviceName());
        helper.setText(R.id.tv_version, "版本：" + waitUpgradeDevice.getVersion());
        helper.setText(R.id.tv_desc, "描述：" + waitUpgradeDevice.getDesc());

        String statusText = "";
        if (waitUpgradeDevice.getDeviceStatus() == OtaUpgradeStatus.UPGRADING
                || (waitUpgradeDevice.getUserConfirmStatus() == OtaUpgradeStatus.UPGRADING && waitUpgradeDevice.getDeviceStatus() == OtaUpgradeStatus.NOT_UPGRADE)) {
            statusText = "状态：升级中" + "进度：" + waitUpgradeDevice.getUpgradeProgress() * 100 + "%";
        } else if (waitUpgradeDevice.getDeviceStatus() == OtaUpgradeStatus.UPGRADE_SUCCESS) {
            statusText = "状态：升级成功";

        } else if (waitUpgradeDevice.getDeviceStatus() == OtaUpgradeStatus.UPGRADE_FAILED_IN_NOT_UPGRADE) {
            statusText = "状态：升级失败";
        } else if (waitUpgradeDevice.getDeviceStatus() == OtaUpgradeStatus.UPGRADE_FAILED) {
            statusText = "状态：升级失败，请重试";
        } else {
            statusText = "状态：未升级，请确认升级";
        }

        helper.setText(R.id.tv_status_text, statusText);
    }

}

