package com.quectel.app.demo.constant;

public class CloudConfig {
    //私有云
    public static final String IS_CUSTOM_CLOUD = "isCustomCloud";
    public static final String PUBLIC_CONFIG_BEAN = "publicConfigBean";
    //移远云
    public static final String QUEC_USER_DOMAIN = "quecUserDomain";
    public static final String QUEC_DOMAIN_SECRET = "quecDomainSecret";
    public static final String QUEC_ClOUD_SERVICE_TYPE = "QuecCloudServiceType";

    //中国区域
    public static final String DATA_CENTER_CHINA_USER_DOMAIN = "";
    public static final String DATA_CENTER_CHINA_DOMAIN_SECRET = "";

    //欧洲区域
    public static final String DATA_CENTER_EUROPE_USER_DOMAIN = "";
    public static final String DATA_CENTER_EUROPE_DOMAIN_SECRET = "";

    //北美区域
    public static final String DATA_CENTER_AMERICA_USER_DOMAIN = "";
    public static final String DATA_CENTER_AMERICA_DOMAIN_SECRET = "";

    public static final String DEFAULT_USER_DOMAIN = DATA_CENTER_CHINA_USER_DOMAIN;
    public static final String DEFAULT_DOMAIN_SECRET = DATA_CENTER_CHINA_DOMAIN_SECRET;
}
