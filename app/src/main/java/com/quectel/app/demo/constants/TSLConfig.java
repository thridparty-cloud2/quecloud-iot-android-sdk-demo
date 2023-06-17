package com.quectel.app.demo.constants;

public class TSLConfig {

    //物模型配置数据
    /**
     * 物模型子类型---只读
     *
     * @type {string}
     */
    public static final String TSL_SUBTYPE_R = "R";
    /**
     * 物模型子类型---读写
     *
     * @type {string}
     */
    public static final String TSL_SUBTYPE_RW = "RW";
    /**
     * 物模型子类型---只写
     *
     * @type {string}
     */
    public static final String TSL_SUBTYPE_W = "W";
    /**
     * 物模型属性类型---整型
     *
     * @type {string}
     */
    public static final String TSL_ATTR_DATA_TYPE_INT = "INT";
    /**
     * 物模型属性类型---布尔型
     *
     * @type {string}
     */
    public static final String TSL_ATTR_DATA_TYPE_BOOL = "BOOL";
    /**
     * 物模型属性类型--单精度浮点型
     *
     * @type {string}
     */
    public static final String TSL_ATTR_DATA_TYPE_FLOAT = "FLOAT";
    /**
     * 物模型属性类型--双精度浮点型
     *
     * @type {string}
     */
    public static final String TSL_ATTR_DATA_TYPE_DOUBLE = "DOUBLE";
    /**
     * 物模型属性类型--枚举型
     *
     * @type {string}
     */
    public static final String TSL_ATTR_DATA_TYPE_ENUM = "ENUM";
    /**
     * 物模型属性类型--文本型
     *
     * @type {string}
     */
    public static final String TSL_ATTR_DATA_TYPE_TEXT = "TEXT";
    /**
     * 物模型属性类型--日期型
     *
     * @type {string}
     */
    public static final String TSL_ATTR_DATA_TYPE_DATE = "DATE";

    /**
     * 视图类型---颜色
     *
     * @type {number}
     */
    public static final String TSL_ATTR_DATA_TYPE_COLOR = "color";
    /**
     * 视图类型---倒计时
     *
     * @type {number}
     */
    public static final String TSL_ATTR_DATA_TYPE_COUNTDOWN = "countdown";

    /**
     * 物模型属性类型---数组型
     *
     * @type {string}
     */
    public static final String TSL_ATTR_DATA_TYPE_ARRAY = "ARRAY";
    /**
     * 物模型属性类型---结构体
     *
     * @type {string}
     */
    public static final String TSL_ATTR_DATA_TYPE_STRUCT = "STRUCT";

    /**
     * 物模型属性类型---结构体 数组
     *
     * @type {string}
     */
    public static final String TSL_ATTR_DATA_TYPE_STRUCT_ARRAY = "array_struct";

    /**
     * 物模型属性类型---结构体 数组
     *
     * @type {string}
     */
    public static final String TSL_ATTR_DATA_TYPE_ARRAY_STRUCT = "struct_array";

    /**
     * 三色灯详情页
     *
     * @type {string}
     */
    public static final String PAGE_DETAIL_RGB_LIGHT = "RGBLightDetail";
    /**
     * 断路器详情页
     *
     * @type {string}
     */
    public static final String PAGE_DETAIL_CIRCUIT_BREAKER = "CircuitBreakerDetail";
    /**
     * 通断器详情页
     *
     * @type {string}
     */
    public static final String PAGE_DETAIL_ON_OFF_SENSOR = "OnOffSensorDetail";

    /**
     * 断路器品类物模型属性---分合闸状态，枚举，只读
     * 0 分闸  1 合闸
     *
     * @type {string}
     */
    public static final String TSL_ATTR_DEVICE_SWITCH_STATUS = "DeviceSwitch";

    /**
     * 通断器品类物模型属性---开关状态，BOOL，读写
     * true 接通  false 关闭
     *
     * @type {string}
     */
    public static final String TSL_ATTR_DEVICE_ON_OFF_STATUS = "switch_state";

    /**
     * 三色灯品类物模型属性---开关，BOOL，读写
     *
     * @type {string}
     */
    public static final String TSL_ATTR_POWER = "power";
    /**
     * 三色灯品类物模型属性---倒计时关灯，INT ，读写, 取值范围：0 ~ 86400
     *
     * @type {string}
     */
    public static final String TSL_ATTR_TURN_OFF_DELAY = "turnoff_delay";
    /**
     * 三色灯品类物模型属性---R灯珠，INT ，读写, 取值范围：0 ~ 255
     *
     * @type {string}
     */
    public static final String TSL_ATTR_R_LED = "R_LED";
    /**
     * 三色灯品类物模型属性---G灯珠，INT ，读写, 取值范围：0 ~ 255
     *
     * @type {string}
     */
    public static final String TSL_ATTR_G_LED = "G_LED";
    /**
     * 三色灯品类物模型属性---B灯珠，INT ，读写, 取值范围：0 ~ 255
     *
     * @type {string}
     */
    public static final String TSL_ATTR_B_LED = "B_LED";
    /**
     * 三色灯品类物模型属性---亮度，INT ，读写, 取值范围：0 ~ 255
     *
     * @type {string}
     */
    public static final String TSL_ATTR_BRIGHTNESS = "brightness";


}
