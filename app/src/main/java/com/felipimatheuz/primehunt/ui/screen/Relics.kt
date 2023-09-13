package com.felipimatheuz.primehunt.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.model.core.RelicItem
import com.felipimatheuz.primehunt.model.core.RelicTier
import com.felipimatheuz.primehunt.ui.component.RelicRewardsDialog
import com.felipimatheuz.primehunt.ui.theme.WarframeprimehuntTheme
import com.felipimatheuz.primehunt.util.PrimeFilter
import com.felipimatheuz.primehunt.viewmodel.RelicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RelicScreen(padding: PaddingValues, primeFilter: PrimeFilter) {
    val viewModel = RelicViewModel(LocalContext.current)
    ConstraintLayout(modifier = Modifier.padding(padding).fillMaxSize()) {
        var searchText by remember { mutableStateOf("") }
        val (tfSearch, lgRelics) = createRefs()
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
        LazyColumn(
            modifier = Modifier.constrainAs(lgRelics) {
                top.linkTo(tfSearch.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(RelicTier.values()) {
                PrimeRelicTierUI(viewModel, it, primeFilter, searchText)
            }
        }
    }
}

@Composable
fun PrimeRelicTierUI(viewModel: RelicViewModel, relicTier: RelicTier, primeFilter: PrimeFilter, searchText: String) {
    var showDialog by remember { mutableStateOf<RelicItem?>(null) }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Image(
            painter = painterResource(viewModel.getImageTier(relicTier)),
            contentDescription = null
        )
        Text(text = stringResource(R.string.tier_relics, relicTier), style = MaterialTheme.typography.titleLarge)
    }
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        val relicList = viewModel.getListTier(relicTier, primeFilter, searchText)
        items(relicList) { relic ->
            Column(modifier = Modifier.clickable {
                showDialog = relic
            }) {
                Text(
                    text = relic.name,
                    color = if (relic.quantity > 0) {
                        MaterialTheme.colorScheme.onSecondary
                    } else {
                        MaterialTheme.colorScheme.onSecondary.copy(0.5f)
                    },
                    textDecoration = if (relic.vaulted) {
                        TextDecoration.LineThrough
                    } else {
                        null
                    }
                )
                if (relic.quantity > 0) {
                    Text(text = "(${relic.quantity})")
                }
                if (relic.hasForma) {
                    Image(painterResource(R.drawable.ic_forma), contentDescription = null)
                }
            }
        }
    }
    if (showDialog != null) {
        RelicRewardsDialog(viewModel, relicTier, showDialog!!) { showDialog = null }
    }
}

@Preview
@Composable
fun RelicScreenPreview() {
    WarframeprimehuntTheme {
        RelicScreen(PaddingValues(10.dp), PrimeFilter.SHOW_ALL)
    }
}