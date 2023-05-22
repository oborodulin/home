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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import com.oborodulin.home.common.ui.components.dialog.AlertDialogComponent
import com.oborodulin.home.common.ui.components.field.TextFieldComponent
import com.oborodulin.home.common.ui.components.field.util.InputFocusRequester
import com.oborodulin.home.common.ui.components.field.util.inputProcess
import com.oborodulin.home.common.ui.state.CommonScreen
import com.oborodulin.home.common.ui.state.MviViewModel
import com.oborodulin.home.common.ui.theme.Typography
import com.oborodulin.home.common.util.Utils
import com.oborodulin.home.common.util.toast
import com.oborodulin.home.metering.R
import com.oborodulin.home.metering.ui.model.MeterValueListItem
import com.oborodulin.home.presentation.navigation.PayerInput
import com.oborodulin.home.presentation.util.MeterIcon
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber
import java.text.DecimalFormat
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

private const val TAG = "Metering.ui.MeterValueView"

@Composable
fun MeterValuesListView(
    viewModel: MeterValuesListViewModel,
    navController: NavController,
    payerInput: PayerInput? = null
) {
    Timber.tag(TAG).d("MeterValuesListView(...) called: payerInput = %s", payerInput)

    val currentPayer by viewModel.primaryObjectData.collectAsStateWithLifecycle()
    var payerId by remember { mutableStateOf(payerInput?.payerId) }

    payerId = payerId ?: if (currentPayer[MviViewModel.IDX_OBJECT_ID].isNotEmpty()) UUID.fromString(
        currentPayer[MviViewModel.IDX_OBJECT_ID]
    ) else null

    Timber.tag(TAG).d("MeterValuesListView: currentPayer = %s, payerId = %s", currentPayer, payerId)
    LaunchedEffect(payerId) {
        Timber.tag(TAG).d("MeterValuesListView: LaunchedEffect() BEFORE collect ui state flow")
        when (payerId) {
            null -> {
                Timber.tag(TAG).d("LaunchedEffect: MeterValuesListUiAction.Init")
                viewModel.submitAction(MeterValuesListUiAction.Init)
            }

            else -> {
                Timber.tag(TAG).d("LaunchedEffect: MeterValuesListUiAction.Load(%s)", payerId)
                viewModel.submitAction(MeterValuesListUiAction.Load(payerId!!))
            }
        }
    }
    viewModel.uiStateFlow.collectAsState().value.let { state ->
        Timber.tag(TAG).d("Collect ui state flow: %s", state)
        CommonScreen(state = state) { list ->
            MeterValuesList(
                list.filter { it.payerId == (payerId ?: it.payerId) },
                viewModel,
                payerId
            )
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
    metersValues: List<MeterValueListItem>,
    viewModel: MeterValuesListViewModel,
    payerId: UUID?
) {
    Timber.tag(TAG).d("MeterValuesList(...) called")
    if (metersValues.isNotEmpty()) {
        val context = LocalContext.current
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
                        elevation = CardDefaults.cardElevation(10.dp)
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
                                    .weight(0.22f),
                                verticalArrangement = Arrangement.Center
                            ) {
                                MeterIcon(meterValue.meterType)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = meterValue.serviceName,
                                    style = Typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    maxLines = 2
                                )
                            }
                            Column(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .weight(0.28f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                meterValue.prevLastDate?.let {
                                    Text(
                                        text = it.format(
                                            DateTimeFormatter.ofLocalizedDate(
                                                FormatStyle.SHORT
                                            ).withLocale(Locale.getDefault())
                                        ) //ISO_LOCAL_DATE
                                        //DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMAN).format(it)
                                    )
                                }
                                Divider(thickness = 1.dp)
                                Spacer(modifier = Modifier.height(8.dp))
                                meterValue.prevValue?.let {
                                    Text(
                                        text = AnnotatedString(
                                            text = DecimalFormat(meterValue.valueFormat).format(it),
                                            spanStyle = SpanStyle(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 20.sp
                                            )
                                        ).plus(
                                            AnnotatedString(
                                                when (meterValue.meterMeasureUnit) {
                                                    null -> ""
                                                    else -> " "
                                                }
                                            )
                                        ).plus(
                                            Utils.scriptText(
                                                meterValue.meterMeasureUnit, listOf("2", "3")
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
                                    meterValueListItem = meterValue,
                                    viewModel = viewModel
                                )
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
                                        .clickable { context.toast("Photo button clicked...") },
                                    painter = painterResource(com.oborodulin.home.presentation.R.drawable.outline_photo_camera_black_24),
                                    contentDescription = ""
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                var showDialogState by remember { mutableStateOf(false) }
                                AlertDialogComponent(
                                    isShow = showDialogState,
                                    title = { Text(stringResource(com.oborodulin.home.common.R.string.dlg_confirm_title)) },
                                    text = { Text(text = stringResource(R.string.dlg_confirm_del_meter_value)) },
                                    onDismiss = { showDialogState = false },
                                    onConfirm = {
                                        showDialogState = false
                                        viewModel.submitAction(
                                            MeterValuesListUiAction.Delete(meterValue.metersId)
                                        )
                                        viewModel.clearInputFieldsStates()
                                        Timber.tag(TAG).d("MeterValuesList: payerId = %s", payerId)
                                        when (payerId) {
                                            null -> viewModel.submitAction(MeterValuesListUiAction.Init)
                                            else -> viewModel.submitAction(
                                                MeterValuesListUiAction.Load(payerId)
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
                                                showDialogState = true
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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MeterValue(
    meterValueListItem: MeterValueListItem,
    viewModel: MeterValuesListViewModel
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
        currentValue.inputs[meterValueListItem.metersId.toString()]
    )
    Timber.tag(TAG).d(
        "MeterValue: currentValue.inputs[%s] = %s",
        meterValueListItem.metersId,
        currentValue.inputs.getValue(meterValueListItem.metersId.toString()),
    )
    var inputWrapper by remember {
        mutableStateOf(
            currentValue.inputs.getValue(
                meterValueListItem.metersId.toString()
            )
        )
    }
    Timber.tag(TAG).d(
        "MeterValue: BEFORE Update remember inputWrapper = %s, currentValue.inputs[%s] = %s",
        inputWrapper,
        meterValueListItem.metersId.toString(),
        currentValue.inputs.getValue(
            meterValueListItem.metersId.toString()
        )
    )

    if (inputWrapper.value != currentValue.inputs.getValue(
            meterValueListItem.metersId.toString()
        ).value
    )
        inputWrapper = inputWrapper.copy(
            value = currentValue.inputs.getValue(
                meterValueListItem.metersId.toString()
            ).value
        )
    Timber.tag(TAG).d(
        "MeterValue: AFTER Update remember inputWrapper = %s, currentValue.inputs[%s] = %s",
        inputWrapper,
        meterValueListItem.metersId.toString(),
        currentValue.inputs.getValue(
            meterValueListItem.metersId.toString()
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
                keyboardType = KeyboardType.Decimal,
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
            inputWrapper = inputWrapper.copy(value = it, isSaved = false)
            viewModel.setStateValue(
                field = MeterValueFields.METER_CURR_VALUE,
                properties = viewModel.currentValue,
                value = it, key = meterValueListItem.metersId.toString(),
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
                    meterValueListItem.metersId.toString(),
                    it
                )
            )
        },
        onImeKeyAction = {
            Timber.tag(TAG).d("MeterValue: onImeKeyAction")
            if (areInputsValid) viewModel.onContinueClick {
                viewModel.submitAction(MeterValuesListUiAction.Save)
            }
        }
    )
}

@Preview(name = "Night Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Day Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun PreviewMeterValuesList() {
    MeterValuesList(
        metersValues = MeterValuesListViewModelImp.previewMeterValueModel(LocalContext.current),
        viewModel = MeterValuesListViewModelImp.previewModel(LocalContext.current),
        payerId = null
    )
}

@Preview(name = "Night Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Day Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun PreviewMeterValue() {
    MeterValue(
        meterValueListItem = MeterValueListItem(metersId = UUID.randomUUID()),
        viewModel = MeterValuesListViewModelImp.previewModel(LocalContext.current)
    )
}