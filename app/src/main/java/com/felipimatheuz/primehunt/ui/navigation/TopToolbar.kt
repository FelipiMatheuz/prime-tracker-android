package com.felipimatheuz.primehunt.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
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
    val primary = MaterialTheme.colorScheme.primary
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer

    TopAppBar(
        modifier = Modifier.drawBehind {
            drawRect(color = primary)

            val path = Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width * 0.4f, 0f)
                cubicTo(
                    x1 = size.width * 0.5f, y1 = 0f,
                    x2 = size.width * 0.5f, y2 = size.height,
                    x3 = size.width * 0.6f, y3 = size.height
                )
                lineTo(0f, size.height)
                close()
            }
            drawPath(path, primaryContainer)
        },
        title = {
            AnimatedContent(targetState = currentKey) {
                Box(
                    Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = it.icon),
                            contentDescription = null,
                            modifier = Modifier
                                .size(32.dp)
                                .padding(end = 8.dp),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = stringResource(it.label),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    painter = painterResource(R.drawable.ic_menu),
                    contentDescription = stringResource(R.string.menu_button_description),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        },
        colors = topAppBarColors(containerColor = Color.Transparent)
    )
}


@Preview
@Composable
fun TopToolbarPreview() {
    PrimeTrackerTheme {
        TopToolbar(OverviewKey) {}
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun TopToolbarDarkPreview() {
    PrimeTrackerTheme {
        TopToolbar(OverviewKey) {}
    }
}