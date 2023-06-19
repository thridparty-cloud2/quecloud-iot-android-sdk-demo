package com.quectel.app.demo.ui;


import android.Manifest;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.quectel.app.blesdk.ble.DeviceBean;
import com.quectel.app.blesdk.ble.QuecError;
import com.quectel.app.blesdk.ble.ScanDevice;
import com.quectel.app.blesdk.bleservice.IBleService;

import com.quectel.app.blesdk.bleservice.IDeviceScanCallback;
import com.quectel.app.blesdk.bleservice.IScanCallBack;
import com.quectel.app.blesdk.utils.BleServiceLocater;

import com.quectel.app.demo.R;

import com.quectel.app.demo.adapter.ScanResultAdapter;
import com.quectel.app.demo.adapter.SmartConfigDeviceAdapter;
import com.quectel.app.demo.base.BaseActivity;

import com.quectel.app.demo.bean.SmartConfigDevice;
import com.quectel.app.demo.dialog.WifiDataBottomDialog;
import com.quectel.app.demo.utils.MyUtils;
import com.quectel.app.demo.utils.PermissionUtil;
import com.quectel.app.demo.utils.ToastUtils;
import com.quectel.app.demo.widget.BottomItemDecorationSystem;
import com.quectel.basic.common.utils.QuecGsonUtil;
import com.quectel.basic.common.utils.QuecToastUtil;
import com.quectel.basic.queclog.QLog;
import com.quectel.sdk.smart.config.api.QuecSmartConfigServiceManager;
import com.quectel.sdk.smart.config.api.bean.QuecResult;
import com.quectel.sdk.smart.config.api.callback.QuecSmartConfigListener;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;

import io.reactivex.disposables.Disposable;


//wifi配网
public class DistributionNetworkActivity extends BaseActivity {


    @BindView(R.id.recycler)
    RecyclerView recycler;

    @BindView(R.id.bt_scan)
    Button bt_scan;

    private SmartConfigDeviceAdapter adapter;

    private WifiDataBottomDialog wifiDataBottomDialog;


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

    private void initPermission(){
        if(!PermissionUtil.hasLocation(DistributionNetworkActivity.this)){
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        if(PermissionUtil.checkPermission(DistributionNetworkActivity.this)){

        }

        BluetoothAdapter bltAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean enabled = bltAdapter.isEnabled();
        //如果没有打开系统蓝牙，请求打开系统蓝牙
        if (!enabled) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {

                ToastUtils.showShort(activity,"请先授予应用的 蓝牙权限");
                return;
            }
            startActivityForResult(intent, 1);
        }
    }



    private void initView(){
        adapter = new SmartConfigDeviceAdapter();
        adapter.setListener(new SmartConfigDeviceAdapter.OnStartConfigListener() {
            @Override
            public void onStartConfig(int position, SmartConfigDevice device) {
                device.setBindResult(100);
                adapter.notifyItemChanged(position);
                List<DeviceBean> deviceBeans = new ArrayList<>();
                deviceBeans.add(device.getDeviceBean());
                QuecSmartConfigServiceManager.getInstance().startConfigDevices(deviceBeans,"QUEC_WIFI_TEST","12332112");
            }
        });
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);
        QuecSmartConfigServiceManager.getInstance().addSmartConfigListener(new QuecSmartConfigListener() {
            @Override
            public void onUpdateConfigResultCallback(DeviceBean deviceBean, QuecResult quecResult) {
                QLog.i(QuecGsonUtil.INSTANCE.gsonString(quecResult));
                for(int i =0;i<adapter.getData().size();i++){
                    if(deviceBean.getMac().equals(adapter.getData().get(i).getDeviceBean().getMac())){
                        adapter.getData().get(i).setBindResult(quecResult.getCode());
                        adapter.getData().get(i).setMessage(quecResult.getMessage());
                        adapter.notifyItemChanged(i);
                    }
                }
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
    @OnClick({R.id.iv_back,R.id.bt_scan})
    public void buttonClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;

            case R.id.bt_scan:

                if(MyUtils.isFastClick()){
                    return;
                }

                if("开始扫描".equals(bt_scan.getText().toString())){
                    bt_scan.setText("停止扫描");
                    getDevice();
                    return;
                }

                if("停止扫描".equals(bt_scan.getText().toString())){
                    bt_scan.setText("开始扫描");
                    BleServiceLocater.getService(IBleService.class).stopScan();
                    return;
                }

                break;
        }

    }

    private void getDevice(){

        BleServiceLocater.getService(IBleService.class).startScan("","", new IDeviceScanCallback() {
            @Override
            public void onScan(DeviceBean deviceBean) {
                if(getData(adapter.getData(),deviceBean)!=null){
                    SmartConfigDevice device = new SmartConfigDevice();
                    device.setDeviceBean(deviceBean);
                    adapter.addData(device);
                }

            }

            @Override
            public void onFail(QuecError quecError) {

            }

        });
    }

    private DeviceBean getData(List<SmartConfigDevice> list, DeviceBean deviceBean){

        if(list == null|| list.size()==0){

            return deviceBean;
        }
        for(int  i=0 ;i<list.size();i++){
            if(deviceBean.getMac().equals(list.get(i).getDeviceBean().getMac())){
                return null;
            }
        }
        return  deviceBean;
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (disposableFirst != null ) {
            disposableFirst.dispose();
            disposableFirst = null;
        }
        finishLoading();
    }



}
