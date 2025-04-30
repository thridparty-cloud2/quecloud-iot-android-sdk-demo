package com.quectel.app.demo.ui

import android.annotation.SuppressLint
import android.os.Bundle
import com.quectel.app.demo.BuildConfig
import com.quectel.app.demo.R
import com.quectel.app.demo.SdkManager
import com.quectel.app.demo.base.activity.QuecBaseActivity
import com.quectel.app.demo.databinding.ActivityStartBinding
import com.quectel.app.demo.dialog.SelectItemDialog
import com.quectel.app.demo.ui.account.AuthCodeLoginActivity
import com.quectel.app.demo.ui.account.LoginExActivity
import com.quectel.app.demo.ui.account.RegisterExActivity
import com.quectel.app.demo.ui.account.ResetPwdExActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class StartActivity : QuecBaseActivity<ActivityStartBinding>() {
    override fun getViewBinding(): ActivityStartBinding {
        return ActivityStartBinding.inflate(layoutInflater)
    }

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initData() {
        val time = BuildConfig.BUILD_TIME.toLong()
        val timeInfo = getString(R.string.app_build_time) + SimpleDateFormat(
            "yyyy-MM-dd HH:mm",
            Locale.ENGLISH
        ).format(Date(time))
        binding.tvBuildTime.text = timeInfo

        showStatus()
    }

    override fun initTestItem() {
        addItem("切换数据中心") {
            selectDataCenter()
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

    @SuppressLint("SetTextI18n")
    private fun showStatus() {
        val type = when (SdkManager.getServiceType(application)) {
            SdkManager.TYPE_CHINA -> "中国"
            SdkManager.TYPE_EUROPE -> "欧洲"
            SdkManager.TYPE_NORTH_AMERICA -> "北美"
            else -> "未知[${SdkManager.getServiceType(application)}]"
        }

        binding.tvStatus.text = "当前数据中心: $type"
    }

    private fun selectDataCenter() {
        SelectItemDialog(this).apply {
            addItem("中国") {
                SdkManager.selectService(application, SdkManager.TYPE_CHINA)
                showStatus()
            }

            addItem("欧洲") {
                SdkManager.selectService(application, SdkManager.TYPE_EUROPE)
                showStatus()
            }

            addItem("北美") {
                SdkManager.selectService(application, SdkManager.TYPE_NORTH_AMERICA)
                showStatus()
            }
        }.show()
    }
}