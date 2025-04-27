package com.quectel.app.demo.ui.account

import android.os.Bundle
import android.view.View
import com.quectel.app.demo.R
import com.quectel.app.demo.SdkManager
import com.quectel.app.demo.base.activity.QuecBaseActivity
import com.quectel.app.demo.common.AuthCodeManager
import com.quectel.app.demo.databinding.ActivityLoginExBinding
import com.quectel.app.demo.ui.HomeActivity
import com.quectel.app.usersdk.constant.UserConstant
import com.quectel.app.usersdk.service.QuecUserService
import com.quectel.sdk.iot.service.QuecIotSdk

class LoginExActivity : QuecBaseActivity<ActivityLoginExBinding>() {
    private var currentMode = Mode.PHONE

    override fun getViewBinding(): ActivityLoginExBinding {
        return ActivityLoginExBinding.inflate(layoutInflater)
    }

    override fun initView(savedInstanceState: Bundle?) {
        binding.apply {
            rbPhone.setOnClickListener { setMode(Mode.PHONE) }
            rbPhoneCode.setOnClickListener { setMode(Mode.PHONE_CODE) }
            rbEmail.setOnClickListener { setMode(Mode.EMAIL) }

            btGetCode.setOnClickListener {
                AuthCodeManager.getSmsCode(
                    UserConstant.TYPE_SMS_CODE_LOGIN,
                    binding.etCountry.text.toString(),
                    binding.etAccount.text.toString()
                )
            }

            btConfirm.setOnClickListener {
                when (currentMode) {
                    Mode.PHONE -> {
                        loginWithPhonePwd(
                            binding.etCountry.text.toString(),
                            binding.etAccount.text.toString(),
                            binding.etPwd.text.toString()
                        )
                    }

                    Mode.PHONE_CODE -> {
                        loginWithPhoneCode(
                            binding.etCountry.text.toString(),
                            binding.etAccount.text.toString(),
                            binding.etPwd.text.toString()
                        )
                    }

                    Mode.EMAIL -> {
                        loginWithEmailPwd(
                            binding.etCountry.text.toString(),
                            binding.etAccount.text.toString(),
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

    private fun setMode(mode: Mode) {
        currentMode = mode
        when (mode) {
            Mode.PHONE -> {
                binding.etAccount.hint = "请输入手机号"
                binding.etPwd.hint = "请输入密码"
                binding.btGetCode.visibility = View.INVISIBLE
            }

            Mode.EMAIL -> {
                binding.etAccount.hint = "请输入邮箱"
                binding.etPwd.hint = "请输入密码"
                binding.btGetCode.visibility = View.INVISIBLE
            }

            Mode.PHONE_CODE -> {
                binding.etAccount.hint = "请输入手机号"
                binding.etPwd.hint = "请输入验证码"
                binding.btGetCode.visibility = View.VISIBLE

            }
        }
        binding.rgMode.check(
            when (mode) {
                Mode.PHONE -> R.id.rb_phone
                Mode.PHONE_CODE -> R.id.rb_phone_code
                Mode.EMAIL -> R.id.rb_email
            }
        )
    }

    private fun loginWithPhonePwd(country: String, phone: String, pwd: String) {
        if (country.isEmpty()) {
            showMessage("请输入国家区号")
            return
        }
        if (phone.isEmpty()) {
            showMessage("请输入手机号")
            return
        }
        if (pwd.isEmpty()) {
            showMessage("请输入密码")
            return
        }
        QuecUserService.loginByPhone(phone, pwd, country) {
            if (it.isSuccess) {
                setLoginSuccess(country)
            } else {
                showMessage("登录失败: ${it.msg}")
            }
        }
    }

    private fun loginWithPhoneCode(country: String, phone: String, code: String) {
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
        QuecUserService.loginWithMobile(phone, code, country) {
            if (it.isSuccess) {
                setLoginSuccess(country)
            } else {
                showMessage("登录失败: ${it.msg}")
            }
        }
    }

    private fun loginWithEmailPwd(country: String, email: String, pwd: String) {
        if (country.isEmpty()) {
            showMessage("请输入国家区号")
            return
        }
        if (email.isEmpty()) {
            showMessage("请输入邮箱")
            return
        }
        if (pwd.isEmpty()) {
            showMessage("请输入密码")
            return
        }
        QuecUserService.loginByEmail(email, pwd) {
            if (it.isSuccess) {
                setLoginSuccess(country)
            } else {
                showMessage("登录失败: ${it.msg}")
            }
        }
    }

    private fun setLoginSuccess(country: String?) {
        showMessage("登录成功")
        if (!SdkManager.isCustomService(application) && country != null
        ) {
            QuecIotSdk.setCountryCode(country)
        }
        startTargetActivity(HomeActivity::class.java)
        finish()
    }

    private enum class Mode {
        PHONE, PHONE_CODE, EMAIL
    }
}