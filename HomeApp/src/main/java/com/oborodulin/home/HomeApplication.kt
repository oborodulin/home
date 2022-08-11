package com.oborodulin.home

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.work.Configuration
import com.oborodulin.home.di.AppInjector
import com.oborodulin.home.accounting.AccountingRepository
//import com.oborodulin.home.domain.rate.RateRepository
//import com.oborodulin.home.domain.service.ServiceRepository
import dagger.hilt.android.HiltAndroidApp

private const val TAG = "home.app"

//@HiltAndroidApp
class HomeApplication : Application(), Configuration.Provider {
    init {
        app = this
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "HomeApp version ${BuildConfig.VERSION_NAME} is starting")

        initialiseDagger()

        AccountingRepository.initialize(this)
/*
        ServiceRepository.initialize(this)
        RateRepository.initialize(this)
 */
    }

    private fun initialiseDagger() {
        AppInjector.initialise(this)
    }

    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder()
            .setMinimumLoggingLevel(if (BuildConfig.DEBUG) android.util.Log.VERBOSE else android.util.Log.ERROR)
            .build()

    companion object {
        private lateinit var app: HomeApplication
        fun getAppContext(): Context = app.applicationContext
    }

}