package com.felipimatheuz.primehunt.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.service.ads.BannerAdView
import com.felipimatheuz.primehunt.ui.component.PrimeItemCard
import com.felipimatheuz.primehunt.ui.theme.WarframeprimehuntTheme
import com.felipimatheuz.primehunt.business.util.PrimeFilter
import com.felipimatheuz.primehunt.viewmodel.OtherPrimeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtherPrimeScreen(padding: PaddingValues, primeFilter: PrimeFilter) {
    ConstraintLayout(modifier = Modifier.padding(padding).fillMaxSize()) {
        val viewModel = OtherPrimeViewModel(LocalContext.current)
        val primeList by viewModel.otherPrimesFiltered.collectAsState()
        var searchText by remember { mutableStateOf("") }
        val (tfSearch, lcOtherPrime) = createRefs()
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
        LazyColumn(modifier = Modifier.constrainAs(lcOtherPrime) {
            top.linkTo(tfSearch.bottom, 8.dp)
            start.linkTo(parent.start, 8.dp)
            end.linkTo(parent.end, 8.dp)
            bottom.linkTo(parent.bottom)
            width = Dimension.fillToConstraints
            height = Dimension.fillToConstraints
        }) {
            viewModel.filterOtherPrime(searchText, primeFilter)
            item {
                BannerAdView("Banner_Other")
            }
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
    WarframeprimehuntTheme {
        OtherPrimeScreen(PaddingValues(10.dp), PrimeFilter.SHOW_ALL)
    }
}