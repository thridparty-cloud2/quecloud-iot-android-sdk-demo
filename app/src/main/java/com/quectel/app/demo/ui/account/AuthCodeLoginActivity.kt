package com.quectel.app.demo.ui.account

import android.os.Bundle
import com.quectel.app.demo.base.activity.QuecBaseActivity
import com.quectel.app.demo.databinding.ActivityAuthCodeLoginBinding
import com.quectel.app.demo.ui.HomeActivity
import com.quectel.app.usersdk.QuecResultCallback
import com.quectel.app.usersdk.userservice.IUserService
import com.quectel.app.usersdk.utils.UserServiceFactory
import com.quectel.basic.common.entity.QuecResult

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

        UserServiceFactory.getInstance().getService(IUserService::class.java)
            .loginByAuthCode(
                authCode,
                object : QuecResultCallback<QuecResult<String?>> {
                    override fun onSuccess(successResult: QuecResult<String?>) {
                        showMessage("登录成功")
                        startTargetActivity(HomeActivity::class.java)
                        finish()
                    }

                    override fun onFail(failResult: QuecResult<String?>) {
                        if (null == failResult.msg) {
                            showMessage("loginAuth fail: " + failResult.data.toString())
                        } else {
                            showMessage("loginAuth fail: " + failResult.msg)
                        }
                    }
                })
    }
}