package com.felipimatheuz.primehunt.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.felipimatheuz.primehunt.BuildConfig
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.business.state.MenuDialogState
import com.felipimatheuz.primehunt.viewmodel.PrimeInfoViewModel

@Composable
fun AboutScreen(
    paddingValues: PaddingValues,
    viewModel: PrimeInfoViewModel = hiltViewModel()
) {
    val state = MenuDialogState.Info
    val update by viewModel.updateState.collectAsState()
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
                BuildConfig.VERSION_NAME.plus(if (update) "*" else ""),
                stringResource(R.string.menu_other_prime),
                stringResource(R.string.menu_relics)
            ),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
