package com.quectel.app.demo.ui;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.quectel.app.blesdk.ble.ScanDevice;
import com.quectel.app.blesdk.bleservice.IBleService;
import com.quectel.app.blesdk.bleservice.IScanCallBack;
import com.quectel.app.blesdk.utils.BleServiceLocater;
import com.quectel.app.blesdk.utils.ManufacturerAnalysisUtils;
import com.quectel.app.demo.R;
import com.quectel.app.demo.adapter.SmartConfigDeviceAdapter;
import com.quectel.app.demo.base.BaseActivity;
import com.quectel.app.demo.bean.SmartConfigDevice;
import com.quectel.app.demo.dialog.WifiDataBottomDialog;
import com.quectel.app.demo.utils.DeviceUtil;
import com.quectel.app.demo.utils.MyUtils;
import com.quectel.app.demo.utils.PermissionUtil;
import com.quectel.app.demo.utils.ToastUtils;
import com.quectel.app.device.iot.IotChannelController;
import com.quectel.basic.common.utils.QuecGsonUtil;
import com.quectel.basic.common.utils.QuecThreadUtil;
import com.quectel.basic.queclog.QLog;
import com.quectel.sdk.smart.config.api.QuecDevicePairingServiceManager;
import com.quectel.sdk.smart.config.api.bean.DeviceBean;
import com.quectel.sdk.smart.config.api.bean.QuecPairDeviceBean;
import com.quectel.sdk.smart.config.api.bean.QuecPairErrorCode;
import com.quectel.sdk.smart.config.api.bean.QuecResult;
import com.quectel.sdk.smart.config.api.callback.QuecPairingListener;
import com.quectel.sdk.smart.config.api.callback.QuecSmartConfigListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import okhttp3.internal.http2.Http2Reader;


//wifi配网
public class DistributionNetworkActivity extends BaseActivity {

    private static final String TAG = "DistributionNetworkActivity";

    @BindView(R.id.recycler)
    RecyclerView recycler;

    @BindView(R.id.bt_scan)
    Button bt_scan;

    private SmartConfigDeviceAdapter adapter;

    private WifiDataBottomDialog wifiDataBottomDialog;


    private String ssid = "";

    private String pwd = "";


    @Override
    protected int getContentLayout() {
        return R.layout.activity_wifi_network;
    }

    @Override
    protected void addHeadColor() {
        MyUtils.addStatusBarView(this, R.color.gray_bg);
    }

    @Override
    protected void initData() {
        initPermission();
        initView();
    }

