package com.quectel.app.demo.ui.device.group

import android.content.Intent
import android.os.Bundle
import com.quectel.app.demo.base.activity.QuecBaseActivity
import com.quectel.app.demo.common.AppVariable
import com.quectel.app.demo.databinding.ActivityListGroupBinding
import com.quectel.app.demo.dialog.CommonDialog
import com.quectel.app.demo.dialog.EditDoubleTextPopup
import com.quectel.app.demo.dialog.EditTextPopup
import com.quectel.app.demo.utils.ToastUtils
import com.quectel.app.device.bean.QuecDeviceGroupParamModel
import com.quectel.app.device.deviceservice.QuecDeviceGroupService
import com.quectel.basic.common.entity.QuecDeviceModel
import com.quectel.basic.common.utils.QuecGsonUtil

class DeviceListGroupActivity : QuecBaseActivity<ActivityListGroupBinding>() {

    companion object {
        const val TAG = "DeviceListGroupActivity"
    }

    private var dGid: String = ""
    private var name: String = ""
    private var shareCode: String? = ""

    override fun getViewBinding(): ActivityListGroupBinding {
        return ActivityListGroupBinding.inflate(layoutInflater)
    }

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initData() {
        dGid = intent.getStringExtra("dGid") ?: ""
        name = intent.getStringExtra("name") ?: ""
        shareCode = intent.getStringExtra("shareCode")
        if (name.isEmpty()) {
            binding.tvStatus.text = name
        }
    }

    override fun initTestItem() {
        addItem("查询设备组详情") {
            queryGroup()
        }

        addItem("修改设备组") {
            changeGroupDialog()
        }

        addItem("添加设备到设备组") {
            addDeviceToGroup()
        }

        addItem("查询设备组中的设备列表") {
            queryDeviceInGroup()
        }

        addItem("移除设备组中的设备") {
            deleteDeviceFromGroup()
        }
        addItem("删除设备组") {
            deleteGroup()
        }
        addItem("取消") {
            finish()
        }
    }

    private fun queryGroup() {
        startLoading()
        QuecDeviceGroupService.getDeviceGroupInfo(dGid) { result ->
            finishLoading()
            handlerResult(result)
            if (result.isSuccess) {
                CommonDialog.showSimpleInfo(
                    this@DeviceListGroupActivity,
                    "查询设备组详情",
                    QuecGsonUtil.gsonString(result.data)
                )
            }
        }
    }

    private fun changeGroupDialog() {
        EditTextPopup(this).apply {
            setTitle("添加设备组")
            setHint("请输入group name")
            setEditTextListener { name ->
                if (name.isNullOrEmpty()) {
                    ToastUtils.showShort(mContext, "参数不能为空")
                    return@setEditTextListener
                }
                dismiss()
                startLoading()
                val model = QuecDeviceGroupParamModel()
                model.name = name
                QuecDeviceGroupService.updateDeviceGroupInfo(dGid, model) { result ->
                    finishLoading()
                    handlerResult(result)
                    AppVariable.setGroupChange()
                    if (result.isSuccess) {
                        finish()
                    }
                }
            }
        }.showPopupWindow()
    }


    private fun addDeviceToGroup() {
        EditDoubleTextPopup(mContext).apply {
            setTitle("添加设备到设备组")
            setHint1("请输入pk")
            setHint2("请输入dk")
            setEditTextListener { pk, dk ->
                if (pk.isNullOrEmpty() || dk.isNullOrEmpty()) {
                    ToastUtils.showShort(mContext, "参数不能为空")
                    return@setEditTextListener
                }
                dismiss()
                startLoading()
                val quecDeviceModels = ArrayList<QuecDeviceModel>()
                val quecDeviceModel = QuecDeviceModel(pk, dk)
                quecDeviceModels.add(quecDeviceModel)
                AppVariable.setGroupChange()
                QuecDeviceGroupService.addDeviceToGroup(dGid, quecDeviceModels) { result ->
                    finishLoading()
                    handlerResult(result)
                    if (result.isSuccess) {
                        finish()
                    }
                }
            }
        }.showPopupWindow()
    }

    private fun queryDeviceInGroup() {
        EditTextPopup(this).apply {
            setTitle("查询设备组中的设备列表")
            setHint("请输入pk")
            setEditTextListener {
                if (it.isNullOrEmpty()) {
                    ToastUtils.showShort(mContext, "参数不能为空")
                    return@setEditTextListener
                }
                dismiss()
                startLoading()
                QuecDeviceGroupService.getDeviceList(
                    dGid, null, null, it, 1, 10
                ) { result ->
                    finishLoading()
                    handlerResult(result)
                    if (result.isSuccess) {
                        if (shareCode.isNullOrEmpty()) {
                            CommonDialog.showSimpleInfo(
                                this@DeviceListGroupActivity,
                                "设备组中的设备列表",
                                result.toString()
                            )
                        }
                    }
                }
            }
        }.showPopupWindow()
    }


    private fun deleteDeviceFromGroup() {
        EditDoubleTextPopup(mContext).apply {
            setTitle("移除设备组中的设备")
            setHint1("请输入pk")
            setHint2("请输入dk")
            setEditTextListener { content1, content2 ->
                if (content1.isNullOrEmpty() || content2.isNullOrEmpty()) {
                    ToastUtils.showShort(mContext, "参数不能为空")
                    return@setEditTextListener
                }
                dismiss()
                startLoading()
                val deviceList = ArrayList<QuecDeviceModel>()
                val quecDeviceModel = QuecDeviceModel(content1, content2)
                deviceList.add(quecDeviceModel)
                QuecDeviceGroupService.deleteDeviceFromGroup(
                    dGid, deviceList
                ) { result ->
                    AppVariable.setGroupChange()
                    finishLoading()
                    handlerResult(result)
                    if (result.isSuccess) {
                        finish()
                    }
                }
            }
        }.showPopupWindow()
    }

    private fun deleteGroup() {
        startLoading()
        QuecDeviceGroupService.deleteDeviceGroup(
            dGid
        ) { result ->
            finishLoading()
            handlerResult(result)
            AppVariable.setGroupChange()
            if (result.isSuccess) {
                finish()
            }
        }
    }

    fun startLoading() {
        showOrHideLoading(true)
    }

    fun finishLoading() {
        showOrHideLoading(false)
    }
}

