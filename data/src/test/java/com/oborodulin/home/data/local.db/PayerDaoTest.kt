package com.oborodulin.home.data.local.db

import android.database.sqlite.SQLiteConstraintException
import android.os.Build
import androidx.test.filters.MediumTest
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
import java.util.UUID
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
@MediumTest
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
        val twoPersonsPayer = PayerEntity.payerWithTwoPersons(appContext)
        val favoritePayer = PayerEntity.favoritePayer(appContext)
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
        val actualPayerId = insertPayer(payerDao, PayerEntity.payerWithTwoPersons(appContext))
        val expectedPayer = PayerEntity.favoritePayer(appContext, actualPayerId)
        // ACT
        payerDao.update(expectedPayer)
        // ASSERT
        payerDao.findDistinctById(actualPayerId).test {
            assertThat(awaitItem()).isEqualTo(expectedPayer)
            cancel()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun updatePayersAndFindAll_shouldReturn_theUpdatedPayers_inFlow() = runTest {
        // 1. ARRANGE
        val twoPersonsPayer = PayerEntity.payerWithTwoPersons(appContext)
        val favoritePayer = PayerEntity.favoritePayer(appContext)
        // 1. ACT
        payerDao.insert(twoPersonsPayer, favoritePayer)
        // 1. ASSERT
        payerDao.findDistinctAll().test {
            assertThat(awaitItem()).containsExactly(favoritePayer, twoPersonsPayer)
            cancel()
        }

        // 2. ARRANGE
        val expectedByPaymentDayPayer =
            PayerEntity.payerWithAlignByPaymentDay(appContext, favoritePayer.payerId)
        val expectedFavoritePayer =
            PayerEntity.favoritePayer(appContext, twoPersonsPayer.payerId)

        // 2. ACT
        payerDao.update(expectedByPaymentDayPayer, expectedFavoritePayer)
        // 2. ASSERT
        payerDao.findDistinctById(favoritePayer.payerId).test {
            assertThat(awaitItem()).isEqualTo(expectedByPaymentDayPayer)
            cancel()
        }
        payerDao.findDistinctById(twoPersonsPayer.payerId).test {
            assertThat(awaitItem()).isEqualTo(expectedFavoritePayer)
            cancel()
        }
    }

    @Test
    fun deleteInsertedPayerAndFindById_shouldReturn_IsNull() = runBlocking {
        // ARRANGE
        val actualPayer = PayerEntity.payerWithTwoPersons(appContext)
        // 1. ACT
        payerDao.insert(actualPayer)
        // 1. ASSERT
        val latch1 = CountDownLatch(1)
        val job1 = async(Dispatchers.IO) {
            payerDao.findDistinctById(actualPayer.payerId).collect {
                assertThat(it).isEqualTo(actualPayer)
                latch1.countDown()
            }
        }
        latch1.await()
        job1.cancelAndJoin()
        // 2. ACT
        payerDao.delete(actualPayer)
        // 2. ASSERT
        val latch2 = CountDownLatch(1)
        val job2 = async(Dispatchers.IO) {
            payerDao.findDistinctById(actualPayer.payerId).collect {
                assertThat(it).isNull()
                latch2.countDown()
            }
        }
        latch2.await()
        job2.cancelAndJoin()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun deletePayerByIdAndFindById_shouldReturn_IsNull() = runTest {
        // ARRANGE
        val actualPayerId = insertPayer(payerDao, PayerEntity.payerWithTwoPersons(appContext))
        // ACT
        payerDao.deleteById(actualPayerId)
        // ASSERT
        payerDao.findDistinctById(actualPayerId).test {
            assertThat(awaitItem()).isNull()
            cancel()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun deleteInsertedPayersListAndFindAll_shouldReturn_IsEmptyList() = runTest {
        // ARRANGE
        val actualTwoPersonsPayer = PayerEntity.payerWithTwoPersons(appContext)
        val actualFavoritePayer = PayerEntity.favoritePayer(appContext)
        payerDao.insert(actualTwoPersonsPayer, actualFavoritePayer)
        // ACT
        payerDao.delete(listOf(actualTwoPersonsPayer, actualFavoritePayer))
        // ASSERT
        payerDao.findDistinctAll().test {
            assertThat(awaitItem()).isEmpty()
            cancel()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun deleteAllInsertedPayersAndFindAll_shouldReturn_IsEmptyList() = runTest {
        // ARRANGE
        val actualTwoPersonsPayer = PayerEntity.payerWithTwoPersons(appContext)
        val actualFavoritePayer = PayerEntity.favoritePayer(appContext)
        // 1. ACT
        payerDao.insert(actualTwoPersonsPayer, actualFavoritePayer)
        // 1. ASSERT
        payerDao.findDistinctAll().test {
            assertThat(awaitItem()).containsExactly(actualFavoritePayer, actualTwoPersonsPayer)
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
        val twoPersonsPayer = PayerEntity.payerWithTwoPersons(appContext)
        val expectedFavoritePayer = PayerEntity.favoritePayer(appContext)
        payerDao.insert(twoPersonsPayer, expectedFavoritePayer)
        // ACT & ASSERT
        payerDao.findDistinctFavorite().test {
            assertThat(awaitItem()).isEqualTo(expectedFavoritePayer)
            cancel()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun favoriteById_return_theChangedFavoritePayer_inFlow() = runTest {
        // ARRANGE
        val expectedTwoPersonsPayer = PayerEntity.payerWithTwoPersons(appContext)
        val favoritePayer = PayerEntity.favoritePayer(appContext)
        payerDao.insert(expectedTwoPersonsPayer, favoritePayer)
        // ACT
        payerDao.setFavoriteById(expectedTwoPersonsPayer.payerId)
        // ASSERT
        payerDao.findDistinctFavorite().test {
            assertThat(awaitItem()).isEqualTo(expectedTwoPersonsPayer)
            cancel()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test(expected = SQLiteConstraintException::class)
    fun duplicatePayerErcCode_ExceptionThrown() = runTest {
        // ARRANGE
        val twoPersonsPayer = PayerEntity.payerWithTwoPersons(appContext)
        val anotherTwoPersonsPayer = PayerEntity.payerWithTwoPersons(appContext)
        // ACT
        payerDao.insert(twoPersonsPayer, anotherTwoPersonsPayer)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertPayerServicesAndFindPayersWithServices_returnsPayersWithServices_inFlow() = runTest {
        // ARRANGE
        val actualPayerId = insertPayer(payerDao, PayerEntity.payerWithTwoPersons(appContext))
        val rentService = ServiceEntity.rentService()
        val rentServiceTl = ServiceTlEntity.rentServiceTl(appContext, rentService.serviceId)
        serviceDao.insert(rentService, rentServiceTl)
        val electricityService = ServiceEntity.electricityService()
        val electricityServiceTl =
            ServiceTlEntity.electricityServiceTl(appContext, electricityService.serviceId)
        serviceDao.insert(electricityService, electricityServiceTl)
        // ACT
        payerDao.insert(
            PayerServiceCrossRefEntity.populatePrivilegesPayerService(
                payerId = actualPayerId, serviceId = rentService.serviceId
            ),
            PayerServiceCrossRefEntity.populateAllocateRatePayerService(
                payerId = actualPayerId, serviceId = electricityService.serviceId
            )
        )
        // ASSERT
        payerDao.findPayersWithServices().test {
            val payerServices = awaitItem()
            assertThat(payerServices).isNotEmpty()
            assertThat(payerServices[0].payer.payerId).isEqualTo(actualPayerId)
            assertThat(payerServices[0].services).hasSize(2)
            assertThat(payerServices[0].services).containsAtLeast(rentService, electricityService)
            cancel()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun deletePayerServiceAndFindPayersWithServices_returnsPayerWithService_inFlow() = runTest {
        // ARRANGE
        val actualPayerId = insertPayer(payerDao, PayerEntity.payerWithTwoPersons(appContext))
        val rentService = ServiceEntity.rentService()
        val rentServiceTl = ServiceTlEntity.rentServiceTl(appContext, rentService.serviceId)
        serviceDao.insert(rentService, rentServiceTl)
        val electricityService = ServiceEntity.electricityService()
        val electricityServiceTl =
            ServiceTlEntity.electricityServiceTl(appContext, electricityService.serviceId)
        serviceDao.insert(electricityService, electricityServiceTl)
        val payerRentService =
            PayerServiceCrossRefEntity.populatePayerService(actualPayerId, rentService.serviceId)
        val payerElectricityService = PayerServiceCrossRefEntity.populatePayerService(
            actualPayerId, electricityService.serviceId
        )
        payerDao.insert(payerRentService, payerElectricityService)
        // ACT
        payerDao.deleteServiceById(payerElectricityService.payerServiceId)
        // ASSERT
        payerDao.findPayersWithServices().test {
            val payerServices = awaitItem()
            assertThat(payerServices).isNotEmpty()
            assertThat(payerServices[0].payer.payerId).isEqualTo(actualPayerId)
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

    companion object {
        suspend fun insertPayer(
            payerDao: PayerDao, payer: PayerEntity = PayerEntity.defaultPayer()
        ): UUID {
            payerDao.insert(payer)
            return payer.payerId
        }
    }
}