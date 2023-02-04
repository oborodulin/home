package com.oborodulin.home.common.ui.components.field.util

import android.content.Context
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.SoftwareKeyboardController
import com.oborodulin.home.common.util.toast
import timber.log.Timber

private const val TAG = "Common.ui.inputProcess"

@OptIn(ExperimentalComposeUiApi::class)
fun inputProcess(
    context: Context,
    focusManager: FocusManager,
    keyboardController: SoftwareKeyboardController?,
    event: ScreenEvent,
    focusRequesters: Map<String, InputFocusRequester>
) {
    Timber.tag(TAG).d("inputProcess(...) called")
    when (event) {
        is ScreenEvent.ShowToast -> context.toast(event.messageId)
        is ScreenEvent.UpdateKeyboard -> {
            if (event.show) keyboardController?.show() else keyboardController?.hide()
        }
        is ScreenEvent.ClearFocus -> focusManager.clearFocus()
        is ScreenEvent.RequestFocus -> focusRequesters[event.textField.key()]?.focusRequester?.requestFocus()
        is ScreenEvent.MoveFocus -> focusManager.moveFocus(event.direction)
    }
}
