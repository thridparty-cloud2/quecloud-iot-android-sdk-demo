package com.quectel.app.demo.base.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.quectel.basic.queclog.QLog
import org.greenrobot.eventbus.EventBus

abstract class QuecBaseFragment<T : ViewBinding> : QuecBaseCommonFragment() {
      lateinit var binding: T


    init {
        QLog.i( "QuecBaseFragment init")
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return getViewBinding(inflater, container).also { binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //注册监听
        if (!EventBus.getDefault().isRegistered(this) && valveEventBus()) {
            EventBus.getDefault().register(this);
        }
        initView(savedInstanceState)
        initData()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //注册监听
        if (EventBus.getDefault().isRegistered(this) && valveEventBus()) {
            EventBus.getDefault().unregister(this);
        }
    }
    /**
     * eventBus注册阀门
     */
    open fun valveEventBus():Boolean{
        return false
    }

    abstract fun getViewBinding(
            inflater: LayoutInflater,
            container: ViewGroup?
    ): T

    abstract fun initView(savedInstanceState: Bundle?)
    abstract fun initData()
}