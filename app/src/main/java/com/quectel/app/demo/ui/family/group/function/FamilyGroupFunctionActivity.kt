package com.quectel.app.demo.ui.family.group.function

import android.os.Bundle
import com.quectel.app.demo.databinding.ActivityCommonListBinding
import com.quectel.app.demo.dialog.CommonDialog
import com.quectel.app.demo.dialog.EditTextPopup
import com.quectel.app.demo.dialog.SelectItemDialog
import com.quectel.app.demo.ui.family.BaseFamilyActivity
import com.quectel.app.device.deviceservice.QuecDeviceService
import com.quectel.app.smart_home_sdk.service.QuecSmartHomeService
import com.quectel.basic.common.entity.QuecDeviceModel
import com.quectel.sdk.group.bean.QuecGroupCreateBean
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
        addItem("群组控制") {

        }

        addItem("查询群组下设备列表") { queryDeviceList() }

        addItem("添加设备") { addDevice() }

        addItem("删除设备") { removeDevice() }

        addItem("修改群组名称") { modifyGroupName() }

        addItem("获取群组信息") { getGroupInfo() }

        addItem("设置群组是否在常用列表中") { setCommonState() }

        addItem("群组移动房间") { moveRoom() }

        addItem("删除群组") { deleteGroup() }
    }

    private fun queryDeviceList() {
        queryDeviceList {
            if (it.isEmpty()) {
                showMessage("群组下没有设备")
            } else {
                CommonDialog.showSimpleInfo(
                    this,
                    "设备列表",
                    it.joinToString("\n") { item -> item.deviceName })
            }
        }
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
            setTitle("修改群组名称")
            setContent(device.deviceName)
                .setEditTextListener {
                    dismiss()

                    editGroupInfo(name = it)
                }
        }.showPopupWindow()
    }

    private fun getGroupInfo() {
        QuecGroupService.getGroupInfo(device.gdid) {
            if (it.isSuccess) {
                val info = it.data
                CommonDialog.showSimpleInfo(
                    this,
                    "群组信息",
                    "群组ID: ${info.gdid}\n" +
                            "群组名称: ${info.deviceName}\n" +
                            "是否在常用列表中: ${info.isCommonUsed}\n" +
                            "在房间: [${info.roomName}]中\n" +
                            "\n群组中设备列表:\n ${info.deviceList.joinToString("\n") { item -> item.deviceName }}\n"

                )
            } else {
                handlerError(it)
            }
        }
    }

    private fun setCommonState() {
        SelectItemDialog(this).apply {
            addItem("在常用列表中") { editGroupInfo(isCommon = true) }

            addItem("不在常用列表中") { editGroupInfo(isCommon = false) }
        }.show()
    }

    private fun moveRoom() {
        QuecSmartHomeService.getFamilyRoomList(getCurrentFid(), 1, 100) {
            if (it.isSuccess) {
                if (it.data.list.isEmpty()) {
                    showMessage("当前家庭没有房间")
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
            setTitle("确认删除群组?")
            setYesOnclickListener("确认") {
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