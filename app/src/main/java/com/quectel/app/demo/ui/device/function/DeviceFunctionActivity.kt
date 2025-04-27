package com.quectel.app.demo.ui.device.function

import android.content.Intent
import android.os.Bundle
import com.quectel.app.demo.R
import com.quectel.app.demo.base.activity.QuecBaseDeviceActivity
import com.quectel.app.demo.common.AppVariable
import com.quectel.app.demo.databinding.ActivityDeviceFunctionBinding
import com.quectel.app.demo.dialog.CommonDialog
import com.quectel.app.demo.dialog.EditDoubleTextPopup
import com.quectel.app.demo.dialog.EditTextPopup
import com.quectel.app.demo.ui.device.control.DeviceControlActivity
import com.quectel.app.demo.ui.device.ota.DeviceOtaActivity
import com.quectel.app.demo.ui.device.share.DeviceShareActivity
import com.quectel.app.device.deviceservice.QuecDeviceService
import com.quectel.app.device.deviceservice.QuecDeviceShareService
import com.quectel.basic.common.entity.QuecCallback
import com.quectel.basic.common.entity.QuecDeviceModel
import com.quectel.sdk.smart.config.api.api.IQuecDeviceVerifyService
import com.quectel.sdk.smart.config.service.QuecDeviceVerifyService

class DeviceFunctionActivity : QuecBaseDeviceActivity<ActivityDeviceFunctionBinding>() {
    private val verifyListener = object : IQuecDeviceVerifyService.QuecVerifyDelegate {
        override fun didStartVerifyingDevice(device: QuecDeviceModel) {

        }

        override fun didUpdateVerifyResult(
            device: QuecDeviceModel,
            result: Boolean,
            error: IQuecDeviceVerifyService.ErrorCode?
        ) {
            showOrHideLoading(false)
            if (result) {
                AppVariable.setDeviceChange()
                showMessage("激活成功")
                finish()
            } else {
                showMessage("激活失败")
            }
        }

    }

    override fun getViewBinding(): ActivityDeviceFunctionBinding {
        return ActivityDeviceFunctionBinding.inflate(layoutInflater)
    }

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initData() {
        binding.title.text = device.deviceName

        QuecDeviceVerifyService.addVerifyListener(verifyListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        QuecDeviceVerifyService.removeVerifyListener(verifyListener)
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
            startActivity(Intent(this, DeviceControlActivity::class.java))
        }

        addItem("设备升级") {
            startActivity(Intent(this, DeviceOtaActivity::class.java))
        }


        addItem("设备分享") {
            startActivity(Intent(this, DeviceShareActivity::class.java))
        }

        addItem("设备解绑") {
            CommonDialog(this).apply {
                setTitle("确认解绑设备?")
                setYesOnclickListener(getString(R.string.confirm)) {
                    dismiss()
                    unbindDevice()
                }
            }.show()
        }

        if (!device.isShared && device.isBleBindState) {
            addItem("设备激活") {
                EditDoubleTextPopup(mContext).apply {
                    setTitle("请输入设备WiFi信息")
                    setHint1("路由器名称")
                    setHint2("密码密码")
                    setEditTextListener { content1, content2 ->
                        dismiss()
                        activateDevice(content1, content2)
                    }
                }.showPopupWindow()
            }
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
                AppVariable.setDeviceChange()
            }
        }
    }

    private fun unbindDevice() {
        val callback = QuecCallback<Unit> {
            handlerResult(it)
            if (it.isSuccess) {
                AppVariable.setDeviceChange()
                finish()
            }
        }
        if (device.isShared) {
            QuecDeviceShareService.unShareDeviceByShareUser(device.shareCode, callback)
        } else {
            QuecDeviceService.unbindDevice(
                device.deviceKey,
                device.productKey,
                false,
                null,
                null,
                callback
            )
        }
    }

    private fun activateDevice(ssid: String, pwd: String) {
        if (ssid.isEmpty()) {
            showMessage("请输入WiFi名称")
            return
        }
        showOrHideLoading(true)
        QuecDeviceVerifyService.startVerifyByDevices(listOf(device), ssid, pwd)
    }
}