package com.felipimatheuz.primehunt.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.felipimatheuz.primehunt.R

@Composable
fun HelpScreen(paddingValues: PaddingValues) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.help_content,
                stringResource(R.string.menu_prime_sets),
                stringResource(R.string.menu_goals),
                stringResource(R.string.menu_relics)
            ),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
