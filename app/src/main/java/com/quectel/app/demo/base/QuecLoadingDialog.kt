package com.quectel.app.demo.base

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.animation.Animation
import android.widget.TextView
import com.quectel.app.demo.R
import com.quectel.basic.common.utils.QuecDimenUtil
import razerdp.basepopup.BasePopupWindow
import razerdp.util.animation.AlphaConfig
import razerdp.util.animation.AnimationHelper.AnimationBuilder

class QuecLoadingDialog : BasePopupWindow {

    constructor(context: Context) : super(context) {
        val size = QuecDimenUtil.dp2px(120f)
        setMaxWidth(size)
        setMaxHeight(size)
        popupGravity = Gravity.CENTER
        setOutSideDismiss(false)
        setBackPressEnable(true)
        setBackgroundColor(Color.TRANSPARENT)
        setContentView(R.layout.quec_custom_loading_dialog)
    }

    override fun onCreateShowAnimation(): Animation? {
        return AnimationBuilder()
            .withAlpha(AlphaConfig.IN)
            .toShow()
    }

    override fun onCreateDismissAnimation(): Animation? {
        return AnimationBuilder()
            .withAlpha(AlphaConfig.OUT)
            .toDismiss()
    }

    fun showPopupWindow(message:String){
        var tvTip = findViewById<TextView>(R.id.tvTip)
        if(!TextUtils.isEmpty(message)){
            tvTip.visibility= View.VISIBLE
            tvTip.text = message
        }else{
            tvTip.visibility= View.GONE
        }
        super.showPopupWindow();
    }
}