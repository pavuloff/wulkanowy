package io.github.wulkanowy.ui.modules.login.options

import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.BaseFragment
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.utils.setOnItemClickListener
import kotlinx.android.synthetic.main.fragment_login_options.*
import javax.inject.Inject

class LoginOptionsFragment : BaseFragment(), LoginOptionsView {

    @Inject
    lateinit var presenter: LoginOptionsPresenter

    @Inject
    lateinit var loginAdapter: FlexibleAdapter<AbstractFlexibleItem<*>>

    companion object {
        fun newInstance() = LoginOptionsFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login_options, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.onAttachView(this)
    }

    override fun initView() {
        loginAdapter.apply { setOnItemClickListener { presenter.onItemSelected(it) } }

        loginOptionsRecycler.apply {
            adapter = loginAdapter
            layoutManager = SmoothScrollLinearLayoutManager(context)
        }
    }

    fun onParentLoadData() {
        presenter.onParentViewLoadData()
    }

    override fun updateData(data: List<LoginOptionsItem>) {
        loginAdapter.updateDataSet(data, true)
    }

    override fun openMainView() {
        activity?.let {
            startActivity(MainActivity.getStartIntent(it)
                .apply { addFlags(FLAG_ACTIVITY_CLEAR_TASK or FLAG_ACTIVITY_NEW_TASK) })
        }
    }

    override fun showProgress(show: Boolean) {
        loginOptionsProgressContainer.visibility = if (show) VISIBLE else GONE
    }

    override fun showContent(show: Boolean) {
        loginOptionsRecycler.visibility = if (show) VISIBLE else GONE
    }

    override fun showActionBar(show: Boolean) {
        (activity as? AppCompatActivity)?.supportActionBar?.run { if (show) show() else hide() }
    }

    override fun onDestroyView() {
        presenter.onDetachView()
        super.onDestroyView()
    }
}
