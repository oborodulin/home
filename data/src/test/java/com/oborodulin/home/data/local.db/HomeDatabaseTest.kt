package com.oborodulin.home.data.local.db

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.oborodulin.home.common.util.Utils
import org.junit.After
import org.junit.Before
import java.io.IOException
import java.time.OffsetDateTime

open class HomeDatabaseTest {
    protected val ctx: Context = ApplicationProvider.getApplicationContext()

    //getApplicationContext<App>()//: Context// = InstrumentationRegistry.getInstrumentation().targetContext
    protected lateinit var db: HomeDatabase
    protected val currentDateTime: OffsetDateTime = OffsetDateTime.now()
    protected val fixCurrDateTime = Utils.toOffsetDateTime("2023-03-26T03:00:00.000+03:00")

    @Before
    open fun setUp() {
        //app.setLocationProvider(mockLocationProvider)
        db = HomeDatabase.getTestInstance(this.ctx)
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        //According to Dianne Hackborn (Android framework engineer) there is no need to close the database in a content provider
        //https://stackoverflow.com/questions/14002022/android-sqlite-closed-exception/25379071#25379071
        //https://stackoverflow.com/questions/23293572/android-cannot-perform-this-operation-because-the-connection-pool-has-been-clos
        HomeDatabase.close()
    }

    fun payerDao() = db.payerDao()
    fun serviceDao() = db.serviceDao()
    fun meterDao() = db.meterDao()
    fun rateDao() = db.rateDao()
    fun receiptDao() = db.receiptDao()
}