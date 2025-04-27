package com.quectel.app.demo

import android.app.Application
import com.quectel.app.demo.constant.CloudConfig
import com.quectel.app.demo.utils.SPUtils
import com.quectel.sdk.iot.QuecCloudServiceType
import com.quectel.sdk.iot.service.QuecIotSdk

object SdkManager {
    private const val QUEC_ClOUD_SERVICE_TYPE: String = "QuecCloudServiceType"
    const val TYPE_CHINA = 0
    const val TYPE_EUROPE = 1
    const val TYPE_NORTH_AMERICA = 2

    fun init(application: Application) {
        when (getServiceType(application)) {
            TYPE_CHINA -> QuecIotSdk.startWithConfig(
                application,
                CloudConfig.DATA_CENTER_CHINA_USER_DOMAIN,
                CloudConfig.DATA_CENTER_CHINA_DOMAIN_SECRET,
                QuecCloudServiceType.QuecCloudServiceTypeChina
            )

            TYPE_EUROPE -> QuecIotSdk.startWithConfig(
                application,
                CloudConfig.DATA_CENTER_EUROPE_USER_DOMAIN,
                CloudConfig.DATA_CENTER_EUROPE_DOMAIN_SECRET,
                QuecCloudServiceType.QuecCloudServiceTypeEurope
            )

            TYPE_NORTH_AMERICA -> QuecIotSdk.startWithConfig(
                application,
                CloudConfig.DATA_CENTER_AMERICA_USER_DOMAIN,
                CloudConfig.DATA_CENTER_AMERICA_DOMAIN_SECRET,
                QuecCloudServiceType.QuecCloudServiceTypeNorthAmerica
            )
        }

        QuecIotSdk.setDebugMode(true)
    }

    fun selectService(application: Application, type: Int) {
        SPUtils.putInt(application, QUEC_ClOUD_SERVICE_TYPE, type)
        init(application)
    }

    fun getServiceType(application: Application): Int {
        return SPUtils.getInt(
            application,
            QUEC_ClOUD_SERVICE_TYPE,
            TYPE_CHINA
        )
    }

    fun isCustomService(application: Application): Boolean {
        //todo: 私有云注册实现
        return false
    }
}