package com.oborodulin.home.accounting.ui.payer.single

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.oborodulin.home.accounting.R
import com.oborodulin.home.accounting.ui.model.PayerModel
import com.oborodulin.home.common.ui.components.creditCardFilter
import com.oborodulin.home.common.ui.components.field.ScreenEvent
import com.oborodulin.home.common.ui.components.field.TextFieldComponent
import com.oborodulin.home.common.ui.state.CommonScreen
import com.oborodulin.home.presentation.navigation.PayerInput
import com.skyyo.userinputvalidation.toast

private const val TAG = "Accounting.ui.PayerScreen"

@Composable
fun PayerScreen(
    viewModel: PayerViewModel = hiltViewModel(),
    payerInput: PayerInput
) {
    viewModel.uiStateFlow.collectAsState().value.let { result ->
        CommonScreen(result) { payerModel ->
            Payer(viewModel, payerModel){
                viewModel.submitAction(PayerUiAction.Save(it))
            }
        }
    }
    LaunchedEffect(payerInput.payerId) {
        viewModel.submitAction(PayerUiAction.Load(payerInput.payerId))
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalLifecycleComposeApi::class)
@Composable
fun Payer(viewModel: PayerViewModel, payerModel: PayerModel, onSubmit: (PayerModel) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val events = remember(viewModel.events, lifecycleOwner) {
        viewModel.events.flowWithLifecycle(
            lifecycleOwner.lifecycle,
            Lifecycle.State.STARTED
        )
    }

    val ercCode by viewModel.ercCode.collectAsStateWithLifecycle()
    val fullName by viewModel.fullName.collectAsStateWithLifecycle()
    val areInputsValid by viewModel.areInputsValid.collectAsStateWithLifecycle()

    val fullNameFocusRequester = remember { FocusRequester() }
    val ercCodeFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        events.collect { event ->
            when (event) {
                is ScreenEvent.ShowToast -> context.toast(event.messageId)
                is ScreenEvent.UpdateKeyboard -> {
                    if (event.show) keyboardController?.show() else keyboardController?.hide()
                }
                is ScreenEvent.ClearFocus -> focusManager.clearFocus()
                is ScreenEvent.RequestFocus -> {
                    when (event.textFieldKey) {
                        PayerFields.ERC_CODE -> ercCodeFocusRequester.requestFocus()
                        PayerFields.FULL_NAME -> fullNameFocusRequester.requestFocus()
                        else -> {}
                    }
                }
                is ScreenEvent.MoveFocus -> focusManager.moveFocus(event.direction)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextFieldComponent(
            modifier = Modifier
                .focusRequester(ercCodeFocusRequester)
                .onFocusChanged { focusState ->
                    viewModel.onTextFieldFocusChanged(
                        focusedField = PayerFields.ERC_CODE,
                        isFocused = focusState.isFocused
                    )
                },
            labelResId = R.string.erc_code_hint,
            keyboardOptions = remember {
                KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            },
            inputWrapper = ercCode,
            onValueChange = { viewModel.onTextFieldEntered(PayerInputEvent.ErcCode(it)) },
            onImeKeyAction = viewModel::moveFocusImeAction
        )
        Spacer(Modifier.height(16.dp))
        TextFieldComponent(
            modifier = Modifier
                .focusRequester(fullNameFocusRequester)
                .onFocusChanged { focusState ->
                    viewModel.onTextFieldFocusChanged(
                        focusedField = PayerFields.FULL_NAME,
                        isFocused = focusState.isFocused
                    )
                },
            labelResId = R.string.full_name_hint,
            keyboardOptions = remember {
                KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                )
            },
          //  visualTransformation = ::creditCardFilter,
            inputWrapper = fullName,
            onValueChange = { viewModel.onTextFieldEntered(PayerInputEvent.FullName(it)) },
            onImeKeyAction = viewModel::onContinueClick(onSubmit())
        )
        Spacer(Modifier.height(32.dp))
        Button(onClick = viewModel::onContinueClick(onSubmit()), enabled = areInputsValid) {
            Text(text = "Continue")
        }
    }

}

