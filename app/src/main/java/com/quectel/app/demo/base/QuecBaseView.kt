package com.quectel.app.demo.base

interface QuecBaseView {
    fun showOrHideLoading(isShow: Boolean)
    fun showMessage(code: Int)

    fun showMessage(info: String)

    //显示下拉加载框, 使用到的Activity实现该方法
    fun onShowRefresh(isShow: Boolean) {

    }

    /**
     * 显示错误页面
     */
    fun showErrorView(isShow: Boolean) {
    }
}