package com.quectel.app.demo;
import android.app.Application;
import com.quectel.app.demo.utils.CrashHandler;

import com.quectel.app.quecnetwork.logservice.ILogService;
import com.quectel.app.quecnetwork.utils.LogService;

public class App extends Application {

    String userDomain = "xxxx";
    String domainSecret = "xxxx";

    @Override
    public void onCreate() {
        super.onCreate();
        QuecSDKMergeManager.getInstance().init(this);
        /**
         *  @param serviceType
         * @param userDomain
         * @param domainSecret
         * 初始化SDK 配置 serviceType  0国内  其他国外
         *  设置 userDomain ,DomainSecret
         */
        QuecSDKMergeManager.getInstance().initProject(0,userDomain,domainSecret);
        //开始日志记录功能
        LogService.get(ILogService.class).startLog(this);
        //异常本地捕获
       // CrashHandler.getInstance().init(this);

    }

}
