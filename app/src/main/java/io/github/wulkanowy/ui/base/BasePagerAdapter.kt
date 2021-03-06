package io.github.wulkanowy.ui.base

import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class BasePagerAdapter(fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {

    val fragments = mutableMapOf<String?, Fragment>()

    val registeredFragments = mutableMapOf<Int, Fragment>()

    override fun getItem(position: Int) = fragments.values.elementAt(position)

    override fun getCount() = fragments.size

    override fun getPageTitle(position: Int): CharSequence? {
        return fragments.keys.elementAtOrNull(position)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        return super.instantiateItem(container, position).also {
            registeredFragments[position] = it as Fragment
        }
    }

    override fun destroyItem(container: ViewGroup, position: Int, fragment: Any) {
        registeredFragments.remove(position)
        super.destroyItem(container, position, fragment)
    }
}
