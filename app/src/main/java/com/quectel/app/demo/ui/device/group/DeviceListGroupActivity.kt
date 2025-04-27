package com.quectel.app.demo.ui.device.group

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.quectel.app.demo.R
import com.quectel.app.demo.base.activity.QuecBaseActivity
import com.quectel.app.demo.common.AppVariable
import com.quectel.app.demo.databinding.ActivityListGroupBinding
import com.quectel.app.demo.dialog.CommonDialog
import com.quectel.app.demo.dialog.EditDoubleTextPopup
import com.quectel.app.demo.dialog.EditTextPopup
import com.quectel.app.demo.ui.SharedGroupOfDevicesActivity
import com.quectel.app.demo.utils.MyUtils
import com.quectel.app.demo.utils.ToastUtils
import com.quectel.app.device.bean.QuecDeviceGroupParamModel
import com.quectel.app.device.deviceservice.QuecDeviceGroupService
import com.quectel.basic.common.entity.QuecDeviceModel
import com.quectel.basic.common.utils.QuecGsonUtil
import com.quectel.basic.queclog.QLog
import java.util.Date

class DeviceListGroupActivity : QuecBaseActivity<ActivityListGroupBinding>() {

    companion object {
        const val TAG = "DeviceListGroupActivity"
    }

    var dGid: String? = ""
    var name: String? = ""
    var shareCode: String? = ""
    var mDialog: Dialog? = null

    override fun getViewBinding(): ActivityListGroupBinding {
        return ActivityListGroupBinding.inflate(layoutInflater)
    }

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initData() {
        dGid = intent.getStringExtra("dGid")
        name = intent.getStringExtra("name")
        shareCode = intent.getStringExtra("shareCode")
        if (!name.isNullOrEmpty()) {
            binding.tvStatus.text = name
        }
    }

    override fun initTestItem() {
        addItem("查询设备组详情") {
            queryGroup()
        }

        addItem("修改设备组") {
            changeGroupDialog()
        }

        addItem("添加设备到设备组") {
            addDeviceToGroup()
        }

        addItem("查询设备组中的设备列表") {
            queryDeviceInGroup()
        }

        addItem("移除设备组中的设备") {
            deleteDeviceFromGroup()
        }
        addItem("删除设备组") {
            deleteGroup()
        }
        addItem("分享人设置设备组分享信息") {
            generateShareGroupInfo()
        }
        addItem("分享人查询设备组的被分享人列表") {
            queryGroupAcceptUsers()
        }
        addItem("被分享人修改分享的设备组名称") {
            changeSharedDeviceGroup()
        }
        addItem("被分享人取消设备组分享") {
            btSharerCancelGroup()
        }
        addItem("分享人取消设备组分享") {
            cancelShareGroupByOwner()
        }
        addItem("取消") {
            finish()
        }
    }

    private fun queryGroup() {
        startLoading()
        QuecDeviceGroupService.getDeviceGroupInfo(dGid!!) { result ->
            finishLoading()
            handlerResult(result)
            if (result.isSuccess) {
                CommonDialog.showSimpleInfo(
                    this@DeviceListGroupActivity,
                    "查询设备组详情",
                    QuecGsonUtil.gsonString(result.data)
                )
            }
        }
    }

    private fun changeGroupDialog() {
        EditTextPopup(this).apply {
            setTitle("添加设备组")
            setHint("请输入group name")
            setEditTextListener { name ->
                if (name.isNullOrEmpty()) {
                    ToastUtils.showShort(mContext, "参数不能为空")
                    return@setEditTextListener
                }
                dismiss()
                startLoading()
                val model = QuecDeviceGroupParamModel()
                model.name = name
                QuecDeviceGroupService.updateDeviceGroupInfo(dGid!!, model) { result ->
                    finishLoading()
                    handlerResult(result)
                    AppVariable.setGroupChange()
                    if (result.isSuccess) {
                        finish()
                    }
                }
            }
        }.showPopupWindow()
    }


