package com.quectel.app.demo.adapter

import android.content.Context
import android.widget.ImageView
import androidx.core.content.ContentProviderCompat.requireContext
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.quectel.app.demo.R
import com.quectel.sdk.scene.bean.QuecSceneModel

class DeviceSceneAdapter(
    private val mContext: Context,
    data: ArrayList<QuecSceneModel>?,
) :

    BaseQuickAdapter<QuecSceneModel, BaseViewHolder>(R.layout.device_scene_item, data) {

    override fun convert(helper: BaseViewHolder, item: QuecSceneModel) {
        val icon: ImageView = helper.getView<ImageView>(R.id.iv_icon);
        Glide.with(mContext)
            .load(item.sceneInfo.icon)
            .into(icon)
        helper.setText(R.id.tv_text, item.sceneInfo.name)
    }
}

