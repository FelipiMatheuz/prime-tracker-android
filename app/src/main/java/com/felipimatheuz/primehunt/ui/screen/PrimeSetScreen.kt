package com.felipimatheuz.primehunt.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.data.remote.enums.PrimeType
import com.felipimatheuz.primehunt.data.remote.enums.RelicSource
import com.felipimatheuz.primehunt.domain.model.PrimeSetDomain
import com.felipimatheuz.primehunt.ui.component.CategoryHeader
import com.felipimatheuz.primehunt.ui.component.CollectionCard
import com.felipimatheuz.primehunt.ui.component.PrimeSetCard
import com.felipimatheuz.primehunt.viewmodel.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PrimeSetScreen(
    paddingValues: PaddingValues,
    viewModel: PrimeSetViewModel = hiltViewModel(),
    onSetClick: (PrimeSetDomain) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var selectedView by remember { mutableIntStateOf(0) }
    val viewOptions = listOf("Collections", "Prime Sets")
    
    val sheetState = rememberModalBottomSheetState()
    var showFilterSheet by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = state.queryFilter,
                    onQueryChange = { viewModel.onIntent(PrimeSetIntent.Search(it)) },
                    onSearch = { },
                    expanded = false,
                    onExpandedChange = { },
                    placeholder = { Text(stringResource(R.string.search_label)) },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_search),
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (state.queryFilter.isNotEmpty()) {
                                IconButton(onClick = { viewModel.onIntent(PrimeSetIntent.ClearSearch) }) {
                                    Icon(painterResource(R.drawable.btn_close), contentDescription = "Clear")
                                }
                            }
                            IconButton(onClick = { showFilterSheet = true }) {
                                Icon(
                                    painter = painterResource(R.drawable.btn_filter),
                                    contentDescription = "Filter",
                                    tint = if (state.activeFilters != PrimeSetFilters()) MaterialTheme.colorScheme.primary else LocalContentColor.current
                                )
                            }
                        }
                    }
                )
            },
            expanded = false,
            onExpandedChange = { },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) { }

        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 8.dp)
        ) {
            viewOptions.forEachIndexed { index, label ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = viewOptions.size
                    ),
                    onClick = { selectedView = index },
                    selected = selectedView == index
                ) {
                    Text(label)
                }
            }
        }

        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
            if (state.isLoading) {
                Column(modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CircularProgressIndicator()
                    Text(
                        text = stringResource(R.string.load_content),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    if (selectedView == 0) {
                        if (state.collections.isEmpty()) {
                            item { EmptyStateMessage() }
                        } else {
                            items(state.collections, key = { it.id }) { collection ->
                                CollectionCard(
                                    collection = collection,
                                    onSetClick = onSetClick
                                )
                            }
                        }
                    } else {
                        if (state.groupedSets.isEmpty()) {
                            item { EmptyStateMessage() }
                        } else {
                            state.groupedSets.forEach { (category, sets) ->
                                item(key = category) {
                                    CategoryHeader(title = category)
                                }
                                items(sets, key = { it.id }) { set ->
                                    PrimeSetCard(
                                        primeSet = set,
                                        modifier = Modifier.padding(vertical = 4.dp),
                                        onClick = { onSetClick(set) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showFilterSheet) {
        FilterBottomSheet(
            filters = state.activeFilters,
            onFiltersChanged = { viewModel.onIntent(PrimeSetIntent.UpdateFilters(it)) },
            onDismiss = { showFilterSheet = false },
            sheetState = sheetState
        )
    }
}

@Composable
private fun EmptyStateMessage() {
    Text(
        text = stringResource(R.string.no_results),
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp),
        textAlign = TextAlign.Center
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheet(
    filters: PrimeSetFilters,
    onFiltersChanged: (PrimeSetFilters) -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Filters", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            // Progress Filter
            FilterSectionTitle("Progress")
            FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ProgressFilter.entries.forEach { option ->
                    FilterChip(
                        selected = filters.progress == option,
                        onClick = { onFiltersChanged(filters.copy(progress = option)) },
                        label = { Text(option.name.lowercase().replace("_", " ").replaceFirstChar { it.uppercase() }) }
                    )
                }
            }

            // Category Filter
            FilterSectionTitle("Category")
            FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PrimeType.entries.forEach { type ->
                    FilterChip(
                        selected = filters.categories.contains(type),
                        onClick = {
                            val newCategories = if (filters.categories.contains(type)) {
                                filters.categories - type
                            } else {
                                filters.categories + type
                            }
                            onFiltersChanged(filters.copy(categories = newCategories))
                        },
                        label = { Text(type.name.lowercase().replace("_", " ").replaceFirstChar { it.uppercase() }) }
                    )
                }
            }

            // Availability Filter
            FilterSectionTitle("Availability")
            FlowRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                RelicSource.entries.forEach { source ->
                    FilterChip(
                        selected = filters.availabilities.contains(source),
                        onClick = {
                            val newAvail = if (filters.availabilities.contains(source)) {
                                filters.availabilities - source
                            } else {
                                filters.availabilities + source
                            }
                            onFiltersChanged(filters.copy(availabilities = newAvail))
                        },
                        label = { Text(source.name.lowercase().replaceFirstChar { it.uppercase() }) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { onFiltersChanged(PrimeSetFilters()); onDismiss() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer)
            ) {
                Text("Clear All")
            }
        }
    }
}

@Composable
private fun FilterSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
    )
}
