package com.quectel.app.demo.base

interface QuecBaseView {
    fun showOrHideLoading(isShow: Boolean)
    fun showMessage(code: Int)

    fun showMessage(info: String)

    // Show pull-to-refresh loader; Activity using this should implement this method
    fun onShowRefresh(isShow: Boolean) {

    }

    /**
     * Show error page
     */
    fun showErrorView(isShow: Boolean) {
    }
}