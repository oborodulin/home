package com.oborodulin.home.servicing.usecases

import android.content.Context
import android.os.Build
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.MediumTest
import com.oborodulin.home.common.di.MainDispatcher
import com.oborodulin.home.data.local.db.entities.PayerEntity
import com.oborodulin.home.data.local.db.mappers.PayerEntityListToPayerListMapper
import com.oborodulin.home.data.local.db.mappers.PayerEntityToPayerMapper
import com.oborodulin.home.domain.model.Payer
import com.oborodulin.home.domain.usecases.*
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.*
import javax.inject.Inject

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
@MediumTest
class ServiceUseCasesTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    private val ctx: Context = ApplicationProvider.getApplicationContext()
    private var payers = HashMap<UUID, Payer>()

    @OptIn(ExperimentalCoroutinesApi::class)
    //private val payerDS = PayerDataSourceImp(FakePayerDao(), StandardTestDispatcher())
    @Inject
    @MainDispatcher
    lateinit var testDispatcher: CoroutineDispatcher

    @Inject
    lateinit var getPayerUseCase: GetPayerUseCase

    @Inject
    lateinit var getFavoritePayerUseCase: GetFavoritePayerUseCase

    @Inject
    lateinit var savePayerUseCase: SavePayerUseCase

    @Inject
    lateinit var deletePayerUseCase: DeletePayerUseCase

    @Inject
    lateinit var favoritePayerUseCase: FavoritePayerUseCase

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        hiltRule.inject()
        Dispatchers.setMain(testDispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        runTest {
            payers.keys.forEach {
                deletePayerUseCase.process(DeletePayerUseCase.Request(it)).first()
            }
        }
        payers.clear()
        Dispatchers.resetMain()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testGetPayerUseCaseProcess() = runTest {
        // ARRANGE
        val newPayer = PayerEntityToPayerMapper().map(PayerEntity.favoritePayer(ctx))
        newPayer.id = null
        val saveResponse = savePayerUseCase.process(SavePayerUseCase.Request(newPayer)).first()
        payers[saveResponse.payer.id!!] = saveResponse.payer
        // ACT
        val response =
            getPayerUseCase.process(GetPayerUseCase.Request(saveResponse.payer.id!!)).first()
        // ASSERT
        assertEquals(GetPayerUseCase.Response(saveResponse.payer), response)
        /*.test(timeout = 10000.milliseconds) {
            assertThat(awaitItem()).isEqualTo(GetPayerUseCase.Response(payer))
            cancel()
        }*/
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testGetFavoritePayerUseCaseProcess() = runTest {
        // ARRANGE
        val newPayers = PayerEntityListToPayerListMapper(PayerEntityToPayerMapper()).map(
            listOf(PayerEntity.favoritePayer(ctx), PayerEntity.payerWithTwoPersons(ctx))
        )
        lateinit var expectedPayer: Payer
        newPayers.forEach {
            it.id = null
            val saveResponse = savePayerUseCase.process(SavePayerUseCase.Request(it)).first()
            payers[saveResponse.payer.id!!] = saveResponse.payer
            if (saveResponse.payer.isFavorite) expectedPayer = saveResponse.payer
        }
        // ACT
        val response = getFavoritePayerUseCase.process(GetFavoritePayerUseCase.Request).first()
        // ASSERT
        assertEquals(GetFavoritePayerUseCase.Response(expectedPayer), response)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testFavoritePayerUseCaseProcess() = runTest {
        // ARRANGE
        val newPayers = PayerEntityListToPayerListMapper(PayerEntityToPayerMapper()).map(
            listOf(PayerEntity.favoritePayer(ctx), PayerEntity.payerWithTwoPersons(ctx))
        )
        lateinit var expectedPayer: Payer
        newPayers.forEach { payer ->
            payer.id = null
            with(savePayerUseCase.process(SavePayerUseCase.Request(payer)).first()) {
                payers[this.payer.id!!] = this.payer
                if (!this.payer.isFavorite) {
                    expectedPayer = this.payer.copy(isFavorite = true)
                    expectedPayer.id = this.payer.id
                }
            }
        }
        // ACT
        favoritePayerUseCase.process(FavoritePayerUseCase.Request(expectedPayer.id!!)).first()
        // ASSERT
        val response = getFavoritePayerUseCase.process(GetFavoritePayerUseCase.Request).first()
        assertEquals(GetFavoritePayerUseCase.Response(expectedPayer), response)
    }
}