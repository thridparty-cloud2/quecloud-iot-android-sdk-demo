package com.quectel.app.demo.common

object AppVariable {
    var isDeviceInfoChange = false
    var isGroupInfoChange = false
    var isMineInfoChange = false

    fun setDeviceChange() {
        isDeviceInfoChange = true
    }

    fun setGroupChange() {
        isGroupInfoChange = true
    }

    fun setMineChange() {
        isMineInfoChange = true
    }
}