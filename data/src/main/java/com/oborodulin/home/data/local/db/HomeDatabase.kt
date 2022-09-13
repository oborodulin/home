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
import com.oborodulin.home.data.util.Constants
import com.oborodulin.home.data.util.ServiceType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.math.BigDecimal
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
    entities = [LanguageEntity::class, PayerEntity::class, ServiceEntity::class, ServiceTlEntity::class,
        PayerServiceEntity::class, RateEntity::class, RatePromotionEntity::class, MeterEntity::class,
        ReceiptEntity::class],
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
                // Languages and locales
                val langRuEntity = LanguageEntity(
                    localeCode = com.oborodulin.home.common.util.Constants.LANGUAGE_RU,
                    name = res.getString(R.string.lang_name_ru)
                )
                db.insert(
                    LanguageEntity.TABLE_NAME,
                    SQLiteDatabase.CONFLICT_REPLACE,
                    Mapper.toContentValues(langRuEntity)
                )
                Timber.tag(TAG)
                    .i("Default language imported: {${jsonLogger.toJson(langRuEntity)}}")
                val langEnEntity = LanguageEntity(
                    localeCode = com.oborodulin.home.common.util.Constants.LANGUAGE_EN,
                    name = res.getString(R.string.lang_name_en)
                )
                db.insert(
                    LanguageEntity.TABLE_NAME,
                    SQLiteDatabase.CONFLICT_REPLACE,
                    Mapper.toContentValues(langEnEntity)
                )
                Timber.tag(TAG)
                    .i("Default language imported: {${jsonLogger.toJson(langEnEntity)}}")
                // Default payer
                val payerEntity = PayerEntity(
                    ercCode = res.getString(R.string.def_payer_erc_code),
                    fullName = res.getString(R.string.def_payer_full_name),
                    address = res.getString(R.string.def_payer_address)
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
                    pos = 1, type = ServiceType.RENT, language = langRuEntity
                )
                // electricity
                val electricityServiceId = insertDefService(
                    db = db, payer = payerEntity, nameResId = R.string.service_electricity, pos = 2,
                    type = ServiceType.ELECRICITY,
                    measureUnitResId = com.oborodulin.home.common.R.string.kWh_unit,
                    language = langRuEntity
                )
                // gas
                val gasServiceId = insertDefService(
                    db = db, payer = payerEntity, nameResId = R.string.service_gas, pos = 3,
                    type = ServiceType.GAS,
                    measureUnitResId = com.oborodulin.home.common.R.string.m3_unit,
                    language = langRuEntity
                )
                // cold water
                val coldWaterServiceId = insertDefService(
                    db = db, payer = payerEntity, nameResId = R.string.service_cold_water, pos = 4,
                    type = ServiceType.COLD_WATER,
                    measureUnitResId = com.oborodulin.home.common.R.string.m3_unit,
                    language = langRuEntity
                )
                // waste
                val wasteServiceId = insertDefService(
                    db = db, payer = payerEntity, nameResId = R.string.service_waste, pos = 5,
                    type = ServiceType.WASTE,
                    measureUnitResId = com.oborodulin.home.common.R.string.m3_unit,
                    language = langRuEntity
                )
                // heating
                val heatingServiceId = insertDefService(
                    db = db, payer = payerEntity, nameResId = R.string.service_heating, pos = 6,
                    type = ServiceType.HEATING,
                    measureUnitResId = com.oborodulin.home.common.R.string.Gcal_unit,
                    language = langRuEntity
                )
                // hot water
                val hotWaterServiceId = insertDefService(
                    db = db, payer = payerEntity, nameResId = R.string.service_hot_water, pos = 7,
                    type = ServiceType.HOT_WATER,
                    measureUnitResId = com.oborodulin.home.common.R.string.m3_unit,
                    language = langRuEntity
                )
                // garbage
                val garbageServiceId = insertDefService(
                    db = db, payer = payerEntity, nameResId = R.string.service_garbage, pos = 8,
                    type = ServiceType.GARBAGE, language = langRuEntity
                )
                // doorphone
                val doorphoneServiceId = insertDefService(
                    db = db, payer = payerEntity, nameResId = R.string.service_doorphone,
                    pos = 9, type = ServiceType.DOORPHONE, language = langRuEntity
                )
                // phone
                val phoneServiceId = insertDefService(
                    db = db, payer = payerEntity, nameResId = R.string.service_phone, pos = 10,
                    type = ServiceType.PHONE, language = langRuEntity
                )
                // ugso
                val ugsoServiceId = insertDefService(
                    db = db, payer = payerEntity, nameResId = R.string.service_ugso, pos = 10,
                    type = ServiceType.USGO, language = langRuEntity
                )
                // Meters
                // electricity
                insertDefMeter(
                    db = db,
                    payerServicesId = electricityServiceId.payerServiceId,
                    maxValue = BigDecimal.valueOf(9999)
                )
                // cold water
                insertDefMeter(
                    db = db,
                    payerServicesId = coldWaterServiceId.payerServiceId,
                    maxValue = BigDecimal.valueOf(99999.999)
                )
                // hot water
                insertDefMeter(
                    db = db,
                    payerServicesId = hotWaterServiceId.payerServiceId,
                    maxValue = BigDecimal.valueOf(99999.999)
                )
                // Default rates:
                // rent
                insertDefRate(
                    db = db, servicesId = rentServiceId.serviceId,
                    payerServicesId = rentServiceId.payerServiceId,
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
                    payerServicesId = heatingServiceId.payerServiceId,
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
                    payerServicesId = garbageServiceId.payerServiceId,
                    isPerPerson = true, rateValue = BigDecimal.valueOf(15.73)
                )
                // doorphone
                val doorphoneRateId = insertDefRate(
                    db = db, servicesId = garbageServiceId.serviceId,
                    payerServicesId = garbageServiceId.payerServiceId,
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
        type: ServiceType, @StringRes measureUnitResId: Int? = null, language: LanguageEntity
    ): PayerServiceId {
        val service = ServiceEntity(pos = pos, type = type)
        val serviceTl =
            ServiceTlEntity(
                name = res.getString(nameResId),
                measureUnit = measureUnitResId?.let {
                    res.getString(it)
                },
                servicesId = service.id,
                languagesId = language.id
            )
        val payerService = PayerServiceEntity(payersId = payer.id, servicesId = service.id)
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
            PayerServiceEntity.TABLE_NAME,
            SQLiteDatabase.CONFLICT_REPLACE,
            Mapper.toContentValues(payerService)
        )
        Timber.tag(TAG)
            .i(
                "Default service imported: {\"service\": {${jsonLogger.toJson(service)}}, " +
                        "\"tl\": {${jsonLogger.toJson(serviceTl)}}, " +
                        "\"payerService\": {${jsonLogger.toJson(payerService)}}}"
            )
        return PayerServiceId(service.id, payerService.id)
    }

    private fun insertDefMeter(
        db: SupportSQLiteDatabase, payerServicesId: UUID, maxValue: BigDecimal
    ) {
        val meter = MeterEntity(
            payerServicesId = payerServicesId,
            num = res.getString(R.string.def_meter_num),
            maxValue = maxValue
        )
        db.insert(
            MeterEntity.TABLE_NAME,
            SQLiteDatabase.CONFLICT_REPLACE,
            Mapper.toContentValues(meter)
        )
        Timber.tag(TAG).i("Default meter imported: {${jsonLogger.toJson(meter)}}")
    }

    private fun insertDefRate(
        db: SupportSQLiteDatabase, servicesId: UUID, payerServicesId: UUID? = null,
        fromMeterValue: BigDecimal? = null, toMeterValue: BigDecimal? = null,
        rateValue: BigDecimal,
        isPerPerson: Boolean = false,
        isPrivileges: Boolean = false,
    ): UUID {
        val rate = RateEntity(
            servicesId = servicesId,
            payerServicesId = payerServicesId,
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
        return rate.id
    }
}