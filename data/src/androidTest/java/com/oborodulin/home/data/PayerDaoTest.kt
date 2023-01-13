package com.oborodulin.home.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.oborodulin.home.data.local.db.HomeDatabase
import com.oborodulin.home.data.local.db.dao.PayerDao
import com.oborodulin.home.data.local.db.entities.PayerEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.util.concurrent.CountDownLatch

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class PayerDaoTest {
    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var database: HomeDatabase
    private lateinit var payerDao: PayerDao

    @Before
    fun setupDatabase() {
        database = HomeDatabase.getTestInstance(appContext, null)
        payerDao = database.payerDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun insertPayer_returnsTrue() = runBlocking {
        val payer = PayerEntity.populatePayer1(appContext)
        payerDao.insert(payer)

        val latch = CountDownLatch(1)
        val job = async(Dispatchers.IO) {
            payerDao.findAll().collect {
                assertThat(it, contains(payer))
                latch.countDown()
            }
        }
        latch.await()
        job.cancelAndJoin()
    }

    @Test
    fun delete_returnsTrue() = runBlocking {
        val payer = PayerEntity.populatePayer1(appContext)
        payerDao.insert(payer)
        payerDao.delete()

        val latch = CountDownLatch(1)
        val job = async(Dispatchers.IO) {
            payerDao.findAll().collect {
                assertThat(it, empty())
                latch.countDown()
            }
        }
        latch.await()
        job.cancelAndJoin()
    }

    @Test
    fun useAppContext() {
        // Context of the app under test.
        assertEquals("com.oborodulin.home.data.test", appContext.packageName)
    }
}