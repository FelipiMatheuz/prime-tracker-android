package com.felipimatheuz.primehunt.ui.screen.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.ui.modifier.PressIntensity
import com.felipimatheuz.primehunt.ui.modifier.pressScale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrimeSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholderRes: Int,
    activeFiltersCount: Int,
    onFilterClick: () -> Unit,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = onQueryChange,
                onSearch = { },
                expanded = false,
                onExpandedChange = { },
                placeholder = { Text(stringResource(placeholderRes)) },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_search),
                        contentDescription = stringResource(R.string.search_description)
                    )
                },
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = onClearClick) {
                                Icon(
                                    painterResource(R.drawable.btn_close),
                                    contentDescription = stringResource(R.string.close)
                                )
                            }
                        }
                        BadgedBox(
                            badge = {
                                if (activeFiltersCount > 0) {
                                    Badge {
                                        Text(activeFiltersCount.toString())
                                    }
                                }
                            }
                        ) {
                            val filterButtonInteraction = remember { MutableInteractionSource() }
                            IconButton(
                                onClick = onFilterClick,
                                interactionSource = filterButtonInteraction,
                                modifier = Modifier.pressScale(filterButtonInteraction, PressIntensity.MODERATE)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.btn_filter),
                                    contentDescription = stringResource(R.string.filter),
                                    tint = if (activeFiltersCount > 0) MaterialTheme.colorScheme.primary else LocalContentColor.current
                                )
                            }
                        }
                    }
                }
            )
        },
        expanded = false,
        onExpandedChange = { },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) { }
}
