
## QuecMergeLibrary

### 一、功能列表

|功能	|功能说明	|实现版本	|DMP API Version|
| --- | --- | --- | --- |
|账户相关| 配置云服务类型|	1.0.0	| V2|

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


## 三、使用步骤
### 集成
```
gradle:7.0.2以上  settings.gradle --> repositories下面也要引入

```






