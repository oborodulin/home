package com.oborodulin.home.common.ui.components.items

import com.oborodulin.home.common.R

/**
 * Created by oborodulin on 12.December.2021
 */
sealed class NavigationItem(var route: String, var icon: Int, var title: String) {
    object Accounting : NavigationItem("Accounting", R.drawable.outline_account_balance_wallet_black_24, "Учёт")
    object Billing : NavigationItem("Billing", R.drawable.outline_monetization_on_black_24, "Тарифы")
    object Metering : NavigationItem("Metering", R.drawable.outline_electric_meter_black_24, "Приборы")
    object Reporting : NavigationItem("Reporting", R.drawable.outline_receipt_black_24, "Квитанции")
}