    private fun addDeviceToGroup() {
        EditDoubleTextPopup(mContext).apply {
            setTitle("添加设备到设备组")
            setHint1("请输入pk")
            setHint2("请输入dk")
            setEditTextListener { pk, dk ->
                if (pk.isNullOrEmpty() || dk.isNullOrEmpty()) {
                    ToastUtils.showShort(mContext, "参数不能为空")
                    return@setEditTextListener
                }
                dismiss()
                startLoading()
                val quecDeviceModels = ArrayList<QuecDeviceModel>()
                val quecDeviceModel = QuecDeviceModel(pk, dk)
                quecDeviceModels.add(quecDeviceModel)
                AppVariable.setGroupChange()
                QuecDeviceGroupService.addDeviceToGroup(dGid!!, quecDeviceModels) { result ->
                    finishLoading()
                    handlerResult(result)
                    if (result.isSuccess) {
                        finish()
                    }
                }
            }
        }.showPopupWindow()
    }

    private fun queryDeviceInGroup() {
        EditTextPopup(this).apply {
            setTitle("查询设备组中的设备列表")
            setHint("请输入pk")
            setEditTextListener {
                if (it.isNullOrEmpty()) {
                    ToastUtils.showShort(mContext, "参数不能为空")
                    return@setEditTextListener
                }
                dismiss()
                startLoading()
                QuecDeviceGroupService.getDeviceList(
                    dGid, null, null, it, 1, 10
                ) { result ->
                    finishLoading()
                    handlerResult(result)
                    if (result.isSuccess) {
                        if (shareCode.isNullOrEmpty()) {
                            CommonDialog.showSimpleInfo(
                                this@DeviceListGroupActivity,
                                "设备组中的设备列表",
                                result.toString()
                            )
                        } else {
                            val intent = Intent(
                                this@DeviceListGroupActivity,
                                SharedGroupOfDevicesActivity::class.java
                            )
                            intent.putExtra("content", QuecGsonUtil.gsonString(result.data))
                            intent.putExtra("shareCode", shareCode)
                            startActivity(intent)
                        }
                    }
                }
            }
        }.showPopupWindow()
    }


    private fun deleteDeviceFromGroup() {
        EditDoubleTextPopup(mContext).apply {
            setTitle("移除设备组中的设备")
            setHint1("请输入pk")
            setHint2("请输入dk")
            setEditTextListener { content1, content2 ->
                if (content1.isNullOrEmpty() || content2.isNullOrEmpty()) {
                    ToastUtils.showShort(mContext, "参数不能为空")
                    return@setEditTextListener
                }
                dismiss()
                startLoading()
                val deviceList = ArrayList<QuecDeviceModel>()
                val quecDeviceModel = QuecDeviceModel(content1, content2)
                deviceList.add(quecDeviceModel)
                QuecDeviceGroupService.deleteDeviceFromGroup(
                    dGid!!, deviceList
                ) { result ->
                    AppVariable.setGroupChange()
                    finishLoading()
                    handlerResult(result)
                    if (result.isSuccess) {
                        finish()
                    }
                }
            }
        }.showPopupWindow()
    }

    private fun deleteGroup() {
        startLoading()
        QuecDeviceGroupService.deleteDeviceGroup(
            dGid!!
        ) { result ->
            finishLoading()
            handlerResult(result)
            AppVariable.setGroupChange()
            if (result.isSuccess) {
                finish()
            }
        }
    }

