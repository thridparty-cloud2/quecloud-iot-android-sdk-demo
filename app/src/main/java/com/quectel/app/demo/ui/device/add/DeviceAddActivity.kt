package com.quectel.app.demo.ui.device.add

import android.content.Intent
import android.os.Bundle
import com.quectel.app.demo.R
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
        addItem(getString(R.string.sn_bind_device)) {
            addWithSn()
        }
        addItem(getString(R.string.receive_device_share)) {
            addWithShare()
        }
        addItem(getString(R.string.nearby_add_title)) {
            startActivity(Intent(this, DeviceNearbyAddActivity::class.java))
        }
    }

    private fun addWithSn() {
        EditDoubleTextPopup(this).apply {
            setTitle(getString(R.string.sn_bind_device))
            setHint1(getString(R.string.hint_product_key))
            setHint2(getString(R.string.hint_sn))
            setEditTextListener { content1, content2 ->
                QuecDeviceService.bindDeviceWithSerialNumber(content2, content1, null) {
                    if (it.isSuccess) {
                        AppVariable.setDeviceChange()
                        showMessage(getString(R.string.sn_bind_success))
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
            setTitle(getString(R.string.accept_device_share))
            setHint(getString(R.string.hint_share_code))
            setEditTextListener {
                dismiss()
                QuecDeviceShareService.acceptShareByShareUser(it, null) { ret ->
                    if (ret.isSuccess) {
                        AppVariable.setDeviceChange()
                        showMessage(getString(R.string.accept_share_success))
                        finish()
                    } else {
                        handlerError(ret)
                    }
                }
            }
        }.showPopupWindow()
    }
}