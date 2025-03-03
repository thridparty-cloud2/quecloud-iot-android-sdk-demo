

## QuecIotSdk
### 功能列表

|功能	|功能说明	|实现版本	|DMP API Version|
| --- | --- | --- | --- |
|初始化sdk	| 设置私有云等基本信息|	1.11.0	|	V2 |
|初始化sdk	| 设置用户域名等基本信息|	1.11.0	|	V2 |
|设置国家/地区code	| 检查代码是否登录|	1.11.0	| V2|


### 二、设计接口服务/属性

#### QuecIotSdk初始化
    void startWithUserDomain(Application context, @NonNull String userDomain, @NonNull String userDomainSecret, @NonNull QuecCloudServiceType cloudServiceType);
|参数|	是否必传|说明|	
| --- | --- | --- | 
| context  |  是 | Application |
| userDomain |	是|用户域| 
| userDomainSecret |	是|用户域密钥| 
| cloudServiceType |是| 枚举 QuecCloudServiceType,中国区/欧洲区/北美地区 | 

#### QuecIotSdk初始化
     public void startWithQuecPublicConfigBean(Application context, QuecPublicConfigBean configBean)
|参数|	是否必传|说明|	
  | --- | --- | --- | 
| context  |  是 | Application |
| QuecPublicConfigBean |	是| 私有云配置实体类|

    
    class QuecPublicConfigBean
| 属性 | 类型 |说明 |
  | --- | --- | --- |
| userDomain | String | 用户域 |
| userDomainSecret | String | 用户域密钥 |
| baseUrl | String | 请求url |
| webSocketV2Url | String | websocket url|
| mcc | String | Mobile Country Code，移动国家码 ，例如中国为460|
| tcpAddr | String | mqtt直连地址 |
| pskAddr | String | mqtt psk 连接地址 |
| tlsAddr | String | mqtt tls 连接地址 |
| cerAddr | String | mqtt cer 连接地址 |

#### 设置国家/地区code
    public void setCountryCode(@NonNull String countryCode) 
|参数|	是否必传|说明|	
| --- | --- | --- | 
| countryCode | 是 |国际代码，默认为国内,传"+86"|


## 账号管理SDK

### 一、功能列表

|功能	|功能说明	|实现版本	|DMP API Version|
| --- | --- | --- | --- |
|用户账号管理	| 手机号和邮箱注册|	1.0.0	| V2|
|用户账号管理	| 手机号和密码以及验证码登录、邮箱和密码登录|	1.0.0	|V2 |
|用户账号管理	| 重置密码|	1.0.0	| V2|
|用户账号管理	| 获取、更新用户信息|	1.0.0	| V2|

### 二、设计接口服务/属性

### IUserService 服务


#### 获取服务Service对象
```
UserServiceFactory.getInstance().getService(IUserService.class)

```

#### http回调接口
```
public interface IHttpCallBack {
    public void onSuccess(String result);
    public void onFail(Throwable e);
}
public interface IResponseCallBack {
    public void onSuccess();
    public void onFail(Throwable e);
    public void onError(String errorMsg);
}
```
#### 发送短信验证码
```
public void sendPhoneSmsCode(String internationalCode, String phone,int type, String ssid, String stid, IHttpCallBack callback);

```

|参数|	是否必传|说明|	
| --- | --- | --- | 
| internationalCode |	否|国际代码，默认为国内,传"86"| 
| phone |	是|手机号码| 
| type |是| 1: 注册验证码, 2: 密码重置验证码, 3: 登录验证码代码 , 4:注销账号| 
| ssid |否|短信签名,传入用自己的,不传默认""	| 
| stid |否|短信模板，传入用自己的,不传默认""	| 

#### 发送V2短信验证码
```
public void sendV2PhoneSmsCode(String internationalCode, String phone, int codeType, IHttpCallBack callback);
```
|参数|	是否必传|说明|	
| --- | --- | --- | 
| internationalCode |	是 | 国际代码，默认为国内,传"86"| 
| phone |	是|手机号码| 
| type |是| 1：密码重置, 2：登录验证码代码 3：注册验证码 4：注销账号| 


#### 发送邮件验证码
```
public void sendEmailCode(String eaid, String email, String etid, int type, IHttpCallBack callback);

```
|参数|	是否必传|说明|	
| --- | --- | --- | 
| eaid |	否|邮件账号| 
| email |	是|邮箱| 
| etid |否| 邮件模板 不传的时候传type	| 
| type |否|  etid不传，传type   1: 注册, 2: 密码重置, 3: 注销账号模板| 

#### 发送V2邮件验证码
```
public void sendV2EmailCode(String email, int emailType, IHttpCallBack callback);

```
|参数|	是否必传|说明|	
| --- | --- | --- | 
| email |	是|邮箱| 
| emailType |是| 1:注册验证码 2:密码重置验证码 3:关联邮箱验证码 4:删除邮箱关联验证码| 


#### 手机号密码注册

```
public void phonePwdRegister(String phone,String pwd,String smsCode,
 String internationalCode,String lang,String nationality,String timezone,IHttpCallBack callback);

```
|参数|	是否必传|说明|	
| --- | --- | --- | 
| phone |	是|手机号码| 
| pwd |	是|密码| 
| smsCode |是|短信验证码	| 
| internationalCode |否|默认不传或""	| 
| lang |否|	默认不传或""| 
| nationality |否|默认不传或""	| 
| timezone |否|默认不传或""	| 

####  手机号密码登录

```
 public void phonePwdLogin(String phone, String pwd, String internationalCode, IResponseCallBack callback);

```

|参数|	是否必传|说明|	
| --- | --- | --- | 
| phone |是|手机号	| 
| pwd |	是|密码	| 
| internationalCode |否|默认不传或""	| 

#### 手机号验证码登录

```
public void phoneSmsCodeLogin(String phone,String smsCode,String internationalCode,IResponseCallBack callback);

```

|参数|	是否必传|说明|	
| --- | --- | --- | 
| phone |	是|手机号码| 
| smsCode |	是|验证码| 
| internationalCode |否|默认不传或""| 


#### 查询用户信息

```
  public void queryUserInfo(IHttpCallBack callback);

```

#### 删除用户

```
 public void deleteUser(int type,IHttpCallBack callback);

```
|参数|	是否必传|说明|	
| --- | --- | --- | 
| type |是|1-- 立即删除 2--  7天后删除，默认为 7 天后删除| 


#### 通过电话号码重置密码

```
 public void userPwdResetByPhone(String internationalCode, String code,String phone,String passWord,IHttpCallBack callback);

```
|参数|	是否必传|说明|	
| --- | --- | --- | 
| phone |	是|手机号码| 
| code |	是|验证码| 
| passWord |	是|密码| 
| internationalCode |否|默认不传或""| 

#### 通过邮箱重置密码

```
 public void userPwdResetByEmail(String internationalCode,String code,String email,String passWord,IHttpCallBack callback);

```

|参数|	是否必传|说明|	
| --- | --- | --- | 
| email |	是|邮箱| 
| code |是|邮箱验证码| 
| passWord |是|密码| 
| internationalCode |否|默认不传或""| 

#### 手机号是否已经注册

```
 public void phoneIsRegister(String internationalCode, String phone,IHttpCallBack callback);

```

####  验证国际手机号格式

```
 public void validateInternationalPhone(String nationalCode,String phone,IHttpCallBack callback);

```
|参数|	是否必传|说明|	
| --- | --- | --- | 
| phone |	是|手机号| 
| nationalCode |是|国际code| 


#### 验证短信验证码

```
public void validateSmsCode(String phone,String smsCode,String internationalCode,int isDisabled, IHttpCallBack callback);

```
|参数|	是否必传|说明|	
| --- | --- | --- | 
| phone |	是|手机号| 
| smsCode |是|验证码| 
| internationalCode |否|默认不传或""| 
| isDisabled |是|验证码验证后是否失效，1：失效 2：不失效，默认传1| 

#### 修改用户手机号

```
 public void updatePhone(String newInternationalCode,String newPhone,String newPhoneCode,
                         String oldInternationalCode,String oldPhone,String oldPhoneCode,
                            IHttpCallBack callback);

```
|参数|	是否必传|说明|	
| --- | --- | --- | 
| newPhone |	是|手机号| 
| oldPhone |	是|旧手机号| 
| newInternationalCode |是|新国际码,国内传86| 
| oldInternationalCode |是|旧国际码,国内传86| 
| newPhoneCode |	是|新手机号验证码| 
| oldPhoneCode |	是|旧手机号验证码| 


#### 更新用户信息

```
 //修改用户地址信息
  public void updateUserAddress(String address ,IHttpCallBack callback);
  //修改用户头像
  public void updateUserHeadImage(String headImage ,IHttpCallBack callback);
   //修改语言
  public void updateUserLanguage(int language ,IHttpCallBack callback);
  //修改昵称
  public void updateUserNickName(String nikeName ,IHttpCallBack callback);
  //修改性别
  public void updateUserSex(int sex ,IHttpCallBack callback);
  //修改时区
  public void updateUserTimezone(int timezone ,IHttpCallBack callback);
  //修改国家
  public void updateUserNationality(int nationality ,IHttpCallBack callback);
 

```

#### 退出登录

```
public void userLogout(IHttpCallBack callback);

```

#### 用户修改密码

```
 public void changeUserPassword(String newPwd,String oldPwd,IHttpCallBack callback);

```

