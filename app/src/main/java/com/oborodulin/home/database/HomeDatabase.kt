package com.oborodulin.home.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.TypeConverters
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.oborodulin.home.domain.meter.Meter
import com.oborodulin.home.domain.payer.Payer
import com.oborodulin.home.domain.rate.Rate
import com.oborodulin.home.domain.service.Service

private const val DATABASE_NAME = "home-database"

private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE payer RENAME TO payers"
        )
    }
}
private val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE payers ADD COLUMN paymentDay INTEGER"
        )
    }
}
private val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE payers ADD COLUMN personsNum INTEGER"
        )
    }
}

@Database(entities = [Payer::class, Service::class, Rate::class, Meter::class], version = 4)
@TypeConverters(HomeTypeConverters::class)
abstract class HomeDatabase : RoomDatabase() {
    abstract fun payerDao(): PayerDao
    abstract fun serviceDao(): ServiceDao
    abstract fun rateDao(): RateDao
    abstract fun meterDao(): MeterDao

    companion object {
        fun newInstance(context: Context) =
            Room.databaseBuilder(context, HomeDatabase::class.java, DATABASE_NAME)
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4).build()

        fun newTestInstance(context: Context) =
            Room.inMemoryDatabaseBuilder(context, HomeDatabase::class.java)
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4).build()
    }
}
