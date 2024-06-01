package com.quectel.app.demo;

import androidx.multidex.MultiDexApplication;

import com.google.gson.Gson;
import com.quectel.app.demo.constant.CloudConfig;
import com.quectel.app.demo.utils.SPUtils;
import com.quectel.app.demo.utils.ToastUtils;
import com.quectel.sdk.iot.QuecCloudServiceType;
import com.quectel.sdk.iot.QuecIotAppSdk;
import com.quectel.sdk.iot.bean.QuecPublicConfigBean;


public class App extends MultiDexApplication {

    String defaulUserDomain = "C.DM.5903.1";
    String defaulDomainSecret = "EufftRJSuWuVY7c6txzGifV9bJcfXHAFa7hXY5doXSn7";

    int defaulQuecCloudServiceType = QuecCloudServiceType.QuecCloudServiceTypeChina.getValue();

    @Override
    public void onCreate() {
        super.onCreate();

        //判断是否是私有云
        if (SPUtils.getBoolean(this, CloudConfig.IS_CUSTOM_CLOUD, false)) {
            String configBeanString = SPUtils.getString(this, CloudConfig.PUBLIC_CONFIG_BEAN, null);
            if (configBeanString == null) {
                ToastUtils.showShort(this, "configBean是null");
                return;
            }
            QuecPublicConfigBean configBean = new Gson().fromJson(configBeanString, QuecPublicConfigBean.class);

            QuecIotAppSdk.getInstance().startWithQuecPublicConfigBean(this, configBean);

        } else {

            String userDomain = SPUtils.getString(this, CloudConfig.QUEC_USER_DOMAIN, defaulUserDomain);
            String domainSecret = SPUtils.getString(this, CloudConfig.QUEC_DOMAIN_SECRET, defaulDomainSecret);
            int serviceType = SPUtils.getInt(this, CloudConfig.QUEC_ClOUD_SERVICE_TYPE, defaulQuecCloudServiceType);

            QuecCloudServiceType quecCloudServiceType = QuecCloudServiceType.QuecCloudServiceTypeChina;
            if (serviceType == 0) {
                quecCloudServiceType = QuecCloudServiceType.QuecCloudServiceTypeChina;
            } else if (serviceType == 1) {
                quecCloudServiceType = QuecCloudServiceType.QuecCloudServiceTypeEurope;
            } else if (serviceType == 2) {
                quecCloudServiceType = QuecCloudServiceType.QuecCloudServiceTypeNorthAmerica;
            }

            /**
             *  @param serviceType
             * @param userDomain
             * @param domainSecret
             * 初始化SDK 配置 serviceType  0国内  其他国外
             *  设置 userDomain ,DomainSecret
             */


            QuecIotAppSdk.getInstance().startWithUserDomain(this, userDomain, domainSecret, quecCloudServiceType);

        }

    }


}
