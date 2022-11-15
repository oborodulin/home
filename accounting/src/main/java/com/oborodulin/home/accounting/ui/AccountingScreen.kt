package com.oborodulin.home.accounting.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.oborodulin.home.accounting.ui.model.AccountingModel
import com.oborodulin.home.accounting.ui.payer.list.PayersList
import com.oborodulin.home.accounting.ui.payer.list.PayersListUiAction
import com.oborodulin.home.accounting.ui.payer.list.PayersListUiSingleEvent
import com.oborodulin.home.accounting.ui.payer.list.PayersListView
import com.oborodulin.home.common.ui.state.CommonScreen
import com.oborodulin.home.common.ui.theme.HomeComposableTheme
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

/**
 * Created by tfakioglu on 12.December.2021
 */
private const val TAG = "Accounting.AccountingScreen"

@Composable
fun AccountingScreen(
    navController: NavHostController,
    viewModel: AccountingViewModel = hiltViewModel()
) { //setFabOnClick: (() -> Unit) -> Unit
    Timber.tag(TAG).d("AccountingScreen() called")
    LaunchedEffect(Unit) {
        viewModel.submitAction(AccountingUiAction.Load)
    }
    viewModel.uiStateFlow.collectAsState().value.let { state ->
        HomeComposableTheme(darkTheme = true) {
            CommonScreen(state = state) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colors.surface),
                    horizontalAlignment = Alignment.CenterHorizontally
                )
                {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(30.dp))
                            .weight(2f)
                    ) {
                        PayersListView(navController = navController)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(30.dp))
                            .weight(1f)
                    ) {
                        PrevServiceMeterVals(it)
                    }
                }
            }
        }
    }
    LaunchedEffect(Unit) {
        viewModel.singleEventFlow.collectLatest {
            when (it) {
                is AccountingUiSingleEvent.OpenPayerDetailScreen -> {
                    navController.navigate(it.navRoute)
                }
            }
        }
    }
}
//val state = viewModel.accountingUiState.value
//    val payersList = viewModel.payersList.collectAsLazyPagingItems()

@Composable
fun PrevServiceMeterVals(accountingModel: AccountingModel) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        val (list, bottomControls, noTasksText) = createRefs()
        Text(text = "Элетроэнергия")
    }
    /*LaunchedEffect(Unit) {
        setFabOnClick { println("") }
    }*/
}
/*
@Preview(name = "Night Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Day Mode", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun PreviewAccountingScreen() {
    AccountingScreen(navController = rememberNavController())
}
 */

