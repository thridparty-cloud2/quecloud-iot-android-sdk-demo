package com.quectel.app.demo.ui.device.automate

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.quectel.app.demo.adapter.AutoMateAdapter
import com.quectel.app.demo.base.activity.QuecBaseActivity
import com.quectel.app.demo.bean.EditListBean
import com.quectel.app.demo.common.AppVariable
import com.quectel.app.demo.databinding.ActivityDeviceAutoMateBinding
import com.quectel.app.demo.dialog.EditListTextPopup
import com.quectel.app.demo.utils.ToastUtils
import com.quectel.sdk.automate.api.model.QuecAutoListItemModel
import com.quectel.sdk.automate.api.model.QuecAutomationActionModel
import com.quectel.sdk.automate.api.model.QuecAutomationConditionModel
import com.quectel.sdk.automate.api.model.QuecAutomationModel
import com.quectel.sdk.automate.api.model.QuecAutomationPreconditionModel
import com.quectel.sdk.automate.api.model.QuecAutomationTimeModel
import com.quectel.sdk.automate.service.QuecAutomateService

class AutoMateListActivity : QuecBaseActivity<ActivityDeviceAutoMateBinding>() {
    private lateinit var mAdapter: AutoMateAdapter

    override fun getViewBinding(): ActivityDeviceAutoMateBinding {
        return ActivityDeviceAutoMateBinding.inflate(layoutInflater)
    }

    override fun initView(savedInstanceState: Bundle?) {
        mAdapter = AutoMateAdapter(this@AutoMateListActivity, null)
        binding.rvList.setAdapter(mAdapter)
        binding.rvList.setLayoutManager(LinearLayoutManager(this@AutoMateListActivity))
        binding.ivAdd.setOnClickListener {
            addAutoMate()
        }
        mAdapter.setOnItemClickListener { adapter, _, position ->
            val data: QuecAutoListItemModel =
                adapter.data[position] as QuecAutoListItemModel
            val intent = Intent(this@AutoMateListActivity, AutoMateInfoActivity::class.java)
            intent.putExtra("data", data)
            startActivity(intent)
        }
    }

    private fun addAutoMate() {
        val arrayData: ArrayList<EditListBean> = ArrayList()
        //name
        arrayData.add(EditListBean("自动化名称"))
        //出发时间24制 09:07
        arrayData.add(EditListBean("定时时间24制 例如 09:07"))
        arrayData.add(EditListBean("场景名称"))
        arrayData.add(EditListBean("场景id"))

        EditListTextPopup(mContext).apply {
            setTitle("添加一个新的自动化")
            setSure("添加")
            setDataList(arrayData)
            setEditTextListener {
                val automationName =
                    arrayData.find { it.name == arrayData[0].name }?.value
                val automationTime =
                    arrayData.find { it.name == arrayData[1].name }?.value
                val sceneName =
                    arrayData.find { it.name == arrayData[2].name }?.value
                val sceneId =
                    arrayData.find { it.name == arrayData[3].name }?.value

                if (automationName.isNullOrEmpty() || automationTime.isNullOrEmpty() ||
                    sceneName.isNullOrEmpty() || sceneId.isNullOrEmpty()
                ) {
                    ToastUtils.showShort(this@AutoMateListActivity, "各种参数不能为空")
                    return@setEditTextListener
                }
                showOrHideLoading(true)
                QuecAutomateService.addAutomation(
                    QuecAutomationModel(
                        "",
                        0L,
                        1,
                        null,
                        automationName,
                        1,
                        QuecAutomationPreconditionModel().apply {
                            effectDate = ""
                            effectDateType = 0
                            effectTimeType = 2
                            location = ""
                            timeZone = "GMT+08:00"
                        },
                        listOf(
                            QuecAutomationConditionModel(
                                type = 1,
                                timer = QuecAutomationTimeModel(
                                    0, automationTime, "", "GMT+08:00"
                                ),
                            )
                        ),
                        listOf(
                            QuecAutomationActionModel(
                                type = 4,
                                icon = "https://iot-oss.quectelcn.com/quec_scene_1.png",
                                name = sceneName,
                                sceneId = sceneId,
                                sort = 1,
                            )
                        )
                    )
                ) { ret ->
                    runOnUiThread {
                        showOrHideLoading(true)
                        handlerResult(ret)
                        if (ret.isSuccess) {
                            //请求成功
                            dismiss()
                            queryAutoMateList()
                        }
                    }
                }
            }
        }.showPopupWindow()
    }

    override fun onResume() {
        super.onResume()
        if (AppVariable.isAutoMateInfoChange) {
            AppVariable.isAutoMateInfoChange = false
            queryAutoMateList()
        }
    }

    override fun initData() {
        queryAutoMateList()
    }

    private fun queryAutoMateList() {
        showOrHideLoading(true)
        QuecAutomateService.getAutomationList(1, 10) { result ->
            runOnUiThread {
                showOrHideLoading(false)
                if (result.isSuccess) {
                    val data = result.data //请求成功, 获取到的数据
                    mAdapter.setNewInstance(data.list as MutableList)
                } else {
                    handlerResult(result)
                }
            }
        }
    }
}