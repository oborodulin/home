package com.oborodulin.home.data.local.db.dao

import androidx.room.*
import com.oborodulin.home.data.local.db.entities.RateEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.util.*

@Dao
interface RateDao : BaseDao<RateEntity> {
    // READS:
    @Query("SELECT * FROM rates")
    fun findAll(): Flow<List<RateEntity>>

    @ExperimentalCoroutinesApi
    fun findDistinctAll() = findAll().distinctUntilChanged()

    @Query("SELECT * FROM rates WHERE rateId = :id")
    fun findById(id: UUID): Flow<RateEntity>

    @ExperimentalCoroutinesApi
    fun findDistinctById(id: UUID) = findById(id).distinctUntilChanged()

    // INSERTS:

    // UPDATES:

    // DELETES:
    @Query("DELETE FROM rates")
    fun deleteAll()
}