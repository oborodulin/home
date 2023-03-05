package com.oborodulin.home.data.local.db

import android.database.sqlite.SQLiteConstraintException
import android.os.Build
import androidx.test.filters.SmallTest
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.oborodulin.home.data.local.db.dao.PayerDao
import com.oborodulin.home.data.local.db.dao.ServiceDao
import com.oborodulin.home.data.local.db.entities.PayerEntity
import com.oborodulin.home.data.local.db.entities.PayerServiceCrossRefEntity
import com.oborodulin.home.data.local.db.entities.ServiceEntity
import com.oborodulin.home.data.local.db.entities.ServiceTlEntity
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.CountDownLatch

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
//https://clintpauldev.com/room-testing-using-dagger-hilt/
//@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
//@Config(constants = BuildConfig::class)
@Config(sdk = [Build.VERSION_CODES.P]) // This config setting is the key to make things work
@SmallTest
class PayerDaoTest : HomeDatabaseTest() {
    /*    @get:Rule
        var hiltRule = HiltAndroidRule(this)

        @get:Rule
        var instantTaskExecutorRule = InstantTaskExecutorRule()

     */
    private lateinit var payerDao: PayerDao
    private lateinit var serviceDao: ServiceDao

    @Before
    override fun setUp() {
        super.setUp()
        payerDao = payerDao()
        serviceDao = serviceDao()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertPayersListAndFindAll_shouldReturn_theOrderedItem_inFlow() = runTest {
        // ARRANGE
        val twoPersonsPayer = PayerEntity.populateTwoPersonsPayer(appContext)
        val favoritePayer = PayerEntity.populateFavoritePayer(appContext)
        // ACT
        payerDao.insert(listOf(twoPersonsPayer, favoritePayer))
        // ASSERT
        payerDao.findDistinctAll().test {
            assertThat(awaitItem()).containsExactly(favoritePayer, twoPersonsPayer).inOrder()
            cancel()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun updatePayerAndFindById_shouldReturn_theUpdatedPayer_inFlow() = runTest {
        // ARRANGE
        val payer = PayerEntity.populateTwoPersonsPayer(appContext)
        payerDao.insert(payer)
        val testPayer = PayerEntity.populateFavoritePayer(appContext, payer.payerId)
        // ACT
        payerDao.update(testPayer)
        // ASSERT
        payerDao.findDistinctById(payer.payerId).test {
            assertThat(awaitItem()).isEqualTo(testPayer)
            cancel()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun updatePayersAndFindAll_shouldReturn_theUpdatedPayers_inFlow() = runTest {
        // 1. ARRANGE
        val twoPersonsPayer = PayerEntity.populateTwoPersonsPayer(appContext)
        val favoritePayer = PayerEntity.populateFavoritePayer(appContext)
        // 1. ACT
        payerDao.insert(twoPersonsPayer, favoritePayer)
        // 1. ASSERT
        payerDao.findDistinctAll().test {
            assertThat(awaitItem()).containsExactly(favoritePayer, twoPersonsPayer)
            cancel()
        }

        // 2. ARRANGE
        val testPayer1 =
            PayerEntity.populateAlignByPaymentDayPayer(appContext, favoritePayer.payerId)
        val testPayer2 = PayerEntity.populateFavoritePayer(appContext, twoPersonsPayer.payerId)

        // 2. ACT
        payerDao.update(testPayer1, testPayer2)
        // 2. ASSERT
        payerDao.findDistinctById(favoritePayer.payerId).test {
            assertThat(awaitItem()).isEqualTo(testPayer1)
            cancel()
        }
        payerDao.findDistinctById(twoPersonsPayer.payerId).test {
            assertThat(awaitItem()).isEqualTo(testPayer2)
            cancel()
        }
    }

    @Test
    fun deleteInsertedPayerAndFindById_returnsIsNull() = runBlocking {
        // ARRANGE
        val payer = PayerEntity.populateTwoPersonsPayer(appContext)
        // 1. ACT
        payerDao.insert(payer)
        // 1. ASSERT
        val latch1 = CountDownLatch(1)
        val job1 = async(Dispatchers.IO) {
            payerDao.findDistinctById(payer.payerId).collect {
                assertThat(it).isEqualTo(payer)
                latch1.countDown()
            }
        }
        latch1.await()
        job1.cancelAndJoin()
        // 2. ACT
        payerDao.delete(payer)
        // 2. ASSERT
        val latch2 = CountDownLatch(1)
        val job2 = async(Dispatchers.IO) {
            payerDao.findDistinctById(payer.payerId).collect {
                assertThat(it).isNull()
                latch2.countDown()
            }
        }
        latch2.await()
        job2.cancelAndJoin()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun deletePayerById_returnsIsNull() = runTest {
        // ARRANGE
        val payer = PayerEntity.populateTwoPersonsPayer(appContext)
        payerDao.insert(payer)
        // ACT
        payerDao.deleteById(payer.payerId)
        // ASSERT
        payerDao.findDistinctById(payer.payerId).test {
            assertThat(awaitItem()).isNull()
            cancel()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun deleteInsertedPayersListAndFindAll_returnsIsEmpty() = runTest {
        // ARRANGE
        val payer1 = PayerEntity.populateTwoPersonsPayer(appContext)
        val payer2 = PayerEntity.populateFavoritePayer(appContext)
        payerDao.insert(payer1, payer2)
        // ACT
        payerDao.delete(listOf(payer1, payer2))
        // ASSERT
        payerDao.findDistinctAll().test {
            assertThat(awaitItem()).isEmpty()
            cancel()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun deleteAllInsertedPayersAndFindAll_returnsIsEmpty() = runTest {
        // ARRANGE
        val payer1 = PayerEntity.populateTwoPersonsPayer(appContext)
        val payer2 = PayerEntity.populateFavoritePayer(appContext)
        // 1. ACT
        payerDao.insert(payer1, payer2)
        // 1. ASSERT
        payerDao.findDistinctAll().test {
            assertThat(awaitItem()).containsExactly(payer2, payer1)
            cancel()
        }
        // 2. ACT
        payerDao.deleteAll()
        // 2. ASSERT
        payerDao.findDistinctAll().test {
            assertThat(awaitItem()).isEmpty()
            cancel()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun findFavorite_return_theFavoritePayer_inFlow() = runTest {
        // ARRANGE
        val twoPersonsPayer = PayerEntity.populateTwoPersonsPayer(appContext)
        val favoritePayer = PayerEntity.populateFavoritePayer(appContext)
        payerDao.insert(twoPersonsPayer, favoritePayer)
        // ACT & ASSERT
        payerDao.findDistinctFavorite().test {
            assertThat(awaitItem()).isEqualTo(favoritePayer)
            cancel()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun favoriteById_return_theChangedFavoritePayer_inFlow() = runTest {
        // ARRANGE
        val twoPersonsPayer = PayerEntity.populateTwoPersonsPayer(appContext)
        val favoritePayer = PayerEntity.populateFavoritePayer(appContext)
        payerDao.insert(twoPersonsPayer, favoritePayer)
        // ACT
        payerDao.setFavoriteById(twoPersonsPayer.payerId)
        // ASSERT
        payerDao.findDistinctFavorite().test {
            assertThat(awaitItem()).isEqualTo(twoPersonsPayer)
            cancel()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test(expected = SQLiteConstraintException::class)
    fun duplicatePayerErcCode_ExceptionThrown() = runTest {
        // ARRANGE
        val payer1 = PayerEntity.populateTwoPersonsPayer(appContext)
        val payer2 = PayerEntity.populateTwoPersonsPayer(appContext)
        // ACT
        payerDao.insert(payer1, payer2)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertPayerServicesAndFindPayersWithServices_returnsPayersWithServices_inFlow() = runTest {
        // ARRANGE
        val payer = PayerEntity.populateTwoPersonsPayer(appContext)
        payerDao.insert(payer)
        val rentService = ServiceEntity.populateRentService()
        val rentServiceTl = ServiceTlEntity.populateRentServiceTl(appContext, rentService.serviceId)
        val electricityService = ServiceEntity.populateElectricityService()
        val electricityServiceTl =
            ServiceTlEntity.populateElectricityServiceTl(appContext, electricityService.serviceId)
        serviceDao.insert(rentService, rentServiceTl)
        serviceDao.insert(electricityService, electricityServiceTl)
        // ACT
        payerDao.insert(
            PayerServiceCrossRefEntity.populatePrivilegesPayerService(
                payerId = payer.payerId, serviceId = rentService.serviceId
            ),
            PayerServiceCrossRefEntity.populateAllocateRatePayerService(
                payerId = payer.payerId, serviceId = electricityService.serviceId
            )
        )
        // ASSERT
        payerDao.findPayersWithServices().test {
            val payerServices = awaitItem()
            assertThat(payerServices).isNotEmpty()
            assertThat(payerServices[0].payer).isEqualTo(payer)
            assertThat(payerServices[0].services).hasSize(2)
            assertThat(payerServices[0].services).containsAtLeast(rentService, electricityService)
            cancel()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun deletePayerServiceAndFindPayersWithServices_returnsPayersWithServices_inFlow() = runTest {
        // ARRANGE
        val payer = PayerEntity.populateTwoPersonsPayer(appContext)
        payerDao.insert(payer)
        val rentService = ServiceEntity.populateRentService()
        val rentServiceTl = ServiceTlEntity.populateRentServiceTl(appContext, rentService.serviceId)
        val electricityService = ServiceEntity.populateElectricityService()
        val electricityServiceTl =
            ServiceTlEntity.populateElectricityServiceTl(appContext, electricityService.serviceId)
        serviceDao.insert(rentService, rentServiceTl)
        serviceDao.insert(electricityService, electricityServiceTl)
        val payerRentService =
            PayerServiceCrossRefEntity.populatePayerService(payer.payerId, rentService.serviceId)
        val payerElectricityService = PayerServiceCrossRefEntity.populatePayerService(
            payer.payerId, electricityService.serviceId
        )
        payerDao.insert(payerRentService, payerElectricityService)
        // ACT
        payerDao.deleteServiceById(payerElectricityService.payerServiceId)
        // ASSERT
        payerDao.findPayersWithServices().test {
            val payerServices = awaitItem()
            assertThat(payerServices).isNotEmpty()
            assertThat(payerServices[0].payer).isEqualTo(payer)
            assertThat(payerServices[0].services).hasSize(1)
            assertThat(payerServices[0].services).containsExactly(rentService)
            cancel()
        }
    }

    @Test
    fun useAppContext() {
        // Context of the app under test.
        assertEquals("com.oborodulin.home.data.test", this.appContext.packageName)
    }
}