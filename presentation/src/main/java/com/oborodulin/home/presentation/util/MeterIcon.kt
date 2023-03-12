package com.oborodulin.home.presentation.util

import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.oborodulin.home.data.util.MeterType
import com.oborodulin.home.presentation.R

@Composable
fun MeterIcon(meterType: MeterType?) =
    Image(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp)),
        painter = painterResource(meterIconId(meterType)),
        contentDescription = ""
    )

fun meterIconId(meterType: MeterType?) =
    when (meterType) {
        MeterType.ELECTRICITY -> R.drawable.ic_electric_meter_36
        MeterType.GAS -> R.drawable.ic_gas_meter_36
        MeterType.COLD_WATER -> R.drawable.ic_water_meter_36
        MeterType.HEATING -> R.drawable.ic_thermometer_36
        MeterType.HOT_WATER -> R.drawable.ic_hot_water_meter_36
        else -> -1
    }
