package com.kiwicorp.dumbdue.di

import android.content.Context
import com.kiwicorp.dumbdue.DumbDueApplication
import com.kiwicorp.dumbdue.ui.addeditreminder.AddEditReminderModule
import com.kiwicorp.dumbdue.ui.reminders.RemindersModule
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
    RemindersModule::class
])
interface AppComponent : AndroidInjector<DumbDueApplication> {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance applicationContext: Context): AppComponent
    }

}