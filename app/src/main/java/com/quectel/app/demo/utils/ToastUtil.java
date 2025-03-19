package com.quectel.app.demo.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;

import com.quectel.basic.common.R;
import com.quectel.basic.common.base.QuecBaseApp;


/**
 * Toast 工具类
 */
public class ToastUtil {
    public static void showS(CharSequence msg) {
        showS(QuecBaseApp.getInstance().getApplicationContext(), msg);
    }

    public static void showS(int resId) {
        showS(QuecBaseApp.getInstance().getApplicationContext(), resId);
    }

    /**
     * 显示toast 短暂
     */
    public static void showS(Context context, CharSequence msg) {
        showReal(context, msg, Toast.LENGTH_SHORT);
    }

    public static void showS(Context context, int resId) {
        showReal(context, resId, Toast.LENGTH_SHORT);
    }

    public static void showL(CharSequence msg) {
        showL(QuecBaseApp.getInstance(), msg);
    }

    public static void showL(int resId) {
        showL(QuecBaseApp.getInstance(), resId);
    }

    /**
     * 显示toast 较长
     */
    public static void showL(Context context, CharSequence msg) {
        showReal(context, msg, Toast.LENGTH_LONG);
    }

    public static void showL(Context context, int resId) {
        showReal(context, resId, Toast.LENGTH_LONG);
    }


    /**
     * 显示toast
     */
    public static void showReal(Context context, CharSequence msg, int duration) {
        Toast toast = Toast.makeText(context, msg, duration);
        toast.setView(LayoutInflater.from(context).inflate(R.layout.quec_toast, null));
        ((TextView) toast.getView().findViewById(R.id.message)).setText(msg);
        toast.setDuration(duration);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    /**
     * 显示toast
     */
    public static void showReal(Context context, int resId, int duration) {
        Toast toast = Toast.makeText(context, resId, duration);
        toast.setView(LayoutInflater.from(context).inflate(R.layout.quec_toast, null));
        ((TextView) toast.getView().findViewById(R.id.message)).setText(context.getResources().getText(resId));
        toast.setDuration(duration);
        toast.setDuration(duration);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }


}