    private void initPermission() {
        if (!PermissionUtil.hasLocation(DistributionNetworkActivity.this)) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        if (PermissionUtil.checkPermission(DistributionNetworkActivity.this)) {

        }

        BluetoothAdapter bltAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean enabled = bltAdapter.isEnabled();
        //如果没有打开系统蓝牙，请求打开系统蓝牙
        if (!enabled) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

                ToastUtils.showShort(activity, "请先授予应用的 蓝牙权限");
                return;
            }
            startActivityForResult(intent, 1);
        }
    }


    private void initView() {
        adapter = new SmartConfigDeviceAdapter();
        adapter.setListener(new SmartConfigDeviceAdapter.OnStartConfigListener() {
            @Override
            public void onStartConfig(int position, SmartConfigDevice device) {

                //判断是否是纯蓝牙设备
                if (device.getDeviceBean().getBleDevice().getCapabilitiesBitmask() == 4) {
                    //直接配网，不需要Wifi名称和密码
                    startPairing(device, position, null, null);
                } else {
                    //弹窗输入Wifi名称和密码
                    showDialog(device, position);
                }
            }
        });
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        QuecDevicePairingServiceManager.INSTANCE.init(this);
        QuecDevicePairingServiceManager.INSTANCE.addPairingListener(new QuecPairingListener() {
            @Override
            public void onScanDevice(@NonNull QuecPairDeviceBean quecPairDeviceBean) {

                //是否已经在列表中
                for (SmartConfigDevice smartConfigDevice : adapter.getData()) {
                    if (TextUtils.equals(smartConfigDevice.getDeviceBean().getBleDevice().getProductKey(), quecPairDeviceBean.getBleDevice().getProductKey())
                            && TextUtils.equals(smartConfigDevice.getDeviceBean().getBleDevice().getDeviceKey(), quecPairDeviceBean.getBleDevice().getDeviceKey())) {
                        return;
                    }
                }
                //添加到列表中
                SmartConfigDevice device = new SmartConfigDevice();
                device.setDeviceBean(quecPairDeviceBean);
                adapter.addData(device);
            }

            @Override
            public void onUpdatePairingStatus(@NonNull QuecPairDeviceBean quecPairDeviceBean, float progress) {

                QLog.i(TAG, "pk:" + quecPairDeviceBean.getBleDevice().getProductKey() + "  dk:" + quecPairDeviceBean.getBleDevice().getDeviceKey() + "  progress:" + progress);
                for (int i = 0; i < adapter.getData().size(); i++) {
                    SmartConfigDevice smartConfigDevice = adapter.getData().get(i);
                    if (TextUtils.equals(smartConfigDevice.getDeviceBean().getBleDevice().getProductKey(), quecPairDeviceBean.getBleDevice().getProductKey())
                            && TextUtils.equals(smartConfigDevice.getDeviceBean().getBleDevice().getDeviceKey(), quecPairDeviceBean.getBleDevice().getDeviceKey())) {
                        smartConfigDevice.setBindResult(100);
                        int finalI = i;
                        QuecThreadUtil.RunMainThread(() -> adapter.notifyItemChanged(finalI));
                        return;
                    }
                }

            }

            @Override
            public void onUpdatePairingResult(@NonNull QuecPairDeviceBean quecPairDeviceBean, boolean isSuccess, @NonNull QuecPairErrorCode errorCode) {
                for (int i = 0; i < adapter.getData().size(); i++) {
                    SmartConfigDevice smartConfigDevice = adapter.getData().get(i);
                    if (TextUtils.equals(smartConfigDevice.getDeviceBean().getBleDevice().getProductKey(), quecPairDeviceBean.getBleDevice().getProductKey())
                            && TextUtils.equals(smartConfigDevice.getDeviceBean().getBleDevice().getDeviceKey(), quecPairDeviceBean.getBleDevice().getDeviceKey())) {

                        if (isSuccess) {
                            smartConfigDevice.setBindResult(200);
                        } else {
                            smartConfigDevice.setBindResult(300);
                            QLog.e(TAG, "pk:" + quecPairDeviceBean.getBleDevice().getProductKey() + "  dk:" + quecPairDeviceBean.getBleDevice().getDeviceKey() + "  配网失败:" + errorCode);
                        }

                        int finalI = i;
                        QuecThreadUtil.RunMainThread(() -> adapter.notifyItemChanged(finalI));
                        return;
                    }
                }

            }
        });

    }

    private void showDialog(SmartConfigDevice deviceBean, int pos) {
        if (wifiDataBottomDialog == null) {
            wifiDataBottomDialog = new WifiDataBottomDialog(this);
        }
        wifiDataBottomDialog.show();
        wifiDataBottomDialog.setSSidAndPwd(ssid, pwd);
        wifiDataBottomDialog.setOnConfirmClickListener(new WifiDataBottomDialog.OnConfirmClickListener() {
            @Override
            public void onConfirm(String ssid, String pwd, int position) {
                if (ssid.isEmpty()) {
                    Toast.makeText(activity, "ssid is empty", Toast.LENGTH_SHORT).show();
                    wifiDataBottomDialog.dismiss();
                    return;
                }
                if (pwd.isEmpty()) {
                    wifiDataBottomDialog.dismiss();
                    Toast.makeText(activity, "pwd is empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                startPairing(deviceBean, pos, ssid, pwd);
                wifiDataBottomDialog.dismiss();
            }
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        QuecDevicePairingServiceManager.INSTANCE.cancelAllDevicePairing();
    }

    private Disposable disposableFirst = null;

    @OnClick({R.id.iv_back, R.id.bt_scan})
    public void buttonClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.iv_back:
                QuecDevicePairingServiceManager.INSTANCE.cancelAllDevicePairing();
                finish();
                break;

            case R.id.bt_scan:

                if (MyUtils.isFastClick()) {
                    return;
                }

                if ("开始扫描".equals(bt_scan.getText().toString())) {
                    bt_scan.setText("停止扫描");
                    adapter.getData().clear();
                    adapter.notifyDataSetChanged();
                    QuecDevicePairingServiceManager.INSTANCE.scan(null, null, null);
                    return;
                }

                if ("停止扫描".equals(bt_scan.getText().toString())) {
                    bt_scan.setText("开始扫描");
                    QuecDevicePairingServiceManager.INSTANCE.stopScan();
                    return;
                }

                break;
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (disposableFirst != null) {
            disposableFirst.dispose();
            disposableFirst = null;
        }
        finishLoading();
    }

    @Override
    public void onBackPressedSupport() {
        super.onBackPressedSupport();
        QuecDevicePairingServiceManager.INSTANCE.cancelAllDevicePairing();
    }

    // 开始配网
    private void startPairing(SmartConfigDevice device, int pos, String ssid, String pwd) {
        // 设置配网状态-正在配网中
        device.setBindResult(100);
        adapter.notifyItemChanged(pos);
        //防止近场通道可能会影响配网，需要移除该通道
        String channelId = device.getDeviceBean().getBleDevice().getProductKey() + "_" + device.getDeviceBean().getBleDevice().getDeviceKey();
        IotChannelController.getInstance().removeDeviceChannel(channelId);
        //开始配网
        List<QuecPairDeviceBean> deviceBeans = new ArrayList<>();
        deviceBeans.add(device.getDeviceBean());
        QuecDevicePairingServiceManager.INSTANCE.startPairingByDevices(deviceBeans, null, ssid, pwd);
    }
}
