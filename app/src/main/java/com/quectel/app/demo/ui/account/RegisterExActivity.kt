package com.quectel.app.demo.ui.account

import android.os.Bundle
import android.view.View
import com.quectel.app.demo.R
import com.quectel.app.demo.base.activity.QuecBaseActivity
import com.quectel.app.demo.common.AuthCodeManager
import com.quectel.app.demo.databinding.ActivityRegisterExBinding
import com.quectel.app.usersdk.constant.UserConstant
import com.quectel.app.usersdk.service.QuecUserService

class RegisterExActivity : QuecBaseActivity<ActivityRegisterExBinding>() {
    private var currentMode = Mode.PHONE

    override fun getViewBinding(): ActivityRegisterExBinding {
        return ActivityRegisterExBinding.inflate(layoutInflater)
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
                        registerWithPhone(
                            binding.etCountry.text.toString(),
                            binding.etAccount.text.toString(),
                            binding.etCode.text.toString(),
                            binding.etPwd.text.toString()
                        )
                    }

                    Mode.EMAIL -> {
                        registerWithEmail(
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

    private fun registerWithPhone(country: String, phone: String, code: String, pwd: String) {
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
        QuecUserService.registerByPhone(phone, code, pwd, country) {
            if (it.isSuccess) {
                setSuccess()
            } else {
                showMessage("注册失败: ${it.msg}")
            }
        }
    }

    private fun registerWithEmail(email: String, code: String, pwd: String) {
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
        QuecUserService.registerByEmail(email, code, pwd) {
            if (it.isSuccess) {
                setSuccess()
            } else {
                showMessage("注册失败: ${it.msg}")
            }
        }
    }

    private fun setSuccess() {
        showMessage("注册成功")
        startTargetActivity(LoginExActivity::class.java)
        finish()
    }

    private enum class Mode {
        PHONE, EMAIL
    }
}