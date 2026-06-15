package com.quectel.app.demo.ui.family.group.function

import android.content.Intent
import android.os.Bundle
import com.quectel.app.demo.R
import com.quectel.app.demo.databinding.ActivityCommonListBinding
import com.quectel.app.demo.dialog.CommonDialog
import com.quectel.app.demo.dialog.EditTextPopup
import com.quectel.app.demo.dialog.SelectItemDialog
import com.quectel.app.demo.ui.family.BaseFamilyActivity
import com.quectel.app.demo.ui.family.group.control.FamilyGroupControlActivity
import com.quectel.app.device.deviceservice.QuecDeviceService
import com.quectel.app.smart_home_sdk.service.QuecSmartHomeService
import com.quectel.basic.common.entity.QuecDeviceModel
import com.quectel.basic.queclog.QLog
import com.quectel.sdk.group.bean.QuecGroupCreateDeviceBean
import com.quectel.sdk.group.bean.QuecGroupDeviceBean
import com.quectel.sdk.group.service.QuecGroupService

class FamilyGroupFunctionActivity : BaseFamilyActivity<ActivityCommonListBinding>() {
    private lateinit var device: QuecDeviceModel


    override fun getViewBinding(): ActivityCommonListBinding {
        return ActivityCommonListBinding.inflate(layoutInflater)
    }

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initData() {
        val bean = intent.getSerializableExtra(KEY_DEVICE) as? QuecDeviceModel
        if (bean == null) {
            finish()
            return
        }

        device = bean
        binding.title.text = device.deviceName
    }

    override fun initTestItem() {
        addItem(getString(R.string.group_control)) { groupControl() }
        addItem(getString(R.string.get_group_info)) { getGroupInfo() }
        addItem(getString(R.string.set_group_common)) { setCommonState() }
        addItem(getString(R.string.delete_group)) { deleteGroup() }

        if (device.isShared) {
            return
        }

        addItem(getString(R.string.rename_group)) { modifyGroupName() }
        addItem(getString(R.string.move_group_room)) { moveRoom() }
        addItem(getString(R.string.add_device_to_group)) { addDevice() }
        addItem(getString(R.string.delete_device_from_group)) { removeDevice() }
        addItem(getString(R.string.group_share)) { shareGroup() }
        addItem(getString(R.string.get_group_share_list)) { getShareInfo() }
        addItem(getString(R.string.remove_share)) { removeShare() }
    }

    private fun queryDeviceList(callback: (list: List<QuecGroupDeviceBean>) -> Unit) {
        QuecGroupService.getGroupInfo(device.gdid) {
            if (it.isSuccess) {
                val list = it.data.deviceList
                callback(list)
            } else {
                handlerError(it)
            }
        }
    }

    private fun addDevice() {
        queryDeviceList { list ->
            QuecGroupService.getAddableList(getCurrentFid(), device.gdid, list.map {
                QuecGroupCreateDeviceBean().apply {
                    productKey = it.productKey
                    deviceKey = it.deviceKey
                }
            }, 1, 100) {
                if (!it.isSuccess) {
                    handlerError(it)
                    return@getAddableList
                }

                if (it.data.list.isEmpty()) {
                    showMessage(getString(R.string.no_available_device))
                    return@getAddableList
                }

                SelectItemDialog(this).apply {
                    for (item in it.data.list) {
                        addItem(item.deviceName) {
                            val sumList = list.map { bean ->
                                QuecGroupCreateDeviceBean().apply {
                                    productKey = bean.productKey
                                    deviceKey = bean.deviceKey
                                }
                            }.toMutableList()

                            sumList.add(QuecGroupCreateDeviceBean().apply {
                                productKey = item.productKey
                                deviceKey = item.deviceKey
                            })

                            QuecGroupService.editGroupInfo(
                                device.gdid,
                                device.deviceName,
                                getCurrentFid(),
                                null,
                                device.isCommonUsed,
                                sumList
                            ) { ret ->
                                handlerResult(ret)
                            }
                        }
                    }
                }.show()
            }
        }
    }

    private fun removeDevice() {
        queryDeviceList { list ->
            if (list.size == 1) {
                showMessage(getString(R.string.group_has_only_one_device))
                return@queryDeviceList
            }

            SelectItemDialog(this).apply {
                for (item in list) {
                    addItem(item.deviceName) {
                        QuecGroupService.editGroupInfo(
                            device.gdid,
                            device.deviceName,
                            getCurrentFid(),
                            null,
                            device.isCommonUsed,
                            list.filter { it !== item }.map {
                                QuecGroupCreateDeviceBean().apply {
                                    productKey = it.productKey
                                    deviceKey = it.deviceKey
                                }
                            }) {
                            handlerResult(it)
                        }
                    }
                }
            }.show()
        }
    }

    private fun modifyGroupName() {
        EditTextPopup(this).apply {
            setTitle(getString(R.string.rename_group))
            setContent(device.deviceName)
                .setEditTextListener {
                    dismiss()
                    editGroupInfo(name = it)
                }
        }.showPopupWindow()
    }

