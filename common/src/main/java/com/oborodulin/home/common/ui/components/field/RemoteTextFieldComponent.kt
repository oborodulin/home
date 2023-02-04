package com.oborodulin.home.common.ui.components.field

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oborodulin.home.common.R
import com.oborodulin.home.common.ui.components.field.util.InputWrapper
import com.oborodulin.home.common.ui.components.field.util.OnImeKeyAction
import com.oborodulin.home.common.ui.components.field.util.OnTextFieldValueChange
import com.oborodulin.home.common.ui.components.field.util.OnValueChange
import com.oborodulin.home.common.ui.theme.HomeComposableTheme
import timber.log.Timber

private const val TAG = "Common.ui.RemoteTextFieldComponent"

@Composable
fun RemoteTextFieldComponent(
    modifier: Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    inputWrapper: InputWrapper,
    fieldValue: TextFieldValue,
    @StringRes labelResId: Int? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    maxLines: Int = Int.MAX_VALUE,
    keyboardOptions: KeyboardOptions = remember {
        KeyboardOptions.Default
    },
    visualTransformation: VisualTransformation = remember {
        VisualTransformation.None
    },
    onValueChange: OnTextFieldValueChange,
    onImeKeyAction: OnImeKeyAction,
    colors: TextFieldColors = TextFieldDefaults.outlinedTextFieldColors()
) {
    Timber.tag(TAG).d("RemoteTextFieldComponent(...) called: fieldValue = %s", fieldValue)
    Column {
        OutlinedTextField(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 8.dp),//.weight(1f),
            enabled = enabled,
            readOnly = readOnly,
            value = fieldValue,
            onValueChange = {
                onValueChange(it)
            },
            label = { labelResId?.let { Text(stringResource(it)) } },
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            maxLines = maxLines,
            isError = inputWrapper.errorId != null,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = remember {
                KeyboardActions(onAny = { onImeKeyAction() })
            },
            colors = colors
        )
        val errorMessage =
            if (inputWrapper.errorId != null) stringResource(inputWrapper.errorId) else inputWrapper.errorMsg
        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colors.error,
                style = MaterialTheme.typography.caption,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

@Preview(name = "Night Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Day Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun PreviewRemoteTextFieldComponent() {
    HomeComposableTheme {
        Surface {
            RemoteTextFieldComponent(modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
                inputWrapper = InputWrapper(
                    value = stringResource(R.string.preview_blank_text_field_val),
                    errorId = R.string.preview_blank_text_field_err
                ),
                fieldValue = TextFieldValue(
                    text = stringResource(R.string.preview_blank_text_field_val),
                    selection = TextRange(stringResource(R.string.preview_blank_text_field_val).length)
                ),
                labelResId = R.string.preview_blank_text_field_lbl,
                onValueChange = {},
                onImeKeyAction = {})
        }
    }
}
