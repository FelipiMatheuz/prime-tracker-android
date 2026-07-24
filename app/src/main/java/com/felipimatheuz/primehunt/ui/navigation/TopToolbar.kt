package com.felipimatheuz.primehunt.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.ui.theme.PrimeTrackerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopToolbar(
    currentKey: AppNavKey,
    onMenuClick: () -> Unit
) {
    val titleRes = when (currentKey) {
        OverviewKey -> R.string.menu_overview
        is PrimeSetsKey -> R.string.menu_prime_sets
        is OtherPrimesKey -> R.string.menu_other_prime
        is RelicsKey -> R.string.menu_relics
        SyncKey -> R.string.sync
        HelpKey -> R.string.help
        AboutKey -> R.string.about
    }

    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.regal_aya),
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 8.dp)
                )
                Text(
                    text = stringResource(titleRes),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_menu),
                    contentDescription = stringResource(R.string.menu_overview),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        colors = topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
    )
}


@Preview
@Composable
fun TopToolbarPreview() {
    PrimeTrackerTheme {
        TopToolbar(OverviewKey, {})
    }
}