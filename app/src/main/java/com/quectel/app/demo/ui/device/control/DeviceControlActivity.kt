package com.quectel.app.demo.ui.device.control

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.quectel.app.demo.R
import com.quectel.app.demo.base.activity.QuecBaseDeviceActivity
import com.quectel.app.demo.databinding.ActivityDeviceControlExBinding
import com.quectel.app.demo.dialog.EditTextPopup
import com.quectel.app.demo.dialog.SelectItemDialog
import com.quectel.app.device.bean.BooleanSpecs
import com.quectel.app.device.bean.ModelBasic
import com.quectel.app.device.bean.NumSpecs
import com.quectel.app.device.bean.QuecProductTSLPropertyModel
import com.quectel.app.device.bean.TSLEvent
import com.quectel.app.device.bean.TSLService
import com.quectel.app.device.bean.TextSpecs
import com.quectel.app.device.callback.IDeviceTSLCallBack
import com.quectel.app.device.callback.IDeviceTSLModelCallback
import com.quectel.app.device.deviceservice.QuecDeviceService
import com.quectel.basic.common.entity.QuecDeviceModel
import com.quectel.basic.common.utils.QuecThreadUtil
import com.quectel.basic.queclog.QLog
import com.quectel.sdk.iot.channel.kit.chanel.bean.QuecDeviceStatus.Type
import com.quectel.sdk.iot.channel.kit.constaint.QuecIotChannelType
import com.quectel.sdk.iot.channel.kit.constaint.QuecIotDataSendMode
import com.quectel.sdk.iot.channel.kit.model.QuecIotDataPointsModel
import com.quectel.sdk.iot.channel.kit.model.QuecIotDataPointsModel.DataModel.QuecIotDataPointDataType
import com.quectel.sdk.iot.channel.kit.v2.QuecDeviceClient
import com.quectel.sdk.iot.channel.kit.v2.QuecDeviceClientApi
import java.util.Calendar

class DeviceControlActivity : QuecBaseDeviceActivity<ActivityDeviceControlExBinding>() {
    private lateinit var deviceClient: QuecDeviceClientApi
    private var isConnecting = 0
    private var itemList = ArrayList<QuecProductTSLPropertyModel<*>>()
    private lateinit var adapter: DeviceControlAdapter
    private var sendMode = QuecIotDataSendMode.QuecIotDataSendModeAuto

    private val dataTypeMap = mapOf(
        QuecIotDataPointDataType.BOOL to QuecIotDataPointDataType.BOOL_NUM,
        QuecIotDataPointDataType.DATE to QuecIotDataPointDataType.DATE_NUM,
        QuecIotDataPointDataType.DOUBLE to QuecIotDataPointDataType.DOUBLE_NUM,
        QuecIotDataPointDataType.FLOAT to QuecIotDataPointDataType.FLOAT_NUM,
        QuecIotDataPointDataType.ARRAY to QuecIotDataPointDataType.ARRAY_NUM,
        QuecIotDataPointDataType.ENUM to QuecIotDataPointDataType.ENUM_NUM,
        QuecIotDataPointDataType.INT to QuecIotDataPointDataType.INT_NUM,
        QuecIotDataPointDataType.TEXT to QuecIotDataPointDataType.TEXT_NUM,
        QuecIotDataPointDataType.STRUCT to QuecIotDataPointDataType.STRUCT_NUM
    )

