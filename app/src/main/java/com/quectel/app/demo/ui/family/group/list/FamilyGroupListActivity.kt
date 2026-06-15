package com.quectel.app.demo.ui.family.group.list

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.quectel.app.demo.R
import com.quectel.app.demo.base.CommonListAdapter
import com.quectel.app.demo.databinding.ActivityCommonRvListBinding
import com.quectel.app.demo.dialog.EditTextPopup
import com.quectel.app.demo.dialog.SelectItemDialog
import com.quectel.app.demo.ui.family.BaseFamilyActivity
import com.quectel.app.demo.ui.family.group.function.FamilyGroupFunctionActivity
import com.quectel.app.smart_home_sdk.bean.QuecFamilyDeviceListParamsModel
import com.quectel.app.smart_home_sdk.service.QuecSmartHomeService
import com.quectel.basic.common.entity.QuecDeviceModel
import com.quectel.sdk.group.bean.QuecGroupCreateBean
import com.quectel.sdk.group.bean.QuecGroupCreateDeviceBean
import com.quectel.sdk.group.service.QuecGroupService

class FamilyGroupListActivity : BaseFamilyActivity<ActivityCommonRvListBinding>() {
    private lateinit var adapter: CommonListAdapter
    private val list = mutableListOf<QuecDeviceModel>()
    private val deviceList = mutableListOf<QuecDeviceModel>()

    override fun getViewBinding(): ActivityCommonRvListBinding {
        return ActivityCommonRvListBinding.inflate(layoutInflater)
    }

    override fun initView(savedInstanceState: Bundle?) {
        adapter = CommonListAdapter.init(binding.rvList) { onItemClick(list[it]) }

        binding.apply {
            title.text = getString(R.string.group_management)
            ivAdd.visibility = View.VISIBLE
            ivAdd.setOnClickListener { addGroup() }
        }
    }

    override fun initData() {

    }

    override fun onResume() {
        super.onResume()
        getList()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getList() {
        QuecSmartHomeService.getFamilyDeviceList(
            QuecFamilyDeviceListParamsModel(
                fid = getCurrentFid(),
                isGroupDeviceShow = true,
                isAddOwnerDevice = true,
                page = 1,
                pageSize = 100
            )
        ) {
            if (it.isSuccess) {
                list.clear()
                list.addAll(it.data.list.filter { item ->
                    //只展示群组设备
                    !item.gdid.isNullOrEmpty()
                })

                deviceList.clear()
                deviceList.addAll(it.data.list.filter { item ->
                    item.gdid.isNullOrEmpty()
                })

                adapter.list.clear()
                adapter.list.addAll(list.map { item ->
                    CommonListAdapter.Item(
                        item.deviceName,
                        item.productKey + " - " + item.deviceKey,
                        if (item.isShared) getString(R.string.share_group) else ""
                    )
                })
                adapter.notifyDataSetChanged()
            } else {
                handlerError(it)
            }
        }
    }

    private fun onItemClick(deviceModel: QuecDeviceModel) {
        startActivity(Intent(this, FamilyGroupFunctionActivity::class.java).apply {
            putExtra(FamilyGroupFunctionActivity.KEY_DEVICE, deviceModel)
        })
    }

    private fun addGroup() {
        SelectItemDialog(this).apply {
            addItem(getString(R.string.create_group_from_device_list)) { createGroup() }
            addItem(getString(R.string.receive_group_share)) { acceptShare() }
        }.show()
    }

    private fun createGroup() {
        QuecSmartHomeService.getFamilyDeviceList(
            QuecFamilyDeviceListParamsModel(
                fid = getCurrentFid(),
                isGroupDeviceShow = false,
                page = 1,
                pageSize = 100
            )
        ) {
            if (!it.isSuccess) {
                handlerError(it)
                return@getFamilyDeviceList
            }

            val list =
                it.data.list.filter { item -> item.gdid.isNullOrEmpty() && item.groupState == 1 && item.bindMode != 1 }

            if (list.isEmpty()) {
                showMessage(getString(R.string.no_available_device))
                return@getFamilyDeviceList
            }

            SelectItemDialog(this).apply {
                for (item in list) {
                    addItem(item.deviceName) {
                        QuecGroupService.createGroup(QuecGroupCreateBean().apply {
                            isCommonUsed = true
                            fid = getCurrentFid()
                            deviceList = listOf(QuecGroupCreateDeviceBean().also { bean ->
                                bean.productKey = item.productKey
                                bean.deviceKey = item.deviceKey
                            })
                        }) { ret ->
                            if (ret.isSuccess) {
                                showMessage(getString(R.string.add_success))
                                getList()
                            } else {
                                handlerError(ret)
                            }
                        }
                    }
                }
            }.show()
        }
    }

    private fun acceptShare() {
        EditTextPopup(this).apply {
            setTitle(getString(R.string.hint_share_code3))
            setEditTextListener {
                dismiss()
                QuecGroupService.acceptShare(it) { ret ->
                    if (ret.isSuccess) {
                        showMessage(getString(R.string.add_success))
                        getList()
                    } else {
                        handlerError(ret)
                    }
                }
            }
        }.showPopupWindow()
    }
}