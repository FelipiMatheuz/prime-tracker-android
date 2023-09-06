package com.felipimatheuz.primehunt.ui.screen

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import com.felipimatheuz.primehunt.R
import com.felipimatheuz.primehunt.ui.component.PrimeChart
import com.felipimatheuz.primehunt.ui.theme.WarframeprimehuntTheme
import com.felipimatheuz.primehunt.viewmodel.OverViewModel


@Composable
fun OverviewScreen(padding: PaddingValues) {
    ConstraintLayout(modifier = Modifier.padding(padding).fillMaxSize()) {
        val viewModel: OverViewModel = viewModel()
        var showDetail by rememberSaveable { mutableStateOf(false) }
        val (overallChart, primeChart, otherChart, toggleOverview) = createRefs()

        val setPrimeItems = viewModel.loadSet()
        val otherPrimeItems = viewModel.loadOther(LocalContext.current)
        if (!showDetail) {
            PrimeChart(
                labelRes = R.string.overall,
                data = setPrimeItems + otherPrimeItems,
                modifier = Modifier.constrainAs(overallChart) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
            )
        } else {
            PrimeChart(
                labelRes = R.string.menu_prime_sets,
                data = setPrimeItems,
                modifier = Modifier.constrainAs(primeChart) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(otherChart.start)
                },
                scaleSize = 0.5f
            )

            PrimeChart(
                labelRes = R.string.menu_other_prime,
                data = otherPrimeItems,
                modifier = Modifier.constrainAs(otherChart) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(primeChart.end)
                    end.linkTo(parent.end)
                },
                scaleSize = 0.5f
            )
        }
        OutlinedButton(onClick = { showDetail = !showDetail },
            modifier = Modifier.constrainAs(toggleOverview) {
                bottom.linkTo(parent.bottom, 8.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }) {
            Text(
                text = stringResource(
                    if (showDetail) {
                        R.string.detailed
                    } else {
                        R.string.overall
                    }
                ), style = LocalTextStyle.current.copy(shadow = Shadow(
                    color = MaterialTheme.colorScheme.onSecondary,
                    offset = Offset(2.0f, 2.0f),
                    blurRadius = 2f
                ))
            )
        }
    }
}

@Preview
@Composable
fun OverviewScreenPreview() {
    WarframeprimehuntTheme {
        OverviewScreen(PaddingValues(10.dp))
    }
}