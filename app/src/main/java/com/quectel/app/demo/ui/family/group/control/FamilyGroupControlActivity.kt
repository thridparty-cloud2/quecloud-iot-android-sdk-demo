package com.quectel.app.demo.ui.family.group.control

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.quectel.app.demo.R
import com.quectel.app.demo.databinding.ActivityCommonRvListBinding
import com.quectel.app.demo.dialog.EditTextPopup
import com.quectel.app.demo.dialog.SelectItemDialog
import com.quectel.app.demo.ui.device.control.DeviceControlAdapter
import com.quectel.app.demo.ui.family.BaseFamilyActivity
import com.quectel.app.demo.ui.family.group.function.FamilyGroupFunctionActivity.Companion.KEY_DEVICE
import com.quectel.app.device.bean.BooleanSpecs
import com.quectel.app.device.bean.NumSpecs
import com.quectel.app.device.bean.QuecProductTSLPropertyModel
import com.quectel.app.device.bean.TextSpecs
import com.quectel.basic.common.entity.QuecDeviceModel
import com.quectel.sdk.group.service.QuecGroupService
import com.quectel.sdk.iot.channel.kit.model.QuecIotDataPointsModel
import com.quectel.sdk.iot.channel.kit.model.QuecIotDataPointsModel.DataModel.QuecIotDataPointDataType
import java.util.Calendar

class FamilyGroupControlActivity : BaseFamilyActivity<ActivityCommonRvListBinding>() {
    private lateinit var device: QuecDeviceModel
    private var itemList = ArrayList<QuecProductTSLPropertyModel<*>>()
    private lateinit var adapter: DeviceControlAdapter

    private val dataTypeMap = mapOf(
        QuecIotDataPointDataType.BOOL to QuecIotDataPointDataType.BOOL_NUM,
        QuecIotDataPointDataType.DATE to QuecIotDataPointDataType.DATE_NUM,
        QuecIotDataPointDataType.DOUBLE to QuecIotDataPointDataType.DOUBLE_NUM,
        QuecIotDataPointDataType.FLOAT to QuecIotDataPointDataType.FLOAT_NUM,
        QuecIotDataPointDataType.ARRAY to QuecIotDataPointDataType.ARRAY_NUM,
        QuecIotDataPointDataType.ENUM to QuecIotDataPointDataType.ENUM_NUM,
        QuecIotDataPointDataType.INT to QuecIotDataPointDataType.INT_NUM,
        QuecIotDataPointDataType.TEXT to QuecIotDataPointDataType.TEXT_NUM,
        QuecIotDataPointDataType.STRUCT to QuecIotDataPointDataType.STRUCT_NUM
    )

    override fun getViewBinding(): ActivityCommonRvListBinding {
        return ActivityCommonRvListBinding.inflate(layoutInflater)
    }

    override fun initView(savedInstanceState: Bundle?) {
        binding.apply {
            adapter = DeviceControlAdapter(itemList) { showControlDialog(it) }
            rvList.adapter = adapter
            rvList.layoutManager = LinearLayoutManager(mContext)
            rvList.addItemDecoration(
                DividerItemDecoration(
                    mContext,
                    DividerItemDecoration.VERTICAL
                )
            )
        }
    }

