package com.oborodulin.home.data.local.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
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
import com.oborodulin.home.data.local.db.views.MetersView
import com.oborodulin.home.data.local.db.views.PrevMetersValuesView
import com.oborodulin.home.data.local.db.views.ServicesView
import com.oborodulin.home.data.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
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
        RateEntity::class, RatePromotionEntity::class,
        MeterEntity::class, MeterTlEntity::class, MeterValueEntity::class, MeterVerificationEntity::class,
        ReceiptEntity::class],
    views = [MetersView::class, ServicesView::class, PrevMetersValuesView::class],
    version = 5
)
@TypeConverters(HomeTypeConverters::class)
abstract class HomeDatabase : RoomDatabase() {
    abstract fun payerDao(): PayerDao
    abstract fun serviceDao(): ServiceDao
    abstract fun meterDao(): MeterDao
    abstract fun rateDao(): RateDao

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
                            .addCallback(DatabaseCallback(context, jsonLogger))
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
                        .addCallback(DatabaseCallback(context, jsonLogger))
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
class DatabaseCallback(
    private val context: Context,
    private val jsonLogger: Gson
) :
    RoomDatabase.Callback() {

    //class PayerServiceId(val serviceId: UUID, val payerServiceId: UUID)

    private val res = context.resources
    private lateinit var db: SupportSQLiteDatabase

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        this.db = db
        Timber.tag(TAG).i("Database onCreate() called on thread '%s':", Thread.currentThread().name)
        // moving to a new thread
        // Executors.newSingleThreadExecutor().execute{f}
        //CoroutineScope(Dispatchers.IO).launch()
        //GlobalScope.launch(Dispatchers.Main)
        //{
        Timber.tag(TAG).i("Start thread '%s': prePopulateDb(...)", Thread.currentThread().name)
        prePopulateDb()
        //}
        Timber.tag(TAG).i("Database onCreate() ended on thread '%s':", Thread.currentThread().name)
    }

