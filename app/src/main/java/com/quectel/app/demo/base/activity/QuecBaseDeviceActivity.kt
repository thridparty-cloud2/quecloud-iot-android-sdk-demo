package com.quectel.app.demo.base.activity

import android.content.Intent
import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.quectel.basic.common.entity.QuecDeviceModel

abstract class QuecBaseDeviceActivity<T : ViewBinding> : QuecBaseActivity<T>() {
    protected lateinit var device: QuecDeviceModel

    override fun onCreate(savedInstanceState: Bundle?) {
        val device = intent.getSerializableExtra(CODE_DEVICE) as? QuecDeviceModel

        if (device == null) {
            showMessage("设备信息为空")
            finish()
            return
        }

        this.device = device

        super.onCreate(savedInstanceState)
    }

    override fun startActivity(intent: Intent?, options: Bundle?) {
        intent?.putExtra(CODE_DEVICE, device)
        super.startActivity(intent, options)
    }

    companion object {
        const val CODE_DEVICE = "CODE_DEVICE"
    }
}