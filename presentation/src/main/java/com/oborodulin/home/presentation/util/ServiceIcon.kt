package com.oborodulin.home.presentation.util

import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.oborodulin.home.data.util.ServiceType
import com.oborodulin.home.presentation.R

@Composable
fun ServiceIcon(serviceType: ServiceType?) =
    Image(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp)),
        painter = painterResource(serviceIconId(serviceType)),
        contentDescription = ""
    )

fun serviceIconId(serviceType: ServiceType?) =
    when (serviceType) {
        ServiceType.RENT -> R.drawable.ic_cottage_36
        ServiceType.ELECTRICITY -> R.drawable.ic_electric_bolt_36
        ServiceType.GAS -> R.drawable.ic_fireplace_36
        ServiceType.COLD_WATER -> R.drawable.outline_water_drop_black_36
        ServiceType.WASTE -> R.drawable.ic_sewage_36
        ServiceType.HEATING -> R.drawable.ic_radiator_36
        ServiceType.HOT_WATER -> R.drawable.outline_opacity_black_36
        ServiceType.GARBAGE -> R.drawable.ic_delete_forever_36
        ServiceType.PHONE -> R.drawable.outline_phone_black_36
        ServiceType.DOORPHONE -> R.drawable.ic_doorbell_36
        ServiceType.USGO -> R.drawable.outline_security_black_36
        ServiceType.INTERNET -> R.drawable.outline_network_check_black_36
        else -> -1
    }
