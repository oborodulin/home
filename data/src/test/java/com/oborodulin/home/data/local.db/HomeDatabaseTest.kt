package com.oborodulin.home.data.local.db

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Before
import java.io.IOException

open class HomeDatabaseTest {
    protected val appContext = ApplicationProvider.getApplicationContext<Context>()

    //getApplicationContext<App>()//: Context// = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var database: HomeDatabase

    @Before
    open fun setUp() {
        //app.setLocationProvider(mockLocationProvider)
        database = HomeDatabase.getTestInstance(this.appContext)
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        //According to Dianne Hackborn (Android framework engineer) there is no need to close the database in a content provider
        //https://stackoverflow.com/questions/14002022/android-sqlite-closed-exception/25379071#25379071
        //https://stackoverflow.com/questions/23293572/android-cannot-perform-this-operation-because-the-connection-pool-has-been-clos
        HomeDatabase.close()
    }

    fun payerDao() = database.payerDao()
    fun serviceDao() = database.serviceDao()
    fun meterDao() = database.meterDao()
    fun rateDao() = database.rateDao()

}