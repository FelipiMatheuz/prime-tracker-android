package com.felipimatheuz.primehunt.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.felipimatheuz.primehunt.R

@Composable
fun PrimeHelpDialog(dismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { dismiss() },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(R.drawable.ic_help), contentDescription = stringResource(R.string.help),
                    modifier = Modifier.size(32.dp), colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
                )
                Text(text = stringResource(R.string.title_basics), modifier = Modifier.padding(start = 8.dp))
            }
        },
        text = {
            Text(
                stringResource(
                    R.string.basics_content,
                    stringResource(R.string.menu_prime_sets),
                    stringResource(R.string.menu_other_prime),
                    stringResource(R.string.menu_relics)
                ), color = MaterialTheme.colorScheme.onSurface
            )
        },
        confirmButton = {},
        shape = RoundedCornerShape(10.dp)
    )
}