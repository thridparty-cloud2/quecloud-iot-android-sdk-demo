package com.quectel.app.demo.base.activity

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.viewbinding.ViewBinding
import com.quectel.app.demo.R
import com.quectel.basic.common.utils.QuecToastUtil
import com.quectel.basic.queclog.QLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

abstract class QuecBaseActivity<T : ViewBinding> : QuecBaseCommonActivity() {
    lateinit var binding: T
    private val mainScope = MainScope()
    private val blockList = ArrayList<Item>()

    abstract fun getViewBinding(): T
    abstract fun initView(savedInstanceState: Bundle?)
    abstract fun initData()
    protected open fun initTestItem() {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!onPreStart()) {
            finish()
            return
        }

        binding = getViewBinding()
        setContentView(binding.root)

        if (!EventBus.getDefault().isRegistered(this) && valveEventBus()) {
            EventBus.getDefault().register(this);
        }

        initView(savedInstanceState)
        initData()

        initTestItem()
        findViewById<ListView>(R.id.lv_list)?.run {
            val items = blockList.map { item -> item.title }
            adapter =
                ArrayAdapter(this@QuecBaseActivity, android.R.layout.simple_list_item_1, items)
            setOnItemClickListener { _, _, position, _ ->
                blockList[position].block()
            }
        }
    }

    protected fun addItem(title: String, block: () -> Unit) {
        blockList.add(Item(title, block))
    }

    protected fun launch(block: suspend CoroutineScope.() -> Unit) {
        mainScope.launch {
            try {
                block()
            } catch (e: Exception) {
                QLog.e(e)
                QuecToastUtil.showL("执行失败: $e")
            }
        }
    }

    protected data class Item(
        val title: String,
        val block: () -> Unit,
    )
}