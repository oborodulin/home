package com.oborodulin.home.data.local.source

// import androidx.test.filters.MediumTest
// import com.oborodulin.home.data.local.db.dao.PayerDao
// import com.oborodulin.home.data.local.db.entities.PayerEntity
// import com.oborodulin.home.data.local.db.repositories.PayerDataSourceImp
// import dagger.hilt.android.testing.HiltAndroidRule
// import dagger.hilt.android.testing.HiltAndroidTest
// import kotlinx.coroutines.ExperimentalCoroutinesApi
// import kotlinx.coroutines.flow.first
// import kotlinx.coroutines.flow.flowOf
// import kotlinx.coroutines.test.runTest
// import org.junit.Assert
// import org.junit.Before
// import org.junit.Rule
// import org.junit.Test
// import org.junit.runner.RunWith
// import org.mockito.kotlin.mock
// import org.mockito.kotlin.verify
// import org.mockito.kotlin.whenever
// import org.robolectric.RobolectricTestRunner
//
// @RunWith(RobolectricTestRunner::class)
// @MediumTest
// class PayerDataSourceImpTest {
// private val payerDao = mock<PayerDao>()
// private val postDataSource = PayerDataSourceImp(payerDao)
//
// @ExperimentalCoroutinesApi
// @Test
// fun testGetPosts() = runTest {
// val payers = listOf(PayerEntity.payerWithTwoPersons())
// val expectedPosts = listOf(Post(1, 1, "title", "body"))
// whenever(payerDao.getPosts()).thenReturn(flowOf(payers))
// val result = postDataSource.getPosts().first()
// Assert.assertEquals(expectedPosts, result)
// }
//
// @ExperimentalCoroutinesApi
// @Test
// fun testAddUsers() = runTest {
// val localPosts = listOf(PostEntity(1, 1, "title", "body"))
// val posts = listOf(Post(1, 1, "title", "body"))
// postDataSource.addPosts(posts)
// verify(payerDao).insertPosts(localPosts)
// }
//
// }