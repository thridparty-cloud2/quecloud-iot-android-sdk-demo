package com.quectel.app.demo;

import android.app.Application;

import com.quectel.app.blesdk.utils.QuecBleServiceManager;
import com.quectel.app.common.tools.utils.QuecCommonManager;
import com.quectel.app.device.utils.QuecDeviceServiceManager;
import com.quectel.app.quecnetwork.QuecNetWorkManager;


public class QuecSDKMergeManager {

    private static QuecSDKMergeManager csi = null;
    private QuecSDKMergeManager() {
    }

    public static QuecSDKMergeManager getInstance() {
        if (csi == null) {
            synchronized (QuecSDKMergeManager.class) {
                if (csi == null) {
                    csi = new QuecSDKMergeManager();
                }
            }
        }
        return csi;
    }

    public  void init(Application app) {
        QuecNetWorkManager.getInstance().init(app);
        QuecDeviceServiceManager.getInstance().init(app);
        QuecBleServiceManager.getInstance().init(app);

    }


    public void initProject(int serviceType,String userDomain,String domainSecret)
    {
        QuecCommonManager.getInstance().configServiceType(serviceType);
        QuecCommonManager.getInstance().configUserDomain(userDomain);
        QuecCommonManager.getInstance().configDomainSecret(domainSecret);
    }

}