    private val listener = object : QuecDeviceClientApi.Listener {
        override fun connectingStateUpdate(device: QuecDeviceModel, connectingState: Int) {
            isConnecting = connectingState
            QuecThreadUtil.RunMainThread {
                showOnlineStatus()
            }
        }

        override fun deviceInfoUpdate(device: QuecDeviceModel) {

        }

        override fun deviceRemoved(device: QuecDeviceModel) {

        }

        override fun dpsUpdate(device: QuecDeviceModel, dps: QuecIotDataPointsModel) {
            QLog.i(TAG, "dpsUpdate:${dps.action}")
            if (dps.action != QuecIotDataPointsModel.QuecIotDataPointAction.QuecIotDataPointActionTSL_REPORT) {
                return
            }

            QuecThreadUtil.RunMainThread {
                dps.dps.forEach { item ->
                    itemList.find { it.id == item.id }?.apply {
                        when (dataType) {
                            QuecIotDataPointDataType.INT, QuecIotDataPointDataType.BOOL,
                            QuecIotDataPointDataType.FLOAT, QuecIotDataPointDataType.DOUBLE,
                            QuecIotDataPointDataType.TEXT, QuecIotDataPointDataType.DATE,
                            QuecIotDataPointDataType.ENUM -> {
                                attributeValue = item.value.toString()
                            }
                        }
                        val index = itemList.indexOf(this)
                        adapter.notifyItemChanged(index)
                    }
                }
            }
        }

        override fun onlineUpdate(device: QuecDeviceModel, onlineState: Int) {
            QLog.i(TAG, "device:${device.channelId} onlineState:$onlineState")
            //todo 主线程调用
            QuecThreadUtil.RunMainThread {
                showOnlineStatus(onlineState)
            }
        }

    }

    override fun getViewBinding(): ActivityDeviceControlExBinding {
        return ActivityDeviceControlExBinding.inflate(layoutInflater)
    }

