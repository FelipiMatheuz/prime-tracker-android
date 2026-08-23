package com.felipimatheuz.primehunt.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.domain.model.enums.AppLanguage
import com.felipimatheuz.primehunt.domain.model.enums.AppTheme
import com.felipimatheuz.primehunt.domain.model.enums.BgIcons
import com.felipimatheuz.primehunt.ui.modifier.PressIntensity
import com.felipimatheuz.primehunt.ui.modifier.pressScale
import com.felipimatheuz.primehunt.ui.theme.PrimeTrackerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopToolbar(
    currentKey: AppNavKey,
    onMenuClick: () -> Unit,
    selectedWallpaper: BgIcons = BgIcons.WARFRAME,
    onWallpaperSelected: (BgIcons) -> Unit = {},
    selectedTheme: AppTheme = AppTheme.SYSTEM,
    onThemeSelected: (AppTheme) -> Unit = {},
    selectedLanguage: AppLanguage = AppLanguage.SYSTEM,
    onLanguageSelected: (AppLanguage) -> Unit = {}
) {
    val primary = MaterialTheme.colorScheme.primary
    val primaryContainer = MaterialTheme.colorScheme.primaryContainer

    TopAppBar(
        modifier = Modifier.drawWithCache {
            val toolbarPath = Path().apply {
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
            onDrawBehind {
                drawRect(color = primary)
                drawPath(toolbarPath, primaryContainer)
            }
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
                            contentDescription = stringResource(
                                R.string.generic_icon_description,
                                stringResource(it.label)
                            ),
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
            val menuInteraction = remember { MutableInteractionSource() }
            IconButton(
                onClick = onMenuClick,
                interactionSource = menuInteraction,
                modifier = Modifier.pressScale(menuInteraction, PressIntensity.SUBTLE)
            ) {
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
                val settingsInteraction = remember { MutableInteractionSource() }
                IconButton(
                    onClick = { expanded = true },
                    interactionSource = settingsInteraction,
                    modifier = Modifier.pressScale(settingsInteraction, PressIntensity.SUBTLE)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_settings),
                        contentDescription = stringResource(R.string.settings_wallpaper_description),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.width(220.dp)
                ) {
                    // Theme Preference
                    SettingsDropdownItem(
                        label = stringResource(R.string.settings_theme_title),
                        selectedOptionLabel = stringResource(selectedTheme.displayNameRes),
                        options = AppTheme.entries,
                        optionLabelProvider = { stringResource(it.displayNameRes) },
                        onOptionSelected = onThemeSelected
                    )

                    // Language Preference
                    SettingsDropdownItem(
                        label = stringResource(R.string.settings_language_title),
                        selectedOptionLabel = stringResource(selectedLanguage.displayNameRes),
                        options = AppLanguage.entries,
                        optionLabelProvider = { stringResource(it.displayNameRes) },
                        onOptionSelected = onLanguageSelected
                    )

                    // Wallpaper Preference (Expandable)
                    var wallpaperExpanded by remember { mutableStateOf(false) }
                    Column {
                        val wallpaperInteraction = remember { MutableInteractionSource() }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    interactionSource = wallpaperInteraction,
                                    indication = null,
                                    onClick = { wallpaperExpanded = !wallpaperExpanded }
                                )
                                .pressScale(wallpaperInteraction, PressIntensity.VERY_SUBTLE)
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = stringResource(R.string.settings_wallpaper_title),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        painter = painterResource(selectedWallpaper.icon),
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        text = selectedWallpaper.displayName,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                            Icon(
                                painter = painterResource(
                                    if (wallpaperExpanded) R.drawable.ic_up_arrow
                                    else R.drawable.ic_down_arrow
                                ),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        DropdownMenu(
                            expanded = wallpaperExpanded,
                            onDismissRequest = { wallpaperExpanded = false }
                        ) {
                            BgIcons.entries.forEach { icon ->
                                val itemInteraction = remember { MutableInteractionSource() }
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = icon.displayName,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    },
                                    onClick = {
                                        onWallpaperSelected(icon)
                                        wallpaperExpanded = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(icon.icon),
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    interactionSource = itemInteraction,
                                    modifier = Modifier.pressScale(
                                        itemInteraction,
                                        PressIntensity.VERY_SUBTLE
                                    )
                                )
                            }
                        }
                    }
                }
            }
        },
        colors = topAppBarColors(containerColor = Color.Transparent)
    )
}

@Composable
private fun <T> SettingsDropdownItem(
    label: String,
    selectedOptionLabel: String,
    options: List<T>,
    optionLabelProvider: @Composable (T) -> String,
    onOptionSelected: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = { expanded = true }
                )
                .pressScale(interactionSource, PressIntensity.VERY_SUBTLE)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = selectedOptionLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
            Icon(
                painter = painterResource(if (expanded) R.drawable.ic_up_arrow else R.drawable.ic_down_arrow),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                val itemInteraction = remember { MutableInteractionSource() }
                DropdownMenuItem(
                    text = {
                        Text(
                            text = optionLabelProvider(option),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    },
                    interactionSource = itemInteraction,
                    modifier = Modifier.pressScale(itemInteraction, PressIntensity.VERY_SUBTLE)
                )
            }
        }
    }
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
