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
import com.quectel.basic.common.utils.QuecGsonUtil;
import com.quectel.basic.queclog.QLog;
import com.quectel.sdk.smart.config.api.QuecSmartConfigServiceManager;
import com.quectel.sdk.smart.config.api.bean.DeviceBean;
import com.quectel.sdk.smart.config.api.bean.QuecResult;
import com.quectel.sdk.smart.config.api.callback.QuecSmartConfigListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import okhttp3.internal.http2.Http2Reader;


//wifi配网
public class DistributionNetworkActivity extends BaseActivity {


    @BindView(R.id.recycler)
    RecyclerView recycler;

    @BindView(R.id.bt_scan)
    Button bt_scan;

    private SmartConfigDeviceAdapter adapter;

    private WifiDataBottomDialog wifiDataBottomDialog;


    private String ssid;

    private String pwd;

    private List<ScanDevice> bleScanDeviceList = new ArrayList<>();

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

//                List<DeviceBean> deviceBeans = new ArrayList<>();
//                deviceBeans.add(device.getDeviceBean());
//                showDialog(device, position);
                // QuecSmartConfigServiceManager.getInstance().startConfigDevices(deviceBeans, "QUEC_WIFI_TEST", "12332112");

                ssid = "ASUS_18";
                pwd = "12345678";
                SmartConfigDevice deviceBean = device;
                deviceBean.setBindResult(100);
                adapter.notifyItemChanged(position);
                List<DeviceBean> deviceBeans = new ArrayList<>();
                deviceBeans.add(deviceBean.getDeviceBean());
                QuecSmartConfigServiceManager.getInstance().startConfigDevices(deviceBeans, ssid, pwd);
            }
        });
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);
        QuecSmartConfigServiceManager.getInstance().addSmartConfigListener(new QuecSmartConfigListener() {
            @Override
            public void onUpdateConfigResultCallback(DeviceBean deviceBean, QuecResult quecResult) {
                QLog.i(QuecGsonUtil.INSTANCE.gsonString(quecResult));
                for (int i = 0; i < adapter.getData().size(); i++) {
                    if (deviceBean.getMac().equals(adapter.getData().get(i).getDeviceBean().getMac())) {
                        adapter.getData().get(i).setBindResult(quecResult.getCode());
                        adapter.getData().get(i).setMessage(quecResult.getMessage());
                        adapter.notifyItemChanged(i);
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
                deviceBean.setBindResult(100);
                adapter.notifyItemChanged(pos);
                List<DeviceBean> deviceBeans = new ArrayList<>();
                deviceBeans.add(deviceBean.getDeviceBean());
                QuecSmartConfigServiceManager.getInstance().startConfigDevices(deviceBeans, ssid, pwd);

                wifiDataBottomDialog.dismiss();
            }
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        QuecSmartConfigServiceManager.getInstance().cancelConfigDevices();
        BleServiceLocater.getService(IBleService.class).release();
    }

    private Disposable disposableFirst = null;

    @OnClick({R.id.iv_back, R.id.bt_scan})
    public void buttonClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.iv_back:
                QuecSmartConfigServiceManager.getInstance().cancelConfigDevices();
                finish();
                break;

            case R.id.bt_scan:

                if (MyUtils.isFastClick()) {
                    return;
                }

                if ("开始扫描".equals(bt_scan.getText().toString())) {
                    bt_scan.setText("停止扫描");
                    getDevice();
                    return;
                }

                if ("停止扫描".equals(bt_scan.getText().toString())) {
                    bt_scan.setText("开始扫描");
                    BleServiceLocater.getService(IBleService.class).stopScan();
                    return;
                }

                break;
        }

    }

    private void getDevice() {

        bleScanDeviceList.clear();
        BleServiceLocater.getService(IBleService.class).scan("", "", new IScanCallBack() {
            @Override
            public void onScan(ScanDevice scanDevice) {

                //过滤没有名称的蓝牙设备
                if (TextUtils.isEmpty(scanDevice.getName())) return;

                if (scanDevice.getManufacturer_specific_data() == null) {
                    return;
                }
                if (scanDevice.getManufacturer_specific_data().length == 0) {
                    return;
                }
                //判断设备是否已经被绑定过
                if (DeviceUtil.isDeviceConfig(scanDevice)){
                    QLog.i("name:" + scanDevice.getName() + " mac:" + scanDevice.getMac() + " 设备已经被绑定！");
                    return;
                }

                //蓝牙设备是否已经扫描过
                for (ScanDevice device: bleScanDeviceList) {
                     if (TextUtils.equals(device.getMac(), scanDevice.getMac())){
                         return;
                     }
                }
                bleScanDeviceList.add(scanDevice);

                SmartConfigDevice device = new SmartConfigDevice();
                DeviceBean deviceBean = new DeviceBean();
                deviceBean.setDeviceType(1);
                deviceBean.setProductKey(DeviceUtil.getPk(scanDevice));
                deviceBean.setName(scanDevice.getName());
                deviceBean.setMac(scanDevice.getMac());
                deviceBean.setDeviceKey(DeviceUtil.getDK(scanDevice));
                deviceBean.setCapabilitiesBitmask(DeviceUtil.getCapabilitiesBitmask(scanDevice));
                deviceBean.setEndpoint(DeviceUtil.getEndPointType(scanDevice));
                device.setDeviceBean(deviceBean);
                adapter.addData(device);
            }

            @Override
            public void onFail(Throwable throwable) {

            }
        });
    }

    private DeviceBean getData(List<SmartConfigDevice> list, DeviceBean deviceBean) {

        if (list == null || list.size() == 0) {

            return deviceBean;
        }
        for (int i = 0; i < list.size(); i++) {
            if (deviceBean.getMac().equals(list.get(i).getDeviceBean().getMac())) {
                return null;
            }
        }
        return deviceBean;
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
        QuecSmartConfigServiceManager.getInstance().cancelConfigDevices();
    }
}
