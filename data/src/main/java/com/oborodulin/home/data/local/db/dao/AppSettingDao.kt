package com.oborodulin.home.data.local.db.dao

import androidx.room.*
import com.oborodulin.home.data.local.db.entities.AppSettingEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import java.util.*

@Dao
interface AppSettingDao { // : BaseDao<AppSettingEntity>
    // READS:
    @Query("SELECT * FROM ${AppSettingEntity.TABLE_NAME}")
    fun findAll(): Flow<List<AppSettingEntity>>

    @ExperimentalCoroutinesApi
    fun findDistinctAll() = findAll().distinctUntilChanged()

    @Query("SELECT * FROM ${AppSettingEntity.TABLE_NAME} WHERE settingId = :id")
    fun findById(id: UUID): Flow<AppSettingEntity>

    @ExperimentalCoroutinesApi
    fun findDistinctById(id: UUID) = findById(id).distinctUntilChanged()

    @Query("SELECT paramValue FROM ${AppSettingEntity.TABLE_NAME} WHERE paramName = :paramName")
    fun findByParamName(paramName: String): Flow<String>

    @ExperimentalCoroutinesApi
    fun findDistinctByParamName(paramName: String) =
        findByParamName(paramName).distinctUntilChanged()

    // INSERTS:
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(setting: AppSettingEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(vararg settings: AppSettingEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(settings: List<AppSettingEntity>)

    // UPDATES:
    @Update
    suspend fun update(setting: AppSettingEntity)

    @Update
    suspend fun update(vararg settings: AppSettingEntity)

    // DELETES:
    @Delete
    suspend fun delete(settings: AppSettingEntity)

    @Delete
    suspend fun delete(vararg settings: AppSettingEntity)

    @Delete
    suspend fun delete(settings: List<AppSettingEntity>)

    @Query("DELETE FROM ${AppSettingEntity.TABLE_NAME} WHERE settingId = :id")
    suspend fun deleteById(id: UUID)

    @Query("DELETE FROM ${AppSettingEntity.TABLE_NAME}")
    suspend fun deleteAll()
}