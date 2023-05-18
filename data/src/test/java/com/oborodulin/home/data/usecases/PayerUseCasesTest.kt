package com.oborodulin.home.data.usecases

import android.os.Build
import androidx.test.filters.MediumTest
import com.oborodulin.home.domain.model.Payer
import com.oborodulin.home.domain.usecases.*
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
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
class PayerUseCasesTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @OptIn(ExperimentalCoroutinesApi::class)
    //private val payerDS = PayerDataSourceImp(FakePayerDao(), StandardTestDispatcher())
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

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testGetPayerUseCaseProcess() = runTest {
        // ARRANGE
        //val useCase = GetPayerUseCase(configuration, payersRepository)
        val request = GetPayerUseCase.Request(UUID.randomUUID())
        val payer = Payer("", "", "title")
        //whenever(payersRepository.getPost(request.postId)).thenReturn(flowOf(payer))
        // ACT
        val response = getPayerUseCase.process(request).first()
        // ASSERT
        assertEquals(GetPayerUseCase.Response(payer), response)
    }
}