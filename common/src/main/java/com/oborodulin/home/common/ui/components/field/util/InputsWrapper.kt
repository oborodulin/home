package com.oborodulin.home.common.ui.components.field.util

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class InputsWrapper(
    var inputs: MutableMap<String, InputWrapper> = mutableMapOf()
) : Parcelable