package com.quectel.app.demo.adapter

import android.content.Context
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.quectel.app.demo.R
import com.quectel.app.device.bean.QuecDeviceGroupInfoModel

class DeviceGroupAdapter(
    private val mContext: Context,
    data: MutableList<QuecDeviceGroupInfoModel>?,
) :
    BaseQuickAdapter<QuecDeviceGroupInfoModel, BaseViewHolder>(R.layout.device_group_item, data) {
    override fun convert(helper: BaseViewHolder, item: QuecDeviceGroupInfoModel) {
        helper.setText(R.id.tv_dgid, "dgid: " + item.dgid)
        helper.setText(R.id.tv_group_name, "groupName: " + item.name)

        val type = item.deviceGroupType
        if (type == 1) {
            helper.setText(R.id.tv_group_type, "自己分组")
        } else {
            helper.setText(R.id.tv_group_type, "接受别人的分组")
        }
    }
}

