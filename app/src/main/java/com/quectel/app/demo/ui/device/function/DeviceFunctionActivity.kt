package com.quectel.app.demo.ui.device.function

import android.content.Intent
import android.os.Bundle
import com.quectel.app.blesdk.v2.api.QuecBleClientApi
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
import com.quectel.sdk.smart.config.api.api.IQuecDeviceNetConfigService
import com.quectel.sdk.smart.config.bean.NetConfigError
import com.quectel.sdk.smart.config.service.QuecDeviceNetConfigService

class DeviceFunctionActivity : QuecBaseDeviceActivity<ActivityDeviceFunctionBinding>() {
    private val netConfigListener = object : IQuecDeviceNetConfigService.QuecNetConfigListener {
        override fun didStartConfigDevice(device: QuecDeviceModel) {

        }

        override fun didUpdateConfigResult(
            device: QuecDeviceModel,
            result: Boolean,
            error: NetConfigError?
        ) {
            showOrHideLoading(false)
            if (result) {
                AppVariable.setDeviceChange()
                showMessage(getString(R.string.network_config_success))
                finish()
            } else {
                showMessage(getString(R.string.network_config_failed))
            }
        }

        override fun onNeedSsid(
            device: QuecDeviceModel,
            client: QuecBleClientApi
        ) {
            log("onNeedSsid: [${device.channelId}]")
        }
    }

    override fun getViewBinding(): ActivityDeviceFunctionBinding {
        return ActivityDeviceFunctionBinding.inflate(layoutInflater)
    }

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initData() {
        binding.title.text = device.deviceName

        QuecDeviceNetConfigService.addListener(netConfigListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        QuecDeviceNetConfigService.removeListener(netConfigListener)
    }

    override fun initTestItem() {
        addItem(getString(R.string.get_device_info)) {
            QuecDeviceService.getDeviceInfoByDeviceKey(device.deviceKey, device.productKey) {
                handlerResult(it)
                if (it.isSuccess) {
                    CommonDialog.showSimpleInfo(this, getString(R.string.device_info), it.data.toString())
                }
            }
        }

        addItem(getString(R.string.rename_device)) {
            EditTextPopup(this).apply {
                setTitle(getString(R.string.rename_device))
                setHint(getString(R.string.hint_device_name))
                setContent(device.deviceName)
                setEditTextListener {
                    dismiss()
                    editDeviceName(it)
                }
                showPopupWindow()
            }
        }

        addItem(getString(R.string.device_control)) {
            startActivity(Intent(this, DeviceControlActivity::class.java))
        }

        addItem(getString(R.string.device_upgrade)) {
            startActivity(Intent(this, DeviceOtaActivity::class.java))
        }

        addItem(getString(R.string.device_share)) {
            startActivity(Intent(this, DeviceShareActivity::class.java))
        }

        addItem(getString(R.string.device_unbind)) {
            CommonDialog(this).apply {
                setTitle(getString(R.string.confirm_unbind_device))
                setYesOnclickListener(getString(R.string.confirm)) {
                    dismiss()
                    unbindDevice()
                }
            }.show()
        }

        if (!device.isShared && device.capabilitiesBitmask != 4) {
            addItem(getString(R.string.device_net_config)) {
                EditDoubleTextPopup(mContext).apply {
                    setTitle(getString(R.string.hint_device_wifi))
                    setHint1(getString(R.string.router_name))
                    setHint2(getString(R.string.hint_wifi_password_enter))
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
            showMessage(getString(R.string.device_name_empty))
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
            showMessage(getString(R.string.hint_wifi_name))
            return
        }
        showOrHideLoading(true)
        QuecDeviceNetConfigService.startConfig(listOf(device), ssid, pwd)
    }
}