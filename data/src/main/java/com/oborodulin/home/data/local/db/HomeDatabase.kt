package com.oborodulin.home.data.local.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import androidx.room.*
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
import com.oborodulin.home.data.local.db.views.*
import com.oborodulin.home.data.util.Constants
import kotlinx.coroutines.*
import timber.log.Timber
import java.time.OffsetDateTime
import java.util.*
import java.util.concurrent.Executors

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
    entities = [PayerEntity::class, ServiceEntity::class, ServiceTlEntity::class,
        PayerServiceCrossRefEntity::class,
        RateEntity::class, ServiceActivityEntity::class, ServicePromotionEntity::class,
        MeterEntity::class, MeterTlEntity::class, MeterValueEntity::class, MeterVerificationEntity::class,
        ReceiptEntity::class, ReceiptLineEntity::class],
    views = [MeterView::class, MeterPayerServiceView::class, ServiceView::class, ReceiptView::class,
        MeterValueMaxPrevDateView::class, MeterValuePrevPeriodView::class,
        MeterValuePaymentPeriodView::class, MeterValuePaymentView::class,
        PayerServiceView::class, RatePayerServiceView::class,
        PayerServiceDebtView::class, PayerServiceSubtotalDebtView::class, PayerTotalDebtView::class],
    version = 5
)
@TypeConverters(HomeTypeConverters::class)
abstract class HomeDatabase : RoomDatabase() {
    abstract fun payerDao(): PayerDao
    abstract fun serviceDao(): ServiceDao
    abstract fun meterDao(): MeterDao
    abstract fun rateDao(): RateDao

    companion object {
        var isImportDone: Deferred<Boolean>? = null

        @Volatile
        var isImportExecute: Boolean = false

        @Volatile
        private var INSTANCE: HomeDatabase? = null

        @Synchronized
        fun getInstance(context: Context, jsonLogger: Gson? = Gson()): HomeDatabase {
            // Multiple threads can ask for the database at the same time, ensure we only initialize
            // it once by using synchronized. Only one thread may enter a synchronized block at a
            // time.
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
                        .addCallback(DatabaseCallback(context, jsonLogger))
                        .build()
                // Assign INSTANCE to the newly created database.
                INSTANCE = instance
            }
            // Return instance; smart cast to be non-null.
            return instance
        }

        @Synchronized
        fun getTestInstance(context: Context, jsonLogger: Gson? = null): HomeDatabase {
            var instance = INSTANCE
            if (instance == null) {
                instance =
                    Room.inMemoryDatabaseBuilder(context, HomeDatabase::class.java)
                        .addMigrations(
                            MIGRATION_1_2,
                            MIGRATION_2_3,
                            MIGRATION_3_4,
                            MIGRATION_4_5
                        )
                        //.addCallback(DatabaseCallback(context, jsonLogger))
                        .allowMainThreadQueries()
                        //https://stackoverflow.com/questions/57027850/testing-android-room-with-livedata-coroutines-and-transactions
                        .setTransactionExecutor(Executors.newSingleThreadExecutor())
                        .build()
                INSTANCE = instance
            }
            return instance
        }

        // https://stackoverflow.com/questions/2421189/version-of-sqlite-used-in-android
        fun sqliteVersion() = SQLiteDatabase.create(null).use {
            android.database.DatabaseUtils.stringForQuery(it, "SELECT sqlite_version()", null)
        }