|参数|	是否必传|说明|	
| --- | --- | --- | 
| newPwd |	是| 新密码 | 
| oldPwd |	是|旧密码| 

#### 查询语言列表

```
 public void queryLanguageList(IHttpCallBack callback);

```
#### 查询国家列表

```
public void queryNationalityList(IHttpCallBack callback);

```
#### 查询时区列表

```
  public void queryTimezoneList(IHttpCallBack callback);
  
```

#### 发送邮件注册验证码

```
 public void sendEmailRegisterCode(String email,IHttpCallBack callback);

```

|参数|	是否必传|说明|	
| --- | --- | --- |
| email |是|邮箱	| 


#### 邮箱密码注册

```
 public void emailPwdRegister(String code,String email,String password,int lang,int nationality,int timezone,IHttpCallBack callback);

```

|参数|	是否必传|说明|	
| --- | --- | --- | 
| code |是|邮箱验证码|  
| email |是|邮箱	| 
| password |是|密码	|
| lang |是|语言	默认传0|
| nationality |是|国家 默认传0	|
| timezone |是|时区	默认传0|

#### 邮箱密码登录

```
 public void emailPwdLogin(String email,String password,IResponseCallBack callback);

```

|参数|	是否必传|说明|	
| --- | --- | --- | 
| email |是|邮箱|  
| password |是|密码	| 


#### 发送邮件密码重置验证码

```
  public void sendEmailRepwdCode(String email,IHttpCallBack callback);

```

|参数|	是否必传|说明|	
| --- | --- | --- | 
| email |是| 邮箱 | 

#### 验证用户发送的邮件验证码

```
  public void validateEmailCode(String email,String code,int isDisabled, IHttpCallBack callback);

```

|参数|	是否必传|说明|	
| --- | --- | --- | 
| email |是| 邮箱 | 
| code |是| 验证码 | 
| isDisabled |是| 验证码验证后是否失效，1：失效 2：不失效，默认 1  (备注:该接口只是验证注销用户时候发送的邮箱验证码)| 



#### 获取用户token

```
  public String getToken();

```

#### 清除本地 token

```
 public void clearToken();

```

#### 手机号码一键登录

```
  public void phoneOneKeyLogin(OneKeyBean oneKeyBean, IHttpCallBack callback);
  参考移动文档 http://dev.10086.cn/dev10086/pub/loadAttach?attachId=6EF75FD09D4F40D1973CB7C36C3DB2E2
```
|参数|	是否必传|说明|	
| --- | --- | --- |
| appid |是| 业务在统一认证申请的应用id | 
| version |是| 2.0 | 
| strictcheck |是| 暂时填写"0"，填写“1”时，将对服务器IP白名单进行强校验（后续将强制要求IP 强校验）| 
| token |是| 业务凭证| 
| sign |是| 请求签名 appid+version+msgid+systemtime+strictcheck+oneKeyBean.getToken()+appSecret MD5 字节转16进制 | 
| userDomain |是| 用户域| 
| systemtime |是|请求消息发送的系统时间，精确到毫秒，共17位，格式：20121227180001165 | 
| msgid |是|标识请求的随机数即可(1-36 位) | 


#### 用户消息列表

```
  public void queryUserMessageList(String pk,String dk,int msgType, int page,int pageSize,boolean isRead, String content,String title,IHttpCallBack callback);
```
|参数|	是否必传|说明|	
| --- | --- | --- |
| pk |否| product key| 
| dk |否| device key| 
| msgType |否|消息类型 1-设备通知  2-设备告警  3-设备故障  4-系统消息| 
| page |否| | 
| pageSize |否|  | 
| isRead |是| 是否已读| 
| content |是|查询内容 | 
| title |是|查询 title | 

#### 阅读消息

```
 public void userReadMessage(String msgIdList,int msgType,IHttpCallBack callback);
```
|参数|	是否必传|说明|	
| --- | --- | --- |
| msgIdList |否| 阅读的消息ID列表,多个 ID 使用英文逗号分隔，如果不传，会阅读所有消息| 
| msgType |否| 消息类型| 


#### 删除消息

```
 public void userDeleteMessage(String msgId,String language,IHttpCallBack callback);
```
|参数|	是否必传|说明|	
| --- | --- | --- |
| msgId |是| 消息ID| 
| language |是| 非必传 不传 传""| 


#### 查询用户消息类型

```
 public void queryUserMessageType(IHttpCallBack callback);
 {"code":200,"msg":"","data":"1,2,3"}
```

#### 设置用户消息类型

```
 public void setUserMessageType(String recvMsgPushType, IHttpCallBack callback);
```
|参数|	是否必传|说明|	
| --- | --- | --- |
| recvMsgPushType |是| 1-设备通知  2-设备告警  3-设备故障  4-系统消息  接收的消息类型和任意组合,多个类型使用英文逗号分隔| 


|http响应码|	value|说明|	
| --- | --- | --- | 
| code |200|成功	| 
| code |5032|token_invalid,code返回5032,请退出当前页面,主动跳转到登录页面,让用户重新获取token|
| code |5106|请输入token,用户没有登录直接调用接口返回此内容	|

#### 检查用户是否登录(异步)
```java
    checkUserLoginState(QuecCallback<Boolean> resultCallback) 
```
|参数|	是否必传|说明|	
| --- | --- | --- |
| QuecResultCallback |是| 回调（result.successCode ） true /false| 

#### 检查用户是否登录
```java
    boolean isLogin(QuecResultCallback callback)
```

#### 

```java
    public void loginByAuthCode(String authCode, QuecResultCallback<QuecResult<String>> resultCallback) 
```
|参数|	是否必传|说明|	
| --- | --- | --- |
| authCode |是| 授权code|
| QuecResultCallback |是| 回调（result.successCode ） true /false| 


## 设备SDK

### 一、功能列表

|功能	|功能说明	|实现版本	|DMP API Version|
| --- | --- | --- | --- |
|设备相关	| 设备绑定、设备解绑、设备列表、设备信息更改|	1.0.0	| V2|
|设备订阅相关	| 设备订阅、解除订阅等|	1.0.0	|	V2 |
|设备控制相关	| 设备控制（基于物模型）|	1.0.0	| V2|
|设备分享相关	| 设备分享、解除分享等|	1.0.0	| V2|

### 二、设计接口服务/属性

### IDevService服务
#### 获取Service对象
```java
DeviceServiceFactory.getInstance().getService(IDevService.class)

```

#### 使用SN绑定设备
```java
 public void bindDeviceSn(String pk,String sn,String deviceName , IHttpCallBack callback);
```

|参数|	是否必传|说明|	
| --- | --- | --- | 
| pk |	是|设备pk	| 
| sn |	是|设备sn	| 
| deviceName |	否| 默认不传或""	| 


#### 查询设备信息

```java
 public void queryDeviceInfo(String pk,String dk,String shareCode,IHttpCallBack callback);

```
|参数|	是否必传|说明|	
| --- | --- | --- | 
| pk |	是|Product Key，配合 dk，和分享码二选一	| 
| dk |	是|Device Key，配合 pk，和分享码二选一|
| shareCode |否|分享码，和 pk、dk 二选一，被分享人使用分享码查询设备信息	|


#### pk dk  解绑设备

```java
 public void unBindDevice(String pk,String dk,IHttpCallBack callback);

```

|参数|	是否必传|说明|	
| --- | --- | --- | 
| pk |	是|设备productKey	| 
| dk |	是|设备DeviceKey	| 

#### 查询用户设备列表

```java
public void queryUserDeviceList(String deviceName,int page,int pageSize,IHttpCallBack callback);

```
|参数|	是否必传|说明|	
| --- | --- | --- | 
| page |	是|分页查询当前页	| 
| pageSize |是|每页多少条|
| deviceName |否| 设备名称 |


#### 蓝牙设备绑定

```java
 public void bindDeviceByBlueTooth(String authCode,String pk, String dk,String pwd,IHttpCallBack callback);

```

|参数|	是否必传|说明|	
| --- | --- | --- | 
| authCode |是|设备authCode| 
| pk |	是|product key|
| dk |	是|device key|
| pwd |	是|设备密码	| 


#### wifi设备绑定

```java
 public void bindDeviceByWifi(String deviceName, String pk,String dk, String authCode,IHttpCallBack callback);

```

|参数|	是否必传|说明|	
| --- | --- | --- | 
| deviceName |否|默认不传或""|
| dk |	是|device key|
| pk |	是|product key|
| authCode |是|设备返回的authCode	| 


#### 分享人设置设备分享信息

```java
 public void shareDeviceInfo(long acceptingExpireAt,String pk, String dk, int coverMark,long sharingExpireAt, IHttpCallBack callback);

```

|参数|	是否必传|说明|	
| --- | --- | --- | 
| acceptingExpireAt |	是|分享二维码种子失效时间 时间戳（毫秒）|
| pk |	是|product key|
| dk |	是|product key|
| coverMark |	否|覆盖标志	1 直接覆盖上条有效分享（默认）（覆盖原有的分享码）;2 直接添加，允许多条并存 ;3 只有分享时间延长了，才允许覆盖上条分享| 
| sharingExpireAt |	否|设备使用到期时间 时间戳（毫秒），表示该分享的设备，被分享人可以使用的时间,如果不填，则为终生有效，只有授权人主动解绑	| 

#### 被分享人接受分享

