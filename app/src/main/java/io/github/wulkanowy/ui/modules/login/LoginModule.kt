package io.github.wulkanowy.ui.modules.login

import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import io.github.wulkanowy.di.scopes.PerActivity
import io.github.wulkanowy.di.scopes.PerFragment
import io.github.wulkanowy.ui.base.BasePagerAdapter
import io.github.wulkanowy.ui.modules.login.form.LoginFormFragment
import io.github.wulkanowy.ui.modules.login.options.LoginOptionsFragment

@Module
internal abstract class LoginModule {

    @Module
    companion object {

        @JvmStatic
        @PerActivity
        @Provides
        fun provideLoginAdapter(activity: LoginActivity) = BasePagerAdapter(activity.supportFragmentManager)
    }

    @PerFragment
    @ContributesAndroidInjector()
    abstract fun bindLoginFormFragment(): LoginFormFragment

    @PerFragment
    @ContributesAndroidInjector()
    abstract fun bindLoginOptionsFragment(): LoginOptionsFragment
}
