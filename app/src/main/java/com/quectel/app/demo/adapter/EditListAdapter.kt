package com.quectel.app.demo.adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.quectel.app.demo.R
import com.quectel.app.demo.bean.EditListBean

class EditListAdapter(private val mContext: Context?, data: MutableList<EditListBean>?) :

    BaseQuickAdapter<EditListBean, BaseViewHolder>(R.layout.edit_list_item, data) {

    override fun convert(helper: BaseViewHolder, item: EditListBean) {

        val mEtText = helper.getView<EditText>(R.id.et_edit_list)
        mEtText.setHint(item.name)
        mEtText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                item.value = s.toString().trim()
            }
        })
    }

    val allContent: MutableList<EditListBean>
        get() {
            return data
        }
}

