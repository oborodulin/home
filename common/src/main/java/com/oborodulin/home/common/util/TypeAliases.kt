package com.oborodulin.home.common.util

import androidx.compose.ui.text.input.TextFieldValue
import com.oborodulin.home.common.ui.model.ListItemModel

typealias OnValueChange = (value: String) -> Unit
typealias OnTextFieldValueChange = (TextFieldValue) -> Unit
typealias OnImeKeyAction = () -> Unit
typealias OnListItemEvent = (ListItemModel) -> Unit

