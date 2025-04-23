package com.quectel.app.demo.ui.device.add

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.quectel.app.demo.R
import com.quectel.app.demo.base.activity.QuecBaseActivity
import com.quectel.app.demo.common.AppVariable
import com.quectel.app.demo.databinding.ActivityNearbyAddBinding
import com.quectel.app.demo.dialog.EditDoubleTextPopup
import com.quectel.app.demo.utils.PermissionUtil
import com.quectel.basic.common.utils.QuecThreadUtil
import com.quectel.basic.queclog.QLog
import com.quectel.sdk.smart.config.api.bean.QuecPairDeviceBean
import com.quectel.sdk.smart.config.api.bean.QuecPairErrorCode
import com.quectel.sdk.smart.config.api.callback.QuecPairingListener
import com.quectel.sdk.smart.config.manager.QuecDevicePairingService

class DeviceNearbyAddActivity : QuecBaseActivity<ActivityNearbyAddBinding>() {
    private var scanTimeout: Runnable? = null
    private val mList = mutableListOf<QuecPairDeviceBean>()
    private lateinit var adapter: DeviceNearbyAdapter

    private val listener = object : QuecPairingListener {
        override fun onScanDevice(deviceBean: QuecPairDeviceBean) {
            if (mList.find {
                    it.bleDevice.productKey == deviceBean.bleDevice.productKey
                            && it.bleDevice.deviceKey == deviceBean.bleDevice.deviceKey
                } != null) {
                return
            }

            mList.add(deviceBean)
            adapter.notifyItemInserted(mList.size - 1)
        }

        override fun onUpdatePairingResult(
            deviceBean: QuecPairDeviceBean,
            result: Boolean,
            errorCode: QuecPairErrorCode
        ) {
            //todo sdk回调应该在主线程执行
            QuecThreadUtil.RunMainThread {
                if (result) {
                    showMessage("添加成功")
                    AppVariable.setDeviceChange()
                    backMain()
                } else {
                    showMessage("添加失败: $errorCode")
                }
            }
        }

        override fun onUpdatePairingStatus(deviceBean: QuecPairDeviceBean, progress: Float) {
            QLog.i("onUpdatePairingStatus: $progress")
        }
    }


    override fun getViewBinding(): ActivityNearbyAddBinding {
        return ActivityNearbyAddBinding.inflate(layoutInflater)
    }

    override fun initView(savedInstanceState: Bundle?) {
        binding.apply {
            btStart.setOnClickListener { startScan() }
            btStop.setOnClickListener { stopScan() }

            adapter = DeviceNearbyAdapter(mList) {
                onItemClick(it)
            }
            rvList.layoutManager = LinearLayoutManager(mContext)
            rvList.adapter = adapter
        }
    }

    override fun initData() {
        initPermission()
        QuecDevicePairingService.addPairingListener(listener)

        startScan()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopScan()
        QuecDevicePairingService.removePairingListener(listener)
        QuecDevicePairingService.cancelAllDevicePairing()
    }

    private fun startScan() {
        QuecDevicePairingService.scan(null, null)
        scanTimeout?.let { QuecThreadUtil.CancelWait(it) }
        scanTimeout = QuecThreadUtil.Wait(20000) {
            stopScan()
        }

        binding.tvState.text = getString(R.string.searching)
    }

    private fun onItemClick(bean: QuecPairDeviceBean) {
        if (bean.activeBluetooth) {
            //蓝牙优先激活, 直接绑定设备
            startAdd(bean, null, null)
        } else {
            // wifi激活, 需输入ssid和pwd
            EditDoubleTextPopup(mContext).apply {
                setTitle("请输入设备WiFi信息")
                setHint1("路由器名称")
                setHint2("密码密码")
                setEditTextListener { content1, content2 ->
                    dismiss()
                    startAdd(bean, content1, content2)
                }
            }.showPopupWindow()
        }
    }

    private fun startAdd(bean: QuecPairDeviceBean, ssid: String?, pwd: String?) {
        if (!bean.activeBluetooth) {
            if (ssid.isNullOrEmpty()) {
                showMessage("请输入WiFi名称")
                return
            }
        }

        showOrHideLoading(true)
        stopScan()
        QuecDevicePairingService.startPairingWithDevices(
            mutableListOf(bean),
            ssid,
            pwd
        )
    }

    private fun stopScan() {
        binding.tvState.text = getString(R.string.not_search)
        QuecDevicePairingService.stopScan()
        scanTimeout?.let {
            QuecThreadUtil.CancelWait(it)
            scanTimeout = null
        }
    }

    private fun initPermission() {
        if (!PermissionUtil.hasLocation(this)) {
            val intent = Intent()
            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        val bltAdapter = BluetoothAdapter.getDefaultAdapter()
        val enabled = bltAdapter.isEnabled
        //如果没有打开系统蓝牙，请求打开系统蓝牙
        if (!enabled) {
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                showMessage("请先授予应用的 蓝牙权限")
                return
            }
            startActivityForResult(intent, 1)
        }
    }
}