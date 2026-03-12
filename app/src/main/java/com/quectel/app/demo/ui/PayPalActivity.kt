package com.quectel.app.demo.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.quectel.app.demo.SdkManager.TYPE_CHINA
import com.quectel.app.demo.SdkManager.TYPE_EUROPE
import com.quectel.app.demo.SdkManager.TYPE_NORTH_AMERICA
import com.quectel.app.demo.SdkManager.getServiceType
import com.quectel.app.demo.databinding.ActivityPaypalBinding
import com.quectel.app.quecnetwork.v2.QuecHttpError
import com.quectel.basic.queclog.QLog
import com.quectel.sdk.pay.entry.QuecPayResult
import com.quectel.sdk.payment.QuecPaymentSdk
import com.quectel.sdk.payment.QuecPaymentSdkApi
import kotlinx.coroutines.launch

class PayPalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaypalBinding

    private val api: QuecPaymentSdkApi = QuecPaymentSdk

    companion object {
        private const val TAG = "PayPalActivity"
        private const val CHECK_TIMEOUT = 20
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaypalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initEnv()
        bindEvent()
    }

    /**
     * 初始化环境
     */
    private fun initEnv() {
        val envType = when (getServiceType(application)) {
            TYPE_CHINA -> "pro"
            TYPE_EUROPE -> "pro-eu"
            TYPE_NORTH_AMERICA -> "pro-us"
            else -> "pro"
        }

        api.setEnvType(envType)
    }

    private fun bindEvent() {
        binding.btnBuy.setOnClickListener { buy() }
        binding.btnStartCheck.setOnClickListener { startCheckPay() }
        binding.btnStopCheck.setOnClickListener { api.stopCheckPayResult() }
    }

    private val bssClientAppId: String
        get() = binding.etBssClientAppId.text.toString().trim()

    private val orderNo: String
        get() = binding.etOrderNo.text.toString().trim()

    private val merchantNo: String
        get() = binding.etMerchantNo.text.toString().trim()

    private val payTitle: String
        get() = binding.etTitle.text.toString().trim()

    /**
     * 开始查询订单支付状态
     */
    private fun startCheckPay() {

        if (!checkOrderParams()) return
        // Params:
        // orderNo - 订单编号
        // merchantNo - 商户号
        // timeout - 超时时间 秒
        // listener - 回调
        api.startCheckPayResult(orderNo, merchantNo, CHECK_TIMEOUT) { info, _ ->

            val resultText = when (info.result) {
                QuecPayResult.STATUS.SUCCESS -> "success"
                QuecPayResult.STATUS.FAIL -> "fail"
                QuecPayResult.STATUS.CANCEL -> "cancel"
                QuecPayResult.STATUS.UNKNOWN -> "unknown"
            }

            runOnUiThread {
                toast("$resultText ${info.message}")
            }
        }
    }

    /**
     * Paypal支付
     */
    private fun buy() {

        if (!checkPayParams()) return

        api.setBssClientAppId(bssClientAppId)

        lifecycleScope.launch {

            runCatching {
                // Paypal 支付
                // @param context 上下文
                // @param orderNo 订单编号
                // @param title 商品标题
                api.payWithPaypal(
                    this@PayPalActivity,
                    orderNo,
                    payTitle
                )
            }.onSuccess {
                toast("打开 PayPal 支付")
            }.onFailure { e ->
                handlePayError(e)
            }
        }
    }

    /**
     * 校验支付参数
     */
    private fun checkPayParams(): Boolean {

        if (bssClientAppId.isEmpty() ||
            orderNo.isEmpty() ||
            merchantNo.isEmpty() ||
            payTitle.isEmpty()
        ) {
            toast("请填写 bssClientAppId / orderNo / merchantNo / title")
            return false
        }
        return true
    }

    /**
     * 校验订单参数
     */
    private fun checkOrderParams(): Boolean {

        if (orderNo.isEmpty() || merchantNo.isEmpty()) {
            toast("请填写 orderNo / merchantNo")
            return false
        }
        return true
    }

    /**
     * 统一错误处理
     */
    private fun handlePayError(e: Throwable) {

        val msg = when (e) {
            is QuecHttpError -> "失败: ${e.message}"
            else -> "异常: ${e.message}"
        }

        toast(msg)

        QLog.e(TAG, "pay error: ${e.message}")
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        QLog.i(TAG, "msg : $msg")
    }
}