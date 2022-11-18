package com.oborodulin.home.data.local.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.annotation.StringRes
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import com.oborodulin.home.common.util.Mapper
import com.oborodulin.home.data.R
import com.oborodulin.home.data.local.db.converters.HomeTypeConverters
import com.oborodulin.home.data.local.db.dao.MeterDao
import com.oborodulin.home.data.local.db.dao.PayerDao
import com.oborodulin.home.data.local.db.dao.RateDao
import com.oborodulin.home.data.local.db.dao.ServiceDao
import com.oborodulin.home.data.local.db.entities.*
import com.oborodulin.home.data.local.db.views.MeterView
import com.oborodulin.home.data.local.db.views.ServiceView
import com.oborodulin.home.data.util.Constants
import com.oborodulin.home.data.util.ServiceType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.math.BigDecimal
import java.text.SimpleDateFormat
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
    views = [MeterView::class, ServiceView::class],
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
                            .addCallback(DatabaseCallback(context, instance, jsonLogger))
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
                        .addCallback(DatabaseCallback(context, instance, jsonLogger))
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
    private val instance: HomeDatabase?,
    private val jsonLogger: Gson
) :
    RoomDatabase.Callback() {

    class PayerServiceId(val serviceId: UUID, val payerServiceId: UUID)

    private val res = context.resources

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        Timber.tag(TAG).i("Database onCreate() called")
        // moving to a new thread
        // Executors.newSingleThreadExecutor().execute{f}
        CoroutineScope(Dispatchers.IO).launch()
        {
            db.beginTransaction()
            try {
                // Default payer
                val payerEntity = PayerEntity(
                    ercCode = res.getString(R.string.def_payer_erc_code),
                    fullName = res.getString(R.string.def_payer_full_name),
                    address = res.getString(R.string.def_payer_address),
                    paymentDay = 20,
                )
                db.insert(
                    PayerEntity.TABLE_NAME,
                    SQLiteDatabase.CONFLICT_REPLACE,
                    Mapper.toContentValues(payerEntity)
                )
                Timber.tag(TAG)
                    .i("Default PayerEntity imported: {${jsonLogger.toJson(payerEntity)}}")
                // Default services:
                // rent
                val rentServiceId = insertDefService(
                    db = db, payer = payerEntity, nameResId = R.string.service_rent,
                    pos = 1, type = ServiceType.RENT
                )
                // electricity
                val electricityServiceId = insertDefService(
                    db = db, payer = payerEntity, nameResId = R.string.service_electricity, pos = 2,
                    type = ServiceType.ELECRICITY,
                    measureUnitResId = com.oborodulin.home.common.R.string.kWh_unit
                )
                // gas
                val gasServiceId = insertDefService(
                    db = db, payer = payerEntity, nameResId = R.string.service_gas, pos = 3,
                    type = ServiceType.GAS,
                    measureUnitResId = com.oborodulin.home.common.R.string.m3_unit
                )
                // cold water
                val coldWaterServiceId = insertDefService(
                    db = db, payer = payerEntity, nameResId = R.string.service_cold_water, pos = 4,
                    type = ServiceType.COLD_WATER,
                    measureUnitResId = com.oborodulin.home.common.R.string.m3_unit
                )
                // waste
                val wasteServiceId = insertDefService(
                    db = db, payer = payerEntity, nameResId = R.string.service_waste, pos = 5,
                    type = ServiceType.WASTE,
                    measureUnitResId = com.oborodulin.home.common.R.string.m3_unit
                )
                // heating
                val heatingServiceId = insertDefService(
                    db = db, payer = payerEntity, nameResId = R.string.service_heating, pos = 6,
                    type = ServiceType.HEATING,
                    measureUnitResId = com.oborodulin.home.common.R.string.Gcal_unit
                )
                // hot water
                val hotWaterServiceId = insertDefService(
                    db = db, payer = payerEntity, nameResId = R.string.service_hot_water, pos = 7,
                    type = ServiceType.HOT_WATER,
                    measureUnitResId = com.oborodulin.home.common.R.string.m3_unit
                )
                // garbage
                val garbageServiceId = insertDefService(
                    db = db, payer = payerEntity, nameResId = R.string.service_garbage, pos = 8,
                    type = ServiceType.GARBAGE
                )
                // doorphone
                val doorphoneServiceId = insertDefService(
                    db = db, payer = payerEntity, nameResId = R.string.service_doorphone,
                    pos = 9, type = ServiceType.DOORPHONE
                )
                // phone
                val phoneServiceId = insertDefService(
                    db = db, payer = payerEntity, nameResId = R.string.service_phone, pos = 10,
                    type = ServiceType.PHONE
                )
                // ugso
                val ugsoServiceId = insertDefService(
                    db = db, payer = payerEntity, nameResId = R.string.service_ugso, pos = 10,
                    type = ServiceType.USGO
                )
                // Meters:
                // electricity
                val electricityMeterId = insertDefMeter(
                    db = db,
                    payersServicesId = electricityServiceId.payerServiceId,
                    maxValue = BigDecimal.valueOf(9999),
                    measureUnitResId = com.oborodulin.home.common.R.string.kWh_unit
                )
                insertDefMeterValue(
                    db = db, metersId = electricityMeterId,
                    valueDate = SimpleDateFormat("yyyy-MM-dd").parse("2022-06-19"),
                    meterValue = BigDecimal.valueOf(9532)
                )
                insertDefMeterValue(
                    db = db, metersId = electricityMeterId,
                    valueDate = SimpleDateFormat("yyyy-MM-dd").parse("2022-07-01"),
                    meterValue = BigDecimal.valueOf(9558)
                )
                insertDefMeterValue(
                    db = db, metersId = electricityMeterId,
                    valueDate = SimpleDateFormat("yyyy-MM-dd").parse("2022-08-01"),
                    meterValue = BigDecimal.valueOf(9628)
                )
                // cold water
                val coldWaterMeterId = insertDefMeter(
                    db = db,
                    payersServicesId = coldWaterServiceId.payerServiceId,
                    maxValue = BigDecimal.valueOf(99999.999),
                    measureUnitResId = com.oborodulin.home.common.R.string.m3_unit
                )
                insertDefMeterValue(
                    db = db, metersId = coldWaterMeterId,
                    valueDate = SimpleDateFormat("yyyy-MM-dd").parse("2022-06-19"),
                    meterValue = BigDecimal.valueOf(1538)
                )
                insertDefMeterValue(
                    db = db, metersId = coldWaterMeterId,
                    valueDate = SimpleDateFormat("yyyy-MM-dd").parse("2022-07-01"),
                    meterValue = BigDecimal.valueOf(1542)
                )
                insertDefMeterValue(
                    db = db, metersId = coldWaterMeterId,
                    valueDate = SimpleDateFormat("yyyy-MM-dd").parse("2022-08-01"),
                    meterValue = BigDecimal.valueOf(1553)
                )
                // hot water
                val hotWaterMeterId = insertDefMeter(
                    db = db,
                    payersServicesId = hotWaterServiceId.payerServiceId,
                    maxValue = BigDecimal.valueOf(99999.999),
                    measureUnitResId = com.oborodulin.home.common.R.string.m3_unit
                )
                // Default rates:
                // rent
                insertDefRate(
                    db = db, servicesId = rentServiceId.serviceId,
                    payersServicesId = rentServiceId.payerServiceId,
                    rateValue = BigDecimal.valueOf(4.62)
                )
                // electricity
                insertDefRate(
                    db = db, servicesId = electricityServiceId.serviceId,
                    fromMeterValue = BigDecimal.ZERO, toMeterValue = BigDecimal.valueOf(150),
                    rateValue = BigDecimal.valueOf(1.56)
                )
                insertDefRate(
                    db = db,
                    servicesId = electricityServiceId.serviceId,
                    fromMeterValue = BigDecimal.valueOf(150),
                    toMeterValue = BigDecimal.valueOf(800),
                    rateValue = BigDecimal.valueOf(2.12)
                )
                insertDefRate(
                    db = db, servicesId = electricityServiceId.serviceId,
                    fromMeterValue = BigDecimal.valueOf(800),
                    rateValue = BigDecimal.valueOf(3.21)
                )
                insertDefRate(
                    db = db, servicesId = electricityServiceId.serviceId,
                    isPrivileges = true, rateValue = BigDecimal.valueOf(0.92),
                )
                // gas
                insertDefRate(
                    db = db, servicesId = gasServiceId.serviceId,
                    isPerPerson = true, rateValue = BigDecimal.valueOf(18.05)
                )
                // cold water
                insertDefRate(
                    db = db, servicesId = coldWaterServiceId.serviceId,
                    rateValue = BigDecimal.valueOf(25.02)
                )
                // waste
                insertDefRate(
                    db = db, servicesId = wasteServiceId.serviceId,
                    rateValue = BigDecimal.valueOf(11.61)
                )
                // heating
                insertDefRate(
                    db = db, servicesId = heatingServiceId.serviceId,
                    payersServicesId = heatingServiceId.payerServiceId,
                    rateValue = BigDecimal.valueOf(14.76)
                )
                // hot water
                insertDefRate(
                    db = db, servicesId = hotWaterServiceId.serviceId,
                    rateValue = BigDecimal.valueOf(77.67)
                )
                // garbage
                insertDefRate(
                    db = db, servicesId = garbageServiceId.serviceId,
                    payersServicesId = garbageServiceId.payerServiceId,
                    isPerPerson = true, rateValue = BigDecimal.valueOf(15.73)
                )
                // doorphone
                val doorphoneRateId = insertDefRate(
                    db = db, servicesId = garbageServiceId.serviceId,
                    payersServicesId = garbageServiceId.payerServiceId,
                    isPerPerson = true, rateValue = BigDecimal.valueOf(15.73)
                )
                // phone
                // ugso

                // Rate promotions:
                val doorphoneRatePromotion = RatePromotionEntity(
                    ratesId = doorphoneRateId, paymentMonths = 10,
                    isPrevRate = true
                )
                db.insert(
                    RatePromotionEntity.TABLE_NAME,
                    SQLiteDatabase.CONFLICT_REPLACE,
                    Mapper.toContentValues(doorphoneRatePromotion)
                )
                Timber.tag(TAG)
                    .i("Default rate promotion imported: {${jsonLogger.toJson(doorphoneRatePromotion)}}")
                db.setTransactionSuccessful()
/*
            val isImport: Boolean = true
            if (isImport) {
            instance?.payerDao()?.add(payerEntity)
            }

 */
            } finally {
                db.endTransaction()
            }
        }
    }

    private fun insertDefService(
        db: SupportSQLiteDatabase, payer: PayerEntity, @StringRes nameResId: Int, pos: Int,
        type: ServiceType, @StringRes measureUnitResId: Int? = null
    ): PayerServiceId {
        val service = ServiceEntity(pos = pos, type = type)
        val serviceTl =
            ServiceTlEntity(
                name = res.getString(nameResId),
                measureUnit = measureUnitResId?.let {
                    res.getString(it)
                },
                servicesId = service.serviceId,
                localeCode = com.oborodulin.home.common.util.Constants.LANGUAGE_RU
            )
        val payerService =
            PayerServiceCrossRefEntity(payersId = payer.payerId, servicesId = service.serviceId)
        db.insert(
            ServiceEntity.TABLE_NAME,
            SQLiteDatabase.CONFLICT_REPLACE,
            Mapper.toContentValues(service)
        )
        db.insert(
            ServiceTlEntity.TABLE_NAME,
            SQLiteDatabase.CONFLICT_REPLACE,
            Mapper.toContentValues(serviceTl)
        )
        db.insert(
            PayerServiceCrossRefEntity.TABLE_NAME,
            SQLiteDatabase.CONFLICT_REPLACE,
            Mapper.toContentValues(payerService)
        )
        Timber.tag(TAG)
            .i(
                "Default service imported: {\"service\": {${jsonLogger.toJson(service)}}, " +
                        "\"tl\": {${jsonLogger.toJson(serviceTl)}}, " +
                        "\"payerService\": {${jsonLogger.toJson(payerService)}}}"
            )
        return PayerServiceId(service.serviceId, payerService.payerServiceId)
    }

    private fun insertDefMeter(
        db: SupportSQLiteDatabase, payersServicesId: UUID, maxValue: BigDecimal,
        @StringRes measureUnitResId: Int? = null
    ): UUID {
        val meter = MeterEntity(
            payersServicesId = payersServicesId,
            num = res.getString(R.string.def_meter_num),
            maxValue = maxValue
        )
        val meterTl = MeterTlEntity(
            metersId = meter.meterId,
            measureUnit = measureUnitResId?.let {
                res.getString(it)
            },
            localeCode = com.oborodulin.home.common.util.Constants.LANGUAGE_RU
        )
        db.insert(
            MeterEntity.TABLE_NAME,
            SQLiteDatabase.CONFLICT_REPLACE,
            Mapper.toContentValues(meter)
        )
        db.insert(
            MeterTlEntity.TABLE_NAME,
            SQLiteDatabase.CONFLICT_REPLACE,
            Mapper.toContentValues(meterTl)
        )
        Timber.tag(TAG).i(
            "Default meter imported: {\"meter\": {${jsonLogger.toJson(meter)}}, " +
                    "\"tl\": {${jsonLogger.toJson(meterTl)}}}"
        )
        return meter.meterId
    }

    private fun insertDefMeterValue(
        db: SupportSQLiteDatabase, metersId: UUID, valueDate: Date, meterValue: BigDecimal
    ) {
        val meterValue = MeterValueEntity(
            metersId = metersId,
            valueDate = valueDate,
            meterValue = meterValue,
        )
        db.insert(
            MeterValueEntity.TABLE_NAME,
            SQLiteDatabase.CONFLICT_REPLACE,
            Mapper.toContentValues(meterValue)
        )
        Timber.tag(TAG).i("Default meter value imported: {${jsonLogger.toJson(meterValue)}}")
    }

    private fun insertDefRate(
        db: SupportSQLiteDatabase, servicesId: UUID, payersServicesId: UUID? = null,
        fromMeterValue: BigDecimal? = null, toMeterValue: BigDecimal? = null,
        rateValue: BigDecimal,
        isPerPerson: Boolean = false,
        isPrivileges: Boolean = false,
    ): UUID {
        val rate = RateEntity(
            servicesId = servicesId,
            payersServicesId = payersServicesId,
            fromMeterValue = fromMeterValue,
            toMeterValue = toMeterValue,
            rateValue = rateValue,
            isPerPerson = isPerPerson,
            isPrivileges = isPrivileges,
        )
        db.insert(
            RateEntity.TABLE_NAME,
            SQLiteDatabase.CONFLICT_REPLACE,
            Mapper.toContentValues(rate)
        )
        Timber.tag(TAG).i("Default rate imported: {${jsonLogger.toJson(rate)}}")
        return rate.rateId
    }
}