package com.felipimatheuz.primehunt.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.felipimatheuz.primehunt.BuildConfig
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.business.external.AppUpdate
import com.felipimatheuz.primehunt.business.state.MenuDialogState

@Composable
fun PrimeInfoDialog(menuDialogState: MenuDialogState, dismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { dismiss() },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(menuDialogState.icon), contentDescription = stringResource(R.string.help),
                    modifier = Modifier.size(32.dp), colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                )
                Text(text = stringResource(menuDialogState.title), modifier = Modifier.padding(start = 8.dp))
            }
        },
        text = {
            Text(
                stringResource(
                    menuDialogState.content,
                    if (menuDialogState.title == R.string.help) {
                        stringResource(R.string.menu_prime_sets)
                    } else {
                        val update by AppUpdate(LocalContext.current).updateState.collectAsState()
                        BuildConfig.VERSION_NAME.plus(
                            if (update) {
                                "*"
                            } else {
                                ""
                            }
                        )
                    },
                    stringResource(R.string.menu_other_prime),
                    stringResource(R.string.menu_relics)
                ), color = MaterialTheme.colorScheme.onSurface
            )
        },
        confirmButton = {
            TextButton(onClick = { dismiss() }) {
                Text(text = stringResource(R.string.close))
            }
        },
        shape = RoundedCornerShape(10.dp)
    )
}