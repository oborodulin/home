package com.oborodulin.home.accounting.ui.payer.single

import com.oborodulin.home.common.ui.components.field.util.Focusable

enum class PayerFields : Focusable {
    PAYER_ID,
    ERC_CODE,
    FULL_NAME,
    ADDRESS,
    TOTAL_AREA,
    LIVING_SPACE,
    HEATED_VOLUME,
    PAYMENT_DAY,
    PERSONS_NUM,
    IS_FAVORITE;

    override fun key(): String {
        return this.name
    }
}
