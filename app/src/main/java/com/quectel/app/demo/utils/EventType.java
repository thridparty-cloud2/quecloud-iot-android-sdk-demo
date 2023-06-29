package com.quectel.app.demo.utils;

public class EventType {

/**
 * 事件类型---设备重命名
 * @type {string}
 */
   public static final String EVENT_TYPE_DEVICE_RENAME = "device_rename";
/**
 * 事件类型---物模型属性上报
 * @type {string}
 */
    public static final String EVENT_TYPE_M_ATTR_REPORT = "websocketPushEmitter";
/**
 * 事件类型---物模型事件上报
 * @type {string}
 */
    public static final String EVENT_TYPE_M_EVENT_REPORT = "websocket_MEVENT";
/**
 * 事件类型---上下线事件 0 - 下线(offline)， 1 - 上线(online)，2 - 重新连接(reonline)
 * @type {string}
 */
    public static final String EVENT_TYPE_ONLINE = "webSocket_device_online";
/**
 * 事件类型---命令应答事件
 * @type {string}
 */
    public static final String EVENT_TYPE_CMD_ACK = "websocketCmdAck";
/**
 * 事件类型---WebSocket错误信息上报
 * @type {string}
 */
    public static final String EVENT_TYPE_WEBSOCKET_ERROR = "websocketERROR";
/**
 * 事件类型---设备未绑定
 * @type {string}
 */
    public static final String EVENT_TYPE_WEBSOCKET_DEVICE_UNBIND = "websocketDeviceUnbind";
/**
 * 事件类型---设备位置信息
 * @type {string}
 */
    public static final String EVENT_TYPE_WEBSOCKET_LOCATION = "websocketLocationPush";

/**
 * 事件类型---webSocket登录失败
 * @type {string}
 */

    public static final String EVENT_TYPE_WEBSOCKET_LOGIN_FAILURE = "websocketLoginFailure";
/**
 * 事件类型---更新物模型数据
 * @type {string}
 */
    public static final String EVENT_TYPE_UPDATE_TSL_ATTR = "updateTSLAttr";
/**
 * 更新结构体文本属性
 * @type {string}
 */

    public static final String EVENT_TYPE_UPDATE_STRUCT_TEXT_ATTR = "updateStructTextAttr";


    public static final String EVENT_TYPE_LOGIN_SUCCESS = "websocket_login_success";


}

