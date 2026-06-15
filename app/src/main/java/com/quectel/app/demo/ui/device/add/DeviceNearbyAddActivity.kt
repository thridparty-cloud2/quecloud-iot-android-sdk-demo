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

        override fun onNeedSsid(deviceBean: QuecPairDeviceBean) {
            log("onNeedSsid: [${deviceBean.bleDevice.getChannelId()}]")
            val title =
                if (deviceBean.activeBindingMode == 2) {
                    getString(R.string.hint_device_wifi_optional)
                } else {
                    getString(R.string.hint_device_wifi_required)
                }
            EditDoubleTextPopup(mContext).apply {
                setTitle(title)
                setHint1(getString(R.string.router_name))
                setHint2(getString(R.string.hint_wifi_password_enter))
                setEditTextListener { content1, content2 ->
                    dismiss()
                    QuecDevicePairingService.setSsidInfo(content1, content2)
                }
            }.showPopupWindow()
        }

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
            //todo sdk callback should be executed on the main thread
            QuecThreadUtil.RunMainThread {
                if (result) {
                    showMessage(getString(R.string.add_success))
                    AppVariable.setDeviceChange()
                    backMain()
                } else {
                    showMessage(getString(R.string.add_failed, errorCode.toString()))
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
            startAdd(bean, null, null)
        } else {
            EditDoubleTextPopup(mContext).apply {
                setTitle(getString(R.string.hint_device_wifi))
                setHint1(getString(R.string.router_name))
                setHint2(getString(R.string.hint_wifi_password_enter))
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
                showMessage(getString(R.string.hint_wifi_name))
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
        // If system Bluetooth is not enabled, request to enable it
        if (!enabled) {
            val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                showMessage(getString(R.string.please_grant_bluetooth_permission))
                return
            }
            startActivityForResult(intent, 1)
        }
    }
}