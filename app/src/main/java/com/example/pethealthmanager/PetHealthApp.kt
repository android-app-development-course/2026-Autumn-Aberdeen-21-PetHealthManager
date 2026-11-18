package com.example.pethealthmanager

import android.app.Application
import com.example.pethealthmanager.notifications.NotificationChannels

class PetHealthApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        NotificationChannels.ensure(this)
    }
}
