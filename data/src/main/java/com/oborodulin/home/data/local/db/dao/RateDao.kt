package com.oborodulin.home.data.local.db.dao

//import androidx.lifecycle.LiveData
import androidx.room.*
import com.oborodulin.home.domain.model.Rate
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
interface RateDao {
    @Query("SELECT * FROM rates")
    fun getAll(): Flow<List<Rate>>

    @Query("SELECT * FROM rates WHERE id=:id")
    fun get(id: UUID): Rate?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(rate: Rate)

    @Update
    suspend fun update(rate: Rate)

    @Delete
    suspend fun delete(rate: Rate)

    @Query("DELETE FROM rates")
    fun deleteAll()
}