package com.quectel.app.demo.ui.device.automate

import android.app.Dialog
import android.os.Bundle
import com.quectel.app.demo.R
import com.quectel.app.demo.base.activity.QuecBaseActivity
import com.quectel.app.demo.common.AppVariable
import com.quectel.app.demo.databinding.ActivityDeviceAutoMateInfoBinding
import com.quectel.app.demo.dialog.CommonDialog
import com.quectel.app.demo.dialog.EditDoubleTextPopup
import com.quectel.app.demo.dialog.EditTextPopup
import com.quectel.app.demo.utils.MyUtils
import com.quectel.app.demo.utils.ToastUtils
import com.quectel.app.device.bean.ModelBasic
import com.quectel.app.device.utils.ObjectModelParse
import com.quectel.basic.common.utils.QuecGsonUtil
import com.quectel.basic.queclog.QLog
import com.quectel.sdk.automate.api.convertor.PropertyConvertor
import com.quectel.sdk.automate.api.model.QuecAutoListItemModel
import com.quectel.sdk.automate.api.model.QuecAutomationActionModel
import com.quectel.sdk.automate.service.QuecAutomateService
import org.json.JSONArray
import org.json.JSONException

class AutoMateInfoActivity : QuecBaseActivity<ActivityDeviceAutoMateInfoBinding>() {

    companion object {
        const val TAG = "AutoMateInfoActivity"
    }

    lateinit var mData: QuecAutoListItemModel

    var mDialog: Dialog? = null

    override fun getViewBinding(): ActivityDeviceAutoMateInfoBinding {
        return ActivityDeviceAutoMateInfoBinding.inflate(layoutInflater)
    }

    override fun initView(savedInstanceState: Bundle?) {
        mData = intent.getSerializableExtra("data") as QuecAutoListItemModel
        addItem(getString(R.string.edit_automation)) { changeAutoName() }
        addItem(getString(R.string.delete_automation)) { deleteAuto() }
        addItem(getString(R.string.query_tsl_for_automation)) { getAutomationTSLWithProductKey() }
        addItem(getString(R.string.get_automation_detail)) { getAutomationInfo() }
        addItem(getString(R.string.toggle_automation)) { updateAutomationSwitchStatus() }
        addItem(getString(R.string.test_automation)) { testAutomationInfo() }
        addItem(getString(R.string.get_automation_test_result)) { getTestAutomationResult() }
        addItem(getString(R.string.get_automation_log_list)) { getAutomationLogList() }
        addItem(getString(R.string.get_log_detail)) { getAutomationLogDetail() }
        addItem(getString(R.string.clear_automation_log)) { clearAutomationLogs() }
    }

    private fun clearAutomationLogs() {
        QuecAutomateService.clearAutomationLogs {
            runOnUiThread {
                dismissLoading()
                handlerResult(it)
                if (it.isSuccess) {
                    // Request successful
                }
            }
        }
    }

    private fun getAutomationLogDetail() {
        EditTextPopup(mContext).apply {
            setTitle(getString(R.string.get_log_detail))
            setHint(getString(R.string.hint_automation_log_id))
            setEditTextListener { content ->
                if (content.trim().toLongOrNull() == null) {
                    ToastUtils.showShort(this@AutoMateInfoActivity, getString(R.string.param_exception))
                    return@setEditTextListener
                }
                startLoading()
                QuecAutomateService.getAutomationLogDetail(content.toLong()) {
                    runOnUiThread {
                        dismiss()
                        finishLoading()
                        handlerResult(it)
                        if (it.isSuccess) {
                            CommonDialog.showSimpleInfo(
                                this@AutoMateInfoActivity, getString(R.string.get_log_detail),
                                QuecGsonUtil.gsonString(it.data)
                            )
                        }
                    }
                }
            }
        }.showPopupWindow()
    }

    private fun getAutomationLogList() {
        EditTextPopup(mContext).apply {
            setTitle(getString(R.string.get_automation_log_list))
            setHint(getString(R.string.hint_last_log_id2))
            setEditTextListener { content ->
                if (content.trim().toLongOrNull() == null) {
                    ToastUtils.showShort(this@AutoMateInfoActivity, getString(R.string.param_exception))
                    return@setEditTextListener
                }
                startLoading()
                QuecAutomateService.getAutomationLogList(content.toLong(), 10) {
                    runOnUiThread {
                        dismiss()
                        finishLoading()
                        handlerResult(it)
                        if (it.isSuccess) {
                            CommonDialog.showSimpleInfo(
                                this@AutoMateInfoActivity, getString(R.string.get_automation_log_list),
                                QuecGsonUtil.gsonString(it.data)
                            )
                        }
                    }
                }
            }
        }.showPopupWindow()
    }

