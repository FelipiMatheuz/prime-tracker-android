package com.felipimatheuz.primehunt.ui.screen.goals.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.felipimatheuz.primehunt.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
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
                placeholder = { Text(stringResource(R.string.goals_search_label)) },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_search),
                        contentDescription = null
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
                            IconButton(onClick = onFilterClick) {
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
