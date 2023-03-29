package com.oborodulin.home.data.util

/**
 * Created by tfakioglu on 13.December.2021
 */
object Constants {
    const val DATABASE_NAME = "home-database.sqlite"

    const val DEF_PAYMENT_DAY = 20
    const val DEF_PERSON_NUM = 1
    const val DEF_TWO_PERSON_NUM = 2

    const val DB_TRUE = 1
    const val DB_FALSE = 0
    const val DB_FRACT_SEC_TIME = "'%Y-%m-%dT%H:%M:%f'"
    const val DB_DAY_TIME = "'%Y-%m-%d'"
    const val TZ_TIME = "strftime('%Y-%m-%dT%H:%M:%S', datetime('now', 'localtime')) || printf('%+.2d:%.2d', ROUND((julianday('now', 'localtime') - julianday('now')) * 24), ABS(ROUND((julianday('now', 'localtime') - julianday('now')) * 24 * 60) % 60))"

    const val FMT_IS_PER_PERSON_EXPR = "'%d %s x %.2f %s = %.2f %s'"
    const val FMT_METER_VAL_EXPR = "'%.2f %s x %.2f %s = %.2f %s'"
    const val FMT_DEBT_EXPR = "'%.2f %s'"
    const val FMT_RATE_DEBT_EXPR = "'%.2f %s = %.2f %s'"
    const val FMT_OPT_FACTOR_EXPR = "'%.2f %s x '"
    const val FMT_PAYMENT_PERIOD_EXPR = "'%02d.%d: '"
    const val FMT_HEATING_METER_EXPR = "'%.5f %s x '"

    const val SRV_RENT_VAL = "'RENT'"
    const val SRV_ELECTRICITY_VAL = "'ELECTRICITY'"
    const val SRV_GAS_VAL = "'GAS'"
    const val SRV_COLD_WATER_VAL = "'COLD_WATER'"
    const val SRV_WASTE_VAL = "'WASTE'"
    const val SRV_HEATING_VAL = "'HEATING'"
    const val SRV_HOT_WATER_VAL = "'HOT_WATER'"
    const val SRV_GARBAGE_VAL = "'GARBAGE'"
    const val SRV_DOORPHONE_VAL = "'DOORPHONE'"
    const val SRV_PHONE_VAL = "'PHONE'"
    const val SRV_USGO_VAL = "'USGO'"
    const val SRV_INTERNET_VAL = "'INTERNET'"

    const val MTR_ELECTRICITY_VAL = "'ELECTRICITY'"
    const val MTR_GAS_VAL = "'GAS'"
    const val MTR_COLD_WATER_VAL = "'COLD_WATER'"
    const val MTR_HEATING_VAL = "'HEATING'"
    const val MTR_HOT_WATER_VAL = "'HOT_WATER'"
    const val MTR_NONE_VAL = "'NONE'"

    const val PRM_LANG_VAL = "'LANG'"
    const val PRM_CURRENCY_CODE_VAL = "'CURRENCY_CODE'"
    const val PRM_DAY_MU_VAL = "'DAY_MU'"
    const val PRM_MONTH_MU_VAL = "'MONTH_MU'"
    const val PRM_PERSON_NUM_MU_VAL = "'PERSON_NUM_MU'"
    const val PRM_TOTAL_AREA_MU_VAL = "'TOTAL_AREA_MU'"
    const val PRM_LIVING_SPACE_MU_VAL = "'LIVING_SPACE_MU'"
    const val PRM_HEATED_VOLUME_MU_VAL = "'HEATED_VOLUME_MU'"
    
/*
    const val SQL_PREV_METERS_VALUES_SUBQUERY = """
SELECT v.metersId, MAX(datetime(v.valueDate)) maxValueDate 
    FROM meter_values v JOIN meters m ON m.meterId = v.metersId
        JOIN payers_services AS ps ON ps.payerServiceId = m.payersServicesId
        JOIN payers AS p ON p.payerId = ps.payersId
    WHERE datetime(v.valueDate) <= CASE WHEN datetime('now') > datetime('now', 'start of month', '+' || (ifnull(p.paymentDay, ${DEF_PAYMENT_DAY}) - 1) || ' days')
            THEN datetime('now', 'start of month', '+' || (ifnull(p.paymentDay, ${DEF_PAYMENT_DAY}) - 1) || ' days')
            ELSE datetime('now', '-1 months', 'start of month', '+' || (ifnull(p.paymentDay, ${DEF_PAYMENT_DAY}) - 1) || ' days') END
GROUP BY v.metersId
"""
//.trimIndent()
//.replace("\n\\s+".toRegex(), "")

    const val TMDB_BASE_URL = "https://api.themoviedb.org/3/"

    private const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"
    private const val IMAGE_SIZE_W185 = "w185"
    private const val IMAGE_SIZE_W780 = "w780"

    const val CAST_AVATAR_URL = IMAGE_BASE_URL + IMAGE_SIZE_W185
    const val CAST_IMDB_URL = "https://www.imdb.com/name/"
    const val POSTER_URL = IMAGE_BASE_URL + IMAGE_SIZE_W185
    const val BACKDROP_URL = IMAGE_BASE_URL + IMAGE_SIZE_W780

 */
}