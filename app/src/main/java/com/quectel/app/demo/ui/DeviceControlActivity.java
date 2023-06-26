package com.quectel.app.demo.ui;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.quectel.app.demo.R;
import com.quectel.app.demo.adapter.DeviceModelAdapter;
import com.quectel.app.demo.base.BaseActivity;
import com.quectel.app.demo.constants.TSLConfig;
import com.quectel.app.demo.utils.AddOperate;
import com.quectel.app.demo.utils.MyUtils;
import com.quectel.app.demo.utils.ToastUtils;
import com.quectel.app.demo.widget.BottomItemDecorationSystem;
import com.quectel.app.device.bean.ArraySpecs;
import com.quectel.app.device.bean.ArrayStructSpecs;
import com.quectel.app.device.bean.BatchControlDevice;
import com.quectel.app.device.bean.BooleanSpecs;
import com.quectel.app.device.bean.BusinessValue;
import com.quectel.app.device.bean.ModelBasic;
import com.quectel.app.device.bean.NumSpecs;
import com.quectel.app.device.bean.QuecProductTSLPropertyModel;
import com.quectel.app.device.bean.TSLProfile;
import com.quectel.app.device.bean.TextSpecs;
import com.quectel.app.device.callback.IDeviceTSLModelCallback;
import com.quectel.app.device.constant.ModelStyleConstant;
import com.quectel.app.device.deviceservice.IDevService;

import com.quectel.app.device.receiver.NetStatusReceiver;
import com.quectel.app.device.utils.DeviceServiceFactory;
import com.quectel.app.device.utils.ObjectModelParse;

