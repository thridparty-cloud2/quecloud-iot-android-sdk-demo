package com.quectel.app.demo.ui.device.scene

import android.app.Dialog
import android.os.Bundle
import com.quectel.app.demo.base.activity.QuecBaseActivity
import com.quectel.app.demo.common.AppVariable
import com.quectel.app.demo.databinding.ActivityDeviceSceneInfoBinding
import com.quectel.app.demo.dialog.CommonDialog
import com.quectel.app.demo.dialog.EditDoubleTextPopup
import com.quectel.app.demo.dialog.EditTextPopup
import com.quectel.app.demo.dialog.SurePopup
import com.quectel.app.demo.dialog.SurePopup.OnSureListener
import com.quectel.app.demo.ui.device.group.DeviceListGroupActivity
import com.quectel.app.demo.ui.device.scene.DeviceSceneActivity
import com.quectel.app.demo.utils.MyUtils
import com.quectel.app.demo.utils.ToastUtils
import com.quectel.app.device.deviceservice.QuecDeviceService
import com.quectel.basic.common.utils.QuecGsonUtil
import com.quectel.basic.queclog.QLog
import com.quectel.sdk.scene.bean.QuecSceneModel
import com.quectel.sdk.scene.service.QuecSceneService

class DeviceSceneInfoActivity : QuecBaseActivity<ActivityDeviceSceneInfoBinding>() {
    companion object {
        const val TAG = "DeviceSceneActivity"
    }

    lateinit var mModel: QuecSceneModel

    var mDialog: Dialog? = null


    override fun getViewBinding(): ActivityDeviceSceneInfoBinding {
        return ActivityDeviceSceneInfoBinding.inflate(layoutInflater)
    }

    override fun initView(savedInstanceState: Bundle?) {
        mModel = intent.getSerializableExtra("data") as QuecSceneModel
        addItem("修改场景名称") { changeSceneName() }
        addItem("删除场景") { deleteScene() }
        addItem("获取场景详情") { getSceneInfo() }
        addItem("场景执行") { executeScene() }
        addItem("测试场景") { executeTestScene() }
        addItem("获取常用场景列表") { getCommonSceneList() }
        addItem("批量添加常用场景") { batchAddCommonScene() }
        addItem("批量删除常用场景") { batchDeleteCommonScene() }
        addItem("获取场景日志列表") { getSceneLogList() }
        addItem("获取场景日志详情") { getSceneLogDetailInfo() }
        addItem("清除场景日志") { clearSceneLog() }
    }

    private fun clearSceneLog() {
        QuecSceneService.clearSceneLog { result ->
            handlerResult(result)
            if (result.isSuccess) {
                //清除日志成功
                QLog.i(TAG, "清除日志成功")
            }
        }
    }

    private fun getSceneLogDetailInfo() {
        EditTextPopup(this).apply {
            setTitle("获取场景日志详情")
            setHint("输入场景日志id")
            setEditTextListener { content ->
                if (content.trim().toLongOrNull() == null) {
                    ToastUtils.showShort(
                        this@DeviceSceneInfoActivity,
                        "必须输入为数字"
                    )
                    return@setEditTextListener
                }
                QuecSceneService.getSceneLogDetailInfo(content.trim().toLong()) { result ->
                    handlerResult(result)
                    if (result.isSuccess) {
                        //查询日志详情成功
                        //查询日志列表成功
                        CommonDialog.showSimpleInfo(
                            this@DeviceSceneInfoActivity,
                            "查询设备组详情",
                            QuecGsonUtil.gsonString(result.data)
                        )
                    }
                }
            }
            showPopupWindow()
        }
    }

    private fun getSceneLogList() {
        EditDoubleTextPopup(this).apply {
            setTitle("获取场景日志列表")
            setHint1("请输入最后一条执行日志的id")
            setHint2("请输入查询的数据数量")
            setEditTextListener { content1, content2 ->
                if (content1.toLongOrNull() == null || content2.toIntOrNull() == null) {
                    ToastUtils.showShort(
                        this@DeviceSceneInfoActivity,
                        "必须输入为数字"
                    )
                    return@setEditTextListener
                }
                QuecSceneService.getSceneLogList(content1.toLong(), content2.toInt()) { result ->
                    handlerResult(result)
                    if (result.isSuccess) {
                        //查询日志列表成功
                        CommonDialog.showSimpleInfo(
                            this@DeviceSceneInfoActivity,
                            "查询设备组详情",
                            QuecGsonUtil.gsonString(result.data)
                        )
                    }
                }
            }
        }.showPopupWindow()
    }

