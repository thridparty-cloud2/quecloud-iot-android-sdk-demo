
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
| phone |	是|手机号码| 
| internationalCode |	否|国际代码，默认为国内,传"86"| 
| type |是| 1: 注册验证码, 2: 密码重置验证码, 3: 登录验证码代码 , 4:注销账号| 
| ssid |否|短信签名,传入用自己的,不传默认""	| 
| stid |否|短信模板，传入用自己的,不传默认""	| 


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
 默认重置为 12345678
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
 public void phoneIsRegister(String phone,IHttpCallBack callback);

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
| isDisabled |是| 验证码验证后是否失效，1：失效 2：不失效，默认 1 | 



#### 获取用户token

```
  public String getToken();

```

#### 清除本地 token

```
 public void clearToken();

```

|http响应码|	value|说明|	
| --- | --- | --- | 
| code |200|成功	| 
| code |5032|token_invalid,code返回5032,请退出当前页面,主动跳转到登录页面重新获取token|
| code |5106|请输入token,用户没有登录直接调用接口返回此内容	|


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
```
DeviceServiceFactory.getInstance().getService(IDevService.class)

```

#### 使用SN绑定设备
```
 public void bindDeviceSn(String pk,String sn,String deviceName , IHttpCallBack callback);
```

|参数|	是否必传|说明|	
| --- | --- | --- | 
| pk |	是|设备pk	| 
| sn |	是|设备sn	| 
| deviceName |	否| 默认不传或""	| 


#### 查询设备信息

```
 public void queryDeviceInfo(String pk,String dk,String shareCode,IHttpCallBack callback);

```
|参数|	是否必传|说明|	
| --- | --- | --- | 
| pk |	是|Product Key，配合 dk，和分享码二选一	| 
| dk |	是|Device Key，配合 pk，和分享码二选一|
| shareCode |否|分享码，和 pk、dk 二选一，被分享人使用分享码查询设备信息	|


#### pk dk  解绑设备

```
 public void unBindDevice(String pk,String dk,IHttpCallBack callback);

```

|参数|	是否必传|说明|	
| --- | --- | --- | 
| pk |	是|设备productKey	| 
| dk |	是|设备DeviceKey	| 

#### 查询用户设备列表

```
public void queryUserDeviceList(int page,int pageSize,IHttpCallBack callback);

```
|参数|	是否必传|说明|	
| --- | --- | --- | 
| page |	是|分页查询当前页	| 
| pageSize |是|每页多少条|


#### 蓝牙设备绑定

```
 public void bindDeviceByBlueTooth(String authCode,String pk, String dk,String pwd,IHttpCallBack callback);

```

|参数|	是否必传|说明|	
| --- | --- | --- | 
| authCode |是|设备authCode| 
| pk |	是|product key|
| dk |	是|device key|
| pwd |	是|设备密码	| 


#### wifi设备绑定

```
 public void bindDeviceByWifi(String deviceName, String pk,String dk, String authCode,IHttpCallBack callback);

```

|参数|	是否必传|说明|	
| --- | --- | --- | 
| deviceName |否|默认不传或""|
| dk |	是|device key|
| pk |	是|product key|
| authCode |是|设备返回的authCode	| 


#### 分享人设置设备分享信息

```
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

```
 public void acceptShareDevice(String shareCode,IHttpCallBack callback);

```
|参数|	是否必传|说明|	
| --- | --- | --- | 
| shareCode |	是| 分享码|


#### 被分享人取消设备分享

```
 public void cancelShareByReceiver(String shareCode,IHttpCallBack callback);

```

|参数|	是否必传|说明|	
| --- | --- | --- | 
| shareCode |	是| 分享码|


#### 分享人取消设备分享

```
public void cancelShareByOwner(String shareCode, IHttpCallBack callback);

```

|参数|	是否必传|说明|	
| --- | --- | --- | 
| shareCode |	是| 分享码|

#### 分享人查询设备的被分享人列表

```
 public void getDeviceShareUserList(String pk,String dk, IHttpCallBack callback);

```

|参数|	是否必传|说明|	
| --- | --- | --- |
| dk |	是|device key|
| pk |	是|product key|


#### 修改设备信息 deviceName

```
public void changeDeviceInfo(String deviceName, String pk,String dk, IHttpCallBack callback);

```
|参数|	是否必传|说明|	
| --- | --- | --- |
| deviceName |	是|设备名称|
| dk |	是|device key|
| pk |	是|product key|



#### 被分享人修改分享的设备名称

```
public void changeShareDeviceName(String deviceName,String shareCode, IHttpCallBack callback);

```

|参数|	是否必传|说明|	
| --- | --- | --- | 
| deviceName |	是|设备名称 | 
| shareCode |	是|分享码 | 


#### 查询物模型 TSL

```
 public void queryProductTSL(String pk, IHttpCallBack callback);
    请调用SDK的 buildModelListContent方法解析出TSL数据结构
   List<ModelBasic> modelBasics = ObjectModelParse.buildModelListContent(jsonArray);
```

|参数|	是否必传|说明|	
| --- | --- | --- |
| pk |	是| productKey | 


#### 查询设备业务属性

```
 public void queryBusinessAttributes(List<String> codeList,String pk,String dk,List<String> typeList, IHttpCallBack callback);
```

|参数|	是否必传|说明|	
| --- | --- | --- | 
| codeList |	否|要查询的属性标识符; 和查询类型配合使用，如果传null 查询所有属性，同时typeList也传null	| 
| pk |	是|pk|
| dk |	是|dk|
| typeList |	否|查询类型:  1 查询设备基础属性  2 查询物模型属性  3 查询定位信息。查询类型可以单选和多选| 

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
 public void scan(Activity activity,String name, String mac, IScanCallBack iScanCallBack);
 public interface IScanCallBack {
    public void onScan(ScanDevice scanDevice);
    public void onFail(Throwable throwable);
}
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
```
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

### wifi配网
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




