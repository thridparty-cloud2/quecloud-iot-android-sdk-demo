package com.quectel.app.demo.ui

import android.app.Dialog
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.OnClick
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.quectel.app.demo.R
import com.quectel.app.demo.adapter.DeviceModelAdapter
import com.quectel.app.demo.base.BaseActivity
import com.quectel.app.demo.bean.UserDeviceList.DataBean.ListBean
import com.quectel.app.demo.utils.AddOperate
import com.quectel.app.demo.utils.MyUtils
import com.quectel.app.demo.utils.ToastUtils
import com.quectel.app.demo.widget.BottomItemDecorationSystem
import com.quectel.app.device.bean.ArraySpecs
import com.quectel.app.device.bean.ArrayStructSpecs
import com.quectel.app.device.bean.BatchControlDevice
import com.quectel.app.device.bean.BooleanSpecs
import com.quectel.app.device.bean.BusinessValue
import com.quectel.app.device.bean.ModelBasic
import com.quectel.app.device.bean.NumSpecs
import com.quectel.app.device.bean.TSLEvent
import com.quectel.app.device.bean.TSLService
import com.quectel.app.device.bean.TextSpecs
import com.quectel.app.device.callback.IDeviceTSLCallBack
import com.quectel.app.device.constant.ModelStyleConstant
import com.quectel.app.device.deviceservice.IDevService
import com.quectel.app.device.receiver.NetStatusReceiver
import com.quectel.app.device.utils.DeviceServiceFactory
import com.quectel.app.quecnetwork.httpservice.IHttpCallBack
import com.quectel.app.websocket.deviceservice.IWebSocketService
import com.quectel.app.websocket.utils.WebSocketServiceLocater
import com.quectel.app.websocket.websocket.cmd.KValue
import com.quectel.basic.common.entity.QuecDeviceModel
import com.quectel.basic.common.utils.QuecGsonUtil
import com.quectel.basic.common.utils.QuecThreadUtil
import com.quectel.basic.common.utils.QuecToastUtil
import com.quectel.sdk.iot.channel.kit.constaint.QuecIotChannelType
import com.quectel.sdk.iot.channel.kit.constaint.QuecIotDataSendMode
import com.quectel.sdk.iot.channel.kit.model.QuecIotDataPointsModel
import com.quectel.sdk.iot.channel.kit.model.QuecIotDataPointsModel.DataModel.QuecIotDataPointDataType
import com.suke.widget.SwitchButton
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.Date


class DeviceControlActivity() : BaseActivity() {


    var pk: String? = ""
    var dk: String? = ""

    var mReceiver: NetStatusReceiver? = null


