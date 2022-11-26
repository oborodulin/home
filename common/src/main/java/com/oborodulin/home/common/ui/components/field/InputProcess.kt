package com.oborodulin.home.common.ui.state

import android.content.Context
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import com.oborodulin.home.common.ui.components.field.InputFocusRequester
import com.oborodulin.home.common.ui.components.field.ScreenEvent
import com.oborodulin.home.common.util.toast
import timber.log.Timber

private const val TAG = "Common.ui.ValidateScreen"

@OptIn(ExperimentalComposeUiApi::class)
fun inputProcess(
    context: Context,
    focusManager: FocusManager,
    keyboardController: SoftwareKeyboardController?,
    event: ScreenEvent,
    focusRequesters: List<InputFocusRequester>
) {
    Timber.tag(TAG).d("ValidateScreen(...) called")
    when (event) {
        is ScreenEvent.ShowToast -> context.toast(event.messageId)
        is ScreenEvent.UpdateKeyboard -> {
            if (event.show) keyboardController?.show() else keyboardController?.hide()
        }
        is ScreenEvent.ClearFocus -> focusManager.clearFocus()
        is ScreenEvent.RequestFocus ->
            for (requester in focusRequesters)
                if (event.textFieldKey.key() == requester.fieldKey)
                    requester.focusRequester.requestFocus()
        is ScreenEvent.MoveFocus -> focusManager.moveFocus(event.direction)
    }
}
