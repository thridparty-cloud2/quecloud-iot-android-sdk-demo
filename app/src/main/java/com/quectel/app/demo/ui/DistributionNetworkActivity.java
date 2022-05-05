package com.quectel.app.demo.ui;

import android.Manifest;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.SystemClock;
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
import com.quectel.app.blesdk.ble.ScanDevice;
import com.quectel.app.blesdk.bleservice.IBleCallBack;
import com.quectel.app.blesdk.bleservice.IBleService;
import com.quectel.app.blesdk.bleservice.IFeedbackCallBack;
import com.quectel.app.blesdk.bleservice.IParseDataListener;
import com.quectel.app.blesdk.bleservice.IScanCallBack;
import com.quectel.app.blesdk.constant.DataStyle;
import com.quectel.app.blesdk.ttlv.EncodeResult;
import com.quectel.app.blesdk.ttlv.ParseResultData;
import com.quectel.app.blesdk.ttlv.ReceiveTTLVData;
import com.quectel.app.blesdk.ttlv.TTLVData;
import com.quectel.app.blesdk.utils.BleServiceLocater;
import com.quectel.app.blesdk.utils.DecodeTools;
import com.quectel.app.blesdk.utils.EncodeTools;
import com.quectel.app.blesdk.utils.StringUtils;
import com.quectel.app.demo.R;
import com.quectel.app.demo.adapter.DeviceAdapter;
import com.quectel.app.demo.adapter.ScanResultAdapter;
import com.quectel.app.demo.base.BaseActivity;
import com.quectel.app.demo.bean.DeviceGroupVO;
import com.quectel.app.demo.bean.WifiData;
import com.quectel.app.demo.utils.MyUtils;
import com.quectel.app.demo.utils.ToastUtils;
import com.quectel.app.demo.widget.BottomItemDecorationSystem;
import com.quectel.app.device.deviceservice.IDevService;
import com.quectel.app.device.param.AddDeviceParam;
import com.quectel.app.device.utils.DeviceServiceFactory;
import com.quectel.app.quecnetwork.httpservice.IHttpCallBack;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

//wifi配网
public class DistributionNetworkActivity extends BaseActivity {

//    private static String[] PERMISSIONS = {
//            Manifest.permission.BLUETOOTH_CONNECT
//    };

//    UUID SERVER_UUID = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb");
//    UUID notifyUUID = UUID.fromString("00009c40-0000-1000-8000-00805f9b34fb");
//    UUID writeUUID = UUID.fromString("00009c40-0000-1000-8000-00805f9b34fb");

    String notifyUUID = "00009c40-0000-1000-8000-00805f9b34fb";
    String writeUUID = "00009c40-0000-1000-8000-00805f9b34fb";
    String macAddress = "64:C4:03:E7:AA:D4";
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

