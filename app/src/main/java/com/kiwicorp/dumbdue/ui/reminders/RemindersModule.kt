package com.kiwicorp.dumbdue.ui.reminders

import androidx.lifecycle.ViewModel
import com.kiwicorp.dumbdue.di.ViewModelKey
import com.kiwicorp.dumbdue.di.ViewModelModule
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class RemindersModule {
    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    internal abstract fun remindersFragment(): RemindersFragment

    @Binds
    @IntoMap
    @ViewModelKey(RemindersViewModel::class)
    internal abstract fun bindRemindersViewModel(viewmodel: RemindersViewModel): ViewModel
}