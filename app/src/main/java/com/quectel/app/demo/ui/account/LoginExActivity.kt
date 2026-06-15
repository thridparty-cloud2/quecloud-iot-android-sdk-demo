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
                binding.etAccount.hint = getString(R.string.hint_phone)
                binding.etPwd.hint = getString(R.string.hint_password)
                binding.btGetCode.visibility = View.INVISIBLE
            }

            Mode.EMAIL -> {
                binding.etAccount.hint = getString(R.string.hint_email)
                binding.etPwd.hint = getString(R.string.hint_password)
                binding.btGetCode.visibility = View.INVISIBLE
            }

            Mode.PHONE_CODE -> {
                binding.etAccount.hint = getString(R.string.hint_phone)
                binding.etPwd.hint = getString(R.string.hint_verification_code)
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
            showMessage(getString(R.string.please_input_country_code))
            return
        }
        if (phone.isEmpty()) {
            showMessage(getString(R.string.please_input_phone))
            return
        }
        if (pwd.isEmpty()) {
            showMessage(getString(R.string.please_input_password))
            return
        }
        showOrHideLoading(true)
        QuecUserService.loginByPhone(phone, pwd, country) {
            showOrHideLoading(false)
            if (it.isSuccess) {
                setLoginSuccess(country)
            } else {
                showMessage(getString(R.string.login_failed, it.msg))
            }
        }
    }

    private fun loginWithPhoneCode(country: String, phone: String, code: String) {
        if (country.isEmpty()) {
            showMessage(getString(R.string.please_input_country_code))
            return
        }
        if (phone.isEmpty()) {
            showMessage(getString(R.string.please_input_phone))
            return
        }
        if (code.isEmpty()) {
            showMessage(getString(R.string.please_input_code))
            return
        }
        showOrHideLoading(true)
        QuecUserService.loginWithMobile(phone, code, country) {
            showOrHideLoading(false)
            if (it.isSuccess) {
                setLoginSuccess(country)
            } else {
                showMessage(getString(R.string.login_failed, it.msg))
            }
        }
    }

    private fun loginWithEmailPwd(country: String, email: String, pwd: String) {
        if (country.isEmpty()) {
            showMessage(getString(R.string.please_input_country_code))
            return
        }
        if (email.isEmpty()) {
            showMessage(getString(R.string.please_input_email))
            return
        }
        if (pwd.isEmpty()) {
            showMessage(getString(R.string.please_input_password))
            return
        }
        showOrHideLoading(true)
        QuecUserService.loginByEmail(email, pwd) {
            showOrHideLoading(false)
            if (it.isSuccess) {
                setLoginSuccess(country)
            } else {
                showMessage(getString(R.string.login_failed, it.msg))
            }
        }
    }

    private fun setLoginSuccess(country: String?) {
        showMessage(getString(R.string.login_success))
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