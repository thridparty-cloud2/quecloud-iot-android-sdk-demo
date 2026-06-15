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

    private fun setMode(mode: Mode) {
        currentMode = mode
        when (mode) {
            Mode.PHONE -> {
                binding.etCountry.visibility = View.VISIBLE
                binding.etAccount.hint = getString(R.string.hint_phone)
            }

            Mode.EMAIL -> {
                binding.etCountry.visibility = View.INVISIBLE
                binding.etAccount.hint = getString(R.string.hint_email)
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
        if (pwd.isEmpty()) {
            showMessage(getString(R.string.please_input_password))
            return
        }
        QuecUserService.resetPasswordByPhone(phone, code, country, pwd) {
            if (it.isSuccess) {
                setSuccess()
            } else {
                showMessage(getString(R.string.reset_password_failed, it.msg))
            }
        }
    }

    private fun resetPwdWithEmail(email: String, code: String, pwd: String) {
        if (email.isEmpty()) {
            showMessage(getString(R.string.please_input_email))
            return
        }
        if (code.isEmpty()) {
            showMessage(getString(R.string.please_input_code))
            return
        }
        if (pwd.isEmpty()) {
            showMessage(getString(R.string.please_input_password))
            return
        }
        QuecUserService.resetPasswordByEmail(code, email, null, pwd) {
            if (it.isSuccess) {
                setSuccess()
            } else {
                showMessage(getString(R.string.reset_password_failed, it.msg))
            }
        }
    }

    private fun setSuccess() {
        showMessage(getString(R.string.reset_password_success))
        finish()
    }

    private enum class Mode {
        PHONE, EMAIL
    }
}