package com.kiwicorp.dumbdue.ui.addeditreminder

import androidx.lifecycle.ViewModel
import com.kiwicorp.dumbdue.di.ViewModelKey
import com.kiwicorp.dumbdue.di.ViewModelModule
import com.kiwicorp.dumbdue.ui.addeditreminder.customrepeat.ChooseCustomRepeatFragment
import com.kiwicorp.dumbdue.ui.addeditreminder.customrepeat.ChooseDailyStartDateFragment
import com.kiwicorp.dumbdue.ui.addeditreminder.customrepeat.ChooseWeeklyStartDateFragment
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class AddEditReminderModule {

    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    internal abstract fun editReminderFragment(): EditReminderFragment

    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    internal abstract fun addReminderFragment(): AddReminderFragment

    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    internal abstract fun chooseRepeatFragment(): ChooseRepeatFragment

    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    internal abstract fun chooseCustomRepeatFragment(): ChooseCustomRepeatFragment

    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    internal abstract fun chooseAutoSnoozeFragment(): ChooseAutoSnoozeFragment

    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    internal abstract fun timePickerFragment(): TimePickerFragment

    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    internal abstract fun chooseDailyStartDateFragment(): ChooseDailyStartDateFragment

    @ContributesAndroidInjector(modules = [ViewModelModule::class])
    internal abstract fun chooseWeeklyStartDateFragment(): ChooseWeeklyStartDateFragment

    @Binds
    @IntoMap
    @ViewModelKey(AddEditReminderViewModel::class)
    internal abstract fun bindAddEditViewModel(viewmodel: AddEditReminderViewModel): ViewModel
}