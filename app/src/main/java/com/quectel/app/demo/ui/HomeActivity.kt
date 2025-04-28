package com.quectel.app.demo.ui

import android.Manifest
import android.os.Build
import android.os.Bundle
import com.quectel.app.demo.R
import com.quectel.app.demo.databinding.ActivityHomeBinding
import com.quectel.app.demo.fragment.MainFragment
import com.quectel.app.demo.utils.MyUtils
import com.quectel.app.demo.utils.QuecPermission
import me.yokeyword.fragmentation.SupportActivity

class HomeActivity : SupportActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MyUtils.addStatusBarView(this, R.color.gray_bg)

        if (findFragment(MainFragment::class.java) == null) {
            loadRootFragment(R.id.fl_container, MainFragment.newInstance())
        }

        val permissionMediator = QuecPermission.init(this)
        permissionMediator.permissions(*permission)
            .request { _: Boolean, _: List<String?>?, _: List<String?>? -> }
    }

    companion object {
        private val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.ACCESS_FINE_LOCATION,
        ) else arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
        )
    }
}
