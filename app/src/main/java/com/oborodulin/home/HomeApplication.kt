package com.oborodulin.home

import android.app.Application
import android.content.Context
import androidx.work.Configuration
import com.oborodulin.home.di.AppInjector
import com.oborodulin.home.accounting.payer.PayerRepository
import com.oborodulin.home.common.util.ReleaseTree
//import com.oborodulin.home.domain.rate.RateRepository
//import com.oborodulin.home.domain.service.ServiceRepository
import timber.log.Timber

//@HiltAndroidApp
class HomeApplication : Application(), Configuration.Provider {
    init {
        app = this
    }

    /**
     *
     */
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(object : Timber.DebugTree() {
                override fun createStackElementTag(element: StackTraceElement): String? {
                    return String.format(
                        "Class:%s: Line: %s, Method: %s",
                        super.createStackElementTag(element),
                        element.lineNumber,
                        element.methodName
                    )
                }
            })
        } else {
            Timber.plant(ReleaseTree())
        }
        Timber.i("Home application version ${BuildConfig.VERSION_NAME} is starting")

        initialiseDagger()

        PayerRepository.initialize(this)
/*
        ServiceRepository.initialize(this)
        RateRepository.initialize(this)
 */
        Timber.i("Home application initialized")
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