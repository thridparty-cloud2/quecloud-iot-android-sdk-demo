package com.quectel.app.demo;

import androidx.multidex.MultiDexApplication;

import com.hm.lifecycle.api.ApplicationLifecycleManager;
import com.quectel.app.blesdk.utils.QuecBleServiceManager;
import com.quectel.app.common.tools.utils.UserModulePreferences;
import com.quectel.app.device.utils.QuecDeviceServiceManager;
import com.quectel.app.quecnetwork.QuecNetWorkManager;
import com.quectel.app.websocket.utils.DeviceModulePreferences;
import com.quectel.basic.quecmmkv.MmkvManager;
import com.quectel.sdk.iot.QuecCloudServiceType;
import com.quectel.sdk.iot.QuecIotAppSdk;
import com.quectel.sdk.smart.config.api.QuecSmartConfigServiceManager;


public class App extends MultiDexApplication {

    String userDomain = "U.SP.8589934603";
    String domainSecret = "pUTp5goB1bLinprRQMmK3EPiiuPiGrJtKUNptWRXVmP";

    @Override
    public void onCreate() {
        super.onCreate();

        /**
         *  @param serviceType
         * @param userDomain
         * @param domainSecret
         * 初始化SDK 配置 serviceType  0国内  其他国外
         *  设置 userDomain ,DomainSecret
         */

        QuecIotAppSdk.getInstance().startWithUserDomain(this,userDomain, domainSecret, QuecCloudServiceType.QuecCloudServiceTypeNorthAmerica);

    }



}
