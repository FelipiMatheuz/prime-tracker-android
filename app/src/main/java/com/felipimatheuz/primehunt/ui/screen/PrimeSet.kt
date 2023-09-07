package com.felipimatheuz.primehunt.ui.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.ui.theme.WarframeprimehuntTheme
import com.felipimatheuz.primehunt.viewmodel.PrimeSetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrimeSetScreen(padding: PaddingValues) {
    ConstraintLayout(modifier = Modifier.padding(padding).fillMaxSize()) {
        val viewModel: PrimeSetViewModel = viewModel()
        val (tfSearch, lcPrimeSet) = createRefs()
        OutlinedTextField(
            value = "",
            singleLine = true,
            leadingIcon = {
                Icon(
                    painterResource(R.drawable.ic_search),
                    contentDescription = stringResource(R.string.search_items_by_name),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            },
            label = { Text(text = stringResource(R.string.search_items_by_name)) },
            onValueChange = {
                //viewModel.updateSearch(it)
            },
            textStyle = MaterialTheme.typography.bodySmall,
            modifier = Modifier.constrainAs(tfSearch) {
                top.linkTo(parent.top, 8.dp)
                start.linkTo(parent.start, 8.dp)
                end.linkTo(parent.end, 8.dp)
                width = Dimension.fillToConstraints
            }
        )
        LazyColumn(modifier = Modifier.constrainAs(lcPrimeSet) {
            top.linkTo(tfSearch.bottom)
            start.linkTo(parent.start, 8.dp)
            end.linkTo(parent.end, 8.dp)
            width = Dimension.fillToConstraints
            height = Dimension.fillToConstraints
        }) {

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