```java
  public void acceptShareDevice(String shareCode,String deviceName,IHttpCallBack callback);

```
|参数|	是否必传|说明|	
| --- | --- | --- | 
| shareCode |	是| 分享码|
| deviceName |	否| 设备名称|


#### 被分享人取消设备分享

```java
 public void cancelShareByReceiver(String shareCode,IHttpCallBack callback);

```

|参数|	是否必传|说明|	
| --- | --- | --- | 
| shareCode |	是| 分享码|


#### 分享人取消设备分享

```java
public void cancelShareByOwner(String shareCode, IHttpCallBack callback);

```

|参数|	是否必传|说明|	
| --- | --- | --- | 
| shareCode |	是| 分享码|

#### 分享人查询设备的被分享人列表

```java
 public void getDeviceShareUserList(String pk,String dk, IHttpCallBack callback);

```

|参数|	是否必传|说明|	
| --- | --- | --- |
| dk |	是|device key|
| pk |	是|product key|


#### 修改设备信息 deviceName

```java
public void changeDeviceInfo(String deviceName, String pk,String dk, IHttpCallBack callback);

```
|参数|	是否必传|说明|	
| --- | --- | --- |
| deviceName |	是|设备名称|
| dk |	是|device key|
| pk |	是|product key|



#### 被分享人修改分享的设备名称

```java
public void changeShareDeviceName(String deviceName,String shareCode, IHttpCallBack callback);

```

|参数|	是否必传|说明|	
| --- | --- | --- | 
| deviceName |	是|设备名称 | 
| shareCode |	是|分享码 | 


#### 查询物模型 TSL

```java
 public void queryProductTSL(String pk, IHttpCallBack callback);
  // 请调用SDK的 buildModelListContent方法解析出TSL数据结构
   List<ModelBasic> modelBasics = ObjectModelParse.buildModelListContent(jsonArray);
```

#### ~~查询物模型 TSL 有网络走http 没有网络本地缓存~~

```java
  public void queryProductTSLWithCache(Context context, String pk, IHttpCallBack callback);
   // 请调用SDK的 buildModelListContent方法解析出TSL数据结构
  List<ModelBasic> modelBasics = ObjectModelParse.buildModelListContent(jsonArray);
```

|参数|	是否必传|说明|	
| --- | --- | --- |
| context |	是| Context | 
| pk |	是| productKey | 

#### 查询物模型 TSL 有网络走http 没有网络本地缓存

```java
  public void getProductTSLWithCache(String pk, IDeviceTSLCallBack iDeviceTSLCallBack);
```
|参数|	是否必传|说明|	
| --- | --- | --- |
| pk |	是| productKey | 


#### 查询设备业务属性值
例如一个设备定义了一个bool的属性，则可以查到该属性值的true或false，仅对联网设备有效。
```java
 public void queryBusinessAttributes(List<String> codeList, String pk, String dk, List<String> typeList, String gatewayPk, String gatewayDk, IHttpCallBack callback);
 
```

|参数|	是否必传|说明|	
| --- | --- | --- | 
| codeList |	否|要查询的属性标识符; 和查询类型配合使用，如果传null 查询所有属性，同时typeList也传null	| 
| pk |	是|pk|
| dk |	是|dk|
| gatewayPk |	否|网关设备pk 没有传空字符串""	| 
| gatewayDk |	否|网关设备dk	没有传空字符串""| 
| typeList |	否| 查询类型  1 查询设备基础属性  2 查询物模型属性  3 查询定位信息| 


#### 查询设备业务物模型和属性值
查询TLS和属性值。
```java
  public void getProductTSLValueWithProductKey(String productKey, String deviceKey, String gatewayPk, String gatewayDk, List<String> codeList, List<String> typeList, IDeviceTSLModelCallback callback); 
```

|参数|	是否必传|说明|	
| --- | --- | --- | 
| productKey |	是|productKey|
| deviceKey |	是|deviceKey|
| gatewayPk |	否|网关设备pk 没有传空字符串""	| 
| gatewayDk |	否|网关设备dk	没有传空字符串""| 
| codeList |	否|要查询的属性标识符; 和查询类型配合使用，如果传null 查询所有属性，同时typeList也传null	| 
| typeList |	否| 查询类型  1 查询设备基础属性  2 查询物模型属性  3 查询定位信息| 



#### 查询设备升级计划

```
 public void  queryFetchPlan(String productKey,String deviceKey,IHttpCallBack callback);
```

|参数|	是否必传|说明|	
| --- | --- | --- | 
| productKey |	是|设备pk	| 
| deviceKey |	是|设备dk|



#### 上报组件升级状态

```
public void  reportUpgradeStatus(String productKey,String deviceKey,String componentNo,int reportStatus,IHttpCallBack callback);

```

|参数|	是否必传|说明|	
| --- | --- | --- | 
| productKey |	是|pk	| 
| deviceKey |	是|dk|
| componentNo |	是|查询计划返回的 componentNo	| 
| reportStatus |是|升级状态 0-12	| 

#### 添加设备组

```
  public void addDeviceGroup(String name,IHttpCallBack callback);
```

|参数|	是否必传|说明|	
| --- | --- | --- | 
| name |	是| 设备group 的名字	| 


#### 查询设备组详情

```
 public void queryDeviceGroup(String dgid,IHttpCallBack callback);
```

|参数|	是否必传|说明|	
| --- | --- | --- | 
| dgid |	是|设备组的dgid	| 


#### 修改设备组

```
public void updateDeviceGroup(String name,String dgid,IHttpCallBack callback);
```

|参数|	是否必传|说明|	
| --- | --- | --- | 
| name |	是|设备group 的名字	| 
| dgid |	是|设备组的dgid	| 


####  添加设备到设备组中

```
public void addDeviceToGroup(String dgid,List<AddDeviceParam> list, IHttpCallBack callback);
```

|参数|	是否必传|说明|	
| --- | --- | --- | 
| dgid |	是|设备组的dgid	| 
| list |	是|要添加的设备信息列表	| 


#### 查询设备组中的设备列表
```
 public void getGroupDeviceList(String dgid,String pk, int page,int pageSize,IHttpCallBack callback);
```

|参数|	是否必传|说明|	
| --- | --- | --- | 
| dgid |	是| 设备组的dgid	| 
| pk |	是|pk| 
| page |是|数据页码,哪一页	|
| pageSize |是|每页多少条size	|


#### 移除设备组中的设备
```
public void deleteDeviceToGroup(String dgid,List<AddDeviceParam> list,IHttpCallBack callback);
```

|参数|	是否必传|说明|	
| --- | --- | --- | 
| dgid |	是| 设备组ID	| 
| list |	是| 要移除的设备的信息集合列表 | 


#### 查询设备组列表
```
 public void queryDeviceGroupList(int page,int pageSize,IHttpCallBack callback);
```

|参数|	是否必传|说明|	
| --- | --- | --- | 
| page |	是| 页码	| 
| pageSize |是|每页条数size| 



#### 删除设备组
```
  public void deleteDeviceGroup(String dgid,IHttpCallBack callback);
```

|参数|	是否必传|说明|	
| --- | --- | --- | 
| dgid |	是| 设备组ID	| 


#### 根据设备查询设备组列表
```
 public void queryGroupListByDevice(String pk,String dk,IHttpCallBack callback);
```

|参数|	是否必传|说明|	
| --- | --- | --- | 
| pk |	是| pk	| 
| dk |	是|dk	| 

#### 查询不在设备组内的设备列表
```
public void getDeviceListByNotInDeviceGroup(int page, int pageSize, String dgid, IHttpCallBack callback);
```

|参数|	是否必传|说明|	
| --- | --- | --- | 
| dgid |	是| 设备组ID	| 
| page |	是|当前页，默认为第 1 页	| 
| pageSize |	是| 页大小，默认为 10 条	| 


#### 查询网关设备下子设备列表
```
    public void getGatewayChildList(String pk, String dk, int page, int pageSize, IHttpCallBack callback);
```
|参数|	是否必传|说明|	
| --- | --- | --- | 
| pk |	是| product key	| 
| dk |	是| device key	| 
| page |	是|当前页，默认为第 1 页	| 
| pageSize |	是| 页大小，默认为 10 条	| 


#### 获取设备属性数据列表
```
    public void getPropertyDataList(DeviceCodeList deviceCodeList, IHttpCallBack callback);
```
|参数|	是否必传|说明|	
| --- | --- | --- | 
| attributeCode  |	是|  物模型属性标识符	| 
| pk |	是| product key	| 
| dk |	是| device key	|
| startTimestamp  |	是|  开始时间（毫秒时间戳）	| 
| endTimestamp  |	是|  结束时间（毫秒时间戳）	| 
| gatewayDk  |	否|  网关设备的 Device Key	| 
| gatewayPk  |	否|  网关设备的 Product Key	| 
| page  |	否|  当前页，默认为第 1 页	| 
| pageSize  |	否|  页大小，默认为 10 条	| 


