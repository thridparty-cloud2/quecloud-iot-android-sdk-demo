package com.quectel.app.demo.ui

import android.os.Bundle
import com.quectel.app.demo.base.activity.QuecBaseActivity
import com.quectel.app.demo.databinding.ActivityStartBinding
import com.quectel.app.demo.ui.account.AuthCodeLoginActivity
import com.quectel.app.demo.ui.account.LoginExActivity
import com.quectel.app.demo.ui.account.RegisterExActivity
import com.quectel.app.demo.ui.account.ResetPwdExActivity

class StartActivity : QuecBaseActivity<ActivityStartBinding>() {
    override fun getViewBinding(): ActivityStartBinding {
        return ActivityStartBinding.inflate(layoutInflater)
    }

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initData() {

    }

    override fun initTestItem() {
        addItem("切换数据中心") {

        }

        addItem("账号登录") {
            startTargetActivity(LoginExActivity::class.java)
        }

        addItem("账号登录: 三方AuthCode") {
            startTargetActivity(AuthCodeLoginActivity::class.java)
        }

        addItem("账号注册") {
            startTargetActivity(RegisterExActivity::class.java)
        }

        addItem("找回密码") {
            startTargetActivity(ResetPwdExActivity::class.java)
        }
    }

    private fun showStatus() {

    }
}