package com.quectel.app.demo.ui.device.group

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.quectel.app.demo.R
import com.quectel.app.demo.base.activity.QuecBaseActivity
import com.quectel.app.demo.databinding.ActivityListGroupBinding
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
            if (result.isSuccess) {
                val infoModelString = QuecGsonUtil.gsonString(result.getData())
                ToastUtils.showShort(mContext, infoModelString)
                QLog.i(TAG, infoModelString)
            } else {
                ToastUtils.showShort(mContext, result.msg)
                QLog.e(TAG, result.toString())
            }
        }
    }

    private fun changeGroupDialog() {
        val view = View.inflate(mContext, R.layout.add_group, null)
        val mDialog = Dialog(this@DeviceListGroupActivity, R.style.dialogTM)
        mDialog.setContentView(view)
        mDialog.setCancelable(true)
        mDialog.setCanceledOnTouchOutside(false)
        val mEditName = mDialog.findViewById<View>(R.id.edit_name) as EditText
        val mBtCancel = mDialog.findViewById<View>(R.id.bt_cancel) as Button
        val mBtSure = mDialog.findViewById<View>(R.id.bt_sure) as Button
        val mTvTitle = mDialog.findViewById<View>(R.id.tv_title) as TextView
        mTvTitle.text = "修改设备组"
        mBtCancel.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                mDialog.dismiss()
            }
        })

        mBtSure.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                mDialog.dismiss()
                val name = MyUtils.getEditTextContent(mEditName)
                if (name.isNullOrEmpty()) {
                    return
                }
                startLoading()
                val model = QuecDeviceGroupParamModel()
                model.name = name
                QuecDeviceGroupService.updateDeviceGroupInfo(dGid!!, model) { result ->
                    finishLoading()
                    if (result.isSuccess) {
                        ToastUtils.showShort(mContext,"成功")
                        finish()
                    } else {
                        QLog.e(TAG, result.msg)
                        ToastUtils.showShort(mContext, result.msg)
                    }
                }
            }
        })
        mDialog.show()
    }


    private fun addDeviceToGroup() {
        val view = View.inflate(mContext, R.layout.add_device_to_group_dialog, null)
        val mDialog = Dialog(this@DeviceListGroupActivity, R.style.dialogTM)
        mDialog.setContentView(view)
        mDialog.setCancelable(true)
        mDialog.setCanceledOnTouchOutside(false)
        val mEditPk = mDialog.findViewById<View>(R.id.edit_pk) as EditText
        val mEditDk = mDialog.findViewById<View>(R.id.edit_dk) as EditText
        val mBtCancel = mDialog.findViewById<View>(R.id.bt_cancel) as Button
        val mBtSure = mDialog.findViewById<View>(R.id.bt_sure) as Button
        mBtCancel.setOnClickListener {
            mDialog.dismiss()
        }

        mBtSure.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                mDialog.dismiss()
                val pk = MyUtils.getEditTextContent(mEditPk)
                val dk = MyUtils.getEditTextContent(mEditDk)
                if (pk.isNullOrEmpty() || dk.isNullOrEmpty()) {
                    ToastUtils.showShort(mContext, "参数不能为空")
                    return
                }
                startLoading()
                val quecDeviceModels = ArrayList<QuecDeviceModel>()
                val quecDeviceModel = QuecDeviceModel(pk, dk)
                quecDeviceModels.add(quecDeviceModel)

                QuecDeviceGroupService.addDeviceToGroup(dGid!!, quecDeviceModels) { result ->
                    finishLoading()
                    if (result.isSuccess) {
                        ToastUtils.showShort(mContext, "添加成功")
                        finish()
                    } else {
                        QLog.e(TAG, result.msg)
                        ToastUtils.showShort(mContext, result.msg)
                    }
                }
            }
        })
        mDialog.show()
    }

    private fun queryDeviceInGroup() {
        val view = View.inflate(mContext, R.layout.query_device_ingroup_dialog, null)
        val mDialog = Dialog(this@DeviceListGroupActivity, R.style.dialogTM)
        mDialog.setContentView(view)
        mDialog.setCancelable(true)
        mDialog.setCanceledOnTouchOutside(false)
        val mEditPk = mDialog.findViewById<View>(R.id.edit_pk) as EditText
        val mBtCancel = mDialog.findViewById<View>(R.id.bt_cancel) as Button
        val mBtSure = mDialog.findViewById<View>(R.id.bt_sure) as Button
        mBtCancel.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                mDialog.dismiss()
            }
        })

        mBtSure.setOnClickListener { it ->
            mDialog.dismiss()
            val pk = MyUtils.getEditTextContent(mEditPk)
            if (pk.isNullOrEmpty()) {
                ToastUtils.showShort(mContext, "参数不能为空")
                return@setOnClickListener
            }
            startLoading()

            QuecDeviceGroupService.getDeviceList(
                dGid, null, null, pk, 1, 10
            ) { result ->
                finishLoading()
                if (result.isSuccess) {
                    if (shareCode.isNullOrEmpty()) {
                        ToastUtils.showShort(mContext, result.toString())
                    } else {
                        val intent = Intent(
                            this@DeviceListGroupActivity,
                            SharedGroupOfDevicesActivity::class.java
                        )
                        intent.putExtra("content", QuecGsonUtil.gsonString(result.data))
                        intent.putExtra("shareCode", shareCode)
                        startActivity(intent)
                    }
                } else {
                    QLog.e(TAG, result.msg)
                    ToastUtils.showShort(mContext, result.msg)
                }
            }
        }
        mDialog.show()
    }


    private fun deleteDeviceFromGroup() {
        val view = View.inflate(mContext, R.layout.add_device_to_group_dialog, null)
        val mDialog = Dialog(this@DeviceListGroupActivity, R.style.dialogTM)
        mDialog.setContentView(view)
        mDialog.setCancelable(true)
        mDialog.setCanceledOnTouchOutside(false)
        val mEditPk = mDialog.findViewById<View>(R.id.edit_pk) as EditText
        val mEditDk = mDialog.findViewById<View>(R.id.edit_dk) as EditText
        val mBtCancel = mDialog.findViewById<View>(R.id.bt_cancel) as Button
        val mBtSure = mDialog.findViewById<View>(R.id.bt_sure) as Button
        val mTvTitle = mDialog.findViewById<View>(R.id.tv_title) as TextView
        mTvTitle.text = "移除设备组中的设备"

        mBtCancel.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                mDialog.dismiss()
            }
        })

        mBtSure.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                mDialog.dismiss()
                val pk = MyUtils.getEditTextContent(mEditPk)
                val dk = MyUtils.getEditTextContent(mEditDk)
                if (pk.isNullOrEmpty() || dk.isNullOrEmpty()) {
                    ToastUtils.showShort(mContext, "参数不能为空")
                    return
                }
                startLoading()
                val deviceList = ArrayList<QuecDeviceModel>()
                val quecDeviceModel = QuecDeviceModel(pk, dk)
                deviceList.add(quecDeviceModel)
                QuecDeviceGroupService.deleteDeviceFromGroup(
                    dGid!!, deviceList
                ) { result ->
                    finishLoading()
                    if (result.isSuccess) {
                        ToastUtils.showShort(mContext, "移除成功")
                        finish()
                    } else {
                        QLog.e(TAG, result.msg)
                        ToastUtils.showShort(mContext, result.msg)
                    }
                }
            }
        })
        mDialog.show()
    }

    private fun deleteGroup() {
        startLoading()
        QuecDeviceGroupService.deleteDeviceGroup(
            dGid!!
        ) { result ->
            finishLoading()
            if (result.isSuccess) {
                ToastUtils.showShort(mContext, "成功")
                finish()
            } else {
                QLog.e(TAG, result.msg)
                ToastUtils.showShort(mContext, result.msg)
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
            if (result.isSuccess) {
                finishLoading()
                QLog.i(TAG, "result-:$result")
                ToastUtils.showShort(mContext, result.toString())
            } else {
                QLog.e(TAG, result.msg)
                ToastUtils.showShort(mContext, result.msg)
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
        val view = View.inflate(mContext, R.layout.accepter_change_group_name_dialog, null)
        val mDialog = Dialog(this@DeviceListGroupActivity, R.style.dialogTM)
        mDialog.setContentView(view)
        mDialog.setCancelable(true)
        mDialog.setCanceledOnTouchOutside(false)
        val mEditName = mDialog.findViewById<View>(R.id.edit_name) as EditText
        val mBtCancel = mDialog.findViewById<View>(R.id.bt_cancel) as Button
        val mBtSure = mDialog.findViewById<View>(R.id.bt_sure) as Button
        mBtCancel.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                mDialog.dismiss()
            }
        })
        mBtSure.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                mDialog.dismiss()
                val name = MyUtils.getEditTextContent(mEditName)
                if (name.isNullOrEmpty()) {
                    ToastUtils.showShort(mContext, "参数不能为空")
                    return
                }
                startLoading()

                QuecDeviceGroupService.getShareUserSetDeviceGroupName(
                    name, shareCode!!
                ) { result ->
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
        })
        mDialog.show()
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
                if (result.isSuccess) {
                    finish()
                    ToastUtils.showShort(mContext,"成功")
                } else {
                    ToastUtils.showShort(mContext, result.msg)
                    QLog.e(TAG, result.toString())
                }
            }
        }
    }

    private fun cancelShareGroupByOwner() {
        val view = View.inflate(mContext, R.layout.receiver_cancel_share_dialog, null)
        val mDialog = Dialog(this@DeviceListGroupActivity, R.style.dialogTM)
        mDialog.setContentView(view)
        mDialog.setCancelable(true)
        mDialog.setCanceledOnTouchOutside(false)
        val mEditCode = mDialog.findViewById<View>(R.id.edit_code) as EditText
        val mTvTitle = mDialog.findViewById<View>(R.id.tv_title) as TextView
        mTvTitle.text = "分享人取消设备组分享"
        val mBtCancel = mDialog.findViewById<View>(R.id.bt_cancel) as Button
        val mBtSure = mDialog.findViewById<View>(R.id.bt_sure) as Button
        mBtCancel.setOnClickListener { it ->
            mDialog.dismiss()
        }

        mBtSure.setOnClickListener(
            object : View.OnClickListener {
                override fun onClick(view: View) {
                    mDialog.dismiss()
                    val code = MyUtils.getEditTextContent(mEditCode)
                    if (code.isNullOrEmpty()) {
                        return
                    }
                    startLoading()

                    QuecDeviceGroupService.getOwerUserUnshare(code) { result ->
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
            })
        mDialog.show()
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

