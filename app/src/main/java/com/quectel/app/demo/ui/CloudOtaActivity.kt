package com.quectel.app.demo.ui

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quectel.app.demo.R
import com.quectel.app.demo.adapter.DeviceOtaAdapter
import com.quectel.app.demo.base.BaseActivity
import com.quectel.app.demo.bean.DeviceOtaModel
import com.quectel.app.demo.bean.DeviceUpgradeSumBean
import com.quectel.app.demo.bean.OtaUpgradePlanModel
import com.quectel.app.demo.utils.MyUtils
import com.quectel.app.demo.utils.ToastUtils
import com.quectel.app.demo.widget.BottomItemDecorationSystem
import com.quectel.app.device.bean.UpgradeDeviceBean
import com.quectel.app.device.bean.UpgradePlan
import com.quectel.app.quecnetwork.httpservice.IHttpCallBack
import com.quectel.basic.common.utils.QuecGsonUtil
import com.quectel.basic.common.utils.QuecThreadUtil
import com.quectel.basic.queclog.QLog
import com.quectel.sdk.ota.upgrade.model.OtaUpgradeStatusModel
import com.quectel.sdk.ota.upgrade.service.IQuecHttpOtaService
import com.quectel.sdk.ota.upgrade.util.QuecHttpOtaServiceFactory
import java.util.Timer
import java.util.TimerTask


class CloudOtaActivity() : BaseActivity() {

    private val TAG = "DeviceOtaActivity"
    private var timer: Timer? = null
    lateinit var deviceOtaAdapter: DeviceOtaAdapter


    override fun getContentLayout(): Int {
        return R.layout.activity_cloud_ota
    }

    override fun addHeadColor() {
        MyUtils.addStatusBarView(this, R.color.gray_bg)
    }


    override fun initData() {
        val intent = intent
        initView()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
        timer = null
    }

    private fun initView() {
        deviceOtaAdapter = DeviceOtaAdapter(this, mutableListOf<DeviceOtaModel>())
        val recyclerView = findViewById<RecyclerView>(R.id.rv_cloud_ota)
        recyclerView.setLayoutManager(LinearLayoutManager(this))
        recyclerView.addItemDecoration(BottomItemDecorationSystem(this))
        recyclerView.adapter = deviceOtaAdapter

        findViewById<View>(R.id.iv_back).setOnClickListener {
            finish()
        }
        //云OTA-用户有可升级的设备
        findViewById<View>(R.id.btn_query_cloud_has_device_upgrade).setOnClickListener {
            QuecHttpOtaServiceFactory.getInstance().getService(IQuecHttpOtaService::class.java)
                .getUserIsHaveDeviceUpgrade(null, object :
                    IHttpCallBack {
                    override fun onSuccess(s: String?) {
                        QLog.i(TAG, "getUserIsHaveDeviceUpgrade onSuccess: $s")
                        val result = QuecGsonUtil.getResult(s, DeviceUpgradeSumBean::class.java)
                        if (result.successCode()) {
                            ToastUtils.showShort(
                                this@CloudOtaActivity,
                                "可升级设备数：${result.data.haveDeviceUpgradeSum}"
                            )
                        } else {
                            ToastUtils.showShort(this@CloudOtaActivity, "查询失败")
                        }

                    }

                    override fun onFail(e: Throwable?) {
                        e?.printStackTrace()
//                        logText("升级计划查询失败")
                    }
                })
        }


        //云OTA-确认升级计划
        findViewById<View>(R.id.btn_commit_cloud_upgrade).setOnClickListener {
            val deviceOtaList = deviceOtaAdapter.data
            if (deviceOtaList == null || deviceOtaList.size == 0) {
                ToastUtils.showShort(this, "请先在平台创建升级计划")
                return@setOnClickListener
            }

            val planList = ArrayList<UpgradePlan>()
            deviceOtaList.forEach {
                val upgradePlan = UpgradePlan()
                upgradePlan.deviceKey = it.deviceKey
                upgradePlan.productKey = it.productKey
                upgradePlan.planId = it.planId
                upgradePlan.operType = 1 //1-马上升级(确认随时升级) 2-预约升级(预约指定时间窗口升级) 3-(取消预约和取消升级)
                planList.add(upgradePlan)
            }
            QuecHttpOtaServiceFactory.getInstance().getService(IQuecHttpOtaService::class.java)
                .userBatchConfirmUpgradeWithList(planList, object :
                    IHttpCallBack {
                    override fun onSuccess(s: String?) {
                        QLog.i(TAG, "userBatchConfirmUpgradeWithList onSuccess: $s")
                        val result = QuecGsonUtil.getResult(s, Object::class.java)
                        if (result.successCode()) {
                            //定时查询升级状态
                            startTimer()
                        } else {
                            ToastUtils.showShort(
                                this@CloudOtaActivity,
                                "升级确认失败：${result.msg}"
                            )
                        }
                    }

                    override fun onFail(e: Throwable?) {
                        e?.printStackTrace()
                        ToastUtils.showShort(this@CloudOtaActivity, "升级确认失败")
                    }
                })
        }

        //查询升级计划
        getUpgradePlanDeviceList()

    }

