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
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.oborodulin.home.common.R
import com.oborodulin.home.common.ui.components.field.util.InputWrapper
import com.oborodulin.home.common.ui.theme.HomeComposableTheme
import com.oborodulin.home.common.util.OnImeKeyAction
import com.oborodulin.home.common.util.OnValueChange

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ExposedDropdownMenuBoxComponent(
    modifier: Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = true,
    inputWrapper: InputWrapper,
    listItems: List<String> = listOf(),
    @StringRes labelResId: Int? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    maxLines: Int = Int.MAX_VALUE,
    keyboardOptions: KeyboardOptions = remember {
        KeyboardOptions.Default
    },
    visualTransformation: VisualTransformation = remember {
        VisualTransformation.None
    },
    onValueChange: OnValueChange,
    onImeKeyAction: OnImeKeyAction,
    colors: TextFieldColors = TextFieldDefaults.outlinedTextFieldColors()
) {
    val fieldValue = remember { mutableStateOf(inputWrapper.value) }
    var expanded by remember { mutableStateOf(false) }
    // the box
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {
        // text field
        Column {
            OutlinedTextField(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp, horizontal = 8.dp),//.weight(1f),
                enabled = enabled,
                readOnly = readOnly,
                value = fieldValue.value,
                onValueChange = {
                    fieldValue.value = it
                    onValueChange(it)
                },
                label = { labelResId?.let { Text(stringResource(it)) } },
                leadingIcon = leadingIcon,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded
                    )
                },
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
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            listItems.forEach { selectedOption ->
                // menu item
                DropdownMenuItem(onClick = {
                    fieldValue.value = selectedOption
                    expanded = false
                    onValueChange(selectedOption)
                }) {
                    Text(text = selectedOption)
                }
            }
        }
    }
}

@Preview(name = "Night Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Day Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun PreviewExposedDropdownMenuBoxComponent() {
    HomeComposableTheme {
        Surface {
            ExposedDropdownMenuBoxComponent(modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
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
