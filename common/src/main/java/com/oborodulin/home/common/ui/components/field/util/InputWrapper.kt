package com.oborodulin.home.common.ui.components.field.util

import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class InputWrapper(
    val value: String = "",
    @StringRes val errorId: Int? = null,
    val errorMsg: String? = null,
    val isEmpty: Boolean = true
) : Parcelable