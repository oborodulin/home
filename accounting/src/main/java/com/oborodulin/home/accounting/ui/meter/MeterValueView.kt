package com.oborodulin.home.accounting.ui.meter

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.oborodulin.home.metering.ui.model.MeterValueModel
import com.oborodulin.home.common.ui.components.field.util.InputFocusRequester
import com.oborodulin.home.common.ui.components.field.TextFieldComponent
import com.oborodulin.home.common.ui.components.field.util.InputWrapper
import com.oborodulin.home.common.ui.components.field.util.inputProcess
import timber.log.Timber
import java.util.*

private const val TAG = "Accounting.ui.MeterValueView"

@Composable
fun MeterValueView(
    viewModel: MeterValueViewModel,
    meterValueModel: MeterValueModel
) {
    Timber.tag(TAG).d("MeterValueView(...) called: meterValueInput = %s", meterValueModel)
    viewModel.initFieldStatesByUiModel(meterValueModel)
    MeterValue(meterValueModel, viewModel) {
        viewModel.submitAction(MeterValueUiAction.Save)
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalLifecycleComposeApi::class)
@Composable
fun MeterValue(
    meterValueModel: MeterValueModel,
    viewModel: MeterValueViewModel,
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
    val focusRequesters: List<InputFocusRequester> = listOf(
        InputFocusRequester(
            MeterValueFields.METER_CURR_VALUE.name,
            remember { FocusRequester() })
    )
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
        currentValue.inputs[meterValueModel.metersId.toString()]
    )
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(120.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextFieldComponent(
            modifier = Modifier
                .focusRequester((focusRequesters.find { it.fieldKey == MeterValueFields.METER_CURR_VALUE.name })!!.focusRequester)
                .onFocusChanged { focusState ->
                    viewModel.onTextFieldFocusChanged(
                        focusedField = MeterValueFields.METER_CURR_VALUE,
                        isFocused = focusState.isFocused
                    )
                },
            keyboardOptions = remember {
                KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            },
            inputWrapper = currentValue.inputs.getValue(meterValueModel.metersId.toString()),
            //  visualTransformation = ::creditCardFilter,
            onValueChange = {
                viewModel.onTextFieldEntered(
                    MeterValueInputEvent.CurrentValue(meterValueModel.metersId.toString(), it)
                )
            },
            onImeKeyAction = { if (areInputsValid) viewModel.onContinueClick { onSubmit() } }
        )
    }
}

@Preview(name = "Night Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Day Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun PreviewMeterValue() {
    MeterValue(
        meterValueModel = MeterValueModel(metersId = UUID.randomUUID()),
        viewModel = MeterValueViewModelImp.previewModel,
        onSubmit = {})
}
