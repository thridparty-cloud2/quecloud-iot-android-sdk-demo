package com.quectel.app.demo.ui.account

import android.os.Bundle
import com.quectel.app.demo.R
import com.quectel.app.demo.base.activity.QuecBaseActivity
import com.quectel.app.demo.databinding.ActivityAuthCodeLoginBinding
import com.quectel.app.demo.ui.HomeActivity
import com.quectel.app.usersdk.service.QuecUserService

class AuthCodeLoginActivity : QuecBaseActivity<ActivityAuthCodeLoginBinding>() {
    override fun getViewBinding(): ActivityAuthCodeLoginBinding {
        return ActivityAuthCodeLoginBinding.inflate(layoutInflater)
    }

    override fun initView(savedInstanceState: Bundle?) {
        binding.btLogin.setOnClickListener {
            login(binding.etCode.text.toString())
        }
    }

    override fun initData() {

    }

    private fun login(authCode: String) {
        if (authCode.isEmpty()) {
            showMessage(getString(R.string.please_input_auth_code))
            return
        }
        QuecUserService.loginByAuthCode(authCode) {
            if (it.isSuccess) {
                showMessage(getString(R.string.login_success))
                startTargetActivity(HomeActivity::class.java)
                finish()
            } else {
                showMessage(getString(R.string.login_failed, it.msg))
            }
        }
    }
}