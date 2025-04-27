package com.quectel.app.demo.ui.device.scene

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.quectel.app.demo.adapter.DeviceSceneAdapter
import com.quectel.app.demo.base.activity.QuecBaseActivity
import com.quectel.app.demo.common.AppVariable
import com.quectel.app.demo.databinding.ActivityDeviceSceneBinding
import com.quectel.app.demo.dialog.EditSceneTextPopup
import com.quectel.app.demo.utils.MyUtils
import com.quectel.app.demo.utils.ToastUtils
import com.quectel.sdk.scene.bean.QuecMetaDataModel
import com.quectel.sdk.scene.bean.QuecSceneActionModel
import com.quectel.sdk.scene.bean.QuecSceneInfoModel
import com.quectel.sdk.scene.bean.QuecSceneModel
import com.quectel.sdk.scene.service.QuecSceneService

class DeviceSceneActivity : QuecBaseActivity<ActivityDeviceSceneBinding>() {
    companion object {
        const val TAG = "DeviceSceneActivity"
    }

    var mDialog: Dialog? = null
    var mAdapter: DeviceSceneAdapter? = null
    private var data = ArrayList<String>()


    override fun getViewBinding(): ActivityDeviceSceneBinding {
        return ActivityDeviceSceneBinding.inflate(layoutInflater)
    }

    override fun initView(savedInstanceState: Bundle?) {
        mAdapter = DeviceSceneAdapter(this@DeviceSceneActivity, null)
        binding.rvList.setAdapter(mAdapter)
        binding.rvList.setLayoutManager(LinearLayoutManager(this@DeviceSceneActivity))
        binding.ivAdd.setOnClickListener { lt ->
            addScene()
        }
        mAdapter!!.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(
                adapter: BaseQuickAdapter<*, *>,
                view: View,
                position: Int,
            ) {
                val data: QuecSceneModel =
                    adapter.data[position] as QuecSceneModel
                var intent = Intent(this@DeviceSceneActivity, DeviceSceneInfoActivity::class.java)
                intent.putExtra("data", data)
                startActivity(intent)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (AppVariable.isSceneInfoChange) {
            AppVariable.isSceneInfoChange = false
            queryGroupList()
        }
    }

    override fun initData() {
        queryGroupList()
    }

    private fun addScene() {
        EditSceneTextPopup(mContext).apply {
            setTitle("添加一个新的场景")
            setHint1("场景名称")
            setHint2("场景图标")
            setHint3("actionModel.code 物模型标志符")
            setHint4("actionModel.dataType 物模型数据类型")
            setHint5("actionModel.id 物模型功能ID")
            setHint6("actionModel.name 物模型功能名称")
            setHint7("actionModel.subName 物模型值subName")
            setHint8("actionModel.subType 物模型subType")
            setHint9("actionModel.type  物模型功能类型")
            setHint10("actionModel.value 物模型值")
            setHint11("设备pk")
            setHint12("设备dk")
            setHint13("物模型数据类型")
            setSure("添加")
            setEditTextListener {
                    sceneInfoModelName, sceneInfoModelIcon, actionModelCode, actionModelDataType,
                    actionModelId, actionModelName, actionModelSubName, actionModelSubType,
                    actionModelType, actionModelValue, metaDataModelProductKey, metaDataModelDeviceKey,
                    metaDataModelDeviceType,
                ->
                val model = QuecSceneModel()
                val sceneInfoModel = QuecSceneInfoModel()
                val metaDataModel = QuecMetaDataModel()
                val actionModel = QuecSceneActionModel()
                if (sceneInfoModelName.isNullOrEmpty() || sceneInfoModelIcon.isNullOrEmpty() ||
                    actionModelCode.isNullOrEmpty() || actionModelDataType.isNullOrEmpty() ||
                    actionModelId.isNullOrEmpty() || actionModelName.isNullOrEmpty() ||
                    actionModelSubName.isNullOrEmpty() || actionModelSubType.isNullOrEmpty() ||
                    actionModelType.isNullOrEmpty() || actionModelValue.isNullOrEmpty() ||
                    metaDataModelProductKey.isNullOrEmpty() || metaDataModelDeviceKey.isNullOrEmpty() ||
                    metaDataModelDeviceType.isNullOrEmpty()
                ) {
                    ToastUtils.showShort(this@DeviceSceneActivity, "各种参数不能为空")
                    return@setEditTextListener
                }

                model.isIsCommon = true
                sceneInfoModel.name = sceneInfoModelName
                sceneInfoModel.icon = sceneInfoModelIcon
                actionModel.code = actionModelCode
                actionModel.dataType = actionModelDataType
                if (actionModelId.toIntOrNull() == null
                ) {
                    ToastUtils.showShort(this@DeviceSceneActivity, "物模型功能ID必须输入为int")
                    return@setEditTextListener
                }
                actionModel.id = actionModelId.toInt()
                actionModel.name = actionModelName
                actionModel.subName = actionModelSubName
                actionModel.subType = actionModelSubType
                actionModel.type = actionModelType
                if (actionModelValue.trim().toIntOrNull() != null) {
                    actionModel.value = actionModelValue.toInt()
                } else if (actionModelValue.trim() == "true") {
                    actionModel.value = true
                } else if (actionModelValue.trim() == "false") {
                    actionModel.value = false
                } else {
                    actionModel.value = actionModelValue
                }

                metaDataModel.productKey = metaDataModelProductKey
                metaDataModel.deviceKey = metaDataModelDeviceKey
                if (metaDataModelDeviceType.toIntOrNull() == null
                ) {
                    ToastUtils.showShort(this@DeviceSceneActivity, "物模型功能类型必须输入为int")
                    return@setEditTextListener
                }
                metaDataModel.deviceType = metaDataModelDeviceType.toInt()

                model.sceneInfo = sceneInfoModel
                sceneInfoModel.metaDataList = listOf(metaDataModel)
                metaDataModel.actionList = listOf(actionModel)
                QuecSceneService.addScene(model) { result ->
                    handlerResult(result)
                    if (result.isSuccess) {
                        //添加场景成功
                        dismiss()
                        queryGroupList()
                    }
                }

            }
        }.showPopupWindow()
    }

    private fun queryGroupList() {
        startLoading()
        QuecSceneService.getSceneList(1, 10) { result ->
            finishLoading()
            handlerResult(result)
            if (result.isSuccess) {
                val data = result.data
                mAdapter!!.setNewInstance(data.list)
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

}
