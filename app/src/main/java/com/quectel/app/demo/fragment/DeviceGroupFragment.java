package com.quectel.app.demo.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.quectel.app.demo.R;
import com.quectel.app.demo.adapter.DeviceGroupAdapter;
import com.quectel.app.demo.fragmentbase.BaseMainFragment;
import com.quectel.app.demo.ui.SharedGroupOfDevicesActivity;
import com.quectel.app.demo.utils.MyUtils;
import com.quectel.app.demo.utils.ToastUtils;
import com.quectel.app.demo.widget.BottomItemDecorationSystem;
import com.quectel.app.demo.widget.PayBottomDialog;
import com.quectel.app.device.bean.QuecDeviceGroupInfoModel;
import com.quectel.app.device.bean.QuecDeviceGroupParamModel;
import com.quectel.app.device.bean.QuecOperateDeviceToGroupModel;
import com.quectel.app.device.bean.QuecShareGroupInfoModel;
import com.quectel.app.device.bean.UpdateGroup;
import com.quectel.app.device.deviceservice.IDevService;
import com.quectel.app.device.deviceservice.QuecDeviceGroupService;
import com.quectel.app.device.param.AddDeviceParam;
import com.quectel.app.device.utils.DeviceServiceFactory;
import com.quectel.app.quecnetwork.httpservice.IHttpCallBack;
import com.quectel.basic.common.entity.QuecCallback;
import com.quectel.basic.common.entity.QuecDeviceModel;
import com.quectel.basic.common.entity.QuecPageResponse;
import com.quectel.basic.common.entity.QuecResult;
import com.quectel.basic.common.utils.QuecGsonUtil;
import com.quectel.basic.queclog.QLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import kotlin.Unit;


public class DeviceGroupFragment extends BaseMainFragment {


    private final String TAG = this.getClass().getSimpleName();

    public static synchronized DeviceGroupFragment newInstance() {
        DeviceGroupFragment frag = new DeviceGroupFragment();

        return frag;
    }

    @Override
    protected void getNeedArguments(Bundle bundle) {

    }

    @Override
    protected int getLayoutView() {

        return R.layout.device_group_layout;

    }

    @Override
    protected void processBusiness() {


    }

    @BindView(R.id.mList)
    RecyclerView mRecyclerView;
    DeviceGroupAdapter mAdapter;

    @BindView(R.id.fragment_ptr_home_ptr_frame)
    PtrFrameLayout mPtrFrameLayout;
    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new BottomItemDecorationSystem(getActivity()));
        queryGroupList();

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
    List<QuecDeviceGroupInfoModel> mList = null;
    private void queryGroupList()
    {
        startLoading();
        QuecDeviceGroupService.INSTANCE.getDeviceGroupList(1, 10, null, new QuecCallback<QuecPageResponse<QuecDeviceGroupInfoModel>>() {
            @Override
            public void onResult(@NonNull QuecResult<QuecPageResponse<QuecDeviceGroupInfoModel>> result) {
                finishLoading();
                mPtrFrameLayout.refreshComplete();
                if(result.isSuccess()){
                    QuecPageResponse<QuecDeviceGroupInfoModel> data = result.getData();
                    QLog.i(TAG,"getDeviceGroupList size = " + data.getList().size());

                    mList = data.getList();
                    QLog.i(TAG,"mList--:"+mList.size());

                    mAdapter = new DeviceGroupAdapter(getActivity(), mList);
                    mRecyclerView.setAdapter(mAdapter);

                    mAdapter.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                            QLog.i(TAG,"position--:"+position);
                            createSelectDialog(mList.get(position));
                        }
                    });

                }else{
                    QLog.e(TAG,result.toString());
                }
            }
        });

    }

    private static final int PAGE_SIZE = 10;
    int  page = 0;
    private void refreshData() {

        queryGroupList();
    }

