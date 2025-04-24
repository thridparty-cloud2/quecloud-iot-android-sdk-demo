package com.quectel.app.demo.ui.device.group

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.quectel.app.demo.R
import com.quectel.app.demo.adapter.DeviceGroupAdapter
import com.quectel.app.demo.base.fragment.QuecBaseFragment
import com.quectel.app.demo.databinding.DeviceGroupLayoutBinding
import com.quectel.app.demo.utils.MyUtils
import com.quectel.app.demo.utils.ToastUtils
import com.quectel.app.demo.widget.BottomItemDecorationSystem
import com.quectel.app.demo.widget.PayBottomDialog
import com.quectel.app.demo.widget.PayBottomDialog.OnBottomItemClickListener
import com.quectel.app.device.bean.QuecDeviceGroupInfoModel
import com.quectel.app.device.bean.QuecDeviceGroupParamModel
import com.quectel.app.device.deviceservice.QuecDeviceGroupService
import com.quectel.basic.queclog.QLog
import `in`.srain.cube.views.ptr.PtrDefaultHandler
import `in`.srain.cube.views.ptr.PtrFrameLayout
import `in`.srain.cube.views.ptr.PtrHandler

class DeviceGroupFragment : QuecBaseFragment<DeviceGroupLayoutBinding>() {

    companion object {
        const val TAG = "DeviceGroupFragment"
    }

    var mDialog: Dialog? = null
    var mList: MutableList<QuecDeviceGroupInfoModel>? = null
    var mAdapter: DeviceGroupAdapter? = null

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): DeviceGroupLayoutBinding {
        return DeviceGroupLayoutBinding.inflate(inflater, container, false)
    }

    override fun initView(savedInstanceState: Bundle?) {
        mAdapter = DeviceGroupAdapter(context, null)
        binding.mList.setLayoutManager(LinearLayoutManager(activity))
        binding.mList.addItemDecoration(BottomItemDecorationSystem(activity))
        binding.fragmentPtrHomePtrFrame.setPtrHandler(object : PtrHandler {
            override fun checkCanDoRefresh(
                frame: PtrFrameLayout,
                content: View,
                header: View
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
            val dialogView =
                View.inflate(context, R.layout.bottom_pop_devicegroup_layout, null)
            val myDialog = PayBottomDialog(
                activity, dialogView, intArrayOf(
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
        queryGroupList()
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
                        position: Int
                    ) {
                        val data: QuecDeviceGroupInfoModel =
                            adapter.data[position] as QuecDeviceGroupInfoModel
                        QLog.i(TAG, "position--:$position")
                        var intent = Intent(activity, DeviceListGroupActivity::class.java)
                        intent.putExtra("dGid", data.dgid)
                        intent.putExtra("name", data.name)
                        intent.putExtra("shareCode", data.shareCode)
                        startActivity(intent)
                    }
                })

            } else {
                ToastUtils.showShort(context, result.msg)
                QLog.e(TAG, result.msg)
            }
        }
    }

    fun startLoading() {
        if (mDialog == null) {
            mDialog = MyUtils.createDialog(context)
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
        val view = View.inflate(context, R.layout.add_group, null)
        val mDialog = Dialog(requireActivity(), R.style.dialogTM)
        mDialog.setContentView(view)
        mDialog.setCancelable(true)
        mDialog.setCanceledOnTouchOutside(false)
        val mEditName = mDialog.findViewById<View?>(R.id.edit_name) as EditText
        val mBtCancel = mDialog.findViewById<View?>(R.id.bt_cancel) as Button
        val mBtSure = mDialog.findViewById<View?>(R.id.bt_sure) as Button
        mBtCancel.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                mDialog.dismiss()
            }
        })

        mBtSure.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                mDialog.dismiss()
                val name: String = MyUtils.getEditTextContent(mEditName)
                if (name.isEmpty()) return

                startLoading()
                val model = QuecDeviceGroupParamModel()
                model.name = name
                QuecDeviceGroupService.addDeviceGroup(model) { result ->
                    finishLoading()
                    if (result.isSuccess) {
                        queryGroupList()
                    } else {
                        ToastUtils.showShort(context, result.msg)
                        QLog.e(TAG, result.toString())
                    }
                }
            }
        })
        mDialog.show()
    }

    private fun createReceiveGroupShare() {
        val view = View.inflate(context, R.layout.receiver_accept_shareinformation_dialog, null)
        val mDialog = Dialog(requireActivity(), R.style.dialogTM)
        mDialog.setContentView(view)
        mDialog.setCancelable(true)
        mDialog.setCanceledOnTouchOutside(false)
        val mEditShareCode = mDialog.findViewById<View?>(R.id.edit_share_code) as EditText
        val mBtCancel = mDialog.findViewById<View?>(R.id.bt_cancel) as Button
        val mBtSure = mDialog.findViewById<View?>(R.id.bt_sure) as Button
        val mTvTitle = mDialog.findViewById<View?>(R.id.tv_title) as TextView
        mTvTitle.text = "接受别人设备组分享"
        mBtCancel.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                mDialog.dismiss()
            }
        })

        mBtSure.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                mDialog.dismiss()
                val code = MyUtils.getEditTextContent(mEditShareCode)
                if (code.isNullOrEmpty()) {
                    return
                }
                startLoading()

                QuecDeviceGroupService.getAcceptDeviceGroupShare(code) { result ->
                    finishLoading()
                    if (result.isSuccess) {
                        ToastUtils.showShort(context, "操作成功")
                        queryGroupList()
                    } else {
                        ToastUtils.showShort(context, result.msg)
                        QLog.e(TAG, result.toString())
                    }
                }
            }
        })
        mDialog.show()
    }
}