package com.quectel.app.demo.ui.account

import android.os.Bundle
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

    override fun initTestItem() {

    }

    private fun login(authCode: String) {
        if (authCode.isEmpty()) {
            showMessage("请输入authCode")
            return
        }
        QuecUserService.loginByAuthCode(authCode) {
            if (it.isSuccess) {
                showMessage("登录成功")
                startTargetActivity(HomeActivity::class.java)
                finish()
            } else {
                showMessage("登录失败: " + it.msg)
            }
        }
    }
}