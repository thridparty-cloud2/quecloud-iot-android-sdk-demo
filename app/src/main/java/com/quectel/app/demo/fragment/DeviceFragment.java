package com.quectel.app.demo.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.quectel.app.demo.R;
import com.quectel.app.demo.adapter.DeviceAdapter;
import com.quectel.app.demo.adapter.LanAdapter;
import com.quectel.app.demo.bean.LanVO;
import com.quectel.app.demo.bean.UserDeviceList;
import com.quectel.app.demo.constant.DeviceConfig;
import com.quectel.app.demo.fragmentbase.BaseMainFragment;
import com.quectel.app.demo.ui.DeviceControlActivity;
import com.quectel.app.demo.ui.DistributionNetworkActivity;
import com.quectel.app.demo.utils.MyUtils;
import com.quectel.app.demo.utils.ToastUtils;
import com.quectel.app.demo.widget.BottomItemDecorationSystem;
import com.quectel.app.demo.widget.PayBottomDialog;
import com.quectel.app.device.bean.BusinessValue;
import com.quectel.app.device.deviceservice.IDevService;
import com.quectel.app.device.utils.DeviceServiceFactory;
import com.quectel.app.quecnetwork.httpservice.IHttpCallBack;
import com.quectel.app.usersdk.userservice.IUserService;
import com.quectel.app.usersdk.utils.UserServiceFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.http.RequestParams;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;


public class DeviceFragment extends BaseMainFragment {


    public static synchronized DeviceFragment newInstance() {
        DeviceFragment frag = new DeviceFragment();

        return frag;
    }

    @Override
    protected void getNeedArguments(Bundle bundle) {


    }

    @Override
    protected int getLayoutView() {
        return R.layout.device_layout;
    }

    @Override
    protected void processBusiness() {


    }

    @BindView(R.id.mList)
    RecyclerView mRecyclerView;
    DeviceAdapter mAdapter;

