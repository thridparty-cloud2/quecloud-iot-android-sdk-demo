package com.quectel.app.demo.ui.device.list

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.quectel.app.demo.base.activity.QuecBaseDeviceActivity
import com.quectel.app.demo.base.fragment.QuecBaseFragment
import com.quectel.app.demo.databinding.ActivityDeviceListBinding
import com.quectel.app.demo.ui.device.function.DeviceFunctionActivity
import com.quectel.app.demo.widget.BottomItemDecorationSystem
import com.quectel.app.device.bean.QuecDeviceListParamsModel
import com.quectel.app.device.deviceservice.QuecDeviceService
import com.quectel.basic.common.entity.QuecDeviceModel
import `in`.srain.cube.views.ptr.PtrDefaultHandler
import `in`.srain.cube.views.ptr.PtrFrameLayout
import `in`.srain.cube.views.ptr.PtrHandler

class DeviceListFragment : QuecBaseFragment<ActivityDeviceListBinding>() {
    private val mDeviceList = mutableListOf<QuecDeviceModel>()
    private lateinit var mAdapter: DeviceListAdapter

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): ActivityDeviceListBinding {
        return ActivityDeviceListBinding.inflate(inflater, container, false)
    }

    override fun initView(savedInstanceState: Bundle?) {
        mAdapter = DeviceListAdapter(mDeviceList) {
            onItemClick(it)
        }

        binding.mList.apply {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(BottomItemDecorationSystem(context))
            adapter = mAdapter
        }

        binding.fragmentPtrHomePtrFrame.setPtrHandler(object : PtrHandler {
            override fun checkCanDoRefresh(p0: PtrFrameLayout?, p1: View?, p2: View?): Boolean {
                return PtrDefaultHandler.checkContentCanBePulledDown(p0, binding.mList, p2)
            }

            override fun onRefreshBegin(p0: PtrFrameLayout?) {
                getDeviceList()
            }

        })
        binding.fragmentPtrHomePtrFrame.disableWhenHorizontalMove(true)
    }

    override fun initData() {
        getDeviceList()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getDeviceList(page: Int = 1) {
        QuecDeviceService.getDeviceList(QuecDeviceListParamsModel().apply {
            this.pageNumber = page
            this.pageSize = 50
        }) {
            binding.fragmentPtrHomePtrFrame.refreshComplete()
            if (it.isSuccess) {
                if (page == 1) {
                    mDeviceList.clear()
                }
                mDeviceList.addAll(it.data.list)
                mAdapter.notifyDataSetChanged()
            } else {
                handlerError(it)
            }
        }
    }

    private fun onItemClick(device: QuecDeviceModel) {
        startActivity(Intent(context, DeviceFunctionActivity::class.java).apply {
            putExtra(QuecBaseDeviceActivity.CODE_DEVICE, device)
        })
    }
}