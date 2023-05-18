package com.oborodulin.home.data.local

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import com.oborodulin.home.common.util.Utils
import com.oborodulin.home.data.local.db.HomeDatabase
import com.oborodulin.home.data.local.db.dao.*
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import java.io.IOException
import java.time.OffsetDateTime
import javax.inject.Inject

@HiltAndroidTest
open class HomeDatabaseTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    protected val ctx: Context = ApplicationProvider.getApplicationContext()
    protected val currentDateTime: OffsetDateTime = OffsetDateTime.now()
    protected val fixCurrDateTime = Utils.toOffsetDateTime("2023-03-26T03:00:00.000+03:00")

    //getApplicationContext<App>()//: Context// = InstrumentationRegistry.getInstrumentation().targetContext
    @Inject
    lateinit var db: HomeDatabase

    @Before
    open fun setUp() {
        hiltRule.inject()
        //app.setLocationProvider(mockLocationProvider)
        //db = HomeDatabase.getTestInstance(this.ctx)
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        //According to Dianne Hackborn (Android framework engineer) there is no need to close the database in a content provider
        //https://stackoverflow.com/questions/14002022/android-sqlite-closed-exception/25379071#25379071
        //https://stackoverflow.com/questions/23293572/android-cannot-perform-this-operation-because-the-connection-pool-has-been-clos
        HomeDatabase.close()
    }
}