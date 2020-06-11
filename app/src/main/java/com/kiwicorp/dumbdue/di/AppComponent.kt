package com.kiwicorp.dumbdue.di

import android.content.Context
import com.kiwicorp.dumbdue.DumbDueApplication
import com.kiwicorp.dumbdue.notifications.BroadcastReceiverModule
import com.kiwicorp.dumbdue.ui.addeditreminder.AddEditReminderModule
import com.kiwicorp.dumbdue.ui.settings.edittimesetbuttons.EditTimeSetButtonsModule
import com.kiwicorp.dumbdue.ui.reminders.RemindersModule
import com.kiwicorp.dumbdue.ui.settings.edittimesetbuttons.editincrementaltimesetter.EditIncrementalTimeSetterModule
import com.kiwicorp.dumbdue.ui.settings.edittimesetbuttons.editquickaccesstimesetter.EditQuickAccessTimeSetterModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

/**
 * Main component for the application
 */
@Singleton
@Component(modules = [
    ApplicationModule::class,
    AndroidSupportInjectionModule::class,
    AddEditReminderModule::class,
    RemindersModule::class,
    EditTimeSetButtonsModule::class,
    EditIncrementalTimeSetterModule::class,
    EditQuickAccessTimeSetterModule::class,
    BroadcastReceiverModule::class
])
interface AppComponent : AndroidInjector<DumbDueApplication> {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): AppComponent
    }

}