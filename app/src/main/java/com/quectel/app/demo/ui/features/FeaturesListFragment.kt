package com.quectel.app.demo.ui.features

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.quectel.app.demo.R
import com.quectel.app.demo.base.fragment.QuecBaseFragment
import com.quectel.app.demo.databinding.ActivityListFeaturesBinding
import com.quectel.app.demo.dialog.CommonDialog
import com.quectel.app.demo.ui.device.automate.AutoMateListActivity
import com.quectel.app.demo.ui.device.group.DeviceGroupActivity
import com.quectel.app.demo.ui.device.scene.DeviceSceneActivity
import com.quectel.app.demo.ui.family.list.FamilyListActivity
import com.quectel.basic.common.utils.QuecFamilyUtil

class FeaturesListFragment : QuecBaseFragment<ActivityListFeaturesBinding>() {
    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): ActivityListFeaturesBinding {
        return ActivityListFeaturesBinding.inflate(layoutInflater)
    }

    override fun initView(savedInstanceState: Bundle?) {
        addItem(getString(R.string.device_grouping)) { openDeviceGroup() }
        addItem(getString(R.string.automation_label)) { openAutoMateList() }
        addItem(getString(R.string.scene_label)) { openScene() }
        addItem(getString(R.string.family_management)) {
            if (QuecFamilyUtil.getFamilyMode()) {
                startActivity(Intent(activity, FamilyListActivity::class.java))
            } else {
                CommonDialog.showSimpleInfo(context, getString(R.string.quec_reminder), getString(R.string.please_open_home_mode))
            }
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