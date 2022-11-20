package com.oborodulin.home.common.ui.components

import androidx.compose.ui.focus.FocusDirection


sealed class ScreenEvent {
    class ShowToast(val messageId: Int) : ScreenEvent()
    class UpdateKeyboard(val show: Boolean) : ScreenEvent()
    class RequestFocus(val textFieldKey: FocusableTextField) : ScreenEvent()
    object ClearFocus : ScreenEvent()
    class MoveFocus(val direction: FocusDirection = FocusDirection.Down) : ScreenEvent()
}
