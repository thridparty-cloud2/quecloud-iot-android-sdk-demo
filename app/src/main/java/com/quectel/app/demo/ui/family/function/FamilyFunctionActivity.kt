package com.quectel.app.demo.ui.family.function

import android.os.Bundle
import com.quectel.app.demo.databinding.ActivityFamilyFunctionBinding
import com.quectel.app.demo.dialog.CommonDialog
import com.quectel.app.demo.dialog.EditDoubleTextPopup
import com.quectel.app.demo.dialog.EditTextPopup
import com.quectel.app.demo.dialog.SelectItemDialog
import com.quectel.app.demo.ui.family.BaseFamilyActivity
import com.quectel.app.smart_home_sdk.bean.QuecFamilyMemberItemModel
import com.quectel.app.smart_home_sdk.bean.QuecFamilyParamModel
import com.quectel.app.smart_home_sdk.bean.QuecInviteFamilyMemberParamModel
import com.quectel.app.smart_home_sdk.service.QuecSmartHomeService

class FamilyFunctionActivity : BaseFamilyActivity<ActivityFamilyFunctionBinding>() {
    override fun getViewBinding(): ActivityFamilyFunctionBinding {
        return ActivityFamilyFunctionBinding.inflate(layoutInflater)
    }

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initData() {
        binding.title.text = family.familyName
    }

    override fun initTestItem() {
        super.initTestItem()
        addItem("房间管理") {

        }

        addItem("修改家庭名称") { modifyFamilyName() }

        if (family.memberRole != 1) {
            addItem("离开家庭") { levelFamily() }
            return
        }

        addItem("移除家庭") { deleteFamily() }

        addItem("邀请新成员") { shareFamily() }

        addItem("成员管理") { manageMember() }
    }

    private fun modifyFamilyName() {
        EditTextPopup(this).apply {
            setTitle("请输入家庭名称")
            setContent(family.familyName)
            setEditTextListener {
                dismiss()
                QuecSmartHomeService.setFamily(
                    QuecFamilyParamModel(
                        family.fid, it, family.familyLocation,
                        family.familyCoordinates, null
                    )
                ) { ret ->
                    if (ret.isSuccess) {
                        family.familyName = it
                        binding.title.text = it
                    }
                    handlerResult(ret)
                }
            }
        }.showPopupWindow()
    }

    private fun deleteFamily() {
        CommonDialog(this).apply {
            setTitle("确认删除家庭？")
            setYesOnclickListener("确认") {
                dismiss()
                QuecSmartHomeService.deleteFamily(getCurrentFid()) {
                    handlerResult(it)
                    if (it.isSuccess) {
                        finish()
                    }
                }
            }
        }.show()
    }

    private fun levelFamily() {
        CommonDialog(this).apply {
            setTitle("确认离开家庭？")
            setYesOnclickListener("确认") {
                QuecSmartHomeService.leaveFamily(getCurrentFid()) {
                    handlerResult(it)
                    if (it.isSuccess) {
                        finish()
                    }
                }
            }
        }.show()
    }

    private fun shareFamily() {
        EditDoubleTextPopup(this).apply {
            setTitle("邀请新成员")
            setHint1("请输入对方的账号")
            setHint2("请输入对方的名称")
            setEditTextListener { account, name ->
                dismiss()
                val phone = if (account.contains("@")) null else account
                val email = if (account.contains("@")) account else null
                QuecSmartHomeService.inviteFamilyMember(
                    QuecInviteFamilyMemberParamModel(
                        getCurrentFid(),
                        "2",
                        name,
                        System.currentTimeMillis() + 60 * 60 * 1000,
                        phone,
                        email,
                        null
                    )
                ) {
                    handlerResult(it)
                }
            }
        }.showPopupWindow()
    }

    private fun manageMember() {
        QuecSmartHomeService.getFamilyMemberList(getCurrentFid(), 1, 10) {
            if (!it.isSuccess) {
                handlerError(it)
                return@getFamilyMemberList
            }

            if (it.data.list.isEmpty()) {
                showMessage("无家庭成员")
                return@getFamilyMemberList
            }

            SelectItemDialog(this).apply {
                it.data.list.forEach { item ->
                    val role = when (item.memberRole) {
                        1 -> "创建者"
                        2 -> "管理员"
                        3 -> "成员"
                        else -> "未知"
                    }
                    addItem("${item.memberName} : $role") {
                        SelectItemDialog(this@FamilyFunctionActivity).also { dialog ->
                            dialog.addItem("显示成员信息") { showMemberInfo(item) }
                            dialog.addItem("修改权限") { modifyMemberRole(item) }
                            dialog.addItem("移除成员") { removeMember(item) }
                            dialog.addItem("修改名称") { modifyMemberName(item) }
                        }.show()
                    }
                }
            }.show()
        }
    }

    private fun showMemberInfo(member: QuecFamilyMemberItemModel) {
        CommonDialog.showSimpleInfo(this, "成员信息", member.toString())
    }

    private fun modifyMemberRole(member: QuecFamilyMemberItemModel) {
        SelectItemDialog(this).apply {
            addItem("管理员") { setMemberRole(member, "2") }
            addItem("成员") { setMemberRole(member, "3") }
        }.show()
    }

    private fun removeMember(member: QuecFamilyMemberItemModel) {
        QuecSmartHomeService.deleteFamilyMember(getCurrentFid(), member.uid ?: "") {
            handlerResult(it)
        }
    }

    private fun modifyMemberName(member: QuecFamilyMemberItemModel) {
        EditTextPopup(this).apply {
            setTitle("修改成员名称")
            setContent(member.memberName)
            setEditTextListener {
                dismiss()
                QuecSmartHomeService.setFamilyMemberName(
                    getCurrentFid(),
                    member.uid ?: "",
                    it
                ) { ret ->
                    handlerResult(ret)
                }
            }
        }.showPopupWindow()
    }

    private fun setMemberRole(member: QuecFamilyMemberItemModel, role: String) {
        QuecSmartHomeService.setFamilyMemberRole(getCurrentFid(), member.uid ?: "", role) {
            handlerResult(it)
        }
    }
}