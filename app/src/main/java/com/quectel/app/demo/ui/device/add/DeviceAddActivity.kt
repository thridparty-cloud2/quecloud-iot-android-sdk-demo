package com.quectel.app.demo.ui.device.add

import android.content.Intent
import android.os.Bundle
import com.quectel.app.demo.base.activity.QuecBaseActivity
import com.quectel.app.demo.common.AppVariable
import com.quectel.app.demo.databinding.ActivityDeviceAddBinding
import com.quectel.app.demo.dialog.EditDoubleTextPopup
import com.quectel.app.demo.dialog.EditTextPopup
import com.quectel.app.device.deviceservice.QuecDeviceService
import com.quectel.app.device.deviceservice.QuecDeviceShareService

class DeviceAddActivity : QuecBaseActivity<ActivityDeviceAddBinding>() {
    override fun getViewBinding(): ActivityDeviceAddBinding {
        return ActivityDeviceAddBinding.inflate(layoutInflater)
    }

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initData() {

    }

    override fun initTestItem() {
        addItem("SN绑定设备") {
            addWithSn()
        }

        addItem("接收设备分享") {
            addWithShare()
        }

        addItem("近场搜索添加设备") {
            startActivity(Intent(this, DeviceNearbyAddActivity::class.java))
        }
    }

    private fun addWithSn() {
        EditDoubleTextPopup(this).apply {
            setTitle("SN绑定设备")
            setHint1("请输入productKey")
            setHint2("请输入Sn")
            setEditTextListener { content1, content2 ->
                //可输入自定义的设备名
                QuecDeviceService.bindDeviceWithSerialNumber(content2, content1, null) {
                    if (it.isSuccess) {
                        AppVariable.setDeviceChange()
                        showMessage("SN绑定设备成功")
                        finish()
                    } else {
                        handlerError(it)
                    }
                }
            }
        }.showPopupWindow()
    }

    private fun addWithShare() {
        EditTextPopup(this).apply {
            setTitle("接受设备分享")
            setHint("请输入shareCode")
            setEditTextListener {
                dismiss()
                //可输入自定义的设备名
                QuecDeviceShareService.acceptShareByShareUser(it, null) { ret ->
                    if (ret.isSuccess) {
                        AppVariable.setDeviceChange()
                        showMessage("接受分享成功")
                        finish()
                    } else {
                        handlerError(ret)
                    }
                }
            }
        }.showPopupWindow()
    }
}