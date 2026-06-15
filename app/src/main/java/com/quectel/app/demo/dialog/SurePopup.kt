package com.quectel.app.demo.dialog

import android.content.Context
import android.view.Gravity
import android.view.View
import com.quectel.app.demo.R
import com.quectel.app.demo.databinding.QuecBasicUiSurePopBinding
import razerdp.basepopup.BasePopupWindow

class SurePopup(context: Context) : BasePopupWindow(context) {
    private var mBinding: QuecBasicUiSurePopBinding? = null
    private var mListener: OnSureListener? = null

    init {
        setContentView(R.layout.quec_basic_ui_sure_pop)
        popupGravity = Gravity.CENTER
    }


    override fun onViewCreated(contentView: View) {
        super.onViewCreated(contentView)
        mBinding = QuecBasicUiSurePopBinding.bind(contentView)
        initListener()
    }

    /**
     * Initialize listeners
     */
    private fun initListener() {
        mBinding!!.tvSure.setOnClickListener { view ->
            if (mListener != null) {
                mListener!!.sure()
            }
        }

        mBinding!!.tvCancel.setOnClickListener { view ->
            dismiss()
        }
    }

    fun setSureListener(listener: OnSureListener) {
        this.mListener = listener
    }


    fun setTitle(title: String): SurePopup {
        mBinding!!.tvTitle.text = title
        return this
    }

    fun setCancel(cancel: String): SurePopup {
        mBinding!!.tvCancel.text = cancel
        return this
    }

    fun setSure(sure: String): SurePopup {
        mBinding!!.tvSure.text = sure
        return this
    }

    /**
     * Confirm listener
     */
    interface OnSureListener {
        /**
         * Confirm
         */
        fun sure()
    }
}
