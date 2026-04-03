package com.quectel.app.demo.ui.device.add

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.quectel.app.demo.databinding.DeviceNearbyItemBinding
import com.quectel.sdk.smart.config.api.bean.QuecPairDeviceBean

class DeviceNearbyAdapter(
    val list: List<QuecPairDeviceBean>,
    val onItemClick: (bean: QuecPairDeviceBean) -> Unit
) :
    RecyclerView.Adapter<DeviceNearbyAdapter.VH>() {

    data class VH(val binding: DeviceNearbyItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            DeviceNearbyItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val bean = list[position]
        holder.binding.apply {
            tvDeviceName.text = bean.deviceName
            tvInfo.text = bean.bleDevice.productKey + " - " + bean.bleDevice.deviceKey
            when (bean.activeBindingMode) {
                QuecPairDeviceBean.ACTIVE_BINDING_MODE_BIND -> {
                    tvStatus.text = "仅蓝牙激活绑定"

                }
                QuecPairDeviceBean.ACTIVE_BINDING_MODE_BIND_AND_CONFIG -> {
                    tvStatus.text = "优先蓝牙激活绑定"
                }

                QuecPairDeviceBean.ACTIVE_BINDING_MODE_CONFIG_AND_BIND -> {
                    tvStatus.text = "优先WiFI配网激活绑定"
                }
            }

            root.setOnClickListener { onItemClick(bean) }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}