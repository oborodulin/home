package com.oborodulin.home.common.ui.components.dialog

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.oborodulin.home.common.R

@Composable
fun AlertDialogComponent(
    isShow: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null
) {

    if (isShow) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = onConfirm)
                { Text(text = stringResource(R.string.btn_ok_lbl)) }
            },
            dismissButton = {
                TextButton(onClick = onDismiss)
                { Text(text = stringResource(R.string.btn_cancel_lbl)) }
            },
            title = title,
            text = text
        )
    }
}