#### 获取设备属性图表列表
```
    public void getPropertyChartList(DeviceChartListBean deviceChartListBean, IHttpCallBack callback);
```
|参数|	是否必传|说明|	
| --- | --- | --- | 
| attributeCode  |	是|  物模型属性标识符，查询多个属性时使用英文逗号分隔，最多查询 10 个	| 
| pk |	是| product key	| 
| dk |	是| device key	|
| startTimestamp  |	是|  开始时间（毫秒时间戳）	| 
| endTimestamp  |	是|  结束时间（毫秒时间戳）	| 
| gatewayDk  |	否|  网关设备的 Device Key	| 
| gatewayPk  |	否|  网关设备的 Product Key	|
| countType  |	否| 聚合类型（默认3）: 1-最大值 2-最小值 3-平均值 4-差值 5-总值	| 
| timeGranularity  |	否| 统计时间粒度（默认2）：1-月 2-日 3-小时 4-分钟 5-秒	| 
| timezone  |	否| 时区偏差，格式：±hh:mm| 


#### 获取设备历史轨迹
```
    public void getLocationHistory(TrackBean trackBean, IHttpCallBack callback);
```
|参数|	是否必传|说明|	
| --- | --- | --- |
| pk |	是| product key	| 
| dk |	是| device key	|
| startTimestamp  |	是|  开始时间（毫秒时间戳）	| 
| endTimestamp  |	是|  结束时间（毫秒时间戳）	| 
| gatewayDk  |	否|  网关设备的 Device Key	| 
| gatewayPk  |	否|  网关设备的 Product Key	|
| locateTypes  |	否| 定位类型（默认查询所有类型的定位）：GP/GL/GA/GN/BD/PQ/LBS，查询多种定位时使用英文逗号分隔| 


#### 获取设备属性环比统计数据
```
    public void getPropertyCompare(DeviceCompareBean deviceCompareBean, IHttpCallBack callback);
```
|参数|	是否必传|说明|	
| --- | --- | --- | 
| attributeCode  |	是|  物模型属性标识符	| 
| pk |	是| product key	| 
| dk |	是| device key	|
| startTimestamp  |	是|  开始时间（毫秒时间戳）	| 
| endTimestamp  |	是|  结束时间（毫秒时间戳）	| 
| gatewayDk  |	否|  网关设备的 Device Key	| 
| gatewayPk  |	否|  网关设备的 Product Key	|
| countType  |	否| 聚合类型（默认3）: 1-最大值 2-最小值 3-平均值 4-差值 5-总值	| 
| timeGranularity  |	否| 统计时间粒度，查询多个粒度时使用英文逗号分隔（默认1）：1-日 2-周 3-月 4-年	| 


#### 分享人设置设备组分享信息
```
public void shareGroupInfo(long acceptingExpireAt,String dgid, int coverMark,long sharingExpireAt,IHttpCallBack callback);
```

|参数|	是否必传|说明|	
| --- | --- | --- | 
| dgid |	是|设备组的id	| 
| acceptingExpireAt |	是|分享二维码种子失效时间 时间戳（毫秒）| 
| coverMark |	否| 覆盖标志	1 直接覆盖上条有效分享（默认）（覆盖原有的分享码）;2 直接添加，允许多条并存 ;3 只有分享时间延长了，才允许覆盖上条分享	| 
| sharingExpireAt |	否|设备使用到期时间 时间戳（毫秒），表示该分享的设备，被分享人可以使用的时间,如果不填，则为终生有效，只有授权人主动解绑	| 


#### 被分享人接受设备组分享
```
public void acceptDeviceGroupShare(String shareCode ,IHttpCallBack callback);
```

|参数|	是否必传|说明|	
| --- | --- | --- | 
| shareCode |是| 分享码	| 


#### 被分享人查询接收的设备组详情
```
public void queryAcceptSharedDeviceGroup(String shareCode ,IHttpCallBack callback);
```

|参数|	是否必传|说明|	
| --- | --- | --- | 
| shareCode |是| 分享码	| 


#### 分享人查询设备组的被分享人列表
```
 public void deviceGroupShareUserList(String dgid ,IHttpCallBack callback);
```

|参数|	是否必传|说明|	
| --- | --- | --- | 
| dgid |是| 设备组id	| 



#### 被分享人修改分享的设备组名称

```
  public void shareUserSetDeviceGroupName(String deviceGroupName,String shareCode,IHttpCallBack callback);
```

|参数|	是否必传|说明|	
| --- | --- | --- | 
| deviceGroupName |是| 设备组名称	| 
| shareCode |是| 分享码	| 


#### 被分享人修改分享的设备组中的设备名称

```
   public void shareUserSetDeviceName(String deviceName, String pk,String dk, String shareCode,IHttpCallBack callback);
```
|参数|	是否必传|说明|	
| --- | --- | --- | 
| deviceName |是| 设备名称	| 
| pk |是| pk	| 
| dk |是| dk	| 
| shareCode |是| 分享码	| 

#### 被分享人取消设备组分享

```
   public void shareUserUnshare(String shareCode,IHttpCallBack callback);
```
|参数|	是否必传|说明|	
| --- | --- | --- |
| shareCode |是| 分享码	| 



#### 分享人取消设备组分享

```
   public void owerUserUnshare(String shareCode,IHttpCallBack callback);
```
|参数|	是否必传|说明|	
| --- | --- | --- |
| shareCode |是| 分享码	| 


#### 批量控制设备

```
   public void batchControlDevice(String data, List<BatchControlDevice> deviceList, int cacheTime,int isCache,int isCover,int dataFormat, int type, IHttpCallBack callback);       
```
|参数|	是否必传|说明|	
| --- | --- | --- |
| type |是|  1：透传 2：属性 3：服务	| 
| data |是| 属性bool int float double enum date text" [{\"key\":\"value\"}]"  属性array "[{\"key\":[{\"id\":\"value1\"},{\"id\":\"value2\"}]}]"（id为0） 属性struct  "[{\"key\":[{\"key1\":\"value1\"},{\"key2\":\"value2\"}]}]"    属性array含有struct  "[{\"key\":[{\"id\":[{\"key1\":\"value1\"}]},{\"id\":[{\"key2\":\"value2\"}]}]}]"（id为0）
|
| dataFormat |是|数据类型 1：Hex 2：Text （当 type 为透传时，需要指定 dataFormat） 默认传2	|
| isCache |是| 是否启用缓存 1：启用  2：不启用    http下发要启用, 默认传1	|
| isCover |是| 是否覆盖之前发送的相同的命令 1：覆盖 2：不覆盖，默认不覆盖，启用缓存时此参数有效     默认传 2 不覆盖	|
| cacheTime |是| 缓存时间，单位为秒，缓存时间范围 1-7776000 秒，启用缓存时必须设置缓存时间	|
| deviceList |是| 要控制的设备的列表list	|


#### 添加云端定时

```java
  void addCornJob(CloudTiming timing, IHttpCallBack callBack);     
```
|参数|	是否必传|说明|	
| --- | --- | --- |
| timing |是|  	定时相关参数 | 
| callBack |是| 回调 |

CloudTiming类
|成员|	类型|说明|	
| --- | --- | --- |
| ruleId |String|  规则唯一标识，修改规则实例信息时必填	| 
| productKey |String| 产品 pk |
| deviceKey |String| 设备 dk	|
| type |String|定时任务类型，once: 执行一次，day-repeat: 每天重复，custom-repeat: 自定义重复，multi-section: 多段执行，random: 随机执行，delay: 延迟执行（倒计时）	|
| enabled |boolean| 规则状态，false: 停止，true: 启动，默认: false	|
| dayOfWeek |String| 当 type 为 custom-repeat、multi-section、random 时必填，周一/周二/周三/周四/周五/周六/周日的任意组合，格式为 "1,3,4"，以 "," 分隔	|
| timers |CloudTimingTimer| 定时器列表	|

CloudTimingTimer类
|成员|	类型|说明|	
| --- | --- | --- |
| action |String|  JSON 格式，指定物模型（属性/服务）+ 指定状态	| 
| time |String| 当 type 为 once、day-repeat、custom-repeat、multi-section 时必填，格式为 "HH:mm:ss"，如 "12:00:00" |
| startTime |String|起始时间 当 type 为 random 时必填，格式为 "HH:mm:ss"，如 "12:00:00"	|
| endTime |String|终止时间 当 type 为 random 时必填，且 endTime 必须在 startTime 之后，格式为 "HH:mm:ss"，如 "13:00:00"	|
| delay |long| 当 type 为 delay 时必填，单位为 s|

#### 修改云端定时
```java
  void setCronJob(CloudTiming timing, IHttpCallBack callBack);
```
|参数|	是否必传|说明|	
| --- | --- | --- |
| timing |是|  	定时相关参数 | 
| callBack |是| 回调 |

#### 查询设备下定时任务列表
```java
  void getCronJobList(CloudTimingList timingList, IHttpCallBack callBack);
```
|参数|	是否必传|说明|	
| --- | --- | --- |
| timingList |是| 定时列表请求参数 | 
| callBack |是| 回调 |

CloudTimingList类
|成员|	类型|说明|	
| --- | --- | --- |
| productKey |String| 产品 pk |
| deviceKey |String| 设备 dk	|
| type |String|定时任务类型，不填则查询所有类型；once: 执行一次，day-repeat: 每天重复，custom-repeat: 自定义重复，multi-section: 多段执行，random: 随机执行，delay: 延迟执行（倒计时）	|
| page |int| 分页页码，默认: 1|
| pageSize |int| 分页大小，默认: 10	|

#### 查询定时任务详情
```java
  void getCronJobInfo(String ruleId, IHttpCallBack callBack);
```
|参数|	是否必传|说明|	
| --- | --- | --- |
| ruleId |是| 规则引擎ID | 
| callBack |是| 回调 |

