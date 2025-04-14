package com.quectel.app.demo.base.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.quectel.app.demo.MainActivity
import com.quectel.app.demo.base.QuecBaseView
import com.quectel.app.demo.base.QuecLoadingDialog
import com.quectel.app.demo.utils.DisplayUtil
import com.quectel.basic.common.entity.QuecResult
import com.quectel.basic.common.utils.QuecStatusBarUtil
import com.quectel.basic.queclog.QLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import java.lang.reflect.Method

abstract class QuecBaseCommonActivity : AppCompatActivity(), QuecBaseView {

    protected lateinit var mContext: AppCompatActivity

    private var showLoading = false
    private var fontScale = 1f
    private val loadingDelay = 200L

    protected val scope by lazy {
        CoroutineScope(Dispatchers.Main)
    }

    //loading框
    private var loadingDialog: QuecLoadingDialog? = null

    //沉浸式状态栏
    open fun openStatusBar(): Boolean {
        return true
    }

    /**
     * eventBus注册阀门
     */
    open fun valveEventBus(): Boolean {
        return false
    }

    /**
     * 显示错误页面
     */
    fun showErrorPage() {

    }

    override fun showOrHideLoading(isShow: Boolean) {
        if (isShow) {
            showLoading("")
        } else {
            dismissLoading()
        }
    }

    open fun showLoading(message: String) {
        showLoading = true
        scope.launch {
            delay(loadingDelay)
            if (showLoading) {
                showLoadingDialog(message)
            }
        }
    }

    open fun dismissLoading() {
        showLoading = false
        scope.launch {
            delay(loadingDelay)
            dismissLoadingDialog()
        }
    }

    fun showLoadingDialog(message: String) {
        if (this != null && !this.isFinishing) {
            if (loadingDialog == null) {
                loadingDialog = QuecLoadingDialog(this)
                    .apply {
                        this.bindLifecycleOwner(this@QuecBaseCommonActivity)
                    }
            }
            if (loadingDialog != null && loadingDialog?.isShowing == false) {
                loadingDialog?.showPopupWindow(message)
            }
        }
    }


    private fun dismissLoadingDialog() {
        loadingDialog?.dismiss()
        loadingDialog = null
    }

    open fun onPreStart(): Boolean {
        return true
    }

    override fun showMessage(code: Int) {
        val toast: Toast = Toast.makeText(this, code.toString(), Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }

    override fun showMessage(info: String) {
        val toast: Toast = Toast.makeText(this, info, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        if (needCheckOrientation()) {
            val fixOrientation = fixOrientation();
            Log.i("Activity", "onCreate fixOrientation when Oreo, result = " + fixOrientation);
        }
        super.onCreate(savedInstanceState)
        mContext = this

        if (openStatusBar()) {
            initStatusBar()
        }

        if (!EventBus.getDefault().isRegistered(this) && valveEventBus()) {
            EventBus.getDefault().register(this);
        }
    }

    inline fun startTargetActivity(
        clazz: Class<*>,
        code: Int? = null,
        block: Intent.() -> Unit = {}
    ) {
        val intent = Intent(this, clazz)
        initIntent(intent)
        intent.block()

        try {
            if (code != null) {
                startActivityForResult(intent, code)
            } else {
                startActivity(intent)
            }
        } catch (e: Exception) {
            QLog.e(e)
            showMessage(e.toString())
        }
    }

    /**
     * 初始化Intent, 可以在子类中传入通用的值
     */
    open fun initIntent(intent: Intent) {

    }

    /**
     * StatusBar默认 透明、沉浸式、反色
     */
    open fun initStatusBar() {
        QuecStatusBarUtil.transparentStatusBar(this, true);
        val color = if (QuecStatusBarUtil.isDarkMode(this)) Color.BLACK else Color.WHITE
        QuecStatusBarUtil.setStatusBarColor(this, color);
        QuecStatusBarUtil.setAndroidNativeLightStatusBar(this);
    }

    override fun onDestroy() {
        super.onDestroy()

        //反注册
        if (EventBus.getDefault().isRegistered(this) && valveEventBus()) {
            EventBus.getDefault().unregister(this);
        }
    }


    private fun fixOrientation(): Boolean {
        try {
            var field = Activity::class.java.getDeclaredField("mActivityInfo");
            field.setAccessible(true);
            var o = field.get(this) as ActivityInfo
            o.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
            field.setAccessible(false);
            return true;
        } catch (e: Exception) {
            e.printStackTrace();
        }
        return false;
    }

    private fun isTranslucentOrFloating(): Boolean {
        var isTranslucentOrFloating = false
        try {
            var styleableRes = Class.forName("com.android.internal.R" + "$" + "styleable").getField(
                "Window"
            ).get(null) as IntArray
            var ta = obtainStyledAttributes(styleableRes);
            var m: Method = ActivityInfo::class.java.getMethod(
                "isTranslucentOrFloating",
                TypedArray::class.java
            );
            m.setAccessible(true);
            isTranslucentOrFloating = m.invoke(null, ta) as Boolean
            m.setAccessible(false);
        } catch (e: Exception) {
            e.printStackTrace();
        }
        return isTranslucentOrFloating
    }

    open fun needCheckOrientation(): Boolean {
        return Build.VERSION.SDK_INT == Build.VERSION_CODES.O && isTranslucentOrFloating()
    }


    override fun setRequestedOrientation(requestedOrientation: Int) {
        if (needCheckOrientation()) {
            QLog.i(
                "BaseActivity",
                "setRequestedOrientation avoid calling setRequestedOrientation when Oreo"
            );
            return;
        }
        super.setRequestedOrientation(requestedOrientation)
    }


    override fun getResources(): Resources? {
        //QLog.i(this.toString(), "getResources")
        var resources = super.getResources()
        return DisplayUtil.getResources(this, resources, fontScale)
    }

    override fun attachBaseContext(base: Context?) {
        //QLog.i(this.toString(), "attachBaseContext")
        super.attachBaseContext(DisplayUtil.attachBaseContext(base, fontScale))
    }

    /**
     * 设置字体大小，同时通知界面重绘
     */
    open fun setFontScale(fontScale: Float) {
        //QLog.i(this.toString(), "setFontSize $fontScale")
        this.fontScale = fontScale
        DisplayUtil.recreate(this)
    }

    protected fun handlerError(result: QuecResult<*>) {
        showMessage("[${result.code}] ${result.msg}")
    }

    protected fun handlerResult(result: QuecResult<*>) {
        if (result.isSuccess) {
            showMessage("操作成功")
        } else {
            handlerError(result)
        }
    }

    protected fun getFid(): String? {
        return null
    }

    protected fun log(info: String?) {
        QLog.i(this.javaClass.simpleName, info)
    }

    protected fun backMain() {
        startActivity(Intent(this, MainActivity::class.java))
    }
}