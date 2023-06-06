package com.oborodulin.home.presentation.navigation

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.oborodulin.home.presentation.R
import com.oborodulin.home.presentation.navigation.MainDestinations.ROUTE_ACCOUNTING
import com.oborodulin.home.presentation.navigation.MainDestinations.ROUTE_BILLING
import com.oborodulin.home.presentation.navigation.MainDestinations.ROUTE_HOME
import com.oborodulin.home.presentation.navigation.MainDestinations.ROUTE_METERING
import com.oborodulin.home.presentation.navigation.MainDestinations.ROUTE_PAYER
import com.oborodulin.home.presentation.navigation.MainDestinations.ROUTE_RATE
import com.oborodulin.home.presentation.navigation.MainDestinations.ROUTE_REPORTING
import com.oborodulin.home.presentation.navigation.MainDestinations.ROUTE_SERVICE
import timber.log.Timber
import java.util.UUID

private const val TAG = "Presentation.NavRoutes"

private const val ARG_PAYER_ID = "payerId"
private const val ARG_SERVICE_ID = "serviceId"
private const val ARG_RATE_ID = "rateId"

/**
 * Created by oborodulin on 12.December.2021
 */
sealed class NavRoutes constructor(
    val route: String,
    @DrawableRes open val iconResId: Int,
    @StringRes open val titleResId: Int,
    val arguments: List<NamedNavArgument> = emptyList()
) {
    object Home : NavRoutes(
        ROUTE_HOME,
        R.drawable.outline_account_balance_wallet_black_24,
        R.string.nav_item_accounting
    )

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
        R.drawable.ic_electric_meter_24,
        R.string.nav_item_metering
    )

    object Reporting :
        NavRoutes(ROUTE_REPORTING, R.drawable.outline_receipt_black_24, R.string.nav_item_reporting)

    object Payer : NavRoutes(
        String.format(ROUTE_PAYER, "{$ARG_PAYER_ID}"),
        R.drawable.outline_person_black_24,
        R.string.nav_item_payer,
        arguments = listOf(navArgument(ARG_PAYER_ID) {
            type = NavType.StringType
            nullable = true
            //defaultValue = null
        })
    ) {
        fun routeForPayer(payerInput: PayerInput? = null): String {
            val route = when (payerInput) {
                null -> baseRoute()
                else -> String.format(ROUTE_PAYER, payerInput.payerId)
            }
            //val route = String.format(ROUTE_PAYER, payerId)
            Timber.tag(TAG).d("Payer - routeForPayer(...): '%s'", route)
            return route
        }

        fun fromEntry(entry: NavBackStackEntry): PayerInput {
            val payerInput =
                PayerInput(UUID.fromString(entry.arguments?.getString(ARG_PAYER_ID) ?: ""))
            Timber.tag(TAG).d("Payer - fromEntry(...): '%s'", payerInput)
            return payerInput
        }
    }

    object Service : NavRoutes(
        String.format(ROUTE_SERVICE, "{$ARG_SERVICE_ID}"),
        R.drawable.outline_domain_black_24,
        R.string.nav_item_service,
        arguments = listOf(navArgument(ARG_SERVICE_ID) {
            type = NavType.StringType
            nullable = true
            //defaultValue = null
        })
    ) {
        fun routeForService(serviceInput: ServiceInput? = null): String {
            val route = when (serviceInput) {
                null -> baseRoute()
                else -> String.format(ROUTE_SERVICE, serviceInput.serviceId)
            }
            //val route = String.format(ROUTE_SERVICE, payerId)
            Timber.tag(TAG).d("Service - routeForService(...): '%s'", route)
            return route
        }

        fun fromEntry(entry: NavBackStackEntry): ServiceInput {
            val serviceInput =
                ServiceInput(UUID.fromString(entry.arguments?.getString(ARG_SERVICE_ID) ?: ""))
            Timber.tag(TAG).d("Service - fromEntry(...): '%s'", serviceInput)
            return serviceInput
        }
    }

    object Rate : NavRoutes(
        String.format(ROUTE_RATE, "{$ARG_RATE_ID}"),
        R.drawable.outline_domain_black_24,
        R.string.nav_item_rate,
        arguments = listOf(navArgument(ARG_RATE_ID) {
            type = NavType.StringType
            nullable = true
            //defaultValue = null
        })
    ) {
        fun routeForRate(rateInput: RateInput? = null): String {
            val route = when (rateInput) {
                null -> baseRoute()
                else -> String.format(ROUTE_RATE, rateInput.rateId)
            }
            //val route = String.format(ROUTE_RATE, payerId)
            Timber.tag(TAG).d("Rate - routeForRate(...): '%s'", route)
            return route
        }

        fun fromEntry(entry: NavBackStackEntry): RateInput {
            val rateInput =
                RateInput(UUID.fromString(entry.arguments?.getString(ARG_RATE_ID) ?: ""))
            Timber.tag(TAG).d("Rate - fromEntry(...): '%s'", rateInput)
            return rateInput
        }
    }

    fun baseRoute() = this.route.substringBefore('/')

    companion object {
        fun bottomNavBarRoutes() = listOf(Accounting, Billing, Metering, Reporting)

        fun isShowBackButton(route: String?) = when (route) {
            Payer.route -> true
            else -> false
        }

        fun titleByRoute(context: Context, route: String): String {
            return when (route) {
                Accounting.route -> context.getString(Accounting.titleResId)
                Payer.route -> context.getString(Payer.titleResId)
                Billing.route -> context.getString(Billing.titleResId)
                Metering.route -> context.getString(Metering.titleResId)
                Reporting.route -> context.getString(Reporting.titleResId)
                else -> ""
            }
        }
    }
}
