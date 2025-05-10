package com.quectel.app.demo.ui.family.room.device

import android.annotation.SuppressLint
import android.os.Bundle
import com.quectel.app.demo.base.CommonListAdapter
import com.quectel.app.demo.databinding.ActivityFamilyDeviceListBinding
import com.quectel.app.demo.dialog.EditTextPopup
import com.quectel.app.demo.dialog.SelectItemDialog
import com.quectel.app.demo.ui.family.BaseFamilyActivity
import com.quectel.app.smart_home_sdk.bean.QuecFamilyDeviceListParamsModel
import com.quectel.app.smart_home_sdk.bean.QuecSetDeviceInfoModel
import com.quectel.app.smart_home_sdk.service.QuecSmartHomeService
import com.quectel.basic.common.entity.QuecCallback
import com.quectel.basic.common.entity.QuecDeviceModel
import com.quectel.basic.common.entity.QuecPageResponse

class FamilyDeviceListActivity : BaseFamilyActivity<ActivityFamilyDeviceListBinding>() {
    private val list = mutableListOf<QuecDeviceModel>()
    private lateinit var adapter: CommonListAdapter
    private lateinit var mode: Mode
    private var frid: String? = null


    override fun getViewBinding(): ActivityFamilyDeviceListBinding {
        return ActivityFamilyDeviceListBinding.inflate(layoutInflater)
    }

    override fun initView(savedInstanceState: Bundle?) {
        adapter = CommonListAdapter.init(binding.rvList) { clickItem(list[it]) }
    }

    override fun initData() {
        frid = intent.getStringExtra(CODE_FRID)

        val name = intent.getStringExtra(CODE_NAME)

        mode = intent.getSerializableExtra(CODE_MODE) as? Mode ?: Mode.COMMON

        binding.title.text = when (mode) {
            Mode.COMMON -> "常用设备"
            Mode.ALL -> "家庭下所有设备"
            Mode.ROOM -> "房间[${name}]中设备"
        }

        getDeviceList()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getDeviceList() {
        val callback = QuecCallback<QuecPageResponse<QuecDeviceModel>> {
            if (it.isSuccess) {
                list.clear()
                list.addAll(it.data.list)

                adapter.list.clear()
                adapter.list.addAll(list.map { item ->
                    CommonListAdapter.Item(
                        item.deviceName,
                        item.productKey + " - " + item.deviceKey,
                        null
                    )
                })
                adapter.notifyDataSetChanged()
            } else {
                handlerError(it)
            }
        }

        when (mode) {
            Mode.COMMON -> QuecSmartHomeService.getCommonUsedDeviceList(
                getCurrentFid(),
                1,
                20,
                false,
                callback
            )

            Mode.ALL -> QuecSmartHomeService.getFamilyDeviceList(
                QuecFamilyDeviceListParamsModel(
                    fid = getCurrentFid(),
                    isAddOwnerDevice = true,
                    page = 1,
                    pageSize = 20,
                    isGroupDeviceShow = false,
                ), callback
            )

            Mode.ROOM -> QuecSmartHomeService.getFamilyRoomDeviceList(
                frid ?: "",
                1,
                20,
                false,
                callback
            )
        }
    }

    private fun clickItem(item: QuecDeviceModel) {
        SelectItemDialog(this).apply {
            if (item.isCommonUsed) {
                addItem("移出常用") { modifyDevice(item, isCommon = false) }
            } else {
                addItem("移入常用") { modifyDevice(item, isCommon = true) }
            }
            addItem("移动至其他房间") { moveDeviceRoom(item) }
            addItem("修改设备名") { modifyDeviceName(item) }
        }.show()
    }

    private fun moveDeviceRoom(deviceModel: QuecDeviceModel) {
        QuecSmartHomeService.getFamilyRoomList(getCurrentFid(), 1, 10) {
            if (it.isSuccess) {
                SelectItemDialog(this).apply {
                    it.data.list.forEach { item ->
                        addItem(item.roomName ?: "") {
                            modifyDevice(deviceModel, newFrid = item.frid)
                        }
                    }
                }.show()
            } else {
                handlerError(it)
            }
        }
    }

    private fun modifyDeviceName(item: QuecDeviceModel) {
        EditTextPopup(this).apply {
            setTitle("修改设备名")
            setContent(item.deviceName)
            setEditTextListener {
                dismiss()
                modifyDevice(item, deviceName = it)
            }
        }.showPopupWindow()
    }

    private fun modifyDevice(
        item: QuecDeviceModel,
        isCommon: Boolean = item.isCommonUsed,
        newFrid: String? = null,
        deviceName: String = item.deviceName
    ) {
        QuecSmartHomeService.setDeviceInfo(
            listOf(
                QuecSetDeviceInfoModel(
                    getCurrentFid(),
                    item.deviceKey,
                    item.productKey,
                    deviceName,
                    isCommon,
                    item.deviceType,
                    frid,
                    newFrid,
                    null
                )
            )
        ) {
            handlerResult(it)
            if (it.isSuccess) {
                getDeviceList()
            }
        }
    }

    companion object {
        const val TAG = "FamilyDeviceListActivity"
        const val CODE_FRID = "CODE_FRID"
        const val CODE_NAME = "CODE_NAME"
        const val CODE_MODE = "CODE_MODE"
    }

    enum class Mode {
        /**
         * 常用设备列表
         */
        COMMON,

        /**
         * 家庭下所有设备
         */
        ALL,

        /**
         * 房间下所有设备
         */
        ROOM,
    }
}