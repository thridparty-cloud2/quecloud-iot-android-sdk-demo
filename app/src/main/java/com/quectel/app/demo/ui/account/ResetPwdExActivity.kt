package com.quectel.app.demo.ui.account

import android.os.Bundle
import android.view.View
import com.quectel.app.demo.R
import com.quectel.app.demo.base.activity.QuecBaseActivity
import com.quectel.app.demo.common.AuthCodeManager
import com.quectel.app.demo.databinding.ActivityResetPwdExBinding
import com.quectel.app.usersdk.constant.UserConstant
import com.quectel.app.usersdk.service.QuecUserService

class ResetPwdExActivity : QuecBaseActivity<ActivityResetPwdExBinding>() {
    private var currentMode = Mode.PHONE

    override fun getViewBinding(): ActivityResetPwdExBinding {
        return ActivityResetPwdExBinding.inflate(layoutInflater)
    }

    override fun initView(savedInstanceState: Bundle?) {
        binding.apply {
            rbPhone.setOnClickListener { setMode(Mode.PHONE) }
            rbEmail.setOnClickListener { setMode(Mode.EMAIL) }

            btGetCode.setOnClickListener {
                val ret = when (currentMode) {
                    Mode.PHONE -> {
                        AuthCodeManager.getSmsCode(
                            UserConstant.TYPE_SMS_CODE_REGISTER,
                            binding.etCountry.text.toString(),
                            binding.etAccount.text.toString()
                        )
                    }

                    Mode.EMAIL -> {
                        AuthCodeManager.getEmailCode(
                            UserConstant.TYPE_SEND_EMAIL_REGISTER,
                            binding.etAccount.text.toString()
                        )
                    }
                }

                if (ret) {
                    etCode.visibility = View.VISIBLE
                    etPwd.visibility = View.VISIBLE
                    btConfirm.visibility = View.VISIBLE
                }
            }

            btConfirm.setOnClickListener {
                when (currentMode) {
                    Mode.PHONE -> {
                        resetPwdWithPhone(
                            binding.etCountry.text.toString(),
                            binding.etAccount.text.toString(),
                            binding.etCode.text.toString(),
                            binding.etPwd.text.toString()
                        )
                    }

                    Mode.EMAIL -> {
                        resetPwdWithEmail(
                            binding.etAccount.text.toString(),
                            binding.etCode.text.toString(),
                            binding.etPwd.text.toString()
                        )
                    }
                }
            }
        }
    }

    override fun initData() {
        setMode(Mode.PHONE)
    }

    override fun initTestItem() {

    }

    private fun setMode(mode: Mode) {
        currentMode = mode
        when (mode) {
            Mode.PHONE -> {
                binding.etCountry.visibility = View.VISIBLE
                binding.etAccount.hint = "请输入手机号"
            }

            Mode.EMAIL -> {
                binding.etCountry.visibility = View.INVISIBLE
                binding.etAccount.hint = "请输入邮箱"
            }
        }
        binding.rgMode.check(
            when (mode) {
                Mode.PHONE -> R.id.rb_phone
                Mode.EMAIL -> R.id.rb_email
            }
        )
    }

    private fun resetPwdWithPhone(country: String, phone: String, code: String, pwd: String) {
        if (country.isEmpty()) {
            showMessage("请输入国家区号")
            return
        }
        if (phone.isEmpty()) {
            showMessage("请输入手机号")
            return
        }
        if (code.isEmpty()) {
            showMessage("请输入验证码")
            return
        }
        if (pwd.isEmpty()) {
            showMessage("请输入密码")
            return
        }
        QuecUserService.resetPasswordByPhone(phone, code, country, pwd) {
            if (it.isSuccess) {
                setSuccess()
            } else {
                showMessage("重置密码失败: ${it.msg}")
            }
        }
    }

    private fun resetPwdWithEmail(email: String, code: String, pwd: String) {
        if (email.isEmpty()) {
            showMessage("请输入邮箱")
            return
        }
        if (code.isEmpty()) {
            showMessage("请输入验证码")
            return
        }
        if (pwd.isEmpty()) {
            showMessage("请输入密码")
            return
        }
        QuecUserService.resetPasswordByEmail(code, email, null, pwd) {
            if (it.isSuccess) {
                setSuccess()
            } else {
                showMessage("重置密码失败: ${it.msg}")
            }
        }
    }

    private fun setSuccess() {
        showMessage("重置密码成功")
        finish()
    }

    private enum class Mode {
        PHONE, EMAIL
    }
}