        //蓝牙扫码位置 权限
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION //30
                )
                .subscribe(new Consumer<Boolean>() {
                               @Override
                               public void accept(Boolean grant) throws Exception {
                                   if(grant)
                                   {
                                       System.out.println("权限同意");
                                   }
                               }
                           }

                );

        BluetoothAdapter bltadapter = BluetoothAdapter.getDefaultAdapter();
        boolean enabled = bltadapter.isEnabled();
        //如果没有打开蓝牙，请求打开蓝牙
        if(!enabled)
        {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
//                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(activity, PERMISSIONS, 1111);
//                    return;
//                }
//            }

            startActivityForResult(intent, 1);
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleServiceLocater.getService(IBleService.class).release();
    }

    private Disposable disposableFirst = null;
    @OnClick({R.id.iv_back,R.id.bt_scan,R.id.bt_stop_scan,R.id.bt_connect,R.id.bt_write})
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
                createScanResultDialog();
                break;

            case R.id.bt_stop_scan:
                if(MyUtils.isFastClick()){
                    return;
                }
                BleServiceLocater.getService(IBleService.class).stopScan();
                break;

            case R.id.bt_connect:

                createMacDialog();

                break;

            case R.id.bt_write:

                createDialog();

                break;

        }

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

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions,
                                           @NonNull final int[] grantResults) {
        if (requestCode == 1111)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                System.out.println("权限通过 权限通过权限通过权限通过");
            }
            else
            {
                System.out.println("没有同意连接蓝牙");
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }



    private void createDialog()
    {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.wifi_config_net_dialog, null);
        final Dialog mDialog = new Dialog(activity, R.style.dialogTM);
        mDialog.setContentView(view);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        EditText edit_ssid = (EditText) mDialog.findViewById(R.id.edit_ssid);
        EditText edit_pass = (EditText) mDialog.findViewById(R.id.edit_pass);
        Button bt_sure = (Button) mDialog.findViewById(R.id.bt_sure);
        Button bt_cancel = (Button) mDialog.findViewById(R.id.bt_cancel);

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });
        bt_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ssid =  MyUtils.getEditTextContent(edit_ssid);
                String pass =  MyUtils.getEditTextContent(edit_pass);

                if(TextUtils.isEmpty(ssid))
                {
                    ToastUtils.showShort(activity,"输入wifi名称");
                    return;
                }
                if(TextUtils.isEmpty(pass))
                {
                    ToastUtils.showShort(activity,"输入wifi密码");
                    return;
                }
                mDialog.dismiss();

                configNet(ssid,pass);
            }
        });

        mDialog.show();
    }

    private void configNet(String ssid,String pass)
    {
        byte[] bytes = ssid.getBytes(StandardCharsets.UTF_8);
        byte[] bytes2 = pass.getBytes(StandardCharsets.UTF_8);
        TTLVData<byte[]> test1 = new TTLVData<byte[]>((short) 1, (short)3, bytes, true) {
        };
        TTLVData<byte[]> test2 = new TTLVData<byte[]>((short) 2, (short)3, bytes2, true) {
        };
        List<TTLVData> list = new ArrayList<TTLVData>();
        list.add(test1);
        list.add(test2);
        //ttlv 数据编码
        EncodeResult encodeResult = EncodeTools.getInstance().startEncode((short) 0x7010, list);
        //wifi 配网 写入数据
        startLoading();
        BleServiceLocater.getService(IBleService.class).write(writeUUID, encodeResult.getCmdData(), new IFeedbackCallBack() {
            @Override
            public void receiveData(byte[] data) {
                ToastUtils.showShort(activity,"write success");
            }
            @Override
            public void onFail(Throwable throwable) {
            }
        });
    }

    private void createScanResultDialog()
    {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.ble_scan_result_dialog, null);
        final Dialog mDialog = new Dialog(activity, R.style.dialogTM);
        mDialog.setContentView(view);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        RecyclerView mList = (RecyclerView) mDialog.findViewById(R.id.mList);
         List<ScanDevice> listContent  = new ArrayList<ScanDevice>();

        mList.setLayoutManager(new LinearLayoutManager(activity));
        mList.addItemDecoration(new BottomItemDecorationSystem(activity));
        ScanResultAdapter  mAdapter = new ScanResultAdapter(activity, listContent);
        mList.setAdapter(mAdapter);
        Button bt_cancel = (Button) mDialog.findViewById(R.id.bt_cancel);
        Button bt_stop = (Button) mDialog.findViewById(R.id.bt_stop);
        bt_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BleServiceLocater.getService(IBleService.class).stopScan();
            }
        });

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BleServiceLocater.getService(IBleService.class).stopScan();
                mDialog.dismiss();
            }
        });
        mDialog.show();
         Set<ScanDevice> useSet = new HashSet<ScanDevice>();
        BleServiceLocater.getService(IBleService.class).scan(
                "", "", new IScanCallBack() {
                    @Override
                    public void onScan(ScanDevice scanDevice) {
                        if(!TextUtils.isEmpty(scanDevice.getName()))
                        {
                            System.out.println("ScanDevice--:"+scanDevice);
                        }
                        useSet.add(scanDevice);
                        List<ScanDevice> dataList = mAdapter.getData();
                        dataList.clear();
                        dataList.addAll(useSet);
                        mAdapter.notifyDataSetChanged();
                        mAdapter.addChildClickViewIds(R.id.bt_copy);
                        mAdapter.setOnItemChildClickListener(new OnItemChildClickListener() {
                            @Override
                            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                                if (view.getId() == R.id.bt_copy) {
                                    ScanDevice sd = mAdapter.getData().get(position);
                                    MyUtils.copyContentToClipboard(activity,sd.getMac());
                                    ToastUtils.showShort(activity,"复制成功");
                                }
                            }
                        });

                    }
                    @Override
                    public void onFail(Throwable e) {
                        e.printStackTrace();
                    }
                }
        );
    }


    private void createMacDialog()
    {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.mac_dialog, null);
        final Dialog mDialog = new Dialog(activity, R.style.dialogTM);
        mDialog.setContentView(view);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        EditText edit_mac = (EditText) mDialog.findViewById(R.id.edit_mac);
        Button bt_cancel = (Button) mDialog.findViewById(R.id.bt_cancel);
        Button bt_sure = (Button) mDialog.findViewById(R.id.bt_sure);
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });

        bt_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                String mac = MyUtils.getEditTextContent(edit_mac);
                if(TextUtils.isEmpty(mac))
                {
                    ToastUtils.showShort(activity,"请输入mac地址");
                    return;
                }

                  connectDevice(mac);
            }
        });
        mDialog.show();
    }


    private void connectDevice(String macAddress)
    {
        startLoading();
        BleServiceLocater.getService(IBleService.class).connectDevice(macAddress, new IBleCallBack() {
            @Override
            public void onSuccess() {
                finishLoading();
                ToastUtils.showShort(activity,"连接成功");
                SystemClock.sleep(500);
                //连接成功 设置notify 监听数据
                BleServiceLocater.getService(IBleService.class).setNotify(notifyUUID, new IFeedbackCallBack() {
                    @Override
                    public void receiveData(byte[] data) {
                        //解码ttlv 包
                        DecodeTools.getInstance().packetSlice(data, new IParseDataListener() {
                            @Override
                            public void onSuccess(ParseResultData resultData) {
                                Map<Integer, ReceiveTTLVData> paramMap = resultData.getValueMap();
                                Set<Map.Entry<Integer, ReceiveTTLVData>> me = paramMap.entrySet();
                                WifiData wifiData = new WifiData();
                                for (Iterator<Map.Entry<Integer, ReceiveTTLVData>> it = me.iterator(); it.hasNext();)
                                {
                                    Map.Entry<Integer,ReceiveTTLVData> mapValue = it.next();
                                    int key =  mapValue.getKey();
                                    System.out.println("key---:"+key);
                                    ReceiveTTLVData useValue = mapValue.getValue();
                                    String dataType = useValue.getData().getClass().getSimpleName();
                                    switch (dataType)
                                    {
                                        case  DataStyle.BYTE:
                                            byte[] data1 = (byte[]) useValue.getData();
                                            String str =  new String(data1, StandardCharsets.UTF_8);
                                            System.out.println("byte str --:" + str);
                                            if(wifiData.isEffective()&&key==7)
                                            {
                                                wifiData.setPk(str);
                                            }
                                            else if(wifiData.isEffective()&&key==8)
                                            {
                                                wifiData.setDk(str);
                                            }
                                            else if(wifiData.isEffective()&&key==9)
                                            {
                                                wifiData.setAuthCode(str);
                                            }
                                            break;
                                        case  DataStyle.LONG:
                                            long data2 = (Long) useValue.getData();
                                            System.out.println("LONG content --:" + data2);
                                            break;
                                        case  DataStyle.DOUBLE:

                                            double data3 = (double) useValue.getData();
                                            System.out.println("double content --:" + data3);
                                            break;

                                        case  DataStyle.BOOLEAN:
                                            boolean data4 = (boolean) useValue.getData();
                                            if(key==6)
                                            {
                                                wifiData.setEffective(data4);
                                            }
                                            System.out.println("BOOLEAN content --:" + data4);
                                            break;
                                    }

                                }

                                finishLoading();
                                System.out.println("wifiData--:"+wifiData);
                                disposableFirst = Observable.interval(0, 2, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                                        .take(8 + 1)
                                        .subscribe(new Consumer<Long>() {
                                            @Override
                                            public void accept(Long aLong) throws Exception {
                                                startLoading();
                                                DeviceServiceFactory.getInstance().getService(IDevService.class).bindDeviceByWifi("",
                                                        wifiData.getPk(), wifiData.getDk(), wifiData.getAuthCode(), new IHttpCallBack() {
                                                            @Override
                                                            public void onSuccess(String result) {
                                                                finishLoading();
                                                                //{"code":5460,"msg":"未上报bindingCode","data":null}
                                                                try {
                                                                    JSONObject obj = new JSONObject(result);
                                                                    if (obj.getInt("code") == 200) {
                                                                        ToastUtils.showLong(activity,"配网成功");
                                                                        disposableFirst.dispose();
                                                                        finish();
                                                                    }
                                                                    else
                                                                    {
                                                                        ToastUtils.showShort(activity,obj.getString("msg"));
                                                                    }
                                                                } catch (JSONException e) {
                                                                    e.printStackTrace();
                                                                }

                                                            }

                                                            @Override
                                                            public void onFail(Throwable e) {
                                                                e.printStackTrace();
                                                                finishLoading();
                                                            }
                                                        }
                                                );
                                            }
                                        }, new Consumer<Throwable>() {
                                            @Override
                                            public void accept(Throwable throwable) throws Exception {

                                            }
                                        }, new Action() {
                                            @Override
                                            public void run() throws Exception {
                                                System.out.println("Action--:");
                                                ToastUtils.showShort(activity,"配网失败");
                                                finish();
                                            }
                                        });

                            }
                            @Override
                            public void onProcessing(String msg) {
                                System.out.println("onFail--:"+msg);

                            }
                        });

                    }
                    @Override
                    public void onFail(Throwable e) {
                        finishLoading();
                        ToastUtils.showShort(activity,"Connect Fail");
                        e.printStackTrace();
                    }
                });

            }
            @Override
            public void onFail(Throwable e) {
                finishLoading();
                e.printStackTrace();
            }
        });
    }



}
