package com.quectel.app.demo.ui.device.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.quectel.app.demo.databinding.DeviceListItemBinding
import com.quectel.basic.common.entity.QuecDeviceModel

class DeviceListAdapter(
    var list: List<QuecDeviceModel>,
    val onItemClick: (device: QuecDeviceModel) -> Unit
) :
    RecyclerView.Adapter<DeviceListAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(DeviceListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val device = list[position]
        holder.binding.tvDeviceName.text = device.deviceName
        holder.binding.tvStatus.text = if (device.onlineChannelState > 0) "在线" else "离线"
        holder.binding.tvInfo.text = "${device.productKey} - ${device.deviceKey}"

        holder.binding.root.setOnClickListener { onItemClick(device) }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    data class VH(val binding: DeviceListItemBinding) : RecyclerView.ViewHolder(binding.root)
}