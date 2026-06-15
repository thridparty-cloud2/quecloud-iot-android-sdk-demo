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
import com.quectel.app.demo.dialog.CommonDialog
import com.quectel.app.demo.dialog.EditTextPopup
import com.quectel.app.demo.dialog.SelectItemDialog
import com.quectel.app.device.bean.ArraySpecs
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
import com.quectel.basic.common.scope.QuecScope
import com.quectel.basic.common.utils.QuecThreadUtil
import com.quectel.basic.queclog.QLog
import com.quectel.sdk.iot.channel.kit.chanel.bean.QuecDeviceStatus.Type
import com.quectel.sdk.iot.channel.kit.constaint.QuecIotChannelType
import com.quectel.sdk.iot.channel.kit.constaint.QuecIotDataSendMode
import com.quectel.sdk.iot.channel.kit.model.QuecIotDataPointsModel
import com.quectel.sdk.iot.channel.kit.model.QuecIotDataPointsModel.DataModel.QuecIotDataPointDataType
import com.quectel.sdk.iot.channel.kit.v2.QuecDeviceClient
import com.quectel.sdk.iot.channel.kit.v2.QuecDeviceClientApi
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Calendar
import kotlin.coroutines.resume

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
                dps.dps?.forEach { item ->
                    itemList.find { it.id == item.id }?.apply {
                        when (dataType) {
                            QuecIotDataPointDataType.INT, QuecIotDataPointDataType.BOOL,
                            QuecIotDataPointDataType.FLOAT, QuecIotDataPointDataType.DOUBLE,
                            QuecIotDataPointDataType.TEXT, QuecIotDataPointDataType.DATE,
                            QuecIotDataPointDataType.ENUM -> {
                                attributeValue = item.value.toString()
                            }

                            QuecIotDataPointDataType.STRUCT -> {
                                val newValue = item.value
                                if (newValue != null && newValue is ArrayList<*>) {
                                    for (newItem in newValue) {
                                        if (newItem is QuecIotDataPointsModel.DataModel) {
                                            specs.forEach { specsItem ->
                                                if (specsItem is QuecProductTSLPropertyModel<*>) {
                                                    if (specsItem.id == newItem.id) {
                                                        if (newItem.value is ByteArray) {
                                                            specsItem.attributeValue =
                                                                String(newItem.value as ByteArray)
                                                        } else {
                                                            specsItem.attributeValue =
                                                                newItem.value?.toString()
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            QuecIotDataPointDataType.ARRAY -> {
                                val newValue = item.value
                                if (newValue != null && newValue is ArrayList<*>) {
                                    val newAttributeValue = mutableListOf<Map<String, Any?>>()
                                    for (newItem in newValue) {
                                        if (newItem is QuecIotDataPointsModel.DataModel) {
                                            newAttributeValue.add(
                                                linkedMapOf(
                                                    "id" to newItem.id,
                                                    "value" to newItem.value?.toString()
                                                )
                                            )
                                        }
                                    }
                                    attributeValue = newAttributeValue
                                }
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
            //todo call on main thread
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
        addItem(getString(R.string.connect)) {
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

        addItem(getString(R.string.disconnect)) {
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

        addItem(getString(R.string.query_data_active)) {
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
                        QLog.e(throwable)
                        showMessage(getString(R.string.query_device_failed, throwable.toString()))
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
                    QLog.e(throwable)
                    showMessage(getString(R.string.query_device_failed, throwable.toString()))
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
            //todo switch to main thread
            QuecThreadUtil.RunMainThread {
                if (it.isSuccess) {
                    showMessage(getString(R.string.device_status_query_success))
                } else {
                    handlerResult(it)
                }
            }
        }
    }

    private fun showControlDialog(item: QuecProductTSLPropertyModel<*>) {
        if (!item.subType.contains("W")) {
            log(getString(R.string.tsl_not_support_control))
            return
        }

        if (item.dataType == QuecIotDataPointDataType.STRUCT) {
            CommonDialog(this@DeviceControlActivity)
                .apply {
                    setTitle(getString(R.string.hint_struct_order))
                    setYesOnclickListener(getString(R.string.confirm)) {
                        dismiss()

                        QuecScope.safeLaunch {
                            val list = mutableListOf<QuecIotDataPointsModel.DataModel>()
                            item.specs?.forEach { item ->
                                if (item is QuecProductTSLPropertyModel<*>) {
                                    val ret = getValue(item)
                                    if (ret != null) {
                                        list.add(QuecIotDataPointsModel.DataModel().apply {
                                            id = item.id
                                            code = item.code
                                            dataType = dataTypeMap[item.dataType]
                                            value = ret
                                        })
                                    }
                                }
                            }
                            if (list.isNotEmpty()) {
                                writeDps(item, list)
                            }
                        }
                    }
                }
                .show()

            return
        }

        if (item.dataType == QuecIotDataPointDataType.ARRAY) {
            if (item.specs.isNullOrEmpty()) {
                showMessage(getString(R.string.type_not_support_control))
                return
            }

            val size = (item.attributeValue as? java.util.ArrayList<*>)?.size ?: 0
            // Confirm array type
            val spec = item.specs[0]
            if (spec is ArraySpecs && size > 0) {
                when (spec.dataType) {
                    QuecIotDataPointDataType.INT, QuecIotDataPointDataType.FLOAT, QuecIotDataPointDataType.DOUBLE -> {
                        QuecScope.safeLaunch {
                            val list = mutableListOf<QuecIotDataPointsModel.DataModel>()
                            for (i in 0 until size) {
                                val ret = getValue(QuecProductTSLPropertyModel<NumSpecs>().apply {
                                    dataType = spec.dataType
                                    specs = arrayListOf(NumSpecs().apply {
                                        min = spec.min
                                        max = spec.max
                                        step = spec.step
                                    })
                                })
                                if (ret != null) {
                                    list.add(QuecIotDataPointsModel.DataModel().apply {
                                        dataType = dataTypeMap[spec.dataType]
                                        value = ret
                                    })
                                }
                            }
                            if (list.isNotEmpty()) {
                                writeDps(item, list)
                            }
                        }
                        return
                    }
                }
            }

            showMessage(getString(R.string.type_not_support_control))
            return
        }

        QuecScope.safeLaunch {
            val value = getValue(item)
            if (value != null) {
                writeDps(item, value)
            }
        }
    }

    private suspend fun getValue(item: QuecProductTSLPropertyModel<*>): Any? {
        return suspendCancellableCoroutine { handler ->
            when (item.dataType) {
                QuecIotDataPointDataType.BOOL -> {
                    val specs = item.specs
                    if (specs is ArrayList<*>) {
                        SelectItemDialog(mContext).apply {
                            specs.forEach {
                                if (it is BooleanSpecs) {
                                    addItem("[${it.name}] ${it.value}") {
                                        handler.resume(it.value)
                                    }
                                }
                            }
                            setCanceledOnTouchOutside(false)
                            setOnCancelListener {
                                handler.resume(null)
                            }
                        }.show()
                    } else {
                        handler.resume(null)
                        showMessage(getString(R.string.data_exception))
                    }
                }

                QuecIotDataPointDataType.TEXT -> {
                    val specs = item.specs
                    if (specs is ArrayList<*> && specs.isNotEmpty()) {
                        val info = specs[0]
                        if (info is TextSpecs) {
                            EditTextPopup(mContext).apply {
                                setTitle(getString(R.string.hint_input_content_with_limit, info.length.toIntOrNull() ?: 0))
                                if (item.attributeValue != null) {
                                    setContent(item.attributeValue.toString())
                                }
                                setEditTextListener {
                                    if (it.length > (info.length.toIntOrNull() ?: Int.MAX_VALUE)) {
                                        showMessage(getString(R.string.input_too_long, info.length.toIntOrNull() ?: 0))
                                    } else {
                                        dismiss()
                                        handler.resume(it)
                                    }
                                }

                                isOutSideTouchable = false
                                onCancelClickListener = View.OnClickListener {
                                    dismiss()
                                    handler.resume(null)
                                }

                            }.showPopupWindow()
                        } else {
                            handler.resume(null)
                            showMessage(getString(R.string.data_exception))
                        }
                    } else {
                        handler.resume(null)
                        showMessage(getString(R.string.data_exception))
                    }
                }

                QuecIotDataPointDataType.ENUM -> {
                    val specs = item.specs
                    if (specs is ArrayList<*>) {
                        SelectItemDialog(mContext).apply {
                            specs.forEach {
                                if (it is BooleanSpecs) {
                                    addItem("[${it.name}] ${it.value}") {
                                        handler.resume(it.value)
                                    }
                                }
                            }
                            setCanceledOnTouchOutside(false)
                            setOnCancelListener {
                                handler.resume(null)
                            }
                        }.show()
                    } else {
                        handler.resume(null)
                        showMessage(getString(R.string.data_exception))
                    }
                }

                QuecIotDataPointDataType.INT, QuecIotDataPointDataType.FLOAT, QuecIotDataPointDataType.DOUBLE -> {
                    val specs = item.specs
                    if (specs is ArrayList<*> && specs.isNotEmpty()) {
                        val info = specs[0]
                        if (info is NumSpecs) {
                            EditTextPopup(mContext).apply {
                                setTitle(getString(R.string.hint_input_range, item.dataType, info.min, info.max))
                                if (item.attributeValue != null) {
                                    setContent(item.attributeValue.toString())
                                }
                                setEditTextListener {
                                    when (item.dataType) {
                                        QuecIotDataPointDataType.INT -> if (it.toIntOrNull() != null) {
                                            dismiss()
                                            handler.resume(it.toLong())
                                        } else showMessage(getString(R.string.hint_input_int))

                                        QuecIotDataPointDataType.FLOAT -> if (it.toFloatOrNull() != null) {
                                            dismiss()
                                            handler.resume(it.toDouble())
                                        } else showMessage(getString(R.string.hint_input_float))

                                        QuecIotDataPointDataType.DOUBLE -> if (it.toDoubleOrNull() != null) {
                                            dismiss()
                                            handler.resume(it.toDouble())
                                        } else showMessage(getString(R.string.hint_input_double))
                                    }
                                }

                                isOutSideTouchable = false
                                onCancelClickListener = View.OnClickListener {
                                    dismiss()
                                }
                            }.showPopupWindow()
                        } else {
                            handler.resume(null)
                            showMessage(getString(R.string.data_exception))
                        }
                    } else {
                        handler.resume(null)
                        showMessage(getString(R.string.data_exception))
                    }
                }

                QuecIotDataPointDataType.DATE -> {
                    showDateTimePicker({
                        val date = it.time.time
                        handler.resume(date)
                    }, {
                        handler.resume(null)
                    })
                }

                else -> {
                    showMessage(getString(R.string.type_not_support_control))
                    handler.resume(null)
                }
            }
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
        val state = (if (isConnecting > 0) getString(R.string.status_connecting) + " " else "") + if (onlineState == 0) {
            getString(R.string.offline)
        } else {
            "${
                if (getOnlineStatus(
                        onlineState,
                        Type.WS
                    )
                ) getString(R.string.status_ws_online) else ""
            } ${
                if (getOnlineStatus(
                        onlineState,
                        Type.WIFI
                    )
                ) getString(R.string.status_wifi_online) else ""
            } ${
                if (
                    getOnlineStatus(onlineState, Type.BLE)
                ) getString(R.string.status_ble_online) else ""
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

    private fun showDateTimePicker(
        onDateTimeSelected: (Calendar) -> Unit,
        onCancel: (() -> Unit)? = null
    ) {
        val currentDate = Calendar.getInstance()

        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                // After date is selected, show time picker
                TimePickerDialog(
                    this,
                    { _, hourOfDay, minute ->
                        val selectedDateTime = Calendar.getInstance()
                        selectedDateTime.set(year, month, dayOfMonth, hourOfDay, minute)
                        onDateTimeSelected(selectedDateTime)
                    },
                    currentDate.get(Calendar.HOUR_OF_DAY),
                    currentDate.get(Calendar.MINUTE),
                    true // 24-hour format
                ).show()
            },
            currentDate.get(Calendar.YEAR),
            currentDate.get(Calendar.MONTH),
            currentDate.get(Calendar.DAY_OF_MONTH)
        ).apply {
            setOnCancelListener { onCancel?.invoke() }
        }.show()
    }

    companion object {
        private const val TAG = "DeviceControlActivity"
    }
}