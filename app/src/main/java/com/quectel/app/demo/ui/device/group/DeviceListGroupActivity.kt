package com.quectel.app.demo.ui.device.group

import android.content.Intent
import android.os.Bundle
import com.quectel.app.demo.R
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
        addItem(getString(R.string.query_device_group_detail)) {
            queryGroup()
        }
        addItem(getString(R.string.modify_device_group)) {
            changeGroupDialog()
        }
        addItem(getString(R.string.add_device_to_group_title)) {
            addDeviceToGroup()
        }
        addItem(getString(R.string.query_device_list_in_group)) {
            queryDeviceInGroup()
        }
        addItem(getString(R.string.remove_device_in_group)) {
            deleteDeviceFromGroup()
        }
        addItem(getString(R.string.delete_device_group)) {
            deleteGroup()
        }
        addItem(getString(R.string.cancel)) {
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
                    getString(R.string.query_device_group_detail),
                    QuecGsonUtil.gsonString(result.data)
                )
            }
        }
    }

    private fun changeGroupDialog() {
        EditTextPopup(this).apply {
            setTitle(getString(R.string.add_device_group))
            setHint(getString(R.string.hint_group_name))
            setEditTextListener { name ->
                if (name.isNullOrEmpty()) {
                    ToastUtils.showShort(mContext, getString(R.string.param_cannot_be_empty))
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
            setTitle(getString(R.string.add_device_to_group_title))
            setHint1(getString(R.string.hint_pk))
            setHint2(getString(R.string.hint_dk))
            setEditTextListener { pk, dk ->
                if (pk.isNullOrEmpty() || dk.isNullOrEmpty()) {
                    ToastUtils.showShort(mContext, getString(R.string.param_cannot_be_empty))
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
            setTitle(getString(R.string.query_device_list_in_group))
            setHint(getString(R.string.hint_pk))
            setEditTextListener {
                if (it.isNullOrEmpty()) {
                    ToastUtils.showShort(mContext, getString(R.string.param_cannot_be_empty))
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
                                getString(R.string.query_device_list_in_group),
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
            setTitle(getString(R.string.remove_device_in_group))
            setHint1(getString(R.string.hint_pk))
            setHint2(getString(R.string.hint_dk))
            setEditTextListener { content1, content2 ->
                if (content1.isNullOrEmpty() || content2.isNullOrEmpty()) {
                    ToastUtils.showShort(mContext, getString(R.string.param_cannot_be_empty))
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