    private fun generateShareGroupInfo() {
        val view = View.inflate(mContext, R.layout.sharer_generate_information_dialog, null)
        val mDialog = Dialog(this@DeviceListGroupActivity, R.style.dialogTM)
        mDialog.setContentView(view)
        mDialog.setCancelable(true)
        mDialog.setCanceledOnTouchOutside(false)
        val mBtGenerate = mDialog.findViewById<View>(R.id.bt_generate) as Button
        val mTvShareInfo = mDialog.findViewById<View>(R.id.tv_share_infor) as TextView
        val mBtCopy = mDialog.findViewById<View>(R.id.bt_copy) as Button
        val mBtCancel = mDialog.findViewById<View>(R.id.bt_cancel) as Button
        val mTvTitle = mDialog.findViewById<View>(R.id.tv_title) as TextView
        mTvTitle.text = "分享人设置设备组分享信息"

        mBtGenerate.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                val time = 1 * 24 * 60 * 60 * 1000
                var useTime = Date().time
                useTime = useTime + time
                startLoading()

                QuecDeviceGroupService.getShareGroupInfo(
                    useTime, dGid!!, 0, 0
                ) { result ->
                    finishLoading()
                    if (result.isSuccess) {
                        mTvShareInfo.text = result.data!!.shareCode
                    } else {
                        ToastUtils.showShort(mContext, result.msg)
                        QLog.e(TAG, result.toString())
                    }
                }
            }
        })

        mBtCopy.setOnClickListener { it ->
            MyUtils.copyContentToClipboard(
                mContext,
                mTvShareInfo.text.toString().trim()
            )
            ToastUtils.showShort(mContext, "复制成功")
        }
        mBtCancel.setOnClickListener { it ->
            mDialog.dismiss()
        }
        mDialog.show()
    }


    private fun queryGroupAcceptUsers() {
        startLoading()
        QuecDeviceGroupService.getDeviceGroupShareUserList(
            dGid!!
        ) { result ->
            handlerResult(result)
            if (result.isSuccess) {
                finishLoading()
                QLog.i(TAG, "result-:$result")
                CommonDialog.showSimpleInfo(this, "分享人列表", result.toString())
            }

        }
    }

    private fun changeSharedDeviceGroup() {
        if (shareCode.isNullOrEmpty()) {
            ToastUtils.showShort(mContext, "不是被分享者")
            return
        } else {
            accepterChangeGroupName()
        }
    }

    private fun accepterChangeGroupName() {
        EditTextPopup(this).apply {
            setTitle("修改设备组名称")
            setHint("请输入group name")
            setEditTextListener {
                if (it.isNullOrEmpty()) {
                    ToastUtils.showShort(mContext, "参数不能为空")
                    return@setEditTextListener
                }
                dismiss()
                startLoading()
                QuecDeviceGroupService.getShareUserSetDeviceGroupName(
                    it, shareCode!!
                ) { result ->
                    AppVariable.setGroupChange()
                    finishLoading()
                    if (result.isSuccess) {
                        ToastUtils.showShort(mContext, "成功")
                        finish()
                    } else {
                        ToastUtils.showShort(mContext, result.msg)
                        QLog.e(TAG, result.toString())
                    }
                }
            }
        }.showPopupWindow()
    }

    private fun btSharerCancelGroup() {
        if (shareCode.isNullOrEmpty()) {
            ToastUtils.showShort(mContext, "不是被分享者")
            return
        } else {
            startLoading()
            QuecDeviceGroupService.getShareUserUnshare(
                shareCode!!
            ) { result ->
                finishLoading()
                AppVariable.setGroupChange()
                if (result.isSuccess) {
                    finish()
                    ToastUtils.showShort(mContext, "成功")
                } else {
                    ToastUtils.showShort(mContext, result.msg)
                    QLog.e(TAG, result.toString())
                }
            }
        }
    }

    private fun cancelShareGroupByOwner() {
        EditTextPopup(this).apply {
            setTitle("分享人取消设备组分享")
            setHint("请输入shareCode")
            setEditTextListener {
                if (it.isNullOrEmpty()) {
                    return@setEditTextListener
                }
                dismiss()
                startLoading()
                AppVariable.setGroupChange()
                QuecDeviceGroupService.getOwerUserUnshare(it) { result ->
                    finishLoading()
                    if (result.isSuccess) {
                        ToastUtils.showShort(mContext, "操作成功")
                        finish()
                    } else {
                        ToastUtils.showShort(mContext, result.msg)
                        QLog.e(TAG, result.toString())
                    }
                }
            }
        }.showPopupWindow()
    }

    fun startLoading() {
        if (mDialog == null) {
            mDialog = MyUtils.createDialog(this@DeviceListGroupActivity)
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

}

