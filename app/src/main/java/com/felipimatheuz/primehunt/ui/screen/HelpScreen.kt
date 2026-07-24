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
import com.felipimatheuz.primehunt.business.state.MenuDialogState

@Composable
fun HelpScreen(paddingValues: PaddingValues) {
    val state = MenuDialogState.Help
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(state.title),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text = stringResource(
                state.content,
                stringResource(R.string.menu_prime_sets),
                stringResource(R.string.menu_other_prime),
                stringResource(R.string.menu_relics)
            ),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
