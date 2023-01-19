package com.oborodulin.home

//import com.oborodulin.home.domain.rate.RateRepository
//import com.oborodulin.home.domain.service.ServiceRepository
import android.app.Application
import android.content.Context
import androidx.work.Configuration
import com.oborodulin.home.common.util.Constants
import com.oborodulin.home.common.util.ReleaseTree
import com.oborodulin.home.common.util.ResourceUtils
import com.oborodulin.home.common.util.setLocale
import com.oborodulin.home.data.local.db.HomeDatabase
import com.oborodulin.home.data.util.dbVersion
import com.oborodulin.home.di.ApplicationInjector
import com.orhanobut.logger.FormatStrategy
import com.orhanobut.logger.PrettyFormatStrategy
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.util.*

private const val TAG = "HomeApp"

@HiltAndroidApp
class HomeApplication : Application(), Configuration.Provider {
    init {
        app = this
    }

    /**
     *
     */
    override fun onCreate() {
        super.onCreate()
        setLocale(locale = Locale(Constants.LANGUAGE_RU))

        val logFormatStrategy: FormatStrategy =
            PrettyFormatStrategy.newBuilder().showThreadInfo(true).methodCount(1).methodOffset(5)
                .tag(TAG)
                .build()
        //Logger.addLogAdapter(AndroidLogAdapter(logFormatStrategy))

        if (BuildConfig.DEBUG) {
            Timber.plant(object : Timber.DebugTree() {
                /*
                   @Override
                   override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                       Logger.log(priority, tag, message, t)
                   }
   */
                @Override
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
        val languages =
            ResourceUtils.getHashMapResource(this, com.oborodulin.home.common.R.xml.languages)
        Timber.tag(TAG)
            .i(
                "Version %s is starting [%s]. Database v.%s",
                BuildConfig.VERSION_NAME,
                Locale.getDefault().language,
                dbVersion()
            )
        Timber.tag(TAG)
            .i(
                "Framework (API %s) SQLite version: %s",
                android.os.Build.VERSION.SDK_INT,
                HomeDatabase.sqliteVersion()
            )
        initialiseDagger()
        Timber.tag(TAG).i("Initialized")
    }

    private fun initialiseDagger() {
        ApplicationInjector.initialise(this)
        Timber.tag(TAG).i("Initialise Dagger")
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