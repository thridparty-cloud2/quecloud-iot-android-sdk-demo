package com.quectel.app.demo.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.ArrayAdapter
import com.quectel.app.demo.R
import com.quectel.app.demo.databinding.SelectItemDialogBinding

class SelectItemDialog(context: Context) : Dialog(context, R.style.quec_basic_ui_MyDialog) {
    private lateinit var binding: SelectItemDialogBinding
    private val items = ArrayList<Item>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SelectItemDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.lvList.adapter =
            ArrayAdapter(context, R.layout.custom_simple_list_item_1, items.map { it.title })
        binding.lvList.setOnItemClickListener { _, _, position, _ ->
            items[position].block()
            dismiss()
        }
    }

    fun addItem(title: String, block: () -> Unit) {
        items.add(Item(title, block))
    }

    data class Item(
        val title: String,
        val block: () -> Unit,
    )
}