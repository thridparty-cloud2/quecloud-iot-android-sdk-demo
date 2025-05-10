package com.quectel.app.demo.ui.family.room.function

import android.content.Intent
import android.os.Bundle
import com.quectel.app.demo.databinding.ActivityCommonListBinding
import com.quectel.app.demo.dialog.EditTextPopup
import com.quectel.app.demo.dialog.SelectItemDialog
import com.quectel.app.demo.ui.family.BaseFamilyActivity
import com.quectel.app.demo.ui.family.room.device.FamilyDeviceListActivity
import com.quectel.app.smart_home_sdk.bean.QuecSortDeviceEnterModel
import com.quectel.app.smart_home_sdk.service.QuecSmartHomeService

class FamilyRoomFunctionActivity : BaseFamilyActivity<ActivityCommonListBinding>() {
    private lateinit var frid: String

    override fun getViewBinding(): ActivityCommonListBinding {
        return ActivityCommonListBinding.inflate(layoutInflater)
    }

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initData() {
        frid = intent.getStringExtra(CODE_FRID) ?: ""
        binding.title.text = intent.getStringExtra(CODE_NAME)
    }

    override fun initTestItem() {
        super.initTestItem()

        addItem("修改房间名") { modifyName() }

        addItem("修改房间序号") { modifySort() }

        addItem("查看房间下设备列表") { queryDeviceList() }

        addItem("删除房间") { deleteRoom() }
    }

    private fun modifyName() {
        EditTextPopup(this).apply {
            setTitle("修改房间名")
            setHint("请输入房间名")
            setContent(binding.title.text.toString())
            setEditTextListener {
                dismiss()
                QuecSmartHomeService.setFamilyRoom(frid, it) { ret ->
                    handlerResult(ret)
                    if (ret.isSuccess) {
                        binding.title.text = it
                    }
                }
            }
        }.showPopupWindow()
    }

    private fun modifySort() {
        SelectItemDialog(this).apply {
            addItem("最前") { modifySort(true) }

            addItem("最后") { modifySort(false) }
        }.show()
    }

    private fun queryDeviceList() {
        startActivity(Intent(this, FamilyDeviceListActivity::class.java).apply {
            putExtra(FamilyDeviceListActivity.CODE_MODE, FamilyDeviceListActivity.Mode.ROOM)
            putExtra(FamilyDeviceListActivity.CODE_FRID, frid)
            putExtra(FamilyDeviceListActivity.CODE_NAME, binding.title.text.toString())
        })
    }

    private fun deleteRoom() {
        QuecSmartHomeService.deleteFamilyRooms(listOf(frid)) { ret ->
            handlerResult(ret)
            if (ret.isSuccess) {
                finish()
            }
        }
    }

    private fun modifySort(isFirst: Boolean) {
        QuecSmartHomeService.getFamilyRoomList(getCurrentFid(), 1, 20) {
            if (!it.isSuccess) {
                handlerError(it)
                return@getFamilyRoomList
            }

            val list = it.data.list.toMutableList()
            val target = list.find { item -> item.frid == frid }
            list.remove(target)

            var index = 0
            val newList = mutableListOf<QuecSortDeviceEnterModel>()
            if (isFirst) {
                newList.add(QuecSortDeviceEnterModel(target?.frid, index))
                index += 1
            }
            for (item in list) {
                newList.add(QuecSortDeviceEnterModel(item.frid, index))
                index += 1
            }
            if (!isFirst) {
                newList.add(QuecSortDeviceEnterModel(target?.frid, index))
            }

            QuecSmartHomeService.setFamilyRoomSort(newList) { ret -> handlerResult(ret) }
        }
    }

    companion object {
        const val CODE_FRID = "CODE_FRID"
        const val CODE_NAME = "CODE_NAME"
    }
}