    private fun prePopulateDb() {
        this.db.beginTransaction()
        try {
            // Default payers
            // 1
            val payer1Entity = PayerEntity.populatePayer1(context)
            db.insert(
                PayerEntity.TABLE_NAME,
                SQLiteDatabase.CONFLICT_REPLACE,
                Mapper.toContentValues(payer1Entity)
            )
            Timber.tag(TAG)
                .i("Default 1 PayerEntity imported: {${jsonLogger.toJson(payer1Entity)}}")
            // 2
            val payer2Entity = PayerEntity.populatePayer2(context)
            db.insert(
                PayerEntity.TABLE_NAME,
                SQLiteDatabase.CONFLICT_REPLACE,
                Mapper.toContentValues(payer2Entity)
            )
            Timber.tag(TAG)
                .i("Default 2 PayerEntity imported: {${jsonLogger.toJson(payer2Entity)}}")
            // Default services:
            // rent
            val rentServiceId = insertDefService(
                ServiceEntity.populateRentService(),
                ServiceTlEntity.populateRentServiceTl(context)
            )
            // electricity
            val electricityServiceId = insertDefService(
                ServiceEntity.populateElectricityService(),
                ServiceTlEntity.populateElectricityServiceTl(context)
            )
            // gas
            val gasServiceId = insertDefService(
                ServiceEntity.populateGasService(),
                ServiceTlEntity.populateGasServiceTl(context)
            )
            // cold water
            val coldWaterServiceId = insertDefService(
                ServiceEntity.populateColdWaterService(),
                ServiceTlEntity.populateColdWaterServiceTl(context)
            )
            // waste
            val wasteServiceId = insertDefService(
                ServiceEntity.populateWasteService(),
                ServiceTlEntity.populateWasteServiceTl(context)
            )
            // heating
            val heatingServiceId = insertDefService(
                ServiceEntity.populateHeatingService(),
                ServiceTlEntity.populateHeatingServiceTl(context)
            )
            // hot water
            val hotWaterServiceId = insertDefService(
                ServiceEntity.populateHotWaterService(),
                ServiceTlEntity.populateHotWaterServiceTl(context)
            )
            // garbage
            val garbageServiceId = insertDefService(
                ServiceEntity.populateGarbageService(),
                ServiceTlEntity.populateGarbageServiceTl(context)
            )
            // doorphone
            val doorphoneServiceId = insertDefService(
                ServiceEntity.populateDoorphoneService(),
                ServiceTlEntity.populateDoorphoneServiceTl(context)
            )
            // phone
            val phoneServiceId = insertDefService(
                ServiceEntity.populatePhoneService(),
                ServiceTlEntity.populatePhoneServiceTl(context)
            )
            // ugso
            val ugsoServiceId = insertDefService(
                ServiceEntity.populateUgsoService(),
                ServiceTlEntity.populateUgsoServiceTl(context)
            )

            // Default rates:
            // electricity
            insertDefRate(RateEntity.populateElectricityRate1(electricityServiceId))
            insertDefRate(RateEntity.populateElectricityRate2(electricityServiceId))
            insertDefRate(RateEntity.populateElectricityRate3(electricityServiceId))
            insertDefRate(RateEntity.populateElectricityPrivilegesRate(electricityServiceId))
            // gas
            insertDefRate(RateEntity.populateGasRate(gasServiceId))
            // cold water
            insertDefRate(RateEntity.populateColdWaterRate(coldWaterServiceId))
            // waste
            insertDefRate(RateEntity.populateWasteRate(wasteServiceId))
            // hot water
            insertDefRate(RateEntity.populateHotWaterRate(hotWaterServiceId))

            // ==============================
            // FOR Payer 1:
            val rentPayer1ServiceId =
                insertPayerService(payer = payer1Entity, serviceId = rentServiceId)
            val electricityPayer1ServiceId =
                insertPayerService(payer = payer1Entity, serviceId = electricityServiceId)
            val gasPayer1ServiceId =
                insertPayerService(payer = payer1Entity, serviceId = gasServiceId)
            val coldWaterPayer1ServiceId =
                insertPayerService(payer = payer1Entity, serviceId = coldWaterServiceId)
            val wastePayer1ServiceId =
                insertPayerService(payer = payer1Entity, serviceId = wasteServiceId)
            val heatingPayer1ServiceId =
                insertPayerService(payer = payer1Entity, serviceId = heatingServiceId)
            val hotWaterPayer1ServiceId =
                insertPayerService(payer = payer1Entity, serviceId = hotWaterServiceId)
            val garbagePayer1ServiceId =
                insertPayerService(payer = payer1Entity, serviceId = garbageServiceId)
            val doorphonePayer1ServiceId =
                insertPayerService(payer = payer1Entity, serviceId = doorphoneServiceId)
            val phonePayer1ServiceId =
                insertPayerService(payer = payer1Entity, serviceId = phoneServiceId)
            val ugsoPayer1ServiceId =
                insertPayerService(payer = payer1Entity, serviceId = ugsoServiceId)
            // Meters:
            // electricity
            val electricityPayer1MeterId = insertDefMeter(
                MeterEntity.populateElectricityMeter(context, electricityPayer1ServiceId),
                MeterTlEntity.populateElectricityMeterTl(context)
            )
            insertDefMeterValue(
                MeterValueEntity.populateElectricityMeterValue1(
                    electricityPayer1MeterId
                )
            )
            insertDefMeterValue(
                MeterValueEntity.populateElectricityMeterValue2(
                    electricityPayer1MeterId
                )
            )
            insertDefMeterValue(
                MeterValueEntity.populateElectricityMeterValue3(
                    electricityPayer1MeterId
                )
            )
            // cold water
            val coldWaterPayer1MeterId = insertDefMeter(
                MeterEntity.populateColdWaterMeter(context, coldWaterPayer1ServiceId),
                MeterTlEntity.populateColdWaterMeterTl(context)
            )
            insertDefMeterValue(
                MeterValueEntity.populateColdWaterMeterValue1(
                    coldWaterPayer1MeterId
                )
            )
            insertDefMeterValue(
                MeterValueEntity.populateColdWaterMeterValue2(
                    coldWaterPayer1MeterId
                )
            )
            insertDefMeterValue(
                MeterValueEntity.populateColdWaterMeterValue3(
                    coldWaterPayer1MeterId
                )
            )
            // hot water
            val hotWaterPayer1MeterId = insertDefMeter(
                MeterEntity.populateHotWaterMeter(context, hotWaterPayer1ServiceId),
                MeterTlEntity.populateHotWaterMeterTl(context)
            )
            // Payer 1 rates:
            // rent
            insertDefRate(
                RateEntity.populateRentRateForPayer(
                    rentServiceId,
                    rentPayer1ServiceId
                )
            )
            // heating
            insertDefRate(
                RateEntity.populateHeatingRateForPayer(
                    heatingServiceId,
                    heatingPayer1ServiceId
                )
            )
            // garbage
            insertDefRate(
                RateEntity.populateGarbageRateForPayer(
                    garbageServiceId,
                    garbagePayer1ServiceId
                )
            )
            // doorphone
            val doorphoneRateId = insertDefRate(
                RateEntity.populateDoorphoneRateForPayer(
                    doorphoneServiceId,
                    doorphonePayer1ServiceId
                )
            )
            // phone
            // ugso

            // Rate promotions:
            val doorphoneRatePromotion =
                RatePromotionEntity.populatePrevRatePromotion(doorphoneRateId)
            db.insert(
                RatePromotionEntity.TABLE_NAME,
                SQLiteDatabase.CONFLICT_REPLACE,
                Mapper.toContentValues(doorphoneRatePromotion)
            )
            Timber.tag(TAG)
                .i("Default rate promotion imported: {${jsonLogger.toJson(doorphoneRatePromotion)}}")

            // ==============================
            // FOR Payer 2:
            val rentPayer2ServiceId =
                insertPayerService(payer = payer2Entity, serviceId = rentServiceId)
            val electricityPayer2ServiceId =
                insertPayerService(payer = payer2Entity, serviceId = electricityServiceId)
            val gasPayer2ServiceId =
                insertPayerService(payer = payer2Entity, serviceId = gasServiceId)
            val coldWaterPayer2ServiceId =
                insertPayerService(payer = payer2Entity, serviceId = coldWaterServiceId)
            val wastePayer2ServiceId =
                insertPayerService(payer = payer2Entity, serviceId = wasteServiceId)
            val heatingPayer2ServiceId =
                insertPayerService(payer = payer2Entity, serviceId = heatingServiceId)
            val hotWaterPayer2ServiceId =
                insertPayerService(payer = payer2Entity, serviceId = hotWaterServiceId)
            val garbagePayer2ServiceId =
                insertPayerService(payer = payer2Entity, serviceId = garbageServiceId)
            val doorphonePayer2ServiceId =
                insertPayerService(payer = payer2Entity, serviceId = doorphoneServiceId)
            val phonePayer2ServiceId =
                insertPayerService(payer = payer2Entity, serviceId = phoneServiceId)
            val ugsoPayer2ServiceId =
                insertPayerService(payer = payer2Entity, serviceId = ugsoServiceId)
            // Meters:
            // electricity
            val electricityPayer2MeterId = insertDefMeter(
                MeterEntity.populateElectricityMeter(context, electricityPayer2ServiceId),
                MeterTlEntity.populateElectricityMeterTl(context)
            )
            insertDefMeterValue(
                MeterValueEntity.populateElectricityMeterValue2(
                    electricityPayer2MeterId
                )
            )
            insertDefMeterValue(
                MeterValueEntity.populateElectricityMeterValue2(
                    electricityPayer2MeterId
                )
            )
            insertDefMeterValue(
                MeterValueEntity.populateElectricityMeterValue3(
                    electricityPayer2MeterId
                )
            )
            // cold water
            val coldWaterPayer2MeterId = insertDefMeter(
                MeterEntity.populateColdWaterMeter(context, coldWaterPayer2ServiceId),
                MeterTlEntity.populateColdWaterMeterTl(context)
            )
            insertDefMeterValue(
                MeterValueEntity.populateColdWaterMeterValue2(
                    coldWaterPayer2MeterId
                )
            )
            insertDefMeterValue(
                MeterValueEntity.populateColdWaterMeterValue2(
                    coldWaterPayer2MeterId
                )
            )
            insertDefMeterValue(
                MeterValueEntity.populateColdWaterMeterValue3(
                    coldWaterPayer2MeterId
                )
            )
            // hot water
            val hotWaterPayer2MeterId = insertDefMeter(
                MeterEntity.populateHotWaterMeter(context, hotWaterPayer2ServiceId),
                MeterTlEntity.populateHotWaterMeterTl(context)
            )
            // Payer 2 rates:
            // rent
            insertDefRate(
                RateEntity.populateRentRateForPayer(
                    rentServiceId,
                    rentPayer2ServiceId
                )
            )
            // heating
            insertDefRate(
                RateEntity.populateHeatingRateForPayer(
                    heatingServiceId,
                    heatingPayer2ServiceId
                )
            )
            // garbage
            insertDefRate(
                RateEntity.populateGarbageRateForPayer(
                    garbageServiceId,
                    garbagePayer2ServiceId
                )
            )
            // doorphone
            insertDefRate(
                RateEntity.populateDoorphoneRateForPayer(
                    doorphoneServiceId,
                    doorphonePayer2ServiceId
                )
            )
            // phone
            // ugso

            this.db.setTransactionSuccessful()
            /*
                        val isImport: Boolean = true
                        if (isImport) {
                        instance?.payerDao()?.add(payerEntity)
                        }

             */
        } finally {
            this.db.endTransaction()
        }
    }

