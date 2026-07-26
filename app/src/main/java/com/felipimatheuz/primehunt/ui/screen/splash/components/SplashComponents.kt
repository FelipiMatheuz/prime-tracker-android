package com.felipimatheuz.primehunt.ui.screen.splash.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.business.state.EtlFile

@Composable
fun SplashSyncStatus(textRes: Int, file: EtlFile? = null) {

    val finalText = if (file == null) {
        stringResource(textRes)
    } else {
        stringResource(textRes, file.text)
    }

    Text(
        text = finalText,
        modifier = Modifier.padding(horizontal = 24.dp),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.labelLarge.copy(
            color = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
fun SplashFooter(modifier: Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.by_owner),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.labelLarge.copy(
                color = MaterialTheme.colorScheme.onSurface
            )
        )
        Image(
            painterResource(R.drawable.cs_logo),
            contentDescription = stringResource(R.string.logo)
        )
    }
}