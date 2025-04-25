package com.quectel.app.demo.ui.mine

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.quectel.app.demo.adapter.MinePhotoAdapter
import com.quectel.app.demo.base.activity.QuecBaseActivity
import com.quectel.app.demo.databinding.ActivityListPhotoBinding
import com.quectel.app.usersdk.service.QuecUserService

class PhotoHeadActivity : QuecBaseActivity<ActivityListPhotoBinding>() {
    companion object {
        const val TAG = "PhotoHeadActivity"
        const val BASE_IMAGE_URL: String = "https://iot-oss.quectelcn.com/"
    }

    var mAdapter: MinePhotoAdapter? = null
    private var data = ArrayList<String>()

    /**
     * 初始化默认头像列表
     */
    fun initDefaultAvatarList() {
        data.add(BASE_IMAGE_URL + "head_1.png")
        data.add(BASE_IMAGE_URL + "head_2.png")
        data.add(BASE_IMAGE_URL + "head_3.png")
        data.add(BASE_IMAGE_URL + "head_4.png")
        data.add(BASE_IMAGE_URL + "head_5.png")
        data.add(BASE_IMAGE_URL + "head_6.png")
        data.add(BASE_IMAGE_URL + "head_7.png")
        data.add(BASE_IMAGE_URL + "head_8.png")
    }

    override fun getViewBinding(): ActivityListPhotoBinding {
        return ActivityListPhotoBinding.inflate(layoutInflater)
    }

    override fun initView(savedInstanceState: Bundle?) {
        initDefaultAvatarList()
        mAdapter = MinePhotoAdapter(this@PhotoHeadActivity, data)
        binding.rvList.setAdapter(mAdapter)
        binding.rvList.setLayoutManager(GridLayoutManager(this@PhotoHeadActivity, 4))
        mAdapter!!.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(
                adapter: BaseQuickAdapter<*, *>,
                view: View,
                position: Int,
            ) {
                val data: String =
                    adapter.data[position] as String
                selectPhotoUrl(data)
            }
        })
    }

    private fun selectPhotoUrl(string: String) {
        QuecUserService.updateUserIcon(imagePath = string) { result ->
            handlerResult(result)
            if (result.isSuccess) {
                finish()
            }
        }
    }

    override fun initData() {
    }
}