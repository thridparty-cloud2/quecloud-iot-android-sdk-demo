package com.quectel.app.demo.ui

import android.app.Activity
import android.content.Context
import com.quectel.app.device.iot.IotChannelController
import com.quectel.basic.common.entity.QuecDeviceModel
import com.quectel.sdk.iot.channel.kit.chanel.IQuecChannelManager
import com.quectel.sdk.iot.channel.kit.constaint.QuecIotChannelType
import com.quectel.sdk.iot.channel.kit.model.QuecIotDataPointsModel
import java.util.Objects

class DeviceControlManager(
    val context: Context,
    var channelId: String,
    var onConnectCallback: (connected: Boolean) -> Unit,
    var onDataCallback: (channelId: String, type: QuecIotChannelType, module: QuecIotDataPointsModel<*>) -> Unit,
    var onDisconnect: (channelId: String, type: QuecIotChannelType) -> Unit
) {


    val listener = object : IQuecChannelManager.IQuecCallBackListener {
        override fun onConnect(p0: String, p1: QuecIotChannelType?, p2: Boolean, p3: String?) {
            val activity = context as Activity
            activity.runOnUiThread {
                onConnectCallback(p2)
            }

        }

        override fun onData(
            p0: String,
            p1: QuecIotChannelType,
            p2: QuecIotDataPointsModel<*>
        ) {
            val activity = context as Activity
            activity.runOnUiThread {
                onDataCallback(p0, p1, p2);
            }

        }

        override fun onBleClose(p0: String?) {

        }

        override fun onDisConnect(p0: String, p1: QuecIotChannelType) {
            val activity = context as Activity
            activity.runOnUiThread {
                onDisconnect(p0, p1);
            }
        }

    }

    public fun startChannel(pkDkModel: QuecDeviceModel) {
        val list = mutableListOf<QuecDeviceModel>()
        list.add(pkDkModel);
        IotChannelController.getInstance().startChannels(context, list, listener)
    }

    fun writeDps(dataPointsModel: MutableList<QuecIotDataPointsModel.DataModel<Any>>) {
        IotChannelController.getInstance().writeDps(channelId, dataPointsModel);
    }

}