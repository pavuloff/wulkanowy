package io.github.wulkanowy.ui.modules.grade

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.modules.main.MainErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import io.reactivex.Completable
import java.util.concurrent.TimeUnit.MILLISECONDS
import javax.inject.Inject

class GradePresenter @Inject constructor(
    private val errorHandler: MainErrorHandler,
    private val schedulers: SchedulersProvider,
    private val studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val analytics: FirebaseAnalyticsHelper
) : BasePresenter<GradeView>(errorHandler) {

    var selectedIndex = 0
        private set

    private var semesters = emptyList<Semester>()

    private val loadedSemesterId = mutableMapOf<Int, Int>()

    fun onAttachView(view: GradeView, savedIndex: Int?) {
        super.onAttachView(view)
        disposable.add(Completable.timer(150, MILLISECONDS, schedulers.mainThread)
            .subscribe {
                selectedIndex = savedIndex ?: 0
                view.initView()
                loadData()
            })
    }

    fun onViewReselected() {
        view?.run { notifyChildParentReselected(currentPageIndex) }
    }

    fun onSemesterSwitch(): Boolean {
        if (semesters.isNotEmpty()) view?.showSemesterDialog(selectedIndex - 1)
        return true
    }

    fun onSemesterSelected(index: Int) {
        if (selectedIndex != index - 1) {
            selectedIndex = index + 1
            loadedSemesterId.clear()
            view?.let {
                notifyChildrenSemesterChange()
                loadChild(it.currentPageIndex)
            }
            analytics.logEvent("changed_semester", mapOf("number" to index + 1))
        }
    }

    fun onChildViewRefresh() {
        view?.let { loadChild(it.currentPageIndex, true) }
    }

    fun onChildViewLoaded(semesterId: Int) {
        view?.apply {
            showContent(true)
            showProgress(false)
            loadedSemesterId[currentPageIndex] = semesterId
        }
    }

    fun onPageSelected(index: Int) {
        loadChild(index)
    }

    private fun loadData() {
        disposable.add(studentRepository.getCurrentStudent()
            .flatMap { semesterRepository.getSemesters(it) }
            .doOnSuccess {
                it.first { item -> item.isCurrent }.also { current ->
                    selectedIndex = if (selectedIndex == 0) current.semesterName else selectedIndex
                    semesters = it.filter { semester -> semester.diaryId == current.diaryId }
                }
            }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .subscribe({ view?.run { loadChild(currentPageIndex) } }) {
                errorHandler.dispatch(it)
                view?.run {
                    showProgress(false)
                    showEmpty()
                }
            })
    }

    private fun loadChild(index: Int, forceRefresh: Boolean = false) {
        semesters.first { it.semesterName == selectedIndex }.semesterId.also {
            if (forceRefresh || loadedSemesterId[index] != it) {
                view?.notifyChildLoadData(index, it, forceRefresh)
            }
        }
    }

    private fun notifyChildrenSemesterChange() {
        for (i in 0..1) view?.notifyChildSemesterChange(i)
    }
}
