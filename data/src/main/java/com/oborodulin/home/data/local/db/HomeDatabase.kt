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
        RateEntity::class, ServicePromotionEntity::class,
        MeterEntity::class, MeterTlEntity::class, MeterValueEntity::class, MeterVerificationEntity::class,
        PayerServiceMeterCrossRefEntity::class,
        ReceiptEntity::class, ReceiptLineEntity::class],
    views = [MetersView::class, ServicesView::class, MeterValueMaxPrevDatesView::class,
        MeterValuePrevPeriodsView::class, MeterValuePaymentPeriodsView::class, MeterValuePaymentsView::class,
        RateServiceLastDatesView::class, RatePayerServicesView::class,
        PayerServiceDebtsView::class],
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
        fun getInstance(context: Context, jsonLogger: Gson? = null): HomeDatabase {
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
                        .addCallback(DatabaseCallback(context, jsonLogger))
                        .allowMainThreadQueries()
                        .build()
                INSTANCE = instance
            }
            return instance
        }

        // https://stackoverflow.com/questions/2421189/version-of-sqlite-used-in-android
        fun sqliteVersion() = SQLiteDatabase.create(null).use {
            android.database.DatabaseUtils.stringForQuery(it, "SELECT sqlite_version()", null)
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
                val payer1Entity = PayerEntity.populatePayer1(context)
                db.insert(
                    PayerEntity.TABLE_NAME,
                    SQLiteDatabase.CONFLICT_REPLACE,
                    Mapper.toContentValues(payer1Entity)
                )
                Timber.tag(TAG)
                    .i("Default 1 PayerEntity imported: {%s}", jsonLogger?.toJson(payer1Entity))
                // 2
                val payer2Entity = PayerEntity.populatePayer2(context)
                db.insert(
                    PayerEntity.TABLE_NAME,
                    SQLiteDatabase.CONFLICT_REPLACE,
                    Mapper.toContentValues(payer2Entity)
                )
                Timber.tag(TAG)
                    .i("Default 2 PayerEntity imported: {%s}", jsonLogger?.toJson(payer2Entity))
                // Default services:
                // rent
                val rentService = ServiceEntity.populateRentService();
                insertDefService(
                    db, rentService,
                    ServiceTlEntity.populateRentServiceTl(context, rentService.serviceId)
                )
                // electricity
                val electricityService = ServiceEntity.populateElectricityService()
                insertDefService(
                    db, electricityService,
                    ServiceTlEntity.populateElectricityServiceTl(
                        context,
                        electricityService.serviceId
                    )
                )
                // gas
                val gasService = ServiceEntity.populateGasService()
                insertDefService(
                    db, gasService,
                    ServiceTlEntity.populateGasServiceTl(context, gasService.serviceId)
                )
                // cold water
                val coldWaterService = ServiceEntity.populateColdWaterService()
                insertDefService(
                    db, coldWaterService,
                    ServiceTlEntity.populateColdWaterServiceTl(context, coldWaterService.serviceId)
                )
                // waste
                val wasteService = ServiceEntity.populateWasteService()
                insertDefService(
                    db, wasteService,
                    ServiceTlEntity.populateWasteServiceTl(context, wasteService.serviceId)
                )
                // heating
                val heatingService = ServiceEntity.populateHeatingService()
                insertDefService(
                    db, heatingService,
                    ServiceTlEntity.populateHeatingServiceTl(context, heatingService.serviceId)
                )
                // hot water
                val hotWaterService = ServiceEntity.populateHotWaterService()
                insertDefService(
                    db, hotWaterService,
                    ServiceTlEntity.populateHotWaterServiceTl(context, hotWaterService.serviceId)
                )
                // garbage
                val garbageService = ServiceEntity.populateGarbageService()
                insertDefService(
                    db, garbageService,
                    ServiceTlEntity.populateGarbageServiceTl(context, garbageService.serviceId)
                )
                // doorphone
                val doorphoneService = ServiceEntity.populateDoorphoneService()
                insertDefService(
                    db, doorphoneService,
                    ServiceTlEntity.populateDoorphoneServiceTl(context, doorphoneService.serviceId)
                )
                // phone
                val phoneService = ServiceEntity.populatePhoneService()
                insertDefService(
                    db, phoneService,
                    ServiceTlEntity.populatePhoneServiceTl(context, phoneService.serviceId)
                )
                // ugso
                val ugsoService = ServiceEntity.populateUgsoService()
                insertDefService(
                    db, ugsoService,
                    ServiceTlEntity.populateUgsoServiceTl(context, ugsoService.serviceId)
                )

                // Default rates:
                // electricity
                insertDefRate(db, RateEntity.populateElectricityRate1(electricityService.serviceId))
                insertDefRate(db, RateEntity.populateElectricityRate2(electricityService.serviceId))
                insertDefRate(db, RateEntity.populateElectricityRate3(electricityService.serviceId))
                insertDefRate(
                    db,
                    RateEntity.populateElectricityPrivilegesRate(electricityService.serviceId)
                )
                // gas
                insertDefRate(db, RateEntity.populateGasRate(gasService.serviceId))
                // cold water
                insertDefRate(db, RateEntity.populateColdWaterRate(coldWaterService.serviceId))
                // heating
                insertDefRate(db, RateEntity.populateHeatingRate(heatingService.serviceId))
                // waste
                insertDefRate(db, RateEntity.populateWasteRate(wasteService.serviceId))
                // hot water
                insertDefRate(db, RateEntity.populateHotWaterRate(hotWaterService.serviceId))

                // ==============================
                // FOR Payer 1:
                val rentPayer1ServiceId =
                    insertPayerService(db, payer = payer1Entity, serviceId = rentService.serviceId)
                val electricityPayer1ServiceId =
                    insertPayerService(
                        db, payer = payer1Entity,
                        serviceId = electricityService.serviceId,
                        isAllocateRate = true
                    )
                val gasPayer1ServiceId =
                    insertPayerService(db, payer = payer1Entity, serviceId = gasService.serviceId)
                val coldWaterPayer1ServiceId =
                    insertPayerService(
                        db,
                        payer = payer1Entity,
                        serviceId = coldWaterService.serviceId
                    )
                val wastePayer1ServiceId =
                    insertPayerService(db, payer = payer1Entity, serviceId = wasteService.serviceId)
                val heatingPayer1ServiceId =
                    insertPayerService(
                        db,
                        payer = payer1Entity,
                        serviceId = heatingService.serviceId
                    )
                val hotWaterPayer1ServiceId =
                    insertPayerService(
                        db,
                        payer = payer1Entity,
                        serviceId = hotWaterService.serviceId
                    )
                val garbagePayer1ServiceId =
                    insertPayerService(
                        db,
                        payer = payer1Entity,
                        serviceId = garbageService.serviceId
                    )
                val doorphonePayer1ServiceId =
                    insertPayerService(
                        db,
                        payer = payer1Entity,
                        serviceId = doorphoneService.serviceId
                    )
                val phonePayer1ServiceId =
                    insertPayerService(db, payer = payer1Entity, serviceId = phoneService.serviceId)
                val ugsoPayer1ServiceId =
                    insertPayerService(db, payer = payer1Entity, serviceId = ugsoService.serviceId)
                // Meters:
                // Electricity
                val electricityPayer1Meter =
                    MeterEntity.populateElectricityMeter(context, payer1Entity.payerId)
                insertDefMeter(
                    db, listOf(electricityPayer1ServiceId), electricityPayer1Meter,
                    MeterTlEntity.populateElectricityMeterTl(
                        context,
                        electricityPayer1Meter.meterId
                    )
                )
                // values:
                insertDefMeterValue(
                    db, MeterValueEntity.populateElectricityMeterValue1(
                        electricityPayer1Meter.meterId,
                        currentDateTime.minusMonths(2).withDayOfMonth(1)
                    )
                )
                insertDefMeterValue(
                    db, MeterValueEntity.populateElectricityMeterValue2(
                        electricityPayer1Meter.meterId,
                        currentDateTime.minusMonths(1).withDayOfMonth(1)
                    )
                )
                insertDefMeterValue(
                    db, MeterValueEntity.populateElectricityMeterValue3(
                        electricityPayer1Meter.meterId,
                        currentDateTime.withDayOfMonth(1)
                    )
                )
                // Cold water
                val coldWaterPayer1Meter =
                    MeterEntity.populateColdWaterMeter(context, payer1Entity.payerId)
                insertDefMeter(
                    db, listOf(coldWaterPayer1ServiceId), coldWaterPayer1Meter,
                    MeterTlEntity.populateColdWaterMeterTl(context, coldWaterPayer1Meter.meterId)
                )
                // values:
                insertDefMeterValue(
                    db, MeterValueEntity.populateColdWaterMeterValue1(
                        coldWaterPayer1Meter.meterId,
                        currentDateTime.minusMonths(2).withDayOfMonth(1)
                    )
                )
                insertDefMeterValue(
                    db, MeterValueEntity.populateColdWaterMeterValue2(
                        coldWaterPayer1Meter.meterId,
                        currentDateTime.minusMonths(1).withDayOfMonth(1)
                    )
                )
                insertDefMeterValue(
                    db, MeterValueEntity.populateColdWaterMeterValue3(
                        coldWaterPayer1Meter.meterId,
                        currentDateTime.withDayOfMonth(1)
                    )
                )
                // Hot water
                val hotWaterPayer1Meter =
                    MeterEntity.populateHotWaterMeter(context, payer1Entity.payerId)
                insertDefMeter(
                    db, listOf(hotWaterPayer1ServiceId, wastePayer1ServiceId), hotWaterPayer1Meter,
                    MeterTlEntity.populateHotWaterMeterTl(context, hotWaterPayer1Meter.meterId)
                )
                // Heating
                val heatingPayer1Meter =
                    MeterEntity.populateHeatingMeter(context, payer1Entity.payerId)
                insertDefMeter(
                    db, listOf(heatingPayer1ServiceId), heatingPayer1Meter,
                    MeterTlEntity.populateHeatingMeterTl(
                        context,
                        heatingPayer1Meter.meterId
                    )
                )
                // values:
                insertDefMeterValue(
                    db, MeterValueEntity.populateHeatingMeterValue1(
                        heatingPayer1Meter.meterId,
                        currentDateTime.minusMonths(2).withDayOfMonth(1)
                    )
                )
                insertDefMeterValue(
                    db, MeterValueEntity.populateHeatingMeterValue2(
                        heatingPayer1Meter.meterId,
                        currentDateTime.minusMonths(1).withDayOfMonth(1)
                    )
                )
                insertDefMeterValue(
                    db, MeterValueEntity.populateHeatingMeterValue3(
                        heatingPayer1Meter.meterId,
                        currentDateTime.withDayOfMonth(1)
                    )
                )
                // Payer 1 rates:
                // rent
                insertDefRate(
                    db, RateEntity.populateRentRateForPayer(
                        rentService.serviceId,
                        rentPayer1ServiceId
                    )
                )
                // heating
                // garbage
                insertDefRate(
                    db, RateEntity.populateGarbageRateForPayer(
                        garbageService.serviceId,
                        garbagePayer1ServiceId
                    )
                )
                // doorphone
                val doorphoneRateId = insertDefRate(
                    db, RateEntity.populateDoorphoneRateForPayer(
                        doorphoneService.serviceId,
                        doorphonePayer1ServiceId
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
                    insertPayerService(db, payer = payer2Entity, serviceId = rentService.serviceId)
                val electricityPayer2ServiceId =
                    insertPayerService(
                        db, payer = payer2Entity,
                        serviceId = electricityService.serviceId,
                        isPrivilege = true
                    )
                val gasPayer2ServiceId =
                    insertPayerService(db, payer = payer2Entity, serviceId = gasService.serviceId)
                val coldWaterPayer2ServiceId =
                    insertPayerService(
                        db,
                        payer = payer2Entity,
                        serviceId = coldWaterService.serviceId
                    )
                val wastePayer2ServiceId =
                    insertPayerService(db, payer = payer2Entity, serviceId = wasteService.serviceId)
                val heatingPayer2ServiceId =
                    insertPayerService(
                        db,
                        payer = payer2Entity,
                        serviceId = heatingService.serviceId
                    )
                val hotWaterPayer2ServiceId =
                    insertPayerService(
                        db,
                        payer = payer2Entity,
                        serviceId = hotWaterService.serviceId
                    )
                val garbagePayer2ServiceId =
                    insertPayerService(
                        db,
                        payer = payer2Entity,
                        serviceId = garbageService.serviceId
                    )
                val doorphonePayer2ServiceId =
                    insertPayerService(
                        db,
                        payer = payer2Entity,
                        serviceId = doorphoneService.serviceId
                    )
                val phonePayer2ServiceId =
                    insertPayerService(db, payer = payer2Entity, serviceId = phoneService.serviceId)
                val ugsoPayer2ServiceId =
                    insertPayerService(db, payer = payer2Entity, serviceId = ugsoService.serviceId)
                // Meters:
                // Electricity
                val electricityPayer2Meter =
                    MeterEntity.populateElectricityMeter(context, payer2Entity.payerId)
                insertDefMeter(
                    db, listOf(electricityPayer2ServiceId), electricityPayer2Meter,
                    MeterTlEntity.populateElectricityMeterTl(
                        context,
                        electricityPayer2Meter.meterId
                    )
                )
                // values:
                insertDefMeterValue(
                    db, MeterValueEntity.populateElectricityMeterValue1(
                        electricityPayer2Meter.meterId,
                        currentDateTime.minusMonths(2).withDayOfMonth(1)
                    )
                )
                insertDefMeterValue(
                    db, MeterValueEntity.populateElectricityMeterValue2(
                        electricityPayer2Meter.meterId,
                        currentDateTime.minusMonths(1).withDayOfMonth(1)
                    )
                )
                insertDefMeterValue(
                    db, MeterValueEntity.populateElectricityMeterValue3(
                        electricityPayer2Meter.meterId,
                        currentDateTime.withDayOfMonth(1)
                    )
                )
                // Cold water
                val coldWaterPayer2Meter =
                    MeterEntity.populateColdWaterMeter(context, payer2Entity.payerId)
                insertDefMeter(
                    db, listOf(coldWaterPayer2ServiceId), coldWaterPayer2Meter,
                    MeterTlEntity.populateColdWaterMeterTl(context, coldWaterPayer2Meter.meterId)
                )
                // values:
                insertDefMeterValue(
                    db, MeterValueEntity.populateColdWaterMeterValue1(
                        coldWaterPayer2Meter.meterId,
                        currentDateTime.minusMonths(2).withDayOfMonth(1)
                    )
                )
                insertDefMeterValue(
                    db, MeterValueEntity.populateColdWaterMeterValue2(
                        coldWaterPayer2Meter.meterId,
                        currentDateTime.minusMonths(1).withDayOfMonth(1)
                    )
                )
                insertDefMeterValue(
                    db, MeterValueEntity.populateColdWaterMeterValue3(
                        coldWaterPayer2Meter.meterId,
                        currentDateTime.withDayOfMonth(1)
                    )
                )
                // Hot water
                val hotWaterPayer2Meter =
                    MeterEntity.populateHotWaterMeter(context, payer2Entity.payerId)
                insertDefMeter(
                    db, listOf(hotWaterPayer2ServiceId, wastePayer2ServiceId), hotWaterPayer2Meter,
                    MeterTlEntity.populateHotWaterMeterTl(context, hotWaterPayer2Meter.meterId)
                )
                // values:
                insertDefMeterValue(
                    db, MeterValueEntity.populateHotWaterMeterValue1(
                        hotWaterPayer2Meter.meterId,
                        currentDateTime.minusMonths(2).withDayOfMonth(1)
                    )
                )
                insertDefMeterValue(
                    db, MeterValueEntity.populateHotWaterMeterValue2(
                        hotWaterPayer2Meter.meterId,
                        currentDateTime.minusMonths(1).withDayOfMonth(1)
                    )
                )
                insertDefMeterValue(
                    db, MeterValueEntity.populateHotWaterMeterValue3(
                        hotWaterPayer2Meter.meterId,
                        currentDateTime.withDayOfMonth(1)
                    )
                )
                // Payer 2 rates:
                // rent
                insertDefRate(
                    db, RateEntity.populateRentRateForPayer(
                        rentService.serviceId,
                        rentPayer2ServiceId
                    )
                )
                // heating
                insertDefRate(
                    db, RateEntity.populateHeatingRateForPayer(
                        heatingService.serviceId,
                        heatingPayer2ServiceId
                    )
                )
                // garbage
                insertDefRate(
                    db, RateEntity.populateGarbageRateForPayer(
                        garbageService.serviceId,
                        garbagePayer2ServiceId
                    )
                )
                // doorphone
                insertDefRate(
                    db, RateEntity.populateDoorphoneRateForPayer(
                        doorphoneService.serviceId,
                        doorphonePayer2ServiceId
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

        private fun insertDefService(
            db: SupportSQLiteDatabase,
            service: ServiceEntity,
            textContent: ServiceTlEntity
        ) {
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
            isPrivilege: Boolean = false, isAllocateRate: Boolean = false
        ): UUID {
            val payerService =
                PayerServiceCrossRefEntity(
                    payersId = payer.payerId, servicesId = serviceId, isPrivileges = isPrivilege,
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

        private fun insertDefMeter(
            db: SupportSQLiteDatabase, payerServiceIds: List<UUID>,
            meter: MeterEntity, textContent: MeterTlEntity
        ) {
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
            for (payerServiceId in payerServiceIds) {
                val payerServiceMeter =
                    PayerServiceMeterCrossRefEntity(
                        payersServicesId = payerServiceId,
                        metersId = meter.meterId
                    )
                db.insert(
                    PayerServiceMeterCrossRefEntity.TABLE_NAME,
                    SQLiteDatabase.CONFLICT_REPLACE,
                    Mapper.toContentValues(payerServiceMeter)
                )
                Timber.tag(TAG).i(
                    "Default meter imported: {\"meter\": {%s}, \"tl\": {%s}, \"psm\": {%s}}",
                    jsonLogger?.toJson(meter),
                    jsonLogger?.toJson(textContent),
                    jsonLogger?.toJson(payerServiceMeter)
                )
            }
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