#### 删除定时任务
```java
  void batchDeleteCronJob(BatchDeleteCloudTiming batchDeleteCloudTiming, IHttpCallBack callBack);
```
|参数|	是否必传|说明|	
| --- | --- | --- |
| batchDeleteCloudTiming |是| 规则引擎ID列表 | 
| callBack |是| 回调 |

BatchDeleteCloudTiming类
|成员|	类型|说明|	
| --- | --- | --- |
| ruleIdList |List<String>| 规则引擎ID列表 |

#### 查询产品下定时任务限制数
```java
  void getProductCornJobLimit(String productKey, IHttpCallBack callBack);
```
|参数|	是否必传|说明|	
| --- | --- | --- |
| productKey |是| 产品 pk | 
| callBack |是| 回调 |

#### 用户确认升级计划
```java
  void userConfirmUpgrade(UpgradePlan plan, IHttpCallBack callBack);
```
|参数|	是否必传|说明|	
| --- | --- | --- |
| plan |是| 升级计划参数 | 
| callBack |是| 回调 |

UpgradePlan类
|成员|	类型|说明|	
| --- | --- | --- |
| deviceKey |String| 产品 dk |
| productKey |String| 产品 pk |
| operType |int| 1-马上升级(确认随时升级) 2-预约升级(预约指定时间窗口升级) 3-(取消预约和取消升级) |
| appointEndTime |long| 预约升级结束时间（毫秒时间戳，当操作类型为 2 时必传 |
| appointStartTime |long| 预约升级开始时间（毫秒时间戳，当操作类型为 2 时必传） |

### IIotChannelControl设备控制
该类主要包含设备控制相关，如设备数据下行，监听设备上行数据，底层会根据设备的能力值和当前APP以及设备的环境，自动选择合适的链路进行连接和数据传输。


#### 获取IIotChannelControl对象
```java
IotChannelController.getInstance()
```
#### 设置通道在线状态和上下行数据监听
```java
IotChannelController setListener(IQuecChannelManager.IQuecCallBackListener listener);
```

#### 开启单个通道
需要提前使用MmkvManager.getInstance().put("uid",”****”)，建议用户在登录成功的时候调用下面方法;
```java
UserServiceFactory.getInstance().getService(IUserService.class).queryUserInfo(
       new IHttpCallBack() {
           @Override
           public void onSuccess(String result) {
               UserInfor user = new Gson().fromJson(result, UserInfor.class);
               if(user.getCode()==200)
               {
                   UserInfor.DataDTO userInfor = user.getData();
                   MmkvManager.getInstance().put("uid", userInfor.getUid());
               }
           }

           @Override
           public void onFail(Throwable e) {
               e.printStackTrace();
           }
       }
);
```
```java
public void startChannel(Context context, QuecDeviceModel pkDkModel, QuecIotDataSendMode channelMode);
```
|参数|	是否必传|说明|	
| --- | --- | --- |
| context |是|  context	| 
| pkDkModel |是| 设备Model	|
| channelMode |是| 通道类型，QuecIotDataSendModeAuto：自动选择，QuecIotDataSendModeWifi：wifi通道，QuecIotDataSendModeWS：ws通道，QuecIotDataSendModeBLE：蓝牙通道|

#### 开启多个通道
需要提前使用MmkvManager.getInstance().put("uid",”****”)，建议用户在登录成功的时候调用下面方法;
```java
UserServiceFactory.getInstance().getService(IUserService.class).queryUserInfo(
       new IHttpCallBack() {
           @Override
           public void onSuccess(String result) {
               UserInfor user = new Gson().fromJson(result, UserInfor.class);
               if(user.getCode()==200)
               {
                   UserInfor.DataDTO userInfor = user.getData();
                   MmkvManager.getInstance().put("uid", userInfor.getUid());
               }
           }

           @Override
           public void onFail(Throwable e) {
               e.printStackTrace();
           }
       }
);
```
```java
public void startChannels(Context context, List<QuecDeviceModel> pkDkModels, IQuecChannelManager.IQuecCallBackListener listener);
```
|参数|	是否必传|说明|	
| --- | --- | --- |
| context |是|  context	| 
| pkDkModels |是| 设备Model列表	|
| listener |否| 道在线状态和上下行数据监听|

#### 关闭单个通道
```java
public void closeChannel(String channelId, int type);
```
|参数|	是否必传|说明|	
| --- | --- | --- |
| channelId |是| channelId的值: productKey + "_" + deviceKey	| 
| type |是| 1:关闭wifi；2：关闭WS；3：关闭蓝牙	|

#### 发送数据--读指令
```java
public void readDps(String channelId, List<QuecIotDataPointsModel.DataModel> data, @Nullable QuecIotChannelExtraData extraData, IotResultCallback callback);
```
|参数|	是否必传|说明|	
| --- | --- | --- |
| channelId |是| channelId的值: productKey + "_" + deviceKey	| 
| data |是| dsp数据	|
| extraData |否| 额外的数据	|
| callback |否| 发送结果回调	|

#### 发送数据--写指令
```java
public void writeDps(String channelId, List<QuecIotDataPointsModel.DataModel> data, @Nullable QuecIotChannelExtraData extraData, IotResultCallback callback);
```
|参数|	是否必传|说明|	
| --- | --- | --- |
| channelId |是| channelId的值: productKey + "_" + deviceKey	| 
| data |是| dsp数据	|
| extraData |否| 额外的数据	|
| callback |否| 发送结果回调	|

#### 获取当前通道状态
```java
public int getOnlineState(String channelId);
```
|参数|	是否必传|说明|	
| --- | --- | --- |
| channelId |是| channelId的值: productKey + "_" + deviceKey	| 

#### 获取蓝牙开关状态
```java
public int getBleState();
```

#### 关闭所有通道
```java
public void closeChannelAll();
```

#### 移除设备-删除通道
```java
public void removeDeviceChannel(String channelId);
```
|参数|	是否必传|说明|	
| --- | --- | --- |
| channelId |是| channelId的值: productKey + "_" + deviceKey	| 

#### 用Http写DPS数据
```java
public void writeDpsByHttp(List<QuecIotDataPointsModel.DataModel<Object>> list, List<BatchControlDevice> deviceList, int type, DpsHttpExtraDataBean extraDataBean,
                               QuecCallback<BatchControlModel> callback);
```
|参数|	是否必传|说明|	
| --- | --- | --- |
| list |是| dps数据	| 
| deviceList |是| 设备列表	| 
| type |是| 类型 1：透传 2：属性 3：服务	| 
| extraDataBean |否| { dataFormat 数据类型 1：Hex 2：Text（当type为透传时，需要指定 dataFormat） cacheTime 缓存时间，单位为秒，缓存时间范围 1-7776000 秒，启用缓存时必须设置缓存时间 isCache 是否启用缓存 1：启用 2：不启用，默认不启用 isCover 是否覆盖之前发送的相同的命令 1：覆盖 2：不覆盖，默认不覆盖，启用缓存时此参数有效
**查看接口定义 }	|
| callback |否| 结果回调	| 

#### 设置正在连接状态监听
```java
public void setConnectingStateListener(@NonNull QuecDeviceModel deviceModel, OnChannelConnectingStateChange change);
```
|参数|	是否必传|说明|	
| --- | --- | --- |
| deviceModel |是| 设备Model | 
| change |是| 状态回调 |

#### 移除正在连接状态监听
```java
public void removeConnectingStateListener(@NonNull QuecDeviceModel deviceModel, OnChannelConnectingStateChange change);
```
|参数|	是否必传|说明|	
| --- | --- | --- |
| deviceModel |是| 设备Model | 
| change |是| 状态回调 |

#### 获取正在连接状态监听
```java
public int getDeviceConnectingState(@NonNull QuecDeviceModel model);
```
|参数|	是否必传|说明|	
| --- | --- | --- |
| model |是| 设备Model | 

#### 移除通道在线状态和上下行数据监听
```java
public void removeListener(IQuecChannelManager.IQuecCallBackListener listener);
```
|参数|	是否必传|说明|	
| --- | --- | --- |
| listener |是| 通道监听listener | 

#### ble设备时间同步
```java
void timeZoneSync(String pk, String dk, IotResultCallback callback);
```
|参数|	是否必传|说明|	
| --- | --- | --- |
| pk |是| 设备pk | 
| dk |是| 设备dk | 
| callback |是| 时间同步结果回调 | 


### IWebSocketService服务
#### 获取Service对象
```
WebSocketServiceLocater.getService(IWebSocketService.class)
```

#### websocket 登录

```
  public void login();
  
```

#### 断开释放

```
  public void disconnect();
  
```


#### 订阅设备

```
   public void subscribeDevice(String deviceKey,String productKey);
  
```


#### 取消设备订阅

```
 public void unsubscribeDevice(String deviceKey,String productKey);
  
```
#### 获取websocket是否开启连接

```
  public boolean  isWebSocketOpenCallback();
  
```

####  获取websocket是否登录成功

```
  public boolean  isWebSocketLoginCallback();
  
```


#### 下发基本类型数据  bool/int/float/double/enum/date/text

```
   public void writeWebSocketBaseData(KValue value, String deviceKey, String productKey);
  
     KValue data = new KValue(3,"buer", ModelStyleConstant.BOOL,"true");
     WebSocketServiceLocater.getService(IWebSocketService.class).writeWebSocketBaseData(data,dk,pk);
  
```


#### 下发数组或者结构体类型数据

