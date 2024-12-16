package com.project.weathertracker

import android.app.Application
import com.project.data.di.dataModule
import com.project.weathertracker.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class AppApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.ERROR)
            androidContext(this@AppApplication)
            modules(dataModule, viewModelModule)
        }
    }

}