package com.oborodulin.home.metering.ui.value

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Text
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import com.oborodulin.home.common.ui.components.dialog.AlertDialogComponent
import com.oborodulin.home.common.ui.components.field.TextFieldComponent
import com.oborodulin.home.common.ui.components.field.util.InputFocusRequester
import com.oborodulin.home.common.ui.components.field.util.inputProcess
import com.oborodulin.home.common.ui.state.CommonScreen
import com.oborodulin.home.common.ui.theme.Typography
import com.oborodulin.home.common.util.Utils
import com.oborodulin.home.data.util.ServiceType
import com.oborodulin.home.metering.R
import com.oborodulin.home.metering.ui.model.MeterValueListItemModel
import com.oborodulin.home.presentation.navigation.PayerInput
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber
import java.text.DecimalFormat
import java.time.format.DateTimeFormatter
import java.util.*

private const val TAG = "Metering.ui.MeterValueView"

@Composable
fun MeterValuesListView(
    viewModel: MeterValuesListViewModel,
    navController: NavController,
    payerInput: PayerInput? = null
) {
    Timber.tag(TAG).d("MeterValuesListView(...) called: payerInput = %s", payerInput)
    LaunchedEffect(payerInput) {
        Timber.tag(TAG).d("MeterValuesListView: LaunchedEffect() BEFORE collect ui state flow")
        when (payerInput) {
            null -> viewModel.submitAction(MeterValuesListUiAction.Init)
            else -> viewModel.submitAction(MeterValuesListUiAction.Load(payerInput.payerId))
        }
    }
    viewModel.uiStateFlow.collectAsState().value.let { state ->
        Timber.tag(TAG).d("Collect ui state flow: %s", state)
        CommonScreen(state = state) {
            MeterValuesList(it, viewModel, payerInput)
        }
    }
    LaunchedEffect(Unit) {
        Timber.tag(TAG)
            .d("MeterValuesListView: LaunchedEffect() AFTER collect ui state flow")
        viewModel.singleEventFlow.collectLatest {
            Timber.tag(TAG).d("Collect Latest UiSingleEvent: %s", it.javaClass.name)
            when (it) {
                is MeterValuesListUiSingleEvent.OpenPhotoScreen -> {
                    navController.navigate(it.navRoute)
                }
            }
        }
    }
}

