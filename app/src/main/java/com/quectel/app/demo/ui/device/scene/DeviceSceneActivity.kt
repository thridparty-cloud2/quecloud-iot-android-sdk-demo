package com.quectel.app.demo.ui.device.scene

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.quectel.app.demo.adapter.DeviceSceneAdapter
import com.quectel.app.demo.base.activity.QuecBaseActivity
import com.quectel.app.demo.bean.EditListBean
import com.quectel.app.demo.common.AppVariable
import com.quectel.app.demo.databinding.ActivityDeviceSceneBinding
import com.quectel.app.demo.dialog.EditListTextPopup
import com.quectel.app.demo.utils.MyUtils
import com.quectel.app.demo.utils.ToastUtils
import com.quectel.sdk.scene.bean.QuecMetaDataModel
import com.quectel.sdk.scene.bean.QuecSceneActionModel
import com.quectel.sdk.scene.bean.QuecSceneInfoModel
import com.quectel.sdk.scene.bean.QuecSceneModel
import com.quectel.sdk.scene.service.QuecSceneService
import com.quectel.app.demo.R

class DeviceSceneActivity : QuecBaseActivity<ActivityDeviceSceneBinding>() {
    companion object {
        const val TAG = "DeviceSceneActivity"
    }
    private lateinit var mAdapter: DeviceSceneAdapter


    override fun getViewBinding(): ActivityDeviceSceneBinding {
        return ActivityDeviceSceneBinding.inflate(layoutInflater)
    }

    override fun initView(savedInstanceState: Bundle?) {
        mAdapter = DeviceSceneAdapter(this@DeviceSceneActivity, null)
        binding.rvList.setAdapter(mAdapter)
        binding.rvList.setLayoutManager(LinearLayoutManager(this@DeviceSceneActivity))
        binding.ivAdd.setOnClickListener {
            addScene()
        }
        mAdapter.setOnItemClickListener { adapter, _, position ->
            val data: QuecSceneModel =
                adapter.data[position] as QuecSceneModel
            val intent = Intent(this@DeviceSceneActivity, DeviceSceneInfoActivity::class.java)
            intent.putExtra("data", data)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        if (AppVariable.isSceneInfoChange) {
            AppVariable.isSceneInfoChange = false
            querySceneList()
        }
    }

    override fun initData() {
        querySceneList()
    }

    private fun addScene() {
        val arrayData: ArrayList<EditListBean> = ArrayList()
        arrayData.add(EditListBean(getString(R.string.scene_name_label)))
        arrayData.add(EditListBean(getString(R.string.scene_icon_label)))
        arrayData.add(EditListBean(getString(R.string.tsl_code_label)))
        arrayData.add(EditListBean(getString(R.string.tsl_data_type_field)))
        arrayData.add(EditListBean(getString(R.string.tsl_id_label)))
        arrayData.add(EditListBean(getString(R.string.tsl_func_name_label)))
        arrayData.add(EditListBean(getString(R.string.tsl_sub_name_label)))
        arrayData.add(EditListBean(getString(R.string.tsl_sub_type_label)))
        arrayData.add(EditListBean(getString(R.string.tsl_type_label)))
        arrayData.add(EditListBean(getString(R.string.tsl_value_label)))
        arrayData.add(EditListBean(getString(R.string.device_pk_label)))
        arrayData.add(EditListBean(getString(R.string.device_dk_label)))
        arrayData.add(EditListBean(getString(R.string.tsl_data_type_label)))

        EditListTextPopup(mContext).apply {
            setTitle(getString(R.string.add_new_scene))
            setSure(getString(R.string.add_label))
            setDataList(arrayData)
            setEditTextListener {
                val model = QuecSceneModel()
                val sceneInfoModel = QuecSceneInfoModel()
                val metaDataModel = QuecMetaDataModel()
                val actionModel = QuecSceneActionModel()
                val sceneInfoModelName =
                    arrayData.find { it.name == arrayData[0].name }?.value
                val sceneInfoModelIcon =
                    arrayData.find { it.name == arrayData[1].name }?.value
                val actionModelCode =
                    arrayData.find { it.name == arrayData[2].name }?.value
                val actionModelDataType =
                    arrayData.find { it.name == arrayData[3].name }?.value
                val actionModelId =
                    arrayData.find { it.name == arrayData[4].name }?.value
                val actionModelName =
                    arrayData.find { it.name == arrayData[5].name }?.value
                val actionModelSubName =
                    arrayData.find { it.name == arrayData[6].name }?.value
                val actionModelSubType =
                    arrayData.find { it.name == arrayData[7].name }?.value
                val actionModelType =
                    arrayData.find { it.name == arrayData[8].name }?.value
                val actionModelValue =
                    arrayData.find { it.name == arrayData[9].name }?.value
                val metaDataModelProductKey =
                    arrayData.find { it.name == arrayData[10].name }?.value
                val metaDataModelDeviceKey =
                    arrayData.find { it.name == arrayData[11].name }?.value
                val metaDataModelDeviceType =
                    arrayData.find { it.name == arrayData[12].name }?.value

                if (sceneInfoModelName.isNullOrEmpty() || sceneInfoModelIcon.isNullOrEmpty() ||
                    actionModelCode.isNullOrEmpty() || actionModelDataType.isNullOrEmpty() ||
                    actionModelId.isNullOrEmpty() || actionModelName.isNullOrEmpty() ||
                    actionModelSubName.isNullOrEmpty() || actionModelSubType.isNullOrEmpty() ||
                    actionModelType.isNullOrEmpty() || actionModelValue.isNullOrEmpty() ||
                    metaDataModelProductKey.isNullOrEmpty() || metaDataModelDeviceKey.isNullOrEmpty() ||
                    metaDataModelDeviceType.isNullOrEmpty()
                ) {
                    ToastUtils.showShort(this@DeviceSceneActivity, getString(R.string.params_cannot_be_empty))
                    return@setEditTextListener
                }

                model.isIsCommon = true
                sceneInfoModel.name = sceneInfoModelName
                sceneInfoModel.icon = sceneInfoModelIcon
                actionModel.code = actionModelCode
                actionModel.dataType = actionModelDataType
                if (actionModelId.toIntOrNull() == null) {
                    ToastUtils.showShort(this@DeviceSceneActivity, getString(R.string.tsl_id_must_be_int))
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
                if (metaDataModelDeviceType.toIntOrNull() == null) {
                    ToastUtils.showShort(this@DeviceSceneActivity, getString(R.string.tsl_type_must_be_int))
                    return@setEditTextListener
                }
                metaDataModel.deviceType = metaDataModelDeviceType.toInt()

                model.sceneInfo = sceneInfoModel
                sceneInfoModel.metaDataList = listOf(metaDataModel)
                metaDataModel.actionList = listOf(actionModel)
                QuecSceneService.addScene(model) { ret ->
                    handlerResult(ret)
                    if (ret.isSuccess) {
                        dismiss()
                        querySceneList()
                    }
                }
            }
        }.showPopupWindow()
    }

    private fun querySceneList() {
        showOrHideLoading(true)
        QuecSceneService.getSceneList(1, 10) { result ->
            showOrHideLoading(false)
            if (result.isSuccess) {
                val data = result.data
                mAdapter.setNewInstance(data.list)
            } else {
                handlerResult(result)
            }
        }
    }
}