    private fun getUpgradePlanDeviceList() {
        QuecHttpOtaServiceFactory.getInstance().getService(IQuecHttpOtaService::class.java)
            .getUpgradePlanDeviceList(null, 1, 20, object :
                IHttpCallBack {
                override fun onSuccess(s: String?) {
                    QLog.i(TAG, "getDeviceUpgradePlan onSuccess: $s")
                    val result = QuecGsonUtil.getPageResult(s, DeviceOtaModel::class.java)
                    if (result.successCode()) {
                        if (result.data != null && result.data.list != null) {
                            if (result.data.list.isEmpty()) {
                                ToastUtils.showShort(
                                    this@CloudOtaActivity,
                                    "请先在平台创建升级计划"
                                )
                            }
                            QuecThreadUtil.RunMainThread {
                                deviceOtaAdapter.setList(result.data.list)
                            }

                        }
                    }
                }

                override fun onFail(e: Throwable?) {
                    e?.printStackTrace()
                }
            })
    }

    //批量查询升级详情
    private fun getBatchUpgradeDetails() {
        val list: MutableList<UpgradeDeviceBean> = ArrayList()
        val deviceOtaList = deviceOtaAdapter.data
        deviceOtaList.forEach {
            val bean = UpgradeDeviceBean()
            bean.deviceKey = it.deviceKey
            bean.planId = it.planId
            bean.productKey = it.productKey
            list.add(bean)
        }

        QuecHttpOtaServiceFactory.getInstance().getService(IQuecHttpOtaService::class.java)
            .getBatchUpgradeDetailsWithList(list, object : IHttpCallBack {
                override fun onSuccess(s: String) {
                    val result = QuecGsonUtil.getPageResult(s, OtaUpgradeStatusModel::class.java)
                    result.data?.list?.forEach { statusModel ->

                        deviceOtaAdapter.data.find { statusModel.deviceKey == it.deviceKey && statusModel.productKey == it.productKey && statusModel.planId == it.planId}?.apply {
                            upgradeProgress = statusModel.upgradeProgress
                            userConfirmStatus = statusModel.userConfirmStatus
                            deviceStatus = statusModel.deviceStatus
                        }
                    }
                    QuecThreadUtil.RunMainThread {
                        deviceOtaAdapter.notifyDataSetChanged()
                    }
                }

                override fun onFail(e: Throwable) {
                    e.printStackTrace()
                }
            })
    }

    //定时器，定时5秒查询升级详情
    private fun startTimer() {
        if (timer != null) return
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                getBatchUpgradeDetails()
            }
        }, 0, 5000)
    }

}