```
     public void writeWebSocketArrayOrStructBaseData(int id, String name, List<KValue> mListChild,String dataType, String deviceKey, String productKey);
   /**
      * 参数id为 物模型属性id
      *  name 物模型 name
      *  下发 数组类型 内容为基本类型   INT FLOAT  DOUBLE   TEXT
      *   mListChild 内容为下面类型的KValue  id=0  name="" 第三个参数为类型，第四个参数为数组的值:
      *   布尔值可以传 "false" "true"
      *    KValue  v1 = new KValue(0,"",ModelStyleConstant.INT,8);
      *    KValue  v2 = new KValue(0,"",ModelStyleConstant.INT,10);
      *     mListChild.add(v1);
      *      mListChild.add(v2);
      *     dataType 数组  ModelStyleConstant.ARRAY  结构体   ModelStyleConstant.STRUCT
      *
      *  下发类型为Struct
      *  第一个参数为Struct中属性模型id  第三个参数为类型  第四个参数为属性值
      *        KValue  v1 = new KValue(1,"是否刷新",ModelStyleConstant.BOOL,"true");
      *         KValue  v2 = new KValue(2,"时间duration",ModelStyleConstant.INT,55);
      *  mListChild.add(v1);
      *   mListChild.add(v2);
      */
    
```


#### 下发数组类型 内容为结构体

```
    public void writeWebSocketArrayContainStructData(int id, String name, List<KValue> mListChild, String deviceKey, String productKey);
    /**
      * 
      *  mListChild内容下面的 KValue 值
      *   KValue  v1 = new KValue(0,"",ModelStyleConstant.STRUCT,ChildList1);
      *   KValue  v2 = new KValue(0,"",ModelStyleConstant.STRUCT,ChildList2);
      *        mListChild.add(v1);
      *    参数ChildList1的值 为每个结构体中的属性值封装
      *        KValue  v11 = new KValue(1,"测试1",ModelStyleConstant.BOOL,"true");
      *         KValue  v12 = new KValue(2,"测试2",ModelStyleConstant.ENUM,1);
      *         List<KValue> ChildList1 = new ArrayList<KValue>();
      *        ChildList1.add(v11);
      *        ChildList1.add(v12);
      */
     
```
## MQTT通道SDK

### 一、功能列表

|功能	|功能说明	|实现版本	|微服务版本号|
| --- | --- | --- | --- |
|MQTT通信相关	| 扫描外设、连接外设、发送数据给外设、接受外设上传数据 |	1.0.0	| |


### 二、设计接口/属性


#### 判断MQTT是否已经连接
```java
public boolean isConnected() 
```

#### 是否自动重连
```java
public void setAutoReconnect(boolean isAutoReconnect)
```
|参数|	是否必传|说明|	
| --- | --- | --- |
|isAutoReconnect|是|ture是自动重连，false是不自动重连|

#### 连接MQTT
需要提前使用MmkvManager.getInstance().put("uid",”****”)，建议用户在登录成功的时候调用下面方法;
```java
UserServiceFactory.getInstance().getService(IUserService.class).queryUserInfo(
       new IHttpCallBack() {
           @Override
           public void onSuccess(String result) {
               UserInfor user = new Gson().fromJson(result, UserInfor.class);
               if(user.getCode()==200)
               {
                   UserInfor.DataDTO userInfor = user.getData();
                   MmkvManager.getInstance().put("uid", userInfor.getUid());
               }
           }

           @Override
           public void onFail(Throwable e) {
               e.printStackTrace();
           }
       }
);
```
```java
public void connect()
```
连接MQTT，默认开启自动重连。 如果不需要自动重连，可以调用setAutoReconnect(false)

#### 重连MQTT
```java
public void reconnect() 
```

#### 订阅设备
```java
public void subscribe(String productKey, String deviceKey)
```
|参数|	是否必传|说明|	
| --- | --- | --- |
|productKey|是|设备productKey|
|deviceKey|是|设备deviceKey|
MQTT连接成功后，调用才生效

#### 取消订阅设备
```java
public void unsubscribe(String productKey, String deviceKey)
```
|参数|	是否必传|说明|	
| --- | --- | --- |
|productKey|是|设备productKey|
|deviceKey|是|设备deviceKey|

#### 发送消息
```java
 public void sendMessage(QuecMqttMessageModel messageModel)
```
|参数|	是否必传|说明|	
| --- | --- | --- |
| messageModel |是| QuecMqttMessageModel数据 |

##### class QuecMqttMessageModel
|成员|	类型|说明|	
| --- | --- | --- |
| productKey | String | 设备productKey|
| deviceKey | String | 设备deviceKey|
| type | String | 消息类型 READ-ATTR 物模型属性-读；WRITE-ATTR 物模型属性-写；EXE-SERV 调用物模型服务；EXE-SERV2 调用物模型服务(数据协议 > 1.8)；RAW 透传命令|
| kv | String | 物模型下发数据|
| msgId | long | 标识发送指令的消息ID，非必填，最大长度20。用于发送响应时对应|
| isCache | boolean | 下发指令是否缓存|
| cacheTime | long | 下发指令缓存时间，单位秒|
| isCover | boolean | 下发指令是否覆盖，默认false|

数据示例
```
透传（RAW）

{
        "isCache": true,
        "cacheTime": 3600,
        "dataFormat": "Text",
        "deviceKey": "866123456789015",
        "isCache": true,
        "productKey": "p12345",
        "raw": "123456",
        "type": "RAW"
}
物模型属性读（READ-ATTR）

{
        "deviceKey": "1234567890",
        "kv": "[\"power\"]",
        "productKey": "p12345",
        "type": "READ-ATTR"
}
物模型属性写（WRITE-ATTR）

{
        "deviceKey": "1234567890",
        "kv": "[{\"power\": \"true\"}]",
        "productKey": "p12345",
        "type": "WRITE-ATTR"
}
物模型服务调用（EXE-SERV）

{
        "productKey": "p12345",
        "deviceKey": "1234567890",
        "type": "EXE-SERV",
        "dataFormat": "Text",
        "kv": "[{\"serv\": [{\"power\":\"false\"}]}]"
}
物模型服务调用（EXE-SERV2）

{
        "productKey": "p12345",
        "deviceKey": "1234567890",
        "type": "EXE-SERV2",
        "dataFormat": "Text",
        "kv": "[{\"serv\": [{\"power\":\"false\"}]}]"
}
```


#### 发送ttlv数据
```java
    public void sendTtlvData(String productKey, String deviceKey, byte[] ttlvData) 
```
|参数|	是否必传|说明|	
| --- | --- | --- |
|productKey|是|设备productKey|
|deviceKey|是|设备deviceKey|
|ttlvData|是|编码后的TTLV数据|

#### 设置MQTT监听的listene
```java
public void setListener(QuecMqttListener listener)
```
##### interface QuecMqttListener

```java
public interface QuecMqttListener {

    /**
     * 连接成功
     */
    void onConnected();

    void onConnectFailed(Throwable e);

    /**
     * 连接已经断开
     */
    void onDisconnect();

    /**
     * 设备订阅成功
     *
     * @param productKey 设备pk
     * @param deviceKey  设备dk
     */
    void onSubscribed(String productKey, String deviceKey);

    /**
     * 设备订阅失败
     *
     * @param productKey 设备pk
     * @param deviceKey  设备dk
     */
    void onSubscribedFail(String productKey, String deviceKey, Throwable e);

    /**
     * 数据接收
     *
     * @param productKey 设备pk
     * @param deviceKey  设备dk
     * @param isTtlv     ture是ttlv二进制数据，false是json string数据
     * @param data       数据
     */
    void onData(String productKey, String deviceKey, boolean isTtlv, byte[] data);
}
```

#### 断开连接
```java
public void disconnect()
```

## 蓝牙通道SDK

### 一、功能列表

|功能	|功能说明	|实现版本	|微服务版本号|
| --- | --- | --- | --- |
|蓝牙通信相关	| 扫描外设、连接外设、发送数据给外设、接受外设上传数据 |	1.0.0	| |


### 二、设计接口/属性


### IBleService 蓝牙服务

#### 获取服务Service对象
```
BleServiceLocater.getService(IBleService.class)

```

#### 扫描设备  可传入name  MAC地址过滤  不过滤传""
```
   public void startScan(String name, String mac, IScanCallBack iScanCallBack) 

```


#### 停止扫描
```
public void stopScan();
```
#### 根据设备mac 连接设备
```
    public void  connectDevice(String mac,IBleCallBack callBack);
    BleServiceLocater.getService(IBleService.class).connectDevice(macAddress, new IBleCallBack() {
                    @Override
                    public void onSuccess() {
                        SystemClock.sleep(300);
                        //连接成功 设置notify 监听数据
                        BleServiceLocater.getService(IBleService.class).setNotify(NOTIFY_CHARACTERISTIC_UUID, new IFeedbackCallBack() {
                            @Override
                            public void receiveData(byte[] data) {
                                String notifyContent =  StringUtils.bytesToHex(data,true);
                                System.out.println("receiveData--:"+notifyContent);

                            }
                            @Override
                            public void onFail(Throwable e) {
                                    e.printStackTrace();
                            }
                        });
                    }
                    @Override
                    public void onFail(Throwable e) {
                            e.printStackTrace();
                    }
                });

```

#### 断开连接
```
 public void  disConnect();

```

#### 释放资源  Activity onStop 方法调用
```
  public void release();

```