    private fun getTestAutomationResult() {
        EditTextPopup(mContext).apply {
            setTitle(getString(R.string.get_automation_test_result))
            setHint(getString(R.string.hint_test_result_id))
            setEditTextListener { content ->
                if (content.trim().toLongOrNull() == null) {
                    ToastUtils.showShort(this@AutoMateInfoActivity, getString(R.string.param_exception))
                    return@setEditTextListener
                }
                startLoading()
                QuecAutomateService.getTestAutomationResult(content.toLong()) {
                    runOnUiThread {
                        dismiss()
                        finishLoading()
                        handlerResult(it)
                        if (it.isSuccess) {
                            CommonDialog.showSimpleInfo(
                                this@AutoMateInfoActivity, getString(R.string.get_automation_test_result),
                                QuecGsonUtil.gsonString(it.data)
                            )
                        }
                    }
                }
            }
        }.showPopupWindow()
    }

    private fun testAutomationInfo() {
        EditDoubleTextPopup(mContext).apply {
            setTitle(getString(R.string.test_automation))
            setHint1(getString(R.string.hint_test_automation_scene_name))
            setHint2(getString(R.string.hint_test_automation_scene_id))
            setEditTextListener { content1, content2 ->
                if (content1.isNullOrEmpty() || content2.isNullOrEmpty()) {
                    ToastUtils.showShort(this@AutoMateInfoActivity, getString(R.string.param_cannot_be_null))
                    return@setEditTextListener
                }
                startLoading()
                QuecAutomateService.testAutomationInfo(
                    listOf(
                        QuecAutomationActionModel(
                            type = 4,
                            icon = "https://iot-oss.quectelcn.com/quec_scene_1.png",
                            name = content1,
                            sceneId = content2,
                            sort = 1,
                        )
                    ), 30
                ) {
                    runOnUiThread {
                        dismiss()
                        finishLoading()
                        handlerResult(it)
                        if (it.isSuccess) { }
                    }
                }
            }
        }.showPopupWindow()
    }

    private fun updateAutomationSwitchStatus() {
        startLoading()
        QuecAutomateService.updateAutomationSwitchStatus(automationId = mData.automationId, true) {
            runOnUiThread {
                finishLoading()
                handlerResult(it)
                if (it.isSuccess) {
                    // Request successful
                }
            }
        }
    }

    private fun getAutomationInfo() {
        startLoading()
        QuecAutomateService.getAutomationInfo(automationId = mData.automationId) {
            runOnUiThread {
                finishLoading()
                handlerResult(it)
                if (it.isSuccess) {
                    CommonDialog.showSimpleInfo(
                        this@AutoMateInfoActivity, getString(R.string.automation_detail_title),
                        QuecGsonUtil.gsonString(it.data)
                    )
                }
            }
        }
    }

    private fun getAutomationTSLWithProductKey() {
        val modelConvertor = object : PropertyConvertor<ModelBasic<*>> {
            override fun convert(objectJson: JSONArray): List<ModelBasic<*>> {
                val modelBasics: MutableList<ModelBasic<*>> = ArrayList()
                try {
                    return ObjectModelParse.buildModelListContent(objectJson)
                } catch (e: JSONException) {
                    QLog.e("TslModelUtils", "convert error $e")
                }
                return modelBasics
            }
        }
        EditTextPopup(mContext).apply {
            setTitle(getString(R.string.query_tsl_for_automation))
            setHint(getString(R.string.hint_product_key))
            setEditTextListener { content ->
                if (content.isNullOrEmpty()) {
                    ToastUtils.showShort(this@AutoMateInfoActivity, getString(R.string.pk_cannot_be_empty))
                    return@setEditTextListener
                }
                startLoading()
                QuecAutomateService.getAutomationTSLWithProductKey(
                    productKey = content,
                    0,
                    modelConvertor
                ) {
                    runOnUiThread {
                        AppVariable.setAutoMateChange()
                        finishLoading()
                        handlerResult(it)
                        if (it.isSuccess) {
                            CommonDialog.showSimpleInfo(
                                this@AutoMateInfoActivity, getString(R.string.tsl_for_automation_result),
                                QuecGsonUtil.gsonString(it.data)
                            )
                        }
                    }
                }
            }
        }.showPopupWindow()
    }

    private fun deleteAuto() {
        startLoading()
        QuecAutomateService.deleteAutomation(mData.automationId) {
            runOnUiThread {
                AppVariable.setAutoMateChange()
                finishLoading()
                handlerResult(it)
                // Request successful
                if (it.isSuccess) {
                    finish()
                }
            }
        }
    }

    private fun changeAutoName() {
        EditTextPopup(mContext).apply {
            setTitle(getString(R.string.edit_automation_name))
            setHint(getString(R.string.hint_new_name))
            setEditTextListener { content ->
                startLoading()
                QuecAutomateService.getAutomationInfo(mData.automationId) { getInfo ->
                    if (getInfo.isSuccess) {
                        val model = getInfo.data
                        model.name = content
                        QuecAutomateService.editAutomation(model) {
                            runOnUiThread {
                                finishLoading()
                                handlerResult(it)
                                if (it.isSuccess) {
                                    AppVariable.setAutoMateChange()
                                    dismiss()
                                }
                            }
                        }
                    } else {
                        runOnUiThread {
                            finishLoading()
                            dismiss()
                            ToastUtils.showShort(this@AutoMateInfoActivity, getInfo.msg)
                        }
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