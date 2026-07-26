package com.felipimatheuz.primehunt.ui.screen.primeset.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.felipimatheuz.primehunt.R

@Composable
fun CategoryHeader(
    titleRes: Int,
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(titleRes),
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 4.dp)
    )
}

@Composable
fun EmptyStateMessage() {
    Text(
        text = stringResource(R.string.no_results),
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp),
        textAlign = TextAlign.Center
    )
}
