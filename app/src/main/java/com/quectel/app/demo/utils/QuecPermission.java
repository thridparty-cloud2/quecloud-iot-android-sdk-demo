package com.quectel.app.demo.utils;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;

import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.permissionx.guolindev.PermissionMediator;

public class QuecPermission {
    public QuecPermission() {
    }

    public static PermissionMediator init(FragmentActivity activity) {
        return new PermissionMediator(activity);
    }

    public static PermissionMediator init(Fragment fragment) {
        return new PermissionMediator(fragment);
    }

    public static boolean isGranted(Context context, String permission) {
        return PermissionChecker.checkSelfPermission(context, permission) == 0;
    }

    public static boolean shouldShowRequestPermissionRationale(Activity activity, String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
    }

    public static boolean check0penBle() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        return adapter != null ? adapter.isEnabled() : false;
    }
}
