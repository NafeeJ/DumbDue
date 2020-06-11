package com.kiwicorp.dumbdue.ui.settings.edittimesetbuttons.editquickaccesstimesetter

import androidx.lifecycle.ViewModel
import com.kiwicorp.dumbdue.di.ViewModelKey
import com.kiwicorp.dumbdue.di.ViewModelModule
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class EditQuickAccessTimeSetterModule {
    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    internal abstract fun editQuickAccessTimeSetterFragment(): EditQuickAccessTimeSetterFragment

    @Binds
    @IntoMap
    @ViewModelKey(EditQuickAccessTimeSetterViewModel::class)
    internal abstract fun bindEditQuickAccessTimeSetterViewModel(viewmodel: EditQuickAccessTimeSetterViewModel): ViewModel
}