package com.quectel.app.demo.ui.device.function

import android.os.Bundle
import com.quectel.app.demo.base.activity.QuecBaseDeviceActivity
import com.quectel.app.demo.databinding.ActivityDeviceFunctionBinding

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
        addItem("设备控制") {

        }

        addItem("设备升级") {

        }

        addItem("修改设备名") {

        }
    }

}