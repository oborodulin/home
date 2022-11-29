package com.oborodulin.home.common.ui.components.field

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oborodulin.home.common.R
import com.oborodulin.home.common.ui.theme.HomeComposableTheme

@Composable
fun TextFieldComponent(
    modifier: Modifier,
    inputWrapper: InputWrapper,
    @StringRes labelResId: Int? = null,
    keyboardOptions: KeyboardOptions = remember {
        KeyboardOptions.Default
    },
    visualTransformation: VisualTransformation = remember {
        VisualTransformation.None
    },
    onValueChange: OnValueChange,
    onImeKeyAction: OnImeKeyAction
) {
    val fieldValue = remember {
        mutableStateOf(TextFieldValue(inputWrapper.value, TextRange(inputWrapper.value.length)))
    }
    Column {
        OutlinedTextField(
            modifier = modifier,
            value = fieldValue.value,
            onValueChange = {
                fieldValue.value = it
                onValueChange(it.text)
            },
            label = { labelResId?.let { Text(stringResource(it)) } },
            isError = inputWrapper.errorId != null,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = remember {
                KeyboardActions(onAny = { onImeKeyAction() })
            },
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
fun PreviewTextFieldComponent() {
    HomeComposableTheme {
        Surface {
            TextFieldComponent(modifier = Modifier.fillMaxWidth().height(60.dp),
                inputWrapper = InputWrapper(
                    value = stringResource(R.string.preview_blank_text_field_val),
                    errorId = R.string.preview_blank_text_field_err
                ),
                labelResId = R.string.preview_blank_text_field_lbl,
                onValueChange = {},
                onImeKeyAction = {})
        }
    }
}
