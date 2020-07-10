package com.kiwicorp.dumbdue.notifications

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class BroadcastReceiverModule {
    @ContributesAndroidInjector
    internal abstract fun notificationBroadcastReceiver(): NotificationBroadcastReceiver

    @ContributesAndroidInjector
    internal abstract fun bootReceiver(): BootReceiver
}