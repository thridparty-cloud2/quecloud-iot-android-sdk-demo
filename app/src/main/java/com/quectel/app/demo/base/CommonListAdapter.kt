package com.quectel.app.demo.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.quectel.app.demo.databinding.CommonListItemBinding
import com.quectel.app.demo.widget.BottomItemDecorationSystem

class CommonListAdapter(
    private val list: List<Item>,
    private val onItemClick: (position: Int) -> Unit,
) : RecyclerView.Adapter<CommonListAdapter.VH>() {

    data class Item(
        var content: String,
        var hint: String?,
        var status: String?,
    )

    data class VH(val binding: CommonListItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(CommonListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = list[position]
        holder.binding.apply {
            tvContent.text = item.content

            if (item.hint != null) {
                tvHint.visibility = View.VISIBLE
                tvHint.text = item.hint
            } else {
                tvHint.visibility = View.GONE
            }

            if (item.status != null) {
                tvStatus.visibility = View.VISIBLE
                tvStatus.text = item.status
            } else {
                tvStatus.visibility = View.GONE
            }

            root.setOnClickListener { onItemClick(position) }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    companion object {
        fun init(
            recyclerView: RecyclerView,
            list: List<Item>,
            onItemClick: (position: Int) -> Unit
        ): CommonListAdapter {
            recyclerView.layoutManager = LinearLayoutManager(recyclerView.context)
            val instance = CommonListAdapter(list, onItemClick)
            recyclerView.addItemDecoration(BottomItemDecorationSystem(recyclerView.context))
            recyclerView.adapter = instance

            return instance
        }
    }
}