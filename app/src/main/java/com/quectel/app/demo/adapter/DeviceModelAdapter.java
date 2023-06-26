package com.quectel.app.demo.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.quectel.app.demo.R;
import com.quectel.app.device.bean.BusinessValue;
import com.quectel.app.device.bean.QuecProductTSLPropertyModel;
import com.quectel.app.device.constant.ModelStyleConstant;
import com.suke.widget.SwitchButton;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DeviceModelAdapter extends BaseQuickAdapter<QuecProductTSLPropertyModel<?>, BaseViewHolder> {

    private Context mContext;
    private SimpleDateFormat sDataFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public DeviceModelAdapter(Context context, List data) {
        super(R.layout.model_item_layout, data);
        this.mContext = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, final QuecProductTSLPropertyModel<?> item) {
        helper.setText(R.id.tv_style, "类型:" + item.getDataType() + " 读写类型:" + item.getSubType());

        SwitchButton switch_button = helper.getView(R.id.switch_button);
        TextView tv_value = helper.getView(R.id.tv_value);
        TextView tv_name = helper.getView(R.id.tv_name);
        tv_value.setText(item.attributeValue.toString());
        tv_name.setText(item.getName());
        if (item.getDataType().equals(ModelStyleConstant.BOOL)) {
            switch_button.setVisibility(View.VISIBLE);
            tv_value.setVisibility(View.GONE);
            if (Boolean.parseBoolean(item.attributeValue.toString())) {
                switch_button.setChecked(true);
            } else {
                switch_button.setChecked(false);
            }

            if (item.getSubType().equals("R")) {
                switch_button.setEnabled(false);
            } else {
                switch_button.setEnabled(true);

            }

        } else {
            switch_button.setVisibility(View.GONE);
            tv_value.setVisibility(View.VISIBLE);
        }

        if (item.getDataType().equals(ModelStyleConstant.DATE)) {
            try {
                Date time = new Date(Long.parseLong(item.attributeValue.toString()));
                tv_value.setText(sDataFormat.format(time));
            } catch (Exception e) {
                tv_value.setText("");
                e.printStackTrace();
            }

        }

    }

}