    private fun batchDeleteCommonScene() {
        EditTextPopup(this).apply {
            setTitle("批量删除常用场景")
            setHint("输入场景id列表 用,隔开依次输入\r\n例如:sceneId1,sceneId2")
            setEditTextListener { content ->
                val sceneList = content.split(",")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                    .toList()
                if (sceneList.isEmpty()) {
                    ToastUtils.showShort(
                        this@DeviceSceneInfoActivity,
                        "输入场景id列表依次，请按用,隔开依次输入\r\n例如:sceneId1,sceneId2"
                    )
                    return@setEditTextListener
                }
                QuecSceneService.batchDeleteCommonScene(sceneList) {
                    handlerResult(it)
                    if (it.isSuccess) {
                        //删除常用场景成功
                        dismiss()
                        AppVariable.setSceneChange()
                    }
                }
            }
            showPopupWindow()
        }
    }

    private fun batchAddCommonScene() {
        EditTextPopup(this).apply {
            setTitle("批量添加常用场景")
            setHint("输入场景id列表 用,隔开依次输入 例如:sceneId1,sceneId2")
            setEditTextListener { content ->
                val sceneList = content.split(",")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                    .toList()
                if (sceneList.isEmpty()) {
                    ToastUtils.showShort(
                        this@DeviceSceneInfoActivity,
                        "输入场景id列表依次，请按用,隔开依次输入 例如:sceneId1,sceneId2"
                    )
                    return@setEditTextListener
                }
                QuecSceneService.batchAddCommonScene(sceneList) {
                    handlerResult(it)
                    if (it.isSuccess) {
                        //添加常用场景成功
                        dismiss()
                        AppVariable.setSceneChange()
                    }
                }
            }
            showPopupWindow()
        }
    }

    private fun getCommonSceneList() {
        QuecSceneService.getCommonSceneList(1, 10) { result ->
            handlerResult(result)
            if (result.isSuccess) {
                //获取常用场景列表成功
                CommonDialog.showSimpleInfo(
                    this@DeviceSceneInfoActivity,
                    "常用场景列表",
                    QuecGsonUtil.gsonString(result.data)
                )
            }
        }
    }

    private fun executeTestScene() {
        QuecSceneService.executeTestScene(mModel) { result ->
            handlerResult(result)
            if (result.isSuccess) {
                //执行场景测试成功
                AppVariable.setSceneChange()
            }
        }
    }

    private fun executeScene() {
        QuecSceneService.executeScene(mModel.sceneInfo.sceneId) { result ->
            handlerResult(result)
            if (result.isSuccess) {
                AppVariable.setSceneChange()
                //执行场景成功
            }
        }
    }

    private fun getSceneInfo() {
        QuecSceneService.getSceneInfo(mModel.sceneInfo.sceneId) { result ->
            handlerResult(result)
            if (result.isSuccess) {
                //获取场景详情成功
                CommonDialog.showSimpleInfo(
                    this@DeviceSceneInfoActivity,
                    "${mModel.sceneInfo.name}的场景详情",
                    QuecGsonUtil.gsonString(result.data)
                )
            }
        }
    }

    private fun deleteScene() {
        SurePopup(this).apply {
            setTitle("确定删除该场景?")
            setSureListener(object : OnSureListener {
                override fun sure() {
                    startLoading()
                    QuecSceneService.deleteScene(mModel.sceneInfo.sceneId) { result ->
                        handlerResult(result)
                        if (result.isSuccess) {
                            //删除场景成功
                            AppVariable.setSceneChange()
                            dismiss()
                            finish()
                        }
                    }
                }
            })
        }.showPopupWindow()
    }

    private fun changeSceneName() {
        EditTextPopup(this).apply {
            setTitle("修改场景名称")
            setHint("请输入要改的新名称")
            setEditTextListener { content ->
                mModel.sceneInfo.name = content
                QuecSceneService.editScene(mModel) { it ->
                    handlerResult(result = it)
                    if (it.isSuccess) {
                        //修改场景成功
                        AppVariable.setSceneChange()
                        dismiss()
                    }
                }
            }
        }.showPopupWindow()
    }

    override fun initData() {
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
}