#### 连接后 发现设备服务
```
  public void findServiceCharacter(IFindServiceCallBack findServiceCallBack);
  public interface IFindServiceCallBack {
    public void onScan(String UUID,FindCharacter findCharacter);
    public void onFail(Throwable throwable);
}
```

#### setNotify or setIndicate
```
  public void setNotify(String notify_UUID, IFeedbackCallBack iFeedbackCallBack);
  public void setIndicate(String indicate_UUID, IFeedbackCallBack iFeedbackCallBack);
  public interface IFeedbackCallBack {
    public void receiveData(byte[] data);
    public void onFail(Throwable throwable);
}
```

#### 读属性
```
  public void read(String read_UUID, IFeedbackCallBack iFeedbackCallBack);
   BleServiceLocater.getService(IBleService.class).read(READ_UUID, new IFeedbackCallBack() {
                    @Override
                    public void receiveData(byte[] data) {
                        String str =    new String(data, StandardCharsets.UTF_8);
                        System.out.println("read-str--:"+str);
                    }
                    @Override
                    public void onFail(Throwable throwable) {

                    }
                });
  
  
```

#### 向设备写数据
```
  public void write(String write_UUID, byte[] data, IFeedbackCallBack iFeedbackCallBack);
  
  BleServiceLocater.getService(IBleService.class).write(WRITE_UUID, byte[] data, new IFeedbackCallBack() {
                    @Override
                    public void receiveData(byte[] data) {
                        System.out.println("write--success");
                    }
                    @Override
                    public void onFail(Throwable throwable) {

                    }
                });
  
```

#### 设备是否已经连接 已经连接返回true
```
   public boolean isConnected(String mac);
```


#### 设置设备连接状态回调
```
   public void setiConnectChange(IConnectChange iConnectChange);
```


#### 根据 uuid判断  该uuid是否支持 notify
```
    public void isNotifiable(String uuid,INotifyCallBack iNotifyCallBack);
```


#### 根据 uuid判断  该uuid是否支持 indicate
```
    public void isIndicatable(String uuid,IndicateCallBack indicateCallBack);
```


### TTLV 格式数据的编码与解码

### ttlv data
```java
  public  class TTLVData<T>  {
    /**
     * 数据类型
     *  type   布尔  0 false  1 true   2数值  3二进制  4结构体
     */
    //id 数据标识
    public short id;
    public short type;
    public T data;
    //是否是 ttlv
    public boolean ttlv;
```

### ttlv 编码
```
        public  EncodeResult startEncode(short cmd,List<TTLVData> payloads)
        TTLVData<Boolean> test1 = new TTLVData<Boolean>((short)1, (short)0, false, true) ;
        TTLVData<Boolean> test2 = new TTLVData<Boolean>((short)2, (short)1, true, true) ;
        …………
        List<TTLVData> payLoads = new ArrayList<TTLVData>();
        payLoads.add(test1);
        payLoads.add(test2);
        //ttlv 格式数据 编码 解码
        EncodeResult encodeResult = EncodeTools.getInstance().startEncode((short) 0x0018, payLoads);
        
```
### 解码ttlv 包
```
  DecodeTools.getInstance().packetSlice(encodeResult.getCmdData(), new IParseDataListener() {
            @Override
            public void onSuccess(ParseResultData resultData) {
                Map<Integer, ReceiveTTLVData> paramMap = resultData.getValueMap();
                Set<Map.Entry<Integer, ReceiveTTLVData>> me = paramMap.entrySet();
                for (Iterator<Map.Entry<Integer, ReceiveTTLVData>> it = me.iterator(); it.hasNext();)
                {
                    Map.Entry<Integer,ReceiveTTLVData> mapValue = it.next();
                    int key =  mapValue.getKey();
                    ReceiveTTLVData useValue = mapValue.getValue();
                    String dataType = useValue.getData().getClass().getSimpleName();
                    switch (dataType)
                    {
                        case  DataStyle.BYTE:
                            byte[] data1 = (byte[]) useValue.getData();
                            String str =  new String(data1, StandardCharsets.UTF_8);
                            System.out.println("byte str --:" + str);
                            break;
                        case  DataStyle.LONG:
                            long data2 = (Long) useValue.getData();
                            break;
                        case  DataStyle.DOUBLE:
                            double data3 = (double) useValue.getData();
                            break;

                        case  DataStyle.BOOLEAN:
                            boolean data4 = (boolean) useValue.getData();
                            break;
                            
                          case  DataStyle.Array:
                        List<ReceiveTTLVData> data5 = (List<ReceiveTTLVData>) useValue.getData();
                        for(ReceiveTTLVData rt: data5)
                        {
                            String style = rt.getData().getClass().getSimpleName();
                            if(style.equals(DataStyle.BYTE))
                            {
                                byte[] data = (byte[]) rt.getData();
                                String content =  new String(data, StandardCharsets.UTF_8);
                                System.out.println("byte--:" + content);
                            }
                            System.out.println("ReceiveTTLVData-:"+rt);
                        }
                        break;
                    }

                }


            }
            @Override
            public void onProcessing(String msg) {
                System.out.println("onProcessing--:"+msg);

            }
        });
 
 
 
```

### ~~wifi配网~~（已过时，使用IQuecDevicePairingService）
```
1 Ble 扫描连接上蓝牙设备;
2 wifi名称ssid和密码pass,编码ttlv格式,ble write数据
3 解码设备返回的pk,dk,authCode;
4 调用http接口bindDeviceByWifi 绑定设备;
example:
    byte[] bytes = ssid.getBytes(StandardCharsets.UTF_8);
    byte[] bytes2 = pass.getBytes(StandardCharsets.UTF_8);
    TTLVData<byte[]> test1 = new TTLVData<byte[]>((short) 1, (short)3, bytes, true) {
    };
    TTLVData<byte[]> test2 = new TTLVData<byte[]>((short) 2, (short)3, bytes2, true) {
    };
    List<TTLVData> list = new ArrayList<TTLVData>();
    list.add(test1);
    list.add(test2);
    //ttlv 数据编码
    EncodeResult encodeResult = EncodeTools.getInstance().startEncode((short) 0x7010, list);
    //wifi 配网 写入数据
    BleServiceLocater.getService(IBleService.class).write(writeUUID, encodeResult.getCmdData(), new IFeedbackCallBack()
    …………
    蓝牙notify 解码数据…… 调接口配网
    DeviceServiceFactory.getInstance().getService(IDevService.class).bindDeviceByWifi(String deviceName, String pk,String dk, String authCode,IHttpCallBack callback);
    
```


## 配网sdk  quec-smart-config-sdk-api

### 一、功能列表
|功能	|功能说明	|实现版本	|微服务版本号|
| --- | --- | --- | --- |
|	设备配网| 设备配网 |	1.0.0	| |
|	注册配网监听|  |	1.0.0	| |
|	注销配网监听|  |	1.0.0	| |

### 二、~~IQuecSmartConfigService设计接口/属性~~（已过时，使用IQuecDevicePairingService）

#### ~~开始配网~~（已过时）
```java
@Deprecated
public void startConfigDevices(@NonNull List<DeviceBean> list, @NonNull String ssid, @NonNull String password)
``` 
|参数|	是否必传|说明|	
| --- | --- | --- |
| list |是| 扫描到的设备列表	| 
| ssid |是| ssid	| 
| password |是| password	| 

#### ~~注册配网监听~~（已过时）

```java
  @Deprecated
   public void addSmartConfigListener(QuecSmartConfigListener listener)
```

#### ~~注销配网监听~~（已过时）

```java
  @Deprecated
  public void removeSmartConfigListener(QuecSmartConfigListener listener) 
```
### 三、QuecDevicePairingServiceManager


#### 初始化
```kotlin
  fun init(context: Context)
```
|参数|	是否必传|说明|	
| --- | --- | --- |
|context|是|Context|

#### 扫描设备

扫描结果参考QuecPairingListener

```kotlin
  fun scan(fid: String?, name: String?, mac: String?)
```
|参数|	是否必传|说明|	
| --- | --- | --- |
|fid|否|家庭id|
|name|否|蓝牙名称|
|mac|否|蓝牙mac地址|

#### 停止扫描

```kotlin
  fun stopScan()
```
#### 开始配对设备

配网进度和结果参考QuecPairingListener

```kotlin
  fun startPairingByDevices(
        devices: MutableList<QuecPairDeviceBean>?, fid: String?, ssid: String?, pw: String?
    )
```
|参数|	是否必传|说明|	
| --- | --- | --- |
|devices|是|待绑定设备|
|fid|否|家庭id|
|ssid|否|WiFi名称|
|pw|否|WiFi密码|


#### 取消所有设备配对

```kotlin
  fun cancelAllDevicePairing()
```

#### 设置WiFi配网超时时间

```kotlin
  fun setWiFiPairingDuration(duration: Int): Boolean
```
|参数|	是否必传|说明|	
| --- | --- | --- |
|duration|是|60~120,默认120秒，单位：秒|

##- return true:设置成功，false:设置失败

#### 设置Ble配对超时时间

```kotlin
  fun setBlePairingDuration(duration: Int): Boolean
```
|参数|	是否必传|说明|	
| --- | --- | --- |
|duration|是|30~60,默认60秒，单位：秒|


#### 添加配网监听

