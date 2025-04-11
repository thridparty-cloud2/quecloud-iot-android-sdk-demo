package com.quectel.app.demo.ui.device.ota

import android.os.Bundle
import android.widget.ScrollView
import com.quectel.app.demo.R
import com.quectel.app.demo.base.activity.QuecBaseDeviceActivity
import com.quectel.app.demo.databinding.ActivityDeviceOtaBinding
import com.quectel.basic.queclog.QLog
import com.quectel.sdk.ota.upgrade.entry.QuecOTAStateModel
import com.quectel.sdk.ota.upgrade.entry.QuecOtaInfo
import com.quectel.sdk.ota.upgrade.v2.QuecOnOtaStateChangeListener
import com.quectel.sdk.ota.upgrade.v2.QuecOtaManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class DeviceOtaActivity : QuecBaseDeviceActivity<ActivityDeviceOtaBinding>() {
    private val infoBuild = StringBuilder()
    private val dataFormat = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)

    private var otaInfo: QuecOtaInfo? = null
    private val stateListener = object : QuecOnOtaStateChangeListener {
        override fun onCall(state: QuecOTAStateModel) {
            showInfo("onData change: $state")
        }
    }

    override fun onResume() {
        super.onResume()
        QuecOtaManager.addStateListener(stateListener)
    }

    override fun onPause() {
        super.onPause()
        QuecOtaManager.removeStateListener(stateListener)
    }

    override fun getViewBinding(): ActivityDeviceOtaBinding {
        return ActivityDeviceOtaBinding.inflate(layoutInflater)
    }

    override fun initView(savedInstanceState: Bundle?) {
        binding.apply {
            tvClear.setOnClickListener {
                infoBuild.clear()
                tvInfo.text = ""
                showMessage("操作记录已清空")
            }

            binding.ivBack.setOnClickListener { finish() }
        }
    }

    override fun initData() {

    }

    override fun initTestItem() {
        addItem("查询设备版本") {
            showInfo("查询设备版本")
            QuecOtaManager.queryCurrentVersion(device.productKey, device.deviceKey) {
                showInfo("查询设备版本结果: $it")
            }
        }

        addItem("查询升级计划") {
            showInfo("查询升级计划")
            QuecOtaManager.checkVersion(device.productKey, device.deviceKey) {
                if (it.isSuccess) {
                    otaInfo = it.data
                }
                showInfo("查询升级计划结果: $it")
            }
        }

        addItem("开始升级") {
            showInfo("开始升级")
            val info = otaInfo
            if (info != null) {
                QuecOtaManager.startOta(info)
            } else {
                showInfo("请先查询升级计划")
            }
        }

        addItem("停止升级") {
            showInfo("停止升级")
            val info = otaInfo
            if (info != null) {
                QuecOtaManager.stopOta(info)
            } else {
                showInfo("请先查询升级计划")
            }
        }
    }

    private fun showInfo(info: String) {
        QLog.i(TAG, info)
        infoBuild
            .append(dataFormat.format(Date()))
            .append(" ")
            .append(info)
            .append("\n")
        binding.apply {
            tvInfo.text = infoBuild.toString()
            tvInfo.post {
                findViewById<ScrollView>(R.id.sv_log).smoothScrollTo(0, tvInfo.height)
            }
        }
    }

    companion object {
        private const val TAG = "DeviceOtaActivity"
    }
}
