package com.quectel.app.demo.base.fragment

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.quectel.app.demo.base.QuecBaseView
import com.quectel.app.demo.base.QuecLoadingDialog
import com.quectel.app.demo.base.activity.QuecBaseCommonActivity
import com.quectel.basic.queclog.QLog

abstract class QuecBaseCommonFragment : Fragment(), QuecBaseView {
    private var progressDialog: QuecLoadingDialog? = null

    override fun showOrHideLoading(isShow: Boolean) {
        val activity = activity ?: return
        if (activity.isFinishing) return
        if (activity is QuecBaseCommonActivity) {
            activity.showOrHideLoading(isShow)
        }
    }
    init {
        QLog.i( "QuecBaseCommonFragment init")
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
}