//    @Override
//    public boolean onBackPressedSupport() {
//        return false;
//    }


    @OnClick({R.id.iv_add})
    public void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.iv_add:
                View dialogView = getLayoutInflater().inflate(R.layout.bottom_pop_devicegroup_layout, null);
                PayBottomDialog myDialog = new PayBottomDialog(getActivity(), dialogView, new int[]{R.id.bt_cancel,
                        R.id.bt_add_group,R.id.bt_receive_group_share});
                myDialog.bottmShow();
                myDialog.setOnBottomItemClickListener(new PayBottomDialog.OnBottomItemClickListener() {
                    @Override
                    public void onBottomItemClick(PayBottomDialog dialog, View view) {

                        switch (view.getId()) {
                            case R.id.bt_cancel:
                                myDialog.cancel();
                                break;

                            case R.id.bt_add_group:
                                myDialog.cancel();
                                createAddGroupDialog();
                                break;

                            case R.id.bt_receive_group_share:
                                myDialog.cancel();
                                createReceiveGroupShare();

                                break;

                        }
                    }
                });


                break;
        }
    }

    private void createAddGroupDialog()
    {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.add_group, null);
        final Dialog mDialog = new Dialog(getActivity(), R.style.dialogTM);
        mDialog.setContentView(view);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        EditText edit_name = (EditText) mDialog.findViewById(R.id.edit_name);
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
                String name = MyUtils.getEditTextContent(edit_name);
                if(TextUtils.isEmpty(name))
                {
                    return;
                }
                startLoading();
                QuecDeviceGroupParamModel model = new QuecDeviceGroupParamModel();
                model.setName(name);
                QuecDeviceGroupService.INSTANCE.addDeviceGroup(model, new QuecCallback<Unit>() {
                    @Override
                    public void onResult(@NonNull QuecResult<Unit> result) {
                        finishLoading();
                        if(result.isSuccess()){
                            queryGroupList();
                        }else{

                        }
                    }
                });
            }
        });
        mDialog.show();
    }

    private void createSelectDialog(QuecDeviceGroupInfoModel item) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.devicegroup_select_dialog, null);
        final Dialog mDialog = new Dialog(getActivity(), R.style.dialogTM);
        mDialog.setContentView(view);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        Button bt_queryGroup = (Button) mDialog.findViewById(R.id.bt_queryGroup);
        Button bt_change_device_group = (Button) mDialog.findViewById(R.id.bt_change_device_group);
        Button bt_add_device_to_group = (Button) mDialog.findViewById(R.id.bt_add_device_to_group);
        Button bt_query_in_group = (Button) mDialog.findViewById(R.id.bt_query_in_group);
        Button bt_delete_device_in_group = (Button) mDialog.findViewById(R.id.bt_delete_device_in_group);
        Button bt_delete_group = (Button) mDialog.findViewById(R.id.bt_delete_group);
        Button bt_set_group_share = (Button) mDialog.findViewById(R.id.bt_set_group_share);
        Button bt_query_group_accept_users = (Button) mDialog.findViewById(R.id.bt_query_group_accept_users);
        Button bt_change_shared_device_group = (Button) mDialog.findViewById(R.id.bt_change_shared_device_group);
        Button bt_sharer_cancel_group = (Button) mDialog.findViewById(R.id.bt_sharer_cancel_group);
        Button bt_user_cancel_share = (Button) mDialog.findViewById(R.id.bt_user_cancel_share);
        bt_user_cancel_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                cancelShareGroupByOwner();
            }
        });

        bt_sharer_cancel_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(item.getShareCode()))
                {
                    ToastUtils.showShort(getActivity(),"不是被分享者");
                    return;
                }
                else
                {
                    mDialog.dismiss();
                    startLoading();
                    QuecDeviceGroupService.INSTANCE.getShareUserUnshare(item.getShareCode(), new QuecCallback<Unit>() {
                        @Override
                        public void onResult(@NonNull QuecResult<Unit> result) {
                            finishLoading();
                            if(result.isSuccess()){
                                queryGroupList();
                            } else {
                                QLog.e(TAG, result.toString());
                            }
                        }
                    });
                }
            }
        });

        bt_change_shared_device_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(item.getShareCode()))
                {
                    ToastUtils.showShort(getActivity(),"不是被分享者");
                    return;
                }
                else
                {
                    mDialog.dismiss();
                    accepterChangeGroupName(item);
                }
            }
        });
        bt_query_group_accept_users.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                startLoading();
                QuecDeviceGroupService.INSTANCE.getDeviceGroupShareUserList(item.getDgid(), new QuecCallback<Unit>() {
                    @Override
                    public void onResult(@NonNull QuecResult<Unit> result) {
                        finishLoading();
                        QLog.i(TAG,"result-:"+result.toString());
                        ToastUtils.showShort(getActivity(),result.toString());
                    }
                });

                DeviceServiceFactory.getInstance().getService(IDevService.class).deviceGroupShareUserList(item.getDgid(), new IHttpCallBack() {
                    @Override
                    public void onSuccess(String result) {
                        finishLoading();
                        System.out.println("result-:"+result);
                        ToastUtils.showShort(getActivity(),result);
                    }
                    @Override
                    public void onFail(Throwable e) {

                    }
                });
            }
        });

        bt_set_group_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                generateShareGroupInfor(item);
            }
        });

        Button bt_cancel = (Button) mDialog.findViewById(R.id.bt_cancel);
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });

        bt_queryGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                startLoading();
                QuecDeviceGroupService.INSTANCE.getDeviceGroupInfo(item.getDgid(), new QuecCallback<QuecDeviceGroupInfoModel>() {
                    @Override
                    public void onResult(@NonNull QuecResult<QuecDeviceGroupInfoModel> result) {
                        if(result.isSuccess()){
                            finishLoading();
                            String infoModelString = QuecGsonUtil.INSTANCE.gsonString(result.getData());
                            ToastUtils.showShort(getActivity(),infoModelString);
                            QLog.i(TAG, infoModelString);
                        } else {
                            QLog.e(TAG, result.toString());
                        }
                    }
                });

            }
        });

        bt_change_device_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                changeGroupDialog(item);

            }
        });

        bt_add_device_to_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                addDeviceToGroup(item);
            }
        });

        bt_query_in_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                queryDeviceInGroup(item);
            }
        });

        bt_delete_device_in_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                deleteDeviceFromGroup(item);
            }
        });

        bt_delete_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
                startLoading();
                QuecDeviceGroupService.INSTANCE.deleteDeviceGroup(item.getDgid(), result -> {
                    finishLoading();
                    if(result.isSuccess()){
                        queryGroupList();
                    }else{
                        QLog.e(TAG, result.toString());
                    }
                });
            }
        });

        mDialog.show();
    }

    private void generateShareGroupInfor(QuecDeviceGroupInfoModel item)
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
        TextView tv_title = (TextView) mDialog.findViewById(R.id.tv_title);
        tv_title.setText("分享人设置设备组分享信息");

        bt_generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int time = 1*24*60*60*1000;
                long useTime = new Date().getTime();
                useTime = useTime + time;
                startLoading();

                QuecDeviceGroupService.INSTANCE.getShareGroupInfo(useTime, item.getDgid(), 0, 0, new QuecCallback<QuecShareGroupInfoModel>() {
                    @Override
                    public void onResult(@NonNull QuecResult<QuecShareGroupInfoModel> result) {
                        finishLoading();
                        if (result.isSuccess()) {
                            tv_share_infor.setText(result.getData().getShareCode());
                        } else {
                            QLog.e(TAG, result.toString());
                        }
                    }
                });
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

    private void changeGroupDialog(QuecDeviceGroupInfoModel item)
    {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.add_group, null);
        final Dialog mDialog = new Dialog(getActivity(), R.style.dialogTM);
        mDialog.setContentView(view);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        EditText edit_name = (EditText) mDialog.findViewById(R.id.edit_name);
        Button bt_cancel = (Button) mDialog.findViewById(R.id.bt_cancel);
        Button bt_sure = (Button) mDialog.findViewById(R.id.bt_sure);
        TextView tv_title = (TextView) mDialog.findViewById(R.id.tv_title);
        tv_title.setText("修改设备组");
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
                String name = MyUtils.getEditTextContent(edit_name);
                if(TextUtils.isEmpty(name))
                {
                    return;
                }
                startLoading();

                UpdateGroup updateGroup =  new UpdateGroup();
                updateGroup.setName(name);
                updateGroup.setDgid(item.getDgid());

                QuecDeviceGroupParamModel model = new QuecDeviceGroupParamModel();
                model.setName(name);

                QuecDeviceGroupService.INSTANCE.updateDeviceGroupInfo(item.getDgid(), model, new QuecCallback<Unit>() {
                    @Override
                    public void onResult(@NonNull QuecResult<Unit> result) {
                        if(result.isSuccess()){
                            queryGroupList();
                        }else{
                            ToastUtils.showShort(getActivity(), result.getMsg());
                        }
                    }
                });

            }
        });
        mDialog.show();
    }

    private void addDeviceToGroup(QuecDeviceGroupInfoModel item)
    {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.add_device_to_group_dialog, null);
        final Dialog mDialog = new Dialog(getActivity(), R.style.dialogTM);
        mDialog.setContentView(view);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        EditText edit_pk = (EditText) mDialog.findViewById(R.id.edit_pk);
        EditText edit_dk = (EditText) mDialog.findViewById(R.id.edit_dk);
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
                String pk = MyUtils.getEditTextContent(edit_pk);
                String dk = MyUtils.getEditTextContent(edit_dk);
                if(TextUtils.isEmpty(pk)||TextUtils.isEmpty(dk))
                {
                    ToastUtils.showShort(getActivity(),"参数不能为空");
                    return;
                }
                startLoading();
                List<QuecDeviceModel> quecDeviceModels = new ArrayList();
                QuecDeviceModel quecDeviceModel = new QuecDeviceModel(pk,dk);
                quecDeviceModels.add(quecDeviceModel);

                QuecDeviceGroupService.INSTANCE.addDeviceToGroup(item.getDgid(), quecDeviceModels, new QuecCallback<QuecOperateDeviceToGroupModel>() {
                    @Override
                    public void onResult(@NonNull QuecResult<QuecOperateDeviceToGroupModel> result) {
                        finishLoading();
                        if(result.isSuccess()){
                            queryGroupList();
                            ToastUtils.showShort(getActivity(),"添加成功");
                        }else{
                            QLog.e(TAG, result.getMsg());
                        }
                    }
                });
            }
        });
        mDialog.show();
    }

    private void queryDeviceInGroup(QuecDeviceGroupInfoModel item)
    {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.query_device_ingroup_dialog, null);
        final Dialog mDialog = new Dialog(getActivity(), R.style.dialogTM);
        mDialog.setContentView(view);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        EditText edit_pk = (EditText) mDialog.findViewById(R.id.edit_pk);
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
                String pk = MyUtils.getEditTextContent(edit_pk);
                if(TextUtils.isEmpty(pk))
                {
                    ToastUtils.showShort(getActivity(),"参数不能为空");
                    return;
                }
                startLoading();

                QuecDeviceGroupService.INSTANCE.getDeviceList(item.getDgid(),null , null, pk, 1, 10, new QuecCallback<QuecPageResponse<QuecDeviceModel>>() {
                    @Override
                    public void onResult(@NonNull QuecResult<QuecPageResponse<QuecDeviceModel>> result) {
                        finishLoading();
                        if(result.isSuccess()){
                            if (TextUtils.isEmpty(item.getShareCode())) {
                                ToastUtils.showShort(getActivity(), result.toString());
                            } else {
                                Intent intent = new Intent(getActivity(), SharedGroupOfDevicesActivity.class);
                                intent.putExtra("content", QuecGsonUtil.INSTANCE.gsonString(result.getData()));
                                intent.putExtra("shareCode", item.getShareCode());
                                startActivity(intent);
                            }
                        }else{

                        }
                    }
                });
            }
        });
        mDialog.show();
    }


    private void deleteDeviceFromGroup(QuecDeviceGroupInfoModel item)
    {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.add_device_to_group_dialog, null);
        final Dialog mDialog = new Dialog(getActivity(), R.style.dialogTM);
        mDialog.setContentView(view);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        EditText edit_pk = (EditText) mDialog.findViewById(R.id.edit_pk);
        EditText edit_dk = (EditText) mDialog.findViewById(R.id.edit_dk);
        Button bt_cancel = (Button) mDialog.findViewById(R.id.bt_cancel);
        Button bt_sure = (Button) mDialog.findViewById(R.id.bt_sure);
        TextView tv_title = (TextView) mDialog.findViewById(R.id.tv_title);
        tv_title.setText("移除设备组中的设备");

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
                String pk = MyUtils.getEditTextContent(edit_pk);
                String dk = MyUtils.getEditTextContent(edit_dk);
                if(TextUtils.isEmpty(pk)||TextUtils.isEmpty(dk))
                {
                    ToastUtils.showShort(getActivity(),"参数不能为空");
                    return;
                }
                startLoading();
                List<QuecDeviceModel> deviceList = new ArrayList<>();
                QuecDeviceModel quecDeviceModel = new QuecDeviceModel(pk,dk);
                deviceList.add(quecDeviceModel);
                QuecDeviceGroupService.INSTANCE.deleteDeviceFromGroup(item.getDgid(), deviceList, new QuecCallback<QuecOperateDeviceToGroupModel>() {
                    @Override
                    public void onResult(@NonNull QuecResult<QuecOperateDeviceToGroupModel> result) {
                        finishLoading();
                        if(result.isSuccess()){
                            queryGroupList();
                            ToastUtils.showShort(getActivity(),"移除成功");
                        }else{
                            QLog.e(TAG, result.getMsg());
                        }
                    }
                });
            }
        });
        mDialog.show();
    }

    private void createReceiveGroupShare()
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
        TextView tv_title = (TextView) mDialog.findViewById(R.id.tv_title);
        tv_title.setText("接受别人设备组分享");
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

                QuecDeviceGroupService.INSTANCE.getAcceptDeviceGroupShare(code, new QuecCallback<Unit>() {
                    @Override
                    public void onResult(@NonNull QuecResult<Unit> result) {
                        finishLoading();
                        if(result.isSuccess()){
                            ToastUtils.showShort(getActivity(),"操作成功");
                            queryGroupList();
                        }else{
                            QLog.e(TAG, result.toString());
                        }
                    }
                });
            }
        });
        mDialog.show();
    }


    private void accepterChangeGroupName(QuecDeviceGroupInfoModel item)
    {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.accepter_change_group_name_dialog, null);
        final Dialog mDialog = new Dialog(getActivity(), R.style.dialogTM);
        mDialog.setContentView(view);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        EditText edit_name = (EditText) mDialog.findViewById(R.id.edit_name);
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
                String name = MyUtils.getEditTextContent(edit_name);
                if(TextUtils.isEmpty(name))
                {
                    ToastUtils.showShort(getActivity(),"参数不能为空");
                    return;
                }
                startLoading();

                QuecDeviceGroupService.INSTANCE.getShareUserSetDeviceGroupName(name, item.getShareCode(), new QuecCallback<Unit>() {
                    @Override
                    public void onResult(@NonNull QuecResult<Unit> result) {
                        finishLoading();
                        if(result.isSuccess()){
                            queryGroupList();
                        }else{
                            QLog.e(TAG, result.toString());
                        }
                    }
                });
            }
        });
        mDialog.show();
    }

     //分享人取消设备组分享
    private void cancelShareGroupByOwner()
    {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.receiver_cancel_share_dialog, null);
        final Dialog mDialog = new Dialog(getActivity(), R.style.dialogTM);
        mDialog.setContentView(view);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(false);
        EditText edit_code = (EditText) mDialog.findViewById(R.id.edit_code);
        TextView tv_title = (TextView) mDialog.findViewById(R.id.tv_title);
        tv_title.setText("分享人取消设备组分享");
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

                QuecDeviceGroupService.INSTANCE.getOwerUserUnshare(code, new QuecCallback<Unit>() {
                    @Override
                    public void onResult(@NonNull QuecResult<Unit> result) {
                        finishLoading();
                        if(result.isSuccess()){
                            ToastUtils.showShort(getActivity(),"操作成功");
                            queryGroupList();
                        }else{
                            ToastUtils.showShort(getActivity(), result.getMsg());
                            QLog.e(TAG, result.toString());
                        }
                    }
                });
            }
        });
        mDialog.show();
    }

}
