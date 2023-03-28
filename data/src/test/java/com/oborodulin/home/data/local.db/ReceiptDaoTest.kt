package com.oborodulin.home.data.local.db

import android.os.Build
import androidx.test.filters.MediumTest
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.oborodulin.home.data.local.db.dao.PayerDao
import com.oborodulin.home.data.local.db.dao.ReceiptDao
import com.oborodulin.home.data.local.db.entities.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
@MediumTest
class ReceiptDaoTest : HomeDatabaseTest() {
    private lateinit var receiptDao: ReceiptDao
    private lateinit var payerDao: PayerDao

    @Before
    override fun setUp() {
        super.setUp()
        payerDao = payerDao()
        receiptDao = receiptDao()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertReceiptsListAndFindAll_shouldReturn_theOrderedItem_inFlow() = runTest {
        // ARRANGE
        val actualPayerId = PayerDaoTest.insertPayer(db, PayerEntity.payerWithTwoPersons(ctx))
        val receiptPaid = ReceiptEntity.receiptPaid(
            actualPayerId, currentDateTime.monthValue, currentDateTime.year
        )
        val receiptNotPaid = ReceiptEntity.receiptNotPaid(
            actualPayerId, currentDateTime.monthValue + 1, currentDateTime.year
        )
        // ACT
        receiptDao.insert(listOf(receiptPaid, receiptNotPaid))
        // ASSERT
        receiptDao.findDistinctAll().test {
            assertThat(awaitItem()).containsExactly(receiptPaid, receiptNotPaid).inOrder()
            cancel()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun updateReceiptAndFindById_shouldReturn_theUpdatedReceipt_inFlow() = runTest {
        // ARRANGE
        val actualPayerId = PayerDaoTest.insertPayer(db, PayerEntity.payerWithTwoPersons(ctx))
        val actualReceiptId = insertReceipt(db, ReceiptEntity.receiptPaid(actualPayerId))
        val expectedReceipt = ReceiptEntity.defaultReceipt(actualPayerId, actualReceiptId)
        // ACT
        receiptDao.update(expectedReceipt)
        // ASSERT
        receiptDao.findDistinctById(actualReceiptId).test {
            assertThat(awaitItem()).isEqualTo(expectedReceipt)
            cancel()
        }
    }

    companion object {
        suspend fun insertReceipt(db: HomeDatabase, receipt: ReceiptEntity): UUID {
            db.receiptDao().insert(receipt)
            return receipt.receiptId
        }
    }
}