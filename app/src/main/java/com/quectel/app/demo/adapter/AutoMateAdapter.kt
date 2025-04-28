package com.quectel.app.demo.adapter

import android.content.Context
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.quectel.app.demo.R
import com.quectel.sdk.automate.api.model.QuecAutoListItemModel

class AutoMateAdapter(
    private val mContext: Context,
    data: MutableList<QuecAutoListItemModel>?,
) :

    BaseQuickAdapter<QuecAutoListItemModel, BaseViewHolder>(R.layout.auto_mate_item, data) {

    override fun convert(helper: BaseViewHolder, item: QuecAutoListItemModel) {
        helper.setText(R.id.tv_text, item.name)
    }
}

