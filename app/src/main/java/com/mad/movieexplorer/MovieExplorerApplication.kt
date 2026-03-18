package com.mad.movieexplorer

import android.app.Application
import com.mad.movieexplorer.worker.RentalReminderWorker

class MovieExplorerApplication : Application() {
    val appContainer: AppContainer by lazy {
        AppContainer(this)
    }

    override fun onCreate() {
        super.onCreate()
        RentalReminderWorker.schedule(this)
    }
}
