package com.oborodulin.home.presentation.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.oborodulin.home.presentation.R
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
    object Accounting : NavRoutes(
        ROUTE_ACCOUNTING,
        R.drawable.outline_account_balance_wallet_black_24,
        R.string.nav_item_accounting
    )

    object Billing : NavRoutes(
        ROUTE_BILLING,
        R.drawable.outline_monetization_on_black_24,
        R.string.nav_item_billing
    )

    object Metering : NavRoutes(
        ROUTE_METERING,
        R.drawable.outline_electric_meter_black_24,
        R.string.nav_item_metering
    )

    object Reporting :
        NavRoutes(ROUTE_REPORTING, R.drawable.outline_receipt_black_24, R.string.nav_item_reporting)

    object Payer : NavRoutes(
        String.format(ROUTE_PAYER_DETAIL, "{$ARG_PAYER_ID}"),
        R.drawable.outline_person_black_24,
        R.string.nav_item_payer_detail,
        arguments = listOf(navArgument(ARG_PAYER_ID) {
            type = NavType.StringType
        })
    ) {
        fun routeForPayer(payerInput: PayerInput) =
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
