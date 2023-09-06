package com.felipimatheuz.primehunt.ui.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.ui.theme.WarframeprimehuntTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopToolbar() {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.regal_aya), contentDescription = null,
                    modifier = Modifier.size(40.dp).padding(end = 8.dp)
                )
                Text(stringResource(R.string.app_name))
            }
        },
        actions = {
            IconButton(onClick = { /* do something */ }) {
                Icon(
                    painterResource(R.drawable.filter),
                    contentDescription = stringResource(R.string.filter),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
    )
}


@Preview
@Composable
fun TopToolbarPreview() {
    WarframeprimehuntTheme {
        TopToolbar()
    }
}