    val map = mapOf(
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

    var mode: QuecIotDataSendMode = QuecIotDataSendMode.QuecIotDataSendModeAuto;

    var isOnline = true
    var readList: MutableList<BusinessValue?> = ArrayList()
    var readWriteList: MutableList<BusinessValue?> = ArrayList()
    var contentList: MutableList<BusinessValue?> = ArrayList()
    var mAdapter: DeviceModelAdapter? = null
    var booleanData: KValue? = null
    var cachePosition = -1
    var cacheMap = HashMap<Int, View>()
    var numberCacheMap = HashMap<Int, BusinessValue>()

    lateinit var device: ListBean
    lateinit var radio: RadioGroup

    override fun getContentLayout(): Int {
        return R.layout.activity_device_control
    }

    override fun addHeadColor() {
        MyUtils.addStatusBarView(this, R.color.gray_bg)
    }

    lateinit var recyclerView: RecyclerView

    lateinit var tvConnect: TextView;

    lateinit var ivBack: ImageView

    var pkDkModle: QuecDeviceModel = QuecDeviceModel();
    var deviceControlManager: DeviceControlManager? = null

    val onConnectCallback = { it: Boolean, type: QuecIotChannelType ->
        QuecThreadUtil.RunMainThread {
            tvConnect.text = "isConnect $it type ${type.`val`}"
        }
        Toast.makeText(activity, "isConnect $it type ${type.`val`}", Toast.LENGTH_SHORT).show()

    }

    val onDataCallback = { channelId: String,
                           type: QuecIotChannelType,
                           module: QuecIotDataPointsModel ->
        Log.e("onDataCallback", QuecGsonUtil.gsonString(module))
        val jsonObject = if (module.rawData == null) module.dps else module.rawData
        Toast.makeText(activity, "onDataCallback ${jsonObject.toString()}", Toast.LENGTH_SHORT)
            .show()

    }

    val onDisconnect = { channelId: String, type: QuecIotChannelType ->
        QuecThreadUtil.RunMainThread {
            tvConnect.text = "onDisConnect $channelId ${type.`val`}"
        }
        Toast.makeText(activity, "onDisConnect $channelId ${type.`val`}", Toast.LENGTH_SHORT).show()
    }


    override fun initData() {

        ivBack = findViewById(R.id.iv_back)
        ivBack.setOnClickListener {
            finish()
        }
        recyclerView = findViewById(R.id.mList);
        tvConnect = findViewById(R.id.tv_connect)
        mReceiver = NetStatusReceiver()
        val filter = IntentFilter()
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(mReceiver, filter)
        val intent = intent
        device = intent.getSerializableExtra("device") as ListBean;
        pk = device.productKey
        dk = device.deviceKey

        isOnline = device.onlineStatus != 0
        queryModelTSL()

        val channelId: String = pk + "_" + dk
        deviceControlManager =
            DeviceControlManager(
                activity,
                channelId,
                onConnectCallback,
                onDataCallback,
                onDisconnect
            )
        val pkDkModle: QuecDeviceModel = QuecDeviceModel();
        pkDkModle.pk = pk;
        pkDkModle.dk = dk;
        pkDkModle.capabilitiesBitmask = device?.capabilitiesBitmask!!
        pkDkModle.onlineStatus = 1
        pkDkModle.bindingkey = device?.authKey

        deviceControlManager?.startChannel(pkDkModle)

        initAdapter()

        //判断设备是否拥有WS能力
        val hasWsCapabilities = pkDkModle.capabilitiesBitmask and 1 != 0
        findViewById<View>(R.id.radio_ws).visibility =
            if (hasWsCapabilities) View.VISIBLE else View.GONE
        //判断设备是否拥有Wifi能力
        val hasWifiCapabilities = pkDkModle.capabilitiesBitmask shr 1 and 1 != 0
        findViewById<View>(R.id.radio_wifi).visibility =
            if (hasWifiCapabilities) View.VISIBLE else View.GONE
        //判断设备是否拥有Wifi能力
        val hasBleCapabilities = pkDkModle.capabilitiesBitmask shr 2 and 1 != 0
        findViewById<View>(R.id.radio_ble).visibility =
            if (hasBleCapabilities) View.VISIBLE else View.GONE

        radio = findViewById(R.id.radioGroup)
        radio.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                when (checkedId) {
                    R.id.radio_auto -> {
                        mode = QuecIotDataSendMode.QuecIotDataSendModeAuto
                    }

                    R.id.radio_wifi -> {
                        mode = QuecIotDataSendMode.QuecIotDataSendModeWifi
                    }

                    R.id.radio_ble -> {
                        mode = QuecIotDataSendMode.QuecIotDataSendModeBLE
                    }

                    R.id.radio_ws -> {
                        mode = QuecIotDataSendMode.QuecIotDataSendModeWS
                    }
                }
                deviceControlManager?.startChannel(pkDkModle, mode)
            }

        })

    }


    fun initAdapter() {
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.addItemDecoration(
            BottomItemDecorationSystem(
                activity
            )
        )
        mAdapter = DeviceModelAdapter(activity, contentList)
        recyclerView!!.adapter = mAdapter
        mAdapter!!.setOnItemClickListener { adapter, view, position ->
            println("position--:$position")
            if (isOnline) {
                val item = mAdapter!!.data[position]
                val type = item.dataType
                val subType = item.subType
                if (type == ModelStyleConstant.BOOL && subType.contains("W")) {
                    cachePosition = position
                    cacheMap[position] = view
                    val switch_button =
                        view.findViewById<SwitchButton>(R.id.switch_button)
                    switch_button.setOnCheckedChangeListener { view, isChecked ->
                        println("isChecked--:$isChecked")
                        val dp = QuecIotDataPointsModel.DataModel<Any>();
                        dp.id = item.abId;
                        dp.dataType = map[item.dataType.uppercase()]
                        dp.value = isChecked.toString()
                        deviceControlManager?.writeDps(mutableListOf(dp))
                    }

                    switch_button.toggle()
                } else if (subType.contains("W")) {
                    if (type == ModelStyleConstant.INT || type == ModelStyleConstant.FLOAT || type == ModelStyleConstant.DOUBLE) {
                        cachePosition = position
                        var step: String? = null
                        val code = item.resourceCode
                        for (mb in modelBasics!!) {
                            if (code == mb.getCode()) {
                                val numSpecs = mb.getSpecs()[0] as NumSpecs
                                step =
                                    "min:" + numSpecs.min + " max:" + numSpecs.max + " step:" + numSpecs.step
                                createSendDialog(numSpecs, step, item)
                            }
                        }
                    } else if (type == ModelStyleConstant.ENUM) {
                        cachePosition = position
                        val code = item.resourceCode
                        for (mb in modelBasics!!) {
                            if (code == mb.getCode()) {
                                val specs: List<BooleanSpecs> =
                                    mb.getSpecs() as List<BooleanSpecs>
                                createSendEnumDialog(specs, item)
                            }
                        }
                    } else if (type == ModelStyleConstant.DATE || type == ModelStyleConstant.TEXT) {
                        cachePosition = position
                        val code = item.resourceCode
                        for (mb in modelBasics!!) {
                            if (code == mb.getCode()) {
                                if (type == ModelStyleConstant.TEXT) {
                                    val ts = mb.getSpecs()[0] as TextSpecs
                                    createDateOrTextDialog(ts, item)
                                } else {
                                    createDateOrTextDialog(null, item)
                                }
                            }
                        }
                    } else if (type == ModelStyleConstant.STRUCT) {
                        cachePosition = position
                        val code = item.resourceCode
                        for (mb in modelBasics!!) {
                            if (code == mb.getCode()) {
                                val specs: List<ModelBasic<*>> =
                                    mb.getSpecs() as List<ModelBasic<*>>
                                createSendStructDialog(specs, item)
                            }
                        }
                    } else if (type == ModelStyleConstant.ARRAY) {
                        cachePosition = position
                        val code = item.resourceCode
                        for (mb in modelBasics!!) {
                            if (code == mb.getCode()) {
                                val obj = mb.getSpecs()[0]
                                if (obj is ArraySpecs) {
                                    createSendSimpleArrayDialog(obj, item)
                                } else if (obj is ArrayStructSpecs<*>) {

                                    createSendArrayContainStructDialog(
                                        obj,
                                        item
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                //  ToastUtils.showShort(activity,"设备离线");
                val item = mAdapter!!.data[position]
                val type = item.dataType
                val subType = item.subType
                if (type == ModelStyleConstant.BOOL && subType == "RW") {
                    cachePosition = position
                    cacheMap[position] = view
                    val switch_button =
                        view.findViewById<SwitchButton>(R.id.switch_button)
                    switch_button.setOnCheckedChangeListener { view, isChecked ->
                        try {
                            val obj = JSONObject()
                            obj.put(item.resourceCode, isChecked.toString())
                            val data = JSONArray().put(obj).toString()
                            sendBaseHttpData(data, pk, dk)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    switch_button.toggle()
                } else if (subType == "RW") {
                    if (type == ModelStyleConstant.INT || type == ModelStyleConstant.FLOAT || type == ModelStyleConstant.DOUBLE) {
                        cachePosition = position
                        var step: String? = null
                        val code = item.resourceCode
                        for (mb in modelBasics!!) {
                            if (code == mb.getCode()) {
                                val numSpecs = mb.getSpecs()[0] as NumSpecs
                                step =
                                    "min:" + numSpecs.min + " max:" + numSpecs.max + " step:" + numSpecs.step
                                createSendDialog(numSpecs, step, item)
                            }
                        }
                    } else if (type == ModelStyleConstant.ENUM) {
                        cachePosition = position
                        val code = item.resourceCode
                        for (mb in modelBasics!!) {
                            if (code == mb.getCode()) {
                                val specs: List<BooleanSpecs> =
                                    mb.getSpecs() as List<BooleanSpecs>
                                createSendEnumDialog(specs, item)
                            }
                        }
                    } else if (type == ModelStyleConstant.DATE || type == ModelStyleConstant.TEXT) {
                        cachePosition = position
                        val code = item.resourceCode
                        for (mb in modelBasics!!) {
                            if (code == mb.getCode()) {
                                if (type == ModelStyleConstant.TEXT) {
                                    val ts = mb.getSpecs()[0] as TextSpecs
                                    createDateOrTextDialog(ts, item)
                                } else {
                                    createDateOrTextDialog(null, item)
                                }
                            }
                        }
                    } else if (type == ModelStyleConstant.STRUCT) {
                        cachePosition = position
                        val code = item.resourceCode
                        for (mb in modelBasics!!) {
                            if (code == mb.getCode()) {
                                val specs: List<ModelBasic<*>> =
                                    mb.getSpecs() as List<ModelBasic<*>>
                                createSendStructDialog(specs, item)
                            }
                        }
                    } else if (type == ModelStyleConstant.ARRAY) {
                        cachePosition = position
                        val code = item.resourceCode
                        for (mb in modelBasics!!) {
                            if (code == mb.getCode()) {
                                val obj = mb.getSpecs()[0]
                                if (obj is ArraySpecs) {
                                    createSendSimpleArrayDialog(obj, item)
                                } else if (obj is ArrayStructSpecs<*>) {
                                    createSendArrayContainStructDialog(
                                        obj,
                                        item
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    //处理websocket 事件上报
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun onEvent(socketEvent: SocketEvent) {
//        println("socketEvent--:$socketEvent")
//        val socketType = socketEvent.type
//        var dataContent: String? = null
//        when (socketType) {
//            EventType.EVENT_TYPE_LOGIN_SUCCESS -> {
//                println("login success--:" + socketEvent.data)
//                WebSocketServiceLocater.getService(IWebSocketService::class.java)
//                    .subscribeDevice(dk, pk)
//            }
//
//            EventType.EVENT_TYPE_ONLINE -> try {
//                val obj = JSONObject(socketEvent.data)
//                val value = obj.getJSONObject("data").getInt("value")
//                isOnline = value == DEVICE_ONLINE
//                val result = if (value == DEVICE_ONLINE) "在线" else "离线"
//                println("result-EVENT_TYPE_ONLINE-:$result")
//            } catch (e: JSONException) {
//                e.printStackTrace()
//            }
//
//            EventType.EVENT_TYPE_M_ATTR_REPORT -> {
//                dataContent = socketEvent.data
//                try {
//                    val obj = JSONObject(dataContent)
//                    val dataObject = obj.getJSONObject("data")
//                    val deviceKey = obj.getString("deviceKey")
//                    if (deviceKey == dk) {
//                        //结构体上报 数据 code标识符:value
//                        //{"kv":{"task":{"Time_Refresh":false,"time_duration":"16"}}
//                        //数组上报
//                        //{"kv":{"array":["6","11","18"]}
//                        //{"kv":{"array_twotest":["111","2222","333333"]}
//                        //数组嵌套结构体  上报数据  code:value
//                        //{"kv":{"Array_Struct":[{"test1":false,"test2":"2"},{"test1":false,"test2":"2"}]}
//                        val kvObject = dataObject.getJSONObject("kv")
//                        for (bv in contentList) {
//                            val code = bv!!.resourceCode
//                            if (kvObject.has(code)) {
//                                val content = kvObject.getString(code)
//                                bv.resourceValce = content
//                            }
//                        }
//                        mAdapter!!.notifyDataSetChanged()
//                    }
//                } catch (e: JSONException) {
//                    e.printStackTrace()
//                }
//            }
//
//            EventType.EVENT_TYPE_CMD_ACK -> {
//                dataContent = socketEvent.data
//                try {
//                    val obj = JSONObject(dataContent)
//                    val status = obj.getString("status")
//                    println("status--:$status")
//                    if (WebSocketConfig.CMD_ACK_STATUS_SUCCESS != status) {
//                        //应答失败,处理数据回滚
//                    } else {
//                        ToastUtils.showShort(activity, "下发成功")
//                    }
//                } catch (e: JSONException) {
//                    e.printStackTrace()
//                }
//            }
//
//            EventType.EVENT_TYPE_WEBSOCKET_DEVICE_UNBIND -> dataContent = socketEvent.data
//            EventType.EVENT_TYPE_WEBSOCKET_LOGIN_FAILURE -> {}
//            EventType.EVENT_TYPE_WEBSOCKET_ERROR -> try {
//                val obj = JSONObject(socketEvent.data)
//                val msg = obj.getString("msg")
//                ToastUtils.showShort(activity, msg)
//                val item = mAdapter!!.data[cachePosition]
//                val type = item.dataType
//                if (item.dataType == ModelStyleConstant.BOOL) {
//                    val view = cacheMap[cachePosition]
//                    val switch_button = view!!.findViewById<SwitchButton>(R.id.switch_button)
//                    val value = Boolean.parseBoolean(item.resourceValce)
//                    switch_button.isChecked = value
//                } else if (type == ModelStyleConstant.INT || type == ModelStyleConstant.FLOAT || type == ModelStyleConstant.DOUBLE || type == ModelStyleConstant.ENUM || type == ModelStyleConstant.DATE || type == ModelStyleConstant.TEXT || type == ModelStyleConstant.ARRAY || type == ModelStyleConstant.STRUCT) {
//                    val businessValue = numberCacheMap[cachePosition]
//                    contentList[cachePosition] = businessValue
//                    mAdapter!!.notifyDataSetChanged()
//                }
//            } catch (e: JSONException) {
//                e.printStackTrace()
//            }
//
//            EventType.EVENT_TYPE_WEBSOCKET_LOCATION -> {}
//            EventType.EVENT_TYPE_M_EVENT_REPORT -> {}
//        }
//    }

    @OnClick(R.id.iv_back)
    fun onViewClick(view: View) {
        val intent: Intent? = null
        when (view.id) {
            R.id.iv_back -> finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        WebSocketServiceLocater.getService(IWebSocketService::class.java).disconnect()
        if (mReceiver != null) {
            unregisterReceiver(mReceiver)
        }
        deviceControlManager?.disConnect(pkDkModle)
    }

    var modelBasics: List<ModelBasic<Any>>? = null
    private fun queryModelTSL() {

        //查询物模型, 如果需要同时查询物模型和属性可以使用DeviceServiceFactory.getInstance().getService(IDevService::class.java) .getProductTSLValueWithProductKey
        DeviceServiceFactory.getInstance().getService(IDevService::class.java)
            .getProductTSLWithCache(pk, object : IDeviceTSLCallBack {
                override fun onSuccess(
                    modelBasicList: MutableList<ModelBasic<Any>>?,
                    tslEventList: MutableList<TSLEvent>?,
                    tslServiceList: MutableList<TSLService>?
                ) {
                    modelBasics = modelBasicList
                    val list = covertBussinevalue(modelBasics)
                    val writeList = list.filter { !it.subType.equals("R") }
                    contentList.clear()
                    contentList.addAll(writeList)
                    mAdapter?.notifyDataSetChanged()
                }

                override fun onFail(throwable: Throwable?) {
                    throwable?.printStackTrace()
                }
            })

    }


    private fun queryBusinessAttributes() {
        //要查询的属性标识符集合
        val codeList: MutableList<String> = ArrayList()
        //标识符集合
//        codeList.add("temperature")
//        codeList.add("state")
        //查询类型集合
        //1 查询设备基础属性 2 查询物模型属性  3 查询定位信息
        val typeList: MutableList<String> = ArrayList()
        typeList.add("1")
        //        typeList.add("2");
//        typeList.add("3");

        //传 null 查询所有属性和类型
        // DeviceServiceFactory.getInstance().getService(IDevService.class).queryBusinessAttributes(codeList,pk,dk,typeList,
        DeviceServiceFactory.getInstance().getService(IDevService::class.java)
            .queryBusinessAttributes(
                emptyList(), pk, dk, null, "", "",
                object : IHttpCallBack {
                    override fun onSuccess(result: String) {
                        println("queryBusinessAttributes--:$result")
                        try {
                            val mainObj = JSONObject(result)
                            val code = mainObj.getInt("code")
                            if (code == 200) {
                                readList.clear()
                                readWriteList.clear()
                                contentList.clear()
                                val obj = mainObj.getJSONObject("data")
                                val array = obj.getJSONArray("customizeTslInfo")
                                val type = object : TypeToken<List<BusinessValue?>?>() {}.type
                                val childList =
                                    Gson().fromJson<List<BusinessValue>>(array.toString(), type)
                                println("childList--:" + childList.size)
                                for (bv in childList) {
                                    if ("R" == bv.subType) {
                                        readList.add(bv)
                                    } else if ("RW" == bv.subType) {
                                        readWriteList.add(bv)
                                    }
                                }
//                                contentList.addAll(readList)
//                                contentList.addAll(readWriteList)
                                mAdapter?.notifyDataSetChanged()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFail(e: Throwable) {
                        e.printStackTrace()
                    }
                }
            )
    }

//    private fun sendWebSocketBasicData(data: KValue, dk: String?, pk: String?) {
//        WebSocketServiceLocater.getService(IWebSocketService::class.java)
//            .writeWebSocketBaseData(data, dk, pk)
//    }

    private fun createSendDialog(numSpecs: NumSpecs, step: String, item: BusinessValue) {
        numberCacheMap[cachePosition] = item
        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(R.layout.send_model_command_dialog, null)
        val mDialog = Dialog(activity, R.style.dialogTM)
        mDialog.setContentView(view)
        mDialog.setCancelable(true)
        mDialog.setCanceledOnTouchOutside(false)
        val edit_content = mDialog.findViewById<View>(R.id.edit_content) as EditText
        edit_content.setText(item.resourceValce)
        val tv_step = mDialog.findViewById<View>(R.id.tv_step) as TextView
        val bt_cancel = mDialog.findViewById<View>(R.id.bt_cancel) as Button
        val bt_sure = mDialog.findViewById<View>(R.id.bt_sure) as Button
        val bt_sub = mDialog.findViewById<View>(R.id.bt_sub) as Button
        val bt_add = mDialog.findViewById<View>(R.id.bt_add) as Button
        tv_step.text = step
        bt_cancel.setOnClickListener { mDialog.dismiss() }
        bt_sure.setOnClickListener {
            mDialog.dismiss()
            if (isOnline) {
                val value = MyUtils.getEditTextContent(edit_content)
                item.resourceValce = value
                sendDps(item, value)
                contentList[cachePosition] = item
                mAdapter!!.notifyDataSetChanged()

//                sendDps(item, item.resourceCode)
//                contentList[cachePosition] = item
//                mAdapter!!.notifyDataSetChanged()
            } else {
                //http下发
                try {
                    val obj = JSONObject()
                    obj.put(item.resourceCode, item.resourceValce)
                    val data = JSONArray().put(obj).toString()
                    sendBaseHttpData(data, pk, dk)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        bt_sub.setOnClickListener {
            item.resourceValce = edit_content.text.toString()
            if (TextUtils.isEmpty(item.resourceValce)) {
                QuecToastUtil.showL("请输入值")
                return@setOnClickListener
            }
            if (item.resourceValce.toInt() <= 0) {
                return@setOnClickListener
            }
            val result = AddOperate.sub(item.resourceValce, numSpecs.step)
            item.resourceValce = result
            edit_content.setText(result)
        }
        bt_add.setOnClickListener {
            item.resourceValce = edit_content.text.toString()
            if (TextUtils.isEmpty(item.resourceValce)) {
                QuecToastUtil.showL("请输入值")
                return@setOnClickListener
            }
            val result = AddOperate.add(item.resourceValce, numSpecs.step)
            item.resourceValce = result
            edit_content.setText(result)
        }
        mDialog.show()
    }

    private fun createSendEnumDialog(specs: List<BooleanSpecs>, item: BusinessValue) {
        numberCacheMap[cachePosition] = item
        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(R.layout.send_model_enum_dialog, null)
        val mDialog = Dialog(activity, R.style.dialogTM)
        mDialog.setContentView(view)
        mDialog.setCancelable(true)
        mDialog.setCanceledOnTouchOutside(false)
        val edit_value = mDialog.findViewById<View>(R.id.edit_value) as EditText
        val tv_enum_name = mDialog.findViewById<View>(R.id.tv_enum_name) as TextView
        val tv_enum_value = mDialog.findViewById<View>(R.id.tv_enum_value) as TextView
        val bt_cancel = mDialog.findViewById<View>(R.id.bt_cancel) as Button
        val bt_sure = mDialog.findViewById<View>(R.id.bt_sure) as Button
        val sb1 = StringBuilder()
        val sb2 = StringBuilder()
        for (bp in specs) {
            sb1.append(bp.name)
            sb1.append(" ")
            sb2.append(bp.value)
            sb2.append(" ")
        }
        tv_enum_name.text = "Enum name: $sb1"
        tv_enum_value.text = "Enum value: $sb2"
        bt_cancel.setOnClickListener { mDialog.dismiss() }
        bt_sure.setOnClickListener {
            mDialog.dismiss()
            if (isOnline) {
                val enumValue = MyUtils.getEditTextContent(edit_value)
                item.resourceValce = enumValue
                sendDps(item, enumValue)
                contentList[cachePosition] = item
                mAdapter!!.notifyDataSetChanged()
            } else {
                val enumValue = MyUtils.getEditTextContent(edit_value)
                try {
                    val obj = JSONObject()
                    obj.put(item.resourceCode, enumValue)
                    val data = JSONArray().put(obj).toString()
                    sendBaseHttpData(data, pk, dk)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        mDialog.show()
    }

    private fun createDateOrTextDialog(specs: TextSpecs?, item: BusinessValue) {
        numberCacheMap[cachePosition] = item
        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(R.layout.send_model_date_text_dialog, null)
        val mDialog = Dialog(activity, R.style.dialogTM)
        mDialog.setContentView(view)
        mDialog.setCancelable(true)
        mDialog.setCanceledOnTouchOutside(false)
        val edit_value = mDialog.findViewById<View>(R.id.edit_value) as EditText
        edit_value.setText(item.resourceValce)
        val tv_text_length = mDialog.findViewById<View>(R.id.tv_text_length) as TextView
        if (specs != null) {
            tv_text_length.text = "文本长度: " + specs.length
        } else {
            tv_text_length.visibility = View.GONE
        }
        val bt_cancel = mDialog.findViewById<View>(R.id.bt_cancel) as Button
        val bt_sure = mDialog.findViewById<View>(R.id.bt_sure) as Button
        bt_cancel.setOnClickListener { mDialog.dismiss() }
        bt_sure.setOnClickListener {
            mDialog.dismiss()
            if (isOnline) {
                val value = MyUtils.getEditTextContent(edit_value)

                item.resourceValce = value

                sendDps(item, value)
                contentList[cachePosition] = item
                mAdapter!!.notifyDataSetChanged()
            } else {
                val enumValue = MyUtils.getEditTextContent(edit_value)
                try {
                    val obj = JSONObject()
                    obj.put(item.resourceCode, enumValue)
                    val data = JSONArray().put(obj).toString()
                    sendBaseHttpData(data, pk, dk)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        mDialog.show()
    }

    //下发结构体数据
    private fun createSendStructDialog(specs: List<ModelBasic<*>>?, item: BusinessValue) {
        numberCacheMap[cachePosition] = item
        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(R.layout.send_model_struct_dialog, null)
        val mDialog = Dialog(activity, R.style.dialogTM)
        mDialog.setContentView(view)
        mDialog.setCancelable(true)
        mDialog.setCanceledOnTouchOutside(false)
        val bt_cancel = mDialog.findViewById<View>(R.id.bt_cancel) as Button
        val bt_sure = mDialog.findViewById<View>(R.id.bt_sure) as Button
        bt_cancel.setOnClickListener { mDialog.dismiss() }
        bt_sure.setOnClickListener {
            mDialog.dismiss()
            //    * 属性array
            //    * "[{\"key\":[{\"id\":\"value1\"},{\"id\":\"value2\"}]}]"（id为0）
            //    * 属性struct
            //    * "[{\"key\":[{\"key1\":\"value1\"},{\"key2\":\"value2\"}]}]"
            //    * 属性array含有struct
            //    * "[{\"key\":[{\"id\":[{\"key1\":\"value1\"}]},{\"id\":[{\"key2\":\"value2\"}]}]}]"（id为0）
            if (specs != null && specs.size > 0) {
                if (isOnline) {
                    val mListChild: MutableList<QuecIotDataPointsModel.DataModel<Any>> = ArrayList()
                    for (mb in specs) {
                        if (mb.getDataType() == ModelStyleConstant.BOOL) {

                            val data = QuecIotDataPointsModel.DataModel<Any>();
                            data.id = mb.id
                            data.code = mb.getCode();
                            data.dataType = map[mb.getDataType()]
                            data.value = true
//                            val v1 =
//                                KValue(mb.getId(), mb.getName(), ModelStyleConstant.BOOL, "true")
                            mListChild.add(data)
                        } else if (mb.getDataType() == ModelStyleConstant.INT) {
                            //val v1 = KValue(mb.getId(), mb.getName(), ModelStyleConstant.INT, 55)
                            val data = QuecIotDataPointsModel.DataModel<Any>();
                            data.id = mb.id
                            data.code = mb.getCode();
                            data.dataType = map[mb.getDataType()]
                            data.value = 55
                            mListChild.add(data)
                        } else if (mb.getDataType() == ModelStyleConstant.ENUM) {
                            val specs: List<BooleanSpecs> = mb.getSpecs() as List<BooleanSpecs>
                            //遍历枚举值
                            for (bs in specs) {
                                println("bs--:" + bs.getValue())
                            }
                            val data = QuecIotDataPointsModel.DataModel<Any>();
                            data.id = mb.id
                            data.code = mb.getCode();
                            data.dataType = map[mb.getDataType()]
                            data.value = specs[0].getValue()
                            mListChild.add(data)
                        } else if (mb.getDataType() == ModelStyleConstant.FLOAT) {
                            val data = QuecIotDataPointsModel.DataModel<Any>();
                            data.id = mb.id
                            data.code = mb.getCode();
                            data.dataType = map[mb.getDataType()]
                            data.value = "22.22"
                            mListChild.add(data)
                        } else if (mb.getDataType() == ModelStyleConstant.DOUBLE) {
                            val data = QuecIotDataPointsModel.DataModel<Any>();
                            data.id = mb.id
                            data.code = mb.getCode();
                            data.dataType = map[mb.getDataType()]
                            data.value = "33.33"
                            mListChild.add(data)
                        } else if (mb.getDataType() == ModelStyleConstant.TEXT) {
                            val data = QuecIotDataPointsModel.DataModel<Any>();
                            data.id = mb.id
                            data.code = mb.getCode();
                            data.dataType = map[mb.getDataType()]
                            data.value = "text_content"
                            mListChild.add(data)
                        } else if (mb.getDataType() == ModelStyleConstant.DATE) {
                            val data = QuecIotDataPointsModel.DataModel<Any>();
                            data.id = mb.id
                            data.code = mb.getCode();
                            data.dataType = map[mb.getDataType()]
                            data.value = Date().time.toString()
                            mListChild.add(data)
                        }
                    }

                    val structData = QuecIotDataPointsModel.DataModel<Any>();
                    structData.id = item.abId
                    structData.code = item.resourceCode
                    structData.dataType = map[item.dataType]
                    structData.value = mListChild
                    deviceControlManager?.writeDps(mutableListOf(structData))
                } else {
                    // * "[{\"key\":[{\"key1\":\"value1\"},{\"key2\":\"value2\"}]}]"
                    try {
                        val obj = JSONObject()
                        val childArray = JSONArray()
                        for (mb in specs) {
                            if (mb.getDataType() == ModelStyleConstant.BOOL) {
                                val child1 = JSONObject()
                                child1.put(mb.getCode(), "true")
                                childArray.put(child1)
                            } else if (mb.getDataType() == ModelStyleConstant.INT) {
                                val child1 = JSONObject()
                                child1.put(mb.getCode(), 88)
                                childArray.put(child1)
                            } else if (mb.getDataType() == ModelStyleConstant.ENUM) {
                                val specs: List<BooleanSpecs> = mb.getSpecs() as List<BooleanSpecs>
                                val child1 = JSONObject()
                                child1.put(mb.getCode(), specs[0].getValue())
                                childArray.put(child1)
                            } else if (mb.getDataType() == ModelStyleConstant.FLOAT) {
                                val child1 = JSONObject()
                                child1.put(mb.getCode(), 12.2)
                                childArray.put(child1)
                            } else if (mb.getDataType() == ModelStyleConstant.DOUBLE) {
                                val child1 = JSONObject()
                                child1.put(mb.getCode(), 12.3)
                                childArray.put(child1)
                            } else if (mb.getDataType() == ModelStyleConstant.TEXT) {
                                val child1 = JSONObject()
                                child1.put(mb.getCode(), "test_content")
                                childArray.put(child1)
                            } else if (mb.getDataType() == ModelStyleConstant.DATE) {
                                val child1 = JSONObject()
                                child1.put(mb.getCode(), Date().time.toString())
                                childArray.put(child1)
                            }
                        }
                        obj.put(item.resourceCode, childArray)
                        val data = JSONArray().put(obj).toString()
                        sendBaseHttpData(data, pk, dk)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        mDialog.show()
    }

    //发送数组包含基本类型数据
    private fun createSendSimpleArrayDialog(specs: ArraySpecs?, item: BusinessValue) {
        numberCacheMap[cachePosition] = item
        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(R.layout.send_model_struct_dialog, null)
        val mDialog = Dialog(activity, R.style.dialogTM)
        mDialog.setContentView(view)
        mDialog.setCancelable(true)
        mDialog.setCanceledOnTouchOutside(false)
        val bt_cancel = mDialog.findViewById<View>(R.id.bt_cancel) as Button
        val bt_sure = mDialog.findViewById<View>(R.id.bt_sure) as Button
        bt_cancel.setOnClickListener { mDialog.dismiss() }
        bt_sure.setOnClickListener {
            mDialog.dismiss()
            if (specs != null) {
                if (isOnline) {
                    val mListChild: MutableList<QuecIotDataPointsModel.DataModel<Any>> = ArrayList()
                    //根据 getDataType  判断 数组里添加什么类型的数据  添加数据不能超过   specs.getSize()
                    val mb = specs
                    if (mb.getDataType() == ModelStyleConstant.BOOL) {
                        val data = QuecIotDataPointsModel.DataModel<Any>();
                        data.dataType = map[mb.getDataType()]
                        data.value = true
                        mListChild.add(data)
                    } else if (mb.getDataType() == ModelStyleConstant.INT) {
                        //val v1 = KValue(mb.getId(), mb.getName(), ModelStyleConstant.INT, 55)
                        val data = QuecIotDataPointsModel.DataModel<Any>();
                        data.dataType = map[mb.getDataType()]
                        data.value = 8
                        mListChild.add(data)

                    } else if (mb.getDataType() == ModelStyleConstant.FLOAT) {
                        val data = QuecIotDataPointsModel.DataModel<Any>();
                        data.dataType = map[mb.getDataType()]
                        data.value = "22.22"
                        mListChild.add(data)
                    } else if (mb.getDataType() == ModelStyleConstant.DOUBLE) {
                        val data = QuecIotDataPointsModel.DataModel<Any>();
                        data.dataType = map[mb.getDataType()]
                        data.value = "33.33"
                        mListChild.add(data)
                    } else if (mb.getDataType() == ModelStyleConstant.TEXT) {
                        val data = QuecIotDataPointsModel.DataModel<Any>();

                        data.dataType = map[mb.getDataType()]
                        data.value = "text_content"
                        mListChild.add(data)
                    } else if (mb.getDataType() == ModelStyleConstant.DATE) {
                        val data = QuecIotDataPointsModel.DataModel<Any>();
                        data.dataType = map[mb.getDataType()]
                        data.value = Date().time.toString()
                        mListChild.add(data)
                    }

                    val arrayData = QuecIotDataPointsModel.DataModel<Any>();
                    arrayData.id = item.abId
                    arrayData.code = item.resourceCode
                    arrayData.dataType = map[item.dataType]
                    arrayData.value = mListChild
                    deviceControlManager?.writeDps(mutableListOf(arrayData))

                } else {
                    //"[{\"key\":[{\"id\":\"value1\"},{\"id\":\"value2\"}]}]"（id为0）
                    try {
                        val obj = JSONObject()
                        val childArray = JSONArray()
                        if (specs.getDataType() == ModelStyleConstant.INT) {
                            val child1 = JSONObject()
                            child1.put("0", "77")
                            childArray.put(child1)
                        } else if (specs.getDataType() == ModelStyleConstant.BOOL) {
                            val child1 = JSONObject()
                            child1.put("0", "false")
                            childArray.put(child1)
                        } else if (specs.getDataType() == ModelStyleConstant.ENUM) {
                            val child1 = JSONObject()
                            child1.put("0", 1)
                            childArray.put(child1)
                        } else if (specs.getDataType() == ModelStyleConstant.FLOAT) {
                            val child1 = JSONObject()
                            child1.put("0", 2.3)
                            childArray.put(child1)
                        } else if (specs.getDataType() == ModelStyleConstant.DOUBLE) {
                            val child1 = JSONObject()
                            child1.put("0", 3.5)
                            childArray.put(child1)
                        } else if (specs.getDataType() == ModelStyleConstant.TEXT) {
                            val child1 = JSONObject()
                            child1.put("0", "text")
                            childArray.put(child1)
                        } else if (specs.getDataType() == ModelStyleConstant.DATE) {
                            val child1 = JSONObject()
                            child1.put("0", Date().time.toString())
                            childArray.put(child1)
                        }
                        obj.put(item.resourceCode, childArray)
                        val data = JSONArray().put(obj).toString()
                        sendBaseHttpData(data, pk, dk)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        mDialog.show()
    }

    //发送数组嵌套结构体
    private fun createSendArrayContainStructDialog(
        specs: ArrayStructSpecs<*>?,
        item: BusinessValue
    ) {
        numberCacheMap[cachePosition] = item
        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(R.layout.send_model_struct_dialog, null)
        val mDialog = Dialog(activity, R.style.dialogTM)
        mDialog.setContentView(view)
        mDialog.setCancelable(true)
        mDialog.setCanceledOnTouchOutside(false)
        val bt_cancel = mDialog.findViewById<View>(R.id.bt_cancel) as Button
        val bt_sure = mDialog.findViewById<View>(R.id.bt_sure) as Button
        bt_cancel.setOnClickListener { mDialog.dismiss() }
        bt_sure.setOnClickListener {
            mDialog.dismiss()
            if (specs != null) {
                if (isOnline) {
                    val specs1 = specs.getSpecs()
                    val ChildList1: MutableList<KValue> = ArrayList()
                    val ChildList2: MutableList<KValue> = ArrayList()
                    //遍历结构体包含哪些类型
                    for (mb in specs1) {
                        if (mb.getDataType() == ModelStyleConstant.BOOL) {
                            val v11 = KValue(mb.getId(), "1111", ModelStyleConstant.BOOL, "true")
                            ChildList1.add(v11)
                            val v12 = KValue(mb.getId(), "2222", ModelStyleConstant.BOOL, "false")
                            ChildList2.add(v12)
                        } else if (mb.getDataType() == ModelStyleConstant.ENUM) {
                            val v21 = KValue(mb.getId(), "enum1", ModelStyleConstant.ENUM, 1)
                            ChildList1.add(v21)
                            val v22 = KValue(mb.getId(), "enum2", ModelStyleConstant.ENUM, 2)
                            ChildList2.add(v22)
                        } else if (mb.getDataType() == ModelStyleConstant.INT) {
                            val v31 = KValue(mb.getId(), "int1", ModelStyleConstant.INT, 5)
                            ChildList1.add(v31)
                            val v32 = KValue(mb.getId(), "int2", ModelStyleConstant.INT, 6)
                            ChildList2.add(v32)
                        } else if (mb.getDataType() == ModelStyleConstant.FLOAT) {
                            val v41 = KValue(mb.getId(), "float", ModelStyleConstant.FLOAT, 5.1)
                            ChildList1.add(v41)
                            val v42 = KValue(mb.getId(), "float", ModelStyleConstant.FLOAT, 5.2)
                            ChildList2.add(v42)
                        } else if (mb.getDataType() == ModelStyleConstant.DOUBLE) {
                            val v51 = KValue(mb.getId(), "double", ModelStyleConstant.DOUBLE, 6.1)
                            ChildList1.add(v51)
                            val v52 = KValue(mb.getId(), "double", ModelStyleConstant.DOUBLE, 7.2)
                            ChildList2.add(v52)
                        } else if (mb.getDataType() == ModelStyleConstant.TEXT) {
                            val v61 = KValue(mb.getId(), "text", ModelStyleConstant.TEXT, "hello1")
                            ChildList1.add(v61)
                            val v62 = KValue(mb.getId(), "text", ModelStyleConstant.TEXT, "hello2")
                            ChildList2.add(v62)
                        } else if (mb.getDataType() == ModelStyleConstant.DATE) {
                            val v71 = KValue(
                                mb.getId(),
                                "date",
                                ModelStyleConstant.DATE,
                                Date().time.toString()
                            )
                            ChildList1.add(v71)
                            val v72 = KValue(
                                mb.getId(),
                                "date",
                                ModelStyleConstant.DATE,
                                Date().time.toString()
                            )
                            ChildList2.add(v72)
                        }
                    }
                    val v1 = KValue(0, "", ModelStyleConstant.STRUCT, ChildList1)
                    val v2 = KValue(0, "", ModelStyleConstant.STRUCT, ChildList2)
                    val mListChild: MutableList<KValue> = ArrayList()
                    mListChild.add(v1)
                    mListChild.add(v2)
                    WebSocketServiceLocater.getService(IWebSocketService::class.java)
                        .writeWebSocketArrayContainStructData(
                            item.abId, item.name,
                            mListChild, dk, pk
                        )
                } else {
                    //"[{\"key\":[{\"id\":[{\"key1\":\"value1\"}]},{\"id\":[{\"key2\":\"value2\"}]}]}]"（id为0）
                    val specs1 = specs.getSpecs()
                    val childArray1 = JSONArray()
                    for (mb in specs1) {
                        if (mb.getDataType() == ModelStyleConstant.BOOL) {
                            val child1 = JSONObject()
                            try {
                                child1.put(mb.getCode(), "true")
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                            childArray1.put(child1)
                        } else if (mb.getDataType() == ModelStyleConstant.ENUM) {
                            val child1 = JSONObject()
                            try {
                                child1.put(mb.getCode(), 2)
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                            childArray1.put(child1)
                        } else if (mb.getDataType() == ModelStyleConstant.INT) {
                            val child1 = JSONObject()
                            try {
                                child1.put(mb.getCode(), 3)
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                            childArray1.put(child1)
                        } else if (mb.getDataType() == ModelStyleConstant.FLOAT) {
                            val child1 = JSONObject()
                            try {
                                child1.put(mb.getCode(), 3.1)
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                            childArray1.put(child1)
                        } else if (mb.getDataType() == ModelStyleConstant.DOUBLE) {
                            val child1 = JSONObject()
                            try {
                                child1.put(mb.getCode(), 5.6)
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                            childArray1.put(child1)
                        } else if (mb.getDataType() == ModelStyleConstant.TEXT) {
                            val child1 = JSONObject()
                            try {
                                child1.put(mb.getCode(), "text")
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                            childArray1.put(child1)
                        } else if (mb.getDataType() == ModelStyleConstant.DATE) {
                            val child1 = JSONObject()
                            try {
                                child1.put(mb.getCode(), Date().time.toString())
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                            childArray1.put(child1)
                        }
                    }
                    val c1 = JSONObject()
                    try {
                        c1.put("0", childArray1)
                        val array = JSONArray()
                        array.put(c1)
                        val obj = JSONObject()
                        obj.put(item.resourceCode, array)
                        val data = JSONArray().put(obj).toString()
                        sendBaseHttpData(data, pk, dk)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        mDialog.show()
    }

    fun sendBaseHttpData(data: String?, pk: String?, dk: String?) {
        val mList: MutableList<BatchControlDevice> = ArrayList()
        val test1 = BatchControlDevice(pk, dk, "", "")
        mList.add(test1)
        //缓存时间 1天
        val time = 60 * 60 * 24
        DeviceServiceFactory.getInstance().getService(IDevService::class.java)
            .batchControlDevice(data, mList, time,
                1, 2, 2, 2,
                object : IHttpCallBack {
                    override fun onSuccess(result: String) {
                        println("batchControlDevice--:$result")
                        try {
                            val obj = JSONObject(result)
                            val data = obj.getJSONObject("data")
                            val jarray = data.getJSONArray("failureList")
                            if (jarray != null && jarray.length() > 0) {
                                ToastUtils.showShort(activity, "http下发失败")
                            } else {
                                ToastUtils.showShort(activity, "http下发成功")
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }

                    override fun onFail(e: Throwable) {
                        e.printStackTrace()
                    }
                }
            )
    }


    fun sendDps(item: BusinessValue, value: Any) {
        val dp = QuecIotDataPointsModel.DataModel<Any>();
        dp.code = item.resourceCode
        dp.id = item.abId;
        dp.dataType = map[item.dataType.uppercase()]
        dp.value = value
        deviceControlManager?.writeDps(mutableListOf(dp))
        Log.i(
            "sendDps",
            "dp.code=" + dp.code + ",dp.id=" + dp.id + ",dp.dataType=" + dp.dataType + ",dp.value=" + dp.value
        )
    }

    fun setDp(item: BusinessValue, value: Any): QuecIotDataPointsModel.DataModel<Any> {
        val dp = QuecIotDataPointsModel.DataModel<Any>();
        dp.code = item.resourceCode
        dp.id = item.abId;
        dp.dataType = map[item.dataType.uppercase()]
        dp.value = value
        return dp;
    }

    companion object {
        const val DEVICE_ONLINE = 1
    }


    fun covertBussinevalue(mudelBasics: List<ModelBasic<Any>>?): List<BusinessValue> {
        val list: MutableList<BusinessValue> = mutableListOf();
        mudelBasics?.forEach {
            val bsvalue = BusinessValue();
            bsvalue.abId = it.id;
            bsvalue.name = it.name;
            bsvalue.subType = it.subType;
            bsvalue.dataType = it.dataType;
            bsvalue.resourceCode = it.code;
            list.add(bsvalue)
        }
        return list

    }
}
