package com.quectel.app.demo.base.fragment

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import com.quectel.app.demo.R
import com.quectel.app.demo.base.QuecBaseView
import com.quectel.app.demo.base.QuecLoadingDialog
import com.quectel.app.demo.base.activity.QuecBaseCommonActivity
import com.quectel.basic.common.entity.QuecResult
import com.quectel.basic.queclog.QLog
import me.yokeyword.fragmentation.SupportFragment

abstract class QuecBaseCommonFragment : SupportFragment(), QuecBaseView {
    private var progressDialog: QuecLoadingDialog? = null
    private var touchTime: Long = 0

    override fun showOrHideLoading(isShow: Boolean) {
        val activity = activity ?: return
        if (activity.isFinishing) return
        if (activity is QuecBaseCommonActivity) {
            activity.showOrHideLoading(isShow)
        }
    }

    init {
        QLog.i("QuecBaseCommonFragment init")
    }

    override fun showMessage(code: Int) {
        context?.run {
            Toast.makeText(this, code.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    override fun showMessage(info: String) {
        context?.run {
            Toast.makeText(this, info, Toast.LENGTH_SHORT).show()
        }
    }

    open fun <T> createIntent(clazz: Class<T>): Intent {
        return Intent(context, clazz)
    }

    fun handlerError(result: QuecResult<*>) {
        showMessage("[${result.code}] ${result.msg}")
    }

    /**
     * 处理回退事件
     *
     * @return
     */
    override fun onBackPressedSupport(): Boolean {
        if (System.currentTimeMillis() - touchTime < WAIT_TIME) {
            _mActivity.finish()
        } else {
            touchTime = System.currentTimeMillis()
            showMessage(R.string.press_again_exit)
        }
        return true
    }

    fun <T : Activity> startTargetActivity(
        clazz: Class<T>,
        code: Int? = null,
        callback: ((intent: Intent) -> Unit)? = null
    ) {
        val intent = createIntent(clazz)
        callback?.invoke(intent)
        if (code != null) {
            startActivityForResult(intent, code)
        } else {
            startActivity(intent)
        }
    }

    companion object {
        // 再点一次退出程序时间设置
        private const val WAIT_TIME: Long = 2000L
    }
}