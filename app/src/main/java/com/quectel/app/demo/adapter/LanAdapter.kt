package com.quectel.app.demo.adapter

import android.content.Context
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.quectel.app.demo.R
import com.quectel.app.usersdk.bean.LangBean

class LanAdapter(private val mContext: Context, data: MutableList<LangBean>?) :

    BaseQuickAdapter<LangBean, BaseViewHolder>(R.layout.lan_item, data) {


    override fun convert(helper: BaseViewHolder, item: LangBean) {
        helper.setText(R.id.tv_text, item.`val`)
    }
}
