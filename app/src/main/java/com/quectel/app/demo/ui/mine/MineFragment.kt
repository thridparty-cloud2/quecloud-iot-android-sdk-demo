package com.quectel.app.demo.ui.mine

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.quectel.app.demo.BuildConfig
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
import com.quectel.app.demo.utils.DensityUtils
import com.quectel.app.demo.utils.ToastUtils
import com.quectel.app.device.iot.IotChannelController
import com.quectel.app.smart_home_sdk.service.QuecSmartHomeService
import com.quectel.app.usersdk.bean.QuecUserModel
import com.quectel.app.usersdk.service.QuecUserService
import com.quectel.basic.common.utils.QuecFamilyUtil
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.concurrent.thread

class MineFragment(
    // 1 language   2 country   3 timezone
    private val lang: Int = 1,
    private val nationality: Int = 2,
    private val timezone: Int = 3,
) : QuecBaseFragment<MineLayoutBinding>() {

    lateinit var user: QuecUserModel

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): MineLayoutBinding {
        return MineLayoutBinding.inflate(inflater, container, false)
    }

    override fun initView(savedInstanceState: Bundle?) {
        val time = BuildConfig.BUILD_TIME.toLong()
        val timeInfo = getString(R.string.app_build_time) + SimpleDateFormat(
            "yyyy-MM-dd HH:mm",
            Locale.ENGLISH
        ).format(Date(time))
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
            tvBuildTime.text = timeInfo
            sFamily.setOnClickListener { changeFamilyMode() }
        }
    }

    private fun openLangCountryTimezone(type: Int) {
        val intent = Intent(activity, UpdataUserActivity::class.java)
        intent.putExtra("type", type)
        startActivity(intent)
    }

    private fun changeSex() {
        SelectItemDialog(requireContext()).apply {
            addItem(getString(R.string.male)) {
                updateSex(0)
                dismiss()
            }
            addItem(getString(R.string.female)) {
                updateSex(1)
                dismiss()
            }
            addItem(getString(R.string.secret)) {
                updateSex(2)
                dismiss()
            }
            addItem(getString(R.string.cancel)) {
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

        binding.sFamily.isChecked = QuecFamilyUtil.getFamilyMode()
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
            setTitle(getString(R.string.modify_address_title))
            setHint(getString(R.string.hint_modify_address))
            setEditTextListener { let ->
                if (let.isNullOrEmpty()) {
                    ToastUtils.showShort(context, getString(R.string.address_empty))
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
            setTitle(getString(R.string.modify_nickname_title))
            setHint(getString(R.string.hint_modify_nickname))
            setEditTextListener { let ->
                if (let.isNullOrEmpty()) {
                    ToastUtils.showShort(context, getString(R.string.nickname_empty))
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
            setTitle(getString(R.string.confirm_logout))
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
            setTitle(getString(R.string.confirm_delete_user))
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
            setTitle(getString(R.string.modify_password_title))
            setHint1(getString(R.string.hint_new_password))
            setHint2(getString(R.string.hint_old_password))
            setEditTextListener { newPass, oldPass ->
                if (newPass.isNullOrEmpty() || oldPass.isNullOrEmpty()) {
                    ToastUtils.showShort(context, getString(R.string.password_empty))
                    return@setEditTextListener
                }
                dismiss()
                startLoading()
                QuecUserService.updatePassword(newPass, oldPass) { result ->
                    finishLoading()
                    handlerResult(result)
                    if (result.isSuccess) {
                        ToastUtils.showShort(context, getString(R.string.password_modify_success))
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

    private fun changeFamilyMode() {
        showOrHideLoading(true)
        QuecSmartHomeService.enabledFamilyMode(!QuecFamilyUtil.getFamilyMode()) {
            showOrHideLoading(false)
            handlerResult(it)
            if (it.isSuccess) {
                AppVariable.setDeviceChange()
            }
        }
    }

    private fun startLoading() {
        showOrHideLoading(true)
    }

    private fun finishLoading() {
        showOrHideLoading(false)
    }
}