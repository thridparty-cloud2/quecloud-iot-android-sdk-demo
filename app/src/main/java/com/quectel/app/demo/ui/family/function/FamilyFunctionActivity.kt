package com.quectel.app.demo.ui.family.function

import android.content.Intent
import android.os.Bundle
import com.quectel.app.demo.databinding.ActivityCommonListBinding
import com.quectel.app.demo.dialog.CommonDialog
import com.quectel.app.demo.dialog.EditDoubleTextPopup
import com.quectel.app.demo.dialog.EditTextPopup
import com.quectel.app.demo.dialog.SelectItemDialog
import com.quectel.app.demo.ui.family.BaseFamilyActivity
import com.quectel.app.demo.ui.family.group.list.FamilyGroupListActivity
import com.quectel.app.demo.ui.family.room.device.FamilyDeviceListActivity
import com.quectel.app.demo.ui.family.room.function.FamilyRoomFunctionActivity
import com.quectel.app.smart_home_sdk.bean.QuecFamilyMemberItemModel
import com.quectel.app.smart_home_sdk.bean.QuecFamilyParamModel
import com.quectel.app.smart_home_sdk.bean.QuecInviteFamilyMemberParamModel
import com.quectel.app.smart_home_sdk.service.QuecSmartHomeService
import com.quectel.app.demo.R

class FamilyFunctionActivity : BaseFamilyActivity<ActivityCommonListBinding>() {
    override fun getViewBinding(): ActivityCommonListBinding {
        return ActivityCommonListBinding.inflate(layoutInflater)
    }

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initData() {
        binding.title.text = family.familyName
    }

    override fun initTestItem() {
        super.initTestItem()
        addItem(getString(R.string.view_common_devices)) { queryCommonDevice() }
        addItem(getString(R.string.view_all_devices)) { queryAllDevice() }

        if (family.memberRole == 1 || family.memberRole == 2) {
            addItem(getString(R.string.room_management)) { manageRoom() }
        }

        addItem(getString(R.string.modify_family_name)) { modifyFamilyName() }

        if (family.memberRole != 1) {
            addItem(getString(R.string.leave_family)) { levelFamily() }
            return
        }

        addItem(getString(R.string.remove_family)) { deleteFamily() }
        addItem(getString(R.string.invite_new_member)) { shareFamily() }
        addItem(getString(R.string.member_management)) { manageMember() }
        addItem(getString(R.string.add_new_room)) { addRoom() }
        addItem(getString(R.string.group_management)) { managerGroup() }
    }

    private fun modifyFamilyName() {
        EditTextPopup(this).apply {
            setTitle(getString(R.string.hint_family_name))
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
            setTitle(getString(R.string.confirm_delete_family))
            setYesOnclickListener(getString(R.string.confirm)) {
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
            setTitle(getString(R.string.confirm_leave_family))
            setYesOnclickListener(getString(R.string.confirm)) {
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
            setTitle(getString(R.string.invite_new_member))
            setHint1(getString(R.string.hint_member_account))
            setHint2(getString(R.string.hint_member_name))
            setEditTextListener { account, name ->
                dismiss()
                val phone = if (account.contains("@")) null else account
                val email = if (account.contains("@")) account else null
                QuecSmartHomeService.inviteFamilyMember(
                    QuecInviteFamilyMemberParamModel(
                        getCurrentFid(), "2", name,
                        System.currentTimeMillis() + 60 * 60 * 1000,
                        phone, email, null
                    )
                ) { handlerResult(it) }
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
                showMessage(getString(R.string.no_family_member))
                return@getFamilyMemberList
            }

            SelectItemDialog(this).apply {
                it.data.list.forEach { item ->
                    val role = when (item.memberRole) {
                        1 -> getString(R.string.role_creator)
                        2 -> getString(R.string.role_admin)
                        3 -> getString(R.string.role_member)
                        else -> getString(R.string.role_unknown)
                    }
                    addItem("${item.memberName} : $role") {
                        SelectItemDialog(this@FamilyFunctionActivity).also { dialog ->
                            dialog.addItem(getString(R.string.show_member_info)) { showMemberInfo(item) }
                            dialog.addItem(getString(R.string.modify_role)) { modifyMemberRole(item) }
                            dialog.addItem(getString(R.string.remove_member)) { removeMember(item) }
                            dialog.addItem(getString(R.string.modify_name_label)) { modifyMemberName(item) }
                        }.show()
                    }
                }
            }.show()
        }
    }

    private fun showMemberInfo(member: QuecFamilyMemberItemModel) {
        CommonDialog.showSimpleInfo(this, getString(R.string.member_info), member.toString())
    }

    private fun modifyMemberRole(member: QuecFamilyMemberItemModel) {
        SelectItemDialog(this).apply {
            addItem(getString(R.string.role_admin)) { setMemberRole(member, "2") }
            addItem(getString(R.string.role_member)) { setMemberRole(member, "3") }
        }.show()
    }

    private fun removeMember(member: QuecFamilyMemberItemModel) {
        QuecSmartHomeService.deleteFamilyMember(getCurrentFid(), member.uid ?: "") {
            handlerResult(it)
        }
    }

    private fun modifyMemberName(member: QuecFamilyMemberItemModel) {
        EditTextPopup(this).apply {
            setTitle(getString(R.string.modify_member_name))
            setContent(member.memberName)
            setEditTextListener {
                dismiss()
                QuecSmartHomeService.setFamilyMemberName(
                    getCurrentFid(), member.uid ?: "", it
                ) { ret -> handlerResult(ret) }
            }
        }.showPopupWindow()
    }

    private fun setMemberRole(member: QuecFamilyMemberItemModel, role: String) {
        QuecSmartHomeService.setFamilyMemberRole(getCurrentFid(), member.uid ?: "", role) {
            handlerResult(it)
        }
    }

    private fun addRoom() {
        EditTextPopup(this).apply {
            setTitle(getString(R.string.hint_room_name))
            setEditTextListener {
                dismiss()
                QuecSmartHomeService.addFamilyRoom(getCurrentFid(), it) { ret ->
                    handlerResult(ret)
                }
            }
        }.showPopupWindow()
    }

    private fun manageRoom() {
        QuecSmartHomeService.getFamilyRoomList(getCurrentFid(), 1, 20) {
            if (!it.isSuccess) {
                handlerError(it)
                return@getFamilyRoomList
            }
            if (it.data.list.isEmpty()) {
                showMessage(getString(R.string.no_room))
                return@getFamilyRoomList
            }

            SelectItemDialog(this).apply {
                it.data.list.forEach { item ->
                    addItem("[${item.roomSort}] ${item.roomName}") {
                        startActivity(
                            Intent(
                                this@FamilyFunctionActivity,
                                FamilyRoomFunctionActivity::class.java
                            ).apply {
                                putExtra(FamilyRoomFunctionActivity.CODE_FRID, item.frid)
                                putExtra(FamilyRoomFunctionActivity.CODE_NAME, item.roomName)
                            })
                    }
                }
            }.show()
        }
    }

    private fun queryCommonDevice() {
        startActivity(Intent(this, FamilyDeviceListActivity::class.java).apply {
            putExtra(FamilyDeviceListActivity.CODE_MODE, FamilyDeviceListActivity.Mode.COMMON)
        })
    }

    private fun queryAllDevice() {
        startActivity(Intent(this, FamilyDeviceListActivity::class.java).apply {
            putExtra(FamilyDeviceListActivity.CODE_MODE, FamilyDeviceListActivity.Mode.ALL)
        })
    }

    private fun managerGroup() {
        startActivity(Intent(this, FamilyGroupListActivity::class.java))
    }
}