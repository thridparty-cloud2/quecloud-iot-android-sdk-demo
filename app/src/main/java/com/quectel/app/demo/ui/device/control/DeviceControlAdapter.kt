package com.quectel.app.demo.ui.device.control

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.quectel.app.demo.databinding.ItemControlLayoutBinding
import com.quectel.app.device.bean.QuecProductTSLPropertyModel
import com.quectel.sdk.iot.channel.kit.model.QuecIotDataPointsModel.DataModel.QuecIotDataPointDataType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DeviceControlAdapter(
    var list: List<QuecProductTSLPropertyModel<*>>,
    val onItemClick: (item: QuecProductTSLPropertyModel<*>) -> Unit
) : RecyclerView.Adapter<DeviceControlAdapter.VH>() {
    private val sDataFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)


    data class VH(val binding: ItemControlLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            ItemControlLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = list[position]
        holder.binding.tvName.text = getItemInfo(item, true)
        holder.binding.root.setOnClickListener { onItemClick(item) }
    }

    private fun getItemInfo(item: QuecProductTSLPropertyModel<*>, isTop: Boolean): String {
        val state = StringBuilder()
        if (item.subType != null) {
            state.append("读写类型: ").append(item.subType)
        }
        state.append(" 数据类型: ").append(item.dataType)
        state.append("\nid: ").append(item.id)
        state.append(" ,code: ").append(item.code)
        state.append("\nname: ").append(item.name)
        if (isTop) {
            state.append(" ,sort: ").append(item.sort)
        }

        state.append("\nvalue: ")

        val attributeValue = item.attributeValue
        if (item.dataType == QuecIotDataPointDataType.STRUCT) {
            if (item.specs != null && item.specs.isNotEmpty()) {
                item.specs.forEach {
                    if (it is QuecProductTSLPropertyModel<*>) {
                        state.append("\n").append(getItemInfo(it, false)).append("\n")
                    }
                }
            } else {
                state.append("无数据")
            }
        } else if (attributeValue == null) {
            state.append("无数据")
        } else if (attributeValue is ArrayList<*>) {
            attributeValue.forEach {
                state.append("\n").append(it.toString())
            }
        } else {
            if (item.dataType == QuecIotDataPointDataType.DATE && attributeValue is String) {
                state.append(sDataFormat.format(Date(attributeValue.toLong())))
            } else {
                state.append(attributeValue.toString())
            }
        }

        return state.toString()
    }

    override fun getItemCount(): Int {
        return list.size
    }
}