```kotlin
  fun addPairingListener(listener: QuecPairingListener?)
```
|参数|	是否必传|说明|	
| --- | --- | --- |
|listener|是|结果回调（扫描设备回调、配网进度和结果）|

#### 移除配网监听

```kotlin
  fun removePairingListener(listener: QuecPairingListener?)
```
|参数|	是否必传|说明|	
| --- | --- | --- |
|listener|是|结果回调）|

#### QuecPairingListener接口

```kotlin
  interface QuecPairingListener {

    /**
     * 扫描到设备
     * @param deviceBean 设备信息
     */
    fun onScanDevice(deviceBean: QuecPairDeviceBean)
    /**
     * 更新配对进度
     * @param deviceBean 设备信息
     * @param progress 进度
     */
    fun onUpdatePairingStatus(deviceBean: QuecPairDeviceBean, progress: Float)

    /**
     * 配网结果
     * @param deviceBean 设备信息
     * @param result 配网结果
     * @param errorCode 错误码
     */
    fun onUpdatePairingResult(deviceBean: QuecPairDeviceBean, result: Boolean, errorCode: QuecPairErrorCode)
}
```

QuecPairDeviceBean类
|成员|	类型|说明|	
| --- | --- | --- |
| bleDevice | QuecBleDevice | 扫描的BLE设备对象|
| deviceName | String | 设备名称|
| productName | String | 产品名称|
| productLogo | String | 产品LOGO |
| bindingMode | int | 设备绑定模式绑 多绑：1 唯一：2 轮流：3|

QuecBleDevice类
|成员|	类型|说明|	
| --- | --- | --- |
| id | String |设备唯一标志|
| version | String | 固件版本|
| productKey | String | 设备pk|
| deviceKey | String | 设备dk |
| mac | String | 蓝牙mac地址|
| isWifiConfig | Boolean | wifi 设备是否已配网，1 表示已配网，0 表示未配网|
| isBind | Boolean | 是否已绑定|
| isEnableBind | String | 是否允许绑定|
| capabilitiesBitmask | Int | 设备能力值 bit0=1 表示设备支持 WAN 远场通讯能力 bit1=1 表示设备支持 WiFi LAN 近场通讯能力 bit2=1 表示设备支持 BLE 近场通讯能力|

QuecPairErrorCodes说明
|类型|	值|说明|	
| --- | --- | --- |
|QUEC_PAIRING_WAITING|301|设备待绑定|	
|QUEC_PAIRING_BLE_CONNECTING|302|蓝牙连接中|	
|QUEC_PAIRING_BLE_CONNECTED_FAIL|303|蓝牙连接失败|	
|QUEC_PAIRING_WIFI_GET_BINDING_CODE_FAIL|304|WiFi配网设备，超时未获取到bindingcode|	
|QUEC_PAIRING_WIFI_BINDING_SUCCESS|305|WiFi配网成功|	
|QUEC_PAIRING_WIFI_BINDING_FAIL|306|WiFi配网失败|	
|QUEC_PAIRING_BLE_GET_RANDOM_FAIL|307|向蓝牙设备询问random失败|	
|QUEC_PAIRING_BLE_GET_ENCRYPTION_CODE_FAIL|308|向云端请求加密bindingcode失败|	
|QUEC_PAIRING_BLE_CODE_AUTH_FAIL|309|向蓝牙设备认证失败|	
|QUEC_PAIRING_BLE_CODE_AUTH_SUCCESS|310|向设备认证成功|	
|QUEC_PAIRING_BLE_BINDING_SUCCESS|311|蓝牙绑定成功|	
|QUEC_PAIRING_BLE_BINDING_FAIL|312|蓝牙绑定失败|	
|QUEC_PAIRING_FAIL|313|通用异常场景：绑定失败, 如入参问题等|	


## OTA sdk

### Http OTA

#### 获取服务Service对象
```kotlin
  QuecHttpOtaServiceFactory.getInstance().getService(IQuecHttpOtaService::class.java)
```

#### 查询用户是否有可升级的设备

```java
  void getUserIsHaveDeviceUpgrade(String fid, IHttpCallBack callBack);
```
|参数|	是否必传|说明|	
| --- | --- | --- |
| fid |是| 家庭id | 
| callBack |是| 回调 |

#### 查询待升级设备列表
```java
  void getUpgradePlanDeviceList(String fid, int page, int pageSize, IHttpCallBack callBack);
```
|参数|	是否必传|说明|	
| --- | --- | --- |
| fid |是| 家庭id | 
| page |是| 要查询的列表页，默认为 1 | 
| pageSize |是| 要查询的页大小，默认 10 | 
| callBack |是| 回调 |

#### 查询设备升级计划
```java
  void getDeviceUpgradePlan(String dk, String pk, IHttpCallBack callBack);
```
|参数|	是否必传|说明|	
| --- | --- | --- |
| dk |是| 产品 dk | 
| pk |是| 产品 pk | 
| callBack |是| 回调 |

#### 批量确认升级
```java
  void userBatchConfirmUpgradeWithList(List<UpgradePlan> list, IHttpCallBack callBack);
```
|参数|	是否必传|说明|	
| --- | --- | --- |
| list |是| 升级计划参数的List | 
| callBack |是| 回调 |

UpgradePlan类
|成员|	类型|说明|	
| --- | --- | --- |
| version | String | 固件版本|
| productKey | String | 设备pk|
| deviceKey | String | 设备dk |
| operType | int | 1-马上升级(确认随时升级) 2-预约升级(预约指定时间窗口升级) 3-(取消预约和取消升级)|
| appointStartTime | long | 预约升级开始时间（毫秒时间戳，当操作类型为 2 时必传）|
| appointEndTime | long | 预约升级结束时间（毫秒时间戳，当操作类型为 2 时必传）|
| planId | long | 升级计划的ID |


#### 批量查询设备升级详情
```java
  void getBatchUpgradeDetailsWithList(List<UpgradeDeviceBean> list, IHttpCallBack callBack);
```
|参数|	是否必传|说明|	
| --- | --- | --- |
| list |是| 升级计划的List | 
| callBack |是| 回调 |

UpgradeDeviceBean类
|成员|	类型|说明|	
| --- | --- | --- |
| deviceKey |String| 产品 dk |
| productKey |String| 产品 pk |
| planId |long| 升级计划的ID|


### 蓝牙OTA QuecBleOtaManager

#### 查询单个设备升级计划

```kotlin
  fun checkVersion(pk: String, dk: String, callback: QuecCallback<QuecBleOtaInfo?>)
```
|参数|	是否必传|说明|	
| --- | --- | --- |
| pk | 是 | 设备pk |
| dk | 是 | 设备dk |
|callback|是|结果回调|

QuecBleOtaInfo类
|成员|	类型|说明|	
| --- | --- | --- |
| pk | String | 设备pk|
| dk | String | 设备dk |
| targetVersion | String | 新版本的版本号 |
| componentNo | String | 组件号 |
| desc | String | 升级说明 |
| fileName | String | 文件名|
| fileUrl | String | 文件下载地址 |
| fileSize | Int | 文件大小|
| fileSign | String | 文件Hash256值 |
| planId | Int | 升级计划ID|

#### 升级状态回调接口

```kotlin
   fun addStateListener(listener: StateListener?)
```
|参数|	是否必传|说明|	
| --- | --- | --- |
| listener | 是 | 升级状态回调接口, 当OTA升级成功或者失败时, 触发回调 |

```kotlin
   fun addProgressListener(listener: ProgressListener?)
```
|参数|	是否必传|说明|	
| --- | --- | --- |
| listener | 是 | 升级进度回调接口, 当OTA升级时, 回调进度, 范围: 0~1 |

#### 开始OTA升级

```kotlin
   fun startOta(infoList: List<QuecBleOtaInfo>)
```
|参数|	是否必传|说明|	
| --- | --- | --- |
| infoList | 是 | QuecBleOtaInfo在查询升级计划时获 |


#### 停止OTA升级

```kotlin
   fun stopOta(infoList: List<QuecBleOtaInfo>)
```
|参数|	是否必传|说明|	
| --- | --- | --- |
| infoList | 是 | QuecBleOtaInfo在查询升级计划时获 |

#### ProgressListener接口

```kotlin
   fun interface ProgressListener {
        /**
        * 升级成功
        * @param pk 设备pk
        * @param dk 设备dk
        * @param progress 升级进度，范围是0.0~1.0
        */
        fun onUpdate(pk: String, dk: String, progress: Double)
    }
```

#### StateListener接口
```kotlin
   interface StateListener {

        /**
        * 升级成功
        * @param pk 设备pk
        * @param dk 设备dk
        * @param waitTime 等待设备升级成功需要的时间
        */
        fun onSuccess(pk: String, dk: String, waitTime: Long)

        /**
        * 升级成功
        * @param pk 设备pk
        * @param dk 设备dk
        * @param errorCode 升级失败错误码
        */
        fun onFail(pk: String, dk: String, errorCode: BleFileErrorType)
    }
```

BleFileErrorType说明
|类型|说明|	
| --- | --- |
|COMMON|通用错误|	
|NOT_CONNECT|蓝牙未连接|	
|NO_FILE_PATH|升级文件路径不存在|	
|FILE_CHECK_FAIL|升级文件校验失败|	
|DEVICE_REFUSE|设备拒绝升级|	
|DEVICE_CANCELLED|设备取消升级|	
|DEVICE_FAIL|设备升级失败|	
|TIMEOUT|升级超时|	