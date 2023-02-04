package com.oborodulin.home.accounting.ui.payer.single

import android.content.res.Configuration
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.oborodulin.home.common.ui.components.field.ExposedDropdownMenuBoxComponent
import com.oborodulin.home.common.ui.components.field.TextFieldComponent
import com.oborodulin.home.common.ui.components.field.util.InputFocusRequester
import com.oborodulin.home.common.ui.components.field.util.inputProcess
import com.oborodulin.home.common.ui.state.CommonScreen
import com.oborodulin.home.common.ui.theme.HomeComposableTheme
import com.oborodulin.home.presentation.AppState
import com.oborodulin.home.presentation.components.ScaffoldComponent
import com.oborodulin.home.presentation.navigation.PayerInput
import com.oborodulin.home.presentation.rememberAppState
import kotlinx.coroutines.launch
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
            ) { it ->
                CommonScreen(paddingValues = it, state = state) {
                    //viewModel.initFieldStatesByUiModel(payerModel)
                    Payer(appState, viewModel) {
                        viewModel.submitAction(PayerUiAction.Save)
                    }
                }
            }
        }
    }
}

@OptIn(
    ExperimentalComposeUiApi::class, ExperimentalLifecycleComposeApi::class,
    ExperimentalMaterialApi::class
)
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
    val paymentDay by viewModel.paymentDay.collectAsStateWithLifecycle()
    val personsNum by viewModel.personsNum.collectAsStateWithLifecycle()

    val areInputsValid by viewModel.areInputsValid.collectAsStateWithLifecycle()

    Timber.tag(TAG).d("Init Focus Requesters for all payer fields")
    val focusRequesters: MutableMap<String, InputFocusRequester> = HashMap()
    enumValues<PayerFields>().forEach {
        focusRequesters[it.name] = InputFocusRequester(it, remember { FocusRequester() })
    }

    LaunchedEffect(Unit) {
        Timber.tag(TAG).d("Payer(...): LaunchedEffect()")
        events.collect { event ->
            Timber.tag(TAG).d("Collect input events flow: %s", event.javaClass.name)
            inputProcess(context, focusManager, keyboardController, event, focusRequesters)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, MaterialTheme.colors.primary, shape = RoundedCornerShape(16.dp))
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TextFieldComponent(
            modifier = Modifier
                .focusRequester(focusRequesters[PayerFields.ERC_CODE.name]!!.focusRequester)
                .onFocusChanged { focusState ->
                    viewModel.onTextFieldFocusChanged(
                        focusedField = PayerFields.ERC_CODE,
                        isFocused = focusState.isFocused
                    )
                },
            labelResId = R.string.erc_code_hint,
            leadingIcon = {
                Icon(
                    painterResource(com.oborodulin.home.common.R.drawable.outline_123_black_36),
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
        TextFieldComponent(
            modifier = Modifier
                .focusRequester(focusRequesters[PayerFields.FULL_NAME.name]!!.focusRequester)
                .onFocusChanged { focusState ->
                    viewModel.onTextFieldFocusChanged(
                        focusedField = PayerFields.FULL_NAME,
                        isFocused = focusState.isFocused
                    )
                },
            labelResId = R.string.full_name_hint,
            leadingIcon = {
                Icon(
                    painterResource(com.oborodulin.home.presentation.R.drawable.outline_person_black_36),
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
        TextFieldComponent(
            modifier = Modifier
                .height(90.dp)
                .focusRequester(focusRequesters[PayerFields.ADDRESS.name]!!.focusRequester)
                .onFocusChanged { focusState ->
                    viewModel.onTextFieldFocusChanged(
                        focusedField = PayerFields.ADDRESS,
                        isFocused = focusState.isFocused
                    )
                },
            labelResId = R.string.address_hint,
            leadingIcon = {
                Icon(
                    painterResource(com.oborodulin.home.presentation.R.drawable.outline_house_black_36),
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
        TextFieldComponent(
            modifier = Modifier
                .focusRequester(focusRequesters[PayerFields.TOTAL_AREA.name]!!.focusRequester)
                .onFocusChanged { focusState ->
                    viewModel.onTextFieldFocusChanged(
                        focusedField = PayerFields.TOTAL_AREA,
                        isFocused = focusState.isFocused
                    )
                },
            labelResId = R.string.total_area_hint,
            leadingIcon = {
                Icon(
                    painterResource(com.oborodulin.home.presentation.R.drawable.outline_space_dashboard_black_36),
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
        TextFieldComponent(
            modifier = Modifier
                .focusRequester(focusRequesters[PayerFields.LIVING_SPACE.name]!!.focusRequester)
                .onFocusChanged { focusState ->
                    viewModel.onTextFieldFocusChanged(
                        focusedField = PayerFields.LIVING_SPACE,
                        isFocused = focusState.isFocused
                    )
                },
            labelResId = R.string.living_space_hint,
            leadingIcon = {
                Icon(
                    painterResource(com.oborodulin.home.presentation.R.drawable.outline_aspect_ratio_black_36),
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
        TextFieldComponent(
            modifier = Modifier
                .focusRequester(focusRequesters[PayerFields.HEATED_VOLUME.name]!!.focusRequester)
                .onFocusChanged { focusState ->
                    viewModel.onTextFieldFocusChanged(
                        focusedField = PayerFields.HEATED_VOLUME,
                        isFocused = focusState.isFocused
                    )
                },
            labelResId = R.string.heated_volume_hint,
            leadingIcon = {
                Icon(
                    painterResource(com.oborodulin.home.presentation.R.drawable.outline_outbox_black_36),
                    null
                )
            },
            keyboardOptions = remember {
                KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            },
            inputWrapper = heatedVolume,
            onValueChange = { viewModel.onTextFieldEntered(PayerInputEvent.HeatedVolume(it)) },
            onImeKeyAction = viewModel::moveFocusImeAction
        )
        ExposedDropdownMenuBoxComponent(
            modifier = Modifier
                .focusRequester(focusRequesters[PayerFields.PAYMENT_DAY.name]!!.focusRequester)
                .onFocusChanged { focusState ->
                    viewModel.onTextFieldFocusChanged(
                        focusedField = PayerFields.PAYMENT_DAY,
                        isFocused = focusState.isFocused
                    )
                },
            labelResId = R.string.payment_day_hint,
            leadingIcon = {
                Icon(
                    painterResource(com.oborodulin.home.common.R.drawable.outline_calendar_month_black_36),
                    null
                )
            },
            keyboardOptions = remember {
                KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            },
            inputWrapper = paymentDay,
            listItems = (1..28).map { it.toString() },
            onValueChange = { viewModel.onTextFieldEntered(PayerInputEvent.PaymentDay(it)) },
            onImeKeyAction = viewModel::moveFocusImeAction,
            //colors = ExposedDropdownMenuDefaults.textFieldColors()
        )
        TextFieldComponent(
            modifier = Modifier
                .focusRequester(focusRequesters[PayerFields.PERSONS_NUM.name]!!.focusRequester)
                .onFocusChanged { focusState ->
                    viewModel.onTextFieldFocusChanged(
                        focusedField = PayerFields.PERSONS_NUM,
                        isFocused = focusState.isFocused
                    )
                },
            labelResId = R.string.persons_num_hint,
            leadingIcon = {
                Icon(
                    painterResource(com.oborodulin.home.presentation.R.drawable.outline_people_black_36),
                    null
                )
            },
            keyboardOptions = remember {
                KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                    //imeAction = ImeAction.Done
                )
            },
            inputWrapper = personsNum,
            onValueChange = { viewModel.onTextFieldEntered(PayerInputEvent.PersonsNum(it)) },
            onImeKeyAction = viewModel::moveFocusImeAction
            //onImeKeyAction = { } //viewModel.onContinueClick { onSubmit() }
        )
        Spacer(Modifier.height(8.dp))
        Button(onClick = {
            viewModel.onContinueClick {
                Timber.tag(TAG).d("PayerScreen(...): Start viewModelScope.launch")
                viewModel.viewModelScope().launch {
                    viewModel.actionsJobFlow.collect {
                        Timber.tag(TAG).d(
                            "PayerScreen(...): Start actionsJobFlow.collect [job = %s]",
                            it?.toString()
                        )
                        it?.join()
                        appState.backToBottomBarScreen()
                    }
                }
                onSubmit()
                Timber.tag(TAG).d("PayerScreen(...): onSubmit() executed")
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
