package com.kiwicorp.dumbdue.ui.settings.edittimesetbuttons.editincrementaltimesetter

import androidx.lifecycle.ViewModel
import com.kiwicorp.dumbdue.di.ViewModelKey
import com.kiwicorp.dumbdue.di.ViewModelModule
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class EditIncrementalTimeSetterModule {
    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    internal abstract fun editIncrementalTimeSetterFragment(): EditIncrementalTimeSetterFragment

    @Binds
    @IntoMap
    @ViewModelKey(EditIncrementalTimeSetterViewModel::class)
    internal abstract fun bindEditIncrementalTimeSetterViewModel(viewmodel: EditIncrementalTimeSetterViewModel): ViewModel
}