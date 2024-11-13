package com.quectel.app.demo.ui

import android.view.View
import android.widget.ScrollView
import android.widget.TextView
import com.google.gson.reflect.TypeToken
import com.quectel.app.demo.R
import com.quectel.app.demo.base.BaseActivity
import com.quectel.app.demo.bean.OtaUpgradePlanModel
import com.quectel.app.demo.bean.UserDeviceList.DataBean.ListBean
import com.quectel.app.demo.utils.MyUtils
import com.quectel.app.device.bean.UpgradePlan
import com.quectel.app.device.deviceservice.IDevService
import com.quectel.app.device.utils.DeviceServiceFactory
import com.quectel.app.quecnetwork.httpservice.IHttpCallBack
import com.quectel.basic.common.entity.QuecResult
import com.quectel.basic.common.utils.QuecGsonUtil
import com.quectel.basic.common.utils.QuecGsonUtil.getGson
import com.quectel.basic.queclog.QLog
import com.quectel.sdk.ota.upgrade.model.OtaUpgradeStatusModel
import java.text.SimpleDateFormat


class CloudOtaActivity() : BaseActivity() {

    val TAG = "DeviceOtaActivity"

    lateinit var device: ListBean
    lateinit var otaUpgradePlanModel: OtaUpgradePlanModel

    override fun getContentLayout(): Int {
        return R.layout.activity_cloud_ota
    }

    override fun addHeadColor() {
        MyUtils.addStatusBarView(this, R.color.gray_bg)
    }


    override fun initData() {
        val intent = intent
        device = intent.getSerializableExtra("device") as ListBean
        initView()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun initView() {
        findViewById<View>(R.id.iv_back).setOnClickListener {
            finish()
        }
//
//        //云OTA-查询升级计划
//        findViewById<View>(R.id.btn_query_cloud_upgrade_plan).setOnClickListener {
//            DeviceServiceFactory.getInstance().getService(IDevService::class.java)
//                .getUpgradePlanDeviceList(null,1, 20, object :
//                    IHttpCallBack {
//                    override fun onSuccess(s: String?) {
//                        QLog.i(TAG, "getDeviceUpgradePlan onSuccess: $s")
//                        val result = QuecGsonUtil.getResult(s, OtaUpgradePlanModel::class.java)
//                        if (result.successCode()) {
//                            if (result.data != null) {
//                                otaUpgradePlanModel = result.data
//                                logText("升级计划查询成功 名称:${otaUpgradePlanModel.planName} 版本:${otaUpgradePlanModel.versionInfo}")
//                            } else {
//                                logText("没有升级计划,请先在平台上配置")
//                            }
//                        } else {
//                            logText("升级计划查询失败 error code ${result.code}")
//                        }
//                    }
//
//                    override fun onFail(e: Throwable?) {
//                        e?.printStackTrace()
//                        logText("升级计划查询失败")
//                    }
//                })
//        }
//        //云OTA-确认升级计划
//        findViewById<View>(R.id.btn_commit_cloud_upgrade).setOnClickListener {
//
//            if (otaUpgradePlanModel == null) {
//                logText("请先查询升级计划")
//                return@setOnClickListener
//            }
//
//            val upgradePlan = UpgradePlan()
//            upgradePlan.deviceKey = device.deviceKey
//            upgradePlan.productKey = device.productKey
//            upgradePlan.planId = otaUpgradePlanModel.planId.toLong()
//            upgradePlan.operType = 1 //1-马上升级(确认随时升级) 2-预约升级(预约指定时间窗口升级) 3-(取消预约和取消升级)
//
//            DeviceServiceFactory.getInstance().getService(IDevService::class.java)
//                .userBatchConfirmUpgradeWithList(mutableListOf(upgradePlan), object :
//                    IHttpCallBack {
//                    override fun onSuccess(s: String?) {
//                        QLog.i(TAG, "userBatchConfirmUpgradeWithList onSuccess: $s")
//                        val result = QuecGsonUtil.getResult(s, Object::class.java)
//                        if (result.successCode()) {
//                            logText("升级确认成功")
//                        } else {
//                            logText("升级确认失败 error code ${result.code}")
//                        }
//                    }
//
//                    override fun onFail(e: Throwable?) {
//                        e?.printStackTrace()
//                        logText("升级确认失败")
//                    }
//                })
//        }
//        //云OTA-获取升级状态
//        findViewById<View>(R.id.btn_get_cloud_upgrade_status).setOnClickListener {
//            DeviceServiceFactory.getInstance().getService(IDevService::class.java)
//                .getUpgradeStatus(device.deviceKey, device.productKey, object :
//                    IHttpCallBack {
//                    override fun onSuccess(s: String?) {
//                        QLog.i(TAG, "getDeviceUpgradePlan onSuccess: $s")
//                        val result = getGson().fromJson<QuecResult<List<OtaUpgradeStatusModel>?>>(
//                            s,
//                            object : TypeToken<QuecResult<List<OtaUpgradeStatusModel?>?>?>() {}.type
//                        )
//                        if (result == null) {
//                            logText("升级状态查询失败")
//                            return
//                        }
//                        if (result.data != null && result.data!!.isNotEmpty()) {
//                            for (model in result.data!!) {
//                                logText("升级状态进度：" + model.upgradeProgress)
//                            }
//                        }
//                    }
//
//                    override fun onFail(e: Throwable?) {
//                        e?.printStackTrace()
//                        logText("升级状态查询失败")
//                    }
//                })
//
//        }





    }


//    private fun logText(msg: String) {
//        val logTextView = findViewById<TextView>(R.id.tv_log)
//        var text = logTextView.text.toString()
//        val dateFormat = SimpleDateFormat("HH:mm:ss")
//        text += "[" + dateFormat.format(System.currentTimeMillis()) + "] " + msg + "\n"
//        logTextView.text = text
//        findViewById<ScrollView>(R.id.sv_log).scrollTo(0, logTextView.bottom)
//    }


}
