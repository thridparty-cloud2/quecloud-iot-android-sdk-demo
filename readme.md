
## iot-android-sdk-demo

### 一、此demo演示如何使用QuecIoTAppSDK从头开始构建物联网应用程序。QuecIoTAppSDK分为几个功能组，让开发人员清楚地了解不同功能的实现，包括用户注册过程、设备绑定和控制、设备群组设置。可绑定蜂窝设备或者WIFI/蓝牙设备。对于设备控制，可基于HTTP和WebSocket进行控制。

### 二、设计接口/属性

### 初始化
```
  需要在一进入应用的 Application onCreate方法里面QuecSDKMergeManager.getInstance().init(this);

```

### 配置用户域、用户域秘钥、云服务类型
```
  public void initProject(int serviceType, String userDomain, String domainSecret)
  在Application onCreate方法里调用 QuecSDKMergeManager.getInstance().initProject(0,userDomain,domainSecret);

```

|参数	|是否必传	|说明	|
| --- | --- | --- | 
|serviceType| 是|	0 国内   非0国际	|
|userDomain| 是|	用户域，DMP平台创建APP生成	|
|domainSecret| 是|	用户域秘钥，DMP平台创建APP生成	|





