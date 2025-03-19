package com.quectel.app.demo.ui.account

import android.os.Bundle
import android.view.View
import com.quectel.app.demo.R
import com.quectel.app.demo.base.activity.QuecBaseActivity
import com.quectel.app.demo.common.AuthCodeManager
import com.quectel.app.demo.constant.CloudConfig
import com.quectel.app.demo.databinding.ActivityLoginExBinding
import com.quectel.app.demo.ui.HomeActivity
import com.quectel.app.demo.utils.SPUtils
import com.quectel.app.quecnetwork.httpservice.IResponseCallBack
import com.quectel.app.usersdk.constant.UserConstant
import com.quectel.app.usersdk.userservice.IUserService
import com.quectel.app.usersdk.utils.UserServiceFactory
import com.quectel.sdk.iot.QuecIotAppSdk

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

    override fun initTestItem() {

    }

    private fun setMode(mode: Mode) {
        currentMode = mode
        when (mode) {
            Mode.PHONE -> {
                binding.etCountry.visibility = View.VISIBLE
                binding.etAccount.hint = "请输入手机号"
                binding.etPwd.hint = "请输入密码"
                binding.btGetCode.visibility = View.INVISIBLE
            }

            Mode.EMAIL -> {
                binding.etCountry.visibility = View.INVISIBLE
                binding.etAccount.hint = "请输入邮箱"
                binding.etPwd.hint = "请输入密码"
                binding.btGetCode.visibility = View.INVISIBLE
            }

            Mode.PHONE_CODE -> {
                binding.etCountry.visibility = View.VISIBLE
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
        UserServiceFactory.getInstance().getService(IUserService::class.java)
            .phonePwdLogin(
                phone, pwd, country.replace("+", ""), object : IResponseCallBack {
                    override fun onSuccess() {
                        setLoginSuccess(country)
                    }

                    override fun onFail(e: Throwable) {
                        showMessage(e.toString())
                    }

                    override fun onError(code: Int, errorMsg: String) {
                        showMessage("$[$code] $errorMsg")
                    }
                }
            )
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
        UserServiceFactory.getInstance().getService(IUserService::class.java)
            .phoneSmsCodeLogin(
                phone, code, country.replace("+", ""), object : IResponseCallBack {
                    override fun onSuccess() {
                        setLoginSuccess(country)
                    }

                    override fun onFail(e: Throwable) {
                        showMessage(e.toString())
                    }

                    override fun onError(code: Int, errorMsg: String?) {
                        showMessage("$[$code] $errorMsg")
                    }
                }
            )
    }

    private fun loginWithEmailPwd(email: String, pwd: String) {
        if (email.isEmpty()) {
            showMessage("请输入邮箱")
            return
        }
        if (pwd.isEmpty()) {
            showMessage("请输入密码")
            return
        }
        UserServiceFactory.getInstance().getService(IUserService::class.java)
            .emailPwdLogin(
                email, pwd, object : IResponseCallBack {
                    override fun onSuccess() {
                        setLoginSuccess(null)
                    }

                    override fun onFail(e: Throwable) {
                        showMessage(e.toString())
                    }

                    override fun onError(code: Int, errorMsg: String?) {}
                }
            )
    }

    private fun setLoginSuccess(country: String?) {
        showMessage("登录成功")
        if (!SPUtils.getBoolean(
                mContext,
                CloudConfig.IS_CUSTOM_CLOUD,
                false
            ) && country != null
        ) {
            QuecIotAppSdk.getInstance().setCountryCode(country)
        }
        startTargetActivity(HomeActivity::class.java)
        finish()
    }

    private enum class Mode {
        PHONE, PHONE_CODE, EMAIL
    }
}