package com.kiwicorp.dumbdue.ui.settings.edittimesetbuttons

import androidx.lifecycle.ViewModel
import com.kiwicorp.dumbdue.di.ViewModelKey
import com.kiwicorp.dumbdue.di.ViewModelModule
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class EditTimeSetButtonsModule {
    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    internal abstract fun editTimeSetButtonsFragment(): EditTimeSetButtonsFragment

    @Binds
    @IntoMap
    @ViewModelKey(EditTimeSetButtonsViewModel::class)
    internal abstract fun bindEditTimeSetButtonsViewModel(viewmodel: EditTimeSetButtonsViewModel): ViewModel
}