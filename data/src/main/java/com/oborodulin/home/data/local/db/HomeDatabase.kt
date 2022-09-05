package com.oborodulin.home.data.local.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import com.oborodulin.home.common.util.Mapper
import com.oborodulin.home.data.local.db.converters.HomeTypeConverters
import com.oborodulin.home.data.local.db.dao.MeterDao
import com.oborodulin.home.data.local.db.dao.PayerDao
import com.oborodulin.home.data.local.db.dao.RateDao
import com.oborodulin.home.data.local.db.dao.ServiceDao
import com.oborodulin.home.data.local.db.entities.*
import com.oborodulin.home.data.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

private const val TAG = "HomeDatabase"

private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE payer RENAME TO ${PayerEntity.TABLE_NAME}"
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
private val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE meters ADD COLUMN payersId TEXT"
        )
    }
}

@Database(
    entities = [PayerEntity::class, ServiceEntity::class, PayerServiceEntity::class, RateEntity::class, MeterEntity::class],
    version = 5
)
@TypeConverters(HomeTypeConverters::class)
abstract class HomeDatabase : RoomDatabase() {
    abstract fun payerDao(): PayerDao
    abstract fun serviceDao(): ServiceDao
    abstract fun rateDao(): RateDao
    abstract fun meterDao(): MeterDao

    companion object {
        @Volatile
        private var INSTANCE: HomeDatabase? = null

        fun getInstance(context: Context, jsonLogger: Gson): HomeDatabase {
            // Multiple threads can ask for the database at the same time, ensure we only initialize
            // it once by using synchronized. Only one thread may enter a synchronized block at a
            // time.
            synchronized(this) {
                // Copy the current value of INSTANCE to a local variable so Kotlin can smart cast.
                // Smart cast is only available to local variables.
                var instance = INSTANCE
                // If instance is `null` make a new database instance.
                if (instance == null) {
                    instance =
                        Room.databaseBuilder(
                            context,
                            HomeDatabase::class.java,
                            Constants.DATABASE_NAME
                        )
                            .addMigrations(
                                MIGRATION_1_2,
                                MIGRATION_2_3,
                                MIGRATION_3_4,
                                MIGRATION_4_5
                            )
                            // Wipes and rebuilds instead of migrating if no Migration object.
                            // Migration is not part of this lesson. You can learn more about
                            // migration with Room in this blog post:
                            // https://medium.com/androiddevelopers/understanding-migrations-with-room-f01e04b07929
                            //.fallbackToDestructiveMigration()
                            .addCallback(DatabaseCallback(instance, jsonLogger))
                            .build()
                    // Assign INSTANCE to the newly created database.
                    INSTANCE = instance
                }
                // Return instance; smart cast to be non-null.
                return instance
            }
        }
    }

    fun getTestInstance(context: Context, jsonLogger: Gson): HomeDatabase {
        synchronized(this) {
            var instance = INSTANCE
            if (instance == null) {
                instance =
                    Room.inMemoryDatabaseBuilder(context, HomeDatabase::class.java)
                        .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                        .addCallback(DatabaseCallback(instance, jsonLogger))
                        .build()
                INSTANCE = instance
            }
            return instance
        }
    }
}

/**
 * https://stackoverflow.com/questions/5955202/how-to-remove-database-from-emulator
 */
class DatabaseCallback(val instance: HomeDatabase?, private val jsonLogger: Gson) :
    RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        Timber.tag(TAG).i("Database onCreate() called")
        // moving to a new thread
        // Executors.newSingleThreadExecutor().execute{f}
        CoroutineScope(Dispatchers.IO).launch()
        {
            db.beginTransaction()
            try {
                val payerEntity = PayerEntity(
                    ercCode = "000000000000000",
                    fullName = "Собственник жилья",
                    address = "Адрес не указан"
                )
                db.insert(
                    PayerEntity.TABLE_NAME,
                    SQLiteDatabase.CONFLICT_REPLACE,
                    Mapper.toContentValues(payerEntity)
                )

                db.setTransactionSuccessful()
/*
            val isImport: Boolean = true
            if (isImport) {
            instance?.payerDao()?.add(payerEntity)
            }

 */
                Timber.tag(TAG)
                    .i("Default PayerEntity imported: {${jsonLogger.toJson(payerEntity)}}")
            } finally {
                db.endTransaction()
            }
        }
    }
}