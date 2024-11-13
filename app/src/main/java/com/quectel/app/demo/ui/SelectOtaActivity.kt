package com.quectel.app.demo.ui

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.OnClick
import com.quectel.app.demo.R
import com.quectel.app.demo.base.BaseActivity
import com.quectel.app.demo.bean.OtaUpgradePlanModel
import com.quectel.app.demo.bean.UserDeviceList.DataBean.ListBean
import com.quectel.app.demo.utils.MyUtils
import com.quectel.app.demo.utils.ToastUtils


class SelectOtaActivity() : BaseActivity() {

    val TAG = "DeviceSelectOtaActivity"

    lateinit var device: ListBean
    lateinit var otaUpgradePlanModel: OtaUpgradePlanModel

    override fun getContentLayout(): Int {
        return R.layout.activity_select_ota
    }

    override fun addHeadColor() {
        MyUtils.addStatusBarView(this, R.color.gray_bg)
    }


    override fun initData() {
        initView()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun initView() {

        findViewById<View>(R.id.iv_back).setOnClickListener {
            finish()
        }

        findViewById<View>(R.id.btn_cloud_ota).setOnClickListener {
            val intent = Intent(this, CloudOtaActivity::class.java)
            startActivity(intent)
        }

        findViewById<View>(R.id.btn_ble_ota).setOnClickListener {
           ToastUtils.showShort(this, "暂不支持")
        }
    }


}