    override fun initData() {
        val bean = intent.getSerializableExtra(KEY_DEVICE) as? QuecDeviceModel
        if (bean == null) {
            finish()
            return
        }

        device = bean
        binding.title.text = device.deviceName

        getTsl()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getTsl() {
        showOrHideLoading(true)
        QuecGroupService.groupAttributesWithTSL(device.gdid, device.productKey, null) {
            showOrHideLoading(false)
            if (it.isSuccess) {
                itemList.clear()

                it.data?.forEach { item ->
                    itemList.add(item)
                }
                adapter.notifyDataSetChanged()
            } else {
                handlerError(it)
            }
        }
    }

    private fun showControlDialog(item: QuecProductTSLPropertyModel<*>) {
        if (!item.subType.contains("W")) {
            showMessage(getString(R.string.tsl_not_support_control))
            return
        }
        when (item.dataType) {
            QuecIotDataPointDataType.BOOL -> {
                val specs = item.specs
                if (specs is ArrayList<*>) {
                    SelectItemDialog(mContext).apply {
                        specs.forEach {
                            if (it is BooleanSpecs) {
                                addItem("[${it.name}] ${it.value}") {
                                    writeDps(item, it.value)
                                }
                            }
                        }
                    }.show()
                } else {
                    showMessage(getString(R.string.data_exception))
                }
            }

            QuecIotDataPointDataType.TEXT -> {
                val specs = item.specs
                if (specs is ArrayList<*> && specs.isNotEmpty()) {
                    val info = specs[0]
                    if (info is TextSpecs) {
                        EditTextPopup(mContext).apply {
                            setTitle(getString(R.string.hint_input_content_with_limit, info.length.toIntOrNull() ?: 0))
                            if (item.attributeValue != null) {
                                setContent(item.attributeValue.toString())
                            }
                            setEditTextListener {
                                if (it.length > (info.length.toIntOrNull() ?: Int.MAX_VALUE)) {
                                    showMessage(getString(R.string.input_too_long, info.length.toIntOrNull() ?: 0))
                                } else {
                                    writeDps(item, it)
                                }
                            }
                        }.showPopupWindow()
                    } else {
                        showMessage(getString(R.string.data_exception))
                    }
                } else {
                    showMessage(getString(R.string.data_exception))
                }
            }

            QuecIotDataPointDataType.ENUM -> {
                val specs = item.specs
                if (specs is ArrayList<*>) {
                    SelectItemDialog(mContext).apply {
                        specs.forEach {
                            if (it is BooleanSpecs) {
                                addItem("[${it.name}] ${it.value}") {
                                    writeDps(item, it.value)
                                }
                            }
                        }
                    }.show()
                } else {
                    showMessage(getString(R.string.data_exception))
                }
            }

            QuecIotDataPointDataType.INT, QuecIotDataPointDataType.FLOAT, QuecIotDataPointDataType.DOUBLE -> {
                val specs = item.specs
                if (specs is ArrayList<*> && specs.isNotEmpty()) {
                    val info = specs[0]
                    if (info is NumSpecs) {
                        EditTextPopup(mContext).apply {
                            setTitle(getString(R.string.hint_input_range, item.dataType, info.min, info.max))
                            if (item.attributeValue != null) {
                                setContent(item.attributeValue.toString())
                            }
                            setEditTextListener {
                                dismiss()
                                when (item.dataType) {
                                    QuecIotDataPointDataType.INT -> if (it.toIntOrNull() != null) writeDps(
                                        item,
                                        it.toLong()
                                    ) else showMessage(getString(R.string.hint_input_int))

                                    QuecIotDataPointDataType.FLOAT -> if (it.toFloatOrNull() != null) writeDps(
                                        item,
                                        it.toDouble()
                                    ) else showMessage(getString(R.string.hint_input_float))

                                    QuecIotDataPointDataType.DOUBLE -> if (it.toDoubleOrNull() != null) writeDps(
                                        item,
                                        it.toDouble()
                                    ) else showMessage(getString(R.string.hint_input_double))
                                }
                            }
                        }.showPopupWindow()
                    } else {
                        showMessage(getString(R.string.data_exception))
                    }
                } else {
                    showMessage(getString(R.string.data_exception))
                }
            }

            QuecIotDataPointDataType.DATE -> {
                showDateTimePicker {
                    val date = it.time.time
                    writeDps(item, date)
                }
            }

            else -> showMessage(getString(R.string.type_not_support_control))
        }
    }

    private fun writeDps(item: QuecProductTSLPropertyModel<*>, input: Any) {
        QuecGroupService.controlGroupByHttp(listOf(QuecIotDataPointsModel.DataModel().apply {
            id = item.id
            code = item.code
            dataType = dataTypeMap[item.dataType]
            value = input
        }), device.gdid, null) {
            handlerResult(it)

            if (it.isSuccess) {
                val index = itemList.indexOf(item)
                item.attributeValue = input
                adapter.notifyItemChanged(index)
            }
        }
    }

    private fun showDateTimePicker(onDateTimeSelected: (Calendar) -> Unit) {
        val currentDate = Calendar.getInstance()

        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                // 日期选完后，继续弹时间选择器
                TimePickerDialog(
                    this,
                    { _, hourOfDay, minute ->
                        val selectedDateTime = Calendar.getInstance()
                        selectedDateTime.set(year, month, dayOfMonth, hourOfDay, minute)
                        onDateTimeSelected(selectedDateTime)
                    },
                    currentDate.get(Calendar.HOUR_OF_DAY),
                    currentDate.get(Calendar.MINUTE),
                    true // 24小时制
                ).show()
            },
            currentDate.get(Calendar.YEAR),
            currentDate.get(Calendar.MONTH),
            currentDate.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

}