package com.quectel.app.demo.ui.device.function

import android.content.Intent
import android.os.Bundle
import com.quectel.app.demo.base.activity.QuecBaseDeviceActivity
import com.quectel.app.demo.common.AppVariable
import com.quectel.app.demo.databinding.ActivityDeviceFunctionBinding
import com.quectel.app.demo.dialog.CommonDialog
import com.quectel.app.demo.dialog.EditTextPopup
import com.quectel.app.demo.ui.device.ota.DeviceOtaActivity
import com.quectel.app.demo.ui.device.share.DeviceShareActivity
import com.quectel.app.device.deviceservice.QuecDeviceService

class DeviceFunctionActivity : QuecBaseDeviceActivity<ActivityDeviceFunctionBinding>() {
    override fun getViewBinding(): ActivityDeviceFunctionBinding {
        return ActivityDeviceFunctionBinding.inflate(layoutInflater)
    }

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initData() {
        binding.title.text = device.deviceName
    }

    override fun initTestItem() {
        addItem("获取设备信息") {
            QuecDeviceService.getDeviceInfoByDeviceKey(device.deviceKey, device.productKey) {
                handlerResult(it)
                if (it.isSuccess) {
                    CommonDialog.showSimpleInfo(this, "设备信息", it.data.toString())
                }
            }
        }

        addItem("修改设备名") {
            EditTextPopup(this).apply {
                setTitle("修改设备名")
                setHint("请输入设备名")
                setContent(device.deviceName)
                setEditTextListener {
                    dismiss()
                    editDeviceName(it)
                }
                showPopupWindow()
            }
        }

        addItem("设备控制") {

        }

        addItem("设备升级") {
            startActivity(Intent(this, DeviceOtaActivity::class.java))
        }


        addItem("设备分享") {
            startActivity(Intent(this, DeviceShareActivity::class.java))
        }
    }

    private fun editDeviceName(name: String?) {
        if (name.isNullOrEmpty()) {
            showMessage("设备名不能为空")
            return
        }

        QuecDeviceService.updateDeviceName(name, device.productKey, device.deviceKey) {
            handlerResult(it)
            if (it.isSuccess) {
                device.deviceName = name
                binding.title.text = name
                AppVariable.isDeviceInfoChange = true
            }
        }
    }
}