    private fun groupControl() {
        startActivity(Intent(this, FamilyGroupControlActivity::class.java).apply {
            putExtra(KEY_DEVICE, device)
        })
    }

    private fun getGroupInfo() {
        QuecGroupService.getGroupInfo(device.gdid) {
            if (it.isSuccess) {
                val info = it.data
                CommonDialog.showSimpleInfo(
                    this,
                    getString(R.string.get_group_info),
                    getString(
                        R.string.group_info_detail,
                        info.gdid,
                        info.deviceName,
                        info.isCommonUsed.toString(),
                        info.roomName ?: "",
                        info.deviceList?.joinToString("\n") { item -> item.deviceName } ?: ""
                    )
                )
            } else {
                handlerError(it)
            }
        }
    }

    private fun setCommonState() {
        SelectItemDialog(this).apply {
            addItem(getString(R.string.in_common_list)) { setCommonState(true) }
            addItem(getString(R.string.not_in_common_list)) { setCommonState(false) }
        }.show()
    }

    private fun setCommonState(isCommon: Boolean) {
        QuecGroupService.batchAddCommon(mutableListOf(device.gdid), getCurrentFid(), isCommon) {
            handlerResult(it)
        }
    }

    private fun moveRoom() {
        QuecSmartHomeService.getFamilyRoomList(getCurrentFid(), 1, 100) {
            if (it.isSuccess) {
                if (it.data.list.isEmpty()) {
                    showMessage(getString(R.string.no_room_in_family))
                    return@getFamilyRoomList
                }

                SelectItemDialog(this).apply {
                    for (item in it.data.list) {
                        addItem(item.roomName ?: "null") {
                            editGroupInfo(frid = item.frid)
                        }
                    }
                }.show()
            } else {
                handlerError(it)
            }
        }
    }

    private fun deleteGroup() {
        CommonDialog(this).apply {
            setTitle(getString(R.string.confirm_delete_group))
            setYesOnclickListener(getString(R.string.confirm)) {
                dismiss()
                QuecDeviceService.batchUnbindDevice(false, listOf(device)) {
                    handlerResult(it)
                    if (it.isSuccess) {
                        finish()
                    }
                }
            }
        }.show()
    }

    private fun shareGroup() {
        QuecGroupService.getShareCode(
            device.gdid,
            System.currentTimeMillis() + 30 * 60 * 1000,
            true,
            System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000,
            1
        ) {
            if (it.isSuccess) {
                CommonDialog.showSimpleInfo(this, getString(R.string.group_share_code), it.data)
                QLog.i("share code", it.data)
            } else {
                handlerError(it)
            }
        }
    }

    private fun getShareInfo() {
        QuecGroupService.getSharedLists(device.gdid) {
            if (it.isSuccess) {
                if (it.data.isEmpty()) {
                    showMessage(getString(R.string.group_not_shared))
                    return@getSharedLists
                }

                CommonDialog.showSimpleInfo(
                    this,
                    getString(R.string.group_share_info),
                    it.data.joinToString("\n") { item ->
                        "${item.userInfo.nikeName} [${item.shareInfo.shareCode}]"
                    }
                )
            } else {
                handlerError(it)
            }
        }
    }

    private fun removeShare() {
        QuecGroupService.getSharedLists(device.gdid) {
            if (it.isSuccess) {
                if (it.data.isEmpty()) {
                    showMessage(getString(R.string.group_not_shared))
                    return@getSharedLists
                }
                SelectItemDialog(this).apply {
                    it.data.forEach { item ->
                        addItem(item.userInfo.nikeName) {
                            QuecGroupService.ownerUnShare(item.shareInfo.shareCode) { ret ->
                                handlerResult(ret)
                            }
                        }
                    }
                }.show()
            } else {
                handlerError(it)
            }
        }
    }

    private fun editGroupInfo(
        name: String? = null,
        isCommon: Boolean? = null,
        frid: String? = null,
        deviceList: List<QuecGroupDeviceBean>? = null
    ) {
        QuecGroupService.getGroupInfo(device.gdid) {
            if (it.isSuccess) {
                val newName = name ?: it.data.deviceName
                val newIsCommon = isCommon ?: it.data.isCommonUsed
                val newFrid = frid ?: it.data.frid
                val list = deviceList ?: it.data.deviceList

                QuecGroupService.editGroupInfo(
                    device.gdid, newName, getCurrentFid(), frid, newIsCommon, list.map { item ->
                        QuecGroupCreateDeviceBean().apply {
                            productKey = item.productKey
                            deviceKey = item.deviceKey
                        }
                    }
                ) { ret ->
                    handlerResult(ret)
                    if (ret.isSuccess) {
                        device.deviceName = newName
                        device.isCommonUsed = newIsCommon
                        device.frid = newFrid
                        binding.title.text = newName
                    }
                }
            } else {
                handlerError(it)
            }
        }
    }

    companion object {
        private const val TAG = "FamilyGroupFunctionActivity"
        const val KEY_DEVICE = "KEY_DEVICE"
    }
}