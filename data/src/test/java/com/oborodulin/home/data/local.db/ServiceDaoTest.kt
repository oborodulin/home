package com.oborodulin.home.data.local.db

import android.database.sqlite.SQLiteConstraintException
import android.os.Build
import androidx.test.filters.SmallTest
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.oborodulin.home.data.local.db.dao.ServiceDao
import com.oborodulin.home.data.local.db.entities.PayerEntity
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
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
@SmallTest
class ServiceDaoTest : HomeDatabaseTest() {
    private lateinit var serviceDao: ServiceDao

    @Before
    override fun setUp() {
        super.setUp()
        serviceDao = serviceDao()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertServicesAndFindAll_shouldReturn_theOrderedItem_inFlow() = runTest {
        // ARRANGE
        val rentService = ServiceEntity.populateRentService()
        val rentServiceTl = ServiceTlEntity.populateRentServiceTl(appContext, rentService.serviceId)
        val electricityService = ServiceEntity.populateElectricityService()
        val electricityServiceTl =
            ServiceTlEntity.populateElectricityServiceTl(appContext, electricityService.serviceId)
        // ACT
        serviceDao.insert(rentService, rentServiceTl)
        serviceDao.insert(electricityService, electricityServiceTl)
        // ASSERT
        serviceDao.findDistinctAll().test {
            assertThat(awaitItem()).hasSize(2)
            assertThat(awaitItem()[0].data).isEqualTo(rentService)
            assertThat(awaitItem()[0].tl).isEqualTo(rentServiceTl)
            assertThat(awaitItem()[1].data).isEqualTo(electricityService)
            assertThat(awaitItem()[1].tl).isEqualTo(electricityServiceTl)
            cancel()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun updateServiceAndFindById_shouldReturn_theUpdatedService_inFlow() = runTest {
        // ARRANGE
        val rentService = ServiceEntity.populateRentService()
        val rentServiceTl = ServiceTlEntity.populateRentServiceTl(appContext, rentService.serviceId)
        serviceDao.insert(rentService, rentServiceTl)
        val electricityService = ServiceEntity.populateElectricityService(rentService.serviceId)
        val electricityServiceTl =
            ServiceTlEntity.populateElectricityServiceTl(appContext, electricityService.serviceId)
        // ACT
        serviceDao.update(electricityService, electricityServiceTl)
        // ASSERT
        serviceDao.findDistinctById(electricityService.serviceId).test {
            assertThat(awaitItem()).isNotNull()
            assertThat(awaitItem().data).isEqualTo(electricityService)
            assertThat(awaitItem().tl).isEqualTo(electricityServiceTl)
            cancel()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun deleteServiceById_returnsIsNull() = runTest {
        // ARRANGE
        val rentService = ServiceEntity.populateRentService()
        val rentServiceTl = ServiceTlEntity.populateRentServiceTl(appContext, rentService.serviceId)
        serviceDao.insert(rentService, rentServiceTl)
        // ACT
        serviceDao.deleteById(rentService.serviceId)
        // ASSERT
        serviceDao.findDistinctById(rentService.serviceId).test {
            assertThat(awaitItem()).isNull()
            cancel()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun deleteAllServicesAndFindAll_returnsIsEmpty() = runTest {
        // ARRANGE
        val rentService = ServiceEntity.populateRentService()
        val rentServiceTl = ServiceTlEntity.populateRentServiceTl(appContext, rentService.serviceId)
        val electricityService = ServiceEntity.populateElectricityService()
        val electricityServiceTl =
            ServiceTlEntity.populateElectricityServiceTl(appContext, electricityService.serviceId)
        serviceDao.insert(rentService, rentServiceTl)
        serviceDao.insert(electricityService, electricityServiceTl)
        // ACT
        serviceDao.deleteAll()
        // ASSERT
        serviceDao.findDistinctAll().test {
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
        serviceDao.insert(twoPersonsPayer, favoritePayer)
        // ACT & ASSERT
        serviceDao.findDistinctFavorite().test {
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
        serviceDao.insert(twoPersonsPayer, favoritePayer)
        // ACT
        serviceDao.setFavoriteById(twoPersonsPayer.payerId)
        // ASSERT
        serviceDao.findDistinctFavorite().test {
            assertThat(awaitItem()).isEqualTo(twoPersonsPayer)
            cancel()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test(expected = SQLiteConstraintException::class)
    fun duplicatePayer_ExceptionThrown() = runTest {
        // ARRANGE
        val payer1 = PayerEntity.populateTwoPersonsPayer(appContext)
        val payer2 = PayerEntity.populateTwoPersonsPayer(appContext)
        // ACT
        serviceDao.insert(payer1, payer2)
    }

    @Test
    fun useAppContext() {
        // Context of the app under test.
        assertEquals("com.oborodulin.home.data.test", this.appContext.packageName)
    }
}