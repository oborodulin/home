package com.oborodulin.home.di

import android.app.Application
import com.oborodulin.mobile.core.di.CoreInjector

object AppInjector {
    fun initialise(application: Application) {
        initialiseCore(application)
    }

    private fun initialiseCore(application: Application) {
        CoreInjector.initialise(application)
    }
}