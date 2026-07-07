package com.felipimatheuz.primehunt.ui.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.business.state.PrimeSetUiState
import com.felipimatheuz.primehunt.model.PrimeSet
import com.felipimatheuz.primehunt.model.PrimeStatus
import com.felipimatheuz.primehunt.service.ads.BannerAdView
import com.felipimatheuz.primehunt.ui.component.PrimeSetCard
import com.felipimatheuz.primehunt.ui.theme.PrimeTrackerTheme
import com.felipimatheuz.primehunt.viewmodel.PrimeSetViewModel

@Composable
fun PrimeSetRoute(padding: PaddingValues, viewModel: PrimeSetViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    PrimeSetScreen(
        padding = padding,
        uiState = uiState,
        onSearchTextChanged = viewModel::updateSearchText,
        onSetSelectedSet = viewModel::setSelectedSet,
        onRefreshRequested = viewModel::refresh,
        onToggleCard = viewModel::togglePrimeSet,
        statusText = viewModel::getStatusTextRes,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrimeSetScreen(
    padding: PaddingValues,
    uiState: PrimeSetUiState,
    onSearchTextChanged: (String) -> Unit,
    onSetSelectedSet: (String) -> Unit,
    onRefreshRequested: () -> Unit,
    onToggleCard: (PrimeSet, Boolean) -> Unit,
    statusText: (PrimeStatus) -> Int,
) {
    ConstraintLayout(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
    ) {
        val (tfSearch, banner, lcPrimeSet) = createRefs()

        OutlinedTextField(
            value = uiState.queryFilter,
            singleLine = true,
            leadingIcon = {
                Icon(
                    painterResource(R.drawable.ic_search),
                    contentDescription = stringResource(R.string.search_items_by_name),
                    modifier = Modifier.padding(start = 3.dp)
                )
            },
            label = { Text(text = stringResource(R.string.search_items_by_name)) },
            onValueChange = { newText ->
                onSearchTextChanged(newText)
            },
            textStyle = MaterialTheme.typography.bodySmall,
            modifier = Modifier.constrainAs(tfSearch) {
                top.linkTo(parent.top)
                start.linkTo(parent.start, 8.dp)
                end.linkTo(parent.end, 8.dp)
                width = Dimension.fillToConstraints
            }
        )
        BannerAdView(bannerId = "Banner_Set", modifier = Modifier.constrainAs(banner) {
            top.linkTo(tfSearch.bottom, 8.dp)
            start.linkTo(parent.start, 8.dp)
            end.linkTo(parent.end, 8.dp)
            width = Dimension.fillToConstraints
        })
        LazyColumn(modifier = Modifier.constrainAs(lcPrimeSet) {
            top.linkTo(banner.bottom, 8.dp)
            start.linkTo(parent.start, 8.dp)
            end.linkTo(parent.end, 8.dp)
            bottom.linkTo(parent.bottom)
            width = Dimension.fillToConstraints
            height = Dimension.fillToConstraints
        }) {
            if (uiState.primeSets.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.no_results),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            } else {
                items(
                    uiState.primeSets,
                    key = { it.setName }) { primeSet ->
                    PrimeSetCard(
                        primeSet,
                        statusText = stringResource(id = statusText(primeSet.status)),
                        onToggleCard = { onToggleCard(primeSet, it) },
                        goToDetails = { onSetSelectedSet(primeSet.setName) })
                    Spacer(modifier = Modifier.padding(bottom = 8.dp))
                }
            }
        }
        if (uiState.selectedPrimeSet.isNotEmpty()) {
            PrimeSetDetailScreen(setName = uiState.selectedPrimeSet, onDismiss = {
                onRefreshRequested()
            })
        }
    }
}

@Preview
@Composable
fun PrimeSetScreenPreview() {
    PrimeTrackerTheme {
        val samplePrimeSet =
            PrimeSet(setName = "Loki Prime", primeItems = emptyList(), released = 0)
        PrimeSetScreen(
            padding = PaddingValues(10.dp),
            uiState = PrimeSetUiState(primeSets = listOf(samplePrimeSet), queryFilter = "Loki"),
            onSearchTextChanged = { },
            onSetSelectedSet = { },
            onRefreshRequested = { },
            onToggleCard = { _, _ -> },
            statusText = { _ -> R.string.status_vault }
        )
    }
}
