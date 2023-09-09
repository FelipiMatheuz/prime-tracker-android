package com.felipimatheuz.primehunt.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.felipimatheuz.primehunt.ui.component.PrimeSetCard
import com.felipimatheuz.primehunt.ui.theme.WarframeprimehuntTheme
import com.felipimatheuz.primehunt.viewmodel.PrimeSetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrimeSetScreen(padding: PaddingValues) {
    ConstraintLayout(modifier = Modifier.padding(padding).fillMaxSize()) {
        val viewModel = PrimeSetViewModel(LocalContext.current)
        val primeSets = viewModel.primeSets.collectAsState()
        val showDialog = remember { mutableStateOf<String?>(null) }
        val (tfSearch, lcPrimeSet) = createRefs()
        OutlinedTextField(
            value = "",
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
                //viewModel.updateSearch(it)
            },
            textStyle = MaterialTheme.typography.bodySmall,
            modifier = Modifier.constrainAs(tfSearch) {
                top.linkTo(parent.top)
                start.linkTo(parent.start, 8.dp)
                end.linkTo(parent.end, 8.dp)
                width = Dimension.fillToConstraints
            }
        )
        LazyColumn(modifier = Modifier.constrainAs(lcPrimeSet) {
            top.linkTo(tfSearch.bottom, 8.dp)
            start.linkTo(parent.start, 8.dp)
            end.linkTo(parent.end, 8.dp)
            bottom.linkTo(parent.bottom)
            width = Dimension.fillToConstraints
            height = Dimension.fillToConstraints
        }) {
            items(primeSets.value) { primeSet ->
                PrimeSetCard(primeSet, viewModel) { showDialog.value = primeSet.warframe.name }
                Spacer(modifier = Modifier.padding(bottom = 8.dp))
            }
            if (primeSets.value.isEmpty()) {
                item {
                    Text(
                        text = stringResource(androidx.compose.ui.R.string.not_selected),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        if (showDialog.value != null) {
            PrimeSetDetailScreen(showDialog.value) { showDialog.value = null }
        }
    }
}

@Preview
@Composable
fun PrimeSetScreenPreview() {
    WarframeprimehuntTheme {
        PrimeSetScreen(PaddingValues(10.dp))
    }
}