import com.quectel.app.quecnetwork.httpservice.IHttpCallBack;
import com.quectel.app.usersdk.userservice.IUserService;
import com.quectel.app.usersdk.utils.UserServiceFactory;
import com.quectel.app.websocket.deviceservice.IWebSocketService;
import com.quectel.app.websocket.utils.WebSocketServiceLocater;
import com.quectel.app.websocket.websocket.cmd.KValue;
import com.quectel.basic.common.entity.QuecResult;
import com.quectel.basic.common.utils.QuecThreadUtil;
import com.quectel.basic.queclog.QLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class DeviceControlActivity  extends BaseActivity {

    @Override
    protected int getContentLayout() {
        return R.layout.activity_device_control;
    }

    @Override
    protected void addHeadColor() {
        MyUtils.addStatusBarView(this,R.color.gray_bg);
    }

    String pk="";
    String dk = "";
   // @BindView(R.id.switch_button)
   // SwitchButton switch_button;
    NetStatusReceiver mReceiver = null;

    List<ModelBasic> list = new ArrayList<>();

//    @BindView(R.id.mList)
//    RecyclerView mRecyclerView;

    boolean isOnline = false;
    @Override
    protected void initData() {

//        mReceiver = new NetStatusReceiver();
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
//        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
//        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
//        registerReceiver(mReceiver, filter);
        Intent intent = getIntent();
        pk =  intent.getStringExtra("pk");
        dk =  intent.getStringExtra("dk");
        isOnline = intent.getBooleanExtra("online",false);

//        queryModelTSL();
//        getProductTSL(pk);
//        queryBusinessAttributes();

        // websocket 登录  在 EventType.EVENT_TYPE_LOGIN_SUCCESS 回调中 登录成功 然后订阅设备
        if(isOnline)
        {
            WebSocketServiceLocater.getService(IWebSocketService.class).login();
        }
    }

    public static final  int  DEVICE_ONLINE = 1;

//    //处理websocket 事件上报
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onEvent(SocketEvent socketEvent) {
//        System.out.println("socketEvent--:"+socketEvent);
//        String socketType =  socketEvent.getType();
//        String dataContent = null;
//        switch (socketType)
//        {
//            case  EventType.EVENT_TYPE_LOGIN_SUCCESS:
//                System.out.println("login success--:"+socketEvent.getData());
//
//                WebSocketServiceLocater.getService(IWebSocketService.class).subscribeDevice(dk,pk);
//                break;
//            //设备在线离线监听
//            case  EventType.EVENT_TYPE_ONLINE:
//                try {
//                    JSONObject obj = new JSONObject(socketEvent.getData());
//                    int value = obj.getJSONObject("data").getInt("value");
//                    if(value== DEVICE_ONLINE)
//                    {
//                        isOnline = true;
//                    }
//                    else
//                    {
//                        isOnline = false;
//                    }
//                    String result = value== DEVICE_ONLINE ?"在线":"离线";
//                    System.out.println("result-EVENT_TYPE_ONLINE-:"+result);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                break;
//            //处理物模型属性上报
//            case  EventType.EVENT_TYPE_M_ATTR_REPORT:
//                dataContent = socketEvent.getData();
//                try {
//                    JSONObject obj = new JSONObject(dataContent);
//                    JSONObject dataObject = obj.getJSONObject("data");
//                    String deviceKey = obj.getString("deviceKey");
//                    if(deviceKey.equals(dk))
//                    {
//                        //结构体上报 数据 code标识符:value
//                        //{"kv":{"task":{"Time_Refresh":false,"time_duration":"16"}}
//                        //数组上报
//                        //{"kv":{"array":["6","11","18"]}
//                        //{"kv":{"array_twotest":["111","2222","333333"]}
//                        //数组嵌套结构体  上报数据  code:value
//                        //{"kv":{"Array_Struct":[{"test1":false,"test2":"2"},{"test1":false,"test2":"2"}]}
//                        JSONObject kvObject = dataObject.getJSONObject("kv");
//                        for(BusinessValue bv: contentList)
//                        {
//                            String code =   bv.getResourceCode();
//                           if(kvObject.has(code))
//                           {
//                                 String content =   kvObject.getString(code);
//                                 bv.setResourceValce(content);
//                           }
//                        }
//                        mAdapter.notifyDataSetChanged();
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                break;
//            //下发指令响应
//            case  EventType.EVENT_TYPE_CMD_ACK:
//                dataContent = socketEvent.getData();
//                try {
//                    JSONObject obj = new JSONObject(dataContent);
//                    String status = obj.getString("status");
//                    System.out.println("status--:"+status);
//                    if(!WebSocketConfig.CMD_ACK_STATUS_SUCCESS.equals(status))
//                    {
//                        //应答失败,处理数据回滚
//                    }
//                    else
//                    {
//                        ToastUtils.showShort(activity,"下发成功");
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                break;
//            case  EventType.EVENT_TYPE_WEBSOCKET_DEVICE_UNBIND:
//                dataContent = socketEvent.getData();
//
//
//                break;
//            case  EventType.EVENT_TYPE_WEBSOCKET_LOGIN_FAILURE:
//
//
//                break;
//            case  EventType.EVENT_TYPE_WEBSOCKET_ERROR:
//                try {
//                    JSONObject obj = new JSONObject(socketEvent.getData());
//                    String msg = obj.getString("msg");
//                    ToastUtils.showShort(activity,msg);
//                    BusinessValue item = mAdapter.getData().get(cachePosition);
//                    String type = item.getDataType();
//                    if(item.getDataType().equals(ModelStyleConstant.BOOL))
//                    {
//                       View view =  cacheMap.get(cachePosition);
//                        SwitchButton switch_button =  view.findViewById(R.id.switch_button);
//                        boolean value = Boolean.parseBoolean(item.getResourceValce());
//                        switch_button.setChecked(value);
//                    }
//                    else if(type.equals(ModelStyleConstant.INT)|| type.equals(ModelStyleConstant.FLOAT)
//                            ||type.equals(ModelStyleConstant.DOUBLE)||type.equals(ModelStyleConstant.ENUM)
//                            ||type.equals(ModelStyleConstant.DATE)||type.equals(ModelStyleConstant.TEXT)
//                            ||type.equals(ModelStyleConstant.ARRAY)||type.equals(ModelStyleConstant.STRUCT)
//                    )
//                    {
//                        BusinessValue businessValue = numberCacheMap.get(cachePosition);
//                        contentList.set(cachePosition,businessValue);
//                        mAdapter.notifyDataSetChanged();
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                break;
//            case  EventType.EVENT_TYPE_WEBSOCKET_LOCATION:
//
//
//                break;
//            case  EventType.EVENT_TYPE_M_EVENT_REPORT:
//
//                break;
//        }
//    }



//    @OnClick({R.id.iv_back})
//    public void onViewClick(View view) {
//        Intent intent = null;
//        switch (view.getId()) {
//            case R.id.iv_back:
//                finish();
//                break;
//        }
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WebSocketServiceLocater.getService(IWebSocketService.class).disconnect();
//        if (mReceiver != null) {
//            unregisterReceiver(mReceiver);
//        }
    }

    public void getProductTSLByCache(String pk,String dk){
        IDevService devService = DeviceServiceFactory.getInstance().getService(IDevService.class);

        devService.getProductTSLValueWithProductKey(pk, dk, "", "", new ArrayList<String>(), new ArrayList<String>(), new IDeviceTSLModelCallback() {
            @Override
            public void onResultCallback(List<QuecProductTSLPropertyModel<?>> list) {

            }

            @Override
            public void onFail(Throwable throwable) {

            }
        });
    }









    List<BusinessValue> readList = new ArrayList<BusinessValue>();
    List<BusinessValue> readWriteList = new ArrayList<BusinessValue>();
    List<BusinessValue> contentList = new ArrayList<BusinessValue>();
    DeviceModelAdapter mAdapter = null;
    KValue booleanData = null;

    int cachePosition = -1;
    HashMap<Integer,View> cacheMap = new HashMap<Integer,View>();

    HashMap<Integer,BusinessValue> numberCacheMap = new HashMap<Integer,BusinessValue>();

    private void sendWebSocketBasicData(KValue data,String dk,String pk)
    {
        WebSocketServiceLocater.getService(IWebSocketService.class).writeWebSocketBaseData(data,dk,pk);
    }


    private void createSendDialog(NumSpecs numSpecs,String step,BusinessValue item)
    {
        numberCacheMap.put(cachePosition,item);
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.send_model_command_dialog, null);
        final Dialog mDialog = new Dialog(activity, R.style.dialogTM);
        mDialog.setContentView(view);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        EditText edit_content = (EditText) mDialog.findViewById(R.id.edit_content);
        edit_content.setText(item.getResourceValce());
        TextView tv_step = (TextView) mDialog.findViewById(R.id.tv_step);
        Button bt_cancel = (Button) mDialog.findViewById(R.id.bt_cancel);
        Button bt_sure = (Button) mDialog.findViewById(R.id.bt_sure);
        Button bt_sub = (Button) mDialog.findViewById(R.id.bt_sub);
        Button bt_add = (Button) mDialog.findViewById(R.id.bt_add);
        tv_step.setText(step);

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
                if(isOnline)
                {
                    //websocket 下发
                    KValue data = new KValue(item.getAbId(),item.getName(), item.getDataType(),item.getResourceValce());
                    sendWebSocketBasicData(data,dk,pk);
                    contentList.set(cachePosition,item);
                    mAdapter.notifyDataSetChanged();
                }
                else
                {
                    //http下发
                    try {
                        JSONObject obj =   new JSONObject();
                        obj.put(item.getResourceCode(),item.getResourceValce());
                        String data =  new JSONArray().put(obj).toString();
                        sendBaseHttpData(data,pk,dk);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        bt_sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String  result  =  AddOperate.sub(item.getResourceValce(),numSpecs.getStep());
                item.setResourceValce(result);
                edit_content.setText(result);
            }
        });

        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String result =  AddOperate.add(item.getResourceValce(),numSpecs.getStep());
                item.setResourceValce(result);
                edit_content.setText(result);
            }
        });

        mDialog.show();
    }


    private void createSendEnumDialog( List<BooleanSpecs> specs,BusinessValue item)
    {
        numberCacheMap.put(cachePosition,item);
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.send_model_enum_dialog, null);
        final Dialog mDialog = new Dialog(activity, R.style.dialogTM);
        mDialog.setContentView(view);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        EditText edit_value = (EditText) mDialog.findViewById(R.id.edit_value);
        TextView tv_enum_name = (TextView) mDialog.findViewById(R.id.tv_enum_name);
        TextView tv_enum_value = (TextView) mDialog.findViewById(R.id.tv_enum_value);
        Button bt_cancel = (Button) mDialog.findViewById(R.id.bt_cancel);
        Button bt_sure = (Button) mDialog.findViewById(R.id.bt_sure);
        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        for(BooleanSpecs bp: specs )
        {
            sb1.append(bp.name);
            sb1.append(" ");
            sb2.append(bp.value);
            sb2.append(" ");
        }
        tv_enum_name.setText("Enum name: "+sb1.toString());
        tv_enum_value.setText("Enum value: "+sb2.toString());
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

                if(isOnline)
                {
                    String enumValue = MyUtils.getEditTextContent(edit_value);
                    KValue data = new KValue(item.getAbId(),item.getName(), item.getDataType(),enumValue);
                    sendWebSocketBasicData(data,dk,pk);
                    item.setResourceValce(enumValue);
                    contentList.set(cachePosition,item);
                    mAdapter.notifyDataSetChanged();
                }
                else
                {
                    String enumValue = MyUtils.getEditTextContent(edit_value);
                    try {
                        JSONObject obj =   new JSONObject();
                        obj.put(item.getResourceCode(),enumValue);
                        String data =  new JSONArray().put(obj).toString();
                        sendBaseHttpData(data,pk,dk);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        mDialog.show();
    }


    private void createDateOrTextDialog(TextSpecs specs, BusinessValue item)
    {
        numberCacheMap.put(cachePosition,item);
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.send_model_date_text_dialog, null);
        final Dialog mDialog = new Dialog(activity, R.style.dialogTM);
        mDialog.setContentView(view);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        EditText edit_value = (EditText) mDialog.findViewById(R.id.edit_value);
         edit_value.setText(item.getResourceValce());
        TextView tv_text_length = (TextView) mDialog.findViewById(R.id.tv_text_length);
         if(specs!=null)
         {
             tv_text_length.setText("文本长度: "+specs.getLength());
         }
         else
         {
             tv_text_length.setVisibility(View.GONE);
         }

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
                if(isOnline)
                {
                    String enumValue = MyUtils.getEditTextContent(edit_value);
                    KValue data = new KValue(item.getAbId(),item.getName(), item.getDataType(),enumValue);
                    sendWebSocketBasicData(data,dk,pk);
                    item.setResourceValce(enumValue);
                    contentList.set(cachePosition,item);
                    mAdapter.notifyDataSetChanged();
                }
                else
                {
                    String enumValue = MyUtils.getEditTextContent(edit_value);
                    try {
                        JSONObject obj =   new JSONObject();
                        obj.put(item.getResourceCode(),enumValue);
                        String data =  new JSONArray().put(obj).toString();
                        sendBaseHttpData(data,pk,dk);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        mDialog.show();
    }

    //下发结构体数据
    private void createSendStructDialog(List<ModelBasic> specs, BusinessValue item)
    {
        numberCacheMap.put(cachePosition,item);
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.send_model_struct_dialog, null);
        final Dialog mDialog = new Dialog(activity, R.style.dialogTM);
        mDialog.setContentView(view);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
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
                //    * 属性array
                //    * "[{\"key\":[{\"id\":\"value1\"},{\"id\":\"value2\"}]}]"（id为0）
                //    * 属性struct
                //    * "[{\"key\":[{\"key1\":\"value1\"},{\"key2\":\"value2\"}]}]"
                //    * 属性array含有struct
                //    * "[{\"key\":[{\"id\":[{\"key1\":\"value1\"}]},{\"id\":[{\"key2\":\"value2\"}]}]}]"（id为0）
                if(specs!=null&&specs.size()>0)
                {
                    if(isOnline)
                    {
                        List<KValue> mListChild = new ArrayList<KValue>();
                        for(ModelBasic mb : specs)
                        {
                            if(mb.getDataType().equals(ModelStyleConstant.BOOL))
                            {
                                KValue  v1 = new KValue(mb.getId(),mb.getName(),ModelStyleConstant.BOOL,"true");
                                mListChild.add(v1);
                            }
                            else if(mb.getDataType().equals(ModelStyleConstant.INT))
                            {
                                KValue  v1 = new KValue(mb.getId(),mb.getName(),ModelStyleConstant.INT,55);
                                mListChild.add(v1);
                            }
                            else if(mb.getDataType().equals(ModelStyleConstant.ENUM))
                            {
                                List<BooleanSpecs> specs = mb.getSpecs();
                                //遍历枚举值
                                for(BooleanSpecs bs : specs)
                                {
                                    System.out.println("bs--:"+bs.getValue());
                                }
                                KValue  v1 = new KValue(mb.getId(),mb.getName(),ModelStyleConstant.ENUM,specs.get(0).getValue());
                                mListChild.add(v1);
                            }
                            else if(mb.getDataType().equals(ModelStyleConstant.FLOAT))
                            {
                                KValue  v1 = new KValue(mb.getId(),mb.getName(),ModelStyleConstant.FLOAT,"22.2");
                                mListChild.add(v1);
                            }
                            else if(mb.getDataType().equals(ModelStyleConstant.DOUBLE))
                            {
                                KValue  v1 = new KValue(mb.getId(),mb.getName(),ModelStyleConstant.DOUBLE,"33.33");
                                mListChild.add(v1);
                            }
                            else if(mb.getDataType().equals(ModelStyleConstant.TEXT))
                            {
                                KValue  v1 = new KValue(mb.getId(),mb.getName(),ModelStyleConstant.TEXT,"test_content");
                                mListChild.add(v1);
                            }
                            else if(mb.getDataType().equals(ModelStyleConstant.DATE))
                            {
                                KValue  v1 = new KValue(mb.getId(),mb.getName(),ModelStyleConstant.DATE,String.valueOf(new Date().getTime()));
                                mListChild.add(v1);
                            }

                        }

                        WebSocketServiceLocater.getService(IWebSocketService.class).writeWebSocketArrayOrStructBaseData(item.getAbId(),
                                item.getName(),mListChild,ModelStyleConstant.STRUCT,dk,pk);
                    }
                    else {
                        // * "[{\"key\":[{\"key1\":\"value1\"},{\"key2\":\"value2\"}]}]"
                        try {
                            JSONObject obj =   new JSONObject();
                            JSONArray childArray = new JSONArray();
                            for(ModelBasic mb : specs) {
                                if(mb.getDataType().equals(ModelStyleConstant.BOOL)) {
                                    JSONObject child1 =   new JSONObject();
                                    child1.put(mb.getCode(),"true");
                                    childArray.put(child1);
                                } else if(mb.getDataType().equals(ModelStyleConstant.INT)) {
                                    JSONObject child1 =   new JSONObject();
                                    child1.put(mb.getCode(),88);
                                    childArray.put(child1);
                                }
                                else if(mb.getDataType().equals(ModelStyleConstant.ENUM))
                                {
                                    List<BooleanSpecs> specs = mb.getSpecs();
                                    JSONObject child1 =   new JSONObject();
                                    child1.put(mb.getCode(),specs.get(0).getValue());
                                    childArray.put(child1);
                                }
                                else if(mb.getDataType().equals(ModelStyleConstant.FLOAT))
                                {
                                    JSONObject child1 =   new JSONObject();
                                    child1.put(mb.getCode(),12.2);
                                    childArray.put(child1);
                                }
                                else if(mb.getDataType().equals(ModelStyleConstant.DOUBLE))
                                {
                                    JSONObject child1 =   new JSONObject();
                                    child1.put(mb.getCode(),12.3);
                                    childArray.put(child1);
                                }
                                else if(mb.getDataType().equals(ModelStyleConstant.TEXT))
                                {
                                    JSONObject child1 =   new JSONObject();
                                    child1.put(mb.getCode(),"test_content");
                                    childArray.put(child1);
                                }
                                else if(mb.getDataType().equals(ModelStyleConstant.DATE))
                                {
                                    JSONObject child1 =   new JSONObject();
                                    child1.put(mb.getCode(),String.valueOf(new Date().getTime()));
                                    childArray.put(child1);
                                }

                            }
                            obj.put(item.getResourceCode(),childArray);
                            String data =  new JSONArray().put(obj).toString();
                            sendBaseHttpData(data,pk,dk);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        });

        mDialog.show();
    }
    //发送数组包含基本类型数据
    private void createSendSimpleArrayDialog(ArraySpecs specs, BusinessValue item)
    {
        numberCacheMap.put(cachePosition,item);
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.send_model_struct_dialog, null);
        final Dialog mDialog = new Dialog(activity, R.style.dialogTM);
        mDialog.setContentView(view);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
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
                if(specs!=null)
                {
                    if(isOnline)
                    {
                        List<KValue> mListChild = new ArrayList<KValue>();
                        //根据 getDataType  判断 数组里添加什么类型的数据  添加数据不能超过   specs.getSize()
                        if(specs.getDataType().equals(ModelStyleConstant.INT))
                        {
                            KValue  v1 = new KValue(0,"",ModelStyleConstant.INT,8);
                            mListChild.add(v1);
                        }
                        else  if(specs.getDataType().equals(ModelStyleConstant.BOOL))
                        {
                            KValue  v1 = new KValue(0,"",ModelStyleConstant.BOOL,"true");
                            mListChild.add(v1);
                        }
                        else  if(specs.getDataType().equals(ModelStyleConstant.ENUM))
                        {
                            KValue  v1 = new KValue(0,"",ModelStyleConstant.ENUM,1);
                            mListChild.add(v1);
                        }
                        else  if(specs.getDataType().equals(ModelStyleConstant.FLOAT))
                        {
                            KValue  v1 = new KValue(0,"",ModelStyleConstant.FLOAT,2.3);
                            mListChild.add(v1);
                        }
                        else  if(specs.getDataType().equals(ModelStyleConstant.DOUBLE))
                        {
                            KValue  v1 = new KValue(0,"",ModelStyleConstant.DOUBLE,3.3);
                            mListChild.add(v1);
                        }
                        else  if(specs.getDataType().equals(ModelStyleConstant.TEXT))
                        {
                            KValue  v1 = new KValue(0,"",ModelStyleConstant.TEXT,"text");
                            mListChild.add(v1);
                        }
                        else  if(specs.getDataType().equals(ModelStyleConstant.DATE))
                        {
                            KValue  v1 = new KValue(0,"",ModelStyleConstant.DATE,String.valueOf(new Date().getTime()));
                            mListChild.add(v1);
                        }

                        WebSocketServiceLocater.getService(IWebSocketService.class).writeWebSocketArrayOrStructBaseData(item.getAbId(),
                                item.getName(),mListChild,ModelStyleConstant.ARRAY,dk,pk);
                    }
                    else
                    {
                        //"[{\"key\":[{\"id\":\"value1\"},{\"id\":\"value2\"}]}]"（id为0）
                        try {
                            JSONObject obj =   new JSONObject();
                            JSONArray childArray = new JSONArray();
                           if(specs.getDataType().equals(ModelStyleConstant.INT))
                           {
                               JSONObject child1 =   new JSONObject();
                               child1.put("0","77");
                               childArray.put(child1);
                           }
                            else if(specs.getDataType().equals(ModelStyleConstant.BOOL))
                            {
                                JSONObject child1 =   new JSONObject();
                                child1.put("0","false");
                                childArray.put(child1);
                            }
                           else if(specs.getDataType().equals(ModelStyleConstant.ENUM))
                           {
                               JSONObject child1 =   new JSONObject();
                               child1.put("0",1);
                               childArray.put(child1);
                           }
                           else if(specs.getDataType().equals(ModelStyleConstant.FLOAT))
                           {
                               JSONObject child1 =   new JSONObject();
                               child1.put("0",2.3);
                               childArray.put(child1);
                           }
                           else if(specs.getDataType().equals(ModelStyleConstant.DOUBLE))
                           {
                               JSONObject child1 =   new JSONObject();
                               child1.put("0",3.5);
                               childArray.put(child1);
                           }
                           else if(specs.getDataType().equals(ModelStyleConstant.TEXT))
                           {
                               JSONObject child1 =   new JSONObject();
                               child1.put("0","text");
                               childArray.put(child1);
                           }
                           else if(specs.getDataType().equals(ModelStyleConstant.DATE))
                           {
                               JSONObject child1 =   new JSONObject();
                               child1.put("0",String.valueOf(new Date().getTime()));
                               childArray.put(child1);
                           }

                            obj.put(item.getResourceCode(),childArray);
                            String data =  new JSONArray().put(obj).toString();
                            sendBaseHttpData(data,pk,dk);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        });

        mDialog.show();
    }

    //发送数组嵌套结构体
    private void createSendArrayContainStructDialog(ArrayStructSpecs specs, BusinessValue item)
    {
        numberCacheMap.put(cachePosition,item);
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.send_model_struct_dialog, null);
        final Dialog mDialog = new Dialog(activity, R.style.dialogTM);
        mDialog.setContentView(view);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
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
                if(specs!=null)
                {
                    if(isOnline)
                    {
                        List<ModelBasic> specs1 = specs.getSpecs();
                        List<KValue> ChildList1 = new ArrayList<KValue>();
                        List<KValue> ChildList2 = new ArrayList<KValue>();
                        //遍历结构体包含哪些类型
                        for(ModelBasic mb: specs1 )
                        {
                            if(mb.getDataType().equals(ModelStyleConstant.BOOL))
                            {
                                KValue  v11 = new KValue(mb.getId(),"1111",ModelStyleConstant.BOOL,"true");
                                ChildList1.add(v11);

                                KValue  v12 = new KValue(mb.getId(),"2222",ModelStyleConstant.BOOL,"false");
                                ChildList2.add(v12);
                            }
                           else if(mb.getDataType().equals(ModelStyleConstant.ENUM))
                            {
                                KValue  v21 = new KValue(mb.getId(),"enum1",ModelStyleConstant.ENUM,1);
                                ChildList1.add(v21);

                                KValue  v22 = new KValue(mb.getId(),"enum2",ModelStyleConstant.ENUM,2);
                                ChildList2.add(v22);
                            }
                            else if(mb.getDataType().equals(ModelStyleConstant.INT))
                            {
                                KValue  v31 = new KValue(mb.getId(),"int1",ModelStyleConstant.INT,5);
                                ChildList1.add(v31);

                                KValue  v32 = new KValue(mb.getId(),"int2",ModelStyleConstant.INT,6);
                                ChildList2.add(v32);
                            }
                            else if(mb.getDataType().equals(ModelStyleConstant.FLOAT))
                            {
                                KValue  v41 = new KValue(mb.getId(),"float",ModelStyleConstant.FLOAT,5.1);
                                ChildList1.add(v41);
                                KValue  v42 = new KValue(mb.getId(),"float",ModelStyleConstant.FLOAT,5.2);
                                ChildList2.add(v42);
                            }
                            else if(mb.getDataType().equals(ModelStyleConstant.DOUBLE))
                            {
                                KValue  v51 = new KValue(mb.getId(),"double",ModelStyleConstant.DOUBLE,6.1);
                                ChildList1.add(v51);
                                KValue  v52 = new KValue(mb.getId(),"double",ModelStyleConstant.DOUBLE,7.2);
                                ChildList2.add(v52);
                            }
                            else if(mb.getDataType().equals(ModelStyleConstant.TEXT))
                            {
                                KValue  v61 = new KValue(mb.getId(),"text",ModelStyleConstant.TEXT,"hello1");
                                ChildList1.add(v61);
                                KValue  v62 = new KValue(mb.getId(),"text",ModelStyleConstant.TEXT,"hello2");
                                ChildList2.add(v62);
                            }
                            else if(mb.getDataType().equals(ModelStyleConstant.DATE))
                            {
                                KValue  v71 = new KValue(mb.getId(),"date",ModelStyleConstant.DATE,String.valueOf(new Date().getTime()));
                                ChildList1.add(v71);
                                KValue  v72 = new KValue(mb.getId(),"date",ModelStyleConstant.DATE,String.valueOf(new Date().getTime()));
                                ChildList2.add(v72);
                            }

                        }

                        KValue  v1 = new KValue(0,"",ModelStyleConstant.STRUCT,ChildList1);
                        KValue  v2 = new KValue(0,"",ModelStyleConstant.STRUCT,ChildList2);
                        List<KValue> mListChild = new ArrayList<KValue>();
                        mListChild.add(v1);
                        mListChild.add(v2);
                        WebSocketServiceLocater.getService(IWebSocketService.class).writeWebSocketArrayContainStructData(item.getAbId(),item.getName(),
                                mListChild,dk,pk
                        );
                    }
                    else
                    {
                        //"[{\"key\":[{\"id\":[{\"key1\":\"value1\"}]},{\"id\":[{\"key2\":\"value2\"}]}]}]"（id为0）
                        List<ModelBasic> specs1 = specs.getSpecs();
                        JSONArray childArray1 = new JSONArray();
                        for(ModelBasic mb: specs1 )
                        {
                            if(mb.getDataType().equals(ModelStyleConstant.BOOL))
                            {
                                JSONObject child1 =   new JSONObject();
                                try {
                                    child1.put(mb.getCode(),"true");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                childArray1.put(child1);
                            }
                           else if(mb.getDataType().equals(ModelStyleConstant.ENUM))
                            {
                                JSONObject child1 =   new JSONObject();
                                try {
                                    child1.put(mb.getCode(),2);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                childArray1.put(child1);
                            }
                            else if(mb.getDataType().equals(ModelStyleConstant.INT))
                            {
                                JSONObject child1 =   new JSONObject();
                                try {
                                    child1.put(mb.getCode(),3);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                childArray1.put(child1);
                            }
                            else if(mb.getDataType().equals(ModelStyleConstant.FLOAT))
                            {
                                JSONObject child1 =   new JSONObject();
                                try {
                                    child1.put(mb.getCode(),3.1);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                childArray1.put(child1);
                            }
                            else if(mb.getDataType().equals(ModelStyleConstant.DOUBLE))
                            {
                                JSONObject child1 =   new JSONObject();
                                try {
                                    child1.put(mb.getCode(),5.6);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                childArray1.put(child1);
                            }
                            else if(mb.getDataType().equals(ModelStyleConstant.TEXT))
                            {
                                JSONObject child1 =   new JSONObject();
                                try {
                                    child1.put(mb.getCode(),"text");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                childArray1.put(child1);
                            }
                            else if(mb.getDataType().equals(ModelStyleConstant.DATE))
                            {
                                JSONObject child1 =   new JSONObject();
                                try {
                                    child1.put(mb.getCode(),String.valueOf(new Date().getTime()));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                childArray1.put(child1);
                            }

                        }
                        JSONObject c1 =   new JSONObject();
                        try {
                            c1.put("0",childArray1);
                        JSONArray array =  new JSONArray();
                        array.put(c1);
                        JSONObject obj =   new JSONObject();
                        obj.put(item.getResourceCode(),array);
                        String data =  new JSONArray().put(obj).toString();
                        sendBaseHttpData(data,pk,dk);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        mDialog.show();
    }


    public void sendBaseHttpData(String data,String pk,String dk)
    {
        List<BatchControlDevice> mList = new ArrayList<BatchControlDevice>();
        BatchControlDevice test1 =   new BatchControlDevice(pk, dk,"","");
        mList.add(test1);
        //缓存时间 1天
        int time =  60*60*24;
        DeviceServiceFactory.getInstance().getService(IDevService.class).batchControlDevice(data,mList,time,
                1,2,2,2,
                new IHttpCallBack() {
                    @Override
                    public void onSuccess(String result) {
                        System.out.println("batchControlDevice--:" + result);
                        try {
                            JSONObject obj =   new JSONObject(result);
                            JSONObject data =  obj.getJSONObject("data");
                            JSONArray jarray = data.getJSONArray("failureList");
                             if(jarray!=null&&jarray.length()>0)
                             {
                                 ToastUtils.showShort(activity,"http下发失败");
                             }
                             else
                             {
                                 ToastUtils.showShort(activity,"http下发成功");
                             }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFail(Throwable e) {
                        e.printStackTrace();
                    }
                }
        );

    }


}
