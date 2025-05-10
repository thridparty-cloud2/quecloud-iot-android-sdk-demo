package com.quectel.app.demo.ui.features

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.quectel.app.demo.base.fragment.QuecBaseFragment
import com.quectel.app.demo.databinding.ActivityListFeaturesBinding
import com.quectel.app.demo.ui.device.automate.AutoMateListActivity
import com.quectel.app.demo.ui.device.group.DeviceGroupActivity
import com.quectel.app.demo.ui.device.scene.DeviceSceneActivity
import com.quectel.app.demo.ui.family.list.FamilyListActivity

class FeaturesListFragment : QuecBaseFragment<ActivityListFeaturesBinding>() {
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): ActivityListFeaturesBinding {
        return ActivityListFeaturesBinding.inflate(layoutInflater)
    }

    override fun initView(savedInstanceState: Bundle?) {
        addItem("设备分组") { openDeviceGroup() }
        addItem("群组") { }
        addItem("自动化") { openAutoMateList() }
        addItem("场景") { openScene() }
        addItem("家庭管理") {
            startActivity(Intent(activity, FamilyListActivity::class.java))
        }
    }

    private fun openDeviceGroup() {
        startActivity(Intent(activity, DeviceGroupActivity::class.java))
    }

    private fun openScene() {
        startActivity(Intent(activity, DeviceSceneActivity::class.java))
    }

    private fun openAutoMateList() {
        startActivity(Intent(activity, AutoMateListActivity::class.java))
    }

    override fun initData() {
    }
}