package com.oborodulin.home.accounting.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.oborodulin.home.accounting.ui.payer.PayersList
import com.oborodulin.home.common.ui.theme.HomeComposableTheme
import timber.log.Timber

/**
 * Created by tfakioglu on 12.December.2021
 */
private const val TAG = "AccountingScreen"

@Composable
fun AccountingScreen(
    navController: NavHostController,
    viewModel: AccountingViewModel = hiltViewModel()
) { //setFabOnClick: (() -> Unit) -> Unit
    Timber.tag(TAG).d("AccountingScreen() called")
    val state = viewModel.accountingUiState.value

//    val payersList = viewModel.payersList.collectAsLazyPagingItems()

    HomeComposableTheme(darkTheme = true) {
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
                    .weight(1f)
            ) {
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    val (list, bottomControls, noTasksText) = createRefs()
                    Text(text = "Элетроэнергия")
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(30.dp))
                    .weight(2f)
            ) {
                PayersList()
            }
        }
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

