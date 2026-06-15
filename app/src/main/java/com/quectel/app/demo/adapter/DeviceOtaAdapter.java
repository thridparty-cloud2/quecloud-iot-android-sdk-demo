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
        helper.setText(R.id.tv_version, mContext.getString(R.string.ota_version_prefix) + waitUpgradeDevice.getVersion());
        helper.setText(R.id.tv_desc, mContext.getString(R.string.ota_desc_prefix) + waitUpgradeDevice.getDesc());

        String statusText = "";
        if (waitUpgradeDevice.getDeviceStatus() == OtaUpgradeStatus.UPGRADING
                || (waitUpgradeDevice.getUserConfirmStatus() == OtaUpgradeStatus.UPGRADING && waitUpgradeDevice.getDeviceStatus() == OtaUpgradeStatus.NOT_UPGRADE)) {
            statusText = mContext.getString(R.string.ota_status_upgrading) + waitUpgradeDevice.getUpgradeProgress() * 100 + "%";
        } else if (waitUpgradeDevice.getDeviceStatus() == OtaUpgradeStatus.UPGRADE_SUCCESS) {
            statusText = mContext.getString(R.string.ota_status_success);
        } else if (waitUpgradeDevice.getDeviceStatus() == OtaUpgradeStatus.UPGRADE_FAILED_IN_NOT_UPGRADE) {
            statusText = mContext.getString(R.string.ota_status_failed);
        } else if (waitUpgradeDevice.getDeviceStatus() == OtaUpgradeStatus.UPGRADE_FAILED) {
            statusText = mContext.getString(R.string.ota_status_failed_retry);
        } else {
            statusText = mContext.getString(R.string.ota_status_pending);
        }

        helper.setText(R.id.tv_status_text, statusText);
    }

}