@Composable
fun MeterValuesList(
    metersValues: List<MeterValueListItemModel>,
    viewModel: MeterValuesListViewModel,
    payerInput: PayerInput?
) {
    Timber.tag(TAG).d("MeterValuesList(...) called")
    if (metersValues.isNotEmpty()) {
        LazyColumn(
            state = rememberLazyListState(),
            modifier = Modifier
                .selectableGroup()
                .padding(4.dp)
        ) {
            items(metersValues.size) { index ->
                metersValues[index].let { meterValue ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .padding(4.dp),
                        elevation = 10.dp
                    )
                    {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 4.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(0.25f)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.Top
                                ) {
                                    ServiceIcon(meterValue.type)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = meterValue.name,
                                        style = Typography.body1.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp
                                        ),
                                        maxLines = 2
                                    )
                                }
                            }
                            Column(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(0.25f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                meterValue.prevLastDate?.let {
                                    Text(
                                        text = it.format(DateTimeFormatter.ISO_LOCAL_DATE)
                                        //DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMAN).format(it)
                                    )
                                }
                                Divider(thickness = 1.dp)
                                Spacer(modifier = Modifier.height(8.dp))
                                meterValue.prevValue?.let {
                                    Text(
                                        text = AnnotatedString(
                                            text = DecimalFormat(meterValue.valueFormat).format(it),
                                            spanStyle = SpanStyle(fontWeight = FontWeight.Bold)
                                        ).plus(
                                            AnnotatedString(
                                                when (meterValue.measureUnit) {
                                                    null -> ""
                                                    else -> " "
                                                }
                                            )
                                        ).plus(
                                            Utils.scriptText(
                                                meterValue.measureUnit, listOf("2", "3")
                                            )
                                        ),
                                        maxLines = 2,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                            Column(
                                modifier = Modifier.weight(0.3f),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                MeterValue(
                                    meterValueListItemModel = meterValue,
                                    viewModel = viewModel
                                ) {
                                    viewModel.submitAction(MeterValuesListUiAction.Save)
                                }
                            }
                            Column(
                                modifier = Modifier.weight(0.1f),
                                verticalArrangement = Arrangement.Top
                            ) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Image(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .padding(4.dp)
                                    //.clickable { onEdit(item) }
                                    ,
                                    painter = painterResource(com.oborodulin.home.presentation.R.drawable.outline_photo_camera_black_24),
                                    contentDescription = ""
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                val showDialogState = remember { mutableStateOf(false) }
                                AlertDialogComponent(
                                    isShow = showDialogState.value,
                                    title = { Text(stringResource(com.oborodulin.home.common.R.string.dlg_confirm_title)) },
                                    text = { Text(text = stringResource(R.string.dlg_confirm_del_meter_value)) },
                                    onDismiss = { showDialogState.value = false },
                                    onConfirm = {
                                        showDialogState.value = false
                                        viewModel.submitAction(
                                            MeterValuesListUiAction.Delete(meterValue.metersId)
                                        )
                                        when (payerInput) {
                                            null -> viewModel.submitAction(MeterValuesListUiAction.Init)
                                            else -> viewModel.submitAction(
                                                MeterValuesListUiAction.Load(payerInput.payerId)
                                            )
                                        }
                                    }
                                )
                                Image(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .padding(4.dp)
                                        .clickable {
                                            if (meterValue.currentValue != null) {
                                                showDialogState.value = true
                                            }
                                        },
                                    painter = painterResource(com.oborodulin.home.common.R.drawable.outline_delete_black_24),
                                    contentDescription = ""
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalLifecycleComposeApi::class)
@Composable
fun MeterValue(
    meterValueListItemModel: MeterValueListItemModel,
    viewModel: MeterValuesListViewModel,
    onSubmit: () -> Unit
) {
    Timber.tag(TAG).d("MeterValue(...) called")
    val lifecycleOwner = LocalLifecycleOwner.current

    val events = remember(viewModel.events, lifecycleOwner) {
        viewModel.events.flowWithLifecycle(
            lifecycleOwner.lifecycle,
            Lifecycle.State.STARTED
        )
    }
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Timber.tag(TAG).d("CollectAsStateWithLifecycle for current Meter Value")
    val currentValue by viewModel.currentValue.collectAsStateWithLifecycle()
    val areInputsValid by viewModel.areInputsValid.collectAsStateWithLifecycle()

    Timber.tag(TAG).d("focusRequesters for current Meter Value")
    val focusRequesters: MutableMap<String, InputFocusRequester> = HashMap()
    enumValues<MeterValueFields>().forEach {
        focusRequesters[it.name] = InputFocusRequester(it, remember { FocusRequester() })
    }
    Timber.tag(TAG).d("MeterValue: LaunchedEffect() - before")
    LaunchedEffect(Unit) {
        Timber.tag(TAG).d("MeterValue: LaunchedEffect()")
        events.collect { event ->
            Timber.tag(TAG).d("Collect input events flow: %s", event.javaClass.name)
            inputProcess(context, focusManager, keyboardController, event, focusRequesters)
        }
    }

    Timber.tag(TAG).d(
        "MeterValue: currentValue.inputs = %s",
        currentValue.inputs[meterValueListItemModel.metersId.toString()]
    )
    Timber.tag(TAG).d(
        "MeterValue: currentValue.inputs[%s] = %s",
        meterValueListItemModel.metersId,
        currentValue.inputs.getValue(meterValueListItemModel.metersId.toString()),
    )
    var inputWrapper by remember {
        mutableStateOf(
            currentValue.inputs.getValue(
                meterValueListItemModel.metersId.toString()
            )
        )
    }
    Timber.tag(TAG).d(
        "MeterValue: BEFORE Update remember inputWrapper = %s, currentValue.inputs[%s] = %s",
        inputWrapper,
        meterValueListItemModel.metersId.toString(),
        currentValue.inputs.getValue(
            meterValueListItemModel.metersId.toString()
        )
    )

    if (inputWrapper.value != currentValue.inputs.getValue(
            meterValueListItemModel.metersId.toString()
        ).value
    )
        inputWrapper = inputWrapper.copy(
            value = currentValue.inputs.getValue(
                meterValueListItemModel.metersId.toString()
            ).value
        )
    Timber.tag(TAG).d(
        "MeterValue: AFTER Update remember inputWrapper = %s, currentValue.inputs[%s] = %s",
        inputWrapper,
        meterValueListItemModel.metersId.toString(),
        currentValue.inputs.getValue(
            meterValueListItemModel.metersId.toString()
        )
    )
    TextFieldComponent(
        modifier = Modifier
            .focusRequester(focusRequesters[MeterValueFields.METER_CURR_VALUE.name]!!.focusRequester)
            .onFocusChanged { focusState ->
                viewModel.onTextFieldFocusChanged(
                    focusedField = MeterValueFields.METER_CURR_VALUE,
                    isFocused = focusState.isFocused,
                    onFocusIn = {
                        Timber
                            .tag(TAG)
                            .d("MeterValue: onFocusIn")
                    },
                    onFocusOut = {
                        Timber
                            .tag(TAG)
                            .d("MeterValue: onFocusOut")
                    }
                )
            },
        keyboardOptions = remember {
            KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            )
        },
        inputWrapper = inputWrapper,
        //  visualTransformation = ::creditCardFilter,
        onValueChange = {
            Timber.tag(TAG).d(
                "MeterValue: onValueChange BEFORE Update - inputWrapper.value = %s, it = %s",
                inputWrapper.value,
                it
            )
            inputWrapper = inputWrapper.copy(value = it)
            viewModel.setStateValue(
                field = MeterValueFields.METER_CURR_VALUE,
                properties = viewModel.currentValue,
                value = it, key = meterValueListItemModel.metersId.toString(),
                isValid = true, isSaved = false
            )
            Timber.tag(TAG).d(
                "MeterValue: onValueChange AFTER Update - inputWrapper.value = %s, it = %s",
                inputWrapper.value,
                it
            )
            Timber.tag(TAG).d("MeterValue: onValueChange - %s", it)
            viewModel.onTextFieldEntered(
                MeterValueInputEvent.CurrentValue(
                    meterValueListItemModel.metersId.toString(),
                    it
                )
            )
        },
        onImeKeyAction = { if (areInputsValid) viewModel.onContinueClick { onSubmit() } }
    )
}

@Composable
fun ServiceIcon(serviceType: ServiceType?) =
    when (serviceType) {
        ServiceType.ELECRICITY -> Image(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp)),
            painter = painterResource(com.oborodulin.home.presentation.R.drawable.outline_electric_bolt_black_36),
            contentDescription = ""
        )
        ServiceType.COLD_WATER -> Image(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp)),
            painter = painterResource(com.oborodulin.home.presentation.R.drawable.outline_water_drop_black_36),
            contentDescription = ""
        )
        ServiceType.HOT_WATER -> Image(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp)),
            painter = painterResource(com.oborodulin.home.presentation.R.drawable.outline_opacity_black_36),
            contentDescription = ""
        )
        ServiceType.HEATING -> Image(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp)),
            painter = painterResource(com.oborodulin.home.presentation.R.drawable.ic_radiator_36),
            contentDescription = ""
        )
        else -> {}
    }

@Preview(name = "Night Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Day Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun PreviewMeterValuesList() {
    MeterValuesList(
        metersValues = MeterValuesListViewModelImp.previewMeterValueModel(LocalContext.current),
        viewModel = MeterValuesListViewModelImp.previewModel(LocalContext.current),
        payerInput = null
    )
}

@Preview(name = "Night Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Day Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun PreviewMeterValue() {
    MeterValue(
        meterValueListItemModel = MeterValueListItemModel(metersId = UUID.randomUUID()),
        viewModel = MeterValuesListViewModelImp.previewModel(LocalContext.current),
        onSubmit = {})
}