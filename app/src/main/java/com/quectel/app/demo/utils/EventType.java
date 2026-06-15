package com.quectel.app.demo.utils;

public class EventType {

/**
 * Event type --- device renamed
 * @type {string}
 */
   public static final String EVENT_TYPE_DEVICE_RENAME = "device_rename";
/**
 * Event type --- TSL property reported
 * @type {string}
 */
    public static final String EVENT_TYPE_M_ATTR_REPORT = "websocketPushEmitter";
/**
 * Event type --- TSL event reported
 * @type {string}
 */
    public static final String EVENT_TYPE_M_EVENT_REPORT = "websocket_MEVENT";
/**
 * Event type --- online/offline: 0=offline, 1=online, 2=reconnected
 * @type {string}
 */
    public static final String EVENT_TYPE_ONLINE = "webSocket_device_online";
/**
 * Event type --- command response event
 * @type {string}
 */
    public static final String EVENT_TYPE_CMD_ACK = "websocketCmdAck";
/**
 * Event type --- WebSocket error reported
 * @type {string}
 */
    public static final String EVENT_TYPE_WEBSOCKET_ERROR = "websocketERROR";
/**
 * Event type --- device not bound
 * @type {string}
 */
    public static final String EVENT_TYPE_WEBSOCKET_DEVICE_UNBIND = "websocketDeviceUnbind";
/**
 * Event type --- device location info
 * @type {string}
 */
    public static final String EVENT_TYPE_WEBSOCKET_LOCATION = "websocketLocationPush";

/**
 * Event type --- WebSocket login failed
 * @type {string}
 */

    public static final String EVENT_TYPE_WEBSOCKET_LOGIN_FAILURE = "websocketLoginFailure";
/**
 * Event type --- update TSL data
 * @type {string}
 */
    public static final String EVENT_TYPE_UPDATE_TSL_ATTR = "updateTSLAttr";
/**
 * Update struct text property
 * @type {string}
 */

    public static final String EVENT_TYPE_UPDATE_STRUCT_TEXT_ATTR = "updateStructTextAttr";


    public static final String EVENT_TYPE_LOGIN_SUCCESS = "websocket_login_success";


}