        @Synchronized
        fun close() {
            INSTANCE?.close()
            INSTANCE = null
        }
    }

    /**
     * https://stackoverflow.com/questions/5955202/how-to-remove-database-from-emulator
     */
    class DatabaseCallback(
        private val context: Context,
        private val jsonLogger: Gson? = null
    ) :
        Callback() {
        private val currentDateTime: OffsetDateTime = OffsetDateTime.now()

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            isImportExecute = true
            Timber.tag(TAG)
                .i("Database onCreate() called on thread '%s':", Thread.currentThread().name)
            // moving to a new thread
            // Executors.newSingleThreadExecutor().execute{f}
            //GlobalScope.launch(Dispatchers.Main)
            //CoroutineScope(Dispatchers.IO).launch()
            isImportDone = CoroutineScope(Dispatchers.IO).async {
                Timber.tag(TAG)
                    .i("Start thread '%s': prePopulateDb(...)", Thread.currentThread().name)
                prePopulateDb(db)
                true
            }
            Timber.tag(TAG)
                .i("Database onCreate() ended on thread '%s':", Thread.currentThread().name)
        }

        private fun prePopulateDb(db: SupportSQLiteDatabase) {
            Timber.tag(TAG).i("prePopulateDb(...) called")
            db.beginTransaction()
            try {
                // Default payers:
                // 1
                val payer1 = PayerEntity.payerWithTwoPersons(context)
                db.insert(
                    PayerEntity.TABLE_NAME,
                    SQLiteDatabase.CONFLICT_REPLACE,
                    Mapper.toContentValues(payer1)
                )
                Timber.tag(TAG)
                    .i("Default 1 PayerEntity imported: {%s}", jsonLogger?.toJson(payer1))
                // 2
                val payer2 = PayerEntity.favoritePayer(context)
                db.insert(
                    PayerEntity.TABLE_NAME,
                    SQLiteDatabase.CONFLICT_REPLACE,
                    Mapper.toContentValues(payer2)
                )
                Timber.tag(TAG)
                    .i("Default 2 PayerEntity imported: {%s}", jsonLogger?.toJson(payer2))
                // Default services:
                // rent
                val rentService = ServiceEntity.rent1Service(); insertDefService(db, rentService)
                // electricity
                val electricityService = ServiceEntity.electricity2Service()
                insertDefService(db, electricityService)
                // gas
                val gasService = ServiceEntity.gas3Service(); insertDefService(db, gasService)
                // cold water
                val coldWaterService = ServiceEntity.coldWater4Service()
                insertDefService(db, coldWaterService)
                // waste
                val wasteService = ServiceEntity.waste5Service(); insertDefService(db, wasteService)
                // heating
                val heatingService = ServiceEntity.heating6Service()
                insertDefService(db, heatingService)
                // hot water
                val hotWaterService = ServiceEntity.hotWater7Service()
                insertDefService(db, hotWaterService)
                // garbage
                val garbageService = ServiceEntity.garbage8Service()
                insertDefService(db, garbageService)
                // doorphone
                val doorphoneService = ServiceEntity.doorphone9Service()
                insertDefService(db, doorphoneService)
                // phone
                val phoneService = ServiceEntity.phone10Service(); insertDefService(
                    db,
                    phoneService
                )
                // ugso
                val ugsoService = ServiceEntity.ugso11Service(); insertDefService(db, ugsoService)
                // internet
                val internetService = ServiceEntity.internet12Service()
                insertDefService(db, internetService)

                // Default rates:
                // electricity
                insertDefRate(
                    db, RateEntity.electricityRateFrom0To150(electricityService.serviceId)
                )
                insertDefRate(
                    db, RateEntity.electricityRateFrom150To800(electricityService.serviceId)
                )
                insertDefRate(db, RateEntity.electricityRateFrom800(electricityService.serviceId))
                insertDefRate(
                    db, RateEntity.electricityPrivilegesRate(electricityService.serviceId)
                )
                // gas
                insertDefRate(db, RateEntity.gasRate(gasService.serviceId))
                // cold water
                insertDefRate(db, RateEntity.coldWaterRate(coldWaterService.serviceId))
                // heating
                insertDefRate(db, RateEntity.heatingRate(heatingService.serviceId))
                // waste
                insertDefRate(db, RateEntity.wasteRate(wasteService.serviceId))
                // hot water
                insertDefRate(db, RateEntity.hotWaterRate(hotWaterService.serviceId))

                // ==============================
                // FOR Payer 1:
                val rentPayer1ServiceId =
                    insertPayerService(db, payer = payer1, serviceId = rentService.serviceId)
                val electricityPayer1ServiceId =
                    insertPayerService(
                        db, payer = payer1,
                        serviceId = electricityService.serviceId,
                        isMeterOwner = true, isAllocateRate = true
                    )
                val gasPayer1ServiceId =
                    insertPayerService(
                        db, payer = payer1, serviceId = gasService.serviceId,
                        isMeterOwner = true
                    )
                val coldWaterPayer1ServiceId =
                    insertPayerService(
                        db, payer = payer1, serviceId = coldWaterService.serviceId,
                        isMeterOwner = true
                    )
                val wastePayer1ServiceId =
                    insertPayerService(db, payer = payer1, serviceId = wasteService.serviceId)
                val heatingPayer1ServiceId =
                    insertPayerService(
                        db, payer = payer1, serviceId = heatingService.serviceId,
                        isMeterOwner = true
                    )
                val hotWaterPayer1ServiceId =
                    insertPayerService(
                        db, payer = payer1, serviceId = hotWaterService.serviceId,
                        isMeterOwner = true
                    )
                val garbagePayer1ServiceId =
                    insertPayerService(
                        db, payer = payer1, serviceId = garbageService.serviceId
                    )
                val doorphonePayer1ServiceId =
                    insertPayerService(
                        db, payer = payer1, serviceId = doorphoneService.serviceId
                    )
                val phonePayer1ServiceId =
                    insertPayerService(db, payer = payer1, serviceId = phoneService.serviceId)
                val ugsoPayer1ServiceId =
                    insertPayerService(db, payer = payer1, serviceId = ugsoService.serviceId)
                // Meters:
                // Electricity
                val electricityPayer1Meter =
                    MeterEntity.electricityMeter(context, payer1.payerId)
                insertDefMeter(db, electricityPayer1Meter)
                // values:
                insertDefMeterValue(
                    db, MeterValueEntity.electricityMeterValue1(
                        electricityPayer1Meter.meterId, currentDateTime
                    )
                )
                insertDefMeterValue(
                    db, MeterValueEntity.electricityMeterValue2(
                        electricityPayer1Meter.meterId, currentDateTime
                    )
                )
                insertDefMeterValue(
                    db, MeterValueEntity.electricityMeterValue3(
                        electricityPayer1Meter.meterId, currentDateTime
                    )
                )
                insertDefMeterValue(
                    db, MeterValueEntity.electricityMeterValue4(
                        electricityPayer1Meter.meterId, currentDateTime
                    )
                )
                // Gas
                val gasPayer1Meter = MeterEntity.gasMeter(context, payer1.payerId, currentDateTime)
                insertDefMeter(db, gasPayer1Meter)
                // values:
                insertDefMeterValue(
                    db, MeterValueEntity.gasMeterValue1(gasPayer1Meter.meterId, currentDateTime)
                )
                insertDefMeterValue(
                    db, MeterValueEntity.gasMeterValue2(gasPayer1Meter.meterId, currentDateTime)
                )
                insertDefMeterValue(
                    db, MeterValueEntity.gasMeterValue3(gasPayer1Meter.meterId, currentDateTime)
                )
                // Cold water
                val coldWaterPayer1Meter =
                    MeterEntity.coldWaterMeter(context, payer1.payerId, currentDateTime)
                insertDefMeter(db, coldWaterPayer1Meter)
                // values:
                insertDefMeterValue(
                    db, MeterValueEntity.coldWaterMeterValue1(
                        coldWaterPayer1Meter.meterId, currentDateTime
                    )
                )
                insertDefMeterValue(
                    db, MeterValueEntity.coldWaterMeterValue2(
                        coldWaterPayer1Meter.meterId, currentDateTime
                    )
                )
                insertDefMeterValue(
                    db, MeterValueEntity.coldWaterMeterValue3(
                        coldWaterPayer1Meter.meterId, currentDateTime
                    )
                )
                // Hot water
                val hotWaterPayer1Meter =
                    MeterEntity.hotWaterMeter(context, payer1.payerId, currentDateTime)
                insertDefMeter(db, hotWaterPayer1Meter)
                // Heating
                val heatingPayer1Meter =
                    MeterEntity.heatingMeter(context, payer1.payerId, currentDateTime)
                insertDefMeter(db, heatingPayer1Meter)
                // values:
                insertDefMeterValue(
                    db, MeterValueEntity.heatingMeterValue1(
                        heatingPayer1Meter.meterId, currentDateTime
                    )
                )
                insertDefMeterValue(
                    db, MeterValueEntity.heatingMeterValue2(
                        heatingPayer1Meter.meterId, currentDateTime
                    )
                )
                insertDefMeterValue(
                    db, MeterValueEntity.heatingMeterValue3(
                        heatingPayer1Meter.meterId, currentDateTime
                    )
                )
                // Payer 1 rates:
                // rent
                insertDefRate(
                    db, RateEntity.rentRateForPayer(rentService.serviceId, rentPayer1ServiceId)
                )
                // heating
                // garbage
                insertDefRate(
                    db, RateEntity.garbageRateForPayer(
                        garbageService.serviceId, garbagePayer1ServiceId
                    )
                )
                // doorphone
                val doorphoneRateId = insertDefRate(
                    db, RateEntity.doorphoneRateForPayer(
                        doorphoneService.serviceId, doorphonePayer1ServiceId
                    )
                )
                // phone
                // ugso

                // Service promotions:
                val doorphoneServicePromotion =
                    ServicePromotionEntity.populatePrevRatePromotion(
                        serviceId = doorphoneService.serviceId,
                        payerServiceId = doorphonePayer1ServiceId
                    )
                db.insert(
                    ServicePromotionEntity.TABLE_NAME,
                    SQLiteDatabase.CONFLICT_REPLACE,
                    Mapper.toContentValues(doorphoneServicePromotion)
                )
                Timber.tag(TAG)
                    .i(
                        "Default service promotion imported: {%s}", jsonLogger?.toJson(
                            doorphoneServicePromotion
                        )
                    )

                // ==============================
                // FOR Payer 2:
                val rentPayer2ServiceId =
                    insertPayerService(db, payer = payer2, serviceId = rentService.serviceId)
                val electricityPayer2ServiceId =
                    insertPayerService(
                        db, payer = payer2,
                        serviceId = electricityService.serviceId,
                        isMeterOwner = true, isPrivilege = true
                    )
                val gasPayer2ServiceId =
                    insertPayerService(
                        db, payer = payer2, serviceId = gasService.serviceId,
                        isMeterOwner = true
                    )
                val coldWaterPayer2ServiceId =
                    insertPayerService(
                        db, payer = payer2, serviceId = coldWaterService.serviceId,
                        isMeterOwner = true
                    )
                val wastePayer2ServiceId =
                    insertPayerService(db, payer = payer2, serviceId = wasteService.serviceId)
                val heatingPayer2ServiceId =
                    insertPayerService(
                        db, payer = payer2, serviceId = heatingService.serviceId,
                        isMeterOwner = true
                    )
                val hotWaterPayer2ServiceId =
                    insertPayerService(
                        db, payer = payer2, serviceId = hotWaterService.serviceId,
                        isMeterOwner = true
                    )
                val garbagePayer2ServiceId =
                    insertPayerService(
                        db, payer = payer2, serviceId = garbageService.serviceId
                    )
                val doorphonePayer2ServiceId =
                    insertPayerService(
                        db, payer = payer2, serviceId = doorphoneService.serviceId
                    )
                val phonePayer2ServiceId =
                    insertPayerService(db, payer = payer2, serviceId = phoneService.serviceId)
                val ugsoPayer2ServiceId =
                    insertPayerService(db, payer = payer2, serviceId = ugsoService.serviceId)
                // Meters:
                // Electricity
                val electricityPayer2Meter =
                    MeterEntity.electricityMeter(context, payer2.payerId)
                insertDefMeter(db, electricityPayer2Meter)
                // values:
                insertDefMeterValue(
                    db, MeterValueEntity.electricityMeterValue1(
                        electricityPayer2Meter.meterId, currentDateTime
                    )
                )
                insertDefMeterValue(
                    db, MeterValueEntity.electricityMeterValue2(
                        electricityPayer2Meter.meterId, currentDateTime
                    )
                )
                insertDefMeterValue(
                    db, MeterValueEntity.electricityMeterValue3(
                        electricityPayer2Meter.meterId, currentDateTime
                    )
                )
                // Cold water
                val coldWaterPayer2Meter =
                    MeterEntity.coldWaterMeter(context, payer2.payerId)
                insertDefMeter(db, coldWaterPayer2Meter)
                // values:
                insertDefMeterValue(
                    db, MeterValueEntity.coldWaterMeterValue1(
                        coldWaterPayer2Meter.meterId, currentDateTime
                    )
                )
                insertDefMeterValue(
                    db, MeterValueEntity.coldWaterMeterValue2(
                        coldWaterPayer2Meter.meterId, currentDateTime
                    )
                )
                insertDefMeterValue(
                    db, MeterValueEntity.coldWaterMeterValue3(
                        coldWaterPayer2Meter.meterId, currentDateTime
                    )
                )
                // Hot water
                val hotWaterPayer2Meter =
                    MeterEntity.hotWaterMeter(context, payer2.payerId)
                insertDefMeter(db, hotWaterPayer2Meter)
                // values:
                insertDefMeterValue(
                    db, MeterValueEntity.hotWaterMeterValue1(
                        hotWaterPayer2Meter.meterId, currentDateTime
                    )
                )
                insertDefMeterValue(
                    db, MeterValueEntity.hotWaterMeterValue2(
                        hotWaterPayer2Meter.meterId, currentDateTime
                    )
                )
                insertDefMeterValue(
                    db, MeterValueEntity.hotWaterMeterValue3(
                        hotWaterPayer2Meter.meterId, currentDateTime
                    )
                )
                // Payer 2 rates:
                // rent
                insertDefRate(
                    db, RateEntity.rentRateForPayer(rentService.serviceId, rentPayer2ServiceId)
                )
                // heating
                insertDefRate(
                    db, RateEntity.heatingRateForPayer(
                        heatingService.serviceId, heatingPayer2ServiceId
                    )
                )
                // garbage
                insertDefRate(
                    db, RateEntity.garbageRateForPayer(
                        garbageService.serviceId, garbagePayer2ServiceId
                    )
                )
                // doorphone
                insertDefRate(
                    db, RateEntity.doorphoneRateForPayer(
                        doorphoneService.serviceId, doorphonePayer2ServiceId
                    )
                )
                // phone
                // ugso

                db.setTransactionSuccessful()
                Timber.tag(TAG).i("prePopulateDb(...) successful ended")
                /*
                            val isImport: Boolean = true
                            if (isImport) {
                            instance?.payerDao()?.add(payerEntity)
                            }

                 */
            } catch (e: SQLiteException) {
                Timber.tag(TAG).e(e)
            } finally {
                db.endTransaction()
                isImportExecute = false
            }
        }

        private fun insertDefService(db: SupportSQLiteDatabase, service: ServiceEntity) {
            val textContent =
                ServiceTlEntity.serviceTl(context, service.serviceType, service.serviceId)
            db.insert(
                ServiceEntity.TABLE_NAME,
                SQLiteDatabase.CONFLICT_REPLACE,
                Mapper.toContentValues(service)
            )
            db.insert(
                ServiceTlEntity.TABLE_NAME,
                SQLiteDatabase.CONFLICT_REPLACE,
                Mapper.toContentValues(textContent)
            )
            Timber.tag(TAG)
                .i(
                    "Default service imported: {\"service\": {%s}, \"tl\": {%s}}",
                    jsonLogger?.toJson(service),
                    jsonLogger?.toJson(textContent)
                )
        }

        private fun insertPayerService(
            db: SupportSQLiteDatabase, payer: PayerEntity, serviceId: UUID,
            isMeterOwner: Boolean = false, isPrivilege: Boolean = false,
            isAllocateRate: Boolean = false
        ): UUID {
            val payerService =
                PayerServiceCrossRefEntity(
                    payersId = payer.payerId, servicesId = serviceId,
                    isMeterOwner = isMeterOwner, isPrivileges = isPrivilege,
                    isAllocateRate = isAllocateRate
                )
            db.insert(
                PayerServiceCrossRefEntity.TABLE_NAME,
                SQLiteDatabase.CONFLICT_REPLACE,
                Mapper.toContentValues(payerService)
            )
            Timber.tag(TAG)
                .i(
                    "Payer service imported: {\"payerService\": {%s}}",
                    jsonLogger?.toJson(payerService)
                )
            return payerService.payerServiceId
        }

        private fun insertDefMeter(db: SupportSQLiteDatabase, meter: MeterEntity) {
            val textContent = MeterTlEntity.meterTl(context, meter.meterType, meter.meterId)
            db.insert(
                MeterEntity.TABLE_NAME,
                SQLiteDatabase.CONFLICT_REPLACE,
                Mapper.toContentValues(meter)
            )
            db.insert(
                MeterTlEntity.TABLE_NAME,
                SQLiteDatabase.CONFLICT_REPLACE,
                Mapper.toContentValues(textContent)
            )
            Timber.tag(TAG).i(
                "Default meter imported: {\"meter\": {%s}, \"tl\": {%s}}",
                jsonLogger?.toJson(meter),
                jsonLogger?.toJson(textContent)
            )
        }

        private fun insertDefMeterValue(
            db: SupportSQLiteDatabase,
            meterValue: MeterValueEntity
        ) {
            db.insert(
                MeterValueEntity.TABLE_NAME,
                SQLiteDatabase.CONFLICT_REPLACE,
                Mapper.toContentValues(meterValue)
            )
            Timber.tag(TAG).i("Default meter value imported: {%s}", jsonLogger?.toJson(meterValue))
        }

        private fun insertDefRate(db: SupportSQLiteDatabase, rate: RateEntity): UUID {
            db.insert(
                RateEntity.TABLE_NAME,
                SQLiteDatabase.CONFLICT_REPLACE,
                Mapper.toContentValues(rate)
            )
            Timber.tag(TAG).i("Default rate imported: {%s}", jsonLogger?.toJson(rate))
            return rate.rateId
        }
    }
}