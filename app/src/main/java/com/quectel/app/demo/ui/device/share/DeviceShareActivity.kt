package com.quectel.app.demo.ui.device.share

import android.os.Bundle
import android.widget.ArrayAdapter
import com.quectel.app.demo.R
import com.quectel.app.demo.base.activity.QuecBaseDeviceActivity
import com.quectel.app.demo.databinding.ActivityDeviceShareBinding
import com.quectel.app.demo.dialog.CommonDialog
import com.quectel.app.demo.dialog.SelectItemDialog
import com.quectel.app.device.bean.QuecShareUserModel
import com.quectel.app.device.deviceservice.QuecDeviceShareService

class DeviceShareActivity : QuecBaseDeviceActivity<ActivityDeviceShareBinding>() {
    override fun getViewBinding(): ActivityDeviceShareBinding {
        return ActivityDeviceShareBinding.inflate(layoutInflater)
    }

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initData() {
        getShareList(false)
    }

    override fun initTestItem() {
        addItem(getString(R.string.get_share_list)) {
            getShareList(true)
        }
        addItem(getString(R.string.create_share_code)) {
            //30分钟的有效期
            val time = 30 * 60 * 1000 + System.currentTimeMillis()
            QuecDeviceShareService.setShareInfoByOwner(
                device.deviceKey,
                device.productKey,
                time, 1, true, 0
            ) {
                handlerResult(it)
                if (it.isSuccess) {
                    CommonDialog.showSimpleInfo(this, getString(R.string.share_code_label), it.data)
                }
            }
        }
    }

    private fun getShareList(isShowResult: Boolean) {
        QuecDeviceShareService.getDeviceShareUserList(device.deviceKey, device.productKey) {
            if (it.isSuccess) {
                if (isShowResult) handlerResult(it)

                binding.lvUserList.adapter =
                    ArrayAdapter(this, android.R.layout.simple_list_item_1, it.data.map { item ->
                        item.userInfo?.phone ?: item.userInfo?.email ?: "unknown name"
                    })
                binding.lvUserList.setOnItemClickListener { _, _, position, _ ->
                    showShareDialog(it.data[position])
                }
            } else {
                handlerError(it)
            }
        }
    }

    private fun showShareDialog(mode: QuecShareUserModel) {
        SelectItemDialog(this).apply {
            addItem(getString(R.string.cancel_share)) {
                QuecDeviceShareService.unShareDeviceByOwner(mode.shareInfo?.shareCode ?: "") {
                    handlerResult(it)
                    if (it.isSuccess) {
                        getShareList(false)
                    }
                }
            }
        }.show()
    }
}