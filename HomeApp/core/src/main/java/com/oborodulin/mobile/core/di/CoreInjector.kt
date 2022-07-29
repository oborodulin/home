package com.oborodulin.mobile.core.di

import android.app.Application

object CoreInjector {
    lateinit var coreComponent: CoreComponent
    fun initialise(application: Application) {
        coreComponent = DaggerCoreComponent.builder().coreModule(CoreModule(application)).build()
    }
}