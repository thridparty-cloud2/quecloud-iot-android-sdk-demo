package com.quectel.app.demo.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.quectel.app.demo.R
import com.quectel.app.demo.databinding.MainFragmentBinding
import com.quectel.app.demo.ui.features.FeaturesListFragment
import com.quectel.app.demo.ui.device.list.DeviceListFragment
import com.quectel.app.demo.ui.mine.MineFragment
import me.yokeyword.fragmentation.SupportFragment

class MainFragment : SupportFragment() {
    private val mFragments = arrayOfNulls<SupportFragment>(3)
    private lateinit var binding: MainFragmentBinding
    private var prePosition: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val firstFragment: SupportFragment? = findChildFragment(
            DeviceListFragment::class.java
        )
        if (firstFragment == null) {
            println("firstFragment == null")
            selectTabOne()

            mFragments[FIRST] = DeviceListFragment()
            mFragments[SECOND] = FeaturesListFragment()
            mFragments[THIRD] = MineFragment()


            loadMultipleRootFragment(
                R.id.fl_tab_container, FIRST,
                mFragments[FIRST],
                mFragments[SECOND],
                mFragments[THIRD]
            )
        } else {
            mFragments[FIRST] = firstFragment
            mFragments[SECOND] = findChildFragment(
                FeaturesListFragment::class.java
            )
            mFragments[THIRD] = findChildFragment(
                MineFragment::class.java
            )
            val prePosition = savedInstanceState?.getInt("prePosition", 0) ?: 0
            resetTab(prePosition)
        }

        initView()
    }

    private fun initView() {
        binding.apply {
            rlTab1.setOnClickListener {
                selectTabOne()
                if (prePosition != 0) {
                    showHideFragment(mFragments[0], mFragments[prePosition])
                }
                prePosition = 0
            }

            rlTab2.setOnClickListener {
                selectTabTwo()
                showHideFragment(mFragments[1], mFragments[prePosition])
                prePosition = 1
            }

            rlTab3.setOnClickListener {
                selectTabThree()
                showHideFragment(mFragments[2], mFragments[prePosition])
                prePosition = 2
            }
        }
    }

    private fun resetTab(type: Int) {
        prePosition = type
        when (type) {
            0 -> selectTabOne()
            1 -> selectTabTwo()
            2 -> selectTabThree()
        }
    }

    private fun selectTabOne() {
        binding.apply {
            ivTab1.setBackgroundResource(R.mipmap.tab_home_1)
            tvTab1.setTextColor(resources.getColor(R.color.main2, null))
            ivTab2.setBackgroundResource(R.mipmap.tab_msg_0)
            tvTab2.setTextColor(resources.getColor(R.color.main2, null))
            ivTab3.setBackgroundResource(R.mipmap.tab_my_0)
            tvTab3.setTextColor(resources.getColor(R.color.main2, null))
        }
    }

    private fun selectTabTwo() {
        binding.apply {
            ivTab1.setBackgroundResource(R.mipmap.tab_home_0)
            tvTab1.setTextColor(resources.getColor(R.color.main2, null))
            ivTab2.setBackgroundResource(R.mipmap.tab_msg_1)
            tvTab2.setTextColor(resources.getColor(R.color.main2, null))
            ivTab3.setBackgroundResource(R.mipmap.tab_my_0)
            tvTab3.setTextColor(resources.getColor(R.color.main2, null))
        }
    }

    private fun selectTabThree() {
        binding.apply {
            ivTab1.setBackgroundResource(R.mipmap.tab_home_0)
            tvTab1.setTextColor(resources.getColor(R.color.main2, null))
            ivTab2.setBackgroundResource(R.mipmap.tab_msg_0)
            tvTab2.setTextColor(resources.getColor(R.color.main2, null))
            ivTab3.setBackgroundResource(R.mipmap.tab_my_1)
            tvTab3.setTextColor(resources.getColor(R.color.main2, null))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("prePosition", prePosition)
        super.onSaveInstanceState(outState)
    }

    companion object {
        const val FIRST: Int = 0
        const val SECOND: Int = 1
        const val THIRD: Int = 2

        fun newInstance(): MainFragment {
            val args = Bundle()
            val fragment = MainFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
