package com.oborodulin.home.common.ui.navigation

import androidx.annotation.StringRes
import androidx.annotation.DrawableRes
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument
import java.util.*

private const val ROUTE_ACCOUNTING = "accounting"
private const val ROUTE_BILLING = "billing"
private const val ROUTE_METERING = "metering"
private const val ROUTE_REPORTING = "reporting"
private const val ROUTE_PAYER_DETAIL = "payer_detail/%s"
private const val ARG_PAYER_ID = "payerId"

/**
 * Created by oborodulin on 12.December.2021
 */
sealed class NavRoutes constructor(
    val route: String,
    @DrawableRes open val iconResId: Int,
    @StringRes open val titleResId: Int,
    val arguments: List<NamedNavArgument> = emptyList()
) {
    class NavAccountingScreen(
        @DrawableRes override val iconResId: Int,
        @StringRes override val titleResId: Int
    ) : NavRoutes(ROUTE_ACCOUNTING, iconResId, titleResId)

    class NavBillingScreen(
        @DrawableRes override val iconResId: Int,
        @StringRes override val titleResId: Int
    ) : NavRoutes(ROUTE_BILLING, iconResId, titleResId)


    class NavMeteringScreen(
        @DrawableRes override val iconResId: Int,
        @StringRes override val titleResId: Int
    ) : NavRoutes(ROUTE_METERING, iconResId, titleResId)

    class NavReportingScreen(
        @DrawableRes override val iconResId: Int,
        @StringRes override val titleResId: Int
    ) :
        NavRoutes(ROUTE_REPORTING, iconResId, titleResId)

    class NavPayerDetailScreen(
        @DrawableRes override val iconResId: Int,
        @StringRes override val titleResId: Int
    ) : NavRoutes(
        String.format(ROUTE_PAYER_DETAIL, "{$ARG_PAYER_ID}"),
        iconResId, titleResId,
        arguments = listOf(navArgument(ARG_PAYER_ID) {
            type = NavType.StringType
        })
    ) {
        fun routeForPayerDetail(payerInput: PayerInput) =
            String.format(ROUTE_PAYER_DETAIL, payerInput.payerId)

        fun fromEntry(entry: NavBackStackEntry): PayerInput {
            return PayerInput(
                UUID.fromString(
                    entry.arguments?.getString(
                        ARG_PAYER_ID
                    ) ?: ""
                )
            )
        }
    }

}
