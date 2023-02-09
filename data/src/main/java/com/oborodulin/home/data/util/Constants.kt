package com.oborodulin.home.data.util

/**
 * Created by tfakioglu on 13.December.2021
 */
object Constants {
    const val DATABASE_NAME = "home-database.sqlite"

    const val DEF_PAYMENT_DAY = 20

/*
    const val SQL_PREV_METERS_VALUES_SUBQUERY = """
SELECT v.metersId, MAX(datetime(v.valueDate)) maxValueDate 
    FROM meter_values v JOIN meters m ON m.meterId = v.metersId
        JOIN payers_services AS ps ON ps.payerServiceId = m.payersServicesId
        JOIN payers AS p ON p.payerId = ps.payersId
    WHERE datetime(v.valueDate) <= CASE WHEN datetime('now') > datetime('now', 'start of month', '+' || (IFNULL(p.paymentDay, ${DEF_PAYMENT_DAY}) - 1) || ' days')
            THEN datetime('now', 'start of month', '+' || (IFNULL(p.paymentDay, ${DEF_PAYMENT_DAY}) - 1) || ' days')
            ELSE datetime('now', '-1 months', 'start of month', '+' || (IFNULL(p.paymentDay, ${DEF_PAYMENT_DAY}) - 1) || ' days') END
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