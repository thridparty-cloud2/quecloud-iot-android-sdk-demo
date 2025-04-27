package com.quectel.app.demo.common

object AppVariable {
    var isDeviceInfoChange = false
    var isGroupInfoChange = false
    var isMineInfoChange = false
    var isSceneInfoChange = false

    fun setDeviceChange() {
        isDeviceInfoChange = true
    }

    fun setGroupChange() {
        isGroupInfoChange = true
    }

    fun setMineChange() {
        isMineInfoChange = true
    }

    fun setSceneChange() {
        isSceneInfoChange = true
    }
}