package com.quectel.app.demo.base.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.viewbinding.ViewBinding
import com.quectel.app.demo.R
import com.quectel.app.demo.base.activity.QuecBaseActivity
import com.quectel.app.demo.base.activity.QuecBaseActivity.Item
import com.quectel.basic.queclog.QLog
import org.greenrobot.eventbus.EventBus

abstract class QuecBaseFragment<T : ViewBinding> : QuecBaseCommonFragment() {
    lateinit var binding: T
    private val blockList = ArrayList<Item>()

    init {
        QLog.i("QuecBaseFragment init")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return getViewBinding(inflater, container).also { binding = it }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //注册监听
        if (!EventBus.getDefault().isRegistered(this) && valveEventBus()) {
            EventBus.getDefault().register(this)
        }
        initView(savedInstanceState)
        initData()
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<ListView>(R.id.lv_list)?.run {
            val items = blockList.map { item -> item.title }
            adapter =
                ArrayAdapter(context, android.R.layout.simple_list_item_1, items)
            setOnItemClickListener { _, _, position, _ ->
                blockList[position].block()
            }
        }
    }

    protected fun addItem(title: String, block: () -> Unit) {
        blockList.add(Item(title, block))
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
    open fun valveEventBus(): Boolean {
        return false
    }

    abstract fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): T

    abstract fun initView(savedInstanceState: Bundle?)
    abstract fun initData()
    protected data class Item(
        val title: String,
        val block: () -> Unit,
    )
}