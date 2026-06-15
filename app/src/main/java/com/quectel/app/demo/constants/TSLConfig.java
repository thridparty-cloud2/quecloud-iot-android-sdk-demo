package com.quectel.app.demo.constants;

public class TSLConfig {

    // TSL configuration data
    /**
     * TSL subType --- read-only
     *
     * @type {string}
     */
    public static final String TSL_SUBTYPE_R = "R";
    /**
     * TSL subType --- read-write
     *
     * @type {string}
     */
    public static final String TSL_SUBTYPE_RW = "RW";
    /**
     * TSL subType --- write-only
     *
     * @type {string}
     */
    public static final String TSL_SUBTYPE_W = "W";
    /**
     * TSL property type --- integer
     *
     * @type {string}
     */
    public static final String TSL_ATTR_DATA_TYPE_INT = "INT";
    /**
     * TSL property type --- boolean
     *
     * @type {string}
     */
    public static final String TSL_ATTR_DATA_TYPE_BOOL = "BOOL";
    /**
     * TSL property type --- single-precision float
     *
     * @type {string}
     */
    public static final String TSL_ATTR_DATA_TYPE_FLOAT = "FLOAT";
    /**
     * TSL property type --- double-precision float
     *
     * @type {string}
     */
    public static final String TSL_ATTR_DATA_TYPE_DOUBLE = "DOUBLE";
    /**
     * TSL property type --- enum
     *
     * @type {string}
     */
    public static final String TSL_ATTR_DATA_TYPE_ENUM = "ENUM";
    /**
     * TSL property type --- text
     *
     * @type {string}
     */
    public static final String TSL_ATTR_DATA_TYPE_TEXT = "TEXT";
    /**
     * TSL property type --- date
     *
     * @type {string}
     */
    public static final String TSL_ATTR_DATA_TYPE_DATE = "DATE";

    /**
     * View type --- color
     *
     * @type {number}
     */
    public static final String TSL_ATTR_DATA_TYPE_COLOR = "color";
    /**
     * View type --- countdown
     *
     * @type {number}
     */
    public static final String TSL_ATTR_DATA_TYPE_COUNTDOWN = "countdown";

    /**
     * TSL property type --- array
     *
     * @type {string}
     */
    public static final String TSL_ATTR_DATA_TYPE_ARRAY = "ARRAY";
    /**
     * TSL property type --- struct
     *
     * @type {string}
     */
    public static final String TSL_ATTR_DATA_TYPE_STRUCT = "STRUCT";

    /**
     * TSL property type --- struct array
     *
     * @type {string}
     */
    public static final String TSL_ATTR_DATA_TYPE_STRUCT_ARRAY = "array_struct";

    /**
     * TSL property type --- struct array
     *
     * @type {string}
     */
    public static final String TSL_ATTR_DATA_TYPE_ARRAY_STRUCT = "struct_array";

    /**
     * Three-color LED detail page
     *
     * @type {string}
     */
    public static final String PAGE_DETAIL_RGB_LIGHT = "RGBLightDetail";
    /**
     * Circuit breaker detail page
     *
     * @type {string}
     */
    public static final String PAGE_DETAIL_CIRCUIT_BREAKER = "CircuitBreakerDetail";
    /**
     * Switch detail page
     *
     * @type {string}
     */
    public static final String PAGE_DETAIL_ON_OFF_SENSOR = "OnOffSensorDetail";

    /**
     * Circuit breaker TSL attribute --- switch state, enum, read-only
     * 0=open  1=closed
     *
     * @type {string}
     */
    public static final String TSL_ATTR_DEVICE_SWITCH_STATUS = "DeviceSwitch";

    /**
     * Switch TSL attribute --- switch state, BOOL, read-write
     * true=on  false=off
     *
     * @type {string}
     */
    public static final String TSL_ATTR_DEVICE_ON_OFF_STATUS = "switch_state";

    /**
     * Three-color LED TSL attribute --- power switch, BOOL, read-write
     *
     * @type {string}
     */
    public static final String TSL_ATTR_POWER = "power";
    /**
     * Three-color LED TSL attribute --- countdown off, INT, read-write, range: 0~86400
     *
     * @type {string}
     */
    public static final String TSL_ATTR_TURN_OFF_DELAY = "turnoff_delay";
    /**
     * Three-color LED TSL attribute --- R channel, INT, read-write, range: 0~255
     *
     * @type {string}
     */
    public static final String TSL_ATTR_R_LED = "R_LED";
    /**
     * Three-color LED TSL attribute --- G channel, INT, read-write, range: 0~255
     *
     * @type {string}
     */
    public static final String TSL_ATTR_G_LED = "G_LED";
    /**
     * Three-color LED TSL attribute --- B channel, INT, read-write, range: 0~255
     *
     * @type {string}
     */
    public static final String TSL_ATTR_B_LED = "B_LED";
    /**
     * Three-color LED TSL attribute --- brightness, INT, read-write, range: 0~255
     *
     * @type {string}
     */
    public static final String TSL_ATTR_BRIGHTNESS = "brightness";


}
