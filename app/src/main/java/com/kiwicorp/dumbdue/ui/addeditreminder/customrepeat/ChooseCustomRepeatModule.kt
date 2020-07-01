package com.kiwicorp.dumbdue.ui.addeditreminder.customrepeat

import androidx.lifecycle.ViewModel
import com.kiwicorp.dumbdue.di.ViewModelKey
import com.kiwicorp.dumbdue.di.ViewModelModule
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class ChooseCustomRepeatModule {

    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    internal abstract fun chooseDailyStartDateFragment(): ChooseDailyStartDateFragment

    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    internal abstract fun chooseWeeklyStartDateFragment(): ChooseWeeklyStartDateFragment

    @Binds
    @IntoMap
    @ViewModelKey(ChooseCustomRepeatViewModel::class)
    internal abstract fun bindCustomRepeatViewModel(viewmodel: ChooseCustomRepeatViewModel): ViewModel
}