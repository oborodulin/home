package com.oborodulin.home.common.ui.state

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import timber.log.Timber

private const val TAG = "Common.ui.CommonScreen"

@Composable
fun <T : Any> CommonScreen(
    paddingValues: PaddingValues? = null,
    state: UiState<T>,
    onSuccess: @Composable (T) -> Unit
) {
    Timber.tag(TAG).d("CommonScreen(...) called")
    val modifier = Modifier.fillMaxSize()
    if (paddingValues != null) {
        modifier.padding(paddingValues)
    }
    when (state) {
        is UiState.Loading -> {
            Loading(modifier)
        }
        is UiState.Error -> {
            Error(modifier, state.errorMessage)
        }
        is UiState.Success -> {
            Timber.tag(TAG).d("CommonScreen: onSuccess(...) called: %s", state.data)
            onSuccess(state.data)
        }
    }
}

@Composable
fun Error(modifier: Modifier, errorMessage: String) {
    Timber.tag(TAG).d("Error(...) called: %s", errorMessage)
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Bottom
    ) {
        Snackbar {
            Text(text = errorMessage)
        }
    }
}

@Composable
fun Loading(modifier: Modifier) {
    Timber.tag(TAG).d("Loading() called")
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator()
    }
}