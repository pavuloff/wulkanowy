package io.github.wulkanowy.ui.modules.login

import android.content.res.Resources
import android.database.sqlite.SQLiteConstraintException
import io.github.wulkanowy.R
import io.github.wulkanowy.api.login.BadCredentialsException
import io.github.wulkanowy.data.ErrorHandler
import javax.inject.Inject

class LoginErrorHandler @Inject constructor(resources: Resources) : ErrorHandler(resources) {

    var onBadCredentials: () -> Unit = {}

    var onStudentDuplicate: (String) -> Unit = {}

    override fun proceed(error: Throwable) {
        when (error) {
            is BadCredentialsException -> onBadCredentials()
            is SQLiteConstraintException -> onStudentDuplicate(resources.getString(R.string.login_duplicate_student))
            else -> super.proceed(error)
        }
    }

    override fun clear() {
        super.clear()
        onBadCredentials = {}
    }
}
