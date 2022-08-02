package com.quectel.app.demo.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.quectel.app.demo.R;
import com.quectel.app.demo.bean.LanVO;
import com.quectel.app.demo.bean.UserDeviceList;

import java.util.List;

public class DeviceAdapter extends BaseQuickAdapter<UserDeviceList.DataBean.ListBean, BaseViewHolder> {

    private Context mContext;

    public DeviceAdapter(Context context, List data) {
        super(R.layout.device_item, data);
        this.mContext  = context;
    }
    @Override
    protected void convert(BaseViewHolder helper, final UserDeviceList.DataBean.ListBean item) {
        helper.setText(R.id.tv_pk, "pk: "+ item.getProductKey());
        helper.setText(R.id.tv_dk,"dk: "+ item.getDeviceKey());
        helper.setText(R.id.tv_device_name,"deviceName: "+ item.getDeviceName());
        helper.setText(R.id.tv_device_status,"设备状态: "+ item.getDeviceStatus());

        boolean isShare = TextUtils.isEmpty(item.getShareCode());
        helper.setText(R.id.tv_share_status,isShare?"自己设备":"被分享设备");


    }



}