    private fun insertDefService(
        service: ServiceEntity,
        textContent: ServiceTlEntity
    ): UUID {
        textContent.servicesId = service.serviceId
        this.db.insert(
            ServiceEntity.TABLE_NAME,
            SQLiteDatabase.CONFLICT_REPLACE,
            Mapper.toContentValues(service)
        )
        this.db.insert(
            ServiceTlEntity.TABLE_NAME,
            SQLiteDatabase.CONFLICT_REPLACE,
            Mapper.toContentValues(textContent)
        )
        Timber.tag(TAG)
            .i(
                "Default service imported: {\"service\": {${jsonLogger.toJson(service)}}, " +
                        "\"tl\": {${jsonLogger.toJson(textContent)}}}"
            )
        return service.serviceId
    }

    private fun insertPayerService(payer: PayerEntity, serviceId: UUID): UUID {
        val payerService =
            PayerServiceCrossRefEntity(payersId = payer.payerId, servicesId = serviceId)
        this.db.insert(
            PayerServiceCrossRefEntity.TABLE_NAME,
            SQLiteDatabase.CONFLICT_REPLACE,
            Mapper.toContentValues(payerService)
        )
        Timber.tag(TAG)
            .i(
                "Payer service imported: {\"payerService\": {${jsonLogger.toJson(payerService)}}}"
            )
        return payerService.payerServiceId
    }

