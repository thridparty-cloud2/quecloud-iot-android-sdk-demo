package com.quectel.app.demo.ui.mine

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.quectel.app.demo.R
import com.quectel.app.demo.base.fragment.QuecBaseFragment
import com.quectel.app.demo.common.AppVariable
import com.quectel.app.demo.databinding.MineLayoutBinding
import com.quectel.app.demo.dialog.EditDoubleTextPopup
import com.quectel.app.demo.dialog.EditTextPopup
import com.quectel.app.demo.dialog.SelectItemDialog
import com.quectel.app.demo.dialog.SurePopup
import com.quectel.app.demo.dialog.SurePopup.OnSureListener
import com.quectel.app.demo.ui.StartActivity
import com.quectel.app.demo.ui.UpdateUserPhoneActivity
import com.quectel.app.demo.ui.device.scene.DeviceSceneActivity
import com.quectel.app.demo.utils.DensityUtils
import com.quectel.app.demo.utils.MyUtils
import com.quectel.app.demo.utils.ToastUtils
import com.quectel.app.device.iot.IotChannelController
import com.quectel.app.usersdk.bean.QuecUserModel
import com.quectel.app.usersdk.service.QuecUserService
import kotlin.concurrent.thread

public class MineFragment(
    //1 语言   2 国家   3时区
    public val lang: Int = 1,
    public val nationality: Int = 2,
    public val timezone: Int = 3,
) : QuecBaseFragment<MineLayoutBinding>() {

    var mDialog: Dialog? = null
    lateinit var user: QuecUserModel

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): MineLayoutBinding {
        return MineLayoutBinding.inflate(inflater, container, false)
    }

    override fun initView(savedInstanceState: Bundle?) {
        binding.apply {
            llChangePhone.setOnClickListener { changePhone() }
            llDeleteUser.setOnClickListener { deleteUser() }
            llChangePassword.setOnClickListener { createChangePasswordDialog() }
            btLogout.setOnClickListener { logout() }
            llNickname.setOnClickListener { changeNickName() }
            llAddress.setOnClickListener { changeAddress() }
            civHead.setOnClickListener { changeHead() }
            llSex.setOnClickListener { changeSex() }
            llLan.setOnClickListener { openLangCountryTimezone(lang) }
            llCountry.setOnClickListener { openLangCountryTimezone(nationality) }
            llTimezone.setOnClickListener { openLangCountryTimezone(timezone) }
        }
    }

    private fun openLangCountryTimezone(type: Int) {
        var intent = Intent(activity, UpdataUserActivity::class.java)
        intent.putExtra("type", type)
        startActivity(intent)
    }

    private fun changeSex() {
        SelectItemDialog(requireContext()).apply {
            addItem("男") {
                updateSex(0)
                dismiss()
            }
            addItem("女") {
                updateSex(1)
                dismiss()
            }
            addItem("保密") {
                updateSex(2)
                dismiss()
            }
            addItem("取消") {
                dismiss()
            }
        }.show()
    }

    private fun updateSex(type: Int) {
        QuecUserService.updateUserSex(sex = type) { result ->
            handlerResult(result)
            if (result.isSuccess) {
                queryUserInfor()
            }
        }
    }

    private fun changeHead() {
        startActivity(Intent(activity, PhotoHeadActivity::class.java))
    }

    override fun initData() {
        queryUserInfor()
    }

    override fun onResume() {
        super.onResume()
        if (AppVariable.isMineInfoChange) {
            queryUserInfor()
        }
    }

    private fun queryUserInfor() {
        startLoading()
        QuecUserService.getUserInfo { result ->
            finishLoading()
            if (result.isSuccess) {
                AppVariable.isMineInfoChange = false
                user = result.data
                if (!result.data.nikeName.isNullOrEmpty()) {
                    binding.tvNickname.text = result.data.nikeName
                }
                if (!result.data.phone.isNullOrEmpty()) {
                    binding.tvPhone.text = result.data.phone
                }
                if (!result.data.address.isNullOrEmpty()) {
                    binding.tvAddress.text = result.data.address
                }
                if (!result.data.sex.isNullOrEmpty()) {
                    binding.tvSex.text = result.data.sex
                }
                if (!result.data.lang.isNullOrEmpty()) {
                    binding.tvLan.text = result.data.lang
                }
                if (!result.data.nationality.isNullOrEmpty()) {
                    binding.tvCountry.text = result.data.nationality
                }
                if (!result.data.timezone.isNullOrEmpty()) {
                    binding.tvTimezone.text = result.data.timezone
                }
                if (!result.data.headimg.isNullOrEmpty()) {
                    val widthPic = DensityUtils.dp2px(context, 50f)
                    Glide.with(requireContext())
                        .load(result.data.headimg)
                        .placeholder(R.mipmap.user_head)
                        .error(R.mipmap.user_head)
                        .override(widthPic, widthPic)
                        .centerCrop()
                        .into(binding.civHead)
                }
            } else {
                handlerResult(result)
            }
        }
    }

    private fun changeAddress() {
        EditTextPopup(context).apply {
            setTitle("修改地址")
            setHint("请输入修改的地址")
            setEditTextListener { let ->
                if (let.isNullOrEmpty()) {
                    ToastUtils.showShort(context, "地址不能为空")
                    return@setEditTextListener
                }
                dismiss()
                startLoading()
                QuecUserService.updateUserAddress(address = let) { result ->
                    handlerResult(result)
                    finishLoading()
                    if (result.isSuccess) {
                        queryUserInfor()
                    }
                }
            }
        }.showPopupWindow()
    }

    private fun changeNickName() {
        EditTextPopup(context).apply {
            setTitle("修改昵称")
            setHint("请输入修改的昵称")
            setEditTextListener { let ->
                if (let.isNullOrEmpty()) {
                    ToastUtils.showShort(context, "昵称不能为空")
                    return@setEditTextListener
                }
                dismiss()
                startLoading()
                QuecUserService.updateUserNickName(nikeName = let) { result ->
                    handlerResult(result)
                    finishLoading()
                    if (result.isSuccess) {
                        queryUserInfor()
                    }
                }
            }
        }.showPopupWindow()
    }

    private fun logout() {
        SurePopup(requireContext()).apply {
            setTitle("确认退出登录?")
            setSureListener(object : OnSureListener {
                override fun sure() {
                    startLoading()
                    QuecUserService.logout { result ->
                        finishLoading()
                        if (result.isSuccess) {
                            thread(start = true) {
                                IotChannelController.getInstance().closeChannelAll()
                            }
                            val intent =
                                Intent(context, StartActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            startActivity(intent)
                        }
                    }
                }
            })
        }.showPopupWindow()
    }

    private fun deleteUser() {
        SurePopup(requireContext()).apply {
            setTitle("确认删除用户?")
            setSureListener(object : OnSureListener {
                override fun sure() {
                    dismiss()
                    startLoading()
                    QuecUserService.deleteUser(1) { result ->
                        finishLoading()
                        handlerResult(result)
                        if (result.isSuccess) {
                            activity?.finish()
                        }
                    }
                }
            })
        }.showPopupWindow()

    }

    private fun createChangePasswordDialog() {
        EditDoubleTextPopup(context).apply {
            setTitle("修改密码")
            setHint1("请输入新密码")
            setHint2("请输入旧密码")
            setEditTextListener { newPass, oldPass ->
                if (newPass.isNullOrEmpty() || oldPass.isNullOrEmpty()) {
                    ToastUtils.showShort(context, "密码不能为空")
                    return@setEditTextListener
                }
                dismiss()
                startLoading()
                QuecUserService.updatePassword(newPass, oldPass) { result ->
                    finishLoading()
                    handlerResult(result)
                    if (result.isSuccess) {
                        ToastUtils.showShort(context, "密码修改成功")
                    }
                }
            }
        }.showPopupWindow()
    }

    private fun changePhone() {
        if (!user.phone.isNullOrEmpty()) {
            startActivity(Intent(activity, UpdateUserPhoneActivity::class.java))
        }
    }

    fun startLoading() {
        if (mDialog == null) {
            mDialog = MyUtils.createDialog(context)
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