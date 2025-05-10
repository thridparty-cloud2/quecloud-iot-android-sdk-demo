package com.quectel.app.demo.ui.family

import android.content.Intent
import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.quectel.app.demo.base.activity.QuecBaseActivity
import com.quectel.app.smart_home_sdk.bean.QuecFamilyItemModel

abstract class BaseFamilyActivity<T : ViewBinding> : QuecBaseActivity<T>() {
    protected lateinit var family: QuecFamilyItemModel

    override fun onCreate(savedInstanceState: Bundle?) {
        val family = intent.getSerializableExtra(CODE_FAMILY) as? QuecFamilyItemModel

        if (family == null) {
            showMessage("数据异常")
            finish()
            return
        }

        this.family = family
        super.onCreate(savedInstanceState)
    }

    override fun startActivity(intent: Intent?, options: Bundle?) {
        intent?.putExtra(CODE_FAMILY, family)
        super.startActivity(intent, options)
    }

    fun getCurrentFid(): String {
        return family.fid ?: ""
    }

    companion object {
        const val CODE_FAMILY = "CODE_FAMILY"
    }
}