package com.quectel.app.demo.adapter

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.quectel.app.demo.R

class MinePhotoAdapter(
    private val mContext: Context,
    data: ArrayList<String>,
) :

    BaseQuickAdapter<String, BaseViewHolder>(R.layout.simple_list_item_imageview, data) {

    override fun convert(helper: BaseViewHolder, item: String) {
        val img = helper.getView<ImageView>(R.id.iv_photo)
        Glide.with(mContext)
            .load(item)
            .placeholder(R.mipmap.user_head)
            .into(img)
    }
}

