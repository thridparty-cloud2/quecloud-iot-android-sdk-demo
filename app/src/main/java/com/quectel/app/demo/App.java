package com.quectel.app.demo;

import android.app.Activity;
import android.app.Application;

import com.hm.lifecycle.api.ApplicationLifecycleManager;
import com.quectel.app.blesdk.utils.QuecBleServiceManager;
import com.quectel.app.common.tools.utils.UserModulePreferences;
import com.quectel.app.demo.utils.CrashHandler;

import com.quectel.app.device.utils.QuecDeviceServiceManager;
import com.quectel.app.quecnetwork.QuecNetWorkManager;
import com.quectel.app.quecnetwork.logservice.ILogService;
import com.quectel.app.quecnetwork.utils.LogService;
import com.quectel.app.usersdk.utils.UserServiceFactory;
import com.quectel.app.websocket.utils.DeviceModulePreferences;
import com.quectel.basic.common.base.QuecBaseApp;
import com.quectel.basic.quecmmkv.MmkvManager;
import com.quectel.sdk.iot.QuecCloudServiceType;
import com.quectel.sdk.iot.QuecIotAppSdk;
import com.quectel.sdk.smart.config.api.QuecSmartConfigServiceManager;


public class App extends QuecBaseApp {

    String userDomain = "";
    String domainSecret = "";

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
        MmkvManager.getInstance().init(this);
        ApplicationLifecycleManager.init();
        ApplicationLifecycleManager.onCreate(this);
        QuecBleServiceManager.getInstance().init(this);
        QuecSmartConfigServiceManager.getInstance().init(this);
        UserModulePreferences.init(this);
        QuecNetWorkManager.getInstance().init(this);
        QuecIotAppSdk.getInstance().startWithUserDomain(BuildConfig.userDomain, BuildConfig.userScrete, QuecCloudServiceType.QuecCloudServiceTypeChina);
        DeviceModulePreferences.init(this);
        //开始日志记录功能
//        LogService.get(ILogService.class).startLog(this);
        //异常本地捕获
        // CrashHandler.getInstance().init(this);
        QuecDeviceServiceManager.getInstance().a = this;
    }

    @Override
    protected void onActivityStarted(Activity activity) {

    }

    @Override
    protected void onActivityStopped(Activity activity) {

    }

}
