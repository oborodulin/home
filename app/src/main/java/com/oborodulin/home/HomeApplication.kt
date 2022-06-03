package com.oborodulin.home

import android.app.Application
import androidx.work.Configuration
import com.oborodulin.home.domain.payer.PayerRepository
import com.oborodulin.home.domain.rate.RateRepository
import com.oborodulin.home.domain.service.ServiceRepository
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HomeApplication : Application(), Configuration.Provider {
    override fun onCreate() {
        super.onCreate()
        PayerRepository.initialize(this)
        ServiceRepository.initialize(this)
        RateRepository.initialize(this)
    }

    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder()
            .setMinimumLoggingLevel(if (BuildConfig.DEBUG) android.util.Log.DEBUG else android.util.Log.ERROR)
            .build()
}