package com.quectel.app.demo.common

import com.quectel.app.demo.R
import com.quectel.app.demo.utils.ToastUtil
import com.quectel.app.quecnetwork.httpservice.IHttpCallBack
import com.quectel.app.usersdk.userservice.IUserService
import com.quectel.app.usersdk.utils.UserServiceFactory
import org.json.JSONException
import org.json.JSONObject

/**
 * Auth code retrieval
 */
object AuthCodeManager {
    fun getSmsCode(type: Int, country: String, phone: String): Boolean {
        if (country.isEmpty()) {
            ToastUtil.showS(R.string.please_input_country_code)
            return false
        }
        if (phone.isEmpty()) {
            ToastUtil.showS(R.string.please_input_phone)
            return false
        }
        UserServiceFactory.getInstance().getService(IUserService::class.java)
            .sendV2PhoneSmsCode(
                country.replace("+", ""),
                phone,
                type,
                object : IHttpCallBack {
                    override fun onSuccess(result: String) {
                        try {
                            val obj = JSONObject(result)
                            if (obj.getInt("code") == 200) {
                                ToastUtil.showS(R.string.get_code_success)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            ToastUtil.showS(e.toString())
                        }
                    }

                    override fun onFail(e: Throwable) {
                        e.printStackTrace()
                        ToastUtil.showS(e.toString())
                    }
                })

        return true
    }

    fun getEmailCode(type: Int, email: String): Boolean {
        if (email.isEmpty()) {
            ToastUtil.showS(R.string.please_input_email)
            return false
        }
        UserServiceFactory.getInstance().getService(IUserService::class.java)
            .sendV2EmailCode(
                email,
                type,
                object : IHttpCallBack {
                    override fun onSuccess(result: String) {
                        try {
                            val obj = JSONObject(result)
                            if (obj.getInt("code") == 200) {
                                ToastUtil.showS(R.string.get_code_success)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                            ToastUtil.showS(e.toString())
                        }
                    }

                    override fun onFail(e: Throwable) {
                        e.printStackTrace()
                        ToastUtil.showS(e.toString())
                    }
                }
            )

        return true
    }
}