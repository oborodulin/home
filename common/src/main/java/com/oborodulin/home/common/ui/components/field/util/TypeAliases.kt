package com.oborodulin.home.common.ui.components.field.util

import androidx.compose.ui.text.input.TextFieldValue

typealias OnValueChange = (value: String) -> Unit
typealias OnTextFieldValueChange = (TextFieldValue) -> Unit
typealias OnImeKeyAction = () -> Unit
