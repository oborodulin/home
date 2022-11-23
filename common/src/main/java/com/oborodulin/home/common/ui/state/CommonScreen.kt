package com.oborodulin.home.common.ui.state

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Snackbar
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import timber.log.Timber

private const val TAG = "Common.ui.CommonScreen"

@Composable
fun <T : Any> CommonScreen(state: UiState<T>, onSuccess: @Composable (T) -> Unit) {
    Timber.tag(TAG).d("CommonScreen(...) called")
    when (state) {
        is UiState.Loading -> {
            Loading()
        }
        is UiState.Error -> {
            Error(state.errorMessage)
        }
        is UiState.Success -> {
            Timber.tag(TAG).d("onSuccess(...) called: %s".format(state.data))
            onSuccess(state.data)
        }
    }
}

@Composable
fun Error(errorMessage: String) {
    Timber.tag(TAG).d("Error(...) called: %s".format(errorMessage))
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
    ) {
        Snackbar {
            Text(text = errorMessage)
        }
    }
}

@Composable
fun Loading() {
    Timber.tag(TAG).d("Loading() called")
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator()
    }
}