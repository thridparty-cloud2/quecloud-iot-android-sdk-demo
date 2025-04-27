package com.quectel.app.demo.ui.mine

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.quectel.app.demo.adapter.LanAdapter
import com.quectel.app.demo.base.activity.QuecBaseActivity
import com.quectel.app.demo.common.AppVariable
import com.quectel.app.demo.databinding.ActivityListUpdateBinding
import com.quectel.app.usersdk.bean.LangBean
import com.quectel.app.usersdk.service.QuecUserService

class UpdataUserActivity(
    //1 语言   2 国家   3时区
    val lang: Int = 1,
    val nationality: Int = 2,
    val timezone: Int = 3,
) : QuecBaseActivity<ActivityListUpdateBinding>(), OnItemClickListener {

    var type: Int = -1

    var mAdapter: LanAdapter? = null

    override fun getViewBinding(): ActivityListUpdateBinding {
        return ActivityListUpdateBinding.inflate(layoutInflater)
    }

    override fun initView(savedInstanceState: Bundle?) {
        type = intent.getIntExtra("type", -1)
        mAdapter = LanAdapter(this@UpdataUserActivity, null)
        binding.mList.setAdapter(mAdapter)
        binding.mList.setLayoutManager(LinearLayoutManager(this@UpdataUserActivity))
        mAdapter!!.animationEnable = true
        mAdapter!!.setOnItemClickListener(this@UpdataUserActivity)
    }

    override fun initData() {
        when (type) {
            lang -> {
                QuecUserService.getQueryLanguageList { it ->
                    it.data
                    mAdapter!!.setNewInstance(it.data)
                }
            }

            nationality -> {
                QuecUserService.getNationalityList { it ->
                    it.data
                    mAdapter!!.setNewInstance(it.data)
                }
            }

            timezone -> {
                QuecUserService.getTimezoneList { it ->
                    it.data
                    mAdapter!!.setNewInstance(it.data)
                }
            }
        }
    }

    override fun onItemClick(
        adapter: BaseQuickAdapter<*, *>,
        view: View,
        position: Int,
    ) {
        val data: LangBean =
            adapter.data[position] as LangBean

        when (type) {
            lang -> {
                QuecUserService.updateUserLang(data.id as Int) { result ->
                    handlerResult(result)
                    AppVariable.setMineChange()
                    if (result.isSuccess) {
                        finish()
                    }
                }
            }

            nationality -> {
                QuecUserService.updateUserNationality(data.id as Int) { result ->
                    handlerResult(result)
                    AppVariable.setMineChange()
                    if (result.isSuccess) {
                        finish()
                    }
                }
            }

            timezone -> {
                QuecUserService.updateUserTimezone(data.id as Int) { result ->
                    handlerResult(result)
                    AppVariable.setMineChange()
                    if (result.isSuccess) {
                        finish()
                    }
                }
            }
        }
    }
}