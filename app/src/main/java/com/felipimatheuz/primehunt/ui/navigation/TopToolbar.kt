package com.felipimatheuz.primehunt.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.felipimatheuz.primehunt.data.local.enums.BgIcons
import com.felipimatheuz.primehunt.ui.theme.PrimeTrackerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopToolbar(
    currentKey: AppNavKey,
    onMenuClick: () -> Unit,
    selectedWallpaper: BgIcons = BgIcons.WARFRAME,
    onWallpaperSelected: (BgIcons) -> Unit = {}
) {
    val primary = MaterialTheme.colorScheme.primary
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer

    val toolbarPath = remember { Path() }

    TopAppBar(
        modifier = Modifier.drawBehind {
            drawRect(color = primary)

            toolbarPath.reset()
            toolbarPath.apply {
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
            drawPath(toolbarPath, primaryContainer)
        },
        title = {
            AnimatedContent(targetState = currentKey, label = "ToolbarTitle") {
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
        actions = {
            var expanded by remember { mutableStateOf(false) }

            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_settings),
                        contentDescription = stringResource(R.string.settings_wallpaper_description),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    Text(
                        text = stringResource(R.string.settings_wallpaper_title),
                        modifier = Modifier.padding(16.dp, 8.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    HorizontalDivider()

                    BgIcons.entries.forEach { icon ->
                        DropdownMenuItem(
                            text = {
                                Text(icon.displayName)
                            },
                            onClick = {
                                onWallpaperSelected(icon)
                                expanded = false
                            },
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(icon.icon),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            trailingIcon = {
                                if (icon == selectedWallpaper) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_check),
                                        contentDescription = stringResource(R.string.settings_wallpaper_selected),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        )
                    }
                }
            }
        },
        colors = topAppBarColors(containerColor = Color.Transparent)
    )
}


@Preview
@Composable
fun TopToolbarPreview() {
    PrimeTrackerTheme {
        TopToolbar(OverviewKey, {})
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun TopToolbarDarkPreview() {
    PrimeTrackerTheme {
        TopToolbar(OverviewKey, {})
    }
}
