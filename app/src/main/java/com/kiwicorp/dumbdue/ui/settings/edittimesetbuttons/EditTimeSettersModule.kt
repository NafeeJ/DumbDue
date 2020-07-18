package com.kiwicorp.dumbdue.ui.settings.edittimesetbuttons

import androidx.lifecycle.ViewModel
import com.kiwicorp.dumbdue.di.ViewModelKey
import com.kiwicorp.dumbdue.di.ViewModelModule
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class EditTimeSettersModule {
    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    internal abstract fun editTimeSetButtonsFragment(): EditTimeSettersFragment

    @Binds
    @IntoMap
    @ViewModelKey(EditTimeSettersViewModel::class)
    internal abstract fun bindEditTimeSetButtonsViewModel(viewmodel: EditTimeSettersViewModel): ViewModel
}