    @BindView(R.id.fragment_ptr_home_ptr_frame)
    PtrFrameLayout mPtrFrameLayout;

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new BottomItemDecorationSystem(getActivity()));
        queryDevice();

        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {

                return PtrDefaultHandler.checkContentCanBePulledDown(frame, mRecyclerView, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                refreshData();
            }
        });
        mPtrFrameLayout.disableWhenHorizontalMove(true);

    }

    private void queryDevice()
    {
        startLoading();
        DeviceServiceFactory.getInstance().getService(IDevService.class).queryUserDeviceList(1,50,
                new IHttpCallBack() {
                    @Override
                    public void onSuccess(String result) {
                        mPtrFrameLayout.refreshComplete();
                        finishLoading();
                        UserDeviceList userDeviceList = new Gson().fromJson(result, UserDeviceList.class);
                        List<UserDeviceList.DataBean.ListBean> mList = userDeviceList.getData().getList();
                        System.out.println("mList--:"+mList.size());

                        if(mList!=null&&mList.size()>0)
                        {
                            if(mAdapter==null)
                            {
                                mAdapter = new DeviceAdapter(getActivity(), mList);
                                mAdapter.setAnimationEnable(true);
                                mRecyclerView.setAdapter(mAdapter);
                            }
                            else
                            {
                                List<UserDeviceList.DataBean.ListBean> list = mAdapter.getData();
                                list.clear();
                                list.addAll(mList);
                                mAdapter.notifyDataSetChanged();
                            }

                            mAdapter.setOnItemClickListener(new OnItemClickListener() {
                                @Override
                                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                                    UserDeviceList.DataBean.ListBean lanVO = mAdapter.getData().get(position);
                                    createSelectDialog(lanVO.getProductKey(),lanVO.getDeviceKey(),lanVO.getShareCode(),lanVO.getDeviceStatus());

                                }
                            });



                        }

                    }
                    @Override
                    public void onFail(Throwable e) {
                        mPtrFrameLayout.refreshComplete();
                        e.printStackTrace();
                    }
                }
        );
    }

    private static final int PAGE_SIZE = 10;
    int  page = 0;
    private void refreshData() {
        queryDevice();

    }
    @OnClick({R.id.iv_add})
    public void buttonClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.iv_add:
                System.out.println("iv_add");
                View dialogView = getLayoutInflater().inflate(R.layout.bottom_pop_device_layout, null);
                PayBottomDialog myDialog = new PayBottomDialog(getActivity(), dialogView, new int[]{R.id.bt_cancel,
                        R.id.bt_sn_bind,R.id.bt_unbind,R.id.bt_receive_share,R.id.bt_wifi});
                myDialog.bottmShow();
                myDialog.setOnBottomItemClickListener(new PayBottomDialog.OnBottomItemClickListener() {
                    @Override
                    public void onBottomItemClick(PayBottomDialog dialog, View view) {
                        Intent intent = null;

                        switch (view.getId()) {
                            case R.id.bt_cancel:
                                myDialog.cancel();
                                break;

                            case R.id.bt_sn_bind:
                                myDialog.cancel();
                                createBindDialog();
                                break;

                            case R.id.bt_unbind:
                                myDialog.cancel();
                                createUnBindDialog();
                                break;

                            case R.id.bt_receive_share:
                                myDialog.cancel();
                                createReceiveShare();
                                break;

                            case R.id.bt_wifi:
                                myDialog.cancel();
                                intent = new Intent(getActivity(), DistributionNetworkActivity.class);
                                startActivity(intent);

                                break;
                        }
                    }
                });

                break;
        }

    }

    private void createReceiveShare()
    {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.receiver_accept_shareinformation_dialog, null);
        final Dialog mDialog = new Dialog(getActivity(), R.style.dialogTM);
        mDialog.setContentView(view);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        EditText edit_share_code = (EditText) mDialog.findViewById(R.id.edit_share_code);
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
                String code = MyUtils.getEditTextContent(edit_share_code);
                if(TextUtils.isEmpty(code))
                {
                    return;
                }
                startLoading();
                DeviceServiceFactory.getInstance().getService(IDevService.class).acceptShareDevice(code,
                        new IHttpCallBack() {
                            @Override
                            public void onSuccess(String result) {
                                finishLoading();
                                try {
                                    JSONObject  obj = new JSONObject(result);
                                    if (obj.getInt("code") == 200) {
                                        ToastUtils.showShort(getActivity(),"操作成功");
                                        queryDevice();
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
        });
        mDialog.show();
    }

    private void receiverCancelShare()
    {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.receiver_cancel_share_dialog, null);
        final Dialog mDialog = new Dialog(getActivity(), R.style.dialogTM);
        mDialog.setContentView(view);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        EditText edit_code = (EditText) mDialog.findViewById(R.id.edit_code);
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
                String code = MyUtils.getEditTextContent(edit_code);
                if(TextUtils.isEmpty(code))
                {
                    return;
                }
                startLoading();
                DeviceServiceFactory.getInstance().getService(IDevService.class).cancelShareByReceiver(code,
                        new IHttpCallBack() {
                            @Override
                            public void onSuccess(String result) {
                                finishLoading();
                                try {
                                    JSONObject  obj = new JSONObject(result);
                                    if (obj.getInt("code") == 200) {
                                        ToastUtils.showShort(getActivity(),"操作成功");
                                        queryDevice();
                                    }
                                    else
                                    {
                                        ToastUtils.showShort(getActivity(),obj.getString("msg"));
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
        });
        mDialog.show();
    }


    private void ownerCancelShare()
    {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.owner_cancel_share_dialog, null);
        final Dialog mDialog = new Dialog(getActivity(), R.style.dialogTM);
        mDialog.setContentView(view);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        EditText edit_code = (EditText) mDialog.findViewById(R.id.edit_code);
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
                String code = MyUtils.getEditTextContent(edit_code);
                if(TextUtils.isEmpty(code))
                {
                    return;
                }
                startLoading();
                DeviceServiceFactory.getInstance().getService(IDevService.class).cancelShareByOwner(code,
                        new IHttpCallBack() {
                            @Override
                            public void onSuccess(String result) {
                                finishLoading();
                                try {
                                    JSONObject  obj = new JSONObject(result);
                                    if (obj.getInt("code") == 200) {
                                        ToastUtils.showShort(getActivity(),"操作成功");
                                    }
                                    else
                                    {
                                        ToastUtils.showShort(getActivity(),obj.getString("msg"));
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
        });
        mDialog.show();
    }

    private void createBindDialog()
    {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.bind_device_dialog, null);
        final Dialog mDialog = new Dialog(getActivity(), R.style.dialogTM);
        mDialog.setContentView(view);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        EditText edit_pk = (EditText) mDialog.findViewById(R.id.edit_pk);
        EditText edit_sn = (EditText) mDialog.findViewById(R.id.edit_sn);
        EditText edit_device_name = (EditText) mDialog.findViewById(R.id.edit_device_name);
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
               String pk =  MyUtils.getEditTextContent(edit_pk);
               String sn =  MyUtils.getEditTextContent(edit_sn);
                if(TextUtils.isEmpty(pk))
                {
                    ToastUtils.showShort(getActivity(),"pk输入不能为空");
                    return;
                }
                if(TextUtils.isEmpty(sn))
                {
                    ToastUtils.showShort(getActivity(),"sn输入不能为空");
                    return;
                }
                String deviceName = "";
                String name =  MyUtils.getEditTextContent(edit_device_name);
                if(!TextUtils.isEmpty(name))
                {
                    deviceName = name;
                }

                mDialog.dismiss();
                DeviceServiceFactory.getInstance().getService(IDevService.class).bindDeviceSn(pk,sn,deviceName,
                        new IHttpCallBack() {
                            @Override
                            public void onSuccess(String result) {
                                //{"code":200,"msg":"","data":{"pk":"p111ti","dk":"888866669999001","moduleType":null}}
                                try {
                                    JSONObject  obj = new JSONObject(result);
                                    if (obj.getInt("code") == 200) {
                                        queryDevice();
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
        });

        mDialog.show();
    }

    private void createSelectDialog(String pk,String dk,String shareCode,String deviceStatus)
    {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.device_select_dialog, null);
        final Dialog mDialog = new Dialog(getActivity(), R.style.dialogTM);
        mDialog.setContentView(view);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        Button bt_changeDevice = (Button) mDialog.findViewById(R.id.bt_changeDevice);
        Button bt_queryDeviceInfor = (Button) mDialog.findViewById(R.id.bt_queryDeviceInfor);
        Button bt_set_share = (Button) mDialog.findViewById(R.id.bt_set_share);
        Button bt_queryShareUserList = (Button) mDialog.findViewById(R.id.bt_queryShareUserList);
        Button bt_queryUpgradePlan = (Button) mDialog.findViewById(R.id.bt_queryUpgradePlan);
        Button bt_owner_cancelShare = (Button) mDialog.findViewById(R.id.bt_owner_cancelShare);
        Button bt_cancelShareByReceiver = (Button) mDialog.findViewById(R.id.bt_cancelShareByReceiver);
        Button bt_device_control = (Button) mDialog.findViewById(R.id.bt_device_control);
        Button bt_query_group = (Button) mDialog.findViewById(R.id.bt_query_group);
        bt_query_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                startLoading();
                DeviceServiceFactory.getInstance().getService(IDevService.class).queryGroupListByDevice(pk, dk, new IHttpCallBack() {
                    @Override
                    public void onSuccess(String result) {
                        finishLoading();
                        ToastUtils.showShort(getActivity(),result);
                    }
                    @Override
                    public void onFail(Throwable e) {

                    }
                });
            }
        });

        bt_device_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                Intent intent = new Intent(getActivity(),DeviceControlActivity.class);
                intent.putExtra("pk",pk);
                intent.putExtra("dk",dk);
                if(deviceStatus.equals(DeviceConfig.OFFLINE))
                {
                    intent.putExtra("online",false);
                }
                else
                {
                    intent.putExtra("online",true);
                }
                startActivity(intent);

            }
        });

        Button bt_cancel = (Button) mDialog.findViewById(R.id.bt_cancel);
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });

        bt_cancelShareByReceiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                receiverCancelShare();
            }
        });

        bt_owner_cancelShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                ownerCancelShare();
            }
        });

        bt_queryUpgradePlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                startLoading();
                DeviceServiceFactory.getInstance().getService(IDevService.class).queryFetchPlan(pk, dk, new IHttpCallBack() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject  obj = new JSONObject(result);
                            if (obj.getInt("code") == 200) {
                                JSONObject  obj2 = obj.getJSONObject("data");
                                JSONObject  obj3 = (JSONObject) obj2.getJSONArray("components").get(0);
                                 String  componentNo =  obj3.getString("componentNo");
                                DeviceServiceFactory.getInstance().getService(IDevService.class).reportUpgradeStatus(pk,dk,componentNo,10,
                                        new IHttpCallBack() {
                                            @Override
                                            public void onSuccess(String result) {
                                               finishLoading();
                                                ToastUtils.showShort(getActivity(),result);
                                            }
                                            @Override
                                            public void onFail(Throwable e) {
                                                e.printStackTrace();
                                            }
                                        }
                                );
                            }
                            else
                            {
                                finishLoading();
                                ToastUtils.showShort(getActivity(),obj.getString("msg"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFail(Throwable e) {
                    }
                });

            }
        });

        bt_queryShareUserList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                startLoading();
                DeviceServiceFactory.getInstance().getService(IDevService.class).getDeviceShareUserList(pk, dk, new IHttpCallBack() {
                    @Override
                    public void onSuccess(String result) {
                       // System.out.println("result-getDeviceShareUserList-:"+result);
                        finishLoading();
                        ToastUtils.showShort(getActivity(),result);
                    }
                    @Override
                    public void onFail(Throwable e) {


                    }
                });

            }
        });

        bt_changeDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                updateDeviceNameDialog(pk,dk,shareCode);
            }
        });

        bt_set_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                generateShareInfor(pk,dk);

            }
        });

        bt_queryDeviceInfor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                startLoading();
               if(TextUtils.isEmpty(shareCode))
               {
                   DeviceServiceFactory.getInstance().getService(IDevService.class).queryDeviceInfo(pk, dk, "", new IHttpCallBack() {
                       @Override
                       public void onSuccess(String result) {
                           finishLoading();
                           ToastUtils.showShort(getActivity(),result);
                       }
                       @Override
                       public void onFail(Throwable e) {

                       }
                   });
               }
               else
               {
                   DeviceServiceFactory.getInstance().getService(IDevService.class).queryDeviceInfo(pk, "", shareCode, new IHttpCallBack() {
                       @Override
                       public void onSuccess(String result) {
                           finishLoading();
                           ToastUtils.showShort(getActivity(),result);
                       }
                       @Override
                       public void onFail(Throwable e) {


                       }
                   });
               }
            }
        });
        mDialog.show();
    }

    private void generateShareInfor(String pk,String dk)
    {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.sharer_generate_information_dialog, null);
        final Dialog mDialog = new Dialog(getActivity(), R.style.dialogTM);
        mDialog.setContentView(view);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        Button bt_generate = (Button) mDialog.findViewById(R.id.bt_generate);
        TextView tv_share_infor = (TextView) mDialog.findViewById(R.id.tv_share_infor);
        Button bt_copy = (Button) mDialog.findViewById(R.id.bt_copy);
        Button bt_cancel = (Button) mDialog.findViewById(R.id.bt_cancel);

        bt_generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int time = 1*24*60*60*1000;
                long useTime =   new Date().getTime();
                useTime = useTime + time;
                   startLoading();
                DeviceServiceFactory.getInstance().getService(IDevService.class).shareDeviceInfo(useTime,pk,dk,0,0,
                        new IHttpCallBack() {
                            @Override
                            public void onSuccess(String result) {
                                finishLoading();
                                //{"code":200,"msg":"","data":"share_dv_C18222055e793c4ee4581b18d3bfcd1a50ada"}
                                System.out.println("shareDeviceInfo--:" + result);
                                try {
                                    JSONObject  obj = new JSONObject(result);
                                    if (obj.getInt("code") == 200) {
                                       String infor =   obj.getString("data");
                                        tv_share_infor.setText(infor);
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
        });

        bt_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyUtils.copyContentToClipboard(getActivity(),tv_share_infor.getText().toString().trim());
                ToastUtils.showShort(getActivity(),"复制成功");
            }
        });

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });

        mDialog.show();

    }

    private void createUnBindDialog()
    {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.unbind_device_dialog, null);
        final Dialog mDialog = new Dialog(getActivity(), R.style.dialogTM);
        mDialog.setContentView(view);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        EditText edit_pk = (EditText) mDialog.findViewById(R.id.edit_pk);
        EditText edit_dk = (EditText) mDialog.findViewById(R.id.edit_dk);
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
                String pk =  MyUtils.getEditTextContent(edit_pk);
                String dk =  MyUtils.getEditTextContent(edit_dk);
                if(TextUtils.isEmpty(pk))
                {
                    ToastUtils.showShort(getActivity(),"pk输入不能为空");
                    return;
                }
                if(TextUtils.isEmpty(dk))
                {
                    ToastUtils.showShort(getActivity(),"dk输入不能为空");
                    return;
                }

                mDialog.dismiss();
                DeviceServiceFactory.getInstance().getService(IDevService.class).unBindDevice(pk,dk,
                        new IHttpCallBack() {
                            @Override
                            public void onSuccess(String result) {
                                try {
                                    JSONObject  obj = new JSONObject(result);
                                    if (obj.getInt("code") == 200) {
                                        queryDevice();
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
        });

        mDialog.show();
    }


    private void updateDeviceNameDialog(String pk,String dk,String shareCode)
    {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.update_device_name_dialog, null);
        final Dialog mDialog = new Dialog(getActivity(), R.style.dialogTM);
        mDialog.setContentView(view);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        EditText edit_name = (EditText) mDialog.findViewById(R.id.edit_name);
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
                String deviceName =  MyUtils.getEditTextContent(edit_name);
                if(TextUtils.isEmpty(deviceName))
                {
                    ToastUtils.showShort(getActivity(),"deviceName输入不能为空");
                    return;
                }

                mDialog.dismiss();
                startLoading();

                if(TextUtils.isEmpty(shareCode))
                {
                    DeviceServiceFactory.getInstance().getService(IDevService.class).changeDeviceInfo(deviceName,pk,dk,
                            new IHttpCallBack() {
                                @Override
                                public void onSuccess(String result) {
                                    try {
                                        JSONObject  obj = new JSONObject(result);
                                        if (obj.getInt("code") == 200) {
                                            queryDevice();
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
                else
                {
                    DeviceServiceFactory.getInstance().getService(IDevService.class).changeShareDeviceName(deviceName,shareCode,
                            new IHttpCallBack() {
                                @Override
                                public void onSuccess(String result) {
                                    try {
                                        JSONObject  obj = new JSONObject(result);
                                        if (obj.getInt("code") == 200) {
                                            queryDevice();
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
        });

        mDialog.show();
    }

    String pk="p111ti";
    String sn = "869537055280516";
    String dk = "869537055280516";
    private void shareDevice(long time)
    {
        DeviceServiceFactory.getInstance().getService(IDevService.class).shareDeviceInfo(time,pk,dk,0,0,
                new IHttpCallBack() {
                    @Override
                    public void onSuccess(String result) {
                         //{"code":200,"msg":"","data":"share_dv_C18222055e793c4ee4581b18d3bfcd1a50ada"}
                        System.out.println("shareDeviceInfo--:" + result);
                    }
                    @Override
                    public void onFail(Throwable e) {
                        e.printStackTrace();
                    }
                }
        );
    }

}
