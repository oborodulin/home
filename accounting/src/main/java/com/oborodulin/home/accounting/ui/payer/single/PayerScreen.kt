package com.oborodulin.home.accounting.ui.payer.single

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.oborodulin.home.accounting.R
import com.oborodulin.home.common.ui.components.field.ScreenEvent
import com.oborodulin.home.common.ui.components.field.TextFieldComponent
import com.oborodulin.home.common.ui.state.CommonScreen
import com.oborodulin.home.common.ui.theme.HomeComposableTheme
import com.oborodulin.home.common.util.toast
import com.oborodulin.home.presentation.AppState
import com.oborodulin.home.presentation.components.ScaffoldComponent
import com.oborodulin.home.presentation.navigation.PayerInput
import com.oborodulin.home.presentation.rememberAppState
import timber.log.Timber

private const val TAG = "Accounting.ui.PayerScreen"

@Composable
fun PayerScreen(
    appState: AppState,
    viewModel: PayerViewModelImp = hiltViewModel(),
    payerInput: PayerInput? = null
) {
    Timber.tag(TAG).d("PayerScreen(...) called: payerInput = %s", payerInput)
    LaunchedEffect(payerInput?.payerId) {
        Timber.tag(TAG).d("PayerScreen: LaunchedEffect() BEFORE collect ui state flow")
        when (payerInput) {
            null -> viewModel.submitAction(PayerUiAction.Create)
            else -> viewModel.submitAction(PayerUiAction.Load(payerInput.payerId))
        }
    }
    val topBarTitleId = when (payerInput) {
        null -> R.string.payer_new_subheader
        else -> R.string.payer_subheader
    }
    viewModel.uiStateFlow.collectAsState().value.let { state ->
        Timber.tag(TAG).d("Collect ui state flow: %s", state)
        HomeComposableTheme { //(darkTheme = true)
            ScaffoldComponent(
                appState = appState,
                scaffoldState = appState.payerScaffoldState,
                topBarTitleId = topBarTitleId,
                topBarNavigationIcon = {
                    IconButton(onClick = { appState.backToBottomBarScreen() }) {
                        Icon(Icons.Filled.ArrowBack, null)
                    }
                }
            ) {
                CommonScreen(paddingValues = it, state = state) {
                    Payer(appState, viewModel) {
                        viewModel.submitAction(PayerUiAction.Save)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalLifecycleComposeApi::class)
@Composable
fun Payer(appState: AppState, viewModel: PayerViewModel, onSubmit: () -> Unit) {
    Timber.tag(TAG).d("Payer(...) called")
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

    Timber.tag(TAG).d("CollectAsStateWithLifecycle for all payer fields")
    val ercCode by viewModel.ercCode.collectAsStateWithLifecycle()
    val fullName by viewModel.fullName.collectAsStateWithLifecycle()
    val address by viewModel.address.collectAsStateWithLifecycle()
    val totalArea by viewModel.totalArea.collectAsStateWithLifecycle()
    val livingSpace by viewModel.livingSpace.collectAsStateWithLifecycle()
    val heatedVolume by viewModel.heatedVolume.collectAsStateWithLifecycle()

    val areInputsValid by viewModel.areInputsValid.collectAsStateWithLifecycle()

    Timber.tag(TAG).d("Init Focus Requesters for all payer fields")
    val ercCodeFocusRequester = remember { FocusRequester() }
    val fullNameFocusRequester = remember { FocusRequester() }
    val addressFocusRequester = remember { FocusRequester() }
    val totalAreaFocusRequester = remember { FocusRequester() }
    val livingSpaceFocusRequester = remember { FocusRequester() }
    val heatedVolumeFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        Timber.tag(TAG).d("Payer(...): LaunchedEffect()")
        events.collect { event ->
            Timber.tag(TAG).d("Collect input events flow: %s", event.javaClass.name)
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
                        PayerFields.ADDRESS -> addressFocusRequester.requestFocus()
                        PayerFields.TOTAL_AREA -> totalAreaFocusRequester.requestFocus()
                        PayerFields.LIVING_SPACE -> livingSpaceFocusRequester.requestFocus()
                        PayerFields.HEATED_VOLUME -> heatedVolumeFocusRequester.requestFocus()
                        else -> {}
                    }
                }
                is ScreenEvent.MoveFocus -> focusManager.moveFocus(event.direction)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
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
            leadingIcon = {
                Icon(
                    painterResource(com.oborodulin.home.common.R.drawable.outline_123_black_24),
                    null
                )
            },
            keyboardOptions = remember {
                KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            },
            inputWrapper = ercCode,
            onValueChange = { viewModel.onTextFieldEntered(PayerInputEvent.ErcCode(it)) },
            onImeKeyAction = viewModel::moveFocusImeAction
        )
        Spacer(Modifier.height(8.dp))
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
            leadingIcon = {
                Icon(
                    painterResource(com.oborodulin.home.presentation.R.drawable.outline_person_24),
                    null
                )
            },
            keyboardOptions = remember {
                KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            },
            //  visualTransformation = ::creditCardFilter,
            inputWrapper = fullName,
            onValueChange = { viewModel.onTextFieldEntered(PayerInputEvent.FullName(it)) },
            onImeKeyAction = viewModel::moveFocusImeAction
        )
        Spacer(Modifier.height(8.dp))
        TextFieldComponent(
            modifier = Modifier
                .height(80.dp)
                .focusRequester(addressFocusRequester)
                .onFocusChanged { focusState ->
                    viewModel.onTextFieldFocusChanged(
                        focusedField = PayerFields.ADDRESS,
                        isFocused = focusState.isFocused
                    )
                },
            labelResId = R.string.address_hint,
            leadingIcon = {
                Icon(
                    painterResource(com.oborodulin.home.presentation.R.drawable.outline_house_black_24),
                    null
                )
            },
            maxLines = 2,
            keyboardOptions = remember {
                KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            },
            //  visualTransformation = ::creditCardFilter,
            inputWrapper = address,
            onValueChange = { viewModel.onTextFieldEntered(PayerInputEvent.Address(it)) },
            onImeKeyAction = viewModel::moveFocusImeAction
        )
        Spacer(Modifier.height(8.dp))
        TextFieldComponent(
            modifier = Modifier
                .focusRequester(totalAreaFocusRequester)
                .onFocusChanged { focusState ->
                    viewModel.onTextFieldFocusChanged(
                        focusedField = PayerFields.TOTAL_AREA,
                        isFocused = focusState.isFocused
                    )
                },
            labelResId = R.string.total_area_hint,
            leadingIcon = {
                Icon(
                    painterResource(com.oborodulin.home.presentation.R.drawable.outline_space_dashboard_black_24),
                    null
                )
            },
            keyboardOptions = remember {
                KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            },
            inputWrapper = totalArea,
            onValueChange = { viewModel.onTextFieldEntered(PayerInputEvent.TotalArea(it)) },
            onImeKeyAction = viewModel::moveFocusImeAction
        )
        Spacer(Modifier.height(8.dp))
        TextFieldComponent(
            modifier = Modifier
                .focusRequester(livingSpaceFocusRequester)
                .onFocusChanged { focusState ->
                    viewModel.onTextFieldFocusChanged(
                        focusedField = PayerFields.LIVING_SPACE,
                        isFocused = focusState.isFocused
                    )
                },
            labelResId = R.string.living_space_hint,
            leadingIcon = {
                Icon(
                    painterResource(com.oborodulin.home.presentation.R.drawable.outline_aspect_ratio_black_24),
                    null
                )
            },
            keyboardOptions = remember {
                KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            },
            inputWrapper = livingSpace,
            onValueChange = { viewModel.onTextFieldEntered(PayerInputEvent.LivingSpace(it)) },
            onImeKeyAction = viewModel::moveFocusImeAction
        )
        Spacer(Modifier.height(8.dp))
        TextFieldComponent(
            modifier = Modifier
                .focusRequester(heatedVolumeFocusRequester)
                .onFocusChanged { focusState ->
                    viewModel.onTextFieldFocusChanged(
                        focusedField = PayerFields.HEATED_VOLUME,
                        isFocused = focusState.isFocused
                    )
                },
            labelResId = R.string.heated_volume_hint,
            leadingIcon = {
                Icon(
                    painterResource(com.oborodulin.home.presentation.R.drawable.outline_outbox_black_24),
                    null
                )
            },
            keyboardOptions = remember {
                KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                )
            },
            inputWrapper = heatedVolume,
            onValueChange = { viewModel.onTextFieldEntered(PayerInputEvent.HeatedVolume(it)) },
            onImeKeyAction = { } //viewModel.onContinueClick { onSubmit() }
        )
        Spacer(Modifier.height(24.dp))
        Button(onClick = {
            viewModel.onContinueClick {
                onSubmit()
                appState.backToBottomBarScreen()
            }
        }, enabled = areInputsValid) {
            Text(text = stringResource(com.oborodulin.home.common.R.string.btn_save_lbl))
        }
    }
}

@Preview(name = "Night Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Day Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun PreviewPayer() {
    Payer(
        appState = rememberAppState(),
        viewModel = PayerViewModelImp.previewModel,
        onSubmit = {})
}
