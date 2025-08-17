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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.business.util.PrimeFilter
import com.felipimatheuz.primehunt.service.ads.BannerAdView
import com.felipimatheuz.primehunt.ui.component.PrimeItemCard
import com.felipimatheuz.primehunt.ui.theme.PrimeTrackerTheme
import com.felipimatheuz.primehunt.viewmodel.OtherPrimeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtherPrimeScreen(padding: PaddingValues, primeFilter: PrimeFilter, viewModel: OtherPrimeViewModel = hiltViewModel()) {
    ConstraintLayout(modifier = Modifier.padding(padding).fillMaxSize()) {
        val primeList by viewModel.otherPrimesFiltered.collectAsState()
        var searchText by remember { mutableStateOf("") }
        val (tfSearch, banner, lcOtherPrime) = createRefs()
        OutlinedTextField(
            value = searchText,
            singleLine = true,
            leadingIcon = {
                Icon(
                    painterResource(R.drawable.ic_search),
                    contentDescription = stringResource(R.string.search_items_by_name),
                    modifier = Modifier.padding(start = 3.dp)
                )
            },
            label = { Text(text = stringResource(R.string.search_items_by_name)) },
            onValueChange = {
                searchText = it
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
        LazyColumn(modifier = Modifier.constrainAs(lcOtherPrime) {
            top.linkTo(banner.bottom, 8.dp)
            start.linkTo(parent.start, 8.dp)
            end.linkTo(parent.end, 8.dp)
            bottom.linkTo(parent.bottom)
            width = Dimension.fillToConstraints
            height = Dimension.fillToConstraints
        }) {
            viewModel.filterOtherPrime(searchText, primeFilter)
            if (primeList.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.no_results),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                    )
                }
            } else {
                items(primeList) { primeItem ->
                    PrimeItemCard(primeItem, viewModel)
                    Spacer(modifier = Modifier.padding(bottom = 8.dp))
                }
            }
        }
    }
}

@Preview
@Composable
fun OtherPrimeScreenPreview() {
    PrimeTrackerTheme {
        OtherPrimeScreen(PaddingValues(10.dp), PrimeFilter.SHOW_ALL)
    }
}