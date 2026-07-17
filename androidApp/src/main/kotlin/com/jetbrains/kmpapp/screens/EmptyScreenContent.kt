package com.jetbrains.kmpapp.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.jetbrains.kmpapp.R

@Composable
fun EmptyScreenContent(
    modifier: Modifier = Modifier,
    message: String = stringResource(R.string.no_data_available),
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Text(message)
    }
}
