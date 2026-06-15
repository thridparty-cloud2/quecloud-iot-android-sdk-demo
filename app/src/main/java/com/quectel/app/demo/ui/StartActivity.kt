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
        addItem(getString(R.string.switch_data_center)) {
            selectDataCenter()
        }

        addItem(getString(R.string.account_login)) {
            startTargetActivity(LoginExActivity::class.java)
        }

        addItem(getString(R.string.account_login_auth_code)) {
            startTargetActivity(AuthCodeLoginActivity::class.java)
        }

        addItem(getString(R.string.account_register)) {
            startTargetActivity(RegisterExActivity::class.java)
        }

        addItem(getString(R.string.retrieve_password)) {
            startTargetActivity(ResetPwdExActivity::class.java)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showStatus() {
        val type = when (SdkManager.getServiceType(application)) {
            SdkManager.TYPE_CHINA -> getString(R.string.region_china)
            SdkManager.TYPE_EUROPE -> getString(R.string.region_europe)
            SdkManager.TYPE_NORTH_AMERICA -> getString(R.string.region_north_america)
            else -> getString(R.string.unknown_data_center, SdkManager.getServiceType(application))
        }

        binding.tvStatus.text = getString(R.string.current_data_center, type)
    }

    private fun selectDataCenter() {
        SelectItemDialog(this).apply {
            addItem(getString(R.string.region_china)) {
                SdkManager.selectService(application, SdkManager.TYPE_CHINA)
                showStatus()
            }

            addItem(getString(R.string.region_europe)) {
                SdkManager.selectService(application, SdkManager.TYPE_EUROPE)
                showStatus()
            }

            addItem(getString(R.string.region_north_america)) {
                SdkManager.selectService(application, SdkManager.TYPE_NORTH_AMERICA)
                showStatus()
            }
        }.show()
    }
}