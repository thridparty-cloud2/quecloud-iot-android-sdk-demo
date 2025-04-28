package com.quectel.app.demo.ui.device.automate

import android.app.Dialog
import android.os.Bundle
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
        addItem("编辑自动化") { changeAutoName() }
        addItem("删除自动化") { deleteAuto() }
        addItem("查询设备可作为自动化条件和触发动作的物模型") { getAutomationTSLWithProductKey() }
        addItem("获取自动化详情") { getAutomationInfo() }
        addItem("开启关闭自动化") { updateAutomationSwitchStatus() }
        addItem("测试自动化") { testAutomationInfo() }
        addItem("获取自动化测试的结果") { getTestAutomationResult() }
        addItem("获取自动化日志列表") { getAutomationLogList() }
        addItem("获取日志详情") { getAutomationLogDetail() }
        addItem("清除自动化日志") { clearAutomationLogs() }
    }

    private fun clearAutomationLogs() {
        QuecAutomateService.clearAutomationLogs {
            runOnUiThread {
                dismissLoading()
                handlerResult(it)
                if (it.isSuccess) {
                    //请求成功
                }
            }
        }
    }

    private fun getAutomationLogDetail() {
        EditTextPopup(mContext).apply {
            setTitle("获取日志详情")
            setHint("请输入自动化日志Id")
            setEditTextListener { content ->
                if (content.trim().toLongOrNull() == null) {
                    ToastUtils.showShort(this@AutoMateInfoActivity, "参数异常")
                    return@setEditTextListener
                }
                startLoading()
                QuecAutomateService.getAutomationLogDetail(content.toLong()) {
                    runOnUiThread {
                        dismiss()
                        finishLoading()
                        handlerResult(it)
                        if (it.isSuccess) {
                            //请求成功
                            CommonDialog.showSimpleInfo(
                                this@AutoMateInfoActivity, "获取日志详情",
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
            setTitle("获取自动化日志列表")
            setHint("请输入最后一条日志Id")
            setEditTextListener { content ->
                if (content.trim().toLongOrNull() == null) {
                    ToastUtils.showShort(this@AutoMateInfoActivity, "参数异常")
                    return@setEditTextListener
                }
                startLoading()
                QuecAutomateService.getAutomationLogList(content.toLong(), 10) {
                    runOnUiThread {
                        dismiss()
                        finishLoading()
                        handlerResult(it)
                        if (it.isSuccess) {
                            //请求成功
                            CommonDialog.showSimpleInfo(
                                this@AutoMateInfoActivity, "获取自动化日志列表",
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
            setTitle("获取自动化测试的结果")
            setHint("请输入测试自动化结果的id")
            setEditTextListener { content ->
                if (content.trim().toLongOrNull() == null) {
                    ToastUtils.showShort(this@AutoMateInfoActivity, "参数异常")
                    return@setEditTextListener
                }
                startLoading()
                QuecAutomateService.getTestAutomationResult(content.toLong()) {
                    runOnUiThread {
                        dismiss()
                        finishLoading()
                        handlerResult(it)
                        if (it.isSuccess) {
                            //请求成功
                            CommonDialog.showSimpleInfo(
                                this@AutoMateInfoActivity, "获取自动化测试的结果",
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
            setTitle("测试自动化")
            setHint1("场景名称")
            setHint2("场景id")
            setEditTextListener { content1, content2 ->
                if (content1.trim().isNullOrEmpty() || content2.trim().isNullOrEmpty()) {
                    ToastUtils.showShort(this@AutoMateInfoActivity, "参数不可为空")
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
                        if (it.isSuccess) {
                            //请求成功
                        }
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
                    //请求成功
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
                    //请求成功
                    CommonDialog.showSimpleInfo(
                        this@AutoMateInfoActivity, "自动化详情",
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
                    //此方法在核心库中有定义
                    return ObjectModelParse.buildModelListContent(objectJson)
                } catch (e: JSONException) {
                    QLog.e("TslModelUtils", "convert error $e")
                }
                return modelBasics
            }
        }
        EditTextPopup(mContext).apply {
            setTitle("查询设备可作为自动化条件和触发动作的物模型")
            setHint("请输入productKey")
            setEditTextListener { content ->
                if (content.trim().isNullOrEmpty()) {
                    ToastUtils.showShort(this@AutoMateInfoActivity, "pk不可为空")
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
                        //请求成功
                        if (it.isSuccess) {
                            CommonDialog.showSimpleInfo(
                                this@AutoMateInfoActivity, "自动化条件和触发动作的物模型",
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
                //请求成功
                if (it.isSuccess) {
                    finish()
                }
            }
        }
    }

    private fun changeAutoName() {
        EditTextPopup(mContext).apply {
            setTitle("编辑自动化名称")
            setHint("请输入新名称")
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
                                    //请求成功
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