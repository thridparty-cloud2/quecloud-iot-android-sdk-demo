package com.quectel.app.demo.base

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.quectel.app.demo.databinding.CommonTitleBinding

class CommonTitle(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {
    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    private lateinit var binding: CommonTitleBinding

    init {
        binding = CommonTitleBinding.inflate(
            LayoutInflater.from(context),
            this.parent as? ViewGroup,
            false
        )
    }
}