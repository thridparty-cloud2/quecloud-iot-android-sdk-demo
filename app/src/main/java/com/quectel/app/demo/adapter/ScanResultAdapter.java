package com.quectel.app.demo.adapter;

import android.content.Context;
import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.quectel.app.blesdk.ble.ScanDevice;
import com.quectel.app.demo.R;
import com.quectel.app.demo.bean.UserDeviceList;

import java.util.List;

public class ScanResultAdapter extends BaseQuickAdapter<ScanDevice, BaseViewHolder> {

    private Context mContext;

    public ScanResultAdapter(Context context, List data) {
        super(R.layout.scan_result_item, data);
        this.mContext  = context;
    }
    @Override
    protected void convert(BaseViewHolder helper, final ScanDevice item) {
        helper.setText(R.id.tv_name, "name: "+ item.getName());
        helper.setText(R.id.tv_mac,"mac: "+ item.getMac());
    }



}

