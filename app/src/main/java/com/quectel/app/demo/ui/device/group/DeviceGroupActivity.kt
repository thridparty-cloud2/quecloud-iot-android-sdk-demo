package com.quectel.app.demo.ui.device.group

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.quectel.app.demo.R
import com.quectel.app.demo.adapter.DeviceGroupAdapter
import com.quectel.app.demo.base.activity.QuecBaseActivity
import com.quectel.app.demo.common.AppVariable
import com.quectel.app.demo.databinding.DeviceGroupLayoutBinding
import com.quectel.app.demo.dialog.EditTextPopup
import com.quectel.app.demo.utils.MyUtils
import com.quectel.app.demo.utils.ToastUtils
import com.quectel.app.demo.widget.BottomItemDecorationSystem
import com.quectel.app.demo.widget.PayBottomDialog
import com.quectel.app.demo.widget.PayBottomDialog.OnBottomItemClickListener
import com.quectel.app.device.bean.QuecDeviceGroupInfoModel
import com.quectel.app.device.bean.QuecDeviceGroupParamModel
import com.quectel.app.device.deviceservice.QuecDeviceGroupService
import com.quectel.basic.common.utils.QuecClickUtils
import com.quectel.basic.queclog.QLog
import `in`.srain.cube.views.ptr.PtrDefaultHandler
import `in`.srain.cube.views.ptr.PtrFrameLayout
import `in`.srain.cube.views.ptr.PtrHandler

class DeviceGroupActivity : QuecBaseActivity<DeviceGroupLayoutBinding>() {

    companion object {
        const val TAG = "DeviceGroupActivity"
    }

    var mDialog: Dialog? = null
    var mList: MutableList<QuecDeviceGroupInfoModel>? = null
    var mAdapter: DeviceGroupAdapter? = null


    override fun getViewBinding(): DeviceGroupLayoutBinding {
        return DeviceGroupLayoutBinding.inflate(layoutInflater)
    }

    override fun initView(savedInstanceState: Bundle?) {
        mAdapter = DeviceGroupAdapter(this, null)
        binding.mList.setLayoutManager(LinearLayoutManager(this))
        binding.mList.addItemDecoration(BottomItemDecorationSystem(this))
        binding.fragmentPtrHomePtrFrame.setPtrHandler(object : PtrHandler {
            override fun checkCanDoRefresh(
                frame: PtrFrameLayout,
                content: View,
                header: View,
            ): Boolean {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, binding.mList, header)
            }

            override fun onRefreshBegin(frame: PtrFrameLayout) {
                queryGroupList()
            }
        })
    }

    override fun initData() {
        queryGroupList()
        binding.ivAdd.setOnClickListener { it ->
            if (QuecClickUtils.isFastClick()) {
                return@setOnClickListener
            }
            val dialogView =
                View.inflate(this, R.layout.bottom_pop_devicegroup_layout, null)
            val myDialog = PayBottomDialog(
                this, dialogView, intArrayOf(
                    R.id.bt_cancel,
                    R.id.bt_add_group, R.id.bt_receive_group_share
                )
            )
            myDialog.bottmShow()
            myDialog.setOnBottomItemClickListener(object : OnBottomItemClickListener {
                override fun onBottomItemClick(dialog: PayBottomDialog, view: View) {
                    when (view.id) {
                        R.id.bt_cancel -> myDialog.cancel()
                        R.id.bt_add_group -> {
                            myDialog.cancel()
                            createAddGroupDialog()
                        }

                        R.id.bt_receive_group_share -> {
                            myDialog.cancel()
                            createReceiveGroupShare()
                        }
                    }
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        if (AppVariable.isGroupInfoChange) {
            AppVariable.isGroupInfoChange = false
            queryGroupList()
        }
    }

    private fun queryGroupList() {
        startLoading()
        QuecDeviceGroupService.getDeviceGroupList(1, 10, null) { result ->
            finishLoading()
            binding.fragmentPtrHomePtrFrame.refreshComplete()
            if (result.isSuccess) {
                val data = result.data
                QLog.i(TAG, "getDeviceGroupList size = " + data.list.size)
                mList = data.list
                QLog.i(TAG, "mList--:" + mList!!.size)
                mAdapter!!.setNewInstance(mList)
                binding.mList.setAdapter(mAdapter)

                mAdapter!!.setOnItemClickListener(object : OnItemClickListener {
                    override fun onItemClick(
                        adapter: BaseQuickAdapter<*, *>,
                        view: View,
                        position: Int,
                    ) {
                        val data: QuecDeviceGroupInfoModel =
                            adapter.data[position] as QuecDeviceGroupInfoModel
                        QLog.i(TAG, "position--:$position")
                        var intent =
                            Intent(this@DeviceGroupActivity, DeviceListGroupActivity::class.java)
                        intent.putExtra("dGid", data.dgid)
                        intent.putExtra("name", data.name)
                        intent.putExtra("shareCode", data.shareCode)
                        startActivity(intent)
                    }
                })

            } else {
                ToastUtils.showShort(this, result.msg)
                QLog.e(TAG, result.msg)
            }
        }
    }

    fun startLoading() {
        if (mDialog == null) {
            mDialog = MyUtils.createDialog(this)
            mDialog!!.show()
        } else {
            mDialog!!.show()
        }
    }

    fun finishLoading() {
        if (mDialog != null) {
            mDialog!!.dismiss()
        }
    }

    private fun createAddGroupDialog() {
        EditTextPopup(this).apply {
            setTitle("添加设备组")
            setHint("请输入group name")
            setEditTextListener { name ->
                if (name.isNullOrEmpty()) {
                    ToastUtils.showShort(context, "参数不能为空")
                    return@setEditTextListener
                }
                dismiss()
                startLoading()
                val model = QuecDeviceGroupParamModel()
                model.name = name
                QuecDeviceGroupService.addDeviceGroup(model) { result ->
                    finishLoading()
                    handlerResult(result)
                    if (result.isSuccess) {
                        queryGroupList()
                    }
                }
            }
        }.showPopupWindow()
    }

    private fun createReceiveGroupShare() {
        EditTextPopup(this).apply {
            setTitle("接受别人设备组分享")
            setHint("请输入share_code")
            setEditTextListener { code ->
                if (code.isNullOrEmpty()) {
                    ToastUtils.showShort(context, "参数不能为空")
                    return@setEditTextListener
                }
                dismiss()
                startLoading()
                QuecDeviceGroupService.getAcceptDeviceGroupShare(code) { result ->
                    finishLoading()
                    handlerResult(result)
                    if (result.isSuccess) {
                        queryGroupList()
                    }
                }
            }
        }.showPopupWindow()
    }
}