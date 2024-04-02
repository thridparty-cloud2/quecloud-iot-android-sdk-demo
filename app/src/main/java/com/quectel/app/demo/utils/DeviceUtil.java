package com.quectel.app.demo.utils;

import android.bluetooth.BluetoothAdapter;
import android.text.TextUtils;

import com.quectel.app.blesdk.ble.ScanDevice;
import com.quectel.basic.queclog.QLog;

public class DeviceUtil {
    /**
     * 判断是否是新设备
     *
     * @param scanDevice
     * @return
     */
    public static boolean isNewDevice(ScanDevice scanDevice) {
        if (scanDevice == null || scanDevice.getManufacturer_specific_data() == null) {
            return false;
        }
//        QLog.i("isNewDevice",QuecGsonUtil.INSTANCE.gsonString(scanDevice));
        if (scanDevice.getManufacturer_specific_data().length >= 21
                && (scanDevice.getManufacturer_specific_data()[0] & 0xFF) == Integer.valueOf('Q').intValue()
                && (scanDevice.getManufacturer_specific_data()[1] & 0xFF) == Integer.valueOf('U').intValue()
                && (scanDevice.getManufacturer_specific_data()[2] & 0xFF) == Integer.valueOf('E').intValue()
                && (scanDevice.getManufacturer_specific_data()[3] & 0xFF) == Integer.valueOf('C').intValue()) {
//            QLog.e("isNewDevice",scanDevice.getName()+"true");
            return true;
        } else {
//            QLog.e("isNewDevice",scanDevice.getName()+"false");
            return false;
        }
    }

    /**
     * 获取PK
     *
     * @param scanDevice
     * @return
     */
    public static String getPk(ScanDevice scanDevice) {
        int length = scanDevice.getManufacturer_specific_data()[6] & 0xFF;
        byte[] data = new byte[length];
        System.arraycopy(scanDevice.getManufacturer_specific_data(), 7, data, 0, length);
        String pk = new String(data);
//        String pk = bytesToHexString(data);
        return pk;
    }

    /**
     * 获取dk
     *
     * @param scanDevice
     * @return
     */
    public static String getDK(ScanDevice scanDevice) {
        byte[] allData = scanDevice.getManufacturer_specific_data();
        int pkLength = allData[6] & 0xFF;
        byte[] data = new byte[allData.length - 7 - pkLength];
        System.arraycopy(allData, 7 + pkLength, data, 0, data.length);
        int macLength = data[0] & 0xFF;
        byte[] dkByte = new byte[macLength];
        System.arraycopy(data, 1, dkByte, 0, macLength);
        String dk = bytesToHexString(dkByte);
        if (TextUtils.isEmpty(dk)) {
            return "";
        }
        int type = getDkType(scanDevice);
        if (type != 1) {
            return dk;
        }

        if ("0".equals(dk.substring(dk.length() - 1))) {
            return dk.substring(0, dk.length() - 1);
        }
        return dk;
    }

    /**
     * 获取
     *
     * @param scanDevice
     * @return
     */
    public static int getFlag(ScanDevice scanDevice) {
        byte[] allData = scanDevice.getManufacturer_specific_data();
        byte[] flagData = new byte[2];
        System.arraycopy(allData, allData.length - flagData.length, flagData, 0, flagData.length);
        int capabilitiesBitmask = byteArrayToIntLittleEndian(flagData);
        return capabilitiesBitmask;
    }


    public static int getDeviceBindStatus(ScanDevice scanDevice) {
        byte[] allData = scanDevice.getManufacturer_specific_data();
        byte[] flagData = new byte[1];
        System.arraycopy(allData, allData.length - (flagData.length + 2), flagData, 0, flagData.length);
        int deviceStatus = byteArrayToIntLittleEndian(flagData);
        QLog.e("SmartConfig scanDevice deviceStatus ", deviceStatus + "");
        //(x>>2)&0x1;
        return deviceStatus >> 2 & 0x1;
    }

