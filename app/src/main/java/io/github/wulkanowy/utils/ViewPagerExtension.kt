package io.github.wulkanowy.utils

import androidx.viewpager.widget.ViewPager

inline fun ViewPager.setOnSelectPageListener(crossinline selectListener: (position: Int) -> Unit) {
    addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageSelected(position: Int) {
            selectListener(position)
        }
        override fun onPageScrollStateChanged(state: Int) {}
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
    })
}