    private fun insertDefMeter(meter: MeterEntity, textContent: MeterTlEntity): UUID {
        textContent.metersId = meter.meterId
        this.db.insert(
            MeterEntity.TABLE_NAME,
            SQLiteDatabase.CONFLICT_REPLACE,
            Mapper.toContentValues(meter)
        )
        this.db.insert(
            MeterTlEntity.TABLE_NAME,
            SQLiteDatabase.CONFLICT_REPLACE,
            Mapper.toContentValues(textContent)
        )
        Timber.tag(TAG).i(
            "Default meter imported: {\"meter\": {${jsonLogger.toJson(meter)}}, " +
                    "\"tl\": {${jsonLogger.toJson(textContent)}}}"
        )
        return meter.meterId
    }

    private fun insertDefMeterValue(meterValue: MeterValueEntity) {
        this.db.insert(
            MeterValueEntity.TABLE_NAME,
            SQLiteDatabase.CONFLICT_REPLACE,
            Mapper.toContentValues(meterValue)
        )
        Timber.tag(TAG).i("Default meter value imported: {${jsonLogger.toJson(meterValue)}}")
    }

    private fun insertDefRate(rate: RateEntity): UUID {
        this.db.insert(
            RateEntity.TABLE_NAME,
            SQLiteDatabase.CONFLICT_REPLACE,
            Mapper.toContentValues(rate)
        )
        Timber.tag(TAG).i("Default rate imported: {${jsonLogger.toJson(rate)}}")
        return rate.rateId
    }
}