    public static boolean isDeviceConfig(ScanDevice device) {
        return getDeviceBindStatus(device) == 1;
    }


    public static int getCapabilitiesBitmask(ScanDevice scanDevice) {
        int mask = getFlag(scanDevice);
        QLog.e("Scan---device----mask", "mask " + mask);
        return mask & 0x0F;
    }


    /**
     * 字节数组转int 小端模式
     */
    public static int byteArrayToIntLittleEndian(byte[] bytes) {
        int x = 0;
        for (int i = 0; i < bytes.length; i++) {
            int b = (bytes[i] & 0xFF) << (i * 8);
            x |= b;
        }
        return x;
    }

//    /**
//     * 校验byte数组的bit 8位上的值 是否为1
//     * @param bytes
//     * @return
//     */
//    public static boolean byteArrayToDkStatus(byte[] bytes){
//        int type = 1;
//        int b = bytes[1] & 0xFF;
//        type &= b;
//        return type==1;
//    }


    /**
     * 获取Mac
     *
     * @param scanDevice
     * @return
     */
    public static String getOldMac(ScanDevice scanDevice) {
        byte[] allData = scanDevice.getManufacturer_specific_data();
        String mac = macBytesToHexString(allData);
        return mac;
    }

    public static byte[] toByteArray(String hexString) {

        hexString = hexString.toLowerCase();
        final byte[] byteArray = new byte[hexString.length() / 2];
        int k = 0;

        for (int i = 0; i < byteArray.length; i++) {// 因为是16进制，最多只会占用4位，转换成字节需要两个16进制的字符，高位在先
            byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
            byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
            byteArray[i] = (byte) (high << 4 | low);
            k += 2;

        }
        return byteArray;

    }


    /**
     * byte数组转换成16进制字符串
     *
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }

        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * byte数组转换成16进制字符串 并重新组合符合要求的mac地址
     *
     * @param src
     * @return
     */
    public static String macBytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }

        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv.toUpperCase());
            if (i < src.length - 1) {
                stringBuilder.append(":");
            }

        }
        return stringBuilder.toString();
    }

    /**
     * 检查系统蓝牙是否打开
     *
     * @return
     */
    public static boolean checkedOpenBle() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        return adapter.isEnabled();
    }


    /**
     * 截取dk后四位 并换成小写
     *
     * @param dk
     * @return
     */
    public static String getLastFourDK(String dk) {
        if (TextUtils.isEmpty(dk)) {
            return "";
        }
        String s = dk.substring(dk.length() - 4);
        return "_" + s.toUpperCase();
    }

    /**
     * 截取mac地址后四位 并换成小写
     *
     * @param mac
     * @return
     */
    public static String getLastFourMac(String mac) {
        if (TextUtils.isEmpty(mac)) {
            return "";
        }
        String s = mac.substring(mac.length() - 5);
        String s1 = s.replace(":", "");
        return "_" + s1;
    }

    /***
     *  获取设备与云端的接入类型 0 = 无效，tcp = 1，tcp_psk=2 tls = 3
     *  cer = 4 upd = 5,upd_psk = 6;
     *  0b1110_1111_0000_0000
     *  右移 9 位 获得 0b0111_0111
     *  &0xF 获得  0b0000_0111
     */
    public static int getEndPointType(ScanDevice deviceBean) {
        int bitmask = getFlag(deviceBean);
        return (bitmask >> 9) & 0x07;

    }

    /**
     * bit8=1表示dk补了一个"0”再编码，接收方解码后应裁剪最后的"0";
     *
     * @param deviceBean 1110_1111_0000_0000
     *                   右移 8位 得到 1110_1111
     * @return
     */
    public static int getDkType(ScanDevice deviceBean) {
        int bitmask = getFlag(deviceBean);
        return (bitmask >> 8) & 0x1;

    }


}
