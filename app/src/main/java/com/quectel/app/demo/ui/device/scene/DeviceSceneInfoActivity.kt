package com.quectel.app.demo.ui.device.scene

import android.app.Dialog
import android.os.Bundle
import com.quectel.app.demo.R
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
        addItem(getString(R.string.rename_scene)) { changeSceneName() }
        addItem(getString(R.string.delete_scene)) { deleteScene() }
        addItem(getString(R.string.get_scene_detail)) { getSceneInfo() }
        addItem(getString(R.string.execute_scene)) { executeScene() }
        addItem(getString(R.string.test_scene)) { executeTestScene() }
        addItem(getString(R.string.get_common_scene_list)) { getCommonSceneList() }
        addItem(getString(R.string.batch_add_common_scene)) { batchAddCommonScene() }
        addItem(getString(R.string.batch_delete_common_scene)) { batchDeleteCommonScene() }
        addItem(getString(R.string.get_scene_log_list)) { getSceneLogList() }
        addItem(getString(R.string.get_scene_log_detail)) { getSceneLogDetailInfo() }
        addItem(getString(R.string.clear_scene_log)) { clearSceneLog() }
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
            setTitle(getString(R.string.get_scene_log_detail))
            setHint(getString(R.string.hint_scene_log_id))
            setEditTextListener { content ->
                if (content.trim().toLongOrNull() == null) {
                    ToastUtils.showShort(this@DeviceSceneInfoActivity, getString(R.string.must_input_number))
                    return@setEditTextListener
                }
                QuecSceneService.getSceneLogDetailInfo(content.trim().toLong()) { result ->
                    handlerResult(result)
                    if (result.isSuccess) {
                        CommonDialog.showSimpleInfo(
                            this@DeviceSceneInfoActivity,
                            getString(R.string.get_scene_log_detail),
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
            setTitle(getString(R.string.get_scene_log_list))
            setHint1(getString(R.string.hint_last_log_id))
            setHint2(getString(R.string.hint_query_count))
            setEditTextListener { content1, content2 ->
                if (content1.toLongOrNull() == null || content2.toIntOrNull() == null) {
                    ToastUtils.showShort(this@DeviceSceneInfoActivity, getString(R.string.must_input_number))
                    return@setEditTextListener
                }
                QuecSceneService.getSceneLogList(content1.toLong(), content2.toInt()) { result ->
                    handlerResult(result)
                    if (result.isSuccess) {
                        CommonDialog.showSimpleInfo(
                            this@DeviceSceneInfoActivity,
                            getString(R.string.get_scene_log_list),
                            QuecGsonUtil.gsonString(result.data)
                        )
                    }
                }
            }
        }.showPopupWindow()
    }

    private fun batchDeleteCommonScene() {
        EditTextPopup(this).apply {
            setTitle(getString(R.string.batch_delete_common_scene))
            setHint(getString(R.string.hint_scene_id_list_comma))
            setEditTextListener { content ->
                val sceneList = content.split(",")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                    .toList()
                if (sceneList.isEmpty()) {
                    ToastUtils.showShort(this@DeviceSceneInfoActivity, getString(R.string.hint_scene_id_list_comma))
                    return@setEditTextListener
                }
                QuecSceneService.batchDeleteCommonScene(sceneList) {
                    handlerResult(it)
                    if (it.isSuccess) {
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
            setTitle(getString(R.string.batch_add_common_scene))
            setHint(getString(R.string.hint_scene_id_list_comma2))
            setEditTextListener { content ->
                val sceneList = content.split(",")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                    .toList()
                if (sceneList.isEmpty()) {
                    ToastUtils.showShort(this@DeviceSceneInfoActivity, getString(R.string.hint_scene_id_list_comma2))
                    return@setEditTextListener
                }
                QuecSceneService.batchAddCommonScene(sceneList) {
                    handlerResult(it)
                    if (it.isSuccess) {
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
                CommonDialog.showSimpleInfo(
                    this@DeviceSceneInfoActivity,
                    getString(R.string.common_scene_list),
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
                CommonDialog.showSimpleInfo(
                    this@DeviceSceneInfoActivity,
                    getString(R.string.scene_detail_title, mModel.sceneInfo.name),
                    QuecGsonUtil.gsonString(result.data)
                )
            }
        }
    }

    private fun deleteScene() {
        SurePopup(this).apply {
            setTitle(getString(R.string.confirm_delete_scene))
            setSureListener(object : OnSureListener {
                override fun sure() {
                    startLoading()
                    QuecSceneService.deleteScene(mModel.sceneInfo.sceneId) { result ->
                        handlerResult(result)
                        if (result.isSuccess) {
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
            setTitle(getString(R.string.rename_scene))
            setHint(getString(R.string.hint_new_name))
            setEditTextListener { content ->
                mModel.sceneInfo.name = content
                QuecSceneService.editScene(mModel) { it ->
                    handlerResult(result = it)
                    if (it.isSuccess) {
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