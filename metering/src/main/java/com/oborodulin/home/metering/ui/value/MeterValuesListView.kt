package com.oborodulin.home.metering.ui.value

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavController
import com.oborodulin.home.common.ui.components.field.TextFieldComponent
import com.oborodulin.home.common.ui.components.field.util.InputFocusRequester
import com.oborodulin.home.common.ui.components.field.util.inputProcess
import com.oborodulin.home.common.ui.state.CommonScreen
import com.oborodulin.home.common.ui.theme.Typography
import com.oborodulin.home.data.util.ServiceType
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
//    meterValueModel: MeterValueModel
) {
    Timber.tag(TAG).d("MeterValuesListView(...) called: payerInput = %s", payerInput)
    /*
    viewModel.initFieldStatesByUiModel(meterValueModel)
    MeterValue(meterValueModel, viewModel) {
        viewModel.submitAction(MeterValuesListUiAction.Save)
    }
     */
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
            MeterValuesList(it, viewModel)
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
    viewModel: MeterValuesListViewModel
) {
    Timber.tag(TAG).d("MeterValuesList(...) called")
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colors.background, shape = RoundedCornerShape(16.dp))
            .verticalScroll(rememberScrollState())
            .padding(vertical = 8.dp)
    ) {
        for (meterValue in metersValues) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Column(modifier = Modifier.width(95.dp)) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Top
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))
                        ServiceIcon(meterValue.type)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = meterValue.name,
                            style = Typography.body1.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(85.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
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
                            text = DecimalFormat(meterValue.valueFormat).format(it),
                            style = Typography.body1.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(45.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    meterValue.measureUnit?.let { Text(text = it) }
                }
                MeterValue(meterValueListItemModel = meterValue, viewModel = viewModel) {
                    viewModel.submitAction(MeterValuesListUiAction.Save)
                }
                Column(
                    modifier = Modifier.fillMaxSize(),
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
                    Image(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .padding(4.dp)
                        //.clickable { onDelete(item) }
                        ,
                        painter = painterResource(com.oborodulin.home.common.R.drawable.outline_delete_black_24),
                        contentDescription = ""
                    )
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
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(120.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        /*
        var fieldValue = TextFieldValue(
            currentValue.inputs.getValue(meterValueListItemModel.metersId.toString()).value,
            TextRange(currentValue.inputs.getValue(meterValueListItemModel.metersId.toString()).value.length)
        )

         */
/*
        val inputWrapper by remember { mutableStateOf(currentValue.inputs.getValue(meterValueModel.metersId.toString())) }
        var fieldValue by remember {
            mutableStateOf(TextFieldValue(inputWrapper.value, TextRange(inputWrapper.value.length)))
        }
 */
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
            inputWrapper = currentValue.inputs.getValue(meterValueListItemModel.metersId.toString()),
            //fieldValue = fieldValue,
            //  visualTransformation = ::creditCardFilter,
            onValueChange = {
                //fieldValue = it
/*                val inputWrapper = currentValue.inputs.getValue(meterValueListItemModel.metersId.toString())
                currentValue.inputs[meterValueListItemModel.metersId.toString()] =
                    InputWrapper(value = it.text, errorId = inputWrapper.errorId, isEmpty = false)
                currentValue.copy(inputs = currentValue.inputs.toMutableMap())

 */
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
}

@Composable
fun ServiceIcon(serviceType: ServiceType?) =
    when (serviceType) {
        ServiceType.ELECRICITY -> Image(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .padding(4.dp),
            painter = painterResource(com.oborodulin.home.presentation.R.drawable.outline_electric_bolt_black_36),
            contentDescription = ""
        )
        ServiceType.COLD_WATER -> Image(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .padding(4.dp),
            painter = painterResource(com.oborodulin.home.presentation.R.drawable.outline_water_drop_black_36),
            contentDescription = ""
        )
        ServiceType.HOT_WATER -> Image(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .padding(4.dp),
            painter = painterResource(com.oborodulin.home.presentation.R.drawable.outline_opacity_black_36),
            contentDescription = ""
        )
        ServiceType.HEATING -> Image(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .padding(4.dp),
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
        viewModel = MeterValuesListViewModelImp.previewModel(LocalContext.current)
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
