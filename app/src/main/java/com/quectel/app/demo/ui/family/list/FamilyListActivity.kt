package com.quectel.app.demo.ui.family.list

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.quectel.app.demo.base.CommonListAdapter
import com.quectel.app.demo.base.activity.QuecBaseActivity
import com.quectel.app.demo.databinding.ActivityFamilyListBinding
import com.quectel.app.demo.dialog.EditTextPopup
import com.quectel.app.demo.dialog.SelectItemDialog
import com.quectel.app.demo.ui.family.BaseFamilyActivity
import com.quectel.app.demo.ui.family.function.FamilyFunctionActivity
import com.quectel.app.smart_home_sdk.bean.QuecFamilyItemModel
import com.quectel.app.smart_home_sdk.bean.QuecFamilyParamModel
import com.quectel.app.smart_home_sdk.bean.QuecInviteItemModel
import com.quectel.app.smart_home_sdk.service.QuecSmartHomeService

class FamilyListActivity : QuecBaseActivity<ActivityFamilyListBinding>() {
    private val showList = mutableListOf<CommonListAdapter.Item>()
    private val itemList = mutableListOf<QuecFamilyItemModel>()
    private lateinit var adapter: CommonListAdapter

    override fun getViewBinding(): ActivityFamilyListBinding {
        return ActivityFamilyListBinding.inflate(layoutInflater)
    }

    override fun initView(savedInstanceState: Bundle?) {
        binding.apply {
            adapter = CommonListAdapter.init(rvList, showList) { onItemClick(itemList[it]) }
        }
    }

    override fun initData() {

    }

    override fun onResume() {
        super.onResume()
        getFamilyList()
    }

    override fun initTestItem() {
        super.initTestItem()


        addItem("新增家庭") {
            addFamily()
        }

        addItem("查询待接收分享的家庭列表") {
            queryInviteList()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getFamilyList() {
        QuecSmartHomeService.getFamilyList(null, 1, 50) {
            if (it.isSuccess) {
                itemList.clear()
                showList.clear()

                itemList.addAll(it.data.list)
                showList.addAll(it.data.list.map { item ->
                    val role = when (item.memberRole) {
                        1 -> "创建者"
                        2 -> "管理员"
                        else -> "成员"
                    }
                    CommonListAdapter.Item(item.familyName ?: "", item.fid, role)
                })

                adapter.notifyDataSetChanged()
            } else {
                handlerError(it)
            }
        }
    }

    private fun onItemClick(item: QuecFamilyItemModel) {
        startActivity(Intent(this, FamilyFunctionActivity::class.java).apply {
            putExtra(BaseFamilyActivity.CODE_FAMILY, item)
        })
    }

    private fun addFamily() {
        EditTextPopup(this).apply {
            setTitle("请输入家庭名称")
            setEditTextListener {
                dismiss()
                QuecSmartHomeService.addFamily(QuecFamilyParamModel(familyName = it)) { ret ->
                    if (ret.isSuccess) {
                        showMessage("创建成功")
                        getFamilyList()
                    } else {
                        handlerError(ret)
                    }
                }
            }
        }.showPopupWindow()
    }

    private fun queryInviteList() {
        QuecSmartHomeService.getFamilyInviteList(1, 10) {
            if (it.isSuccess) {
                if (it.data.list.isEmpty()) {
                    showMessage("没有待接收分享的家庭")
                } else {
                    SelectItemDialog(this).apply {
                        for (item in it.data.list) {
                            addItem(item.familyName ?: "") {
                                SelectItemDialog(this@FamilyListActivity).also { dialog ->
                                    dialog.addItem("接受") {
                                        setInviteStatus(item, true)
                                    }

                                    dialog.addItem("拒绝") {
                                        setInviteStatus(item, false)
                                    }
                                }.show()
                            }
                        }
                    }.show()
                }
            } else {
                handlerError(it)
            }
        }
    }

    private fun setInviteStatus(item: QuecInviteItemModel, isAccept: Boolean) {
        QuecSmartHomeService.familyMemberInviteHandle(
            item.fid,
            if (isAccept) 1 else 0
        ) {
            if (it.isSuccess) {
                showMessage("设置成功")
                getFamilyList()
            } else {
                handlerError(it)
            }
        }
    }
}