    override fun initView(savedInstanceState: Bundle?) {
        binding.apply {
            ivBack.setOnClickListener { finish() }

            adapter = DeviceControlAdapter(itemList) { showControlDialog(it) }
            rvList.adapter = adapter
            rvList.layoutManager = LinearLayoutManager(mContext)
            rvList.addItemDecoration(
                DividerItemDecoration(
                    mContext,
                    DividerItemDecoration.VERTICAL
                )
            )

            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                sendMode = when (checkedId) {
                    R.id.radio_ws -> QuecIotDataSendMode.QuecIotDataSendModeWS
                    R.id.radio_wifi -> QuecIotDataSendMode.QuecIotDataSendModeWifi
                    R.id.radio_ble -> QuecIotDataSendMode.QuecIotDataSendModeBLE
                    else -> QuecIotDataSendMode.QuecIotDataSendModeAuto
                }
            }
        }
    }

    override fun initData() {
        deviceClient = QuecDeviceClient.initWithDevice(device)

        binding.title.text = device.deviceName

        deviceClient.addListener(listener)

        isConnecting = deviceClient.getConnectingState()
        showOnlineStatus()
        queryTsl()
    }

    override fun onDestroy() {
        super.onDestroy()

        deviceClient.removeListener(listener)
    }

    override fun initTestItem() {
        addItem("建立连接") {
            SelectItemDialog(mContext).apply {
                addItem("auto") {
                    deviceClient.connect()
                }
                if (isSupport(Type.WS)) {
                    addItem("ws") {
                        deviceClient.connectWithMode(QuecIotDataSendMode.QuecIotDataSendModeWS)
                    }
                }
                if (isSupport(Type.WIFI)) {
                    addItem("wifi") {
                        deviceClient.connectWithMode(QuecIotDataSendMode.QuecIotDataSendModeWifi)
                    }
                }
                if (isSupport(Type.BLE)) {
                    addItem("ble") {
                        deviceClient.connectWithMode(QuecIotDataSendMode.QuecIotDataSendModeBLE)
                    }
                }
            }.show()
        }

        addItem("断开连接") {
            SelectItemDialog(mContext).apply {
                addItem("all") {
                    deviceClient.disconnect()
                }
                if (isSupport(Type.WS)) {
                    addItem("ws") {
                        deviceClient.disconnectWithType(QuecIotChannelType.QuecIotChannelTypeWS)
                    }
                }
                if (isSupport(Type.WIFI)) {
                    addItem("wifi") {
                        deviceClient.disconnectWithType(QuecIotChannelType.QuecIotChannelTypeWifi)
                    }
                }
                if (isSupport(Type.BLE)) {
                    addItem("ble") {
                        deviceClient.disconnectWithType(QuecIotChannelType.QuecIotChannelTypeBLE)
                    }
                }
            }.show()
        }

        addItem("主动查询数据") {
            queryDps()
        }
    }

    private fun queryTsl() {
        if (!device.isOnlyBle) {
            QuecDeviceService.getProductTslAndDeviceBusinessAttributes(
                device.productKey,
                device.deviceKey,
                null,
                null,
                null,
                null,
                object : IDeviceTSLModelCallback {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onResultCallback(list: MutableList<QuecProductTSLPropertyModel<*>>?) {
                        itemList.clear()
                        list?.forEach {
                            itemList.add(it)
                        }
                        adapter.notifyDataSetChanged()
                    }

                    override fun onFail(throwable: Throwable?) {
                        showMessage("查询设备失败: $throwable")
                    }

                }
            )
        } else {
            QuecDeviceService.getProductTSL(device.productKey, object : IDeviceTSLCallBack {
                @SuppressLint("NotifyDataSetChanged")
                override fun onSuccess(
                    modelBasicList: MutableList<ModelBasic<Any>>?,
                    tslEventList: MutableList<TSLEvent>?,
                    tslServiceList: MutableList<TSLService>?
                ) {
                    itemList.clear()
                    modelBasicList?.forEach { item ->
                        itemList.add(QuecProductTSLPropertyModel<Any>().apply {
                            id = item.id
                            code = item.code
                            dataType = item.dataType
                            name = item.name
                            subType = item.subType
                            sort = item.sort
                            specs = item.specs
                        })
                    }
                    adapter.notifyDataSetChanged()
                    if (getOnlineStatus(deviceClient.getConnectState(), Type.BLE)) {
                        queryDps()
                    }
                }

                override fun onFail(throwable: Throwable?) {
                    showMessage("查询设备失败: $throwable")
                }

            })
        }
    }

    private fun queryDps() {
        deviceClient.readDps(itemList.map { item ->
            QuecIotDataPointsModel.DataModel().apply {
                id = item.id
                code = item.code
                dataType = dataTypeMap[item.dataType]
            }
        }, sendMode) {
            //todo 主线程切换
            QuecThreadUtil.RunMainThread {
                if (it.isSuccess) {
                    showMessage("设备状态查询成功")
                } else {
                    handlerResult(it)
                }
            }
        }
    }

    private fun showControlDialog(item: QuecProductTSLPropertyModel<*>) {
        if (!item.subType.contains("W")) {
            showMessage("该物模型不支持控制")
            return
        }
        when (item.dataType) {
            QuecIotDataPointDataType.BOOL -> {
                val specs = item.specs
                if (specs is ArrayList<*>) {
                    SelectItemDialog(mContext).apply {
                        specs.forEach {
                            if (it is BooleanSpecs) {
                                addItem("[${it.name}] ${it.value}") {
                                    writeDps(item, it.value)
                                }
                            }
                        }
                    }.show()
                } else {
                    showMessage("数据异常")
                }
            }

            QuecIotDataPointDataType.TEXT -> {
                val specs = item.specs
                if (specs is ArrayList<*> && specs.isNotEmpty()) {
                    val info = specs[0]
                    if (info is TextSpecs) {
                        EditTextPopup(mContext).apply {
                            setTitle("请输入内容, 长度限制: ${info.length}")
                            if (item.attributeValue != null) {
                                setContent(item.attributeValue.toString())
                            }
                            setEditTextListener {
                                if (it.length > (info.length.toIntOrNull() ?: Int.MAX_VALUE)) {
                                    showMessage("输入内容长度不能超过${info.length}")
                                } else {
                                    writeDps(item, it)
                                }
                            }
                        }.showPopupWindow()
                    } else {
                        showMessage("数据异常")
                    }
                } else {
                    showMessage("数据异常")
                }
            }

            QuecIotDataPointDataType.ENUM -> {
                val specs = item.specs
                if (specs is ArrayList<*>) {
                    SelectItemDialog(mContext).apply {
                        specs.forEach {
                            if (it is BooleanSpecs) {
                                addItem("[${it.name}] ${it.value}") {
                                    writeDps(item, it.value)
                                }
                            }
                        }
                    }.show()
                } else {
                    showMessage("数据异常")
                }
            }

            QuecIotDataPointDataType.INT, QuecIotDataPointDataType.FLOAT, QuecIotDataPointDataType.DOUBLE -> {
                val specs = item.specs
                if (specs is ArrayList<*> && specs.isNotEmpty()) {
                    val info = specs[0]
                    if (info is NumSpecs) {
                        EditTextPopup(mContext).apply {
                            setTitle("请输入${item.dataType}类型数据, 最小值: ${info.min} ,最大值:${info.max}")
                            if (item.attributeValue != null) {
                                setContent(item.attributeValue.toString())
                            }
                            setEditTextListener {
                                dismiss()
                                when (item.dataType) {
                                    QuecIotDataPointDataType.INT -> if (it.toIntOrNull() != null) writeDps(
                                        item,
                                        it.toLong()
                                    ) else showMessage("请输入Int类型数据")

                                    QuecIotDataPointDataType.FLOAT -> if (it.toFloatOrNull() != null) writeDps(
                                        item,
                                        it.toDouble()
                                    ) else showMessage("请输入FLOAT类型数据")

                                    QuecIotDataPointDataType.DOUBLE -> if (it.toDoubleOrNull() != null) writeDps(
                                        item,
                                        it.toDouble()
                                    ) else showMessage("请输入DOUBLE类型数据")
                                }
                            }
                        }.showPopupWindow()
                    } else {
                        showMessage("数据异常")
                    }
                } else {
                    showMessage("数据异常")
                }
            }

            QuecIotDataPointDataType.DATE -> {
                showDateTimePicker {
                    val date = it.time.time
                    writeDps(item, date)
                }
            }

            else -> showMessage("此类型数据demo中暂不支持控制")
        }
    }

    private fun writeDps(item: QuecProductTSLPropertyModel<*>, input: Any) {
        deviceClient.writeDps(listOf(QuecIotDataPointsModel.DataModel().apply {
            id = item.id
            code = item.code
            dataType = dataTypeMap[item.dataType]
            value = input
        }), sendMode) {
            QuecThreadUtil.RunMainThread { handlerResult(it) }
        }
    }

    private fun showOnlineStatus(onlineState: Int = deviceClient.getConnectState()) {
        QLog.i(TAG, "showOnlineStatus onlineState: $onlineState ,conning: $isConnecting")
        val state = (if (isConnecting > 0) "[连接中...] " else "") + if (onlineState == 0) {
            "离线"
        } else {
            "${
                if (getOnlineStatus(
                        onlineState,
                        Type.WS
                    )
                ) "ws在线" else ""
            } ${
                if (getOnlineStatus(
                        onlineState,
                        Type.WIFI
                    )
                ) " WiFi在线" else ""
            } ${
                if (
                    getOnlineStatus(onlineState, Type.BLE)
                ) " ble在线" else ""
            }"
        }
        binding.apply {
            tvConnect.text = state
            radioWs.visibility =
                if (getOnlineStatus(onlineState, Type.WS)) View.VISIBLE else View.GONE
            radioWifi.visibility =
                if (getOnlineStatus(onlineState, Type.WIFI)) View.VISIBLE else View.GONE
            radioBle.visibility =
                if (getOnlineStatus(onlineState, Type.BLE)) View.VISIBLE else View.GONE
        }

    }

    private fun isSupport(type: Type): Boolean {
        val maskEnable = device.capabilitiesBitmask and type.mask != 0
        return when (type) {
            Type.WS, Type.WIFI -> maskEnable && device.verified != "0"
            else -> maskEnable
        }
    }

    private fun getOnlineStatus(onlineState: Int, type: Type): Boolean {
        return onlineState and type.mask != 0
    }

    private fun showDateTimePicker(onDateTimeSelected: (Calendar) -> Unit) {
        val currentDate = Calendar.getInstance()

        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                // 日期选完后，继续弹时间选择器
                TimePickerDialog(
                    this,
                    { _, hourOfDay, minute ->
                        val selectedDateTime = Calendar.getInstance()
                        selectedDateTime.set(year, month, dayOfMonth, hourOfDay, minute)
                        onDateTimeSelected(selectedDateTime)
                    },
                    currentDate.get(Calendar.HOUR_OF_DAY),
                    currentDate.get(Calendar.MINUTE),
                    true // 24小时制
                ).show()
            },
            currentDate.get(Calendar.YEAR),
            currentDate.get(Calendar.MONTH),
            currentDate.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    companion object {
        private const val TAG = "DeviceControlActivity"
    }
}